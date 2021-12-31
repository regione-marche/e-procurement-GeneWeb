/*
 * Created on 25-lug-2008
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
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Azione Struts che consente l'accesso come utente anonimo mediante
 * prelevamento della sua definizione (login) dal file di properties.<br>
 * Questa classe non estende la classe ActionBase in quanto non è legata ad una
 * funzionalità specifica dato che l'autenticazione deve avvenire sempre
 * all'interno di ogni applicazione web. Difatti, ne duplica alcune sezioni,
 * quali i resource bundle e i metodi per la scrittura di messaggi di errore nel
 * request.
 * 
 * @author Stefano.Sabbadin
 */
public class LoginAnonimoAction extends IsUserLoggedAction {

  /** Logger Log4J di classe */
  static Logger               logger                  = Logger.getLogger(LoginAnonimoAction.class);

  /**
   * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: inizio metodo");
    }

    String target = CostantiGeneraliStruts.FORWARD_OK;
    
    if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_ATTIVA_ACCESSO_ANONIMO))) {
      // se l'applicativo prevede l'accesso anonimo, allora si estraggono i dati per l'accesso anonimo
      String account = ConfigManager.getValore(CostantiGenerali.PROP_ACCOUNT_ACCESSO_ANONIMO);
      request.setAttribute("username", account);
      request.setAttribute("password", account);
    } else {
      // l'accesso anonimo non è previsto, segnalo l'errore perchè è un
      // tentativo di accesso illecito
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      String messageKey = "errors.loginAnonimo.nonAttivo";
      logger.error(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: fine metodo");
    }

    return mapping.findForward(target);
  }

}