/*
 * Created on 22/gen/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.login;

import it.eldasoft.gene.bl.LoginManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.system.LdapManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Testa se all'atto della presentazione della pagina di login, l'utente è già
 * loggato nella stessa sessione
 *
 * @author Stefano.Sabbadin
 * @since 1.4.6
 */
public class IsUserLoggedAction extends Action {

  /** Logger Log4J di classe */
  static Logger            logger            = Logger.getLogger(IsUserLoggedAction.class);
  
  /** Reference alla classe di business logic per l'estrazione dell'account */
  protected LoginManager      loginManager;

  protected AccountManager      accountManager;

  protected LdapManager         ldapManager;

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
   * Resource bundle parte generale dell'applicazione (modulo GENE)
   */
  protected ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("execute: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;

    DataSourceTransactionManagerBase.setRequest(request);
    
    if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_LOGIN_MULTI_ENTE))) {
      if (request.getSession().getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO) == null) {
        // nel caso di configurazione multiente, blocco l'azione se bypasso la selezione ente
        this.aggiungiMessaggio(request, "errors.loginMultiente.enteNonSelezionato");
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      }
    }    

    // se il client è già loggato, ne blocco l'accesso
    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    boolean isPairingSSO = "true".equals((String) request.getSession().getAttribute(
        CostantiGenerali.PAIRING_SSO));
    if (profiloUtente != null && !isPairingSSO) {
      target = "logged";
      String messageKey = "errors.login.utenteLoggato";
      logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
          UtilityStringhe.getPatternParametroMessageBundle(0),
          profiloUtente.getNome()));
      this.aggiungiMessaggio(request, messageKey, profiloUtente.getNome());
    }

    ActionForward forward = null;
    if (CostantiGeneraliStruts.FORWARD_OK.equals(target)) {
      forward = this.runAction(mapping, form, request, response);
    } else {
      forward = mapping.findForward(target);
    }

    if (logger.isDebugEnabled()) logger.debug("execute: fine metodo");

    return forward;
  }

  /**
   * Consente un'eventuale personalizzazione da parte di classi figlie della
   * presente dopo il check sull'utente se già loggato
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    return mapping.findForward(CostantiGeneraliStruts.FORWARD_OK);
  }

  /**
   * Crea un ActionMessages nel request, popolato con il messaggio individuato
   * dalla chiave in input
   *
   * @param request
   *        request http
   * @param chiave
   *        chiave del messaggio da inserire
   */
  public void aggiungiMessaggio(HttpServletRequest request, String chiave) {
    String tipoMessaggio = ActionBase.getTipoMessaggioFromChiave(chiave);

    ActionMessages errors = new ActionMessages();
    errors.add(tipoMessaggio, new ActionMessage(chiave));
    if (!errors.isEmpty()) this.addMessages(request, errors);
  }

  /**
   * Crea un ActionMessages nel request, popolato con il messaggio individuato
   * dalla chiave in input e con il parametro individuato dall'argomento1
   *
   * @param request
   *        request http
   * @param chiave
   *        chiave del messaggio da inserire
   * @param argomento1
   *        argomento da inserire nel testo del messaggio
   */
  public void aggiungiMessaggio(HttpServletRequest request, String chiave,
      String argomento1) {
    String tipoMessaggio = ActionBase.getTipoMessaggioFromChiave(chiave);

    ActionMessages errors = new ActionMessages();
    errors.add(tipoMessaggio, new ActionMessage(chiave, argomento1));
    if (!errors.isEmpty()) this.addMessages(request, errors);
  }

  /**
   * Crea un ActionMessages nel request, popolato con il messaggio individuato
   * dalla chiave in input e con due argomenti.
   *
   * @param request
   *        request http
   * @param chiave
   *        chiave del messaggio da inserire
   * @param argomento1
   *        argomento 1 da inserire nel testo del messaggio
   * @param argomento2
   *        argomento 2 da inserire nel testo del messaggio
   */
  protected void aggiungiMessaggio(HttpServletRequest request, String chiave,
      String argomento1, String argomento2) {
    String tipoMessaggio = ActionBase.getTipoMessaggioFromChiave(chiave);

    ActionMessages errors = new ActionMessages();
    errors.add(tipoMessaggio, new ActionMessage(chiave, argomento1,
        argomento2));
    if (!errors.isEmpty()) this.addMessages(request, errors);
  }
  
  /**
   * Traccia i parametri presenti nell'header HTTP della richiesta.
   *
   * @param request request HTTP
   */
  protected void logHeaderParameters(HttpServletRequest request) {
    @SuppressWarnings("unchecked")
    Enumeration<String> headerNames = request.getHeaderNames();
    StringBuilder sb = new StringBuilder("Parametri header richiesta HTTP\n");
    while (headerNames.hasMoreElements()) {
      String key = headerNames.nextElement();
      String value = request.getHeader(key);
      sb.append(key).append("=").append(value).append("\n");
    }
    if (logger.isDebugEnabled()) {
      logger.debug(sb.toString());
    }
  }
  

  protected ActionForward forwardToRegistrationForm(HttpServletRequest request){
    String nomePagina = ConfigManager.getValore(CostantiGenerali.PROP_REGISTRAZIONE_NOME_PAGINA);
    if(nomePagina == null || "".equals(nomePagina)){
      nomePagina = "registrazione-account.jsp?modo=NUOVO";
    }
    return UtilityStruts.redirectToPage("/" + nomePagina,true,request);
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
  protected String pairUtenteLdap0WithSSO(HttpServletRequest request, String codiceFiscale, String sso) {
    
    request.getSession().removeAttribute(CostantiGenerali.PAIRING_SSO);
    
    ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    String descEvento="Associazione utente "+profilo.getLogin()+" (syscon: "+profilo.getId()+") a sistema di autenticazione esterno ("+sso+")";
    int livEvento=1;
    String errore=null;
    
    try {
      Account account = this.accountManager.getAccountBySysCf(codiceFiscale);
      if(account != null && account.getIdAccount()!=profilo.getId()) {
        String messageKey = "errors.pairingSSO.cfPresente";
        errore=this.resBundleGenerale.getString(messageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0),codiceFiscale);
        logger.error(errore);
        this.aggiungiMessaggio(request, messageKey, codiceFiscale);
        livEvento=3;
      }else {
        this.accountManager.updateLdapUsr(profilo.getId(), codiceFiscale);
        this.aggiungiMessaggio(request, "info.pairingSSO.successo", codiceFiscale); 
      }  
    }catch(Exception e) {
      String messageKey = "errors.pairingSSO.generico";
      errore=this.resBundleGenerale.getString(messageKey);
      logger.error(errore);
      this.aggiungiMessaggio(request, messageKey);
      livEvento=3;
    }finally{
      try {
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(codiceFiscale);
        logEvento.setCodEvento("ASSOCIAZIONE_UTENZA_SSO");
        logEvento.setDescr(descEvento);
        logEvento.setErrmsg(errore);
        LogEventiUtils.insertLogEventi(logEvento);
      } catch (Exception le) {
        logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
      }
    }
    return "pairingSSOResult";
  }
}
