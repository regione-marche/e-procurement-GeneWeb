package it.eldasoft.gene.tags.gestori.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.KeyParamValidator;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.spring.UtilitySpring;

public class GestoreNoteAvvisiPlugin extends AbstractGestorePreload {

  // Costanti
  private static final Logger LOGGER = Logger.getLogger(GestoreNoteAvvisiPlugin.class);

  private static final String ENTITA = "G_NOTEAVVISI";

  private static final String ENTITA_PARAM_NAME = "entita";
  private static final String CHIAVE_PARAM_NAME = "chiave";
  private static final String KEY_PARENT_PARAM_NAME = "keyParent";
  private static final String KEY_ADD_PARAM_NAME = "keyAdd";


  private static final String CAMPI_KEY_TEMPLATE = "G_NOTEAVVISI.NOTEKEY%d=T:%s";
  private static final String WHERE_KEY_TEMPLATE = "G_NOTEAVVISI.NOTEKEY%d = ?";

  private static final String ERROR_MSG = "Parametri passati alla pagina alterati. Possibile tentativo di SQL injection:\nParametro 'entità': %s\nParametro 'chiave': %s";

  // Costruttori
  public GestoreNoteAvvisiPlugin(BodyTagSupportGene tag) {
    super(tag);
  }

  // Metodi
  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda) throws JspException {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreNoteAvvisi.doBeforeBodyProcessing: inizio metodo");

    final int popUpId = UtilityTags.getNumeroPopUp(page);
    String entitaParam = page.getRequest().getParameter(ENTITA_PARAM_NAME);
    String chiaveParam = page.getRequest().getParameter(CHIAVE_PARAM_NAME);
    final String keyParentParam = page.getRequest().getParameter(KEY_PARENT_PARAM_NAME);
    
    if (StringUtils.isEmpty(chiaveParam)) {
      chiaveParam = page.getRequest().getParameter(KEY_ADD_PARAM_NAME);
      entitaParam = keyParentParam.substring(0, keyParentParam.indexOf("."));
    }

    // Controllo che i parametri siano validi
    if (!checkParams(page, chiaveParam)) {
        final String formattedErrorMsg = String.format(ERROR_MSG, entitaParam, chiaveParam);
        LOGGER.error(formattedErrorMsg);
        throw new JspException(formattedErrorMsg);
    }

    // Estraggo le chiavi
    final List<String> chiavi = extractCampi(chiaveParam);

    UtilityTags.createHashAttributeForSqlBuild(page.getSession(), ENTITA, popUpId);

    // Compongo la stringa where
    final String where = composeWhere(page, popUpId, chiavi);
    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);

    // Compongo la stringa dei parametri
    final String params = composeParams(page, entitaParam, popUpId, chiavi);
    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, params);

    // Compongo la variabile addKeyRiga
    final String addKeyRiga = composeAddKeyRiga(chiavi);
    page.getSession().setAttribute("addKeyRiga", addKeyRiga);

    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreNoteAvvisi.doBeforeBodyProcessing: fine metodo");
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {
    // Metodo non utilizzato
  }

  /**
   * Controlla che i parametri url-encoded immessi siano validi ed in nessun modo contaminati
   *
   * @param page
   *            Contesto della pagina
   * @param entitaParam
   *            Entitï¿½ della pagina
   * @param chiaveParam
   *            Parametri da inserire nella query
   *
   * @return True se tutti i parametri ricevuti sono corretti e non sono stati manipolati in alcun modo
   */
  private boolean checkParams(final PageContext page, final String chiaveParam) {
    final SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", page, SqlManager.class);

    for (final String chiave : chiaveParam.split(";")) {
      if (!Pattern.matches(KeyParamValidator.KEY_FORMAT_VALIDATION_REGEX, chiave)) {
        return false;
      }

      final String nomeEntita = chiave.substring(0, chiave.indexOf("."));
      if (!sqlManager.isTable(nomeEntita)) {
        return false;
      }

      final int startIndex = chiave.indexOf(".") + 1;
      final int endIndex = chiave.indexOf("=");
      final String nomeCampo = chiave.substring(startIndex, endIndex);

      if (DizionarioCampi.getInstance().getCampoByNomeFisico(String.join(".", nomeEntita, nomeCampo)) == null) {
        return false;
      }
    }

    return true;
  }

  /**
   * Estrae i campi JdbcParam dalla chiave
   *
   * @param chiaveParam
   *           Chiave della query
   *
   * @return Lista delle chiavi
   */
  private List<String> extractCampi(final String chiaveParam) {
    final List<String> result = new ArrayList<String>();

    String valPar=null;

    for (final String chiave : chiaveParam.split(";")) {
      valPar = chiave.substring(chiave.indexOf("=") + 1);
      valPar = "T" + valPar.substring(valPar.indexOf(":"));
      result.add(valPar);
    }

    return result;
  }

  /**
   * Compone la condizione di where da mettere in sessione
   *
   * @param page
   *            Contesto della pagina
   * @param entitaParam
   *            Entitï¿½ della pagina
   * @param popUpId
   *            ID del pop-up
   *
   * @return Stringa composta della condizione di where
   */
  private String composeWhere(final PageContext page, final int popUpId, final List<String> chiavi) {
    String where = UtilityTags.getAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA);
    if (StringUtils.isNotEmpty(where)) {
      where += " AND ";
    } else {
      where = "";
    }

    final List<String> whereKeyTemp = new ArrayList<String>();
    for (int i = 0; i < chiavi.size(); i++) {
      final String chiave = String.format(WHERE_KEY_TEMPLATE, i + 1);
      whereKeyTemp.add(chiave);
    }
    where += StringUtils.join(whereKeyTemp, " AND ") + " AND G_NOTEAVVISI.NOTEENT = ?";

    return where;
  }

  /**
   * Compone i parametri da aggiungere in sessione
   *
   * @param page
   *            Contesto della pagina
   * @param entitaParam
   *            Entitï¿½ della query
   * @param popUpId
   *            ID del pop-up
   * @param chiavi
   *            Lista delle chiavi
   *
   * @return Stringa composta dai parametri
   */
  private String composeParams(final PageContext page, final String entitaParam, final int popUpId, final List<String> chiavi) {
    String params = UtilityTags.getAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA);
    if (StringUtils.isNotEmpty(params) ) {
      params += ";";
    } else {
      params = "";
    }

    params += StringUtils.join(chiavi, ";") + ";T:" + entitaParam;

    return params;
  }

  /**
   * Compone la variabile "addKeyRiga"
   *
   * @param chiavi
   *            Lista delle chiavi estratte dal parametro passato alla pagina
   *
   * @return Stringa composta della variabile da inserire nella pagina
   */
  private String composeAddKeyRiga(final List<String> chiavi) {
    final List<String> chiaviTemp = new ArrayList<String>();
    for (int i = 0; i < chiavi.size(); i++) {
      final String element = chiavi.get(i);
      final String chiave = String.format(CAMPI_KEY_TEMPLATE, i + 1, element.substring(element.indexOf(":") + 1));
      chiaviTemp.add(chiave);
    }

    return StringUtils.join(chiaviTemp, ";");
  }

}
