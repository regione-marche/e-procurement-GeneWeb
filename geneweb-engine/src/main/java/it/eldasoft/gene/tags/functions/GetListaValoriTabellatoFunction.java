/*
 * Created on 11/apr/2013
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
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Estrae l'elenco delle coppie codice descrizione di un tabellato e lo inserisce nel request con un attributo denominato come il parametro
 * in input.
 *
 * @author Stefano.Sabbadin
 */
public class GetListaValoriTabellatoFunction extends AbstractFunzioneTag {

  public GetListaValoriTabellatoFunction() {
    super(3, new Class[] {PageContext.class, String.class, String.class });
  }

  /**
   * @see it.eldasoft.gene.tags.utils.AbstractFunzioneTag#function(javax.servlet.jsp.PageContext, java.lang.Object[])
   */
  @Override
  public String function(PageContext pageContext, Object[] args) throws JspException {
    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager", pageContext, TabellatiManager.class);

    String codice = (String) args[1];
    String nomeAttributo = (String) args[2];

    List<Tabellato> lista = tabellatiManager.getTabellato(codice);

    pageContext.setAttribute(nomeAttributo, lista, PageContext.REQUEST_SCOPE);
    return null;
  }

}
