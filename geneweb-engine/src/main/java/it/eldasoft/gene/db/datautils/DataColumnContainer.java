/*
 * Created on 13-ago-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.datautils;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * Classe contenitore di colonne con valore (ex JdbcColumnWithValueImpl)
 *
 * @author cit_franceschin
 * @author Stefano.Sabbadin
 */
public class DataColumnContainer implements Serializable {

  /**
   * UID.
   */
  private static final long serialVersionUID = -1758499186911887650L;

  private HashMap<String, DataColumn> colonne;

  /**
   * Costruttore della classe
   *
   * @param campiPar
   */
  public DataColumnContainer(DataColumn[] campiPar) {
    colonne = new HashMap<String, DataColumn>();
    for (int i = 0; i < campiPar.length; i++) {
      colonne.put(campiPar[i].toString().toUpperCase(), campiPar[i]);
    }
  }

  public DataColumnContainer(Vector<DataColumn> column) {
    colonne = new HashMap<String, DataColumn>();
    for (int i = 0; i < column.size(); i++) {
      if (column.get(i) instanceof DataColumn) {
        DataColumn colonna = column.get(i);
        colonne.put(colonna.toString().toUpperCase(), colonna);
      }
    }
  }

  /**
   * Costruttore che parte dall'elenco di campi nel seguente modo
   *
   * @param campi
   *        ENTITA.CAMPO=tipo:VALORE[;ENTITA.CAMPO=tipo:VALORE]
   */
  public DataColumnContainer(String campi) {
    // Divido tutti i campi
    String campiStr[] = UtilityTags.stringToArray(campi, ';');
    colonne = new HashMap<String, DataColumn>();
    for (int i = 0; i < campiStr.length; i++) {
      if (campiStr[i].indexOf('=') >= 0) {
        String campo = campiStr[i].substring(0, campiStr[i].indexOf('='));
        String valore = campiStr[i].substring(campiStr[i].indexOf('=') + 1);
        DataColumn col = new DataColumn(campo,
            new JdbcParametro(valore));
        colonne.put(col.toString().toUpperCase(), col);
      }
    }
  }

  /**
   * Creazione delle colonne con valori dalla select
   *
   * @param sql
   *        Manager dell'sql
   * @param entita
   *        Nome dell'entità toincipale
   * @param select
   * @param string
   * @param objects
   */
  public DataColumnContainer(SqlManager sql, String entita, String select,
      Object[] objects) throws GestoreException {
    this.init(sql, entita, select, objects);
  }

  private void init(SqlManager sql, String entita, String select,
      Object[] objects) throws GestoreException {
    colonne = new HashMap<String, DataColumn>();
    HashMap<?,?> mapCol;
    try {
      mapCol = sql.getHashMap(select, objects);
      for (Iterator<?> iter = mapCol.keySet().iterator(); iter.hasNext();) {
        String id = (String) iter.next();
        JdbcParametro par = (JdbcParametro) mapCol.get(id);
        if (id.indexOf('.') < 0) id = entita + '.' + id;
        this.addColumn(id, par);
        this.getColumn(id).setOriginalValue(this.getColumn(id).getValue());
      }
    } catch (Throwable e) {
      throw new GestoreException(
          "Errore nella creazione di un implementatore di colonne con valori !",
          "newDataColumn", e);
    }

  }

  /**
   * Costruttore con i parametri formattati come i parametri chiave
   *
   * @param sql
   *        SqlManager per l'esecuzione della select
   * @param entita
   *        Entità principale della select
   * @param sqlSelect
   *        Select con i parametri inseriti come #TABELLA.CAMPO{@link #addColumn(String, char)}in
   *        cui vengono sostituiti
   * @param parametri
   *        Parametri nel formato definito per i campi chiave nei tags
   */
  public DataColumnContainer(SqlManager sql, String entita, String sqlSelect,
      String parametri) throws GestoreException {
    HashMap<String, JdbcParametro> mapParams = UtilityTags.stringParamsToHashMap(parametri, null);
    Vector<JdbcParametro> vectParams = new Vector<JdbcParametro>();

    sqlSelect = UtilityTags.replaceParametri(vectParams, sqlSelect, mapParams);
    init(sql, entita, sqlSelect,
        UtilityTags.vectorParamToObjectArray(vectParams));
  }

  /**
   * Costruttore prendendo i campi da un'altro gestore
   *
   * @param impl
   * @throws GestoreException
   */
  public DataColumnContainer(DataColumnContainer impl) throws GestoreException {
    colonne = new HashMap<String, DataColumn>();
    for (Iterator<String> iter = impl.getColonne().keySet().iterator(); iter.hasNext();) {
      String campo = iter.next();
      this.addColumn(campo, impl.getColumn(campo));
    }
  }

  /**
   * Funzione che aggiunge una colonna all'elenco. Molto utile nell'update e
   * nell'inserimento
   *
   * @param nomeFisico
   * @param tipoCampo
   * @throws GestoreException
   */
  public void addColumn(String nomeFisico, char tipoCampo)
      throws GestoreException {
    if (nomeFisico == null || nomeFisico.indexOf('.') < 0)
      throw new GestoreException(
          "addColumn: Il nome delle colonna deve essere TABELLA.CAMPO e non <"
              + (nomeFisico == null ? "null" : nomeFisico)
              + "> !", "gestoreColumn.addColumn");
    // Aggiungo la colonna solo se non esiste di gia
    if (this.colonne.get(nomeFisico.toUpperCase()) == null) {
      DataColumn col = new DataColumn(null, nomeFisico,
          tipoCampo);
      colonne.put(col.toString().toUpperCase(), col);
    }
  }

  public void addColumn(String nomeFisico, char tipoCampo, Object valore)
      throws GestoreException {
    this.addColumn(nomeFisico, tipoCampo);
    this.setValue(nomeFisico, valore);
  }

  public void addColumn(String nomeFisico, Object valore)
      throws GestoreException {
    if (valore == null || valore instanceof String) {
      this.addColumn(nomeFisico, JdbcParametro.TIPO_TESTO, valore);
    } else if (valore instanceof Long) {
      this.addColumn(nomeFisico, JdbcParametro.TIPO_NUMERICO, valore);
    } else if (valore instanceof Double) {
      this.addColumn(nomeFisico, JdbcParametro.TIPO_DECIMALE, valore);
    } else if (valore instanceof Date) { //solitamente cmq è timestamp
      this.addColumn(nomeFisico, JdbcParametro.TIPO_DATA, valore);
    } else if (valore instanceof ByteArrayOutputStream) {
      this.addColumn(nomeFisico, JdbcParametro.TIPO_BINARIO, valore);
    } else if (valore instanceof JdbcParametro) {
      JdbcParametro param = (JdbcParametro) valore;
      this.addColumn(nomeFisico, param.getTipo(), param.getValue());
    } else {
      throw new GestoreException("Tipo di dato non supportato: "
          + valore.getClass().getName(), "ColImpladdColumn");
    }
  }

  public void addColumn(String nomeFisico, DataColumn colonna)
      throws GestoreException {
    this.addColumn(nomeFisico, colonna.getTipoCampo(),
        colonna.getValue().getValue());
    this.getColumn(nomeFisico).setOriginalValue(colonna.getOriginalValue());
    this.getColumn(nomeFisico).setChiave(colonna.isChiave());

  }

  /**
   * Funzione che estrae una colonna dall'elenco
   *
   * @param colonna
   * @return
   * @throws GestoreException
   */
  public DataColumn getColumn(String colonna) throws GestoreException {
    DataColumn col = null;
    if (colonna != null) {
      col = this.colonne.get(colonna.toUpperCase());
      if (col != null) return col;
      if (colonna.indexOf('.') < 0) {
        colonna = colonna.toUpperCase();
        // Se non è settato il nome delle tabella allora ricerca solo per nome
        for (Iterator<String> iter = this.colonne.keySet().iterator(); iter.hasNext();) {
          String nome = iter.next();
          col = this.colonne.get(nome);
          if (nome != null && nome.indexOf('.') >= 0) {
            nome = nome.substring(nome.indexOf('.') + 1);
            if (nome.equals(colonna)) {
              return col;
            }
          }
        }
        col = null;
      }
    }
    if (col == null)
      throw new GestoreException("La colonna non esiste nell'elenco ("
          + colonna
          + ") !", "columnimpl.nocampo");
    return col;
  }

  /**
   * Funzione che setta un valore su una colonna
   *
   * @param colonna
   * @param valore
   * @throws GestoreException
   */
  public void setValue(String colonna, Object valore) throws GestoreException {
    DataColumn col = this.getColumn(colonna);
    try {
      col.setObjectValue(valore);
    } catch (Throwable t) {
      throw new GestoreException("Errore durante il settaggio della colonna ("
          + colonna
          + ") !\n"
          + t.getMessage(), "columnimpl.setcampo", t);
    }
  }

  /**
   * Funzione che setta un valore su una colonna
   *
   * @param colonna
   * @param valore
   * @throws GestoreException
   */
  public void setOriginalValue(String colonna, JdbcParametro valore)
      throws GestoreException {
    DataColumn col = this.getColumn(colonna);
    try {
      col.setOriginalValue(valore);
    } catch (Throwable t) {
      throw new GestoreException("Errore durante il settaggio della colonna ("
          + colonna
          + ") !\n"
          + t.getMessage(), "columnimpl.setcampo", t);
    }
  }

  /**
   * Funzione che estrae il valore double di una colonna
   *
   * @param colonna
   * @return
   * @throws GestoreException
   */
  public Double getDouble(String colonna) throws GestoreException {
    DataColumn col = this.getColumn(colonna);
    if (col.getValue() == null) return null;
    return col.getValue().doubleValue();
  }

  /**
   * Funzione che non verifica il tipo di campo ed estrae il suo valore
   *
   * @param colonna
   *        Nome della colonna
   * @return Object Valore Oggetto della colonna
   * @throws GestoreException
   */
  public Object getObject(String colonna) throws GestoreException {
    DataColumn col = this.getColumn(colonna);
    if (col.getValue() == null) return null;
    return col.getValue().getValue();
  }

  /**
   * Funzione che estrae il valore Long di una colonna
   *
   * @param colonna
   * @return
   * @throws GestoreException
   */
  public Long getLong(String colonna) throws GestoreException {
    DataColumn col = this.getColumn(colonna);
    if (col.getValue() == null) return null;
    return col.getValue().longValue();
  }

  /**
   * Funzione che estrae il valore Timestamp di una colonna
   *
   * @param colonna
   * @return
   * @throws GestoreException
   */
  public Timestamp getData(String colonna) throws GestoreException {
    DataColumn col = this.getColumn(colonna);
    if (col.getValue() == null) return null;
    return col.getValue().dataValue();
  }

  /**
   * Funzione che estrae il valore stringa di una colonna
   *
   * @param colonna
   * @return
   * @throws GestoreException
   */
  public String getString(String colonna) throws GestoreException {
    DataColumn col = this.getColumn(colonna);
    if (col.getValue() == null) return null;
    return col.getValue().stringValue();
  }

  /**
   * Funzione che estrae il valore BLOB di una colonna
   *
   * @param colonna
   * @return
   * @throws GestoreException
   */
  public ByteArrayOutputStream getBLOB(String colonna) throws GestoreException {
    DataColumn col = this.getColumn(colonna);
    if (col.getValue() == null) return null;
    return col.getValue().byteArrayOutputStreamValue();
  }

  /**
   * Estrae un elenco di colonne con valore.
   *
   * @param entita
   *        Entità delle colonne da estrarre, null per tutte
   * @param key
   *        Intero che definisce che tipo di campi:
   *        <ul>
   *        <li>0 (default)Tutti i campi</li>
   *        <li>1 Solo i campi chiave </li>
   *        <li>2 Solo i campi non chiave</li>
   *        </ul>
   * @return
   */
  public DataColumn[] getColumns(String entita, int key) {
    Vector<DataColumn> cols = new Vector<DataColumn>();
    // Scorro tutte le colonne
    for (Iterator<String> iter = this.colonne.keySet().iterator(); iter.hasNext();) {
      DataColumn col = this.colonne.get(iter.next());
      if (entita == null
          || (col.getTable() != null && entita.equals(col.getTable().getName()))) {
        switch (key) {
        case 1: // Solo campi chiave
          if (col.isChiave()) cols.add(col);
          break;
        case 2: // Solo i campi non chiave
          if (!col.isChiave()) cols.add(col);
          break;
        default:
          cols.add(col);
        }
      }
    }
    // Creo il valore da restituire
    DataColumn ret[] = new DataColumn[cols.size()];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = cols.get(i);
    }
    return ret;
  }

  /**
   * Estrae e clona un elenco di colonne di un'entit&agrave; come campi di un'altra entit&agrave;.
   *
   * @param entitaPartenza
   *        Entit&agrave; delle colonne da estrarre
   * @param entitaDestinazione
   *        Entit&agrave; delle colonne da generare
   * @param key
   *        Intero che definisce che tipo di campi:
   *        <ul>
   *        <li>0 (default)Tutti i campi</li>
   *        <li>1 Solo i campi chiave </li>
   *        <li>2 Solo i campi non chiave</li>
   *        </ul>
   * @return
   */
  public DataColumn[] copyColumnsToEntity(String entitaPartenza, String entitaDestinazione, int key) {
    DataColumn[] dataColumns = this.getColumns(entitaPartenza, key);
    DataColumn[] newDatacol = new DataColumn[dataColumns.length];
    for (int i = 0; i < dataColumns.length; i++) {
        String nomeCol = dataColumns[i].getNomeFisico();
        nomeCol = StringUtils.replace(nomeCol, entitaPartenza, entitaDestinazione);
        DataColumn dc = new DataColumn(nomeCol, dataColumns[i].getValue());
        dc.setOriginalValue(dataColumns[i].getOriginalValue());
        dc.setChiave(dataColumns[i].isChiave());
        dc.setTipoCampo(dataColumns[i].getTipoCampo());
        newDatacol[i] = dc;
    }
    return newDatacol;
  }

  /**
   * Funzione che esegue l'eliminazione prendendo i campi chiave
   *
   * @param entita
   *        Entità in cui eseguire l'eliminazione
   * @param sqlManager
   */
  public void delete(String entita, SqlManager sqlManager) throws SQLException {
    Vector<JdbcParametro> params = new Vector<JdbcParametro>();
    StringBuffer lsSql = new StringBuffer("delete from ");
    lsSql.append(entita);
    lsSql.append(" where ");
    lsSql.append(columnsToString(getColumns(entita, 1), true, true, " = ?",
        " and ", params, 0));
    sqlManager.update(lsSql.toString(),
        UtilityTags.vectorParamToObjectArray(params), 1);
  }

  /**
   * Funzione che esegue l'inserimento nel database
   *
   * @param entita
   *        entità da dove estrarre i dati
   * @param sqlManager
   *        maneger per l'update
   */
  public void insert(String entita, SqlManager sqlManager) throws SQLException {
    DataColumn colonneTab[] = this.getColumns(entita, 0);
    Vector<JdbcParametro> params = new Vector<JdbcParametro>();
    StringBuffer lsSql = new StringBuffer("insert into ");
    lsSql.append(entita);
    lsSql.append("( ");
    lsSql.append(columnsToString(colonneTab, false, true, "", ", ", null, 1));
    lsSql.append(") values (");
    lsSql.append(columnsToString(colonneTab, false, false, "?", ", ", params, 1));
    lsSql.append(")");
    sqlManager.update(lsSql.toString(),
        UtilityTags.vectorParamToObjectArray(params), 1);
  }

  /**
   * Funzione che esegue l'update dei campi modificati su une tabella
   *
   * @param entita
   *        Nome dell'entita
   * @param sqlManager
   *        Manager per SQL
   * @throws SQLException
   */
  public void update(String entita, SqlManager sqlManager) throws SQLException {
    // ///////////////////////////////////////////////////////////////
    // Modifiche
    // ----------------
    // 04.06.2007: M.F. Per l'update permetto di eseguirlo anche sui campi
    // chiave (se modificati)
    // ////////////////////////////////////////////////////////////// /
    boolean modifiche = false;
    // Estraggo colo i campi dell'entità in considerazione
    DataColumn colKey[] = this.getColumns(entita, 1);
    DataColumn colUpd[] = this.getColumns(entita, 0);
    // Verifico l'esistenza di almeno una modifica (solo in quel caso
    // viene eseguito l'update)
    for (int i = 0; i < colUpd.length; i++) {
      if (colUpd[i].isModified()) {
        modifiche = true;
        break;
      }
    }
    // Eseguo l'update solo se ci sono campi cambiati
    if (modifiche) {
      Vector<JdbcParametro> params = new Vector<JdbcParametro>();
      StringBuffer lsSql = new StringBuffer("update ");
      lsSql.append(entita);
      lsSql.append(" set ");
      lsSql.append(columnsToString(colUpd, false, true, "= ?", ", ", params, 1));
      lsSql.append(" where ");
      lsSql.append(columnsToString(colKey, false, true, "= ?", " and ", params,
          3));
      sqlManager.update(lsSql.toString(),
          UtilityTags.vectorParamToObjectArray(params), 1);
    }

  }

  /**
   * Funzione che trasforma un array di colonne con valori in una stringa
   * settando il vettore con i parametri
   *
   * @param valori
   *        Elenco delle colonne con i valori
   * @param addNomeTab
   *        Flag che dice se deve o meno essere aggiunto il nome della tabella
   *        sul campo
   * @param addNomeCampo
   *        Flag che dice di aggiungere il nome del campo ai valori
   * @param post
   *        Stringa da mettere dopo il nome del campo
   * @param congiunzione
   *        Congiunzione
   * @param parametri
   *        Vettore che conterrà tutti i parametri
   * @param modo
   *        Modo di esecuzione<br>
   *        <b>0</b> Tutte<br>
   *        <b>1</b> Modificate <br>
   *        <b>2</b> Non modificate <br>
   *        <b>3</b> Tutte con valore originale <br>
   *        <b>4</b> Modificate con il valore originario <br>
   *        <b>5</b> Non modificate con il valore originario
   * @return sql creato
   */
  private String columnsToString(DataColumn[] valori,
      boolean addNomeTab, boolean addNomeCampo, String post,
      String congiunzione, Vector<JdbcParametro> parametri, int modo) {
    StringBuffer buf = new StringBuffer("");
    int inserted = 0;
    boolean doInsert = false;
    boolean original = modo > 2;
    for (int i = 0; i < valori.length; i++) {
      //if(valori[i].isChiave()){
      //  doInsert = true;
      //} else {
        switch (modo) {
        case 0: // Tutte
        case 3:
          doInsert = true;
          break;
        case 1: // Modificate
        case 4:
          doInsert = valori[i].isModified();
          break;
        case 2: // Non modificate
        case 5:
          doInsert = !valori[i].isModified();
          break;
        default:
          doInsert = false;
        }
      //}
      if (doInsert) {
        if (inserted > 0) buf.append(congiunzione);
        if (addNomeCampo) {
          if (addNomeTab)
            buf.append(valori[i].toString());
          else
            buf.append(valori[i].getName());
        }
        if (post.indexOf("?") >= 0 && parametri != null) {
          JdbcParametro valore = original
              ? valori[i].getOriginalValue()
              : valori[i].getValue();
          // Se è nullo metto direttamente nella stringa il valore
          // null. Se il valore è vuoto allora è come fosse null
          if (valore == null || valore.getValue() == null) {
            buf.append(StringUtils.replace(post, "?", "null"));
          } else {
            buf.append(post);
            parametri.add(valore);
          }
        } else
          buf.append(post);
        inserted++;
      }
    }
    return buf.toString();
  }

  /**
   * Funzione che verifica l'esistenza di una colonna
   *
   * @param colonna
   *        Nome delle colonna
   * @return true esiste; false se non esiste
   */
  public boolean isColumn(String colonna) {
    boolean esiste = false;
    try {
      this.getColumn(colonna);
      esiste = true;
    } catch (GestoreException e) {
      // Se non esiste la colonna allora restituisce false
    }
    return esiste;
  }

  /**
   * Verifica se una colonna è stata modificata. Se la colonna non esiste allora
   * restituisce false
   *
   * @param colonna
   *        Nome della colonna
   * @return true se modificata altrimenti false se non esiste o se non è stata
   *         modificata
   */
  public boolean isModifiedColumn(String colonna) {
    if (isColumn(colonna)) try {
      return getColumn(colonna).isModified();
    } catch (GestoreException e) {
    }
    return false;
  }

  /**
   * Verifica che almeno un campo (compresi i campi chiave) sia stato modificato
   *
   * @param entita
   *        entità da verificare se è stata modificata
   * @return Ritorna true se almeno un campo dell'entita specificato e' stato
   *         modificato, false altrimenti
   */
  public boolean isModifiedTable(String entita) {
    return this.isModifiedTable(entita, 0);
  }

  /**
   * Verifica che almeno un campo sia stato modificato
   *
   * @param entita
   *        entità da verificare se è stata modificata
   * @param tipoColonne
   *        Intero che definisce che tipo di campi controllare:
   *        <ul>
   *        <li>0 - Tutti i campi</li>
   *        <li>1 - Solo i campi chiave</li>
   *        <li>2 - Solo i campi non chiave</li>
   *        </ul>
   *
   * @return Ritorna true se almeno un campo dell'entita individuato e' stato
   *         modificato, false altrimenti
   */
  public boolean isModifiedTable(String entita, int tipoColonne) {
    boolean result = false;
    DataColumn cols[] = this.getColumns(entita, tipoColonne);
    for (int i = 0; i < cols.length && !result; i++) {
      if (cols[i].isModified()) result = true;
    }
    return result;
  }

  /**
   * @return the colonne
   */
  public HashMap<String, DataColumn> getColonne() {
    return colonne;
  }

  /**
   * Trasformazione in stringa dell'oggetto (solo per il debug)
   */
  @Override
  public String toString() {
    StringBuffer buf = new StringBuffer("");
    buf.append("[");
    for (Iterator<String> iter = this.colonne.keySet().iterator(); iter.hasNext();) {
      String campo = iter.next();
      DataColumn val = this.colonne.get(campo);
      buf.append("[");
      buf.append(campo);
      buf.append("=");
      if (val.getValue() != null)
        buf.append(val.getValue().toString(true));
      else
        buf.append("null");
      buf.append("{");
      if (val.getOriginalValue() != null)
        buf.append(val.getOriginalValue().toString(true));
      else
        buf.append("null");
      buf.append("}");
      buf.append("]");
    }
    buf.append("]");

    return buf.toString();
  }

  /**
   * Funzione che estrae un dato o dall'impl o se non esiste esegue la select e
   * restituisce il valore
   *
   * @param sql
   *        Sql manager per l'estrazione eventuale del dato dal database
   * @param campo
   *        Nome del campo nel formato "TABELLA.CAMPO"
   * @param where
   *        Eventuale where per l'estrazione del campo
   * @param params
   *        Parametri della where
   * @return
   */
  public Object getCampoObject(SqlManager sql, String campo, String where,
      Object[] params) throws GestoreException {
    if (isColumn(campo)) {
      return getObject(campo);
    } else {
      // Se non c'è il campo allora lo estraggo
      if (campo.indexOf('.') < 0)
        throw new GestoreException("Il nome del campo errato "
            + campo
            + " doveva essere TABELLA.CAMPO !", "getCampoObj");
      String lTable = campo.substring(0, campo.indexOf('.'));
      String lCampo = campo.substring(campo.indexOf('.') + 1);
      try {
        return sql.getObject("select "
            + lCampo
            + " from "
            + lTable
            + " where "
            + where, params);

      } catch (SQLException e) {
        throw new GestoreException("Errore nell'estrazione del valore "
            + campo
            + "; "
            + where
            + " !", "getCampoObj", e);
      }
    }
  }

  /**
   * Funzione che estrae un dato o dall'impl o se non esiste esegue la select e
   * restituisce il valore
   *
   * @param sql
   *        Sql manager per l'estrazione eventuale del dato dal database
   * @param campo
   *        Nome del campo nel formato "TABELLA.CAMPO"
   * @param where
   *        Eventuale where per l'estrazione del campo
   * @param params
   *        Parametri della where
   * @return
   */
  public String getCampoString(SqlManager sql, String campo, String where,
      Object[] params) throws GestoreException {
    Object obj = getCampoObject(sql, campo, where, params);
    if (obj != null) {
      if (obj instanceof String)
        return (String) obj;
      else
        throw new GestoreException("Tipo di campo inatteso "
            + obj.getClass().getName()
            + " !", "getCampoDouble");
    }
    return null;
  }

  /**
   * Funzione che estrae un dato o dall'impl o se non esiste esegue la select e
   * restituisce il valore
   *
   * @param sql
   *        Sql manager per l'estrazione eventuale del dato dal database
   * @param campo
   *        Nome del campo nel formato "TABELLA.CAMPO"
   * @param where
   *        Eventuale where per l'estrazione del campo
   * @param params
   *        Parametri della where
   * @return
   */
  public Double getCampoDouble(SqlManager sql, String campo, String where,
      Object[] params) throws GestoreException {
    Object obj = getCampoObject(sql, campo, where, params);
    if (obj != null) {
      if (obj instanceof Double)
        return (Double) obj;
      else if (obj instanceof Long)
        return new Double(((Long) obj).doubleValue());
      else if (obj instanceof Integer)
        return new Double(((Integer) obj).doubleValue());
      else
        throw new GestoreException("Tipo di campo inatteso "
            + obj.getClass().getName()
            + " !", "getCampoDouble");
    }
    return null;
  }

  /**
   * Funzione che setta il campo sull'impl (se esiste) mentre se non esiste
   * esegue l'update diretto sul database impostando il filtro impostato come
   * parametro
   *
   * @param sql
   *        SqlManager
   * @param campo
   *        Campo nel formato: TABELLA.CAMPO
   * @param valore
   *        Valore da impostare al campo
   * @param where
   *        Filtro per l'update sul valore se non esiste nell'impl
   * @param params
   *        Parametri della where
   */
  public void updateCampo(SqlManager sql, String campo, Object valore,
      String where, Object[] params) throws GestoreException {
    if (isColumn(campo)) {
      setValue(campo, valore);
    } else {
      // Se non c'è il campo allora lo estraggo
      if (campo.indexOf('.') < 0)
        throw new GestoreException("Il nome del campo errato "
            + campo
            + " doveva essere TABELLA.CAMPO !", "updateCampo");
      String lTable = campo.substring(0, campo.indexOf('.'));
      String lCampo = campo.substring(campo.indexOf('.') + 1);
      try {
        Object lPar[] = new Object[(params != null ? params.length : 0) + 1];
        lPar[0] = valore;
        for (int i = 0; i < (params != null ? params.length : 0); i++) {
          lPar[i + 1] = params[i];
        }
        sql.update("update "
            + lTable
            + " set "
            + lCampo
            + " = ? where "
            + where, lPar, 1);

      } catch (SQLException e) {
        throw new GestoreException("Errore nell'sellaggio del valore "
            + campo
            + "; "
            + where
            + " !", "updateCampo", e);
      }
    }

  }

  /**
   * Aggiunge le colonne all'impl prendendole dal request
   *
   * @param request
   *        Request in cui sono impostati i campi
   * @param elencoColonne
   *        Elenco dei campi divisi da ;
   * @param aggiornaColonneEsistenti
   *        se vale true allora aggiorna anche le colonne esistenti nell'impl,
   *        altrimenti esegue l'inserimento delle sole colonne nuove
   */
  public void addColumns(ServletRequest request, String elencoColonne,
      boolean aggiornaColonneEsistenti) {
    String colonne[] = UtilityTags.stringToArray(elencoColonne, ';');
    for (int i = 0; i < colonne.length; i++) {
      DataColumn col = UtilityStruts.getColumnWithValue(
          (HttpServletRequest) request, colonne[i]);
      if (col != null) {
        if (aggiornaColonneEsistenti
            || this.colonne.get(col.toString().toUpperCase()) == null)
          this.colonne.put(col.toString().toUpperCase(), col);
      }
    }

  }

  /**
   * Aggiunge le colonne all'impl prendendole dall'elenco in input
   *
   * @param elencoColonne
   *        elenco delle colonne da inserire
   * @param aggiornaColonneEsistenti
   *        se vale true allora aggiorna anche le colonne esistenti nell'impl,
   *        altrimenti esegue l'inserimento delle sole colonne nuove
   */
  public void addColumns(DataColumn[] elencoColonne,
      boolean aggiornaColonneEsistenti) {
    for (int i = 0; i < elencoColonne.length; i++) {
      DataColumn col = elencoColonne[i];
      if (col != null) {
        if (aggiornaColonneEsistenti
            || this.colonne.get(col.toString().toUpperCase()) == null)
          this.colonne.put(col.toString().toUpperCase(), col);
      }
    }
  }

  /**
   * Rimuove le colonne in input dall'impl a partire dai nomi delle colonne
   * stesse
   *
   * @param elencoColonne
   *        elenco dei nomi delle colonne da rimuovere
   */
  public void removeColumns(String[] elencoColonne) {
    if (elencoColonne != null) {
      for (int i = 0; i < elencoColonne.length; i++) {
        this.colonne.remove(elencoColonne[i]);
      }
    }
  }

  /**
   * Funzione che imposta i valori prelevandoli da un HashMap
   *
   * @param map
   */
  public void setValoriFromMap(HashMap<?,?> map, boolean sbiancaOriginale) {
    for (Iterator<?> iter = map.keySet().iterator(); iter.hasNext();) {
      Object key = iter.next();
      if (key != null && this.isColumn(key.toString())) {
        Object value = map.get(key);
        try {
          this.setValue(key.toString(), value);
          // Sbianco il valore originale
          if (sbiancaOriginale)
            this.getColumn(key.toString()).setObjectOriginalValue(null);
        } catch (GestoreException e) {
        }
      }
    }
  }

  /**
   * Estrae dall'oggetto tutti gli elementi il cui suffisso finale nel nome del
   * campo è uguale al parametro in input
   *
   * @param suffisso
   *        suffisso da ricercare
   * @param ritornaCampiConSuffisso
   *        true se si vuole ritornare i campi con il suffisso finale, false
   *        altrimenti (si ritornano i campi ripuliti del suffisso)
   * @return elenco di campi caratterizzati dal suffisso finale
   */
  public DataColumn[] getColumnsBySuffix(String suffisso,
      boolean ritornaCampiConSuffisso) {
    Vector<DataColumn> cols = new Vector<DataColumn>();
    DataColumn colonna = null;
    DataColumn nuovaColonna = null;
    String nomeColonna = null;
    // Scorro tutte le colonne
    for (Iterator<String> iter = this.colonne.keySet().iterator(); iter.hasNext();) {
      colonna = this.colonne.get(iter.next());
      if (colonna.getName().endsWith(suffisso)) {
        // se la colonna soddisfa il suffisso, determino il nome della colonna
        // destinazione, e creo una copia della colonna attualmente considerata
        if (ritornaCampiConSuffisso)
          nomeColonna = colonna.getName();
        else
          nomeColonna = colonna.getName().substring(0,
              colonna.getName().length() - suffisso.length());
        nuovaColonna = colonna.copy(nomeColonna);
        cols.add(nuovaColonna);
      }
    }
    // Creo il valore da restituire
    DataColumn ret[] = new DataColumn[cols.size()];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = cols.get(i);
    }
    return ret;
  }

}