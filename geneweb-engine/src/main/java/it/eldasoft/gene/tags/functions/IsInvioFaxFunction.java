/*
 * Created on 27/apr/2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityBool;


public class IsInvioFaxFunction extends AbstractFunzioneTag {
  
  public IsInvioFaxFunction() {
    super(1, new Class[] { PageContext.class});
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    
    String invioFax = ConfigManager.getValore(CostantiGenerali.PROP_FAX_ABILITATO);
    if ("1".equals(invioFax)){
      invioFax="true";
    }else{
      invioFax="false";
    }
    
    return invioFax;
  }

}
