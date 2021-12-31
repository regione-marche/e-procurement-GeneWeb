/*
 * Created on 4-lug-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.displaytag;

import it.eldasoft.utils.utility.UtilityDate;

import java.util.Date;

import javax.servlet.jsp.PageContext;

import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;

/**
 * Decoratore di colonna per DisplayTag che converte una data in GG/MM/AAAA
 * 
 * @author Stefano Sabbadin
 */
public class DataDecorator implements DisplaytagColumnDecorator {

  public Object decorate(Object data, PageContext arg1, MediaTypeEnum arg2)
      throws DecoratorException {
    Date dataTipizzata = (Date) data;
    String risultato = UtilityDate.convertiData(dataTipizzata,
        UtilityDate.FORMATO_GG_MM_AAAA);
    return (risultato == null ? "" : risultato);
  }

}
