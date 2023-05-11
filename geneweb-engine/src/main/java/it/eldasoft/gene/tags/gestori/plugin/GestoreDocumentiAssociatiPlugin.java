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

public class GestoreDocumentiAssociatiPlugin extends AbstractGestorePreload {

  // Costanti
  private static final Logger LOGGER = Logger.getLogger(GestoreDocumentiAssociatiPlugin.class);

  private static final String ENTITA = "C0OGGASS";

  private static final String ENTITA_PARAM_NAME = "entita";
  private static final String VALORI_PARAM_NAME = "valori";
  private static final String KEY_PARENT_PARAM_NAME = "keyParent";
  private static final String KEY_ADD_PARAM_NAME = "keyAdd";

  private static final String CAMPI_KEY_TEMPLATE = "C0OGGASS.C0AKEY%d=T:%s";
  private static final String WHERE_KEY_TEMPLATE = "C0OGGASS.C0AKEY%d = ?";

  private static final String ERROR_MSG = "Parametri passati alla pagina alterati. Possibile tentativo di SQL injection:\nParametro 'entità': %s\nParametro 'chiave': %s";

  // Costruttore
  public GestoreDocumentiAssociatiPlugin(BodyTagSupportGene tag) {
    super(tag);
  }

  // Metodi
  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda) throws JspException {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreDocumentiAssociatiPlugin.doBeforeBodyProcessing: inizio metodo");

    final int popUpId = UtilityTags.getNumeroPopUp(page);
    String entitaParam = page.getRequest().getParameter(ENTITA_PARAM_NAME);
    String valoriParam = page.getRequest().getParameter(VALORI_PARAM_NAME);
    final String keyParentParam = page.getRequest().getParameter(KEY_PARENT_PARAM_NAME);

    if (StringUtils.isEmpty(valoriParam)) {
        valoriParam = page.getRequest().getParameter(KEY_ADD_PARAM_NAME);
        entitaParam = keyParentParam.substring(0, keyParentParam.indexOf("."));
    }

    if (!checkParams(page, valoriParam)) {
      final String formattedErrorMsg = String.format(ERROR_MSG, entitaParam, valoriParam);
      LOGGER.error(formattedErrorMsg);
      throw new JspException(formattedErrorMsg);
    }

    // Estraggo i valori
    final List<String> valori = extractCampi(valoriParam);

    UtilityTags.createHashAttributeForSqlBuild(page.getSession(), ENTITA, popUpId);

    // Compongo la stringa where
    final String where = composeWhere(page, popUpId, valori);
    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);

    // Compongo la stringa dei parametri
    final String params = composeParams(page, entitaParam, popUpId, valori);
    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, params);

    // Compongo la variabile addKeyRiga
    final String addKeyRiga = composeAddKeyRiga(valori);
    page.getSession().setAttribute("addKeyRiga", addKeyRiga);

    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreDocumentiAssociatiPlugin.doBeforeBodyProcessing: fine metodo");
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {
    // TODO Auto-generated method stub
  }

  private boolean checkParams(final PageContext page, final String valoriParam) {
    final SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", page, SqlManager.class);

    for (final String valore : valoriParam.split(";")) {
      if (!Pattern.matches(KeyParamValidator.KEY_FORMAT_VALIDATION_REGEX, valore)) {
        return false;
      }

      final String nomeEntita = valore.substring(0, valore.indexOf("."));
      if (!sqlManager.isTable(nomeEntita)) {
        return false;
      }

      final int startIndex = valore.indexOf(".") + 1;
      final int endIndex = valore.indexOf("=");
      final String nomeCampo = valore.substring(startIndex, endIndex);

      if (DizionarioCampi.getInstance().getCampoByNomeFisico(String.join(".", nomeEntita, nomeCampo)) == null) {
        return false;
      }
    }

    return true;
  }

  private List<String> extractCampi(final String valoriParam) {
    final List<String> result = new ArrayList<String>();

    String valPar=null;

    for (final String valore : valoriParam.split(";")) {
      valPar = valore.substring(valore.indexOf("=") + 1);
      valPar = "T" + valPar.substring(valPar.indexOf(":"));
      result.add(valPar);
    }

    return result;
  }

  private String composeWhere(final PageContext page, final int popUpId, final List<String> valori) {
    String where = UtilityTags.getAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA);
    if (StringUtils.isNotEmpty(where)) {
      where += " AND ";
    } else {
      where = "";
    }

    final List<String> whereKeyTemp = new ArrayList<String>();
    for (int i = 0; i < valori.size(); i++) {
      final String valore = String.format(WHERE_KEY_TEMPLATE, i + 1);
      whereKeyTemp.add(valore);
    }
    where += StringUtils.join(whereKeyTemp, " AND ") + " AND C0OGGASS.C0AENT = ?";

    return where;
  }

  private String composeParams(final PageContext page, final String entitaParam, final int popUpId, final List<String> valori) {
    String params = UtilityTags.getAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA);
    if (StringUtils.isNotEmpty(params)) {
      params += ";";
    } else {
      params = "";
    }

    params += StringUtils.join(valori, ";") + ";T:" + entitaParam;

    return params;
  }

  private String composeAddKeyRiga(final List<String> valori) {
    final List<String> valoriTemp = new ArrayList<String>();
    for (int i = 0; i < valori.size(); i++) {
      final String element = valori.get(i);
      final String valore = String.format(CAMPI_KEY_TEMPLATE, i + 1, element.substring(element.indexOf(":") + 1));
      valoriTemp.add(valore);
    }

    return StringUtils.join(valoriTemp, ";");
  }

}
