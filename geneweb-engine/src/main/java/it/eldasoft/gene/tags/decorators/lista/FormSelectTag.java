/*
 * Created on Nov 13, 2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.lista;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import it.eldasoft.gene.tags.BodyTagSupportGene;


/**
 * Tag che gestisce una select diretta su database
 * @author cit_franceschin
 *
 */
public class FormSelectTag extends BodyTagSupportGene {

  /**
   * 
   */
  private static final long serialVersionUID = 1803834673339566097L;
  public int doStartTag() throws JspException {
    
    super.doStartTag();
    return EVAL_BODY_BUFFERED;
  }
  public int doEndTag() throws JspException {
    FormListaTag lista=(FormListaTag)getParent(FormListaTag.class);
    if(lista.isFirstIteration()){
      // Se si tratta della prima iterazione allora setto l'SQL sulla lista
      BodyContent body = getBodyContent();
      if (body != null) {
        lista.setSql(body.getString().trim());
        body.clearBody();
      }
    }
    return super.doEndTag();
  }
}
