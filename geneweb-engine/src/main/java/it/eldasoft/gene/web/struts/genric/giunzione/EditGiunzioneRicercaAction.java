/*
 * Created on 21-ago-2006
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.giunzione;

import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.CambiaTabAction;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.utility.UtilityNumeri;

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
 * DispatchAction per la gestione delle operazioni di update di una giunzione
 * 
 * @author Luca Giacomazzo
 */
public class EditGiunzioneRicercaAction extends AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(EditGiunzioneRicercaAction.class);
  
  public ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
  {
    if (logger.isDebugEnabled()) 
      logger.debug("runAction: inizio metodo");
    
    //target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;
    
    GiunzioneRicercaForm giunzioneForm = (GiunzioneRicercaForm)form;
    Integer progressivo = UtilityNumeri.convertiIntero(giunzioneForm.getProgressivo());
    
    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
          session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    GiunzioneRicercaForm giunzione = (GiunzioneRicercaForm) 
        contenitore.getElencoGiunzioni().elementAt(progressivo.intValue());
    giunzione.setTipoGiunzione(giunzioneForm.getTipoGiunzione());
    giunzione.setGiunzioneAttiva(giunzioneForm.getGiunzioneAttiva());
    giunzione.setCampiTabella1(giunzioneForm.getCampiTabella1());
    giunzione.setCampiTabella2(giunzioneForm.getCampiTabella2());
    
    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    
    request.setAttribute("tab", CambiaTabAction.CODICE_TAB_JOIN);
    
    if (logger.isDebugEnabled()) 
      logger.debug("runAction: fine metodo");
    
    return mapping.findForward(target);
  }
}