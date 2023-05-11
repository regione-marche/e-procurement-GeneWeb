package it.eldasoft.gene.bl.admin;

import it.eldasoft.gene.db.dao.TecniciDAO;
import it.eldasoft.gene.db.domain.admin.Tecni;

/**
 * Classe di Manager per la gestione delle entita Gare
 * (eg. TORN,GARE, ... )
 * @author gabriele.nencini
 *
 */
public class TecniciManager {

  private TecniciDAO tecniDao;
  
  /**
   * @return the tecniDao
   */
  public TecniciDAO getTecniDao() {
    return tecniDao;
  }
  
  /**
   * @param tecniDao the tecniDao to set
   */
  public void setTecniDao(TecniciDAO tecniDao) {
    this.tecniDao = tecniDao;
  }

  public Tecni getTecniFullByPK(String codtec) {
    return tecniDao.getTecniFullByPK(codtec);
  }
}
