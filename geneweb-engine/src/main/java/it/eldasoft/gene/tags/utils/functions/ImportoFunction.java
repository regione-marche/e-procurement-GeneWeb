/*
 * Created on 16-nov-2006
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

import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoMoney;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;

public class ImportoFunction extends AbstractFunzioneTag {

  public ImportoFunction() {
    // super(1, new Class[]{Double.class});
    super(1, new Class[] { Object.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    if (params[0] != null){
      if (params[0] instanceof Double) {
        Double val = (Double) params[0];
        return new GestoreCampoMoney().getValorePerVisualizzazione(val.toString());
      }
      if (params[0] instanceof Integer) {
        Integer val = (Integer) params[0];
        return new GestoreCampoMoney().getValorePerVisualizzazione(val.toString());
      }
      if (params[0] instanceof Long) {
        Long val = (Long) params[0];
        return new GestoreCampoMoney().getValorePerVisualizzazione(val.toString());
      }
      if (params[0] instanceof String && ((String)params[0]).length() > 0) {
        String s = (String)params[0];
        Double val = new Double(s);
        return new GestoreCampoMoney().getValorePerVisualizzazione(val.toString());
      } else 
        return "";
    }
    return "";
  }

}
