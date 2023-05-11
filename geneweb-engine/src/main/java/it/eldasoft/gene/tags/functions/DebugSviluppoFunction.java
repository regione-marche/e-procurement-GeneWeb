/*
 * Created on 26/ott/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.struts.PlugInBase;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.sql.Time;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che gestisce la sezione di debug si sviluppo
 * 
 * @author Marco.Franceschin
 * 
 */
public class DebugSviluppoFunction extends AbstractFunzioneTag {

  private static final String REQUEST_DEBUG_OBJ  = "requestDebugObject";

  private static final String ID_PAGINA          = "idPagina";
  private static final String ID_MASCHERA        = "idMaschera";

  private static final String LISTA_PROTEZIONI   = "listaProtezioni";
  private static final String OGGETTI_PROTEZIONI = "oggettiProtezioni";

  private static final String STR_REQUEST        = "strRequest";
  private static final String STR_SESSION        = "strSession";
  private static final String STR_APPLICATION    = "strApplication";

  private PageContext         page               = null;

  private static class CompareStrNoCase implements Comparator {

    public int compare(Object arg0, Object arg1) {
      if (arg0 instanceof String && arg0 instanceof String) {
        String str0 = (String) arg0;
        String str1 = (String) arg1;
        return str0.compareToIgnoreCase(str1);
      }
      return 0;
    }

  }

  private static class CompareOggetto implements Comparator {

    public CompareOggetto() {
    }

    public int compare(Object arg0, Object arg1) {
      if (arg0 instanceof HashMap && arg0 instanceof HashMap) {
        HashMap map0 = (HashMap) arg0;
        HashMap map1 = (HashMap) arg1;
        if (map0.get("key") instanceof String
            && map1.get("key") instanceof String) {
          return ((String) map0.get("key")).compareTo((String)map1.get("key"));
        }

      }
      return 0;
    }

  }

  public DebugSviluppoFunction() {
    super(1, new Class[] { String.class });
  }

  /**
   * Funzione che aggiunge un oggetto all'oggetto di debug (che è un hashMap)
   * 
   * @param page
   *        PageContext
   * @param nome
   *        Nome dell'oggetto da aggiungere
   * @param valore
   *        Valore dell'oggetto
   */
  public static void putDebugObj(PageContext page, String nome, Object valore) {
    if (PlugInBase.isSviluppo()) {
      getDebugObj(page).put(nome, valore);
    }
  }

  /**
   * Funzione che estrae un oggetto dal debug
   * 
   * @param page
   *        PageContext
   * @param nome
   *        Nome dell'oggetto
   * @return
   */
  public static Object getAddObj(PageContext page, String nome, Class classe) {
    HashMap debug = getDebugObj(page);
    if (classe.isInstance(debug.get(nome))) {
      return debug.get(nome);
    }
    Object ret = null;
    // Devo create l'oggetto
    try {
      ret = classe.newInstance();
    } catch (Throwable e) {
    }
    // Aggiungo l'oggetto
    debug.put(nome, ret);
    return ret;

  }

  /**
   * Estrazione dell'oggetto di debug
   * 
   * @param page
   * @return
   */
  private static HashMap getDebugObj(PageContext page) {
    HashMap debug;
    if (page.getAttribute(REQUEST_DEBUG_OBJ, PageContext.REQUEST_SCOPE) instanceof HashMap) {
      debug = (HashMap) page.getAttribute(REQUEST_DEBUG_OBJ,
          PageContext.REQUEST_SCOPE);
    } else {
      debug = new HashMap();
      page.setAttribute(REQUEST_DEBUG_OBJ, debug, PageContext.REQUEST_SCOPE);
    }
    return debug;
  }

  /**
   * Funzione che verifica l'esistenza di un oggetto
   * 
   * @param page
   * @param tipo
   * @param oggetto
   * @param valore
   * @return
   */
  private static HashMap getOggetto(PageContext page, String tipo,
      String azione, String oggetto, boolean valore) {
    SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager", page,
        SqlManager.class);
    GeneManager gene = (GeneManager) UtilitySpring.getBean("geneManager",
        page, GeneManager.class);
    HashMap map = new HashMap();
    HashMap azioni = new HashMap();
    String key;
    if ("FUNZ".equals(tipo)) {
      key = oggetto.indexOf('.') >= 0
          ? oggetto.substring(oggetto.indexOf('.') + 1)
              + "."
              + oggetto.substring(0, oggetto.indexOf('.'))
          : oggetto;
    } else
      key = oggetto + " ";
    key += "." + tipo;

    map.put("key", key);
    map.put("id", oggetto);
    map.put("tipo", tipo);
    azioni.put(azione, new Boolean(valore));
    map.put("azioni", azioni);
    map.put("ok", new Boolean(false));

    try {
      Vector v = sql.getVector(
          "select descr from W_OGGETTI where tipo = ? and oggetto = ?",
          new Object[] { tipo, oggetto });
      if (v != null && v.size() > 0) {
        map.put("ok", new Boolean(true));
        map.put("descr", SqlManager.getValueFromVectorParam(v, 0).toString());
      }
    } catch (SQLException e) {
    }

    map.put("subDescr", gene.getSubDescrFromW_OGGETTI(tipo, oggetto));

    return map;
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    this.page = pageContext;
    try {
      pageContext.setAttribute("isSviluppo", new Boolean(
          PlugInBase.isSviluppo()));
      if (PlugInBase.isSviluppo()) {
        if (UtilityTags.getIdRequest(pageContext) != null) {
          String id = UtilityTags.getIdRequest(pageContext);
          if (id.indexOf('.') > 0 && id.indexOf('.', id.indexOf('.') + 1) > 0) {
            addOggetto(pageContext, ID_PAGINA, "PAGE", "VIS", id);
            addProtecCheck(pageContext, "PAGE.VIS." + id, true);
            id = id.substring(0, id.lastIndexOf('.'));
          }
          addOggetto(pageContext, ID_MASCHERA, "MASC", "VIS", id);
          addProtecCheck(pageContext, "MASC.VIS." + id, true);
        }
        // Aggiungo le stringhe con il request
        HashMap debug = getDebugObj(pageContext);
        debug.put(STR_REQUEST, getRequestStr(pageContext.getRequest()));
        debug.put(STR_SESSION, getSessionStr(pageContext.getSession()));
        debug.put(STR_APPLICATION,
            getApplicationStr(pageContext.getServletContext()));

      }

    } catch (Throwable t) {

    }
    return null;
  }

  private Object getApplicationStr(ServletContext context) {
    StringBuffer buf = new StringBuffer("");
    if (context != null) {
      buf.append("<br><b>Attributes</b><br><ul>");
      String keys[] = sortEnum(context.getAttributeNames());
      for (int i = 0; i < keys.length; i++) {
        String id = keys[i];
        addHTMLAttrib(buf, id, context.getAttribute(id));

      }

      buf.append("</ul><br><b>InitParameters</b><br><ul>");
      keys = sortEnum(context.getInitParameterNames());
      for (int i = 0; i < keys.length; i++) {
        String id = keys[i];
        addHTMLAttrib(buf, id, context.getInitParameter(id));
      }
      addHTMLEle(buf, "MajorVersion", context.getMajorVersion());
      addHTMLEle(buf, "MinorVersion", context.getMinorVersion());
      addHTMLEle(buf, "ServerInfo", context.getServerInfo());
      addHTMLEle(buf, "String", context.toString());
      buf.append("</ul>");
    }
    return buf.toString();
  }

  private Object getSessionStr(HttpSession session) {
    StringBuffer buf = new StringBuffer("");
    if (session != null) {

      buf.append("<br><b>Attributes</b><br><ul>");
      String keys[] = sortEnum(session.getAttributeNames());
      for (int i = 0; i < keys.length; i++) {
        String id = keys[i];
        addHTMLAttrib(buf, id, session.getAttribute(id));
      }
      addHTMLEle(buf, "Id", session.getId());
      addHTMLEle(buf, "CreationTime", new Time(session.getCreationTime()));
      addHTMLEle(buf, "LastAccessedTime", new Time(
          session.getLastAccessedTime()));
      addHTMLEle(buf, "MaxInactiveInterval", new Time(
          session.getMaxInactiveInterval()));
      buf.append("</ul>");
    }
    return buf.toString();
  }

  private Object getRequestStr(ServletRequest request) {
    StringBuffer buf = new StringBuffer("");

    if (request != null) {
      String keys[];
      buf.append("<br><b>Parameters</b><br><ul>");

      keys = sortEnum(request.getParameterNames());
      for (int i = 0; i < keys.length; i++) {
        String id = keys[i];
        addHTMLAttrib(buf, id, request.getParameter(id));
      }
      buf.append("</ul><br><b>Attributes</b><br><ul>");
      keys = sortEnum(request.getAttributeNames());
      for (int i = 0; i < keys.length; i++) {
        String id = keys[i];
        addHTMLAttrib(buf, id, request.getAttribute(id));
      }
      addHTMLEle(buf, "CharacterEncoding", request.getCharacterEncoding());
      addHTMLEle(buf, "ContentType", request.getContentType());
      addHTMLEle(buf, "LocalAddr", request.getLocalAddr());
      addHTMLEle(buf, "LocalName", request.getLocalName());
      addHTMLEle(buf, "LocalPort", request.getLocalPort());
      addHTMLEle(buf, "Protocol", request.getProtocol());
      addHTMLEle(buf, "RemoteAddr", request.getRemoteAddr());
      addHTMLEle(buf, "RemoteHost", request.getRemoteHost());
      addHTMLEle(buf, "RemotePort", request.getRemotePort());
      addHTMLEle(buf, "Scheme", request.getScheme());
      addHTMLEle(buf, "ServerName", request.getServerName());
      addHTMLEle(buf, "ServerPort", request.getServerPort());
      buf.append("</ul>");
    }
    return buf.toString();
  }

  private String[] sortEnum(Enumeration en) {
    Vector sort = new Vector();
    while (en.hasMoreElements())
      sort.add(en.nextElement().toString());
    // Ordino la collezione
    Collections.sort(sort, new CompareStrNoCase());
    String ret[] = new String[sort.size()];
    for (int i = 0; i < ret.length; i++)
      ret[i] = (String) sort.get(i);
    return ret;
  }

  private void addHTMLAttrib(StringBuffer buf, String id, Object attribute) {
    buf.append("<li><font color=\"#003000\" size=\"-1\">");
    buf.append(id);
    buf.append("</font><font size=\"-1\">=");
    if (attribute != null && attribute.toString().length() > 0) {
      buf.append("<i>");
      bufAppend(buf, attribute);
      buf.append("</i>");
    }
    buf.append("</font></li>");
  }

  void addHTMLEle(StringBuffer buf, String desc, Object attribute) {
    buf.append("<li><b><font color=\"#003000\" size=\"-1\">");
    buf.append(desc);
    buf.append("</font></b><font size=\"-1\">=");
    if (attribute != null && attribute.toString().length() > 0) {
      buf.append("<i>");
      bufAppend(buf, attribute);
      buf.append("</i>");
    }
    buf.append("</font></li>");
  }

  private void addHTMLEle(StringBuffer buf, String string, int intVal) {
    addHTMLEle(buf, string, new Integer(intVal));
  }

  /**
   * Funzione che appende al buffer
   * 
   * @param buf
   * @param attribute
   */
  private void bufAppend(StringBuffer buf, Object attribute) {
    String toAppend = attribute != null ? attribute.toString() : "";
    boolean big = toAppend.length() > 255;
    if (big) {
      String objId = getId();
      buf.append("<a href='javascript:showHideTr(\"");
      buf.append(objId);
      buf.append("\");' title='...' ><b>...</b></a><span id=\"");
      buf.append(objId);
      buf.append("\" style=\"display: none;\"><font size=\"-2\">");
    }
    buf.append(convStringHTML(toAppend));
    if (big) {
      buf.append("</font></span>");
    }

  }

  private String convStringHTML(String toAppend) {
    return UtilityStringhe.replace(UtilityStringhe.convStringHTML(toAppend), "\n", "<br>");
  }

  private String getId() {
    if (page != null) {
      HashMap debug = getDebugObj(page);
      Long idx = null;
      if (debug.get("objCNT") instanceof Long)
        idx = (Long) debug.get("objCNT");
      else
        idx = new Long(0);
      debug.put("objCNT", new Long(idx.longValue() + 1));
      return "idObj" + String.valueOf(idx.longValue() + 1);

    }
    return null;
  }

  /**
   * Funzione che aggiunge un oggetto
   * 
   * @param pageContext
   * @param nome
   *        Nome da settare nel debug
   * @param tipo
   *        Tipo di oggetto
   * @param azione
   * @param idOggetto
   *        Identificativo delloggetto
   */
  public static void addOggetto(PageContext pageContext, String nome,
      String tipo, String azione, String idOggetto) {
    putDebugObj(pageContext, nome, getOggetto(pageContext, tipo, azione,
        idOggetto, true));

  }

  public static void addProtecCheck(PageContext page, String key, boolean valore) {
    // Se siamo in sviluppo aggiungo la verifica di protezione
    if (PlugInBase.isSviluppo()) {
      Vector listProt = (Vector) getAddObj(page, LISTA_PROTEZIONI, Vector.class);
      HashMap oggetti = (HashMap) getAddObj(page, OGGETTI_PROTEZIONI,
          HashMap.class);
      String sez[] = key.split("[.]");
      String tipo = sez.length > 0 ? sez[0] : "";
      String azione = sez.length > 1 ? sez[1] : "";
      String id = "";
      for (int i = 2; i < sez.length; i++) {
        if (i > 2) id += '.';
        id += sez[i];
      }
      HashMap obj = (HashMap) oggetti.get(tipo + "." + id);
      if (obj == null) {
        obj = getOggetto(page, tipo, azione, id, valore);
        listProt.add(obj);
        oggetti.put(tipo + "." + id, obj);
        Collections.sort(listProt, new CompareOggetto());
      } else {
        ((HashMap) obj.get("azioni")).put(azione, new Boolean(valore));
      }

    }
  }
}
