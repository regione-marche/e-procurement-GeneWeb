package it.eldasoft.gene.tags.utils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class SetParameterSupportTag extends BodyTagSupport {

  /**
   * UID
   */
  private static final long serialVersionUID = -3622851685357745511L;

  private String            name;
  private Object            value;
  private String            scope            = null;

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


  public SetParameterSupportTag() {
    this.name = null;
    this.value = null;
  }
  
  /**
   * Funzione che calcola il numero dello scope
   * @return
   * @throws JspException
   */
  private int getScopeInt() throws JspException {
    if (scope == null) scope = "requestScope";
    if ("pageScope".equals(scope))
      return PageContext.PAGE_SCOPE;
    else if ("requestScope".equals(scope))
      return PageContext.REQUEST_SCOPE;
    else if ("sessionScope".equals(scope))
      return PageContext.SESSION_SCOPE;
    else if ("applicationScope".equals(scope))
      return PageContext.APPLICATION_SCOPE;
    else
      throw new JspException(
          "gene:set variabile scope inesistente: "
              + this.getScope()
              + "\nSi aspettava uno dei seguenti valori: pageScope, requestScope, sessionScope, applicationScope");
  }

  public int doStartTag() throws JspException {
    super.doStartTag();
    if (this.value != null) {
      pageContext.setAttribute(this.getName(), this.getValue(),
          getScopeInt());
      return SKIP_BODY;
    }
    return EVAL_BODY_BUFFERED;
  }

  public int doEndTag() throws JspException {
    BodyContent body = this.getBodyContent();
    if (body != null) {
      pageContext.setAttribute(this.getName(), body.getString().trim(),
          getScopeInt());
      body.clearBody();
    }
    return super.doEndTag();
  }

  
  /**
   * @return the scope
   */
  public String getScope() {
    return scope;
  }

  
  /**
   * @param scope the scope to set
   */
  public void setScope(String scope) {
    this.scope = scope;
  }

  
  /**
   * @return the value
   */
  public Object getValue() {
    return value;
  }

  
  /**
   * @param value the value to set
   */
  public void setValue(Object value) {
    this.value = value;
  }

}
