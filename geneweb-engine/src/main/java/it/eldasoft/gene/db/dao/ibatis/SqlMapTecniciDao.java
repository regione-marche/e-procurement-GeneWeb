/**
 * 
 */
package it.eldasoft.gene.db.dao.ibatis;

import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.dao.TecniciDAO;
import it.eldasoft.gene.db.domain.admin.Tecni;


/**
 * Implementazione dei metodi per la lettura della tabella TECNI
 * @author gabriele.nencini
 *
 */
public class SqlMapTecniciDao extends SqlMapClientDaoSupportBase implements TecniciDAO {

  @Override
  public Tecni getTecniFullByPK(String codtec) {
    return (Tecni) this.getSqlMapClientTemplate().queryForObject("getTecniFullByPK", codtec);
  }

}
