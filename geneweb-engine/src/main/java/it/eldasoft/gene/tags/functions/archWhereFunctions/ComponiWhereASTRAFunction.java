package it.eldasoft.gene.tags.functions.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Funzione usata per creare la condizione where per l'archivio ASTRA.
 * 
 * @author Alvise Gorinati
 */
public class ComponiWhereASTRAFunction extends AbstractComponiWhereFunction {

  // Costruttori
  public ComponiWhereASTRAFunction() {
    super(Logger.getLogger(ComponiWhereASTRAFunction.class), "ASTRA");
  }

  // Metodi
  @Override
  protected void integrityChecks(PageContext pageContext) throws JspException {
    ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
    if (functionId.equals(SKIP_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else {
      throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
    }
  }

  @Override
  protected String composeWhere(PageContext pageContext, int popUpId) throws JspException {
    String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
    if (StringUtils.isNotEmpty(where)) {
      // TODO aggiungere qui le eventuali condizioni controllando il functionId
    } else {
      if (functionId.equals(SKIP_ID)) {
        where = "";
      }
    }
    
    return where;
  }

}
