package it.eldasoft.gene.tags.functions.archWhereFunctions;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.utils.spring.UtilitySpring;


/**
 * Funzione usata per creare la condizione where per l'archivio G_COMUNI.
 * 
 * @author Manuel.Bridda
 */
public class ComponiWhereG_COMUNIFunction extends AbstractComponiWhereFunction {

  // Where conditions
  private static final String DEFAULT_WHERE = "g_comuni.provincia = ?";
  
  private static final String FILTER_ACTIVE = "dateBetween";
  
  private static final String CAP_FIELD = "CAP";
  private static final String DESCRI_FIELD = "DESCRI";
  private static final String ISTAT_FIELD = "CODISTAT";
      
  private static final String OPTIONAL_WHERE = "(dtinizio <= DATA OR dtinizio is null) AND (dtfine >= DATA OR dtfine is null)";
  
  private static final String OPTIONAL_VALUE = "UPPER(CAMPO) like '%VALUE%'";
  
  // Costruttori
  public ComponiWhereG_COMUNIFunction() {
    super(Logger.getLogger(ComponiWhereG_COMUNIFunction.class), "G_COMUNI");
  }

  // Metodi
  @Override
  protected void integrityChecks(PageContext pageContext) throws JspException {
    if (functionId.equals(DEFAULT_ID)) {
      
      int params = 1;
      for(String param : functionParams) {
        if(param.indexOf(CAP_FIELD)>-1 || param.indexOf(DESCRI_FIELD)>-1 || param.indexOf(FILTER_ACTIVE)>-1 || param.indexOf(ISTAT_FIELD)>-1) {
          params++;
        }
      }
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, params);
      
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
  
  @Override
  protected String composeAdditionalFilters(final PageContext pageContext, final int popUpId) throws JspException {
      final List<String> filters = new ArrayList<String>();
       
      for (final String filter : functionFilters) {
          final String filterVal = filter.substring(filter.indexOf(":") + 1);
         
          // Solo comuni attivi a una certa data
          if (filter.startsWith(FILTER_ACTIVE)) {
            SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
            String dbFunctionStringToDate = sqlManager.getDBFunction("stringtodate", new String[] { filterVal });
            filters.add(OPTIONAL_WHERE.replace("DATA", dbFunctionStringToDate)); 
          }
          
       // Gestione se immesso codice istat o descrizione del comune 
          if (filter.startsWith(ISTAT_FIELD) || filter.startsWith(DESCRI_FIELD) || filter.startsWith(CAP_FIELD)){
                filters.add(OPTIONAL_VALUE.replace("CAMPO", filter.split(":")[0]).replace("VALUE", filterVal)); 
          }
      }
      return String.join(" AND ", filters);
  }
}
