/*
 * Created on 29-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Azione che popola il request con le informazioni da visualizzare nella popup di "about".
 *
 * @author Stefano.Sabbadin
 */
public class InformazioniSuAction extends Action {

  /** Logger Log4J di classe */
  static Logger              logger          = Logger.getLogger(InformazioniSuAction.class);

  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    logger.debug("execute: inizio metodo");

    request.setAttribute("acquirente", ConfigManager.getValore(CostantiGenerali.PROP_ACQUIRENTE));
    request.setAttribute("codiceProdotto", ConfigManager.getValore(CostantiGenerali.PROP_CODICE_PRODOTTO));
    request.setAttribute("chiaveAccesso", ConfigManager.getValore(CostantiGenerali.PROP_CHIAVE_DI_ACCESSO));

    logger.debug("runAction: fine metodo");
    return mapping.findForward(CostantiGeneraliStruts.FORWARD_OK);
  }

}