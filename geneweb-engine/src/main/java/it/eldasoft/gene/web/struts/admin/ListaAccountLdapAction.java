/*
 * Created on 02-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.system.LdapManager;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;
import org.springframework.ldap.LimitExceededException;

/**
 * Action di preparazione della lista degli account presa dal server LDAP
 * 
 * @author cit_defilippis
 */
public class ListaAccountLdapAction extends AbstractActionBaseAdmin {

  /** Logger Log4J di classe */
  static Logger         logger      = Logger.getLogger(ListaAccountLdapAction.class);

  private static String ERROR_LIMIT = "erroreLimite";

  protected LdapManager ldapManager;

  /**
   * @param ldapManager
   *        The ldapManager to set.
   */
  public void setLdapManager(LdapManager ldapManager) {
    this.ldapManager = ldapManager;
  }

  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    FiltroAccountLdapForm filtroAccountForm = (FiltroAccountLdapForm) form;

    request.getSession().setAttribute(CostantiDettaglioAccount.TROVA_ACCOUNT_LDAP,
        filtroAccountForm);

    List<?> listaAccountByCategory = null;

    try {
      // inserisco le wildcard per la ricerca se non sono già state inserite per
      // filtro vuoto metto un solo *
      String cn = filtroAccountForm.getFiltroCn();
      if (cn.indexOf("*") <= 0) {
        if (cn.equals(""))
          cn = "*";
        else
          cn = "*" + cn + "*";
      }
      // se sono valorizzati tutti e due i filtri eseguo una ricerca completa
      if (filtroAccountForm.getFiltroOU().equals(""))
        listaAccountByCategory = ldapManager.getAccountLdap(cn);
      else
        listaAccountByCategory = ldapManager.getAccountLdap(cn,
            filtroAccountForm.getFiltroOU());

      if (listaAccountByCategory.size() == 0) {
        target = CostantiGeneraliStruts.FORWARD_NESSUN_RECORD_TROVATO;
        messageKey = "info.search.noRecordFound";
        this.aggiungiMessaggio(request, messageKey);
      } else {
        request.setAttribute("listaAccountLdap", listaAccountByCategory);
      }

    } catch (LimitExceededException e) {
      target = ERROR_LIMIT;
      messageKey = "errors.ldap.limiteElementiSuperato";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.ldap.connessioneFallita";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }

}