/*
 * Created on 29/ott/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.bl.SqlManager;
//import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
//import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
//import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
//import it.eldasoft.utils.sicurezza.ICriptazioneByte;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action che ritorna tutti gli utenti della USRSYS e permette di associare e dissociare gli stessi alla Stazione appaltante.
 *
 * @author Mirco.Franzoni
 * @since 2.0.7
 */
public class GetAccountUfficioJSONAction extends Action {

  Logger             logger = Logger.getLogger(GetAccountUfficioJSONAction.class);

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

  /**
   * Estrae l'elenco di tutti gli utenti evidenziando quelli associati alla stazione appaltante in formato JSON.
   *
   * @param codein
   *        codice stazione appaltante per ricavare gli utenti associati
   * @param onlyActive
   *        true se devo recuperare solo quelli associati, false se devo recuperare turri gli utenti
   * @throws SQLException
   * @throws GestoreException
   * @throws CriptazioneException
   */
  private Map<String, Object> getUtenti(String codein, boolean onlyActive) throws SQLException, GestoreException, CriptazioneException {

    Map<String, Object> risultato = new HashMap<String, Object>();
    String where = " where 1 = 0 ";
    // ricavo gli utenti attivi sulla stazione appaltante
    Map<String, Object> usr_ein = new HashMap<String, Object>();
    @SuppressWarnings("unchecked")
    List<Vector<JdbcParametro>> usrList = sqlManager.getListVector("select syscon from usr_ein where codein = ?", new Object[] {codein });
    if (usrList != null && usrList.size() > 0) {
      for (Vector<JdbcParametro> riga : usrList) {
        Long codice = SqlManager.getValueFromVectorParam(riga, 0).longValue();
        usr_ein.put(codice.toString(), codein);
        if (onlyActive) {
        	where += " OR syscon=" + codice.toString();
        }
      }
    }
    // si estraggono i dati
    @SuppressWarnings("unchecked")
    List<Vector<JdbcParametro>> listaUtenti = sqlManager.getListVector("select syscon, sysute, syslogin, sysdisab from usrsys" + ((onlyActive)? where:""), null);
    List<Object> utenti = new ArrayList<Object>();
    if (listaUtenti != null && listaUtenti.size() > 0) {
      
      for (Vector<JdbcParametro> riga : listaUtenti) {

        Long codice = SqlManager.getValueFromVectorParam(riga, 0).longValue();
        String descrizione = SqlManager.getValueFromVectorParam(riga, 1).stringValue();
        String login = SqlManager.getValueFromVectorParam(riga, 2).stringValue();
        String disabilitato = SqlManager.getValueFromVectorParam(riga, 3).stringValue();
        //ICriptazioneByte decriptatore = FactoryCriptazioneByte.getInstance(
        //    ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI), login.getBytes(),
        //    ICriptazioneByte.FORMATO_DATO_CIFRATO);
        Map<String, Object> mappaRiga = new HashMap<String, Object>();
        mappaRiga.put("codice", codice);
        mappaRiga.put("descrizione", descrizione);
        mappaRiga.put("login", login);
        if (usr_ein.containsKey(codice.toString())) {
          mappaRiga.put("active", 1);
        } else {
          mappaRiga.put("active", 0);
        }
        mappaRiga.put("disabled", (disabilitato != null && disabilitato.equals("1"))?1:0);
        utenti.add(mappaRiga);
      }
      
    }
    risultato.put("draw", 0);
    risultato.put("recordsTotal", listaUtenti.size());
    risultato.put("recordsFiltered", listaUtenti.size());
    risultato.put("data", utenti);
    return risultato;
  }

  @Override
  public final ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
      final HttpServletResponse response) throws IOException, ServletException {
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    try {
      PrintWriter out = response.getWriter();

      // Codice stazione appaltante
      String codein = request.getParameter("codein");
      // Tipo azione
      String modoAperturaScheda = request.getParameter("modoAperturaScheda");
      Map<String, Object> risultato = getUtenti(codein, modoAperturaScheda.equals("VISUALIZZA"));

      // si popola il risultato in formato JSON
      JSONArray jsonArray = JSONArray.fromObject(risultato);
      String json = jsonArray.toString();
      out.println(json.substring(1, json.length() - 1));
      if (logger.isDebugEnabled()) {
        logger.debug("Risposta JSON=" + jsonArray);
      }
      out.flush();
    } catch (IOException e) {
      logger.error("Errore durante la lettura del writer della response", e);
      throw e;
    } catch (CriptazioneException e) {
      throw new RuntimeException("Errore inaspettato durante la criptazione dei dati di USRSYS", e);
    } catch (GestoreException e) {
      throw new RuntimeException("Errore inaspettato durante la lettura dei dati di SYS_EIN", e);
    } catch (SQLException e) {
      logger.error("Errore durante l'estrazione degli utenti per la stazione appaltante", e);
      throw new RuntimeException("Errore durante l'estrazione degli utenti per la stazione appaltante", e);
    }
    return null;
  }
}
