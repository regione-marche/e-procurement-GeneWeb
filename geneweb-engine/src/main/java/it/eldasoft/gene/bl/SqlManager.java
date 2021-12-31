package it.eldasoft.gene.bl;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.dao.SqlDao;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.db.sql.sqlparser.JdbcSqlSelect;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.sql.comp.SqlComposerException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.transaction.TransactionStatus;

/**
 * Manager che si incarica dell'interrogazione generica della banca dati
 *
 * @author marco.franceschin
 */
public class SqlManager {

  private static Logger  logger                       = Logger.getLogger(SqlManager.class);

  private SqlDao         sqlDao;
  private Properties     sqlProperties;
  private static Boolean esisteTabellaStoriaModifiche = null;

  /**
   * Funzione che restituisce un vettore di HashMap
   *
   * @param select
   * @return
   */
  public List getListHashMap(JdbcSqlSelect select) throws SQLException {
    return sqlDao.getQueryForList(select.toString(),
        getObjectFromPram(select.getParemetri()));
  }

  /**
   * Funzione che restituische una lista di vettori
   *
   * @param select
   * @return
   * @throws SQLException
   */
  public List getListVector(JdbcSqlSelect select) throws SQLException {
    return sqlDao.getVectorQueryForList(select.toString(),
        getObjectFromPram(select.getParemetri()));
  }

  /**
   * Funzione che restituische una lista di vettori
   *
   * @param select
   * @param pageNumber
   *        Pagina di dati da estrarre, 0 in caso di nessuna paginazione
   * @param pageSize
   *        Numero massimo di righe estraibili, 0 altrimenti
   * @return
   * @throws SQLException
   */
  public List<Vector<JdbcParametro>> getListVector(JdbcSqlSelect select, int pageNumber, int pageSize)
      throws SQLException {
    return sqlDao.getVectorQueryForList(select.toString(),
        getObjectFromPram(select.getParemetri()), pageNumber, pageSize);
  }

  /**
   * Restituisce un HashMap da una select
   *
   * @param select
   * @return
   * @throws SQLException
   */
  public HashMap getHashMap(JdbcSqlSelect select) throws SQLException {
    return sqlDao.getQuery(select.toString(),
        getObjectFromPram(select.getParemetri()));
  }

  /**
   * Restituisce un vettore partendo da una select
   *
   * @param select
   * @return
   * @throws SQLException
   */
  public Vector getVector(JdbcSqlSelect select) throws SQLException {
    return sqlDao.getVectorQuery(select.toString(),
        getObjectFromPram(select.getParemetri()));
  }

  /**
   * Funzione che restituisce una lista di HashMap
   *
   * @param select
   *        Select da eseguire
   * @param params
   *        Eventuali parametri della select
   * @return Lista di HashMap
   * @throws SQLException
   */
  public List getListHashMap(String select, Object[] params)
      throws SQLException {
    return sqlDao.getQueryForList(select, params);
  }

  /**
   * Funzione che restituisce una lista di HashMap
   *
   * @param select
   *        Select da eseguire
   * @param params
   *        Eventuali parametri della select
   * @return Lista di HashMap
   * @throws SQLException
   */
  public List getListOrderedMap(String select, Object[] params)
      throws SQLException {
    return sqlDao.getOrderedMapListFromQuery(select, params);
  }

  /**
   * Funzione che restituisce una lista di Vettori
   *
   * @param select
   *        Select da eseguire
   * @param params
   *        Eventuali parametri della select
   * @return Lista di HashMap
   * @throws SQLException
   */
  public List getListVector(String select, Object[] params) throws SQLException {
    return sqlDao.getVectorQueryForList(select, params);
  }

  /**
   * Funzione che restituisce la prima riga ritrovata in formato HashMap
   *
   * @param select
   *        Sql di selezione
   * @param params
   *        Parametri d'ingresso
   * @return HashMap con i valori
   * @throws SQLException
   */
  public HashMap getHashMap(String select, Object[] params) throws SQLException {
    return sqlDao.getQuery(select, params);
  }

  /**
   * Funzione che restituisce la prima riga ritrovata in formato Vector
   *
   * @param select
   *        Sql di selezione
   * @param params
   *        Parametri d'ingresso
   * @return Vettore con i valori (di tipo JdbcParam)
   * @throws SQLException
   */
  public Vector getVector(String select, Object[] params) throws SQLException {
    return sqlDao.getVectorQuery(select, params);
  }

  /**
   * Esecuzione di un SQL d'update sul database
   *
   * @param asSqlUpdate
   *        Sql per l'update
   * @param param
   *        Elenco dei parametri
   * @return Numero di righe in cui ha effetto
   * @throws SQLException
   */
  public int update(String asSqlUpdate, Object[] param) throws SQLException {
    return this.sqlDao.update(asSqlUpdate, param);
  }

  /**
   * Esecuzione di un SQL d'update sul database
   *
   * @param asSqlUpdate
   *        Sql per l'update
   * @param param
   *        Elenco dei parametri
   * @param numRow
   *        Numero di righe a cui deve dare effetto (se diverso da un SQL
   *        Exceprion)
   * @return Numero di righe in cui ha effetto
   * @throws SQLException
   */
  public int update(String asSqlUpdate, Object[] param, int numRow)
      throws SQLException {
    return this.sqlDao.update(asSqlUpdate, param, numRow);
  }

  /**
   * @return Returns the sqlDao.
   */
  public SqlDao getSqlDao() {
    return sqlDao;
  }

  /**
   * @param sqlDao
   *        The sqlDao to set.
   */
  public void setSqlDao(SqlDao sqlDao) {
    this.sqlDao = sqlDao;
  }

  /**
   * Trasforma un elenco di parametri in un array di oggetti
   *
   * @param parametri
   * @return
   */
  public static Object[] getObjectFromPram(Vector<JdbcParametro> parametri) {
    Object ret[] = new Object[parametri.size()];
    for (int i = 0; i < parametri.size(); i++) {
      JdbcParametro par = parametri.get(i);
      ret[i] = par.getValue();
    }
    return ret;
  }

  public TransactionStatus startTransaction() throws SQLException {
    return this.sqlDao.startTransaction();
  }

  public void commitTransaction(TransactionStatus status) throws SQLException {
    this.sqlDao.commitTransaction(status);
  }

  public void rollbackTransaction(TransactionStatus status) throws SQLException {
    this.sqlDao.rollbackTransaction(status);
  }

  /**
   * Restituisce il codice per il compositore con il tipo di database
   *
   * @return <b>O</b> Oracle; <b>P</b> PostgreSQL; <b>M</b> SQL Server; <b>D</b>
   *         DB2
   */
  public static char getTipoDBperCompositore() {
    char tipoDB = '*';
    try {
      tipoDB = it.eldasoft.utils.sql.comp.SqlManager.getCodiceDatabasePerCompositore(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
    } catch (SqlComposerException e) {
    }
    return tipoDB;
  }

  public static String getTipoDB() {
    return ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);
  }

  public void setSqlProperties(Resource sqlProperies) {
    this.sqlProperties = new Properties();
    try {
      this.sqlProperties.load(sqlProperies.getInputStream());
    } catch (FileNotFoundException e) {

    } catch (IOException e) {

    }
  }

  /**
   * Converte il nome di una funzione SQL in funzione del database attivo
   *
   * @param function
   *        funzione da convertire
   * @return funzione convertita; se non esiste una codifica, ritorna la
   *         funzione stessa ricevuta come parametro
   */
  public String getDBFunction(String function) {
    String key = "fn.";
    String ret;
    key += function.toLowerCase();
    ret = this.sqlProperties.getProperty(key
        + "."
        + ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
    if (ret != null && ret.length() > 0) return ret;
    // Se non ho trovato la funzione cerco senza definizione del database
    ret = this.sqlProperties.getProperty(key);
    if (ret != null && ret.length() > 0) return ret;
    // Di default restituisco lo stesso nome di funzione
    return function;
  }

  /**
   * Converte il nome di una funzione SQL in funzione del database attivo e
   * sostituisce i parametri nella chiamata
   *
   * @param function
   *        funzione da convertire
   * @param params
   *        elenco degli eventuali parametri da sostituire
   * @return funzione convertita, pronta per l'utilizzo nell'SQL con i parametri
   *         opportunamente sostituiti
   */
  public String getDBFunction(String function, String params[]) {
    String ret = getDBFunction(function);
    ret = MessageFormat.format(ret, (Object[])params);
    return ret;
  }

  /**
   * Esecuzione dell'update facendo il replace dei valori tra #nomePar# sulla
   * stringa SQL
   *
   * @param sql
   *        Sql per l'update con i parametri tra ##
   * @param map
   *        Mappa del tipo JdbcParametri
   * @return
   * @throws SQLException
   */
  public int updateWithReplace(String sql, HashMap<String, JdbcParametro> map) throws SQLException {
    Vector<JdbcParametro> parametri = new Vector<JdbcParametro>();
    sql = UtilityTags.replaceParametri(parametri, sql, map);
    return update(sql, UtilityTags.vectorParamToObjectArray(parametri));
  }

  /**
   * Esecuzione dell'update facendo il replace dei valori tra #nomePar# sulla
   * stringa SQL
   *
   * @param sql
   *        Sql per l'update con i parametri tra ##
   * @param map
   *        Mappa del tipo JdbcParametri
   * @param numrow
   *        Numero di riche che interessano l'update
   * @return
   * @throws SQLException
   */
  public int updateWithReplace(String sql, HashMap<String, JdbcParametro> map, int numrow)
      throws SQLException {
    Vector<JdbcParametro> parametri = new Vector<JdbcParametro>();
    sql = UtilityTags.replaceParametri(parametri, sql, map);
    return update(sql, UtilityTags.vectorParamToObjectArray(parametri), numrow);
  }

  /**
   * Funzione che restituisce il jdbcParametro dal vettore
   *
   * @param vect
   *        Vettore di JdbcParametri
   * @param col
   *        Colonna da estrarre
   * @return NULL se non valido altrimenti il parametro
   */
  public static JdbcParametro getValueFromVectorParam(Object vect, int col) {
    if (vect instanceof Vector) {
      Vector<Object> lVect = (Vector<Object>) vect;
      if (lVect.size() > col) {
        if (lVect.get(col) instanceof JdbcParametro) {
          return (JdbcParametro) lVect.get(col);
        }
      }
    }
    return null;
  }

  /**
   * Funzione che verifica se una tabella esiste nel database
   *
   * @param table
   *        Tabella da verificare
   * @return
   */
  public boolean isTable(String table) {
    boolean esito = false;

    if (it.eldasoft.utils.sql.comp.SqlManager.DATABASE_POSTGRES.equals(SqlManager.getTipoDB())) {
      /*
       * L.G. 10/09/2009: E' stato necessario distinguere per PostGres, perche'
       * se si esegue la query "select count(*) from PROVA1" e la tabella PROVA1
       * non esiste, il DBMS chiude l'eventuale transazione aperta, effettua la
       * rollback degli aggiornamenti precedenti e, anche se a livello di codice
       * viene gestita la SQLException, non è più possibile effettuare altri
       * insert, update, delete. L'applicativo ritorna con un errore imprevisto
       * (o pagina di errore generale) e nel log si trova una dicitura del tipo:
       *
       * Cause: org.postgresql.util.PSQLException: ERROR: current transaction is
       * aborted, commands ignored until end of transaction block
       *
       * E' stata implementata la soluzione che esegue la select su una vista di
       * sistema (pg_tables), la quale elenca tutte le tabelle del DB (a
       * prescindere dall'utente che accede al DBMS).
       * Sabbadin 14/10/2015: esteso il controllo alle view usando pg_views
       *
       * Febas 12/04/2017: la verifica dell'esistenza di una
       * tabella/vista in postgres va effettuata a livello di schema e non di db
       */
      try {
        Long esisteTabella = (Long) getObject(
            "select count(*) from information_schema.tables where table_name = ?",
            new Object[] { table.toLowerCase() });
        if (esisteTabella != null && esisteTabella.longValue() > 0)
          esito = true;
      } catch (SQLException e0) {
        logger.error("Errore nella verifica dell'esistenza della tabella '"
            + table.toLowerCase()
            + "' nella vista di sistema information_schema.tables", e0);
      }
    } else {
      try {
        getVector("select count(1) from " + table, new Object[] {});
        esito = true;
      } catch (SQLException e1) {
        // Se c'è un errore allora significa che la tabella non esiste
        if (logger.isDebugEnabled())
          logger.debug("La tabella '" + table + "' non esiste");
      }
    }

    return esito;
  }

  /**
   * Funzione che esegue una select con l'estrazione di un unico dato
   *
   * @param sql
   *        Sql di selezione
   * @param params
   *        Parametri di passaggio alla select
   * @return
   */
  public Object getObject(String sql, Object[] params) throws SQLException {
    Vector<?> ret = getVector(sql, params);
    if (ret != null && ret.size() > 0)
      return SqlManager.getValueFromVectorParam(ret, 0).getValue();
    return null;
  }

  /**
   * Funzione che setta l'utente per la storia delle modifiche
   *
   * @param req
   */
  public static void setUserStoriaModifiche(ServletRequest req) {

    ServletContext context = SpringAppContext.getServletContext();
    SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager", context, SqlManager.class);

    // Se non è ancora stata verificata l'esistenza della tabella della
    // storia delle modifiche eseguo la verifica e salvo quello che
    // è stato letto
    if (esisteTabellaStoriaModifiche == null) {
      synchronized (SqlManager.class) {
        if (esisteTabellaStoriaModifiche == null) {
          esisteTabellaStoriaModifiche = new Boolean(sql.isTable("ST_TRG"));
        }
      }
    }

    // Eseguo l'update delle informazioni solo se si ha l'opzione della
    // storia delle modifiche
    if (esisteTabellaStoriaModifiche.booleanValue()) {
      if (sql != null) {
        try {
          String propDBMS = ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);
          if (it.eldasoft.utils.sql.comp.SqlManager.DATABASE_ORACLE.equals(propDBMS)) {
            tracciaStoricoModOracle(req,sql);
          }else{
            tracciaStoricoModPostgres(req,sql);
          }
        } catch (SQLException e) {
          logger.error("Errore nel settare i dati utente ed i dati di rete per la storia delle modifiche", e);
        } catch (UnknownHostException e) {
          logger.error("Errore nel determinare i dati dei rete del server per la storia delle modifiche", e);
        }
      }
    }

  }
  
  private static void tracciaStoricoModOracle(ServletRequest req, SqlManager sql) throws UnknownHostException, SQLException{
    if (req instanceof HttpServletRequest) {
      // si tratta di una modifica scatenata da un utente loggato, quindi traccio le informazioni dell'utente (dati utente e client
      // richiedente)
      HttpServletRequest request = (HttpServletRequest) req;
      // Estraggo il profilo utente
      ProfiloUtente utente = null;
      if (request.getSession() != null) {
        // non succede mai che la sessione è nulla se la request e' quella in corso, succedeva invece nel momento in cui il thread in
        // esecuzione referenziava erroneamente una request precedente terminata
        utente = UtilityTags.getProfileUtente(request.getSession());
      }
    
      StringBuffer buf = new StringBuffer("call INFO_SESSIONE_PKG.SET_ATTRIB(");
      if (utente != null) {
        buf.append("'");
        buf.append(utente.getId());
        buf.append("', '");
        buf.append(utente.getLogin());
        buf.append("', '");
        buf.append(StringUtils.replace(utente.getNome(), "'", "''"));
        buf.append("')");
      } else {
        buf.append("null");
        buf.append(", ");
        buf.append("null");
        buf.append(", ");
        buf.append("null");
        buf.append(")");
      }
      sql.execute(buf.toString());
      buf = new StringBuffer("call INFO_SESSIONE_PKG.SET_ATTRIB_WEB( '");
      buf.append(req.getRemoteAddr());
      buf.append("', '");
      buf.append(req.getRemoteHost());
      buf.append("')");
      sql.execute(buf.toString());
    
    } else {
      // si tratta di una modifica scatenata da un task in background
      StringBuffer buf = new StringBuffer("call INFO_SESSIONE_PKG.SET_ATTRIB(");
      buf.append("null");
      buf.append(", ");
      buf.append("null");
      buf.append(", ");
      buf.append("null");
      buf.append(")");
      sql.execute(buf.toString());
      buf = new StringBuffer("call INFO_SESSIONE_PKG.SET_ATTRIB_WEB('");
      buf.append(InetAddress.getLocalHost().getHostAddress());
      buf.append("', '");
      buf.append(InetAddress.getLocalHost().getHostName());
      buf.append("')");
      sql.execute(buf.toString());
    }
  }
  
  private static void tracciaStoricoModPostgres(ServletRequest req, SqlManager sql) throws UnknownHostException{
    
    if (req instanceof HttpServletRequest) {
      // si tratta di una modifica scatenata da un utente loggato, quindi traccio le informazioni dell'utente (dati utente e client
      // richiedente)
      HttpServletRequest request = (HttpServletRequest) req;
      // Estraggo il profilo utente
      ProfiloUtente utente = null;
      if (request.getSession() != null) {
        // non succede mai che la sessione è nulla se la request e' quella in corso, succedeva invece nel momento in cui il thread in
        // esecuzione referenziava erroneamente una request precedente terminata
        utente = UtilityTags.getProfileUtente(request.getSession());
      }
    
      Map<String,String> valori = new HashMap<String,String>();
      if (utente != null) {
        valori.put("v_syscon", ""+utente.getId());
        valori.put("v_sysnom", utente.getLogin());
        valori.put("v_sysute", StringUtils.replace(utente.getNome(), "'", "''"));
      } else {
        valori.put("v_syscon", "null");
        valori.put("v_sysnom", "null");
        valori.put("v_sysute", "null");
      }
      SqlParameter param_v_syscon = new SqlParameter("v_syscon",java.sql.Types.VARCHAR);
      SqlParameter param_v_sysnom = new SqlParameter("v_sysnom",java.sql.Types.VARCHAR);
      SqlParameter param_v_sysute = new SqlParameter("v_sysute",java.sql.Types.VARCHAR);
      sql.callStoredProcedure("SET_ATTRIB", true, new SqlParameter[]{param_v_syscon,param_v_sysnom,param_v_sysute},valori);
      valori.clear();
      valori.put("v_user", req.getRemoteAddr());
      valori.put("v_context", req.getRemoteHost());
      SqlParameter param_v_user = new SqlParameter("v_user",java.sql.Types.VARCHAR);
      SqlParameter param_v_context = new SqlParameter("v_context",java.sql.Types.VARCHAR);
      sql.callStoredProcedure("SET_ATTRIB_WEB", true, new SqlParameter[]{param_v_user,param_v_context},valori);
      valori.clear();
    } else {
        // si tratta di una modifica scatenata da un task in background
        Map<String,String> valori = new HashMap<String,String>();
        valori.put("v_syscon", "null");
        valori.put("v_sysnom", "null");
        valori.put("v_sysute", "null");
        SqlParameter param_v_syscon = new SqlParameter("v_syscon",java.sql.Types.VARCHAR);
        SqlParameter param_v_sysnom = new SqlParameter("v_sysnom",java.sql.Types.VARCHAR);
        SqlParameter param_v_sysute = new SqlParameter("v_sysute",java.sql.Types.VARCHAR);
        sql.callStoredProcedure("SET_ATTRIB", true, new SqlParameter[]{param_v_syscon,param_v_sysnom,param_v_sysute},valori);
        valori.clear();
        valori.put("v_user", InetAddress.getLocalHost().getHostAddress());
        valori.put("v_context", InetAddress.getLocalHost().getHostName());
        SqlParameter param_v_user = new SqlParameter("v_user",java.sql.Types.VARCHAR);
        SqlParameter param_v_context = new SqlParameter("v_context",java.sql.Types.VARCHAR);
        sql.callStoredProcedure("SET_ATTRIB_WEB", true, new SqlParameter[]{param_v_user,param_v_context},valori);
       
    }
    
  }
  
  /**
   * Funzione per eseguire un altersession o il richiamo di una procedura senza
   * neccessariamente aprire una transazione
   *
   * @param sql
   * @throws SQLException
   */
  public void execute(String sql) throws SQLException {
    this.sqlDao.execute(sql);
  }

  /**
   * Ritorna la lista di coppie della classe Tabellato, contenente tipo e
   * descrizione, identificanti un tabellato a partire dalla select fornita nei
   * parametri. Il primo campo estratto nella select è il tipo/codice, il
   * secondo è la descrizione
   *
   * @param sql
   *        sql da eseguire per estrarre i valori del tabellato
   * @param params
   *        eventuali parametri per eseguire la query
   * @return lista di oggetti della classe Tabellato
   * @throws SQLException
   */
  public List<?> getElencoTabellati(String sql, Object[] params)
      throws SQLException {
    List<?> dati = sqlDao.getVectorQueryForList(sql, params);
    ArrayList<Tabellato> risultato = new ArrayList<Tabellato>();
    Tabellato tabellato = null;
    Vector<?> row = null;
    for (int i = 0; i < dati.size(); i++) {
      tabellato = new Tabellato();
      row = (Vector<?>) dati.get(i);
      tabellato.setTipoTabellato(row.get(0).toString());
      tabellato.setDescTabellato(row.get(1).toString());
      risultato.add(tabellato);
    }
    return risultato;
  }

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
  public Map<?,?> callStoredProcedure(String storedProcedure, boolean isFunction,
      SqlParameter[] parametersTypes, Map<?,?> values) {
    return this.sqlDao.callStoredProcedure(storedProcedure, isFunction,
        parametersTypes, values);
  }


}