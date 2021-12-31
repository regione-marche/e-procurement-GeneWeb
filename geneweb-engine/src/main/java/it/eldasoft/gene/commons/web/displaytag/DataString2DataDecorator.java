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
 * 
 * @since 1.3.3
 */
public class DataString2DataDecorator implements DisplaytagColumnDecorator {

  public Object decorate(Object obj, PageContext arg1, MediaTypeEnum arg2)
      throws DecoratorException {
    String risultato = null;
    String dataStringa = (String) obj;
    Date data = null;
    if (dataStringa != null) {
      // si tentano varie conversioni
      if (data == null
          && UtilityDate.isDataInFormato(dataStringa,
              UtilityDate.FORMATO_GG_MM_AAAA))
        data = UtilityDate.convertiData(dataStringa,
            UtilityDate.FORMATO_GG_MM_AAAA);

      if (data == null
          && UtilityDate.isDataInFormato(dataStringa,
              UtilityDate.FORMATO_AAAAMMGG))
        data = UtilityDate.convertiData(dataStringa,
            UtilityDate.FORMATO_AAAAMMGG);

      if (data == null
          && UtilityDate.isDataInFormato(dataStringa,
              UtilityDate.FORMATO_GG_MM_AAAA_CON_TRATTINI))
        data = UtilityDate.convertiData(dataStringa,
            UtilityDate.FORMATO_GG_MM_AAAA_CON_TRATTINI);

      if (data == null
          && UtilityDate.isDataInFormato(dataStringa,
              UtilityDate.FORMATO_AAAA_MM_GG_CON_TRATTINI))
        data = UtilityDate.convertiData(dataStringa,
            UtilityDate.FORMATO_AAAA_MM_GG_CON_TRATTINI);

      // se la conversione è riuscita, si porta al formato italiano di
      // visualizzazione
      if (data != null)
        risultato = UtilityDate.convertiData(data,
            UtilityDate.FORMATO_GG_MM_AAAA);

    }
    return (risultato == null ? "" : risultato);
  }

}
