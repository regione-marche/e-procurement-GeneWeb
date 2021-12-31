package it.eldasoft.gene.tags.js;

import it.eldasoft.gene.tags.utils.UtilityTags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Classe che esegue l'inserimento dei javascript aggiunnti dai vari tag. Questo
 * javascript viene attaccato al punto in cui viene inserito
 * 
 * @author marco.franceschin
 * 
 */
public class JavascriptSupportTag extends TagSupport {

  /**
   * UID
   */
  private static final long serialVersionUID = 3917053784613327939L;

  public int doStartTag() throws JspException {
    super.doStartTag();
    // Se c'è nel request allora inserisco il javascript
    Javascript js = (Javascript) this.pageContext.getAttribute(
        UtilityTags.REQUEST_JAVASCRIPT, PageContext.REQUEST_SCOPE);
    if (js != null) {
      try {
        this.pageContext.getOut().write(js.toString());
      } catch (IOException e) {
        throw new JspException(e.getMessage(), e);
      }
      // Elimino l'oggetto perché non deve essere ripetuto ai prossimi tag
      this.pageContext.removeAttribute(UtilityTags.REQUEST_JAVASCRIPT,
          PageContext.REQUEST_SCOPE);
    }
    return SKIP_BODY;
  }

}
