/*
 * Created on 07-mag-2007
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.console.web.struts.schedric.wizard;


/**
 * Classe con le costanti java usate nel wizard per la creazione di report base
 *  
 * @author Luca.Giacomazzo
 */
public class CostantiWizard {

  /**
   * Codice inviato nel request e che individua la pagina del wizard per la
   * selezione del report da schedulare
   */
  public static final String CODICE_PAGINA_REPORT = "REP";
  
  /**
   * Codice inviato nel request e che individua la pagina del wizard per la
   * frequenza con cui si deve effettuare la schedulazione
   */
  public static final String CODICE_PAGINA_FREQUENZA = "FRE";
  
  /**
   * Codice inviato nel request e che individua la pagina del wizard per la 
   * configurazione della frequenza giornaliera
   */
  public static final String CODICE_PAGINA_GIORNO = "GIO";
  
  /**
   * Codice inviato nel request e che individua la pagina del wizard per la 
   * configurazione della frequenza settimanale
   */
  public static final String CODICE_PAGINA_SETTIMANA = "SET";

  /**
   * Codice inviato nel request e che individua la pagina del wizard per la 
   * configurazione della frequenza mensile
   */
  public static final String CODICE_PAGINA_MESE = "MES";
  
  /**
   * Codice inviato nel request e che individua la pagina del wizard per la 
   * configurazione dell'unica esecuzione
   */
  public static final String CODICE_PAGINA_UNICA = "UNI";
  
  /**
   * Codice inviato nel request e che individua la pagina del wizard per la
   * definizione dei risulati delle schedulazioni
   */
  public static final String CODICE_PAGINA_RISULTATO = "RIS";

  /**
   * Codice inviato nel request e che individua la pagina del wizard per la
   * definizione del titolo
   */
  public static final String CODICE_PAGINA_TITOLO = "TIT";

    
  // Voci che costituiscono la riga dell'avanzamento wizard
  public static final String TITOLO_REPORT         = "Report";
  public static final String TITOLO_FREQUENZA      = "Frequenza";
  public static final String TITOLO_SCHEDULAZIONE  = "Schedulazione";
  public static final String TITOLO_RISULTATO      = "Risultato";
  public static final String TITOLO_TITOLO         = "Dati generali";
  
}