/*
 * Created on 23-ago-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod.impexp;

import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.genmod.ContenitoreDatiImport;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
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
 * Action per il salvataggio nell'oggetto in contenitoreDatiImport presente in
 * sessione della lista dei gruppi da associare al modello in importazione
 * 
 * @author Francesco.DeFilippis
 */
public class GruppiImportAction extends ActionBaseNoOpzioni {

  static Logger logger = Logger.getLogger(GruppiImportAction.class);

  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;
    
    GruppiModelliImportForm gruppiModelliForm = (GruppiModelliImportForm) form;
    
    ContenitoreDatiImport contenitore = (ContenitoreDatiImport)
      request.getSession().getAttribute(CostantiGenModelli.OGGETTO_DETTAGLIO);
    
    if(gruppiModelliForm != null && gruppiModelliForm.getIdGruppo() != null && gruppiModelliForm.getIdGruppo().length > 0)
      contenitore.setGruppiModelliForm(gruppiModelliForm);
    
    request.setAttribute("pageFrom", CostantiWizard.CODICE_PAGINA_GRUPPI);
    
    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

}