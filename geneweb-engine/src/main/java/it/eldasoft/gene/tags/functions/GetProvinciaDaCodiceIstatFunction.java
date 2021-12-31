/*
 * Created on 27/04/12
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 *  * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per prelevare la provincia associata ad un codice ISTAT
 *
 * @author Marcello Caminiti
 */
public class GetProvinciaDaCodiceIstatFunction extends
		AbstractFunzioneTag {


	  public GetProvinciaDaCodiceIstatFunction() {
		super( 3,new Class[] { PageContext.class, String.class, String.class });
	  }

	public String function(PageContext pageContext, Object[] params)
			throws JspException {

	  String chiave = (String) params[1];
      String valoreChiave = chiave.substring(chiave.indexOf(":") + 1);

      String infoEntita = chiave.substring(0, chiave.indexOf("="));
      String entita = infoEntita.substring(0, infoEntita.indexOf("."));
      String campoChiave = infoEntita.substring(infoEntita.indexOf(".")+1);

      String campo = (String) params[2];


      String ret = null;
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

      String select="select " + campo + " from " + entita + " where " + campoChiave + " = ?";

      try {
        String valoreIstat = (String)sqlManager.getObject(select, new Object[]{valoreChiave});
        if(valoreIstat !=null && !"".equals(valoreIstat)){
          select="select TABDESC from TABSCHE where TABCOD='S2003' and TABSCHE.TABCOD1='07' and TABSCHE.TABCOD2=?";
          ret = (String)sqlManager.getObject(select, new Object[]{valoreIstat});
        }
      } catch (SQLException e) {
		throw new JspException("Errore nell'estrarre la provincia associata al codice istat "+valoreChiave, e);
      }

      return ret;
	}




}