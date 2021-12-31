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
package it.eldasoft.gene.web.struts.genmod.impexp;

import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.genmod.TrovaModelli;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.sql.comp.SqlComposerException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 * Action per la ricerca dei modelli per esportarne la definizione
 *
 * @author Francesco.DeFilippis
 */
public class TrovaModelliExportAction extends DispatchActionBaseNoOpzioni {

  // Forward predefiniti per il DispatchAction in linea con il file
  // struts-config.xml
  private static final String FORWARD_LISTA_MODELLI = "successListaModelli";
  private static final String FORWARD_TROVA_MODELLI = "successTrovaModelli";


  /**   logger di Log4J   */
  static Logger logger = Logger.getLogger(TrovaModelliExportAction.class);

  @Override
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.ABILITAZIONE_DEFAULT;
  }

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi ai
   * modelli
   */
  private ModelliManager     modelliManager;

  /**
   * @param modelliManager
   *        modelliManager da settare internamente alla classe.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action trovaModelli
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniTrovaModelli() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }

  /**
   * Esegue la query per la recuperare i modelli in linea con i parametri
   * inseriti nel form della pagina TrovaModelli, caricando nel request la
   * lista dei modelli e le relative informazioni.
   */
  public ActionForward trovaModelli(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("trovaModelli: inizio metodo");
    }

    // Target di default per l'azione 'trovaModelli', da modificare nel momento
    // in cui si verificano dei problemi
    String target = TrovaModelliExportAction.FORWARD_LISTA_MODELLI;
    String messageKey = null;
    HttpSession session = request.getSession();

    try {
      // cancellazione di oggetti in sessione precedentemente creati
      this.cleanSession(request);

      // lettura dal request del form TrovaModelliExportForm per impostare i criteri
      // di ricerca
      TrovaModelliExportForm trovaModelliExportForm = (TrovaModelliExportForm) form;

      // Nel caso in cui si deve ritornare alla pagina Lista Modelli il form
      // contiene un oggetto di tipo TrovaModelliExportForm con tutti gli argomenti null
      // anche se la lista dei modelli e' filtrata per qualche parametro.
      // Quanto segue evita di tornare alla lista dei modelli precedentemente
      // ottenuta, caricando l'oggetto dalla sessione
      if(trovaModelliExportForm == null || this.isTrovaModelliFormArgomentiNull(trovaModelliExportForm)){
        // l'azione è stata richiamata per ripetere l'ultima ricerca effettuata,
        // quindi i dati relativi al form sono da leggere dalla sessione.
        trovaModelliExportForm = (TrovaModelliExportForm)
            request.getSession().getAttribute(
                CostantiGenModelli.TROVA_MODELLI_EXPORT);
      }

      // Esecuzione della logica di business per recuperare la lista dei
      // modelli
      TrovaModelli trovaModelli = trovaModelliExportForm.getDatiPerModel();
      //trovaModelli.setCodiceApplicazione((String) session.getAttribute(
      //    CostantiGenerali.MODULO_ATTIVO));
      trovaModelli.setCodiceProfiloAttivo((String) session.getAttribute(
          CostantiGenerali.PROFILO_ATTIVO));

      List<?> listaModelli = this.modelliManager.getModelli(trovaModelli);

      if (listaModelli != null
          && !listaModelli.isEmpty()
          && listaModelli.size() > 0) {
        // Set nel request della lista di modelli
        request.setAttribute("listaModelli", listaModelli);
        request.setAttribute("risultatiPerPagina",
            "Tutti".equals(trovaModelliExportForm.getRisPerPagina())
                ? null
                : trovaModelliExportForm.getRisPerPagina());

        this.gestioneVociAvanzamento(request,
            CostantiWizard.CODICE_PAGINA_LISTA_MODELLI_PER_EXPORT);

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
      this.updateParametriTrovaModelli(request, response, trovaModelliExportForm);

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
      logger.debug("trovaModelli: fine metodo");
    }
    return mapping.findForward(target);
  }

  /**
   * @param trovaRicercheExportForm
   * @return Ritorna true se tutti gli attributi dell'oggetto passato per argomento
   * sono tutti null o pari al valore di default, false altrimenti
   */
  private boolean isTrovaModelliFormArgomentiNull(TrovaModelliExportForm trovaModelliExportForm){
    boolean result = false;

    if(trovaModelliExportForm.getDescrModello() == null &&
        trovaModelliExportForm.getNomeModello() == null &&
       trovaModelliExportForm.getDisponibile() == null &&
       trovaModelliExportForm.getFileModello() == null &&
       trovaModelliExportForm.getOwner() == null &&
       trovaModelliExportForm.getPersonale() == null &&
       trovaModelliExportForm.getTipoDocumento() == null &&
       !Boolean.valueOf(trovaModelliExportForm.getNoCaseSensitive()))
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
    String target = TrovaModelliExportAction.FORWARD_TROVA_MODELLI;
    String messageKey = null;

    try {
      HttpSession sessione = request.getSession();
      TrovaModelliExportForm trovaModelliExportForm = (TrovaModelliExportForm)
          sessione.getAttribute(CostantiGenModelli.TROVA_MODELLI_EXPORT);
      if (trovaModelliExportForm != null) {
        // l'oggetto in sessione esiste e quindi lo rimuovo
        sessione.removeAttribute(CostantiGenModelli.TROVA_MODELLI_EXPORT);
      }
      // Set nel request di un oggetto TrovaModelliExportForm non inizializzato.
      trovaModelliExportForm = new TrovaModelliExportForm();
      trovaModelliExportForm.setNoCaseSensitive(Boolean.TRUE.toString());
      request.setAttribute("trovaModelliExportForm", trovaModelliExportForm);

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
  private void updateParametriTrovaModelli(HttpServletRequest request,
      HttpServletResponse response, TrovaModelliExportForm form) {

    HttpSession session = request.getSession();
    // Rimuozione della sessione dell'oggetto TrovaModelliExportForm
    session.removeAttribute(CostantiGenModelli.TROVA_MODELLI_EXPORT);

    // Inserimento in sessione del contenitore dei parametri di trova modelli
    session.setAttribute(CostantiGenModelli.TROVA_MODELLI_EXPORT, form);
  }

  private void gestioneVociAvanzamento(HttpServletRequest request,
      String paginaAttiva) {
    List<String> pagineVisitate = new ArrayList<String>();
    List<String> pagineDaVisitare = new ArrayList<String>();

    if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_TROVA_MODELLI_PER_EXPORT)) {
      pagineVisitate.add(CostantiWizard.TITOLO_TROVA_MODELLI_PER_EXPORT);

      pagineDaVisitare.add(CostantiWizard.TITOLO_LISTA_MODELLI_PER_EXPORT);
    } else if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_LISTA_MODELLI_PER_EXPORT)) {
      pagineVisitate.add(CostantiWizard.TITOLO_TROVA_MODELLI_PER_EXPORT);
      pagineVisitate.add(CostantiWizard.TITOLO_LISTA_MODELLI_PER_EXPORT);
    }
    request.setAttribute("pagineVisitate", pagineVisitate);
    request.setAttribute("pagineDaVisitare", pagineDaVisitare);
  }

}