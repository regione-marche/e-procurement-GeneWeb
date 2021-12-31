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
import it.eldasoft.utils.spring.UtilitySpring;

import java.util.HashMap;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che gestisce l'estrazione dell'elenco dei campi da utilizzare nella
 * pagina di lista e relativa ad una entità dinamica figlia in relazione 1:N a
 * partire da un'entità.
 * 
 * @author Stefano.Sabbadin
 */
public class GetElencoCampiLista1NFunction extends AbstractFunzioneTag {

  /**
   * Costruttore
   */
  public GetElencoCampiLista1NFunction() {
    super(1, null);
  }

  /*
   * @see it.eldasoft.gene.tags.utils.AbstractFunzioneTag#function(javax.servlet.jsp.PageContext,
   *      java.lang.Object[])
   */
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    if (pageContext != null) {

      // lettura dei parameter passati alla pagina che richiama la function
      String entitaFiglia1N = pageContext.getRequest().getParameter(
          "entita");

      GeneManager geneManager = (GeneManager) UtilitySpring.getBean(
          "geneManager", pageContext, GeneManager.class);

      StringBuffer sbSql = new StringBuffer("");
      sbSql.append("SELECT DYNCAM_NAME, DYNCAM_PK, DYNCAM_LIS, DYNCAM_DESC ");
      sbSql.append("FROM DYNCAM ");
      sbSql.append("WHERE DYNENT_NAME = ? ");
      sbSql.append("AND DYNCAM.DYNENT_TYPE = ? ");
      sbSql.append("AND DYNCAM_LIS = ? ");
      sbSql.append("ORDER BY DYNCAM_NUMORD");

      try {
        // esecuzione della query
        List campiFiglia1N = geneManager.getSql().getListHashMap(
            sbSql.toString(),
            new Object[] { entitaFiglia1N, new Integer(3), "1" });
        // il campo PK, che viene usato nella JSP, viene convertito in stringa
        // da JdbcParametro
        for (int i = 0; i < campiFiglia1N.size(); i++) {
          campiFiglia1N.set(
              i,
              SqlSelectTag.convertHasMapString((HashMap) campiFiglia1N.get(i)));
        }
        pageContext.setAttribute("campi", campiFiglia1N,
            PageContext.REQUEST_SCOPE);

      } catch (Throwable e) {
        throw new JspException(
            "Errore durante la lettura dell'elenco dei campi dell'entità dinamica \""
                + entitaFiglia1N
                + "\"", e);
      }
    }

    return null;
  }
}