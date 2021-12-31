package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

public class SetW_TAGSLISTAction extends Action {

  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    String operation = request.getParameter("operation");
    String codapp = request.getParameter("codapp");
    String tagcod = request.getParameter("tagcod");
    String tagentity = request.getParameter("tagentity");
    String tagfield = request.getParameter("tagfield");
    String taginfo = request.getParameter("taginfo");

    TransactionStatus status = null;
    boolean commitTransaction = false;
    try {
      status = this.sqlManager.startTransaction();

      if ("DELETE".equals(operation)) {
        if (tagentity == null || "".equals(tagentity.trim())) {
          String deleteW_TAGSLIST = "delete from w_tagslist where codapp = ? and tagcod = ? and tagentity is null and tagfield = ?";
          this.sqlManager.update(deleteW_TAGSLIST, new Object[] { codapp, tagcod, tagfield });
        } else {
          String deleteW_TAGSLIST = "delete from w_tagslist where codapp = ? and tagcod = ? and tagentity = ? and tagfield = ?";
          this.sqlManager.update(deleteW_TAGSLIST, new Object[] { codapp, tagcod, tagentity, tagfield });
        }

      } else if ("INSERTUPDATE".equals(operation)) {
        
        if (tagentity == null || "".equals(tagentity.trim())) {
          String selectW_TAGSLIST = "select count(*) from w_tagslist where codapp = ? and tagcod = ? and tagentity is null and tagfield = ?";
          Long cnt = (Long) this.sqlManager.getObject(selectW_TAGSLIST, new Object[] { codapp, tagcod, tagfield });
  
          if (cnt != null && cnt.longValue() > 0) {
            String updateW_TAGSLIST = "update w_tagslist set taginfo = ? where codapp = ? and tagcod = ? and tagentity is null and tagfield = ?";
            this.sqlManager.update(updateW_TAGSLIST, new Object[] { taginfo, codapp, tagcod, tagfield });
          } else {
            String insertW_TAGSLIST = "insert into w_tagslist (codapp, tagcod, tagentity, tagfield, taginfo) values (?,?,?,?,?)";
            Object[] obj = new Object[5];
            obj[0] = codapp;
            obj[1] = tagcod;
            obj[2] = null;
            obj[3] = tagfield;
            obj[4] = taginfo;
            this.sqlManager.update(insertW_TAGSLIST, obj);
          }
        } else {

          String selectW_TAGSLIST = "select count(*) from w_tagslist where codapp = ? and tagcod = ? and tagentity = ? and tagfield = ?";
          Long cnt = (Long) this.sqlManager.getObject(selectW_TAGSLIST, new Object[] { codapp, tagcod, tagentity, tagfield });
  
          if (cnt != null && cnt.longValue() > 0) {
            String updateW_TAGSLIST = "update w_tagslist set taginfo = ? where codapp = ? and tagcod = ? and tagentity = ? and tagfield = ?";
            this.sqlManager.update(updateW_TAGSLIST, new Object[] { taginfo, codapp, tagcod, tagentity, tagfield });
          } else {
            String insertW_TAGSLIST = "insert into w_tagslist (codapp, tagcod, tagentity, tagfield, taginfo) values (?,?,?,?,?)";
            Object[] obj = new Object[5];
            obj[0] = codapp;
            obj[1] = tagcod;
            obj[2] = tagentity;
            obj[3] = tagfield;
            obj[4] = taginfo;
            this.sqlManager.update(insertW_TAGSLIST, obj);
          }
        }
      }

      commitTransaction = true;
    } catch (Exception e) {
      commitTransaction = false;
    } finally {
      if (status != null) {
        if (commitTransaction) {
          this.sqlManager.commitTransaction(status);
        } else {
          this.sqlManager.rollbackTransaction(status);
        }
      }
    }
    return null;
  }

}
