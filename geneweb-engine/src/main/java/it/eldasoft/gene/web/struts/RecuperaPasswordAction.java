/*
 * Created on 18/dic/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.utils.MailUtils;
import it.eldasoft.utils.mail.IMailSender;
import it.eldasoft.utils.mail.MailSenderException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ResourceBundle;

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
 * Viene inviata via mail la password richiesta; ovviamente le impostazioni dei parametri di posta devono essere
 * opportunamente configurate e verificate.
 *
 * @author Stefano.Sabbadin
 */
public class RecuperaPasswordAction extends Action {

  /** Logger Log4J di classe */
  private Logger         logger            = Logger.getLogger(RecuperaPasswordAction.class);

  /**
   * Resource bundle parte generale dell'applicazione (modulo GENE)
   */
  private ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  /** Manager per l'invio mail. */
  private MailManager    mailManager;
  
  /** Manager per la gestione degli account. */
  private AccountManager    accountManager;
  
  

  /**
   * @param mailManager
   *        mailManager da settare internamente alla classe.
   */
  public void setMailManager(MailManager mailManager) {
    this.mailManager = mailManager;
  }

  /**
   * @param accountManager
   *        accountManager da settare internamente alla classe.
   */
  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }

  
  /**
   * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String target = CostantiGeneraliStruts.FORWARD_OK;

    RecuperaPasswordForm recuperaPasswordForm = (RecuperaPasswordForm) form;
    Account account;
    logger.debug("passo 0"); 
    ICriptazioneByte criptatore;
      if (this.rpHash(recuperaPasswordForm.getCaptcha()).equals(recuperaPasswordForm.getCaptchaHash())) {
    	  // Recupero la username inserita dall'utente
    	  String username = recuperaPasswordForm.getUsername();
    	  logger.debug("passo 1"); 
        try {
        	criptatore = FactoryCriptazioneByte.getInstance(ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), username.getBytes(), ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
        	account = accountManager.getAccountByLogin(username);
        	//Se esiste l'utente invio la mail con la password
        	if (account != null) {
        	  logger.debug("passo 2"); 
        		if (account.getEmail() != null && account.getEmail().trim().length() > 0) {
        			IMailSender sender = MailUtils.getInstance(mailManager, ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE), CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD);
        			
                    String oggetto = resBundleGenerale.getString("recupera-password.mail.oggetto");
                    String testo = resBundleGenerale.getString("recupera-password.mail.testo");
                    criptatore = FactoryCriptazioneByte.getInstance(ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), account.getPassword().getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
                    String pswTemp = "";
                    final String caratteri = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
                    final SecureRandom rnd = new SecureRandom();

                     for( int i = 0; i < 8; i++ ) 
                       pswTemp = pswTemp + caratteri.charAt( rnd.nextInt(caratteri.length()) );
                     
                    //logger.debug("PASS PRECEDENTE = "+new String(criptatore.getDatoNonCifrato())); 
                    //logger.debug("PASS TEMPORANEA = "+pswTemp); 
                    testo = UtilityStringhe.replaceParametriMessageBundle(testo, new String[] {pswTemp});

                    accountManager.updatePasswordSenzaVecchia(account.getIdAccount(), pswTemp);
                    
                    sender.send(account.getEmail(), oggetto, testo);
                    if (account.getEmail().indexOf('@') != -1) {
                    	request.setAttribute("email", "************" + account.getEmail().substring(account.getEmail().indexOf('@')));
                    } else {
                    	request.setAttribute("email", account.getEmail());
                    }
                    
                    logger.info("Inviata mail di richiesta recupero password per la login \""
                            + username
                            + "\", all'indirizzo \""
                            + account.getEmail()
                            + "\"");
        		} else {
        			// Indirizzo di mail non presente
        			target = "captcha";
        			String chiave = "errors.recupera-password.nomail";
        	        ActionMessages errors = new ActionMessages();
        	        errors.add(ActionBase.getTipoMessaggioFromChiave(chiave), new ActionMessage(chiave));
        	        if (!errors.isEmpty()) this.addMessages(request, errors);
        		}
        	} else {
        		// Utente non presente
        		target = "captcha";
    			String chiave = "errors.recupera-password.nousername";
    	        ActionMessages errors = new ActionMessages();
    	        errors.add(ActionBase.getTipoMessaggioFromChiave(chiave), new ActionMessage(chiave));
    	        if (!errors.isEmpty()) this.addMessages(request, errors);
        	}
        } catch (MailSenderException ms) {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;

          // si traccia nel log l'errore tecnico rilevato
          /*String logMessageKey = ms.getChiaveResourceBundle();
          String logMessageError = this.resBundleGenerale.getString(logMessageKey);
          for (int i = 0; ms.getParametri() != null && i < ms.getParametri().length; i++)
            logMessageError = logMessageError.replaceAll(UtilityStringhe.getPatternParametroMessageBundle(i), (String) ms.getParametri()[i]);
          */
          logger.error(ms);

          // si traccia nel log e si manda a video il messaggio per l'utente
          String chiave = "errors.assistenza.mailSender";
          logger.error(resBundleGenerale.getString(chiave));
          ActionMessages errors = new ActionMessages();
          errors.add(ActionBase.getTipoMessaggioFromChiave(chiave), new ActionMessage(chiave));
          if (!errors.isEmpty()) this.addMessages(request, errors);
        }
        catch (Exception ex) {
        	target = "captcha";
			String chiave = "errors.recupera-password.nousername";
	        ActionMessages errors = new ActionMessages();
	        errors.add(ActionBase.getTipoMessaggioFromChiave(chiave), new ActionMessage(chiave));
	        if (!errors.isEmpty()) this.addMessages(request, errors);
	        logger.error(ex.getMessage(), ex);
  	    }
      } else {
        String chiave = "errors.captcha";
        ActionMessages errors = new ActionMessages();
        errors.add(ActionBase.getTipoMessaggioFromChiave(chiave), new ActionMessage(chiave));
        if (!errors.isEmpty()) this.addMessages(request, errors);
        recuperaPasswordForm.setCaptcha(null);
        target = "captcha";
      }
    return mapping.findForward(target);
  }

  /**
   * Calcola la hash per il controllo del captcha inserito.
   *
   * @param value
   *        valore da convertire
   * @return hash del valore
   */
  private String rpHash(String value) {
    int hash = 5381;
    value = value.toUpperCase();
    for (int i = 0; i < value.length(); i++) {
      hash = ((hash << 5) + hash) + value.charAt(i);
    }
    return String.valueOf(hash);
  }

 
}
