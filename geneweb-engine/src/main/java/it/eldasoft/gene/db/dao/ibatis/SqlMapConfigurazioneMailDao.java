/*
 * Created on 16-giu-2016
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
import it.eldasoft.gene.db.dao.ConfigurazioneMailDao;
import it.eldasoft.gene.db.domain.system.ConfigurazioneMail;

import java.util.HashMap;
import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Classe di appoggio per l'esecuzione tramite estensione del framework Spring
 * di interazioni con la tabella W_MAIL tramite iBatis.
 *
 * @author Cristian.Febas
 */
public class SqlMapConfigurazioneMailDao extends SqlMapClientDaoSupportBase implements
    ConfigurazioneMailDao {

  public ConfigurazioneMail getConfigurazioneMailByCodappIdcfg(String codapp, String idcfg)
      throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("codapp", codapp);
    hash.put("idcfg", idcfg);
    return (ConfigurazioneMail) getSqlMapClientTemplate().queryForObject("getConfigurazioneMailByCodappIdcfg", hash);
  }

  @SuppressWarnings("unchecked")
  public List<ConfigurazioneMail> getListaConfigurazioneMailByCodapp(String codapp)
      throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("codapp", codapp);
    return getSqlMapClientTemplate().queryForList("getListaConfigurazioneMailByCodapp", hash);
  }

  public void deleteConfigurazioneMail(String codiceApplicazione,String idConfigurazione) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("codapp",codiceApplicazione);
    hash.put("idcfg",idConfigurazione);
    this.getSqlMapClientTemplate().delete("deleteConfigurazioneMail", hash);
  }

  public void insertConfigurazioneMail(ConfigurazioneMail configMail) throws DataAccessException {
    this.getSqlMapClientTemplate().insert("insertConfigurazioneMail", configMail);
  }

  public void updateConfigurazioneMail(ConfigurazioneMail configMail) throws DataAccessException {
    this.getSqlMapClientTemplate().update("updateConfigurazioneMail", configMail);
  }

}
