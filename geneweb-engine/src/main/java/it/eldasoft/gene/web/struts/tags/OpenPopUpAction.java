/*
 * Created on 19-giu-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags;

import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action analoga a OpenPageAction, con la differenza che questa action svuota
 * le voci di history presenti nei livelli di popup maggiori o uguali al livello
 * di popup attuale
 *
 * Questa action deve venir usata per l'apertura di una finestra di popup.
 *
 * @author Luca.Giacomazzo
 */
public class OpenPopUpAction extends ActionBaseNoOpzioni {

  private static Logger logger = Logger.getLogger(OpenPageAction.class);

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    try {
      String href = request.getParameter("href");
      // non deve essere possibile redirezionare ad una risorsa non jsp e nemmeno risalire sopra WEB-INF/pages
      if (UtilityStruts.isValidJspPath(href)) {
        // Si va a cancellare tutte le voci di history presenti nei livelli di popup
        // maggiori o uguali al livello di popup attuale
        UtilityTags.getUtilityHistory((request).getSession()).clear(UtilityStruts.getNumeroPopUp(request));

        request.setAttribute(UtilityTags.REQUEST_VAR_HISTORY_SIZE, new Integer(
        UtilityTags.getUtilityHistory( (request).getSession()).size( UtilityStruts.getNumeroPopUp(request))));

        // Redirezionamento verso la pagina richiesta
        return UtilityStruts.redirectToPage(href, false, request);
      } else {
        String messageKey = "errors.url.notWellFormed";
        String messageError = this.resBundleGenerale.getString(messageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0),
            href);
        logger.error(messageError);
        this.aggiungiMessaggio(request, messageKey, href);
      }
    } catch (Throwable t) {
      logger.error(t.getMessage(), t);
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
  }

}
