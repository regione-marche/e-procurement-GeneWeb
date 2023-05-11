package it.eldasoft.gene.web.struts.tags;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.gene.commons.web.struts.ActionInterface;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Classe che gestisce le eccezioni di default
 *
 * @author marco.franceschin
 *
 */
public class GestoreEccezioni {

  /**
   * Funzione che gestisce le eccezioni da una Action.
   *
   * @param t
   *        Eccezione catturata
   * @param action
   *        Reference alla action scatenante
   * @param logger
   *        Logger della action
   * @param mapping
   *        mapping forward della action
   * @param forceErroreGenerale
   *        true se si vuole andare incondizionatamente alla pagina di errore generale, false altrimenti
   * @return forward
   */
  public static ActionForward gestisciEccezioneAction(Throwable t,
      ActionInterface action, HttpServletRequest request, Logger logger,
      ActionMapping mapping, boolean forceErroreGenerale) {
    String messageKey = "errors.applicazione.inaspettataException";
    String target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
    Object params[] = null;
    // In caso non definito creo il codice aggiungendo il nome della classe
    // dopo errors
    if (t != null) {
      messageKey = "errors."
          + UtilityStringhe.decapitalize(t.getClass().getName().substring(
              t.getClass().getName().lastIndexOf('.') + 1));
    }
    if (t instanceof GestoreException) {
      GestoreException e = (GestoreException) t;
      if (e.getCodice() != null && e.getCodice().length() > 0)
        messageKey += "." + e.getCodice();
      params = e.getParameters();
    }
    aggiungiMessaggioProperties(action, request, messageKey, params);
    logger.error(getMessaggioProperties(messageKey, t.getMessage(), params), t);

    // Se esiste il nome della pagina chiamante allora lo ridireziono li
    if (!forceErroreGenerale) {
      String pathSource = UtilityStruts.getParametroString(request,
          UtilityTags.DEFAULT_HIDDEN_FORM_JSP_PATH);
        if (pathSource != null) {
          if (UtilityStruts.isValidJspPath(pathSource)) {
            return UtilityStruts.redirectToPage(pathSource, true, request);
          } else {
            messageKey = "errors.url.notWellFormed";
            logger.error("Rifiutata apertura url anomala: " + pathSource);
            aggiungiMessaggioProperties(action, request, messageKey, new String[] {pathSource});
          }
        }
    }
    // Se non è settato il path sorgente oppure si forza l'errore generale allora trovo il target
    return mapping.findForward(target);
  }

  /**
   * Funzione che gestisce le eccezioni da una Action.
   *
   * @param t
   *        Eccezione catturata
   * @param action
   *        Reference alla action scatenante
   * @param logger
   *        Logger della action
   * @param mapping
   *        mapping forward della action
   * @return forward
   */
  public static ActionForward gestisciEccezioneAction(Throwable t,
      ActionInterface action, HttpServletRequest request, Logger logger,
      ActionMapping mapping) {
    return GestoreEccezioni.gestisciEccezioneAction(t, action, request, logger, mapping, false);
  }

  public static boolean isMatch(String str, String match) {
    if (str != null && str.length() > 0) {
      boolean mustStart;
      int pos;
      String lStr;
      while (match.length() > 0) {
        mustStart = true;
        while (match.length() > 0 && match.charAt(0) == '*') {
          mustStart = false;
          match = match.substring(1);
        }
        if (match.length() > 0) {
          if (match.indexOf('*') < 0) {
            lStr = match;
            match = "";
          } else {
            lStr = match.substring(0, match.indexOf('*'));
            match = match.substring(match.indexOf('*'));
          }
          pos = str.indexOf(lStr);
          if (pos < 0) return false;
          if (pos > 0 && mustStart) return false;
          str = str.substring(pos + lStr.length());
        } else {
          if (!mustStart)
            return true;
          else
            return false;
        }
      }
    }
    if (str.length() == 0) return true;
    return false;
  }

  /**
   * Funzione che cerca nel resource il valore con una stringa formatata come
   * per match
   *
   * @param searchStr
   *        Elemento da cercare
   * @param boundle
   *        baundle dove cercare
   * @return Identificativo del resource ritrovato
   */
  private static String searchInResource(String id, ResourceBundle boundle) {
    // Prima scorro per verificare se esiste la chiave identica
    for (java.util.Enumeration<?> en = boundle.getKeys(); en.hasMoreElements();) {
      String nome = (String) en.nextElement();
      if (nome.equals(id)) {
        return nome;
      }
    }
    // Scorro le chiavi una seconda volta per vedere se esiste nelle stringhe di
    // ricerca con carattere * in mezzo
    for (java.util.Enumeration<?> en = boundle.getKeys(); en.hasMoreElements();) {
      String nome = (String) en.nextElement();
      if ((nome.indexOf('*') >= 0 && isMatch(id, nome))) {
        return nome;
      }
    }
    return null;
  }

  /**
   * Funzione che restituisce un resource boundle valido pertendo dalla stringa
   * intera man a mano eliminando le parti a destra
   *
   * @param messageKey
   * @return
   */
  private static String getValidResource(String messageKey) {
    ResourceBundle baundle = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);
    String idResource = null;
    while (messageKey != null && messageKey.length() > 0) {
      idResource = searchInResource(messageKey, baundle);
      if (idResource != null)
        return idResource;
      if (messageKey != null && messageKey.indexOf('.') >= 0)
        messageKey = messageKey.substring(0, messageKey.lastIndexOf('.'));
      else
        messageKey = null;
    }
    return "errors.applicazione.inaspettataException";
  }

  private static String getMessaggioProperties(String messageKey,
      String message, Object[] params) {
    StringBuffer buf = new StringBuffer("");
    ResourceBundle res = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

    String logMessageError = res.getString(getValidResource(messageKey));
    if (params != null) {
      for (int i=0; i< params.length; i++) {
      logMessageError = logMessageError.replaceAll(
          UtilityStringhe.getPatternParametroMessageBundle(i),
          params[i].toString());
      }
    }
    buf.append(message);
    buf.append("\n");
    buf.append("Messaggio a video: " + logMessageError);

    return buf.toString();
  }

  /**
   * Funzione che aggiunge un messaggio al request
   *
   * @param action
   *        Azione
   * @param request
   *        request
   * @param messageKey
   *        Chiave del messaggio
   * @param object[]
   *        Parametri d'ingresso
   * @param params
   */
  public static void aggiungiMessaggioProperties(ActionInterface action,
      HttpServletRequest request, String messageKey, Object[] params) {
    // Etraggo un resource valido
    messageKey = getValidResource(messageKey);
    // Qui esegue l'aggiunta dei messaggi
    ActionMessages errors = new ActionMessages();
    if (params == null || params.length == 0)
      errors.add(ActionBase.getTipoMessaggioFromChiave(messageKey),
          new ActionMessage(messageKey));
    else
      errors.add(ActionBase.getTipoMessaggioFromChiave(messageKey),
          new ActionMessage(messageKey, params));
    if (!errors.isEmpty()) action.publicSaveMessages(request, errors);
  }

}
