/*
 * Created on 18/05/2017
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
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare le sezioni dei settori per
 * gli uffici intestatari
 *
 * @author Marcello Caminiti
 */
public class GestioneSettoriFunction extends
		AbstractFunzioneTag {

	public GestioneSettoriFunction() {
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
				List<?> listaSettori = sqlManager
						.getListVector(
								"select ID, CODEIN, NOMSET, DATFIN "
						        + "from UFFSET where CODEIN = ? order by ID",
								new Object[] { codei });
				if (listaSettori != null
						&& listaSettori.size() > 0) {
				  pageContext.setAttribute("settori",
				      listaSettori, PageContext.REQUEST_SCOPE);

				}
			} catch (SQLException e) {
				throw new JspException(
						"Errore nell'estrarre i settori "
								+ "dell'ufficio intestatario " + codei, e);
			}
		}
		return null;
	}

}