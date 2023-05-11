package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.sign.DigitalSignatureChecker;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.transaction.TransactionStatus;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;



public class GetVerificaFirmaDigitaleFunction extends AbstractFunzioneTag {

  public GetVerificaFirmaDigitaleFunction() {
    super(4, new Class[] { PageContext.class, String.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    FileAllegatoManager fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager", pageContext,
        FileAllegatoManager.class);
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String verificaFirmaDigitaleXML = null;
    String verificaMarcheTemporaliXML = null;
    Date ckdate = null;
    String state = null;
    String message = null;
    String dignomdoc = null;
    String dignomdoc_tsd = null;
    String dignomdoc_p7m = null;
    String dignomdoc_doc = null;
    DigitalSignatureChecker digitalSignatureChecker = null;
    BlobFile digogg = null;
    TransactionStatus transazione = null;
    Boolean commitTransaction = true;
    try {
      transazione = sqlManager.startTransaction();
      String idprg = (String) params[1];
      Long iddocdig = null;
      if ((String) params[2] != null) {
        iddocdig = new Long((String) params[2]);
      }

      dignomdoc = (String) sqlManager.getObject("select dignomdoc from w_docdig where idprg = ? and iddocdig = ?", new Object[] {
          idprg, iddocdig });

      if (dignomdoc != null) {
    	  if (dignomdoc.toLowerCase().endsWith(".tsd")) {
    	      dignomdoc_tsd = dignomdoc;
    		  dignomdoc_p7m = dignomdoc.substring(0, dignomdoc.toLowerCase().indexOf(".tsd"));
          } else {
            // si presuppone sia un .p7m (senza estensione .tsd)
    		  dignomdoc_p7m = dignomdoc;
    	  }
    	  if (dignomdoc_p7m.toLowerCase().indexOf(".p7m") >= 0) {
    	    // si tolgono tutte le estensioni p7m e si recupera il documento originariamente firmato
    		  dignomdoc_doc = dignomdoc_p7m.substring(0, dignomdoc_p7m.toLowerCase().indexOf(".p7m"));
    	  } else {
    	    // trattasi di un documento marcato temporalmente, senza firma digitale applicata
    		  dignomdoc_doc = dignomdoc_p7m;
    		  dignomdoc_p7m = null;
    	  }
        digogg = fileAllegatoManager.getFileAllegato(idprg, iddocdig);
        if (digogg != null && digogg.getStream() != null) {
          digitalSignatureChecker = new DigitalSignatureChecker();
          //Params[3] contiene la data da controllare per stabilire la validità del certificato
          if ((String) params[3] != null) {
            //La data di controllo deve essere nel formato per esempio 20190528 10:30:12
            //Si deve controllare se è presente o meno l'orario, se non è presente si
            //forza 12:00:00
            String dataString = (String) params[3];
            if(dataString.length()==8){
              dataString+= " 12:00:00";
            }
            ckdate = new SimpleDateFormat("yyyyMMdd HH:mm:ss").parse(dataString);
          }
          byte[] content = null;
		  if (dignomdoc.toLowerCase().endsWith(".tsd")) {
			content = digitalSignatureChecker.getContentTimeStamp(digogg.getStream());
		  }
          if (content != null) {
        	  byte[] xmlTimeStamps = digitalSignatureChecker.getXMLTimeStamps(digogg.getStream());
        	  verificaMarcheTemporaliXML = new String(xmlTimeStamps);
          } else {
        	  content = digogg.getStream();
          }
          
          if (dignomdoc_p7m != null) {
            byte[] xml = digitalSignatureChecker.getXMLSignature(content, ckdate);
            verificaFirmaDigitaleXML = new String(xml);
            if(verificaFirmaDigitaleXML!=null && !"".equals(verificaFirmaDigitaleXML)){
              try{
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                Document document = docBuilder.parse(new InputSource(new StringReader(verificaFirmaDigitaleXML)));
              }catch (Exception e) {
                verificaFirmaDigitaleXML=null;
                state = "ERROR";
                message = this.resBundleGenerale.getString("errors.firmadigitale.errorxml");
                message +=e.getMessage();
              }
              boolean verified = digitalSignatureChecker.verifySignature(content);
              Timestamp dataOraCorrente = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
              String queryUpdate = "update w_docdig set firmacheck = ?,firmacheckts = ? where idprg = ? and iddocdig = ? and (firmacheck is null or firmacheck = ?)";
              sqlManager.update(queryUpdate,new Object[]{Boolean.TRUE.equals(verified)?"1":"2", dataOraCorrente, idprg, iddocdig.toString(), "2"});
            }
          }
          
        } else {
          state = "NO-DATA-FOUND";
        }
      } else {
        state = "NO-DATA-FOUND";
      }
    } catch (ParseException p) {
      state = "DATE-PARSE-EXCEPTION";
    } catch (Exception e) {
      state = "ERROR";
      message = e.getMessage();
      commitTransaction = false;
      if (e.getCause() != null) message += " (" + e.getCause().toString() + ")";
    } finally {
      if (transazione != null) {
        try {
          if (Boolean.TRUE.equals(commitTransaction)) {
            sqlManager.commitTransaction(transazione);            
          } else {
            sqlManager.rollbackTransaction(transazione);            
          }
        } catch (SQLException e) {
          throw new JspException(
              "Errore durante l'aggiornamento dell'esito della verifica firma digitale", e);
        }
      }
    }
    pageContext.setAttribute("dignomdoc_tsd", dignomdoc_tsd, PageContext.REQUEST_SCOPE);
   	pageContext.setAttribute("dignomdoc_p7m", dignomdoc_p7m, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("dignomdoc_doc", dignomdoc_doc, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("verificaFirmaDigitaleXML", verificaFirmaDigitaleXML, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("verificaMarcheTemporaliXML", verificaMarcheTemporaliXML, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("ckdateformat", UtilityDate.convertiData(ckdate, UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS), PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("state", state, PageContext.REQUEST_SCOPE);
    pageContext.setAttribute("message", message, PageContext.REQUEST_SCOPE);

    return null;
  }
}
