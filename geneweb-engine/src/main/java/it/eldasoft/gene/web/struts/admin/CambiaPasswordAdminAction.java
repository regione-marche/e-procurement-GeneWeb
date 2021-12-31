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
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.GestoreEccezioni;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.utility.UtilityPassword;

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
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Azione che esegue il cambio della password nel database per l'utente. E'
 * un'azione diversa da tutte le altre del package, in quanto non eredita da
 * AbstractActionBaseAdmin dato che è un'azione generale standard.
 * 
 * @author Stefano.Sabbadin
 */
public class CambiaPasswordAdminAction extends AbstractDispatchActionBaseAdmin {

  /** Logger Log4J di classe */
  static Logger          logger = Logger.getLogger(CambiaPasswordAction.class);

  /** Reference alla classe di business logic per l'estrazione dell'account */
  private AccountManager accountManager;

 
  /**
   * @param accountManager
   *        accountManager da settare internamente alla classe.
   */
  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action modifica
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModifica() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  /**
   * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward modifica(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("modifica: inizio metodo");
    }

    // target di default, da modificare nel momento in cui si verificano dei
    // problemi
    String target = null;
    String messageKey = null;

    ActionForward vaiA = null;
    // si leggono i dati ricevuti dal form
    CambiaPasswordAdminForm chPswForm = (CambiaPasswordAdminForm) form;

    Account account = null;
    String id = "";
    // gestione USRSYS e login/password gestite in modo legacy
    try {

      // F.D. 11/07/2007 gestione protezione password
      // viene inserita la gestione della sicurezza delle password: ogni cambio
      // di password viene memorizzato in una tabella apposita in modo da
      // potere controllare se la password è già stata utilizzata in passato.
      // Se la coppia login/password nuova non è stata utilizzata in passato
      // allora inserisco il record nella storia e modifico la password

      // se l'utente è abilitato alla sicurezza password andiamo a gestire la
      // storia delle password effettuando la insert sulla STOUTESYS

      // per inserire la storia delle password ho bisogno di caricare l'account
      // e verificare se è attivata la voce sicurezza password e utilizzare
      // anche la password che ho memorizzato nel profilo per salvarla
      id = chPswForm.getIdAccount();
      account = this.accountManager.getAccountById(new Integer(
          chPswForm.getIdAccount()));
      OpzioniUtente opzioniUtente = new OpzioniUtente(
          account.getOpzioniUtente());
      
      if(!UtilityPassword.isPasswordCaratteriAmmessi(chPswForm.getNuovaPassword())){
        messageKey = "errors.gestoreException.*.gestoreFormRegistrazione.passwordChrNoAmm";
        this.aggiungiMessaggio(request, messageKey);
        throw new GestoreException("Errore: La password contiene caratteri non ammessi!", "gestoreFormRegistrazione.passwordChr2Num", null);
      }
      
      CheckOpzioniUtente opzioniSicurezzaPassword = new CheckOpzioniUtente(
          CostantiGeneraliAccount.OPZIONI_SICUREZZA_PASSWORD);
      CheckOpzioniUtente opzioniAmministratore = new CheckOpzioniUtente(
          CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
      
      int amministrator = 8;
      if (opzioniAmministratore.test(opzioniUtente)) {amministrator = 14;}
      if(opzioniSicurezzaPassword.test(opzioniUtente)){
        if(chPswForm.getNuovaPassword().length() < amministrator){
          messageKey = "errors.gestoreException.*.gestoreFormRegistrazione.passwordCorta";
          this.aggiungiMessaggio(request, messageKey, Integer.toString(amministrator));
          target = "errataVecchiaPassword";
        }
        if(!UtilityPassword.hasPasswordMinimoUnaCifra(chPswForm.getNuovaPassword())){
          messageKey = "errors.gestoreException.*.gestoreFormRegistrazione.passwordChrNum";
          this.aggiungiMessaggio(request, messageKey);
          target = "errataVecchiaPassword";
        }
        if(!UtilityPassword.hasPasswordMinimoUnaMaiuscola(chPswForm.getNuovaPassword())){
          messageKey = "errors.gestoreException.*.gestoreFormRegistrazione.passwordChrMaiusc";
          this.aggiungiMessaggio(request, messageKey);
          target = "errataVecchiaPassword";
        }
        if(UtilityPassword.passwordSimilarity(chPswForm.getNuovaPassword(),account.getNome())){
          messageKey = "errors.gestoreException.*.gestoreFormRegistrazione.passwordSimilarityNomeUtente";
          this.aggiungiMessaggio(request, messageKey);
          target = "errataVecchiaPassword";
        }
        if(UtilityPassword.passwordSimilarity(chPswForm.getNuovaPassword(),account.getCodfisc())){
          messageKey = "errors.gestoreException.*.gestoreFormRegistrazione.passwordSimilarityCFUtente";
          this.aggiungiMessaggio(request, messageKey);
          target = "errataVecchiaPassword";
        }
        if(target != null){return mapping.findForward(target);}
        logger.debug("errori non trovati" );
        this.accountManager.updatePasswordSenzaVecchiaInsertStorico(new Integer(
            chPswForm.getIdAccount()).intValue(), account.getLogin(),
            chPswForm.getNuovaPassword());
      } else {
        // aggiornamento della password dell'account
        this.accountManager.updatePasswordSenzaVecchia((new Integer(
            chPswForm.getIdAccount())).intValue(), chPswForm.getNuovaPassword());
      }
      messageKey = "info.chgPsw.ok";
      if (logger.isInfoEnabled())
        logger.info(this.resBundleGenerale.getString(messageKey));

      request.setAttribute(CostantiGenerali.ATTR_MESSAGGIO_ESITO_OPERAZIONE,
          this.resBundleGenerale.getString(messageKey));

    } catch (DataIntegrityViolationException e) {
      // la password scelta è già stata utilizzata in passato
      chPswForm = new CambiaPasswordAdminForm();
      chPswForm.setIdAccount(id);
      request.setAttribute("cambiaPasswordAdminForm", chPswForm);

      request.getSession().setAttribute(CostantiGenerali.ID_OGGETTO_SESSION,
          account.getNome());
      request.getSession().setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
          account.getNome());
      // request.setAttribute("cambiaPasswordForm", chPswForm);
      target = "errataVecchiaPassword";
      messageKey = "errors.chgPsw.passwordGiaUtilizzata";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (CriptazioneException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = e.getChiaveResourceBundle();
      logger.error(this.resBundleGenerale.getString(messageKey), e);
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
      logger.debug("modifica: fine metodo");
    }

    if (target != null){
      vaiA = mapping.findForward(target);
      CheckOpzioniUtente opzioniSicurezzaPassword = new CheckOpzioniUtente(
          CostantiGeneraliAccount.OPZIONI_SICUREZZA_PASSWORD);
      
      // Determino se l'utente a cui si sta cambiando la password ha impostata
      // l'ou39 (per sicurezza password), per passare un parametro alla pagina
      OpzioniUtente opzioniUtente = new OpzioniUtente(account.getOpzioniUtente());
      if (opzioniSicurezzaPassword.test(opzioniUtente))
        request.setAttribute("controlloPasswordUtenteAttivo", Boolean.TRUE);
      else
        request.setAttribute("controlloPasswordUtenteAttivo", Boolean.FALSE);
      
    } else {
      try {
        vaiA = UtilityTags.getUtilityHistory(request.getSession()).back(request);
        
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setLivEvento(LogEvento.LIVELLO_INFO);
        logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_CHANGE_PASSWORD);
        logEvento.setDescr("Cambio password utente con id = " + id + " e login = " + account.getLogin());
        logEvento.setErrmsg("");
        LogEventiUtils.insertLogEventi(logEvento);
        
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
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  // action annulla che mi riporta alla pagina precedente caricata nell'history
  public ActionForward annulla(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // Setto il modo di update
    String modo = UtilityStruts.getParametroString(request,
        UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO);
    try {

      return UtilityTags.getUtilityHistory(request.getSession()).back(request);
    } catch (Throwable t) {
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO, modo);
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }
  }
}
