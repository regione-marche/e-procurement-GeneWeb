/*
 * Created on 21/nov/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che esegue il ricarico del profilo corrente
 * @author Marco.Franceschin
 *
 */
public class RicaricaProfiloFunction extends AbstractFunzioneTag {

  public RicaricaProfiloFunction() {
    super(1, new Class[]{PageContext.class});
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {
    GeneManager gene=(GeneManager)UtilitySpring.getBean("geneManager", pageContext,GeneManager.class);
    String profilo=(String) pageContext.getAttribute(CostantiGenerali.PROFILO_ATTIVO,
        PageContext.SESSION_SCOPE);
    if(gene.getProfili().getProfilo(profilo)!=null){
      gene.getProfili().remove(profilo);
    }
    else
      return "NEGATIVO";
    return "POSITIVO";
  }

}
