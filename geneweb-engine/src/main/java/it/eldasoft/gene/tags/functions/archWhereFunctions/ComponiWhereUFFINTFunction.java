package it.eldasoft.gene.tags.functions.archWhereFunctions;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Funzione usata per creare la condizione where per l'archivio UFFINT.
 *
 * @author Alvise Gorinati
 */
public class ComponiWhereUFFINTFunction extends AbstractComponiWhereFunction {

  // Function IDs
  private static final String G1AQSPESA_ID = "g1aqspesa";
  private static final String NULL_ISCUC_ID = "nullIscuc";
  private static final String CODEIN_NOT_EQUALS_ID = "codeinNotEquals";

  // Nomi form
  private static final String TORN_PARENT_FORM_NAME = "TORNAltriSogg";
  private static final String GARE_PARENT_FORM_NAME = "GareAltriSogg";
  private static final String G1AQSPESA_PARENT_FORM_NAME = "G1AQSPESA";

  // Where conditions
  private static final String NULL_ISCUC_WHERE =
      "(uffint.iscuc IS NULL OR uffint.iscuc <> '1')";
  private static final String G1AQSPESA_SCHEDA_DEFAULT_WHERE =
      "uffint.codein IN (SELECT cenint FROM garaltsog WHERE ngara = ?)";
  private static final String G1AQSPESA_SCHEDA_MODCONT_2_WHERE =
      "uffint.codein IN (SELECT cenint FROM garaltsog WHERE ngara in";
  private static final String CODEIN_NOT_EQUALS_WHERE =
      "uffint.codein <> ?";

  // Nomi filtri addizionali
  private static final String ABILITAZIONE_FILTER_NAME = "abilitazione";
  private static final String PARENT_FORM_NAME_FILTER_NAME = "parentFormName";

  // Filtri addizionali
  private static final String NOT_NULL_DATFIN_FILTER =
		  "uffint.datfin IS NOT NULL";
  private static final String NULL_DATFIN_FILTER =
		  "uffint.datfin IS NULL";

  // Costruttori
  public ComponiWhereUFFINTFunction() {
    super(Logger.getLogger(ComponiWhereUFFINTFunction.class), "UFFINT");
  }

  // Metodi
  @Override
  protected void integrityChecks(final PageContext pageContext) throws JspException {
    if (functionId.equals(SKIP_ID) ||
        functionId.equals(NULL_ISCUC_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else if (functionId.equals(G1AQSPESA_ID)) {
      final int modcont = Integer.parseInt(functionParams[0]);
      if (modcont == 2) {
        ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 2);
        ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
      } else {
        ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 1);
        ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
      }
    } else if (functionId.equals(CODEIN_NOT_EQUALS_ID)) {
      ComponiWhereUtils.expectedFunctionParamsCountCheck(LOGGER, ENTITA, functionParams, 0);
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    } else {
      throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
    }
  }

  @Override
  protected String composeWhere(final PageContext pageContext, final int popUpId) throws JspException {
    String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
    if (functionId.equals(SKIP_ID)) {
      return where;
    }

    boolean notEmptyWhere = false;
    if (StringUtils.isNotEmpty(where)) {
      where += " AND (";
      notEmptyWhere = true;
    } else {
      where = "";
    }

    if (functionId.equals(G1AQSPESA_ID)) {
      final int modcont = Integer.parseInt(functionParams[0]);
      String elencoLotti = null;
      if(modcont==2)
        elencoLotti = functionParams[1];

      where += composeG1aqspesaWhere(modcont, elencoLotti);
    } else if (functionId.equals(NULL_ISCUC_ID)) {
      where += NULL_ISCUC_WHERE;
    } else if (functionId.equals(CODEIN_NOT_EQUALS_ID)) {
      where += CODEIN_NOT_EQUALS_WHERE;
    }

    if (notEmptyWhere) {
      where += ")";
    }

    return where;
  }

  @Override
  protected String composeAdditionalFilters(final PageContext pageContext, final int popUpId) throws JspException {
	  final List<String> filters = new ArrayList<String>();

	  for (final String filter : functionFilters) {
		  final String filterVal = filter.substring(filter.indexOf(":") + 1);

		  // Abilitazione
		  if (filter.startsWith(ABILITAZIONE_FILTER_NAME)) {
			  final int abilitazione = Integer.parseInt(filterVal);

			  switch (abilitazione) {
			  case 1:
				  filters.add(NULL_DATFIN_FILTER);
				  break;
			  case 2:
				  filters.add(NOT_NULL_DATFIN_FILTER);
				  break;
			  default:
				  LOGGER.warn("Valore del filtro addizionale \"abilitazione\" non conosciuto: " + abilitazione);
				  break;
			  }

		  // Nome parent form
		  } else if (filter.startsWith(PARENT_FORM_NAME_FILTER_NAME)) {
			  if (filterVal.equals(TORN_PARENT_FORM_NAME) || filterVal.equals(GARE_PARENT_FORM_NAME)) {
				  filters.add(NULL_ISCUC_WHERE);
			  } else if (filterVal.equals(G1AQSPESA_PARENT_FORM_NAME)) {
				  final String where = UtilityTags.getAttributeForSqlBuild(pageContext.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA);
				  if (StringUtils.isBlank(where)) {
					  continue;
				  }

			      final int garaltsogFilterPos = where.toLowerCase().indexOf("and uffint.codein in");

			      if (garaltsogFilterPos > 0) {
			        filters.add(where.substring(garaltsogFilterPos + 4));
			      }
			  }
		  }
	  }

	  return String.join(" AND ", filters);
  }

  /**
   * Compone la condizione where specifica per l'ID g1aqspesa.
   *
   * @param where
   *            Condizione where da modificare
   * @param modcont
   *            Modalità contratto
   * @param elencoLotti
   *            Elenco lotti
   */
  private String composeG1aqspesaWhere(final int modcont, final String elencoLotti) {
    String where="";
    switch (modcont) {
      default:
        where += G1AQSPESA_SCHEDA_DEFAULT_WHERE;
        break;
      case 2:
        where += G1AQSPESA_SCHEDA_MODCONT_2_WHERE + " " + elencoLotti + ")";
        break;
    }
    return where;
  }

}
