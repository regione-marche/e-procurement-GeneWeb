/*
 * Created on 20 - Feb - 2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.utils.MailUtils;
import it.eldasoft.utils.mail.IMailSender;
import it.eldasoft.utils.mail.MailSenderException;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
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

/**
 * Action di gestione dell'account: crea un nuovo Account (form), carica il form
 * dell'Account, elimina un Account ed elimina gli Account selezionati
 *
 * @author cit_defilippis
 */
public class AccountAction extends AbstractDispatchActionBaseAdmin {

  static Logger               logger                       = Logger.getLogger(AccountAction.class);

  private static final String FORWARD_SUCCESS_MODIFICA     = "successModifica";
  private static final String FORWARD_SUCCESS_SOLA_LETTURA = "successDettaglio";
  private static final String FORWARD_SUCCESS_ATTIVA       = "successAttiva";
  private static final String FORWARD_SUCCESS_DISATTIVA    = "successDisattiva";

  protected AccountManager    accountManager;

  protected TabellatiManager tabellatiManager;

  private MailManager         mailManager;

  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }

  /**
   * @return Ritorna tabellatiManager.
   */
  public TabellatiManager getTabellatiManager() {
    return tabellatiManager;
  }

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @param mailManager
   *        mailManager da settare internamente alla classe.
   */
  public void setMailManager(MailManager mailManager) {
    this.mailManager = mailManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action editLista
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniVisualizza() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_LETTURA_ADMIN);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward visualizza(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("visualizza: inizio metodo");
    String target = null;

    Integer id = ((Integer) request.getAttribute("idAccount"));
    if (id == null) id = new Integer(request.getParameter("idAccount"));

    String metodo = (String) request.getAttribute("metodo");
    if (metodo == null) metodo = request.getParameter("metodo");

    String messageKey = null;

    try {
      Account account = this.accountManager.getAccountById(id);
      AccountForm formUtente = new AccountForm(account);
      List<Tabellato> listaUffAppartenenza = this.tabellatiManager.getTabellato(TabellatiManager.UFF_APPARTENENZA);
      List<Tabellato> listaRuoliME = this.tabellatiManager.getTabellato(TabellatiManager.RUOLO_ME);
      List<Tabellato> listaCategorie = this.tabellatiManager.getTabellato(TabellatiManager.CATEGORIE_UTENTE);

      target = FORWARD_SUCCESS_SOLA_LETTURA;
      this.setMenuTab(request);
      HttpSession sessione = request.getSession();
      sessione.setAttribute(CostantiGenerali.ID_OGGETTO_SESSION, id);
      sessione.setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
          account.getNome());
      request.setAttribute("listaUffAppartenenza", listaUffAppartenenza);
      request.setAttribute("listaRuoliME", listaRuoliME);
      request.setAttribute("listaCategorie", listaCategorie);
      request.setAttribute("idAccount", new Integer(account.getIdAccount()));
      request.setAttribute("accountForm", formUtente);
      OpzioniUtente opzioniUtente = formUtente.getOpzioniUtente();
      if(opzioniUtente == null){
        request.setAttribute("nascondiUffint",false);
      }else{
        request.setAttribute("nascondiUffint", opzioniUtente.isOpzionePresente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA));
      }
      request.setAttribute("metodo", metodo);

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
    if (logger.isDebugEnabled()) logger.debug("visualizza: fine metodo");
    return mapping.findForward(target);
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
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward modifica(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("modifica: inizio metodo");
    String target = null;
    String messageKey = null;
    String id = request.getParameter("idAccount");
    String metodo = request.getParameter("metodo");
    // String modo = (String) request.getAttribute("modo");
    Integer i = Integer.valueOf(id);
    try {
      Account account = this.accountManager.getAccountById(i);
      AccountForm formUtente = new AccountForm(account);
      List<Tabellato> listaUffAppartenenza = this.tabellatiManager.getTabellato(TabellatiManager.UFF_APPARTENENZA);
      List<Tabellato> listaRuoliME = this.tabellatiManager.getTabellato(TabellatiManager.RUOLO_ME);
      List<Tabellato> listaCategorie = this.tabellatiManager.getTabellato(TabellatiManager.CATEGORIE_UTENTE);

      // Le opzioni utente sono caricate in un String[], tuttavia alla voce
      // "Utenti applicativo" la condizione relativa a "Solo lettura" prevede
      // il valore che è pari a "ou11|ou12". Pertanto se nello String[] delle
      // opzioni utente sono presenti sia "ou11" che "ou12", li rimuovo
      // inserisco una nuova opzione che è la loro concatenazione separati
      // da "|"
      if (formUtente.getOpzioniUtenteSys() != null) {
        Collection<String> opzioniUtente = Arrays.asList(formUtente.getOpzioniUtenteSys());

        if (opzioniUtente.contains("ou11") && opzioniUtente.contains("ou12")) {
          int j = 0;
          while (j < formUtente.getOpzioniUtenteSys().length) {
            if (formUtente.getOpzioniUtenteSys()[j].equals("ou11"))
              formUtente.getOpzioniUtenteSys()[j] = "ou11|ou12";

            if (formUtente.getOpzioniUtenteSys()[j].equals("ou12"))
              formUtente.getOpzioniUtenteSys()[j] = null;
            j++;
          }
        }
      }


      request.setAttribute("listaUffAppartenenza", listaUffAppartenenza);
      request.setAttribute("listaRuoliME", listaRuoliME);
      request.setAttribute("listaCategorie", listaCategorie);

      // Obbligatorieta' dell'ufficio di appartenenza
      String temp = ConfigManager.getValore("it.eldasoft.dettaglioAccount.ufficioAppartenenza.obbligatorio");
      if (StringUtils.isNotEmpty(temp)) {
        if ("1".equals(StringUtils.trim(temp))) {
          request.setAttribute("uffAppObbligatorio", temp);
        }
      }

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      // update del menu a tab
      this.setMenuTabEdit(request);

      HttpSession sessione = request.getSession();
      sessione.setAttribute(CostantiGenerali.ID_OGGETTO_SESSION, i);
      sessione.setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
          account.getNome());
      request.setAttribute("idAccount", new Integer(account.getIdAccount()));
      request.setAttribute("accountForm", formUtente);
      request.setAttribute("nascondiUffint", formUtente.getOpzioniUtente().isOpzionePresente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA));
      request.setAttribute("metodo", metodo);
      target = FORWARD_SUCCESS_MODIFICA;

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
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action nuovo
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniNuovo() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward nuovo(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("nuovo: inizio metodo");

    String target = FORWARD_SUCCESS_MODIFICA;
    String messageKey = null;
    HttpSession sessione = request.getSession();

    try {
      AccountForm formUtente = new AccountForm();
      List<Tabellato> listaUffAppartenenza = this.tabellatiManager.getTabellato(TabellatiManager.UFF_APPARTENENZA);
      List<Tabellato> listaRuoliME = this.tabellatiManager.getTabellato(TabellatiManager.RUOLO_ME);
      List<Tabellato> listaCategorie = this.tabellatiManager.getTabellato(TabellatiManager.CATEGORIE_UTENTE);

      formUtente.setAbilitazioneLavori(CostantiGeneraliAccount.DEFAULT_ABILITAZIONE_LAVORI);
      formUtente.setLivelloLavori(CostantiGeneraliAccount.DEFAULT_LIVELLO_LAVORI);
      formUtente.setAbilitazioneContratti(CostantiGeneraliAccount.DEFAULT_ABILITAZIONE_CONTRATTI);
      formUtente.setLivelloContratti(CostantiGeneraliAccount.DEFAULT_LIVELLO_CONTRATTI);
      formUtente.setAbilitazioneGare(CostantiGeneraliAccount.DEFAULT_ABILITAZIONE_GARE);
      formUtente.setLivelloGare(CostantiGeneraliAccount.DEFAULT_LIVELLO_GARE);
      formUtente.setAbilitazioneAP(CostantiGeneraliAccount.DEFAULT_ABILITAZIONE_AP);

      formUtente.setOpzioniUtente(new OpzioniUtente(new String[]{CostantiGeneraliAccount.OPZIONI_MENU_STRUMENTI,CostantiGeneraliAccount.OPZIONI_SICUREZZA_PASSWORD, CostantiGeneraliAccount.OPZIONI_BLOCCO_MODIFICA_UFFINT}));
      formUtente.setOpzioniUtenteSys(formUtente.getOpzioniUtente().getElencoOpzioni());

      sessione.setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION, "");
      request.setAttribute("listaUffAppartenenza", listaUffAppartenenza);
      request.setAttribute("listaRuoliME", listaRuoliME);
      request.setAttribute("listaCategorie", listaCategorie);

   // Obbligatorieta' dell'ufficio di appartenenza
      String temp = ConfigManager.getValore("it.eldasoft.dettaglioAccount.ufficioAppartenenza.obbligatorio");
      if (StringUtils.isNotEmpty(temp)) {
        if ("1".equals(StringUtils.trim(temp))) {
          request.setAttribute("uffAppObbligatorio", temp);
        }
      }

      request.setAttribute("accountForm", formUtente);
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);
      this.setMenuTabEdit(request);
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
    if (logger.isDebugEnabled()) logger.debug("nuovo: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab
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
          CostantiDettaglioAccount.GRUPPI, CostantiDettaglioAccount.UFFICI_INTESTATARI,
          CostantiDettaglioAccount.TECNICI});
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioAccount.DETTAGLIO);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiDettaglioAccount.DETTAGLIO, CostantiDettaglioAccount.PROFILI,
          CostantiDettaglioAccount.GRUPPI, CostantiDettaglioAccount.UFFICI_INTESTATARI,
          CostantiDettaglioAccount.TECNICI });
      sessione.setAttribute(CostantiDettaglioAccount.NOME_GESTORE_TAB,
          gestoreTab);
    }
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action abilita
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniAbilita() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  public ActionForward abilita(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("abilita: inizio metodo");
    String target = FORWARD_SUCCESS_ATTIVA;

    this.abilitaDisabilita(request, target, CostantiDettaglioAccount.ABILITATO);

    if (logger.isDebugEnabled()) logger.debug("abilita: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action disabilita
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniDisabilita() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  public ActionForward disabilita(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("disabilita: inizio metodo");
    String target = FORWARD_SUCCESS_DISATTIVA;

    this.abilitaDisabilita(request, target,
        CostantiDettaglioAccount.DISABILITATO);

    if (logger.isDebugEnabled()) logger.debug("disabilita: fine metodo");
    return mapping.findForward(target);
  }

  private String abilitaDisabilita(HttpServletRequest request, String target,
      String flag) {
    String messageKey = null;
    String id = request.getParameter("idAccount");
    int i = Integer.valueOf(id).intValue();
    Account account = null;
    try {
      this.accountManager.updateAbilitazioneUtente(i, flag);
      account = this.accountManager.getAccountById(new Integer(i));

      if (account.getFlagLdap() != 1) {
        // decripto la password per inviarla nell'email
        ICriptazioneByte decriptatore = FactoryCriptazioneByte.getInstance(
            ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
            account.getPassword().getBytes(),
            ICriptazioneByte.FORMATO_DATO_CIFRATO);
        account.setPassword(new String(decriptatore.getDatoNonCifrato()));
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

    String email = account.getEmail();
    // l'email deve essere inviata se esiste uan email dell'utente e se è
    // settata la property di invio in abilitazione
    if (email != null && !"".equals(email))
      this.inviaMail(account, flag, request);
    else if (email == null || "".equals(email)) {
      messageKey = "warnings.abilitazione.mancaEmail";
      logger.warn(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    }
    return target;
  }

  private void inviaMail(Account account, String flagAbilitazione,
      HttpServletRequest request) {

    String nomeMittente = ConfigManager.getValore(CostantiGenerali.PROP_TITOLO_APPLICATIVO);

    boolean creazioneMailSender = false;
    boolean invioMailUtente = false;

    try {
      IMailSender mailSender = MailUtils.getInstance(
          this.mailManager,
          (String) request.getSession().getAttribute(
              CostantiGenerali.MODULO_ATTIVO),CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD);

      creazioneMailSender = true;
      String intestazione = account.getNome();
      String login = account.getLogin();
      String password = account.getPassword();
      String testoMail = null;
      String oggettoMail = null;

      if (!flagAbilitazione.equals(CostantiDettaglioAccount.DISABILITATO)) {
        oggettoMail = resBundleGenerale.getString(CostantiGenerali.RESOURCE_OGGETTO_MAIL_ABILITAZIONE_UTENTE);
        oggettoMail = UtilityStringhe.replaceParametriMessageBundle(
            oggettoMail, new String[] { nomeMittente });

        testoMail = resBundleGenerale.getString(CostantiGenerali.RESOURCE_TESTO_MAIL_ABILITAZIONE_UTENTE);
        testoMail = UtilityStringhe.replaceParametriMessageBundle(testoMail,
            new String[] { intestazione, nomeMittente, login, password,
                nomeMittente });

      } else {
        oggettoMail = resBundleGenerale.getString(CostantiGenerali.RESOURCE_OGGETTO_MAIL_DISABILITAZIONE_UTENTE);
        oggettoMail = UtilityStringhe.replaceParametriMessageBundle(
            oggettoMail, new String[] { nomeMittente });

        testoMail = resBundleGenerale.getString(CostantiGenerali.RESOURCE_TESTO_MAIL_DISABILITAZIONE_UTENTE);
        testoMail = UtilityStringhe.replaceParametriMessageBundle(testoMail,
            new String[] { intestazione, nomeMittente, nomeMittente });
      }

      mailSender.send(account.getEmail(), oggettoMail, testoMail);
      invioMailUtente = true;
      // mailSender.send(mailAmministratore, oggettoMail, testoMail);

    } catch (MailSenderException ms) {

      String logMessageKey = ms.getChiaveResourceBundle();
      String logMessageError = resBundleGenerale.getString(logMessageKey);
      for (int i = 0; ms.getParametri() != null && i < ms.getParametri().length; i++)
        logMessageError = logMessageError.replaceAll(
            UtilityStringhe.getPatternParametroMessageBundle(i),
            (String) ms.getParametri()[i]);
      logger.error(logMessageError, ms);

      if (!creazioneMailSender) {
        this.aggiungiMessaggio(request, "errors.applicazione.inaspettataException");
      } else {
        if (!invioMailUtente) {
          logMessageKey = "warnings.registrazione.mancatoInvioMailUtente";
          logMessageError = resBundleGenerale.getString(logMessageKey);
          this.aggiungiMessaggio(request, logMessageKey);
        }
        // } else {
        // logMessageKey = "warnings.registrazione.mancatoInvioMailAdmin";
        // logMessageError = resBundleGenerale.getString(logMessageKey);
        // }
        logger.warn(logMessageError, ms);
      }
    }
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di editing della lista utenti di gruppo
   *
   * @param request
   */
  private void setMenuTabEdit(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioAccount.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiDettaglioAccount.DETTAGLIO);
      gestoreTab.setTabSelezionabili(null);
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiDettaglioAccount.DETTAGLIO);
      gestoreTab.setTabSelezionabili(null);
      sessione.setAttribute(CostantiDettaglioAccount.NOME_GESTORE_TAB,
          gestoreTab);
    }
  }

}