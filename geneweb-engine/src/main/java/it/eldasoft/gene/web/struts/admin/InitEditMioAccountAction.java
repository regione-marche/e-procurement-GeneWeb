/*
 * Created on 16-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBase;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;

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
 * Azione che esegue il cambio della password nel database per l'utente. E'
 * un'azione diversa da tutte le altre del package, in quanto non eredita da
 * AbstractActionBaseAdmin dato che è un'azione generale standard.
 * 
 * @author Mirco.Franzoni
 */
public class InitEditMioAccountAction extends DispatchActionBase {

  /** Logger Log4J di classe */
  static Logger               logger                = Logger.getLogger(InitEditMioAccountAction.class);

  /**
   * Manager di gestione degli account
   */
  private AccountManager      accountManager;

  /**
   * @param accountManager
   *        The accountManager to set.
   */
  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioneAcquistata()
   */
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_DEFAULT;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action modifica
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModifica() {
    return new CheckOpzioniUtente("");
  }

  public ActionForward modifica(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String messageKey = null;
    if (logger.isDebugEnabled()) logger.debug("modifica: inizio metodo");

    String target = null;
    Integer id = null;
    HttpSession sessione = request.getSession();
    ProfiloUtente profiloUtente = (ProfiloUtente) sessione.getAttribute("profiloUtente");
    String metodo = request.getParameter("metodo");
    target = CostantiGeneraliStruts.FORWARD_OK;
    id = new Integer(profiloUtente.getId());

    try {
      Account account = this.accountManager.getAccountById(id);
      MioAccountForm mioAccountForm = new MioAccountForm(account);
      mioAccountForm.setIdAccount(id.toString());
      request.setAttribute("mioAccountForm", mioAccountForm);
      request.setAttribute("metodo", metodo);
      sessione.setAttribute(CostantiGenerali.ID_OGGETTO_SESSION, id);
      sessione.setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
          account.getNome());

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
    if (logger.isDebugEnabled()) logger.debug("cambioBase: fine metodo");
    return mapping.findForward(target);
  }

}
