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
package it.eldasoft.gene.web.struts.system.assistenza;

import it.eldasoft.gene.bl.system.RichiestaAssistenzaManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.spring.SpringAppContext;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action per il salvataggio dei parametri di configurazione della funzionalit&agrave; di richiesta assistenza.
 *
 * @author Marco.Perazzetta
 */
public class SalvaConfigurazioneRichiestaAssistenzaAction extends ActionBase {

  static Logger                      logger = Logger.getLogger(SalvaConfigurazioneRichiestaAssistenzaAction.class);

  private RichiestaAssistenzaManager richiestaAssistenzaManager;

  /**
   * @param richiestaAssistenzaManager
   *        richiestaAssistenzaManager da settare internamente alla classe.
   */
  public void setRichiestaAssistenzaManager(RichiestaAssistenzaManager richiestaAssistenzaManager) {
    this.richiestaAssistenzaManager = richiestaAssistenzaManager;
  }

  @Override
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_DEFAULT;
  }

  /**
   * Restituisce le opzioni per accedere alla action salva.
   *
   * @return opzioni per accedere alla action
   */
  @Override
  public CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
  }

  @Override
  public ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    logger.debug("runAction: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;
    ConfigurazioneRichiestaAssistenzaForm cfgForm = (ConfigurazioneRichiestaAssistenzaForm) form;
    try {
      richiestaAssistenzaManager.updateConfigurazione(cfgForm.getDatiPerModel());
    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }
    if (CostantiGeneraliStruts.FORWARD_OK.equals(target)) {
      ServletContext context = SpringAppContext.getServletContext();
      context.setAttribute(CostantiGenerali.ATTR_ATTIVA_FORM_ASSISTENZA, cfgForm.getModo());
    }
    logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }

}
