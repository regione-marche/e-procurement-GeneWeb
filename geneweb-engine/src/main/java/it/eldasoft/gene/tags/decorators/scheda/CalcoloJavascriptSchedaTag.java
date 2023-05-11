package it.eldasoft.gene.tags.decorators.scheda;

import javax.servlet.jsp.JspException;

import it.eldasoft.gene.tags.TagSupportGene;
import it.eldasoft.gene.tags.decorators.wizard.FormSchedaWizardTag;
import it.eldasoft.gene.tags.js.Javascript;
import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Classe che gestisce l'aggiunta in una maschera di funzioni javascript
 * 
 * @author cit_franceschin
 */
public class CalcoloJavascriptSchedaTag extends TagSupportGene {

  /**
   * 
   */
  private static final long serialVersionUID = 8445831827534648715L;

  private String            funzione;

  private String            elencocampi;

  private boolean           esegui           = false;

  /**
   * Funzione che da il javascript per l'inserimento di un camlcolo in una form
   * 
   * @param formName
   *        Nome dell'oggetto form
   * @param funzione
   *        Funzione che calcolo
   * @param elencoCampiDiv
   *        Elenco di campi che fanno scattare il ricalcolo divisi da ;
   * @param esegui
   *        Flag che dice di eseguire la funzione alla costruzione
   * @return Javascript per l'aggiunta del calcolo
   */
  public static String toString(String formName, String funzione,
      String elencoCampiDiv, boolean esegui) {
    StringBuffer buf = new StringBuffer("");
    buf.append(formName);
    buf.append(".addCalcoloHtml(\"");
    buf.append(UtilityTags.convStringa(funzione));
    buf.append("\",\"");
    buf.append(UtilityTags.convStringa(elencoCampiDiv));
    buf.append("\",");
    buf.append(esegui ? "true" : "false");
    buf.append(");");
    return buf.toString();
  }

  public int doStartTag() throws JspException {
    super.doStartTag();
    // {MF071106} Aggiungo il javascript solo se siamo alla prima
    // interazione
    IFormScheda parent = (FormSchedaTag) getParent(FormSchedaTag.class);
    if (parent == null)
      parent = (FormSchedaWizardTag) getParent(FormSchedaWizardTag.class);
    if (parent == null)
      throw new JspException(
          "Il tag fnJavaScriptScheda per il calcolo javascript deve trovarsi all'interno di una scheda (formScheda o formSchedaWizard)");

    if (parent.isFirstIteration()) {
      Javascript js = UtilityTags.getJavascript(this.pageContext);
      if (js != null) {
        js.println(toString(parent.getFormName(), this.getFunzione(),
            this.getElencocampi(), this.isEsegui()));
      }
    }
    return SKIP_BODY;
  }

  /**
   * @return Returns the elencocampi.
   */
  public String getElencocampi() {
    return elencocampi;
  }

  /**
   * @param elencocampi
   *        The elencocampi to set.
   */
  public void setElencocampi(String elencocampi) {
    this.elencocampi = elencocampi;
  }

  /**
   * @return Returns the funzione.
   */
  public String getFunzione() {
    return funzione;
  }

  /**
   * @param funzione
   *        The funzione to set.
   */
  public void setFunzione(String funzione) {
    this.funzione = funzione;
  }

  /**
   * @return Returns the esegui.
   */
  public boolean isEsegui() {
    return esegui;
  }

  /**
   * @param esegui
   *        The esegui to set.
   */
  public void setEsegui(boolean esegui) {
    this.esegui = esegui;
  }

}
