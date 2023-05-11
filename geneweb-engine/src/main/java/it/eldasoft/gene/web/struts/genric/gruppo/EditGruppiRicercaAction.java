/*
 * Created on 30-ago-2006
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.gruppo;

import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.genric.GruppoRicerca;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

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
 * Action per il caricamento dal DB dell'elenco dei gruppi, in cui vengono
 * smarcati quelli indicati nella sessione ed associati alla ricerca in analisi
 * 
 * @author Luca Giacomazzo
 */
public class EditGruppiRicercaAction extends AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger         logger = Logger.getLogger(EditGruppiRicercaAction.class);

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

  /**
   * Funzione che restituisce le opzioni per accedere alla action runAction
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
  }
  
  public ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: inizio metodo");
    }

    // target di default, da modificare nel momento in cui si verificano dei
    // errori
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    try {
      HttpSession sessione = request.getSession();
      ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
          sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

      Set<String> setGruppiAssociati = new HashSet<String>();
      for (int i = 0; i < contenitore.getElencoGruppi().size(); i++) {
        setGruppiAssociati.add(((GruppoForm) contenitore.getElencoGruppi().elementAt(
            i)).getIdGruppo());
      }
      // lista di tutti i gruppi, filtrati per codice profilo atitvo esistenti
      // con l'attributo 'associato' di tipo boolean valorizzato a true se il
      // gruppo è associato alla ricerca in analisi e a false altrimenti.
      // La lista è ordinata per nome dei gruppi
      List<?> listaGruppiAssociatiRicerca = 
        this.gruppiManager.getGruppiConAssociazioneRicerca(setGruppiAssociati,
            (String) sessione.getAttribute(CostantiGenerali.PROFILO_ATTIVO));

      GruppiRicercaForm gruppiRicercaForm = new GruppiRicercaForm();
      Vector<String> tmp = new Vector<String>();
      for (int i = 0; i < listaGruppiAssociatiRicerca.size(); i++)
        if (((GruppoRicerca) listaGruppiAssociatiRicerca.get(i)).getAssociato())
          tmp.add(""
              + ((GruppoRicerca) listaGruppiAssociatiRicerca.get(i)).getIdGruppo());

      gruppiRicercaForm.setIdGruppo((String[]) tmp.toArray(new String[0]));

      // set nel request della stringa necessaria al form di modifica
      // associazione utenti-gruppo
      request.setAttribute("gruppiRicercaForm", gruppiRicercaForm);

      // set nel request della lista di tutte le ricerche e lo stato di
      // associazione con il gruppo in analisi
      request.setAttribute("listaGruppiRicerca", listaGruppiAssociatiRicerca);

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      // update del menu a tab per la fase di editing
      this.setMenuTabEdit(request);
      
      // L.G. 26/03/2007 modifica per implementazione dei report base
      // Si cambia target se il report e' un report base
      if (CostantiGenRicerche.REPORT_BASE == contenitore.getTestata().getFamiglia().intValue())
        target = target.concat("Base");
      else if (CostantiGenRicerche.REPORT_SQL == contenitore.getTestata().getFamiglia().intValue()) 
        target = target.concat("Sql");
      
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
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di editing del dettaglio della lista dei gruppi
   * associati ad una ricerca
   * 
   * @param request
   */
  private void setMenuTabEdit(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);

    boolean isInSessione = true;
    if (obj == null) {
      isInSessione = false;
      obj = new GestioneTab();
    }

    GestioneTab gestoreTab = (GestioneTab) obj;
    gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_GRUPPI);
    gestoreTab.setTabSelezionabili(null);

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }
}