/*
 * 	Created on 09/04/2018
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.integrazioni.Art80Manager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class Art80SetServiceOEAction extends ActionBaseNoOpzioni {

  protected static final String FORWARD_SUCCESS = "art80setserviceoesuccess";
  protected static final String FORWARD_ERROR   = "art80setserviceoeerror";

  static Logger                 logger          = Logger.getLogger(Art80SetServiceOEAction.class);

  private Art80Manager          art80Manager;

  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("Art80SetServiceOEAction: inizio metodo");

    String target = FORWARD_SUCCESS;
    String messageKey = null;

    try {

      String codimp = request.getParameter("codimp");
      String codein = (String) request.getSession().getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
      String status_service = request.getParameter("status_service");

      List<Object> resultSetServiceOE = new Vector<Object>();
      resultSetServiceOE = art80Manager.art80SetServiceOE(codimp, codein, status_service);

      request.getSession().setAttribute("codimp", codimp);
      request.getSession().setAttribute("resultSetServiceOE", resultSetServiceOE);
      request.getSession().setAttribute("status_service", status_service);

    } catch (Exception e) {
      target = FORWARD_ERROR;
      messageKey = "errors.art80.error";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey, e.getMessage());
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (messageKey != null) response.reset();
    if (logger.isDebugEnabled()) logger.debug("Art80SetServiceOEAction: fine metodo");

    return mapping.findForward(target);

  }

  public Art80Manager getArt80Manager() {
    return art80Manager;
  }

  public void setArt80Manager(Art80Manager art80Manager) {
    this.art80Manager = art80Manager;
  }

}
