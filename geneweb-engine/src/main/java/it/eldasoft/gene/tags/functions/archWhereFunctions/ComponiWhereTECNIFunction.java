package it.eldasoft.gene.tags.functions.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.utils.properties.ConfigManager;

/**
 * Funzione usata per creare la condizione where per l'archivio TECNI.
 * 
 * @author Alvise Gorinati
 */
public class ComponiWhereTECNIFunction extends AbstractComponiWhereFunction {

  // Function IDs
  private static final String COMMNOMIN_ID = "commnomin";
  private static final String W3_ID = "w3";
  private static final String TECNI_ID = "tecni";
  private static final String TECNI_UFFINT_FILTER_ID = "tecniUffintFilter";
  
  // Where conditions
  private static final String COMMNOMIN_WHERE = 
      "NOT EXISTS (SELECT * FROM commnomin WHERE commnomin.codtec = tecni.codtec AND idalbo = ?)";
  private static final String W3_WHERE_CODEIN =
      "tecni.cftec in (select cfrup from w9deleghe where id_collaboratore = ? and codein = ?) or tecni.cftec = ?  and nomtec is not null and cftec is not null";
  private static final String W3_WHERE=             
      "tecni.cftec in (select cfrup from w9deleghe where id_collaboratore = ? or (codein = ? and codein is not null)) or tecni.cftec = ?  and nomtec is not null and cftec is not null ";
  private static final String TECNI_WHERE =
      "tecni.syscon IS NULL OR tecni.syscon = ?";
  private static final String TECNI_UFFINT_FILTER_WHERE = 
      "tecni.cgentei = ?";
  
  // Costruttori
  public ComponiWhereTECNIFunction() {
    super(Logger.getLogger(ComponiWhereTECNIFunction.class), "TECNI");
  }

  // Metodi
  @Override
  protected void integrityChecks(final PageContext pageContext) throws JspException {
    ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
    if (functionId.equals(SKIP_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else if (functionId.equals(COMMNOMIN_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else if (functionId.equals(W3_ID)) {
      if (hasFiltroUffintTecni()) {
        ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 4);
      } else {
        ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 3);
      }
    } else if (functionId.equals(TECNI_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else if (functionId.equals(TECNI_UFFINT_FILTER_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else {
      throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
    }
  }
  
  @Override
  protected String composeWhere(final PageContext pageContext, final int popUpId) throws JspException {
    String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
    String w3filter = W3_WHERE;
    String uffint = (String) pageContext.getSession().getAttribute("uffint");
    if(uffint!=null) {
      w3filter = W3_WHERE_CODEIN;
    }
    if(StringUtils.isNotEmpty(where)) {
      if (functionId.equals(COMMNOMIN_ID)) {
        where += " AND (" + COMMNOMIN_WHERE + ")";
      } else if (functionId.equals(W3_ID)) {
        if (hasFiltroUffintTecni()) {
          where += " AND (" + TECNI_UFFINT_FILTER_WHERE + " AND " + w3filter + ")";
        } else {
          where += " AND (" + w3filter + ")";
        }
      } else if (functionId.equals(TECNI_ID)) {
        where += " AND (" + TECNI_WHERE + ")";
      } else if (functionId.equals(TECNI_UFFINT_FILTER_ID)) {
        where += " AND " + TECNI_UFFINT_FILTER_WHERE;
      }
    } else {
      if (functionId.equals(COMMNOMIN_ID)) {
        where = COMMNOMIN_WHERE;
      } else if (functionId.equals(W3_ID)) {
        if (hasFiltroUffintTecni()) {
          where = TECNI_UFFINT_FILTER_WHERE + " AND " + w3filter;
        } else {
          where = w3filter;
        }
      } else if (functionId.equals(TECNI_ID)) {
        where = TECNI_WHERE;
      } else if (functionId.equals(TECNI_UFFINT_FILTER_ID)) {
        where = TECNI_UFFINT_FILTER_WHERE;
      } else if (functionId.equals(SKIP_ID)) {
        where = "";
      }
    }
    
    return where;
  }
  
  /**
   * Controlla se sono presente gli archivi filtrati.
   * 
   * @return True se esistono gli arhivi filtrati
   */
  private boolean hasFiltroUffintTecni() {
    final String archiviFiltrati = ConfigManager.getValore("it.eldasoft.associazioneUffintAbilitata.archiviFiltrati");
    
    return archiviFiltrati != null && archiviFiltrati.contains(ENTITA);
  }

}
