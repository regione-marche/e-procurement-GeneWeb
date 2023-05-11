/*
 * Created on 01-set-2006
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

/**
 * Decoratore di colonna per DiplayTag che converte in 'Si' e 'No'
 * rispettivamente i valori '1' e '0' di un oggetto di tipo intero
 * 
 * @author marco.franceschin
 */
public class IntBooleanDecorator implements DisplaytagColumnDecorator {

  public Object decorate(Object integer, PageContext arg1, MediaTypeEnum arg2)
      throws DecoratorException {


    String result = "No";
    Integer test = (Integer) integer;
    if (test != null && test.intValue() == 1)
        result = "Si";
    
    return result;
  }
  

}
