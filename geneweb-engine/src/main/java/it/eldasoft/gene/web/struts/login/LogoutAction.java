/*
 * Created on 13-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.eldasoft.utils.utility.UtilityWeb;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.cedaf.authservice.service.AuthService;
import it.cedaf.authservice.service.AuthServiceProxy;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.AccountSSO;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Azione per eseguire il logout dall'applicazione
 *
 * @author Stefano.Sabbadin
 */
public class LogoutAction extends ActionBaseNoOpzioni {

  /** logger Log4J di classe */
  static Logger            logger            = Logger.getLogger(LogoutAction.class);

  /**
   * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: inizio metodo");
    }

    DataSourceTransactionManagerBase.setRequest(request);

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_LOGIN;
    if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_LOGIN_MULTI_ENTE))) {
      if (this.getMessages(request).isEmpty()) {
        // in configurazione multiente, il processo riparte prima di tutto dalla scelta ente
        target = "selezioneInizialeEnte";        
      } else {
        // in caso di errore verificatosi in fase di accesso, e logout forzato, si manda alla pagina di errore generale per visualizzare un messaggio
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      }
    }
    
    HttpSession session = request.getSession();
    ProfiloUtente utente = (ProfiloUtente) session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    AccountSSO accountSSO = (AccountSSO) request.getSession().getAttribute(
        CostantiGenerali.ID_ATTRIBUTO_SESSIONE_ACCOUNT_SSO);
    
    String provider= "";
    if(accountSSO != null) {
      provider = UtilityStringhe.convertiNullInStringaVuota(accountSSO.getLoginProvider());
    }
    String propDefURL = null;
    String urlWS = null;
    
    switch (provider) {
      case CostantiGenerali.PROP_SSO_WEBSERVER1:
        propDefURL = ConfigManager.getValore(CostantiGenerali.PROP_AUTH_SSO_WEBSERVER_LOGOUT1);
        break;
      case CostantiGenerali.PROP_SSO_WEBSERVER2:
        propDefURL = ConfigManager.getValore(CostantiGenerali.PROP_AUTH_SSO_WEBSERVER_LOGOUT2);
        break;
      case CostantiGenerali.PROP_SSO_WEBSERVER3:
        propDefURL = ConfigManager.getValore(CostantiGenerali.PROP_AUTH_SSO_WEBSERVER_LOGOUT3);
        break;
      case CostantiGenerali.PROP_SSO_GATEWAY:
        final String url = StringUtils.stripToNull(ConfigManager
            .getValore(CostantiGenerali.PROP_SSO_GATEWAY_WS_AUTHSERVICE_URL));
        final String endpoint = StringUtils.stripToNull(ConfigManager
          .getValore(CostantiGenerali.PROP_SSO_GATEWAY_ENDPOINT));
  	    final String clientId = StringUtils.stripToNull(ConfigManager
  	        .getValore(CostantiGenerali.PROP_SSO_GATEWAY_CLIENTID));
  	    
  	    propDefURL = url + endpoint + "/" + clientId;
  	    break;
      case CostantiGenerali.PROP_SSO_COHESION:
        propDefURL = ConfigManager.getValore(CostantiGenerali.PROP_AUTH_SSO_COHESION_LOGOUT);
        break;
      case CostantiGenerali.PROP_SSO_CIE:
        urlWS = StringUtils.stripToNull(ConfigManager.getValore(CostantiGenerali.PROP_SSO_CIE_WS_AUTHSERVICESPID_URL));
        break;
      case CostantiGenerali.PROP_SSO_SPID:
        urlWS = StringUtils.stripToNull(ConfigManager.getValore(CostantiGenerali.PROP_SSO_SPID_WS_AUTHSERVICESPID_URL));
        break;
      case CostantiGenerali.PROP_SSO_SPIDBUSINESS:
        urlWS = StringUtils.stripToNull(ConfigManager.getValore(CostantiGenerali.PROP_SSO_SPIDBUSINESS_WS_AUTHSERVICESPID_URL));
        break;
  	  default:
    	break;
    }
    if(urlWS != null && accountSSO.getAuthId()!=null) {//Per SPID e CIE 
      AuthServiceProxy authServiceProxy = new AuthServiceProxy();
      authServiceProxy.setEndpoint(urlWS);
      AuthService ws = authServiceProxy.getAuthService();

      String backUrl = UtilityWeb.calculateCompleteServerURL(request, null);
      propDefURL = ws.singleSignOut(accountSSO.getAuthId())+"&backUrl="+backUrl;
    }
    //Per sistemi con autenticazione SSO si fa la redirect con disconnessione
    // anche della sessione remota
    if(accountSSO != null && propDefURL != null){
      String url = propDefURL;
      request.setAttribute("url", url);
		//Se arriviamo da un errore facciamo il redirect alla pagina di errore
    	if ("error".equals(target)) {
			//popolo la variabile per riprovare il login
			request.setAttribute("ritentaAccesso", "true");
    	} else {
			target = "logoutSSO";
    	}
      
    }


    if (utente != null) {
      // l'utente è diverso da null solo se la sessione non è già scaduta,
      // quindi eseguo il test prima di emettere il messaggio e invalidare la sessione
      if (logger.isInfoEnabled())
        logger.info(this.resBundleGenerale.getString("info.logout.ok").replaceAll(
            UtilityStringhe.getPatternParametroMessageBundle(0),
            utente.getLogin()));

      session.invalidate();
    }

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: fine metodo");
    }

    return mapping.findForward(target);
  }
}
