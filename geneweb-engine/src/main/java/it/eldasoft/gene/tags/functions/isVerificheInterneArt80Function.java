/*
 * Created on 19-02-2020
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
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;

/**
 * Funzione per controllare l'abilitazione alle verifiche interne Art.80
 *
 * @author Cristian Febas
 */
public class isVerificheInterneArt80Function extends AbstractFunzioneTag {

  public isVerificheInterneArt80Function() {
    super(1, new Class[] { PageContext.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String isVerificheInterneArt80 = "0";
    //condizione necessaria: uffint abilitati
    HttpSession session = this.getRequest().getSession();
    if ( session != null) {
      String ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
      ufficioIntestatario = UtilityStringhe.convertiNullInStringaVuota(ufficioIntestatario);
      if(!"".equals(ufficioIntestatario)){
        if(GeneManager.checkOP(pageContext.getServletContext(), "OP133")){
          isVerificheInterneArt80 = "1";
       }
      }
    }

    return isVerificheInterneArt80;

  }

}