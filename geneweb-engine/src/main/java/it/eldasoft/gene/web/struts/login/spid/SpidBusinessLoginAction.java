/*
 * Created on 03-oct-2019
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.login.spid;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.eldasoft.utils.utility.UtilityWeb;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.cedaf.authservice.service.AuthService;
import it.cedaf.authservice.service.AuthServiceProxy;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.login.IsUserLoggedAction;
import it.eldasoft.utils.properties.ConfigManager;

/**
 * Action per il popolamento del request con redirect successiva alla pagina di login.
 *
 * @author Manuel Bridda
 */
public class SpidBusinessLoginAction extends IsUserLoggedAction {

  static Logger  logger = Logger.getLogger(SpidBusinessLoginAction.class);

  private static final String SPID_AUTHSYSTEM_DEFAULT         = "spid";
 
  private static final String PROP_SSO_SPIDBUSINESS_SERVICEPROVIDER                      = "auth.sso.spidbusiness.serviceprovider";
  private static final String PROP_SSO_SPIDBUSINESS_SERVICEINDEX                         = "auth.sso.spidbusiness.serviceindex";
  private static final String PROP_SSO_SPIDBUSINESS_AUTHLEVEL                            = "auth.sso.spidbusiness.authlevel";
  private static final String PROP_SSO_SPIDBUSINESS_AUTHPURPOSE                          = "auth.sso.spidbusiness.authpurpose";

  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("execute: inizio metodo");
    }
    
    String provider = PROP_SSO_SPIDBUSINESS_SERVICEPROVIDER;
    
    String codein = (String) request.getSession().getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
    if(StringUtils.isNotBlank(codein) && "1".equals(ConfigManager.getValore(CostantiGenerali.PROP_LOGIN_MULTI_ENTE)))
      provider+="."+codein;

    String target = CostantiGeneraliStruts.FORWARD_OK.concat("PrepareLogin");

    String url = StringUtils.stripToNull(ConfigManager.getValore(CostantiGenerali.PROP_SSO_SPIDBUSINESS_WS_AUTHSERVICESPID_URL));

    String authSystem = SPID_AUTHSYSTEM_DEFAULT;

    String serviceProvider = StringUtils.stripToNull(ConfigManager.getValore(provider));
    
    String serviceIndex = StringUtils.stripToNull(ConfigManager.getValore(PROP_SSO_SPIDBUSINESS_SERVICEINDEX));

    String authLevel = StringUtils.stripToNull(ConfigManager.getValore(PROP_SSO_SPIDBUSINESS_AUTHLEVEL));

    String authLevelUrl = StringUtils.stripToNull(CostantiGenerali.PROP_SSO_AUTHLEVEL_URL);
    
    String authPurpose = StringUtils.stripToNull(ConfigManager.getValore(PROP_SSO_SPIDBUSINESS_AUTHPURPOSE));

    String idProvider = request.getParameter("idp");
    String messageKey = "";

    // validazione dei parametri...da COMPLETARE
    if(StringUtils.isEmpty(url)) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.login.sso.noParametro";
      this.aggiungiMessaggio(request, messageKey, CostantiGenerali.PROP_SSO_SPIDBUSINESS_WS_AUTHSERVICESPID_URL);
    }

    if(StringUtils.isEmpty(serviceProvider)) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.login.sso.noParametro";
      this.aggiungiMessaggio(request, messageKey,provider);
    }

    if(StringUtils.isEmpty(serviceIndex)) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.login.sso.noParametro";
      this.aggiungiMessaggio(request, messageKey,PROP_SSO_SPIDBUSINESS_SERVICEINDEX);
    }

    if(StringUtils.isEmpty(authLevel)) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.login.sso.noParametro";
      this.aggiungiMessaggio(request, messageKey,PROP_SSO_SPIDBUSINESS_AUTHLEVEL);
    }

    if(StringUtils.isEmpty(authLevelUrl)) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.login.sso.noParametro";
      this.aggiungiMessaggio(request, messageKey,CostantiGenerali.PROP_SSO_AUTHLEVEL_URL);
    }

    if(StringUtils.isEmpty(idProvider)) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.login.sso.noParametro";
      this.aggiungiMessaggio(request, messageKey, "idp");
    }
    
    if (!Pattern.matches("(?i)https?://(www.)?[a-z.\\d/:_-]+\\??(&?[a-z\\d]+=[a-z/\\.\\d]*)*", idProvider)) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.login.sso.noParametro";
      this.aggiungiMessaggio(request, messageKey, "idp");
    }

    if(CostantiGeneraliStruts.FORWARD_OK.concat("PrepareLogin").equals(target)) {

      AuthServiceProxy authServiceProxy = new AuthServiceProxy();
      authServiceProxy.setEndpoint(url);
      AuthService ws = authServiceProxy.getAuthService();

      /**
       * backurl          url di ritorno dell’ SP
       * authSystem       spid (default)
       * authId           token ottenuto via soap getAuthId (validità temporale limitata)
       * serviceProvider  alias configurato su AuthserviceSPID
       * authLevel        definisce L1 L2 L3 di SPID https://www.spid.gov.it/SpidL1
       * idp              entityID ricavato dai metadata dell’IDP
       * authPurpose      definisce il purpose P, LP, PG, PF, PX
       */
      String backUrl =  UtilityWeb.calculateCompleteServerURL(request, "/SpidBusinessLoginResponse.do");

      // richiesta del token temporaneo al sevizio SPID e salvataggio in sessione per il login...
      String authId = ws.getAuthId();
      request.getSession().setAttribute(CostantiGenerali.SESSION_ID_SPID_AUTHID, authId);

      // invio della richiesta di login al servizio SPID...
      int i = url.indexOf("/services/");
      url = (i > 0 ? url.substring(0, i) : url);

      String urlLogin = url + "/auth.jsp" +
      "?backUrl=" + backUrl +
      "&authSystem=" + authSystem +
      "&authId=" + authId +
      "&serviceProvider=" + serviceProvider +
      "&serviceIndex=" + serviceIndex +
      "&authLevel=" + authLevelUrl + authLevel +
      "&authPurpose=" + authPurpose +
      "&idp=" + idProvider;

      request.setAttribute("url", urlLogin);

    } else {
      // si annulla qualsiasi effetto nella sessione in quanto occorre rientrare e navigare da zero l'interfaccia senza mantenere eventuali
      // selezioni errate fatte in precedenza (es: in cfg multiente, aver selezionato un ente per il quale non esistono le configurazioni
      // corrette)
      request.getSession().invalidate();
    }

    if (logger.isDebugEnabled()) {
      logger.debug("execute: fine metodo");
    }

    return mapping.findForward(target);
  }


}
