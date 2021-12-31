/**
 * 
 */
package it.eldasoft.gene.commons.web.displaytag;

import javax.servlet.jsp.PageContext;

import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.exception.DecoratorException;
import org.displaytag.properties.MediaTypeEnum;


/**
 * Decoratore di colonna per DisplayTag che converte il carattere ' ' nel 
 * carattere HTML &nbsp; per evitare che, ad esempio, la stringa "casa     caso"
 * venga rappresentata nella pagina come "casa cosa" 
 * 
 * @author Luca.Giacomazzo
 */
public class SpaziHTMLDecorator implements DisplaytagColumnDecorator {

  /**
   * @param valore  
   * @param arg1
   * @param arg2
   * @return Ritorna la stringa convertendo gli spazi nel carattere HTML che 
   *         rappresenta lo spazio (&nbsp;). Se l'argomento valore e' di tipo 
   *         java.lang.String ed è diversa da null, ritorna la stringa convertita,
   *         altrimenti ritorna null  
   */
  public Object decorate(Object valore, PageContext arg1, MediaTypeEnum arg2) throws DecoratorException {
    String result = null;
    if(valore != null && valore instanceof String) {
      String valoreStr = (String) valore;
      result = valoreStr.replaceAll(" ", "&nbsp;");
    }
    return result;
  }
}
