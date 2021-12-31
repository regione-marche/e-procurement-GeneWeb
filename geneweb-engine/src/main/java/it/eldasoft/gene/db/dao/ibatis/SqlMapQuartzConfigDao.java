package it.eldasoft.gene.db.dao.ibatis;

import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.dao.QuartzConfigDao;
import it.eldasoft.gene.db.domain.admin.QuartzConfig;
import it.eldasoft.utils.sql.comp.SqlManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Classe di appoggio per l'esecuzione delle interazioni con la tabella
 * W_QUARTZ e W_QUARTZLOCK.
 */
public class SqlMapQuartzConfigDao extends SqlMapClientDaoSupportBase implements QuartzConfigDao {

  /** tipologia di DBMS da property */
  private String dbms;

  /**
   * @param dbms
   *            the dbms to set
   */
  public void setDbms(String dbms) {
      this.dbms = dbms;
  }

  @SuppressWarnings("unchecked")
  public List<QuartzConfig> getQuartzConfigByCodapp(String codapp) throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("codapp", codapp);
    return getSqlMapClientTemplate().queryForList("getQuartzConfigByCodapp", hash);
  }

  public boolean isQuartzLock(String codapp, String job) {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("codapp", codapp);
    hash.put("job", job);
    int numRow = getSqlMapClientTemplate().update("isQuartzLock", hash);
    // se si aggiorna in modo fittizio una riga allora vuol dire che esiste, altrimenti non esiste
    return numRow == 1;
  }

  public void insertQuartzLock(String codapp, String job, Date lockDate, String server, String node) {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("codapp", codapp);
    hash.put("job", job);
    hash.put("lockDate", lockDate);
    hash.put("server", server);
    hash.put("node", node);
    getSqlMapClientTemplate().insert("insertQuartzLock", hash);
  }

  public void deleteQuartzLock(String codapp, String job) {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("codapp", codapp);
    hash.put("job", job);
    getSqlMapClientTemplate().delete("deleteQuartzLock", hash, 1);
  }

  public boolean deleteQuartzLockByDate(String codapp, String job, Date maxLockDate) {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("codapp", codapp);
    hash.put("job", job);
    // http://www-01.ibm.com/support/docview.wss?uid=swg21207965
    // le statement sql per sql server funzionano, le prepared statement creano dei deadlock in caso di esecuzione concorrente pertanto si creano gli sql esatti
    String maxLockDateAsString =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(maxLockDate);
    if (SqlManager.DATABASE_SQL_SERVER.equals(this.dbms)) {
      // SQL SERVER
      hash.put("maxLockDate", "CONVERT(DATETIME, '" + maxLockDateAsString + "', 120)");
    } else if (SqlManager.DATABASE_DB2.equals(this.dbms)) {
      // DB2
      hash.put("maxLockDate", "TIMESTAMP_FORMAT('" + maxLockDateAsString + "', 'RRRR-MM-DD HH24:MI:SS')");
    }  else {
      // ORACLE
      // POSTGRESQL
      hash.put("maxLockDate", "TO_DATE('" + maxLockDateAsString + "', 'YYYY-MM-DD HH24:MI:SS')");
    }
    int numRow = getSqlMapClientTemplate().delete("deleteQuartzLockByDate", hash);
    // se e' stata cancellata la riga allora e' stato rimosso il lock
    return numRow == 1;
  }
}
