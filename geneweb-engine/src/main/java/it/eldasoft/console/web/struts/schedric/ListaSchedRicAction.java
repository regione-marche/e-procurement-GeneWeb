/*
 * Created on 25-mag-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.console.web.struts.schedric;

import it.eldasoft.console.bl.schedric.SchedRicManager;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.ListaForm;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Dispatch Action per la gestione delle operazioni su singoli oggetti presenti
 * nella lista delle schedulazioni
 * 
 * @author Francesco De Filippis
 */
public class ListaSchedRicAction extends DispatchActionBaseNoOpzioni {

  private static final String SUCCESS_ELIMINA = "successElimina";

  /** Logger Log4J di classe */
  static Logger               logger          = Logger.getLogger(ListaSchedRicAction.class);

  private SchedRicManager    schedRicManager;

  public ActionForward eliminaMultiplo(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled())
      logger.debug("eliminaMultiplo: inizio metodo");

    // target di default, da modificare nel momento in cui si verificano degli
    // errori
    String target = ListaSchedRicAction.SUCCESS_ELIMINA;
    String messageKey = null;
    
    try {
      ListaForm schedRic = (ListaForm) form;

      // costruzione dell'elenco degli id delle ricerche da rimuovere
      String id[] = schedRic.getId();
      int[] elencoSchedRic = new int[id.length];
      for (int i = 0; i < elencoSchedRic.length; i++)
        elencoSchedRic[i] = Integer.parseInt(id[i]);

      this.schedRicManager.deleteSchedulazioniRicerche(elencoSchedRic);

    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (logger.isDebugEnabled()) logger.debug("eliminaMultiplo: fine metodo");

    return mapping.findForward(target);
  }

  
  /**
   * @param schedRicManager The schedRicManager to set.
   */
  public void setSchedRicManager(SchedRicManager schedRicManager) {
    this.schedRicManager = schedRicManager;
  }

}