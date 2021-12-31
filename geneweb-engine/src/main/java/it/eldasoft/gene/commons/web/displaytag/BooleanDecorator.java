/*
 * Created on 24-lug-2006
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
 * Decoratore di colonna per DisplayTag che converte in 'Si' e 'No'
 * rispettivamente i valori 'true' e 'false' di un oggetto di tipo boolean
 * 
 * @author Luca Giacomazzo
 */
public class BooleanDecorator implements DisplaytagColumnDecorator {

  public Object decorate(Object booleano, PageContext arg1, MediaTypeEnum arg2)
      throws DecoratorException {
    String result = null;
    if(booleano != null){
    Boolean test = (Boolean) booleano;
      if (test.booleanValue())
        result = "Si";
      else
        result = "No";
    } else {
      result = "";
    }
    return result;
  }

}