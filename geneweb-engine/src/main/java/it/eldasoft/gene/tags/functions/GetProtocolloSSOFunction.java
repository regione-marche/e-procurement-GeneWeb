/*
 * Creato 18/02/2016
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
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che determina l'eventuale protocollo di autenticazione SSO
 *
 * 1 = Shibboleth
 * 2 = Cohesion
 * 3 = SSOBart
 *
 * @author Cristian.Febas
 */

public class GetProtocolloSSOFunction extends AbstractFunzioneTag {



  public GetProtocolloSSOFunction() {
    super(1, new Class[] { PageContext.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String confSSOAttiva = ConfigManager.getValore(CostantiGenerali.PROP_SSO_PROTOCOLLO);
    confSSOAttiva = UtilityStringhe.convertiNullInStringaVuota(confSSOAttiva);
    if("".equals(confSSOAttiva)){
      confSSOAttiva = "0";
    }
    return confSSOAttiva;
  }
}