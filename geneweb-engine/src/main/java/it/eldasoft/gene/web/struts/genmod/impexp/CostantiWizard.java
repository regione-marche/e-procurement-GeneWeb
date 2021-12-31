/*
 * Created on 20-ago-2007
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod.impexp;

/**
 * Classe con le costanti usate nel wizard per la creazione di importazione
 * della definizione di un modello
 *  
 * @author Francesco.DeFilippis
 */
public class CostantiWizard {

  /**
   * Codice inviato nel request che individua la pagina del wizard per la 
   */
  public static final String CODICE_PAGINA_UPLOAD = "UPL";
  /**
   * Codice inviato nel request e che individua la pagina del wizard per la 
   * definizione dei dati generali di un modello.
   */
  public static final String CODICE_PAGINA_DATI_GENERALI = "DG";
  
  /**
   * Codice inviato nel request e che individua la pagina di pubblicazione del
   * wizard per l'importazione della definizione di un modello.
   */
  public static final String CODICE_PAGINA_PUBBLICAZIONE = "PUB";
  
  /**
   * Codice inviato nel request e che individua la pagina del wizard per la
   * associazione dei gruppi al modello in creazione.
   */
  public static final String CODICE_PAGINA_GRUPPI = "GRP";
  
  /**
   * Codice inviato nel request e che individua la pagina del wizard di export
   * modello per trovare il modello da esportare
   */
  public static final String CODICE_PAGINA_TROVA_MODELLI_PER_EXPORT = "TRV";
  
  /**
   * Codice inviato nel request e che individua la pagina del wizard di export
   * modello con la lista dei modelli che si possono esportare
   */
  public static final String CODICE_PAGINA_LISTA_MODELLI_PER_EXPORT = "LIS";
  
  // Voci che costituiscono la riga dell'avanzamento wizard
  public static final String TITOLO_UPLOAD       = "File";
  public static final String TITOLO_DATIGENERALI = "Dati generali";
  public static final String TITOLO_PUBBLICA     = "Pubblicazione";
  public static final String TITOLO_GRUPPI       = "Gruppi";
  
  public static final String TITOLO_TROVA_MODELLI_PER_EXPORT = "Trova";
  public static final String TITOLO_LISTA_MODELLI_PER_EXPORT = "Lista";
  
  // Target delle diverse chiamate nel wizard
  protected static final String SUCCESS_UPLOAD           = "successUpload";
  protected static final String SUCCESS_DATI_GENERALI    = "successDatiGenerali"; 
  protected static final String SUCCESS_DOMANDA_PUBBLICA = "successDomandaPub";
  protected static final String SUCCESS_EDIT_GRUPPI      = "successEditGrp";
  protected static final String SUCCESS_FINE             = "successFine";
  protected static final String ERROR_IMPORT_MODELLI      = "errorImport";
  protected static final String ERROR_EXPORT_MODELLI      = "errorExport";
  
  // Tipi di import possibili
  public static final String IMPORT_SOVRASCRIVI_ESISTENTE   = "update";
  public static final String IMPORT_SOVRASCRIVI_PARZIALE    = "updateParziale";
  public static final String IMPORT_INSERT_CON_NUOVO_TITOLO = "insertConNuovoTitolo";
  public static final String IMPORT_INSERT_NUOVO_MODELLO     = "insert";
  
}