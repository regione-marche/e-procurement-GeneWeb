package it.eldasoft.gene.tags.functions.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Funzione usata per creare la condizione where per l'archivio G_EVENTISCADENZ.
 *
 * @author Marcello Caminiti
 */
public class ComponiWhereG_EVENTISCADENZFunction extends AbstractComponiWhereFunction {

  // Where conditions
  private static final String DEFAULT_WHERE = "G_EVENTISCADENZ.DISCR = ? and G_EVENTISCADENZ.ENT = ?";

  // Costruttori
  public ComponiWhereG_EVENTISCADENZFunction() {
    super(Logger.getLogger(ComponiWhereG_EVENTISCADENZFunction.class), "G_EVENTISCADENZ");
  }

  // Metodi
  @Override
  protected void integrityChecks(PageContext pageContext) throws JspException {
    if (functionId.equals(DEFAULT_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);

     ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 2);
    } else {
      throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
    }
  }

  @Override
  protected String composeWhere(PageContext pageContext, int popUpId) throws JspException {
    String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
    if (StringUtils.isNotEmpty(where)) {
      if (functionId.equals(DEFAULT_ID))
        where += " AND " + DEFAULT_WHERE ;

    } else {
      if (functionId.equals(DEFAULT_ID))
        where = DEFAULT_WHERE;
    }

    return where;
  }

}
