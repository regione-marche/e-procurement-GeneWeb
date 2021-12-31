/*
 * Created on 23/04/2014
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
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare le sezioni dei punti di contatto per
 * gli uffici intestatari
 *
 * @author Marcello Caminiti
 */
public class GestionePuntiDiContattoFunction extends
		AbstractFunzioneTag {

	public GestionePuntiDiContattoFunction() {
		super(1, new Class[] { String.class });
	}

	@Override
  public String function(PageContext pageContext, Object[] params)
			throws JspException {

		if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(UtilityTags
				.getParametro(pageContext,
						UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))) {
			String chiave = (String) params[0];

			String codei = null;

			codei = chiave.substring(chiave.indexOf(":") + 1);

			SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
					"sqlManager", pageContext, SqlManager.class);
			try {
				List<?> listaPuntiContatto = sqlManager
						.getListVector(
								"select CODEIN, NUMPUN, NOMPUN, VIAEIN, NCIEIN, CITEIN, PROEIN, "
						        + "CAPEIN, CODCIT, CODNAZ, TELEIN, FAXEIN, EMAIIN, EMAI2IN, INDWEB, CODFE, CODRES, CODCONS_NSO "
						        + "from PUNTICON where CODEIN = ? order by NUMPUN",
								new Object[] { codei });
				if (listaPuntiContatto != null
						&& listaPuntiContatto.size() > 0) {
				  List<String> listaNomTec = new ArrayList<String>(listaPuntiContatto.size());

                  for (int i = 0; i < listaPuntiContatto.size(); i++) {

                      String nomtec = (String) sqlManager.getObject(
                              "select nomtec from tecni where CODTEC= ?",
                              new Object[] { SqlManager
                                      .getValueFromVectorParam(
                                          listaPuntiContatto.get(i),
                                              16).getStringValue() });

                      listaNomTec.add(nomtec);

                  }
					pageContext.setAttribute("puntiContatto",
							listaPuntiContatto, PageContext.REQUEST_SCOPE);
					pageContext.setAttribute("nomeTecnico", listaNomTec,
                        PageContext.REQUEST_SCOPE);

				}
			} catch (SQLException e) {
				throw new JspException(
						"Errore nell'estrarre i punti di contatto "
								+ "dell'ufficio intestatario " + codei, e);
			}
		}
		return null;
	}

}