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
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;
import java.sql.Timestamp;

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
 * @author Stefano.Sabbadin
 */
public class InitCambiaPasswordAdminAction extends DispatchActionBase {

  private static final String FORWARD_SUCCESS_ADMIN = "successAdmin";

  /** Logger Log4J di classe */
  static Logger               logger                = Logger.getLogger(CambiaPasswordAction.class);

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
   * Funzione che restituisce le opzioni per accedere alla action cambioBase
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniCambioBase() {
    return new CheckOpzioniUtente("");
  }

  public ActionForward cambioBase(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String messageKey = null;
    if (logger.isDebugEnabled()) logger.debug("cambioBase: inizio metodo");

    // String provenienza = request.getParameter("provenienza");
    String target = null;
    Integer id = null;
    HttpSession sessione = request.getSession();
    ProfiloUtente profiloUtente = (ProfiloUtente) sessione.getAttribute("profiloUtente");

    target = CostantiGeneraliStruts.FORWARD_OK;
    id = new Integer(profiloUtente.getId());

    // carico i dati dell'account a cui voglio cambiare la password per prendere
    // il nome
    // e settarlo in sessione per visualizzarlo nella form
    if (logger.isDebugEnabled()) logger.debug("INIT CAMBIA PASSWORD BEGIN");
    try {
      Account account = this.accountManager.getAccountById(id);
      OpzioniUtente opzioniUtente = new OpzioniUtente(account.getOpzioniUtente());
      CheckOpzioniUtente checkSicurezza = new CheckOpzioniUtente(
          CostantiGeneraliAccount.OPZIONI_SICUREZZA_PASSWORD);
      if (checkSicurezza.test(opzioniUtente)){
       
        Timestamp LastChange = this.accountManager.getUltimoCambioPassword(id);
        int secondiNecessari = Integer.parseInt(ConfigManager.getValore(CostantiGenerali.PROP_INTERVALLO_CAMBIO_PASSWORD));
        Timestamp currentTime = new Timestamp(System.currentTimeMillis() - (1000 * secondiNecessari ));
        if (LastChange.after(currentTime)){
          //errore tracciato nel file di log
          if (logger.isDebugEnabled()) logger.debug("errore: tentativo da parte dell'utente " + account.getLogin() + " (" + account.getIdAccount() + ") di eseguire un cambio password prima che siano trascorsi " + secondiNecessari + " secondi dall\'ultimo cambio password");
          //display messaggio di errore
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.chgPsw.intervalloMinimo";
          logger.error(messageKey);
          this.aggiungiMessaggio(request, messageKey, Integer.toString(secondiNecessari));
          //tracciamento errore nel db
          LogEvento logEvento = LogEventiUtils.createLogEvento(request);
          logEvento.setLivEvento(LogEvento.LIVELLO_ERROR);
          logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_CHANGE_PASSWORD);
          logEvento.setDescr("Tentato cambio password da parte dell'utente " + account.getLogin() + " (" + account.getIdAccount() + ")");
          logEvento.setErrmsg("Ritentato un cambio password prima di " + secondiNecessari + " secondi dall'ultimo effettuato");
          LogEventiUtils.insertLogEventi(logEvento);
        }
      }else{
      
      CambiaPasswordAdminForm chPswForm = new CambiaPasswordAdminForm();
      chPswForm.setIdAccount(id.toString());
      request.setAttribute("cambiaPasswordAdminForm", chPswForm);

      // Se la vecchia password e' null, allora passo un parametro alla pagina
      // per evitare di chiedere all'utente la vecchia password e non fare alcun
      // controllo di obbligatorieta' di tale password
      if(account.getPassword() == null || (account.getPassword() != null &&
           account.getPassword().length() == 0))
        request.setAttribute("vecchiaPasswordIsNull", Boolean.TRUE);
      else
        request.setAttribute("vecchiaPasswordIsNull", Boolean.FALSE);
      sessione.setAttribute(CostantiGenerali.ID_OGGETTO_SESSION, id);
      sessione.setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
          account.getNome());
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
    if (logger.isDebugEnabled()) logger.debug("cambioBase: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action cambioAdmin
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniCambioAdmin() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  public ActionForward cambioAdmin(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String messageKey = null;

    // String provenienza = request.getParameter("provenienza");
    String target = null;
    Integer id = null;
    HttpSession sessione = request.getSession();
    // ProfiloUtente profiloUtente =
    // (ProfiloUtente)sessione.getAttribute("profiloUtente");
    // if ("admin".equalsIgnoreCase(provenienza)) {
    target = FORWARD_SUCCESS_ADMIN;
    id = new Integer(request.getParameter("idAccount"));

    // carico i dati dell'account a cui voglio cambiare la password per prendere
    // il nome
    // e settarlo in sessione per visualizzarlo nella form
    try {
      Account account = this.accountManager.getAccountById(id);
      CambiaPasswordAdminForm chPswForm = new CambiaPasswordAdminForm();
      chPswForm.setIdAccount(id.toString());
      request.setAttribute("cambiaPasswordAdminForm", chPswForm);
      
      // Determino se l'utente a cui si sta cambiando la password ha impostata
      // l'ou39 (per sicurezza password), per passare un parametro alla pagina
      OpzioniUtente opzioniUtente = new OpzioniUtente(account.getOpzioniUtente());
      CheckOpzioniUtente opzioniSicurezzaPassword = new CheckOpzioniUtente(
          CostantiGeneraliAccount.OPZIONI_SICUREZZA_PASSWORD);
      if (opzioniSicurezzaPassword.test(opzioniUtente)){
        request.setAttribute("controlloPasswordUtenteAttivo", Boolean.TRUE);}
      else{
        request.setAttribute("controlloPasswordUtenteAttivo", Boolean.FALSE);}
      
      CheckOpzioniUtente opzioniAmministratore = new CheckOpzioniUtente(
          CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
      if (opzioniAmministratore.test(opzioniUtente)){
        request.setAttribute("controlloAmministratoreUtenteAttivo", Boolean.TRUE);}
      else{
        request.setAttribute("controlloAmministratoreUtenteAttivo", Boolean.FALSE);}
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
    if (logger.isDebugEnabled()) logger.debug("cambioAdmin: fine metodo");
    return mapping.findForward(target);
  }

}
