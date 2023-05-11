/*
 * Created on 20-giu-2018
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.web.struts.login.openid;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.web.struts.login.LoginAction;
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
 * Action per la gestione della risposta di OpenID.
 *
 * @author Stefano.Sabbadin
 */

public class OpenIDLoginAction extends LoginAction {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(OpenIDLoginAction.class);

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    Account account = null;

    String messageKey = "";

    this.logHeaderParameters(request);

    // lettura della configurazione con i parametri necessari per l'autenticazione
    String attributeKeyLogin = ConfigManager.getValore(CostantiGenerali.PROP_SSO_ATTRIBUTE_LOGIN);
    String attributeKeyFirstName = ConfigManager.getValore(CostantiGenerali.PROP_SSO_ATTRIBUTE_FIRST_NAME);
    String attributeKeyLastName = ConfigManager.getValore(CostantiGenerali.PROP_SSO_ATTRIBUTE_LAST_NAME);
    String attributeKeyDescription = ConfigManager.getValore(CostantiGenerali.PROP_SSO_ATTRIBUTE_DESCRIPTION);
    String attributeKeyMail = ConfigManager.getValore(CostantiGenerali.PROP_SSO_ATTRIBUTE_EMAIL);
    String attributeKeyFiscalCode = ConfigManager.getValore(CostantiGenerali.PROP_SSO_ATTRIBUTE_FISCAL_CODE);

    // lettura dei dati ricevuti e significativi
    String login = request.getHeader(attributeKeyLogin);
    String nome = request.getHeader(attributeKeyFirstName);
    String cognome = request.getHeader(attributeKeyLastName);
    String denominazione = request.getHeader(attributeKeyDescription);
    String codiceFiscale = request.getHeader(attributeKeyFiscalCode);
    String email = request.getHeader(attributeKeyMail);

    String target = CostantiGeneraliStruts.FORWARD_OK;

    try {

      target = this.testSkipProfili(request, target);

      // verifica se l'utente viene individuato ed e' presente in base dati
      account = this.loginManager.getAccountByLogin(login);

      if (account != null) {
        target = this.checkLogin(account.getLogin(), account.getPassword(), request, true, target);
      } else {
        // Settaggio dei vari data da usare in preinizializzazione della form di registrazione

        // se arriva nome e cognome si settano separatamente, altrimenti nel nome si memorizza la denominazione
        request.setAttribute("nome", nome != null ? nome : denominazione);
        request.setAttribute("cognome", cognome);
        request.setAttribute("login", login);
        request.setAttribute("codfisc", codiceFiscale);
        request.setAttribute("email", email);
        // Attributo che in questo caso indica presenza di integrazione SSO
        request.setAttribute("flagLdap", "3");
        if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_ATTIVA_FORM_REGISTRAZIONE))) {
          return this.forwardToRegistrationForm(request);
        } else {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.login.unknown";
          this.aggiungiMessaggio(request, messageKey, login);
        }
      }
    } catch (Exception e) {
      logger.error("Errore in fase di individuazione dell'utente (login="
          + login
          + ", nome="
          + nome
          + ", cognome="
          + cognome
          + ", denominazione="
          + denominazione
          + ", codice fiscale="
          + codiceFiscale
          + ", email="
          + email
          + ")", e);
      String error = "Attenzione: C'è stato un problema con la richiesta di autenticazione, si prega di riprovare più tardi o di contattare l'assistenza in quanto l'utente può non disporre dei permessi necessari al login.\n\nGrazie";
      request.setAttribute("error", error);
      messageKey = "errors.login.sso.noAutenticazione";
      this.aggiungiMessaggio(request, messageKey, login);
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
    }

    return mapping.findForward(target);

  }

}
