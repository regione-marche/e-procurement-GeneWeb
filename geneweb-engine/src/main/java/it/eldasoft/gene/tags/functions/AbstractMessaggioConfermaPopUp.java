/*
 * Created on 29/nov/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione astratte per gestire l'eliminazione con popUp
 * 
 * @author Marco.Franceschin
 * 
 */
public abstract class AbstractMessaggioConfermaPopUp extends
    AbstractFunzioneTag {

  /** Tipi di messaggi */
  public final static int     TIPO_MESSAGGIO          = 0;
  public final static int     TIPO_ATTENZIONE         = 1;
  public final static int     TIPO_ERRORE             = 2;
  /** Tipo di bulsanti */
  public final static int     BUTTON_DEFAULT          = 0;
  public final static int     BUTTON_CONFERMA         = 1;
  public final static int     BUTTON_ANNULLA          = 2;
  public final static int     BUTTON_CONFERMA_ANNULLA = 3;
  /** Costanti del page context */
  private final static String PAGE_ERROR_LEVEL        = "errorLevel";
  private final static String PAGE_TITOLO             = "titoloMessaggio";
  private final static String PAGE_BUTTONS            = "bottoniPagina";
  private final static String PAGE_ESEGUI_BUTTON      = "autoEseguiPagina";
  /** Costanti nel resource */
  private final static String RES_MSG_MSG             = "label.tags.confermaPopUp.msg";
  private final static String RES_MSG_WAR             = "label.tags.confermaPopUp.war";
  private final static String RES_MSG_ERR             = "label.tags.confermaPopUp.err";
  private final static String RES_TITLE               = "label.tags.confermaPopUp.title";

  SqlManager                  sql;
  Vector                      messaggi;
  PageContext                 pageContext;

  private boolean                 autoEsegui            = false;

  private class Messaggio {

    /** Tipo del messaggio */
    private int    tipo;
    /** Testo del messaggio */
    private String messaggio;

    public Messaggio(int tipo, String messaggio) {
      switch (tipo) {
      case 2:
        this.tipo = TIPO_ERRORE;
        break;
      case 1:
        this.tipo = TIPO_ATTENZIONE;
        break;
      default:
        this.tipo = TIPO_MESSAGGIO;
      }
      this.messaggio = messaggio;
    }

    public String toString() {
      StringBuffer buf = new StringBuffer("");
      switch (this.getTipo()) {
      case TIPO_ERRORE:
        buf.append("<li class=\"errori-javascript-err\" ><b>ERRORE:</b> ");
        break;
      case TIPO_ATTENZIONE:
        buf.append("<li class=\"errori-javascript-war\" ><b>ATTENZIONE:</b> ");
        break;
      default:
        buf.append("<li class=\"errori-javascript-msg\" > ");
        break;
      }
      buf.append(this.getMessaggio());
      return buf.toString();
    }

    public String getMessaggio() {
      return messaggio;
    }

    public int getTipo() {
      return tipo;
    }

  }

  /**
   * I parametri d'ingresso sono il PageContext
   * 
   */
  public AbstractMessaggioConfermaPopUp() {
    super(3, new Class[] { PageContext.class, String.class, String.class });
    messaggi = new Vector();
  }

  /**
   * Funzione che estrae il tipo massimo nell'elenco dei messaggi
   * 
   * @return
   */
  private int getMaxMessaggio() {
    int ret = 0;
    for (int i = 0; i < messaggi.size(); i++) {
      if (ret < ((Messaggio) messaggi.get(i)).getTipo()) {
        ret = ((Messaggio) messaggi.get(i)).getTipo();
        if (ret == TIPO_ERRORE) return TIPO_ERRORE;
      }
    }

    return ret;
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    int errorLevel;
    int buttons;
    int rowCount = 1;
    String msg;
    String userMsg;
    String addTipo = params[2] != null && params[2].toString().length() > 0
        ? "." + params[2].toString()
        : "";
    // Elimino tutti i messaggi
    messaggi.clear();
    setAutoEsegui(false);
    this.pageContext = pageContext;
    // Estraggo l'sql manager
    sql = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext,
        SqlManager.class);
    try {
      // Si devono eliminare più occorrenze
      String chiavi[] = pageContext.getRequest().getParameterValues(
          (String) params[1]);
      if (chiavi != null) {
        for (int i = 0; i < chiavi.length; i++) {
          verificaElimina(chiavi[i], i + 1, chiavi.length);
        }
        rowCount = chiavi.length;
      } else {
        verificaElimina((String) params[1], 1, 1);
      }

    } catch (GestoreException e) {
      UtilityStruts.addMessage(this.getRequest(), "error", e.getCodice(), null);
    }
    errorLevel = getMaxMessaggio();
    userMsg = getMessaggio(errorLevel);
    msg = getMessaggi();
    if (userMsg != null) {
      msg = UtilityStringhe.replace(userMsg, "{0}", msg);
    } else {
      String keyMsgStandard = null;
      switch (errorLevel) {
      case TIPO_ERRORE:
        keyMsgStandard = RES_MSG_ERR;
        break;
      case TIPO_ATTENZIONE:
        keyMsgStandard = RES_MSG_WAR;
        break;
      default:
        keyMsgStandard = RES_MSG_MSG;
      }
      msg = UtilityTags.getResource(keyMsgStandard + addTipo,
          new String[] { msg }, true);
    }
    // Setto nel contesto della pagina il livello degli errori
    this.getPageContext().setAttribute(PAGE_ERROR_LEVEL,
        new Integer(errorLevel));
    if (this.getTitolo(rowCount) != null)
      this.getPageContext().setAttribute(PAGE_TITOLO, this.getTitolo(rowCount));
    else
      this.getPageContext().setAttribute(PAGE_TITOLO,
          UtilityTags.getResource(RES_TITLE + addTipo, new String[] {}, true));
    // Settaggio del tipo di bottonu
    buttons = getPulsanti(errorLevel);
    if (buttons == BUTTON_DEFAULT) {
      // Calcolo i bottoni in funzione del livello d'errore
      if (errorLevel == TIPO_ERRORE) {
        buttons = BUTTON_ANNULLA;
      } else
        buttons = BUTTON_CONFERMA_ANNULLA;
    }
    this.getPageContext().setAttribute(PAGE_BUTTONS, new Integer(buttons));
    this.getPageContext().setAttribute(PAGE_ESEGUI_BUTTON,
        new Boolean(autoEsegui));

    return msg;
  }

  private String getMessaggi() {
    StringBuffer buf = new StringBuffer("");
    // if (messaggi.size() > 0) buf.append("<UL>");
    for (int i = 0; i < messaggi.size(); i++)
      buf.append(messaggi.get(i));
    // if (messaggi.size() > 0) buf.append("</UL>");
    if (buf.toString().length() > 0) return "<UL>" + buf.toString() + "</UL>";
    return "";
  }

  /**
   * Funzione che esegue la verifica dell'eliminazione dell'occorrenza
   * 
   * @param chiaveRiga
   *        Chiave della riga
   * @param rowNum
   *        Numero della riga
   * @param rowCount
   *        Numero di righe totali
   */
  private void verificaElimina(String chiaveRiga, int rowNum, int rowCount)
      throws GestoreException {
    DataColumnContainer impl = new DataColumnContainer(chiaveRiga);
    if (impl.getColonne().values().size() == 0) {
      String elencoColonne = UtilityStruts.getParametroString(
          (HttpServletRequest) this.getPageContext().getRequest(), chiaveRiga);
      if (elencoColonne == null) elencoColonne = chiaveRiga;
      // Se non si è creata nessuna colonna significa che si tratta dell'elenco
      // delle colonne
      impl.addColumns(this.getPageContext().getRequest(), elencoColonne, false);
    }
    verificaRiga(impl, rowNum, rowCount);
  }

  /**
   * Funzione che aggiunge un messaggio
   * 
   * @param messaggio
   *        Testo del messaggio
   */
  public void addMsg(String messaggio) {
    this.messaggi.add(new Messaggio(TIPO_MESSAGGIO, messaggio));
  }

  /**
   * Funzione che aggiunge un messaggio d'attenzione
   * 
   * @param messaggio
   *        Testo del messaggio
   */
  public void addWarning(String messaggio) {
    this.messaggi.add(new Messaggio(TIPO_ATTENZIONE, messaggio));
  }

  /**
   * Funzione che aggiunge un messaggio d'errore
   * 
   * @param messaggio
   *        Testo del messaggio
   */
  public void addError(String messaggio) {
    this.messaggi.add(new Messaggio(TIPO_ERRORE, messaggio));
  }

  /**
   * Funzione che aggiunge il messaggio d'errore prendendolo dal resource
   * 
   * @param keyResource
   * @param params
   */
  public void addErrorKey(String keyResource, String[] params) {
    addError(UtilityTags.getResource(keyResource, params, true));

  }

  /**
   * Funzione che aggiunge il messaggio d'attenzione prendendolo dal resource
   * 
   * @param keyResource
   * @param params
   */
  public void addWarningKey(String keyResource, String[] params) {
    addWarning(UtilityTags.getResource(keyResource, params, true));

  }

  /**
   * Funzione che aggiunge il messaggio prendendolo dal resource
   * 
   * @param keyResource
   * @param params
   */
  public void addMsgKey(String keyResource, String[] params) {
    addMsg(UtilityTags.getResource(keyResource, params, true));
  }

  /**
   * PageContext
   * 
   * @return
   */
  public PageContext getPageContext() {
    return pageContext;
  }

  /**
   * SqlManager per l'esecuzione di selezioni su database
   * 
   * @return
   */
  public SqlManager getSql() {
    return sql;
  }

  /**
   * Funzione che estrae il titolo del messaggio
   * 
   * @param rowCount
   *        Conteggio delle righe da eliminare
   * @return
   */
  abstract public String getTitolo(int rowCount);

  /**
   * Funzione per settare il messaggio di conferma d'eliminazione
   * 
   * @param errorLevel
   *        Livello d'errore che puo assumere i seguenti valori:
   *        <li><b>{@link #TIPO_MESSAGGIO}</b> Livello di messaggio
   *        <li><b>{@link #TIPO_ATTENZIONE}</b> Livello di atenzione
   *        <li><b>{@value #TIPO_ERRORE}</b> Livello d'errore
   * @return Messaggio da mettere se null imposta il messaggio standard. Nel
   *         testo viene sostituita la parte <b>{0}</b> con i testi dei
   *         messaggi
   */
  abstract protected String getMessaggio(int errorLevel);

  /**
   * Funzione che serve per verificare l'eliminazione sulla riga. In questa
   * funzione saranno aggiunti i messaggi con #{@link #addMsg(String)}, #{@link #addWarning(String)}
   * e #{@link #addError(String)}
   * 
   * @param impl
   *        Impl con il valore dei campi
   * @param row
   *        Riga corrente in eliminazione
   * @param count
   *        Conteggio delle righe da eliminare
   */
  abstract protected void verificaRiga(DataColumnContainer impl, int row,
      int count) throws GestoreException;

  /**
   * Funzione che restituisce i tipo di pulsanti da inserire nel messaggio.
   * 
   * @param errorLevel
   *        Livello d'errore che puo assumere i seguenti valori:
   *        <li><b>{@link #TIPO_MESSAGGIO}</b> Livello di messaggio
   *        <li><b>{@link #TIPO_ATTENZIONE}</b> Livello di atenzione
   *        <li><b>{@value #TIPO_ERRORE}</b> Livello d'errore
   * @return Pulsanti da inserire:
   *         <li><b>{@link #BUTTON_DEFAULT}</b> Viene gestito automaticamente
   *         in funzione del livello
   *         <li><b>{@link #BUTTON_CONFERMA}</b> Solo il pulsante di conferma
   *         <li><b>{@link #BUTTON_ANNULLA}</b> Solo il pulsante di annulla
   *         <li><b>{@link #BUTTON_CONFERMA_ANNULLA}</b> Pulsante di conferma
   *         e annulla.
   */
  abstract protected int getPulsanti(int errorLevel);

  
  public boolean isAutoEsegui() {
    return autoEsegui;
  }

  
  public void setAutoEsegui(boolean autoEsegui) {
    this.autoEsegui = autoEsegui;
  }


}
