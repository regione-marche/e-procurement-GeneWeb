/*
 * Created on 12-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.impexp;

import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.genric.TrovaRicerche;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposerException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContext;
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
 * Action per la ricerca dei report per esportarne la definizione
 *
 * @author Luca.Giacomazzo
 */
public class TrovaRicercheExportAction extends DispatchActionBaseNoOpzioni {

  // Forward predefiniti per il DispatchAction in linea con il file
  // struts-config.xml
  private static final String FORWARD_LISTA_RICERCHE = "successListaRicerche";
  private static final String FORWARD_TROVA_RICERCHE = "successTrovaRicerche";


  /**   logger di Log4J   */
  static Logger logger = Logger.getLogger(TrovaRicercheExportAction.class);

  @Override
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.ABILITAZIONE_DEFAULT;
  }

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi alle
   * ricerche
   */
  private RicercheManager     ricercheManager;

  /**
   * @return Ritorna ricercheManager.
   */
  public RicercheManager getRicercheManager() {
    return this.ricercheManager;
  }

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action trovaRicerche
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniTrovaRicerche() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }

  /**
   * Esegue la query per la recuperare le ricerche in linee con i parametri
   * inseriti nel form della pagina TrovaRicerche, caricando nel request la
   * lista delle ricerche e le relative informazioni.
   */
  public ActionForward trovaRicerche(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("trovaRicerche: inizio metodo");
    }

    // Target di default per l'azione 'trovaRicerche', da modificare nel momento
    // in cui si verificano dei problemi
    String target = TrovaRicercheExportAction.FORWARD_LISTA_RICERCHE;
    String messageKey = null;
    HttpSession session = request.getSession();

    try {
      // cancellazione di oggetti in sessione precedentemente creati
      this.cleanSession(request);

      // lettura dal request del form TrovaRicercheExportForm per impostare i criteri
      // di ricerca
      TrovaRicercheExportForm trovaRicercheExportForm = (TrovaRicercheExportForm) form;

      // Nel caso in cui si deve ritornare alla pagina Lista Ricerche il form
      // contiene un oggetto di tipo TrovaRicercheExportForm con tutti gli argomenti null
      // anche se la lista delle ricerca e' filtrata per qualche parametro.
      // Quanto segue evita di tornare alla lista delle ricerche precedentemente
      // ottenuta, caricando l'oggetto dalla sessione
      if(trovaRicercheExportForm == null || this.isTrovaRicercheFormArgomentiNull(trovaRicercheExportForm)){
        // l'azione è stata richiamata per ripetere l'ultima ricerca effettuata,
        // quindi i dati relativi al form sono da leggere dalla sessione.
        trovaRicercheExportForm = (TrovaRicercheExportForm)
            request.getSession().getAttribute(
                CostantiGenRicerche.TROVA_RICERCHE_EXPORT);
      }

      boolean mostraRicercheBase = true;
      // Se la propertiy it.eldasoft.generatoreRicerche.base.schemaViste
      // e' valorizzata allora è possibile trovare le ricerche base, altrimenti no
      String nomeSchemaVista =  ConfigManager.getValore(
          CostantiGenerali.PROP_SCHEMA_VISTE_REPORT_BASE);
      if(nomeSchemaVista == null || nomeSchemaVista.length() == 0)
        mostraRicercheBase = false;

      // Esecuzione della logica di business per recuperare la lista delle
      // ricerche
      TrovaRicerche trovaRicerche = trovaRicercheExportForm.getDatiPerModel();
      //se non è stato settato nessun filtro per la famiglia del report inserisco tutti le
      //famiglie abilitate
      //controllo se il campo famiglia non ha elementi oppure se il primo elemento è nullo
      if (trovaRicerche.getFamiglia().size()==0) {// || (trovaRicercheForm.getFamiglia().length == 1 && trovaRicercheForm.getFamiglia()[0].length() == 0 )) {

        ServletContext context = request.getSession().getServletContext();
        Collection<String> opzioni = Arrays.asList((String[]) context.getAttribute(CostantiGenerali.OPZIONI_DISPONIBILI));

        Vector<Integer> famiglia = new Vector<Integer>();

        if (opzioni.contains(CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL)) {
          famiglia.add(new Integer(CostantiGenRicerche.REPORT_BASE));
          famiglia.add(new Integer(CostantiGenRicerche.REPORT_AVANZATO));
          famiglia.add(new Integer(CostantiGenRicerche.REPORT_PROSPETTO));
          famiglia.add(new Integer(CostantiGenRicerche.REPORT_SQL));
        }
        trovaRicerche.setFamiglia(famiglia);
      }
      trovaRicerche.setCodiceApplicazione((String) session.getAttribute(
          CostantiGenerali.MODULO_ATTIVO));
      trovaRicerche.setProfiloOwner((String) session.getAttribute(
          CostantiGenerali.PROFILO_ATTIVO));
      List<?> listaRicerche = this.ricercheManager.getRicerche(trovaRicerche,
          mostraRicercheBase);

      if (listaRicerche != null
          && !listaRicerche.isEmpty()
          && listaRicerche.size() > 0) {
        // Set nel request della lista di ricerche
        request.setAttribute("listaRicerche", listaRicerche);
        request.setAttribute("risultatiPerPagina",
            "Tutti".equals(trovaRicercheExportForm.getRisPerPagina())
                ? null
                : trovaRicercheExportForm.getRisPerPagina());

        this.gestioneVociAvanzamento(request,
            CostantiWizard.CODICE_PAGINA_LISTA_REPORT_PER_EXPORT);

        // set nel request del parameter per disabilitare la navigazione
        request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
            CostantiGenerali.DISABILITA_NAVIGAZIONE);

      } else {
        target = CostantiGeneraliStruts.FORWARD_NESSUN_RECORD_TROVATO;
        messageKey = "info.search.noRecordFound";
        this.aggiungiMessaggio(request, messageKey);
      }

      // Aggiornamento del contenitore dei parametri di ricerca presente in
      // sessione
      this.updateParametriTrovaRicerche(request, response, trovaRicercheExportForm);

    } catch (SqlComposerException sc){
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = sc.getChiaveResourceBundle();
      logger.error(this.resBundleGenerale.getString(messageKey), sc);
      this.aggiungiMessaggio(request, messageKey);
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
      logger.debug("trovaRicerche: fine metodo");
    }
    return mapping.findForward(target);
  }

  /**
   * @param trovaRicercheExportForm
   * @return Ritorna true se tutti gli attributi dell'oggetto passato per argomento
   * sono tutti null o pari al valore di default, false altrimenti
   */
  private boolean isTrovaRicercheFormArgomentiNull(TrovaRicercheExportForm trovaRicercheExportForm){
    boolean result = false;

    if(trovaRicercheExportForm.getDescrizioneRicerca() == null &&
       trovaRicercheExportForm.getDisponibile() == null &&
       trovaRicercheExportForm.getFamiglia() == null &&
       trovaRicercheExportForm.getIdGruppo() == null &&
       trovaRicercheExportForm.getNomeRicerca() == null &&
       trovaRicercheExportForm.getOwner() == null &&
       trovaRicercheExportForm.getPersonale() == null &&
       trovaRicercheExportForm.getRisPerPagina() == null &&
       trovaRicercheExportForm.getTipoRicerca() == null &&
       !Boolean.valueOf(trovaRicercheExportForm.getNoCaseSensitive()))
      result = true;

    return result;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action nuovaRicerca
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniNuovaRicerca() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }

  /**
   * Cancella dalla sessione l'oggetto di tipo TrovaRicercheForm, contenente i
   * parametri dell'ultima ricerca effettuata.
   */
  public ActionForward nuovaRicerca(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("nuovaRicerca: inizio metodo");
    }

    // Target di default da modificare nel momento in cui si verificano dei
    // problemi
    String target = TrovaRicercheExportAction.FORWARD_TROVA_RICERCHE;
    String messageKey = null;

    try {
      HttpSession sessione = request.getSession();
      TrovaRicercheExportForm trovaRicercheExportForm = (TrovaRicercheExportForm)
          sessione.getAttribute(CostantiGenRicerche.TROVA_RICERCHE_EXPORT);
      if (trovaRicercheExportForm != null) {
        // l'oggetto in sessione esiste e quindi lo rimuovo
        sessione.removeAttribute(CostantiGenRicerche.TROVA_RICERCHE_EXPORT);
      }
      // Set nel request di un oggetto TrovaRicercheExportForm non inizializzato.
      trovaRicercheExportForm = new TrovaRicercheExportForm();
      trovaRicercheExportForm.setNoCaseSensitive(Boolean.TRUE.toString());
      request.setAttribute("trovaRicercheExportForm", trovaRicercheExportForm);

    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("nuovaRicerca: fine metodo");
    }

    return mapping.findForward(target);
  }

  /**
   * Aggiorna il form in sessione, con i nuovi parametri impostati dall'utente
   *
   * @param session
   *        sessione dell'utente
   */
  private void updateParametriTrovaRicerche(HttpServletRequest request,
      HttpServletResponse response, TrovaRicercheExportForm form) {

    HttpSession session = request.getSession();
    // Rimuozione della sessione dell'oggetto TrovaRicercheExportForm
    session.removeAttribute(CostantiGenRicerche.TROVA_RICERCHE_EXPORT);

    // Inserimento in sessione del contenitore dei parametri di trova ricerche
    session.setAttribute(CostantiGenRicerche.TROVA_RICERCHE_EXPORT, form);
  }

  private void gestioneVociAvanzamento(HttpServletRequest request,
      String paginaAttiva) {
    List<String> pagineVisitate = new ArrayList<String>();
    List<String> pagineDaVisitare = new ArrayList<String>();

    if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_TROVA_REPORT_PER_EXPORT)) {
      pagineVisitate.add(CostantiWizard.TITOLO_TROVA_REPORT_PER_EXPORT);

      pagineDaVisitare.add(CostantiWizard.TITOLO_LISTA_REPORT_PER_EXPORT);
    } else if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_LISTA_REPORT_PER_EXPORT)) {
      pagineVisitate.add(CostantiWizard.TITOLO_TROVA_REPORT_PER_EXPORT);
      pagineVisitate.add(CostantiWizard.TITOLO_LISTA_REPORT_PER_EXPORT);
    }
    request.setAttribute("pagineVisitate", pagineVisitate);
    request.setAttribute("pagineDaVisitare", pagineDaVisitare);
  }

}