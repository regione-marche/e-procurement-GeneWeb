/*
 * Created on 21-lug-2006
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
import it.eldasoft.gene.db.dao.ModelliDao;
import it.eldasoft.gene.db.domain.genmod.CacheParametroComposizione;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genmod.ParametroComposizione;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.gene.db.domain.genmod.TrovaModelli;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposer;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.sql.comp.SqlManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

/**
 * Classe di appoggio per l'esecuzione tramite estensione del framework Spring
 * di interazioni con la tabella W_MODELLI e W_GRPMOD tramite iBatis.
 * 
 * @author Luca.Giacomazzo
 */
public class SqlMapModelliDao extends SqlMapClientDaoSupportBase implements
    ModelliDao {

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#getModelliDiGruppo(int, String)
   */
  public List<?> getModelliDiGruppo(int idGruppo, String codiceApplicazione,
      String codiceProfilo) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idGruppo", new Integer(idGruppo));
    hash.put("codApp", codiceApplicazione);
    hash.put("tabTipoModelli", TabellatiManager.TIPO_MODELLI);
    hash.put("codiceProfilo", codiceProfilo);
    return this.getSqlMapClientTemplate().queryForList("getModelliDiGruppo",
        hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#getAllModelliByCodApp(String)
   */
  public List<?> getAllModelliByCodApp(String codiceApplicazione,
      String codiceProfilo) throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("codApp", codiceApplicazione);
    hash.put("tabTipoModelli", TabellatiManager.TIPO_MODELLI);
    hash.put("codiceProfilo", codiceProfilo);
    return this.getSqlMapClientTemplate().queryForList("getAllModelliByCodApp",
        hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheDao#getModelliAssociatiAGruppo(int,
   *      String)
   */
  public List<?> getModelliAssociatiAGruppo(int idGruppo, String codiceApplicazione)
      throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idGruppo", new Integer(idGruppo));
    hash.put("codApp", codiceApplicazione);
    return this.getSqlMapClientTemplate().queryForList(
        "getModelliAssociatiAGruppo", hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.RicercheModelliDao#getModelliAssociatiAGruppoasMap(int,
   *      String)
   */
  public Map<?,?> getModelliAssociatiAGruppoasMap(int idGruppo,
      String codiceApplicazione, String codiceProfilo)
          throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idGruppo", new Integer(idGruppo));
    hash.put("codApp", codiceApplicazione);
    hash.put("codiceProfilo", codiceProfilo);
    hash.put("tabTipoModelli", TabellatiManager.TIPO_MODELLI);
    return this.getSqlMapClientTemplate().queryForMap("getModelliDiGruppo",
        hash, "idModello", "nomeModello");
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#insertAssociazioneModelloGruppo(int,
   *      int)
   */
  public void insertAssociazioneModelloGruppo(int idGruppo, int idModello)
      throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idGruppo", new Integer(idGruppo));
    hash.put("idModello", new Integer(idModello));
    this.getSqlMapClientTemplate().insert("insertAssociazioneModelloGruppo",
        hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#deleteModelliNonAssociate(int,
   *      String, java.util.List)
   */
  public int deleteModelliNonAssociati(int idGruppo, String codApp, List<?> lista)
      throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idGruppo", new Integer(idGruppo));
    hash.put("codApp", codApp);
    hash.put("listaModelliAssociati", lista);
    return this.getSqlMapClientTemplate().delete("deleteModelliNonAssociati",
        hash);
  }

  /**
   * @throws SqlComposerException 
   * @see it.eldasoft.gene.db.dao.ModelliDao#getModelli(it.eldasoft.gene.db.domain.genmod.TrovaModelli)
   */
  public List<?> getModelli(TrovaModelli trovaModelli) throws DataAccessException, SqlComposerException {
    HashMap<String, Object> map = new HashMap<String, Object>();
    // Imposto i parametri di passaggio per l'estrazione dei dati
    map.put("tipoDocumento", trovaModelli.getTipoDocumento());
    map.put("nomeModello", trovaModelli.getNomeModello());
    //this.setOperatoreMatch(map, trovaModelli.getNomeModello(), "operatoreNome");
    map.put("operatoreNome", trovaModelli.getOperatoreNomeModello());
    map.put("escapeNomeModello", trovaModelli.getEscapeNomeModello());
    map.put("descrModello", trovaModelli.getDescrModello());
    //this.setOperatoreMatch(map,  trovaModelli.getDescrModello(), "operatoreDescrizione");
    map.put("operatoreDescrizione", trovaModelli.getOperatoreDescrModello());
    map.put("escapeDescrModello", trovaModelli.getEscapeDescrModello());
    map.put("fileModello", trovaModelli.getFileModello());
    //this.setOperatoreMatch(map, trovaModelli.getFileModello(), "operatoreFile");
    map.put("operatoreFile", trovaModelli.getOperatoreFileModello());
    map.put("escapeFileModello", trovaModelli.getEscapeFileModello());
    map.put("disponibile", trovaModelli.getDisponibile());
    map.put("idGruppo", trovaModelli.getIdGruppo());
    map.put("codiceApplicazione", trovaModelli.getCodiceApplicazione());
    map.put("codiceProfilo", trovaModelli.getCodiceProfiloAttivo());
    //F.D. 09/03/07 aggiungo al map per la query i due campi per il filtro per i modelli personali
    map.put("owner", trovaModelli.getOwner());
    map.put("personale", trovaModelli.getPersonale());
    map.put("tabTipoModelli", TabellatiManager.TIPO_MODELLI);
    if(trovaModelli.isNoCaseSensitive()){
      SqlComposer composer = SqlManager.getComposer(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
      map.put("operatoreUpper", composer.getFunzioneUpperCase());
      if(trovaModelli.getNomeModello() != null)
        map.put("nomeModello", trovaModelli.getNomeModello().toUpperCase());
      else
        map.put("nomeModello", null);
      if(trovaModelli.getDescrModello() != null)
        map.put("descrModello", trovaModelli.getDescrModello().toUpperCase());
      else
        map.put("descrModello", null);
      if(trovaModelli.getFileModello() != null)
        map.put("fileModello", trovaModelli.getFileModello().toUpperCase());
      else
        map.put("fileModello", null);
    }
    //La lista dei modelli e' costituita da tutti i modelli che non sono dei 
    //prospetti, cioè quelli che non sono usati nei report con modello e devo 
    //quindi filtrare per W_MODELLI.PROSPETTO = 0
    map.put("prospetto", new Integer(0));

    // Modifica per il database DB2: la query getModelli fa la UNION di diverse
    // select ed una di esse estrae un campo settandolo sempre a null. DB2 vuole
    // che tale valore sia tipizzato anche se null. In questo caso, si esegue
    // il cast al tipo di dato della colonna che si estrae nelle altre query in
    // union.
    if(it.eldasoft.utils.sql.comp.SqlManager.DATABASE_DB2.equals(
            ConfigManager.getValore(CostantiGenerali.PROP_DATABASE)))
      map.put("adattaQueryPerDB2", "1");

    return this.getSqlMapClientTemplate().queryForList("getModelli", map);
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#getModelliPredefiniti(int,
   *      java.lang.String, java.lang.String, boolean)
   */
  public List<?> getModelliPredefiniti(int idAccount, String codiceApplicazione,
      String codiceProfilo, String entita, boolean riepilogativo)
      throws DataAccessException {
    String disponibile = "1"; // solo per leggibilita' del codice
    String personale = "1"; // solo per leggibilita' del codice
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("codApp", codiceApplicazione);
    hash.put("codiceProfilo", codiceProfilo);
    hash.put("personale", personale);
    hash.put("disponibile", disponibile);
    hash.put("entita", entita);
    hash.put("tabTipoModelli", TabellatiManager.TIPO_MODELLI);
    hash.put("riepilogativo", (riepilogativo ? null : new Integer(0)));

    //La lista dei modelli e' costituita da tutti i modelli che non sono dei 
    //prospetti, cioè quelli che non sono usati nei report con modello e devo 
    //quindi filtrare per W_MODELLI.PROSPETTO = 0
    hash.put("prospetto", new Integer(0));
    return this.getSqlMapClientTemplate().queryForList("getModelliPredefiniti",
        hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#getModelloById(int)
   */
  public DatiModello getModelloById(int idModello) throws DataAccessException {

    return (DatiModello) this.getSqlMapClientTemplate().queryForObject(
        "getModelloById", new Integer(idModello));
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#getGruppiModello(int)
   */
  public List<?> getGruppiModello(int idModello) throws DataAccessException {
    return this.getSqlMapClientTemplate().queryForList("getGruppiModello",
        new Integer(idModello));
  }

  public List<?> getGruppiModelloPerModifica(int idModello, String codiceProfilo)
      throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idModello", new Integer(idModello));
    hash.put("codiceProfilo", codiceProfilo);
    return this.getSqlMapClientTemplate().queryForList(
        "getModificaGruppiModello", hash);
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#updateModello(it.eldasoft.gene.db.domain.genmod.DatiModello)
   */
  public void updateModello(DatiModello datiModello) throws DataAccessException {
    this.getSqlMapClientTemplate().update("updateModello", datiModello, 1);

  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#insertModello(it.eldasoft.gene.db.domain.genmod.DatiModello)
   */
  public void insertModello(DatiModello datiModello) throws DataAccessException {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 04/09/2006 M.F. Prima Versione
    // ************************************************************
    this.getSqlMapClientTemplate().insert("insertModello", datiModello);
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#updateGruppiModello(int,
   *      java.lang.Integer[], java.lang.String)
   */
  public void updateGruppiModello(int idModello, Integer[] idGruppi, String profiloOwner)
      throws DataAccessException {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("idModello", new Integer(idModello));
    map.put("gruppiAssociati", idGruppi);
    map.put("profiloOwner", profiloOwner);
    this.getSqlMapClientTemplate().delete("deleteGruppiModelloNonAssociati",
        map);
    this.getSqlMapClientTemplate().insert("insertGruppiModelloAssociati", map);
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#deleteModello(int)
   */
  public void deleteModello(int idModello) throws DataAccessException {

    // Elimino tutti i gruppi appartenenti al modello
    this.getSqlMapClientTemplate().delete("deleteGruppiModello",
        new Integer(idModello));
    // Elimino i parametri del modello
    this.getSqlMapClientTemplate().delete("deleteParametri",
        new Integer(idModello));
    // Eseguo l'eliminazione del modello
    // verificando che ne elimini solo 1
    this.getSqlMapClientTemplate().delete("deleteModello",
        new Integer(idModello), 1);
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#deleteGruppiModello(int)
   */
  public void deleteGruppiModello(int idModello) throws DataAccessException {

    // Elimino tutti i gruppi appartenenti al modello
    this.getSqlMapClientTemplate().delete("deleteGruppiModello",
        new Integer(idModello));
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#getIdModelloByNomeFileCodApp(java.lang.String,
   *      java.lang.String)
   */
  public int getIdModelloByNomeFileCodApp(String nomeFile, String codApp) {
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("nomeFile", nomeFile);
    map.put("codApp", codApp);
    Integer i = (Integer) this.getSqlMapClientTemplate().queryForObject(
        "getIdModelloByNomeFileCodApp", map);
    return i.intValue();
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#getParametriModello(int)
   */
  public List<?> getParametriModello(int idModello) throws DataAccessException {
    return this.getSqlMapClientTemplate().queryForList("getParametriModello",
        new Integer(idModello));
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#getParametroModello(int, int)
   */
  public ParametroModello getParametroModello(int idModello, int progressivo)
      throws DataAccessException {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("idModello", new Integer(idModello));
    map.put("progressivo", new Integer(progressivo));
    return (ParametroModello) this.getSqlMapClientTemplate().queryForObject(
        "getParametroModello", map);
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#getNuovoProgressivoParametroModello(int)
   */
  public int getNuovoProgressivoParametroModello(int idModello)
      throws DataAccessException {
    int nuovoProgressivo = 0;

    Integer ultimoProgressivo = (Integer) this.getSqlMapClientTemplate().queryForObject(
        "getMaxProgressivoParametroModello", new Integer(idModello));
    if (ultimoProgressivo != null)
      nuovoProgressivo = ultimoProgressivo.intValue() + 1;

    return nuovoProgressivo;
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#insertParametro(it.eldasoft.gene.db.domain.genmod.ParametroModello)
   */
  public void insertParametro(ParametroModello parametro)
      throws DataAccessException {
    this.getSqlMapClientTemplate().insert("insertParametro", parametro);
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#updateParametro(it.eldasoft.gene.db.domain.genmod.ParametroModello)
   */
  public void updateParametro(ParametroModello parametro)
      throws DataAccessException {
    this.getSqlMapClientTemplate().update("updateParametro", parametro);
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#deleteParametro(int, int)
   */
  public void deleteParametro(int idModello, int progressivo)
      throws DataAccessException {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("idModello", new Integer(idModello));
    map.put("progressivo", new Integer(progressivo));
    this.getSqlMapClientTemplate().delete("deleteParametro", map);
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#deleteParametri(int)
   */
  public void deleteParametri(int idModello)
      throws DataAccessException {
    this.getSqlMapClientTemplate().delete("deleteParametri",
        new Integer(idModello));
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#updateProgressivoParametro(int,
   *      int, int)
   */
  public boolean updateProgressivoParametro(int idModello, int progressivoOld,
      int progressivoNew) throws DataAccessException {
    HashMap<String, Integer> map = new HashMap<String, Integer>();
    map.put("idModello", new Integer(idModello));
    map.put("progressivoOld", new Integer(progressivoOld));
    map.put("progressivoNew", new Integer(progressivoNew));
    int numeroRecordAggiornati = this.getSqlMapClientTemplate().update(
        "updateProgressivoParametro", map);
    return numeroRecordAggiornati == 1;
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#updateDecrementaProgressivoParametri(int,
   *      int)
   */
  public void updateDecrementaProgressivoParametri(int idModello,
      int progressivoInizio) throws DataAccessException {
    HashMap<String, Integer> map = new HashMap<String, Integer>();
    map.put("idModello", new Integer(idModello));
    map.put("progressivoInizio", new Integer(progressivoInizio));
    map.put("progressivoFine", null);
    this.getSqlMapClientTemplate().update(
        "updateDecrementaProgressivoParametri", map);
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#updateDecrementaProgressivoParametri(int,
   *      int, int)
   */
  public void updateDecrementaProgressivoParametri(int idModello,
      int progressivoInizio, int progressivoFine) throws DataAccessException {
    HashMap<String, Integer> map = new HashMap<String, Integer>();
    map.put("idModello", new Integer(idModello));
    map.put("progressivoInizio", new Integer(progressivoInizio));
    map.put("progressivoFine", new Integer(progressivoFine));
    this.getSqlMapClientTemplate().update(
        "updateDecrementaProgressivoParametri", map);
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#updateIncrementaProgressivoParametri(int,
   *      int, int)
   */
  public void updateIncrementaProgressivoParametri(int idModello,
      int progressivoInizio, int progressivoFine) throws DataAccessException {
    HashMap<String, Integer> map = new HashMap<String, Integer>();
    map.put("idModello", new Integer(idModello));
    map.put("progressivoInizio", new Integer(progressivoInizio));
    map.put("progressivoFine", new Integer(progressivoFine));
    this.getSqlMapClientTemplate().update(
        "updateIncrementaProgressivoParametri", map);
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#insertParametriComposizione(it.eldasoft.gene.db.domain.genmod.ParametroComposizione)
   */
  public void insertParametroComposizione(ParametroComposizione parametro)
      throws DataAccessException {
    this.getSqlMapClientTemplate().insert("insertParametroComposizione",
        parametro);
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#deleteParametriComposizione(int)
   */
  public void deleteParametriComposizione(int idSessione)
      throws DataAccessException {
    this.getSqlMapClientTemplate().delete("deleteParametriComposizione",
        new Integer(idSessione));
  }

  /**
   * @see it.eldasoft.gene.db.dao.ModelliDao#updatePersonale(int)
   */
  public void updatePersonale(int idModello, int i)
    throws DataAccessException {
    
    HashMap<String, Integer> map = new HashMap<String, Integer>();
    map.put("idModello", new Integer(idModello));
    map.put("personale", new Integer(i));
    this.getSqlMapClientTemplate().update(
        "updatePersonale", map);
  }

  public void deleteCacheParametriComposizione(int idAccount, int idModello)
      throws DataAccessException {
    HashMap<String, Integer> map = new HashMap<String, Integer>();
    map.put("idAccount", new Integer(idAccount));
    map.put("idModello", new Integer(idModello));
    this.getSqlMapClientTemplate().update("deleteCacheParametriComposizione",
        map);
  }

  public void deleteCacheParametriComposizioneUtente(Integer idAccount)
      throws DataAccessException {
    this.getSqlMapClientTemplate().update(
        "deleteCacheParametriComposizioneUtente", idAccount);
  }

  public void deleteCacheParametriComposizioneModello(int idModello)
      throws DataAccessException {
    this.getSqlMapClientTemplate().update(
        "deleteCacheParametriComposizioneModello", new Integer(idModello));
  }

  public void insertCacheParametroComposizione(
      CacheParametroComposizione cacheParametroComposizione)
      throws DataAccessException {
    this.getSqlMapClientTemplate().insert("insertCacheParametroComposizione",
        cacheParametroComposizione);
  }

  public String getCacheParametroModello(int idAccount, int idModello,
      String codice) throws DataAccessException {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("idAccount", new Integer(idAccount));
    map.put("idModello", new Integer(idModello));
    map.put("codice", codice);
    return (String) this.getSqlMapClientTemplate().queryForObject(
        "getCacheParametroModello", map);
  }

  public Integer getNumeroModelliCollegatiASorgenteReport(int idRicerca)
      throws DataAccessException {
    return (Integer) this.getSqlMapClientTemplate().queryForObject(
        "getNumeroModelliCollegatiASorgenteReport", new Integer(idRicerca));
  }
  
}
