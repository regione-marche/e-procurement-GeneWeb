/*
 * Created on 18/12/2019
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import net.sf.json.JSONObject;

public class GetListaW_TAGSLISTAction extends ActionAjaxLogged {

  static Logger      logger = Logger.getLogger(GetListaW_TAGSLISTAction.class);

  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String target = null;
    String messageKey = null;

    DataSourceTransactionManagerBase.setRequest(request);

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    JSONObject result = new JSONObject();
    int total = 0;
    List<HashMap<String, Object>> hMapC0 = new ArrayList<HashMap<String, Object>>();

    String modoapertura = request.getParameter("modoapertura");
    String codapp = request.getParameter("codapp");
    String tagcod = request.getParameter("tagcod");

    try {

      if ("VISUALIZZA".equals(modoapertura)) {
        String selectW_TAGSLIST = "select tagentity, tagfield, taginfo from w_tagslist where codapp = ? and tagcod = ?";
        List<?> datiW_TAGSLIST = sqlManager.getListVector(selectW_TAGSLIST, new Object[] { codapp, tagcod });
        if (datiW_TAGSLIST != null && datiW_TAGSLIST.size() > 0) {
          for (int t = 0; t < datiW_TAGSLIST.size(); t++) {
            total++;

            HashMap<String, Object> hMap = new HashMap<String, Object>();
            String tagentity = (String) SqlManager.getValueFromVectorParam(datiW_TAGSLIST.get(t), 0).getValue();
            String tagfield = (String) SqlManager.getValueFromVectorParam(datiW_TAGSLIST.get(t), 1).getValue();

            String descrizione = "";

            if (tagentity != null) {
              String coc_mne_uni = tagfield + "." + tagentity + ".%";
              descrizione = (String) sqlManager.getObject("select coc_des from c0campi where coc_mne_uni like ?",
                  new Object[] { coc_mne_uni });
            }

            hMap.put("tagentity", tagentity);
            hMap.put("tagfield", tagfield);
            hMap.put("descrizione", descrizione);
            hMap.put("taginfo", (String) SqlManager.getValueFromVectorParam(datiW_TAGSLIST.get(t), 2).getValue());
            hMap.put("associato", true);
            hMapC0.add(hMap);
          }

        }

      } else {

        String selectC0CAMPI = "select distinct coc_mne_uni, coc_des from c0campi where c0c_tip = 'E'";
        String selectW_TAGSLIST = "select tagentity, tagfield, taginfo from w_tagslist where codapp = ? and tagcod = ?";
        String selectW_TASGLIST_NULL = "select tagfield, taginfo from w_tagslist where codapp = ? and tagcod = ? and tagentity is null";

        List<?> datiC0CAMPI = sqlManager.getListVector(selectC0CAMPI, new Object[] {});
        List<?> hMapW_TAGSLIST = sqlManager.getListHashMap(selectW_TAGSLIST, new Object[] { codapp, tagcod });

        if (datiC0CAMPI != null && datiC0CAMPI.size() > 0) {
          for (int c = 0; c < datiC0CAMPI.size(); c++) {

            total++;

            HashMap<String, Object> hMap = new HashMap<String, Object>();

            String coc_mne_uni = (String) SqlManager.getValueFromVectorParam(datiC0CAMPI.get(c), 0).getValue();
            int firstPoint = coc_mne_uni.indexOf(".");
            int secondPoint = coc_mne_uni.indexOf(".", firstPoint + 1);
            String tagfield = coc_mne_uni.substring(0, firstPoint);
            String tagentity = coc_mne_uni.substring(firstPoint + 1, secondPoint);

            hMap.put("tagentity", tagentity);
            hMap.put("tagfield", tagfield);
            hMap.put("descrizione", (String) SqlManager.getValueFromVectorParam(datiC0CAMPI.get(c), 1).getValue());

            if (hMapW_TAGSLIST != null && hMapW_TAGSLIST.size() > 0) {
              boolean associato = false;
              for (int ht = 0; ht < hMapW_TAGSLIST.size() && associato == false; ht++) {
                HashMap<?, ?> _o = (HashMap<?, ?>) hMapW_TAGSLIST.get(ht);
                String _e = (String) ((JdbcParametro) _o.get("TAGENTITY")).getValue();
                String _f = (String) ((JdbcParametro) _o.get("TAGFIELD")).getValue();
                if (tagentity.equals(_e) && tagfield.equals(_f)) {
                  hMap.put("taginfo", (String) ((JdbcParametro) _o.get("TAGINFO")).getValue());
                  associato = true;
                } else {
                  hMap.put("taginfo", null);
                }
                hMap.put("associato", associato);
              }
            } else {
              hMap.put("taginfo", null);
              hMap.put("associato", false);
            }
            hMapC0.add(hMap);
          }
        }

        // Aggiunta dei tag non associati ad entita' (sono quelli per i titoli
        // o
        // i campi dinamici)
        List<?> datiTAGSLIST_NULL = sqlManager.getListVector(selectW_TASGLIST_NULL, new Object[] { codapp, tagcod });
        if (datiTAGSLIST_NULL != null && datiTAGSLIST_NULL.size() > 0) {
          for (int n = 0; n < datiTAGSLIST_NULL.size(); n++) {
            total++;

            HashMap<String, Object> hMap = new HashMap<String, Object>();
            hMap.put("tagentity", null);
            hMap.put("tagfield", (String) SqlManager.getValueFromVectorParam(datiTAGSLIST_NULL.get(n), 0).getValue());
            hMap.put("descrizione", null);
            hMap.put("taginfo", (String) SqlManager.getValueFromVectorParam(datiTAGSLIST_NULL.get(n), 1).getValue());
            hMap.put("associato", true);
            hMapC0.add(hMap);
          }

        }

      }

      result.put("iTotalRecords", total);
      result.put("iTotalDisplayRecords", total);
      result.put("data", hMapC0);

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
