/*
 * Created on 28-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao.ibatis;

import java.util.HashMap;
import java.util.List;

import org.springframework.dao.DataAccessException;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.dao.TabellatiDao;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.TabellatoWsdm;

/**
 * Classe di appoggio per l'esecuzione tramite estensione del framework Spring
 * di interazioni con i tabellati tramite iBatis.
 *
 * @author Luca.Giacomazzo
 */
public class SqlMapTabellatiDao extends SqlMapClientDaoSupportBase implements
    TabellatiDao {

  /**
   * Funzione che estrae il tipo in funzione del tabellato
   *
   * @param codiceTabellato
   *        Codice del tabellato da estrarre
   * @return Lista con codice
   */
  @Override
  public List<Tabellato> getTabellati(String codiceTabellato) {
    return getTabellati(codiceTabellato,
        TabellatiManager.getNumeroTabellaByCodice(codiceTabellato));
  }

  /**
   * Funzione che estrae l'elenco dei tabellati conoscendo la tabella in cui è
   * tabellato
   *
   * @param codiceTabellato
   *        Codice del tabellato
   * @param numeroTabella
   *        Numero della tabella. Di default è la tabella 1
   * @return Lista con l'estrazione dei dati sul tabellato
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<Tabellato> getTabellati(String codiceTabellato, int numeroTabella) {
    switch (numeroTabella) {
    case 0: // Tabella 0
      return getSqlMapClientTemplate().queryForList("getTabellatoTab0",
          codiceTabellato);
    case 2: // Tabella 2
      return getSqlMapClientTemplate().queryForList("getTabellatoTab2",
          codiceTabellato);
    case 3: // Tabella 3
      return getSqlMapClientTemplate().queryForList("getTabellatoTab3",
          codiceTabellato);
    case 5: // Tabella 5
      return getSqlMapClientTemplate().queryForList("getTabellatoTab5",
          codiceTabellato);
    }
    // Di default si tratta della tab 1
    return getSqlMapClientTemplate().queryForList("getTabellatoTab1",
        codiceTabellato);
  }

  /*
   * @see it.eldasoft.gene.db.dao.TabellatiDao#getTabellato(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public Tabellato getTabellato(String codiceTabellato, String valoreTabellato) {
    @SuppressWarnings("rawtypes")
    HashMap<String, Comparable> map = new HashMap<String, Comparable>();

    map.put("tabellatoCodice", codiceTabellato);
    switch (TabellatiManager.getNumeroTabellaByCodice(codiceTabellato)) {
    case 0:
      map.put("valoreTabellato", valoreTabellato);
      return (Tabellato) getSqlMapClientTemplate().queryForObject(
          "getRigaTabellatoTab0", map);
    case 1:
      //map.put("valoreTabellato", valoreTabellato);
      map.put("valoreTabellato",new Integer(valoreTabellato));
      return (Tabellato) getSqlMapClientTemplate().queryForObject(
          "getRigaTabellatoTab1", map);
    case 2:
      map.put("valoreTabellato", valoreTabellato);
      return (Tabellato) getSqlMapClientTemplate().queryForObject(
          "getRigaTabellatoTab2", map);
    case 3:
      map.put("valoreTabellato", valoreTabellato);
      return (Tabellato) getSqlMapClientTemplate().queryForObject(
          "getRigaTabellatoTab3", map);
    case 5:
      map.put("valoreTabellato", valoreTabellato);
      return (Tabellato) getSqlMapClientTemplate().queryForObject(
          "getRigaTabellatoTab5", map);
    }
    return null;
  }

  /*
   * @see it.eldasoft.gene.db.dao.TabellatiDao#getTabellato(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public String getDescrTabellato(String codiceTabellato, String valoreTabellato) {
    String descrizione = null;

    Tabellato tab = this.getTabellato(codiceTabellato, valoreTabellato);
    if (tab != null) descrizione = tab.getDescTabellato();

    return descrizione;
  }

  @Override
  public String getDescrSupplementare(String codiceTabellato, String valoreTabellato) {
    String descrizione = null;

    Tabellato tab = this.getTabellato(codiceTabellato, valoreTabellato);
    if (tab != null) descrizione = tab.getDatoSupplementare();

    return descrizione;
  }

  /*
   * @see it.eldasoft.gene.db.dao.TabellatiDao#getElencoTabellati(java.lang.String)
   */
  @Override
  @SuppressWarnings("unchecked")
  public List<Tabellato> getElencoTabellati(String codiceTabellato)
      throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("codiceTabellato", this.convertiValoreConEscape(hash, codiceTabellato, "escapeCodiceTabellato") + "%");
    return getSqlMapClientTemplate().queryForList("getElencoTabellati",
        hash);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<String> getCampiTabellati(String schemaViste,String entita) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("formato1", "NO_EDIT");
    hash.put("formato2", "NOEDIT");
    hash.put("schemaViste", "%." + this.convertiValoreConEscape(hash, schemaViste, "escapeSchemaViste"));
    hash.put("entita", "%." + this.convertiValoreConEscape(hash, entita, "escapeEntita"));
    return getSqlMapClientTemplate().queryForList("getCampiTabellati", hash);
  }

  @Override
  public void updateDescTabellato(String valoreCampo,String valoreChiave1,String valoreChiave2,String tabellato)throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    if (tabellato.equals("TAB1")) {
        hash.put("valoreCampo", valoreCampo);
        hash.put("valoreChiave1", valoreChiave1);
        hash.put("valoreChiave2", valoreChiave2);
        hash.put("tabella", new String(tabellato));
        this.getSqlMapClientTemplate().update("updateDescTabellatoTab1",hash);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<TabellatoWsdm> getTabellatiWsdm(Long idconfi, String sistema, String codice) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idconfi", idconfi);
    hash.put("sistema", sistema);
    hash.put("codice", codice);
    return getSqlMapClientTemplate().queryForList("getTabellatiWsdm", hash);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<TabellatoWsdm> getTabellatiFromIdconfiCftab(Long idconfi, String cftab) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idconfi", idconfi);
    hash.put("cftab", cftab);
    return getSqlMapClientTemplate().queryForList("getTabellatiFromIdconfiCftab", hash);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Tabellato> getElencoTabellatiWsdm(String codapp, String sistema, Long idconfi) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("codapp", codapp);
    hash.put("sistema", sistema);
    hash.put("idconfi", idconfi);
    return getSqlMapClientTemplate().queryForList("getElencoTabellatiWsdm", hash);
  }
}