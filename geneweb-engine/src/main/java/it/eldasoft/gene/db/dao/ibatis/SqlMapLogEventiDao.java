/*
 * Created on 10/gen/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao.ibatis;

import java.util.Date;
import java.util.HashMap;

import org.springframework.dao.DataAccessException;

import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.dao.LogEventiDao;
import it.eldasoft.gene.db.domain.LogEvento;


/**
 * Classe di appoggio per l'esecuzione tramite estensione del framework Spring
 * di interazioni con la tabella W_LOGEVENTI tramite iBatis.
 * 
 * @author stefano.sabbadin
 * @since 1.5.4
 */
public class SqlMapLogEventiDao extends SqlMapClientDaoSupportBase implements LogEventiDao {

  /**
   * @see it.eldasoft.gene.db.dao.LogEventiDao#insertLog(it.eldasoft.gene.db.domain.LogEvento)
   */
  public void insertLog(LogEvento logEvento) throws DataAccessException {
    this.getSqlMapClientTemplate().insert("insertLog", logEvento);
  }

  /**
   * @see it.eldasoft.gene.db.dao.LogEventiDao#deleteLogBeforeDate(java.util.Date)
   */
  public void deleteLogBeforeDate(Date data) throws DataAccessException {
    this.getSqlMapClientTemplate().update("deleteLogBeforeDate", data);
  }
  
  /**
   * @see it.eldasoft.gene.db.dao.LogEventiDao#searchLogFromCodOggetto(java.util.String,java.util.String)
   */
  public String searchLogFromCodOggetto(String codice, String oggetto) throws DataAccessException {

      HashMap<String, Comparable> hash = new HashMap<String, Comparable>();
      hash.put("codice", codice);
      hash.put("oggetto", oggetto);
      String sql = (String) getSqlMapClientTemplate().queryForObject(
          "searchLogFromCodOggetto", hash);

      return sql;
  }

}
