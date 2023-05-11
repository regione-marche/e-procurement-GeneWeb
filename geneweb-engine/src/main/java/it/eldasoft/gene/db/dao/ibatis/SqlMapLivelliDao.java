/*
 * Created on 1-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao.ibatis;

import java.util.List;

import org.springframework.dao.DataAccessException;

import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.dao.LivelliDao;

/**
 * @author Stefano.Sabbadin
 */
public class SqlMapLivelliDao extends SqlMapClientDaoSupportBase implements
    LivelliDao {

  /**
   * @see it.eldasoft.gene.db.dao.LivelliDao#getElencoLivelli()
   */
  public List<?> getElencoLivelli() throws DataAccessException {
    return getSqlMapClientTemplate().queryForList("getElencoLivelli", null);
  }

}
