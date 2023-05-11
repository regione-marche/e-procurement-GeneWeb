package it.eldasoft.gene.web.struts;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.gene.commons.web.struts.ActionAjaxLogged;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import net.sf.json.JSONObject;

public class GetRecipientListAction extends ActionAjaxLogged {

  static Logger      logger = Logger.getLogger(GetRecipientListAction.class);

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

  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String target = null;
    String messageKey = null;

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    try {

      JSONObject result = new JSONObject();

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

      out.println(result);
      out.flush();

    } catch (Throwable e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (target != null) {
      return mapping.findForward(target);
    } else {
      return null;
    }

  }

}
