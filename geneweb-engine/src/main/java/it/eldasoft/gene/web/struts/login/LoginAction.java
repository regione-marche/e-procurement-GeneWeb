/*
 * Created on 13-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.CommunicationException;

import it.eldasoft.gene.bl.LoginManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.system.LdapManager;
import it.eldasoft.gene.commons.web.LimitatoreConnessioniUtenti;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.CostantiIntegrazioneKronos;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.domain.admin.AccountLdap;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioAccount;
import it.eldasoft.gene.web.struts.login.cohesion.AccountCohesion;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Azione Struts che consente l'autenticazione dell'utente all'inserimento dei
 * dati nel form di login costituito dalla username dell'utente e dalla sua
 * password.<br>
 * Questa classe non estende la classe ActionBase in quanto non è legata ad una
 * funzionalità specifica dato che l'autenticazione deve avvenire sempre
 * all'interno di ogni applicazione web. Difatti, ne duplica alcune sezioni,
 * quali i resource bundle e i metodi per la scrittura di messaggi di errore nel
 * request.
 *
 * @author Stefano.Sabbadin
 */
public class LoginAction extends IsUserLoggedAction {

  /** Logger Log4J di classe */
  static Logger               logger                         = Logger.getLogger(LoginAction.class);

  private static final String FORWARD_CAMBIA_PASSWORD        = "successCambiaPassword";

  /**
   * Property di configurazione, contenente l'elenco dei nomi dei campi hidden
   * da caricare nei dati parametrici dell'utente, separati da ";"
   */
  private static final String PROP_PARAMETRI_ACCESSO_DIRETTO = "it.eldasoft.accessoDiretto.parametri";

  /** Reference alla classe di business logic per l'estrazione dell'account */
  protected LoginManager      loginManager;

  private AccountManager      accountManager;

  private LdapManager         ldapManager;

  /**
   * @param ldapManager
   *        The ldapManager to set.
   */
  public void setLdapManager(LdapManager ldapManager) {
    this.ldapManager = ldapManager;
  }

  /**
   * @param loginManager
   *        loginManager da settare internamente alla classe.
   */
  public void setLoginManager(LoginManager loginManager) {
    this.loginManager = loginManager;
  }

  /**
   * @return Ritorna LoginManager.
   */
  public LoginManager getLoginManager() {
    return this.loginManager;
  }
  /**
   * @param accountManager
   *        accountManager da settare internamente alla classe.
   */
  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }

  /**
   * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: inizio metodo");
    }
    

    /*    
    String adminLogged = (String) request.getAttribute("AdminLogged");
    if("Admin".equals(((LoginForm) form).getUsername()) && !"1".equals(adminLogged)){
      request.getSession().setAttribute("username",((LoginForm) form).getUsername());
      request.getSession().setAttribute("password",((LoginForm) form).getPassword());
      return mapping.findForward("successAdmin");
    }
    */
    
    // target di default, da modificare nel momento in cui si verificano dei
    // problemi o per redirezionare la navigazione in modo opportuno
    String target = CostantiGeneraliStruts.FORWARD_OK;

    target = this.testSkipProfili(request, target);

    boolean autenticazioneSSO = false;
    String propProtSSO = StringUtils.stripToNull(ConfigManager.getValore(CostantiGenerali.PROP_SSO_PROTOCOLLO));
    if("2".equals(propProtSSO)){
      // Cohesion: autenticazione SSO con Cohesion se ho memorizzato in sessione l'account cohesion
      autenticazioneSSO = request.getSession().getAttribute(AccountCohesion.ID_ATTRIBUTO_SESSIONE_ACCOUNT_COHESION) != null;
    }
    target = this.checkLogin(((LoginForm) form).getUsername(),
        ((LoginForm) form).getPassword(), request, autenticazioneSSO, target);

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: fine metodo");
    }

    if (CostantiGeneraliStruts.FORWARD_REGISTRAZIONE.equals(target)){
      return this.forwardToRegistrationForm(request);
    }
    return mapping.findForward(target);
  }

  /**
   * Verifica se l'applicativo non utilizza i profili: in tal caso viene
   * predisposto, in caso di successo, l'avanzamento dell'operazione di
   * autenticazione mediante check della versione dell'applicativo e non con il
   * check dei profili attribuiti all'utente
   *
   * @param request
   *        request HTTP
   * @param target
   *        target per il forward di struts
   * @return target modificato
   */
  protected String testSkipProfili(HttpServletRequest request, String target) {
    // {MF231007} Aggiunta dello skip sull'uso dei profili
    if ("1".equals(ConfigManager.getValore(CostantiGenerali.SKIP_USO_PROFILI))) {
      target = "successSkipProfili";
      // Setto anche come se fosse un unico profilo d'applicazione
      request.getSession().setAttribute(
          CostantiGenerali.SENTINELLA_UNICO_CODICE_PROFILO, "1");
      request.setAttribute("codApp",
          ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE));
    } else {
      request.getSession().removeAttribute(
          CostantiGenerali.SENTINELLA_UNICO_CODICE_PROFILO);
    }
    return target;
  }

  /**
   * Estra l'account in base alla credenziali fornite, esegue una serie di controlli, quindi popola in sessione il profilo dell'utente
   *
   * @param username
   *        username ricevuto
   * @param password
   *        password eventuale ricevuta
   * @param request
   *        request HTTP
   * @param autenticazioneSSO
   *        true se l'autenticazione avviene mediante single sign on, false altrimenti
   * @param target
   *        target di struts di destinazione
   * @return target eventualmente modificato
   */
  @SuppressWarnings("unchecked")
  protected String checkLogin(String username, String password,
      HttpServletRequest request, boolean autenticazioneSSO, String target) {
    String messageKey = null;
    String targetChiamato = null;
    // utile per deallocare il lock su una connessione all'applicativo nel
    // momento in cui non si riesce ad eseguire con successo l'operazione di
    // login
    boolean liberaLock = false;

    int livEvento = 1;
    String errMsgEvento = "";


    if (username == null && password == null) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.login.mancanoCredenziali";
      logger.error(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    } else {

      HttpSession session = request.getSession();
      if (!LimitatoreConnessioniUtenti.getInstance().allocaConnessione(
          session.getId())) {
        // se l'applicativo ha raggiunto il numero massimo di utenti connessi
        // allora non consento la login
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.client.connessioniEsaurite";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);

      } else {

        boolean tracciaEventoLogin = true;

        // SS 06/11/2006: la password può anche essere null, ma arriva stringa
        // vuota e va corretta
        if ("".equals(password)) password = null;

        // CriptazioneByte criptatore = null;
        // DatoBase64 pswB64 = null;
        Account account = null;
        ProfiloUtente utente = null;

        try {
          // JIRA GENEWEB-10: introduzione dei controlli per il GDPR
          if (this.isBloccoLogin(request, username)) {
            target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
            tracciaEventoLogin = false;
            liberaLock = true;
          } else {

            // F.D. 02/09/08 gestione login in lower per dati non case sensitive
            // estrazione dell'account
            account = this.getAccount(username, password);
            if (autenticazioneSSO) {
              // si esegue una forzatura del valore quando l'utenza viene autenticata esternamente e quindi agganciata.
              // in questo modo si riesce ad effettuare una dopia autenticazione sia dall'esterno che ad esempio LDAP
              account.setFlagLdap(3);
            }

            // CF 29/03/16 nel caso non si estragga alcun account, si verifica se l'applicativo viene configurato con LDAP ed e' disponibile
            // la form di registrazione, in tal caso si controlla se l'utente si autentica su LDAP e deve essere redirezionato alla form di
            // registrazione
            if(account == null){
              /// prima di tutto verifico se sono in regime di LDAP con form di registrazione attiva
              boolean isLdap = false;
              boolean isFormRegistrazioneAttiva = false;
              ServletContext context = request.getSession().getServletContext();
              Collection<String> opzioni = Arrays.asList((String[]) context.getAttribute(CostantiGenerali.OPZIONI_DISPONIBILI));
              if (opzioni.contains(CostantiGenerali.OPZIONE_ADMIN_LDAP)) {
                isLdap = true;
              }
              if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_ATTIVA_FORM_REGISTRAZIONE))) {
                isFormRegistrazioneAttiva = true;
              }

              if (isLdap && isFormRegistrazioneAttiva) {
                List<AccountLdap> listaAccountByUsername = null;
                String cn = "*" + username + "*";
                listaAccountByUsername  = ldapManager.getAccountLdap(cn);
                for (AccountLdap accountLdap : listaAccountByUsername) {
                  // per proseguire devo individuare univocamente l'unico account con tale login, ciclo perche' potrei avere utenti che
                  // contengono nella propria login l'intera login dell'utenza che tenta l'autenticazione
                  cn = accountLdap.getCn();
                  String dn = accountLdap.getDn();
                  String sn = accountLdap.getSn();
                  if (sn.equalsIgnoreCase(username)) {
                    // creo un oggetto account fittizio solo con le informazioni utili per verifyAccountLdap
                    Account accountStub = new Account();
                    accountStub.setLogin(sn);
                    accountStub.setPassword(password);
                    accountStub.setDn(dn);
                    targetChiamato = this.verifyAccountLdap(request, sn, password, accountStub,
                        targetChiamato);
                    if (targetChiamato == null) {
                      // l'utente si autentica su LDAP, pertanto si impostano
                      // i parametri per la form di registrazione
                      //request.setAttribute("nome", cn);
                      //request.setAttribute("username", sn);
                      request.setAttribute("flagLdap", "1");
                      request.setAttribute("dn", dn);
                      request.setAttribute("login", sn);
                      target = CostantiGeneraliStruts.FORWARD_REGISTRAZIONE;
                      tracciaEventoLogin = false; // si passa per la form di registrazione
                      liberaLock = true;
  //                  return target;
                    }
                    break; // trovato l'account esco dal ciclo
                  }
                }
              }
            }

            if (targetChiamato == null && !CostantiGeneraliStruts.FORWARD_REGISTRAZIONE.equals(target)) {
              // si eseguono i controlli sull'utenza se estratta da  USRSYS
              targetChiamato = this.verificaAccount(request, username,
                  password, account);
            }
            if (targetChiamato == null && !CostantiGeneraliStruts.FORWARD_REGISTRAZIONE.equals(target)) {

              // se i controlli sono andati a buon fine, allora si crea e si
              // memorizza
              // il profilo dell'utente in sessione
              utente = this.loginManager.getProfiloUtente(
                  account, ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE));
              utente.setAutenticazioneSSO(autenticazioneSSO);

              // SS 24/02/2010
              // si memorizza la password inserita in modo da consentire eventuali
              // aperture di altri applicativi alice senza riautenticarsi. La password
              // viene memorizzata cifrata, ed è la password inserita all'accesso,
              // in modo da gestire anche il caso LDAP, dato che nella USRSYS non
              // viene memorizzata
              if (password != null) {
                ICriptazioneByte criptatore = FactoryCriptazioneByte.getInstance(
                    ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
                    password.getBytes(),
                    ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
                utente.setPwd(new String(criptatore.getDatoCifrato()));
              }

              // SS 20090316: inserita la memorizzazione dei parametri aggiuntivi
              // passati nel request all'atto della login (viene usata solo con
              // AccessoDirettoAction)
              String[] nomiParametri = UtilityStringhe.deserializza(
                  UtilityStringhe.convertiStringaVuotaInNull(ConfigManager.getValore(LoginAction.PROP_PARAMETRI_ACCESSO_DIRETTO)),
                  ';');

              if (nomiParametri != null) {

                Map<String, String> hash = null;
                if (CostantiIntegrazioneKronos.INTEGRAZIONE_KRONOS.equals(ConfigManager.getValore(CostantiGenerali.PROP_INTEGRAZIONE))) {
                  // se l'accesso diretto e' dovuto all'integrazione con Kronos, i parametri si prendono dal DB
                  hash = this.loginManager.getDatiUtenteKronos(Integer.parseInt(request.getParameter("id")));
                } else {
                  // altrimenti i parametri si prendono direttamente dal request
                  hash = new HashMap<String, String>();
                  for (int i = 0; i < nomiParametri.length; i++) {
                    hash.put(nomiParametri[i], request.getParameter(nomiParametri[i]));
                  }
                }

                String parametroEsterno = null;
                for (int i = 0; i < nomiParametri.length; i++) {
                  parametroEsterno = hash.get(nomiParametri[i]);
                  // se il parametro è valorizzato, oppure non è obbligatorio, lo
                  // inserisco nei dati dell'utente
                  utente.getParametriUtente().put(nomiParametri[i],
                      parametroEsterno);
                }
              }

              session.setAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE, utente);
              session.setAttribute(CostantiGenerali.SENTINELLA_SESSION_TIMEOUT, "1");
              session.setAttribute(CostantiGenerali.PROFILI_KEYS, new HashMap<String, HashSet<String>>());
              session.setAttribute(CostantiGenerali.PROFILI_KEY_PARENTS, new HashMap<String, HashSet<String>>());

              if (logger.isInfoEnabled())
                logger.info(StringUtils.replace(
                    this.resBundleGenerale.getString("info.login.ok"), "{0}",
                    utente.getLogin()));

              // se l'utente ha abilitata la sicurezza password effettuo i
              // controlli se no effettuo login classico
              OpzioniUtente opzioniUtente = new OpzioniUtente(
                  utente.getFunzioniUtenteAbilitate());

              CheckOpzioniUtente opzioniSicurezzaPassword = new CheckOpzioniUtente(
                  CostantiGeneraliAccount.OPZIONI_SICUREZZA_PASSWORD);
              if (!utente.isAutenticazioneSSO() && opzioniSicurezzaPassword.test(opzioniUtente)) {
                try {
                  // leggo la data gli sommo i giorni di durata della password
                  // se superano la data odierna rimando alla pagina di cambio
                  // password
                  Date dataUltimoCambioPsw = this.accountManager.getDataUltimoCambioPsw(
                      account.getLogin(), account.getPassword());
                  GregorianCalendar data = new GregorianCalendar();
                  GregorianCalendar dataOdierna = new GregorianCalendar();
                  boolean continuaControlli = true;
                  if (dataUltimoCambioPsw != null) {
                    data.setTime(dataUltimoCambioPsw);
                    int giorniAnno = data.get(Calendar.DAY_OF_YEAR);
                    try {
                      int durata = Integer.parseInt(ConfigManager.getValore(CostantiGenerali.PROP_DURATA_PASSWORD));
                      data.set(Calendar.DAY_OF_YEAR, giorniAnno + durata);
                    } catch (Throwable t) {
                      continuaControlli = false;
                      // si emette un primo messaggio su log esplicativo del problema
                      messageKey = "errors.login.notValid.it.eldasoft.account.durataPassword";
                      logger.error(this.resBundleGenerale.getString(messageKey),
                          t);
                      // si emette un secondo messaggio per l'utente
                      messageKey = "errors.login.checkScadenzaPassword";
                      this.aggiungiMessaggio(request, messageKey);
                      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
                      livEvento = 3;
                      errMsgEvento = this.resBundleGenerale.getString(messageKey);
                    }
                  }
                  if (continuaControlli
                      && ((dataUltimoCambioPsw == null || (dataOdierna.getTimeInMillis() > data.getTimeInMillis())) || accountManager.getPasswordDisallineata(account) )) {
                    // se devo cambiare la password (password scaduta)rimando alla
                    // pagina di cambio
                    request.setAttribute("passwordScaduta", "scaduta");
                    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
                        CostantiGenerali.DISABILITA_NAVIGAZIONE);

                    // Se la vecchia password e' null, allora passo un parametro
                    // alla
                    // pagina per evitare di chiedere all'utente la vecchia
                    // password
                    // e non fare alcun controllo di obbligatorieta' di tale
                    // password
                    if (account.getPassword() != null
                        && account.getPassword().length() > 0)
                      request.setAttribute("vecchiaPasswordIsNull", Boolean.FALSE);
                    else
                      request.setAttribute("vecchiaPasswordIsNull", Boolean.TRUE);

                    target = FORWARD_CAMBIA_PASSWORD;
                    messageKey = "warnings.login.passwordScaduta";
                    logger.warn(this.resBundleGenerale.getString(messageKey));
                    this.aggiungiMessaggio(request, messageKey);
                    livEvento  = 2;
                    errMsgEvento="Login effettuato ma con cambio password necessario (utente " + username + ")";
                  }
                } catch (CriptazioneException e) {
                  target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
                  messageKey = e.getChiaveResourceBundle();
                  logger.error(this.resBundleGenerale.getString(messageKey), e);
                  this.aggiungiMessaggio(request, messageKey);
                  livEvento  = 3;
                  errMsgEvento = this.resBundleGenerale.getString(messageKey);
                }
              }
            } else {
              // se le verifiche non sono andate a buon fine allora elimino
              // l'oggetto dalla sessione per poter tornare alla pagina di login con
              // i campi svuotati
              if (!CostantiGeneraliStruts.FORWARD_REGISTRAZIONE.equals(target)) {
                // caso di autenticazione LDAP con successo per utente non presente in USRSYS e da redirezionare alla form di registrazione:
                // non devo considerarlo come errore ma faccio terminare con il target. in tutti gli altri casi traccio l'errore
                target = targetChiamato;
                request.setAttribute("loginForm", new LoginForm());
                liberaLock = true;
                livEvento  = 3;
                errMsgEvento = "Autenticazione fallita (utente " + username + ")";
              }
            }
          }

        } catch (CriptazioneException e) {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = e.getChiaveResourceBundle();
          logger.error(this.resBundleGenerale.getString(messageKey), e);
          this.aggiungiMessaggio(request, messageKey);
          liberaLock = true;
          livEvento  = 3;
          errMsgEvento = this.resBundleGenerale.getString(messageKey);

        } catch (DataAccessException e) {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.database.dataAccessException";
          logger.error(this.resBundleGenerale.getString(messageKey), e);
          this.aggiungiMessaggio(request, messageKey);
          liberaLock = true;
          livEvento  = 3;
          errMsgEvento = this.resBundleGenerale.getString(messageKey);

        } catch (AuthenticationException a) {
          target = CostantiGeneraliStruts.FORWARD_LOGIN;
          messageKey = "errors.ldap.autenticazioneFallita";
          String message = this.resBundleGenerale.getString(messageKey).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(0),
              username);
          logger.error(message, a);
          this.aggiungiMessaggio(request, messageKey, username);
          liberaLock = true;
          livEvento  = 3;
          errMsgEvento = message;
          if (account == null) {
            // nel caso di utenticazione LDAP fallita per utente inesistente, aggiungo l'utenza per cui e' stato tentato l'accesso
            errMsgEvento += " (utente " + username + ")";
          }
        } catch (CommunicationException c) {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.ldap.autenticazioneFallita";
          String message = this.resBundleGenerale.getString(messageKey).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(0),
              username);
          logger.error(message, c);
          this.aggiungiMessaggio(request, messageKey, username);
          liberaLock = true;
          livEvento  = 3;
          errMsgEvento = message;
        } catch (RuntimeException c) {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.login.loginDoppia";
          logger.error(this.resBundleGenerale.getString(messageKey), c);
          this.aggiungiMessaggio(request, messageKey);
          liberaLock = true;
          livEvento  = 3;
          errMsgEvento = this.resBundleGenerale.getString(messageKey);

        } catch (Throwable t) {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.applicazione.inaspettataException";
          logger.error(this.resBundleGenerale.getString(messageKey), t);
          this.aggiungiMessaggio(request, messageKey);
          liberaLock = true;
          livEvento  = 3;
          errMsgEvento = this.resBundleGenerale.getString(messageKey);

        } finally {
          if (liberaLock) {
            // non sono riuscito a connettermi, allora libero il lock preso in
            // precedenza
            LimitatoreConnessioniUtenti.getInstance().deallocaConnessione(
                session.getId());
          } else {
            utente.setIp(request.getRemoteAddr());
            utente.setDataAccesso(new Date());
            LimitatoreConnessioniUtenti.getInstance().setDatiSessioneUtente(
                session.getId(),
                request.getRemoteAddr(),
                account.getLogin(),
                UtilityDate.getDataOdiernaAsString(UtilityDate.FORMATO_GG_MM_AAAA_HH_MI_SS),
                session);
          }
          
          if(account != null && (account.getIdAccount() == 48 || account.getIdAccount() == 47)){
            if("success".equals(target) || "successSkipProfili".equals(target)){
              target = "successAdmin";
            }
            request.getSession().setAttribute(CostantiGenerali.SENTINELLA_ACCESSO_AMMINISTRATORE, "1");
            request.getSession().setAttribute(CostantiGenerali.SENTINELLA_DOPPIA_AUTENTICAZIONE, "1");
          }else if (!CostantiGeneraliStruts.FORWARD_REGISTRAZIONE.equals(target) && tracciaEventoLogin) {
            // nel caso di accesso di un utente LDAP con redirect alla form di registrazione non si traccia l'aute
            LogEvento logEvento = LogEventiUtils.createLogEvento(request);
            logEvento.setLivEvento(livEvento);
            logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_LOGIN);
            logEvento.setDescr("Login id sessione = " + request.getSession().getId());
            logEvento.setErrmsg(errMsgEvento);
            LogEventiUtils.insertLogEventi(logEvento);
            
            //JIRA GENEWEB-159: controllo accessi multipli 
            HashMap<String, String[]> sessioni = LimitatoreConnessioniUtenti.getInstance().getDatiSessioniUtentiConnessi();
            List<String> ipList= new ArrayList<String>();
            
            if (account != null) {
	            for(String key :sessioni.keySet()) {
	              if(account.getLogin().equalsIgnoreCase(sessioni.get(key)[1])){ 
	                ipList.add(sessioni.get(key)[0]);   
	              }
	            }  
	            if(ipList.size()>1) {
	              List<String> ipListUnique= new ArrayList<String>();
	              for(String ip : ipList) {
	                if(!ipListUnique.contains(ip)) {
	                  ipListUnique.add(ip);
	                }
	              }
	              LogEvento eventoLoginMultipli = LogEventiUtils.createLogEvento(request);
	               eventoLoginMultipli.setLivEvento(LogEvento.LIVELLO_WARNING);
	               eventoLoginMultipli.setCodEvento(LogEventiUtils.COD_EVENTO_ACCESSO_SIMULTANEO);
	               eventoLoginMultipli.setDescr("Sessioni attive: "+ipList.size()+" | Ip connessi: "+String.join(", ", ipListUnique));
	               eventoLoginMultipli.setErrmsg(errMsgEvento);
	               LogEventiUtils.insertLogEventi(eventoLoginMultipli);
	            }           
            }
          }
          
        }
      }
      
    }
    return target;
  }

  /**
   * Effettua i controlli previsti dal GDPR per cui un utente raggiunto il limite massimo di tentativi di login falliti non puo' entrare per
   * un lasso di tempo, e se prova il processo di login viene comunque rallentato.
   *
   * @param request
   * @param username
   *        utente da verificare
   * @return true se l'utente e' bloccato, false altrimenti
   * @throws InterruptedException
   */
  private boolean isBloccoLogin(HttpServletRequest request, String username) throws InterruptedException {
    boolean isBlocco = false;
    String messageKey;
    String numMaxTentativiLogin = ConfigManager.getValore(CostantiGenerali.PROP_NUM_MAX_TENTATIVI_LOGIN);
    int numSecondiDelayLoginBloccata = Integer.parseInt(ConfigManager.getValore(CostantiGenerali.PROP_NUM_SECONDI_DELAY_LOGIN_BLOCCATA));
    int numMinutiBloccoLogin = Integer.parseInt(ConfigManager.getValore(CostantiGenerali.PROP_DURATA_MINUTI_BLOCCO_LOGIN));
    int numTentativiFalliti = this.accountManager.getNumeroLoginFallite(username);
    if (numTentativiFalliti >= Integer.parseInt(numMaxTentativiLogin)) {
      // l'utenza e' bloccata, e' forse ora si sbloccarla?
      Date dataOraUltimaLoginFallita = this.accountManager.getUltimaLoginFallita(username);
      Date dataOraAttuale = new Date();
      if (dataOraAttuale.compareTo(DateUtils.addMinutes(dataOraUltimaLoginFallita, numMinutiBloccoLogin)) > 0) {
        // si, sono trascorsi i minuti di blocco previsti per l'utente, pertanto...
        // ...si ripristina la normalita'...
        this.accountManager.deleteLoginFallite(username);
        // ...si traccia l'evento di sblocco utente
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setLivEvento(LogEvento.LIVELLO_INFO);
        logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_UNLOCK_LOGIN_UTENTE);
        logEvento.setDescr("Rimozione blocco temporaneo per utente " + username);
        LogEventiUtils.insertLogEventi(logEvento);
        // si passa all'effettivo processo di verifica autenticazione
      } else {
        // no, non sono ancora trascorsi i minuti di blocco, pertanto...
        // ...si rallenta l'esecuzione per evitare che sia un eventuale attacco di brute force...
        Thread.sleep(numSecondiDelayLoginBloccata*1000);
        //...si inserisce nel log ed a video il messaggio di errore...
        messageKey = "errors.login.raggiuntoMaxNumTentativiFalliti";
        this.aggiungiMessaggio(request, messageKey, username, numMaxTentativiLogin);
        logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
            UtilityStringhe.getPatternParametroMessageBundle(0),
            username).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(1), numMaxTentativiLogin));
        //... e si esce
        isBlocco = true;
      }
    }
    return isBlocco;
  }

  /**
   * Estrae l'account individuato dai parametri di input
   *
   * @param username
   *        username dell'utente
   * @param password
   *        password dell'utente
   * @return oggetto account estratto dalla USRSYS
   * @throws CriptazioneException
   * @throws SqlComposerException
   * @throws DataAccessException
   */
  protected Account getAccount(String username, String password)
      throws CriptazioneException, DataAccessException, SqlComposerException {
    Account account = this.loginManager.getAccountByLoginEPassword(username, password);
    return account;
  }

  /**
   * Esegue una serie di verifiche sull'account estratto
   *
   * @param request
   *        http request ricevuto dalla form
   * @param username
   *        username utilizzato in fase di autenticazione
   * @param password
   *        password utilizzata in fase di autenticazione in formato per il DB
   * @param account
   *        account estratto dal database
   * @return nuovo valore da far assumere al target
   */
  protected String verificaAccount(HttpServletRequest request, String username,
      String password, Account account) throws AuthenticationException,
      Exception {

    String target = null;
    String messageKey = null;

    if (account == null) {
      // JIRA GENEWEB-10: in caso di login fallita si inserisce una nuova occorrenza
      target = this.insertLoginFallita(request, username, target);
    } else if (account.getFlagLdap().intValue() == 0) {
      // JIRA GENEWEB-10: in caso di login con successo, si fa pulizia
      this.accountManager.deleteLoginFallite(username);
    }

    // se è stato estratto un utente, si verifica la password, lo stato,
    // l'eventuale scadenza (in futuro)

    // controllo password
    if (target == null) {
      // FD 27/09/2007: gestione login utenti LDAP

      if (account.getFlagLdap().intValue() == 1) {
        target = this.verifyAccountLdap(request, username, password, account,
            target);
      } else {
        // FD 01/09/08: si è deciso di portare il numero di caratteri della
        // password a 30 per gli utenti non LDAP quindi commento il
        // controllo
        target = this.verifyPassword(request, username, password, account, target);
      }

      if (target == null) {
        // controllo ultimo accesso
        if (account.getUltimoAccesso() != null) {
          // il controllo sulla data ultimo accesso deve essere effettuata solo per gli utenti non amministratori
          OpzioniUtente opzioniUtente = new OpzioniUtente(account.getOpzioniUtente());

          CheckOpzioniUtente opzioniAmministratoreSistema = new CheckOpzioniUtente(
              CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);

          // controlla se l'accesso e' avvenuto prima della scadenza utente (ultimo accesso + gg da configurazione)
          String numGiorniDurata = ConfigManager.getValore(CostantiGenerali.PROP_DURATA_ACCOUNT);
          Date dataAttuale = new Date();
          if (!opzioniAmministratoreSistema.test(opzioniUtente) && dataAttuale.compareTo(DateUtils.addDays(account.getUltimoAccesso(), Integer.parseInt(numGiorniDurata))) > 0) {
            // va bloccato l'utente, in tal caso si disabilita l'utente, lo si avvisa dall'interfaccia e si traccia nel log
            this.accountManager.updateAbilitazioneUtente(account.getIdAccount(), CostantiDettaglioAccount.DISABILITATO);
            target = CostantiGeneraliStruts.FORWARD_LOGIN;
            messageKey = "errors.login.accountScadutoPerInattivita";
            logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
                UtilityStringhe.getPatternParametroMessageBundle(0),
                account.getLogin()).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(1), numGiorniDurata));
            this.aggiungiMessaggio(request, messageKey, account.getLogin(), numGiorniDurata);
          } else {
            // va aggiornata in db l'informazione di ultimo accesso per l'utente
            this.accountManager.updateUltimoAccesso(account.getIdAccount());
          }
        } else {
          // si imposta per la prima volta l'informazione di ultimo accesso per l'utente
          this.accountManager.updateUltimoAccesso(account.getIdAccount());
        }
      }

      // F.D. 16/04/08 controllo sull'abilitazione dell'account
      if (target == null) {
        if (account.isNotAbilitato()) {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.login.utenteDisabilitato";
          logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(0),
              username));
          this.aggiungiMessaggio(request, messageKey, username);
        } else {
        //C.F. 19/11/2009 metto il controllo sulla scadenza account
          target = this.verifyExpiry(request, account, target);
        }
      }

    }
    
    
    return target;
  }

  /**
   * Introduce la marcatura per un'autenticazione fallita, procdice il messaggio per l'utente e nel log, e nel caso di raggiunto limite di
   * login falliti traccia l'evento di blocco temporaneo utente.
   *
   * @param request
   *        request http
   * @param username
   *        utente
   * @param target
   *        target Struts
   * @return target struts modificato ed eventuale messaggio di errore
   */
  private String insertLoginFallita(HttpServletRequest request, String username, String target) {
    String messageKey;
    String message;
    this.accountManager.insertLoginFallita(username, request.getRemoteAddr());
    String numMaxTentativiLogin = ConfigManager.getValore(CostantiGenerali.PROP_NUM_MAX_TENTATIVI_LOGIN);
    int numTentativiFalliti = this.accountManager.getNumeroLoginFallite(username);
    if (numTentativiFalliti == Integer.parseInt(numMaxTentativiLogin)) {
      // raggiunto il limite, si traccia l'evento
      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setLivEvento(LogEvento.LIVELLO_ERROR);
      logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_LOCK_LOGIN_UTENTE);
      logEvento.setDescr("Blocco temporaneo utente " + username);
      LogEventiUtils.insertLogEventi(logEvento);
      // si manda alla pagina di errore generale
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      // e si logga, fornendo un messaggio di errore del blocco
      messageKey = "errors.login.raggiuntoMaxNumTentativiFalliti";
      message = this.resBundleGenerale.getString(messageKey).replaceAll(
          UtilityStringhe.getPatternParametroMessageBundle(0),
          username).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(1), numMaxTentativiLogin);
      this.aggiungiMessaggio(request, messageKey, username, numMaxTentativiLogin);
      logger.error(message);
    } else {
      // se l'utente non esiste, allora lo si redireziona
      // alla pagina di login, fornendo un messaggio di errore
      // di utente sconosciuto
      target = CostantiGeneraliStruts.FORWARD_LOGIN;
      messageKey = "errors.login.unknown";
      message = this.resBundleGenerale.getString(messageKey).replaceAll(
          UtilityStringhe.getPatternParametroMessageBundle(0),
          username);
      this.aggiungiMessaggio(request, messageKey, username);
      logger.error(message);
    }
    return target;
  }

  /**
   * Verifica l'utenza LDAP mediante la chiamata al server LDAP per
   * l'autenticazione
   *
   * @param request
   *        request HTTP
   * @param username
   *        username inserita
   * @param password
   *        password inserita
   * @param account
   *        account estratto dalla USRSYS
   * @param target
   *        target Struts
   * @return target modificato
   * @throws Exception
   */
  protected String verifyAccountLdap(HttpServletRequest request,
      String username, String password, Account account, String target)
      throws Exception {
    String messageKey;
    // FD 15/09/08 se l'utente è LDAP la password non può essere null
    if (password == null && account.getPassword() == null) {
      target = CostantiGeneraliStruts.FORWARD_LOGIN;
      messageKey = "errors.login.passwordErrata";
      logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
          UtilityStringhe.getPatternParametroMessageBundle(0),
          username));
      this.aggiungiMessaggio(request, messageKey, username);
    } else {
      try {
        ldapManager.verificaAccount(account.getDn(), password);
        // JIRA GENEWEB-10: in caso di login con successo, si fa pulizia
        this.accountManager.deleteLoginFallite(username);
      } catch (AuthenticationException e) {
        // JIRA GENEWEB-10: nel caso di login con errori, si inserisce la tracciatura errata
        target = this.insertLoginFallita(request, username, target);
      }
    }
    return target;
  }

  /**
   * Verifica che la password in input sia la stessa dell'account estratto
   *
   * @param request
   *        request HTTP
   * @param password
   *        password ricevuta
   * @param account
   *        account estratto
   * @param target
   *        target Struts
   * @return target modificato
   */
  protected String verifyPassword(HttpServletRequest request, String username, String password,
      Account account, String target) {
    String messageKey;
    // SS 06/11/2006: la password può anche essere null
    if (!((password == null && account.getPassword() == null) || (password != null
        && account.getPassword() != null && password.equals(account.getPassword())))) {
      target = CostantiGeneraliStruts.FORWARD_LOGIN;
      messageKey = "errors.login.passwordErrata";
      logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
          UtilityStringhe.getPatternParametroMessageBundle(0),
          username));
      this.aggiungiMessaggio(request, messageKey);
    }
    return target;
  }

  /**
   * Verifica che la data di scadenza dell'account non sia espirata
   *
   * @param request
   *        request HTTP
   * @param account
   *        account estratto
   * @param target
   *        target Struts
   * @return Integer
   *         risultato operazione
   *         1=account scaduto;0= account in vita
   */
  protected String verifyExpiry(HttpServletRequest request,Account account, String target) {

    java.util.Date dataSys = new Date();
    Date dataScad = account.getScadenzaAccount();
    String messageKey = null;

    if(dataScad != null && dataScad.before(dataSys)){
      target = CostantiGeneraliStruts.FORWARD_LOGIN;
      messageKey = "errors.login.accountScaduto";
      logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
          UtilityStringhe.getPatternParametroMessageBundle(0),
          account.getLogin()));
      this.aggiungiMessaggio(request, messageKey, account.getLogin());
      try {
        this.accountManager.updateAbilitazioneUtente(account.getIdAccount(),
            CostantiDettaglioAccount.DISABILITATO);
      } catch (DataAccessException e) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.database.dataAccessException";
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
      }
      return target;
    }
    return target;
  }
  
}