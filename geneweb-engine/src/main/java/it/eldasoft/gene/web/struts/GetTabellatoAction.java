/*
 * Created on 14-apr-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.Tabellato;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author cit_defilippis
 * 
 */
public class GetTabellatoAction extends ActionBaseNoOpzioni {

  /** Logger Log4J di classe */
  static Logger              logger = Logger.getLogger(GetTabellatoAction.class);

  protected TabellatiManager tabellatiManager;

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    String target = CostantiGeneraliStruts.FORWARD_OK;

    // codice tabellato da visualizzare
    String tabellato = (String) request.getAttribute("tabellato");
    if (tabellato == null)
      tabellato = (String) request.getParameter("tabellato");

    // nome della funzione js da richiamare dalla popup alla chiamante
    String funzione = (String) request.getAttribute("funzione");
    if (funzione == null) funzione = (String) request.getParameter("funzione");

    // il risultato deve contenere gli apici
    String apici = (String) request.getAttribute("apici");
    if (apici == null) apici = (String) request.getParameter("apici");

    // se il tabellato non è string oppure se non devono essere messi gli apici
    // (non è un operatore IN o NOT IN) allora non faccio mettere gli apici
    if (!tabellatiManager.isTabellatoString(tabellato) || !"1".equals(apici))
      apici = "0";

    List<Tabellato> listaValoriTabellato = tabellatiManager.getTabellato(tabellato);

    request.setAttribute("apici", apici);

    request.setAttribute("listaValoriTabellato", listaValoriTabellato);

    request.setAttribute("funzione", funzione);

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);

  }

}
