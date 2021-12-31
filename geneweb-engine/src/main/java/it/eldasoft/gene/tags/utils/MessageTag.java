/*
 * Created on 12/ott/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.utils;

import it.eldasoft.gene.tags.TagSupportGene;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;

import javax.servlet.jsp.JspException;

public class MessageTag extends TagSupportGene {

  /**
   * Identificativo seriale
   */
  private static final long serialVersionUID = -2342427700344494019L;
  private String            tipo             = null;
  private String            idMsg            = null;
  private String            params           = null;

  public MessageTag() {
    super("messageTag");
  }

  public int doStartTag() throws JspException {

    super.doStartTag();

    UtilityStruts.addMessage(this.getPageContext().getRequest(),
        this.getTipo(), this.getIdMsg(), params != null && params.length() > 0
            ? UtilityTags.stringToArray(this.getParams(), ';')
            : null);
    return SKIP_BODY;
  }

  /**
   * @return the tipo
   */
  public String getTipo() {
    return tipo;
  }

  /**
   * @param tipo
   *        the tipo to set
   */
  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  /**
   * @return the params
   */
  public String getParams() {
    return params;
  }

  /**
   * @param params
   *        the params to set
   */
  public void setParams(String params) {
    this.params = params;
  }

  /**
   * @return the idMsg
   */
  public String getIdMsg() {
    return idMsg;
  }

  /**
   * @param idMsg
   *        the idMsg to set
   */
  public void setIdMsg(String idMsg) {
    this.idMsg = idMsg;
  }

}
