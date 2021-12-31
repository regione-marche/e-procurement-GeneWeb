/*
 * Created on 12-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.displaytag;

import it.eldasoft.gene.db.domain.genric.OrdinamentoRicerca;

import javax.servlet.jsp.PageContext;

import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;


/**
 * Decoratore di colonna per DiplayTag che converte il numero associato a 
 * ciascun tipo di ordinamento nella stringa corrispondente. In particolare:
 * - 0: 'Discendente'  
 * - 1: 'Ascendente'
 *  
 * @author Luca Giacomazzo
 */
public class OrdinamentoDecorator implements DisplaytagColumnDecorator {

  public Object decorate(Object arg0, PageContext arg1, MediaTypeEnum arg2) throws DecoratorException {
    int tipoOrdinamento = (new Integer((String)arg0)).intValue();
    String str = null;
    switch (tipoOrdinamento) {
      case OrdinamentoRicerca.ORDINAMENTO_ASCENDENTE:
        str = "Crescente";
        break;
      case OrdinamentoRicerca.ORDINAMENTO_DISCENDENTE:
        str = "Decrescente";
        break;
    }
    return str;
  }
}