package it.eldasoft.gene.tags.functions.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Funzione usata per creare la condizione where per l'archivio TABSCHE.
 * 
 * @author Alvise Gorinati
 */
public class ComponiWhereTABSCHEFunction extends AbstractComponiWhereFunction {

  // Where conditions
  private static final String DEFAULT_WHERE = "tb1.tabcod3 = ?";
  
  // Costruttori
  public ComponiWhereTABSCHEFunction() {
    super(Logger.getLogger(ComponiWhereTABSCHEFunction.class), "TABSCHE");
  }

  // Metodi
  @Override
  protected void integrityChecks(PageContext pageContext) throws JspException {
    if (functionId.equals(DEFAULT_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 1);
      
      if (!functionParams[0].equalsIgnoreCase("true") && !functionParams[0].equalsIgnoreCase("false")) {
        throw ComponiWhereUtils.getWrongParamTypeException(Boolean.class, functionParams[0], ENTITA, LOGGER);
      }
      
      int expectedParamCount = 0;
      if (Boolean.parseBoolean(functionParams[0])) {
        expectedParamCount = 1;
      }
      
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, expectedParamCount);
    } else if (functionId.equals(SKIP_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else {
      throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
    }
  }

  @Override
  protected String composeWhere(PageContext pageContext, int popUpId) throws JspException {
    String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
    if (StringUtils.isNotEmpty(where)) {
      if (functionId.equals(DEFAULT_ID)) {
      	
        final boolean applyCondition = Boolean.parseBoolean(functionParams[0]);
        
        if (applyCondition && !StringUtils.containsIgnoreCase(where, DEFAULT_WHERE)) {
        	where += " AND (" + DEFAULT_WHERE + ")";
        }
      }
    } else {
      if (functionId.equals(DEFAULT_ID)) {
        final boolean applyCondition = Boolean.parseBoolean(functionParams[0]);
        
        if (applyCondition) {
          where = DEFAULT_WHERE;
        } else {
          where = "";
        }
      } else if (functionId.equals(SKIP_ID)) {
        where = "";
      }
    }
    
    return where;
  }

}
