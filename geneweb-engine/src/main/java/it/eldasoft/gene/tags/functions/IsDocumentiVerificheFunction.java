/*
 * Created on 25-03-2020
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
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione per l'inizializzazione della lista delle verifiche
 *
 * @author Cristian Febasi
 */
public class IsDocumentiVerificheFunction extends AbstractFunzioneTag {

  public IsDocumentiVerificheFunction() {
    super(2, new Class[] { PageContext.class, String.class });
  }

  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String isdoc = "0";
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", pageContext, SqlManager.class);

    String idVerifica = (String) params[1];
    idVerifica = UtilityStringhe.convertiNullInStringaVuota(idVerifica);
    String selDocVerifiche="select count(*) from documenti_verifiche where id_verifica = ?";

    try {
      if(!"".equals(idVerifica)){

        Long countDoc = (Long) sqlManager.getObject(selDocVerifiche, new Object[]{new Long(idVerifica)});
        if(!new Long(0).equals(countDoc)){
          isdoc = "1";
        }
        pageContext.setAttribute("isdoc", isdoc, PageContext.REQUEST_SCOPE);

      }
    } catch (SQLException e) {
      throw new JspException("Errore nell'estrarre il codice impresa della verifica con id = " + idVerifica, e);
    }


    return isdoc;
  }

}