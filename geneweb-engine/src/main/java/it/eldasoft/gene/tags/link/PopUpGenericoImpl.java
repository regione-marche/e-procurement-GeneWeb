package it.eldasoft.gene.tags.link;

import it.eldasoft.gene.tags.js.Javascript;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.eldasoft.web.tags.ITagCostanti;

public class PopUpGenericoImpl {

  /**
   * Variabili d'istanza
   */
  private String            titolo;

  private String            id;

  private String            contextPath;

  private Javascript        js;

  private String            onClick;

  private String            variableJs;

  private int               numeroVoci;
  
  /* F.D. 28/02/08 popup con menù speciale */
  private boolean           speciale;

  /**
   * Costruttore di default. Di default il nome dell'oggetto inizia con popUp
   * 
   */
  public PopUpGenericoImpl(String id, String contextPath, Javascript js) {
    this.titolo = null;
    this.id = id;
    this.contextPath = contextPath;
    this.js = js;
    this.onClick = null;
    this.variableJs = null;
    this.numeroVoci = 0;
    this.speciale = false;
  }

  /**
   * Funzione che restituisce il nome della variabile Javascript utilizata
   * 
   * @return
   */
  public String getNomeVariabileJs() {
    if (this.variableJs == null) {
      return getNomeVariabileJs(this.getId());

    }
    return this.variableJs;

  }

  /**
   * Funzione che restituisce il nome della variabile Javascript utilizata
   * partendo da un identificativo
   * 
   * @param id
   * @return
   */
  public static String getNomeVariabileJs(String id) {
    return ITagCostanti.PREFISSO_VARIABILE_JS + UtilityStringhe.capitalize(id);
  }

  /**
   * Conversione a stringa
   */
  public String toString() {
    StringBuffer buffer = new StringBuffer("");
    buffer.append("&nbsp;");
    buffer.append("<A id=\"").append(this.getId());
    buffer.append("\" ");
    buffer.append("href=\"javascript:");
    if (onClick != null) buffer.append(UtilityTags.convStringa(onClick) + ";");
    buffer.append("showMenuPopup('");
    buffer.append(this.getId());
    buffer.append("',");
    buffer.append(this.getNomeVariabileJs());
    buffer.append(");\" ");
    // Aggiungo l'eliminazione del tab order solo se le icone sono disabilite
    if(UtilityTags.isIconeDisabilitate())
      buffer.append(UtilityTags.getHtmlAttrib("tabindex", "-1"));
    buffer.append("><IMG src=\"");
    if (this.getContextPath() != null)
      buffer.append(this.getContextPath()).append("/");
   //F.D. 28/02/08 se è un menù speciale differenzio le icone!
    if (!this.speciale)
      buffer.append("img/opzioni_info.gif\" ");
    else
      buffer.append("img/opzioni_speciali.gif\" ");
    buffer.append(UtilityTags.convStringa(this.titolo));
    buffer.append("title=\"");
    buffer.append(UtilityTags.convStringa(this.titolo));
    //if (!this.speciale)
      buffer.append("\" alt=\"\" height=\"16\" width=\"16\"></A>");
    //else
    //  buffer.append("\" alt=\"\" height=\"16\" width=\"28\"></A>");
    // Chiamo l'eventuale inizializzazione del PopUp
    PopUpItemSupportTag.initPopUp(js, this.getVariableJs(),
        this.getContextPath());
    return buffer.toString();
  }

  /**
   * Funzione che esegue l'inserimento di una nuova voce di menu che richiama un
   * javascript
   * 
   * @param functionJS
   * @param descrizione
   */
  public void addVoceJs(String functionJS, String descrizione) {
    if (js != null) {
      // Aggiungo la voce di menu
      PopUpItemSupportTag.addVoceJs(js, this.getNomeVariabileJs(), functionJS,
          descrizione, this.getContextPath());
      numeroVoci++;
    }
  }

  /**
   * @return Returns the id.
   */
  public String getId() {
    return id;
  }

  /**
   * @return Returns the contextPath.
   */
  public String getContextPath() {
    return contextPath;
  }

  /**
   * @param contextPath
   *        The contextPath to set.
   */
  public void setContextPath(String contextPath) {
    this.contextPath = contextPath;
  }

  /**
   * @return Returns the titolo.
   */
  public String getTitolo() {
    return titolo;
  }

  /**
   * @param titolo
   *        The titolo to set.
   */
  public void setTitolo(String titolo) {
    this.titolo = titolo;
  }

  /**
   * @return Returns the onClick.
   */
  public String getOnClick() {
    return onClick;
  }

  /**
   * @param onClick
   *        The onClick to set.
   */
  public void setOnClick(String onClick) {
    this.onClick = onClick;
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
   * Funzione che restituisce il numero voci inserite
   * 
   * @return
   */
  public int getNumeroVoci() {
    return numeroVoci;
  }

  /**
   * @param id
   *        The id to set.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @param js
   *        The js to set.
   */
  public void setJs(Javascript js) {
    this.js = js;
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

}
