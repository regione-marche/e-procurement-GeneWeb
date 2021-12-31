/*
 * Created on 29/gen/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che estrae il numero di record salvati nello storico dell'anagrafica UFFINT
 *
 * @author Mirco Franzoni
 */
public class GetNumDatiStoricoUffintFunction extends
AbstractFunzioneTag{

  public GetNumDatiStoricoUffintFunction() {
    super( 2,new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    String codiceUffint = (String) params[1];
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    String ret=null;
    try{
      Long numDatiStorico = (Long)sqlManager.getObject("select count(*) from STO_UFFINT where CODEIN = ? ", new Object[]{codiceUffint});
      ret = numDatiStorico.toString();

    }catch (SQLException e) {
      throw new JspException("Errore nella lettura dello storico collegato all'ufficio interstatario con codice=" + codiceUffint,e);
    }
    return ret;
  }

}
