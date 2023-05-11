package it.eldasoft.gene.web.struts.tags;

import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.tags.decorators.archivi.ArchivioRequest;
import it.eldasoft.gene.tags.decorators.archivi.ArchivioTagImpl;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Classe che gestisce le richieste per gli archivi
 *
 * @author cit_franceschin
 *
 */
public class ArchivioAction extends DispatchActionBaseNoOpzioni {

  // ************************************************************
  // Storia Modifiche:
  // Data Utente Descrizione
  // 23/11/2006 M.F. Sbianco l'history allapertura della pagina
  // ************************************************************

  private Logger logger= Logger.getLogger(ArchivioAction.class);

  /**
   * Funzione che restituisce le opzioni per accedere alla action lista
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniLista() {
    return new CheckOpzioniUtente("");
  }
  public ActionForward lista(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // Sbianco l'history solo se viene aperto in popUp
    String openInPopUp = UtilityStruts.getParametroString(request,
        ArchivioTagImpl.HIDE_INPUT_IS_OPEN_IN_POPUP);
    if (openInPopUp != null && openInPopUp.equals("1"))
      UtilityTags.getUtilityHistory(request.getSession()).clear(
          UtilityStruts.getNumeroPopUp(request));
    new ArchivioRequest(request, true);
    // Eseguo il calcolo della lista da richiamare
    boolean absolute = false;
    String pathLista = UtilityStruts.getParametroString(request,
        ArchivioTagImpl.HIDE_INPUT_LISTA);
    if (pathLista != null) {
      absolute = pathLista.charAt(0) == '/';
      if (!UtilityStruts.isValidJspPath(pathLista)) {
        // non deve essere possibile redirezionare ad una risorsa non jsp e nemmeno risalire sopra WEB-INF/pages
        String messageKey = "errors.url.notWellFormed";
        String messageError = this.resBundleGenerale.getString(messageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0),
            pathLista);
        logger.error(messageError);
        this.aggiungiMessaggio(request, messageKey, pathLista);
        return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
      }
    }
    // Setto che di default è popUp
    request.setAttribute(UtilityTags.DEFAULT_HIDDEN_IS_POPUP, "1");
    // Lancio la lista
    return UtilityStruts.redirectToPage(pathLista, absolute, request);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action trova
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniScheda() {
    return new CheckOpzioniUtente("");
  }
  public ActionForward scheda(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // Sbianco l'history solo se viene aperto in popUp
    String openInPopUp = UtilityStruts.getParametroString(request,
        ArchivioTagImpl.HIDE_INPUT_IS_OPEN_IN_POPUP);
    if (openInPopUp != null && openInPopUp.equals("1"))
      UtilityTags.getUtilityHistory(request.getSession()).clear(
          UtilityStruts.getNumeroPopUp(request));
    // Eseguo il settaggio del campo chiave
    String key = "";
    String nomeKey[] = UtilityTags.stringToArray(
        UtilityStruts.getParametroString(request,
            ArchivioTagImpl.HIDE_INPUT_CHIAVE), ';');
    String valKey[] = UtilityTags.stringToArray(
        UtilityStruts.getParametroString(request,
            ArchivioTagImpl.HIDE_INPUT_VALUE_CHIAVE), ';');
    String campiArchivio[] = UtilityTags.stringToArray(
        UtilityStruts.getParametroString(request,
            ArchivioTagImpl.HIDE_INPUT_CAMPI_ARCHIVIO), ';');
    String campiScheda[] = UtilityTags.stringToArray(
        UtilityStruts.getParametroString(request,
            ArchivioTagImpl.HIDE_INPUT_CAMPI), ';');

    for (int i = 0; i < nomeKey.length; i++) {
      if (i >= valKey.length) break;
      if (i > 0) key += ";";
      // Ricerco il numero del campo nell'archivio
      int pos = this.findString(campiScheda, nomeKey[i]);
      if (pos >= 0) {
        key += campiArchivio[pos] + "=" + valKey[i];
      }
    }
    // Setto la chiave per l'apertura e la pagina attiva come prima
    request.setAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, key);
    request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE, "0");
    // Setto l'apertura in visualizzazione
    request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO,
        UtilityTags.SCHEDA_MODO_VISUALIZZA);
    boolean absolute = false;
    String pathLista = null;
    // Se si tratta di un popUp allora apro la scheda popUp altrimenti apro
    // la scheda normale
    // Apro la scheda popUp anche se è in modifica
    if (UtilityStruts.isPopUp(request) || UtilityTags.isInModifica(request)) {
      pathLista = UtilityStruts.getParametroString(request,
          ArchivioTagImpl.HIDE_INPUT_SCHEDAPOPUP);
      // Setto la visualizzazione in modo popup
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_IS_POPUP, "1");
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_TO_JSP, pathLista);
    } else {
      pathLista = UtilityStruts.getParametroString(request,
          ArchivioTagImpl.HIDE_INPUT_SCHEDA);
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_IS_POPUP, "0");
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_TO_JSP, pathLista);
    }
    if (pathLista != null) {
      absolute = pathLista.charAt(0) == '/';
      if (!UtilityStruts.isValidJspPath(pathLista)) {
        // non deve essere possibile redirezionare ad una risorsa non jsp e nemmeno risalire sopra WEB-INF/pages
        String messageKey = "errors.url.notWellFormed";
        String messageError = this.resBundleGenerale.getString(messageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0),
            pathLista);
        logger.error(messageError);
        this.aggiungiMessaggio(request, messageKey, pathLista);
        return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
      }
    }
    try{
      // Lancio la lista
      return UtilityStruts.redirectToPage(pathLista, absolute, request);
    }catch(Throwable t){
      return GestoreEccezioni.gestisciEccezioneAction(t,this,request,logger,mapping);
    }
  }

  private int findString(String[] array, String str) {
    for (int i = 0; i < array.length; i++)
      if (array[i].equals(str)) return i;
    return -1;
  }
}
