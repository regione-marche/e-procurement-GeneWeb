/*
 * Created on 10-dic-2007
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.risultato;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per la stampa del risultato di una ricerca
 * 
 * @author Luca.Giacomazzo
 */
public class StampaRisultatoRicercaAction extends EstraiRicercaAction {
  
  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(StampaRisultatoRicercaAction.class);
  
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    
    if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    // Set nel request di un attributo che specifica al metodo runAction della
    // classe EstraiRicercaAction di non considerare il numero di record per
    // pagina impostato nel report
    request.setAttribute("stampaReport", "Si");
    
    if(logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return super.runAction(mapping, form, request, response);
  }

}