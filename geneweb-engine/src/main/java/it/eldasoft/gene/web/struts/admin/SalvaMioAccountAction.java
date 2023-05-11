/*
 * Created on Oct 19, 2006
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
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.tags.history.UtilityHistory;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.GestoreEccezioni;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action per insert e modifica di un account
 *
 * @author cit_defilippis
 */
public class SalvaMioAccountAction extends AbstractDispatchActionBaseAdmin {

  static Logger               logger                 = Logger.getLogger(SalvaMioAccountAction.class);

  private static final String TORNA_A_HOME_PAGE = "tornaAHomePage";


  private AccountManager      accountManager;


  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
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
    if (logger.isDebugEnabled()) logger.debug("modifica: inizio metodo");
    String target = null;
    ActionForward vaiA = null;
    String messageKey = null;

    MioAccountForm mioAccountForm = (MioAccountForm) form;

    try {
      Integer id = Integer.valueOf(mioAccountForm.getIdAccount());
      Account accountDb = this.accountManager.getAccountById(id);
      accountDb.setNome(mioAccountForm.getNome());
      accountDb.setEmail(mioAccountForm.getEmail());
      accountDb.setCodfisc(mioAccountForm.getCodfisc());
      this.accountManager.updateAccount(accountDb);
     

        // Set in sessione di id e nome dell'account che si sta modificando
        request.getSession().setAttribute(CostantiGenerali.ID_OGGETTO_SESSION,
            new Integer(accountDb.getIdAccount()));
        request.getSession().setAttribute(
            CostantiGenerali.NOME_OGGETTO_SESSION, accountDb.getNome());

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

    if (logger.isDebugEnabled()) logger.debug("modifica: fine metodo");
    
    if (target != null)
        vaiA = mapping.findForward(target);
      else {
        try {
          UtilityHistory history = UtilityTags.getUtilityHistory(request.getSession());
          if (history.size(0) == 0)
            vaiA = mapping.findForward(TORNA_A_HOME_PAGE);
          else
            vaiA = UtilityTags.getUtilityHistory(request.getSession()).back(request);
        } catch (JspException e) {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.applicazione.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), e);
          this.aggiungiMessaggio(request, messageKey);
        }
      }
    
    return vaiA;
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action annulla
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniAnnulla() {
    return new CheckOpzioniUtente("");
  }
  /**
   * Meotodo che riporta alla pagina precedente caricata nell'history oppure
   * alla pagina di home qualora l'history abbia dimensione nulla
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward annulla(ActionMapping mapping, ActionForm form,
	      HttpServletRequest request, HttpServletResponse response)
	      throws IOException, ServletException {
	    ActionForward actForward = null;

	    // Setto il modo di update
	    String modo = UtilityStruts.getParametroString(request,
	        UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO);
	    try {
	      UtilityHistory history = UtilityTags.getUtilityHistory(request.getSession());
	      if (history.size(0) == 0)
	        actForward = mapping.findForward(TORNA_A_HOME_PAGE);
	      else
	        actForward = history.back(request);
	    } catch (Throwable t) {
	      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO, modo);
	      actForward = GestoreEccezioni.gestisciEccezioneAction(t, this, request,
	          logger, mapping);
	    }
	    return actForward;
	  }



}