/*
 * Created on 10/04/12 
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
 * Funzione che legge il tabellato G_011 per stabilire i controlli di obbligatorietà
 * per codice fiscale e partita i.v.a
 *  
 * @author Marcello Caminiti
 */
public class EsisteControlloObbligatorietaCodFiscPivaFunction extends AbstractFunzioneTag {

  public EsisteControlloObbligatorietaCodFiscPivaFunction() {
    super(3, new Class[] { PageContext.class, String.class,String.class });
  }
  
  /**
   * Il vettore params contiene come secondo elemento l'entita da mappare nel valore del campo
   * tab1tip del tabellato G_011 come segue:
   * IMPR(Impresa) -> 2  
   * TECN(Tecnici) -> 3  
   * TECNI(Tecnici impresa) -> 4 
   * UFFINT(Uffici intestatari) -> 5 
   * 
   */
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        pageContext, TabellatiManager.class);
    
        
    String codiceTabellato = "G_011";
    String entita = (String) params[1];
    String campo = (String) params[2];
    String ret = "false";
    String valoreTabellato="";
    
    if("IMPR".equals(entita))
      valoreTabellato ="2";
    else if("TECNI".equals(entita))
      valoreTabellato ="3";
    else if("TEIM".equals(entita))
      valoreTabellato ="4";
    else if("UFFINT".equals(entita))
      valoreTabellato ="5";
    else if("UTENT".equals(entita))
      valoreTabellato ="6";
    
    String descTabellato = tabellatiManager.getDescrTabellato(codiceTabellato, valoreTabellato);
      
    if (descTabellato != null && descTabellato.length()>1)
      descTabellato = descTabellato.substring(0, 1);
    
    if(descTabellato!=null && "1;2;3".indexOf(descTabellato) >= 0){
      boolean campoVisibile = UtilityTags.checkProtection(pageContext, "COLS.VIS.GENE." + entita + "." + campo,true);
        
      boolean campoModificabile = UtilityTags.checkProtection(pageContext, "COLS.MOD.GENE." + entita + "." + campo,true);
        
      if(campoVisibile && campoModificabile){
        /*
        if(("CFIMP".equals(campo) || "CFTEC".equals(campo)|| "CFTIM".equals(campo) || "CFEIN".equals(campo))&& (descTabellato.equals("1") || descTabellato.equals("3")))
            ret = "true";
        else if(("PIVIMP".equals(campo) || "PIVATEC".equals(campo) || "PIVATEI".equals(campo) || "IVAEIN".equals(campo)) && (descTabellato.equals("2") || descTabellato.equals("3")))
            ret = "true";
        */
        if(descTabellato.equals("3") || (("CFIMP".equals(campo) || "CFTEC".equals(campo)|| "CFTIM".equals(campo) || "CFEIN".equals(campo) || "CFUTE".equals(campo)) && descTabellato.equals("1")) ||
            (("PIVIMP".equals(campo) || "PIVATEC".equals(campo) || "PIVATEI".equals(campo) || "IVAEIN".equals(campo) || "PIVAUTE".equals(campo)) && descTabellato.equals("2")))
          ret = "true";
      }
      
    }
    

    return ret;

  }

}
