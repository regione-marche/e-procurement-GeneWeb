/*
 * Created on 26-feb-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.layout;

import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.ListaForm;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.CambiaTabAction;
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
 * Azione che gestisce il salvataggio in sessione dei nuovi titoli dei campi 
 * della ricerca dopo l'edit del client
 * 
 * @author Luca.Giacomazzo
 */
public class SalvaTitoliCampiRicercaAction extends AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(SalvaTitoliCampiRicercaAction.class);

  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    
    String target = CostantiGeneraliStruts.FORWARD_OK;
    ListaForm listaTitoli = (ListaForm) form;
    
    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
         sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    
    CampoRicercaForm campoRicercaForm = null;
    for(int i=0; i < contenitore.getNumeroCampi(); i++){
      campoRicercaForm = (CampoRicercaForm) contenitore.getElencoCampi().get(i);
      campoRicercaForm.setTitoloColonna(listaTitoli.getId()[i]);
    }

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    request.setAttribute("tab", CambiaTabAction.CODICE_TAB_LAYOUT);

    // Si cambia target se il report e' un report base
    if(CostantiGenRicerche.REPORT_BASE == contenitore.getTestata().getFamiglia().intValue())
      target = target.concat("Base");

    if(logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

}