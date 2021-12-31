package it.eldasoft.gene.web.struts.tags;

import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class HistoryAction extends DispatchActionBaseNoOpzioni {

  private static Logger logger = Logger.getLogger(HistoryAction.class);

  /**
   * Funzione che restituisce le opzioni per accedere alla action vaia
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniVaia() {
    return new CheckOpzioniUtente("");
  }

  /**
   * Eseguo il ridirezionamento in un determinato
   */
  public ActionForward vaia(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String numero = UtilityStruts.getParametroString(request, "numero");
    try {

      return UtilityTags.getUtilityHistory(request.getSession()).vaiA(
          new Integer(numero).intValue(), UtilityStruts.getNumeroPopUp(request),
          request);

    } catch (Throwable t) {
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }
  }

  /**
   * Eseguo il ridirezionamento, ricaricando la pagina inserita nel history
   * N passi precedenti, con N numero intero > 0 e < historySize
   */
  public ActionForward vaiIndietroDi(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String numero = UtilityStruts.getParametroString(request, "numero");
    String numeroPopup = UtilityStruts.getParametroString(request, "numeroPopUp");
    try {
      return UtilityTags.getUtilityHistory(request.getSession()).vaiIndietroDi(
          new Integer(numero).intValue(), new Integer(numeroPopup).intValue(),
          request);

    } catch (Throwable t) {
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action back
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniBack() {
    return new CheckOpzioniUtente("");
  }

  public ActionForward back(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    ActionForward result = null; 
    try {
      result = UtilityTags.getUtilityHistory(request.getSession()).back(request);
    } catch (Throwable t) {
      result = GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }
    return result;
  }
  
  public ActionForward reload(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    try {
      return UtilityTags.getUtilityHistory(request.getSession()).last(request);
    } catch (Throwable t) {
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }
  }

}
