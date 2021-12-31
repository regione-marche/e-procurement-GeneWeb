/*
 * Created on 11/10/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che controlla se è configurata la classifica per le categorie per forniture e servizi
 * 
 * @author Sara Santi
 */
public class EsisteClassificaCategoriaFunction extends AbstractFunzioneTag {

  public EsisteClassificaCategoriaFunction() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String entita = (String) params[1];
    String codiceTabellato = (String) params[2];
        
    String select="";
    
    if("TAB1".equals(entita)){
      select = "select count(*) from tab1 where tab1cod=?";
    }else{
      select = "select count(*) from tab2 where tab2cod=?";
    }
    
    String esito = "false";

    try {
      Long numeroClassi = (Long) sqlManager.getObject(select, new Object[] { codiceTabellato});
      
      if (numeroClassi != null && numeroClassi.longValue()>0)
        esito = "true";

    } catch (SQLException e) {
      throw new JspException(
          "Errore durante i controlli per determinare se è configurata la classificazione per categorie di forniture e servizi", e);
    }

    return esito;

  }

}
