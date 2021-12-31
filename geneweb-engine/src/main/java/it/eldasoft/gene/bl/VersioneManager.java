/*
 * Created on 30-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl;

import it.eldasoft.gene.db.dao.VersioneDao;

/**
 * Manager che si occupa di gestire le operazioni di business logic sulla
 * tabella W_APPLICAZIONE (o ELDAVER)
 *
 * @author Stefano.Sabbadin
 */
public class VersioneManager {

  /** Reference al DAO per l'accesso alla tabella W_APPLICAZIONE (o ELDAVER) */
  private VersioneDao versioneDao;

  /**
   * @return Ritorna versioneDao.
   */
  public VersioneDao getVersioneDao() {
    return versioneDao;
  }

  /**
   * @param versioneDao
   *        versioneDao da settare internamente alla classe.
   */
  public void setVersioneDao(VersioneDao versioneDao) {
    this.versioneDao = versioneDao;
  }

  /**
   * Estrae la versione del modulo applicativo in input
   *
   * @param codiceApplicazione
   *        codice applicazione
   * @return versione del modulo applicativo
   */
  public String getVersione(String codiceApplicazione) {
    return this.versioneDao.getVersione(codiceApplicazione);
  }


}
