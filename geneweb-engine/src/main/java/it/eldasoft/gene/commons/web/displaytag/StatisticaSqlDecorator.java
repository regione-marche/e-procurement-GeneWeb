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

import javax.servlet.jsp.PageContext;

import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;

/**
 * Decoratore di colonna per DiplayTag che converte ogni funzione statistica Sql
 * nella descrizione in italiano
 * 
 * @author Stefano.Sabbadin
 */
public class StatisticaSqlDecorator implements DisplaytagColumnDecorator {

  public Object decorate(Object obj, PageContext arg1, MediaTypeEnum arg2)
      throws DecoratorException {
    String funzione = (String) obj;
    String risultato = null;

    if ("MIN".equals(funzione))
      risultato = "Minimo";
    else if ("MAX".equals(funzione))
      risultato = "Massimo";
    else if ("AVG".equals(funzione))
      risultato = "Media";
    else if ("COUNT".equals(funzione))
      risultato = "Conta";
    else if ("SUM".equals(funzione)) risultato = "Somma";

    return risultato;
  }

}
