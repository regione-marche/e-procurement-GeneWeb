/*
 * Created on 24/04/12
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
 * Funzione per inizializzare le sezioni collaboratori per la pagina
 * Legali ed altri soggetti
 *
 * @author Marcello Caminiti
 */
public class GestioneCollaboratoriFunction extends AbstractFunzioneTag {

  public GestioneCollaboratoriFunction() {
    super(1, new Class[] { String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String chiave = (String) params[0];

    String codimp = null;

    codimp = chiave.substring(chiave.indexOf(":") + 1);

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    try {

      List listaCollaboratori = sqlManager.getListVector(
          "select CODIMP, NUMCOL, CODTEC, NOMTEC, INCTIP, INCINI, INCFIN, NOTCOL "
              + "from G_IMPCOL where CODIMP = ? order by NUMCOL",
          new Object[] { codimp });
      if (listaCollaboratori != null && listaCollaboratori.size() > 0) {

        pageContext.setAttribute("collaboratori", listaCollaboratori,
            PageContext.REQUEST_SCOPE);

      }
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre i collaboratori "
          + "dell'impresa "
          + codimp, e);
    }
    return null;
  }

}