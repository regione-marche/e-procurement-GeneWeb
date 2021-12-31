/*
 * Created on 15/nov/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.utils;

import java.util.Stack;

import javax.servlet.jsp.PageContext;


public class SqlSelectAttributes extends Stack {

  private static final String REQUEST_STACK = "REQ_SQL_SELECT_STACK";
  /**
   * 
   */
  private static final long serialVersionUID = 7133892302922874573L;
  
  static SqlSelectAttributes getInstance(PageContext pageContext){
    if(pageContext.getAttribute(REQUEST_STACK) instanceof SqlSelectAttributes)
      return (SqlSelectAttributes)pageContext.getAttribute(REQUEST_STACK);
    SqlSelectAttributes ret=new SqlSelectAttributes();
    pageContext.setAttribute(REQUEST_STACK, ret);
    return ret;
  }


}
