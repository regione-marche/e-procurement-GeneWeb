/*
 * Created on 12-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric;

import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.genric.TrovaRicerche;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposerException;

import java.io.IOException;
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
 * Action che controlla l'operazione di apertura della pagina Trova Ricerche, la
 * quale richiede la presenza in sessione di un oggetto (TrovaRicercaForm),
 * contenente i parametri di ricerca precedentemente definiti. Tale oggetto sarà
 * vuoto se si accede alla pagina per la prima volta.
 *
 * @author Luca.Giacomazzo
 */
public class TrovaRicercheAction extends AbstractDispatchActionBaseGenRicerche {

  // Forward predefiniti per il DispatchAction in linea con il file
  // struts-config.xml
  private static final String FORWARD_LISTA_RICERCHE = "successListaRicerche";
  private static final String FORWARD_TROVA_RICERCHE = "successTrovaRicerche";

  /** Logger Log4J di classe */
  static Logger               logger                 = Logger.getLogger(TrovaRicercheAction.class);

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
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
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
    String target = TrovaRicercheAction.FORWARD_LISTA_RICERCHE;
    String messageKey = null;
    HttpSession session = request.getSession();

    try {
      // cancellazione di oggetti in sessione precedentemente creati
      this.cleanSession(request);

      // Cancellazione di id oggetto e nome oggetto precedentemente caricati in sessione
      session.removeAttribute(CostantiGenerali.ID_OGGETTO_SESSION);
      session.removeAttribute(CostantiGenerali.NOME_OGGETTO_SESSION);

      // lettura dal request del form TrovaRicercheForm per impostare i criteri
      // di ricerca
      TrovaRicercheForm trovaRicercheForm = (TrovaRicercheForm) form;

      // Nel caso in cui si deve ritornare alla pagina Lista Ricerche il form
      // contiene un oggetto di tipo TrovaRicercheForm con tutti gli argomenti null
      // anche se la lista delle ricerca e' filtrata per qualche parametro.
      // Quanto segue evita di tornare alla lista delle ricerche precedentemente
      // ottenuta, caricando l'oggetto dalla sessione
      if(trovaRicercheForm == null || this.isTrovaRicercheFormArgomentiNull(trovaRicercheForm)){
        // l'azione è stata richiamata per ripetere l'ultima ricerca effettuata,
        // quindi i dati relativi al form sono da leggere dalla sessione.
        trovaRicercheForm = (TrovaRicercheForm)
            request.getSession().getAttribute(CostantiGenRicerche.TROVA_RICERCHE);
      }

      boolean mostraRicercheBase = true;
      // Se la property it.eldasoft.generatoreRicerche.base.schemaViste
      // e' valorizzata allora è possibile trovare le ricerche base, altrimenti no
      String nomeSchemaVista =  ConfigManager.getValore(
          CostantiGenerali.PROP_SCHEMA_VISTE_REPORT_BASE);
      if(nomeSchemaVista == null || nomeSchemaVista.length() == 0)
        mostraRicercheBase = false;

      // Esecuzione della logica di business per recuperare la lista delle
      // ricerche
      TrovaRicerche trovaRicerche = trovaRicercheForm.getDatiPerModel();
      //se non è stato settato nessun filtro per la famiglia del report inserisco tutti le
      //famiglie abilitate
      //controllo se il campo famiglia non ha elementi oppure se il primo elemento è nullo
      if (trovaRicerche.getFamiglia().size()==0) {// || (trovaRicercheForm.getFamiglia().length == 1 && trovaRicercheForm.getFamiglia()[0].length() == 0 )) {

        Vector<Integer> famiglia = TrovaRicercheAction.getFamiglieReportAbilitateUtente(request);
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
            "Tutti".equals(trovaRicercheForm.getRisPerPagina())
                ? null
                : trovaRicercheForm.getRisPerPagina());

      } else {
        target = CostantiGeneraliStruts.FORWARD_NESSUN_RECORD_TROVATO;
        messageKey = "info.search.noRecordFound";
        this.aggiungiMessaggio(request, messageKey);
      }

      // Aggiornamento del contenitore dei parametri di ricerca presente in
      // sessione
      this.updateParametriTrovaRicerche(request, response, trovaRicercheForm);

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
   * Estrae l'elenco delle famiglie di report attribuite all'utente per la
   * gestione
   *
   * @param request
   *        request HTTP
   * @return vettore di identificativi delle famiglie di report
   */
  public static Vector<Integer> getFamiglieReportAbilitateUtente(HttpServletRequest request) {
    ServletContext context = request.getSession().getServletContext();
    Collection<String> opzioni = Arrays.asList((String[]) context.getAttribute(CostantiGenerali.OPZIONI_DISPONIBILI));

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute("profiloUtente");
    OpzioniUtente opzioniUtente = new OpzioniUtente(profiloUtente.getFunzioniUtenteAbilitate());

    CheckOpzioniUtente opzioniPerAbilitazioneBase = new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
    CheckOpzioniUtente opzioniPerAbilitazioneAvanzato = new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_AVANZATI);
    CheckOpzioniUtente opzioniPerAbilitazioneProspetto = new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_CON_PROSPETTO);
    CheckOpzioniUtente opzioniPerAbilitazioneReportSQL = new CheckOpzioniUtente(CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
    Vector<Integer> famiglia = new Vector<Integer>();

    //è abilitato per le ricerche base
    if (opzioni.contains(CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL) && opzioniPerAbilitazioneBase.test(opzioniUtente)) {
      famiglia.add(new Integer(CostantiGenRicerche.REPORT_BASE));
    }
    //è abilitato per le ricerche avanzate
    if (opzioni.contains(CostantiGenerali.OPZIONE_GEN_RICERCHE) && (opzioniPerAbilitazioneAvanzato.test(opzioniUtente))) {
      famiglia.add(new Integer(CostantiGenRicerche.REPORT_AVANZATO));
    }
    //è abilitato per le ricerche con modello
    if  (opzioni.contains(CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL) && (opzioniPerAbilitazioneProspetto.test(opzioniUtente)))  {
      famiglia.add(new Integer(CostantiGenRicerche.REPORT_PROSPETTO));
    }

    if (opzioni.contains(CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL)
            && opzioniPerAbilitazioneReportSQL.test(opzioniUtente)) {
      famiglia.add(new Integer(CostantiGenRicerche.REPORT_SQL));
    }

    return famiglia;
  }

  /**
   * @param trovaRicercheForm
   * @return Ritorna true se tutti gli attributi dell'oggetto passato per argomento
   * sono tutti null o pari al valore di default, false altrimenti
   */
  private boolean isTrovaRicercheFormArgomentiNull(TrovaRicercheForm trovaRicercheForm){
    boolean result = false;

    if(trovaRicercheForm.getDescrizioneRicerca() == null &&
       trovaRicercheForm.getDisponibile() == null &&
       trovaRicercheForm.getFamiglia() == null &&
       trovaRicercheForm.getIdGruppo() == null &&
       trovaRicercheForm.getNomeRicerca() == null &&
       trovaRicercheForm.getOwner() == null &&
       trovaRicercheForm.getPersonale() == null &&
       "20".equals(trovaRicercheForm.getRisPerPagina()) &&
       trovaRicercheForm.getTipoRicerca() == null &&
       Boolean.valueOf(trovaRicercheForm.getNoCaseSensitive()))
      result = true;

    return result;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action nuovaRicerca
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniNuovaRicerca() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
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
    String target = TrovaRicercheAction.FORWARD_TROVA_RICERCHE;
    String messageKey = null;

    try {
      HttpSession sessione = request.getSession();
      TrovaRicercheForm trovaRicercheForm = (TrovaRicercheForm) sessione.getAttribute(CostantiGenRicerche.TROVA_RICERCHE);
      if (trovaRicercheForm != null) {
        // l'oggetto in sessione esiste e quindi lo rimuovo
        sessione.removeAttribute(CostantiGenRicerche.TROVA_RICERCHE);
      }
      // Set nel request di un oggetto TrovaricercheForm non inizializzato.
      trovaRicercheForm = new TrovaRicercheForm();
      trovaRicercheForm.setNoCaseSensitive(Boolean.TRUE.toString());

      request.setAttribute("trovaRicercheForm", trovaRicercheForm);

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
      HttpServletResponse response, TrovaRicercheForm form) {

    HttpSession session = request.getSession();
    // Rimuozione della sessione dell'oggetto TrovaRicercheForm
    session.removeAttribute(CostantiGenRicerche.TROVA_RICERCHE);

    // Inserimento in sessione del contenitore dei parametri di trova ricerche
    session.setAttribute(CostantiGenRicerche.TROVA_RICERCHE, form);
  }

}