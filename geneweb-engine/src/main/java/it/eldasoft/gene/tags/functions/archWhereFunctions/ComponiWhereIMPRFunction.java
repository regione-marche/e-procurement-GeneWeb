package it.eldasoft.gene.tags.functions.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Funzione usata per creare la condizione where per l'archivio IMPR.
 * 
 * @author Alvise Gorinati
 */
public class ComponiWhereIMPRFunction extends AbstractComponiWhereFunction {

  // Function IDs
  private static final String DITTE_RAGGRUPPAMENTO_AVVALIMENTO_INTERNO_ID = "ditteRaggruppamentoAvvalimentoInterno";
  private static final String DITTE_AVVALIMENTO_INTERNO_ID = "ditteAvvalimentoInterno";
  private static final String DITTE_DITG_ID = "ditteDitg";
  private static final String DITTE_DITG_SMAT_ID = "ditteDitgSmat";
  private static final String PERP_ID = "perp";
  private static final String PERP_INVOFF_ID = "perpInvoff";
  private static final String ANTICOR_ID = "anticor";
  private static final String TIPIMP_FILTER_ID = "tipimpFilter";

  // Function Id per Sitat/Vigilanza
  private final String W9_IMPR1_FILTER_ID = "imprW9aggiFilter";       // richiede 3 parametri
  private final String W9_IMPR2_FILTER_ID = "imprW9AggiW9subaFilter"; // richiede 6 parametri
  
  // Where conditions
  private static final String DITTE_RAGGRUPPAMENTO_AVVALIMENTO_INTERNO_WHERE =
      "codimp IN (SELECT coddic FROM ragimp WHERE codime9 = ?)";
  private static final String DITTE_AVVALIMENTO_INTERNO_WHERE =
      "codimp <> ? AND tipimp NOT IN (3, 10)";
  private static final String DITTE_DITG_WHERE =
      "(impr.tipimp <> 3 AND impr.tipimp <> 10) OR impr.tipimp IS NULL";
  private static final String DITTE_DITG_SMAT_WHERE =
      "(v_impr_smat.tipimp <> 3 AND v_impr_smat.tipimp <> 10) OR v_impr_smat.tipimp IS NULL";
  private static final String PERP_WHERE =
      "impr.codimp IN (SELECT dittao FROM ditg WHERE ngara5 = ? AND invoff = 1 AND (fasgar IS NULL OR fasgar > 1))";
  private static final String PERP_INVOFF_WHERE =
      "impr.codimp IN (SELECT dittao FROM ditg WHERE ngara5 = ? AND (invoff IN ('0', '1') OR invoff IS NULL) AND (fasgar IS NULL OR fasgar = 0 OR fasgar > 1))";
  private static final String ANTICOR_WHERE =
      "impr.tipimp IS NOT NULL AND impr.tipimp <> 3 AND impr.tipimp <> 10";
  private static final String TIPIMP_FILTER_WHERE =
      "impr.tipimp IN (6, 7, 8, 9, 10, 11, 12)";
  
  private final String W9_IMPR_W9AGGI_WHERE = "(IMPR.CODIMP in (select CODIMP from W9AGGI where CODGARA=? and CODLOTT=? and NUM_APPA=?))";
  
  private final String W9_IMPR_W9AGGI_W9SUBA_WHERE = "(IMPR.CODIMP in (select CODIMP from W9AGGI where CODGARA=? and CODLOTT=? and NUM_APPA=?) or "
  		+ " IMPR.CODIMP in (select CODIMP from W9SUBA where CODGARA=? and CODLOTT=? and NUM_APPA=?))";

  // Costruttori
  public ComponiWhereIMPRFunction() {
    super(Logger.getLogger(ComponiWhereIMPRFunction.class), "IMPR");
  }

  // Metodi
  @Override
  protected void integrityChecks(PageContext pageContext) throws JspException {
    ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
    if (functionId.equals(ANTICOR_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else if (functionId.equals(DITTE_RAGGRUPPAMENTO_AVVALIMENTO_INTERNO_ID) ||
        functionId.equals(DITTE_AVVALIMENTO_INTERNO_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else if (functionId.equals(DITTE_DITG_ID) ||
        functionId.equals(DITTE_DITG_SMAT_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else if (functionId.equals(PERP_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else if (functionId.equals(PERP_INVOFF_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else if (functionId.equals(TIPIMP_FILTER_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else if (functionId.equals(W9_IMPR1_FILTER_ID)) {
   		ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 3);
    } else if (functionId.equals(W9_IMPR2_FILTER_ID)) {
    	ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 6);
    } else if (functionId.equals(SKIP_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else {
      throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
    }
  }

  @Override
  protected String composeWhere(PageContext pageContext, int popUpId) throws JspException {
    String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
    if (StringUtils.isNotEmpty(where)) {
      if (functionId.equals(ANTICOR_ID)) {
        where += " AND (" + ANTICOR_WHERE + ")";
      } else if (functionId.equals(DITTE_RAGGRUPPAMENTO_AVVALIMENTO_INTERNO_ID)) {
        where += " AND (" + DITTE_RAGGRUPPAMENTO_AVVALIMENTO_INTERNO_WHERE + ")";
      } else if (functionId.equals(DITTE_AVVALIMENTO_INTERNO_ID)) {
        where += " AND (" + DITTE_AVVALIMENTO_INTERNO_WHERE + ")";
      } else if (functionId.equals(DITTE_DITG_ID)) {
        where += " AND (" + DITTE_DITG_WHERE + ")";
      } else if (functionId.equals(DITTE_DITG_SMAT_ID)) {
        where += " AND (" + DITTE_DITG_SMAT_WHERE + ")";
      } else if (functionId.equals(PERP_ID)) {
        where += " AND (" + PERP_WHERE + ")";
      } else if (functionId.equals(PERP_INVOFF_ID)) {
        where += " AND (" + PERP_INVOFF_WHERE + ")";
      } else if (functionId.equals(TIPIMP_FILTER_ID)) {
        where += " AND (" + TIPIMP_FILTER_WHERE + ")";
      } else if (functionId.equals(W9_IMPR1_FILTER_ID)) {
     		where += " AND (" + W9_IMPR_W9AGGI_WHERE + ")";
      } else if (functionId.equals(W9_IMPR2_FILTER_ID)) {
    		where += " AND (" + W9_IMPR_W9AGGI_W9SUBA_WHERE + ")";
      }
    } else {
      if (functionId.equals(ANTICOR_ID)) {
        where = ANTICOR_WHERE;
      } else if (functionId.equals(DITTE_RAGGRUPPAMENTO_AVVALIMENTO_INTERNO_ID)) {
        where = DITTE_RAGGRUPPAMENTO_AVVALIMENTO_INTERNO_WHERE;
      } else if (functionId.equals(DITTE_AVVALIMENTO_INTERNO_ID)) {
        where = DITTE_AVVALIMENTO_INTERNO_WHERE;
      } else if (functionId.equals(DITTE_DITG_ID)) {
        where = DITTE_DITG_WHERE;
      } else if (functionId.equals(DITTE_DITG_SMAT_ID)) {
        where = DITTE_DITG_SMAT_WHERE;
      } else if (functionId.equals(PERP_ID)) {
        where = PERP_WHERE;
      } else if (functionId.equals(PERP_INVOFF_ID)) {
        where = PERP_INVOFF_WHERE;
      } else if (functionId.equals(TIPIMP_FILTER_ID)) {
        where = TIPIMP_FILTER_WHERE;
      } else if (functionId.equals(W9_IMPR1_FILTER_ID)) {
     		where = W9_IMPR_W9AGGI_WHERE;
      } else if (functionId.equals(W9_IMPR2_FILTER_ID)) {
    		where = W9_IMPR_W9AGGI_W9SUBA_WHERE;
      } else if (functionId.equals(SKIP_ID)) {
        where = "";
      }
    }
    
    return where;
  }

}
