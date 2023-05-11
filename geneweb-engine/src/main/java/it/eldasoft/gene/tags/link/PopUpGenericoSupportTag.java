package it.eldasoft.gene.tags.link;

import it.eldasoft.gene.tags.TagSupportGene;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;

public class PopUpGenericoSupportTag extends TagSupportGene {

  /**
   * Serial number statico
   */
  private static final long serialVersionUID = 2223696676093884675L;
  /**
   * Variabili d'istanza
   */
  private String            titolo;
  private PopUpGenericoImpl impl;
  private String            variableJs;
  private String            onClick;
  //F.D. 06/03/08 attributo speciale per gene:PopUp
  private boolean           speciale;

  
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
   * Costruttore di default. Di default il nome dell'oggetto inizia con popUp
   * 
   */
  public PopUpGenericoSupportTag() {
    super("popUp");
    this.titolo = null;
    this.impl = null;
    this.onClick = null;
    this.variableJs = null;
    this.speciale = false;
  }

  public String getNomeVariabileJs() {
    if (this.variableJs != null) return this.variableJs;
    return PopUpGenericoImpl.getNomeVariabileJs(this.getId());
  }

  @Override
  public int doStartTag() throws JspException {
    super.doStartTag();
    impl = new PopUpGenericoImpl(this.getId(), this.getContextPath(),
        this.getJavascript());
    impl.setTitolo("Visualizza opzioni");
    if (this.variableJs != null) impl.setVariableJs(this.getVariableJs());
    if (this.onClick != null) impl.setOnClick(this.onClick);
    if (StringUtils.isNotBlank(this.titolo)) {
      impl.setTitolo(this.titolo);
    }
    //F.D. 06/03/08 setto l'attributo speciale
    impl.setSpeciale(this.speciale);
    try {
      this.pageContext.getOut().write(impl.toString());
    } catch (IOException e) {

    }
    return EVAL_PAGE;
  }

  /**
   * @param titolo
   *        The titolo to set.
   */
  public void setTitolo(String titolo) {
    this.titolo = titolo;
  }

  /**
   * @return Returns the impl.
   */
  public PopUpGenericoImpl getImpl() {
    return impl;
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

}
