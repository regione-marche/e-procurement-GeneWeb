/*
 * Created on 28/06/2017
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
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
import net.sf.json.JSONObject;

public class GetWTagsAction extends ActionAjaxLogged {

  static Logger      logger = Logger.getLogger(GetWTagsAction.class);

  private SqlManager sqlManager;

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
    JSONObject result = new JSONObject();
    List<HashMap<String, String>> w_tagsHMap = new ArrayList<HashMap<String, String>>();
    List<HashMap<String, String>> w_tagslistHMap = new ArrayList<HashMap<String, String>>();

    String q = request.getParameter("q");
    String codapp = request.getParameter("codapp");
    String profilo = request.getParameter("profilo");
    String tagentity = request.getParameter("tagentity");
    String tagfield = request.getParameter("tagfield");
    String tagcod = request.getParameter("tagcod");

    try {

      // Lista dei possibili "tag"
      if ("tags".equals(q)) {
        List<?> datiW_TAGS = sqlManager.getListVector(
            "select tagcod, tagview, tagdesc, tagcolor, tagbordercolor, tagprofili from w_tags where codapp = ? and (tagvis is null or tagvis = '1') order by tagview",
            new Object[] { codapp });
        if (datiW_TAGS != null && datiW_TAGS.size() > 0) {
          for (int t = 0; t < datiW_TAGS.size(); t++) {

            boolean _ok = false;

            if (profilo == null) {
              _ok = true;
            } else {
              String tagprofili = (String) SqlManager.getValueFromVectorParam(datiW_TAGS.get(t), 5).getValue();
              if (tagprofili == null) {
                _ok = true;
              } else {
                String[] arrayProfili = tagprofili.trim().split(",");
                for (int ap = 0; ap < arrayProfili.length; ap++) {
                  if (arrayProfili[ap].trim().equals(profilo.trim())) {
                    _ok = true;
                  }
                }
              }
            }

            if (_ok) {
              HashMap<String, String> hMap = new HashMap<String, String>();
              hMap.put("c", (String) SqlManager.getValueFromVectorParam(datiW_TAGS.get(t), 0).getValue());
              hMap.put("v", (String) SqlManager.getValueFromVectorParam(datiW_TAGS.get(t), 1).getValue());
              hMap.put("d", (String) SqlManager.getValueFromVectorParam(datiW_TAGS.get(t), 2).getValue());
              hMap.put("cl", (String) SqlManager.getValueFromVectorParam(datiW_TAGS.get(t), 3).getValue());
              hMap.put("bcl", (String) SqlManager.getValueFromVectorParam(datiW_TAGS.get(t), 4).getValue());
              w_tagsHMap.add(hMap);
            }
          }
          result.put("tags", w_tagsHMap);
        }
      } else if ("tagslist".equals(q)) {
        // Lista dei campi configurati per le informazioni di integrazione
        List<?> datiW_TAGSLIST = sqlManager.getListVector(
            "select w_tagslist.tagentity, w_tagslist.tagfield, w_tagslist.tagcod, w_tags.tagview "
                + " from w_tagslist, w_tags where w_tagslist.codapp = ? and w_tags.codapp = w_tagslist.codapp and w_tags.tagcod = w_tagslist.tagcod"
                + " order by w_tagslist.tagentity, w_tagslist.tagfield, w_tags.tagview",
            new Object[] { codapp });
        if (datiW_TAGSLIST != null && datiW_TAGSLIST.size() > 0) {
          for (int a = 0; a < datiW_TAGSLIST.size(); a++) {
            HashMap<String, String> hMap = new HashMap<String, String>();
            hMap.put("e", (String) SqlManager.getValueFromVectorParam(datiW_TAGSLIST.get(a), 0).getValue());
            hMap.put("f", (String) SqlManager.getValueFromVectorParam(datiW_TAGSLIST.get(a), 1).getValue());
            hMap.put("c", (String) SqlManager.getValueFromVectorParam(datiW_TAGSLIST.get(a), 2).getValue());
            hMap.put("v", (String) SqlManager.getValueFromVectorParam(datiW_TAGSLIST.get(a), 3).getValue());
            w_tagslistHMap.add(hMap);
          }
          result.put("tagslist", w_tagslistHMap);
        }
      } else if ("taginfo".equals(q)) {
        if (tagentity == null || "".equals(tagentity.trim()) || "null".equals(tagentity.trim())) {
          String taginfo = (String) sqlManager.getObject(
              "select taginfo from w_tagslist where codapp = ? and tagentity is null and tagfield = ? and tagcod = ?",
              new Object[] { codapp, tagfield, tagcod });
          result.put("taginfo", taginfo);
        } else {
          String taginfo = (String) sqlManager.getObject(
              "select taginfo from w_tagslist where codapp = ? and tagentity = ? and tagfield = ? and tagcod = ?",
              new Object[] { codapp, tagentity, tagfield, tagcod });
          result.put("taginfo", taginfo);
        }
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
