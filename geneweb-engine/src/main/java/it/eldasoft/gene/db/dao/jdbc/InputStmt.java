/*
 * Created on 12/dic/2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao.jdbc;


/**
 * Classe che wrappa l'sql da eseguire mediante prepared statement ed i parametri di input.
 * 
 * @author Stefano.Sabbadin
 */
public class InputStmt {
  
  /** Query SQL da eseguire con i ? al posto dei parametri. */
  private String querySql;
  
  /** Set di parametri da utilizzare nella query. */
  private ParametroStmt[] parametriSql;
  
  public InputStmt() {
    this.querySql = null;
    this.parametriSql = null;
  }

  /**
   * @return Ritorna sql.
   */
  public String getQuerySql() {
    return querySql;
  }

  
  /**
   * @param sql sql da settare internamente alla classe.
   */
  public void setQuerySql(String sql) {
    this.querySql = sql;
  }

  
  /**
   * @return Ritorna parametriSql.
   */
  public ParametroStmt[] getParametriSql() {
    return parametriSql;
  }

  
  /**
   * @param parametriSql parametriSql da settare internamente alla classe.
   */
  public void setParametriSql(ParametroStmt[] parametriSql) {
    this.parametriSql = parametriSql;
  }
  
  /** Serializza i parametri per la stampa. */
  public String toStringParametriSql() {
    StringBuffer strBuffer = new StringBuffer("[");
    if (this.parametriSql != null && this.parametriSql.length > 0) {
      for (int ii = 0; ii < this.parametriSql.length; ii++) {
        strBuffer.append(this.parametriSql[ii].getValore());
        if (ii < (this.parametriSql.length - 1)) strBuffer.append(", ");
      }
    }
    strBuffer.append("]");
    return strBuffer.toString();
  }
  
}
