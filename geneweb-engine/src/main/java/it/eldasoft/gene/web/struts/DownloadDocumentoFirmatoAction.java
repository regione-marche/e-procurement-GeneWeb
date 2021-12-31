package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sign.DigitalSignatureChecker;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.eldasoft.utils.utility.UtilityWeb;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Esegue il download di un file allegato
 */
public class DownloadDocumentoFirmatoAction extends ActionBaseNoOpzioni {

  static Logger               logger                      = Logger.getLogger(DownloadDocumentoFirmatoAction.class);

  private static final String DOCUMENTO_MARCATO           = "tsd";
  private static final String DOCUMENTO_FIRMATO           = "p7m";
  private static final String DOCUMENTO_CONTENUTO_NEL_P7M = "doc";

  private FileAllegatoManager fileAllegatoManager;

  private SqlManager          sqlManager;

  /**
   *
   * @param fileAllegatoManager
   */
  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  /**
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("DownloadDocumentoFirmatoAction-runAction: inizio metodo");

    String target = null;
    String messageKey = null;

    String codiceApplicazione = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
    codiceApplicazione = UtilityStringhe.convertiNullInStringaVuota(codiceApplicazione);
    if("".equals(codiceApplicazione)){
      codiceApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
    }
    int livEvento=1;
    String codiceProfilo = (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);
    Integer idUtente = null;
    if(request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE) != null){
      int idUtenteInt = ((ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId();
      idUtente = new Integer(idUtenteInt);
    }
    String ip = request.getRemoteAddr();

    String idprg = request.getParameter("idprg");
    Long iddocdig = null;
    if (request.getParameter("iddocdig") != null && !"".equals(request.getParameter("iddocdig").trim())) {
      iddocdig = new Long(request.getParameter("iddocdig"));
    }

    String errMsgEvento=null;
    try {


      String type = request.getParameter("type");

      if (idprg != null && iddocdig != null && type != null) {
        String dignomdoc_p7m = (String) this.sqlManager.getObject("select dignomdoc from w_docdig where idprg = ? and iddocdig = ?",
            new Object[] { idprg, iddocdig });
        String[] fileNameSplit = dignomdoc_p7m.split("\\.");
        String ext = fileNameSplit[fileNameSplit.length - 1].toLowerCase();

        if (DOCUMENTO_MARCATO.equals(type)) {
            this.fileAllegatoManager.downloadFileAllegato(dignomdoc_p7m, idprg, iddocdig, response);
        } else if (DOCUMENTO_FIRMATO.equals(type)) {
        	if (DOCUMENTO_MARCATO.equals(ext)) {
        		BlobFile p7m = this.fileAllegatoManager.getFileAllegato(idprg, iddocdig);
                DigitalSignatureChecker digitalSignatureChecker = new DigitalSignatureChecker();
                byte[] doc = digitalSignatureChecker.getContentTimeStamp(p7m.getStream());
                String dignomdoc_doc = dignomdoc_p7m.substring(0, dignomdoc_p7m.toLowerCase().indexOf(".tsd"));
                UtilityWeb.download(dignomdoc_doc, doc, response);
        	} else {
        		this.fileAllegatoManager.downloadFileAllegato(dignomdoc_p7m, idprg, iddocdig, response);
        	}
        } else if (DOCUMENTO_CONTENUTO_NEL_P7M.equals(type)) {
            byte[] doc = null;
            BlobFile p7m = this.fileAllegatoManager.getFileAllegato(idprg, iddocdig);
            DigitalSignatureChecker digitalSignatureChecker = new DigitalSignatureChecker();
            while(true){
            if(doc == null){doc = p7m.getStream();}
            try{
          	  if (DOCUMENTO_MARCATO.equals(ext)) {
                  byte[] docp7m = digitalSignatureChecker.getContentTimeStamp(doc);
                  doc = digitalSignatureChecker.getContent(docp7m);
          	  } else {
                  doc = digitalSignatureChecker.getContent(doc);
          	  }
      	  }catch(Exception e){
      	    UtilityWeb.download(dignomdoc_p7m, doc, response);
      	    return null;
      	  }
    	    if (dignomdoc_p7m.toLowerCase().indexOf(".p7m") >= 0) {
              dignomdoc_p7m = dignomdoc_p7m.substring(0, dignomdoc_p7m.toLowerCase().lastIndexOf(".p7m"));
    	    } else {
          	if (dignomdoc_p7m.toLowerCase().indexOf(".tsd") >= 0) {
          	dignomdoc_p7m = dignomdoc_p7m.substring(0, dignomdoc_p7m.toLowerCase().lastIndexOf(".tsd"));
          	  }
              }
            }
          }
      }
    } catch (Exception io) {
      livEvento = 3;
      errMsgEvento = io.getMessage();
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.download";
      this.aggiungiMessaggio(request, messageKey);
    }finally{
      LogEvento logEvento =  new LogEvento();
      logEvento.setCodApplicazione(codiceApplicazione);
      logEvento.setLivEvento(livEvento);
      String oggetto = idprg;
      if(iddocdig!=null)
        oggetto+="/" + iddocdig.toString();
      logEvento.setOggEvento(oggetto);
      logEvento.setCodEvento("DOWNLOAD_FILE");
      logEvento.setDescr("Download file");
      logEvento.setErrmsg(errMsgEvento);
      logEvento.setCodProfilo(codiceProfilo);
      logEvento.setIdUtente(idUtente);
      logEvento.setIp(ip);
      try{
        LogEventiUtils.insertLogEventi(logEvento);
      }catch(Exception e){
        logger.error("Errore inaspettato durante la tracciatura su w_logeventi");
      }
    }
    if (logger.isDebugEnabled()) logger.debug("DownloadDocumentoFirmatoAction-runAction: fine metodo");

    if (target != null)
      return mapping.findForward(target);
    else
      return null;
  }
}
