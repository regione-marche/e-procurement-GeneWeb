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

import it.cedaf.authservice.service.AuthService;
import it.cedaf.authservice.service.AuthServiceProxy;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.login.IsUserLoggedAction;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per il popolamento del request con redirect successiva alla pagina di login.
 *
 * @author Cristian.Febas
 */
public class SpidLoginAction extends IsUserLoggedAction {

  static Logger  logger = Logger.getLogger(SpidLoginAction.class);

  private static final String SESSION_ID_SPID_AUTHID          = "ACTION_SPID_AUTHID";
  private static final String SPID_AUTHSYSTEM_DEFAULT         = "spid";

  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("execute: inizio metodo");
    }

    String target = CostantiGeneraliStruts.FORWARD_OK.concat("PrepareLogin");

    String url = StringUtils.stripToNull(ConfigManager.getValore(CostantiGenerali.PROP_SSO_SPID_WS_AUTHSERVICESPID_URL));

    String authSystem = SPID_AUTHSYSTEM_DEFAULT;

    String serviceProvider = StringUtils.stripToNull(ConfigManager.getValore(CostantiGenerali.PROP_SSO_SPID_SERVICEPROVIDER));

    String authLevel = StringUtils.stripToNull(ConfigManager.getValore(CostantiGenerali.PROP_SSO_SPID_AUTHLEVEL));

    String authLevelUrl = StringUtils.stripToNull(ConfigManager.getValore(CostantiGenerali.PROP_SSO_SPID_AUTHLEVEL_URL));

    String idProvider = request.getParameter("idp");
    String messageKey = "";

    // validazione dei parametri...da COMPLETARE
    if(StringUtils.isEmpty(url)) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.login.sso.noParametro";
      this.aggiungiMessaggio(request, messageKey, "url");
    }

    if(StringUtils.isEmpty(serviceProvider)) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.login.sso.noParametro";
      this.aggiungiMessaggio(request, messageKey,"serviceProvider");
    }

    if(StringUtils.isEmpty(authLevel)) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.login.sso.noParametro";
      this.aggiungiMessaggio(request, messageKey,"authLevel");
    }

    if(StringUtils.isEmpty(authLevelUrl)) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.login.sso.noParametro";
      this.aggiungiMessaggio(request, messageKey,"authLevelUrl");
    }

    if(StringUtils.isEmpty(idProvider)) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.login.unknown";
      this.aggiungiMessaggio(request, messageKey);
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
       */
      String backUrl =  request.getScheme() + "://" +
                        request.getServerName() + ":" +
                        request.getServerPort() +
                        request.getContextPath() + "/SpidLoginResponse.do";

      // richiesta del token temporaneo al sevizio SPID e salvataggio in sessione per il login...
      String authId = ws.getAuthId();
      request.getSession().setAttribute(SESSION_ID_SPID_AUTHID, authId);

      // invio della richiesta di login al servizio SPID...
      int i = url.indexOf("/services/");
      url = (i > 0 ? url.substring(0, i) : url);

      String urlLogin = url + "/auth.jsp" +
      "?backUrl=" + backUrl +
      "&authSystem=" + authSystem +
      "&authId=" + authId +
      "&serviceProvider=" + serviceProvider +
      "&authLevel=" + authLevelUrl + authLevel +
      "&idp=" + idProvider;

      request.setAttribute("url", urlLogin);

    }

    if (logger.isDebugEnabled()) {
      logger.debug("execute: fine metodo");
    }

    return mapping.findForward(target);
  }


}
