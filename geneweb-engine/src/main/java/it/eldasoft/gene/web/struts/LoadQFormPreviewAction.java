package it.eldasoft.gene.web.struts;

import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.integrazioni.QformManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import net.sf.json.JSONObject;

public class LoadQFormPreviewAction extends Action {

  private QformManager qformManager;

  public void setQformManager(QformManager qformManager) {
    this.qformManager = qformManager;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String id = request.getParameter("id");
    String ent = request.getParameter("ent");
    String idPreview = request.getParameter("idPreview");
    if (id != null && ent !=null && idPreview!=null) {

      HashMap<String,Object> letturaModello = this.qformManager.getJsonModello(new Long(id), ent, new Long(idPreview));
      JSONObject result = new JSONObject();
      Boolean esito = (Boolean)letturaModello.get("esito");
      String messaggio = (String)letturaModello.get("messaggio");
      JSONObject jsonDati = (JSONObject)letturaModello.get("jsonDati");
      result.put("esito", esito);
      result.put("dato", jsonDati);
      result.put("messaggio", messaggio);
      out.println(result);
      out.flush();
    }

    return null;

  }

}
