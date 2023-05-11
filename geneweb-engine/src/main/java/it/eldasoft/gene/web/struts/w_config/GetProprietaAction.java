package it.eldasoft.gene.web.struts.w_config;

import it.eldasoft.gene.bl.PropsConfigManager;
import it.eldasoft.gene.db.domain.PropsConfig;

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
 * Action per la lettura di una proprieta' dalla tabella W_CONFIG.
 * Ritorna il valore della chiave della proprietà a partire da CODAPP, CHIAVE
 * presi come argomenti della funzione.
 *
 * @author Luca.Giacomazzo
 */
public class GetProprietaAction extends Action {

  Logger logger = Logger.getLogger(GetProprietaAction.class);

  /**
   * PropsConfigManager.
   */
  private PropsConfigManager propsConfigManager;

  public void setPropsConfigManager(PropsConfigManager propsConfigManager) {
    this.propsConfigManager = propsConfigManager;
  }

  /**
   * Comparatore che per effettuare l'ordinamento
   * di 2 stringhe basandosi sulla loro lunghezza
   * e a parità di lunghezza, sull'ordine alfabetico
   * @author Marcello
   *
   */
  class MyComparatorString implements Comparator {
    public int compare(Object a, Object b) {
      String a1 = (String)a;
      String b1 = (String)b;
      if (a1.length()>b1.length()) return 1;
      else if (a1.length()==b1.length())
        return a1.compareTo(b1);//A parità di lunghezza si ordina alfabeticamente
      else return -1;
    }
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

      List<String> nomiOrdinati = new ArrayList<String>();
      while (paramNames.hasMoreElements()) {
        String nomeParam = (String) paramNames.nextElement();
        nomiOrdinati.add(nomeParam);
      }
      Collections.sort(nomiOrdinati, new MyComparatorString());

      Map<?,?> mappa = request.getParameterMap();
      List<Map<String, String>> risultato = new ArrayList<Map<String, String>>();

      for (int i=0; i < nomiOrdinati.size(); i++) {
        String[] codappChiave = (String[]) mappa.get(nomiOrdinati.get(i));
        PropsConfig prop = this.propsConfigManager.getProperty(codappChiave[0], codappChiave[1]);

        Map<String, String> mappaRiga = new HashMap<String, String>();
        if (prop != null) {
          mappaRiga.put("codapp", prop.getCodApp());
          mappaRiga.put("chiave", prop.getChiave());
          mappaRiga.put("valore", prop.getValore());
        } else {
          mappaRiga.put("codapp", codappChiave[0]);
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
