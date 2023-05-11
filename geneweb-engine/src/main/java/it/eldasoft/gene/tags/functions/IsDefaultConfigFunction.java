/*
 * Created on 03/feb/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;


public class IsDefaultConfigFunction extends AbstractFunzioneTag{
  
  public IsDefaultConfigFunction(){
    super(3, new Class[] { PageContext.class, String.class, String.class });
  }
  
  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    String res = null;
    try {
      String idconfi = (String) params[1];
      String codapp = (String) params[2];
      Long count = (Long) sqlManager.getObject("select count(*) from wsdmconfiuff u, wsdmconfi c where c.id = ? and c.codapp = ? and u.idconfi = c.id", new Object[]{new Long(idconfi),codapp});
      if(count.intValue()>0){
        res = "No";
      }else{
        res = "Sì";
        Long defNonAttiva = (Long) sqlManager.getObject("select count(*) from wsdmconfi wc where wc.id < ? and codapp = ? and not exists(select * from wsdmconfiuff wu where wu.idconfi = wc.id) and exists " +
        		"(select * from wsdmconfipro wp where wp.idconfi = wc.id)", new Object[]{new Long(idconfi),codapp});
        if(defNonAttiva.intValue()!=0){
          res = "defaultNonAttiva";
        }
      }
      
    } catch (SQLException e) {
      throw new JspException("Errore durante la lettura della tabella wsdmconfiuff", e);
    }
    if("defaultNonAttiva".equals(res)){
        UtilityStruts.addMessage(this.getRequest(), "warning",
        "warnings.generico",new Object[]{"Questa configurazione non viene mai utilizzata perchè è già definita e utilizzata un'altra configurazione di default, ovvero senza uffici intestatari associati."});
    }
    return res;
    
  }

}
