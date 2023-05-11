package it.eldasoft.gene.tags.link;

import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;

import it.eldasoft.gene.tags.TagSupportGene;
import it.eldasoft.gene.tags.functions.ResourceFunction;
import it.eldasoft.gene.tags.js.Javascript;
import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Classe che implementa un tag per una voce di menu
 * 
 * @author marco.franceschin
 * 
 */
public class PopUpItemSupportTag extends TagSupportGene {

  /**
   * UID
   */
  private static final long serialVersionUID = 7466653869956658612L;

  /*
   * Variabili del popp item
   */
  private String            variableJs;

  private String            title;

  private String            href;

  /** Eventuale resource da sove etrarre il menu */
  private String            resource;

  /** Parametri da pasare al resource */
  private String            parametri;

  /**
   * Funzione che sbianca l'elemanto
   */
  private void setNull() {
    this.variableJs = null;
    this.title = null;
    this.href = null;
    this.resource = null;
    this.parametri = null;
  }

  public PopUpItemSupportTag() {
    super("itemPopUp");
    this.setNull();
  }

  /**
   * Funzione privata che aggiunge il menu
   * 
   */
  private void addMenu() {
    if (this.getResource() == null) {
      PopUpItemSupportTag.addVoceJs(this.getJavascript(), this.getVariableJs(),
          this.getHref(), this.getTitle(), this.getContextPath());
    } else {
      // Si deve estrarre il menu dal resource
      String params[];
      if (this.getParametri() != null)
        params = UtilityTags.stringToArray(this.getParametri(), ';');
      else
        params = new String[] {};
      String resource = ResourceFunction.get(this.getResource(), params);
      String title = "", function = "";
      int pos = UtilityTags.indexOf(resource, ';', '\\', 0);
      if (pos > 1) {
        title = resource.substring(0, pos);
        function = resource.substring(pos + 1);
      } else
        title = resource;
      if (this.getTitle() != null) title = this.getTitle();
      if (this.getHref() != null) function = this.getHref();
      PopUpItemSupportTag.addVoceJs(this.getJavascript(), this.getVariableJs(),
          function, title, this.getContextPath());
    }
  }

  /**
   * Implementazione del tag
   */
  public int doStartTag() throws JspException {

    PopUpGenericoSupportTag parent = (PopUpGenericoSupportTag) getParent(PopUpGenericoSupportTag.class);
    if (parent != null && this.getVariableJs() == null) {
      this.setVariableJs(parent.getNomeVariabileJs());
      addMenu();
    } else {
      if (this.getVariableJs() == null)
        throw new JspException(
            "PopUpItem: deve avere il nome della variabile o essere contenuto in un PopUp !");
      addMenu();
    }
    this.setVariableJs(null);
    return super.doStartTag();
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
    this.href = UtilityTags.convertHREF(href, this.pageContext.getRequest());
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
   * @return Returns the variableJs.
   */
  public String getVariableJs() {
    return variableJs;
  }

  /**
   * @param variableJs
   *        The variableJs to set.
   */
  public void setVariableJs(String variableJs) {
    this.variableJs = variableJs;
  }

  /**
   * Inizializzo un popup
   * 
   * @param js
   *        Javascript
   * @param varName
   *        Nome della variabile
   * @param contextPath
   *        ContextPath
   */
  public static void initPopUp(Javascript js, String varName, String contextPath) {
    // Aggiungo lo sript per la visualizzazione del menu
    if (js != null) {
      if (js.addVarToJS(varName)) {
        // Inserisco la variavile e i valori di default
        js.print(varName);
        // Aggiungo la visualizzazione del chiudi
        js.print("+=creaVocePopUpChiusura(\"");
        if (contextPath != null) 
          js.print(UtilityTags.convStringa(contextPath+ "/"));
        js.println("\");");
      }
    }
  }

  /**
   * Funzione che esegue l'inserimento di una nuova voce di menu che richiama un
   * javascript
   * 
   * @param js
   *        Javascript
   * @param functionJS
   *        Funzione da riferire al meno
   * @param descrizione
   *        Descrizione
   */
  public static void addVoceJs(Javascript js, String nomeVar,
      String functionJS, String descrizione, String contextPath) {
    if (js != null) {
      if (!js.existVar(nomeVar)) {
        PopUpItemSupportTag.initPopUp(js, nomeVar, contextPath);
      }
      // Aggiungo la voce solo se esiste la definizione della variabile
      if (js.existVar(nomeVar)) {
        if (functionJS == null) functionJS = "";
        StringBuffer buffer = new StringBuffer("");
        buffer.append(nomeVar);
        buffer.append("+=creaPopUpSubmenu(\"javascript:");
        // Rimozione dalla funzione Javascript eventuali caratteri di line feed
        // e carriage return, i quali, se non rimossi, generano un errore JS in
        // fase di creazione della pagina HTML: praticamente l'istruzione JS
        // risulta non terminata dal carattere ';', in quanto nella stringa che
        // rappresenta la funzione JS sono presenti i caratteri '\n' e/o '\r'.
        // Si e' deciso di rimuovere tali caratteri, sostituendoli con uno spazio
        // per non dover inserire tanti caratteri '\'.
        StringTokenizer tokenizer = new StringTokenizer(functionJS, "\n\r", false); 
        int tokenCount = tokenizer.countTokens();
        if (tokenCount == 1)
          buffer.append(functionJS);
        else {
          StringBuffer result = new StringBuffer(functionJS.length());
          while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.length() > 0)
              result.append(token.concat(" "));
          }
          buffer.append(UtilityTags.convStringa(result.toString().trim()));
        }
        buffer.append(";hideMenuPopup();\",0,\"&nbsp;");
        buffer.append(UtilityTags.convStringa(descrizione));
        buffer.append("\"");
        if (functionJS.length() > 0)
          buffer.append(");");
        else
          buffer.append(",false);");
        
        js.println(buffer.toString());
      }
    }
  }

  /**
   * @return Returns the parametri.
   */
  public String getParametri() {
    return parametri;
  }

  /**
   * @param parametri
   *        The parametri to set.
   */
  public void setParametri(String parametri) {
    this.parametri = parametri;
  }

  /**
   * @return Returns the resource.
   */
  public String getResource() {
    return resource;
  }

  /**
   * @param resource
   *        The resource to set.
   */
  public void setResource(String resource) {
    this.resource = resource;
  }

}
