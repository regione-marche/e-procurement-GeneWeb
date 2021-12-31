/*
 * Created on 11/04/12 
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che legge il tabellato G_012 per stabilire i controlli sulla correttezza e
 * unicita per codice fiscale e partita i.v.a
 *  
 * @author Marcello Caminiti
 */
public class EsisteControlloCorrettezzaFunction extends AbstractFunzioneTag {

  public EsisteControlloCorrettezzaFunction() {
    super(3, new Class[] { PageContext.class, String.class,String.class});
  }
  
  
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        pageContext, TabellatiManager.class);
        
    String codiceTabellato = "G_012";
    String entita = (String) params[1];
    String campo = (String) params[2];
    String ret = "false";
    String valoreTabellato="1";
    
    String descTabellato = tabellatiManager.getDescrTabellato(codiceTabellato, valoreTabellato);
      
    if (descTabellato != null && descTabellato.length()>1)
      descTabellato = descTabellato.substring(0, 1);
    
    if(descTabellato!=null && "1".equals(descTabellato) ){
      boolean campoVisibile = UtilityTags.checkProtection(pageContext, "COLS.VIS.GENE." + entita + "." + campo,true);
      boolean campoModificabile = UtilityTags.checkProtection(pageContext, "COLS.MOD.GENE." + entita + "." + campo,true);
      
      if(campoVisibile && campoModificabile){
        ret = "true";
      }
      
    }
    

    return ret;

  }

}
