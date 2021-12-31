/*
 * Created on Nov 10, 2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.utils.functions;

import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;

public class GetHashMapItemFunction extends AbstractFunzioneTag {

  public GetHashMapItemFunction() {
    super(2, new Class[] { HashMap.class, Object.class });
  }

  public String function(PageContext pageContext, Object params[])
      throws JspException {

    HashMap map = (HashMap) params[0];
    if (map != null) {
      Object ret = map.get(params[1]);
      if (ret != null) return ret.toString();
    }
    return "";
  }

}
