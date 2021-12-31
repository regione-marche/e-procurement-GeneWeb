/*
 * Created on 2-ott-2009
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
import it.eldasoft.gene.db.dao.UffintDao;
import it.eldasoft.gene.db.domain.UfficioIntestatario;

import java.util.HashMap;
import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Classe di appoggio per l'esecuzione tramite estensione del framework Spring
 * di interazioni con la tabella UFFINT tramite iBatis.
 * 
 * @author Stefano.Sabbadin
 */
public class SqlMapUffintDao extends SqlMapClientDaoSupportBase implements
    UffintDao {

  /**
   * Metodo per estrarre la lista di tutti gli uffici intestatari
   */
  public List<?> getUfficiIntestatari() throws DataAccessException {
    return this.getSqlMapClientTemplate().queryForList("getUfficiIntestatari");
  }

  public UfficioIntestatario getUfficioIntestatarioByPK(String codice)
      throws DataAccessException {
    return (UfficioIntestatario) this.getSqlMapClientTemplate().queryForObject(
        "getUfficioIntestatarioByPK", codice);
  }

  /**
   * Metodo per estrarre la lista degli uffici intestatari a cui un account e'
   * associato
   * 
   * @param idAccount
   */
  public List<?> getUfficiIntestatariAccount(int idAccount)
      throws DataAccessException {
    return this.getSqlMapClientTemplate().queryForList(
        "getUfficiIntestatariAccount", new Integer(idAccount));
  }

  public int deleteUfficiAccount(Integer idAccount) throws DataAccessException {
    return this.getSqlMapClientTemplate().delete("deleteUfficiAccount",
        idAccount);
  }

  public int deleteUfficiNonAssociatiAccount(int idAccount,
      List<?> listaUfficiAssociati) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("listaUfficiAssociati", listaUfficiAssociati);
    return this.getSqlMapClientTemplate().delete(
        "deleteUfficiNonAssociatiAccount", hash);
  }

  public void insertAssociazioneAccountUfficio(String codUfficio, int idAccount)
      throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("codUfficio", codUfficio);
    hash.put("idAccount", new Integer(idAccount));
    this.getSqlMapClientTemplate().insert("insertAssociazioneAccountUfficio",
        hash);
  }

}