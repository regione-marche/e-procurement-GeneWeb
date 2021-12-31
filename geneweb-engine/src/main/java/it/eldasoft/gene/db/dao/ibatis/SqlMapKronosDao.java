/*
 * Created on 23/mar/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao.ibatis;

import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.dao.KronosDao;

import java.util.HashMap;
import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Classe di appoggio per l'esecuzione tramite estensione del framework Spring
 * di interazioni tramite iBatis per il reperimento di dati dall'integrazione
 * con KRONOS.
 *
 * @author Stefano.Sabbadin
 */
public class SqlMapKronosDao extends SqlMapClientDaoSupportBase implements
    KronosDao {

  /**
   * {@inheritDoc}
   */
  public String getDatiUtente(Integer id) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("id", id);
    hash.put("termId", "WEBKRONOS");
    hash.put("appId", "WEBCONSOLE");
    return (String) getSqlMapClientTemplate().queryForObject("getDatiUtente", hash);
  }

  public String getTemporalita(String tabella) throws DataAccessException {
    return (String) getSqlMapClientTemplate().queryForObject("getTemporalita",
        tabella);
  }

  public List<?> getCampiJoin(String tabella) throws DataAccessException {
    return getSqlMapClientTemplate().queryForList("getCampiJoin", tabella);
  }

  public List<?> getVariabiliUTE(String prefissoVariabileUtente)
      throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("prefissoVariabile", this.convertiValoreConEscape(hash, prefissoVariabileUtente, "escapePrefissoVariabile") + "%");
    return getSqlMapClientTemplate().queryForList("getVariabiliUTE",
        hash);
  }

  public List<?> getValoriVariabileUTE(String variabileUtente, String idUtente,
      String idRuolo) throws DataAccessException {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("variabileUtente", variabileUtente);
    String[] attributiUtente = new String[3];
    attributiUtente[0] = idUtente;
    attributiUtente[1] = idRuolo;
    attributiUtente[2] = "ALL";
    map.put("listaAttributiUtente", attributiUtente);
    return getSqlMapClientTemplate().queryForList("getValoriVariabileUTE", map);
  }

}
