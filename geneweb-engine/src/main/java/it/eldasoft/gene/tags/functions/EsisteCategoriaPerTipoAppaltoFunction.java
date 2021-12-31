/*
 * Created on 25/01/12
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Funzione che controlla se esistono categorie in archivio per una certa tipologia di appalto 
 * 
 * @author Sara Santi
 */
public class EsisteCategoriaPerTipoAppaltoFunction extends AbstractFunzioneTag {

  public EsisteCategoriaPerTipoAppaltoFunction() {
    super(2, new Class[] { PageContext.class, String.class});
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);
    
    TabellatiManager tabellatiManager = (TabellatiManager) UtilitySpring.getBean("tabellatiManager",
        pageContext, TabellatiManager.class);

    String tipoCategoria = (String) params[1];
    String esito = "false";

    try {
      Long numeroCategorie = (Long) sqlManager.getObject("select count(*) from cais where tiplavg=?",
                    new Object[] {tipoCategoria});
      
      if (numeroCategorie != null && numeroCategorie.longValue()>0)
        esito = "true";

    } catch (SQLException e) {
      throw new JspException(
          "Errore durante i controlli per determinare se esistono categorie per una data tipologia", e);
    }

    //Recupera i valori del tabellato associato al campo 'Tipo appalto' della categoria
    //per riportarli nelle diciture dei radiobutton della lista di selezione 
    pageContext.setAttribute("descTipoCategoria",
        tabellatiManager.getTabellato("G_038"), PageContext.REQUEST_SCOPE);

    return esito;

  }

}
