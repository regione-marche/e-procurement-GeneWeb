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
import it.eldasoft.gene.db.domain.AccountSSO;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.web.struts.login.LoginAction;
import it.eldasoft.utils.properties.ConfigManager;

public class SSOGatewayLoginResponseAction extends LoginAction {
  
  private static final String   PROP_SSO_GATEWAY_PASSPHRASE  = "auth.sso.gateway.passphrase";
  
  private final class GatewayAuthData {
    
    private final String codiceFiscale;
    private final String nome;
    private final String cognome;
    private final String email;
    private int usrLoa = 2;
    private String userIdpType;
    
    public String getCodiceFiscale() {
      return codiceFiscale;
    }
    
    public String getNome() {
      return nome;
    }
    
    public String getCognome() { 
      return cognome;
    }
    
    public String getEmail() {
      return email;
    }
    
    public int getUsrLoa() {
      return usrLoa;
    }

    public String getUserIdpType() {
      return userIdpType;
    }

    public GatewayAuthData(final String jwtToken) {
      final String passphrase = StringUtils.stripToNull(ConfigManager.getValore(PROP_SSO_GATEWAY_PASSPHRASE));

      JwtParser parser = Jwts.parser().setSigningKey(passphrase);
      Jws<Claims> jws = parser.parseClaimsJws(jwtToken);
      
      codiceFiscale = jws.getBody().get("codiceFiscale", String.class);
      nome = jws.getBody().get("nome", String.class);
      cognome = jws.getBody().get("cognome", String.class);
      email = jws.getBody().get("email", String.class);
      String loa = jws.getBody().get("userLoa", String.class);
      if (StringUtils.isNotBlank(loa)) {
        usrLoa = Integer.valueOf(loa);
        if (usrLoa < 2 || usrLoa > 4) {
          // se non rientra nel range 2,3,4 allora lo resetto al valore minimo ovvero 2
          usrLoa = 2;
        }        
      }
      String idpType = jws.getBody().get("userIdpType", String.class); 
      if (StringUtils.isNotBlank(idpType) && ("SPID".equalsIgnoreCase(idpType) || "CIE".equalsIgnoreCase(idpType))) {
        // al momento si accettano solo i provider ufficiali SPID e CIE
        userIdpType = idpType.toUpperCase();
      } 
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
    
    if("true".equals((String) request.getSession().getAttribute(CostantiGenerali.PAIRING_SSO))) {//pairing utenza base con utenzaSSO
      return mapping.findForward(this.pairUtenteLdap0WithSSO(request, authData.getCodiceFiscale(), CostantiGenerali.PROP_SSO_GATEWAY));
    }
    
    try {
      final Account account = this.loginManager.getAccountByLogin(authData.getCodiceFiscale());
      AccountSSO accountSSO = null;
      
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
          this.aggiungiMessaggio(request, "errors.login.unknownUser");
        }
      } else {
        accountSSO = new AccountSSO();
        accountSSO.setLogin(authData.getCodiceFiscale());
        accountSSO.setNome(authData.getNome());
        accountSSO.setCognome(authData.getCognome());
        accountSSO.setEmail(authData.getEmail());
        accountSSO.setLoginProvider(CostantiGenerali.PROP_SSO_GATEWAY);
        accountSSO.setStandardProvider(authData.getUserIdpType());
        accountSSO.setCurrentLoa(authData.getUsrLoa());
        
        target = this.checkLogin(account.getLogin(), account.getPassword(), request, accountSSO, target);
        request.setAttribute("username", account.getLogin());
        request.setAttribute("password", account.getPassword());
      }
    } catch (Exception e) {
      LOGGER.error("Errore nella verifica dell'utente. Dati passati"
          + " -Nome: " + authData.getNome()
          + " -Cognome: " + authData.getCognome()
          + " -CodFiscale: " + authData.getCodiceFiscale()
          + " -Email: " + authData.getEmail()
          + " -LoA: " + authData.getUsrLoa()
          + " -IdPType: " + authData.getUserIdpType(), e);
      request.setAttribute("error", ERROR_MSG);
      this.aggiungiMessaggio(request, "errors.login.sso.noAutenticazione", authData.getCodiceFiscale());
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
    }
    
    if (LOGGER.isDebugEnabled()) LOGGER.debug("runAction: fine metodo");
    
    return mapping.findForward(target);
  }

}
