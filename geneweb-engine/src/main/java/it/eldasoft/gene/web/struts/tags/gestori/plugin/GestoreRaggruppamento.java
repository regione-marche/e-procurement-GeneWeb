/*
 * Created on 04-giu-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Gestore preload per la pagina del raggruppamento
 * 
 * @author Marcello.Caminiti
 */
public class GestoreRaggruppamento extends AbstractGestorePreload {

  public GestoreRaggruppamento(BodyTagSupportGene tag) {
    super(tag);
  }

  // Precarico le occorrenze della RAGIMP solo se si accede alla pagina
  // in visualizzazione e in modifica
  @Override
  public void doBeforeBodyProcessing(PageContext pageContext, String modoAperturaScheda) throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String codimp = "";

    HashMap<String, JdbcParametro> key = UtilityTags.stringParamsToHashMap(
        (String) pageContext.getAttribute(UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA, PageContext.REQUEST_SCOPE), null);
    codimp = ((JdbcParametro) key.get("IMPR.CODIMP")).getStringValue();

    try {
      String select = "select TIPIMP from IMPR where CODIMP = ?";
      Long tipimp = (Long) sqlManager.getObject(select, new Object[] { codimp });
      if (tipimp != null) pageContext.setAttribute("tipoImpr", tipimp.toString(), PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore durante il caricamento dei dati del raggruppamento", e);
    }

    if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))) {

      try {

        String url_gateway = ConfigManager.getValore("art80.ws.url.gateway");
        String gateway_multiuffint = ConfigManager.getValore("art80.gateway.multiuffint");
        if (gateway_multiuffint == null) {
          gateway_multiuffint = "1";
        }
        
        String select = null;
        if ("1".equals(url_gateway) && "1".equals(gateway_multiuffint)) {
          String codein = (String) pageContext.getSession().getAttribute(CostantiGenerali.UFFICIO_INTESTATARIO_ATTIVO);
          select = "SELECT CODIME9, CODDIC, NOMDIC, QUODIC, IMPMAN, CGENIMP, CFIMP, PIVIMP, ART80.STATO,ART80.DATA_RICHIESTA, ART80.DATA_LETTURA, ART80.SERVICE "
              + " FROM RAGIMP LEFT OUTER JOIN ART80 ON (RAGIMP.CODDIC = ART80.CODIMP AND ART80.CODEIN = '"
              + codein
              + "'), IMPR "
              + " WHERE RAGIMP.CODIME9 = ? "
              + " AND RAGIMP.CODDIC=IMPR.CODIMP";
        } else {
          select = "select CODIME9, CODDIC, NOMDIC, QUODIC, IMPMAN, CGENIMP, CFIMP, PIVIMP, ART80_STATO, ART80_DATA_RICHIESTA, ART80_DATA_LETTURA, ART80_SERVICE from RAGIMP,IMPR where CODIME9 = ? and coddic=codimp";
        }

        List<?> listaRaggruppamenti = sqlManager.getListVector(select, new Object[] { codimp });
        if (listaRaggruppamenti != null && listaRaggruppamenti.size() > 0) {
          pageContext.setAttribute("listaRaggruppamenti", listaRaggruppamenti, PageContext.REQUEST_SCOPE);

          // Per ogni occorrenza di ragimp si deve ricercare se vi sono
          // note/avvisi
          List<String> listaNoteAvvisi = new ArrayList<String>(listaRaggruppamenti.size());
          String coddic = null;
          Long numNoteAvvisi = new Long(0);
          String selectNoteAvvisi = "select count(*) from g_noteavvisi where noteprg='PG' and noteent='IMPR'"
              + " and statonota = 1 and notekey1 = ? ";
          String flagNoteAvvisi = "";
          for (int i = 0; i < listaRaggruppamenti.size(); i++) {
            coddic = SqlManager.getValueFromVectorParam(listaRaggruppamenti.get(i), 1).getStringValue();
            numNoteAvvisi = (Long) sqlManager.getObject(selectNoteAvvisi, new Object[] { coddic });
            if (numNoteAvvisi > 0)
              flagNoteAvvisi = "1";
            else
              flagNoteAvvisi = "0";
            listaNoteAvvisi.add(flagNoteAvvisi);
          }
          pageContext.setAttribute("listaNoteAvvisi", listaNoteAvvisi, PageContext.REQUEST_SCOPE);
        }

      } catch (SQLException e) {
        throw new JspException("Errore durante il caricamento dei dati del raggruppamento", e);
      }
    }

  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda) throws JspException {
  }

}