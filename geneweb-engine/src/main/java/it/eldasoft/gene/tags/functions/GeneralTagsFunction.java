package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

/**
 * Classe che gestisce altre funzioni generali sui tags
 *
 * @author cit_franceschin
 */
public class GeneralTagsFunction {

  private static Logger logger = Logger.getLogger(GeneralTagsFunction.class);

  public static String getValCampo(String valori, String campo) {
    String vals[] = UtilityTags.stringToArray(valori, ';');
    // Scorro tutti i valori per ricercare i voluti
    for (int i = 0; i < vals.length; i++) {
      if (vals[i].indexOf('=') >= 0) {
        String nomeCampo = vals[i].substring(0, vals[i].indexOf('='));
        if (campo.indexOf('.') < 0) {
          nomeCampo = nomeCampo.substring(nomeCampo.indexOf('.') + 1);
        }
        // Se il nome è quello cercato allora restituisco il valore
        if (nomeCampo.equals(campo)) {
          String valoreCampo = vals[i].substring(vals[i].indexOf('=') + 1);
          if (valoreCampo.length() > 1 && valoreCampo.charAt(1) == ':')
            valoreCampo = valoreCampo.substring(2);
          return valoreCampo;
        }
      }
    }
    return "";
  }

  public static String callFunction(String objClass, Object param0)
      throws JspException {
    return callFunction(objClass, new Object[] { param0 });
  }

  public static String callFunction2(String objClass, Object param0,
      Object param1) throws JspException {
    return callFunction(objClass, new Object[] { param0, param1 });
  }

  public static String callFunction3(String objClass, Object param0,
      Object param1, Object param2) throws JspException {
    return callFunction(objClass, new Object[] { param0, param1, param2 });
  }

  public static String callFunction4(String objClass, Object param0,
      Object param1, Object param2, Object param3) throws JspException {
    return callFunction(objClass,
        new Object[] { param0, param1, param2, param3 });
  }

  public static String callFunction5(String objClass, Object param0,
      Object param1, Object param2, Object param3, Object param4)
      throws JspException {
    return callFunction(objClass, new Object[] { param0, param1, param2,
        param3, param4 });
  }

  private static String callFunction(String objClass, Object[] params)
      throws JspException {
    Object obj = UtilityTags.createObject(objClass);
    if (!(obj instanceof AbstractFunzioneTag)) {
      throw new JspException("Attenzione ! Tag callFunction: l'oggetto "
          + objClass
          + " non è ereditato da AbstractFunzioneTag !");
    }
    AbstractFunzioneTag fn = (AbstractFunzioneTag) obj;

    PageContext contextPage = null;
    // Cerco tra i parametri se esiste il page context
    for (int i = 0; i < params.length; i++) {
      if (params[i] instanceof PageContext) {
        contextPage = (PageContext) params[i];
        fn.setRequest((HttpServletRequest)contextPage.getRequest());
      }
    }

    JspException e = fn.getJspException(contextPage, params);
    if (e != null) throw e;
    String lsRet = fn.function(contextPage, params);
    if (lsRet != null) return lsRet;
    return "";
  }

  /**
   * Funzione che restituisce
   *
   * @param condizione
   * @param objTrue
   * @param objFalse
   * @return
   */
  public static Object iif(Boolean condizione, Object objTrue, Object objFalse) {
    if (condizione == null || condizione.booleanValue())
      return objTrue;
    else
      return objFalse;
  }

  /**
   * Funzione che converte una stringa per essere messa all'interno di un
   * javascript
   *
   * @param str
   * @return
   */
  public static String convertJsString(String str) {
    StringBuffer buf = new StringBuffer("\"");
    if (str != null && str.length() > 0) {
      str = UtilityStringhe.replace(UtilityTags.convStringa(str), "\n", "\\n");
      buf.append(str);
    }
    buf.append("\"");
    return buf.toString();
  }

  /**
   * Funzione che converte una stringa per essere messa all'interno di un
   * javascript
   *
   * @param str
   * @return
   */
  public static String toString(Object str) {
    if (str != null) return str.toString();
    return null;
  }

  /**
   * Funzione che estrae un oggetto da una list o da un HashMap
   *
   * @param map
   *        Mappa o lista
   * @param id
   *        Identificativo dell'oggetto
   * @return
   */
  public static Object getObj(Object map, Object id) {
    try {
      if (map instanceof HashMap) {
        return ((HashMap) map).get(id);

      } else if (map instanceof List) {
        List l = (List) map;
        int index = -1;
        if (id instanceof String)
          index = Integer.valueOf((String) id).intValue();
        else if (id instanceof Integer)
          index = ((Integer) id).intValue();
        else if (id instanceof Long) index = ((Long) id).intValue();
        if (index >= 0) return l.get(index);

      }
    } catch (Throwable t) {
      logger.error("Errore nel passaggio dei parametri !");
    }
    return null;
  }

  public static String getIdPagina(PageContext page) {
    return UtilityTags.getIdRequest(page, true);
  }

  public static String getInfoPagina(PageContext page, String modoVisualizzazione) throws JspException {
    String idPagina = UtilityTags.getIdRequest(page, true);
    SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager", page, SqlManager.class);
    String nota = null;
    try {
      nota = GestioneHelpPaginaFunction.getNota(sql, 2, idPagina,modoVisualizzazione);
    } catch (SQLException e) {
      throw new JspException(e.getMessage(), e);
    }
    return nota;
  }

  /**
   * Funzione che estrae la funzione in funzione del database attivo
   *
   * @param pageContext
   *        Page Context
   * @param funz
   *        Funzione da cercare
   * @param parameters
   *        Parametri di passaggio divisi da ;
   * @return Funzione convertita in funzione del tipo di database
   */
  public static String getDBFunction(PageContext pageContext, String funz,
      String parameters) {
    SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    String params[] = UtilityTags.stringToArray(parameters, ';');
    return sql.getDBFunction(funz, params);
  }

  /**
   * Testa se esiste una tabella nel DB
   *
   * @param pageContext
   *        Page Context
   * @param table
   *        Tabella da verificare se esiste
   * @return true se la tabella esiste, false altrimenti
   */
  public static Boolean isTable(PageContext pageContext, String table) {
    SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    return new Boolean(sql.isTable(table));
  }

  /**
   * Funzione che esegue la concatenazione di due oggetti trasformandoli in
   * stringa
   *
   * @param obj0
   *        Primo oggetto
   * @param obj1
   *        Secondo oggetto
   * @return
   */
  public static String concat(Object obj0, Object obj1) {
    StringBuffer buf = new StringBuffer("");
    buf.append(obj0);
    buf.append(obj1);
    return buf.toString();
  }

  /**
   * Funzione che esegue il cast di un oggetto
   *
   * @param tipo
   *        Tipo di oggetto in output che può essere:
   *        <li><b>double</b> double<br>
   *        <li><b>string</b> Stringa
   *        <li><b>long</b> Long
   * @param obj
   *        Oggetto d'ingresso
   * @return
   */
  public static Object cast(String tipo, Object obj) {
    if (tipo != null && obj != null) {
      if ("double".equalsIgnoreCase(tipo)) {
        // Trasformazione in double
        if (obj instanceof Double) {
          return obj;
        } else if (obj instanceof Long) {
          return new Double(((Long) obj).doubleValue());
        } else if (obj instanceof Integer) {
          return new Double(((Integer) obj).doubleValue());
        } else {
          String str = obj.toString();
          if (str.length() > 0)
            try {

              Double ret = new Double(str);
              return ret;
            } catch (Throwable t) {
              throw new RuntimeException(
                  "gene:cast errore in conversione del tipo String->Double!", t);
            }
        }

      } else if ("long".equalsIgnoreCase(tipo)) {
        // Trasformazione in long
        if (obj instanceof Double) {
          return new Long(((Double) obj).longValue());
        } else if (obj instanceof Long) {
          return new Long(((Long) obj).longValue());
        } else if (obj instanceof Integer) {
          return new Long(((Integer) obj).longValue());
        } else {
          String str = obj.toString();
          if (str.length() > 0)
            try {

              Long ret = new Long(str);
              return ret;
            } catch (Throwable t) {
              throw new RuntimeException(
                  "gene:cast errore in conversione del tipo String->Long!", t);
            }
        }

      } else if ("string".equalsIgnoreCase(tipo)) {
        return obj.toString();
      } else {
        throw new RuntimeException("gene:cast tipo di oggetto non definito: "
            + tipo);
      }
    } else if (obj != null) {
      throw new RuntimeException("gene:cast tipo di oggetto non definito: "
          + tipo);
    }
    return null;
  }

}
