package it.eldasoft.gene.tags.functions.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.utils.properties.ConfigManager;

/**
 * Funzione usata per creare la condizione where per l'archivio QFORMLIB.
 *
 * @author Marcello Caminiti
 */
public class ComponiWhereQFORMLIBFunction extends AbstractComponiWhereFunction {

  // Where conditions
  private static final String BASE_WHERE =
      "QFORMLIB.STATO = 2 and QFORMLIB.GENERE = 1 and (QFORMLIB.BUSTA = ? or QFORMLIB.BUSTA is null)"
      + " and (QFORMLIB.DATAINI is null or QFORMLIB.DATAINI <=";

  private static final String ELENCO_BASE_WHERE =
      "QFORMLIB.STATO = 2 and QFORMLIB.GENERE = 2 and (QFORMLIB.DATAINI is null or QFORMLIB.DATAINI <=";

  private static final String ELENCO_ID = "elenco";

  private static final String ELENCOPREMIALE_ID = "elencopremiale";

  private static final String ELENCOPREMIALE_WHERE = "QFORMLIB.TIPOLOGIA=6 AND "+ ELENCO_BASE_WHERE;

  private static final String ELENCONONPREMIALE_WHERE = "(QFORMLIB.TIPOLOGIA is null or QFORMLIB.TIPOLOGIA<>6) AND "+ ELENCO_BASE_WHERE;

  // Costruttori
  public ComponiWhereQFORMLIBFunction() {
    super(Logger.getLogger(ComponiWhereQFORMLIBFunction.class), "QFORMLIB");
  }

  // Metodi
  @Override
  protected void integrityChecks(final PageContext pageContext) throws JspException {
    if (functionId.equals(DEFAULT_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 1);
    }else if (functionId.equals(ELENCO_ID) || functionId.equals(ELENCOPREMIALE_ID)) {
      ComponiWhereUtils.expectedParamsCountCheck(pageContext, LOGGER, ENTITA, 0);
    } else {
      throw ComponiWhereUtils.getUnknownFunctionIdException(functionId, ENTITA, LOGGER);
    }

  }

  @Override
  protected String composeWhere(final PageContext pageContext, final int popUpId) throws JspException {
    String db = ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);
    String whereArchivio = BASE_WHERE;
    if(functionId.equals(ELENCO_ID))
      whereArchivio = ELENCONONPREMIALE_WHERE;
    else if(functionId.equals(ELENCOPREMIALE_ID))
      whereArchivio = ELENCOPREMIALE_WHERE;

    if("ORA".equals(db))
      whereArchivio+="trunc(sysdate)";
    else if("POS".equals(db))
      whereArchivio+="DATE(now())";
    else
      whereArchivio+="CAST(GETDATE() AS DATE)";
    whereArchivio+=") and (QFORMLIB.DATAFINE is null or QFORMLIB.DATAFINE >= ";
    if("ORA".equals(db))
      whereArchivio+="trunc(sysdate)";
    else if("POS".equals(db))
      whereArchivio+="DATE(now())";
    else
      whereArchivio+="CAST(GETDATE() AS DATE)";
    whereArchivio+=")";

    String where = ComponiWhereUtils.getWhere(pageContext.getSession(), ENTITA, popUpId);
    if (StringUtils.isNotEmpty(where)) {
      where += " AND (" + whereArchivio + ")";
    } else {
      where = whereArchivio;
    }

    return where;
  }


}
