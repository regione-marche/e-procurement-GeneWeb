/*
 * Created on 12/02/13
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
 * Funzione che controlla se il tecnico è associato ad una impresa registrata su portale
 *
 * @author Marcello Caminiti
 */
public class TecnicoAssociatoImpresaRegistrataSuPortaleFunction extends
		AbstractFunzioneTag {

	public TecnicoAssociatoImpresaRegistrataSuPortaleFunction() {
		super(2, new Class[] { PageContext.class,String.class });
	}

	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

	  String codiceTecnico = (String) params[1];

	  SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
          "sqlManager", pageContext, SqlManager.class);

	  String result = "NO";
	  try {
	    if (codiceTecnico!=null && !"".equals(codiceTecnico)){
	      String select="select count(codtim) from teim where codtim= ? and (exists(select userkey1 from w_puser, impleg where USERENT=? and codimp2=userkey1 and codleg=?)"+
	        " or exists(select userkey1 from w_puser, impdte where USERENT=? and codimp3=userkey1 and coddte=?) "+
	        " or exists(select userkey1 from w_puser, impazi where USERENT=? and codimp4=userkey1 and codtec=?)"+
	        " or exists(select userkey1 from w_puser, g_impcol where USERENT=? and codimp=userkey1 and codtec=?))";
	      Long numOccorrenze = (Long) sqlManager.getObject(select, new Object[]{codiceTecnico,"IMPR",codiceTecnico,"IMPR",codiceTecnico,"IMPR",codiceTecnico,"IMPR",codiceTecnico});
	      if(numOccorrenze!=null && numOccorrenze.longValue()>0)
	          result = "SI";
	    }

	  } catch (SQLException e) {
	    throw new JspException("Errore durante la lettura delle tabelle TEIM, IMPLEG, IMPDTE,IMPAZI, G_IMPCOL e W_PUSER " +
	    		"per stabilire se il tecnico è associato ad una impresa registrata su portale ",e);

	  }

	  return result;
	}

}