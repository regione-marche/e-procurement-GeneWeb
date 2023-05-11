/*
 * Created on 14-feb-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;


/**
 * Classe che gestisce l'estrazione di un dato da un HashMap
 * @author marco.franceschin
 *
 */
public class GetValHashMapFunction extends AbstractFunzioneTag {

  public GetValHashMapFunction() {
    super(2, new Class[]{HashMap.class,Object.class});
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {
    if(params[0] instanceof HashMap){
      HashMap map=(HashMap)params[0];
      Object obj=map.get(params[1]);
      if(obj!=null)
        return obj.toString();
    }
    return "";
  }

}
