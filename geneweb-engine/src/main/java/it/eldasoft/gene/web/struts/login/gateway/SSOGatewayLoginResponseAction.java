package it.eldasoft.gene.web.struts.login.gateway;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.web.struts.login.LoginAction;
import it.eldasoft.utils.properties.ConfigManager;

public class SSOGatewayLoginResponseAction extends LoginAction {
  
  private final class GatewayAuthData {
    
    private final String CODICE_FISCALE;
    private final String NOME;
    private final String COGNOME;
    private final String EMAIL;
    
    public String getCodiceFiscale() {
      return CODICE_FISCALE;
    }
    
    public String getNome() {
      return NOME;
    }
    
    public String getCognome() { 
      return COGNOME;
    }
    
    public String getEmail() {
      return EMAIL;
    }
    
    public GatewayAuthData(final String jwtToken) {
      final String passphrase = StringUtils.stripToNull(ConfigManager.getValore(CostantiGenerali.PROP_SSO_GATEWAY_PASSPHRASE));

      JwtParser parser = Jwts.parser().setSigningKey(passphrase);
      Jws<Claims> jws = parser.parseClaimsJws(jwtToken);
      
      CODICE_FISCALE = jws.getBody().get("codiceFiscale", String.class);
      NOME = jws.getBody().get("nome", String.class);
      COGNOME = jws.getBody().get("cognome", String.class);
      EMAIL = jws.getBody().get("email", String.class);
    }
    
  }
  
  private static final Logger LOGGER = Logger.getLogger(SSOGatewayLoginResponseAction.class);
  
  private static final String ERROR_MSG = 
      "Attenzione: C'è stato un problema con la richiesta di autenticazione, si prega di riprovare più tardi o di contattare l'assistenza in quanto l'utente può non disporre dei permessi necessari al login.\n\nGrazie";
  
  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("runAction: inizio metodo");
    
    String target = CostantiGeneraliStruts.FORWARD_OK;
    
    // Recuper le informazioni dal token JWT
    final String jwtToken = request.getParameter("jwtToken");
    final GatewayAuthData authData = new GatewayAuthData(jwtToken);
    
    try {
      final Account account = this.loginManager.getAccountByLogin(authData.getCodiceFiscale());
      
      if (account == null) {
        request.setAttribute("login", authData.getCodiceFiscale());
        request.setAttribute("codfisc", authData.getCodiceFiscale());
        request.setAttribute("nome", authData.getNome());
        request.setAttribute("cognome", authData.getCognome());
        request.setAttribute("email", authData.getEmail());
        request.setAttribute("flagLdap", "3");

        if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_ATTIVA_FORM_REGISTRAZIONE))) {
          return this.forwardToRegistrationForm(request);
        }else{
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          this.aggiungiMessaggio(request, "errors.login.unknown", "");
        }
      } else {
        target = this.checkLogin(account.getLogin(), account.getPassword(), request, true, target);
        request.setAttribute("username", account.getLogin());
        request.setAttribute("password", account.getPassword());
      }
    } catch (Exception e) {
      LOGGER.error("Errore nella verifica dell'utente " 
          + e.getMessage() + " " + e.getLocalizedMessage() + " Dati passati"
          + " -Nome: " + authData.getNome()
          + " -Cognome: " + authData.getCognome()
          + " -CodFiscale: " + authData.getCodiceFiscale());
      request.setAttribute("error", ERROR_MSG);
      this.aggiungiMessaggio(request, "errors.login.sso.noAutenticazione", authData.getCodiceFiscale());
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
    }
    
    if (LOGGER.isDebugEnabled()) LOGGER.debug("runAction: fine metodo");
    
    return mapping.findForward(target);
  }

}
