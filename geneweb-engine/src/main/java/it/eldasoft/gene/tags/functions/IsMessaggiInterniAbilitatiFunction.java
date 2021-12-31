/*
 * Creato 22/01/2009
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

public class IsMessaggiInterniAbilitatiFunction extends AbstractFunzioneTag {

  private static final String PROP_MESSAGGI_INTERNI_ABILITATI = "messaggi.interni.abilitati";

  public IsMessaggiInterniAbilitatiFunction() {
    super(1, new Class[] { PageContext.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    String result = "false";
    String valore = ConfigManager.getValore(PROP_MESSAGGI_INTERNI_ABILITATI);
    if (valore != null && "1".equals(valore)) {
      result = "true";
    }

    return result;
  }
}