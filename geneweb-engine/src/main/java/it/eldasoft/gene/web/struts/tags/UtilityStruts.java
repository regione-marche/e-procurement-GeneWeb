package it.eldasoft.gene.web.struts.tags;

import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.decorators.campi.UtilityDefinizioneCampo;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.DefaultGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Classe con le costanti generali utilizzate da struts
 *
 * @author marco.franceschin
 *
 */
public class UtilityStruts {

  /** Nella struttura dati il nome del path in cui e stato rediretto il trova */
  public static final String  DATI_PATH_FILE          = "PathRedirectFile";

  /** Parametro di modalità d'apertura della maschera * */
  public static final String  PARAMETRO_MODO_APERTURA = "modoApertura";

  private static final String MSG_GESTORE_ERRATO      = "gestore.classeErrata";

  /**
   * Funzione che ridireziona una pagina su un path voluto
   *
   * @param ref
   *        Path di dove ridirezionare
   * @param absolute
   *        Se false aggiunge l'inizio il path di default delle pagine
   * @return ActionFoward
   */
  public static ActionForward redirectToPage(String ref, boolean absolute,
      ServletRequest request) {

    String page;
    if (!absolute)
      page = UtilityTags.DEFAULT_PATH_PAGINE_JSP;
    else
      page = "";
    page += ref;
    // Creo l'azione di ritorno
    ActionForward ret = new ActionForward(page, false);
    // Setto un nome fittizio
    ret.setName("out");
    // Setto all'inizio del modulo perché cosi facendo
    // parte dall'inizio dell'applicazione
    ret.setModule("/");
    UtilityTags.addDefaultAttributi(request);
    return ret;

  }

  /**
   * Funzione che restituisce un parametro. Se il parametro è vuoto restituisce
   * null anche se è ""
   *
   * @param request
   *        Request da cui estrarre il parametro
   * @param parametro
   *        Parametro da estrarre
   * @return
   */
  public static String getParametroString(HttpServletRequest request,
      String parametro) {
    String ret = null;

    // Come prima cosa verifico se si trova negli attributi
    if (request.getAttribute(parametro) != null
        && request.getAttribute(parametro) instanceof String)
      ret = (String) request.getAttribute(parametro);
    else if (request.getParameter(parametro) != null)
      ret = request.getParameter(parametro);
    else if (request.getAttribute(parametro) != null) {
      ret = request.getAttribute(parametro).toString();
    }
    if (ret != null && ret.length() == 0) ret = null;
    return ret;
  }

  /**
   * Funzione che converte un parametro nell'oggetto
   *
   * @param valore
   *        valore del parametro
   * @param tipoParametro
   * @return
   */
  public static Object getParameter(String valore, String tipoParametro) {
    if (valore != null && valore.length() != 0) {

      switch (getTipo(tipoParametro)) {
      case 'F': // Campo decimale
        return Double.valueOf(valore);
      case 'N': // Campo numerico
        return Long.valueOf(valore);
      case 'D': // Campo data
        Date date = UtilityDate.convertiData(valore,
            UtilityDate.FORMATO_GG_MM_AAAA);
        Timestamp time = new Timestamp(date.getTime());
        return time;

      case 'T': // Campo testo
        return valore;
      }
    }
    return null;
  }

  /**
   * Funzione che retituisce il tipo di parametro da un parametro
   *
   * @param tipoParametro
   * @return
   */
  public static char getTipo(String tipoParametro) {
    switch (tipoParametro.charAt(0)) {
    case JdbcParametro.TIPO_ENUMERATO:
      // Se è un tipo enumerato significa che la definizione del tipo
      // di campo è dopo
      return getTipo(tipoParametro.substring(1));
    case JdbcParametro.TIPO_DECIMALE: // Campo decimale
    case JdbcParametro.TIPO_NUMERICO: // Campo numerico
    case JdbcParametro.TIPO_DATA: // Campo data
    case JdbcParametro.TIPO_TESTO: // Campo testo
      return tipoParametro.charAt(0);
    }
    // Se non definito restituisce non definito
    return JdbcParametro.TIPO_INDEFINITO;
  }

  public static AbstractGestoreEntita getGestoreEntita(String entita,
      HttpServletRequest request, String classe) throws GestoreException {

    AbstractGestoreEntita gestoreResult = null;
    // se non è assegnato gestore ne assegno uno di default
    if ("".equals(classe) || classe == null)
      gestoreResult = new DefaultGestoreEntita(entita, request);
    else {
      // se è stato indicato un gestore controllo che sia una classe esistente e
      // del giusto tipo
      Object obj = UtilityTags.createObject(classe);
      if (obj != null && (obj instanceof AbstractGestoreEntita)) {
        AbstractGestoreEntita gest = (AbstractGestoreEntita) obj;
        gest.setRequest(request);
        gestoreResult = (AbstractGestoreEntita) obj;
      } else
        // se non esiste il gestore indicato o non è una AbstractGestoreEntita
        throw new GestoreException("Il gestore indicato non esiste",
            MSG_GESTORE_ERRATO);
    }
    return gestoreResult;
  }

  /**
   * Funzione che ridireziona sulla stessa pagina chiamante
   *
   * @param request
   * @return
   */
  public static ActionForward redirectToSamePage(HttpServletRequest request) {
    String path;
    path = getParametroString(request, UtilityTags.DEFAULT_HIDDEN_FORM_JSP_PATH);
    if (path != null && path.length() > 0 && UtilityStruts.isValidJspPath(path))
      return redirectToPage(path, true, request);
    return redirectToPage(request.getServletPath(), true,
        request);
  }

  /**
   * Verifica se il path relativo ad una risorsa JSP interna all'applicativo &egrave; valida. In questo modo si riescono a bloccare
   * eventuali path paricolosi a risorse iniettati mediante modifica dei parametri da querystring o nelle form.
   *
   * @param relativePath
   *        path da controllare
   * @return true se il path &egrave; relativo esclusivamente ad una JSP e non cerca di risalire le cartelle a partire da WEB-INF/pages,
   *         false altrimenti
   */
  public static boolean isValidJspPath(String relativePath) {
    int queryParams = relativePath.indexOf("?");
    boolean isJsp = false;
    if (queryParams == -1) {
      isJsp = relativePath.endsWith(".jsp");
    } else {
      isJsp = relativePath.substring(0, queryParams).endsWith(".jsp");
    }

    boolean valid = !StringUtils.contains(relativePath, "../") && isJsp;
    return valid;
  }

  /**
   * Funzione che restituisce il valore della colonna
   *
   * @param request
   *        request per l'estrazione dei dati
   * @param nomeCampo
   *        Nome del campo da estrarre
   * @return DataColumn colonna con il valore
   */
  public static DataColumn getColumnWithValue(
      HttpServletRequest request, String nomeCampo) {
    String definizione = UtilityStruts.getParametroString(request,
        UtilityTags.DEFAULT_HIDDEN_INIZIO_DEFINIZIONE + nomeCampo);
    String valore = UtilityStruts.getParametroString(request, nomeCampo);
    return getColumnWithValue(valore, definizione, nomeCampo);
  }

  /**
   * Funzione che trasforma un valore e la sua definizione in una colonna con
   * valori
   *
   * @param valore
   *        Valore della colonna
   * @param definizione
   *        Definizione del campo
   * @param nomeCampo
   *        Nome eventuale del campo se non è settato nella definizione
   * @return
   */
  public static DataColumn getColumnWithValue(String valore,
      String definizione, String nomeCampo) {
    DataColumn ret = null;
    char tipoVal = JdbcParametro.TIPO_TESTO;

    if (definizione != null) {
      tipoVal = UtilityDefinizioneCampo.getTipoCharFromDef(definizione);
      ret = new DataColumn(null,
          UtilityDefinizioneCampo.getNomeFisicoFromDef(definizione), tipoVal);

      ret.setOriginalValue(JdbcParametro.getParametro(tipoVal,
          UtilityDefinizioneCampo.getValue(definizione)));
      ret.setChiave(UtilityDefinizioneCampo.isKey(definizione));
    } else {

      /*
       * ret = new DataColumn(null, "CAMPIAGGIUNTI."+nomeCampo ,
       * JdbcParametro.TIPO_TESTO); ret.setChiave(false);
       */
    }
    if (ret != null) ret.setValue(JdbcParametro.getParametro(tipoVal, valore));
    return ret;
  }

  /**
   * Funzione che verifica se la pagina è in modalità popUp
   *
   * @param request
   * @return
   */
  public static boolean isPopUp(HttpServletRequest request) {
    String isPopUp = UtilityStruts.getParametroString(request,
        UtilityTags.DEFAULT_HIDDEN_IS_POPUP);
    return isPopUp != null && isPopUp.equals("1");
  }

  /**
   * Funzione che estrae il numero del popup dalla request
   *
   * @param request
   * @return
   */
  public static int getNumeroPopUp(ServletRequest request) {
    String numeroPopUp = getParametroString((HttpServletRequest) request,
        "numeroPopUp");
    int liNumeroPopUp = 0;
    if (numeroPopUp != null && numeroPopUp.length() > 0)
      liNumeroPopUp = new Integer(numeroPopUp).intValue();
    if (liNumeroPopUp < 0) liNumeroPopUp = 0;
    return liNumeroPopUp;
  }

  /**
   * Funzione che aggiunge un messaggio sul request
   *
   * @param request
   *        Request dove aggiungere il messaggio
   * @param tipo
   *        Tipo di messaggio che può assumere i valori: error; warning; info
   * @param key
   *        Chiave del messaggio
   * @param params
   *        Eventuali parametri del messaggio
   */
  public static void addMessage(ServletRequest request, String tipo,
      String key, Object[] params) {
    Vector vect = new Vector();
    vect.add("error");
    vect.add("warning");
    vect.add("info");

    // Di default setto il tipo info
    if (vect.indexOf(tipo) < 0) tipo = "info";

    // Estraggo il nome della chiave dei messaggi struts

    ActionMessages errors = (ActionMessages) request.getAttribute(Globals.MESSAGE_KEY);
    if (errors == null) errors = new ActionMessages();
    if (params == null || params.length == 0)
      errors.add(tipo, new ActionMessage(key));
    else {
      errors.add(tipo, new ActionMessage(key, params));
    }
    // Verifico dove devo aggiungere
    request.setAttribute(Globals.MESSAGE_KEY, errors);

  }

//  public static Object[] getArrayObjFromList(List par) {
//
//    Object ret[] = new Object[par != null ? par.size() : 0];
//    if (par != null) for (int i = 0; i < par.size(); i++) {
//      ret[i] = par.get(i);
//    }
//    return ret;
//  }


  /**
   * Legge i dati presenti nel request
   *
   * @param request
   *        request HTTP
   * @return array di elementi contenenti le informazioni ricevute dalla pagina
   *         e presenti nel form
   */
  public static DataColumn[] getDatiRequest(HttpServletRequest request) {
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
    // creo l'array con i dati estratti
    return (DataColumn[]) campiAdd.toArray(new DataColumn[0]);
  }


}
