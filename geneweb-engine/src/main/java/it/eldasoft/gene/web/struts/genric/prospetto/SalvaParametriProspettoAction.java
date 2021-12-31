/*
 * Created on 19-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.prospetto;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.web.struts.genmod.SalvaParametriModelloAction;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Ridefinizione della Action SalvaParametriModelloAction per il salvataggio dei
 * parametri necessari all'esecuzione della ricerca con modello, cioè i 
 * parametri per la composizione del modello associato.
 * 
 * @author Luca.Giacomazzo
 */
public class SalvaParametriProspettoAction extends SalvaParametriModelloAction {

  public ActionForward salvaEComponiModello(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    
    //Disabilito la navigazione dei vari menu'
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);
    ActionForward actForward = super.salvaEComponiModello(mapping, form, request, response);
    
    ParametriProspettoForm parametriProspettoForm = (ParametriProspettoForm) form; 
    
    if(request.getAttribute("componiModelloForm") != null){
      Object tmp = request.getAttribute("componiModelloForm");
      request.removeAttribute("componiModelloForm");
      request.setAttribute("componiModelloConIdUtenteForm", tmp);
    }
    
    request.setAttribute("idProspetto", parametriProspettoForm.getIdProspetto());
    return actForward;
  }

}