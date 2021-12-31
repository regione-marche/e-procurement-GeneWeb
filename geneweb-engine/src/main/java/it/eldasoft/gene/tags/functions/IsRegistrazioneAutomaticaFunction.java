/*
 * Creato 18/03/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;
/**
 * Funzione che controlla se la registrazione automatica
 * per stabilire il flag abilitazione se spedire la mail
 * di ricevuta
 *
 * @author Cristian Febas
 */

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.properties.ConfigManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class IsRegistrazioneAutomaticaFunction extends AbstractFunzioneTag {



  public IsRegistrazioneAutomaticaFunction() {
    super(1, new Class[] { PageContext.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {

    String result = "false";
    String valore = ConfigManager.getValore(CostantiGenerali.PROP_REGISTRAZIONE_AUTOMATICA);
    if (valore != null && "1".equals(valore)) {
      result = "true";
    }

    return result;
  }
}