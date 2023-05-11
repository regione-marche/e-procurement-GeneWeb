/*
 * Created on 24/05/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.properties.ConfigManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;


public class IsSsoProtocolloAbilitatoFunction extends AbstractFunzioneTag {

  public IsSsoProtocolloAbilitatoFunction() {
    super(1, new Class[] { PageContext.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String ssoProtocollo = ConfigManager.getValore(CostantiGenerali.PROP_SSO_PROTOCOLLO);
    if (!"".equals(ssoProtocollo) && !"0".equals(ssoProtocollo)){
      ssoProtocollo="true";
    }else{
      ssoProtocollo="false";
    }

    return ssoProtocollo;
  }

}
