/*
 * Created on 28/ott/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.displaytag;

import javax.servlet.jsp.PageContext;

import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;
import it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoMoney;

/**
 * Decoratore di campi importo visualizzato con il simbolo di euro e 2 decimali
 * 
 * @author Gianluigi.Passiatore
 * 
 * @since 1.3.3
 */
public class MoneyDecorator implements DisplaytagColumnDecorator {

  public Object decorate(Object valore, PageContext arg1, MediaTypeEnum arg2)
      throws DecoratorException {
    String result = null;
    GestoreCampoMoney Money = new GestoreCampoMoney();
    result = Money.getValorePerVisualizzazione(valore.toString());
    return result;
  }

}
