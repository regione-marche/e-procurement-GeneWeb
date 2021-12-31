/*
 * Created on 21-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.ModelloGruppo;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.List;
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
 * Action che controlla l'operazione di apertura della pagina Lista Modelli di
 * Gruppo
 * 
 * @author Luca.Giacomazzo
 */
public class ListaModelliGruppoAction extends AbstractDispatchActionBaseAdmin {

  // Forward prdefiniti per il DispatchAction in linea con il file
  // struts-config.xml
  private final String   FORWARD_VISUALIZZA = "successVisualizzaLista";
  private final String   FORWARD_EDIT       = "successEditLista";

  /** Logger Log4J di classe */
  static Logger          logger             = Logger.getLogger(ListaModelliGruppoAction.class);

  private ModelliManager modelliManager;

  /**
   * @return Ritorna modelliManager.
   */
  public ModelliManager getModelliManager() {
    return modelliManager;
  }

  /**
   * @param modelliManager
   *        modelliManager da settare internamente alla classe.
   */
  public void setModelliManager(ModelliManager modelliGruppoManager) {
    this.modelliManager = modelliGruppoManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * visualizzaLista
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniVisualizzaLista() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_LETTURA_ADMIN
            + "&"
            + CostantiGeneraliAccount.GESTIONE_COMPLETA_GENMOD);
  }

  /*
   * (non-Javadoc)
   * 
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

    if (logger.isDebugEnabled()) {
      logger.debug("visualizzaLista: inizio metodo");
    }

    // target di default, da modificare nel momento in cui si verificano dei
    // problemi
    String target = this.FORWARD_VISUALIZZA;
    String messageKey = null;
    HttpSession session = request.getSession();

    try {
      // lettura dal request del idGruppo
      int idGruppo = Integer.parseInt(request.getParameter("idGruppo"));

      // Determinazione della lista degli utenti associati al gruppo in analisi
      List<?> listaModelliGruppo = this.modelliManager.getModelliDiGruppo(
          idGruppo,
          (String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO),
          (String) session.getAttribute(CostantiGenerali.PROFILO_ATTIVO));

      // set nel request del beanForm contenente la lista dei gruppi con le
      // funzionalità
      request.setAttribute("listaModelliGruppo", listaModelliGruppo);

      // set nel request di 'idGruppo'
      request.setAttribute("idGruppo", "" + idGruppo);

      // update del menu a tab
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

    if (logger.isDebugEnabled()) {
      logger.debug("visualizzaLista: fine metodo");
    }

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action editLista
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniEditLista() {
    // ou11 e non ou12
    return new CheckOpzioniUtente("("
        + CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN
        + ")&"
        + CostantiGeneraliAccount.GESTIONE_COMPLETA_GENMOD);
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

    if (logger.isDebugEnabled()) {
      logger.debug("editLista: inizio metodo");
    }

    // target di default per l'azione 'apriAssocModelliGruppo', da modificare
    // nel momento
    // in cui si verificano dei problemi
    String target = this.FORWARD_EDIT;
    String messageKey = null;

    HttpSession session = request.getSession();

    try {
      // Lettura dal request l'ID_GRUPPO del gruppo in analisi
      int idGruppo = Integer.parseInt((String) request.getParameter("idGruppo"));

      // lista dei modelli con attributo 'associato' di tipo boolean valorizzato
      // a true se il modello
      // è associato al gruppo in analisi e a false altrimenti. La lista è
      // ordinata per nome del modello
      List<?> listaModelliAssociatiGruppo = modelliManager.getModelliConAssociazioneGruppo(
          idGruppo,
          (String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO),
          (String) session.getAttribute(CostantiGenerali.PROFILO_ATTIVO));

      ModelliGruppoForm modelliDiGruppo = new ModelliGruppoForm();
      Vector<String> tmp = new Vector<String>();
      for (int i = 0; i < listaModelliAssociatiGruppo.size(); i++)
        if (((ModelloGruppo) listaModelliAssociatiGruppo.get(i)).getAssociato())
          tmp.add(""
              + ((ModelloGruppo) listaModelliAssociatiGruppo.get(i)).getIdModello());

      modelliDiGruppo.setIdModello((String[]) tmp.toArray(new String[0]));

      // set nel request della stringa necessaria al form di modifica
      // associazione utenti-gruppo
      request.setAttribute("modelliGruppoForm", modelliDiGruppo);

      // set nel request della lista di tutte i modelli e lo stato di
      // associazione con il gruppo in analisi
      request.setAttribute("listaModelliAssociatiGruppo",
          listaModelliAssociatiGruppo);

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      // set nel request di 'idGruppo'
      request.setAttribute("idGruppo", "" + idGruppo);

      // update del menu a tab
      this.setMenuTabEdit(request);

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
   * del menua tab in fase di visualizzazione della lista modelli di gruppo
   * 
   * @param request
   */
  private void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiDettaglioGruppo.MODELLI);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioGruppo.DETTAGLIO, CostantiDettaglioGruppo.UTENTI,
          CostantiDettaglioGruppo.RICERCHE });
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioGruppo.MODELLI);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioGruppo.DETTAGLIO, CostantiDettaglioGruppo.UTENTI,
          CostantiDettaglioGruppo.RICERCHE });
      sessione.setAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB,
          gestoreTab);
    }
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di editing della lista modelli di gruppo
   * 
   * @param request
   */
  private void setMenuTabEdit(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiDettaglioGruppo.MODELLI);
      gestoreTab.setTabSelezionabili(null);
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioGruppo.MODELLI);
      gestoreTab.setTabSelezionabili(null);
      sessione.setAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB,
          gestoreTab);
    }
  }
}