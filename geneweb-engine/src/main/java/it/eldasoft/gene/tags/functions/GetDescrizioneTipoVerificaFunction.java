/*
 * Created on 06-02-2020
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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per l'inizializzazione delle pagine delle verifiche
 *
 * @author Cristian Febas
 */
public class GetDescrizioneTipoVerificaFunction extends AbstractFunzioneTag {

  public GetDescrizioneTipoVerificaFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);
    String desc = "";

    String chiave = (String) params[1];
    String id = chiave.substring(chiave.indexOf(':') + 1);
    try{
      desc = (String)sqlManager.getObject("select tab2d2 from verifiche,tab2" +
      		" where id = ? and tab2cod = ? and tab2tip = tipo_verifica",
          new Object[]{id,"G_z24"});

    }catch (SQLException e) {
      throw new JspException("Errore nella lettura della tipologia di richiesta/certificazione",e);
    }

    desc = desc + " (ID VERIFICA = "+id+")";

    return desc;

  }

}