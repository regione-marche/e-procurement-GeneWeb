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
import it.eldasoft.utils.properties.ConfigManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che legge il valore di una property specificata come parametro di ingresso
 *
 * @author Marcello Caminiti
 */
public class GetPropertyWsdmFunction extends AbstractFunzioneTag {

  /**
   * Costruttore
   */
  public GetPropertyWsdmFunction() {
    super(2, new Class[] { String.class, String.class });
  }


  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String property = ((String) params[0]);
    String idconfi = ((String) params[1]);
    String valoreProperty;
    if(idconfi != null && !"".equals(idconfi)){
      valoreProperty = ConfigManager.getValore(property + "." + idconfi);
    }else{
      valoreProperty = ConfigManager.getValore(property);
    }

    return valoreProperty;
  }
}