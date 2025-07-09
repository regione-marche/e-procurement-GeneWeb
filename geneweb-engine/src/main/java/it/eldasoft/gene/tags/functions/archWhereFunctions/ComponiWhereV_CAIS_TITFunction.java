package it.eldasoft.gene.tags.functions.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Funzione usata per creare la condizione where per l'archivio V_CAIS_TIT.
 * 
 * @author Alvise Gorinati
 */
public class ComponiWhereV_CAIS_TITFunction extends AbstractComponiWhereFunction {

  // Function IDs
  private static final String CATEGORIE_GARA_ID = "categorieGara";
  private static final String CATEGORIE_GARALOTTO_ID = "categorieGaraLotto";
  
  // Where conditions
  private static final String FILTER_TIPLAVG_WHERE =
      "v_cais_tit.tiplavg = ?";
  private static final String CATEGORIE_GARA_WHERE =
      "caisim IN (SELECT catiga FROM catg WHERE ngara = ?) OR caisim IN (SELECT catoff FROM opes WHERE ngara3 = ?)";
  private static final String CATEGORIE_GARALOTTO_WHERE =
          "(" + CATEGORIE_GARA_WHERE + ") and " + FILTER_TIPLAVG_WHERE;
  
  // Costruttori
  public ComponiWhereV_CAIS_TITFunction() {
    super(Logger.getLogger(ComponiWhereV_CAIS_TITFunction.class), "V_CAIS_TIT");
  }

  // Metodi
  @Override
  protected void integrityChecks(final PageContext pageContext) throws JspException {
    ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
    if (functionId.equals(DEFAULT_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else if (functionId.equals(CATEGORIE_GARA_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 2);
    } else if (functionId.equals(CATEGORIE_GARALOTTO_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 3);
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
      if (functionId.equals(CATEGORIE_GARA_ID)) {
        where += " AND (" + CATEGORIE_GARA_WHERE + ")";
      } else if (functionId.equals(CATEGORIE_GARALOTTO_ID)) {
        where += " AND (" + CATEGORIE_GARALOTTO_WHERE + ")";
      } else if (functionId.equals(DEFAULT_ID)) {
        where += " AND (" + FILTER_TIPLAVG_WHERE + ")";
      }
    } else {
      if (functionId.equals(CATEGORIE_GARA_ID)) {
        where = CATEGORIE_GARA_WHERE;
      } else if (functionId.equals(CATEGORIE_GARALOTTO_ID)) {
        where = CATEGORIE_GARALOTTO_WHERE;
      } else if (functionId.equals(DEFAULT_ID)) {
        where = FILTER_TIPLAVG_WHERE;
      } else if (functionId.equals(SKIP_ID)) {
        where = "";
      }
    }
    
    return where;
  }
  
}
