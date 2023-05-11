/*
 * Created on 03-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Gruppo;
import it.eldasoft.gene.tags.history.UtilityHistory;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.admin.GruppoForm;
import it.eldasoft.gene.web.struts.tags.GestoreEccezioni;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * DispatchAction che raccoglie le azioni relative alle operazioni di insert,
 * update e delete sulle tabelle W_GRUPPI e W_FUNZGRP.
 * 
 * @author Luca.Giacomazzo
 */
public class GruppoAction extends AbstractDispatchActionBaseAdmin {

  // Forward predefiniti per il DispatchAction in linea con il file
  // struts-config.xml
  private static final String SUCCESS_MODIFICA_FORWARD = "successUpdate";
  private static final String ERROR_MODIFICA_FORWARD   = "errorOnUpdateGruppo";
  private static final String SUCCESS_CREA_FORWARD     = "successInsert";
  private static final String ERROR_CREA_FORWARD       = "errorOnInsertGruppo";
  private static final String SUCCESS_ELIMINA_FORWARD  = "successDelete";
  private static final String ERROR_ELIMINA_FORWARD    = "errorOnDeleteGruppo";

  /** Logger Log4J di classe */
  static Logger               logger                   = Logger.getLogger(GruppoAction.class);

  /**
   * Reference alla classe di business logic per le operazioni su un gruppo e le
   * sue funzionalità
   */
  private GruppiManager       gruppiManager;

  /**
   * @return Ritorna gruppiManager.
   */
  public GruppiManager getGruppiManager() {
    return gruppiManager;
  }

  /**
   * @param gruppiManager
   *        gruppiManager da settare internamente alla classe.
   */
  public void setGruppiManager(GruppiManager gruppiManager) {
    this.gruppiManager = gruppiManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action eliminaGruppo
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniEliminaGruppo() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }
  /**
   * Effettua la cancellazione prima dei record dalla tabella W_FUNZGRP,
   * filtrando per CODAPP, e del gruppo dalla tabella W_GRUPPI, non prima di
   * aver verificato che non esistano legami dell'ID_GRUPPO con le tabelle
   * W_ACCGRP, W_GRPRIC e W_GRPMOD.
   */
  public ActionForward eliminaGruppo(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("eliminaGruppo: inizio metodo");
    }

    // target di default per l'azione 'updateGruppoConFunzionalita', da
    // modificare nel momento in cui si verificano degli errori
    String target = GruppoAction.SUCCESS_ELIMINA_FORWARD;
    String messageKey = null;

    HttpSession session = request.getSession();

    try {
      // Lettura dal request l'ID_GRUPPO del gruppo da cancellare
      int idGruppo = Integer.parseInt((String) request.getParameter("idGruppo"));

      String messaggio = this.gruppiManager.deleteGruppo(
          idGruppo,
          (String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO));

      if (messaggio != null) {
        target = GruppoAction.ERROR_ELIMINA_FORWARD;
        messageKey = messaggio;
        logger.error(this.resBundleGenerale.getString(messageKey));

        this.aggiungiMessaggio(request, messageKey);
      }

      // Nessun aggiornamento in sessione dell'oggetto GestioneTab, perchè
      // l'azione di eliminazione
      // di un gruppo ritorna sempre alla pagina di partenza e nulla cambia sul
      // menu a tab.

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

    if (logger.isDebugEnabled()) {
      logger.debug("eliminaGruppo: fine metodo");
    }

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action creaGruppoConFunzionalita
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniCreaGruppo() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }
  
  public ActionForward creaGruppo(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("creaGruppo: inizio metodo");
    }

    // target di default per l'azione 'updateGruppoConFunzionalita', da
    // modificare nel momento
    // in cui si verificano dei problemi
    String target = GruppoAction.SUCCESS_CREA_FORWARD;
    String messageKey = null;
    GruppoForm gruppoForm = null;
    try {
      String codiceProfilo = (String) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_ATTIVO);
      // Lettura dal request del form proveniente dal client
      gruppoForm = (GruppoForm) form;
      if (gruppoForm != null) {
        // Creazione del bean per la Business Logic
        Gruppo gruppo = this.getDatiPerModel(gruppoForm);

        int newIdGruppo = this.gruppiManager.insertGruppo(gruppo, codiceProfilo);
        request.removeAttribute("gruppoForm");

        // Update del menu a tab: dopo la creazione di un nuovo gruppo, si passa
        // al dettaglio del gruppo
        // stesso: di conseguenza tab Attivo = Dettaglio e tutti gli altri tab
        // selezionabili
        this.setMenuTab(request);

        // set nel request dell'ID_GRUPPO del gruppo in analisi per la creazione
        // dei link nel TAB menù
        request.setAttribute("idGruppo", "" + newIdGruppo);

        // Ripristino nella sessione degli oggetti 'idOggetto' e 'nomeOggetto',
        // perchè dopo la creazione
        // del gruppo si passa al dettaglio dello stesso.
        HttpSession sessione = request.getSession();
        sessione.setAttribute(CostantiGenerali.ID_OGGETTO_SESSION, new Integer(
            newIdGruppo));
        sessione.setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
            gruppo.getNomeGruppo());
      }
    } catch (DataIntegrityViolationException div){
      target = ERROR_CREA_FORWARD;
      messageKey = "errors.insertGruppo.nomeDuplicatoException";
      logger.error(this.resBundleGenerale.getString(
          UtilityStringhe.replaceParametriMessageBundle(messageKey, 
              new String[]{gruppoForm.getNomeGruppo()})), div);
      this.aggiungiMessaggio(request, messageKey, gruppoForm.getNomeGruppo());
    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = ERROR_CREA_FORWARD;// CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.insertGruppo.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    request.removeAttribute("gruppoForm");

    if (logger.isDebugEnabled()) {
      logger.debug("creaGruppo: fine metodo");
    }

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action annulla
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniAnnulla() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }
  
  public ActionForward annulla(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("annulla: inizio metodo");

    ActionForward actForward = null;
    UtilityHistory history = UtilityTags.getUtilityHistory(request.getSession());
    String target = null;
    
    switch(history.size(0)){
    case 1:
      target = "tornaAListaGruppi";
      actForward = mapping.findForward(target);
      break;
    case 2:
      try {
        actForward = history.last(request);
      } catch (Throwable t) {
        actForward = GestoreEccezioni.gestisciEccezioneAction(t, this, request,
            logger, mapping);
      }
      break;
    }

    if (logger.isDebugEnabled()) logger.debug("annulla: fine metodo ");
    return actForward;
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action updateGruppoConFunzionalita
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniUpdateGruppo() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }
  public ActionForward updateGruppo(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("updateGruppo: inizio metodo");
    }

    // target di default per l'azione 'updateGruppoConFunzionalita', da
    // modificare nel momento
    // in cui si verificano dei problemi
    String target = GruppoAction.SUCCESS_MODIFICA_FORWARD;
    String messageKey = null;
    try {
      // Lettura dal request del form proveniente dal client
      GruppoForm gruppoForm = (GruppoForm) form;
      if (gruppoForm != null) {
        // Creazione del bean per la Business Logic
        Gruppo gruppo = this.getDatiPerModel(gruppoForm);
        this.gruppiManager.updateGruppo(gruppo);
        // metto nel request l'idGruppo necessario nel forward per il dettaglio
        // del gruppo appena aggiornato
        request.setAttribute("idgruppo", new Integer(gruppoForm.getIdGruppo()));

        // Update del menu a tab: dopo l'update del gruppo, si passa al
        // dettaglio del gruppo stesso: di conseguenza tab
        // Attivo = Dettaglio e tutti gli altri tab selezionabili
        this.setMenuTab(request);
      }
    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = GruppoAction.ERROR_MODIFICA_FORWARD;
      messageKey = "errors.updateGruppo.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    request.removeAttribute("gruppoForm");

    if (logger.isDebugEnabled()) {
      logger.debug("updateGruppo: fine metodo");
    }

    return mapping.findForward(target);
  }

  /**
   * Conversione di un oggetto di tipo
   * it.eldasoft.gene.db.domain.GruppoForm ad un oggetto di tipo
   * it.eldasoft.gene.web.struts.admin.Gruppo
   * 
   * @param gruppoFormIn
   * 
   * @return gruppo
   */
  private Gruppo getDatiPerModel(
      GruppoForm gruppoFormIn) {
    Gruppo gruppo = new Gruppo();
    gruppo.setIdGruppo(gruppoFormIn.getIdGruppo());
    gruppo.setNomeGruppo(gruppoFormIn.getNomeGruppo());
    gruppo.setDescrGruppo(gruppoFormIn.getDescrizione());
    return gruppo;
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab
   * 
   * @param request
   */
  private void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiDettaglioGruppo.DETTAGLIO);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioGruppo.DETTAGLIO, CostantiDettaglioGruppo.UTENTI,
          CostantiDettaglioGruppo.RICERCHE, CostantiDettaglioGruppo.MODELLI });
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioGruppo.DETTAGLIO);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioGruppo.DETTAGLIO, CostantiDettaglioGruppo.UTENTI,
          CostantiDettaglioGruppo.RICERCHE, CostantiDettaglioGruppo.MODELLI });
      sessione.setAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB,
          gestoreTab);
    }
  }
}