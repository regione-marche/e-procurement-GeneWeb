/*
 * Created on 02/feb/2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.system.mail;

import it.eldasoft.gene.bl.system.MailManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.system.ConfigurazioneMail;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action per il salvataggio dei parametri di configurazione della posta.
 *
 * @author Stefano.Sabbadin
 */
public class ImpostaPasswordConfigurazioneMailAction extends ActionBase {

  static Logger       logger = Logger.getLogger(ImpostaPasswordConfigurazioneMailAction.class);

  private MailManager mailManager;

  /**
   * @param mailManager
   *        mailManager da settare internamente alla classe.
   */
  public void setMailManager(MailManager mailManager) {
    this.mailManager = mailManager;
  }

  @Override
  protected String getOpzioneAcquistata() {

    return CostantiGenerali.OPZIONE_DEFAULT;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action di modifica password
   *
   * @return opzioni per accedere alla action
   */
  @Override
  public CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
  }

  @Override
  public ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    logger.debug("runAction: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    String oldPassword = StringUtils.stripToNull(request.getParameter("oldPassword"));
    String password = StringUtils.stripToNull(request.getParameter("password"));

    String paramIdcfg = request.getParameter("idcfg");
    String codapp = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);

    try {

      ConfigurazioneMail cfg = mailManager.getConfigurazione(codapp, paramIdcfg);
      String oldCryptedPassword = cfg.getPassword();

      String oldPasswordDB = null;
      if (oldCryptedPassword != null) {
        ICriptazioneByte decriptatore = FactoryCriptazioneByte.getInstance(
            ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
            oldCryptedPassword.getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
        oldPasswordDB = StringUtils.stripToNull(new String(
            decriptatore.getDatoNonCifrato()));
      }
      if ((oldPassword == null && oldPasswordDB == null)
          || (oldPassword != null && oldPassword.equals(oldPasswordDB))) {
        // se la vecchia password coincide con quella presente in db si procede
        // all'aggiornamento
        mailManager.updatePassword(password, codapp, paramIdcfg);
      } else {
        // altrimenti si segnala l'errore
        target = "errorModificaPassword";
        messageKey = "errors.chgPsw.errataVecchiaPassword";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }

    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (CriptazioneException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = e.getChiaveResourceBundle();
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }
}
