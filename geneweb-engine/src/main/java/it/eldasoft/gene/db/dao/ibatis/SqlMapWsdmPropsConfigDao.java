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
import it.eldasoft.gene.db.dao.WsdmPropsConfigDao;
import it.eldasoft.gene.db.domain.WsdmPropsConfig;

import java.util.HashMap;
import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Classe di appoggio per l'esecuzione tramite estensione del framework Spring
 * di interazioni con la tabella W_CONFIG tramite iBatis.
 * 
 * @author Francesco.DeFilippis
 */
public class SqlMapWsdmPropsConfigDao extends SqlMapClientDaoSupportBase implements
  WsdmPropsConfigDao {

  /* @see it.eldasoft.gene.db.dao.PropsConfig#getConfigLdap()
   */
  public WsdmPropsConfig getProperty(Long idconfi,String chiave) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idconfi",idconfi);
    hash.put("chiave",chiave);
    return (WsdmPropsConfig)getSqlMapClientTemplate().queryForObject(
        "getWsdmConfig",hash);
  }
  

  public List<?> getPropertiesByPrefix(Long idconfi, String prefix)
      throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idconfi", idconfi);
    hash.put("chiave", prefix + "%");
    return getSqlMapClientTemplate().queryForList("getWsdmConfigsByPrefix", hash);
  }

  
  public List<?> getPropertiesByCodapp(String codapp)
      throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("codApp", codapp);
    return getSqlMapClientTemplate().queryForList("getWsdmConfigsByCodapp", hash);
  }
  
  /*
   * @see it.eldasoft.gene.db.dao.PropsConfig#updateConfigLdap(PropsConfig)
   */
  public void deleteProperties(Long idconfi,String[] chiave) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idconfi",idconfi);
    hash.put("chiave",chiave);
    this.getSqlMapClientTemplate().delete("deleteWsdmConfigs", hash);
  }

  public void insertProperty(WsdmPropsConfig property) throws DataAccessException {
    this.getSqlMapClientTemplate().insert("insertWsdmConfig", property);
  }

  public void updateProperty(WsdmPropsConfig property) throws DataAccessException {
    this.getSqlMapClientTemplate().update("updateWsdmConfig", property);
  }
  
  public void deletePropertiesByPrefix(String codapp, String prefix)
      throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("idconfi", codapp);
    hash.put("chiave", prefix + "%");
    this.getSqlMapClientTemplate().delete("deleteWsdmConfigsByPrefix", hash);
  }

}
