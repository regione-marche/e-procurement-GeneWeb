/*
 * Created on 25-07-2023
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
import java.util.Arrays;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * La funzione esegue la prepared statement fornita e ritorna il risultato.
 * Il valore ritornato dal db viene convertito in stringa
 * Ad esempio: 'select id from WSDMCONFI where id=?',idconfi
 * Il passaggio attraverso questa funzione permette di validare l'input fornito dall'utente e prevenire potenziali sql injection.
 * La funzione nasce per risolvere la vulnerabilità del campoLista/campoScheda con condizioni where dinamiche 
 * Ad esempio 'where="... = ${param.var}"'
 */
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

public class GetSafeDataFromDB extends AbstractFunzioneTag {

  public GetSafeDataFromDB() {
    super(-1, null);
  }

  /**
   * params[1] : sql
   * params[2+]: parametri
   */
  @Override
  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    String sql = (String) params[1];

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        pageContext, SqlManager.class);

    String ret=null;
    Object valore = null;
    try {
      valore = sqlManager.getObject(sql, Arrays.copyOfRange(params,2,params.length));
      if(valore!=null){
        if(valore instanceof Date){
          Date data = (Date) valore;
          ret = UtilityDate.convertiData(data, UtilityDate.FORMATO_GG_MM_AAAA);
        }else if(valore instanceof Long){
          Long valoreLong = (Long) valore;
          ret = Long.toString(valoreLong.longValue());
        }else if(valore instanceof Double){
          Double valoreDouble = (Double) valore;
          ret = Double.toString(valoreDouble.doubleValue());
        }else{
          ret = (String)valore;
        }

      }
    } catch (SQLException s) {
      throw new JspException("Errore durante la validazione dei parametri forniti", s);
    }


    return ret;
  }

}