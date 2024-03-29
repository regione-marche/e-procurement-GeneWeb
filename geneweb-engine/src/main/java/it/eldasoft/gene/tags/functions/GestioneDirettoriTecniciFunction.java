/*
 * Created on 3/feb/11
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
import it.eldasoft.gene.tags.utils.SqlSelectTag;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per inizializzare la sezione dei direttori tecnici nella pagina 'Legali e altri soggetti' dell'anagrafica Imprese
 *
 * @author Sara Santi
 */
//Modifiche per cambiamento chiave entit� IMPLEG e IMPDTE
public class GestioneDirettoriTecniciFunction extends AbstractFunzioneTag {

  public GestioneDirettoriTecniciFunction() {
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
      List listaDirettoriTecnici = sqlManager.getListVector(
          "select id, coddte, nomdte, dirini , dirfin, respdich, notdte  from impdte where codimp3 = ? "
              + "order by impdte.id asc", new Object[] { codimp });

      if (listaDirettoriTecnici != null && listaDirettoriTecnici.size() > 0)
        pageContext.setAttribute("listaDirettoriTecniciEsito", listaDirettoriTecnici,
            PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre i direttori tecnici dell'impresa "
          + codimp, e);
    }

    return null;
  }

}
