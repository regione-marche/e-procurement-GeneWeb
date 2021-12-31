/*
 * Created on 5-ott-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.admin.UffintManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.UfficioIntestatario;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Azione per settare in sessione l'ufficio intestatario nella pagina successiva
 * alla selezione profilo
 *
 * @author Stefano.Sabbadin
 */
public class SetUfficioIntestatarioAction extends ActionBaseNoOpzioni {

  /** Logger Log4J di classe */
  static Logger         logger = Logger.getLogger(SetUfficioIntestatarioAction.class);

  private UffintManager uffintManager;

  /**
   * @param uffintManager
   *        uffintManager da settare internamente alla classe.
   */
  public void setUffintManager(UffintManager uffintManager) {
    this.uffintManager = uffintManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String idUffint = request.getParameter("uffint");

    UfficioIntestatario ufficio = this.uffintManager.getUfficioIntestatarioByPK(idUffint);

    // set in sessione dell'id dell'ufficio e del suo nome
    request.getSession().setAttribute(
        CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO, ufficio.getCodice());
    request.getSession().setAttribute(
        CostantiGenerali.NOME_UFFICIO_INTESTATARIO_ATTIVO, ufficio.getNome());

    // forward alla pagina iniziale
    String codiceApplicazione = (String) request.getSession().getAttribute(
        CostantiGenerali.MODULO_ATTIVO);

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: fine metodo");
    }

    int livEvento = 1;
    String errMsgEvento = "";

    LogEvento logEvento = LogEventiUtils.createLogEvento(request);
    logEvento.setLivEvento(livEvento);
    logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_SET_UFFINT);
    logEvento.setDescr("Selezione ufficio intestatario (" + ufficio.getCodice() + ")");
    logEvento.setErrmsg(errMsgEvento);
    LogEventiUtils.insertLogEventi(logEvento);

    return UtilityStruts.redirectToPage("home" + codiceApplicazione + ".jsp",
        false, request);
  }

}