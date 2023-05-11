/*
 * Created on 04-09-2006
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
package it.eldasoft.gene.web.struts.genric.filtro;

import it.eldasoft.utils.eccezioni.EldaCheckedException;

/**
 * Classe di gestione delle eccezioni per i controlli sui filtri delle ricerche
 * 
 * @author Francesco De Filippis
 *
 */
public class ControlloFiltriException extends EldaCheckedException {

  /**   UID   */
  private static final long  serialVersionUID = 3546170096751206948L;
  
  /** Famiglia di eccezioni legate alla gestione del file system */
  private static final String FAMIGLIA        = "CONTROLLO_FILTRI";
  
  /** Codici di errore */
  
  public static final String CODICE_ERRORE_PARENTESI_APERTA          = "PARENTESI_APERTA";
  public static final String CODICE_ERRORE_PARENTESI_CHIUSA          = "PARENTESI_CHIUSA";
  public static final String CODICE_ERRORE_OPERATORE_NOT             = "OPERATORE_NOT";
  public static final String CODICE_ERRORE_OPERATORI                 = "OPERATORI";
  public static final String CODICE_ERRORE_CONDIZIONE                = "CONDIZIONE";
  public static final String CODICE_ERRORE_ELEMENTO_INIZIALE         = "INIZIALE";
  public static final String CODICE_ERRORE_ELEMENTO_FINALE           = "FINALE";
  public static final String CODICE_ERRORE_NUMERO_PARENTESI_APERTE   = "MAGGIORANZA_PARENTESI_APERTE";
  public static final String CODICE_ERRORE_NUMERO_PARENTESI_CHIUSE   = "MAGGIORANZA_PARENTESI_CHIUSE";
  
  
  /**
   * @see it.eldasoft.utils.eccezioni.EldaCheckedException#getFamiglia()
   */  
  public String getFamiglia() {
    return ControlloFiltriException.FAMIGLIA;
  }
  
  public ControlloFiltriException(String codiceErrore) {
    super(codiceErrore);
  }

  public ControlloFiltriException(String codiceErrore, Throwable e) {
    super(codiceErrore, e);
  }

  public ControlloFiltriException(String codiceErrore, String message) {
    super(codiceErrore, message);
  }

  public ControlloFiltriException(String codiceErrore, String message, Throwable e) {
    super(codiceErrore, message, e);
  }
  
}