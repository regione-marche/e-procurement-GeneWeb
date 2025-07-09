package it.eldasoft.gene.web.struts.login.webserver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.AccountSSO;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.web.struts.login.LoginAction;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Action per la gestione generica della risposta di un'autenticazione "webserver" alla login
 *
 * @author Manuel Bridda
 */


public class WebserverLoginAction extends LoginAction {
    private static final String PROP_SSO_ATTRIBUTE_STANDARD_PROVIDER = "auth.sso.webserver.mapping.standardProvider.";
    private static final String PROP_SSO_ATTRIBUTE_STANDARD_PROVIDER_ENCODING = "auth.sso.webserver.mapping.standardProviderEncoding.";
    private static final String PROP_SSO_ATTRIBUTE_LOA = "auth.sso.webserver.mapping.loa.";
    private static final String PROP_SSO_ATTRIBUTE_LOA_ENCODING = "auth.sso.webserver.mapping.loaEncoding.";
    private static final String PROP_SSO_ATTRIBUTE_LOGIN = "auth.sso.webserver.mapping.login.";
    private static final String PROP_SSO_ATTRIBUTE_FIRSTNAME = "auth.sso.webserver.mapping.firstName.";
    private static final String PROP_SSO_ATTRIBUTE_LASTNAME = "auth.sso.webserver.mapping.lastName.";
    private static final String PROP_SSO_ATTRIBUTE_DESCRIPTION = "auth.sso.webserver.mapping.description.";
    private static final String PROP_SSO_ATTRIBUTE_MAIL = "auth.sso.webserver.mapping.mail.";
    private static final String PROP_SSO_ATTRIBUTE_FISCALCODE = "auth.sso.webserver.mapping.fiscalCode.";

    /** Logger Log4J di classe */
    static Logger logger = Logger.getLogger(WebserverLoginAction.class);
	
	/** Discriminante configurazione. */
	private Integer configurazione;
	
	public void setConfigurazione(Integer configurazione) {
		this.configurazione = configurazione;
	}
	

    @Override
    protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

      Account account = null;
      AccountSSO accountSSO = null;
      String messageKey = "";
      
      if (this.configurazione == null || this.configurazione < 1 || this.configurazione > 3) {
        throw new NumberFormatException("url WebserverLogin con indice errato (" + this.configurazione  + ")");
      }
      String configurazioneWebserver = null;
      switch (this.configurazione) {
      case 2:
        configurazioneWebserver = CostantiGenerali.PROP_SSO_WEBSERVER2;
        break;
      case 3:
        configurazioneWebserver = CostantiGenerali.PROP_SSO_WEBSERVER3;
        break;
      default:
        configurazioneWebserver = CostantiGenerali.PROP_SSO_WEBSERVER1;
      }

      this.logHeaderParameters(request);

      // lettura della configurazione con i parametri necessari per l'autenticazione
      String attributeKeyStandardProvider = ConfigManager.getValore(PROP_SSO_ATTRIBUTE_STANDARD_PROVIDER+configurazione);
      String standardProviderEncoding = ConfigManager.getValore(PROP_SSO_ATTRIBUTE_STANDARD_PROVIDER_ENCODING+configurazione);
      String attributeKeyLoA = ConfigManager.getValore(PROP_SSO_ATTRIBUTE_LOA+configurazione);
      String loAEncoding = ConfigManager.getValore(PROP_SSO_ATTRIBUTE_LOA_ENCODING+configurazione);
      String attributeKeyLogin = ConfigManager.getValore(PROP_SSO_ATTRIBUTE_LOGIN+configurazione);
      String attributeKeyFirstName = ConfigManager.getValore(PROP_SSO_ATTRIBUTE_FIRSTNAME+configurazione);
      String attributeKeyLastName = ConfigManager.getValore(PROP_SSO_ATTRIBUTE_LASTNAME+configurazione);
      String attributeKeyDescription = ConfigManager.getValore(PROP_SSO_ATTRIBUTE_DESCRIPTION+configurazione);
      String attributeKeyMail = ConfigManager.getValore(PROP_SSO_ATTRIBUTE_MAIL+configurazione);
      String attributeKeyFiscalCode = ConfigManager.getValore(PROP_SSO_ATTRIBUTE_FISCALCODE+configurazione);

      // lettura dei dati ricevuti e significativi
      String standardProviderSource = request.getHeader(attributeKeyStandardProvider);
      String standardProvider = null;
      if(StringUtils.isBlank(standardProviderSource)) {
          // se non e' presente un attributo che mappa il provider, allora leggo il valore da una cfg
        if(StringUtils.isNotBlank(standardProviderEncoding))
          standardProvider = standardProviderEncoding;
      } else {
          if("SPID".equalsIgnoreCase(standardProviderSource) || "CIE".equalsIgnoreCase(standardProviderSource)) {
          //Se ottengo SPID/CIE direttamente, non faccio altro (per retrocompatibilità)
            standardProvider=standardProviderSource;
          } else if(StringUtils.isNotBlank(standardProviderEncoding)){
            // mappo la lista valoresorgente1|providerdestinazione1;valoresorgente2|providerdestinazione2 in una mappa
            Map<String, String> mappa = Arrays.stream(
                standardProviderEncoding.split(";")).map(next -> next.split("\\|"))
                .collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
            // se e' presente l'attributo, allora memorizzo il mapping associato al valore estratto
            standardProvider = mappa.get(standardProviderSource);
          }   
      }
      if(StringUtils.isNotBlank(standardProvider))
        standardProvider = standardProvider.toUpperCase();
      
      String loaSource = request.getHeader(attributeKeyLoA);
      int loa = 2; // livello minimo per una autenticazione sso
      if (StringUtils.isBlank(loaSource)) {
        // se non e' presente un attributo che mappa il loa, allora leggo il valore da una cfg
        if(StringUtils.isNotBlank(loAEncoding))
          loa = UtilityNumeri.convertiIntero(loAEncoding);
      } else {
        // mappo la lista valoresorgente1|loanumericodestinazione1;valoresorgente2|loanumericodestinazione2 in una mappa
        Map<String, Integer> mappa = Arrays.stream(
            loAEncoding.split(";")).map(next -> next.split("\\|"))
            .collect(Collectors.toMap(entry -> entry[0], entry -> Integer.parseInt(entry[1])));
        // se e' presente l'attributo, allora memorizzo il mapping associato al valore estratto
        loa = mappa.get(loaSource);
      }
      String login = UtilityStringhe.convertiNullInStringaVuota(request.getHeader(attributeKeyLogin));
      String nome = request.getHeader(attributeKeyFirstName);
      String cognome = request.getHeader(attributeKeyLastName);
      String denominazione = request.getHeader(attributeKeyDescription);
      String codiceFiscale = UtilityStringhe.convertiNullInStringaVuota(request.getHeader(attributeKeyFiscalCode));
      String email = request.getHeader(attributeKeyMail);
      
      if("true".equals((String) request.getSession().getAttribute(CostantiGenerali.PAIRING_SSO))) {//pairing utenza base con utenzaSSO
        return mapping.findForward(this.pairUtenteLdap0WithSSO(request, codiceFiscale, configurazioneWebserver));
      }

      String target = CostantiGeneraliStruts.FORWARD_OK;

      try {

        target = this.testSkipProfili(request, target);

        // verifica se l'utente viene individuato ed e' presente in base dati
        if(StringUtils.isNotBlank(attributeKeyFiscalCode))
          account = this.accountManager.getAccountBySysCf(codiceFiscale);
        else
          account = this.loginManager.getAccountByLogin(login);
          
        
        if (account != null) {
          
          accountSSO = new AccountSSO();
          accountSSO.setLogin(login);
          accountSSO.setNome(nome);
          accountSSO.setCognome(cognome);
          accountSSO.setEmail(email);
          accountSSO.setLoginProvider(configurazioneWebserver);
          accountSSO.setStandardProvider(standardProvider);
          accountSSO.setCurrentLoa(loa);

          target = this.checkLogin(account.getLogin(), account.getPassword(), request, accountSSO, target);
        } else {
          // Settaggio dei vari data da usare in preinizializzazione della form di registrazione

          // se arriva nome e cognome si settano separatamente, altrimenti nel nome si memorizza la denominazione
          request.setAttribute("nome", nome != null ? nome : denominazione);
          request.setAttribute("cognome", cognome);
          request.setAttribute("login", login);
          request.setAttribute("codfisc", codiceFiscale);
          request.setAttribute("email", email);
          // Attributo che in questo caso indica presenza di integrazione SSO
          request.setAttribute("flagLdap", "3");
          if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_ATTIVA_FORM_REGISTRAZIONE))) {
            return this.forwardToRegistrationForm(request);
          } else {
            target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
            messageKey = "errors.login.unknownUser";
            this.aggiungiMessaggio(request, messageKey);
          }
        }
      } catch (Exception e) {
        logger.error("Errore in fase di individuazione dell'utente (login="
            + login
            + ", nome="
            + nome
            + ", cognome="
            + cognome
            + ", denominazione="
            + denominazione
            + ", codice fiscale="
            + codiceFiscale
            + ", email="
            + email
            + ")", e);
        String error = "Attenzione: C'è stato un problema con la richiesta di autenticazione, si prega di riprovare più tardi o di contattare l'assistenza.\n\nGrazie";
        request.setAttribute("error", error);
        messageKey = "errors.login.sso.noAutenticazione";
        this.aggiungiMessaggio(request, messageKey, login);
        return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
      }

      return mapping.findForward(target);

    }
}
