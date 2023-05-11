package it.eldasoft.gene.web.struts.tags;

import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class OpenPageAction extends ActionBaseNoOpzioni {

	private static Logger logger = Logger.getLogger(OpenPageAction.class);


	@Override
	protected ActionForward runAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if (logger.isDebugEnabled())
			logger.debug("runAction: inizio metodo");
		try {
			// Ridireziono sulla pagina voluta
			String href = request.getParameter("href");
            // non deve essere possibile redirezionare ad una risorsa non jsp e nemmeno risalire sopra WEB-INF/pages
			if (UtilityStruts.isValidJspPath(href)) {
		      return UtilityStruts.redirectToPage(href,false, request);
			} else {
              String messageKey = "errors.url.notWellFormed";
              String messageError = this.resBundleGenerale.getString(messageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0),
                  href);
              logger.error(messageError);
			  this.aggiungiMessaggio(request, messageKey, href);
			}
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
		if (logger.isDebugEnabled())
			logger.debug("runAction: fine metodo");
		return mapping
				.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
	}
}
