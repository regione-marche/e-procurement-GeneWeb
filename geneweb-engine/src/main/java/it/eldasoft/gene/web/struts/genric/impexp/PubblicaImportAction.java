/*
 * Created on 09-mag-2007
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.impexp;

import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiImport;
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
 * Action per il salvataggio della pubblicazione o meno del report in 
 * importazione nel contenitoreDatiImport presente in sessione
 * 
 * @author Luca.Giacomazzo
 */
public class PubblicaImportAction extends ActionBaseNoOpzioni {

  static Logger logger = Logger.getLogger(PubblicaImportAction.class);

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioniRunAction()
   */
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = null;
    ContenitoreDatiImport contenitore = (ContenitoreDatiImport)
      request.getSession().getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    String disponibile = request.getParameter("disp");

    if (this.bloccaGestioneGruppiDisabilitata(request, false, false)){
      // Gestione gruppi disabilitata
      if ("1".equals(disponibile))
        contenitore.setPubblicaReport(true);
        else
        contenitore.setPubblicaReport(false);
      
      target = CostantiWizard.SUCCESS_FINE;
    } else {
      // Gestione gruppi abilitata
      if ("1".equals(disponibile)) {
        contenitore.setPubblicaReport(true);

        target = CostantiWizard.SUCCESS_EDIT_GRUPPI;
      } else {
        contenitore.setPubblicaReport(false);
        target = CostantiWizard.SUCCESS_FINE;
      }
    }
    
    request.setAttribute("pageFrom", CostantiWizard.CODICE_PAGINA_PUBBLICAZIONE);
    
    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }
}