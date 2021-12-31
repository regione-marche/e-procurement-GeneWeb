package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.SqlManager;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author Cristian.Febas
 *
 */
public class GetListaEntiAction extends Action {

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
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String cfein = request.getParameter("cfamm");
    
    String selectUFFINT = "select nomein, cfein, codein from uffint where cfein like '" + cfein + "%' or ivaein like '" + cfein + "%' order by nomein";

    List<?> datiUFFINT = null;
    datiUFFINT = sqlManager.getListVector(selectUFFINT, new Object[] {});

    JSONArray jsonArrayUFFINT = null;
    if (datiUFFINT != null && datiUFFINT.size() > 0) {
    	jsonArrayUFFINT= JSONArray.fromObject(datiUFFINT.toArray());
    } else {
    	jsonArrayUFFINT = new JSONArray();
    }

    out.println(jsonArrayUFFINT);


    out.flush();
    return null;
  }

}
