/*
 * Created on 06-dic-2007
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
 * Action per il controllo della definizione di una ricerca base
 * 
 * @author Luca.Giacomazzo
 */
public class ControllaDatiRicercaBaseAction extends ControllaDatiRicercaAction{

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(ControllaDatiRicercaAction.class);
  
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    ActionForward actForward = super.runAction(mapping, form, request, response);  
    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    
    return actForward; 
  }
}