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
package it.eldasoft.gene.db.dao.ibatis;

import org.springframework.dao.DataAccessException;

import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.dao.VersioneDao;

/**
 * Classe di appoggio per l'esecuzione tramite estensione del framework Spring
 * di interazioni con la tabella W_APPLICAZIONE (o ELDAVER) tramite iBatis.
 *
 * @author Stefano.Sabbadin
 */
public class SqlMapVersioneDao extends SqlMapClientDaoSupportBase implements
    VersioneDao {

  /**
   * @see it.eldasoft.gene.db.dao.VersioneDao#getVersione(java.lang.String)
   */
  public String getVersione(String codiceApplicazione)
      throws DataAccessException {
    return (String) getSqlMapClientTemplate().queryForObject("getVersione",
        codiceApplicazione);
  }

}
