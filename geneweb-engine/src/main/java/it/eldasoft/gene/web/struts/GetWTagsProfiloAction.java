package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetWTagsProfiloAction extends Action {

  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws Exception {

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONArray jsonArray = new JSONArray();

    String codapp = request.getParameter("codapp");
    String tagprofili = request.getParameter("tagprofili");

    List<?> datiW_PROFILI = sqlManager.getListVector(
        "select cod_profilo, nome, descrizione from w_profili where codapp = ? order by cod_profilo", new Object[] { codapp });

    if (datiW_PROFILI != null && datiW_PROFILI.size() > 0) {
      for (int p = 0; p < datiW_PROFILI.size(); p++) {

        String cod_profilo = (String) SqlManager.getValueFromVectorParam(datiW_PROFILI.get(p), 0).getValue();
        String nome = (String) SqlManager.getValueFromVectorParam(datiW_PROFILI.get(p), 1).getValue();
        String descrizione = (String) SqlManager.getValueFromVectorParam(datiW_PROFILI.get(p), 2).getValue();

        JSONObject _oW_PROFILI = new JSONObject();
        _oW_PROFILI.accumulate("cod_profilo", cod_profilo);
        _oW_PROFILI.accumulate("nome", nome);
        _oW_PROFILI.accumulate("descrizione", descrizione);

        String associato = "false";
        if (tagprofili != null && !"".equals(tagprofili.trim())) {
          String[] arraytagprofili = tagprofili.split(",");
          for (int a = 0; a < arraytagprofili.length; a++) {
            if (cod_profilo.equals(arraytagprofili[a])) {
              associato = "true";
            }
          }
        }
        _oW_PROFILI.accumulate("associato", associato);

        jsonArray.add(_oW_PROFILI);
      }
    }

    out.print(jsonArray);
    out.flush();

    return null;

  }

}
