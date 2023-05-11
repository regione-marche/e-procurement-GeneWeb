/*
 * Created on 09/mag/2013
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
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.profiles.FiltroLivelloUtente;
import it.eldasoft.utils.profiles.cache.DizionarioLivelli;
import it.eldasoft.utils.profiles.domain.Livello;
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

/**
 * Action che filtra sul calendario visualizzato l'elenco delle attivit&agrave; da inserire nello stesso.
 *
 * @author Stefano.Sabbadin
 * @since 2.0.4
 */
public class GetAttivitaCalendarioJSONAction extends DispatchAction {

  Logger             logger = Logger.getLogger(GetAttivitaCalendarioJSONAction.class);

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

  public final ActionForward singolo(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws IOException, ServletException {
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    try {
      PrintWriter out = response.getWriter();

      // data inizio in secondi
      String start = request.getParameter("start");
      Date dataInizio = new Date(Long.parseLong(start) * 1000);
      // data fine in secondi
      String end = request.getParameter("end");
      Date dataFine = new Date(Long.parseLong(end) * 1000);
      // entita' del record su cui e' definito lo scadenzario
      String entita = request.getParameter("entita");
      // chiave del record sui cui e' definito lo scadenzario
      String chiave = request.getParameter("chiave");

      if (logger.isDebugEnabled()) {
        logger.debug("Ricerca attività record di una singola entità con data inizio="
            + dataInizio
            + ", data fine="
            + dataFine
            + ", entita="
            + entita
            + ", chiave="
            + chiave);
      }

      DizionarioTabelle dizionarioTabelle = DizionarioTabelle.getInstance();
      Tabella tabella = dizionarioTabelle.getDaNomeTabella(entita);
      List<Campo> campiChiaveDefinizione = tabella.getCampiKey();

      // definizione dei parametri per l'estrazione dei dati e definizione della query
      StringBuilder select = new StringBuilder(
          "SELECT TIT, DATASCAD, DATACONS, DATAIN, DATAFI, DURATA, ENT, KEY1, KEY2, KEY3, KEY4, KEY5 FROM G_SCADENZ");
      Object[] params = null;
      int i = 0;
      // lavoro su un singolo record di un'entita', pertanto estraggo i dati dalla sola G_SCADENZ
      select.append(" WHERE PREV=0");
      HashMap<String, JdbcParametro> campiChiave = UtilityTags.stringParamsToHashMap(chiave, null);
      params = new Object[tabella.getCampiKey().size() + 4];
      for (Campo campo : campiChiaveDefinizione) {
        params[i++] = campiChiave.get(campo.getNomeFisicoCampo()).getValue();
        select.append(" AND KEY").append(i).append("=?");
      }
      select.append(" AND ENT=? AND PRG=? AND DATASCAD>=? AND DATASCAD<=?");
      params[i++] = entita;
      params[i++] = request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
      params[i++] = dataInizio;
      params[i++] = dataFine;

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
      logger.error("Errore durante l'estrazione delle attività da porre nel calendario", e);
      throw new RuntimeException("Errore durante l'estrazione delle attività da porre nel calendario", e);
    }
    return null;
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
    Calendar dataOggi = Calendar.getInstance();
    dataOggi.set(Calendar.HOUR_OF_DAY, 0);
    dataOggi.set(Calendar.MINUTE, 0);
    dataOggi.set(Calendar.SECOND, 0);
    dataOggi.set(Calendar.MILLISECOND, 0);

    // si estraggono i dati
    @SuppressWarnings("unchecked")
    List<Vector<JdbcParametro>> listaAttivita = sqlManager.getListVector(select.toString(), params);
    if (listaAttivita != null && listaAttivita.size() > 0) {
      String descrizione = null;
      for (Vector<JdbcParametro> riga : listaAttivita) {
        String titolo = SqlManager.getValueFromVectorParam(riga, 0).stringValue();
        Date scadenza = SqlManager.getValueFromVectorParam(riga, 1).dataValue();
        Date consuntivo = SqlManager.getValueFromVectorParam(riga, 2).dataValue();
        Date inizio = SqlManager.getValueFromVectorParam(riga, 3).dataValue();
        Date fine = SqlManager.getValueFromVectorParam(riga, 4).dataValue();
        Long durata = SqlManager.getValueFromVectorParam(riga, 5).longValue();
        String entitaRiga = SqlManager.getValueFromVectorParam(riga, 6).stringValue();

        // si estrae il campo descrizione, dipendente dall'entita' in cui si va a reperire
        if (chiave == null || descrizione == null) {
          StringBuilder sbDescr = new StringBuilder();
          // eseguo la query ogni volta quando si esegue lo scadenzario su tutto, o la prima volta quando la si esegue su un dettaglio
          params = new Object[tabella.getCampiKey().size()];
          for (int j = 0; j < tabella.getCampiKey().size(); j++) {
            Campo campo = tabella.getCampiKey().get(j);
            params[j] = SqlManager.getValueFromVectorParam(riga, 7 + j).getStringValue();
            sbDescr.append(" AND ").append(campo.getNomeCampo()).append("=?");
          }
          descrizione = (String) this.sqlManager.getObject(
              "SELECT DESCR FROM V_SCAD_" + entitaRiga + "_CAL WHERE 1=1" + sbDescr.toString(), params);
        }

        Map<String, Object> mappaRiga = new HashMap<String, Object>();
        mappaRiga.put("titolo", titolo);
        mappaRiga.put("scadenza", UtilityDate.convertiData(scadenza, UtilityDate.FORMATO_AAAA_MM_GG_CON_TRATTINI));// scadenza.getTime()/1000);
        mappaRiga.put("consuntivo", UtilityDate.convertiData(consuntivo, UtilityDate.FORMATO_AAAA_MM_GG_CON_TRATTINI));
        mappaRiga.put("inizio", UtilityDate.convertiData(inizio, UtilityDate.FORMATO_AAAA_MM_GG_CON_TRATTINI));
        mappaRiga.put("fine", UtilityDate.convertiData(fine, UtilityDate.FORMATO_AAAA_MM_GG_CON_TRATTINI));
        mappaRiga.put("durata", durata);
        mappaRiga.put("descrizione", descrizione);
        // stato: 1=da svolgere, 2=completata, 3=scaduta
        mappaRiga.put("stato", (consuntivo != null ? 2 : (dataOggi.getTime().compareTo(scadenza) <= 0 ? 1 : 3)));
        risultato.add(mappaRiga);
      }
    }
  }

  public final ActionForward globale(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws IOException, ServletException {
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    try {
      PrintWriter out = response.getWriter();

      // data inizio in secondi
      String start = request.getParameter("start");
      Date dataInizio = new Date(Long.parseLong(start) * 1000);
      // data fine in secondi
      String end = request.getParameter("end");
      Date dataFine = new Date(Long.parseLong(end) * 1000);
      // entita' del record su cui e' definito lo scadenzario
      String listaEntita = request.getParameter("entita");
      String chiave = null;

      if (logger.isDebugEnabled()) {
        logger.debug("Ricerca attività di record di un'entità con data inizio="
            + dataInizio
            + ", data fine="
            + dataFine
            + ", entita="
            + listaEntita);
      }

      List<Map<String, Object>> risultato = new ArrayList<Map<String, Object>>();

      // per ogni entita' indicata nel parametro eseguo la ricerca, e poi aggiungo alla lista dei risultati
      String[] entitaSplittate = StringUtils.split(listaEntita, ';');
      for (String entita : entitaSplittate) {
        DizionarioTabelle dizionarioTabelle = DizionarioTabelle.getInstance();
        Tabella tabella = dizionarioTabelle.getDaNomeTabella(entita);
        List<Campo> campiChiaveDefinizione = tabella.getCampiKey();

        // definizione dei parametri per l'estrazione dei dati e definizione della query
        StringBuilder select = new StringBuilder(
            "SELECT TIT, DATASCAD, DATACONS, DATAIN, DATAFI, DURATA, ENT, KEY1, KEY2, KEY3, KEY4, KEY5 FROM G_SCADENZ");
        Object[] params = null;
        int i = 0;

        // lavoro su tutti i record di un'entita', pertanto aggiungo in join l'entita
        select.append(" INNER JOIN ").append(entita).append(" ON ");
        for (int j = 0; j < campiChiaveDefinizione.size(); j++) {
          Campo campo = campiChiaveDefinizione.get(j);
          if (j > 0) {
            select.append(" AND ");
          }
          select.append("KEY").append(j + 1).append("=");
          // si suppone che i campi chiave possano essere esclusivamente stringhe e numeri interi
          switch (campo.getTipoColonna()) {
          case Campo.TIPO_INTERO:
            select.append(sqlManager.getDBFunction("inttostr", new String[] {entita + "." + campo.getNomeCampo() }));
            break;
          default:
            select.append(entita).append(".").append(campo.getNomeCampo());
            break;
          }
        }

        // si passa alle condizioni di filtro...
        select.append(" WHERE PREV=0");
        // si verifica se sull'entita' ci sono delle regole per limitare i record visibili all'utente
        ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        FiltroLivelloUtente filtroUtente = profiloUtente.getFiltroLivelloUtente();
        Livello livello = DizionarioLivelli.getInstance().get(entita);
        filtroUtente.setLivello(livello);
        if (filtroUtente.getCondizione() != null) {
          // se c'e' il filtro sui dati visibili dell'utente, aggiungo un parametro di filtro
          params = new Object[4 + 1];
          params[i++] = filtroUtente.getValore();
          select.append(" AND ").append(filtroUtente.getCondizione());
        } else {
          params = new Object[4];
        }

        select.append(" AND ENT=? AND PRG=? AND DATASCAD>=? AND DATASCAD<=?");
        params[i++] = entita;
        params[i++] = request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
        params[i++] = dataInizio;
        params[i++] = dataFine;

        this.getAttivita(chiave, tabella, select, params, risultato);
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
    } catch (GestoreException e) {
      throw new RuntimeException("Errore inaspettato durante la lettura dei dati di G_SCADENZ", e);
    } catch (SQLException e) {
      logger.error("Errore durante l'estrazione delle attività da porre nel calendario", e);
      throw new RuntimeException("Errore durante l'estrazione delle attività da porre nel calendario", e);
    }
    return null;
  }
}
