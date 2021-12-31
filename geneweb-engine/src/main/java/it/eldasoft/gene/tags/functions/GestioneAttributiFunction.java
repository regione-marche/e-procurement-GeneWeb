/*
 * Created on 19-feb-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.util.HashMap;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che gestisce la lettura dei campi degli attributi per una
 * determinata tabella
 * 
 * @author marco.franceschin
 */
public class GestioneAttributiFunction extends AbstractFunzioneTag {

  /**
   * Costruttore
   */
  public GestioneAttributiFunction() {
    super(1, new Class[] { String.class });
  }

  /*
   * @see it.eldasoft.gene.tags.utils.AbstractFunzioneTag#function(javax.servlet.jsp.PageContext,
   *      java.lang.Object[])
   */
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    if (pageContext != null) {

      GeneManager geneManager = (GeneManager) UtilitySpring.getBean(
          "geneManager", pageContext, GeneManager.class);

      String entitaPadre = ((String) params[0]);

      // la query usa DYNCAM e non DYNCAM_GEN in quanto ci interessa sapere se
      // esistono campi veri e propri per l'entità
      StringBuffer sql = new StringBuffer("");
      sql.append("select DYNENT_NAME, DYNENT_PGNAME, DYNENT_DESC ");
      sql.append("from DYNENT ");
      sql.append("where DYNENT.DYNENT_TYPE = 2 and ");
      sql.append("DYNENT.DYNENT_NAME_E = ? and exists(select 1 from DYNCAM where DYNCAM.DYNENT_NAME = DYNENT.DYNENT_NAME and DYNCAM.DYNCAM_SCH = ?)");

      try {
        // estrazione dell'eventuale entità dinamica
        HashMap rsEntitaDinamica = geneManager.getSql().getHashMap(
            sql.toString(), new Object[] { entitaPadre, "1" });
        
        if (rsEntitaDinamica != null) {
          // valorizzazione di nome entita, descrizione e titolo pagina/sezione
          // nel request
          String entitaDinamica = ((JdbcParametro) rsEntitaDinamica.get("DYNENT_NAME")).stringValue();
          String tabEntitaDinamica = ((JdbcParametro) rsEntitaDinamica.get("DYNENT_PGNAME")).stringValue();
          String descrizioneEntitaDinamica = ((JdbcParametro) rsEntitaDinamica.get("DYNENT_DESC")).stringValue();
          
          String schemaEntitaDinamica = DizionarioTabelle.getInstance().getDaNomeTabella(entitaDinamica).getNomeSchema();

          // ricerca e popolamento dei campi del generatore attributi per
          // l'entità dinamica determinata
          geneManager.setCampiGeneratoreAttributi(pageContext, entitaDinamica,
              entitaPadre);

          // se esistono dei campi definiti con il generatore attributi, allora
          // inserisco tutte le informazioni per generare la pagina
          Vector elencoCampi = (Vector) pageContext.getAttribute("elencoCampi_".concat(entitaPadre),
              PageContext.REQUEST_SCOPE);
          if (elencoCampi != null && elencoCampi.size() > 0) {
            pageContext.setAttribute("DYNENT_SCHEMA_".concat(entitaPadre),
                schemaEntitaDinamica, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute("DYNENT_NAME_".concat(entitaPadre),
                entitaDinamica, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute("DYNENT_PGNAME_".concat(entitaPadre),
                tabEntitaDinamica, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute("DYNENT_DESC_".concat(entitaPadre),
                descrizioneEntitaDinamica, PageContext.REQUEST_SCOPE);
            
            // se esiste una property nascosta valorizzata a 1 allora si
            // controllano i campi del generatore attributi mediante profilo
            Boolean gestisciProtezioni = new Boolean(false);
            if ("1".equals(ConfigManager.getValore("it.eldasoft.genAttributi.controllo.usaProfilo")))
              gestisciProtezioni = new Boolean(true);
            pageContext.setAttribute("gestisciProtezioniGenAttributi",
                gestisciProtezioni, PageContext.REQUEST_SCOPE);
          }
        }

      } catch (Throwable e) {
        throw new JspException(
            "Errore durante la lettura dei campi del generatore attributi per la tabella \""
                + entitaPadre
                + "\"", e);
      }
    }

    return null;
  }
}