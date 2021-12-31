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
import it.eldasoft.gene.tags.history.UtilityHistory;
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
import org.springframework.dao.IncorrectUpdateSemanticsDataAccessException;

/**
 * Azione che esegue il cambio della password nel database per l'utente. E'
 * un'azione diversa da tutte le altre del package, in quanto non eredita da
 * AbstractActionBaseAdmin dato che è un'azione generale standard.
 * 
 * @author Stefano.Sabbadin
 */
public class CambiaPasswordAction extends DispatchActionBase {

  private static final String TORNA_A_HOME_PAGE = "tornaAHomePage";
  private static final String SUCCESS_SCADUTA = "successScaduta";
  
  /** Logger Log4J di classe */
  static Logger               logger            = Logger.getLogger(CambiaPasswordAction.class);

  /** Reference alla classe di business logic per l'estrazione dell'account */
  private AccountManager      accountManager;

  /**
   * @param accountManager
   *        accountManager da settare internamente alla classe.
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
    // chiunque può cambiare la propria password
    return new CheckOpzioniUtente("");
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

    String target = this.salvaPassword(form, request,"modifica");
    ActionForward vaiA = null;
    String messageKey = null;

    if (target != null)
      vaiA = mapping.findForward(target);
    else {
      try {
        UtilityHistory history = UtilityTags.getUtilityHistory(request.getSession());
        if (history.size(0) == 0){
          vaiA = mapping.findForward(TORNA_A_HOME_PAGE);
          LogEvento logEvento = LogEventiUtils.createLogEvento(request);
          logEvento.setLivEvento(LogEvento.LIVELLO_INFO);
          logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_CHANGE_PASSWORD);
          logEvento.setDescr("Cambio password");
          logEvento.setErrmsg("");
          LogEventiUtils.insertLogEventi(logEvento);
        }
        else{
          vaiA = UtilityTags.getUtilityHistory(request.getSession()).back(request);}
      } catch (JspException e) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.applicazione.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
      }
    }
    
    if (logger.isDebugEnabled()) {
      logger.debug("modifica: fine metodo");
    }
    return vaiA;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action modifica
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModificaPasswordScaduta() {
    // chiunque può cambiare la propria password
    return new CheckOpzioniUtente("");
  }

  /**
   * Metodo per il cambio password in caso di scadenza password
   */
  public ActionForward modificaPasswordScaduta(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("modificaPasswordScaduta: inizio metodo");
    
    String target = SUCCESS_SCADUTA;
    String targetErrore = this.salvaPassword(form, request,"modificaPasswordScaduta");
    if (targetErrore != null){
      target = targetErrore;}
    else{
      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setLivEvento(LogEvento.LIVELLO_INFO);
      logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_CHANGE_PASSWORD);
      logEvento.setDescr("Cambio password scaduta");
      logEvento.setErrmsg("");
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    if("1".equals(request.getSession().getAttribute(CostantiGenerali.SENTINELLA_ACCESSO_AMMINISTRATORE))){
      target = "successAdminAccess";
    }
    
    if(logger.isDebugEnabled())
      logger.debug("modificaPasswordScaduta: fine metodo");

    return mapping.findForward(target);
  }

  private String salvaPassword(ActionForm form, HttpServletRequest request,String metodo) {

    String target = null;
    if (logger.isDebugEnabled()) {
      logger.debug("salvaPassword: inizio metodo");
    }

    // target di default, da modificare nel momento in cui si verificano dei
    // problemi
    // String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    // si leggono i dati ricevuti dal form
    CambiaPasswordForm chPswForm = (CambiaPasswordForm) form;
    ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    // SS: 02/10/2006: sostituita con gestione USRSYS e login/password gestite
    // in modo legacy
    // CriptazioneByte criptatoreOldPsw = null;
    // CriptazioneByte criptatoreNewPsw = null;
    // DatoBase64 oldPswB64 = null;
    // DatoBase64 newPswB64 = null;

    try {

      // // conversione vecchia password in formato del DB per il confronto
      // criptatoreOldPsw = new CriptazioneByte(
      // chPswForm.getVecchiaPassword().getBytes(),
      // CriptazioneByte.FORMATO_DATO_NON_CIFRATO);
      // oldPswB64 = new DatoBase64(criptatoreOldPsw.getDatoCifrato(),
      // DatoBase64.FORMATO_ASCII);
      //
      // // conversione nuova password in formato del DB per l'aggiornamento
      // criptatoreNewPsw = new CriptazioneByte(
      // chPswForm.getNuovaPassword().getBytes(),
      // CriptazioneByte.FORMATO_DATO_NON_CIFRATO);
      // newPswB64 = new DatoBase64(criptatoreNewPsw.getDatoCifrato(),
      // DatoBase64.FORMATO_ASCII);

      // F.D. 11/07/2007 gestione protezione password
      // viene inserita la gestione della sicurezza delle password:
      // ogni cambio di password viene memorizzato in una tabella apposita in
      // modo da
      // potere controllare se la password è già stata utilizzata in passato.
      // se la coppia login/password nuova non è stata utilizzata in passato
      // allora inserisco il record nella storia
      // e modifico la password

      // se l'utente è abilitato alla sicurezza password andiamo a gestire la
      // storia delle password
      // effettuando la insert sulla STOUTESYS
      OpzioniUtente opzioniUtente = new OpzioniUtente(
          profilo.getFunzioniUtenteAbilitate());

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
      
      if (opzioniSicurezzaPassword.test(opzioniUtente)) {
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
        if(UtilityPassword.passwordSimilarity(chPswForm.getNuovaPassword(),profilo.getNome())){
          messageKey = "errors.gestoreException.*.gestoreFormRegistrazione.passwordSimilarityNomeUtente";
          this.aggiungiMessaggio(request, messageKey);
          target = "errataVecchiaPassword";
        }

        if(UtilityPassword.passwordSimilarity(chPswForm.getNuovaPassword(),profilo.getCodiceFiscale())){
          messageKey = "errors.gestoreException.*.gestoreFormRegistrazione.passwordSimilarityCFUtente";
          this.aggiungiMessaggio(request, messageKey);
          target = "errataVecchiaPassword";
        }
        
        if(target != null){return target;}
        logger.debug("errori non trovati" );
        this.accountManager.updatePasswordInsertStorico(profilo.getId(),
            profilo.getLogin(), chPswForm.getVecchiaPassword(),chPswForm.getNuovaPassword());
      } else {
      // aggiornamento della password dell'account
        this.accountManager.updatePassword(profilo.getId(),
          chPswForm.getVecchiaPassword(), chPswForm.getNuovaPassword());
      }

      messageKey = "info.chgPsw.ok";
      if (logger.isInfoEnabled())
        logger.info(this.resBundleGenerale.getString(messageKey));

      request.setAttribute(CostantiGenerali.ATTR_MESSAGGIO_ESITO_OPERAZIONE,
          this.resBundleGenerale.getString(messageKey));

      // il target viene gestito in maniera da riportare alla pagina da cui si è
      // richiamata la funzione
      // target = CostantiGeneraliStruts.FORWARD_OK;
    } catch (DataIntegrityViolationException e) {
      if(chPswForm.getVecchiaPassword() != null && chPswForm.getVecchiaPassword().length() > 0)
        request.setAttribute("vecchiaPasswordIsNull", Boolean.FALSE);
      else
        request.setAttribute("vecchiaPasswordIsNull", Boolean.TRUE);
        
      // la password scelta è già stata utilizzata in passato
      chPswForm = new CambiaPasswordForm();
      request.setAttribute("cambiaPasswordForm", chPswForm);
      if ("modificaPasswordScaduta".equalsIgnoreCase(metodo)){
        request.setAttribute("passwordScaduta","scaduta");
        request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
            CostantiGenerali.DISABILITA_NAVIGAZIONE);
      }
      target = "errataVecchiaPassword";
      messageKey = "errors.chgPsw.passwordGiaUtilizzata";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (CriptazioneException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = e.getChiaveResourceBundle();
      // logger.error(this.resBundleUtils.getString(messageKey), e);
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);

    } catch (IncorrectUpdateSemanticsDataAccessException e) {
      if(chPswForm.getVecchiaPassword() != null && chPswForm.getVecchiaPassword().length() > 0)
        request.setAttribute("vecchiaPasswordIsNull", Boolean.FALSE);
      else
        request.setAttribute("vecchiaPasswordIsNull", Boolean.TRUE);

      // eccezione legata alla verifica che si esegua esattamente un'update:
      // siccome il filtro nell'update è anche per la vecchia password, allora
      // vuol dire che la vecchia password è errata
      chPswForm = new CambiaPasswordForm();
      request.setAttribute("cambiaPasswordForm", chPswForm);
      if ("modificaPasswordScaduta".equalsIgnoreCase(metodo)){
        request.setAttribute("passwordScaduta","scaduta");
        request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
            CostantiGenerali.DISABILITA_NAVIGAZIONE);
      }
      target = "errataVecchiaPassword";
      messageKey = "errors.chgPsw.errataVecchiaPassword";
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
      logger.debug("salvaPassword: fine metodo");
    }
    return target;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action annulla
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniAnnulla() {
    // chiunque può fare annulla
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