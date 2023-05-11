package it.eldasoft.gene.web.struts;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionAjaxLogged;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import net.sf.json.JSONObject;

public class GetContaNonLettiW_MESSAGEAction extends ActionAjaxLogged {

  static Logger      logger = Logger.getLogger(GetContaNonLettiW_MESSAGEAction.class);

  /**
   * Manager per la gestione delle interrogazioni di database.
   */
  private SqlManager sqlManager;

  /**
   * @param sqlManager
   *        the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String target = null;
    String messageKey = null;

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    try {

      ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      if (profilo != null) {
        Long syscon = new Long(profilo.getId());

        HashMap<String, Long> hMapResult = new HashMap<String, Long>();

        String selectW_MESSAGE = "select count(*) from w_message_in where message_recipient_syscon = ? and (message_recipient_read is null or message_recipient_read = 0)";
        Long numeroMessaggiNonLetti = (Long) sqlManager.getObject(selectW_MESSAGE, new Object[] { syscon });
        hMapResult.put("numeromessagginonletti", numeroMessaggiNonLetti);

        JSONObject jsonResult = JSONObject.fromObject(hMapResult);
        out.println(jsonResult);
        out.flush();
      }

    } catch (Throwable e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (target != null) {
      return mapping.findForward(target);
    } else {
      return null;
    }

  }

}
