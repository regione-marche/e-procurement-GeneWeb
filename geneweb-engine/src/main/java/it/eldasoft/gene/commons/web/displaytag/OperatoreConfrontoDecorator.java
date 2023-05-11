/*
 * Created on 14-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.displaytag;

import it.eldasoft.gene.web.struts.genric.filtro.FiltroRicercaForm;
import it.eldasoft.utils.sql.comp.SqlElementoCondizione;

import javax.servlet.jsp.PageContext;

import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;

/**
 * Decoratore di colonna per DiplayTag che converte il numero associato a 
 * ciascun operatore di confronto nella stringa corrispondente, secondo quanto
 * le costanti definite nel file it.eldasoft.utils.sql.comp.SqlElementoCondizione.
 * 
 * @author Luca Giacomazzo
 */
public class OperatoreConfrontoDecorator implements DisplaytagColumnDecorator {

  public Object decorate(Object arg0, PageContext arg1, MediaTypeEnum arg2) throws DecoratorException {
    int tipoOperatore = (new Integer((String)arg0)).intValue();
    String str = null;
    switch (tipoOperatore) {
      case FiltroRicercaForm.OPERATORE_PARENTESI_APERTA:
        str = SqlElementoCondizione.STR_OPERATORE_PARENTESI_APERTA;
        break;
      case FiltroRicercaForm.OPERATORE_PARENTESI_CHIUSA:
        str = SqlElementoCondizione.STR_OPERATORE_PARENTESI_CHIUSA;
        break;
      case FiltroRicercaForm.OPERATORE_LOGICO_AND:
        str = SqlElementoCondizione.STR_OPERATORE_LOGICO_AND;
        break;
      case FiltroRicercaForm.OPERATORE_LOGICO_OR:
        str = SqlElementoCondizione.STR_OPERATORE_LOGICO_OR;
        break;
      case FiltroRicercaForm.OPERATORE_LOGICO_NOT:
        str = SqlElementoCondizione.STR_OPERATORE_LOGICO_NOT;
        break;
      case FiltroRicercaForm.OPERATORE_CONFRONTO_UGUALE:
        str = SqlElementoCondizione.STR_OPERATORE_CONFRONTO_UGUALE;
        break;
      case FiltroRicercaForm.OPERATORE_CONFRONTO_DIVERSO:
        str = SqlElementoCondizione.STR_OPERATORE_CONFRONTO_DIVERSO;
        break;
      case FiltroRicercaForm.OPERATORE_CONFRONTO_MAGGIORE:
        str = SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MAGGIORE;
        break;
      case FiltroRicercaForm.OPERATORE_CONFRONTO_MAGGIORE_UGUALE:
        str = SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MAGGIORE_UGUALE;
        break;
      case FiltroRicercaForm.OPERATORE_CONFRONTO_MINORE:
        str = SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MINORE;
        break;
      case FiltroRicercaForm.OPERATORE_CONFRONTO_MINORE_UGUALE:
        str = SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MINORE_UGUALE;
        break;
      case FiltroRicercaForm.OPERATORE_CONFRONTO_NULL:
        str = SqlElementoCondizione.STR_OPERATORE_CONFRONTO_NULL;
        break;
      case FiltroRicercaForm.OPERATORE_CONFRONTO_NOT_NULL:
        str = SqlElementoCondizione.STR_OPERATORE_CONFRONTO_NOT_NULL;
        break;
      case FiltroRicercaForm.OPERATORE_CONFRONTO_MATCH:
        str = SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MATCH;
        break;
      case FiltroRicercaForm.OPERATORE_CONFRONTO_NOT_MATCH:
        str = SqlElementoCondizione.STR_OPERATORE_CONFRONTO_NOT_MATCH;
        break;
    }
    return str;
  }
}