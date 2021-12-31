/*
 * Created on 06-lug-2006
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
import it.eldasoft.gene.web.struts.admin.GruppoForm;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

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

/**
 * Action per l'apertura dell'editing del dettaglio di un gruppo e delle
 * relative funzionalita'
 * 
 * @author Luca.Giacomazzo
 */
public class InitEditGruppoAction extends AbstractActionBaseAdmin {

  /** Logger Log4J di classe */
  static Logger         logger = Logger.getLogger(InitEditGruppoAction.class);

  /**
   * Reference alla classe di business logic per le operazioni su un gruppo e le
   * sue funzionalità
   */
  private GruppiManager gruppiManager;

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

  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // è un blocco che deve essere effettuato a priori ancor prima di entrare
    // nell'elaborazione vera e propria; proprio per questo, nonostante di
    // solito si esca solo alla fine del metodo, e il logger demarca inizio e
    // fine metodo, in questo caso il controllo e l'uscita avviene a monte
    if (this.bloccaGestioneGruppiDisabilitata(request))
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    // target di default per l'azione 'modifica', da modificare nel momento
    // in cui si verificano dei problemi
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    try {
      // lettura dal request del parametro 'idGruppo'
      int idGruppo = Integer.parseInt(request.getParameter("idGruppo"));

      // Determinazione di nome, descrizione, funzionalità abilitate al gruppo
      // indivuato da idGruppo
      Gruppo gruppo = this.gruppiManager.getGruppoById(idGruppo);

      // Conversione del gruppo con funzionalità abilitate alla versione per MVC
      GruppoForm gruppoForm = this.setDatiPerModel(gruppo);

      // set nel request del beanForm contenente il gruppo con le funzionalità
      request.setAttribute("gruppoForm",
          gruppoForm);

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      // update in sessione del gestore del menu a tab
      this.setMenuTab(request);

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

  /**
   * Conversione di un oggetto di tipo
   * it.eldasoft.gene.db.domain.Gruppo ad un oggetto di tipo
   * it.eldasoft.gene.web.struts.admin.GruppoForm.
   * 
   * @param gruppoIn
   * 
   * @return gruppoForm
   */
  private GruppoForm setDatiPerModel(
      Gruppo gruppoIn) {
    GruppoForm gruppoForm = null;

    gruppoForm = new GruppoForm();
    gruppoForm.setIdGruppo(gruppoIn.getIdGruppo());
    gruppoForm.setNomeGruppo(gruppoIn.getNomeGruppo());
    gruppoForm.setDescrizione(gruppoIn.getDescrGruppo());
//    gruppoForm.setAmmUtenti(gruppoIn.isAmmUtenti());
//    gruppoForm.setAmmRicerche(gruppoIn.isAmmRicerche());
//    gruppoForm.setAmmModelli(gruppoIn.isAmmModelli());

    return gruppoForm;
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
      gestoreTab.setTabSelezionabili(null);
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioGruppo.DETTAGLIO);
      gestoreTab.setTabSelezionabili(null);
      sessione.setAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB,
          gestoreTab);
    }
  }

}
