package it.eldasoft.gene.db.dao;

import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.transaction.TransactionStatus;

public interface SqlDao {

  /**
   * Funzione che esegue una query e restituisce una lista di oggetti di tipo
   * HashMap
   *
   *
   * @param asSql
   *        Sql da eseguire
   * @param param
   *        Parametri d'ingresso
   * @return Lista di oggetti di tipo OrderedMap
   * @throws SQLException
   */
  List getQueryForList(String asSql, Object[] param) throws SQLException;

  /**
   * Funzione che restituisce una lista di vettori partendo da una select
   *
   * @param asSql
   *        Sql da eseguire
   * @param param
   *        Parametri di passaggio
   * @return Lista
   * @throws SQLException
   */
  List getVectorQueryForList(String asSql, Object[] param) throws SQLException;

  /**
   * Funzione che restituisce una lista di vettori partendo da una select
   *
   * @param asSql
   *        Sql da eseguire
   * @param param
   *        Parametri di passaggio
   * @param pageNumber
   *        Pagina di dati da estrarre, 0 in caso di nessuna paginazione
   * @param pageSize numero massimo di record da estrarre
   * @return Lista
   * @throws SQLException
   */
  List<Vector<JdbcParametro>> getVectorQueryForList(String asSql, Object[] param, int pageNumber, int pageSize)
      throws SQLException;

  /**
   * Esegue una query e restituisce una lista di oggetti di tipo "Ordered
   * Map" (LinkedHashMap).
   *
   * @param asSql
   *        Sql da eseguire
   * @param param
   *        Parametri d'ingresso
   * @return Lista di oggetti di tipo OrderedMap
   * @throws SQLException
   */
  List getOrderedMapListFromQuery(String asSql, Object[] param) throws SQLException;

  /**
   * Funzione che esegue una select e restituise un result set di tipo HashMap
   *
   * @param asSql
   *        Sql da eseguire
   * @param param
   *        Parametri
   * @return HashMap
   */
  HashMap getQuery(String asSql, Object[] param) throws SQLException;

  /**
   * Funzione che esegue una select e restituise un result set di tipo Vector
   *
   * @param asSql
   *        Sql da eseguire
   * @param param
   *        Parametri
   * @return Vector
   */
  Vector getVectorQuery(String asSql, Object[] param) throws SQLException;

  /**
   * Funzione che esegue un sql d'aggiornamento sul database
   *
   * @param asSqlUpdate
   *        Sql d'update d'aggiornamento (INSERT, DELETE o UPDATE)
   * @param param
   *        Parametri
   * @return numero di righe sulle quali l'update è andato a buon fine
   * @throws SQLException
   */
  int update(String asSqlUpdate, Object[] param) throws SQLException;

  /**
   * Funzione che esegue un sql d'aggiornamento sul database
   *
   * @param asSqlUpdate
   *        Sql d'update d'aggiornamento (INSERT, DELETE o UPDATE)
   * @param param
   *        Parametri
   * @param numRow
   *        Numero di righe in cui eseguire l'update
   * @return numero di righe sulle quali l'update è andato a buon fine
   * @throws SQLException
   */
  int update(String asSqlUpdate, Object[] param, int numRow)
      throws SQLException;

  /**
   * Inizializzo una transazione
   *
   * @return
   * @throws SQLException
   */
  public TransactionStatus startTransaction() throws SQLException;

  public void commitTransaction(TransactionStatus status) throws SQLException;

  public void rollbackTransaction(TransactionStatus status) throws SQLException;

  /**
   * Funzione per eseguire un altersession o il richiamo di una procedura senza
   * neccessariamente aprire una transazione
   *
   * @param sql
   * @throws SQLException
   */
  public int execute(String sql) throws SQLException;

  /**
   * Esegue la chiamata ad una stored procedure
   *
   * @param storedProcedure
   *        nome della stored procedure
   * @param isFunction
   *        true se è una funzione con un risultato di ritorno, false altrimenti
   *        (anche nel caso di stored procedure che prevede argomenti di output)
   * @param parametersTypes
   *        array di parametri di input (SqlParameter) e output
   *        (SqlOutParameter) nell'ordine esatto in cui sono dichiarati nella
   *        stored procedure
   * @param values
   *        mappa contenente le chiavi dei parametri di input, e i valori di
   *        tali parametri
   * @return mappa con i dati di ritorno
   *
   * @since 1.4.6
   */
  public Map callStoredProcedure(String storedProcedure, boolean isFunction,
      SqlParameter[] parametersTypes, Map values) throws DataAccessException;

}
