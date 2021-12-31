package it.eldasoft.gene.db.dao.ibatis;

import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.dao.AttConfigDao;
import it.eldasoft.gene.db.domain.admin.AttConfig;

import java.util.HashMap;
import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Classe di appoggio per l'esecuzione delle interazioni con la tabella W_ATT.
 * 
 */
public class SqlMapAttConfigDao extends SqlMapClientDaoSupportBase implements AttConfigDao {

  public List<AttConfig> getAttConfigByCodapp(String codapp) throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("codapp", codapp);
    return getSqlMapClientTemplate().queryForList("getAttConfigByCodapp", hash);
  }

  public AttConfig getAttConfig(String codapp, String chiave) throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("codapp", codapp);
    hash.put("chiave", chiave);
    return (AttConfig) getSqlMapClientTemplate().queryForObject("getAttConfig", hash);
  }

  public void insertAttConfig(String codapp, String chiave, String valore) throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("codapp", codapp);
    hash.put("chiave", chiave);
    hash.put("valore", valore);
    this.getSqlMapClientTemplate().insert("insertAttConfig", hash);
  }

  public void updateAttConfig(String codapp, String chiave, String valore) throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("codapp", codapp);
    hash.put("chiave", chiave);
    hash.put("valore", valore);
    this.getSqlMapClientTemplate().update("updateAttConfig", hash, 1);
  }

  public Long countAttConfig(String codapp, String chiave) throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("codapp", codapp);
    hash.put("chiave", chiave);
    return (Long) getSqlMapClientTemplate().queryForObject("countAttConfig", hash);
  }

}
