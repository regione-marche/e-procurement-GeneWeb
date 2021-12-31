/*
 * Created on 21/nov/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.trova;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.sql.sqlparser.JdbcWhere;

import javax.servlet.ServletRequest;

/**
 * Classe per l'implementazione del trova per la modifica sui filtri
 * 
 * @author Marco.Franceschin
 * 
 */
public abstract class AbstractGestoreTrova {

  private ServletRequest request;
  
  public AbstractGestoreTrova(ServletRequest request) {
    this.request=request;
  }
  

  
  /**
   * @return the request
   */
  public ServletRequest getRequest() {
    return request;
  }



  /**
   * Funzione che gestisce il filtro su un campo. Se viene gestito deve restituire true aggiungendo l'eventuale parte sulla WHERE. 
   * @param where Where in cui aggiungere il confromto
   * @param colWithValue Colonna con valore da aggiungere
   * @param conf Confronto
   * @param manager SqlManager
   * @return true Se gestito; false se non gestito
   */
  abstract public boolean gestisciCampo(JdbcWhere where, DataColumn colWithValue, String conf, SqlManager manager);



  /**
   * Funzione chemata dopo l'impostazione della where
   * @param where Where da modificare eventualmente
   * @param caseSensitive Flag per dire che è case sensitive
   */
  abstract public void postWhere(JdbcWhere where, boolean caseSensitive);

}
