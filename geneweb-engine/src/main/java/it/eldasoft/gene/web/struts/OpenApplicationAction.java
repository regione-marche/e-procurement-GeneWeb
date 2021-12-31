/*
 * Created on 22-feb-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.LogEventiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
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
 * Azione che apre l'applicativo alla pagina richiesta oppure alla homepage
 *
 * @author Stefano.Sabbadin
 * @since 1.4.6
 */
public class OpenApplicationAction extends ActionBaseNoOpzioni {

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(OpenApplicationAction.class);

  /** Manager per la tracciatura di eventi */
  private LogEventiManager logEventiManager;

  /**
   * @param logEventiManager
   *        logEventiManager da settare internamente alla classe.
   */
  public void setLogEventiManager(LogEventiManager logEventiManager) {
    this.logEventiManager = logEventiManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: inizio metodo");
    }

    String codiceApplicazione = (String) request.getSession().getAttribute(
        CostantiGenerali.MODULO_ATTIVO);

    if ("1".equals(request.getSession().getServletContext().getAttribute(
        CostantiGenerali.ATTR_ATTIVA_LOG_EVENTI))) {
      // nel caso sia prevista la gestione della tracciatura sul log eventi,
      // si inserisce la tracciatura per l'accesso
      String codiceProfilo = (String) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_ATTIVO);

      int idUtente = ((ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId();
    }

    String href = request.getParameter("href");

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: fine metodo");
    }

    int livEvento = LogEvento.LIVELLO_INFO;
    String messageKey = "";
    String messageError = "";
    // di default si va alla homepage, a meno che non sia presente un
    // parametro nel request denominato "href"
    if (href == null)
      return UtilityStruts.redirectToPage("home" + codiceApplicazione + ".jsp",
          false, request);
    else {
      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setLivEvento(livEvento);
      logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_OPEN_APPLICATION);
      logEvento.setDescr("Accesso esterno all'applicativo");

      // si verifica se il path è scritto correttamente oppure no, non sono concesse risalite di percorso
      if (UtilityStruts.isValidJspPath(href)) {
        LogEventiUtils.insertLogEventi(logEvento);
        return UtilityStruts.redirectToPage(href, false,
            request);
      } else {
        messageKey = "errors.url.notWellFormed";
        messageError = this.resBundleGenerale.getString(messageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0),
            href);
        logEvento.setErrmsg(messageError);
        logEvento.setLivEvento(LogEvento.LIVELLO_ERROR);
        LogEventiUtils.insertLogEventi(logEvento);
        logger.error(messageError);
        this.aggiungiMessaggio(request, messageKey, href);
        return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
      }
    }
  }

}
