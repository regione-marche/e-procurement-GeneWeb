/*
 * Created on 04-mag-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.base.wizard;

import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.ListaForm;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicercheBase;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.campo.CampoRicercaForm;

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
 * Action per il salvataggio dei titoli del report base
 * 
 * @author Luca.Giacomazzo
 */
public class LayoutAction extends AbstractActionBaseGenRicercheBase {

  static Logger logger = Logger.getLogger(LayoutAction.class);
  
  
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, 
      HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {
    
    if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String pageFrom = request.getParameter("pageFrom"); 
    if(pageFrom != null && pageFrom.length() > 0){
      request.setAttribute("pageFrom", request.getParameter("pageFrom"));
      target += "Fine";
    }
    ListaForm listaTitoli = (ListaForm) form;
    
    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
         sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    
    CampoRicercaForm campoRicercaForm = null;
    for(int i=0; i < contenitore.getNumeroCampi(); i++){
      campoRicercaForm = (CampoRicercaForm) contenitore.getElencoCampi().get(i);
      campoRicercaForm.setTitoloColonna(listaTitoli.getId()[i]);
    }

    if(logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }
}