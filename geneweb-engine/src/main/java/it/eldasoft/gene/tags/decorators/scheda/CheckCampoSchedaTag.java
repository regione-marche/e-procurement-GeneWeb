package it.eldasoft.gene.tags.decorators.scheda;

import javax.servlet.jsp.JspException;

import it.eldasoft.gene.tags.TagSupportGene;
import it.eldasoft.gene.tags.decorators.wizard.FormSchedaWizardTag;
import it.eldasoft.gene.tags.js.Javascript;
import it.eldasoft.gene.tags.utils.UtilityTags;

public class CheckCampoSchedaTag extends TagSupportGene {

  /**   UID   */
  private static final long serialVersionUID = 8445831827534648715L;

  private String            funzione;

  private String            messaggio;

  // Flag per indicare se il controllo sul campo e' bloccante o meno 
  private boolean           obbligatorio;
  // Flag per indicare se il controllo sul campo deve essere effettuato
  // all'onsubmit del form o all'onchange del campo stesso. Non e' un attributo
  // obbligatorio ed ha quindi un valore di default
  private boolean           onsubmit = true;

  public static String toString(String formName, String nomeCampo,
      String funzione, String messaggio, boolean obbligatorio,
      boolean onSubmit) {
    StringBuffer buf = new StringBuffer("");
    buf.append(formName);
    buf.append(".addCheck(\"");
    buf.append(UtilityTags.convStringa(nomeCampo));
    buf.append("\",\"");
    buf.append(UtilityTags.convStringa(funzione));
    buf.append("\", \"");
    buf.append(UtilityTags.convStringa(messaggio));
    buf.append("\",");
    buf.append(obbligatorio ? "true" : "false");
    buf.append(",");
    if(obbligatorio)
      buf.append(onSubmit ? "true" : "false");
    else
      buf.append("false");
    buf.append(");");
    return buf.toString();
  }

  public int doStartTag() throws JspException {
    super.doStartTag();
    CampoSchedaTag campo = (CampoSchedaTag) getParent(CampoSchedaTag.class);
    if (campo == null)
      throw new JspException(
          "Il controllo su un campo deve essere all'interno di un campo di una scheda !");
    // {MF071106} Aggiungo il javascript solo se siamo alla prima interazione
    // si prova prima con form scheda e poi con form scheda wizard
    IFormScheda parent = (FormSchedaTag) getParent(FormSchedaTag.class);
    if (parent == null)
      parent = (FormSchedaWizardTag) getParent(FormSchedaWizardTag.class);
    if (parent == null)
      throw new JspException(
          "Il tag checkCampoScheda deve trovarsi all'interno di un tag " +
          "campoScheda in una scheda (formScheda o formSchedaWizard)");
    if (!parent.isFirstIteration()) {
      Javascript js = UtilityTags.getJavascript(this.pageContext);
      if (js != null) {
        js.println(toString(campo.getFormName(), campo.getNome(),
            this.getFunzione(), this.getMessaggio(), this.isObbligatorio(),
            this.isOnsubmit()));
      }
    }
    return SKIP_BODY;
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
   * @return Returns the messaggio.
   */
  public String getMessaggio() {
    return messaggio;
  }

  /**
   * @param messaggio
   *        The messaggio to set.
   */
  public void setMessaggio(String messaggio) {
    this.messaggio = messaggio;
  }

  /**
   * @return Returns the obbligatorio.
   */
  public boolean isObbligatorio() {
    return obbligatorio;
  }

  /**
   * @param obbligatorio
   *        The obbligatorio to set.
   */
  public void setObbligatorio(boolean obbligatorio) {
    this.obbligatorio = obbligatorio;
  }
  
  /**
   * @return Ritorna onsubmit.
   */
  public boolean isOnsubmit() {
    return onsubmit;
  }
  
  /**
   * @param onsubmit onsubmit da settare internamente alla classe.
   */
  public void setOnsubmit(boolean onsubmit) {
    this.onsubmit = onsubmit;
  }
  
}