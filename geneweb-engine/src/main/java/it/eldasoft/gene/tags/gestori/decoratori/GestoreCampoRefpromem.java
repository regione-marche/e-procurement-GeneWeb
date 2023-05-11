/*
 * Created on 21/05/13
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.gestori.decoratori;

import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoTabellato;

import javax.servlet.jsp.PageContext;

/**
 * Gestore per popolare il campo refpromem
 * con i valori della tabella G_REFPROMSCADENZ
 *
 * @author Marcello Caminiti
 */
public class GestoreCampoRefpromem extends AbstractGestoreCampoTabellato {

  public GestoreCampoRefpromem() {
    super(false, "T30");
  }

   @Override
  public SqlSelect getSql() {

    String moduloAttivo = (String) this.getPageContext().getAttribute("moduloAttivo",
        PageContext.SESSION_SCOPE);

    String entita = (String) this.getPageContext().getAttribute("entitaPartenza",
        PageContext.PAGE_SCOPE);

    String discriminante = (String) this.getPageContext().getAttribute("discriminante",
        PageContext.PAGE_SCOPE);

    String sql="select cod, tit from g_refpromscadenz where prg=? and ent=? and discr=?";
    return new SqlSelect(sql,new Object[]{moduloAttivo,entita,discriminante});

  }

}