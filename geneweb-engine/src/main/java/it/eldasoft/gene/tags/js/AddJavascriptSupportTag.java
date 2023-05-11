package it.eldasoft.gene.tags.js;

import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.TagAttributes;
import it.eldasoft.gene.tags.decorators.lista.FormListaTag;
import it.eldasoft.gene.tags.decorators.scheda.FormSchedaTag;
import it.eldasoft.gene.tags.decorators.wizard.FormSchedaWizardTag;
import it.eldasoft.gene.tags.utils.UtilityTags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class AddJavascriptSupportTag extends BodyTagSupportGene {

  /**
   * UID
   */
  private static final long serialVersionUID = -5658418179223393267L;

  /**
   * Inizializzo bufferizzando il corpo
   */
  public int doStartTag() throws JspException {
    super.doStartTag();

    if (isCheck()) {
      FormListaTag lista = (FormListaTag) getParent(FormListaTag.class);
      if (lista != null && !lista.isFirstIteration()) return SKIP_BODY;
      FormSchedaTag scheda = (FormSchedaTag) getParent(FormSchedaTag.class);
      if (scheda != null && !scheda.isFirstIteration()) return SKIP_BODY;
      FormSchedaWizardTag schedaWizard = (FormSchedaWizardTag) getParent(FormSchedaWizardTag.class);
      if (schedaWizard != null && !schedaWizard.isFirstIteration()) return SKIP_BODY;
    }
    return BodyTagSupport.EVAL_BODY_BUFFERED;
  }

  /**
   * Ridirezione del body nell'oggetto javascript
   */
  public int doEndTag() throws JspException {
    // Scrivo il body nel javascript
    BodyContent body = getBodyContent();
    if (body != null) {
      Javascript js = UtilityTags.getJavascript(this.pageContext);
      if (js != null) {
        js.println(body.getString().trim());
      }

      body.clearBody();
    }
    super.doEndTag();
    return BodyTagSupport.EVAL_PAGE;
  }

  public boolean isCheck() {

    if (TagAttributes.getInstance(this).get("check") instanceof Boolean)
      return ((Boolean) TagAttributes.getInstance(this).get("check")).booleanValue();
    return true;
  }

  public void setCheck(boolean check) {
    TagAttributes.getInstance(this).put("check", new Boolean(check));
  }

}
