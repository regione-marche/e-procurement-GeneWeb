/*
 * Created on 07-03-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.MessageManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.ListaForm;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;

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
 * Dispatch Action per visualizzazione lista account ed elimina account multiplo
 * 
 * @author cit_defilippis
 */
public class ListaAccountAction extends AbstractDispatchActionBaseAdmin {

  protected static final String FORWARD_SUCCESS_ELIMINA      = "successElimina";
  
  protected AccountManager accountManager;
  
  protected MessageManager messageManager;

  static Logger          logger = Logger.getLogger(ListaAccountAction.class);

  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }

  public void setMessageManager(MessageManager messageManager) {
  this.messageManager = messageManager;
}
  /**
   * Funzione che restituisce le opzioni per accedere alla action eliminaSelez
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniEliminaSelez() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  public ActionForward eliminaSelez(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("eliminaSelez: inizio metodo");
    String target = FORWARD_SUCCESS_ELIMINA;
    String messageKey = null;
    
    ListaForm listaIdAccount = (ListaForm) form;
    try {
      
      HttpSession sessione = request.getSession();
      ProfiloUtente profiloUtente = (ProfiloUtente) sessione.getAttribute("profiloUtente");
      int id = new Integer(profiloUtente.getId());
      Account currentUser = this.accountManager.getAccountById(id);
      
      //verifico se l'utente in uso è amministratore di sistema 
      OpzioniUtente opzioniUtenteCorrente = new OpzioniUtente(currentUser.getOpzioniUtente());
      CheckOpzioniUtente checkCurrentAmministratore = new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
      
      if(checkCurrentAmministratore.test(opzioniUtenteCorrente)){
        for(int i=0; i < listaIdAccount.getId().length; i++){
          id = new Integer(listaIdAccount.getId()[i]);
          Account accountToDelete = this.accountManager.getAccountById(id);
          //verifico se l'utente che sto eliminando è amministratore di sistema 
          OpzioniUtente opzioniUtenteDaEliminare = new OpzioniUtente(accountToDelete.getOpzioniUtente());
          CheckOpzioniUtente checkAmministratore = new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
        
          if (checkAmministratore.test(opzioniUtenteDaEliminare)){
            this.messageManager.insertMessageToUsersAdministrator("L'amministratore "+ currentUser.getLogin() +" (id = "+ currentUser.getIdAccount() +") ha rimosso l'utenza amministrativa "+ accountToDelete.getLogin() +" (id = "+ accountToDelete.getIdAccount() +")","", currentUser.getIdAccount());
            this.accountManager.insertCancellazioneUtente(accountToDelete);
          }
          this.accountManager.deleteAccount(id);
        }
      }
      else{
        for(int i=0; i < listaIdAccount.getId().length; i++){
          id = new Integer(listaIdAccount.getId()[i]);
          Account accountToDelete = this.accountManager.getAccountById(id);
          //verifico se l'utente che sto eliminando è amministratore di sistema 
          OpzioniUtente opzioniUtenteDaEliminare = new OpzioniUtente(accountToDelete.getOpzioniUtente());
          CheckOpzioniUtente checkAmministratore = new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
        
          if (checkAmministratore.test(opzioniUtenteDaEliminare)){
            target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
            messageKey = "errors.deleteUtente.permessoNegato";
            logger.error(this.resBundleGenerale.getString(messageKey));
            this.aggiungiMessaggio(request, messageKey);
          }else{
            this.accountManager.deleteAccount(id);
          }
        }
      }
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
    if (logger.isDebugEnabled()) logger.debug("eliminaSelez: fine metodo");
    return mapping.findForward(target);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action elimina
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniElimina() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  public ActionForward elimina(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("elimina: inizio metodo");
    String target = FORWARD_SUCCESS_ELIMINA;
    String messageKey = null;

    String id = request.getParameter("idAccount");
    Integer i = Integer.valueOf(id);
    
    try {
      
      //inserisco il log di inserimento nuovo utente nel database
      Account accountToDelete = this.accountManager.getAccountById(i);
      Account account = this.accountManager.getAccountById(i);
      HttpSession sessione = request.getSession();
      ProfiloUtente profiloUtente = (ProfiloUtente) sessione.getAttribute("profiloUtente");
      int idAutore = new Integer(profiloUtente.getId());
      Account currentUser = this.accountManager.getAccountById(idAutore);
      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setLivEvento(LogEvento.LIVELLO_INFO);
      logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_DEL_USER);
      logEvento.setDescr("L'utente " + idAutore + " ha eliminato l'utente con id =" + i + " e login = "+ account.getLogin());
      logEvento.setErrmsg("");
      LogEventiUtils.insertLogEventi(logEvento);
      //verifico se l'utente che sto eeiminando è amministratore di sistema 
      OpzioniUtente opzioniUtenteDaEliminare = new OpzioniUtente(accountToDelete.getOpzioniUtente());
      CheckOpzioniUtente checkAmministratore = new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
      //verifico se l'utente attualmente in uso è amministratore di sistema
      OpzioniUtente opzioniCurrentUtente = new OpzioniUtente(currentUser.getOpzioniUtente());
      CheckOpzioniUtente checkCurrentAmministratore = new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
     
      if(checkAmministratore.test(opzioniUtenteDaEliminare) && !checkCurrentAmministratore.test(opzioniCurrentUtente)){
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey ="errors.deleteUtente.permessoNegato";
        this.aggiungiMessaggio(request, messageKey);
      }else{
      if (checkAmministratore.test(opzioniUtenteDaEliminare)){
      this.messageManager.insertMessageToUsersAdministrator("L'amministratore "+ currentUser.getLogin() +" (id = "+ currentUser.getIdAccount() +") ha rimosso l'utenza amministrativa "+ accountToDelete.getLogin() +" (id = "+ accountToDelete.getIdAccount() +")","", currentUser.getIdAccount());
      this.accountManager.insertCancellazioneUtente(accountToDelete);
      }
      this.accountManager.deleteAccount(i);
      request.setAttribute("metodo", "visualizzaLista");}
      
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

    if (logger.isDebugEnabled()) logger.debug("elimina: fine metodo");
    return mapping.findForward(target);
  }
  
}