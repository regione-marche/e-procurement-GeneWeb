/*
 * Created on 11/giu/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.bl.integrazioni;

import it.eldasoft.gene.bl.SqlManager;

import java.sql.SQLException;
import java.util.List;


public class WsdmConfigManager {
  
  private SqlManager          sqlManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }
  
  public Long getWsdmConfigurazione(String uffint, String codapp) throws SQLException {
    
    Long codiceConfi = null;
    try {
      if(!"".equals(uffint) && uffint != null){
        String select="select id from wsdmconfi c, WSDMCONFIUFF u where u.idconfi = c.id and u.codein = ? and c.codapp = ?";
        codiceConfi = (Long) sqlManager.getObject(select, new Object[]{uffint,codapp});
      }
      if("".equals(codiceConfi) || codiceConfi == null){
        List<?> listaConfDefault = sqlManager.getListVector("select id from wsdmconfi wc where codapp = ? and not exists(select * from wsdmconfiuff wu where wu.idconfi = wc.id) order by id asc", new Object[] { codapp });
        if (listaConfDefault != null && listaConfDefault.size() > 0) {
          codiceConfi = (Long) SqlManager.getValueFromVectorParam(listaConfDefault.get(0), 0).getValue();
        }
      }
      
    } catch (SQLException e) {
      throw new SQLException("Errore durante la lettura della configurazione WSDM ", e);
    }
    
    return codiceConfi;
  }
  
}
