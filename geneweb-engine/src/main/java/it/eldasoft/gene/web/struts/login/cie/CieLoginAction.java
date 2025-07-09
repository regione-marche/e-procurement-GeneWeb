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
package it.eldasoft.gene.web.struts.login.cie;

import java.io.IOException;

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
public class CieLoginAction extends IsUserLoggedAction {

  static Logger  logger = Logger.getLogger(CieLoginAction.class);

  private static final String CIE_AUTHSYSTEM_DEFAULT         = "cieid";
 
  private static final String PROP_SSO_CIE_SERVICEPROVIDER                      = "auth.sso.cie.serviceprovider";
  private static final String PROP_SSO_CIE_SERVICEINDEX                         = "auth.sso.cie.serviceindex";
  private static final String PROP_SSO_CIE_AUTHLEVEL                            = "auth.sso.cie.authlevel";

  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("execute: inizio metodo");
    }
      
    String target = CostantiGeneraliStruts.FORWARD_OK.concat("PrepareLogin");

    String url = StringUtils.stripToNull(ConfigManager.getValore(CostantiGenerali.PROP_SSO_CIE_WS_AUTHSERVICESPID_URL));
    String authSystem = CIE_AUTHSYSTEM_DEFAULT;
    String serviceProvider = StringUtils.stripToNull(ConfigManager.getValore(PROP_SSO_CIE_SERVICEPROVIDER));
    String serviceIndex = StringUtils.stripToNull(ConfigManager.getValore(PROP_SSO_CIE_SERVICEINDEX));
    String authLevel = StringUtils.stripToNull(ConfigManager.getValore(PROP_SSO_CIE_AUTHLEVEL));
    String authLevelUrl = StringUtils.stripToNull(CostantiGenerali.PROP_SSO_AUTHLEVEL_URL);

    String messageKey = "";

    // validazione dei parametri...da COMPLETARE
    if(StringUtils.isEmpty(url)) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.login.sso.noParametro";
      this.aggiungiMessaggio(request, messageKey, CostantiGenerali.PROP_SSO_CIE_WS_AUTHSERVICESPID_URL);
    }

    if(StringUtils.isEmpty(serviceProvider)) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.login.sso.noParametro";
      this.aggiungiMessaggio(request, messageKey,PROP_SSO_CIE_SERVICEPROVIDER);
    }

    if(StringUtils.isEmpty(serviceIndex)) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.login.sso.noParametro";
      this.aggiungiMessaggio(request, messageKey,PROP_SSO_CIE_SERVICEINDEX);
    }

    if(StringUtils.isEmpty(authLevel)) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.login.sso.noParametro";
      this.aggiungiMessaggio(request, messageKey,PROP_SSO_CIE_AUTHLEVEL);
    }

    if(StringUtils.isEmpty(authLevelUrl)) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.login.sso.noParametro";
      this.aggiungiMessaggio(request, messageKey,CostantiGenerali.PROP_SSO_AUTHLEVEL_URL);
    }

    if(CostantiGeneraliStruts.FORWARD_OK.concat("PrepareLogin").equals(target)) {


      AuthServiceProxy authServiceProxy = new AuthServiceProxy();
      authServiceProxy.setEndpoint(url);
      AuthService ws = authServiceProxy.getAuthService();

      /**
       * backurl          url di ritorno dell’ SP
       * authSystem       cie (default)
       * authId           token ottenuto via soap getAuthId (validità temporale limitata)
       * serviceProvider  alias configurato su AuthserviceCIE
       * authLevel        definisce L1 L2 L3 di CIE https://www.spid.gov.it/SpidL1
       * idp              entityID ricavato dai metadata dell’IDP
       */
      String backUrl =  UtilityWeb.calculateCompleteServerURL(request, "/CieLoginResponse.do");

      // richiesta del token temporaneo al sevizio CIE e salvataggio in sessione per il login...
      String authId = ws.getAuthId();
      request.getSession().setAttribute(CostantiGenerali.SESSION_ID_CIE_AUTHID, authId);

      // invio della richiesta di login al servizio CIE...
      int i = url.indexOf("/services/");
      url = (i > 0 ? url.substring(0, i) : url);

      String urlLogin = url + "/auth.jsp" +
      "?backUrl=" + backUrl +
      "&authSystem=" + authSystem +
      "&authId=" + authId +
      "&serviceProvider=" + serviceProvider +
      "&serviceIndex=" + serviceIndex +
      "&authLevel=" + authLevelUrl + authLevel;

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
