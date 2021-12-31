package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.DataSourceTransactionManagerBase;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.PrintWriter;
import java.sql.Date;
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

public class GetListaW_MESSAGEAction extends Action {

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

    String type = request.getParameter("type");
    String start = request.getParameter("start");
    String end = request.getParameter("end");
    Long indexstart = new Long(0);
    Long indexend = new Long(99);
    
    if (start != null && !"".equals(start)) {
      indexstart = new Long(start);
    }
    
    if (end != null && !"".equals(end)) {
      indexend = new Long(end);
    }
    

    ProfiloUtente profilo = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    if (profilo != null) {
      Long syscon = new Long(profilo.getId());

      JSONObject result = new JSONObject();
      int totalRecords = 0;
      List<HashMap<String, Object>> hMapRecords = new ArrayList<HashMap<String, Object>>();

      try {
        if ("IN".equals(type)) {
          String selectW_MESSAGE = "select message_id, " // 0
              + "message_date, " // 1
              + "message_subject, " // 2
              + "message_sender_syscon, " // 3
              + "message_recipient_read, " // 4
              + "message_recipient_archive " // 5
              + "from w_message_in "
              + "where message_recipient_syscon = ? "
              + "order by message_date desc";

          List<?> datiW_MESSAGE = sqlManager.getListVector(selectW_MESSAGE, new Object[] { syscon });
          if (datiW_MESSAGE != null && datiW_MESSAGE.size() > 0) {
            totalRecords = datiW_MESSAGE.size();
            for (int m = indexstart.intValue(); m < datiW_MESSAGE.size() && m < indexend; m++) {
              Long message_id = (Long) SqlManager.getValueFromVectorParam(datiW_MESSAGE.get(m), 0).getValue();
              Date message_date = (Date) SqlManager.getValueFromVectorParam(datiW_MESSAGE.get(m), 1).getValue();
              String message_subject = (String) SqlManager.getValueFromVectorParam(datiW_MESSAGE.get(m), 2).getValue();
              Long message_sender_syscon = (Long) SqlManager.getValueFromVectorParam(datiW_MESSAGE.get(m), 3).getValue();
              Long message_recipient_read = (Long) SqlManager.getValueFromVectorParam(datiW_MESSAGE.get(m), 4).getValue();
              Long message_recipient_archive = (Long) SqlManager.getValueFromVectorParam(datiW_MESSAGE.get(m), 5).getValue();

              HashMap<String, Object> hMap = new HashMap<String, Object>();
              hMap.put("message_id", message_id);
              if (message_date != null) {
                hMap.put("message_date", UtilityDate.convertiData(message_date, UtilityDate.FORMATO_GG_MM_AAAA));
              }
              hMap.put("message_subject", message_subject);
              hMap.put("message_sender", this.getDescrizioneUtente(message_sender_syscon));
              hMap.put("message_recipient_read", message_recipient_read);
              hMap.put("message_recipient_archive", message_recipient_archive);
              hMapRecords.add(hMap);
            }
          }
        } else if ("OUT".equals(type)) {
          String selectW_MESSAGE = "select message_id, " // 0
              + "message_date, " // 1
              + "message_subject " // 2
              + "from w_message_out " // 3
              + "where message_sender_syscon = ? "
              + "order by message_date desc";
          List<?> datiW_MESSAGE = sqlManager.getListVector(selectW_MESSAGE, new Object[] { syscon });
          if (datiW_MESSAGE != null && datiW_MESSAGE.size() > 0) {
            totalRecords = datiW_MESSAGE.size();
            for (int m = indexstart.intValue(); m < datiW_MESSAGE.size() && m < indexend; m++) {
              Long message_id = (Long) SqlManager.getValueFromVectorParam(datiW_MESSAGE.get(m), 0).getValue();
              Date message_date = (Date) SqlManager.getValueFromVectorParam(datiW_MESSAGE.get(m), 1).getValue();
              String message_subject = (String) SqlManager.getValueFromVectorParam(datiW_MESSAGE.get(m), 2).getValue();

              String recipientlist = "";
              String selectW_RECIPIENT = "select recipient_syscon from w_message_out_rec where message_id = ? order by recipient_id";
              List<?> datiW_RECIPIENT = sqlManager.getListVector(selectW_RECIPIENT, new Object[] { message_id });
              if (datiW_RECIPIENT != null && datiW_RECIPIENT.size() > 0) {
                for (int r = 0; r < datiW_RECIPIENT.size() && r < 2; r++) {
                  Long recipient_syscon = (Long) SqlManager.getValueFromVectorParam(datiW_RECIPIENT.get(r), 0).getValue();
                  if (!recipientlist.equals("")) recipientlist += ", ";
                  recipientlist += this.getDescrizioneUtente(recipient_syscon);
                }
                if (datiW_RECIPIENT.size() > 2) recipientlist += " ... (" + (datiW_RECIPIENT.size() - 2) + ")";
              }

              HashMap<String, Object> hMap = new HashMap<String, Object>();
              hMap.put("message_id", message_id);
              if (message_date != null) {
                hMap.put("message_date", UtilityDate.convertiData(message_date, UtilityDate.FORMATO_GG_MM_AAAA));
              }
              hMap.put("message_subject", message_subject);
              hMap.put("recipientlist", recipientlist);
              hMapRecords.add(hMap);
            }
          }
        }

        result.put("iTotalRecords", totalRecords);
        result.put("iTotalDisplay", totalRecords);
        result.put("data", hMapRecords);

      } catch (SQLException e) {
        throw new JspException("Errore durante la lettura dei messaggi", e);
      }

      out.println(result);
      out.flush();
    }

    return null;

  }

  /**
   * Ricava la descrizione
   * 
   * @param syscon
   * @return
   * @throws Exception
   */
  private String getDescrizioneUtente(Long syscon) throws Exception {
    String descrizione = null;
    if (syscon != null) {
      descrizione = (String) sqlManager.getObject("select sysute from usrsys where syscon = ?", new Object[] { syscon });
    }
    return descrizione;
  }

}
