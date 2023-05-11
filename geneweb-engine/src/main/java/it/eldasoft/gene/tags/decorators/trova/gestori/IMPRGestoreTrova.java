package it.eldasoft.gene.tags.decorators.trova.gestori;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore di ricerca per l'entità IMPR.
 *
 * @author Alvise Gorinati
 */
public class IMPRGestoreTrova extends AbstractGestoreTrova {

	// Costanti
	private static final String TIPO_APPALTO_NAME = "tipoAppalto";
	private static final String CAMPO_14_WHERE_NAME = "Campo14_where";
	private static final String CAMPO_15_WHERE_NAME = "Campo15_where";
	private static final String CAMPO_16_WHERE_NAME = "Campo16_where";
	
	private static final String NEW_CAMPO_WHERE = "IMPR.CODIMP = V_CATE_ELENCHI.CODIMP AND V_CATE_ELENCHI.TIPCAT = %s";
	
	private final boolean PROTECTION_CHECK;
	
	// Costruttori
	public IMPRGestoreTrova(HttpServletRequest request, String entity) {
		super(Logger.getLogger(IMPRGestoreTrova.class), request, entity);
		
		final GeneManager geneManager = (GeneManager) UtilitySpring.getBean("geneManager", 
				SpringAppContext.getServletContext(), GeneManager.class);
		final String profiloAttivo = (String) request.getSession()
				.getAttribute(CostantiGenerali.PROFILO_ATTIVO);
		
		PROTECTION_CHECK = geneManager.getProfili()
				.checkProtec(profiloAttivo, "COLS", "VIS", "GARE.V_CATE_ELENCHI.NUMCLA");
	}

	// Metodi
	@Override
	public String composeFilter() {
        String filter = "";

		if (LOGGER.isDebugEnabled()) LOGGER.debug("composeFilter: inizio metodo");
		
		if (PROTECTION_CHECK) {
			final String tipoAppalto = UtilityStruts.getParametroString(request, TIPO_APPALTO_NAME);
			
			if (StringUtils.isNotBlank(tipoAppalto)) {
				final String composedWhere = String.format(NEW_CAMPO_WHERE, tipoAppalto);
				
				UtilityTags.putAttributeForSqlBuild(request.getSession(), ENTITY, popUpLevel, CAMPO_14_WHERE_NAME, composedWhere);
				UtilityTags.putAttributeForSqlBuild(request.getSession(), ENTITY, popUpLevel, CAMPO_15_WHERE_NAME, composedWhere);
				UtilityTags.putAttributeForSqlBuild(request.getSession(), ENTITY, popUpLevel, CAMPO_16_WHERE_NAME, composedWhere);
			}
		}

		if (LOGGER.isDebugEnabled()) LOGGER.debug("composeFilter: fine metodo");
		
		return filter;
	}

}
