/*
 * Created on 12/12/12
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
import it.eldasoft.utils.spring.UtilitySpring;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che legge il tabellato G_045 per stabilire se si deve fare il controllo di
 * obbligatorietà della partita iva, se è presente il valore
 * 1 si salta il controllo di obbligatorieta
 *
 * @author Marcello Caminiti
 */
public class saltareControlloObbligPivaFunction extends AbstractFunzioneTag {

  public saltareControlloObbligPivaFunction() {
    super(2, new Class[] { PageContext.class,String.class });
  }


  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        pageContext, TabellatiManager.class);


    //String codiceTabellato = "G_045";
    String codiceTabellato = "G_045";
    String ret = "false";
    String valoreTabellato=(String) params[1];

    String descTabellato = tabellatiManager.getDescrTabellato(codiceTabellato, valoreTabellato);

    if (descTabellato != null && descTabellato.length()>1)
      descTabellato = descTabellato.substring(0, 1);

    if(descTabellato!=null && "1".equals(descTabellato)){
      ret = "true";
    }


    return ret;

  }

}
