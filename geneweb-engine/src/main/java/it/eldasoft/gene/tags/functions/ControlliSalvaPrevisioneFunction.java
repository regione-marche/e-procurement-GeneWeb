/*
 * Creato 22/05/13
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
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
/**
 * Funzione per i controlli che devono essere esguiti all'apertura della popup
 * popup-salva-previsone.jsp
 *
 * @author Marcello Caminiti
 */
public class ControlliSalvaPrevisioneFunction extends AbstractFunzioneTag {

  public ControlliSalvaPrevisioneFunction() {
    super(3, new Class[] {PageContext.class, String.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ent = (String) params[1];
    String chiavi = (String) params[2];
    String select = "select count(id) from g_scadenz where";
    String whereChiavi ="";

    // si esplode la chiave del record da cui partire
    Vector<JdbcParametro> campiChiave = UtilityTags.stringParamsToVector(chiavi, null);
    Object[] valoriChiave = new Object[campiChiave.size()];
    for (int i=0; i<campiChiave.size(); i++) {
      valoriChiave[i]=campiChiave.get(i).getValue();
      whereChiavi += " key" + Integer.toString(i+1) + "='" + valoriChiave[i] + "' and ";
    }

    select += whereChiavi + " ent='" + ent+ "'";

    //Controllo sul consuntivo
    String ulterioreWhere=" and datacons is not null";
    String selectFinale = select + ulterioreWhere;
    try {
      Long countConsuntivo = (Long)sqlManager.getObject(selectFinale, null);
      pageContext.setAttribute("conteggioConsuntivo",
          countConsuntivo, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura del consuntivo per le attività con chiavi " + whereChiavi,e);
    }

    //Controllo sulla previsione
    ulterioreWhere=" and prev=1";
    selectFinale = select + ulterioreWhere;
    try {
      Long countPrevisione = (Long)sqlManager.getObject(selectFinale, null);
      pageContext.setAttribute("conteggioPrevisione",
          countPrevisione, PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nella lettura della previsione per le attività con chiavi " + whereChiavi,e);
    }



    return "";
  }

}