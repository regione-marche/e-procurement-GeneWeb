package it.eldasoft.gene.web.struts;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.xml.sax.InputSource;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.springframework.transaction.TransactionStatus;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.model.FirmaCheck;
import it.eldasoft.gene.web.struts.model.Signature;
import it.eldasoft.gene.web.struts.model.Timestamp;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.maggioli.eldasoft.digital.signature.DigitalSignatureCheckClient;
import it.maggioli.eldasoft.digital.signature.ProviderEnum;
import it.maggioli.eldasoft.digital.signature.model.ResponseCheckSignature;
import it.maggioli.eldasoft.digital.signature.providers.Provider;

public class VerificaFirmaServizioEsterno extends Action {

    private static final String DIGITAL_SIGNATURE_PROVIDER     = "digital-signature-provider";
	private static final String DIGITAL_SIGNATURE_CHECK_URL = "digital-signature-check-url";
	private static final String DIGITAL_SIGNATURE_CHECK_AUTHSECRET = "digital-signature-check-authsecret";
	private static final String DIGITAL_SIGNATURE_CHECK_AUTHUSERNAME = "digital-signature-check-authusername";
	private static final String DIGITAL_SIGNATURE_CHECK_AUTHURL = "digital-signature-check-authurl";
	private final Logger logger = Logger.getLogger(VerificaFirmaServizioEsterno.class);

//	private final extensionsAllowed = 
	private SqlManager sqlManager;
	
	public void setSqlManager(SqlManager sqlManager) {
	    this.sqlManager = sqlManager;
	}    
	
	@Override
	public final ActionForward execute(final ActionMapping mapping, final ActionForm form,
			final HttpServletRequest request, final HttpServletResponse response) throws Exception {

		Long time = System.currentTimeMillis();
		
		logger.debug("Called method at: "+time);
		DynaActionForm dynaForm = (DynaActionForm)form;
		String codiceProfilo = null;
		String ip = null;
		Integer idUtente = null;
		String errMsgEvento = "";
        int livEvento = 1;
        ResponseCheckSignature rcs = null;
        TransactionStatus transazione = this.sqlManager.startTransaction();
        Boolean commitTransaction = true;
        String providerMessage = "";
		try {
		    codiceProfilo = (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);
	        if(request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE) != null){
	          int idUtenteInt = ((ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId();
	          idUtente = new Integer(idUtenteInt);
	        }
	        ip = request.getRemoteAddr();
	        
			FileAllegatoManager fileAllegatoManager = (FileAllegatoManager) UtilitySpring.getBean("fileAllegatoManager", request.getSession(false).getServletContext(), FileAllegatoManager.class);
			BlobFile digogg = fileAllegatoManager.getFileAllegato(dynaForm.getString("idprg"), (Long)dynaForm.get("iddocdig"));
			if(digogg!=null) {
			  Date firmacheckts = new Date();
			  FirmaCheck fc = new FirmaCheck();
			  Provider provider = null;
			  
			  if(!"".equals(StringUtils.stripToEmpty(ConfigManager.getValore(DIGITAL_SIGNATURE_CHECK_URL)))) {
			  
    			  switch (StringUtils.stripToEmpty(ConfigManager.getValore(DIGITAL_SIGNATURE_PROVIDER))) {
                  case "2":
                	providerMessage = "INFOCERT";
                	String urlInfocertVerify = ConfigManager.getValore(DIGITAL_SIGNATURE_CHECK_URL);
                    provider = DigitalSignatureCheckClient.getInstance(ProviderEnum.INFOCERT)
                        .withConfiguration(urlInfocertVerify);
                    
                    rcs = provider.checkDigitalSignature(new ByteArrayInputStream(digogg.getStream()), digogg.getNome());
                    logger.debug("Result: "+rcs);
                    
                    fc = getObjectsByXML(rcs.getXmlbody());
                    break;
                  case "1":
                  	providerMessage = "MAGGIOLI";
                    String authUrl = ConfigManager.getValore(DIGITAL_SIGNATURE_CHECK_AUTHURL);
                    String authUsername = StringUtils.trimToNull(ConfigManager.getValore(DIGITAL_SIGNATURE_CHECK_AUTHUSERNAME));
                    String authSecret = ConfigManager.getValore(DIGITAL_SIGNATURE_CHECK_AUTHSECRET);
                    String providerUrl = ConfigManager.getValore(DIGITAL_SIGNATURE_CHECK_URL);
                    
                    logger.debug("authUrl: "+authUrl);
                    logger.trace("authUsername: "+authUsername);
                    logger.debug("authSecret.isNull: "+StringUtils.isNotBlank(providerUrl));
                    logger.debug("providerUrl: "+providerUrl);
                    
                    provider = DigitalSignatureCheckClient.getInstance(ProviderEnum.MAGGIOLI)
                            .withConfiguration(authUrl, authUsername, authSecret, providerUrl);
                    
                    rcs = provider.checkDigitalSignature(firmacheckts, new ByteArrayInputStream(digogg.getStream()), digogg.getNome());
                    logger.debug("Result: "+rcs);
                    ObjectMapper objectMapper = new ObjectMapper();
                    //per evitare failures nel caso vengano modificati gli output del WS
                    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                    fc = objectMapper.readValue(rcs.getJsonbody(), FirmaCheck.class);
                    
                    break;
                  default:
                    break;
                  }
    			  
    			  if(fc.getSignatures()!=null) {
    			    List<String> listOfInvalidSignatures = fc.getSignatures().parallelStream().filter(el -> !el.isValid()).map(el->{return el.getIndex()+" "+el.getSubjectCommonName();}).collect(Collectors.toList());
    			    fc.setListOfInvalidSignatures(listOfInvalidSignatures);
    			  }
    			  if(fc.getTimestamps()!=null) {
    			    List<String> listOfInvalidTimestamps = fc.getTimestamps().parallelStream().filter(el -> !el.isValid()).map(el->{return el.getTsa();}).collect(Collectors.toList());
    			    fc.setListOfInvalidTimestamps(listOfInvalidTimestamps);
    			  }
  			      dynaForm.set("firmacheckts", firmacheckts);
    			  logger.debug("Result: "+fc.toString());
    			  dynaForm.set("firma", fc);
    			  dynaForm.set("nomeFile", digogg.getNome());
    			  
    			  java.sql.Timestamp dataOraCorrente = new java.sql.Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
    			  String queryUpdate = "update w_docdig set firmacheck = ?,firmacheckts = ? where idprg = ? and iddocdig = ? and (firmacheck is null or firmacheck = ?)";
                  sqlManager.update(queryUpdate,new Object[]{Boolean.TRUE.equals(rcs.getVerified())?"1":"2", dataOraCorrente, dynaForm.getString("idprg"), dynaForm.get("iddocdig").toString(), "2"});
			  }
			}		
		} catch (Exception e) {
			logger.error("Errore: ",e);
			livEvento = 3;
			commitTransaction = false;
		    errMsgEvento = e.getLocalizedMessage();
		} finally {
		  
		  if (transazione != null) {
	        if (Boolean.TRUE.equals(commitTransaction))
	          this.sqlManager.commitTransaction(transazione);
	        else
	          this.sqlManager.rollbackTransaction(transazione);
		  }
	      LogEvento logEvento =  new LogEvento();
	      logEvento.setCodApplicazione("PG");
	      logEvento.setLivEvento(livEvento);
	      String oggetto = dynaForm.getString("idprg");
	      if(oggetto!=null)
	        oggetto+="/" + dynaForm.get("iddocdig").toString();
	      logEvento.setOggEvento(oggetto);
	      logEvento.setCodEvento("VERIFICAFIRMA_FILE");
	      logEvento.setDescr("Richiesta verifica firma file a servizi "+providerMessage);
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
		return mapping.findForward("success");
	}
	
	
	private FirmaCheck getObjectsByXML(String xml) throws DocumentException {
	  FirmaCheck fc = new FirmaCheck();
	  
      Document document = new SAXReader().read(new InputSource(new ByteArrayInputStream(xml.getBytes())));
      List<Node> nodes = document.selectNodes("deSign/signedData/signer");
      
      List<Signature> listSignature = new ArrayList<Signature>();
      
      SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
      
     int index = 1;
      for (Node node : nodes) {
        Signature s = new Signature();
        s.setIndex(index);
        index++;
        s.setSubjectCommonName(node.selectSingleNode("subject/CN").getText());
        s.setSubjectSerialNumber(node.selectSingleNode("subject/SER").getText());
        
        s.setIssuerCommonName(node.selectSingleNode("issuer/CN").getText());
        s.setIssuerOrganization(node.selectSingleNode("issuer/O").getText());

        s.setSerialNumber(node.selectSingleNode("serial").getText());
        
        Date data = null;
        try {
          s.setDesValidityIni(outputFormat.format(inputFormat.parse(node.selectSingleNode("certNotBefore").getText())));
          s.setDesValidityFin(outputFormat.format(inputFormat.parse(node.selectSingleNode("certNotAfter").getText())));
          if(node.selectSingleNode("signedAttributes/signingTime") != null)
            data = outputFormat.parse(outputFormat.format(inputFormat.parse(node.selectSingleNode("signedAttributes/signingTime").getText())));
          else
            data = outputFormat.parse(outputFormat.format(inputFormat.parse(node.selectSingleNode("signingTime").getText())));            
        } catch (ParseException e) {
          logger.error("ParseException errore: ",e);
        }
        s.setSigningTimeISO(data);
        
        s.setSignerDigestAlgDes(node.selectSingleNode("digestAlgorithm").getText());
        s.setValid("OK".equals(node.selectSingleNode("status").getText()));

        
        listSignature.add(s);
      }
      fc.setSignatures(listSignature);
      
      List<Timestamp> listTimestamp = new ArrayList<Timestamp>();
      if(!document.selectNodes("deSign/timeStamp").isEmpty()) {
        Timestamp ts = new Timestamp();
        
        try {
          ts.setTimestamp(outputFormat.parse(outputFormat.format(inputFormat.parse(document.selectSingleNode("deSign/timeStamp/timeStampDate").getText()))));
        } catch (ParseException e) {
          logger.error("ParseException errore: ",e);
        }
        ts.setValid("OK".equals(document.selectSingleNode("deSign/timeStamp/status").getText()));
        ts.setSerial(document.selectSingleNode("deSign/timeStamp/timeStampSerial").getText());
        ts.setTsa(document.selectSingleNode("deSign/timeStamp/issuer/CN").getText());
        List<Node> nodesPolicy = document.selectNodes("deSign/timeStamp/policyInformationList/policyInformation");
        if(!nodesPolicy.isEmpty())
          ts.setPolicyId(nodesPolicy.get(0).selectSingleNode("policyID").getText());
        
        listTimestamp.add(ts);
      }
      fc.setTimestamps(listTimestamp);
      
      return fc;
    }
	

}
