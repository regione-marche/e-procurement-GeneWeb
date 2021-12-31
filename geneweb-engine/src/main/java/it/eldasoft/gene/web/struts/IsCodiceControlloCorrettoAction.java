package it.eldasoft.gene.web.struts;

import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class IsCodiceControlloCorrettoAction extends Action {

  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    HashMap<String, Boolean> hMapResult = new HashMap<String, Boolean>();

    if (!this.rpHash(request.getParameter("realperson")).equals(request.getParameter("realpersonHash"))) {
      hMapResult.put("codiceControlloCorretto", Boolean.FALSE);
    } else {
      hMapResult.put("codiceControlloCorretto", Boolean.TRUE);
    }
    JSONObject jsonResult = JSONObject.fromObject(hMapResult);
    out.println(jsonResult);

    out.flush();
    return null;
  }

  /**
   * Gestione della HashMap per il controllo mediante CAPTCHA.
   *
   * @param value
   * @return
   */
  private String rpHash(String value) {
    int hash = 5381;
    value = value.toUpperCase();
    for (int i = 0; i < value.length(); i++) {
      hash = ((hash << 5) + hash) + value.charAt(i);
    }
    return String.valueOf(hash);
  }

}
