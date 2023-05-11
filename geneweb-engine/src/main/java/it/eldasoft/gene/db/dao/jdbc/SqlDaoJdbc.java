package it.eldasoft.gene.db.dao.jdbc;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.dao.SqlDao;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.sql.comp.SqlManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

class SqlDaoJdbc implements SqlDao {

  private static Logger              logger            = Logger.getLogger(SqlDaoJdbc.class);

  private PlatformTransactionManager transactionManager;

  /** Datasource per l'accesso ai dati */
  private DataSource                 dataSource;

  /**
   * Resource bundle parte generale dell'applicazione (modulo GENE)
   */
  private ResourceBundle             resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  private class RowMapperVector implements RowMapper{

    public Vector<JdbcParametro> mapRow(ResultSet rs, int row) throws SQLException {
      return SqlDaoJdbc.getVector(rs);
    }

  }

  private class RowMapperHashMap implements RowMapper {

    public Object mapRow(ResultSet rs, int row) throws SQLException {
      return SqlDaoJdbc.getHashMap(rs);
    }

  }

  private class RowMapperOrderedMap implements RowMapper {

    public Object mapRow(ResultSet rs, int row) throws SQLException {
      return SqlDaoJdbc.getOrderedMap(rs);
    }

  }

  private class ResultSetExtractorMap implements ResultSetExtractor {

    public Object extractData(ResultSet rs) throws SQLException,
        DataAccessException {
      if (rs.next())
        return SqlDaoJdbc.getHashMap(rs);
      else
        return null;
    }
  }

  private class ResultSetExtractorVector implements ResultSetExtractor {

    public Object extractData(ResultSet rs) throws SQLException,
        DataAccessException {
      if (rs.next())
        return SqlDaoJdbc.getVector(rs);
      else
        return null;
    }
  }

  private class ResultSetExtractorListVector implements ResultSetExtractor {

    //http://forum.spring.io/forum/spring-projects/data/71443-streaming-resultset-and-jdbctemplate

    /** In caso di paginazione indica il numero di pagina, 0 &egrave; la prima. */
    private int pageNumber = -1;
    /** Indica il numero massimo di record estraibili, in caso di paginazione corrisponde alla dimensione pagina. */
    private int maxrow = 0;

    public ResultSetExtractorListVector(int pageNumber, int maxrow) {
      this.pageNumber = pageNumber;
      this.maxrow = maxrow;
    }

    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
      List<Vector<JdbcParametro>> results = new ArrayList<Vector<JdbcParametro>>();

      int first = -1;
      int last = maxrow;
      boolean isPaginated = (pageNumber > -1 && maxrow > 0);
      if (isPaginated) {
        first = (pageNumber * maxrow);
        last = (first + maxrow) - 1;
      }

      int rowNum = 0;
      RowMapperVector mapper = new RowMapperVector();
      while (rs.next() && ((isPaginated && rowNum <= last) || !isPaginated)) {
        if ((isPaginated && rowNum >= first) || !isPaginated) {
          // in caso di paginazione considero solo i record con indice da first a last
          results.add(mapper.mapRow(rs, rowNum));
        }
        rowNum++;
      }

      return results;
    }
  }

  /**
   * Funzione che estrae un HashMap da un result set
   *
   * @param rs
   *        Result set da cui ricavare i dati
   * @return HashMap
   * @throws SQLException
   */
  protected static HashMap<String, JdbcParametro> getHashMap(ResultSet rs) throws SQLException {
    HashMap<String, JdbcParametro> map = new HashMap<String, JdbcParametro>();
    populateMap(rs, map);
    return map;
  }

  /**
   * Estrae un ordered Map da un result set.
   *
   * @param rs
   *        Result set da cui ricavare i dati
   * @return map ordinato in base all'ordine di inserimento (LinkedHashMap)
   * @throws SQLException
   */
  protected static Map<String, JdbcParametro> getOrderedMap(ResultSet rs) throws SQLException {
    Map<String, JdbcParametro> map = new LinkedHashMap<String, JdbcParametro>();
    populateMap(rs, map);
    return map;
  }

  /**
   * Popola la mappa con il result set in input
   * @param rs result set con la riga di dati da estrarre
   * @param map mappa da popolare con i dati del resultset
   * @throws SQLException
   */
  private static void populateMap(ResultSet rs, Map<String, JdbcParametro> map)
      throws SQLException {
    // ///////////////////////////////////////////////////////////////
    // Modifiche
    // ----------------
    // 05.12.2007: M.F. Aggiunta del lower del nome delle colonne e
    // dell'aggiunta nel map della riga con il solo nome se è visibile il nome
    // tabella
    // ////////////////////////////////////////////////////////////// /
    ResultSetMetaData md = rs.getMetaData();
    // Scorro tutte le colonne
    for (int i = 1; i <= md.getColumnCount(); i++) {
      StringBuffer nomeColonna = new StringBuffer("");
      JdbcParametro val = getColValue(i, rs, md.getColumnType(i));
      if (md.getTableName(i) != null && md.getTableName(i).length() > 0) {
        nomeColonna.append(md.getTableName(i));
        nomeColonna.append(".");
        // Se si ha il nome della tabella aggiungo anche al riferimento con il
        // solo nome colonna
        if (md.getColumnName(i) != null) {
          if (!md.getColumnName(i).toUpperCase().equals(md.getColumnName(i)))
            map.put(md.getColumnName(i), val);
          map.put(md.getColumnName(i).toUpperCase(), val);
        }
      }
      nomeColonna.append(md.getColumnName(i));
      // Aggiungo il valore anche se il nome della colonna non è uppercase
      if (!nomeColonna.toString().toUpperCase().equals(nomeColonna.toString()))
        map.put(nomeColonna.toString().toUpperCase(), val);
      // Aggiungo il valore
      map.put(nomeColonna.toString().toUpperCase(), val);
    }
  }

  protected static Vector<JdbcParametro> getVector(ResultSet rs) throws SQLException {
    Vector<JdbcParametro> vect = new Vector<JdbcParametro>();
    ResultSetMetaData md = rs.getMetaData();
    // Scorro tutte le colonne
    for (int i = 1; i <= md.getColumnCount(); i++) {
      // Aggiungo il valore
      vect.add(getColValue(i, rs, md.getColumnType(i)));
    }
    return vect;
  }

  /**
   * Funzione che ricava il valore di una colonna. Trasformandola nell'eventuale
   * stringa
   *
   * @param nCol
   *        Numero della colonna. Deve iniziare da 1
   * @param rs
   *        ResultSet da cui estrarre il valore
   * @param columnType
   *        Tipo di colonna
   * @return Valore della colonna. Sempre del tipo JdbcParametri
   * @throws SQLException
   */
  protected static JdbcParametro getColValue(int nCol, ResultSet rs,
      int columnType) throws SQLException {
    Object obj = rs.getObject(nCol);
    switch (columnType) {
    case Types.DATE: // Campo data
    case Types.TIMESTAMP: // Campo Timestamp (DateTime)
      return new JdbcParametro(getTypeColumn(columnType), rs.getDate(nCol));
//      return new JdbcParametro(getTypeColumn(columnType), rs.getTimestamp(nCol));
    case Types.NUMERIC: // Campo numerico
      // Verifico se si tratta di un long o di un double
      if (obj == null) {
        if (rs.getMetaData().getScale(nCol) == 0)
          return new JdbcParametro(getTypeColumn(columnType), null);
        else
          return new JdbcParametro(JdbcParametro.TIPO_DECIMALE, null);
      } else {
        // NOTA: in Oracle, in caso di funzione statistica o matematica su campo
        // decimale (vedi sum su importi, o addizione), la scala ritornata nei
        // metadati è erroneamente 0, per cui si deve controllare se la scala
        // del metadato della colonna OPPURE la scala del dato numerico (ove
        // prevista) sono maggiori di 0, indicativi del fatto che è un numero
        // con una parte decimale
        if (rs.getMetaData().getScale(nCol) > 0
            || (obj instanceof BigDecimal && ((BigDecimal) obj).scale() > 0)) {
          // Se il numero ha cifre decimali, allora si crea un
          // JdbcParametro di tipo decimale a cui si associa un value
          // di tipo Double
          return new JdbcParametro(JdbcParametro.TIPO_DECIMALE, new Double(
              rs.getDouble(nCol)));
        } else {
          // Se il numero non ha cifre decimali, allora si crea un
          // JdbcParametro di tipo numerico a cui si associa un value
          // di tipo Long
          return new JdbcParametro(getTypeColumn(columnType), new Long(
              rs.getLong(nCol)));
        }
      }

    case Types.DECIMAL: // Campo decimale
      if (obj == null)
        return new JdbcParametro(getTypeColumn(columnType), null);
      else {
        if (obj instanceof BigDecimal) {
          // Gestione specifica per il database DB2, il quale per i dati di tipo
          // NUMERIC e DECIMAL (con e senza cifre decimali) attraverso il
          // driver JDBC crea nel ResultSet un oggetto di tipo BigDecimal.
          // Tale oggetto deve essere gestito per creare un oggetto di tipo
          // Long o un oggetto di tipo Double a seconda che l'oggetto
          // BigDecimal abbia cifre decimali o meno (attraverso il metodo
          // getScale())
          //
          // 23/02/2011 - modifica effettuata per correggere l'errore di conversione
          // dell'istruzione rs.getDouble(nCol).
          // Con alcuni importi, infatti, la conversione genera un numero
          // con molti decimali, per esempio
          // 19417,87 diventa 19417.870000000003
          // 19417,88 diventa 19417.879999999997
          // 35863.48 diventa 35863.479999999996
          // 54794.73 diventa 54794.729999999996
          // ed altri ancora.

          BigDecimal tmp = (BigDecimal) obj;
          if (tmp.scale() == 0) {
            // Se il numero non ha cifre decimali, allora si crea un
            // JdbcParametro di tipo numerico a cui si associa un value
            // di tipo Long
            // return new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(
            //    rs.getLong(nCol)));
            return new JdbcParametro(JdbcParametro.TIPO_NUMERICO, new Long(
                tmp.longValue()));
          } else {
            // Se il numero ha cifre decimali, allora si crea un
            // JdbcParametro di tipo decimale a cui si associa un value
            // di tipo Double
            // return new JdbcParametro(getTypeColumn(columnType), new Double(
            //    rs.getDouble(nCol)));
            return new JdbcParametro(getTypeColumn(columnType), new Double(
                tmp.doubleValue()));
          }
        }
      }

    case Types.FLOAT: // Campo float
    case Types.REAL: // Campo di tipo real
    case Types.DOUBLE: // Campo double
      if (obj == null)
        return new JdbcParametro(getTypeColumn(columnType), null);
      return new JdbcParametro(getTypeColumn(columnType), new Double(
          rs.getDouble(nCol)));

    case Types.BIGINT: // Intero grande
    case Types.INTEGER: // Campo di tipo intero
    case Types.SMALLINT: // Intero piccolo
      if (obj == null)
        return new JdbcParametro(getTypeColumn(columnType), null);
      return new JdbcParametro(getTypeColumn(columnType), new Long(
          rs.getLong(nCol)));
    case Types.CHAR: // Campo char
    case Types.VARCHAR: // Campo varchar
    case Types.LONGVARCHAR: // Campo varchar grandissimo
      return new JdbcParametro(getTypeColumn(columnType), rs.getString(nCol));

    case Types.CLOB:
      return new JdbcParametro(getTypeColumn(columnType), rs.getString(nCol));
    default:
      // Se tipo non gestito allora do l'errore
      throw new SQLException(rs.getMetaData().getColumnTypeName(nCol)
          + ": Tipo di campo non supportato per l'estrazione dei dati !");
    }
  }

  /**
   * Converte il tipo di colonna estratto dai metadati nel tipo di dato interno
   *
   * @param columnType
   * @return
   */
  protected static char getTypeColumn(int columnType) {
    switch (columnType) {
    case Types.DATE: // Campo data
    case Types.TIMESTAMP: // Campo Timestamp (DateTime)
      return JdbcParametro.TIPO_DATA;

    case Types.FLOAT: // Campo float
    case Types.DECIMAL: // Campo decimale
    case Types.REAL: // Campo di tipo real
    case Types.DOUBLE: // Campo double
      return JdbcParametro.TIPO_DECIMALE;
    case Types.BIGINT: // Intero grande
    case Types.INTEGER: // Campo di tipo intero
    case Types.SMALLINT: // Intero piccolo
    case Types.NUMERIC: // Campo numerico (BigNumber)
      return JdbcParametro.TIPO_NUMERICO;
    case Types.CHAR: // Campo char
    case Types.VARCHAR: // Campo varchar
    case Types.LONGVARCHAR: // Campo varchar grandissimo
    case Types.CLOB:
      return JdbcParametro.TIPO_TESTO;
    default:
      // Se tipo non gestito allora do l'errore
      return JdbcParametro.TIPO_INDEFINITO;
    }
  }

  /*
   * /** Funzione che esegue il settaggio dei parametri in un prepared stantment
   *
   * @param ps Prepare Stantment in cui settare i parametri @param pars
   * Parametri da settare @throws SQLException
   *
   * @deprecated Non più utilizzata da quando si usa JdbcTemplate che gestisce
   *             le transazioni
   *
   *
   * private static void setParameters(PreparedStatement ps, Vector pars) throws
   * SQLException {
   *
   * logger.debug("setParameters: inizio metodo: di " + pars.size() + "
   * parametri");
   *
   * for (int i = 0; i < pars.size(); i++) { setParam(i, ps, (JdbcParametro)
   * pars.get(i)); } logger.debug("setParameters: fine metodo"); } * Funzione
   * che setta un parametro in un prepared stantment
   *
   * @param nPar Numero del parametro @param ps Prepared Stantment @param par
   * Parametro @throws SQLException
   *
   * private static void setParam(int nPar, PreparedStatement ps, JdbcParametro
   * par) throws SQLException { int tipo; logger.debug("Setto il parametro: " +
   * par.toString(true)); switch (par.getTipo()) { case
   * JdbcParametro.TIPO_NUMERICO: // Numerico tipo = Types.INTEGER; break; case
   * JdbcParametro.TIPO_DECIMALE: // Float tipo = Types.DOUBLE; break; case
   * JdbcParametro.TIPO_DATA: // Data tipo = Types.DATE; break; default: //
   * Testo di default tipo = Types.VARCHAR; break; } if (par.getValue() == null) {
   * ps.setNull(nPar, tipo); return; } else { switch (tipo) { case
   * Types.INTEGER: ps.setLong(nPar + 1, ((Long) par.getValue()).longValue());
   * break; case Types.DOUBLE: ps.setDouble(nPar + 1, ((Double)
   * par.getValue()).doubleValue()); break; case Types.DATE: ps.setDate(nPar +
   * 1, ((Date) par.getValue())); break; case Types.VARCHAR: ps.setString(nPar +
   * 1, ((String) par.getValue())); break; } } }
   */

  /**
   * Funzione che esegue una query e restituisce una lista di oggetti di tipo
   * OrderedMap
   *
   * @param asSql
   *        Sql da eseguire
   * @return Lista di oggerri di tipo OrderedMap
   * @throws SQLException
   */
  public List getQueryForList(String asSql) throws SQLException {

    return this.getQueryForList(asSql, new Object[] {});
  }

  /**
   * Esegue una query e restituisce una lista di oggetti di tipo
   * HashMap.
   *
   * @param asSql
   *        Sql da eseguire
   * @param parametri
   *        d'ingresso
   * @return Lista di oggetti di tipo HashMap
   * @throws SQLException
   */
  public List getQueryForList(String asSql, Object[] param) throws SQLException {
    RowMapper mapper = new RowMapperHashMap();
    return extractMapListFromQuery(asSql, param, mapper);
  }

  public List getOrderedMapListFromQuery(String asSql, Object[] param)
      throws SQLException {
    RowMapper mapper = new RowMapperOrderedMap();
    return extractMapListFromQuery(asSql, param, mapper);
  }

  /**
   * Estrae la query in input e popola un lista di oggetti di interfaccia Map in
   * base al mapper in input
   *
   * @param asSql
   *        stringa sql da eseguire
   * @param param
   *        parametri da utilizzare nella statement sql
   * @param mapper
   *        mapper del result set in un oggetto che implementa Map
   * @return lista di oggetti che implementano Map
   * @throws SQLException
   */
  private List extractMapListFromQuery(String asSql, Object[] param,
      RowMapper mapper) throws SQLException {
    List tmp = null;
    if (param == null) param = new Object[] {};
    try {
      JdbcTemplate jt = new JdbcTemplate(this.dataSource);
      Vector vect = this.array2Vector(param);
      asSql = repairParameters(asSql, vect);
      param = vect.toArray();
      tmp = jt.query(asSql, param, mapper);
      if (logger.isDebugEnabled()) {
        StringBuffer buf = new StringBuffer(asSql);
        buf.append("\nParametri: " + parametriToString(param));
        buf.append("\n" + tmp.toString());
        logger.debug(buf.toString());
      }
    } catch (SqlComposerException e) {
      throw new SQLException(
          resBundleGenerale.getString(e.getChiaveResourceBundle()));
    } catch (Throwable t) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);
      StringBuffer buf = new StringBuffer("Errore nella select: \n");
      buf.append(asSql).append("\nParametri: ").append(parametriToString(param)).append(
          "\n").append(sw.toString());
      throw new SQLException(buf.toString());
    }
    return tmp;
  }

  /**
   * Funzione che restituisce una lista di vettori partendo da una select
   *
   * @param asSql
   *        Sql da eseguire
   * @return
   * @throws SQLException
   */
  public List getVectorQueryForList(String asSql) throws SQLException {
    return this.getVectorQueryForList(asSql, new Object[] {});
  }

//  /**
//   * Funzione che restituisce una lista di vettori partendo da una select
//   *
//   * @param asSql
//   *        Sql da eseguire
//   * @param maxrow
//   *        0 per tutte altrimenti il numero massimo di righe
//   * @return
//   * @throws SQLException
//   */
//  public List getVectorQueryForList(String asSql, int maxrow)
//      throws SQLException {
//    return this.getVectorQueryForList(asSql, new Object[] {}, maxrow);
//  }

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
  public List getVectorQueryForList(String asSql, Object[] param)
      throws SQLException {
    return this.getVectorQueryForList(asSql, param, -1, 0);
  }

  /**
   * Funzione che esegue una select e restituise un result set di tipo HashMap
   *
   * @param asSql
   *        Sql da eseguire
   * @param param
   *        Parametri
   * @return HashMap
   */
  public HashMap getQuery(String asSql, Object[] param) throws SQLException {
    HashMap obj = null;
    if (param == null) param = new Object[] {};
    try {
      JdbcTemplate jt = new JdbcTemplate(this.dataSource);
      Vector vect = this.array2Vector(param);
      asSql = repairParameters(asSql, vect);
      param = vect.toArray();
      obj = (HashMap) jt.query(asSql, param, new ResultSetExtractorMap());
      if (logger.isDebugEnabled()) {
        StringBuffer buf = new StringBuffer(asSql);
        buf.append("\nParametri: " + parametriToString(param));
        if (obj != null) buf.append("\n" + obj.toString());
        logger.debug(buf.toString());
      }
    } catch (SqlComposerException e) {
      throw new SQLException(
          resBundleGenerale.getString(e.getChiaveResourceBundle()));
    } catch (Throwable t) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);
      StringBuffer buf = new StringBuffer("Errore nella selezione: \n");
      buf.append(asSql).append("\nParametri: ").append(parametriToString(param)).append(
          "\n").append(sw.toString());
      throw new SQLException(buf.toString());
    }
    return obj;
  }

  /**
   * Funzione che esegue una select e restituise un result set di tipo HashMap
   *
   * @param asSql
   *        Sql da eseguire
   * @return HashMap
   */
  public HashMap getQuery(String asSql) throws SQLException {
    return getQuery(asSql, new Object[] {});
  }

  /**
   * Funzione che esegue una select e restituise un result set di tipo HashMap
   *
   * @param asSql
   *        Sql da eseguire
   * @param param
   *        Parametri
   * @return HashMap
   */
  public Vector getVectorQuery(String asSql, Object[] param)
      throws SQLException {
    Vector obj = null;
    if (param == null) param = new Object[] {};
    try {
      JdbcTemplate jt = new JdbcTemplate(this.dataSource);
      Vector vect = this.array2Vector(param);
      asSql = repairParameters(asSql, vect);
      param = vect.toArray();
      obj = (Vector) jt.query(asSql, param, new ResultSetExtractorVector());
      if (logger.isDebugEnabled()) {
        StringBuffer buf = new StringBuffer(asSql);
        buf.append("\nParametri: " + parametriToString(param));
        if (obj != null) buf.append("\n" + obj.toString());
        logger.debug(buf.toString());
      }
    } catch (SqlComposerException e) {
      throw new SQLException(
          resBundleGenerale.getString(e.getChiaveResourceBundle()));
    } catch (Throwable t) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);
      StringBuffer buf = new StringBuffer("Errore nella selezione: \n");
      buf.append(asSql).append("\nParametri: ").append(parametriToString(param)).append(
          "\n").append(sw.toString());
      throw new SQLException(buf.toString());
    }
    return obj;
  }

  /**
   * Funzione che esegue una select e restituise un result set di tipo HashMap
   *
   * @param asSql
   *        Sql da eseguire
   * @return HashMap
   */
  public Vector getVectorQuery(String asSql) throws SQLException {
    return getVectorQuery(asSql, new Object[] {});
  }

  /**
   * Funzione che esegue un sql d'aggiornamento su database
   *
   * @param asSqlUpdate
   *        Sql d'update d'aggiornamento (INSERT, DELETE o UPDATE)
   * @param param
   *        Parametri
   * @return numero di righe sulle quali l'update è andato a segno
   * @throws SQLException
   */
  public int update(String asSqlUpdate, Object[] param) throws SQLException {
    int ret = -1;
    if (param == null) param = new Object[] {};
    try {
      JdbcTemplate jt = new JdbcTemplate(this.dataSource);
      Vector vect = this.array2Vector(param);
      asSqlUpdate = repairParameters(asSqlUpdate, vect);
      param = vect.toArray();
      // a causa della gestione dei lob, va aggiunto il check dei tipi dei
      // parametri
      int[] types = SqlDaoJdbc.findSqlTypes(param);
      ret = jt.update(asSqlUpdate, param, types);
      if (logger.isDebugEnabled()) {
        StringBuffer buf = new StringBuffer(asSqlUpdate);
        buf.append("\nParametri: " + parametriToString(param));
        buf.append("\nRighe modificate:" + ret);
        logger.debug(buf.toString());
      }
    } catch (SqlComposerException e) {
      throw new SQLException(
          resBundleGenerale.getString(e.getChiaveResourceBundle()));
    } catch (Throwable t) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);
      StringBuffer buf = new StringBuffer("Errore nell'update: \n");
      buf.append(asSqlUpdate).append("\nParametri: ").append(
          parametriToString(param)).append("\nRighe modificate:").append(ret).append(
          "\n").append(sw.toString());
      throw new SQLException(buf.toString());
    }
    return ret;
  }

  /**
   * Determina il tipo SQL di ogni elemento dell'elenco in input
   *
   * @param params
   *        elenco parametri per cui calcolare il tipo
   * @return elenco dei tipi SQL (classe java.sql.Types)
   */
  public static int[] findSqlTypes(Object[] params) {
    int[] elenco = new int[params.length];
    for (int i = 0; i < params.length; i++) {
      elenco[i] = SqlDaoJdbc.getSqlType(params[i]);
    }
    return elenco;
  }

  /**
   * Determina il tipo SQL di un dato a partire dalla classe dell'oggetto
   *
   * @param parametro
   *        parametro di cui determinare il tipo
   * @return tipo sql (classe java.sql.Types)
   */
  public static int getSqlType(Object parametro) {
    int type = Types.NULL;
    if (parametro != null) {
      if (parametro instanceof String)
        type = Types.VARCHAR;
      else if (parametro instanceof Integer)
        type = Types.INTEGER;
      else if (parametro instanceof Float)
        type = Types.FLOAT;
      else if (parametro instanceof Double)
        type = Types.DOUBLE;
      else if (parametro instanceof Long)
        type = Types.NUMERIC;
      else if (parametro instanceof Timestamp)
        type = Types.TIMESTAMP;
      else if (parametro instanceof java.util.Date)
        type = Types.DATE;
      else if (parametro instanceof SqlLobValue) type = Types.BLOB;
    }
    return type;
  }

  /**
   * Funzione che esegue un sql d'aggiornamento su database
   *
   * @param asSqlUpdate
   *        Sql d'update d'aggiornamento (INSERT, DELETE o UPDATE)
   * @param param
   *        Parametri
   * @param numRow
   *        Numero di righe in cui eseguire l'update
   * @return numero di righe sulle quali l'update è andato a segno
   * @throws SQLException
   */
  public int update(String asSqlUpdate, Object[] param, int numRow)
      throws SQLException {

    int ret = this.update(asSqlUpdate, param);

    if (ret != numRow)
      throw new SQLException("Attenzione ! L'update:"
          + asSqlUpdate
          + "\nNon ha eseguito update su "
          + numRow
          + " righe ma su "
          + ret);
    return ret;
  }

  public TransactionStatus startTransaction() throws SQLException {
    TransactionStatus status = null;
    try {
      DefaultTransactionDefinition trans = new DefaultTransactionDefinition(
          TransactionDefinition.PROPAGATION_REQUIRED);
      status = this.getTransactionManager().getTransaction(trans);
    } catch (Throwable t) {
      status = null;
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);
      throw new SQLException("Errore in creazione di una transazione:\n"
          + sw.toString());
    }
    return status;

  }

  public void commitTransaction(TransactionStatus status) throws SQLException {
    try {
      if (!status.isCompleted()) this.getTransactionManager().commit(status);
    } catch (Throwable t) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);
      throw new SQLException("Errore nel commit della transazione:\n"
          + sw.toString());
    }
  }

  /**
   * Funzione che esegue il rollback
   *
   * @param ignoreErrors
   *        se a true non restituisce mai errore
   * @throws SQLException
   */
  public void rollbackTransaction(TransactionStatus status) throws SQLException {
    try {
      if (!status.isCompleted()) this.getTransactionManager().rollback(status);
    } catch (Throwable t) {
      // Do l'errore solo se devo ignorare gli errori
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);
      throw new SQLException("Errore nel rollback della transazione:\n"
          + sw.toString());
    }
  }

  /**
   * @return Returns the transactionManager.
   */
  public PlatformTransactionManager getTransactionManager() {
    return transactionManager;
  }

  /**
   * @param transactionManager
   *        The transactionManager to set.
   */
  public void setTransactionManager(
      PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  /**
   * @param dataSource
   *        dataSource da settare internamente alla classe.
   */
  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  private String parametriToString(Object[] lista) {
    StringBuffer buf = new StringBuffer("");
    if (lista != null && lista.length > 0) {
      buf.append("[");
      for (int i = 0; i < lista.length; i++) {
        buf.append(" [");
        if (lista[i] != null) buf.append(lista[i].toString());
        buf.append("]");
      }
      buf.append(" ]");
    }
    return buf.toString();
  }

  public List<Vector<JdbcParametro>> getVectorQueryForList(String asSql, Object[] param, int pageNumber, int maxrow)
      throws SQLException {
    List<Vector<JdbcParametro>> tmp = null;
    if (param == null) param = new Object[] {};
    try {
      JdbcTemplate jt = new JdbcTemplate(this.dataSource);
      // maxrow rappresenta il numero massimo di record estraibili in caso di assenza di paginazione, altrimenti rappresenta la dimensione
      // di una pagina di risultato
      if (maxrow > 0) {
        jt.setMaxRows(pageNumber == -1 ? maxrow : (pageNumber+1)*maxrow);
      }
      Vector vect = this.array2Vector(param);
      asSql = repairParameters(asSql, vect);
      param = vect.toArray();
      tmp = (List<Vector<JdbcParametro>>) jt.query(asSql, param, new ResultSetExtractorListVector(pageNumber, maxrow));
      if (logger.isDebugEnabled()) {
        StringBuffer buf = new StringBuffer(asSql);
        buf.append("\nParametri: " + parametriToString(param));
        buf.append("\n" + tmp.toString());
        logger.debug(buf.toString());
      }
    } catch (SqlComposerException e) {
      throw new SQLException(
          resBundleGenerale.getString(e.getChiaveResourceBundle()));
    } catch (Throwable t) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);
      StringBuffer buf = new StringBuffer("Errore nella selezione: \n");
      buf.append(asSql).append("\nParametri: ").append(parametriToString(param)).append(
          "\n").append(sw.toString());
      throw new SQLException(buf.toString());
    }
    return tmp;
  }

  private int posNotInStr(String str, char c) {
    int pos = -1;
    int posTmp, posTmpInStr, posTmpEndStr;
    do {
      posTmp = str.indexOf(c, pos + 1);
      if (posTmp < 0) {
        pos = -1;
        break;
      }
      posTmpInStr = str.indexOf('\'', pos + 1);
      // Se esiste il catarrere
      if (posTmpInStr >= 0 && posTmpInStr < posTmp) {
        posTmpEndStr = str.indexOf('\'', posTmpInStr + 1);
        if (posTmpEndStr < 0) {
          pos = -1;
          break;
        }
        pos = posTmpEndStr + 1;
        continue;
      }
      pos = posTmp;
      break;
    } while (true);
    return pos;
  }

  private String repairParameters(String sql, Vector<?> param)
      throws SqlComposerException {

    if (param == null || param.size() == 0) return sql;
    char tipoDB = SqlManager.getCodiceDatabasePerCompositore(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));

    StringBuffer ret = new StringBuffer();
    // Scorro tutti i parametri
    for (int i = 0; i < param.size(); i++) {
      int posTmp = posNotInStr(sql, '?');
      if (posTmp < 0) break;
      ret.append(sql.substring(0, posTmp));
      if (param.get(i) == null) {
        ret.append("null");
        param.remove(i);
        i--;
      } else {
        switch (tipoDB) {
        // case SqlManager.DATABASE_ACCESS_PER_COMPOSITORE:
        // // Se si tratta di access allora trasformo il parametro in una
        // stringa
        // ret.append(JdbcParametro.toString(param.get(i), tipoDB));
        // param.remove(i);
        // i--;
        // break;
        default:
          ret.append('?');
          break;
        }
      }
      sql = sql.substring(posTmp + 1);
    }
    ret.append(sql);
    return ret.toString();
  }

  /**
   * Funzione che trasforma un array di oggetti in un vettore
   *
   * @param objs
   * @return
   */
  private Vector array2Vector(Object objs[]) {
    Vector ret = new Vector();
    for (int i = 0; i < objs.length; i++) {
      ret.add(objs[i]);
    }
    return ret;
  }

  public int execute(String sql) throws SQLException {
    int ret = -1;

    try {
      JdbcTemplate jt = new JdbcTemplate(this.dataSource);
      ret = jt.update(sql);
    } catch (Throwable t) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      t.printStackTrace(pw);
      StringBuffer buf = new StringBuffer("Errore nell'execute: \n");
      buf.append(sql).append("\nRighe modificate:").append(ret).append(
          "\n").append(sw.toString());
      throw new SQLException(buf.toString());
    }
    return ret;

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
   */
  @SuppressWarnings("rawtypes")
  public Map callStoredProcedure(String storedProcedure, boolean isFunction,
      SqlParameter[] parametersTypes, Map values) throws DataAccessException {
    Map risultato = null;
    StoredProcedure sp = new StoredProcedure(this.dataSource, storedProcedure,
        isFunction, parametersTypes);
    risultato = sp.execute(values);
    return risultato;
  }

  /**
   * Inner class per la chiamata a stored procedure
   *
   * @author Stefano.Sabbadin
   * @since 1.4.6
   */
  class StoredProcedure extends org.springframework.jdbc.object.StoredProcedure {

    /**
     * Crea l'oggetto per eseguire una chiamata ad una stored procedure
     *
     * @param datasource
     *        data source
     * @param sql
     *        nome della stored procedure
     * @param isFunction
     *        true se è una funzione con un risultato di ritorno, false
     *        altrimenti (anche nel caso di stored procedure che prevede
     *        argomenti di output)
     * @param parametersTypes
     *        array di parametri di input e output nell'ordine esatto in cui
     *        sono dichiarati nella stored procedure
     */
    public StoredProcedure(DataSource datasource, String spName,
        boolean isFunction, SqlParameter[] parametersTypes) {
      super();
      setDataSource(datasource);
      setSql(spName);
      setFunction(isFunction);
      for (int i = 0; i < parametersTypes.length; i++) {
        declareParameter(parametersTypes[i]);
      }
      compile();
    }
  }
}
