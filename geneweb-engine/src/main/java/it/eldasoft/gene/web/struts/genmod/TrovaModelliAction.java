/*
 * Created on 09-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod;

import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.genmod.TrovaModelli;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
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

public class TrovaModelliAction extends AbstractDispatchActionBaseGenModelli {

  /* logger della classe */
  static Logger          logger = Logger.getLogger(TrovaModelliAction.class);

  private ModelliManager modelliManager;

  /**
   * @return Returns the modelliManager.
   */
  public ModelliManager getModelliManager() {
    return modelliManager;
  }

  /**
   * @param modelliManager
   *        The modelliManager to set.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action nuovaRicerca
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniNuovaRicerca() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  public ActionForward nuovaRicerca(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String target = CostantiGenModelli.FORWARD_OK_NUOVA_RICERCA;
    // successNuovaRicerca
    if (logger.isDebugEnabled()) logger.debug("nuovaRicerca: inizio metodo");
    String messageKey = null;

    try {
      // Aggiungo alla sessione l'ultima il filtro di ricerca
      TrovaModelliForm trovaModelliForm = new TrovaModelliForm();
      trovaModelliForm.setNoCaseSensitive(Boolean.TRUE.toString());
      request.getSession().setAttribute(
          CostantiGenModelli.TROVA_MODELLI_SESSION, trovaModelliForm);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) logger.debug("nuovaRicerca: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action trovaModelli
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniTrovaModelli() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  /**
   * Ricerca dei modelli
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward trovaModelli(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String target = CostantiGeneraliStruts.FORWARD_OK;
    if (logger.isDebugEnabled()) logger.debug("trovaModello: inizio metodo");

    String messageKey = null;
    HttpSession session = request.getSession();

    try {
      // lettura dal request del form TrovaRicercheForm per impostare i
      // criteri di ricerca
      TrovaModelliForm trovaModelliForm = (TrovaModelliForm) form;
      // Se non è settata la ricerca allora estrae l'ultima

      // F.D. 09/03/07 aggiungo al controllo i due nuovi campi (personale e
      // owner)
      if (trovaModelliForm.getTipoDocumento() == null
          || (trovaModelliForm.getIdGruppo() == null
              && trovaModelliForm.getDisponibile() == null
              && trovaModelliForm.getNomeModello() == null
              && "20".equals(trovaModelliForm.getRisPerPagina())
              && trovaModelliForm.getFileModello() == null
              && trovaModelliForm.getPersonale() == null && trovaModelliForm.getOwner() == null)) {

        // l'azione è stata richiamata per ripetere l'ultima ricerca
        // effettuata, quindi i dati
        // relativi al form sono da leggere dalla sessione.
        trovaModelliForm = (TrovaModelliForm) session.getAttribute(CostantiGenModelli.TROVA_MODELLI_SESSION);
      }

      if (trovaModelliForm == null) {
        trovaModelliForm = new TrovaModelliForm();
        trovaModelliForm.setNoCaseSensitive(Boolean.TRUE.toString());

      }

      // Esecuzione della logica di business per recuperare la lista dei
      // modelli
      TrovaModelli trovaModelli = trovaModelliForm.getDatiPerModel();
      trovaModelli.setCodiceApplicazione((String) session.getAttribute(
          CostantiGenerali.MODULO_ATTIVO));
      trovaModelli.setCodiceProfiloAttivo((String) session.getAttribute(
          CostantiGenerali.PROFILO_ATTIVO));

      List listaModelli = this.modelliManager.getModelli(trovaModelli);

      if (listaModelli != null
          && !listaModelli.isEmpty()
          && listaModelli.size() > 0) {
        // Set nel request della lista di modelli
        request.setAttribute("listaModelli", listaModelli);

      } else {
        target = CostantiGeneraliStruts.FORWARD_NESSUN_RECORD_TROVATO;
        messageKey = "info.search.noRecordFound";
        this.aggiungiMessaggio(request, messageKey);
      }

      // Aggiungo alla sessione l'ultima il filtro di ricerca
      request.getSession().setAttribute(
          CostantiGenModelli.TROVA_MODELLI_SESSION, trovaModelliForm);

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

    if (logger.isDebugEnabled()) logger.debug("trovaModello: fine metodo");

    return mapping.findForward(target);
  }
}
