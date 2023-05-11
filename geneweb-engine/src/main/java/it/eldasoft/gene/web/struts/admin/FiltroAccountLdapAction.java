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
import it.eldasoft.gene.db.domain.admin.AccountLdap;
import it.eldasoft.gene.db.domain.system.ConfigurazioneLdap;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action di preparazione della lista degli account presa dal server LDAP
 * 
 * @author cit_defilippis
 */
public class FiltroAccountLdapAction extends AbstractActionBaseAdmin {

  /** Logger Log4J di classe */
  static Logger         logger = Logger.getLogger(FiltroAccountLdapAction.class);

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
    try {

      ConfigurazioneLdap server = ldapManager.getConfigurazione();

      FiltroAccountLdapForm filtroForm = (FiltroAccountLdapForm) request.getSession().getAttribute(
          CostantiDettaglioAccount.TROVA_ACCOUNT_LDAP);

      if (filtroForm == null) filtroForm = new FiltroAccountLdapForm();

      if (server.getFiltroOU() != null) {
        //se è stato settato un filtro per gli ou
        List<?> listaOU = ldapManager.getOrganizationalUnit(server.getFiltroOU());

        if (listaOU.size() > 0) {
          //se la lista degli ou non è vuota 
          listaOU = this.ordinaLista(listaOU);

          filtroForm.setListaValueOU(listaOU);
          filtroForm.setListaTextOU(listaOU);
        }
      }
      request.getSession().setAttribute(CostantiDettaglioAccount.TROVA_ACCOUNT_LDAP,
          filtroForm);

      request.setAttribute("filtroAccountLdapForm", filtroForm);

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

  private List<AccountLdap> ordinaLista(List<?> lista) {

    AccountLdap[] listaOrdinata = (AccountLdap[]) lista.toArray(new AccountLdap[0]);

    Arrays.sort(listaOrdinata);

    List<AccountLdap> listaResult = Arrays.asList(listaOrdinata);

    return listaResult;
  }
}