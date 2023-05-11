/*
 * Created on 21/nov/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import java.util.Collections;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;

public class OrdinaKeyPerHistoryFunction extends AbstractFunzioneTag {

  public OrdinaKeyPerHistoryFunction() {
    super(1, new Class[] { String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String sezs[] = UtilityTags.stringToArray((String) params[0],
        UtilityTags.DEFAULT_CHAR_DIVISORE);
    if (sezs.length > 1) {
      Vector sort = new Vector();
      for (int i = 0; i < sezs.length; i++) {
        sort.add(sezs[i]);
      }
      Collections.sort(sort);
      StringBuffer buf = new StringBuffer("");
      for (int i = 0; i < sort.size(); i++) {
        if(i>0)buf.append(';');
        buf.append(sort.get(i));
      }
      return buf.toString();
    }
    return (String) params[0];
  }
}
