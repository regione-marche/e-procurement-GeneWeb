package it.eldasoft.gene.tags.functions.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Funzione usata per creare la condizione where per l'archivio TEIM.
 * 
 * @author Alvise Gorinati
 */
public class ComponiWhereTEIMFunction extends AbstractComponiWhereFunction {

  // Function IDs
  private static final String FILTERED_WLEG_ID = "filteredWleg";
  private static final String FILTERED_WDTE_ID = "filteredWdte";
  
  // Where conditions
  private static final String FILTERED_WLEG_WHERE = 
      "teim.codtim IN (SELECT codleg FROM impleg WHERE codimp2 = ?)";
  private static final String FILTERED_WDTE_WHERE = 
      "teim.codtim IN (SELECT coddte FROM impdte WHERE codimp3 = ?)";
  
  // Costruttori
  public ComponiWhereTEIMFunction() {
    super(Logger.getLogger(ComponiWhereTEIMFunction.class), "TEIM");
  }

  // Metodi
  @Override
  protected void integrityChecks(PageContext pageContext) throws JspException {
    ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
    if (functionId.equals(SKIP_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else if (functionId.equals(FILTERED_WLEG_ID) || functionId.equals(FILTERED_WDTE_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else {
      throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
    }
  }

  @Override
  protected String composeWhere(PageContext pageContext, int popUpId) throws JspException {
    String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
    if (StringUtils.isNotEmpty(where)) {
      if (functionId.equals(FILTERED_WLEG_ID)) {
        where += " AND (" + FILTERED_WLEG_WHERE + ")";
      } else if (functionId.equals(FILTERED_WDTE_ID)) {
        where += " AND (" + FILTERED_WDTE_WHERE + ")";
      }
    } else {
      if (functionId.equals(FILTERED_WLEG_ID)) {
        where = FILTERED_WLEG_WHERE;
      } else if (functionId.equals(FILTERED_WDTE_ID)) {
        where = FILTERED_WDTE_WHERE;
      } else if (functionId.equals(SKIP_ID)) {
        where = "";
      }
    }
    
    return where;
  }

}
