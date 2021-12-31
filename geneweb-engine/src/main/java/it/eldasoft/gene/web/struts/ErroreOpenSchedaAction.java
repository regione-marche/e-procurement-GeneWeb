/*
 * Created on 14-dic-2015
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli S.p.A. - Divisione ELDASOFT.
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
 * Azione che effettua il forward alla pagina di errore generale inserendo il messaggio di errore per indicare il motivo dell'accesso
 * fallito.
 *
 * @author Stefano.Sabbadin
 */
public class ErroreOpenSchedaAction extends ActionBaseNoOpzioni {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(ErroreOpenSchedaAction.class);

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    logger.debug("runAction: inizio metodo");
    String target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
    String messageKey = "errors.scheda.recordNonEstratto";
    if (logger.isDebugEnabled()) logger.error(this.resBundleGenerale.getString(messageKey));
    this.aggiungiMessaggio(request, messageKey);
    logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }
}