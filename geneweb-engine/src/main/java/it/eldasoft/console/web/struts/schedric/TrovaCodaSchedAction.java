/*
 * Created on 25-mag-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.console.web.struts.schedric;

import it.eldasoft.console.bl.schedric.SchedRicManager;
import it.eldasoft.console.db.domain.schedric.TrovaCodaSched;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.utils.sql.comp.SqlComposerException;

import java.io.IOException;
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
 * Action che esegue la trova per le schedulazioni restituendo la lista dei
 * risultati ottenuti.
 *
 * @author Francesco De Filippis
 */
public class TrovaCodaSchedAction extends DispatchActionBaseNoOpzioni {

  // Forward predefiniti per il DispatchAction in linea con il file
  // struts-config.xml
  private static final String FORWARD_LISTA_CODASCHED = "successListaCodaSched";
  private static final String FORWARD_TROVA_CODASCHED = "successTrovaCodaSched";

  /** Logger Log4J di classe */
  static Logger               logger                  = Logger.getLogger(TrovaCodaSchedAction.class);

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi alle
   * ricerche
   */
  private SchedRicManager    schedRicManager;

  /**
   * @param schedRicManager schedRicManager da settare internamente alla classe.
   */
  public void setSchedRicManager(SchedRicManager schedRicManager) {
    this.schedRicManager = schedRicManager;
  }

  /**
   * Esegue la query per la recuperare le ricerche in linee con i parametri
   * inseriti nel form della pagina TrovaRicerche, caricando nel request la
   * lista delle ricerche e le relative informazioni.
   */
  public ActionForward trovaCodaSched(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("trovaCodaSched: inizio metodo");
    }

    // Target di default per l'azione 'trovaRicerche', da modificare nel momento
    // in cui si verificano dei problemi
    String target = TrovaCodaSchedAction.FORWARD_LISTA_CODASCHED;
    String messageKey = null;

    try {

      // lettura dal request del form TrovaCodaSchedForm per impostare i criteri
      // di ricerca
      TrovaCodaSchedForm trovaCodaSchedForm = (TrovaCodaSchedForm) form;

      if (trovaCodaSchedForm == null
          || this.isTrovaCodaSchedFormArgomentiNull(trovaCodaSchedForm)) {
        // l'azione è stata richiamata per ripetere l'ultima ricerca effettuata,
        // quindi i dati relativi al form sono da leggere dalla sessione.
        trovaCodaSchedForm = (TrovaCodaSchedForm) request.getSession().getAttribute(
            CostantiCodaSched.TROVA_CODASCHED);
      }
      // Esecuzione della logica di business per recuperare la lista delle
      // schedulazioni delle ricerche
      TrovaCodaSched trovaCodaSched = trovaCodaSchedForm.getDatiPerModel();
      trovaCodaSched.setProfiloOwner((String) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_ATTIVO));
      List<?> listaCodaSched = this.schedRicManager.getCodaSchedulazioni(trovaCodaSched);

      if (listaCodaSched != null
          && !listaCodaSched.isEmpty()
          && listaCodaSched.size() > 0) {
        // Set nel request della lista di ricerche
        request.setAttribute("listaCodaSched", listaCodaSched);
        request.setAttribute("risultatiPerPagina",
            "Tutti".equals(trovaCodaSchedForm.getRisPerPagina())
                ? null
                : trovaCodaSchedForm.getRisPerPagina());

      } else {
        target = CostantiGeneraliStruts.FORWARD_NESSUN_RECORD_TROVATO;
        messageKey = "info.search.noRecordFound";
        this.aggiungiMessaggio(request, messageKey);
      }

      // Aggiornamento del contenitore dei parametri di ricerca presente in
      // sessione
      this.updateParametriTrovaCodaSched(request, response, trovaCodaSchedForm);

    } catch (SqlComposerException sc) {
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
      logger.debug("trovaCodaSched: fine metodo");
    }
    return mapping.findForward(target);
  }

  /**
   * @param trovaCodaSchedForm
   * @return Ritorna true se tutti gli attributi dell'oggetto passato per
   *         argomento sono tutti null o pari al valore di default, false
   *         altrimenti
   */
  private boolean isTrovaCodaSchedFormArgomentiNull(
      TrovaCodaSchedForm trovaCodaSchedForm) {
    boolean result = false;

    if( trovaCodaSchedForm.getNome() == null &&
        trovaCodaSchedForm.getMsg() == null &&
        trovaCodaSchedForm.getNomeRicerca() == null &&
        trovaCodaSchedForm.getIdRicerca() == null &&
        trovaCodaSchedForm.getStato() == null &&
        "20".equals(trovaCodaSchedForm.getRisPerPagina()))
      result = true;

    return result;
  }

  /**
   * Cancella dalla sessione l'oggetto di tipo TrovaCodaSchedForm, contenente i
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
    String target = TrovaCodaSchedAction.FORWARD_TROVA_CODASCHED;
    String messageKey = null;

    try {
      HttpSession sessione = request.getSession();
      TrovaCodaSchedForm trovaCodaSchedForm = (TrovaCodaSchedForm) sessione.getAttribute(CostantiCodaSched.TROVA_CODASCHED);
      if (trovaCodaSchedForm != null) {
        // l'oggetto in sessione esiste e quindi lo rimuovo
        sessione.removeAttribute(CostantiCodaSched.TROVA_CODASCHED);
      }
      // Set nel request di un oggetto TrovaCodaSchedForm non inizializzato.
      trovaCodaSchedForm = new TrovaCodaSchedForm();
      trovaCodaSchedForm.setNoCaseSensitive(Boolean.TRUE.toString());
      request.setAttribute("trovaCodaSchedForm", trovaCodaSchedForm);

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
  private void updateParametriTrovaCodaSched(HttpServletRequest request,
      HttpServletResponse response, TrovaCodaSchedForm form) {

    HttpSession session = request.getSession();
    // Rimozione della sessione dell'oggetto TrovaCodaSchedForm
    session.removeAttribute(CostantiCodaSched.TROVA_CODASCHED);

    // Inserimento in sessione del contenitore dei parametri di trova schedric
    session.setAttribute(CostantiCodaSched.TROVA_CODASCHED, form);
  }

}