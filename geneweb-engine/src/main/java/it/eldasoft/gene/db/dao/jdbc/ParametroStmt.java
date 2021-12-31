/*
 * Created on 21-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao.jdbc;

import java.sql.Types;

import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityNumeri;

/**
 * Classe che individua un parametro da inviare ad una query per
 * l'inizializzazione di una statement
 * 
 * @author Stefano.Sabbadin
 */
public class ParametroStmt {

  /** Individua il tipo di dato Stringa */
  public static final short TIPO_STRINGA  = Campo.TIPO_STRINGA;

  /** Individua il tipo di dato Data */
  public static final short TIPO_DATA     = Campo.TIPO_DATA;

  /** Individua il tipo di dato Intero */
  public static final short TIPO_INTERO   = Campo.TIPO_INTERO;

  /** Individua il tipo di dato Decimale */
  public static final short TIPO_DECIMALE = Campo.TIPO_DECIMALE;

  /** Individua il tipo di dato Nota */
  public static final short TIPO_NOTA     = Campo.TIPO_NOTA;

  /** Codice da attribuire al parametro */
  private String            codice;

  /** Valore del parametro */
  private String            valore;

  /** Tipo del parametro */
  private short             tipoParametro;

  public ParametroStmt(String codice, short tipoParametro) {
    this(codice, null, tipoParametro);
  }

  public ParametroStmt(String codice, String valore, short tipoParametro) {
    this.codice = codice;
    this.valore = valore;
    this.tipoParametro = tipoParametro;
  }

  /**
   * @return Ritorna codice.
   */
  public String getCodice() {
    return codice;
  }

  /**
   * @return Ritorna tipoParametro.
   */
  public short getTipoParametro() {
    return tipoParametro;
  }

  /**
   * @return Ritorna valore.
   */
  public String getValore() {
    return valore;
  }

  /**
   * @return Ritorna il valore del parametro opportunamente castato al tipo di
   *         oggetto che lo rappresenta
   */
  public Object getValoreObject() {
    Object risultato = null;

    switch (this.tipoParametro) {
    case ParametroStmt.TIPO_DATA:
      risultato = UtilityDate.convertiData(this.valore,
          UtilityDate.FORMATO_GG_MM_AAAA);
      break;
    case ParametroStmt.TIPO_DECIMALE:
      risultato = UtilityNumeri.convertiDouble(this.valore);
    case ParametroStmt.TIPO_INTERO:
      risultato = UtilityNumeri.convertiIntero(this.valore);
      break;
    case ParametroStmt.TIPO_NOTA:
      risultato = this.valore;
      break;
    case ParametroStmt.TIPO_STRINGA:
      risultato = this.valore;
      break;
    }

    return risultato;
  }

  /**
   * @return Ritorna il tipo del parametro interpretabile dagli oggetti del
   *         package java.sql per eseguire l'interazione con la base dati
   */
  public int getTipoDatoDB() {
    int risultato = 0;

    switch (this.tipoParametro) {
    case ParametroStmt.TIPO_DATA:
      risultato = Types.DATE;
      break;
    case ParametroStmt.TIPO_DECIMALE:
      risultato = Types.DOUBLE;
    case ParametroStmt.TIPO_INTERO:
      risultato = Types.INTEGER;
      break;
    case ParametroStmt.TIPO_NOTA:
      risultato = Types.VARCHAR;
      break;
    case ParametroStmt.TIPO_STRINGA:
      risultato = Types.VARCHAR;
      break;
    }

    return risultato;
  }
}
