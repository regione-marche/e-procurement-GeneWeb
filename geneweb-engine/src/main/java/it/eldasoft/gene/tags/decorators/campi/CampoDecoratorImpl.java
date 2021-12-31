/*
 * Created on 29-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.campi;

import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoCLOB;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoDataElda;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoDecimale;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoMoney;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoMoney5Dec;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoPercentuale0Dec;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoPercentuale2Dec;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoPercentuale5Dec;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoPercentuale9Dec;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNo;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoTimestamp;
import it.eldasoft.gene.tags.js.Javascript;
import it.eldasoft.gene.tags.link.PopUpGenericoImpl;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;

/**
 * Classe base per un decoratore di campo di input o output. Attenzione che ha
 * valore solo se all'interno della pagina viene linkato anche forms.js
 *
 * @author marco.franceschin
 */
public class CampoDecoratorImpl {

  // ************************************************************
  // Storia Modifiche:
  // Data Utente Descrizione
  // 29/09/2006 M.F. Aggiunta del nome fisico del campo
  // ************************************************************

  Logger                         logger        = Logger.getLogger(CampoDecoratorImpl.class);

  /** Proprietà da impostare nel campo decorator generico */
  /** Nome del campo */
  private String                 nome;

  /** Nome delle form */
  private String                 formName;

  /** Valore del campo */
  private String                 value;

  /** Valore di default */
  private String                 defaultValue  = null;

  /** Valore originario del campo, nel caso di ricaricamento della pagina con errori */
  private String                 originalValue;

  /** Titolo del campo */
  private String                 title;

  /** link a qualcosaltro */
  private String                 href;

  /** Titolo da mettere al link */
  private String                 titleHref     = null;

  /** Flag per dire se il campo è abilitato o meno */
  private boolean                abilitato;

  /** Flag che dice se il campo deve essere visibile o meno */
  private boolean                visibile;

  /** Flag che dice se il campo e' obbligatorio o meno */
  private boolean                obbligatorio;

  /** Flag che dice se si tratta di un campo chiave */
  private boolean                chiave;

  /**
   * Tipo di campo che può assumere i seguenti valori:<br/> <b>D</b> Data
   * <li><b>Fnn.dd</b> Decimale
   * <li><b>Nnn</b> Numerico
   * <li><b>Tnn</b> Stringa
   * <li><b>E[tipo]</b> Campo select (elenco di valori) mentre [tipo] e una
   * definizione successiva di tipo (escludendo E)
   */
  private String                 tipo;

  /** Nome fisico del campo */
  private String                 nomeFisico;

  /** Flag che dice che siamo in situazione di sola visualizzazione */
  private boolean                visualizzazione;

  /** Gestore del campo * */
  protected AbstractGestoreCampo gestore;

  /** Javascript */
  private Javascript             js;

  /** Page context per eventuali immagini */
  private String                 pageContext;

  private boolean                active;

  /** Eventuale tooltip */
  protected String               tooltip;

  private ServletContext         servletContext;

  /** Flag che identifica il campo fittizio */
  private boolean                campoFittizio = false;

  /** Classe per l'edit box */
  private String                 classEdit     = null;

  /** Flag per settare di non scrivere gli input sulla form */
  private boolean                outInput      = true;

  /** Dominio del campo */
  private String                 dominio       = null;

  /** Il menù popup contiene funzionalità speciali? */
  private boolean                 speciale      = false;

  /** Flag indicante se il campo e' per una pagina di trova o meno */
  protected boolean isCampoInPaginaTrova;

  /** Eventuale id specifico da attribuire al campo. */
  private String id;

  /**
   * Inner class per la gestione delle voci presenti nella popup del campo
   */
  private class PopUpItem {

    /** Etichetta da utilizzare nella voce della popup */
    private String titolo;

    /** Link associato all'elemento */
    private String href;

    public PopUpItem(String titolo, String href) {
      this.titolo = titolo;
      this.href = href;
    }

    public String getTitolo() {
      return titolo;
    }

    public String getHref() {
      return href;
    }

    @Override
    public boolean equals(Object obj) {
      boolean esito = false;
      if (obj instanceof PopUpItem) {
        PopUpItem item = (PopUpItem) obj;
        if (item.titolo == null && this.titolo == null)
          esito = true;
        else if (item.titolo != null && this.titolo != null)
          esito = item.titolo.equals(this.titolo);
      }
      return esito;
    }

  }

  /** Elenco delle opzioni di popup per il campo */
  private Vector popupItems;

  private Vector valori;

  private int    len;

  public CampoDecoratorImpl() {
    this(false);
  }

  public CampoDecoratorImpl(boolean isCampoInPaginaTrova) {
    this.valori = new Vector();
    this.nome = "CAMPO_GENERICO";
    this.tipo = "";
    this.formName = "localForm";
    this.value = null;
    this.originalValue = null;
    this.title = null;
    this.visualizzazione = false;
    this.gestore = null;
    this.abilitato = true;
    this.href = null;
    this.nomeFisico = null;
    this.visibile = true;
    this.obbligatorio = false;
    this.chiave = false;
    this.js = null;
    this.pageContext = null;
    this.active = true;
    this.popupItems = new Vector();
    this.isCampoInPaginaTrova = isCampoInPaginaTrova;
    this.id = null;
  }

  /**
   * Funzione che aggiunge un valore all'elenco
   *
   * @param valore
   *        Valore da impostare
   * @param descr
   *        Descrizione del valore impostato
   */
  public void addValore(String valore, String descr) {
    this.valori.add(new ValoreTabellato(valore, descr));
  }
  
  /**
   * Funzione che aggiunge un valore all'elenco
   * 
   * @param valore
   *        Valore
   * @param descr
   *        Descrizione
   * @param arc
   *        Valore archiviato
   */
  public void addValore(String valore, String descr, String arc) {
    this.valori.add(new ValoreTabellato(valore, descr, arc));
  }

  /**
   * @param dominio
   *        The dominio to set.
   */
  public void setDominio(String dominio, PageContext pageContext) {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 16/11/2006 M.F. Non imposta il dominio se un gestore è gia settato
    // ************************************************************
    this.dominio = dominio;
    if (this.getGestore() == null) {
      String nomeClasseGestore = this.getNomeGestoreDefaultFromDominioEFormato();
      if (nomeClasseGestore != null)
        this.setGestore(nomeClasseGestore, pageContext);
    }
  }

  /**
   * Factory Method per l'ottenimento della classe da istanziare a partire dal
   * dominio del campo ed eventuali altre informazioni se necessarie per
   * specializzare ulteriormente la ricerca
   *
   * @return nome della classe del gestore
   */
  private String getNomeGestoreDefaultFromDominioEFormato() {
    String nomeGestore = null;
    if ("SN".equalsIgnoreCase(this.dominio))
      nomeGestore = GestoreCampoSiNo.class.getName();
    else if ("NOTE".equalsIgnoreCase(this.dominio))
      nomeGestore = GestoreCampoNote.class.getName();
    else if ("CLOB".equalsIgnoreCase(this.dominio))
      nomeGestore = GestoreCampoCLOB.class.getName();
    else if ("MONEY".equalsIgnoreCase(this.dominio))
      nomeGestore = GestoreCampoMoney.class.getName();
    else if ("MONEY5".equalsIgnoreCase(this.dominio))
      nomeGestore = GestoreCampoMoney5Dec.class.getName();
    else if ("DATA_ELDA".equalsIgnoreCase(this.dominio))
      nomeGestore = GestoreCampoDataElda.class.getName();
    else if ("TIMESTAMP".equalsIgnoreCase(this.dominio))
      nomeGestore = GestoreCampoTimestamp.class.getName();
    else if ("PRC".equalsIgnoreCase(this.dominio)) {
      if (this.getTipo().startsWith("N"))
        nomeGestore = GestoreCampoPercentuale0Dec.class.getName();
      else if (this.getTipo().startsWith("F") && this.getTipo().endsWith("2"))
        nomeGestore = GestoreCampoPercentuale2Dec.class.getName();
      else if (this.getTipo().startsWith("F") && this.getTipo().endsWith("5"))
        nomeGestore = GestoreCampoPercentuale5Dec.class.getName();
      else if (this.getTipo().startsWith("F") && this.getTipo().endsWith("9"))
        nomeGestore = GestoreCampoPercentuale9Dec.class.getName();
    } else if (this.dominio == null && this.getTipo().startsWith("F")) {
      nomeGestore = GestoreCampoDecimale.class.getName();
    }

    return nomeGestore;
  }

  /**
   * @return Returns the nome.
   */
  public String getNome() {
    return nome;
  }

  /**
   * @param nome
   *        The nome to set.
   */
  public void setNome(String nome) {
    this.nome = nome;
  }

  /**
   * @return Returns the tipo.
   */
  public String getTipo() {
    return tipo;
  }

  /**
   * Funzione che ricava la grandezza dal tipo di dato
   *
   * @param tipo
   *        Tipo di campo
   * @return
   */
  private int getLenDaTipo(String tipo) {
    int len = 80;
    switch (tipo.charAt(0)) {
    case JdbcParametro.TIPO_DATA:
      len = 10;
      break;
    case JdbcParametro.TIPO_DECIMALE:
      len = 20;
      try {
        Double dato = Double.valueOf(tipo.substring(1));
        len = dato.intValue();
        if ((dato.doubleValue() - Math.round(dato.doubleValue())) > 0) {
          len++;
          len += new Double(
              (dato.doubleValue() - Math.round(dato.doubleValue())) * 10).intValue();
        }
      } catch (Throwable t) {

      }
      break;
    case JdbcParametro.TIPO_NUMERICO:
      len = 20;
      try {
        len = Integer.valueOf(tipo.substring(1)).intValue();
      } catch (Throwable t) {

      }
      break;
    case JdbcParametro.TIPO_TESTO:
      len = 80;
      try {
        len = Integer.valueOf(tipo.substring(1)).intValue();
      } catch (Throwable t) {

      }
      break;
    case JdbcParametro.TIPO_ENUMERATO:
      len = 80;
      break;
    }
    return len;
  }

  /**
   * Settaggio del tipo di campo. Imposta anche la dimensione del campo in
   * funzione del settaggio
   *
   * @param tipo
   *        Tipo di campo che può assumere i seguenti valori:<br/>
   *        <li><b>D</b> Data
   *        <li><b>Fnn.dd</b> Decimale
   *        <li><b>Nnn</b> Numerico
   *        <li><b>Tnn</b> Stringa
   *        <li><b>E</b> Campo select (elenco di valori)
   */
  public void setTipo(String tipo) {
    // Verifico se il tipo è un tipo valido
    // prima di settarlo
    if (new String("DFNTE").indexOf(tipo.charAt(0)) >= 0) {
      this.len = this.getLenDaTipo(tipo);
      this.tipo = tipo;
    }
  }

  /**
   * @return Returns the title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * @param title
   *        The title to set.
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Funzione che restituisce il nome ed il titolo da mettere
   *
   * @param keyPress
   *        Flag che dice di appendere l'evento di keypress
   * @return
   */
  protected String getDefaultHtml(boolean keyPress) {
    StringBuffer buf = new StringBuffer();
    if (this.getId() != null) {
      buf.append(UtilityTags.getHtmlAttrib("id", this.getId()));
    } else {
      buf.append(UtilityTags.getHtmlAttrib("id", this.getNome()));
    }
    buf.append(UtilityTags.getHtmlAttrib("name", this.getNome()));
    buf.append(UtilityTags.getHtmlAttrib("title", this.getTooltip()));
    // buf.append(UtilityTags.getHtmlAttrib("onchange", "javascript:"
    // + this.getFormName() + ".onChange(this);"));
    buf.append(UtilityTags.getHtmlAttrib("onchange", "javascript:callObjFn('"
        + this.getFormName()
        + "','onChange',this);"));
    // if (keyPress)
    // buf.append(UtilityTags.getHtmlAttrib("onkeypress",
    // "javascript:callObjFn('"
    // + this.getFormName()
    // + "','onChanging',this);"));
    if (this.gestore != null) {
      // Se è settata la classe di visualizzazione allora la setto
      // sull'edit
      String lsTmp = this.gestore.getClasseEdit();
      if (lsTmp != null) buf.append(UtilityTags.getHtmlAttrib("class", lsTmp));
    }
    if (this.classEdit != null && this.classEdit.length() > 0) {
      buf.append(UtilityTags.getHtmlAttrib("class", this.classEdit));
    }
    return buf.toString();
  }

  /**
   * Scrive il campo aggiungendo l'eventuale tipo alla form
   *
   * @param script
   * @return
   */
  public String toString(Javascript script) {
    this.setJs(script);

    if (!this.isVisualizzazione()) {

      // setto il dominio se esiste
      if (this.getDominio() != null)
        script.print(this.formName
            + ".setDominio(\""
            + this.getNome()
            + "\",\""
            + this.getDominio()
            + "\");");

      // Aggiungo al javascript il settaggio del tipo di campo
      script.println(this.formName
          + ".setTipo(\""
          + this.getNome()
          + "\",\""
          + this.getTipo()
          + "\");");
    }

    return this.localToString();
  }

  /**
   * Implementazione del to string in locale
   *
   * @return
   */
  private String localToString() {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 29/09/2006 M.F. Aggiunto il campo invisibile con la definizione del
    // campo
    // 13/10/2006 M.F. Aggiunta della gestione con gestore
    // 23/10/2006 M.F. La definizione del campo viene messa in qualunque
    // stato. Aggiunta dell'input invisibile in sola visualizzazione
    // 17/01/2007 M.F. Elimino la visualizzazione degli input se non voluto in
    // visualizzazione
    // 04/08/2009 S.S. Append del class solo se non è stato fatto in precedenza
    // (in questo modo si evita la duplicazione dell'attributo nell'html)
    // 10/06/2010 S.S. nel caso di ricaricamento della pagina in seguito ad un errore
    // viene popolato il nuovo campo originalValue in modo da ripristinare la definizione
    // così come era all'interazione precedente che ha causato l'errore (prima di questa
    // modifica la definizione riportava il value)
    // ************************************************************

    String lTmp = null;
    StringBuffer buf = new StringBuffer();

    // Aggiungo l'input nascosto con la definizione del campo
    if ((!this.isVisualizzazione() && this.isAbilitato()) || this.isOutInput()) {
      // SS 10/06/2010 corretta la gestione della definizione in fase di
      // ricaricamento pagina in seguito ad un errore in submit
      if (this.originalValue != null)
        buf.append(UtilityDefinizioneCampo.getHtmlDefinizioneCampoWithOriginalValue(this));
      else
        buf.append(UtilityDefinizioneCampo.getHtmlDefinizioneCampo(this));
    }

    // Inserisco l'edit solo se come visualizzazione
    if (!this.isVisualizzazione() && this.isAbilitato()) {

      // Se esiste un gestore provo a vedere se ha implementato
      // l'estrazione dell'HTML
      if (this.gestore != null) {
        lTmp = this.gestore.preHTML(false, this.isAbilitato());
        if (lTmp != null) {
          buf.append(lTmp);
        }
        lTmp = this.gestore.getHTML(false, this.isAbilitato());
        // Verifico se è stato ridefinito l'HTML
        if (lTmp != null) {
          buf.append(lTmp);
          lTmp = this.gestore.postHTML(false, this.isAbilitato());
          if (lTmp != null) buf.append(lTmp);
          this.addPopUpItems(buf);
          return buf.toString();
        }
      }
      if (this.isVisibile()) {
        String htmlDefault = null;
        switch (this.getTipo().charAt(0)) {
        case JdbcParametro.TIPO_DATA: // Data
          buf.append("<input ");
          htmlDefault = this.getDefaultHtml(true);
          buf.append(htmlDefault);
          buf.append(UtilityTags.getHtmlAttrib("type", "text"));
          buf.append(UtilityTags.getHtmlAttrib("size", "10"));
          buf.append(UtilityTags.getHtmlAttrib("value", this.getValue()));
          buf.append(UtilityTags.getHtmlAttrib("maxlength",
              String.valueOf(this.getLen())));
          // SS 20090804
          // l'attributo class va settato solo se non e' stato fatto in
          // precedenza, utilizzando classEdit del decorator (perche' e' un
          // campo archivio) o del gestore
          if (htmlDefault.indexOf("class=") == -1)
            buf.append(UtilityTags.getHtmlAttrib("class", "data testo"));
          if (!this.isAbilitato())
            buf.append(UtilityTags.getHtmlAttrib("readonly", "readOnly"));
          buf.append("/>");
          break;
        case JdbcParametro.TIPO_DECIMALE: // decimal
        case JdbcParametro.TIPO_NUMERICO: // intero
        case JdbcParametro.TIPO_TESTO: // Stringa
          buf.append("<input ");
          htmlDefault = this.getDefaultHtml(true);
          buf.append(htmlDefault);
          // SS 20090804
          // l'attributo class va settato solo se non e' stato fatto in
          // precedenza, utilizzando classEdit del decorator (perche' e' un
          // campo archivio) o del gestore
          if (htmlDefault.indexOf("class=") == -1) {
            if ("MONEY".equals(this.getDominio()) || "MONEY5".equals(this.getDominio()))
              buf.append(UtilityTags.getHtmlAttrib("class", "importo"));
            else if (this.getTipo().charAt(0) != JdbcParametro.TIPO_TESTO)
              buf.append(UtilityTags.getHtmlAttrib("class", "numero"));
            else
              buf.append(UtilityTags.getHtmlAttrib("class", "testo"));
          }
          buf.append(UtilityTags.getHtmlAttrib("type", "text"));
          buf.append(UtilityTags.getHtmlAttrib("size",
              String.valueOf(this.getLenForInput())));
          buf.append(UtilityTags.getHtmlAttrib("value", this.getValue()));
          buf.append(UtilityTags.getHtmlAttrib("maxlength",
              String.valueOf(this.getLen())));
          if (!this.isAbilitato())
            buf.append(UtilityTags.getHtmlAttrib("readonly", "yes"));
          buf.append("/>");
          break;
        case JdbcParametro.TIPO_ENUMERATO: // Campo Elenco valori
          buf.append("<select ");
          buf.append(this.getDefaultHtml(false));
          if (!this.isAbilitato())
            buf.append(UtilityTags.getHtmlAttrib("disabled", "yes"));
          buf.append("  >");
          // Scorro tutti i valori
          for (int i = 0; i < this.valori.size(); i++) {
            ValoreTabellato valoreTabellato = (ValoreTabellato) this.valori.get(i);
            if ("1".equals(valoreTabellato.getArc())) {
              if (this.getValue() != null && this.getValue().equals(valoreTabellato.getValore())) {
                buf.append(((ValoreTabellato) this.valori.get(i)).toString(this.getValue()));
              }
            } else {
              buf.append(((ValoreTabellato) this.valori.get(i)).toString(this.getValue()));
            }
          }
          buf.append("</select>");
        }
      } else {
        // Se non è visibile allora inserisco l'imput invisibile
        buf.append("<input ");
        buf.append(this.getDefaultHtml(true));
        buf.append(UtilityTags.getHtmlAttrib("type", "hidden"));
        buf.append(UtilityTags.getHtmlAttrib("value", this.getValue()));
        buf.append("/>");
      }

      lTmp = null;
      if (this.gestore != null) {
        lTmp = this.gestore.postHTML(false, this.isAbilitato());
        if (lTmp != null) buf.append(lTmp);
      }
      this.addPopUpItems(buf);

    } else {
      // {MF231006} Aggiungo sempre l'input invisibile per poter
      // utilizzarlo nei javascript
      // Se non è visibile allora inserisco l'imput invisibile
      if (this.isOutInput()) {
        // {MF170107} Scrivo l'imput solo se non è in visualizzazione
        buf.append("<input ");
        buf.append(this.getDefaultHtml(true));
        buf.append(UtilityTags.getHtmlAttrib("type", "hidden"));
        buf.append(UtilityTags.getHtmlAttrib("value", this.getValue()));
        buf.append("/>");
      }
      String classe = null;

      if (this.isVisibile()) {
        // Verifico se la visualizzazione deve essere all'interno di una
        // classe
        if (this.gestore != null) classe = this.gestore.getClasseVisua();
        if (this.href != null) {
          buf.append("<a ");
          buf.append(UtilityTags.getHtmlAttrib("href", this.getHref()));
          if (this.getTitleHref() != null)
            buf.append(UtilityTags.getHtmlAttrib("title", this.getTitleHref()));
          buf.append(">");
        }
        if (classe != null) {
          buf.append("<div ");
          buf.append(UtilityTags.getHtmlAttrib("class", classe));
          buf.append(">");
        }
        if (this.gestore != null) {
          lTmp = gestore.preHTML(true, this.isAbilitato());
          if (lTmp != null) buf.append(lTmp);
        }
        lTmp = null;
        if (this.gestore != null) {
          lTmp = gestore.getHTML(true, this.isAbilitato());
        }
        // {M.F. 22.11.2006} Aggiunta dell'id per il settaggio da javascript
        // della visualizzazione
        buf.append("<span ");
        if (this.isOutInput())
          buf.append(UtilityTags.getHtmlAttrib("id", (this.getId() != null ? this.getId() : this.getNome()) + "view"));
        // Appendo il tooltip se non è un link con il proprio tooltip
        if (this.href == null || this.titleHref == null)
          buf.append(UtilityTags.getHtmlAttrib("title", this.getTooltip()));
        // SS 30-09-2009: il class span.numero non esiste, per cui viene rimosso il codice
//        if (this.getTipo().charAt(0) == JdbcParametro.TIPO_NUMERICO
//            || this.getTipo().charAt(0) == JdbcParametro.TIPO_DECIMALE)
//          buf.append(UtilityTags.getHtmlAttrib("class", "numero"));

        buf.append(">");

        // Appendo il valore per la visualizzazione
        if (lTmp == null) {
          buf.append(this.getValuePerVisualizzazione());
        } else
          buf.append(lTmp);

        buf.append("</span>");
        if (this.gestore != null) {
          lTmp = gestore.postHTML(true, this.isAbilitato());
          if (lTmp != null) buf.append(lTmp);
        }
        if (classe != null) buf.append("</div>");
        if (this.href != null) buf.append("</a>");
        // Se è in modifica aggiungo il menu popUp
        if (!this.isVisualizzazione()) this.addPopUpItems(buf);
      }
    }
    return buf.toString();
  }

  private void addPopUpItems(StringBuffer buf) {
    // Se ci sono voci di menu le aggiungo al campo
    if (this.popupItems.size() > 0) {
      /* F.D. 28/02/08 aggiugo il passaggio del parametro "speciale" */
      PopUpGenericoImpl popup = new PopUpGenericoImpl("jsPopUp"
          + this.getNome(), this.getPageContext(), this.getJs());
      popup.setSpeciale(this.speciale);
      buf.append(popup.toString());
      for (int i = 0; i < popupItems.size(); i++) {
        PopUpItem item = (PopUpItem) popupItems.get(i);
        popup.addVoceJs(item.getHref(), item.getTitolo());
      }
    }
  }

  /**
   * Funzione che ricerca l'indice dei valori enumerati
   *
   * @param valore
   *        Valore da ricercare nell'enumerazione
   * @return
   */
  private int indexOf(String valore) {
    if (valore == null) valore = "";
    for (int i = 0; i < this.valori.size(); i++) {
      ValoreTabellato tbl = (ValoreTabellato) this.valori.get(i);
      if (valore.equals(tbl.getValore())) return i;
    }
    return -1;
  }

  /**
   * Riscrittura delle funzione to string
   */
  @Override
  public String toString() {
    return this.localToString();
  }

  public int getLenForInput() {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 16/11/2006 M.F. Aggiungo 1 carattere ogni 5 (perche la scifezza di
    // Internet Explorer non
    // riesce a calcolare la larghezza esatta, CASPITA !!!)
    // ************************************************************
    int len = this.len + (this.len / 5);
    if (len < 1) return 1;
    if (len > 80){
      if(this.isCampoInPaginaTrova)
        return 73;
      else
        return 80;
    }
    return len;
  }

  /**
   * @return Returns the formName.
   */
  public String getFormName() {
    return formName;
  }

  /**
   * @param formName
   *        The formName to set.
   */
  public void setFormName(String formName) {
    this.formName = formName;
  }

  /**
   * @return Returns the value.
   */
  public String getValue() {
    String ret = this.value;
    // Se c'è un gestore allora chiamo la modifica del valore
    if (gestore != null) {
      ret = gestore.getValore(ret);
      if (ret == null) ret = this.value;
    }
    if (ret == null) return "";
    return ret;
  }

  /**
   * Funzione che restituisce il valore da utilizzare nella visualizzazione
   *
   * @return Valore da visualizzate
   */
  public String getValuePerVisualizzazione() {
    // Se esiste un gestore allora richiamo la funzione che esegue la
    // vonversione
    // di visualizzazione
    if (this.gestore != null) {
      String ret = gestore.getValorePerVisualizzazione(this.getValue());
      if (ret != null) return ret;
    }
    // Il campo è in sola visualizzazione
    // Faccio il case in funzione del tipo di campo
    if (this.getTipo() != null && this.getTipo().length() > 0)
      switch (this.getTipo().charAt(0)) {
      case JdbcParametro.TIPO_ENUMERATO:
        int index = indexOf(this.getValue());
        if (index >= 0) {
          ValoreTabellato value = (ValoreTabellato) this.valori.get(index);
          return UtilityStringhe.convStringHTML(value.toString());
        } else
          return "";
      }
    return UtilityStringhe.convStringHTML(this.getValue());
  }

  /**
   * @param value
   *        The value to set.
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Funzione che restituisce il nome difico del campo ovvero TABELLA.CAMPO
   *
   * @return Returns the nomeFisico.
   */
  public String getNomeFisico() {
    return nomeFisico;
  }

  /**
   * @param nomeFisico
   *        The nomeFisico to set.
   */
  public void setNomeFisico(String nomeFisico) {
    this.nomeFisico = nomeFisico;
  }

  /**
   * @return Returns the visualizzazione.
   */
  public boolean isVisualizzazione() {
    return visualizzazione;
  }

  /**
   * Setta il campo in sola visualizzazioni se non è in visualizzazione si
   * intende in edit
   *
   * @param visualizzazione
   *        The visualizzazione to set.
   */
  public void setVisualizzazione(boolean visualizzazione) {
    this.visualizzazione = visualizzazione;
  }

  /**
   * Funzione che restituiisce il gestore del campo
   *
   * @return
   */
  public AbstractGestoreCampo getGestore() {
    return gestore;
  }

  /**
   * Crea e imposta il gestore di un campo
   *
   * @param nome
   *        del gestore
   * @param pageContext
   *        page context dell'applicativo
   */
  public void setGestore(String gestore, PageContext pageContext) {
    this.gestore = null;

    Object lGestore = UtilityTags.createObject(gestore);
    // Setto il gestore solo se è un gestore astratto di campo
    if (lGestore instanceof AbstractGestoreCampo) {
      this.gestore = (AbstractGestoreCampo) lGestore;
      // Setto il page context dell'oggetto
      this.gestore.setPageContext(pageContext);
      this.gestore.setCampo(this, this.getServletContext());
    }

  }

  /**
   * @return Estrazione dell'elenco dei valori
   */
  public Vector getValori() {
    return valori;
  }

  /**
   * @return Returns the abilitato.
   */
  public boolean isAbilitato() {
    return abilitato;
  }

  /**
   * @param abilitato
   *        The abilitato to set.
   */
  public void setAbilitato(boolean abilitato) {
    this.abilitato = abilitato;
  }

  /**
   * @return Returns the href.
   */
  public String getHref() {
    return href;
  }

  /**
   * @param href
   *        The href to set.
   */
  public void setHref(String href) {
    if (href != null && href.length() == 0) href = null;
    this.href = href;
  }

  /**
   * Funzione che setta il nome del campo
   *
   * @param campo
   */
  public void setCampo(String campo) {
    if (campo == null) campo = "";
    campo = campo.toUpperCase();

    String lsTmp = this.getNomeFisico();

    if (lsTmp == null || lsTmp.indexOf('.') <= 0)
      lsTmp = "";
    else
      lsTmp = lsTmp.substring(0, lsTmp.indexOf('.'));
    if (lsTmp.length() > 0 && !lsTmp.equals(".")) lsTmp += ".";
    lsTmp += campo;
    this.setNomeFisico(lsTmp);

  }

  /**
   * Funzione che setta il nome dell'entità
   *
   * @param entita
   */
  public void setEntita(String entita) {
    if (entita == null) entita = "";
    entita = entita.toUpperCase();
    String lsTmp = this.getNomeFisico();
    if (lsTmp == null) lsTmp = "";
    if (lsTmp.indexOf('.') >= 0)
      lsTmp = lsTmp.substring(lsTmp.indexOf('.') + 1);
    if (entita != null && entita.length() > 0) lsTmp = entita + "." + lsTmp;
    this.setNomeFisico(lsTmp);
  }

  /**
   * Funzione che estrae il nome del campo
   *
   * @return
   */
  public String getCampo() {
    try {
      if (this.getNomeFisico() != null) {
        String lsTmp = this.getNomeFisico().substring(
            this.getNomeFisico().indexOf('.') + 1);
        if (lsTmp != null && lsTmp.length() > 0) return lsTmp;
      }
    } catch (Throwable t) {

    }
    return null;
  }

  /**
   * Funzione che estrae il nome dell'entità
   *
   * @return
   */
  public String getEntita() {
    try {
      if (this.getNomeFisico() != null) {
        String lsTmp = this.getNomeFisico().substring(0,
            this.getNomeFisico().indexOf('.'));
        if (lsTmp != null && lsTmp.length() > 0) return lsTmp;
      }
    } catch (Throwable t) {

    }
    return null;
  }

  /**
   * @return Returns the visibile.
   */
  public boolean isVisibile() {
    return visibile;
  }

  /**
   * @param visibile
   *        The visibile to set.
   */
  public void setVisibile(boolean visibile) {
    this.visibile = visibile;
  }

  /**
   * @return Ritorna obbligatorio.
   */
  public boolean isObbligatorio() {
    return obbligatorio;
  }

  /**
   * @param obbligatorio obbligatorio da settare internamente alla classe.
   */
  public void setObbligatorio(boolean obbligatorio) {
    this.obbligatorio = obbligatorio;
  }

  /**
   * @return Returns the chiave.
   */
  public boolean isChiave() {
    return chiave;
  }

  /**
   * @param chiave
   *        The chiave to set.
   */
  public void setChiave(boolean chiave) {
    this.chiave = chiave;
  }

  public void addPopUp(String titolo, String href) {
    // Se non è attivo il campo allora non permetto l'inserimento di menu
    // popup
    // if(!this.isActive())
    // return;
    PopUpItem item = new PopUpItem(titolo, href);
    if (popupItems.contains(item)) return;
    popupItems.add(item);
  }

  /**
   * @return Returns the js.
   */
  public Javascript getJs() {
    return js;
  }

  /**
   * @param js
   *        The js to set.
   */
  public void setJs(Javascript js) {
    this.js = js;
  }

  /**
   * @return Returns the pageContext.
   */
  public String getPageContext() {
    return pageContext;
  }

  /**
   * @param pageContext
   *        The pageContext to set.
   */
  public void setPageContext(String pageContext) {
    this.pageContext = pageContext;
  }

  /**
   * @return Returns the active.
   */
  public boolean isActive() {
    return active;
  }

  /**
   * @param active
   *        The active to set.
   */
  public void setActive(boolean active) {
    this.active = active;
  }

  /**
   * @return Returns the titleHref.
   */
  public String getTitleHref() {
    return titleHref;
  }

  /**
   * @param titleHref
   *        The titleHref to set.
   */
  public void setTitleHref(String titleHref) {
    this.titleHref = titleHref;
  }

  /**
   * @return Returns the defaultValue.
   */
  public String getDefaultValue() {
    return defaultValue;
  }

  /**
   * @param defaultValue
   *        The defaultValue to set.
   */
  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  /**
   * @return Returns the servletContext.
   */
  public ServletContext getServletContext() {
    return servletContext;
  }

  /**
   * @param servletContext
   *        The servletContext to set.
   */
  public void setServletContext(ServletContext servletContext) {
    this.servletContext = servletContext;
  }

//  public String getId() {
//    return super.toString();
//  }

  /**
   * @return Returns the classEdit.
   */
  public String getClassEdit() {
    return classEdit;
  }

  /**
   * @param classEdit
   *        The classEdit to set.
   */
  public void setClassEdit(String classEdit) {
    this.classEdit = classEdit;
  }

  /**
   * @return Returns the tooltip.
   */
  public String getTooltip() {
    // Se non è settato il tooltip allora restituisco il titolo
    if (this.tooltip == null || this.tooltip.length() == 0) return this.title;
    return tooltip;
  }

  /**
   * @param tooltip
   *        The tooltip to set.
   */
  public void setTooltip(String tooltip) {
    this.tooltip = tooltip;
  }

  /**
   * @return Returns the campoFittizio.
   */
  public boolean isCampoFittizio() {
    return campoFittizio;
  }

  /**
   * @param campoFittizio
   *        The campoFittizio to set.
   */
  public void setCampoFittizio(boolean campoFittizio) {
    this.campoFittizio = campoFittizio;
  }

  public int getLen() {
    return len;
  }

  public void setLen(int len) {
    this.len = len;
  }

  public boolean isOutInput() {
    return outInput;
  }

  public void setOutInput(boolean outInput) {
    this.outInput = outInput;
  }

  public String getSchema() {
    Tabella tab = DizionarioTabelle.getInstance().getDaNomeTabella(
        this.getEntita());
    if (tab != null) return tab.getNomeSchema();
    return "";
  }

  /**
   * @return Ritorna dominio.
   */
  public String getDominio() {
    return dominio;
  }


  /**
   * @return Returns the speciale.
   */
  public boolean isSpeciale() {
    return speciale;
  }


  /**
   * @param speciale The speciale to set.
   */
  public void setSpeciale(boolean speciale) {
    this.speciale = speciale;
  }

  /**
   * @return Ritorna popupItems.
   */
  public Vector getPopupItems() {
    return popupItems;
  }

  /**
   * @return Ritorna originalValue.
   */
  public String getOriginalValue() {
    return originalValue;
  }

  /**
   * @param originalValue originalValue da settare internamente alla classe.
   */
  public void setOriginalValue(String originalValue) {
    this.originalValue = originalValue;
  }

  /**
   * @return Ritorna id.
   */
  public String getId() {
    return id;
  }

  /**
   * @param id id da settare internamente alla classe.
   */
  public void setId(String id) {
    this.id = id;
  }

}
