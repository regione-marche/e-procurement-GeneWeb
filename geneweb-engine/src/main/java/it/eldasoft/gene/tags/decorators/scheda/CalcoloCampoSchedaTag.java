package it.eldasoft.gene.tags.decorators.scheda;

import javax.servlet.jsp.JspException;

import it.eldasoft.gene.tags.TagSupportGene;
import it.eldasoft.gene.tags.decorators.wizard.FormSchedaWizardTag;
import it.eldasoft.gene.tags.js.Javascript;
import it.eldasoft.gene.tags.utils.UtilityTags;

public class CalcoloCampoSchedaTag extends TagSupportGene {

  /**
   * 
   */
  private static final long serialVersionUID = 8445831827534648715L;

  private String            funzione;

  private String            elencocampi;

  /**
   * Funzione che da il javascript per l'inserimento di un camlcolo in una form
   * 
   * @param formName
   *        Nome dell'oggetto form
   * @param nomeCampo
   *        Nome del campo
   * @param funzione
   *        Funzione che calcolo
   * @param elencoCampiDiv
   *        Elenco di campi che fanno scattare il ricalcolo divisi da ;
   * @return Javascript per l'aggiunta del calcolo
   */
  public static String toString(String formName, String nomeCampo,
      String funzione, String elencoCampiDiv) {
    StringBuffer buf = new StringBuffer("");
    buf.append(formName);
    buf.append(".addCalcolo(\"");
    buf.append(UtilityTags.convStringa(nomeCampo));
    buf.append("\",\"");
    buf.append(UtilityTags.convStringa(funzione));
    buf.append("\", new Array(");
    String elencoCampi[] = UtilityTags.stringToArray(elencoCampiDiv, ';');
    for (int i = 0; i < elencoCampi.length; i++) {
      if (i > 0) buf.append(", ");
      buf.append("\"");
      buf.append(elencoCampi[i]);
      buf.append("\"");
    }
    buf.append("));");
    return buf.toString();
  }

  public int doStartTag() throws JspException {
    super.doStartTag();
    CampoSchedaTag campo = (CampoSchedaTag) getParent(CampoSchedaTag.class);
    if (campo == null)
      throw new JspException(
          "Il controllo su un campo deve essere all'interno di un campo di una scheda !");
    // {MF071106} Aggiungo il javascript solo se siamo alla prima
    // interazione
    IFormScheda parent = (FormSchedaTag) getParent(FormSchedaTag.class);
    if (parent == null)
      parent = (FormSchedaWizardTag) getParent(FormSchedaWizardTag.class);
    if (parent == null)
      throw new JspException(
          "Il tag calcoloCampoScheda deve trovarsi all'interno di un tag campoScheda in una scheda (formScheda o formSchedaWizard)");
    if (!parent.isFirstIteration()) {
      Javascript js = UtilityTags.getJavascript(this.pageContext);
      if (js != null) {
        js.println(toString(campo.getFormName(), campo.getNome(),
            this.getFunzione(), this.getElencocampi()));
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

}
