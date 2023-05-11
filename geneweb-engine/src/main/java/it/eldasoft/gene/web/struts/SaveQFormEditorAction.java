package it.eldasoft.gene.web.struts;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.QeditorForm;
import net.sf.json.JSONObject;

public class SaveQFormEditorAction extends ActionBaseNoOpzioni {

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
  @SuppressWarnings("unchecked")
  public final ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) throws IOException {

    DataSourceTransactionManagerBase.setRequest(request);

    String target = "success";
    String messageKey = null;

    //response.setHeader("cache-control", "no-cache");
    //response.setContentType("text/text;charset=utf-8");
    //PrintWriter out = response.getWriter();

    QeditorForm qeditordForm = (QeditorForm) form;
    String id = request.getParameter("id");
    String ent = request.getParameter("ent");

    if (id != null && ent !=null) {
      TransactionStatus status = null;
      boolean commitTransaction = false;
      JSONObject result = new JSONObject();
      String update="update " + ent + " set oggetto =? where id=?";
      String msgErr="";
      try {

        String oggetto = qeditordForm.getJsonFile();
        if(oggetto!=null) {
          byte[] test = Base64.decodeBase64(oggetto);
          oggetto = new String(test, Charset.forName("UTF-8"));
       }
        status = this.sqlManager.startTransaction();
        this.sqlManager.update(update, new Object[] {oggetto, new Long(id)});
        commitTransaction = true;
      } catch (Exception e) {
        commitTransaction = false;
        msgErr = e.getMessage();
        messageKey = "errors.applicazione.inaspettataException";
        this.aggiungiMessaggio(request, messageKey);
      } finally {
        if (status != null) {
          try {
          if (commitTransaction) {
            this.sqlManager.commitTransaction(status);
          } else {
            this.sqlManager.rollbackTransaction(status);
          }
          }catch(Exception e) {
            throw new IOException(e);
          }
        }
      }

      result.put("esito", new Boolean(commitTransaction));
      result.put("messaggio", msgErr);
      //out.println(result);
      //out.flush();
      request.setAttribute("esito",new Boolean(commitTransaction));
      request.setAttribute("messaggio",msgErr);
    }

    if (messageKey != null) response.reset();

    return mapping.findForward(target);

  }

}
