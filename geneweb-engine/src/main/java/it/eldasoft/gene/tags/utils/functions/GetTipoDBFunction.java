/*
 * Created on 07/nov/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.utils.functions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.properties.ConfigManager;
/**
 * Funzione che restituisce il tipo di database (da utilizzare nelle pagine jsp)
 * 
 * @author Filippo Rossetto
 *
 */
public class GetTipoDBFunction extends AbstractFunzioneTag {

  public GetTipoDBFunction() {
    super(1, new Class[] { Object.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    return ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);
  }

}
