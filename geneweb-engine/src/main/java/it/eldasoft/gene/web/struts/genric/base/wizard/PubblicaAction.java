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
package it.eldasoft.gene.web.struts.genric.base.wizard;

import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicercheBase;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

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
 * Action per il salvataggio della pubblicazione o meno del report base
 * 
 * @author Luca.Giacomazzo
 */
public class PubblicaAction extends AbstractActionBaseGenRicercheBase {

  private final String SUCCESS_FINE   = "successFine";
  private final String SUCCESS_GRUPPI = "successGruppi";

  static Logger        logger         = Logger.getLogger(PubblicaAction.class);

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioniRunAction()
   */
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
        //CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = null;
    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    String disponibile = request.getParameter("disp");

    if (this.bloccaGestioneGruppiDisabilitata(request, false, false)) {
      // Gestione gruppi disabilitata
      if ("1".equals(disponibile)) {
        contenitore.getTestata().setDisp(true);
        contenitore.getTestata().setPersonale(false);
      } else {
        contenitore.getTestata().setDisp(false);
        contenitore.getTestata().setPersonale(true);
      }
      request.setAttribute("pageFrom",
          CostantiWizard.CODICE_PAGINA_PUBBLICAZIONE);
      target = SUCCESS_FINE;
    } else {
      // Gestione gruppi abilitata
      if ("1".equals(disponibile)) {
        contenitore.getTestata().setDisp(true);
        contenitore.getTestata().setPersonale(false);
        target = SUCCESS_GRUPPI;
      } else {
        contenitore.getTestata().setDisp(false);
        contenitore.getTestata().setPersonale(true);
        for (int i = contenitore.getNumeroGruppi() - 1; i >= 0; i--)
          contenitore.eliminaGruppo(i);
        request.setAttribute("pageFrom",
            CostantiWizard.CODICE_PAGINA_PUBBLICAZIONE);
        target = SUCCESS_FINE;
      }
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }
}