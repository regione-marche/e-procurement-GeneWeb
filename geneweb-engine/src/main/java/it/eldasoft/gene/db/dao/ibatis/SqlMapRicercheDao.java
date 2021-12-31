/*
 * Created on 20-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao.ibatis;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.dao.RicercheDao;
import it.eldasoft.gene.db.domain.genric.CacheParametroEsecuzione;
import it.eldasoft.gene.db.domain.genric.CampoRicerca;
import it.eldasoft.gene.db.domain.genric.DatiGenRicerca;
import it.eldasoft.gene.db.domain.genric.FiltroRicerca;
import it.eldasoft.gene.db.domain.genric.GiunzioneRicerca;
import it.eldasoft.gene.db.domain.genric.GruppoRicerca;
import it.eldasoft.gene.db.domain.genric.OrdinamentoRicerca;
import it.eldasoft.gene.db.domain.genric.ParametroRicerca;
import it.eldasoft.gene.db.domain.genric.TabellaRicerca;
import it.eldasoft.gene.db.domain.genric.TrovaRicerche;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposer;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.sql.comp.SqlManager;

import java.util.HashMap;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Classe di appoggio per l'esecuzione tramite estensione del framework Spring
 * di interazioni con la tabella W?RICERCHE e W_GRPRIC tramite iBatis.
 * 
 * @author Luca.Giacomazzo
 */
public class SqlMapRicercheDao extends SqlMapClientDaoSupportBase implements
    RicercheDao {

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#getRicercheDiGruppo(int, String)
   */
  public List<?> getRicercheDiGruppo(int idGruppo, String codiceApplicazione,
      String codiceProfilo) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idGruppo", new Integer(idGruppo));
    hash.put("codApp", codiceApplicazione);
    hash.put("tabTipoRicerche", TabellatiManager.TIPO_RICERCHE);
    hash.put("codiceProfilo", codiceProfilo);

    // Modifica per il database DB2: la query getModelli fa la UNION di diverse
    // select ed una di esse estrae un campo settandolo sempre a null. DB2 vuole
    // che tale valore sia tipizzato anche se null. In questo caso, si esegue
    // il cast al tipo di dato della colonna che si estrae nelle altre query in
    // union.
    if(it.eldasoft.utils.sql.comp.SqlManager.DATABASE_DB2.equals(
            ConfigManager.getValore(CostantiGenerali.PROP_DATABASE)))
      hash.put("adattaQueryPerDB2", "1");

    return this.getSqlMapClientTemplate().queryForList("getRicercheDiGruppo",
        hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#getAllRicercheByCodApp(String)
   */
  public List<?> getAllRicercheByCodApp(String codiceApplicazione,
      String codiceProfilo) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("codApp", codiceApplicazione);
    hash.put("tabTipoRicerche", TabellatiManager.TIPO_RICERCHE);
    hash.put("codiceProfilo", codiceProfilo);

    // Modifica per il database DB2: la query getModelli fa la UNION di diverse
    // select ed una di esse estrae un campo settandolo sempre a null. DB2 vuole
    // che tale valore sia tipizzato anche se null. In questo caso, si esegue
    // il cast al tipo di dato della colonna che si estrae nelle altre query in
    // union.
    if(it.eldasoft.utils.sql.comp.SqlManager.DATABASE_DB2.equals(
            ConfigManager.getValore(CostantiGenerali.PROP_DATABASE)))
      hash.put("adattaQueryPerDB2", "1");
   
    return this.getSqlMapClientTemplate().queryForList(
        "getAllRicercheByCodApp", hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#getRicercheAssociateAGruppoasList(int,
   *      String)
   */
  public List<?> getRicercheAssociateAGruppoAsList(int idGruppo,
      String codiceApplicazione, String codiceProfilo)
          throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idGruppo", new Integer(idGruppo));
    hash.put("codApp", codiceApplicazione);
    hash.put("codiceProfilo", codiceProfilo);
    hash.put("tabTipoRicerche", TabellatiManager.TIPO_RICERCHE);
    return this.getSqlMapClientTemplate().queryForList("getRicercheDiGruppo",
        hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#insertAssociazioneRicercaGruppo(int,
   *      int)
   */
  public void insertAssociazioneRicercaGruppo(int idGruppo, int idRicerca)
      throws DataAccessException {
    HashMap<String, Integer> hash = new HashMap<String, Integer>();
    hash.put("idGruppo", new Integer(idGruppo));
    hash.put("idRicerca", new Integer(idRicerca));
    this.getSqlMapClientTemplate().insert("insertAssociazioneRicercaGruppo",
        hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#deleteRicercheNonAssociate(int,
   *      String, java.util.List)
   */
  public int deleteRicercheNonAssociate(int idGruppo, String codApp,
      List<?> ricercheDiGruppo) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idGruppo", new Integer(idGruppo));
    hash.put("codApp", codApp);
    hash.put("listaRicercheAssociate", ricercheDiGruppo);
    return this.getSqlMapClientTemplate().delete("deleteRicercheNonAssociate",
        hash);
  }

  /**
   * @throws SqlComposerException 
   * @see it.eldasoft.gene.db.dao.RicercheDao#getRicerche(it.eldasoft.gene.db.domain.genric.TrovaRicerche)
   */
  public List<?> getRicerche(TrovaRicerche trovaRicerche, boolean mostraReportBase)
      throws DataAccessException, SqlComposerException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("codiceProfilo", trovaRicerche.getProfiloOwner());
    hash.put("codApp", trovaRicerche.getCodiceApplicazione());
    hash.put("idGruppo", trovaRicerche.getIdGruppo());
    hash.put("idTipoRicerca", trovaRicerche.getTipoRicerca());
    hash.put("disponibile", trovaRicerche.getDisponibile());
    //this.setOperatoreMatch(hash, trovaRicerche.getNomeRicerca(), "operatoreNome");
    hash.put("operatoreNome", trovaRicerche.getOperatoreNomeRicerca());
    hash.put("nomeRicerca", trovaRicerche.getNomeRicerca());
    hash.put("escapeNomeRicerca", trovaRicerche.getEscapeNomeRicerca());
    //this.setOperatoreMatch(hash, trovaRicerche.getDescrizioneRicerca(), "operatoreDescrizione");
    hash.put("operatoreDescrizione", trovaRicerche.getOperatoreDescrizioneRicerca());
    hash.put("descrizioneRicerca", trovaRicerche.getDescrizioneRicerca());
    hash.put("escapeDescrizioneRicerca", trovaRicerche.getEscapeDescrizioneRicerca());
    hash.put("tabTipoRicerche", TabellatiManager.TIPO_RICERCHE);
    hash.put("tabFamigliaRicerca", TabellatiManager.FAMIGLIA_RICERCA);
    hash.put("famiglia", trovaRicerche.getFamiglia());
    if(!mostraReportBase)
      hash.put("mostraReportBase", new Integer(CostantiGenRicerche.REPORT_BASE));
    hash.put("owner", trovaRicerche.getOwner());
    hash.put("personale", trovaRicerche.getPersonale());
    if(trovaRicerche.isNoCaseSensitive()){
      SqlComposer composer = SqlManager.getComposer(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
      hash.put("operatoreUpper", composer.getFunzioneUpperCase());
      if(trovaRicerche.getNomeRicerca() != null)
        hash.put("nomeRicerca", trovaRicerche.getNomeRicerca().toUpperCase());
      else
        hash.put("nomeRicerca", null);
      if(trovaRicerche.getDescrizioneRicerca() != null)
        hash.put("descrizioneRicerca", trovaRicerche.getDescrizioneRicerca().toUpperCase());
      else
        hash.put("descrizioneRicerca", null);
    }

    // Modifica per il database DB2: la query getModelli fa la UNION di diverse
    // select ed una di esse estrae un campo settandolo sempre a null. DB2 vuole
    // che tale valore sia tipizzato anche se null. In questo caso, si esegue
    // il cast al tipo di dato della colonna che si estrae nelle altre query in
    // union.
    if(it.eldasoft.utils.sql.comp.SqlManager.DATABASE_DB2.equals(
            ConfigManager.getValore(CostantiGenerali.PROP_DATABASE)))
      hash.put("adattaQueryPerDB2", "1");

    return this.getSqlMapClientTemplate().queryForList("getRicerche", hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#getRicerchePredefinite(int,
   *      java.lang.String)
   */
  public List<?> getRicerchePredefinite(int idAccount, String codiceApplicazione,
      String codiceProfilo, boolean mostraReportBase, boolean mostraReportSql) {
    String disponibile = "1"; // solo per leggibilita' del codice
    String personale = "1"; // solo per leggibilita' del codice
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("codApp", codiceApplicazione);
    hash.put("codiceProfilo", codiceProfilo);
    hash.put("disponibile", disponibile);
    hash.put("personale", personale);
    hash.put("tabTipoRicerche", TabellatiManager.TIPO_RICERCHE);
    if (!mostraReportBase) {
      hash.put("mostraReportBase",new Integer(CostantiGenRicerche.REPORT_BASE));
    }
    if (!mostraReportSql) {
      hash.put("mostraReportSql",new Integer(CostantiGenRicerche.REPORT_SQL));
    }
    
    // Modifica per il database DB2: la query getModelli fa la UNION di diverse
    // select ed una di esse estrae un campo settandolo sempre a null. DB2 vuole
    // che tale valore sia tipizzato anche se null. In questo caso, si esegue
    // il cast al tipo di dato della colonna che si estrae nelle altre query in
    // union.
    if (it.eldasoft.utils.sql.comp.SqlManager.DATABASE_DB2.equals(
            ConfigManager.getValore(CostantiGenerali.PROP_DATABASE)))
      hash.put("adattaQueryPerDB2", "1");

    return this.getSqlMapClientTemplate().queryForList(
        "getRicerchePredefinite", hash);
  }

  /**
   * @throws SqlComposerException 
   * @see it.eldasoft.gene.db.dao.RicercheDao#getRicercheSenzaParametri(it.eldasoft.gene.db.domain.genric.TrovaRicerche)
   */
  public List<?> getRicercheSenzaParametri(TrovaRicerche trovaRicerche, boolean mostraReportBase)
      throws DataAccessException, SqlComposerException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("codApp", trovaRicerche.getCodiceApplicazione());
    hash.put("codiceProfilo", trovaRicerche.getProfiloOwner());
    hash.put("idGruppo", trovaRicerche.getIdGruppo());
    hash.put("idTipoRicerca", trovaRicerche.getTipoRicerca());
    hash.put("disponibile", trovaRicerche.getDisponibile());
    //this.setOperatoreMatch(hash, trovaRicerche.getNomeRicerca(), "operatoreNome");
    hash.put("operatoreNome", trovaRicerche.getOperatoreNomeRicerca());
    hash.put("nomeRicerca", trovaRicerche.getNomeRicerca());
    hash.put("escapeNomeRicerca", trovaRicerche.getEscapeNomeRicerca());
    //this.setOperatoreMatch(hash, trovaRicerche.getDescrizioneRicerca(), "operatoreDescrizione");
    hash.put("operatoreDescrizione", trovaRicerche.getOperatoreDescrizioneRicerca());
    hash.put("descrizioneRicerca", trovaRicerche.getDescrizioneRicerca());
    hash.put("escapeDescrizioneRicerca", trovaRicerche.getEscapeDescrizioneRicerca());
    hash.put("tabTipoRicerche", TabellatiManager.TIPO_RICERCHE);
    hash.put("tabFamigliaRicerca", TabellatiManager.FAMIGLIA_RICERCA);
    hash.put("famiglia", trovaRicerche.getFamiglia());
    if(!mostraReportBase)
      hash.put("mostraReportBase", new Integer(CostantiGenRicerche.REPORT_BASE));
    hash.put("owner", trovaRicerche.getOwner());
    hash.put("personale", trovaRicerche.getPersonale());
    if(trovaRicerche.isNoCaseSensitive()){
      SqlComposer composer = SqlManager.getComposer(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
      hash.put("operatoreUpper", composer.getFunzioneUpperCase());
      if(trovaRicerche.getNomeRicerca() != null)
        hash.put("nomeRicerca", trovaRicerche.getNomeRicerca().toUpperCase());
      else
        hash.put("nomeRicerca", null);
      if(trovaRicerche.getDescrizioneRicerca() != null)
        hash.put("descrizioneRicerca", trovaRicerche.getDescrizioneRicerca().toUpperCase());
      else
        hash.put("descrizioneRicerca", null);
    }
    hash.put("tipoParametro", "U");

    // Modifica per il database DB2: la query getModelli fa la UNION di diverse
    // select ed una di esse estrae un campo settandolo sempre a null. DB2 vuole
    // che tale valore sia tipizzato anche se null. In questo caso, si esegue
    // il cast al tipo di dato della colonna che si estrae nelle altre query in
    // union.
    if(it.eldasoft.utils.sql.comp.SqlManager.DATABASE_DB2.equals(
            ConfigManager.getValore(CostantiGenerali.PROP_DATABASE)))
      hash.put("adattaQueryPerDB2", "1");

    return this.getSqlMapClientTemplate().queryForList("getRicercheSenzaParametri", hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#getRicerchePredefiniteSenzaParametri(int,
   *      java.lang.String)
   */
  public List<?> getRicerchePredefiniteSenzaParametri(int idAccount,
      String codiceApplicazione, String codiceProfilo, boolean mostraReportBase) {
    String disponibile = "1"; // solo per leggibilita' del codice
    String personale = "1"; // solo per leggibilita' del codice
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("codApp", codiceApplicazione);
    hash.put("codiceProfilo", codiceProfilo);
    hash.put("disponibile", disponibile);
    hash.put("personale", personale);
    hash.put("tabTipoRicerche", TabellatiManager.TIPO_RICERCHE);
    hash.put("tipoParametro", "U");
    if(!mostraReportBase)
      hash.put("mostraReportBase",new Integer(CostantiGenRicerche.REPORT_BASE));

    // Modifica per il database DB2: la query getModelli fa la UNION di diverse
    // select ed una di esse estrae un campo settandolo sempre a null. DB2 vuole
    // che tale valore sia tipizzato anche se null. In questo caso, si esegue
    // il cast al tipo di dato della colonna che si estrae nelle altre query in
    // union.
    if(it.eldasoft.utils.sql.comp.SqlManager.DATABASE_DB2.equals(
            ConfigManager.getValore(CostantiGenerali.PROP_DATABASE)))
      hash.put("adattaQueryPerDB2", "1");

    return this.getSqlMapClientTemplate().queryForList(
        "getRicerchePredefiniteSenzaParametri", hash);
  }
  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#getTestataRicercaByIdRicerca(int)
   */
  public DatiGenRicerca getTestataRicercaByIdRicerca(int idRicerca) {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idRicerca", new Integer(idRicerca));
    hash.put("tabTipoRicerche", TabellatiManager.TIPO_RICERCHE);

    // Modifica per il database DB2: la query getModelli fa la UNION di diverse
    // select ed una di esse estrae un campo settandolo sempre a null. DB2 vuole
    // che tale valore sia tipizzato anche se null. In questo caso, si esegue
    // il cast al tipo di dato della colonna che si estrae nelle altre query in
    // union.
    if(it.eldasoft.utils.sql.comp.SqlManager.DATABASE_DB2.equals(
            ConfigManager.getValore(CostantiGenerali.PROP_DATABASE)))
      hash.put("adattaQueryPerDB2", "1");

    return (DatiGenRicerca) this.getSqlMapClientTemplate().queryForObject(
        "getTestataRicercaByIdRicerca", hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#getTabellaRicercaByIdRicerca(int)
   */
  public List<?> getTabelleRicercaByIdRicerca(int idRicerca)
      throws DataAccessException {
    return this.getSqlMapClientTemplate().queryForList(
        "getTabelleRicercaByIdRicerca", new Integer(idRicerca));
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#getCampoRicercaByIdRicerca(int)
   */
  public List<?> getCampiRicercaByIdRicerca(int idRicerca)
      throws DataAccessException {
    return this.getSqlMapClientTemplate().queryForList(
        "getCampiRicercaByIdRicerca", new Integer(idRicerca));
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#getGiunzioneRicercaByIdRicerca(int)
   */
  public List<?> getGiunzioniRicercaByIdRicerca(int idRicerca)
      throws DataAccessException {
    return this.getSqlMapClientTemplate().queryForList(
        "getGiunzioniRicercaByIdRicerca", new Integer(idRicerca));
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#getParametriRicercaByIdRicerca(int)
   */
  public List<?> getParametriRicercaByIdRicerca(int idRicerca)
      throws DataAccessException {
    return this.getSqlMapClientTemplate().queryForList(
        "getParametriRicercaByIdRicerca", new Integer(idRicerca));
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#getFiltroRicercaByIdRicerca(int)
   */
  public List<?> getFiltriRicercaByIdRicerca(int idRicerca)
      throws DataAccessException {
    return this.getSqlMapClientTemplate().queryForList(
        "getFiltriRicercaByIdRicerca", new Integer(idRicerca));
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#getOrdinamentoRicercaByIdRicerca(int)
   */
  public List<?> getOrdinamentiRicercaByIdRicerca(int idRicerca)
      throws DataAccessException {
    return this.getSqlMapClientTemplate().queryForList(
        "getOrdinamentiRicercaByIdRicerca", new Integer(idRicerca));
  }

  public Integer getNumeroParametriRicercaByIdRicerca(int idRicerca) throws DataAccessException {
    return (Integer) this.getSqlMapClientTemplate().queryForObject(
        "getNumeroParametriRicercaByIdRicerca", new Integer(idRicerca));
  }
  
  public Integer getFamigliaRicercaById(int idRicerca) throws DataAccessException {
    return (Integer) this.getSqlMapClientTemplate().queryForObject(
        "getFamigliaRicercaById", new Integer(idRicerca));
  }
  
  public Integer getNumeroParametriProspettoByIdRicerca(int idRicerca) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idRicerca", new Integer(idRicerca));
    // Tipo parametro = U <--> Identificativo utente: questo tipo di parametro
    // non viene ma richiesto in fase di esecuzione, ma viene popolato
    // automaticamente con l'ID dell'utente esecutore del report con modello
    // o con l'id dell'utente schedulante
    hash.put("tipoParametro", "U");
    return (Integer) this.getSqlMapClientTemplate().queryForObject(
        "getNumeroParametriProspettoByIdRicerca", hash);
  }
  
  public Integer getNumeroParametriReportSorgenteProspettoByIdRicerca(int idRicerca) throws DataAccessException {
    return (Integer) this.getSqlMapClientTemplate().queryForObject(
        "getNumeroParametriReportSorgenteProspettoByIdRicerca",
        new Integer(idRicerca));
  }
  
  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#deleteRicercheById(java.util.List)
   */
  public void deleteRicercheById(List<?> idRicerca) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("listaRicerche", idRicerca);
    this.getSqlMapClientTemplate().delete("deleteRicercheById", hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#deleteRuoliByIdRicerca(java.util.List)
   */
  public void deleteGruppiByIdRicerca(List<?> idRicerca)
      throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("listaRicerche", idRicerca);
    this.getSqlMapClientTemplate().delete("deleteGruppiByIdRicerca", hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#deleteTabelleByIdRicerca(java.util.List)
   */
  public void deleteTabelleByIdRicerca(List<?> idRicerca)
      throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("listaRicerche", idRicerca);
    this.getSqlMapClientTemplate().delete("deleteTabelleByIdRicerca", hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#deleteCampiByIdRicerca(java.util.List)
   */
  public void deleteCampiByIdRicerca(List<?> idRicerca) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("listaRicerche", idRicerca);
    this.getSqlMapClientTemplate().delete("deleteCampiByIdRicerca", hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#deleteJoinByIdRicerca(java.util.List)
   */
  public void deleteJoinByIdRicerca(List<?> idRicerca) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("listaRicerche", idRicerca);
    this.getSqlMapClientTemplate().delete("deleteJoinByIdRicerca", hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#deleteFiltriByIdRicerca(java.util.List)
   */
  public void deleteFiltriByIdRicerca(List<?> idRicerca)
      throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("listaRicerche", idRicerca);
    this.getSqlMapClientTemplate().delete("deleteFiltriByIdRicerca", hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#deleteOrdinamentiByIdRicerca(java.util.List)
   */
  public void deleteOrdinamentiByIdRicerca(List<?> idRicerca)
      throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("listaRicerche", idRicerca);
    this.getSqlMapClientTemplate().delete("deleteOrdinamentiByIdRicerca", hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#deleteParametriByIdRicerca(java.util.List)
   */
  public void deleteParametriByIdRicerca(List<?> idRicerca)
      throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("listaRicerche", idRicerca);
    this.getSqlMapClientTemplate().delete("deleteParametriByIdRicerca", hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#insertTabellaRicerca(it.eldasoft.gene.db.domain.genric.TabellaRicerca)
   */
  public void insertTabellaRicerca(TabellaRicerca tabellaRicerca)
      throws DataAccessException {
    this.getSqlMapClientTemplate().insert("insertTabellaRicerca",
        tabellaRicerca);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#insertCampoRicerca(it.eldasoft.gene.db.domain.genric.CampoRicerca)
   */
  public void insertCampoRicerca(CampoRicerca campoRicerca)
      throws DataAccessException {
    this.getSqlMapClientTemplate().insert("insertCampoRicerca", campoRicerca);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#insertGiunzioneRicerca(it.eldasoft.gene.db.domain.genric.GiunzioneRicerca)
   */
  public void insertGiunzioneRicerca(GiunzioneRicerca joinRicerca)
      throws DataAccessException {
    this.getSqlMapClientTemplate().insert("insertGiunzioneRicerca", joinRicerca);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#insertFiltroRicerca(it.eldasoft.gene.db.domain.genric.FiltroRicerca)
   */
  public void insertFiltroRicerca(FiltroRicerca filtroRicerca)
      throws DataAccessException {
    this.getSqlMapClientTemplate().insert("insertFiltroRicerca", filtroRicerca);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#insertParametroRicerca(it.eldasoft.gene.db.domain.genric.ParametroRicerca)
   */
  public void insertParametroRicerca(ParametroRicerca parametroRicerca)
      throws DataAccessException {
    this.getSqlMapClientTemplate().insert("insertParametroRicerca",
        parametroRicerca);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#insertOrdinamentoRicerca(it.eldasoft.gene.db.domain.genric.OrdinamentoRicerca)
   */
  public void insertOrdinamentoRicerca(OrdinamentoRicerca ordinamentoRicerca)
      throws DataAccessException {
    this.getSqlMapClientTemplate().insert("insertOrdinamentoRicerca",
        ordinamentoRicerca);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#insertGruppoRicerca(it.eldasoft.gene.db.domain.admin.Gruppo)
   */
  public void insertGruppoRicerca(GruppoRicerca gruppo)
      throws DataAccessException {
    this.insertAssociazioneRicercaGruppo(gruppo.getIdGruppo(),
        gruppo.getId().intValue());
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#insertTestataRicerca(it.eldasoft.gene.db.domain.genric.DatiGenRicerca,
   *      java.lang.String)
   */
  public void insertTestataRicerca(DatiGenRicerca testataRicerca)
      throws DataAccessException {
    this.getSqlMapClientTemplate().insert("insertTestataRicerca",
        testataRicerca);
    if (testataRicerca.getCodReportWS() != null) {
      Integer numDuplicati = (Integer) this.getSqlMapClientTemplate().queryForObject(
          "getNumReportByCodReportWS", testataRicerca.getCodReportWS());
      if (numDuplicati.intValue() > 1)
        throw new DataIntegrityViolationException("Violazione UNIQUE del campo W_RICERCHE.CODREPORTWS");
    }
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#updatePersonale(int)
   */
  public void updatePersonale(int idRicerca, int i)
    throws DataAccessException {
    
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("idRicerca", new Integer(idRicerca));
    map.put("personale", new Integer(i));
    this.getSqlMapClientTemplate().update(
        "updateProspettoPersonale", map);
  }

  public void deleteCacheParametriEsecuzione(int idAccount, int idRicerca)
      throws DataAccessException {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("idAccount", new Integer(idAccount));
    map.put("idRicerca", new Integer(idRicerca));
    this.getSqlMapClientTemplate().update("deleteCacheParametriEsecuzione", map);
  }

  public void deleteCacheParametriEsecuzioneRicerca(int idRicerca)
      throws DataAccessException {
    this.getSqlMapClientTemplate().update(
        "deleteCacheParametriEsecuzioneRicerca", new Integer(idRicerca));
  }

  public void deleteCacheParametriEsecuzioneUtente(Integer idAccount)
      throws DataAccessException {
    this.getSqlMapClientTemplate().update(
        "deleteCacheParametriEsecuzioneUtente", idAccount);
  }

  public void insertCacheParametroEsecuzione(
      CacheParametroEsecuzione cacheParametroEsecuzione)
      throws DataAccessException {
    this.getSqlMapClientTemplate().insert("insertCacheParametroEsecuzione",
        cacheParametroEsecuzione);
  }

  public String getCacheParametroEsecuzione(int idAccount, int idRicerca,
      String codice) throws DataAccessException {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("idAccount", new Integer(idAccount));
    map.put("idRicerca", new Integer(idRicerca));
    map.put("codice", codice);
    return (String) this.getSqlMapClientTemplate().queryForObject(
        "getCacheParametroEsecuzione", map);
  }

  public Integer getIdRicercaByCodReportWS(String codReportWS)
      throws DataAccessException {
    return (Integer) this.getSqlMapClientTemplate().queryForObject(
        "getIdRicercaByCodReportWS", codReportWS);
  }

}