/*
 * Created on 16-giu-2006
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
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Action per il popolamento del request con il messaggio di "sessione scaduta"
 * e la redirect successiva alla pagina di login.
 *
 * @author Stefano.Sabbadin
 */
public class SessionTimeOutAction extends Action {

  /** Logger Log4J di classe */
  static Logger  logger = Logger.getLogger(SessionTimeOutAction.class);

  /**
   * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("execute: inizio metodo");
    }

    // target di default, da modificare nel momento in cui si verificano dei
    // problemi
    String target = CostantiGeneraliStruts.FORWARD_LOGIN;

    String messageKey = null;
    messageKey = "errors.session.timeOut";
    this.aggiungiMessaggio(request, messageKey);

    String propProtSSO = StringUtils.stripToNull(ConfigManager.getValore(CostantiGenerali.PROP_SSO_PROTOCOLLO));
    String propDefURL = StringUtils.stripToNull(ConfigManager.getValore(CostantiGenerali.PROP_SSO_LOGOUT_URL));
    //Per sistemi con autenticazione SSO si fa la redirect condisconnessione
    // anche della sessione remota (1-Shibboleth; 2-Cohesion; 3-SSOBart)
    if(propProtSSO != null && !"0".equals(propProtSSO) && propDefURL != null){
      String url = propDefURL;
      request.setAttribute("url", url);
      target = "logoutSSO";
    }

    if (logger.isDebugEnabled()) {
      logger.debug("execute: fine metodo");
    }

    return mapping.findForward(target);
  }

  /**
   * Crea un ActionMessages nel request, popolato con il messaggio individuato
   * dalla chiave in input
   *
   * @param request
   *        request http
   * @param chiave
   *        chiave del messaggio da inserire
   */
  public void aggiungiMessaggio(HttpServletRequest request, String chiave) {
    String tipoMessaggio = ActionBase.getTipoMessaggioFromChiave(chiave);

    ActionMessages errors = new ActionMessages();
    errors.add(tipoMessaggio, new ActionMessage(chiave));
    if (!errors.isEmpty()) this.addMessages(request, errors);
  }

}
