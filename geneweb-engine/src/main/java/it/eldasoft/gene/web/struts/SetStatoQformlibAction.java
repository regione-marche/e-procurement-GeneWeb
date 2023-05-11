package it.eldasoft.gene.web.struts;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONObject;

public class SetStatoQformlibAction extends Action {

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

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String id = request.getParameter("id");
    String tipo = request.getParameter("tipo");
    String cod = request.getParameter("cod");

    int livEvento = 1;
    String codEvento="";
    String descEvento="";
    String msgErr="";

    if (id != null && tipo !=null) {
      if("2".equals(tipo)) {
        codEvento="QFORMLIB_ATTIVA";
        descEvento = "Attivazione modello Q-form";
      }else if("3".equals(tipo)) {
        codEvento="QFORMLIB_DISATTIVA";
        descEvento = "Disattivazione modello Q-form";
      }else if("4".equals(tipo)) {
        codEvento="QFORMLIB_ARCHIVIA";
        descEvento = "Archiviazione modello Q-form";
      }

      TransactionStatus status = null;
      boolean commitTransaction = false;
      JSONObject result = new JSONObject();
      String update="update qformlib set stato =? where id=?";
      Object par[]=null;
      if("2".equals(tipo)) {
        Date oggi = UtilityDate.getDataOdiernaAsDate();
        Timestamp t = new Timestamp(oggi.getTime());
        update="update qformlib set stato =?, dultagg = ? where id=?";
        par = new Object[] {new Long(tipo), t,  new Long(id)};
      }else {
        par = new Object[] {new Long(tipo), new Long(id)};
      }

      try {
        status = this.sqlManager.startTransaction();
        this.sqlManager.update(update, par);
        commitTransaction = true;
      } catch (Exception e) {
        livEvento = 3;
        commitTransaction = false;
        msgErr = e.getMessage();
        throw e;
      } finally {
        if (status != null) {
          if (commitTransaction) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
        }
        LogEvento logEvento = LogEventiUtils.createLogEvento(request);
        logEvento.setLivEvento(livEvento);
        logEvento.setOggEvento(cod);
        logEvento.setCodEvento(codEvento);
        logEvento.setDescr(descEvento);
        logEvento.setErrmsg(msgErr);
        LogEventiUtils.insertLogEventi(logEvento);
      }

      result.put("esito", new Boolean(commitTransaction));
      result.put("messaggio", msgErr);
      out.println(result);
      out.flush();
    }

    return null;

  }

}
