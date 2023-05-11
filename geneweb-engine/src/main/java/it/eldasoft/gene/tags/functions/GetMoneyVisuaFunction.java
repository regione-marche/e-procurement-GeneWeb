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
package it.eldasoft.gene.tags.functions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoMoney;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;

public class GetMoneyVisuaFunction extends AbstractFunzioneTag {

  public GetMoneyVisuaFunction() {
    super(1, new Class[] { Object.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    return new GestoreCampoMoney().getValorePerVisualizzazione(params[0] == null
        ? ""
        : params[0].toString());
  }

}
