package it.eldasoft.gene.web.struts.login.spid;

import it.cedaf.authservice.service.AuthData;
import it.cedaf.authservice.service.AuthException;
import it.cedaf.authservice.service.AuthService;
import it.cedaf.authservice.service.AuthServiceProxy;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.AccountSSO;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.web.struts.login.LoginAction;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per la gestione della risposta di SPID alla login
 *
 * @author Cristian.Febas
 */


public class SpidLoginResponseAction extends LoginAction {

    static Logger logger = Logger.getLogger(SpidLoginResponseAction.class);


    @Override
    protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	    throws IOException, ServletException {

    String url = StringUtils.stripToNull(ConfigManager.getValore(CostantiGenerali.PROP_SSO_SPID_WS_AUTHSERVICESPID_URL));

    Account account = null;
    AccountSSO accountSSO = null;
	String nome = "";
	String cognome = "";
	String codiceFiscale = "";
	String partitaIVA = "";
	String email = "";
	String username = "";
	String azienda = "";
	String messageKey = "";
	String target = CostantiGeneraliStruts.FORWARD_OK;

    AuthServiceProxy authServiceProxy = new AuthServiceProxy();
    authServiceProxy.setEndpoint(url);
    AuthService ws = authServiceProxy.getAuthService();

    // recupera dalla sessione il token temporaneo...
    String authId = (String) request.getSession().getAttribute(CostantiGenerali.SESSION_ID_SPID_AUTHID);

    // recupera le info dell'utente dal servizio SPID tramite il token...
    AuthData userInfo = null;
    try {
      userInfo = ws.retrieveUserData(authId);
    }catch(AuthException e) {
      logger.error("Errore nel recupero delle informazioni dal provider SPID", e);
      this.aggiungiMessaggio(request, "errors.generico", "Errore nel recupero delle informazioni dal provider");
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_LOGIN);
    }
    if(userInfo != null) {

      // azienda o libero professionista ?
      nome = StringUtils.stripToNull(userInfo.getNome());
      cognome = StringUtils.stripToNull(userInfo.getCognome());
      azienda = StringUtils.stripToNull(userInfo.getAziendaDenominazione());
      codiceFiscale = StringUtils.stripToNull(userInfo.getCodiceFiscale());
      partitaIVA = StringUtils.stripToNull(userInfo.getAziendaPIVA());
      email = ( StringUtils.isNotEmpty(userInfo.getPec()) ? StringUtils.stripToNull(userInfo.getPec()) : StringUtils.stripToNull(userInfo.getMailAddress()) );

      //per essere un nome azienda valido deve avere almeno 3 caratteri
      boolean isPersonaFisica = azienda != null && azienda.length() > 2 ? false : true;

      // calcolo di alcune variabili in seguito all'autenticazione SPID
      // e valida anche per soggetti giuridici
      String login = codiceFiscale;
      if (!isPersonaFisica) {
          login = (partitaIVA != null ? partitaIVA : codiceFiscale);
      }

      if (((isPersonaFisica && nome != null && cognome != null) || (!isPersonaFisica && azienda != null))
              && login != null && login.length() >= 11) {
        request.getSession().removeAttribute("errMsg");
        
        if("true".equals((String) request.getSession().getAttribute(CostantiGenerali.PAIRING_SSO))) {//pairing utenza base con utenzaSSO
            return mapping.findForward(this.pairUtenteLdap0WithSSO(request, codiceFiscale, CostantiGenerali.PROP_SSO_SPID));
        }

        String error = "Attenzione: C'è stato un problema con la richiesta di autenticazione, si prega di riprovare più tardi o di contattare l'assistenza in quanto l'utente può non disporre dei permessi necessari al login.\n\nGrazie";

        try {

          target = this.testSkipProfili(request, target);

          account = this.accountManager.getAccountBySysCf(codiceFiscale);

          if(account != null){
            accountSSO = new AccountSSO();
            accountSSO.setAuthId(authId);
            accountSSO.setLogin(codiceFiscale);
            accountSSO.setNome(nome);
            accountSSO.setCognome(cognome);
            accountSSO.setEmail(email);
            accountSSO.setLoginProvider(CostantiGenerali.PROP_SSO_SPID);
            accountSSO.setStandardProvider(CostantiGenerali.PROP_SSO_SPID);
            accountSSO.setCurrentLoa(Integer.parseInt(userInfo.getLivelloAutenticazione().split("SpidL")[1])+1);
            
            target = this.checkLogin(account.getLogin(), account.getPassword(), request, accountSSO, target);
            //TODO: probabilmente queste set non servono a nulla e vanno rimosse
            request.setAttribute("username", account.getLogin());
            request.setAttribute("password", account.getPassword());
            
          }else{
            //Settaggio dei vari parametri nella form di registrazione
            request.setAttribute("nome", nome);
            request.setAttribute("cognome", cognome);
            request.setAttribute("login", codiceFiscale);
            request.setAttribute("codfisc", codiceFiscale);
            request.setAttribute("email", email);
            //Attributo che in questo caso indica presenza di integrazione SSO
            request.setAttribute("flagLdap", "3");
            if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_ATTIVA_FORM_REGISTRAZIONE))) {
              return this.forwardToRegistrationForm(request);
            }else{
              target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
              messageKey = "errors.login.unknownUser";
              this.aggiungiMessaggio(request, messageKey);
            }
          }
        } catch (Exception e) {
            logger.error("Errore nella verifica dell'utente " + e.getMessage() + " " + e.getLocalizedMessage() + " Dati passati -Nome: " + nome
                + " -Cognome: " + cognome + " -CodFiscale: " + codiceFiscale);
            request.setAttribute("error", error);
            messageKey = "errors.login.sso.noAutenticazione";
            this.aggiungiMessaggio(request, messageKey, codiceFiscale);
            return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
        }

      }

    }
      return mapping.findForward(target);
    }
}
