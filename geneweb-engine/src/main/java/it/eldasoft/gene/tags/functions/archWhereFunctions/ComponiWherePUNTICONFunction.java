package it.eldasoft.gene.tags.functions.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Funzione richiamata dal file 'punticon-lista-popup.jsp' per creare la condizione where per l'archivio PUNTICON.
 *
 * @author Alvise Gorinati
 */
public class ComponiWherePUNTICONFunction extends AbstractComponiWhereFunction {

  // Where conditions
  private static final String DEFAULT_WHERE = "punticon.codein = ? and (orgricorso is null or orgricorso='2')";
  private static final String ORGRICORSO_WHERE = "punticon.codein = ? and  orgricorso='1'";
  private static final String ORGRISCORSO_ID = "orgricorso";

  // Costruttori
  public ComponiWherePUNTICONFunction() {
    super(Logger.getLogger(ComponiWherePUNTICONFunction.class), "PUNTICON");
  }

  // Metodi
  @Override
  protected void integrityChecks(PageContext pageContext) throws JspException {
    ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
    if (functionId.equals(DEFAULT_ID) || functionId.equals(ORGRISCORSO_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else if (functionId.equals(SKIP_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else {
      throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
    }
  }

  @Override
  protected String composeWhere(final PageContext pageContext, final int popUpId) throws JspException {
    String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
    if (StringUtils.isNotEmpty(where)) {
      if (functionId.equals(DEFAULT_ID)) {
        where += " AND (" + DEFAULT_WHERE + ")";
      }else if (functionId.equals(ORGRISCORSO_ID)) {
        where += " AND (" + ORGRICORSO_WHERE + ")";
      }
    } else {
      if (functionId.equals(DEFAULT_ID)) {
        where = DEFAULT_WHERE;
      } else if (functionId.equals(SKIP_ID)) {
        where = "";
      }else if(functionId.equals(ORGRISCORSO_ID)){
        where = ORGRICORSO_WHERE;
      }
    }

    return where;
  }

}
