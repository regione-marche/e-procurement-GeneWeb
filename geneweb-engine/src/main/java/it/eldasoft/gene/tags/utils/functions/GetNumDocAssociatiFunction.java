/*
 * Created on 10-ott-2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.utils.functions;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcWhere;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Classe che realizza una funzione per ottenere il numero di documenti
 * associati a partire da un'entit&agrave: di partenza
 * 
 * @author Stefano.Sabbadin
 */
public class GetNumDocAssociatiFunction extends AbstractFunzioneTag {

  public GetNumDocAssociatiFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  /**
   * @see it.eldasoft.gene.tags.utils.AbstractFunzioneTag#function(javax.servlet.jsp.PageContext,
   *      java.lang.Object[])
   */
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String entita = (String) params[1];
    String defChiave = (String) params[2];

    JdbcWhere where = new JdbcWhere();
    UtilityTags.jdbcAddKeyWhere(where, defChiave);

    GeneManager gene = (GeneManager) UtilitySpring.getBean("geneManager",
        pageContext, GeneManager.class);
    try {
      long numDocumentiAssociati = gene.contaOccorrenzeOggettiAssociati(entita,
          where.toString(false),
          SqlManager.getObjectFromPram(where.getParametri()));
      if (numDocumentiAssociati > 0)
        pageContext.setAttribute("numRecordDocAssociati", new Long(
            numDocumentiAssociati), PageContext.REQUEST_SCOPE);
    } catch (GestoreException e) {
      throw new JspException(
          "Errore nell'estrazione del numero di documenti associati per l'entità "
              + entita
              + " con chiave "
              + defChiave, e);
    }

    return "";
  }

}
