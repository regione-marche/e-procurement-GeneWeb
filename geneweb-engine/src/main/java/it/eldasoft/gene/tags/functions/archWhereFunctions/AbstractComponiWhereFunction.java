package it.eldasoft.gene.tags.functions.archWhereFunctions;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.decorators.archivi.ArchivioTagImpl;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;

public abstract class AbstractComponiWhereFunction extends AbstractFunzioneTag{

  // Costanti
  protected final Logger LOGGER;
  
  protected final String ENTITA;
  
  // Function IDs
  protected static final String SKIP_ID = "skip";
  protected static final String DEFAULT_ID = "default";
  
  // Variabili
  protected String functionId;
  protected String[] functionParams;
  protected String saveName;
  protected String loadName;
  protected String[] functionFilters;
  
  // Costruttori
  public AbstractComponiWhereFunction(final Logger logger, final String entita) {
    super(1, new Class<?>[] { Object.class });
    
    LOGGER = logger;
    ENTITA = entita;
  }
  
  // Metodi
  @Override
  public String function(PageContext pageContext, Object[] params) throws JspException {
    if (LOGGER.isDebugEnabled()) LOGGER.debug(String.format(ComponiWhereUtils.START_COMPOSE_WHERE_MSG, ENTITA));
    
    final String fullFunctionId = StringUtils.strip(pageContext.getRequest().getParameter(ArchivioTagImpl.HIDE_INPUT_FUNCTION_ID));
    functionId = ComponiWhereUtils.extractFunctionId(fullFunctionId, LOGGER, ENTITA);
    functionParams = ComponiWhereUtils.extractFunctionParams(fullFunctionId, LOGGER, ENTITA);
    saveName = ComponiWhereUtils.extractSaveName(fullFunctionId, LOGGER, ENTITA);
    loadName = ComponiWhereUtils.extractLoadName(fullFunctionId, LOGGER, ENTITA);
    functionFilters = ComponiWhereUtils.extractFunctionFilters(fullFunctionId, LOGGER, ENTITA);
    
    integrityChecks(pageContext);
    
    int popUpId = UtilityTags.getNumeroPopUp(pageContext);
    if (!UtilityTags.existsHashAttributeForSqlBuild(pageContext.getSession(), ENTITA, popUpId)) {
      UtilityTags.createHashAttributeForSqlBuild(pageContext.getSession(), ENTITA, popUpId);
    }
    
    final String where = composeWhere(pageContext, popUpId);
    UtilityTags.putAttributeForSqlBuild(pageContext.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    
    if (functionFilters.length > 0) {
    	final String addFilters = composeAdditionalFilters(pageContext, popUpId);
    	UtilityTags.putAttributeForSqlBuild(pageContext.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_FILTRI_ADDIZIONALI_DA_TROVA, addFilters);
	}
    
    final String whereParams = ComponiWhereUtils.getParameters(pageContext, ENTITA, popUpId);
    UtilityTags.putAttributeForSqlBuild(pageContext.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, whereParams);
    
    ComponiWhereUtils.saveAndLoadForSqlBuild(pageContext, functionParams, where, whereParams);
    
    if (LOGGER.isDebugEnabled()) LOGGER.debug(String.format(ComponiWhereUtils.END_COMPOSE_WHERE_MSG, ENTITA));

    return null;
  }
  
  /**
   * Esegue dei controlli di integrità sulla funzione chiamata.
   * 
   * @param pageContext
   *        Contesto della pagina
   *        
   * @throws JspException
   */
  protected abstract void integrityChecks(final PageContext pageContext) throws JspException;
  
  /**
   * Genera la condizione where.
   * 
   * @param pageContext
   *            Contesto della pagina
   * @param popUpId
   *            Livello del pop-up
   *            
   * @return La condizione where generata
   */
  protected abstract String composeWhere(final PageContext pageContext, final int popUpId) throws JspException;
  
  /**
   * Metodo da estendere per aggiungere filtri in coda alla condizione che si sta creando.
   * 
   * @param pageContext
   * 			Contesto della pagna
   * @param popUpId
   * 			Livello del pop-up
   * 
   * @throws JspException
   */
  protected String composeAdditionalFilters(final PageContext pageContext, final int popUpId) throws JspException {
	  LOGGER.warn("Sono stati passati filtri addizionali inutilizzati all'archivio.");
	  
	  return "1 = 1";
  }

}
