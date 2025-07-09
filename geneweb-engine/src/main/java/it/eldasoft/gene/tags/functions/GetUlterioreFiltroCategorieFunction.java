/*
 * Created on 13/11/12
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
 * Funzione che costruisce il filtro su caisim e descat per la view v_cais_tit
 *
 * @author Marcello Caminiti
 */
public class GetUlterioreFiltroCategorieFunction extends
		AbstractFunzioneTag {

	public GetUlterioreFiltroCategorieFunction() {
		super(2, new Class[] { PageContext.class,String.class });
	}

	@Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

	  String parametro = (String) params[1];
	  String filtro ="";
	  
	  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
	        pageContext, SqlManager.class);
	  

	  filtro = " and ( UPPER(v_cais_tit.caisim) like ? or UPPER(v_cais_tit.descat) like ? or exists (select c1.caisim from v_cais_tit c1 where c1.codliv1 = v_cais_tit.caisim and (UPPER(c1.caisim) like ? or UPPER(c1.descat) like ?))" ;
	  filtro += " or exists (select c2.caisim from v_cais_tit c2 where c2.codliv1=v_cais_tit.codliv1 and c2.codliv2 = v_cais_tit.caisim and (UPPER(c2.caisim) like ? or UPPER(c2.descat) like ?))";
	  filtro += " or exists (select c3.caisim from v_cais_tit c3 where c3.codliv1=v_cais_tit.codliv1 and c3.codliv2 = v_cais_tit.codliv2 and c3.codliv3=v_cais_tit.caisim and (UPPER(c3.caisim) like ? or UPPER(c3.descat) like ?))";
	  filtro += " or exists (select c4.caisim from v_cais_tit c4 where c4.codliv1=v_cais_tit.codliv1 and c4.codliv2 = v_cais_tit.codliv2 and c4.codliv3=v_cais_tit.codliv3 and c4.codliv4=v_cais_tit.caisim and (UPPER(c4.caisim) like ? or UPPER(c4.descat) like ?)))";

	  try {
        Long numeroOccorrenze = (Long)sqlManager.getObject ("select count(*) from v_cais_tit where 1=1"+ filtro,new Object[] {parametro,parametro,parametro,parametro,parametro,parametro,parametro,parametro,parametro,parametro});
        if(numeroOccorrenze==0)
          filtro= " and 1=2"; //se non ho risultati, potrebbe essere un tentantivo di sql injection. In caso di parametro "buono", se non ottengo record, l'impostare come filtro una condizione falsa non cambia il resulset
	  } catch (SQLException e) {
	    throw new JspException("Errore nell'applicazione del filtro: " + parametro,e);
    }

	  return filtro.replace("?", "'" + parametro + "'");
	}

}