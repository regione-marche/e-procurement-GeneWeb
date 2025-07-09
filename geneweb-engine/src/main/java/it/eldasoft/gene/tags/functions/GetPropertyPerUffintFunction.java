/*
 * Created on 11-03-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.utils.ConfigurazionePerUffintUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che legge il valore di una property specificata come parametro di ingresso
 *
 * @author Manuel Bridda
 */
public class GetPropertyPerUffintFunction extends AbstractFunzioneTag {

  /**
   * Costruttore
   */
  public GetPropertyPerUffintFunction() {
    super(4, new Class[] { String.class });
  }


  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String property = ((String) params[0]);
    String codein = ((String) params[1]);
    String separatore = ((String) params[2]);
    String propertyBaseSeAssente = ((String) params[3]);
    if("true".equals(propertyBaseSeAssente))
      return ConfigurazionePerUffintUtils.getOptConfigurazionePerUfficioIntestatario(property,codein,separatore);
    else
      return ConfigurazionePerUffintUtils.getConfigurazionePerUfficioIntestatario(property,codein,separatore);

  }
}