/*
 * Created on 04-ago-2008
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
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che gestisce l'estrazione dell'elenco delle tabelle dinamiche
 * collegate in relazione 1:N a partire da un'entità.
 * 
 * @author Stefano.Sabbadin
 */
public class GetElencoTabelleLista1NFunction extends AbstractFunzioneTag {

  /**
   * Costruttore
   */
  public GetElencoTabelleLista1NFunction() {
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
      String entitaParent = pageContext.getRequest().getParameter(
          "entitaParent");
      String from = pageContext.getRequest().getParameter("from");
      String where = pageContext.getRequest().getParameter("where");
      String sqlParams = pageContext.getRequest().getParameter("sqlParams");

      GeneManager geneManager = (GeneManager) UtilitySpring.getBean(
          "geneManager", pageContext, GeneManager.class);

      StringBuffer sbSql = new StringBuffer("");
      sbSql.append("SELECT DYNENT.DYNENT_NAME, DYNENT.DYNENT_PGNAME, DYNENT.DYNENT_DESC ");
      sbSql.append("FROM DYNENT");
      if (from != null && from.trim().length() > 0)
        sbSql.append(", ").append(from);
      sbSql.append(" ");
      sbSql.append("WHERE DYNENT.DYNENT_TYPE = 3");
      if (where != null && where.trim().length() > 0)
        sbSql.append(" AND ").append(where);
      sbSql.append(" ORDER BY DYNENT.DYNENT_PGNUM ASC");

      try {
        Object[] oPars = null;
        String sql = sbSql.toString();

        // se esistono dei parametri, si eseguono le sostituzioni del caso
        // nell'sql e si crea l'elenco dei parametri nel formato corretto
        if (sqlParams != null && sqlParams.trim().length() > 0) {
          Vector pars = new Vector();
          // replace marcature dei parametri nell'sql con i ? e produzione
          // elenco parametri
          sql = UtilityTags.replaceParametri(pars, sbSql.toString(),
              UtilityTags.stringParamsToHashMap(sqlParams, null));
          // conversione vector dei parametri in array
          oPars = UtilityTags.vectorParamToObjectArray(pars);
        }
        // esecuzione della query
        List tabelle1N = geneManager.getSql().getListHashMap(sql, oPars);

        pageContext.setAttribute("tabelle1N", tabelle1N,
            PageContext.REQUEST_SCOPE);

      } catch (Throwable e) {
        throw new JspException(
            "Errore durante la lettura dell'elenco delle entità dinamiche figlie 1:N per la tabella \""
                + entitaParent
                + "\"", e);
      }
    }

    return null;
  }
}