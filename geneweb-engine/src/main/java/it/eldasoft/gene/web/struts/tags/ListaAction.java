package it.eldasoft.gene.web.struts.tags;

import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.decorators.trova.FormTrovaTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.transaction.TransactionStatus;

/**
 * Classe che gestisce le interazioni con la lista
 *
 * @author marco.franceschin
 *
 */
public class ListaAction extends DispatchActionBaseNoOpzioni {

  private static Logger logger = Logger.getLogger(ListaAction.class);

  public ActionForward apri(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // Aggiunta del settaggio del path del file in apertura
    String isPopUp = UtilityStruts.getParametroString(request,
        UtilityTags.DEFAULT_HIDDEN_IS_POPUP);
    String pathScheda = null;
    if (isPopUp != null && isPopUp.equals("1")) {
      pathScheda = UtilityStruts.getParametroString(request,
          UtilityTags.DEFAULT_HIDDEN_PATH_SCHEDA_POPUP);
    } else {
      pathScheda = UtilityStruts.getParametroString(request,
          UtilityTags.DEFAULT_HIDDEN_PATH_SCHEDA);
    }
    if (pathScheda != null && !UtilityStruts.isValidJspPath(pathScheda)) {
      // non deve essere possibile redirezionare ad una risorsa non jsp e nemmeno risalire sopra WEB-INF/pages
      String messageKey = "errors.url.notWellFormed";
      String messageError = this.resBundleGenerale.getString(messageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0),
          pathScheda);
      logger.error(messageError);
      this.aggiungiMessaggio(request, messageKey, pathScheda);
      return mapping.findForward(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE);
    }

    request.setAttribute(UtilityTags.DEFAULT_HIDDEN_FORM_TO_JSP,
        pathScheda == null ? "" : pathScheda);
    // Ridireziono sull'azione che gestisce le schede
    return UtilityStruts.redirectToPage("/Scheda.do", true, request);
  }

  /**
   * MOdifica dell'occorrenza
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward modifica(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // {MF150207} Aggiunta delle gestione del controllo sulle protezioni
    try {
      // Setto il metodo in modifica
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO,
          UtilityTags.SCHEDA_MODO_MODIFICA);
      // Ridireziono sull'azione che gestisce le schede
      return this.apri(mapping, form, request, response);
    } catch (Throwable t) {
      if (UtilityStruts.getParametroString(request,
          UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT) != null)
        request.setAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA,
            UtilityStruts.getParametroString(request,
                UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT));
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }

  }

  /**
   * Inserimentyo di una nuova occorrenza
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward nuovo(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    try {
      return this.apri(mapping, form, request, response);
    } catch (Throwable t) {
      if (UtilityStruts.getParametroString(request,
          UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT) != null)
        request.setAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA,
            UtilityStruts.getParametroString(request,
                UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT));
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }

  }

  public ActionForward leggi(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // Ridireziono sulla stesa pagina
    return UtilityStruts.redirectToSamePage(request);
  }

  /**
   * Funzione che esegue il ritorno alla maschera di ricerca
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward ricerca(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    try {
      String entita;
      String outPath;

      // Esecuzione dell'apertura della scheda
      entita = UtilityStruts.getParametroString(request,
          FormTrovaTag.CAMPO_ENTITA);
      if (entita == null)
        throw new Exception(
            "Nell'apertura della scheda non è impostata l'entità principale di partenza !");
      // Ridireziono sulla pagina di default
      outPath = UtilityTags.getPathFromEntita(entita) + "trova.jsp";
      return UtilityStruts.redirectToPage(outPath, false, request);

    } catch (Throwable t) {
      // Se vi è un errore allora gestisco le eccezioni
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }
  }

  /**
   * Esecuzione dell'eliminazione di un elemento
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward elimina(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String entita = UtilityStruts.getParametroString(request,
        UtilityTags.DEFAULT_HIDDEN_NOME_TABELLA);
    // Estraggo le chiavi
    DataColumn colVal[] = UtilityTags.stringKeysToColumnWithParam(UtilityStruts.getParametroString(
        request, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA));
    try {

    AbstractGestoreEntita gestore = UtilityStruts.getGestoreEntita(entita,
        request, UtilityStruts.getParametroString(request,
            UtilityTags.DEFAULT_HIDDEN_NOME_GESTORE));

      gestore.setAction(this);
      gestore.elimina(null, new DataColumnContainer(colVal));
      return UtilityTags.getUtilityHistory(request.getSession()).last(request);
    } catch (Throwable t) {
      if (UtilityStruts.getParametroString(request,
          UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT) != null)
        request.setAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA,
            UtilityStruts.getParametroString(request,
                UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT));
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }

  }

  /**
   * Gestore dell'eliminazione di più elementi della stessa entità. Da selezione
   * multipla
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward eliminaSelez(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String entita = UtilityStruts.getParametroString(request,
        UtilityTags.DEFAULT_HIDDEN_NOME_TABELLA);
    try {
      AbstractGestoreEntita gestore = UtilityStruts.getGestoreEntita(entita,
          request, UtilityStruts.getParametroString(request,
              UtilityTags.DEFAULT_HIDDEN_NOME_GESTORE));
      // Estraggo l'elenco delle chiavi
      // Object
      // keys=request.getAttribute(UtilityTags.DEFAULT_HIDDEN_KEYS_SELECTED);
      String keys[] = request.getParameterValues(UtilityTags.DEFAULT_HIDDEN_KEYS_SELECTED);
      if (keys != null && keys.length > 0) {
        TransactionStatus status = null;
        try {
          // {MF150207} Verifico le protezioni
          gestore.setAction(this);
          // Inizio la transazione
          status = gestore.getSqlManager().startTransaction();
          // Una per una elimino tutte le occorrenze
          for (int i = 0; i < keys.length; i++) {
            gestore.elimina(status, new DataColumnContainer(
                UtilityTags.stringKeysToColumnWithParam(keys[i])));
          }
          gestore.getSqlManager().commitTransaction(status);
        } catch (Throwable t) {
          try {
            gestore.getSqlManager().rollbackTransaction(status);
          } catch (SQLException e) {

          }
          if (UtilityStruts.getParametroString(request,
              UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT) != null)
            request.setAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA,
                UtilityStruts.getParametroString(request,
                    UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT));
          return GestoreEccezioni.gestisciEccezioneAction(t, this, request,
              logger, mapping);
        }
      }
      return UtilityTags.getUtilityHistory(request.getSession()).last(request);

    } catch (Throwable t) {

      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);

    }

  }

  /**
   * Esecuzione dell'update di dati presenti in una lista in modifica
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward updateLista(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String entita = UtilityStruts.getParametroString(request,
        UtilityTags.DEFAULT_HIDDEN_NOME_TABELLA);

    try {
      if (entita == null)
        throw new Exception(
            "Nella lista non è impostata l'entità principale!");

      // Estraggo l'elenco dei campi
      String elencoCampi[] = UtilityTags.stringToArray(
          UtilityStruts.getParametroString(request,
              UtilityTags.DEFAULT_HIDDEN_ELENCO_CAMPI), ';');
      Vector campiAdd = new Vector();

      // Scorro tutti i campi
      for (int i = 0; i < elencoCampi.length; i++) {
        DataColumn col = UtilityStruts.getColumnWithValue(request,
            elencoCampi[i]);
        if (col != null) {
          campiAdd.add(col);
        }
      }
      // Estraggo il gestore che implementa le funzionalità sull'entità
      AbstractGestoreEntita gestore = UtilityStruts.getGestoreEntita(entita,
          request, UtilityStruts.getParametroString(request,
              UtilityTags.DEFAULT_HIDDEN_NOME_GESTORE));

      gestore.setAction(this);
      DataColumnContainer impl = new DataColumnContainer(campiAdd);

      // Eseguo l'update passando le colonne
      gestore.update(null, impl);
      return this.leggi(mapping, form, request, response);
    } catch (Throwable t) {
      // 20090223: si setta l'attributo in modo da tornare alla modalità di edit
      // della lista
      request.setAttribute(UtilityTags.DEFAULT_HIDDEN_UPDATE_LISTA, "1");

      if (UtilityStruts.getParametroString(request,
          UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT) != null)
        request.setAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA,
            UtilityStruts.getParametroString(request,
                UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT));
      return GestoreEccezioni.gestisciEccezioneAction(t, this, request, logger,
          mapping);
    }
  }

}
