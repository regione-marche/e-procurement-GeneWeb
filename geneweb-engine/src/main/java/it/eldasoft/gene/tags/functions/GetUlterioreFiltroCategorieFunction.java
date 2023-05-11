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

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;

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
  public String function(PageContext pageContext, Object[] params) {

	  String parametro = (String) params[1];
	  String filtro ="";

	  filtro = " and ( UPPER(v_cais_tit.caisim) like '" + parametro + "' or UPPER(v_cais_tit.descat) like '" + parametro + "' or exists (select c1.caisim from v_cais_tit c1 where c1.codliv1 = v_cais_tit.caisim and (UPPER(c1.caisim) like '" + parametro + "' or UPPER(c1.descat) like '" + parametro + "'))" ;
	  filtro += " or exists (select c2.caisim from v_cais_tit c2 where c2.codliv1=v_cais_tit.codliv1 and c2.codliv2 = v_cais_tit.caisim and (UPPER(c2.caisim) like '" + parametro + "' or UPPER(c2.descat) like '" + parametro + "'))";
	  filtro += " or exists (select c3.caisim from v_cais_tit c3 where c3.codliv1=v_cais_tit.codliv1 and c3.codliv2 = v_cais_tit.codliv2 and c3.codliv3=v_cais_tit.caisim and (UPPER(c3.caisim) like '" + parametro + "' or UPPER(c3.descat) like '" + parametro + "'))";
	  filtro += " or exists (select c4.caisim from v_cais_tit c4 where c4.codliv1=v_cais_tit.codliv1 and c4.codliv2 = v_cais_tit.codliv2 and c4.codliv3=v_cais_tit.codliv3 and c4.codliv4=v_cais_tit.caisim and (UPPER(c4.caisim) like '" + parametro + "' or UPPER(c4.descat) like '" + parametro + "')))";



	  return filtro;
	}

}