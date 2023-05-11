/*
 * Created on 25-mag-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.console.db.dao.ibatis;

import it.eldasoft.console.db.dao.SchedRicDao;
import it.eldasoft.console.db.domain.schedric.CodaSched;
import it.eldasoft.console.db.domain.schedric.SchedRic;
import it.eldasoft.console.db.domain.schedric.TrovaCodaSched;
import it.eldasoft.console.db.domain.schedric.TrovaSchedRic;
import it.eldasoft.console.web.struts.schedric.CostantiCodaSched;
import it.eldasoft.console.web.struts.schedric.CostantiSchedRic;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposer;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.sql.comp.SqlManager;
import it.eldasoft.utils.utility.UtilityDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Classe di appoggio per l'esecuzione tramite estensione del framework Spring
 * di interazioni con la tabella W_SCHEDRIC e W_CODASCHED tramite iBatis.
 *
 * @author Francesco.DeFilippis
 */
public class SqlMapSchedRicDao extends SqlMapClientDaoSupportBase implements
    SchedRicDao {

  /**
   * (non-Javadoc)
   *
   * @see it.eldasoft.console.db.dao.SchedRicDao#getSchedulazioniRicerche(it.eldasoft.console.db.domain.schedric.TrovaSchedRic)
   */
  public List<?> getSchedulazioniRicerche(TrovaSchedRic trovaSched)
      throws DataAccessException, SqlComposerException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("tipo", trovaSched.getTipo());
    hash.put("attivo", trovaSched.getAttivo());
    hash.put("idRicerca", trovaSched.getIdRicerca());
    hash.put("operatoreNome", trovaSched.getOperatoreNome());
    //this.setOperatoreMatch(hash, trovaSched.getNome(), "operatoreNome");
    hash.put("nome", trovaSched.getNome());
    hash.put("escapeNome", trovaSched.getEscapeNome());
    hash.put("owner", trovaSched.getOwner());
    hash.put("esecutore", trovaSched.getEsecutore());
    hash.put("tabTipo", CostantiSchedRic.TABELLATO_TIPO_SCHEDRIC);
    hash.put("codiceProfilo", trovaSched.getProfiloOwner());
    hash.put("codiceApplicazione", trovaSched.getCodiceApplicazione());
    if (trovaSched.isNoCaseSensitive()) {
      SqlComposer composer = SqlManager.getComposer(
          ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
      hash.put("operatoreUpper", composer.getFunzioneUpperCase());
      if(trovaSched.getNome() != null)
        hash.put("nome", trovaSched.getNome().toUpperCase());
      else
        hash.put("nome", null);
    }

    // Modifica per il database DB2: la query getModelli fa la UNION di diverse
    // select ed una di esse estrae un campo settandolo sempre a null. DB2 vuole
    // che tale valore sia tipizzato anche se null. In questo caso, si esegue
    // il cast al tipo di dato della colonna che si estrae nelle altre query in
    // union.
    if(it.eldasoft.utils.sql.comp.SqlManager.DATABASE_DB2.equals(
            ConfigManager.getValore(CostantiGenerali.PROP_DATABASE)))
      hash.put("adattaQueryPerDB2", "1");

    return getSqlMapClientTemplate().queryForList("getSchedRic", hash);

  }

  /**
   * @see it.eldasoft.console.db.dao.SchedRicDao#getSchedulazioneRicerca(java.lang.Integer)
   */
  public SchedRic getSchedulazioneRicerca(int idSchedRic)
      throws DataAccessException {
    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("idSchedRic", new Integer(idSchedRic));
    map.put("tabTipo", CostantiSchedRic.TABELLATO_TIPO_SCHEDRIC);
    map.put("tabFormato", CostantiSchedRic.TABELLATO_FORMATO_SCHEDRIC);
    return (SchedRic) getSqlMapClientTemplate().queryForObject(
        "getSchedRicById", map);

  }

  /**
   * @see it.eldasoft.console.db.dao.SchedRicDao#insertSchedulazioneRicerca(it.eldasoft.console.db.domain.schedric.SchedRic)
   */
  public void insertSchedulazioneRicerca(SchedRic schedRic)
      throws DataAccessException {
    this.getSqlMapClientTemplate().insert("insertSchedRic", schedRic);
  }

  /**
   * @see it.eldasoft.console.db.dao.SchedRicDao#updateAttivaDisattivaSchedulazione(int,
   *      int, java.util.Date)
   */
  public void updateAttivaDisattivaSchedulazione(int idSchedRic, int attivo,
      Date dataProxEsec, int ora, int minuti) throws DataAccessException {

    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("idSchedRic", new Integer(idSchedRic));
    map.put("attivo", new Integer(attivo));
    map.put("dataProxEsec", dataProxEsec);
    map.put("ora", new Integer(ora));
    map.put("minuti", new Integer(minuti));
    this.getSqlMapClientTemplate().update("updateAttivaDisattiva", map);
  }

  /**
   * @see it.eldasoft.console.db.dao.SchedRicDao#updateSchedulazioneRicerca(it.eldasoft.console.db.domain.schedric.SchedRic)
   */
  public void updateSchedulazioneRicerca(SchedRic schedRic)
      throws DataAccessException {

//    HashMap map = new HashMap();
//    map.put("idSchedRic", new Integer(schedRic.getIdSchedRic()));
//    map.put("idRicerca", new Integer(schedRic.getIdRicerca()));
//    map.put("tipo", schedRic.getTipo());
//    map.put("attivo", new Integer(schedRic.getAttivo()));
//    map.put("nome", schedRic.getNome());
//    map.put("oraAvvio", new Integer(schedRic.getOraAvvio()));
//    map.put("minutoAvvio", new Integer(schedRic.getMinutoAvvio()));
//    map.put("giorno", schedRic.getGiorno());
//    map.put("dataPrimaEsec", schedRic.getDataPrimaEsec());
//    map.put("settimana", schedRic.getSettimana());
//    map.put("giorniSettimana", schedRic.getGiorniSettimana());
//    map.put("mese", schedRic.getMese());
//    map.put("giorniMese", schedRic.getGiorniMese());
//    map.put("formato", new Integer(schedRic.getFormato()));
//    map.put("email", schedRic.getEmail());
//    map.put("dataUltEsec", schedRic.getDataUltEsec());
//    map.put("dataProxEsec", schedRic.getDataProxEsec());
    this.getSqlMapClientTemplate().update("updateSchedRic", schedRic);
  }

  /**
   * @see it.eldasoft.console.db.dao.SchedRicDao#deleteSchedulazioniRicerche(int[])
   */
  public void deleteSchedulazioniRicerche(int[] id) throws DataAccessException {
    List<Integer> listaId = new ArrayList<Integer>();
    for (int i = 0; i < id.length; i++)
      listaId.add(new Integer(id[i]));
    HashMap<String, List<Integer>> map = new HashMap<String, List<Integer>>();
    map.put("id", listaId);
    getSqlMapClientTemplate().delete("deleteSchedRic", map);
  }

  /**
   * @see it.eldasoft.console.db.dao.CodaSchedDao#getCodaSchedulazioni(it.eldasoft.console.db.domain.schedric.TrovaCodaSched)
   */
  public List<?> getCodaSchedulazioni(TrovaCodaSched trovaCodaSched)
      throws DataAccessException, SqlComposerException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("stato", trovaCodaSched.getStato());
    hash.put("attivo", trovaCodaSched.getNome());
    hash.put("idRicerca", trovaCodaSched.getIdRicerca());
    hash.put("idSchedRic", trovaCodaSched.getIdSchedRic());
    //this.setOperatoreMatch(hash, trovaCodaSched.getNome(), "operatoreNome");
    //hash.put("nome", trovaCodaSched.getNome());
    //this.setOperatoreMatch(hash, trovaCodaSched.getMsg(), "operatoreMsg");
    hash.put("operatoreMsg", trovaCodaSched.getOperatoreMsg());
    hash.put("msg", trovaCodaSched.getMsg());
    hash.put("escapeMsg", trovaCodaSched.getEscapeMsg());
    hash.put("esecutore", trovaCodaSched.getEsecutore());
    hash.put("tabStato", CostantiCodaSched.TABELLATO_STATO_CODASCHED);
    hash.put("profiloOwner", trovaCodaSched.getProfiloOwner());
    hash.put("codiceApplicazione", trovaCodaSched.getCodiceApplicazione());

    // L.G. 12/09/2007: le due date vengono passate alla query per trovare le
    // schedulazioni come stringhe nel formato AAAAMMGG. La query è stata
    // modificata di conseguenza e in funzione dei diversi dbms supportati
    SqlComposer composer = SqlManager.getComposer(ConfigManager.getValore(
        CostantiGenerali.PROP_DATABASE));

    hash.put("dataEsecSuc", UtilityDate.convertiData(
        trovaCodaSched.getDataEsecSuc(), UtilityDate.FORMATO_AAAAMMGG));
    hash.put("dataEsecPrec", UtilityDate.convertiData(
        trovaCodaSched.getDataEsecPrec(), UtilityDate.FORMATO_AAAAMMGG));
    hash.put("operatoreDataEsecSuc", trovaCodaSched.getOperatoreDataEsecSuc());
    hash.put("operatoreDataEsecPrec", trovaCodaSched.getOperatoreDataEsecPrec());
    hash.put("campoCodaSchedDataEsec", composer.getDateAsStringAAAAMMGG("W_CODASCHED.DATA_ESEC"));
    hash.put("dbms", ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
    // L.G. 12/09/2007: fine modifica

    if (trovaCodaSched.isNoCaseSensitive()){
      hash.put("operatoreUpper", composer.getFunzioneUpperCase());
      if(trovaCodaSched.getMsg() != null)
        hash.put("msg", trovaCodaSched.getMsg().toUpperCase());
      else
        hash.put("msg", null);
    }
    return getSqlMapClientTemplate().queryForList("getCodaSched", hash);

  }

  /**
   * @see it.eldasoft.console.db.dao.CodaSchedDao#getSchedulazioneEseguita(int)
   */
  public CodaSched getSchedulazioneEseguita(int id) throws DataAccessException {
    return (CodaSched) getSqlMapClientTemplate().queryForObject(
        "getCodaSchedById", new Integer(id));
  }

  /**
   * @see it.eldasoft.console.db.dao.CodaSchedDao#deleteSchedulazioneEseguita(int)
   */
  public void deleteSchedulazioneEseguita(int id) throws DataAccessException {
    getSqlMapClientTemplate().delete("deleteCodaSched", new Integer(id));
  }

  /**
   * @see it.eldasoft.console.db.dao.SchedRicDao#
   */
  public boolean isSchedulazioneReportSenzaParametri(int idSchedRic) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idSchedRic", new Integer(idSchedRic));
    // Tipo parametro = U <--> Identificativo utente: questo tipo di parametro
    // non viene ma richiesto in fase di esecuzione, ma viene popolato
    // automaticamente con l'ID dell'utente esecutore del report con modello
    // o con l'id dell'utente schedulante
    hash.put("tipoParametro", "U");
    int count = ((Integer)getSqlMapClientTemplate().queryForObject(
        "getSchedulazioneReportSenzaParametri", hash)).intValue();
    boolean result = false;
    if (count != 0)
      result = true;

    return result;
  }

  public List<?> getSchedulazioniPerOrario(Integer oraInMinuti,Date dataEsec, String codiceApplicazione)
  throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("oraInMinuti", oraInMinuti);
    hash.put("dataProxEsec", dataEsec);
    hash.put("codiceApplicazione", codiceApplicazione);
    return getSqlMapClientTemplate().queryForList("getSchedPerOra", hash);
  }

  public void insertCodaSched(CodaSched codaSched)
    throws DataAccessException {
  this.getSqlMapClientTemplate().insert("insertCodaSched", codaSched);
  }

  public void updateDataProxEsecSchedRic(Integer idSchedRic,Date dataProxEsec,int ora, int minuti,Date dataUltEsec)
  throws DataAccessException {

    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("idSchedRic", idSchedRic);
    map.put("dataProxEsec", dataProxEsec);
    map.put("dataUltEsec", dataUltEsec);
    map.put("ora", new Integer(ora));
    map.put("minuti", new Integer(minuti));
    this.getSqlMapClientTemplate().update("updateDataProxEsecSchedRic", map);
  }

  public void updateStatoCodaSched(Integer idCodaSched,Integer stato,String msg,String nomeFile)
  throws DataAccessException {

    HashMap<String, Object> map = new HashMap<String, Object>();
    map.put("idCodaSched", idCodaSched);
    map.put("stato", stato);
    map.put("msg", msg);
    map.put("nomeFile", nomeFile);
    this.getSqlMapClientTemplate().update("updateStatoCodaSched", map);
  }
}
