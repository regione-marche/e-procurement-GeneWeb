/*
 * Created on 03/feb/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare le sezioni degli azionisti nella pagina 'Legali e
 * altri soggetti' dell'anagrafica imprese
 *
 * @author Sara Santi
 */
public class GestioneAzionistiFunction extends AbstractFunzioneTag {

  public GestioneAzionistiFunction() {
    super(1, new Class[] { String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String chiave = (String) params[0];

    String codimp = null;

    codimp = chiave.substring(chiave.indexOf(":") + 1);

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    try {

      //Recupera il TIPIMP perchè la sezione degli azionisti non viene visualizzata nel caso di ATI
      Long tipimp = (Long) sqlManager.getObject("select TIPIMP from IMPR where CODIMP = ?", new Object[] { codimp });
      pageContext.setAttribute("tipimp", tipimp, PageContext.REQUEST_SCOPE);

      List listaAzionisti = sqlManager.getListVector(
          "select CODIMP4, CODTEC, NOMTEC, INCAZI, QUOAZI, INIAZI, FINAZI, NOTAZI, "
              + " NUMAZI, RESPDICH from IMPAZI where CODIMP4 = ? order by NUMAZI",
          new Object[] { codimp });
      if (listaAzionisti != null && listaAzionisti.size() > 0) {

        pageContext.setAttribute("azionisti", listaAzionisti,
            PageContext.REQUEST_SCOPE);

      }
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre gli azionisti "
          + "dell'impresa "
          + codimp, e);
    }
    return null;
  }

}