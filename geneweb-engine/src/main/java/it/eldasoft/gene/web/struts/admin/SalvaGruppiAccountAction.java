/*
 * Created on 17-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action che controlla le operazioni - di apertura della pagina per la
 * visualizzazione delle associazioni tra gli utenti ed il gruppo in analisi -
 * di apertura della pagina per la modifica delle associazioni tra gli utenti ed
 * il gruppo in analisi
 * 
 * @author Luca.Giacomazzo
 */
public class SalvaGruppiAccountAction extends AbstractActionBaseAdmin {

  /** Logger Log4J di classe */
  static Logger          logger = Logger.getLogger(SalvaGruppiAccountAction.class);

  /**
   * Reference alla classe di business logic per le operazioni gli
   * utenti/account e il gruppo
   */
  private AccountManager accountManager;

  /**
   * @param accountManager
   *        accountManager da settare internamente alla classe.
   */
  public void setAccountManager(AccountManager utentiManager) {
    this.accountManager = utentiManager;
  }

  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  public ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // è un blocco che deve essere effettuato a priori ancor prima di entrare
    // nell'elaborazione vera e propria; proprio per questo, nonostante di
    // solito si esca solo alla fine del metodo, e il logger demarca inizio e
    // fine metodo, in questo caso il controllo e l'uscita avviene a monte
    if (this.bloccaGestioneGruppiDisabilitata(request))
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    // target di default per l'azione 'updateGruppoConFunzionalita', da
    // modificare nel
    // momento in cui si verificano dei problemi
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    try {
      // Lettura dal request l'ID_GRUPPO del gruppo in analisi
      int idAccount = Integer.parseInt((String) request.getParameter("idAccount"));

      GruppiAccountForm idGruppiAssociati = (GruppiAccountForm) form;

      this.accountManager.updateAssociazioneGruppoAccount(idAccount,
          idGruppiAssociati.getIdGruppo(),(String)
          request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO),  
          (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO));

      request.setAttribute("idAccount", new Integer(idAccount));
      request.setAttribute("metodo", "visualizzaLista");

    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }

}