/*
 * Created on 15-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.prospetto;

import it.eldasoft.gene.bl.genric.ProspettoManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicercheProspetto;
import it.eldasoft.gene.web.struts.genric.gruppo.GruppiRicercaForm;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.utility.UtilityNumeri;

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
 * Action per il salvataggio dell'associazione tra i gruppi e la ricerca con
 * modello in analisi
 * 
 * @author Luca.Giacomazzo
 */
public class SalvaGruppiProspettoAction extends AbstractActionBaseGenRicercheProspetto {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(SalvaGruppiProspettoAction.class);

  /**
   * Reference alla classe di business logic per accesso ai dati relativi alle
   * ricerche
   */
  private ProspettoManager prospettoManager;

  /**
   * @param prospettoManager
   *        prospettoManager da settare internamente alla classe.
   */
  public void setProspettoManager(ProspettoManager prospettoManager) {
    this.prospettoManager = prospettoManager;
  }
  
  
  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioniRunAction()
   */
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
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

    // target di default, da modificare nel momento in cui si verificano dei
    // errori
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    try {
      String idRicercaTMP = (String) request.getSession().getAttribute(
          CostantiGenerali.ID_OGGETTO_SESSION);
      int idRicerca = UtilityNumeri.convertiIntero(idRicercaTMP).intValue();
      GruppiRicercaForm gruppiRicercaForm = (GruppiRicercaForm) form;
      
      // inserisco i gruppi degli utenti associati alla ricerca    
      this.prospettoManager.insertGruppoRicerca(idRicerca,
          gruppiRicercaForm.getIdGruppo());
      
      // Set nel request dell'idProspetto da passare all'azione di
      // visualizzazione della lista dei gruppi associati alla ricerca
      // con modello in analisi
      request.setAttribute("idRicerca", "" + idRicerca);

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