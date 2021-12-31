package it.eldasoft.gene.web.struts.w_config;

import it.eldasoft.gene.bl.PropsConfigManager;
import it.eldasoft.gene.db.domain.PropsConfig;
import it.eldasoft.gene.db.domain.WsdmPropsConfig;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;


/**
 * Action per la lettura di una proprieta' dalla tabella WSDMCONFIPRO.
 * Ritorna il valore della chiave della proprietà a partire da IDCONFI, CHIAVE
 * presi come argomenti della funzione.
 *
 * @author Luca.Giacomazzo
 */
public class GetWsdmProprietaAction extends Action {

  Logger logger = Logger.getLogger(GetProprietaAction.class);

  /**
   * PropsConfigManager.
   */
  private PropsConfigManager propsConfigManager;

  public void setPropsConfigManager(PropsConfigManager propsConfigManager) {
    this.propsConfigManager = propsConfigManager;
  }

  @Override
  public ActionForward execute(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) throws Exception {

    if (this.logger.isDebugEnabled()) {
      logger.debug("execute: inizio metodo");
    }

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    try {
      PrintWriter out = response.getWriter();

      Enumeration<?> paramNames = request.getParameterNames();

      Map<?,?> mappa = request.getParameterMap();
      List<Map<String, String>> risultato = new ArrayList<Map<String, String>>();

      while (paramNames.hasMoreElements()) {
        String[] codappChiave = (String[]) mappa.get(paramNames.nextElement());
        String idconfi = codappChiave[0];
        WsdmPropsConfig prop = this.propsConfigManager.getWsdmProperty(new Long(idconfi), codappChiave[1]);

        Map<String, String> mappaRiga = new HashMap<String, String>();
        if (prop != null) {
          mappaRiga.put("chiave", prop.getChiave());
          mappaRiga.put("valore", prop.getValore());
        } else {
          mappaRiga.put("chiave", codappChiave[1]);
          mappaRiga.put("valore", null);
        }
        risultato.add(mappaRiga);
      }

      // si popola il risultato in formato JSON
      JSONArray jsonArray = JSONArray.fromObject(risultato.toArray());

      out.println(jsonArray);
      if (logger.isDebugEnabled()) {
        logger.debug("Risposta JSON=" + jsonArray);
      }
      out.flush();
    } catch (IOException e) {
      logger.error("Errore durante la lettura del writer della response", e);
      throw e;
    }

    if (logger.isDebugEnabled()) {
      logger.debug("execute: fine metodo");
    }

    return null;
  }

}
