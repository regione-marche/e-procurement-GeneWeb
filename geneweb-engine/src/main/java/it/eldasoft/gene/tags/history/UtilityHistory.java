package it.eldasoft.gene.tags.history;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;

import java.io.Serializable;
import java.util.Vector;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.struts.action.ActionForward;

/**
 * Classe che si incarica di gestire gli history
 *
 * @author cit_franceschin
 *
 */
public class UtilityHistory implements Serializable {

  /** UID */
  private static final long serialVersionUID = 7205879352363361105L;

  /** Vettore di vettori di history con numero di popUp */
  private Vector<HistoryVector> historyVector;

  HistoryItem    current;

  public UtilityHistory() {
    this.historyVector = new Vector<HistoryVector>();
  }

  /**
   * Funzione che esegue il clear della history
   *
   */
  public void clear(int numPopUp) {
    this.getHistory(numPopUp).clear();
    eliminaPopUpHistory(numPopUp);
  }

  /**
   * Funzione che aggiunge all'histori l'oggetto con i dati di sessione
   *
   * @param id
   *        Identificativo univoco della pagina
   * @param title
   *        Tittolo da mettere nella history
   * @param context
   *        CPageContext
   * @param replaceParams
   *        Parametri da sostituire o da eseguire il replace
   */
  public void add(String id, String title, PageContext context,
      String replaceParams) {
    HistoryItem item = null;
    if (this.current != null) {
      item = current;
      item.setTitle(title);
      item.setId(id);
      // current=null;
    }
    item.replaceParam(replaceParams);
    // Se l'ultimo item � uguale a quello corrente non lo inserisce
    if (getHistory(item.getNumeroPopUp()).size() > 0
        && this.get(getHistory(item.getNumeroPopUp()).size() - 1,
            item.getNumeroPopUp()).equals(item)) {
      // Risetto l'item
      this.getHistory(item.getNumeroPopUp()).set(
          getHistory(item.getNumeroPopUp()).size() - 1, item);
      return;
    }
    getHistory(item.getNumeroPopUp()).add(item);
    eliminaPopUpHistory(item.getNumeroPopUp());
    // Setto gli attributi di dafault (come la dimensione dell'history)
    UtilityTags.addDefaultAttributi(context.getRequest());
  }

  public void saveCurrent(ServletRequest request) {
    this.current = new HistoryItem(request);
  }

  /**
   * Funzione che restituisce il nomero degli item
   *
   * @return
   */
  public int size(int numPopUp) {
    return this.getHistory(numPopUp).size();
  }

  /**
   * Funzione che da un History item voluto
   *
   * @param index
   * @return
   */
  public HistoryItem get(int index, int numPopUp) {
    if (index < 0 || index >= getHistory(numPopUp).size()) return null;
    return (HistoryItem) getHistory(numPopUp).get(index);
  }

  /**
   * Funzione che esegue il vaiA
   *
   * @param idx
   *        Indice dell'history
   * @return
   */
  public ActionForward vaiA(int idx, int numPopUp, HttpServletRequest request)
      throws JspException {
    ActionForward result = null;
    if (idx < 0 || idx >= this.getHistory(numPopUp).size()){
      // Se idx < 0 o >= della dimensione dell'history allora si rimanda alla
      // home page dell'applicazione
      String codiceApplicazione = (String) request.getSession().getAttribute(
          CostantiGenerali.MODULO_ATTIVO);
      result =  UtilityStruts.redirectToPage("home" + codiceApplicazione + ".jsp",
          false, request);
    } else {
      HistoryItem item = this.get(idx, numPopUp);
      // Adesso elimino dall'history tutti sino al punto
      while (getHistory(numPopUp).size() >= (idx + 1)) {
        this.getHistory(numPopUp).remove(getHistory(numPopUp).size() - 1);
      }
      eliminaPopUpHistory(numPopUp);
      result = item.redirect(request);
    }
    return result;
  }

  /**
   * Funzione che esegue il vaiIndietroDi a partire dall'ultimo elemento presente
   * nel history
   *
   * @param numeroPassi
   *        Numero di passi da retrocedere
   * @param numPopUp
   *        Numero della popup di cui chiedere l'history
   * @return
   */
  public ActionForward vaiIndietroDi(int numeroPassi, int numPopUp,
      HttpServletRequest request) throws JspException {
    ActionForward result = null;
    int historySize = this.getHistory(numPopUp).size();
    if (numeroPassi < 0 || numeroPassi >= historySize){
      // Se idx < 0 o >= della dimensione dell'history allora si rimanda alla
      // home page dell'applicazione
      String codiceApplicazione = (String) request.getSession().getAttribute(
          CostantiGenerali.MODULO_ATTIVO);
      result =  UtilityStruts.redirectToPage("home" + codiceApplicazione + ".jsp",
          false, request);
    } else {
      numeroPassi++;
      HistoryItem item = this.get(historySize - numeroPassi, numPopUp);
      // Adesso elimino dall'history tutti sino al punto
      while (numeroPassi > 0) {
        this.getHistory(numPopUp).remove(getHistory(numPopUp).size() - 1);
        numeroPassi--;
      }
      eliminaPopUpHistory(numPopUp);
      result = item.redirect(request);
    }
    return result;
  }

  /**
   * Funzione che ritorna alla pagina precedente
   *
   * @return
   * @throws JspException
   */
  public ActionForward back(HttpServletRequest request)
      throws JspException {
    int numPopUp = UtilityStruts.getNumeroPopUp(request);
    int numero = getHistory(numPopUp).size() - 1;
    if (numero > 0 && current == getHistory(numPopUp).get(numero)) numero--;
    return vaiA(numero, numPopUp, request);
  }

  /**
   * Funzione che ritorna all'ultima pagina letta. Dopo eliminazione Ecc
   *
   * @return
   * @throws JspException
   */
  public ActionForward last(HttpServletRequest request)
      throws JspException {
    int numPopUp = UtilityStruts.getNumeroPopUp(request);
    return vaiA(getHistory(numPopUp).size() - 1, numPopUp, request);
  }

  /**
   * Funzione che estrae l'histori di un popUp
   *
   * @param numPopUp
   * @return
   */
  private Vector<HistoryItem> getHistory(int numPopUp) {
    HistoryVector obj = this.getHistoryObj(numPopUp);
    return obj.getVect();
  }

  private HistoryVector getHistoryObj(int numPopUp) {
    if (this.historyVector.size() < (numPopUp + 1)) {
      while (this.historyVector.size() < (numPopUp + 1))
        this.historyVector.add(new HistoryVector());
    }
    return ((HistoryVector) this.historyVector.get(numPopUp));
  }

  /**
   * Funzione che esegue l'eliminazione degli history maggiori di un determinato
   * popUp
   *
   * @param numPopUp
   */
  private void eliminaPopUpHistory(int numPopUp) {
    if (numPopUp < 0) numPopUp = 0;
    while (this.historyVector.size() > numPopUp + 1)
      this.historyVector.remove(this.historyVector.size() - 1);
  }

  /**
   * Funzione che setta un attributo sull'history
   *
   * @param numPopUp
   *        Numero della popUp
   * @param nome
   *        Nome dell'attributo
   * @param value
   *        Valore dell'attributo
   *
  public void addAttribute(int numPopUp, String nome, Object value) {
    this.getHistoryObj(numPopUp).addAttribute(nome, value);
  }*/

  /**
   * Funzione che estrae un attributo dall'history sul popUp
   *
   * @param numPopUp
   * @param nome
   * @return
   *
  public Object getAttribute(int numPopUp, String nome) {
    return this.getHistoryObj(numPopUp).getAttribute(nome);
  }*/

  /**
   * Funzione che rimuova un attributo dell'history
   *
   * @param numPopUp
   * @param nome
   * @return
   *
  public void removeAttribute(int numPopUp, String nome) {
    this.getHistoryObj(numPopUp).removeAttribute(nome);
  }*/

  /**
   * Funzione che rimuove un elemento dal vettore dell'history
   *
   * @param numPopUp
   * @param nome
   */
  public void removeAttribute(int numPopUp, int indice) {
    this.getHistory(numPopUp).remove(indice);
  }

  public void setDefaultAttribToCurrent(ServletRequest request) {
    if (this.current != null) {
      if (request instanceof HttpServletRequest) {
        HttpServletRequest req = (HttpServletRequest) request;
        this.current.addDefaultAttribParam(req,
            UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
        this.current.addDefaultAttribParam(req,
            UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO);
        this.current.addDefaultAttribParam(req,
            UtilityStruts.PARAMETRO_MODO_APERTURA);
        this.current.addDefaultAttribParam(req,
            UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT);
        this.current.addDefaultAttribParam(req,
            UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA);
        this.current.addDefaultAttribParam(req,
            UtilityTags.DEFAULT_HIDDEN_FROM_DA_TROVA);
        this.current.addDefaultAttribParam(req,
            UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA);
        this.current.addDefaultAttribParam(req, UtilityStruts.DATI_PATH_FILE);
        this.current.addDefaultAttribParam(req,
            UtilityTags.DEFAULT_HIDDEN_FORM_ACTIVEPAGE);
        this.current.addDefaultAttribParam(req,
            UtilityTags.DEFAULT_HIDDEN_FORM_TO_JSP);
        this.current.addDefaultAttribParam(req,
            UtilityTags.DEFAULT_HIDDEN_IS_POPUP);
        this.current.addDefaultAttribParam(req,
            UtilityTags.DEFAULT_HIDDEN_NUMERO_POPUP);
      }
    }

  }

  @Override
  public String toString() {
    StringBuffer buf=new StringBuffer("");

    buf.append(this.historyVector);
    buf.append(" curItem:");
    buf.append(this.current);

    return buf.toString();
  }

}
