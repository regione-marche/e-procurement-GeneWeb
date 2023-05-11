package it.eldasoft.gene.commons.web.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMessages;

/**
 * Interfaccio per Azione base e Dispatch Action base
 * 
 * @author cit_franceschin
 * 
 */
public interface ActionInterface {

	/**
	 * Funzione per l'aggiunta di messaggi etratti dal boundle.
	 * @param request
	 * @param errors
	 */
	public void publicSaveMessages(HttpServletRequest request, ActionMessages errors);
}
