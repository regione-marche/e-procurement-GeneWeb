/*
 * Created on 08/05/13
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
 * Funzione per prelevare il codice fiscale dell'ufficio intestatario
 *
 * @author Marcello Caminiti
 */
public class GetCodfiscUffintFunction extends
		AbstractFunzioneTag {


	  public GetCodfiscUffintFunction() {
		super( 3,new Class[] { PageContext.class, String.class, });
	  }

	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

	  String codiceUffint = (String) params[1];

      String ret = null;
      SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

      String select="select cfein from uffint where codein = ?";

      try {
        ret = (String)sqlManager.getObject(select, new Object[]{codiceUffint});
      } catch (SQLException e) {
		throw new JspException("Errore nell'estrarre il codice fiscale dell'ufficio intestatario " + codiceUffint, e);
      }

      return ret;
	}




}