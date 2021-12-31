/*
 * Created on 10-07-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 * 
 * Created by Luca Giacomazzo
 */
package it.eldasoft.gene.db.dao;

import it.eldasoft.utils.eccezioni.EldaCheckedException;

/**
 * 
 * @author Luca.Giacomazzo
 */
public class QueryDaoException extends EldaCheckedException {

  /**   UID   */
  private static final long serialVersionUID = -8954032061208417905L;

  /** Famiglia di eccezioni legate alla gestione delle classi che implementano
   * l'interfaccia QueryDao
   */
  private static final String FAMIGLIA       = "QUERY_DAO";
  
  /** Codici di errore */
  public static final String CODICE_ERRORE_MAX_RECORD_ESTRAIBILI_NON_VALIDO           = "MAX_RECORD_ESTRAIBILI_NON_VALIDO";
  public static final String CODICE_ERRORE_NUMERO_PAGINA_NON_VALIDO                   = "NUMERO_PAGINA_NON_VALIDO";
  public static final String CODICE_ERRORE_NUMERO_RECORD_PER_PAGINA_NON_VALIDO        = "NUMERO_RECORD_PER_PAGINA_NON_VALIDO";
  public static final String CODICE_ERRORE_NUMERO_RECORD_ESTRATTI_MAGGIORE_VALORE_MAX = "NUMERO_RECORD_ESTRATTI_MAGGIORE_VALORE_MAX";
  public static final String CODICE_ERRORE_TIPO_DATO_NON_RICONOSCIUTO                 = "TIPO_DATO_NON_RICONOSCIUTO";
  
  // Codice di errore usato solo nei report SQL per indicare il caso di una query sintatticamente non valida
  public static final String CODICE_ERRORE_REPORT_SQL_QUERY_NON_VALIDA                = "REPORT_SQL_QUERY_NON_VALIDA";
  
  public String getFamiglia() {
    return FAMIGLIA;
  }

  public QueryDaoException(String codiceErrore) {
    super(codiceErrore);
  }

  public QueryDaoException(String codiceErrore, Throwable e) {
    super(codiceErrore, e);
  }

  public QueryDaoException(String codiceErrore, String message) {
    super(codiceErrore, message);
  }

  public QueryDaoException(String codiceErrore, String message, Throwable e) {
    super(codiceErrore, message, e);
  }

}