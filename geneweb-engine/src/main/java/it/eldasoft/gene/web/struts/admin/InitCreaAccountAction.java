/*
 * Created on 02-apr-2007
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per accesso alla pagina creanNuovaRicerca.jsp per la scelta del tipo
 * di ricerca da creare.
 * 
 * @author Luca.Giacomazzo
 */
public class InitCreaAccountAction extends AbstractActionBaseAdmin {

  private static final String SUCCESS_SCELTA_NUOVO_ACCOUNT = "successScelta";
  private static final String SUCCESS_NUOVO_ACCOUNT        = "successAccount";

  /** Logger Log4J di classe */
  static Logger               logger                       = Logger.getLogger(InitCreaAccountAction.class);


  /**
   * Funzione che restituisce le opzioni per accedere alla action visualizza
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_ADMIN);
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = "";

    ServletContext context = request.getSession().getServletContext();
    Collection<String> opzioni = Arrays.asList((String[]) context.getAttribute(CostantiGenerali.OPZIONI_DISPONIBILI));

    if (opzioni.contains(CostantiGenerali.OPZIONE_ADMIN_LDAP)) {
      target = SUCCESS_SCELTA_NUOVO_ACCOUNT;
      // set nel request del parameter per disabilitare la navigazione
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);
    } else {
      target = SUCCESS_NUOVO_ACCOUNT;
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

}