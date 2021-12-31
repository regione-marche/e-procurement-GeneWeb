/*
 * Created on 01-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao.ibatis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.dao.ProfiliDao;
import it.eldasoft.gene.db.domain.admin.Profilo;

/**
 * Classe di appoggio per l'esecuzione tramite estensione del framework Spring
 * di interazioni con la tabella PROFILI tramite iBatis.
 * 
 * @author Luca.Giacomazzo
 */
public class SqlMapProfiliDao extends SqlMapClientDaoSupportBase
    implements ProfiliDao {

  /**
   * Metodo per estrarre la lista dei profili filtrati per codice applicazione.
   * Se il codice applicazione
   * @param codApp 
   */
  public List<?> getProfiliByCodApp(String codApp) throws DataAccessException {
    
    HashMap<String, Object> hash = new HashMap<String, Object>();
    
    List<String> listaCodApp = new ArrayList<String>();
    if (codApp.indexOf(CostantiGenerali.SEPARATORE_PROPERTIES_MULTIVALORE) >= 0) {
      String[] arrayCodApp = 
            codApp.split(CostantiGenerali.SEPARATORE_PROPERTIES_MULTIVALORE);
      for (int i=0; i < arrayCodApp.length; i++) {
        listaCodApp.add(arrayCodApp[i]);
      }
    } else {
      listaCodApp.add(codApp);
    }
    
    hash.put("listaCodApp", listaCodApp);
    
    return this.getSqlMapClientTemplate().queryForList(
        "getProfiliByCodAppOrderByNome", hash);
  }
  
  /** 
   * Metodo per estrarre il profilo a partire dal codice del profilo (PK)
   * 
   * @param codiceProfilo codice/PK del profilo da estrarre
   * @return Ritorna il profilo a partire dal codice del profilo
   */
  public Profilo getProfiloByPK(String codiceProfilo) throws DataAccessException {
    return (Profilo) this.getSqlMapClientTemplate().queryForObject(
        "getProfiloByPK", codiceProfilo);
  }

  /**
   * Metodo per estrarre la lista degli account associati ad un profilo,
   * filtrando per codice applicazione
   * 
   * @param codiceProfilo codice del profilo
   * @param codApp codice applicazione o modulo attivo
   * @return Ritorna la lista degli account associati ad un profilo, filtrando
   *         per codice applicazione
   */
  public List<?> getAccountProfiloByCodApp(String codiceProfilo, String codApp)
        throws DataAccessException {

    HashMap<String, Object> hash = new HashMap<String, Object>();

    List<String> listaCodApp = new ArrayList<String>();
    if (codApp.indexOf(CostantiGenerali.SEPARATORE_PROPERTIES_MULTIVALORE) >= 0) {
      String[] arrayCodApp = 
            codApp.split(CostantiGenerali.SEPARATORE_PROPERTIES_MULTIVALORE);
      for (int i=0; i < arrayCodApp.length; i++) {
    	listaCodApp.add(arrayCodApp[i]);
      }
    } else {
      listaCodApp.add(codApp);
    }
    
    hash.put("codiceProfilo", codiceProfilo);
    hash.put("listaCodApp", listaCodApp);
    
    return this.getSqlMapClientTemplate().queryForList("getAccountProfiloByCodApp", hash);
  }

  public int deleteAccountNonAssociatiProfilo(String codiceProfilo, String
      codiceApplicazione, List<?> lista) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("codiceProfilo", codiceProfilo);
    hash.put("listaAccountAssociati", lista);
    hash.put("codApp", codiceApplicazione);
    return this.getSqlMapClientTemplate().delete(
        "deleteAccountNonAssociatiProfilo", hash);
  }

  public Map<?,?> getUtentiAssociatiAProfiloAsMap(String codiceProfilo)
      throws DataAccessException {
    return this.getSqlMapClientTemplate().queryForMap("getAccountDiProfilo",
        codiceProfilo, "idAccount", "nome");
  }

  public void insertAssociazioneAccountProfilo(String codiceProfilo,
      int idAccount) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("codiceProfilo", codiceProfilo);
    hash.put("idAccount", new Integer(idAccount));
    this.getSqlMapClientTemplate().insert("insertAssociazioneAccountProfilo",
        hash);
  }

  /** 
   * Metodo per estrarre la lista dei profili a cui un account e' associato
   * @param idAccount
   * @param codApp codice applicazione. Se il codice applicazione e' costituito
   *        da piu' codici applicazione separati da ';', allora tale stringa 
   *        viene splittata per estrarre tutti i profili a cui l'utente e'
   *        associato per ciascun codice applicativo
   *        (Es: per UGC il codice applicazione configurato nel file di property
   *        e' uguale a 'C1;C2;C3' --> cercare tutti i profili a cui l'utente è
   *        associato, filtrando per i tre codici applicativi C1, C2 e C3.
   */
  public List<?> getProfiliUtenteByCodApp(int idAccount, String codApp)
        throws DataAccessException {
    
    List<String> listaCodApp = new ArrayList<String>();
    if(codApp.indexOf(CostantiGenerali.SEPARATORE_PROPERTIES_MULTIVALORE) >= 0){
      String[] arrayCodApp = 
            codApp.split(CostantiGenerali.SEPARATORE_PROPERTIES_MULTIVALORE);
      for(int i=0; i < arrayCodApp.length; i++)
      listaCodApp.add(arrayCodApp[i]);
    } else {
      listaCodApp.add(codApp);
    }
    
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("listaCodApp", listaCodApp);
    
    return this.getSqlMapClientTemplate().queryForList(
        "getProfiliUtenteByCodApp", hash);
  }
  
  public int deleteProfiliNonAssociatiUtente(int idAccount, List<?>
      listaCodiceProfili, String codiceApplicazione) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idAccount", new Integer(idAccount));
    hash.put("listaProfiliAssociati", listaCodiceProfili);
    
    List<String> listaCodApp = new ArrayList<String>();
    if(codiceApplicazione.indexOf(CostantiGenerali.SEPARATORE_PROPERTIES_MULTIVALORE) >= 0){
      String[] arrayCodApp = 
            codiceApplicazione.split(CostantiGenerali.SEPARATORE_PROPERTIES_MULTIVALORE);
      for(int i=0; i < arrayCodApp.length; i++)
      listaCodApp.add(arrayCodApp[i]);
    } else {
      listaCodApp.add(codiceApplicazione);
    }

    hash.put("listaCodApp", listaCodApp);
    
    return this.getSqlMapClientTemplate().delete(
        "deleteProfiliNonAssociatiAccount", hash);
  }

  public void deleteAccountConAssociazioneProfili(Integer idAccount)
          throws DataAccessException {
    this.getSqlMapClientTemplate().delete("deleteAccountConAssociazioneProfili",
        idAccount);
  }
  
  public List<?> getGruppiProfiloByCodApp(String  codiceProfilo, String codApp)
        throws DataAccessException {

    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("codiceProfilo", codiceProfilo);
    List<String> listaCodApp = new ArrayList<String>();
    if (codApp.indexOf(CostantiGenerali.SEPARATORE_PROPERTIES_MULTIVALORE) >= 0) {
      String[] arrayCodApp = 
            codApp.split(CostantiGenerali.SEPARATORE_PROPERTIES_MULTIVALORE);
      for(int i=0; i < arrayCodApp.length; i++)
        listaCodApp.add(arrayCodApp[i]);
    } else {
      listaCodApp.add(codApp);
    }

    hash.put("listaCodApp", listaCodApp);
    
    return this.getSqlMapClientTemplate().queryForList(
        "getGruppiProfiloByCodApp", hash);
  }
}