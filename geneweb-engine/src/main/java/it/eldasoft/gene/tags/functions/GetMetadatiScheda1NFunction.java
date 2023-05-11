/*
 * Created on 05-ago-2008
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
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.SqlSelectTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.spring.UtilitySpring;

import java.util.HashMap;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che gestisce l'estrazione dell'elenco dei campi da utilizzare nella
 * pagina di lista e relativa ad una tabella dinamica figlia 1:N a partire da
 * un'entità.
 * 
 * @author Stefano.Sabbadin
 */
public class GetMetadatiScheda1NFunction extends AbstractFunzioneTag {

  /**
   * Costruttore
   */
  public GetMetadatiScheda1NFunction() {
    super(1, null);
  }

  /*
   * @see it.eldasoft.gene.tags.utils.AbstractFunzioneTag#function(javax.servlet.jsp.PageContext,
   *      java.lang.Object[])
   */
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    if (pageContext != null) {
      // dal keyParent nel request, valorizzato con la chiave dell'entità padre,
      // si determinano il nome dell'entità padre e lo schema concettuale
      String keyParent = (String) pageContext.getAttribute(
          UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT,
          PageContext.REQUEST_SCOPE);
      Tabella tabParent = DizionarioTabelle.getInstance().getDaNomeTabella(
          keyParent.substring(0, keyParent.indexOf('.')));
      pageContext.setAttribute("schema", tabParent.getNomeSchema(),
          PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("entitaParent", tabParent.getNomeTabella(),
          PageContext.REQUEST_SCOPE);

      // lettura dal request del campo key in modo da determinare il nome
      // dell'entità per la quale creare il dettaglio
      String entitaFiglia1N = (String) pageContext.getAttribute(
          UtilityTags.DEFAULT_HIDDEN_NOME_TABELLA, PageContext.REQUEST_SCOPE);
      
      GeneManager geneManager = (GeneManager) UtilitySpring.getBean(
          "geneManager", pageContext, GeneManager.class);

      StringBuffer sbSql = new StringBuffer("");
      sbSql.append("SELECT DYNCAM_NAME, DYNCAM_PK, DYNCAM_SCH, DYNCAM_SCH_B, DYNCAM_NAME_P, DYNCAM_DESC ");
      sbSql.append("FROM DYNCAM ");
      sbSql.append("WHERE DYNENT_NAME = ? ");
      sbSql.append("AND DYNCAM.DYNENT_TYPE = ? ");
      sbSql.append("AND (DYNCAM_SCH = ? OR DYNCAM_PK = ?) ");
      sbSql.append("ORDER BY DYNCAM_NUMORD");

      try {
        // esecuzione della query
        List campiFiglia1N = geneManager.getSql().getListHashMap(
            sbSql.toString(),
            new Object[] { entitaFiglia1N, new Integer(3), "1", "1" });
        // si convertono i dati da JdbcParametro a stringhe per meglio operare
        // nella pagina
        for (int i = 0; i < campiFiglia1N.size(); i++) {
          campiFiglia1N.set(
              i,
              SqlSelectTag.convertHasMapString((HashMap) campiFiglia1N.get(i)));
        }
        pageContext.setAttribute("campi", campiFiglia1N,
            PageContext.REQUEST_SCOPE);

      } catch (Throwable e) {
        throw new JspException(
            "Errore durante la lettura dell'elenco dei campi della tabella di estensione \""
                + entitaFiglia1N
                + "\"", e);
      }
    }

    return null;
  }
}