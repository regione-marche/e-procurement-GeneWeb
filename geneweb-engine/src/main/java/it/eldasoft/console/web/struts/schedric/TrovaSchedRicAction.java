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
import it.eldasoft.console.db.domain.schedric.TrovaSchedRic;
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
public class TrovaSchedRicAction extends DispatchActionBaseNoOpzioni {

  // Forward predefiniti per il DispatchAction in linea con il file
  // struts-config.xml
  private static final String FORWARD_LISTA_SCHEDRIC = "successTrova";
  private static final String FORWARD_TROVA_SCHEDRIC = "successNuovaRicerca";

  /** Logger Log4J di classe */
  static Logger               logger                 = Logger.getLogger(TrovaSchedRicAction.class);

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi alle
   * ricerche
   */
  private SchedRicManager     schedRicManager;

  /**
   * @return Ritorna ricercheManager.
   */
  public SchedRicManager getSchedRicManager() {
    return this.schedRicManager;
  }

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setSchedRicManager(SchedRicManager schedRicManager) {
    this.schedRicManager = schedRicManager;
  }

  public ActionForward trovaSchedRic(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("trovaSchedRic: inizio metodo");
    }

    String target = TrovaSchedRicAction.FORWARD_LISTA_SCHEDRIC;
    String messageKey = null;

    try {

      // lettura dal request del form TrovaSchedRicForm per impostare i criteri
      // di ricerca
      TrovaSchedRicForm trovaSchedRicForm = (TrovaSchedRicForm) form;

      if (trovaSchedRicForm == null
          || this.isTrovaSchedRicFormArgomentiNull(trovaSchedRicForm)) {
        // l'azione è stata richiamata per ripetere l'ultima ricerca effettuata,
        // quindi i dati relativi al form sono da leggere dalla sessione.
        trovaSchedRicForm = (TrovaSchedRicForm) request.getSession().getAttribute(
            CostantiSchedRic.TROVA_SCHEDRIC);
      }
      // Esecuzione della logica di business per recuperare la lista delle
      // schedulazioni delle ricerche
      TrovaSchedRic trovaSchedRic = trovaSchedRicForm.getDatiPerModel();
      trovaSchedRic.setProfiloOwner((String) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_ATTIVO));
      List<?> listaSchedRic = this.schedRicManager.getSchedulazioniRicerche(trovaSchedRic);

      if (listaSchedRic != null
          && !listaSchedRic.isEmpty()
          && listaSchedRic.size() > 0) {
        // Set nel request della lista
        request.setAttribute("listaSchedRic", listaSchedRic);
        request.setAttribute("risultatiPerPagina",
            "Tutti".equals(trovaSchedRicForm.getRisPerPagina())
                ? null
                : trovaSchedRicForm.getRisPerPagina());

      } else {
        target = CostantiGeneraliStruts.FORWARD_NESSUN_RECORD_TROVATO;
        messageKey = "info.search.noRecordFound";
        this.aggiungiMessaggio(request, messageKey);
      }

      // Aggiornamento del contenitore dei parametri di ricerca presente in
      // sessione
      request.getSession().setAttribute(CostantiSchedRic.TROVA_SCHEDRIC,
          trovaSchedRicForm);

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
      logger.debug("trovaSchedRic: fine metodo");
    }
    return mapping.findForward(target);
  }

  /**
   * @param trovaSchedRicForm
   * @return Ritorna true se tutti gli attributi dell'oggetto passato per
   *         argomento sono tutti null o pari al valore di default, false
   *         altrimenti
   */
  private boolean isTrovaSchedRicFormArgomentiNull(
      TrovaSchedRicForm trovaSchedRicForm) {
    boolean result = false;

    if (trovaSchedRicForm.getNome() == null
        && trovaSchedRicForm.getAttivo() == null
        && trovaSchedRicForm.getOwner() == null
        && trovaSchedRicForm.getTipo() == null
        && "20".equals(trovaSchedRicForm.getRisPerPagina())
        && !Boolean.valueOf(trovaSchedRicForm.getNoCaseSensitive())) result = true;

    return result;
  }

  /**
   * Cancella dalla sessione l'oggetto di tipo TrovaSchedRicForm, contenente i
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
    String target = TrovaSchedRicAction.FORWARD_TROVA_SCHEDRIC;
    String messageKey = null;

    try {
      HttpSession sessione = request.getSession();
      TrovaSchedRicForm trovaSchedRicForm = (TrovaSchedRicForm) sessione.getAttribute(CostantiSchedRic.TROVA_SCHEDRIC);
      if (trovaSchedRicForm != null) {
        // l'oggetto in sessione esiste e quindi lo rimuovo
        sessione.removeAttribute(CostantiSchedRic.TROVA_SCHEDRIC);
      }
      // Set nel request di un oggetto TrovaSchedRicForm non inizializzato.
      trovaSchedRicForm = new TrovaSchedRicForm();
      trovaSchedRicForm.setNoCaseSensitive(Boolean.TRUE.toString());
      request.setAttribute("trovaSchedRicForm", trovaSchedRicForm);

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

}