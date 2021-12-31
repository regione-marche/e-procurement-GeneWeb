/*
 * Created on 03-mag-2010
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
 * Gestisce l'estrazione dell'elenco dei campi dell'archivio esterno da
 * visualizzare in una scheda di dettaglio
 * 
 * @author Filippo Rossetto
 */
public class GetMetadatiSchedaArchEstFunction extends AbstractFunzioneTag {

  /**
   * Costruttore
   */
  public GetMetadatiSchedaArchEstFunction() {
    super(1, new Class[] { String.class });
  }

  /*
   * @see it.eldasoft.gene.tags.utils.AbstractFunzioneTag#function(javax.servlet.jsp.PageContext,
   *      java.lang.Object[])
   */
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    if (pageContext != null) {
      // dalla key (passata per parametro), valorizzato con la chiave
      // dell'entità, si determinano il nome dell'entità e lo schema concettuale
      String key = (String) params[0];
      Tabella tabParent = DizionarioTabelle.getInstance().getDaNomeTabella(
          key.substring(0, key.indexOf('.')));
      pageContext.setAttribute("schema", tabParent.getNomeSchema(),
          PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("entitaParent", tabParent.getNomeTabella(),
          PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("entita", tabParent.getNomeTabella(),
          PageContext.REQUEST_SCOPE);

      // lettura dal request del campo key in modo da determinare il nome
      // dell'entità per la quale creare il dettaglio
      String entita = (String) pageContext.getAttribute(
          UtilityTags.DEFAULT_HIDDEN_NOME_TABELLA, PageContext.REQUEST_SCOPE);

      GeneManager geneManager = (GeneManager) UtilitySpring.getBean(
          "geneManager", pageContext, GeneManager.class);

      StringBuffer sbSql = new StringBuffer("");
      sbSql.append("SELECT DYNCAM_NAME, DYNCAM_PK, DYNCAM_SCH, DYNCAM_SCH_B, DYNCAM_NAME_P, DYNCAM_DESC, DYNCAM_DOM, DYNCAM_TAB ");
      sbSql.append("FROM DYNCAM ");
      sbSql.append("WHERE DYNENT_NAME = ? ");
      sbSql.append("AND DYNCAM.DYNENT_TYPE = ? ");
      sbSql.append("AND (DYNCAM_SCH = ? OR DYNCAM_PK = ?) ");
      sbSql.append("ORDER BY DYNCAM_NUMORD");

      try {
        // esecuzione della query
        List campiEntita = geneManager.getSql().getListHashMap(
            sbSql.toString(), new Object[] { entita, new Integer(4), "1", "1" });
        // si convertono i dati da JdbcParametro a stringhe per meglio operare
        // nella pagina
        for (int i = 0; i < campiEntita.size(); i++) {
          campiEntita.set(i,
              SqlSelectTag.convertHasMapString((HashMap) campiEntita.get(i)));
        }
        pageContext.setAttribute("campi", campiEntita,
            PageContext.REQUEST_SCOPE);

      } catch (Throwable e) {
        throw new JspException(
            "Errore durante la lettura dell'elenco dei campi dell'archivio esterno \""
                + entita
                + "\"", e);
      }
    }

    return null;
  }
}