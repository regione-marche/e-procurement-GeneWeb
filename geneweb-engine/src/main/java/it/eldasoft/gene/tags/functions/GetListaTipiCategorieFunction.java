package it.eldasoft.gene.tags.functions;
/*
 * Created on 07/10/21
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */


import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
/**
 * Funzione per caricare la lista delle tipologie delle categorie 
 * 
 * @author Cristian Febas
 */
public class GetListaTipiCategorieFunction extends AbstractFunzioneTag{
	public GetListaTipiCategorieFunction() {
	    super(1, new Class[]{PageContext.class});
	  }
	
	public String function(PageContext pageContext, Object[] params)
    throws JspException {
  
		  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
		      "sqlManager", pageContext, SqlManager.class);
		  
		  try {
			  
			  String selectCategorie = "SELECT TAB1TIP, TAB1DESC, TAB1ARC FROM TAB1 WHERE TAB1COD = ?"
			  		+ " AND (TAB1ARC IS NULL OR TAB1ARC <> ?) ORDER BY TAB1NORD, TAB1TIP";
			  
			  List<Tabellato> tipiCategorie = new ArrayList<Tabellato>();
			  List<?> datiTipoCategoria = sqlManager.getListVector(selectCategorie, new String[] {"G_038","1"});
	          if (datiTipoCategoria != null && datiTipoCategoria.size() > 0) {
	              for (int i = 0; i < datiTipoCategoria.size(); i++) {
	                Long tipoL = (Long) SqlManager.getValueFromVectorParam(datiTipoCategoria.get(i), 0).getValue();
	                String tipo = String.valueOf(tipoL);
	                String descrizione = (String) SqlManager.getValueFromVectorParam(datiTipoCategoria.get(i), 1).getValue();
        			Tabellato t = new Tabellato();
				    t.setTipoTabellato(tipo);
				    t.setDescTabellato(descrizione);
				    tipiCategorie.add(t);
	              }
	          }

			this.getRequest().setAttribute("tipiCategorie", tipiCategorie);
			
		} catch (SQLException e) {
			throw new JspException("Errore nella lettura dei tipi categorie", e);
		}
		
		  return null;
		  
		}
}
