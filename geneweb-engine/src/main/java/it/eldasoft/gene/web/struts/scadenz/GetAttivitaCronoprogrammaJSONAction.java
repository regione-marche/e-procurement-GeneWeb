/*
 * Created on 28/mag/2013
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
 * Action che filtra estrae l'elenco delle attivit&agrave; da inserire nel cronoprogramma.
 *
 * @author Stefano.Sabbadin
 * @since 2.0.4
 */
public class GetAttivitaCronoprogrammaJSONAction extends DispatchAction {

  Logger             logger = Logger.getLogger(GetAttivitaCronoprogrammaJSONAction.class);

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
    // entita' del record su cui e' definito lo scadenzario
    String entita = request.getParameter("entita");
    // chiave del record sui cui e' definito lo scadenzario
    String chiave = request.getParameter("chiave");

    if (logger.isDebugEnabled()) {
      logger.debug("Ricerca di TUTTE le attività per il cronoprogramma del record dell'entità=" + entita + ", chiave=" + chiave);
    }

    commonExecute(request, response, entita, chiave, "(PREV=1 OR (PREV=0 AND DATACONS IS NOT NULL))");
    return null;
  }

  public final ActionForward previsione(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws IOException, ServletException {
    // entita' del record su cui e' definito lo scadenzario
    String entita = request.getParameter("entita");
    // chiave del record sui cui e' definito lo scadenzario
    String chiave = request.getParameter("chiave");

    if (logger.isDebugEnabled()) {
      logger.debug("Ricerca delle attività di PREVISIONE per il cronoprogramma del record dell'entità=" + entita + ", chiave=" + chiave);
    }

    commonExecute(request, response, entita, chiave, "PREV=1");
    return null;
  }

  public final ActionForward consuntivo(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws IOException, ServletException {
    // entita' del record su cui e' definito lo scadenzario
    String entita = request.getParameter("entita");
    // chiave del record sui cui e' definito lo scadenzario
    String chiave = request.getParameter("chiave");

    if (logger.isDebugEnabled()) {
      logger.debug("Ricerca delle attività di CONSUNTIVO per il cronoprogramma del record dell'entità=" + entita + ", chiave=" + chiave);
    }

    commonExecute(request, response, entita, chiave, "(PREV=0 AND DATACONS IS NOT NULL)");
    return null;
  }

  /**
   * Estrae le attivit&agrave; in base al criterio di filtro in input e crea il messaggio JSON da inserire nella response.
   *
   * @param request
   *        request HTTP
   * @param response
   *        response HTTP
   * @param entita
   *        entit&agrave; di partenza
   * @param chiave
   *        chiave del record dell'entit&agrave; di partenza sul quale viene definito uno scadenzario
   * @throws IOException
   */
  private void commonExecute(final HttpServletRequest request, final HttpServletResponse response, String entita, String chiave,
      String filtro) throws IOException {
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    try {
      PrintWriter out = response.getWriter();

      DizionarioTabelle dizionarioTabelle = DizionarioTabelle.getInstance();
      Tabella tabella = dizionarioTabelle.getDaNomeTabella(entita);
      List<Campo> campiChiaveDefinizione = tabella.getCampiKey();

      // definizione dei parametri per l'estrazione dei dati e definizione della query
      StringBuilder select = new StringBuilder("SELECT TIT, DATAIN, DATASCAD, DATACONS, PREV FROM G_SCADENZ WHERE ").append(filtro);
      Object[] params = null;
      int i = 0;
      // lavoro su un singolo record di un'entita', pertanto estraggo i dati dalla sola G_SCADENZ
      HashMap<String, JdbcParametro> campiChiave = UtilityTags.stringParamsToHashMap(chiave, null);
      params = new Object[tabella.getCampiKey().size() + 2];
      for (Campo campo : campiChiaveDefinizione) {
        params[i++] = campiChiave.get(campo.getNomeFisicoCampo()).getValue();
        select.append(" AND KEY").append(i).append("=?");
      }
      select.append(" AND PRG=? AND ENT=? ORDER BY DATAIN ASC, PREV DESC");
      params[i++] = request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
      params[i++] = entita;

      List<Map<String, Object>> risultato = new ArrayList<Map<String, Object>>();

      getAttivita(chiave, tabella, select, params, risultato);

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
    } catch (GestoreException e) {
      throw new RuntimeException("Errore inaspettato durante la lettura dei dati di G_SCADENZ", e);
    } catch (SQLException e) {
      logger.error("Errore durante l'estrazione delle attività da porre nel cronoprogramma", e);
      throw new RuntimeException("Errore durante l'estrazione delle attività da porre nel cronoprogramma", e);
    }
  }

  /**
   * Estrae l'elenco delle attivit&agrave; individuato dalla query in input su un'entit&agrave; e popola la lista dei risultati da inviare
   * in formato JSON.
   *
   * @param chiave
   *        chiave eventuale del singolo record, null se vanno estratte tutte le occorrenze su un'entit&agrave;
   * @param tabella
   *        tabella su cui effettuare le ricerche
   * @param select
   *        select da eseguire
   * @param params
   *        parametri da utilizzare nella select
   * @param risultato
   *        risultato JSON da popolare
   * @throws SQLException
   * @throws GestoreException
   */
  private void getAttivita(String chiave, Tabella tabella, StringBuilder select, Object[] params, List<Map<String, Object>> risultato)
      throws SQLException, GestoreException {
    // si estraggono i dati
    @SuppressWarnings("unchecked")
    List<Vector<JdbcParametro>> listaAttivita = sqlManager.getListVector(select.toString(), params);
    if (listaAttivita != null && listaAttivita.size() > 0) {
      String titoloPrec = null;
      for (Vector<JdbcParametro> riga : listaAttivita) {
        String titolo = SqlManager.getValueFromVectorParam(riga, 0).stringValue();
        Date inizio = SqlManager.getValueFromVectorParam(riga, 1).dataValue();
        Date fine = SqlManager.getValueFromVectorParam(riga, 2).dataValue();
        if (fine.before(inizio)) {
          // se per qualche motivo si anticipano talmente tanto i consuntivi per cui il termine e' precedente l'inizio dell'attivita',
          // aggiorno l'inizio con la data termine
          inizio = fine;
        }
        // Date consuntivo = SqlManager.getValueFromVectorParam(riga, 3).dataValue();
        Long previsionale = SqlManager.getValueFromVectorParam(riga, 4).longValue();

        Map<String, Object> mappaRiga = new HashMap<String, Object>();
        mappaRiga.put("name", titolo.equals(titoloPrec) ? "" : titolo);
        if (previsionale == 1) {
          mappaRiga.put("desc", "Previsione");
        } else {
          mappaRiga.put("desc", "Consuntivo");
        }
        List<Map<String, String>> valori = new ArrayList<Map<String, String>>();
        Map<String, String> mappaValori = new HashMap<String, String>();
        mappaValori.put("from", "/Date(" + inizio.getTime() + ")/");
        mappaValori.put("to", "/Date(" + fine.getTime() + ")/");
        mappaValori.put("label", titolo);
        StringBuilder tooltip = new StringBuilder();
        tooltip.append("<b>Attivit&agrave;: ").append(titolo).append("</b>");
        tooltip.append("<br/>Tipologia: ").append(previsionale == 1 ? "Previsione" : "Consuntivo");
        tooltip.append("<br/>Inizio: ").append(UtilityDate.convertiData(inizio, UtilityDate.FORMATO_GG_MM_AAAA));
        tooltip.append("<br/>Fine: ").append(UtilityDate.convertiData(fine, UtilityDate.FORMATO_GG_MM_AAAA));
        if (previsionale == 0) {
          tooltip.append("<br/>Stato: Completata");
        }
        mappaValori.put("desc", tooltip.toString());
        if (previsionale == 0) {
          // il consuntivo va colorato in verde
          mappaValori.put("customClass", "ganttGreen");
        }
        valori.add(mappaValori);
        mappaRiga.put("values", valori);
        risultato.add(mappaRiga);
        titoloPrec = titolo;
      }
    }
  }
}
