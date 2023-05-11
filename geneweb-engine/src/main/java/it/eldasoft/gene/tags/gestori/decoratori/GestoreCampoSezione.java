/*
 * Created on 14-mar-2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.gestori.decoratori;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato;

import javax.servlet.jsp.PageContext;

/**
 * Gestore per le sezioni delle configurazioni di W_CONFIG
 *
 */
public class GestoreCampoSezione extends AbstractGestoreCampoTabellato {

  public GestoreCampoSezione() {
    super(false, "T500");
  }

  @Override
  public SqlSelect getSql() {
    String codapp = (String)this.getPageContext().getAttribute(CostantiGenerali.MODULO_ATTIVO, PageContext.SESSION_SCOPE);
    return new SqlSelect("select distinct sezione, sezione as sezione_descrizione from w_config where sezione is not null and codapp = ? order by sezione", new Object[] {codapp});
  }

}
