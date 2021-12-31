/*
 * Created on 25/giu/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori.plugin;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

/**
 * Plugin per gestire l'apertura della pagina delle categorie di una impresa
 * 
 * @author Luca.Giacomazzo
 */
public class GestorePluginCATE extends AbstractGestorePreload {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(GestorePluginCATE.class);
  
  public GestorePluginCATE(BodyTagSupportGene tag) {
    super(tag);
  }
  
  public void doBeforeBodyProcessing(PageContext pageContext, String modoAperturaScheda)
      throws JspException {
    
    if(logger.isDebugEnabled()) logger.debug("doBeforeBodyProcessing: inizio metodo");
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager",
        pageContext, GeneManager.class);
    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        pageContext, TabellatiManager.class);
    
    long numeroCategorieImpresa = -1;
    List listaCategorieImpresa = new ArrayList();
    
    try {
      String codiceImpresa = (String) UtilityTags.getParametro(pageContext,
          UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
      codiceImpresa = codiceImpresa.substring(codiceImpresa.indexOf(":") + 1);
      
      numeroCategorieImpresa = geneManager.countOccorrenze("CATE",
          "CODIMP1 = ?", new Object[]{codiceImpresa});
     
      String sql =
          "select CATE.CODIMP1, CATE.CATISC,  CATE.NUMCLA, CATE.IMPISC, " +
                 "CAIS.DESCAT,  CAIS.ACONTEC, CAIS.QUAOBB, CAIS.TIPLAVG " +
            "from CATE, CAIS " + 
           "where CATE.CODIMP1 = ? " +
             "and CATE.CATISC = CAIS.CAISIM " +
        "order by CAIS.CAISORD asc";

      if(numeroCategorieImpresa > 0) {
        listaCategorieImpresa = sqlManager.getListVector(sql,
            new Object[]{codiceImpresa});
        
        pageContext.setAttribute("listaCategorieImpresa", listaCategorieImpresa,
            PageContext.REQUEST_SCOPE);
      }

      if(UtilityTags.SCHEDA_MODO_MODIFICA.equals(modoAperturaScheda)){
        pageContext.setAttribute("importiIscrizioneLavori",
            tabellatiManager.getTabellato("G_z09"), PageContext.REQUEST_SCOPE);
               
        List<Tabellato> listaImporti =tabellatiManager.getTabellato("G_z07");
        this.aggiornaListaImporti(listaImporti);
        pageContext.setAttribute("importiIscrizioneForniture",
            listaImporti, PageContext.REQUEST_SCOPE);
        
        listaImporti = tabellatiManager.getTabellato("G_z08");
        this.aggiornaListaImporti(listaImporti);
        pageContext.setAttribute("importiIscrizioneServizi",
            listaImporti, PageContext.REQUEST_SCOPE);
        
        listaImporti = tabellatiManager.getTabellato("G_z11");
        this.aggiornaListaImporti(listaImporti);
        pageContext.setAttribute("importiIscrizioneLavori150",
            listaImporti, PageContext.REQUEST_SCOPE);
      }

      
      
      String entitaGeneratoreAttributi = geneManager.getNomeEntitaDinamica("CATE");
      if(entitaGeneratoreAttributi != null && entitaGeneratoreAttributi.length() > 0){
        geneManager.setCampiGeneratoreAttributi(pageContext, entitaGeneratoreAttributi,
            "CATE", listaCategorieImpresa.size() + 5);
        // 5 rappresenta il numero di sezioni nascoste che possono essere aggiunte
        
        if(listaCategorieImpresa != null && listaCategorieImpresa.size() > 0){
          List listaValoriCampiGenAttributi = new ArrayList(listaCategorieImpresa.size());
          if(numeroCategorieImpresa > 0){
            Vector vettoreCampiGenAttrib = (Vector) pageContext.getAttribute(
                "elencoCampi_CATE", PageContext.REQUEST_SCOPE);
            Vector<String> campiChiaveEntitaPadre = new Vector<String>();
            campiChiaveEntitaPadre.add("CODIMP1");
            campiChiaveEntitaPadre.add("CATISC");
            
            if(vettoreCampiGenAttrib != null && vettoreCampiGenAttrib.size() > 0){
              for(int i=0; i < listaCategorieImpresa.size(); i++){
                HashMap<String, Object> valoriCampiChiave = new HashMap<String, Object>();
                valoriCampiChiave.put("CODIMP1", ((Vector) listaCategorieImpresa.get(i)).get(0));
                valoriCampiChiave.put("CATISC", ((Vector) listaCategorieImpresa.get(i)).get(1));
                
                HashMap valoriCampiGenAttrib = this.valoriCampiGenAttributi(vettoreCampiGenAttrib,
                    valoriCampiChiave, "CATE", campiChiaveEntitaPadre, pageContext, sqlManager);
                listaValoriCampiGenAttributi.add(i, valoriCampiGenAttrib);
              }
              pageContext.setAttribute("listaValoriCampiGenAttrib", listaValoriCampiGenAttributi);
            }
          }
        }
      }
    } catch (SQLException e) {
      throw new JspException("Errore in fase di inizializzazione della scheda " +
            "'Categorie d'iscrizione impresa'", e);
    }
    
    if(logger.isDebugEnabled()) logger.debug("doBeforeBodyProcessing: fine metodo");
  }

  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }

  private HashMap valoriCampiGenAttributi(Vector vettoreCampiGenAttrib,
      HashMap valoriCampiChiave, String entitaPadre, Vector campiChiaveEntitaPadre,
      PageContext pageContext,SqlManager sqlManager) throws SQLException{
    
    HashMap valoriCampiGenAttributi = null;
    StringBuffer sql1 = new StringBuffer("select");
    
    for(int i=0; i < vettoreCampiGenAttrib.size(); i++){
      HashMap campo = (HashMap) vettoreCampiGenAttrib.get(i);
      if (!((Boolean) campo.get("titolo")).booleanValue()) {
        if(i > 0)
          sql1.append(", ".concat((String) campo.get("nome")));
        else
          sql1.append(" ".concat((String) campo.get("nome")));
      }
    }
    // se non si inseriscono campi reali allora si termina immediatamente in
    // quanto non ha senso costruire una query senza campo da estrarre
    if (sql1.toString().equals("select"))
      return null;

    String entitaGenAttributi = (String) ((HashMap) vettoreCampiGenAttrib.get(0)).get("entitaDinamica");
    sql1.append(" from ".concat(entitaGenAttributi));
    
    if(valoriCampiChiave != null && valoriCampiChiave.size() > 0){
      sql1.append(" where ");
      
      int indice = 0;
      Object[] parametriSql = new Object[valoriCampiChiave.size()];
      
      for(int i=0; i < vettoreCampiGenAttrib.size(); i++) {
        HashMap campo = (HashMap) vettoreCampiGenAttrib.get(i);
        if( ((Boolean) campo.get("chiave")).booleanValue()) {
          if (indice > 0)
            sql1.append(" and ");
          
          sql1.append(entitaGenAttributi.concat(".").concat((String) campo.get("nome")).concat(" = ? "));
          parametriSql[indice] = ((JdbcParametro) valoriCampiChiave.get((String) campiChiaveEntitaPadre.get(indice))).getValue();
          indice++;
        }
      }
      valoriCampiGenAttributi = sqlManager.getHashMap(sql1.toString(), parametriSql);
    }
    
    if (valoriCampiGenAttributi != null && !valoriCampiGenAttributi.isEmpty())
      return valoriCampiGenAttributi;
    else
      return null;
  }
  
  /**
   * Vengono controllati i valori della lista, quando un valore non è
   * numerico, allora viene sbiancato tale valore
   * 
   */
  public void  aggiornaListaImporti(List<?> lista) {
    if (lista !=null && lista.size()>0) {
      for (int i=0; i < lista.size(); i++) {
        String importo = ((Tabellato) lista.get(i)).getDatoSupplementare();
        if (importo != null && !NumberUtils.isNumber(importo))
          lista.set(i, null);
      }
    }
   
  }
}