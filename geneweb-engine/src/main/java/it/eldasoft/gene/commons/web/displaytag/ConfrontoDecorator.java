/*
 * Created on 13-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.displaytag;

import it.eldasoft.gene.db.domain.genric.FiltroRicerca;

import javax.servlet.jsp.PageContext;

import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;

/**
 * Decoratore di colonna per DiplayTag che converte il numero associato a 
 * ciascun tipo di confronto nella stringa corrispondente. In particolare:
 * - 0: 'Campo'
 * - 1: 'Valore'
 * - 2: 'Parametro'
 * @author Luca Giacomazzo
 */
public class ConfrontoDecorator implements DisplaytagColumnDecorator {

  public Object decorate(Object arg0, PageContext arg1, MediaTypeEnum arg2) throws DecoratorException {
    int tipoConfronto = (new Integer((String)arg0)).intValue();
    String str = null;
    switch (tipoConfronto) {
      case FiltroRicerca.TIPO_CONFRONTO_CAMPO:
        str = "Campo";
        break;
      case FiltroRicerca.TIPO_CONFRONTO_VALORE:
        str = "Valore";
        break;
      case FiltroRicerca.TIPO_CONFRONTO_PARAMETRO:
        str = "Parametro";
        break;
    }
    return str;
  }
}