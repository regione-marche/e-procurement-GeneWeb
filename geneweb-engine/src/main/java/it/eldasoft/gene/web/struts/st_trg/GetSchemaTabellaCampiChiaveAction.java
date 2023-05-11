package it.eldasoft.gene.web.struts.st_trg;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioSchemi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Schema;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.spring.UtilitySpring;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * DispatchAction per la valorizzazione delle dropdown Schema, Tabella e campi chiave
 * presenti nella pagina di ricerca della storia delle modifiche (trova-st_trg.jsp).
 * 
 * @author Luca.Giacomazzo
 */
public class GetSchemaTabellaCampiChiaveAction extends DispatchAction {

  Logger             logger = Logger.getLogger(GetSchemaTabellaCampiChiaveAction.class);
  
  private SqlManager sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  
  

  /**
   * Caricamento in un oggetto JSON della lista delle tabelle relative allo schema  per l'autocomplete
   * presente nella pagina di ricerca della storia delle modifiche (trova-st_trg.jsp)
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public final ActionForward getTabelle(final ActionMapping mapping, final ActionForm form,
      final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
    
    if (logger.isDebugEnabled()) {
      logger.debug("getTabelle: inizio metodo");
    }
    
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    try {
      PrintWriter out = response.getWriter();
      
      String codiceSchema = request.getParameter("schema");
      
      DizionarioSchemi dizionarioSchemi = DizionarioSchemi.getInstance();
      DizionarioTabelle dizionarioTabelle = DizionarioTabelle.getInstance();
      
      Schema schema = dizionarioSchemi.get(codiceSchema);
      
      List<String> listaMnemoniciTabelle = schema.getMnemoniciTabellePerRicerche();

      List<Map<String, String>> risultato = new ArrayList<Map<String, String>>();
      
      String mnemonicoTabella = null;
      for (int q=0; q < listaMnemoniciTabelle.size(); q++) {
        Map<String, String> mappaRiga = new HashMap<String, String>();
        
        mnemonicoTabella = listaMnemoniciTabelle.get(q);
        
        Tabella tabella = dizionarioTabelle.get(mnemonicoTabella);
        
        mappaRiga.put("chiave", tabella.getNomeTabella().replaceAll("[\"]","\\\\\""));
        mappaRiga.put("descrizione", tabella.getNomeTabella().replaceAll("[\"]","\\\\\"").concat(" - ").concat(
            tabella.getDescrizione().replaceAll("[\"]","\\\\\"")));
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
      logger.debug("getTabelle: fine metodo");
    }
    return null;
  }
  


  
  /**
   * Caricamento in un oggetto JSON della lista delle tabelle relative allo schema  per l'autocomplete
   * presente nella pagina di ricerca della storia delle modifiche (trova-st_trg.jsp)
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public final ActionForward getEntita(final ActionMapping mapping, final ActionForm form,
      final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException, SQLException {
    if (logger.isDebugEnabled()) {
      logger.debug("getEntita: inizio metodo");
    }
    
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    try {
      PrintWriter out = response.getWriter();
      DizionarioTabelle dizionarioTabelle = DizionarioTabelle.getInstance();
      
      if(sqlManager == null){logger.debug("its null");}
      
      List gruppiTab = sqlManager.getListVector("select distinct st_table from st_trg",new Object[]{} );
      List <String> entita = new ArrayList<String>();
      String tempEntit;
      if (gruppiTab != null && gruppiTab.size() > 0) {
        for (int i = 0; i < gruppiTab.size(); i++) {
          tempEntit = (String) SqlManager.getValueFromVectorParam(
              gruppiTab.get(i), 0).getValue();
          entita.add(tempEntit);
          }
      } 
      String entitaSingola = null;
      List<Map<String, String>> risultato = new ArrayList<Map<String, String>>();
      
      for (int i = 0; i < entita.size(); i++) {
        Map<String, String> mappaRiga = new HashMap<String, String>();
        entitaSingola = entita.get(i);
        Tabella tabella = dizionarioTabelle.getDaNomeTabella(entitaSingola);
        if(tabella != null){
        mappaRiga.put("chiave", entitaSingola.replaceAll("[\"]","\\\\\""));
        mappaRiga.put("descrizione", entitaSingola.replaceAll("[\"]","\\\\\"").concat(" - ").concat(
            tabella.getDescrizione().replaceAll("[\"]","\\\\\"")));
        risultato.add(mappaRiga);}
        
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
      logger.debug("getTabelle: fine metodo");
    }
    return null;
  }
  
  
  /**
   * Caricamento in un oggetto JSON della lista delle chiavi da inserire nei caompi di filtro
   * presenti nella pagina di ricerca della storia delle modifiche (trova-st_trg.jsp)
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public final ActionForward getChiavi(final ActionMapping mapping, final ActionForm form,
      final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException, SQLException {
    
    if (logger.isDebugEnabled()) {
      logger.debug("getCampiChiave: inizio metodo");
    }
    
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
      PrintWriter out = response.getWriter();
      
      String nomeTabella = request.getParameter("tabella");
      
      DizionarioTabelle dizionarioTabelle = DizionarioTabelle.getInstance();
      DizionarioCampi dizionarioCampi = DizionarioCampi.getInstance();
      
      List chiaviTab = sqlManager.getListVector("SELECT DISTINCT st_key1, st_key2, st_key3, st_key4,st_key5,st_key6, st_key7, st_key8 FROM st_trg where st_table=?",new Object[]{nomeTabella} );
      
      List <List<String>> listaChiavi = new ArrayList<List<String>>();
      String tempChiave;
      List<String> chiavi;
      if (chiaviTab != null && chiaviTab.size() > 0) {
        for (int i = 0; i < chiaviTab.size(); i++) {
          chiavi = new ArrayList<String>();
          chiavi.clear();
          for (int n = 0; n < 8; n++) {
            tempChiave = (String) SqlManager.getValueFromVectorParam(
            chiaviTab.get(i), n).getValue();
              if(tempChiave != null && (tempChiave.length()>0)){
                chiavi.add(tempChiave);
                for(int m = 0; m < chiavi.size(); m++){
                }
              }
            }
          listaChiavi.add(chiavi);
         }
      } 
      List<Map<String, String>> risultatoSingolaChiave;
      List<List<Map<String, String>>> risultatoDef = new ArrayList<List<Map<String, String>>>();
      
      for (int m=0; m < listaChiavi.size(); m++) {
        risultatoSingolaChiave = new ArrayList<Map<String, String>>();
        for (int q=0; q < listaChiavi.get(m).size(); q++) {
          Map<String, String> mappaRiga = new HashMap<String, String>();  
          mappaRiga.put("nomeCampo", listaChiavi.get(m).get(q));
          risultatoSingolaChiave.add(mappaRiga);
        }
        risultatoDef.add(risultatoSingolaChiave);
      }  

      // si popola il risultato in formato JSON
      JSONArray jsonArray = JSONArray.fromObject(risultatoDef.toArray());
      out.println(jsonArray);
      if (logger.isDebugEnabled()) {
        logger.debug("Risposta JSON=" + jsonArray);
      }
      out.flush();
    
    if (logger.isDebugEnabled()) {
      logger.debug("getCampiChiave: fine metodo");
    }
    return null;
  }
  
}
