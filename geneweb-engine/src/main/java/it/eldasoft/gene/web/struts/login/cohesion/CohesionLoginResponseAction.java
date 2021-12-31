package it.eldasoft.gene.web.struts.login.cohesion;

import it.eldasoft.gene.bl.LoginManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioAccount;
import it.eldasoft.gene.web.struts.login.IsUserLoggedAction;
import it.eldasoft.gene.web.struts.login.cohesion.AccountCohesion;
import it.eldasoft.gene.web.struts.login.cohesion.error.AuthenticationException;
import it.eldasoft.gene.web.struts.login.cohesion.error.CohesionAuthenticationException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;

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
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.w3c.dom.Document;

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

  // private boolean forceLogout = false;

  private LoginManager loginManager;

  public void setLoginManager(LoginManager loginManager) {
    this.loginManager = loginManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    String target = CostantiGeneraliStruts.FORWARD_OK;

    AccountCohesion accountCohesion = null;

    // parso l'xml per recuperare la username del tipo che si e' autenticato

    String cohesionServlet = ConfigManager.getValore(CostantiGenerali.PROP_SSO_LOGIN_URL);
    cohesionServlet = UtilityStringhe.convertiNullInStringaVuota(cohesionServlet);

    String encryptionKey = ConfigManager.getValore(CostantiGenerali.PROP_SSO_COHESION_ENCRYPTION_KEY);

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

        String attributeKeyLogin = ConfigManager.getValore(CostantiGenerali.PROP_SSO_ATTRIBUTE_LOGIN);
        String attributeKeyFirstName = ConfigManager.getValore(CostantiGenerali.PROP_SSO_ATTRIBUTE_FIRST_NAME);
        String attributeKeyLastName = ConfigManager.getValore(CostantiGenerali.PROP_SSO_ATTRIBUTE_LAST_NAME);
        String attributeKeyMail = ConfigManager.getValore(CostantiGenerali.PROP_SSO_ATTRIBUTE_EMAIL);

        // lettura dei dati ricevuti e significativi
        Document cohesionResponseXML = getXmlDocFromString(cohesionResponse);
        cohesionResponseXML.getDocumentElement().normalize();
        String login = cohesionResponseXML.getElementsByTagName(attributeKeyLogin).item(0).getTextContent();
        String nome = cohesionResponseXML.getElementsByTagName(attributeKeyFirstName).item(0).getTextContent();
        String cognome = cohesionResponseXML.getElementsByTagName(attributeKeyLastName).item(0).getTextContent();
        String email = cohesionResponseXML.getElementsByTagName(attributeKeyMail).item(0).getTextContent();
        String tipoAutenticazione = cohesionResponseXML.getElementsByTagName("tipo_autenticazione").item(0).getTextContent();

        // l'utente e' gia' presente e abilitato, quindi lo mando alla homepase dell'applicazione,
        // prima pero' setto l'attributo accountCohesion in sessione
        accountCohesion = new AccountCohesion();
        accountCohesion.setLogin(login);
        accountCohesion.setNome(nome);
        accountCohesion.setCognome(cognome);
        accountCohesion.setEmail(email);
        accountCohesion.setTipoAutenticazione(tipoAutenticazione);
        accountCohesion.setCohesionLogin(true);

        Account account = this.loginManager.getAccountByLogin(login);
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
            String messageKey = "errors.login.unknown";
            this.aggiungiMessaggio(request, messageKey, login);
          }
        } else if (account != null && (account.getUtenteDisabilitato() != null && CostantiDettaglioAccount.DISABILITATO.equals(account.getUtenteDisabilitato().toString()))) {
            throw new AuthenticationException(AuthenticationException.USER_NOT_ACTIVE);
        } else {

          request.getSession().setAttribute(AccountCohesion.ID_ATTRIBUTO_SESSIONE_ACCOUNT_COHESION,
              accountCohesion);

          request.setAttribute("username", account.getLogin());
          request.setAttribute("password", account.getPassword());

        }
      } else {
        if (request.getSession().getAttribute(AccountCohesion.ID_ATTRIBUTO_SESSIONE_COHESION_TOKEN) == null) {
          try {
            response.sendRedirect(cohesionServlet);
          } catch (IOException ex) {
            throw new CohesionAuthenticationException(CohesionAuthenticationException.GENERIC_ERROR);
          }
        }
      }
    } catch (Exception ex) {
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

}