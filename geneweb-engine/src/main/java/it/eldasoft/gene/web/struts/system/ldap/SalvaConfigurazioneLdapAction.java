/*
 * Created on Oct 19, 2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.system.ldap;

import it.eldasoft.gene.bl.system.LdapManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.system.ConfigurazioneLdap;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * @author cit_defilippis
 * 
 */
public class SalvaConfigurazioneLdapAction extends ActionBase {

  static Logger                logger                   = Logger.getLogger(SalvaConfigurazioneLdapAction.class);

  private static final String  FORWARD_SUCCESS          = "success";

  protected LdapManager        ldapManager;

  /**
   * @param propsConfigManager
   *        The propsConfigManager to set.
   */
  public void setLdapManager(LdapManager ldapManager) {
    this.ldapManager = ldapManager;
  }

  protected String getOpzioneAcquistata() {

    return CostantiGenerali.OPZIONE_ADMIN_LDAP;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action modifica
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
  }

  public ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    logger.debug("runAction: inizio metodo");
    String target = SalvaConfigurazioneLdapAction.FORWARD_SUCCESS;

    String messageKey = null;
    ConfigurazioneLdapForm serverForm = (ConfigurazioneLdapForm) form;

    try {
      ldapManager.updateConfigurazione(serverForm.getDatiPerModel());

      ConfigurazioneLdap server = ldapManager.getConfigurazione();

      serverForm = new ConfigurazioneLdapForm(server);
      request.setAttribute("cfgLdapForm", serverForm);
      request.setAttribute("metodo", "visualizza");

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

    logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }
}
