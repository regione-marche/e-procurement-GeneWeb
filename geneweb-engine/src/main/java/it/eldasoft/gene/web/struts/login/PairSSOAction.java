/*
 * Created on 09/nov/23
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.login;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;

/**
 * Inietta nella pagina di login un attributo PairingSSO
 * per procedere alla login SSO finalizzata al pairing
 *
 * @author Manuel Bridda
 */
public class PairSSOAction extends Action {

  /** Logger Log4J di classe */
  static Logger            logger            = Logger.getLogger(PairSSOAction.class);

  /**
   * Resource bundle parte generale dell'applicazione (modulo GENE)
   */
  protected ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("execute: inizio metodo");

    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    if(profiloUtente != null)
      request.getSession().setAttribute("pairingSSO", "true");
    
    if (logger.isDebugEnabled()) logger.debug("execute: fine metodo");
    
    return mapping.findForward(CostantiGeneraliStruts.FORWARD_OK);
  }
}
