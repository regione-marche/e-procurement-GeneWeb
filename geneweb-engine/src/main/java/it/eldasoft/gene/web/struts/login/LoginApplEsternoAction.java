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
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Azione Struts che consente l'accesso con un utente associato ad un
 * applicativo esterno mediante prelevamento della sua definizione (login) dal
 * file di properties.<br>
 * Questa classe non estende la classe ActionBase in quanto non è legata ad una
 * funzionalità specifica dato che l'autenticazione deve avvenire sempre
 * all'interno di ogni applicazione web. Difatti, ne duplica alcune sezioni,
 * quali i resource bundle e i metodi per la scrittura di messaggi di errore nel
 * request.
 * 
 * @author Stefano.Sabbadin
 */
public class LoginApplEsternoAction extends Action {

  /** Logger Log4J di classe */
  static Logger            logger            = Logger.getLogger(LoginApplEsternoAction.class);

  /**
   * Resource bundle parte generale dell'applicazione (modulo GENE)
   */
  protected ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  /**
   * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward execute(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("execute: inizio metodo");
    }

    String target = CostantiGeneraliStruts.FORWARD_OK;
    
    HttpSession session = request.getSession();
    String sentinella = (String) session.getAttribute(CostantiGenerali.SENTINELLA_SESSION_TIMEOUT);
    // l'accesso da applicativo esterno avviene sempre mediante lo stesso
    // utente, per cui si bypassa l'autenticazione e la selezione del profilo e
    // si va direttamente all'apertura della pagina con il medesimo utente già
    // loggato.
    // inoltre può verificarsi che l'utente abbia già l'applicativo aperto con
    // la sua utenza, per cui si sfrutta tale utenza/sessione per l'accesso
    // all'applicativo
    if (sentinella != null) target = "apriPagina";
    else {

        String account = ConfigManager.getValore(CostantiGenerali.PROP_ACCOUNT_ACCESSO_APPLICATIVO_ESTERNO);
        
        if (account != null && account.trim().length() > 0) {
          // se l'applicativo prevede l'accesso da applicativo esterno, allora si
          // estraggono i dati per l'accesso
          request.setAttribute("username", account);
          request.setAttribute("password", account);
        } else {
          // l'accesso da applicativo esterno non è previsto, segnalo l'errore
          // perchè è un tentativo di accesso illecito
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          String messageKey = "errors.loginApplicativoEsterno.nonAttivo";
          logger.error(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        }    	
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