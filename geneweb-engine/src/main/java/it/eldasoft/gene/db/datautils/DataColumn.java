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

import it.eldasoft.gene.db.sql.sqlparser.JdbcColumn;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.db.sql.sqlparser.JdbcTable;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.sql.Timestamp;

/**
 * Estensione della classe colonna con l'aggiunta del valore originale ed il
 * valore nuovo
 * 
 * @author cit_franceschin
 * @author Stefano.Sabbadin
 */
public class DataColumn extends JdbcColumn {

  // ************************************************************
  // Storia Modifiche:
  // Data Utente Descrizione
  // 23/10/2006 M.F. Aggiunta del settaggio se si tratta o meno del campo
  // chiave
  // 09/11/2006 M.F. Aggiunta del tipo di campo richiesto in column with value
  // ************************************************************

  private JdbcParametro originalValue;

  private JdbcParametro value;

  private boolean       chiave;

  private char          tipoCampo;

  public DataColumn(JdbcTable table, String columnName, char tipoCampo) {
    super(table, columnName);
    this.originalValue = null;
    this.value = null;
    this.tipoCampo = tipoCampo;
  }

  /**
   * Costruttore con nome campo e valore del campo
   * 
   * @param columnName
   *        Nome del campo nel formato TABELLA.CAMPO
   * @param value
   *        Valore da impostare nel campo
   */
  public DataColumn(String columnName, JdbcParametro value) {
    super(null, columnName);
    this.originalValue = null;
    this.value = value;
    this.tipoCampo = value.getTipo();
  }

  /**
   * Crea una copia dell'elemento rinominandolo in base al parametro in input
   * 
   * @param columnName
   *        nuovo nome di colonna
   * @return oggetto di tipo colonna con valore, clonato dall'oggetto attuale
   *         eccetto nel nome
   */
  public DataColumn copy(String columnName) {
    DataColumn copia = new DataColumn(this.getTable(),
        columnName, this.tipoCampo);
    copia.originalValue = this.originalValue;
    copia.value = this.value;
    copia.chiave = this.chiave;
    return copia;
  }

  /**
   * @return Returns the originalValue.
   */
  public JdbcParametro getOriginalValue() {
    return originalValue;
  }

  /**
   * @param originalValue
   *        The originalValue to set.
   */
  public void setOriginalValue(JdbcParametro originalValue) {
    // Se il tipo è diverso dalla colonna allora creo il tipo adeguato
    if (originalValue != null && this.getTipoCampo() != originalValue.getTipo()) {
      this.originalValue = JdbcParametro.getParametro(this.getTipoCampo(),
          originalValue.toString(false));
      return;
    }
    this.originalValue = originalValue;
  }

  /**
   * @return Returns the value.
   */
  public JdbcParametro getValue() {
    return value;
  }

  /**
   * @param value
   *        The value to set.
   */
  public void setValue(JdbcParametro value) {
    // Se il tipo è diverso dalla colonna allora creo il tipo adeguato
    if (value != null && this.getTipoCampo() != value.getTipo()) {
      this.value = JdbcParametro.getParametro(this.getTipoCampo(),
          value.toString(false));
      return;
    }
    this.value = value;
  }

  public void setObjectValue(Object value) {
    if (value == null) {
      this.setValue(new JdbcParametro(this.getTipoCampo(), null));
      return;
    }
    if (value instanceof JdbcParametro) {
      this.setValue((JdbcParametro) value);
      return;
    }
    char tipo = JdbcParametro.TIPO_TESTO;
    if (value instanceof String) {
      tipo = JdbcParametro.TIPO_TESTO;
    } else if (value instanceof Double) {
      tipo = JdbcParametro.TIPO_DECIMALE;
    } else if (value instanceof Long) {
      tipo = JdbcParametro.TIPO_NUMERICO;
    } else if (value instanceof ByteArrayOutputStream) {
      tipo = JdbcParametro.TIPO_BINARIO;
    } else if (value instanceof Date) {
      //solitamente è timestamp, per cui viene convertito in tale formato
      tipo = JdbcParametro.TIPO_DATA;
      value = new Timestamp(((Date) value).getTime());
    } else {
      throw new RuntimeException("Tipo di dato non supportato: "
          + value.getClass().getName());
    }
    this.setValue(new JdbcParametro(tipo, value));

  }

  public void setObjectOriginalValue(Object value) {
    if (value == null) {
      this.setOriginalValue(null);
      return;
    }
    char tipo = JdbcParametro.TIPO_TESTO;
    if (value instanceof String) {
      tipo = JdbcParametro.TIPO_TESTO;
    } else if (value instanceof Double) {
      tipo = JdbcParametro.TIPO_DECIMALE;
    } else if (value instanceof Long) {
      tipo = JdbcParametro.TIPO_NUMERICO;
    } else if (value instanceof ByteArrayOutputStream) {
      tipo = JdbcParametro.TIPO_BINARIO;
    } else if (value instanceof Date) {
      //solitamente è timestamp, per cui viene convertito in tale formato
      tipo = JdbcParametro.TIPO_DATA;
      value = new Timestamp(((Date) value).getTime());
    } else if (value instanceof JdbcParametro) {
      this.setOriginalValue((JdbcParametro) value);
      return;
    } else {
      throw new RuntimeException("Tipo di dato non supportato: "
          + value.getClass().getName());
    }
    this.setOriginalValue(new JdbcParametro(tipo, value));

  }

  public boolean isModified() {
    Object value = null;
    Object original = null;
    if (this.value != null) value = this.value.getValue();
    if (this.originalValue != null) original = this.originalValue.getValue();

    if (value != null && original != null)
      return !value.toString().equals(original.toString());

    return !(value == null && original == null);
  }

  /**
   * @return Returns the chiave.
   */
  public boolean isChiave() {
    return chiave;
  }

  /**
   * @param chiave
   *        The chiave to set.
   */
  public void setChiave(boolean chiave) {
    this.chiave = chiave;
  }

  /**
   * @return Returns the tipoCampo.
   */
  public char getTipoCampo() {
    return tipoCampo;
  }

  /**
   * @param tipoCampo
   *        The tipoCampo to set.
   */
  public void setTipoCampo(char tipoCampo) {
    this.tipoCampo = tipoCampo;
  }

  public String getNomeFisico() {
    StringBuffer buf = new StringBuffer("");
    if (getTable() != null) {
      buf.append(getTable().getName());
      buf.append(".");
    }
    buf.append(getName());
    return buf.toString();
  }

}
