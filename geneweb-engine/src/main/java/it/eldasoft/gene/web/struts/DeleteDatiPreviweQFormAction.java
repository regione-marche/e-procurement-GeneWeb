package it.eldasoft.gene.web.struts;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import net.sf.json.JSONObject;

public class DeleteDatiPreviweQFormAction extends Action {

  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String id = request.getParameter("id");
    if (id != null ) {
      TransactionStatus status = null;
      boolean commitTransaction = false;
      try {
        status = this.sqlManager.startTransaction();
        sqlManager.update("delete from QFORMCONFITEMP where idpreview=?", new Object[] {new Long (id)});
        commitTransaction = true;
      } catch (Exception e) {
        commitTransaction = false;
        throw e;
      } finally {
        if (status != null) {
          if (commitTransaction) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        }
      }
      JSONObject result = new JSONObject();
      out.println(result);
      out.flush();
    }

    return null;

  }

}
