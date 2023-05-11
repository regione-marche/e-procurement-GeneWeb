package it.eldasoft.gene.tags.templates;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class JspTemplateGetStringTag extends TagSupport {

  /**
   * UID
   */
  private static final long serialVersionUID = -8015571313832950247L;

  private String            name;

  private String            defaultVal;

  public int doStartTag() throws JspException {
    super.doStartTag();
    JspTemplateTag template = JspTemplateTag.getLastTemplateTag(pageContext);
    if (template != null) {
      // Aggiungo la stringa all'elenco delle stringhe
      try {
        if (template.getStrings().get(this.getName()) != null) {
          String value = (String) template.getStrings().get(this.getName());
          pageContext.getOut().write(value);
        } else {
          pageContext.getOut().write(this.getDefaultVal());
        }
      } catch (Throwable t) {
        throw new JspException("Errore in getString: " + t.getMessage());
      }

    } else {
      throw new JspException(
          "Il tag getString deve essere all'interno di un templateTag");
    }
    return SKIP_BODY;
  }

  /**
   * @return Returns the defaultVal.
   */
  public String getDefaultVal() {
    return defaultVal;
  }

  /**
   * @param defaultVal
   *        The defaultVal to set.
   */
  public void setDefaultVal(String defaultVal) {
    this.defaultVal = defaultVal;
  }

  /**
   * @return Returns the name.
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *        The name to set.
   */
  public void setName(String name) {
    this.name = name;
  }

}
