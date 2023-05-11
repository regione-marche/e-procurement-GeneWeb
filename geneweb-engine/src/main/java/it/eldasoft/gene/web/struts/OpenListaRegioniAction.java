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

import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;

import java.io.IOException;

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
public class OpenListaRegioniAction extends ActionBaseNoOpzioni {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(GetTabellatoAction.class);

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
    String zone = (String) request.getAttribute("zone");
    if (zone == null) zone = (String) request.getParameter("zone");

    String[] listaRegioni = new String[] { "Piemonte", "Valle d'Aosta",
        "Liguria", "Lombardia", "Friuli Venezia Giulia", "Trentino Alto Adige",
        "Veneto", "Emilia Romagna", "Toscana", "Umbria", "Marche", "Abruzzo",
        "Molise", "Lazio", "Campania", "Basilicata", "Puglia", "Calabria",
        "Sardegna", "Sicilia" };

    String[] zoneAttive = new String[20];
    for (int i = 0; i < zone.length(); i++) {
      zoneAttive[i] = "" + zone.charAt(i);
    }

    request.setAttribute("listaRegioni", listaRegioni);

    request.setAttribute("zoneAttive", zoneAttive);

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);

  }

}
