/*
 * Created on 20 - Feb - 2007
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
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBase;
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
 * Action di gestione dell'account: crea un nuovo Account (form), carica il form
 * dell'Account, elimina un Account ed elimina gli Account selezionati
 * 
 * @author cit_defilippis
 */
public class ConfigurazioneLdapAction extends DispatchActionBase {

  static Logger                logger                       = Logger.getLogger(ConfigurazioneLdapAction.class);

  private static final String FORWARD_SUCCESS_SOLA_LETTURA      = "successVisualizza";
  private static final String FORWARD_SUCCESS_MODIFICA          = "successModifica";
  private static final String FORWARD_SUCCESS_MODIFICA_PASSWORD = "successModificaPassword";

  private LdapManager        ldapManager;

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
   * Funzione che restituisce le opzioni per accedere alla action visualizza
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniVisualizza() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward visualizza(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("visualizza: inizio metodo");
    String target = FORWARD_SUCCESS_SOLA_LETTURA;

    target = getConfig(target, request); //, "visualizza");

    if (logger.isDebugEnabled()) logger.debug("visualizza: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action modifica
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModifica() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward modifica(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("modifica: inizio metodo");
    String target = FORWARD_SUCCESS_MODIFICA;

    target = getConfig(target, request); //, "modifica");

    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);

    if (logger.isDebugEnabled()) logger.debug("modifica: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action di modifica
   * password
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModificaPassword() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward modificaPassword(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    return mapping.findForward(FORWARD_SUCCESS_MODIFICA_PASSWORD);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * verificaConnessione
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniVerificaConnessione() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward verificaConnessione(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled())
      logger.debug("verificaConnessione: inizio metodo");

    String target = FORWARD_SUCCESS_SOLA_LETTURA;

    String messageKey = null;
    try {

      ldapManager.connettiServer();

    } catch (DataAccessException e) {
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      messageKey = "errors.ldap.connessioneFallita";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    // non è andato in errore significa che ha connesso il server senza problemi
    if (messageKey == null) {
      messageKey = "info.ldap.connessioneAvvenuta";
      logger.info(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    }

    target = getConfig(target, request); //, "visualizza");

    if (logger.isDebugEnabled())
      logger.debug("verificaConnessione: fine metodo");

    return mapping.findForward(target);
  }

  private String getConfig(String target, HttpServletRequest request) {
    String messageKey = null;
    try {

      ConfigurazioneLdap server = ldapManager.getConfigurazione();
      // indico che la password esiste, ma non la passo nel form per non
      // mandarla erroneamente nell'HTML
      if (server.getPassword() != null) server.setPassword("IMPOSTATA");

      ConfigurazioneLdapForm serverForm = new ConfigurazioneLdapForm(server);

      request.setAttribute("cfgLdapForm", serverForm);
      //request.setAttribute("metodo", metodo);

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
    return target;
  }

}
