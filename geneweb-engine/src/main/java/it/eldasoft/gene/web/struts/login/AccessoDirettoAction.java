/*
 * Created on 3-mar-2009
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
import it.eldasoft.gene.commons.web.domain.CostantiIntegrazioneKronos;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Azione Struts che consente l'autenticazione dell'utente all'inserimento dei
 * dati nel form di login costituito dalla username dell'utente e dalla sua
 * password.<br>
 * Questa classe non estende la classe ActionBase in quanto non è legata ad una
 * funzionalità specifica dato che l'autenticazione deve avvenire sempre
 * all'interno di ogni applicazione web. Difatti, ne duplica alcune sezioni,
 * quali i resource bundle e i metodi per la scrittura di messaggi di errore nel
 * request.
 *
 * @author Stefano.Sabbadin
 */
public class AccessoDirettoAction extends LoginAction {

  /** Logger Log4J di classe */
  static Logger               logger                                     = Logger.getLogger(AccessoDirettoAction.class);

  /**
   * indica, se valorizzata a 1, che è stato abilitato l'accesso diretto
   * bypassando la procedura di login in quanto si accede mediante preventiva
   * autenticazione esterna
   */
  private static final String PROP_ACCESSO_DIRETTO                       = "it.eldasoft.accessoDiretto";

  /**
   * Property di configurazione, contenente l'elenco dei nomi dei campi hidden
   * obbligatori nei dati parametrici dell'utente, separati da ";"
   */
  private static final String PROP_PARAMETRI_OBBLIGATORI_ACCESSO_DIRETTO = "it.eldasoft.accessoDiretto.parametri.obbligatori";

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

    // target di default, da modificare nel momento in cui si verificano dei
    // problemi
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    String username = request.getParameter("username");

    if ("1".equals(ConfigManager.getValore(PROP_ACCESSO_DIRETTO))) {
      target = this.testSkipProfili(request, target);

      String[] nomiParametriObbligatori = UtilityStringhe.deserializza(
          UtilityStringhe.convertiStringaVuotaInNull(ConfigManager.getValore(AccessoDirettoAction.PROP_PARAMETRI_OBBLIGATORI_ACCESSO_DIRETTO)),
          ';');
      String parametroEsterno = null;
      if (nomiParametriObbligatori != null) {

        Map<String, String> hash = null;
        if (CostantiIntegrazioneKronos.INTEGRAZIONE_KRONOS.equals(ConfigManager.getValore(CostantiGenerali.PROP_INTEGRAZIONE))) {
          // se l'accesso diretto e' dovuto all'integrazione con Kronos, i parametri si prendono dal DB
          hash = this.loginManager.getDatiUtenteKronos(new Integer(request.getParameter("id")));
          // e si prende lo username dal parametro ricevuto, che non e' presente nel request
          username = hash.get("username");
        } else {
          // altrimenti i parametri si prendono direttamente dal request
          hash = new HashMap<String, String>();
          for (int i = 0; i < nomiParametriObbligatori.length; i++) {
            hash.put(nomiParametriObbligatori[i], request.getParameter(nomiParametriObbligatori[i]));
          }
        }

        for (int i = 0; i < nomiParametriObbligatori.length; i++) {
          parametroEsterno = hash.get(nomiParametriObbligatori[i]);
          if (parametroEsterno == null) {
            // se il parametro è obbligatorio ma non viene passato, allora
            // si blocca l'accesso all'applicativo
            target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
            messageKey = "errors.accessoDiretto.parametriObbligatoriMancanti";
            logger.error(UtilityStringhe.replaceParametriMessageBundle(
                this.resBundleGenerale.getString(messageKey),
                new String[] { nomiParametriObbligatori[i] }));
            this.aggiungiMessaggio(request, messageKey,
                nomiParametriObbligatori[i]);
            // al primo parametro obbligatorio mancante termino i controlli ed
            // esco con errore
            break;
          }
        }
      }

      if (!CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE.equals(target)) {
        target = this.checkLogin(username, null,
            request, false, target);
      }

    } else {
      // l'accesso diretto non è previsto, segnalo l'errore perchè è un
      // tentativo di accesso illecito
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.accessoDiretto.nonAttivo";
      logger.error(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: fine metodo");
    }

    return mapping.findForward(target);
  }

  @Override
  protected Account getAccount(String username, String password)
      throws CriptazioneException, DataAccessException, SqlComposerException {
    Account account = null;
    account = this.loginManager.getAccountByLogin(username);
    return account;
  }

  @Override
  protected String verifyAccountLdap(HttpServletRequest request,
      String username, String password, Account account, String target)
      throws Exception {
    // non viene eseguito alcun controllo in quanto l'autenticazione si ritiene
    // sia già stata fatta esternamente all'applicativo, e non va nemmeno emesso
    // un errore nel caso di password non valorizzata (che è proprio il nostro
    // caso, in cui basta l'utenza per accedere al sistema)
    return target;
  }

  protected String verifyPassword(HttpServletRequest request, String password,
      Account account, String target) {
    // non va effettuato alcun controllo sulla password, in quanto la password
    // non viene ricevuta da chi richiede l'accesso diretto, e l'autenticazione
    // si intende accettata
    return target;
  }

}