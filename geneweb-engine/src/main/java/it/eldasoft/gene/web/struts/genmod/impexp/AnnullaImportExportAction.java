/*
 * Created on 13-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

package it.eldasoft.gene.web.struts.genmod.impexp;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per la gestione dell'annullamento di import/export definizine modelli:
 * si ritorna alla pagina precedente oppure alla home page  
 * 
 * @author Francesco.DeFilippis
 */
public class AnnullaImportExportAction extends ActionBaseNoOpzioni {

  /** logger di classe  */
  static Logger logger = Logger.getLogger(AnnullaImportExportAction.class);
  
  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioniRunAction()
   */
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }
  
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, 
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
    if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    // Cancellazione dalla sessione dell'oggetto ContenitoreDatiImport
    if(request.getSession().getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO) != null)
      request.getSession().removeAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    
    request.getSession().removeAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA);
    
    if(logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(CostantiGeneraliStruts.FORWARD_OK);
  }

}