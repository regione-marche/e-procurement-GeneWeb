/*
 * Created on 17-ott-2007
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

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposer;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.sql.comp.SqlManager;

/**
 * Classe che realizza una funzione per ottenere l'uppercase utilizzato nel DBMS
 * in uso dall'applicativo
 * 
 * @author Stefano.Sabbadin
 */
public class GetUpperCaseDBFunction extends AbstractFunzioneTag {

  public GetUpperCaseDBFunction() {
    super(1, new Class[] { Object.class });
  }

  /**
   * @see it.eldasoft.gene.tags.utils.AbstractFunzioneTag#function(javax.servlet.jsp.PageContext,
   *      java.lang.Object[])
   */
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String funzione = null;
    // si ritorna la funzione determinata dall'SqlComposer
    try {
      SqlComposer composer = SqlManager.getComposer(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
      funzione = composer.getFunzioneUpperCase();
    } catch (SqlComposerException e) {
      throw new JspException(e);
    }
    return funzione;
  }

}
