package it.eldasoft.gene.tags.templates;

import it.eldasoft.gene.tags.TagSupportGene;

import java.io.IOException;

import javax.servlet.jsp.JspException;

public class JspTemplateInsertTag extends TagSupportGene {

  //private static Logger     logger           = Logger.getLogger(JspTemplateInsertTag.class);

  /**
   * UID
   */
  private static final long serialVersionUID = 1210525318059097686L;

  private String            name;
  private String            src;

  public JspTemplateInsertTag() {
    super("JspTemplateInsert");
    name = null;
    src = null;
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
   * Inizio del tag
   */
  public int doStartTag() throws JspException {
    super.doStartTag();
    JspTemplateTag parent = JspTemplateTag.getLastTemplateTag(pageContext);
    if (parent != null) {
      // Verifico se è stato ridefinito il nome
      Object obj = parent.getBlocks().get(this.getName());
      if (obj != null && obj instanceof String) {
        // Non eseguo l'intero tag perche gia ridefinito
        try {
          // Scrivo il corpo precedentemente configurato
          this.pageContext.getOut().write((String) obj);
        } catch (IOException e) {
          throw new JspException(e.getMessage(), e);
        }
        //logger.debug(this.getName() + " non viene inserito perchè già definito");
        return SKIP_BODY;
      } else {
        // Includo l'eventuale parte sul file
        if (this.src != null && this.src.length() > 0) {
          //logger.debug(this.getName() + " includo il file: " + this.src);
          try {
            this.pageContext.include(this.src);
          } catch (Exception e) {
            throw new JspException(e.getMessage(), e);
          }
        }
        return EVAL_PAGE;

      }
    } else {
      // Non è inserito all'iterno di un template
      throw new JspException(
          "Attenzione il tag insert deve trovarsi all'interno di un tag template "
              + "e non all'interno di un tag: "
              + this.getParent());
    }
  }

  /**
   * @return Returns the pathFile.
   */
  public String getSrc() {
    return src;
  }

  /**
   * @param pathFile
   *        The pathFile to set.
   */
  public void setSrc(String pathFile) {
    this.src = pathFile;
  }
}
