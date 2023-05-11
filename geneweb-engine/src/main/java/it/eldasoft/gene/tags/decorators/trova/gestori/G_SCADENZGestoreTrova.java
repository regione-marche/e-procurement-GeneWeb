package it.eldasoft.gene.tags.decorators.trova.gestori;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.functions.ListaAttivitaScadenzarioFunction;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;

/**
 * Gestore di ricerca per l'entit� G_SCADENZ.
 *
 * @author Alvise Gorinati
 */
public class G_SCADENZGestoreTrova extends AbstractGestoreTrova {

	// Costanti
	private static final String CAMPO_FITT_NAME = "CampoFitt";
	
	// Costruttori
	public G_SCADENZGestoreTrova(HttpServletRequest request, String entity) {
		super(Logger.getLogger(G_SCADENZGestoreTrova.class), request, entity);
	}

	// Metodi
	@Override
	public String composeFilter() {
		String filter = "";
		
		if (LOGGER.isDebugEnabled()) LOGGER.debug("composeFilter: inizio metodo");
		
		final String campoFittStr = UtilityStruts.getParametroString(request, CAMPO_FITT_NAME);
		try {
			final int campoFitt = Integer.parseInt(campoFittStr);
			
			if (campoFitt == 1) {
				filter += "G_SCADENZ.DATACONS is not null";
			} else if (campoFitt == 2) {
				filter += "G_SCADENZ.DATACONS is null";
			}
		} catch (NumberFormatException e) {
			LOGGER.warn(String.format("Valore del campo non numerico: %s", campoFittStr));
			return filter;
		}
		
		// Dal momento che il filtro in sessione non viene pi� applicato direttamente nella query, ma serve solo ad indicare se
		// � stato o meno applicato un filtro, quando eseguo la ricerca nella form di trova imposto il valore di "filtroAttivita"
		// a 1.
		request.getSession().setAttribute(ListaAttivitaScadenzarioFunction.FILTRO_ATTIVITA, "1");

		if (LOGGER.isDebugEnabled()) LOGGER.debug("composeFilter: fine metodo");
		
		return filter;
	}

}
