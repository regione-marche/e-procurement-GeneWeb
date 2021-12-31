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

import it.eldasoft.gene.bl.MessageManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.admin.ProfiliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.domain.admin.AccountCodFiscDuplicati;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.GestoreEccezioni;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Action per insert e modifica di un account
 *
 * @author cit_defilippis
 */
public class SalvaAccountAction extends AbstractDispatchActionBaseAdmin {

  static Logger               logger                 = Logger.getLogger(SalvaAccountAction.class);

  private static final String FORWARD_INSERIMENTO_OK = "inserimentoOK";
  private static final String FORWARD_MODIFICA_OK    = "modificaOK";
  // non utilizzo più il forward ok per la password perchè lo faccio tornare
  // automaticamente alla pagina da dove proviene
  // private static final String FORWARD_PASSWORD_OK = "passwordOK";
  private static final String FORWARD_LOGIN_KO       = "loginKO";

  private AccountManager      accountManager;

  private ProfiliManager      profiliManager;

  private TabellatiManager    tabellatiManager;
  
  private MessageManager      messageManager;

  
  public void setMessageManager(MessageManager messageManager) {
    this.messageManager = messageManager;
  }
  
  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }

  /**
   * @param profiliManager profiliManager da settare internamente alla classe.
   */
  public void setProfiliManager(ProfiliManager profiliManager) {
    this.profiliManager = profiliManager;
  }

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }


  /**
   * Funzione che restituisce le opzioni per accedere alla action inserisci
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniInserisci() {
    // ou11 e non ou12
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  public ActionForward inserisci(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String target = SalvaAccountAction.FORWARD_INSERIMENTO_OK;
    String messageKey = null;

    AccountForm utenteProdottoForm = (AccountForm) form;
    boolean amministratoreForm = false;
    boolean credenzialiDisponibili = true;
    Collection<String> list = Arrays.asList(utenteProdottoForm.getOpzioniUtenteSys());
    if (list.contains("ou89")) {amministratoreForm = true;}
    if(amministratoreForm) credenzialiDisponibili = this.accountManager.getCredenziaDisponibili(utenteProdottoForm.getLogin());
    
    try {
      if (!(this.accountManager.isUsedLogin(utenteProdottoForm.getLogin(), -1) || ("1".equals(utenteProdottoForm.getFlagLdap()) && this.accountManager.isUsedDn(
          utenteProdottoForm.getDn(), -1))) && (credenzialiDisponibili)) {
        Account accountUtente = new Account();
        accountUtente = utenteProdottoForm.getDatiPerModel(accountUtente,
            (String) request.getSession().getAttribute(
                CostantiGenerali.MODULO_ATTIVO));
        this.insertAccount(accountUtente, request);

        // preparo il form per la visualizzazione ricaricando dal db il nuovo
        // account in modo che venga decriptata la login
        Account account = this.accountManager.getAccountById(new Integer(
            accountUtente.getIdAccount()));
        request.setAttribute("idAccount", new Integer(account.getIdAccount()));
        
        
        
        // controllo sul codice fiscale duplicato
        if (StringUtils.isNotBlank(account.getCodfisc())) {
          List<AccountCodFiscDuplicati> ListaUtentiUgualeCodfisc = this.accountManager.getListaUtentiUgualeCodfisc(account.getIdAccount(), utenteProdottoForm.getCodfisc());

          // set nel request della lista di tutti gli utenti col medesimo codice fiscale dell'utente corrente
          request.setAttribute("ListaUtentiUgualeCodfisc", ListaUtentiUgualeCodfisc);
        }

        // setto il modo così riesco a stabilire in fase di creazione della
        // pagina se siamo in inserimento
        // e di conseguenza non inserisco nell'history la pagina di dettaglio
        HttpSession sessione = request.getSession();
        // Set in sessione di id e nome dell'account che si sta modificando
        sessione.setAttribute(CostantiGenerali.ID_OGGETTO_SESSION, new Integer(
            account.getIdAccount()));
        sessione.setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
            account.getNome());
        this.setMenuTab(request);
        
        ProfiloUtente profiloUtente = (ProfiloUtente) sessione.getAttribute("profiloUtente");
        int id = new Integer(profiloUtente.getId());
        Account currentUser = this.accountManager.getAccountById(id);
        
        //inserisco il log di inserimento nuovo utente nel database
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setLivEvento(LogEvento.LIVELLO_INFO);
        logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_ADD_USER);
        logEvento.setDescr("L'utente " + id + " ha aggiunto nuovo utente con id ="+ account.getIdAccount() + " e login = " + account.getLogin());
        logEvento.setErrmsg("");
        LogEventiUtils.insertLogEventi(logEvento);
        if (amministratoreForm){
          this.messageManager.insertMessageToUsersAdministrator("L'amministratore "+ currentUser.getLogin() +" (id = " + id + ") ha inserito l'utenza amministrativa "+ account.getLogin() +" (id = " + account.getIdAccount() + ")","", id);
        }
      } else {
        if (this.accountManager.isUsedLogin(utenteProdottoForm.getLogin(), -1))
          messageKey = "errors.login.loginDuplicato";
        else{
          if(!credenzialiDisponibili){
            messageKey = "errors.login.credenzialiGiàUtilizzateDiRecente";
          }else{
          messageKey = "errors.login.dnDuplicato";}
        }
        target = SalvaAccountAction.FORWARD_LOGIN_KO;

        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
        request.setAttribute("accountForm", utenteProdottoForm);
        request.setAttribute("metodo", "inserisci");
        List<Tabellato> listaUffAppartenenza = this.tabellatiManager.getTabellato(TabellatiManager.UFF_APPARTENENZA);
        List<Tabellato> listaRuoliME = this.tabellatiManager.getTabellato(TabellatiManager.RUOLO_ME);
        List<Tabellato> listaCategorie = this.tabellatiManager.getTabellato(TabellatiManager.CATEGORIE_UTENTE);
        request.setAttribute("listaUffAppartenenza", listaUffAppartenenza);
        request.setAttribute("listaRuoliME", listaRuoliME);
        request.setAttribute("listaCategorie", listaCategorie);
      }

    } catch (DataIntegrityViolationException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.inserimento.chiaveDuplicata";
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

    return mapping.findForward(target);
  }

  /**
   * Metodo per effettuare l'insert di un documento associato, gestendo il caso
   * di inserimento contemporaneo di due documenti: in caso di eccezione di tipo
   * DataIntegrityViolationException si ripete l'operazione di insert, fino ad
   * un numero massimo di tentativi. Raggiunto il numero massimo di tentativi il
   * metodo esce con un eccezione
   */
  private void insertAccount(Account account, HttpServletRequest request) throws CriptazioneException {
    boolean inserito = false;
    int numeroTentativi = 0;

    boolean gruppiDis = false;
    String gruppiDisabilitati = ConfigManager.getValore(CostantiGenerali.PROP_GRUPPI_DISABILITATI);
    if ("1".equals(gruppiDisabilitati)) gruppiDis = true;
    String codiceApplicazione = (String) request.getSession().getAttribute(
        CostantiGenerali.MODULO_ATTIVO);
    String profiloUtenteDefault = ConfigManager.getValore(CostantiGenerali.PROP_PROFILO_DEFAULT_INSERIMENTO);

    // tento di inserire il record finchè non genero un ID univoco a causa
    // della concorrenza, o raggiungo il massimo numero di tentativi
    while (!inserito
        && numeroTentativi < CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
      try {
        this.accountManager.insertAccount(account);
        inserito = true;
      } catch (DataIntegrityViolationException div) {
        if (numeroTentativi < CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
          logger.error(
              "Fallito tentativo "
                  + (numeroTentativi + 1)
                  + " di inserimento record per chiave duplicata, si ritenta nuovamente",
              div);
          numeroTentativi++;
        }
      }
    }
    if (!inserito
        && numeroTentativi >= CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
      throw new DataIntegrityViolationException(
          "Raggiunto limite massimo di tentativi");
    }

    // una volta inserito l'utente si verifica se si necessita dell'inserimento
    // del profilo di default
    if (profiloUtenteDefault != null && profiloUtenteDefault.trim().length()>0) {
      // inserisco l'associazione con il profilo di default che gestisce
      // automaticamente l'associazione al gruppi se abilitata
      this.profiliManager.updateAssociazioneProfiliAccount(
          account.getIdAccount(), new String[] { profiloUtenteDefault },
          codiceApplicazione, gruppiDis);
    }
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action modifica
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModifica() {
    // ou11 e non ou12
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  public ActionForward modifica(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("modifica: inizio metodo");
    String target = SalvaAccountAction.FORWARD_MODIFICA_OK;

    String messageKey = null;

    AccountForm accountForm = (AccountForm) form;
    boolean amministratoreForm = false;
    boolean credenzialiDisponibili = true;
    Collection<String> list = Arrays.asList(accountForm.getOpzioniUtenteSys());
    if (list.contains("ou89")) {
      amministratoreForm = true;
    }
    if(amministratoreForm) credenzialiDisponibili = this.accountManager.getCredenziaDisponibili(accountForm.getLogin());
    
    try {
      Integer id = Integer.valueOf(accountForm.getIdAccount());
      if (!this.accountManager.isUsedLogin(accountForm.getLogin(),
          id.intValue()) && (credenzialiDisponibili)) {
        Account accountDb = this.accountManager.getAccountById(id);
        OpzioniUtente opzioniUtente = new OpzioniUtente(accountDb.getOpzioniUtente());
        CheckOpzioniUtente checkAmministratore = new CheckOpzioniUtente(
            CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);

        // crea opzioniUtenteForm,   
        HttpSession sessione = request.getSession();
        ProfiloUtente profiloUtente = (ProfiloUtente) sessione.getAttribute("profiloUtente");
        int idCurrentUser = new Integer(profiloUtente.getId());
        Account currentUser = this.accountManager.getAccountById(idCurrentUser);
        
        if (!checkAmministratore.test(opzioniUtente) && amministratoreForm ){
          this.messageManager.insertMessageToUsersAdministrator("L'amministratore " + currentUser.getLogin() + " (id = " + idCurrentUser + ") ha conferito i diritti di amministratore all'utenza " + accountForm.getLogin() + " (id = " + id + ")","", idCurrentUser);
          // tracciatura evento, metti anche nel log una tracciatura info
        }
        else{
          if (checkAmministratore.test(opzioniUtente) && !amministratoreForm ){
            this.messageManager.insertMessageToUsersAdministrator("L'amministratore "+ currentUser.getLogin() +" (id = "+ idCurrentUser +") ha rimosso i diritti di amministratore all'utenza "+ accountForm.getLogin() +" (id = " + id + ")","", idCurrentUser);
          }
        }

        this.accountManager.updateAccount(accountForm.getDatiPerModel(
            accountDb, (String) request.getSession().getAttribute(
                CostantiGenerali.MODULO_ATTIVO)));
        Account account = this.accountManager.getAccountById(id);
        AccountForm formUtente = new AccountForm(account);
        request.setAttribute("modo", "visualizza");
        request.setAttribute("accountForm", formUtente);
        request.setAttribute("metodo", "carica");
        
        if(amministratoreForm){
          this.messageManager.insertMessageToUsersAdministrator("L'amministratore "+ currentUser.getLogin() +" (id = "+ idCurrentUser +") ha aggiornato l'utenza amministrativa  "+ accountForm.getLogin() +" (" + id + ") con opzioni utente = " + list.toString() + ", sysab3 = "+ account.getAbilitazioneStd() +", sysabg = "+ account.getAbilitazioneGare(),"", idCurrentUser);
        }
        
        // controllo sul codice fiscale duplicato
        if (StringUtils.isNotBlank(account.getCodfisc())) {
          List<AccountCodFiscDuplicati> ListaUtentiUgualeCodfisc = this.accountManager.getListaUtentiUgualeCodfisc(account.getIdAccount(), accountForm.getCodfisc());

          // set nel request della lista di tutti gli utenti col medesimo codice fiscale dell'utente corrente
          request.setAttribute("ListaUtentiUgualeCodfisc", ListaUtentiUgualeCodfisc);
        }

        // Set in sessione di id e nome dell'account che si sta modificando
        request.getSession().setAttribute(CostantiGenerali.ID_OGGETTO_SESSION,
            new Integer(formUtente.getIdAccount()));
        request.getSession().setAttribute(
            CostantiGenerali.NOME_OGGETTO_SESSION, formUtente.getNome());

        this.setMenuTab(request);
      } else {
        target = SalvaAccountAction.FORWARD_LOGIN_KO;
        if(credenzialiDisponibili){
        messageKey = "errors.login.loginDuplicato";}
        else{messageKey = "errors.login.credenzialiGiàUtilizzateDiRecente"; }
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
        request.setAttribute("idAccount", accountForm.getIdAccount());
        request.setAttribute("accountForm", accountForm);
        request.setAttribute("metodo", "carica");
        request.setAttribute("modo", "visualizza");
        List<Tabellato> listaUffAppartenenza = this.tabellatiManager.getTabellato(TabellatiManager.UFF_APPARTENENZA);
        List<Tabellato> listaRuoliME = this.tabellatiManager.getTabellato(TabellatiManager.RUOLO_ME);
        List<Tabellato> listaCategorie = this.tabellatiManager.getTabellato(TabellatiManager.CATEGORIE_UTENTE);
        request.setAttribute("listaUffAppartenenza", listaUffAppartenenza);
        request.setAttribute("listaRuoliME", listaRuoliME);
        request.setAttribute("listaCategorie", listaCategorie);
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

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action annulla
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniAnnulla() {
    // basta l'ou11 così sia in visualizzazione che in modifica è possibile
    // utilizzarlo
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

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menu a tab
   *
   * @param request
   */
  protected void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioAccount.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiDettaglioAccount.DETTAGLIO);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioAccount.DETTAGLIO, CostantiDettaglioAccount.PROFILI,
          CostantiDettaglioAccount.GRUPPI });
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioAccount.DETTAGLIO);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioAccount.DETTAGLIO, CostantiDettaglioAccount.PROFILI,
          CostantiDettaglioAccount.GRUPPI });
      sessione.setAttribute(CostantiDettaglioAccount.NOME_GESTORE_TAB,
          gestoreTab);
    }
  }

}