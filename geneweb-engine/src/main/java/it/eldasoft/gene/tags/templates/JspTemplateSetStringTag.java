package it.eldasoft.gene.tags.templates;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class JspTemplateSetStringTag extends TagSupport {

  /**
   * UID
   */
  private static final long serialVersionUID = 24406158095934253L;

  private String            name;
  private String            value;

  public int doStartTag() throws JspException {
    super.doStartTag();
    // Aggiungo la stringa all'elenco delle stringhe
    JspTemplateTag template = null;
    if (this.getParent() instanceof JspTemplateTag)
      template = (JspTemplateTag) this.getParent();
    else
      template = JspTemplateTag.getLastTemplateTag(this.pageContext);
    if (template!=null) {
      template.getStrings().put(this.getName(), this.getValue());
    } else {
      throw new JspException(
          "Il tag setString deve essere all'interno di un templateTag !");
    }
    // Eseguo il skyp del body
    return SKIP_BODY;
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

  /**
   * @return Returns the value.
   */
  public String getValue() {
    return value;
  }

  /**
   * @param value
   *        The value to set.
   */
  public void setValue(String value) {
    this.value = value;
  }

}
