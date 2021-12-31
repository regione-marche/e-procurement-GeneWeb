/*
 * Created on 24/04/12
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
 * Funzione che estrae da TAB1 il codice di un tabellato a partire dalla descrizione 
 * 
 * @author Marcello Caminiti
 */
public class GetCodiceTabellatoDaDescrFunction extends
		AbstractFunzioneTag {

	public GetCodiceTabellatoDaDescrFunction() {
		super(3, new Class[] { PageContext.class,String.class,String.class });
	}

	public String function(PageContext pageContext, Object[] params)
			throws JspException {
	
	  String codiceTabellato = (String) params[1];
	  String descrizione = (String) params[2];
	  
	  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
          "sqlManager", pageContext, SqlManager.class);
	  
	  String result = ""; 
	    
	  
	    try {
	        if (codiceTabellato!=null && !"".equals(codiceTabellato) && descrizione!=null && !"".equals(descrizione)){
	          String select="select tab1tip from tab1 where tab1cod = ? and upper(tab1desc) = ?";
	          Long valoreTabellato = (Long) sqlManager.getObject(select, new Object[]{codiceTabellato,descrizione.toUpperCase()});
	          if(valoreTabellato!=null)
	            result = valoreTabellato.toString();
	        }
	        
	      } catch (SQLException e) {
	          throw new JspException(
	              "Errore durante la lettura del valore del tabellato " + codiceTabellato + " associato alla descrizione " + descrizione ,e);
	          
	      }
	      
	     
	  return result;
	}

}