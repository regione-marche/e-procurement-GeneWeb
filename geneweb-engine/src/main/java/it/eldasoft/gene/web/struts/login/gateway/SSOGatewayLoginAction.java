package it.eldasoft.gene.web.struts.login.gateway;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.login.IsUserLoggedAction;
import it.eldasoft.utils.properties.ConfigManager;

public class SSOGatewayLoginAction extends IsUserLoggedAction {
  
  private static final Logger LOGGER = Logger.getLogger(SSOGatewayLoginAction.class);
  
  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form, 
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("execute: inizio metodo");
    
    final String url = StringUtils.stripToNull(ConfigManager
        .getValore(CostantiGenerali.PROP_SSO_GATEWAY_WS_AUTHSERVICE_URL));
    final String endpoint = StringUtils.stripToNull(ConfigManager
        .getValore(CostantiGenerali.PROP_SSO_GATEWAY_ENDPOINT));
    final String clientId = StringUtils.stripToNull(ConfigManager
        .getValore(CostantiGenerali.PROP_SSO_GATEWAY_CLIENTID));
    
    // Validazione parametri
    final String target = parametersValidation(request, url, endpoint, clientId);
    if (target.equals(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE))
      return mapping.findForward(target);
    
    // Esecuzione del metodo
    final String loginUrl = url + endpoint + "/" + clientId;
    
    request.setAttribute("url", loginUrl);
    
    if (LOGGER.isDebugEnabled()) LOGGER.debug("execute: fine metodo");

    return mapping.findForward(target);
  }
  
  private final String parametersValidation(final HttpServletRequest request, 
      final String url, final String endpoint, final String clientId) {
    final String target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
    final String messageKey = "errors.login.sso.noParametro";
    boolean error = false;
    
    if (StringUtils.isEmpty(url)) {
      this.aggiungiMessaggio(request, messageKey, "url");
      error = true;
    }

    if (StringUtils.isEmpty(endpoint)) {
      this.aggiungiMessaggio(request, messageKey, "endpoint");
      error = true;
    }

    if (StringUtils.isEmpty(clientId)) {
      this.aggiungiMessaggio(request, messageKey, "clientId");
      error = true;
    }
    
    if (error) {
      return target;
    }
    
    return CostantiGeneraliStruts.FORWARD_OK.concat("PrepareLogin");
  }
  
}
