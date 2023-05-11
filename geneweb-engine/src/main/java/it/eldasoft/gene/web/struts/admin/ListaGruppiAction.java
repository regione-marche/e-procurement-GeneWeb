/*
 * Created on 28-giu-2006
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
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.GruppoConNumeroAssociazioni;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Azione che esegue il caricamento della pagina Lista Gruppi, visualizzazndo
 * tutti i gruppi e le relative funzionalità
 * 
 * @author Luca.Giacomazzo
 */
public class ListaGruppiAction extends AbstractActionBaseAdmin {

  /** Logger Log4J di classe */
  static Logger         logger = Logger.getLogger(ListaGruppiAction.class);

  /**
   * Reference alla classe di business logic per l'estrazione della lista gruppo
   */
  private GruppiManager gruppiManager;

  /**
   * @return Ritorna gruppiManager.
   */
  public GruppiManager getGruppiManager() {
    return this.gruppiManager;
  }

  /**
   * @param listaGruppiManager
   *        listaGruppiManager da settare internamente alla classe.
   */
  public void setGruppiManager(GruppiManager gruppiManager) {
    this.gruppiManager = gruppiManager;
  }

  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_LETTURA_ADMIN);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // è un blocco che deve essere effettuato a priori ancor prima di entrare
    // nell'elaborazione vera e propria; proprio per questo, nonostante di
    // solito si esca solo alla fine del metodo, e il logger demarca inizio e
    // fine metodo, in questo caso il controllo e l'uscita avviene a monte
    if (this.bloccaGestioneGruppiDisabilitata(request))
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: inizio metodo");
    }

    // cancellazione di oggetti in sessione precedentemente creati
    this.cleanSession(request);

    // target di default, da modificare nel momento in cui si verificano dei
    // problemi
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    try {

      // Determinazione della lista dei gruppi con le relative statistiche sul
      // numero di oggetti associati
      List<?> listaGruppiConNumeroAssociazioni =
        this.gruppiManager.getGruppiConNumeroAssociazioni((String) 
            request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO));

      // Conversione da una lista di oggetti
      ArrayList<GruppoConNumeroAssociazioniForm> listaGruppiConNumeroAssociazioniForm =
          this.setDatiPerModel(listaGruppiConNumeroAssociazioni);

      // set nel request del beanForm contenente la lista dei gruppi con le
      // funzionalità
      request.setAttribute("listaGruppiConNumeroAssociazioniForm",
          listaGruppiConNumeroAssociazioniForm);

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
      logger.debug("runAction: fine metodo");
    }

    return mapping.findForward(target);
  }

  /**
   * Conversione da una lista di oggetti di tipo
   * it.eldasoft.gene.db.domain.GruppoConNumeroAssociazioni a una lista di
   * oggetti di tipo
   * it.eldasoft.gene.web.struts.admin.GruppoConNumeroAssociazioniForm
   * 
   * @param listaIn
   * @return
   */
  private ArrayList<GruppoConNumeroAssociazioniForm> setDatiPerModel(List<?> listaIn) {
    ArrayList<GruppoConNumeroAssociazioniForm> listaOut = new ArrayList<GruppoConNumeroAssociazioniForm>();
    Iterator<?> iteratorListaIn = listaIn.iterator();
    GruppoConNumeroAssociazioni gruppo = null;
    GruppoConNumeroAssociazioniForm gruppoForm = null;

    while (iteratorListaIn.hasNext()) {
      gruppoForm = null;
      gruppo = null;
      gruppo = (GruppoConNumeroAssociazioni) iteratorListaIn.next();
      gruppoForm = new GruppoConNumeroAssociazioniForm(gruppo);
      listaOut.add(gruppoForm);
    }
    return listaOut;
  }

}