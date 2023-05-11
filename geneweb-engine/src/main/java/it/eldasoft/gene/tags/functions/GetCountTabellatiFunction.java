/*
 * Created on 09/lug/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.tags.functions;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

public class GetCountTabellatiFunction extends
AbstractFunzioneTag{

  public GetCountTabellatiFunction() {
    super(3, new Class[] { PageContext.class,String.class,String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    String id = (String) params[1];
    String idconfi = (String) params[2];
    
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
        "sqlManager", pageContext, SqlManager.class);
    
    String result = ""; 
      
    
      try {
          if (id!=null && !"".equals(id)){
            String select="select count(*) from wsdmtab where idcftab = ? and  idconfi = ? and (isarchi = 2 or isarchi is null)";
            Long count = (Long) sqlManager.getObject(select, new Object[]{new Long(id),new Long(idconfi)});
            if(count!=null && count.intValue()>0)
              result = count.toString();
          }
          
        } catch (SQLException e) {
            throw new JspException(
                "Errore durante la lettura dei tabellati" ,e);
            
        }
        
       
    return result;
  }

}
