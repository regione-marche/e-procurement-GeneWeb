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

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

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

    HttpSession session = request.getSession();
    ProfiloUtente utente = (ProfiloUtente) session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    String propProtSSO = StringUtils.stripToNull(ConfigManager.getValore(CostantiGenerali.PROP_SSO_PROTOCOLLO));
    String propDefURL;
    if ("6".equals(propProtSSO)) {
    	final String url = StringUtils.stripToNull(ConfigManager
	        .getValore(CostantiGenerali.PROP_SSO_GATEWAY_WS_AUTHSERVICE_URL));
	    final String endpoint = StringUtils.stripToNull(ConfigManager
	        .getValore(CostantiGenerali.PROP_SSO_GATEWAY_ENDPOINT));
	    final String clientId = StringUtils.stripToNull(ConfigManager
	        .getValore(CostantiGenerali.PROP_SSO_GATEWAY_CLIENTID));
	    
	    propDefURL = url + endpoint + "/" + clientId;
    } else {
    	propDefURL = StringUtils.stripToNull(ConfigManager.getValore(CostantiGenerali.PROP_SSO_LOGOUT_URL));
    }
    //Per sistemi con autenticazione SSO si fa la redirect con disconnessione
    // anche della sessione remota
    if(propProtSSO != null && !"0".equals(propProtSSO) && propDefURL != null){
      String url = propDefURL;
      request.setAttribute("url", url);
      target = "logoutSSO";
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
