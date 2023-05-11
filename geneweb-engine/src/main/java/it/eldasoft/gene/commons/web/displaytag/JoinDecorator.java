/*
 * Created on 11-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.displaytag;

import it.eldasoft.gene.db.domain.genric.GiunzioneRicerca;

import javax.servlet.jsp.PageContext;

import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;

/**
 * Decoratore di colonna per DiplayTag che converte il valore numero associato a 
 * ciascun tipo di Join nella stringa corrispondente. In particolare:
 * - 0: 'normale <-->'  
 * - 1: 'sinistra <--'
 * - 2: 'destra -->'
 * @author Luca Giacomazzo
 */
public class JoinDecorator implements DisplaytagColumnDecorator {

  public Object decorate(Object arg0, PageContext arg1, MediaTypeEnum arg2) throws DecoratorException {
    int tipoJoin = (new Integer((String)arg0)).intValue();
    String str = null;
    switch (tipoJoin) {
      case GiunzioneRicerca.INNER_JOIN:
        str = "<-->";
        break;
      case GiunzioneRicerca.LEFT_OUTER_JOIN:
        str = "-->";
        break;
      case GiunzioneRicerca.RIGHT_OUTER_JOIN:
        str = "<--";
        break;
    }
    return str;
  }
}
