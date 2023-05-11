/*
 * Created on 30/mag/2019
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.utils.LogEventiUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;


public class TracciaLogEventiFunction  extends AbstractFunzioneTag{

  public TracciaLogEventiFunction() {
    super(4, new Class[] { PageContext.class, String.class,String.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String esito="ok";

    String codiceEvento = (String) params[1];
    String oggettoEvento = (String) params[2];
    String descrizione = (String) params[3];
    
    if(codiceEvento != null && codiceEvento.length()>0 && oggettoEvento != null && oggettoEvento.length()>0 && descrizione != null && descrizione.length()>0){
    
    LogEvento logEvento = LogEventiUtils.createLogEvento((HttpServletRequest) pageContext.getRequest());
    logEvento.setLivEvento(1);
    logEvento.setOggEvento(oggettoEvento);
    logEvento.setCodEvento(codiceEvento);
    logEvento.setDescr(descrizione);
    logEvento.setErrmsg("");
    LogEventiUtils.insertLogEventi(logEvento);
    
    }else{
      return "false";
    }
    
    return esito;
  }
  
}
