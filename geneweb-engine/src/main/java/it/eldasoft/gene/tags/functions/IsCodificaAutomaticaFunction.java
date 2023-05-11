/*
 * Creato 22/01/2009
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

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che determina se per l'entita specificata in ingresso, per il campo
 * chiave e' stata attivata la Codifica codici archivi 
 *  
 * @author Luca.Giacomazzo
 */
public class IsCodificaAutomaticaFunction extends AbstractFunzioneTag {

  public IsCodificaAutomaticaFunction(){
    super(3, new Class[]{PageContext.class, String.class, String.class,  });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    
    String result = null;
    if(pageContext != null){
      GeneManager gene = (GeneManager) UtilitySpring.getBean("geneManager",
          pageContext, GeneManager.class);

      String entita = (String) params[1];
      String campoChiave = (String) params[2];

      if(gene.isCodificaAutomatica(entita, campoChiave))
        result = "true";
      else 
        result = "false";
    } else {
      throw new JspException("Il primo parametro della funzione e' nullo",
          new NullPointerException("pageContext nullo"));
    }
    
    return result;
  }

}