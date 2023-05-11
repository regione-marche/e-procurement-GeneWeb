package it.eldasoft.gene.tags.gestori.plugin;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.tags.utils.functions.GetUpperCaseDBFunction;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.properties.ConfigManager;

public class GestoreImprListaRaggruppamento extends AbstractGestorePreload {

  // Costanti
  private static final Logger LOGGER = Logger.getLogger(GestoreImprListaRaggruppamento.class);

  private static final String ENTITA = "IMPR";

  // Costruttori
  public GestoreImprListaRaggruppamento(BodyTagSupportGene tag) {
    super(tag);
  }

  // Metodi
  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda) throws JspException {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreImprListaRaggruppamento.doBeforeBodyProcessing: inizio metodo");

    final int popUpId = UtilityTags.getNumeroPopUp(page);
    String nomest = page.getRequest().getParameter("denom");
    String dittao = page.getRequest().getParameter("ditta");
    final String tipoRTI = page.getRequest().getParameter("tipoRTI");
    final String raggruppamentoSelezionato = page.getRequest().getParameter("raggSel");
    final String offertaRT = page.getRequest().getParameter("offertaRT");
    final String codiceDitta = page.getRequest().getParameter("codiceDitta");

    UtilityTags.createHashAttributeForSqlBuild(page.getSession(), ENTITA, popUpId);

    String where = UtilityTags.getAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA);
    String filtro="IMPR.TIPIMP=?";
    String params = "N:"+ tipoRTI;

    if (!"SI".equals(raggruppamentoSelezionato)) {
      String functionUpperCase = "";
      try {
        functionUpperCase = GetUpperCaseDBFunction.getUpperCaseDb();
      } catch (JspException e) {
        LOGGER.error("Errore inaspettato", e);
      }


      if (StringUtils.isNotEmpty(dittao)) {
        dittao = nomest.toUpperCase();
        String  filtroCodimp = functionUpperCase + "( IMPR.CODIMP ) like ?" ;
        filtro += " AND " + filtroCodimp;
        params += ";T:%" + dittao + "%";
      }

      String filtroNomest="";
      if (StringUtils.isNotEmpty(nomest)) {
        nomest = nomest.toUpperCase();
        filtroNomest = functionUpperCase + "( IMPR.NOMEST ) like ?" ;
        filtroNomest += " OR " + functionUpperCase + "( IMPR.CODIMP ) like ?" ;
        filtroNomest = "(" + filtroNomest + ")";

        filtro += " AND " + filtroNomest;
        params += ";T:%" + nomest + "%;T:%" + nomest + "%";
      }

      if("1".equals(offertaRT)) {
        filtro += " AND exists( select codime9 from ragimp where codime9=IMPR.CODIMP and coddic=? and impman='1')";
        params += ";T:" + codiceDitta ;

      }
    }

    final String archiviFiltrati = ConfigManager.getValore("it.eldasoft.associazioneUffintAbilitata.archiviFiltrati");
    final String uffint = (String)page.getSession().getAttribute("uffint");

    if(uffint!=null && !"".equals(uffint) && archiviFiltrati != null && archiviFiltrati.indexOf("IMPR")>=0) {
      filtro += " and CGENIMP = ?";
      params += ";T:" + uffint;
    }

    if (StringUtils.isNotEmpty(where)) {
      where += " AND " + filtro;
    } else {
      where = filtro;
    }

    String whereParams = UtilityTags.getAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA);
    if (StringUtils.isNotEmpty(whereParams)) {
      whereParams += ";";
    } else {
      whereParams = "";
    }
    whereParams += params;

    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_PARAMETRI_DA_TROVA, whereParams);

    UtilityTags.putAttributeForSqlBuild(page.getSession(), ENTITA, popUpId, UtilityTags.DEFAULT_HIDDEN_WHERE_DA_TROVA, where);

    if (LOGGER.isDebugEnabled()) LOGGER.debug("GestoreImprListaRaggruppamento.doBeforeBodyProcessing: fine metodo");
  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {

  }

}
