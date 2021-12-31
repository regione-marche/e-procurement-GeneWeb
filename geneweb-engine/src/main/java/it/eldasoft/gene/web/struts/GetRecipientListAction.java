package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import net.sf.json.JSONObject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetRecipientListAction extends Action {

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

    JSONObject result = new JSONObject();
    
    try {
      // Lista degli uffici
      List<?> datiUFFINT = sqlManager.getListVector("select codein, nomein from uffint order by nomein", new Object[] {});
      if (datiUFFINT != null && datiUFFINT.size() > 0) {
        List<HashMap<String, Object>> hMapRecords = new ArrayList<HashMap<String, Object>>();
        for (int uff = 0; uff < datiUFFINT.size(); uff++) {
          HashMap<String, Object> hMap = new HashMap<String, Object>();
          hMap.put("type", "UFF");
          hMap.put("val", (String) SqlManager.getValueFromVectorParam(datiUFFINT.get(uff), 0).getValue());
          hMap.put("descr", (String) SqlManager.getValueFromVectorParam(datiUFFINT.get(uff), 1).getValue());
          hMapRecords.add(hMap);
        }
        result.put("uff", hMapRecords);
      }
      
      // Lista degli utenti
      List<?> datiUSRSYS = sqlManager.getListVector("select syscon, sysute from usrsys order by sysute", new Object[] {});
      if (datiUSRSYS != null && datiUSRSYS.size() > 0) {
        List<HashMap<String, Object>> hMapRecords = new ArrayList<HashMap<String, Object>>();
        for (int usr = 0; usr < datiUSRSYS.size(); usr++) {
          HashMap<String, Object> hMap = new HashMap<String, Object>();
          hMap.put("type", "USR");
          hMap.put("val", (Long) SqlManager.getValueFromVectorParam(datiUSRSYS.get(usr), 0).getValue());
          hMap.put("descr", (String) SqlManager.getValueFromVectorParam(datiUSRSYS.get(usr), 1).getValue());
          hMapRecords.add(hMap);
        }
        // Aggiunta della keyword "Tutti" per l'invio a tutti gli utenti
        HashMap<String, Object> hMapTutti = new HashMap<String, Object>();
        hMapTutti.put("type", "USR");
        hMapTutti.put("val", new Long(-999999));
        hMapTutti.put("descr", "Tutti gli utenti");
        hMapRecords.add(hMapTutti);
        result.put("usr", hMapRecords);
      }
      
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura della lista dei destinatari", e);
    }

    out.println(result);
    out.flush();

    return null;

  }

}
