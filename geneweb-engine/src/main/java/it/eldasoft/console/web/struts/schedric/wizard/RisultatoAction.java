/*
 * Created on 26-apr-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.console.web.struts.schedric.wizard;

import it.eldasoft.console.web.struts.schedric.CostantiSchedRic;
import it.eldasoft.console.web.struts.schedric.SchedRicForm;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * DispatchAction per la memorizzazione in sessione dell'argomento della ricerca
 * base
 * 
 * @author Luca.Giacomazzo
 */
public class RisultatoAction extends DispatchActionBaseNoOpzioni {

  private final String SUCCESS_SALVA = "successSalva";
  
  static Logger logger = Logger.getLogger(RisultatoAction.class);
  
  /**
   * Metodo per il salvataggio del report a cui verra pianificata la schedulazione
   * 
   * il metodo setta nel request il form con le modifiche effettuate nella 
   * pagina di scelta del report
   *  
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward salvaRisultato(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("salvaRisultato: inizio metodo");

    // target di default
    String target = SUCCESS_SALVA;

    SchedRicForm schedRicForm = (SchedRicForm) form;

    
    request.setAttribute("schedRicForm",schedRicForm);
    
    HttpSession sessione = request.getSession();
    sessione.setAttribute(CostantiSchedRic.OGGETTO_DETTAGLIO,schedRicForm);

    if (logger.isDebugEnabled()) logger.debug("salvaRisultato: fine metodo");
    return mapping.findForward(target);
  }

 
}