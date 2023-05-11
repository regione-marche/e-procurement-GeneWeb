/*
 * Created on 24-lug-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.campo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;

/**
 * Inizializzazione della popup Trova campo 
 * 
 * @author Luca.Giacomazzo
 */
public class InitTrovaCampoRicercaAction extends AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(InitTrovaCampoRicercaAction.class);

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    
    if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    
    String target = CostantiGeneraliStruts.FORWARD_OK;

    // lista per il popolamento della comboBox dei valori della combobox di
    // confronto fra stringhe
    request.setAttribute("listaValueConfrontoStringa",
        GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA);
    // lista per il popolamento della comboBox dei testi della combobox di
    // confronto fra stringhe
    request.setAttribute("listaTextConfrontoStringa", 
        GestioneOperatoreConfrontoStringa.CBX_TESTO_CONFRONTO_STRINGA);
    
    if (request.getAttribute("campoRicercaForm") == null)
      request.setAttribute("campoRicercaForm", new CampoRicercaForm());
    
    
    if(logger.isDebugEnabled()) logger.debug("runAction: fine metodo");    
    return mapping.findForward(target);
  }

}