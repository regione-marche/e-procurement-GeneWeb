package it.eldasoft.gene.tags.history;

import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ActionRedirect;
import org.owasp.csrfguard.CsrfGuard;

public class HistoryItem implements Serializable {

  /** UID */
  private static final long serialVersionUID = -3311382139623135770L;

  private String      title;

  private String      path;

  private String      id;

  private HashMap<String, Object>     parametri;

  /**
   * contenitore delle chiavi dei parametri che non sono memorizzate con un
   * unico valore bensì con un array di valori
   */
  private ArrayList<String>   parametriMultipli;

//  private HttpSession session = null;

  private int         numeroPopUp;

//  private void setSession(ServletRequest request) {
//    if (request instanceof HttpServletRequest) {
//      session = ((HttpServletRequest) request).getSession();
//    }
//  }

  /**
   * Funzione che aggiunge tra i parametri uno di default se sttatto come
   * attributo e non esistente tra tutti i parametri
   *
   * @param nomeParam
   */
  public void addDefaultAttribParam(HttpServletRequest req, String nomeParam) {
    // Verifico se esiste l'attributo ed è di tipo stringa
    if (req.getAttribute(nomeParam) instanceof String) {
      String lsPar = (String) req.getAttribute(nomeParam);
      // Setto l'attributo come parametro solamente se la sua dimensione è
      // maggiore di 0
      if (lsPar.length() > 0) {
        this.parametri.put(nomeParam, lsPar);
      }
    }
  }

  private void setParametri(HttpServletRequest req) {
    // Adesso aggiungo tutti gli hidden di default
    this.path = req.getServletPath();
    this.parametri = new HashMap<String, Object>();
    this.parametriMultipli = new ArrayList<String>();

    // SS 2008-10-27: aggiunta la gestione mediante parameterMap in modo da
    // ottenere anche i parametri di tipo array, in quanto nella versione con
    // "req.getParameterNames" veniva preso solo il primo della lista per un
    // dato nome
    Map.Entry[] setParametriMappa = (Map.Entry[]) req.getParameterMap().entrySet().toArray(
        new Map.Entry[0]);
    for (int i = 0; i < setParametriMappa.length; i++) {
      String nome = (String) setParametriMappa[i].getKey();
      String[] valori = (String[]) setParametriMappa[i].getValue();
      if (valori.length > 1) {
        // se il parametro è un array, viene inserito anche nel contenitore
        // parametri multipli
        this.parametri.put(nome, valori);
        this.parametriMultipli.add(nome);
      } else
        this.parametri.put(nome, valori[0]);
    }
    // per semplificare le modifiche ed evitare di modificare tutte le chiamate get che entrano
    // nell'history, si verifica se tra i parametri è presente il token csrf, e se manca lo si aggiunge in automatico
    CsrfGuard csrfGuard = CsrfGuard.getInstance();
    String tokenName = csrfGuard.getTokenName();
    String tokenValue = csrfGuard.getTokenValue(req, null);
    if (!this.parametri.keySet().contains(tokenName)) {
      this.parametri.put(tokenName, tokenValue);
    }
//    this.setSession(req);

    this.numeroPopUp = 0;
    String lNumeroPopUp = UtilityStruts.getParametroString(req,
        UtilityTags.DEFAULT_HIDDEN_NUMERO_POPUP);
    if (lNumeroPopUp != null && lNumeroPopUp.length() > 0)
      this.numeroPopUp = new Integer(lNumeroPopUp).intValue();
    if (this.numeroPopUp < 0) this.numeroPopUp = 0;
    // Aggiunta di tutti i parametri di default ridirezionati nelle azioni come
    // parametri

  }

  HistoryItem(String id, String title, PageContext context) {
    this.id = id;
    this.title = title;
    setParametri((HttpServletRequest) context.getRequest());

  }

  public HistoryItem(ServletRequest request) {
    setParametri((HttpServletRequest) request);
  }

  /**
   * Funzione che esegue il replace dei parametri
   *
   * @param params
   *        Parametri in cui eseguire il replace nel formato:
   *        [Parametro];[nuovoValore]...
   */
  public void replaceParam(String params) {
    if (params == null || params.length() == 0) return;
    String replaces[] = UtilityTags.stringToArray(params, ';');
    for (int i = 0; i < replaces.length - 1; i++) {
      // Se il parametro esiste lo sostituisco
      if (this.parametri.get(replaces[i]) != null) {
        this.parametri.remove(replaces[i]);
        this.parametriMultipli.remove(replaces[i]);
      }
      this.parametri.put(replaces[i], replaces[i + 1]);
      this.parametriMultipli.add(replaces[i]);
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof HistoryItem) {
      HistoryItem o = (HistoryItem) obj;
      return o.getId().equals(this.id);
    }
    return false;
  }

  /**
   * @return Returns the parametri.
   */
  public Collection<?> getParametri() {
    return this.parametri.entrySet();
  }

  /**
   * @return Returns the path.
   */
  public String getPath() {
    return path;
  }

  /**
   * @return Returns the title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * @return Returns the id.
   */
  public String getId() {
    if (id == null) return "";
    return id;
  }

  /**
   * @param id
   *        The id to set.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @param title
   *        The title to set.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * @param path
   *        The path to set.
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * @return Ritorna parametriMultipli.
   */
  public String[] getParametriMultipli() {
    return parametriMultipli.toArray(new String[0]);
  }

  /**
   * Funzione che esegue il redirect settando anche tutti i parametri
   *
   * @return ActionForward con tutti i parametri settati
   */
  public ActionForward redirect(HttpServletRequest request) {
    ActionForward result = null;
    ActionForward actForward = null;

    if (request.getAttribute(Globals.MESSAGE_KEY) != null
        && !((ActionMessages) request.getAttribute(Globals.MESSAGE_KEY)).isEmpty()) {
      request.getSession().setAttribute(Globals.MESSAGE_KEY,
          request.getAttribute(Globals.MESSAGE_KEY));
      request.removeAttribute(Globals.MESSAGE_KEY);
    }

    HttpSession session = request.getSession();

    if (session != null) {
      session.setAttribute("historyRedirect", this);
      // Creo l'azione di ritorno
      actForward = new ActionForward(UtilityTags.DEFAULT_PATH_PAGINE_JSP
          + "commons/historyRedirect.jsp", false);
      // Setto un nome fittizio
      actForward.setName("out");
      // Setto all'inizio del modulo perché cosi facendo
      // parte dall'inizio dell'applicazione
      actForward.setModule("/");
      result = actForward;
    } else {
      // Creo l'azione di ritorno
      ActionForward foward = new ActionForward(this.getPath(), false);
      // Setto un nome fittizio
      foward.setName("out");
      // Setto all'inizio del modulo perché cosi facendo
      // parte dall'inizio dell'applicazione
      foward.setModule("/");
      ActionRedirect redirect = new ActionRedirect(foward);

      for (Iterator<?> ite = this.parametri.keySet().iterator(); ite.hasNext();) {
        String key = (String) ite.next();
        if (this.parametriMultipli.contains(key)) {
          // siamo nel caso di un array di parametri con lo stesso nome
          String[] elenco = (String[])this.parametri.get(key);
          for (int i= 0; i < elenco.length; i++)
            redirect.addParameter(key, elenco[i]);
        } else {
          redirect.addParameter(key, this.parametri.get(key));
        }
      }
      result = redirect;
    }
    return result;
  }

  /**
   * @return Returns the numeroPopUp.
   */
  public int getNumeroPopUp() {
    return numeroPopUp;
  }

  /**
   * Funzione atta a modificare un parametro
   *
   * @param param
   *        Parametro
   * @param valore
   *        Valore (null se si vuole eliminare)
   */
  public void setParametro(String param, String valore) {
    if(valore==null)
      this.parametri.remove(param);
    else
      this.parametri.put(param, valore);
  }

  @Override
  public String toString() {
    StringBuffer buf = new StringBuffer();

    buf.append("{ ");
    buf.append("title=");
    buf.append(title);
    buf.append(", path=");
    buf.append(path);
    buf.append(", id=");
    buf.append(id);
    buf.append(", parametri=");
    buf.append(parametri);
    buf.append(", numeroPopUp=");
    buf.append(numeroPopUp);
    buf.append(" }");
    return buf.toString();
  }

}