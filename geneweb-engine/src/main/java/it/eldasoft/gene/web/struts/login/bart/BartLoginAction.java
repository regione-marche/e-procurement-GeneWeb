package it.eldasoft.gene.web.struts.login.bart;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.web.struts.login.LoginAction;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per la gestione della risposta di SSOBart alla login
 *
 * @author Cristian.Febas
 */


public class BartLoginAction extends LoginAction {

    /** Logger Log4J di classe */
    static Logger logger = Logger.getLogger(BartLoginAction.class);

    @Override
    protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	    throws IOException, ServletException {

	Account account = null;
	String nome = "";
	String cognome = "";
	String fiscalcode = "";
	String email = "";
	String username = "";
	String messageKey = "";

    String bartUser = (String) request.getSession().getAttribute("edu.yale.its.tp.cas.client.filter.user");
    logger.info("edu.yale.its.tp.cas.client.filter.user = "+bartUser);
    if(bartUser != null && bartUser != "" ){
      int barra = bartUser.indexOf("/");
      fiscalcode = bartUser.substring(0,barra);
    }


	String error = "Attenzione: C'è stato un problema con la richiesta di autenticazione, si prega di riprovare più tardi o di contattare l'assistenza in quanto l'utente può non disporre dei permessi necessari al login.\n\nGrazie";

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
	    request.setAttribute("username", username);
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
