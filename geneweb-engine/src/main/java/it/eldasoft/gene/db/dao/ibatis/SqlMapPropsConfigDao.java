/*
 * Created on 13-giu-2006
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
import it.eldasoft.gene.db.dao.PropsConfigDao;
import it.eldasoft.gene.db.domain.PropsConfig;

import java.util.HashMap;
import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Classe di appoggio per l'esecuzione tramite estensione del framework Spring
 * di interazioni con la tabella W_CONFIG tramite iBatis.
 * 
 * @author Francesco.DeFilippis
 */
public class SqlMapPropsConfigDao extends SqlMapClientDaoSupportBase implements
    PropsConfigDao {

  /* @see it.eldasoft.gene.db.dao.PropsConfig#getConfigLdap()
   */
  public PropsConfig getProperty(String codiceApplicazione,String chiave) throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("codApp",codiceApplicazione);
    hash.put("chiave",chiave);
    return (PropsConfig)getSqlMapClientTemplate().queryForObject(
        "getConfig",hash);
  }
  

  public List<?> getPropertiesByPrefix(String codapp, String prefix)
      throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("codApp", codapp);
    hash.put("chiave", prefix + "%");
    return getSqlMapClientTemplate().queryForList("getConfigsByPrefix", hash);
  }

  
  public List<?> getPropertiesByCodapp(String codapp)
      throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("codApp", codapp);
    return getSqlMapClientTemplate().queryForList("getConfigsByCodapp", hash);
  }
  
  /*
   * @see it.eldasoft.gene.db.dao.PropsConfig#updateConfigLdap(PropsConfig)
   */
  public void deleteProperties(String codiceApplicazione,String[] chiave) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("codApp",codiceApplicazione);
    hash.put("chiave",chiave);
    this.getSqlMapClientTemplate().delete("deleteConfigs", hash);
  }

  public void insertProperty(PropsConfig property) throws DataAccessException {
    this.getSqlMapClientTemplate().insert("insertConfig", property);
  }

  public void updateProperty(PropsConfig property) throws DataAccessException {
    this.getSqlMapClientTemplate().update("updateConfig", property);
  }
  
  public void deletePropertiesByPrefix(String codapp, String prefix)
      throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("codApp", codapp);
    hash.put("chiave", prefix + "%");
    this.getSqlMapClientTemplate().delete("deleteConfigsByPrefix", hash);
  }

}
