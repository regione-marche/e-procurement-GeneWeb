/*
 * Created on 03-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.docass;

/**
 * Costanti in uso per la gestione dei documenti associati.
 *
 * @author Luca Giacomazzo
 */
public class CostantiDocumentiAssociati {

  /**
   * Chiave della property contenente il valore del path di output per i
   * documenti associati lato server
   */
  public static final String PROP_PATH_DOCUMENTI = "it.eldasoft.documentiAssociati";
  
  /**
   * Chiave della property contenente il valore che definisce se rendere attiva la gestione 
   * dei documenti su database e non su filesystem.
   * Se la proprieta' vale 1 allora si abilita la gestione dei documenti in database.
   */
  public static final String PROP_DOCUMENTI_DB = "it.eldasoft.documentiAssociatiDB";
  
  /**
   * Chiave della property contenente il valore del path di output per i
   * documenti associati lato client
   */
  public static final String PROP_PATH_DOCUMENTI_CLIENT = "it.eldasoft.documentiAssociati.client";

  /**
   * Chiave della property contenente il valore della che imposta il download
   * automatico del documento associato
   */
  public static final String PROP_DOWNLOAD_DOCUMENTI_ASSOCIATI = "it.eldasoft.downloadDocumentiAssociati";

  /**
   * Stringa settata nel file di properties quando il client può solo
   * fare l'apertura diretta del documento associato.
   */
  public static final String DISABILITA_DOWNLOAD = "0";

  /**
   * Path di default dei documenti associati
   */
  public static final String PATH_DOCUMENTI_DEFAULT = "[default]";
}