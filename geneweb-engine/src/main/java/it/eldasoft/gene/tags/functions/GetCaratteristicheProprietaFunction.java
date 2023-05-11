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

import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.properties.ConfigManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che determina se la proprieta' indicata e' quella di database ed il
 * suo effettivo valore. Il valore protrebbe essere differente da quello
 * memorizzato in database (tabella W_CONFIG) perche' ha priorità il valore
 * definito nell'eventuale file di properties.
 * 
 */
public class GetCaratteristicheProprietaFunction extends AbstractFunzioneTag {

  public GetCaratteristicheProprietaFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  public String function(PageContext pageContext, Object[] params) throws JspException {

    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))) {

      String chiave = (String) params[1];

      try {

        pageContext.setAttribute("esisteProprieta", ConfigManager.esisteProprieta(chiave), PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("esisteProprietaDB", ConfigManager.esisteProprietaDB(chiave), PageContext.REQUEST_SCOPE);
        pageContext.setAttribute("valoreEffettivoProprieta", ConfigManager.getValore(chiave), PageContext.REQUEST_SCOPE);

      } catch (Exception e) {
        throw new JspException("Errore nella lettura della proprieta'", e);
      }
    }

    return null;

  }

}