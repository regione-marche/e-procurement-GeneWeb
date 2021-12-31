/*
 * Created on 20-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.RicercaGruppo;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
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
 * Action che controlla l'operazione di apertura della pagina Lista Ricerche di
 * Gruppo
 * 
 * @author Luca.Giacomazzo
 */
public class ListaRicercheGruppoAction extends AbstractDispatchActionBaseAdmin {

  // Forward p redefiniti per il DispatchAction in linea con il file
  // struts-config.xml
  private static final String FORWARD_VISUALIZZA = "successVisualizzaLista";
  private static final String FORWARD_EDIT       = "successEditLista";

  /** Logger Log4J di classe */
  static Logger               logger             = Logger.getLogger(ListaRicercheGruppoAction.class);

  /**
   * Resource bundle parte generale dell'applicazione (modulo GENE)
   */
  protected ResourceBundle    resBundleGenerale  = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  /**
   * Reference alla classe di business logic per il popolamento delle comboBox
   * presenti nella pagina
   */
  private RicercheManager     ricercheManager;

  /**
   * @return Ritorna ricercheManager.
   */
  public RicercheManager getRicercheManager() {
    return ricercheManager;
  }

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action editLista
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniEditLista() {
    return new CheckOpzioniUtente("("
        + CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN
        + ")&"
        + CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
  }

  public ActionForward editLista(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // � un blocco che deve essere effettuato a priori ancor prima di entrare
    // nell'elaborazione vera e propria; proprio per questo, nonostante di
    // solito si esca solo alla fine del metodo, e il logger demarca inizio e
    // fine metodo, in questo caso il controllo e l'uscita avviene a monte
    if (this.bloccaGestioneGruppiDisabilitata(request))
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
    
    if (logger.isDebugEnabled()) {
      logger.debug("editLista: inizio metodo");
    }

    // target di default per l'azione 'apriAssocRicercheGruppo', da modificare
    // nel momento
    // in cui si verificano dei problemi
    String target = ListaRicercheGruppoAction.FORWARD_EDIT;
    String messageKey = null;
    HttpSession session = request.getSession();

    try {
      // Lettura dal request l'ID_GRUPPO del gruppo in analisi
      int idGruppo = Integer.parseInt((String) request.getParameter("idGruppo"));

      // lista delle ricerche con attributo 'associato' di tipo boolean
      // valorizzato a true se la ricerca
      // � associato al gruppo in analisi e a false altrimenti. La lista �
      // ordinata per nome delle ricerche
      List<?> listaRicercheAssociateGruppo = ricercheManager.getRicercheConAssociazioneGruppo(
          idGruppo,
          (String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO),
          (String) session.getAttribute(CostantiGenerali.PROFILO_ATTIVO));

      RicercheGruppoForm ricercheDiGruppo = new RicercheGruppoForm();
      Vector<String> tmp = new Vector<String>();
      for (int i = 0; i < listaRicercheAssociateGruppo.size(); i++)
        if (((RicercaGruppo) listaRicercheAssociateGruppo.get(i)).getAssociato())
          tmp.add(""
              + ((RicercaGruppo) listaRicercheAssociateGruppo.get(i)).getIdRicerca());

      ricercheDiGruppo.setIdRicerca((String[]) tmp.toArray(new String[0]));

      // set nel request della stringa necessaria al form di modifica
      // associazione utenti-gruppo
      request.setAttribute("ricercheGruppoForm", ricercheDiGruppo);

      // set nel request della lista di tutte le ricerche e lo stato di
      // associazione con il gruppo in analisi
      request.setAttribute("listaRicercheAssociateGruppo",
          listaRicercheAssociateGruppo);

      // set nel request di 'idGruppo'
      request.setAttribute("idGruppo", "" + idGruppo);

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

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
   * Funzione che restituisce le opzioni per accedere alla action
   * visualizzaLista
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniVisualizzaLista() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_LETTURA_ADMIN
            + "&"
            + CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
  }

  /**
   * Visualizza la lista di tutte le ricerche con il check di associazione di
   * ogni singola ricerca con il gruppo in analisi
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward visualizzaLista(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // � un blocco che deve essere effettuato a priori ancor prima di entrare
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
    String target = ListaRicercheGruppoAction.FORWARD_VISUALIZZA;
    String messageKey = null;
    HttpSession session = request.getSession();

    try {
      // lettura dal request del idGruppo
      int idGruppo = Integer.parseInt(request.getParameter("idGruppo"));

      // Determinazione della lista degli utenti associati al gruppo in analisi
      List<?> listaRicercheDiGruppo = this.ricercheManager.getRicercheDiGruppo(
          idGruppo,
          (String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO),
          (String) session.getAttribute(CostantiGenerali.PROFILO_ATTIVO));

      // set nel request del beanForm contenente la lista dei gruppi con le
      // funzionalit�
      request.setAttribute("listaRicercheDiGruppo", listaRicercheDiGruppo);

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
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di visualizzazione della lista ricerche di gruppo
   * 
   * @param request
   */
  private void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiDettaglioGruppo.RICERCHE);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioGruppo.DETTAGLIO, CostantiDettaglioGruppo.UTENTI,
          CostantiDettaglioGruppo.MODELLI });
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioGruppo.RICERCHE);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioGruppo.DETTAGLIO, CostantiDettaglioGruppo.UTENTI,
          CostantiDettaglioGruppo.MODELLI });
      sessione.setAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB,
          gestoreTab);
    }
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di editing della lista ricerche di gruppo
   * 
   * @param request
   */
  private void setMenuTabEdit(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiDettaglioGruppo.RICERCHE);
      gestoreTab.setTabSelezionabili(null);
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioGruppo.RICERCHE);
      gestoreTab.setTabSelezionabili(null);
      sessione.setAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB,
          gestoreTab);
    }
  }
}