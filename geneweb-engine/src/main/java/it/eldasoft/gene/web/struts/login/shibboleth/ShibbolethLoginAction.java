package it.eldasoft.gene.web.struts.login.shibboleth;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.web.struts.login.LoginAction;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per la gestione della risposta di Shibboleth alla login
 *
 * @author Cristian.Febas
 */


public class ShibbolethLoginAction extends LoginAction {

    /** Logger Log4J di classe */
    static Logger logger = Logger.getLogger(ShibbolethLoginAction.class);

    @Override
    protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	    throws IOException, ServletException {

    this.logHeaderParameters(request);

    Account account = null;
	String nome = "";
	String cognome = "";
	String fiscalcode = "";
	String email = "";
	String username = "";
	String messageKey = "";
	nome = request.getHeader("shibb-firstname");
	cognome = request.getHeader("shibb-lastname");
	fiscalcode = request.getHeader("shibb-fiscalcode");
	fiscalcode = UtilityStringhe.convertiNullInStringaVuota(fiscalcode);
	email = request.getHeader("shibb-email");
	String error = "Attenzione: C'� stato un problema con la richiesta di autenticazione, si prega di riprovare pi� tardi o di contattare l'assistenza in quanto l'utente pu� non disporre dei permessi necessari al login.\n\nGrazie";

    String target = CostantiGeneraliStruts.FORWARD_OK;

	try {

	  target = this.testSkipProfili(request, target);

	  account = this.loginManager.getAccountByLogin(fiscalcode);

	  if(account != null){
	    target = this.checkLogin(account.getLogin(), account.getPassword(), request, true, target);
        //TODO: probabilmente queste set non servono a nulla e vanno rimosse
	    request.setAttribute("username", account.getLogin());
	    request.setAttribute("password", account.getPassword());
	  }else{
	    //Settaggio dei vari parametri nella form di registrazione
	    request.setAttribute("nome", nome);
	    request.setAttribute("cognome", cognome);
	    request.setAttribute("login", fiscalcode);
	    request.setAttribute("codfisc", fiscalcode);
	    request.setAttribute("email", email);
	    //Attributo che in questo caso indica presenza di integrazione SSO
	    request.setAttribute("flagLdap", "3");
	    if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_ATTIVA_FORM_REGISTRAZIONE))) {
	      return this.forwardToRegistrationForm(request);
	    }else{
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          messageKey = "errors.login.unknown";
          this.aggiungiMessaggio(request, messageKey, username);
	    }
	  }
	} catch (Exception e) {
	    logger.error("Errore nella verifica dell'utente " + e.getMessage() + " " + e.getLocalizedMessage() + " Dati passati -Nome: " + nome
		    + " -Cognome: " + cognome + " -CodFiscale: " + fiscalcode);
	    request.setAttribute("error", error);
	    messageKey = "errors.login.sso.noAutenticazione";
	    this.aggiungiMessaggio(request, messageKey, fiscalcode);
	    return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
	}

	return mapping.findForward(target);

    }
}
