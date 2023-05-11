/*
 * Created on 03-lug-2006
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
import it.eldasoft.gene.db.dao.GruppiDao;
import it.eldasoft.gene.db.domain.admin.Gruppo;

import java.util.HashMap;
import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Classe di appoggio per l'esecuzione tramite estensione del framework Spring
 * di interazioni con la tabella W_GRUPPI tramite iBatis.
 * 
 * @author Luca.Giacomazzo
 */
public class SqlMapGruppiDao extends SqlMapClientDaoSupportBase implements GruppiDao {
  
  /**
   * @see it.eldasoft.gene.db.dao.ListaGruppiDao#getGruppiOrderByNome()
   */
  public List<?> getGruppiOrderByNome(String codiceProfilo)
        throws DataAccessException {
    return getSqlMapClientTemplate().queryForList("getGruppiOrderByNome",
        codiceProfilo);
  } 
  
  /** 
   * @see it.eldasoft.gene.db.dao.GruppiDao#getGruppoById(int)
   */
  public Gruppo getGruppoById(int idGruppo) throws DataAccessException {
    return (Gruppo) getSqlMapClientTemplate().queryForObject("getGruppoById", new Integer(idGruppo));
  }

  /**
   * @see it.eldasoft.gene.db.dao.GruppiDao#updateGruppo(it.eldasoft.gene.db.domain.Gruppo)
   */
  public void updateGruppo(Gruppo gruppo) throws DataAccessException {
    getSqlMapClientTemplate().update("updateGruppo", gruppo,1);
  }

  /**
   * @see it.eldasoft.gene.db.dao.GruppiDao#deleteGruppo(int)
   */
  public void deleteGruppo(int idGruppo) throws DataAccessException {
    this.getSqlMapClientTemplate().delete("deleteGruppo", new Integer(idGruppo) ,1);
  }
  
  /**
   * @see it.eldasoft.gene.db.dao.GruppiDao#insertGruppo(it.eldasoft.gene.db.domain.Gruppo)
   */
  public void insertGruppo(Gruppo gruppo, String codiceProfilo) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idGruppo", new Integer(gruppo.getIdGruppo()));
    hash.put("nomeGruppo", gruppo.getNomeGruppo());
    hash.put("descrGruppo", gruppo.getDescrGruppo());
    hash.put("codiceProfilo", codiceProfilo);
    this.getSqlMapClientTemplate().insert("insertGruppo", hash);
  }

  /** 
   * @see it.eldasoft.gene.db.dao.GruppiDao#getIdAccountByIdGruppo(int)
   */
  public List<?> getIdAccountByIdGruppo(int idGruppoIn) throws DataAccessException {
    Integer idGruppo = new Integer(idGruppoIn);
    List<?> listaQuery = this.getSqlMapClientTemplate().queryForList("getIdAccountByIdGruppo", idGruppo);
    return listaQuery;
  }
  
  /** 
   * @see it.eldasoft.gene.db.dao.GruppiDao#getNumeroRicercheByIdGruppoCodApp(int, java.lang.String)
   */
  public int getNumeroRicercheByIdGruppoCodApp(int idGruppo, String codiceApplicazione) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idGruppo", new Integer(idGruppo));
    hash.put("codApp", codiceApplicazione);
    Integer i = (Integer) this.getSqlMapClientTemplate().queryForObject("getNumeroRicercheByIdGruppoCodApp", hash);
    return i.intValue();
  }
  
  /**
   * @see it.eldasoft.gene.db.dao.GruppiDao#getNumeroRicercheByIdGruppoAltriCodApp(int, java.lang.String)
   */
  public int getNumeroRicercheByIdGruppoAltriCodApp(int idGruppo, String codiceApplicazione) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idGruppo", new Integer(idGruppo));
    hash.put("codApp", codiceApplicazione);
    Integer i = (Integer) this.getSqlMapClientTemplate().queryForObject("getNumeroRicercheByIdGruppoAltriCodApp", hash);
    return i.intValue();
  }

  /**
   * @see it.eldasoft.gene.db.dao.GruppiDao#getNumeroModelliByIdGruppoCodApp(int, java.lang.String)
   */
  public int getNumeroModelliByIdGruppoCodApp(int idGruppo,
      String codiceApplicazione) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idGruppo", new Integer(idGruppo));
    hash.put("codApp", codiceApplicazione);
    Integer i = (Integer) this.getSqlMapClientTemplate().queryForObject(
        "getNumeroModelliByIdGruppoCodApp", hash);
    return i.intValue();
  }

  /**
   * @see it.eldasoft.gene.db.dao.GruppiDao#getNumeroModelliByIdGruppoAltriCodApp(int, java.lang.String)
   */
  public int getNumeroModelliByIdGruppoAltriCodApp(int idGruppo,
      String codiceApplicazione) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idGruppo", new Integer(idGruppo));
    hash.put("codApp", codiceApplicazione);
    Integer i = (Integer)this.getSqlMapClientTemplate().queryForObject(
        "getNumeroModelliByIdGruppoAltriCodApp", hash);
    return i.intValue();
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#getGruppiByIdRicerca(int)
   */
  public List<?> getGruppiByIdRicerca(int idRicerca) throws DataAccessException {
    return this.getSqlMapClientTemplate().queryForList("getGruppiByIdRicerca",
        new Integer(idRicerca));
  }

  public List<?> getGruppiConNumeroAssociazioniByCodApp(String codiceProfilo)
          throws DataAccessException {
    return getSqlMapClientTemplate().queryForList(
        "getGruppiConNumeroAssociazioniByCodApp", codiceProfilo);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#getGruppiByIdRicerca(int)
   */
  public List<?> getGruppiByIdModello(int idModello) throws DataAccessException {
    return this.getSqlMapClientTemplate().queryForList("getGruppiByIdModello",
        new Integer(idModello));
  }
}