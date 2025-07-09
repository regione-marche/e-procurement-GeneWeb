package it.eldasoft.gene.web.struts.login.cohesion;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.w3c.dom.Document;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.AccountSSO;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioAccount;
import it.eldasoft.gene.web.struts.login.IsUserLoggedAction;
import it.eldasoft.gene.web.struts.login.cohesion.error.AuthenticationException;
import it.eldasoft.gene.web.struts.login.cohesion.error.CohesionAuthenticationException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;

/**
 * Action per la gestione della risposta di Cohesion alla login
 *
 * @author Luca.Giacomazzo
 */
public class CohesionLoginResponseAction extends IsUserLoggedAction {

  /**
   * Logger di classe.
   */
  private static Logger logger = Logger.getLogger(CohesionLoginResponseAction.class);
  
  /** property per decriptare il token per la autenticazione SSO Cohesion */
  private static final String   PROP_SSO_COHESION_ENCRYPTION_KEY                   = "auth.sso.cohesion.encryption.key";
  private static final String   PROP_SSO_COHESION_LOGIN_URL                        = "auth.sso.cohesion.login.url"; 
  private static final String   PROP_SSO_COHESION_MAPPING_LASTNAME                 = "auth.sso.cohesion.mapping.lastName";
  private static final String   PROP_SSO_COHESION_MAPPING_FIRSTNAME                = "auth.sso.cohesion.mapping.firstName";
  private static final String   PROP_SSO_COHESION_MAPPING_EMAIL                    = "auth.sso.cohesion.mapping.email";
  private static final String   PROP_SSO_COHESION_MAPPING_LOGIN                    = "auth.sso.cohesion.mapping.login"; 
  private static final String   COHESION_TOKEN                                     = "cohesion_token";

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    String target = CostantiGeneraliStruts.FORWARD_OK;

    AccountSSO accountSSO = null;

    // parso l'xml per recuperare la username del tipo che si e' autenticato

    String cohesionServlet = ConfigManager.getValore(PROP_SSO_COHESION_LOGIN_URL);
    cohesionServlet = UtilityStringhe.convertiNullInStringaVuota(cohesionServlet);

    String encryptionKey = ConfigManager.getValore(PROP_SSO_COHESION_ENCRYPTION_KEY);

    try {
      //token della richiesta di autenticazione in cohesion
      String token = StringEscapeUtils.unescapeXml(request.getParameter("token"));

      if (token != null) {
        String cohesionResponse = new String(base64Decode(token));
        if (!"".equals(encryptionKey)) {
          try {
            cohesionResponse = new String(cipher3DES(false, base64Decode(token), encryptionKey.getBytes()));
          } catch (Exception ex) {
            throw new CohesionAuthenticationException(CohesionAuthenticationException.GENERIC_ERROR);
          }
        }

        String attributeKeyLogin = ConfigManager.getValore(PROP_SSO_COHESION_MAPPING_LOGIN);
        String attributeKeyFirstName = ConfigManager.getValore(PROP_SSO_COHESION_MAPPING_FIRSTNAME);
        String attributeKeyLastName = ConfigManager.getValore(PROP_SSO_COHESION_MAPPING_LASTNAME);
        String attributeKeyMail = ConfigManager.getValore(PROP_SSO_COHESION_MAPPING_EMAIL);

        // lettura dei dati ricevuti e significativi
        Document cohesionResponseXML = getXmlDocFromString(cohesionResponse);
        cohesionResponseXML.getDocumentElement().normalize();
        String login = getCohesionElement(cohesionResponseXML, attributeKeyLogin);
        String nome = getCohesionElement(cohesionResponseXML, attributeKeyFirstName);
        String cognome = getCohesionElement(cohesionResponseXML, attributeKeyLastName);
        String email = getCohesionElement(cohesionResponseXML, attributeKeyMail);
        
        if("true".equals((String) request.getSession().getAttribute(CostantiGenerali.PAIRING_SSO))) {//pairing utenza base con utenzaSSO
          return mapping.findForward(this.pairUtenteLdap0WithSSO(request, login, CostantiGenerali.PROP_SSO_COHESION));
        }
        
        String standardProvider = null;
        Integer loa = 2;
        //recupero info di autenticazione
        try {
          String base64info = getCohesionElement(cohesionResponseXML,"samlResponseBase64");
          String info =  new String(base64Decode(base64info));
          Document infoXML = getXmlDocFromString(info);
          infoXML.getDocumentElement().normalize();
          
          String saml = "saml2:";
          if (getCohesionElement(infoXML, saml + "Assertion") == null) {
            // per autenticazione SPID e CIE si riceve un inner xml con namespace saml2, mentre per utente/psw si riceve saml
            saml = "saml:";
          }
          String issuer=getCohesionElement(infoXML, saml + "Issuer");
          
          if(StringUtils.isNotBlank(issuer)) {
            String authnContextClassRef = getCohesionElement(infoXML, saml + "AuthnContextClassRef");
            if ("cohesion2.regione.marche.it:idp".equals(issuer)) {
              // autenticazione user+psw, eventualmente con PIN oppure OTP
              if ("urn:oasis:names:tc:SAML:2.0:ac:classes:Smartcard".equals(authnContextClassRef)) {
                // autenticazione con PIN oppure OTP viene considerata L2 SPID (LoA3)
                loa = 3;
              } else if ("urn:oasis:names:tc:SAML:2.0:ac:classes:Password".equals(authnContextClassRef)) {
                // autenticazione con PIN oppure OTP viene considerata L1 SPID (LoA2)
                loa = 2;
              } else if ("urn:oasis:names:tc:SAML:2.0:ac:classes:Kerberos".equals(authnContextClassRef)) {
                // autenticazione di DOMINIO, viene considerata L1 SPID (LoA2)
                loa = 2;
              }
            } else if ("https://idserver.servizicie.interno.gov.it/idp/profile/SAML2/POST/SSO".equals(issuer)) {
              // autenticazione CIE
              standardProvider=CostantiGenerali.PROP_SSO_CIE;
              loa=Integer.parseInt(authnContextClassRef.split("SpidL")[1])+1;
            } else {
              // autenticazione SPID
              standardProvider=CostantiGenerali.PROP_SSO_SPID;
              loa=Integer.parseInt(authnContextClassRef.split("SpidL")[1])+1;
            }
          }
            
        } catch (Exception ex) {
          throw new CohesionAuthenticationException(CohesionAuthenticationException.GENERIC_ERROR);
        }
        
		logger.info("Autenticazione Cohesion: login=" + login + ", nome=" + nome + ", cognome=" + cognome + ", email="
				+ email);

        // l'utente e' gia' presente e abilitato, quindi lo mando alla homepase dell'applicazione,
        // prima pero' setto l'attributo accountCohesion in sessione
		accountSSO = new AccountSSO();
        accountSSO.setLogin(login);
        accountSSO.setNome(nome);
        accountSSO.setCognome(cognome);
        accountSSO.setEmail(email);
        accountSSO.setLoginProvider(CostantiGenerali.PROP_SSO_COHESION);
        accountSSO.setStandardProvider(standardProvider);
        accountSSO.setCurrentLoa(loa);

        Account account = this.accountManager.getAccountBySysCf(login);
        if (account == null) {
          //Settaggio dei vari parametri nella form di registrazione
          request.setAttribute("nome", nome);
          request.setAttribute("cognome", cognome);
          request.setAttribute("login", login);
          request.setAttribute("codfisc", login);
          request.setAttribute("email", email);
          //Attributo che in questo caso indica presenza di integrazione SSO
          request.setAttribute("flagLdap", "3");
          if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_ATTIVA_FORM_REGISTRAZIONE))) {
            return this.forwardToRegistrationForm(request);
          }else{
            target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
            String messageKey = "errors.login.unknownUser";
            this.aggiungiMessaggio(request, messageKey);
          }
        } else if (account != null && (account.getUtenteDisabilitato() != null && CostantiDettaglioAccount.DISABILITATO.equals(account.getUtenteDisabilitato().toString()))) {
            throw new AuthenticationException(AuthenticationException.USER_NOT_ACTIVE);
        } else {

          request.getSession().setAttribute(CostantiGenerali.ID_ATTRIBUTO_SESSIONE_ACCOUNT_SSO,
              accountSSO);

          request.setAttribute("username", account.getLogin());
          request.setAttribute("password", account.getPassword());

        }
      } else {
        if (request.getSession().getAttribute(COHESION_TOKEN) == null) {
          try {
            response.sendRedirect(cohesionServlet);
          } catch (IOException ex) {
            throw new CohesionAuthenticationException(CohesionAuthenticationException.GENERIC_ERROR);
          }
        }
      }
    } catch (Exception ex) {
    	logger.error("Errore durante l'autenticazione Cohesion", ex);
      if (ex instanceof RemoteException && ((RemoteException) ex).getMessage().equals("USER-UNKNOWN")) {
        //this.addActionError(this.getText("errors.login.unknownUser"));
        this.aggiungiMessaggio(request, "errors.login.unknownUser");
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE; //ERROR;
      } else if (ex instanceof RemoteException && ((RemoteException) ex).getMessage().equals("USER-DISABLED")) {
        //this.addActionError(this.getText("errors.login.suspendedUser"));
        this.aggiungiMessaggio(request, "errors.login.suspendedUser");
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE; //ERROR;
      } else if (ex instanceof RemoteException && ((RemoteException) ex).getMessage().equals("NO-PROFILE")) {
        //this.addActionError(this.getText("errors.login.unknownUserForApp"));
        this.aggiungiMessaggio(request, "errors.login.unknownUserForApp");
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE; //ERROR;
      } else if (ex instanceof AuthenticationException && ((AuthenticationException) ex).getMessage().equals(AuthenticationException.USER_NOT_ACTIVE)) {
        //this.addActionError(this.getText("errors.login.userNotActive"));
        this.aggiungiMessaggio(request, "errors.login.cohesion.userNotActive");
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;

      //} else if (ex instanceof CohesionAuthenticationException && ((CohesionAuthenticationException) ex).getMessage().equals(CohesionAuthenticationException.WEAK_PWD_ERROR)) {
        //this.addActionError(this.getText("errors.login.cohesion.weakAuthentication"));
      //  this.aggiungiMessaggio(request, "errors.login.cohesion.weakAuthentication");
      //  this.forceLogout = true;
      // In questo caso servirebbe mettere nel request il valore della property contenente
      // l'URL di Cohesion in caso di logout.
      //  target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE; //ERROR;

      } else if (ex instanceof CohesionAuthenticationException && ((CohesionAuthenticationException) ex).getMessage().equals(CohesionAuthenticationException.GENERIC_ERROR)) {
        this.aggiungiMessaggio(request, "errors.login.cohesion.errorAuthentication");
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE; //ERROR;
      } else {
        this.aggiungiMessaggio(request, "errors.login.unknownError");
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE; //ERROR;
      }
    }

    return mapping.findForward(target);
  }

  protected static byte[] base64Decode(String data) {
    byte[] result = new byte[0];
    try {
      result = DatatypeConverter.parseBase64Binary(data);
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
    }
    return result;
  }

  private static Document getXmlDocFromString(String xml) {

    Document xmlDoc = null;
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      xmlDoc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
    }
    return xmlDoc;
  }

  private static byte[] cipher3DES(boolean encrypt, byte[] message, byte[] key) throws Exception {

    byte[] cipher = new byte[0];
    try {
      if (key.length != 24) {
        throw new Exception("key size must be 24 bytes");
      }
      int cipherMode = Cipher.DECRYPT_MODE;
      if (encrypt) {
        cipherMode = Cipher.ENCRYPT_MODE;
      }
      Cipher sendCipher = Cipher.getInstance("DESede/ECB/NoPadding");
      SecretKey myKey = new SecretKeySpec(key, "DESede");
      sendCipher.init(cipherMode, myKey);
      cipher = sendCipher.doFinal(message);
    } catch (Exception ex) {
      logger.error(ex.getMessage(), ex);
    }
    return cipher;
  }
  
	private String getCohesionElement(Document doc, String name) {
		String value = null;
		try {
			value = StringUtils.stripToNull(doc.getElementsByTagName(name)
					.item(0).getTextContent());
		} catch (Exception e) {
			value = null;
		}
		return value;
	}


}