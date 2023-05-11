package it.eldasoft.gene.tags.functions.archWhereFunctions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.tags.decorators.archivi.ArchivioTagImpl;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Classe di utilità per le classi ComponiWhereFunction.
 * 
 * @author Alvise Gorinati
 */
public final class ComponiWhereUtils {

  // Costanti
  private static final String NO_FUNCTION_ID_ERROR_MSG = "Non è stato trovato nessun function ID";
  private static final String NO_PARAMS_DEBUG_MSG = "Non sono stati passati parametri al campo 'archWhereParametriLista'";
  private static final String WRONG_PARAMS_COUNT_ERROR_MSG = "Numero di parametri errato: attesi %s, presenti %s";
  private static final String WRONG_FUNCTION_FILTER_FORMAT_ERROR_MSG = "Il filtro passato non è formattato correttamente: %s";
  private static final String MULTIPLE_SAVE_PARAM_ERROR_MSG = "Sono presenti multipli parametri SAVE. È consentito l'utilizzo di un solo parametro di questo tipo.";
  private static final String MULTIPLE_LOAD_PARAM_ERROR_MSG = "Sono presenti multipli parametri LOAD. È consentito l'utilizzo di un solo parametro di questo tipo.";
  
  private static final String FILTER_FUNCTION_FILTER_PATTERN = "^[^:]+:[^:]+$";
  private static final String FILTER_FUNCTION_PARAMS_PATTERN = "^(save|load):[^:]+$";
  private static final String FILTER_SAVE_PARAM_PATTERN = "^save:[^:]+$";
  private static final String FILTER_LOAD_PARAM_PATTERN = "^load:[^:]+$";

  public static final String START_COMPOSE_WHERE_MSG = "Inizio della costruzione della condizione where per l'entità %s";
  public static final String END_COMPOSE_WHERE_MSG = "Fine della costruzione della condizione where per l'entità %s";
  public static final String GENERIC_ERROR_MSG = "Errore nella costruzione della condizione where nell'archivio %s";
  public static final String UNKNOWN_FUNCTION_ID_ERROR_MSG = "Il function ID ricevuto non è riconosciuto dalla funzione: %s";
  public static final String WRONG_PARM_TYPE_ERROR_MSG = "Il parametro passato non è del tipo atteso: tipo atteso %s, valore passato %s";
  
  // Costruttori
  /**
   * Costruttore di default. Privato in modo da non poter istanziare la classe.
   */
  private ComponiWhereUtils() {
    
  }
  
  // Metodi
  /**
   * Metodo utilizzato per controllare che il function ID sia valorizzato nelle funzioni che lo richiedono.
   * 
   * @param functionId
   *            Function ID
   * @param logger
   *            Logger della classe chiamante
   * @param entity
   *            Entità per la quale si sta costruendo la condizione where
   *            
   * @throws JspException
   */
  private static void emptyFunctionIdCheck(final String functionId, final Logger logger, final String entity) throws JspException {
    if (StringUtils.isBlank(functionId)) {
      final JspException e = new JspException(NO_FUNCTION_ID_ERROR_MSG);
      logger.error(String.format(GENERIC_ERROR_MSG, entity), e);
      throw e;
    }
  }
  
  /**
   * Rimuove la parte dei function filters dal function ID
   * 
   * @param fullFunctionId
   * 			Stringa ricevuta dall'attributo functionId dell'archivio
   * 
   * @return Function ID pulito dai filtri
   */
  private static final String removeFunctionFilters(String fullFunctionId) {
	  String result = fullFunctionId;
	  
	  if (fullFunctionId.contains("|")) {
		  result = fullFunctionId.substring(0, fullFunctionId.indexOf("|"));
	  }
	  
	  return result;
  }
  
  /**
   * Estrae il function ID.
   * 
   * @param fullFunctionId
   * 			Stringa ricevuta dall'attributo functionId dell'archivio
   * @param logger
   * 			Logger della classe chiamante
   * @param entity
   * 			Entità per la quale si sta costruendo la condizione where
   * 
   * @return Il solo function ID
   * 
   * @throws JspException
   */
  public static final String extractFunctionId(String fullFunctionId, final Logger logger, final String entity) throws JspException {
	  emptyFunctionIdCheck(fullFunctionId, logger, entity);
	  
	  fullFunctionId = removeFunctionFilters(fullFunctionId);
	  
	  return fullFunctionId.split("_")[0];
  }
  
  /**
   * Estrae i function params.
   * 
   * @param fullFunctionId
   * 			Stringa ricevuta dall'attributo functionId dell'archivio
   * @param logger
   * 			Logger della classe chiamante
   * @param entity
   * 			Entità per la quale si sta costruendo la condizione where
   * 
   * @return Lista contenente i function params
   * 
   * @throws JspException
   */
  public static final String[] extractFunctionParams(String fullFunctionId, final Logger logger, final String entity) throws JspException {
	  emptyFunctionIdCheck(fullFunctionId, logger, entity);
	  
	  fullFunctionId = removeFunctionFilters(fullFunctionId);
	  
	  if (!fullFunctionId.contains("_")) {
		  return new String[0];
	  }
	  
	  final int startIndex = fullFunctionId.indexOf("_") + 1;
	  int endIndex;
	  if (fullFunctionId.contains("|")) {
		  endIndex = fullFunctionId.indexOf("|");
	  } else {
		  endIndex = fullFunctionId.length();
	  }
	  
	  final List<String> functionParams = new ArrayList<String>();
	  for (final String param : Arrays.asList(fullFunctionId.substring(startIndex, endIndex).split("_"))) {
		  if (!Pattern.matches(FILTER_FUNCTION_PARAMS_PATTERN, param)) {
			  functionParams.add(param);
		  }
	  }
	  
	  return functionParams.toArray(new String[functionParams.size()]);
  }
  
  /**
   * Estrae il nome con cui deve essere salvata la query
   * 
   * @param fullFunctionId
   * 			Stringa ricevuta dall'attributo functionId dell'archivio
   * @param logger
   * 			Logger della classe chiamante
   * @param entity
   * 			Entità per la quale si sta costruendo la condizione where
   * 
   * @return Nome con cui deve essere salvata la query
   * 
   * @throws JspException
   */
  public static final String extractSaveName(String fullFunctionId, final Logger logger, final String entity) throws JspException {
	  emptyFunctionIdCheck(fullFunctionId, logger, entity);
	  
	  fullFunctionId = removeFunctionFilters(fullFunctionId);
	  
	  final int startIndex = fullFunctionId.indexOf("_") + 1;
	  int endIndex;
	  if (fullFunctionId.contains("|")) {
		  endIndex = fullFunctionId.indexOf("|");
	  } else {
		  endIndex = fullFunctionId.length();
	  }
	  
	  final String[] functionParams = fullFunctionId.substring(startIndex, endIndex).split("_");
	  String saveParam = null;
	  for (final String param : functionParams) {
		  if (Pattern.matches(FILTER_SAVE_PARAM_PATTERN, param)) {
			  if (saveParam != null) {
				  final JspException e = new JspException(MULTIPLE_SAVE_PARAM_ERROR_MSG);
				  logger.error(GENERIC_ERROR_MSG, e);
				  throw e;
			  } else {
				  saveParam = param;
			  }
		  } 
	  }
	  
	  return saveParam;
  }

  /**
   * Estrae il nome della query da caricare
   * 
   * @param fullFunctionId
   * 			Stringa ricevuta dall'attributo functionId dell'archivio
   * @param logger
   * 			Logger della classe chiamante
   * @param entity
   * 			Entità per la quale si sta costruendo la condizione where
   * 
   * @return Nome della query da caricare
   * 
   * @throws JspException
   */
  public static final String extractLoadName(String fullFunctionId, final Logger logger, final String entity) throws JspException {
	  emptyFunctionIdCheck(fullFunctionId, logger, entity);
	  
	  fullFunctionId = removeFunctionFilters(fullFunctionId);
	  
	  final int startIndex = fullFunctionId.indexOf("_") + 1;
	  int endIndex;
	  if (fullFunctionId.contains("|")) {
		  endIndex = fullFunctionId.indexOf("|");
	  } else {
		  endIndex = fullFunctionId.length();
	  }
	  
	  final String[] functionParams = fullFunctionId.substring(startIndex, endIndex).split("_");
	  String loadParam = null;
	  for (final String param : functionParams) {
		  if (Pattern.matches(FILTER_LOAD_PARAM_PATTERN, param)) {
			  if (loadParam != null) {
				  final JspException e = new JspException(MULTIPLE_LOAD_PARAM_ERROR_MSG);
				  logger.error(GENERIC_ERROR_MSG, e);
				  throw e;
			  } else {
				  loadParam = param;
			  }
		  } 
	  }
	  
	  return loadParam;
  }
  
  /**
   * Estrae i function filters.
   * 
   * @param fullFunctionId
   * 			Stringa ricevuta dall'attributo functionId dell'archivio	
   * @param logger
   * 			Logger della classe chiamante
   * @param entity
   * 			Entità per la quale si sta costruendo la condizione where
   * 
   * @return Lista dei filtri
   * 
   * @throws JspException
   */
  public static final String[] extractFunctionFilters(final String fullFunctionId, final Logger logger, final String entity) throws JspException {
	  emptyFunctionIdCheck(fullFunctionId, logger, entity);
	  
	  if (!fullFunctionId.contains("|")) {
		  return new String[0];
	  }
	  
	  final String[] functionFilters = fullFunctionId.substring(fullFunctionId.indexOf("|") + 1).split("_");
	  for (final String param : functionFilters) {
		  if (!Pattern.matches(FILTER_FUNCTION_FILTER_PATTERN, param)) {
			  final JspException e = new JspException(String.format(WRONG_FUNCTION_FILTER_FORMAT_ERROR_MSG, param));
			  logger.error(String.format(GENERIC_ERROR_MSG, entity), e);
			  throw e;
		  }
	  }
	  
	  return functionFilters;
  }
  
  /**
   * Filtra i parametri della funzione passati tramite il function ID per escludere le direttive di save e load
   * 
   * @param functionParams
   *            Parametri di funzione non filtrati
   *            
   * @return Parametri di funzione filtrati
   */
  public static final String[] filterFunctionParams(final String[] functionParams) {
    List<String> filteredFunctionParams = new ArrayList<String>();
    
    for (final String param : functionParams) {
      if (!Pattern.matches(FILTER_FUNCTION_PARAMS_PATTERN, param)) {
        filteredFunctionParams.add(param);
      }
    }
    
    return filteredFunctionParams.toArray(new String[filteredFunctionParams.size()]);
  }
  
  /**
   * Metodo utilizzato per controllare se il numero di parametri atteso per una funzione corrisponde ai parametri effettivamente ricevuti.
   * 
   * @param pageContext
   *            Contesto della pagina
   * @param logger
   *            Logger della classe chiamante
   * @param entity
   *            Entità per la quale si sta costruendo la condizione where
   * @param expectedParamsCount
   *            Numero atteso di parametri da ricevere
   *            
   * @throws JspException
   */
  public static final void expectedParamsCountCheck(final PageContext pageContext, final Logger logger, 
      final String entity, final int expectedParamsCount) throws JspException {
    int paramsCount = 0;
    try {
      final String params = pageContext.getRequest().getParameter(ArchivioTagImpl.HIDE_INPUT_WHERE_PARAMETRI_SU_LISTA);
      if (StringUtils.isNotEmpty(params)) {
        paramsCount = pageContext.getRequest().getParameter(ArchivioTagImpl.HIDE_INPUT_WHERE_PARAMETRI_SU_LISTA).split(";").length;
      }
    } catch (NullPointerException e) {
      if (logger.isDebugEnabled()) logger.debug(NO_PARAMS_DEBUG_MSG);
    }
    
    if (paramsCount != expectedParamsCount) {
      final JspException e = new JspException(String.format(WRONG_PARAMS_COUNT_ERROR_MSG, expectedParamsCount, paramsCount));
      logger.error(String.format(GENERIC_ERROR_MSG, entity));
      throw e;
    }
  }
  
  /**
   * Metodo utilizzato per controllare se il numero di parametri passati con il function ID necessari alla funzione sia corretto.
   * 
   * @param logger
   *            Logger della classe chiamante
   * @param entity
   *            Entità per la quale si sta costruendo la condizione where
   * @param functionParams
   *            Parametri passati tramite function ID
   * @param expectedParamsCount
   *            Numero atteso di parametri da ricevere
   *            
   * @throws JspException
   */
  public static final void expectedFunctionParamsCountCheck(final Logger logger, final String entity, 
      final String[] functionParams, final int expectedParamsCount) throws JspException {
    final int paramsCount = functionParams.length;
    
    if (paramsCount != expectedParamsCount) {
      final JspException e = new JspException(String.format(WRONG_PARAMS_COUNT_ERROR_MSG, expectedParamsCount, paramsCount));
      logger.error(String.format(GENERIC_ERROR_MSG, entity));
      throw e;
    }
  }
  
  /**
   * Controlla se sono presenti dei filtri da applicare alla condizione di where nella richiesta della servlet.
   * 
   * @param request
   * 		Richiesta della servlet
   * 
   * @return True se è presente un filtro
   */
  public static final boolean isFilterPresent(final HttpSession session, final String entita, final int popupLevel) {
	  final String filter = UtilityTags.getAttributeForSqlBuild(session, entita, popupLevel, UtilityTags.HIDDEN_WHERE_FILTRO_ARCHIVIO);
	  
	  if (filter == null) {
		  return false;
	  }
	  
	  return StringUtils.isNotEmpty(UtilityTags.getAttributeForSqlBuild(session, entita, popupLevel, UtilityTags.HIDDEN_WHERE_FILTRO_ARCHIVIO).toString());
  }
  
  /**
   * Ritorna la condizione where controllando se è necessario applicarci dei filtri.
   * 
   * @param request
   * 			Richiesta della servlet
   * 
   * @return La condizione where richiesta
   */
  public static final String getWhere(final HttpSession session, final String entita, final int popupLevel) {
	  String where = "";
	  
	  if (isFilterPresent(session, entita, popupLevel)) {
		  where += UtilityTags.getAttributeForSqlBuild(session, entita, popupLevel, UtilityTags.HIDDEN_WHERE_FILTRO_ARCHIVIO);
	  }
	  
	  return where;
  }
  
  /**
   * Fa avere i parametri da utilizzare nella query controllando se è necessario recuperarli.
   * 
   * @param ctx
   * 			Contesto della pagina
   * @param entity
   * 			Entità per la quale si sta costruendo la condizione where
   * @param popupLevel
   * 			Livello del pop-up
   * @param functionParams
   * 			Parametri del function ID
   * 
   * @return I parametri richiesti
   */
  public static final String getParameters(final PageContext ctx, final String entita, final int popupLevel) {
	  final String requestParams = ctx.getRequest().getParameter(ArchivioTagImpl.HIDE_INPUT_WHERE_PARAMETRI_SU_LISTA);
	  String filterParams = "";
	  
	  if (isFilterPresent(ctx.getSession(), entita, popupLevel)) {
		  filterParams += UtilityTags.getAttributeForSqlBuild(ctx.getSession(), entita, popupLevel, UtilityTags.HIDDEN_PARAMETRI_FILTRO_ARCHVIO);
	  }
	  
	  if (StringUtils.isEmpty(filterParams)) {
		  return requestParams;
	  } else {
		  return filterParams + ";" + requestParams;
	  }
  }
  
  /**
   * Salva la where ed i parametri in sessione come attributi temporanei.
   * 
   * @param session
   *            Sessione HTTP
   * @param varName
   *            Entità chiamante
   * @param where
   *            Condizione where
   * @param whereParams
   *            Parametri della condizione where
   */
  public static final void saveSqlBuild(final HttpSession session, final String varName, final String where, final String whereParams) {
    UtilityTags.createTempHashAttributeForSqlBuild(session, varName);
    UtilityTags.putTempAttributeForSqlBuild(session, varName, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);
    UtilityTags.putTempAttributeForSqlBuild(session, varName, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, whereParams);
  }
  
  /**
   * Carica una condizione where temporanea ed i duoi parametri e l'aggiunge alla where e ad i parametri passati come argomenti.
   * 
   * @param session
   *            Sessione HTTP
   * @param varName
   *            Entità da cui prendere la condizione ed i suoi parametri
   * @param where
   *            Where da estendere
   * @param whereParams
   *            Parametri da estendere
   */
  public static final void loadSqlBuild(final HttpSession session, final String varName, String where, String whereParams) {
    final String loadWhere = UtilityTags.popTempAttributeForSqlBuild(session, varName, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA);
    where += " AND " + loadWhere;
    
    final String loadWhereParams = UtilityTags.popTempAttributeForSqlBuild(session, varName, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA);
    whereParams += ";" + loadWhereParams;
  }
  
  /**
   * Metodo utilizzato per salvare la query eseguita con la chiamata della funzione (utilizzando il function param 'save'), oppure per
   * caricare query salvate in precendenza da altre entità (utilizzando il function param 'load:' seguito dal nome dell'entità da cui
   * si vuole reperire la condizione where).
   * 
   * @param pageContext
   *            Contesto della pagina
   * @param functionParams
   *            Parametri di funzione
   * @param 
   *            Entità per la quale si sta costruendo la condizione where
   * @param where
   *            Where da modificare
   * @param whereParams
   *            Parametri della where da modificare
   */
  public static final void saveAndLoadForSqlBuild(final PageContext pageContext, final String[] functionParams, String where, String whereParams) {
    for (final String param : functionParams) {
      if (param.startsWith("save")) {
        final String varName = param.substring(param.indexOf(":") + 1);
        ComponiWhereUtils.saveSqlBuild(pageContext.getSession(), varName, where, whereParams);
      }
    }
    
    for (final String param : functionParams) {
      if (param.startsWith("load")) {
        final String savedEntity = param.substring(param.indexOf(":") + 1);
        ComponiWhereUtils.loadSqlBuild(pageContext.getSession(), savedEntity, where, whereParams);
      }
    }
  }
  
  /**
   * Crea un'eccezione Jsp dovuta ad un function ID non riconosciuto completa di messaggi e log
   * 
   * @param functionId
   *            Function ID non riconosciuto
   * @param entity
   *            Entità per la quale non si conosce il function ID
   * @param logger
   *            Logger dell'entità
   *            
   * @return L'eccezione completa di messaggio
   */
  public static final JspException getUnknownFunctionIdException(final String functionId, final String entity, final Logger logger) {
    final JspException e = new JspException(String.format(ComponiWhereUtils.UNKNOWN_FUNCTION_ID_ERROR_MSG, functionId));
    logger.error(String.format(ComponiWhereUtils.GENERIC_ERROR_MSG, entity), e);
    
    return e;
  }
  
  /**
   * Crea un'eccezione Jsp dovuta ad un parametro di tipo errato passato alla funzione.
   * 
   * @param clazz
   *            Classe aspettata
   * @param entity
   *            Entità alla quale è stato passato un parametro di tipo errato
   * @param logger
   *            Logger dell'entità
   *            
   * @return L'eccezione completa di messaggio
   */
  public static final JspException getWrongParamTypeException(final Class<?> clazz, final String value, final String entity, final Logger logger) {
    final JspException e = new JspException(String.format(ComponiWhereUtils.WRONG_PARAMS_COUNT_ERROR_MSG, clazz.getName(), value));
    logger.error(String.format(ComponiWhereUtils.GENERIC_ERROR_MSG, entity), e);
    
    return e;
  }
  
  /**
   * Reperisce il filtro a livello utente.
   * 
   * @param pageContext
   *            Contesto della pagina
   *            
   * @return Il filtro a livello utente
   */
  public static String getFiltroLivelloUtente(final PageContext pageContext, final String onEntity) {
    final ProfiloUtente profiloUtente = (ProfiloUtente) pageContext.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    
    return FiltroLivelloUtenteFunction.getFiltroLivelloUtente(onEntity, profiloUtente);
  }
  
  /**
   * Reperisce il filtro dell'ufficio intestatario
   * 
   * @param pageContext
   *            Contesto della pggin]a
   *            
   * @return Il contesto della pagina
   * 
   * @throws JspException
   */
  public static String getFiltroUffint(final PageContext pageContext) throws JspException {
    String filtroUffint = "";
    
    final String uffint = (String) pageContext.getSession().getAttribute("uffint");
    if (StringUtils.isNotEmpty(uffint)) {
      final String filtroAbilitato = ConfigManager.getValore("integrazioneLFS.filtroUffint");
      if (filtroAbilitato.equals("1")) {
        final SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
        
        String iscuc = "";
        try {
          iscuc = (String) sqlManager.getObject("SELECT iscuc FROM uffint WHERE codein = ?", new Object[] { uffint });
        } catch (SQLException e) {
          throw new JspException("Errore durante la lettura del campo UFFINT.ISCUC", e);
        }
        
        if (StringUtils.isNotEmpty(iscuc) && !iscuc.equals("1")) {
          filtroUffint = "AND EXISTS (SELECT codlav FROM peri WHERE codlav = appa.codlav AND cenint = '" + uffint + "')";
        }
      }
    }
    
    return filtroUffint;
  }
  
}
