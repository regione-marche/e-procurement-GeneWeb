package it.eldasoft.gene.tags.templates;

import it.eldasoft.gene.tags.BodyTagSupportGene;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.log4j.Logger;

public class JspTemplateBlockTag extends BodyTagSupportGene {

  public static Logger      logger           = Logger.getLogger(JspTemplateBlockTag.class);

  /**
   * UID
   */
  private static final long serialVersionUID = 4676300827426695706L;

  private String            name;
  
  private JspTemplateTag templateParent=null;

  public JspTemplateBlockTag() {
  }

  /**
   * 
   */
  public int doStartTag() throws JspException {
    super.doStartTag();
    templateParent=(JspTemplateTag)getParent(JspTemplateTag.class);
    if(templateParent==null)
      templateParent = JspTemplateTag.getLastTemplateTag(pageContext);
    if ( templateParent == null)
      throw new JspException(
          "Attenzione il tag JspTemplateBlock non è all'interno di un templateTag");
    
    return EVAL_BODY_BUFFERED;
  }

  public int doEndTag() throws JspException {

    // Use the body of the tag as URL to redirect to
    BodyContent body = getBodyContent();
    String s = "";
    // Se c'è il corpo lo setto
    if (body != null) {
      // logger.error("Errore il body è vuoto !");
      s = body.getString().trim();
      // Clear the body since we only used it as input for the URL
      // value
      body.clearBody();
    }
    // Imposto il corpo interpretato
    templateParent.getBlocks().put(this.getName(), s);
    return super.doEndTag();
  }

  public int doAfterBody() throws JspException {

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

}
