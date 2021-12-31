package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetContaNonLettiW_MESSAGEAction extends Action {

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

  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    if (profilo != null) {
      Long syscon = new Long(profilo.getId());

      HashMap<String, Long> hMapResult = new HashMap<String, Long>();

      try {
        String selectW_MESSAGE = "select count(*) from w_message_in where message_recipient_syscon = ? and (message_recipient_read is null or message_recipient_read = 0)";
        Long numeroMessaggiNonLetti = (Long) sqlManager.getObject(selectW_MESSAGE, new Object[] { syscon });
        hMapResult.put("numeromessagginonletti", numeroMessaggiNonLetti);
      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura dei messaggi", e);
      }

      JSONObject jsonResult = JSONObject.fromObject(hMapResult);
      out.println(jsonResult);
      out.flush();
    }

    return null;

  }

}
