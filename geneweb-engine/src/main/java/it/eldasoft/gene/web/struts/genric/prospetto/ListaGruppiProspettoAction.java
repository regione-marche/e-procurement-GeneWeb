/*
 * Created on 13-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.prospetto;

import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Gruppo;
import it.eldasoft.gene.db.domain.genric.GruppoRicerca;
import it.eldasoft.gene.web.struts.genric.AbstractDispatchActionBaseGenRicercheProspetto;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.gruppo.GruppiRicercaForm;
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
 * Azione che esegue il caricamento della pagina Lista Gruppi, visualizzazndo
 * tutti i gruppi associati alla ricerca con modello 
 * 
 * @author Luca.Giacomazzo
 */
public class ListaGruppiProspettoAction extends AbstractDispatchActionBaseGenRicercheProspetto {

  private final String FORWARD_VISUALIZZA = "successVisualizza";
  private final String FORWARD_EDIT       = "successEdit";
  
  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(ListaGruppiProspettoAction.class);

  /**
   * Reference alla classe di business logic per l'estrazione della lista gruppo
   */
  private GruppiManager gruppiManager;
  
  /**
   * @param gruppiManager
   *        gruppiManager da settare internamente alla classe.
   */
  public void setGruppiManager(GruppiManager gruppiManager) {
    this.gruppiManager = gruppiManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action visualizzaLista
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniVisualizzaLista() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
  }
  
  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward visualizzaLista(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // è un blocco che deve essere effettuato a priori ancor prima di entrare
    // nell'elaborazione vera e propria; proprio per questo, nonostante di
    // solito si esca solo alla fine del metodo, e il logger demarca inizio e
    // fine metodo, in questo caso il controllo e l'uscita avviene a monte
    if (this.bloccaGestioneGruppiDisabilitata(request))
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);

    if (logger.isDebugEnabled()) logger.debug("VisualizzaLista: inizio metodo");

    // target di default, da modificare nel momento in cui si verificano dei
    // problemi
    String target = FORWARD_VISUALIZZA;
    String messageKey = null;

    try {
      // idProspetto lo si trova come attribute nel caso di apertura della lista
      // dei gruppi dopo l'edti della lista stessa, altrimenti lo si trova come
      // parameter
      String idRicerca = request.getParameter("idRicerca");
      if(idRicerca == null)
        idRicerca = (String) request.getAttribute("idRicerca");
      
      // Determinazione della lista dei gruppi con le relative funzionalita'
      List<?> listaGruppiAssociatiProspetto = 
          this.gruppiManager.getGruppiByIdRicerca(Integer.parseInt(idRicerca));
            
      // set nel request del beanForm contenente la lista dei gruppi
      request.setAttribute("listaGruppiAssociati", listaGruppiAssociatiProspetto);

      this.setMenuTab(request, true);
      
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
      logger.debug("VisualizzaLista: fine metodo");
    }

    return mapping.findForward(target);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action editLista
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniEditLista() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
  }
  
  public ActionForward editLista(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // è un blocco che deve essere effettuato a priori ancor prima di entrare
    // nell'elaborazione vera e propria; proprio per questo, nonostante di
    // solito si esca solo alla fine del metodo, e il logger demarca inizio e
    // fine metodo, in questo caso il controllo e l'uscita avviene a monte
    if (this.bloccaGestioneGruppiDisabilitata(request))
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);

    if (logger.isDebugEnabled()) logger.debug("editLista: inizio metodo");

    // target di default da modificare nel momento in cui si verificano dei
    // problemi
    String target = FORWARD_EDIT;
    String messageKey = null;

    try {
      // lettura dal request del idProspetto
      int idRicerca = Integer.parseInt(request.getParameter("idRicerca"));
      
      // lista dei gruppi con attributo 'associato' di tipo boolean
      // valorizzato a true se il gruppo è associato all'utente in analisi e a 
      // false altrimenti. La lista è ordinata per nome del gruppo
      List<?> listaGruppiAssociatiProspetto = 
          this.gruppiManager.getGruppiByIdRicerca(idRicerca);

      Set<String> setGruppiAssociati = new HashSet<String>();
      for (int i = 0; i < listaGruppiAssociatiProspetto.size(); i++) {
        setGruppiAssociati.add("" + ((Gruppo) 
            listaGruppiAssociatiProspetto.get(i)).getIdGruppo());
      }
      
      // lista di tutti i gruppi esistenti con l'attributo 'associato' di tipo
      // boolean valorizzato a true
      // se il gruppo è associato alla ricerca in analisi e a false altrimenti.
      // La lista è ordinata per nome dei gruppi
      List<?> listaGruppiAssociatiRicerca = 
          this.gruppiManager.getGruppiConAssociazioneRicerca(setGruppiAssociati,
              (String) request.getSession().getAttribute(
                  CostantiGenerali.PROFILO_ATTIVO));

      GruppiRicercaForm gruppiRicercaForm = new GruppiRicercaForm();
      Vector<String> tmp = new Vector<String>();
      for (int i = 0; i < listaGruppiAssociatiRicerca.size(); i++)
        if (((GruppoRicerca) listaGruppiAssociatiRicerca.get(i)).getAssociato())
          tmp.add(""
              + ((GruppoRicerca) listaGruppiAssociatiRicerca.get(i)).getIdGruppo());

      gruppiRicercaForm.setIdGruppo((String[]) tmp.toArray(new String[0]));

      // set nel request della stringa necessaria al form di modifica
      // associazione ricerca-gruppo
      request.setAttribute("gruppiRicercaForm", gruppiRicercaForm);

      // set nel request della lista di tutti i gruppi e lo stato di
      // associazione con la ricerca in analisi
      request.setAttribute("listaGruppiRicerca", listaGruppiAssociatiRicerca);

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      // update del menu a tab per la fase di editing
      this.setMenuTab(request, false);

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
      logger.debug("editLista: fine metodo");
    }

    return mapping.findForward(target);
  }
  
  
  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di visualizzazione del dettaglio della lista dei 
   * gruppi associati alla ricerca con modello in analisi
   * 
   * @param request
   */
  private void setMenuTab(HttpServletRequest request, boolean isVisualizzazione) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiGenRicerche.NOME_GESTORE_TAB);
    if(isVisualizzazione){
      if (obj != null) {
        GestioneTab gestoreTab = (GestioneTab) obj;
        gestoreTab.setTabAttivo(CostantiGenProspetto.TAB_GRUPPI);
        gestoreTab.setTabSelezionabili(new String[] {
            CostantiGenProspetto.TAB_DATI_GENERALI, CostantiGenProspetto.TAB_PARAMETRI });
      } else {
        GestioneTab gestoreTab = new GestioneTab();
        gestoreTab.setTabAttivo(CostantiGenProspetto.TAB_GRUPPI);
        gestoreTab.setTabSelezionabili(new String[] {
            CostantiGenProspetto.TAB_DATI_GENERALI, CostantiGenProspetto.TAB_PARAMETRI });
        sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
      }
    } else {
      if (obj != null) {
        GestioneTab gestoreTab = (GestioneTab) obj;
        gestoreTab.setTabAttivo(CostantiGenProspetto.TAB_GRUPPI);
        gestoreTab.setTabSelezionabili(null);
      } else {
        GestioneTab gestoreTab = new GestioneTab();
        gestoreTab.setTabAttivo(CostantiGenProspetto.TAB_GRUPPI);
        gestoreTab.setTabSelezionabili(null);
        sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
      }
    }
  }

}