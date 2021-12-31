/*
 * Created on 22/09/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.scadenz;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

/**
 * Action che filtra estrae l'elenco delle attivit&agrave; da inserire nel
 * cronoprogramma.
 * 
 */
public class GetAttivitaTimelineJSONAction extends DispatchAction {

  Logger             logger = Logger.getLogger(GetAttivitaTimelineJSONAction.class);

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

  public final ActionForward all(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws IOException, ServletException {

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");

    String entita = request.getParameter("entita");
    String chiave = request.getParameter("chiave");

    try {
      PrintWriter out = response.getWriter();
      JSONArray jsonArray = new JSONArray();

      DizionarioTabelle dizionarioTabelle = DizionarioTabelle.getInstance();
      Tabella tabella = dizionarioTabelle.getDaNomeTabella(entita);
      List<Campo> campiChiaveDefinizione = tabella.getCampiKey();

      StringBuilder selectG_SCADENZ = new StringBuilder("select tit, datafi, datascad, datacons, id from g_scadenz ");
      Object[] paramsG_SCADENZ = null;
      int i = 0;
      HashMap<String, JdbcParametro> campiChiave = UtilityTags.stringParamsToHashMap(chiave, null);
      paramsG_SCADENZ = new Object[tabella.getCampiKey().size() + 2];
      for (Campo campo : campiChiaveDefinizione) {
        if (i == 0) {
          selectG_SCADENZ.append(" where ");
        } else {
          selectG_SCADENZ.append(" and ");
        }
        paramsG_SCADENZ[i++] = campiChiave.get(campo.getNomeFisicoCampo()).getValue();
        selectG_SCADENZ.append(" key").append(i).append("=?");
      }
      selectG_SCADENZ.append(" and prg=? and ent=? ");
      paramsG_SCADENZ[i++] = request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
      paramsG_SCADENZ[i++] = entita;

      Date dataMinima = null;
      Date dataMassima = null;

      List<?> datiG_SCADENZ = sqlManager.getListVector(selectG_SCADENZ.toString(), paramsG_SCADENZ);
      if (datiG_SCADENZ != null && datiG_SCADENZ.size() > 0) {
        List<Object> listG_SCADENZ = new Vector<Object>();
        for (int s = 0; s < datiG_SCADENZ.size(); s++) {
          String tit = (String) SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(s), 0).getValue();
          Date datafi = (Date) SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(s), 1).getValue();
          Date datascad = (Date) SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(s), 2).getValue();

          if (dataMinima == null || (dataMinima != null && datafi != null && dataMinima.after(datafi))) dataMinima = datafi;
          if (dataMinima == null || (dataMinima != null && datascad != null && dataMinima.after(datascad))) dataMinima = datascad;
          if (dataMassima == null || (dataMassima != null && datafi != null && dataMassima.before(datafi))) dataMassima = datafi;
          if (dataMassima == null || (dataMassima != null && datascad != null && dataMassima.before(datascad))) dataMassima = datascad;
          
          boolean b_consuntivo = false;
          Date datacons = (Date) SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(s), 3).getValue();
          if (datacons != null) b_consuntivo = true;
          
          Long id = (Long) SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(s), 4).getValue();
          
          Object[] row = new Object[6];
          row[0] = tit;
          row[1] = datafi.getTime();
          row[2] = datascad.getTime();
          row[3] = b_consuntivo;
          row[4] = id;
          row[5] = false;
          listG_SCADENZ.add(row);
        }
        
        if (dataMinima != null) {
          Calendar c = Calendar.getInstance(); 
          c.setTime(dataMinima); 
          c.add(Calendar.DATE, -10);
          dataMinima = c.getTime();
        }
          
        if (dataMassima != null) {
          Calendar c = Calendar.getInstance(); 
          c.setTime(dataMassima); 
          c.add(Calendar.DATE, 10);
          dataMassima = c.getTime();
        }
        
        // Aggiungo la data odierna
        Object[] row = new Object[6];
        row[0] = "Oggi";
        row[1] = new Date().getTime();
        row[2] = new Date().getTime();
        row[3] = "";
        row[4] = "";
        row[5] = true;
        listG_SCADENZ.add(row);
        
        jsonArray.add(dataMinima.getTime());
        jsonArray.add(dataMassima.getTime());
        jsonArray.add(listG_SCADENZ);
        out.println(jsonArray);
      }

      out.flush();

    } catch (IOException e) {
      logger.error("Errore durante la lettura della lista delle scadenze", e);
      throw e;
    } catch (SQLException e) {
      logger.error("Errore durante la lettura della lista delle scadenze", e);
      throw new RuntimeException("Errore durante la lettura della lista delle scadenze", e);
    }

    return null;
  }

}
