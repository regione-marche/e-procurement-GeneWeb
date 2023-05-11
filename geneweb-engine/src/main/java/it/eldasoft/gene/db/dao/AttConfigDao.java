package it.eldasoft.gene.db.dao;

import it.eldasoft.gene.db.domain.admin.AttConfig;

import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Interfaccia DAO per l'accesso ai dati della tabella W_ATT.
 * 
 */
public interface AttConfigDao {

  /**
   * Estrae le configurazioni filtrate per codice applicazione.
   * 
   * @param codapp
   *        codice applicazione
   * @return lista dei bean trigger e della cron expression
   * @throws DataAccessException
   */
  List<AttConfig> getAttConfigByCodapp(String codapp) throws DataAccessException;

  /**
   * Estrae la configurazione filtrata per codice applicazione e chiave.
   * 
   * @param codapp
   * @param chiave
   * @return
   * @throws DataAccessException
   */
  AttConfig getAttConfig(String codapp, String chiave) throws DataAccessException;

  /**
   * Inserisce nella tabella W_ATT la configurazione.
   * 
   * @param codapp
   * @param chiave
   * @param valore
   * @throws DataAccessException
   */
  void insertAttConfig(String codapp, String chiave, String valore) throws DataAccessException;

  /**
   * Aggiorna la configurazione gia' presente nella tabella W_ATT.
   * 
   * @param codapp
   * @param chiave
   * @param valore
   * @throws DataAccessException
   */
  void updateAttConfig(String codapp, String chiave, String valore) throws DataAccessException;

  /**
   * Conteggio della configurazione indicata.
   * 
   * @param codapp
   * @param chiave
   * @return
   * @throws DataAccessException
   */
  Long countAttConfig(String codapp, String chiave) throws DataAccessException;

}