/*
 * Created on 16/05/11
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
 * Funzione per inizializzare le sezioni delle tecnici associati ad un utente
 * 
 * @author Marcello Caminiti
 */
public class GestioneListaTecniciAccountFunction extends AbstractFunzioneTag {

  public GestioneListaTecniciAccountFunction() {
    super(1, new Class[] { String.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    String syscon = (String) params[0];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    try {
      Long sysconLong = new Long(syscon);
      List listaTecniciAccount = sqlManager.getListVector(
          "select CODTEC, NOMTEC from TECNI where SYSCON = ? order by NOMTEC asc,CODTEC asc", new Object[] { sysconLong });
      
      if (listaTecniciAccount != null && listaTecniciAccount.size() > 0)
        pageContext.setAttribute("listaTecniciAccount", listaTecniciAccount,
            PageContext.REQUEST_SCOPE);
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre i tecnici associati all'utente " + syscon, e);
    }
    
    return null;
  }

}
