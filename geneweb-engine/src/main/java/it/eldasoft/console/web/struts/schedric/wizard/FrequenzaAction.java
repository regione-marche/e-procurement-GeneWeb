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
 * DispatchAction per la memorizzazione in request della frequenza della schedulazioni
 * 
 * @author Francesco De Filippis
 */
public class FrequenzaAction extends DispatchActionBaseNoOpzioni {

  private final String SUCCESS_SALVA = "successSalva";
  private final String SUCCESS_GIORNO = "successGiorno";
  private final String SUCCESS_SETTIMANA = "successSettimana";
  private final String SUCCESS_MESE = "successMese";
  private final String SUCCESS_UNICA = "successUnica";
  private final String SUCCESS_RISULTATO = "successRisultato";
  
  static Logger logger = Logger.getLogger(FrequenzaAction.class);
  
  /**
   * Metodo per il salvataggio della frequenza con cui verra pianificata la schedulazione
   * 
   * il metodo setta nel request il form con le modifiche effettuate nella 
   * pagina e gestisce in base al valore del tipo di schedulazione il forward 
   *  
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward salvaFrequenza(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("salvaFrequenza: inizio metodo");

    // target di default
    String target = SUCCESS_SALVA;

    SchedRicForm schedRicForm = (SchedRicForm) form;
    HttpSession session = request.getSession();
    SchedRicForm schedRicSessionForm = (SchedRicForm)
            session.getAttribute(CostantiSchedRic.OGGETTO_DETTAGLIO);
    
    if (! schedRicSessionForm.getTipo().equals(schedRicForm.getTipo()) ) {
      this.sbiancaPeriodicita(schedRicForm);
    }
    
    if (CostantiSchedRic.GIORNO.equalsIgnoreCase(schedRicForm.getTipo())) {
      target = SUCCESS_GIORNO;
    } else if (CostantiSchedRic.SETTIMANA.equalsIgnoreCase(schedRicForm.getTipo())){
      target = SUCCESS_SETTIMANA;
    } else if (CostantiSchedRic.MESE.equalsIgnoreCase(schedRicForm.getTipo())){
      target = SUCCESS_MESE;
    } else if (CostantiSchedRic.UNICA.equalsIgnoreCase(schedRicForm.getTipo())){
      target = SUCCESS_UNICA;
    }
    
    request.setAttribute("schedRicForm",schedRicForm);
    HttpSession sessione = request.getSession();
    sessione.setAttribute(CostantiSchedRic.OGGETTO_DETTAGLIO,schedRicForm);

    if (logger.isDebugEnabled()) logger.debug("salvaFrequenza: fine metodo");
    return mapping.findForward(target);
  }
  
  /**
   * Metodo per il salvataggio della frequenza con cui verra pianificata la schedulazione
   * 
   * il metodo setta nel request il form con le modifiche effettuate nella 
   * pagina e gestisce in base al valore del tipo di schedulazione il forward 
   *  
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward salvaFrequenzaScelta(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("salvaFrequenzaScelta: inizio metodo");

    // target di default
    String target = SUCCESS_RISULTATO;

    SchedRicForm schedRicForm = (SchedRicForm) form;
    
    
    if (CostantiSchedRic.GIORNO.equalsIgnoreCase(schedRicForm.getTipo())) {
      request.setAttribute("pageFrom",CostantiWizard.CODICE_PAGINA_GIORNO);
    } else if (CostantiSchedRic.SETTIMANA.equalsIgnoreCase(schedRicForm.getTipo())){
      request.setAttribute("pageFrom",CostantiWizard.CODICE_PAGINA_SETTIMANA);
    } else if (CostantiSchedRic.MESE.equalsIgnoreCase(schedRicForm.getTipo())){
      request.setAttribute("pageFrom",CostantiWizard.CODICE_PAGINA_MESE);
    } else if (CostantiSchedRic.UNICA.equalsIgnoreCase(schedRicForm.getTipo())){
      request.setAttribute("pageFrom",CostantiWizard.CODICE_PAGINA_UNICA);
    }
    
    request.setAttribute("schedRicForm",schedRicForm);
    HttpSession sessione = request.getSession();
    sessione.setAttribute(CostantiSchedRic.OGGETTO_DETTAGLIO,schedRicForm);

    if (logger.isDebugEnabled()) logger.debug("salvaFrequenzaScelta: fine metodo");
    return mapping.findForward(target);
  }
  
 
  private void sbiancaPeriodicita(SchedRicForm schedRicForm) {
    
    schedRicForm.setDataPrimaEsec("");
    schedRicForm.setGiorniMese("");
    schedRicForm.setGiorniSettimana("");
    schedRicForm.setGiorno(new Integer(0));
    schedRicForm.setMinutoAvvio(null);
    schedRicForm.setOpzioneAgosto(false);
    schedRicForm.setOpzioneAprile(false);
    schedRicForm.setOpzioneDicembre(false);
    schedRicForm.setOpzioneDomenica(false);
    schedRicForm.setOpzioneFebbraio(false);
    schedRicForm.setOpzioneGennaio(false);
    schedRicForm.setOpzioneGiovedi(false);
    schedRicForm.setOpzioneGiugno(false);
    schedRicForm.setOpzioneLuglio(false);
    schedRicForm.setOpzioneLunedi(false);
    schedRicForm.setOpzioneMaggio(false);
    schedRicForm.setOpzioneMartedi(false);
    schedRicForm.setOpzioneMarzo(false);
    schedRicForm.setOpzioneMercoledi(false);
    schedRicForm.setOpzioneNovembre(false);
    schedRicForm.setOpzioneOttobre(false);
    schedRicForm.setOpzioneSabato(false);
    schedRicForm.setOpzioneSettembre(false);
    schedRicForm.setOpzioneVenerdi(false);
    schedRicForm.setOraAvvio(null);
    schedRicForm.setRadioGiorno(0);
    schedRicForm.setSettimana(new Integer(0));
    
  }
}