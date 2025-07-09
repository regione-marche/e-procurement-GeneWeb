/*
  * Created on: 24/07/2023
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Funzione per impostare una where da inserire in sessione nei parametri trovaAddWhere e trovaParameter
 * @author Manuel Bridda
 *
 */
public class ImpostazioneFiltroFunction extends AbstractFunzioneTag {

  public ImpostazioneFiltroFunction() {
    super(4, new Class[] {PageContext.class,String.class,String.class,String.class});
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String entita = (String) params[1];
    String where = (String) params[2];
    String parametri = (String) params[3];

    int numPopup = UtilityTags.getNumeroPopUp(pageContext);
    UtilityTags.createHashAttributeForSqlBuild(pageContext.getSession(), entita, numPopup);
    UtilityTags.putAttributeForSqlBuild(pageContext.getSession(), entita, numPopup, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putAttributeForSqlBuild(pageContext.getSession(), entita, numPopup, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, parametri);


    return null;
  }

}
