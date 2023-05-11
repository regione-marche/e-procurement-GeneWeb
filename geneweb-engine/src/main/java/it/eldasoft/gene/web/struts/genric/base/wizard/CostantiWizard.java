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
package it.eldasoft.gene.web.struts.genric.base.wizard;


/**
 * Classe con le costanti java usate nel wizard per la creazione di report base
 *  
 * @author Luca.Giacomazzo
 */
public class CostantiWizard {

  /**
   * Codice inviato nel request e che individua la pagina del wizard per la
   * definizione dell'argomento per la creazione di un report base.
   */
  public static final String CODICE_PAGINA_ARGOMENTO = "ARG";
  
  /**
   * Codice inviato nel request e che individua la pagina del wizard per la
   * definizione dei campi per la creazione di un report base.
   */
  public static final String CODICE_PAGINA_CAMPI = "CAM";
  
  /**
   * Codice inviato nel request e che individua la pagina del wizard con la
   * domanda per la definizione o meno di filtri per la creazione di un report
   * base.
   */
  public static final String CODICE_PAGINA_DOMANDA_FILTRI = "FIL1";
  
  /**
   * Codice inviato nel request e che individua la pagina del wizard per la
   * definizione dei filtri per la creazione di un report base.
   */
  public static final String CODICE_PAGINA_FILTRO = "FIL2";

  /**
   * Codice inviato nel request e che individua la pagina del wizard per la
   * definizione dei filtri per la creazione di un report base.
   */
  public static final String CODICE_PAGINA_LISTA_FILTRI = "FIL3";
  
  /**
   * Codice inviato nel request e che individua la pagina del wizard con la
   * domanda per la definizione o meno di ordinamenti per la creazione di un
   * report base.
   */
  public static final String CODICE_PAGINA_DOMANDA_ORDINAMENTI = "ORD1";
  
  /**
   * Codice inviato nel request e che individua la pagina del wizard per la
   * definizione degli ordinamenti per la creazione di un report base.
   */
  public static final String CODICE_PAGINA_ORDINAMENTO = "ORD2";

  /**
   * Codice inviato nel request e che individua la pagina del wizard per la
   * definizione degli ordinamenti per la creazione di un report base.
   */
  public static final String CODICE_PAGINA_LISTA_ORDINAMENTI = "ORD3";

  /**
   * Codice inviato nel request e che individua la pagina del wizard per la
   * definizione dei titoli delle colonne di un report base.
   */
  public static final String CODICE_PAGINA_LAYOUT = "LAY";

  /**
   * Codice inviato nel request e che individua la pagina di pubblicazione del
   * wizard per la creazione di un report base.
   */
  public static final String CODICE_PAGINA_PUBBLICAZIONE = "PUB";
  
  /**
   * Codice inviato nel request e che individua la pagina del wizard per la
   * associazione dei gruppi al report base in creazione.
   */
  public static final String CODICE_PAGINA_GRUPPI = "GRP";

  /**
   * Codice inviato nel request e che individua la pagina del wizard per la 
   * definizione dei dati generali di un report base.
   */
  public static final String CODICE_PAGINA_DATI_GENERALI = "DG";
  
  // Voci che costituiscono la riga dell'avanzamento wizard
  public static final String TITOLO_ARGOMENTO    = "Argomento";
  public static final String TITOLO_CAMPI        = "Campi";
  public static final String TITOLO_FILTRI       = "Filtri";
  public static final String TITOLO_ORDINAMENTI  = "Ordinamenti";
  public static final String TITOLO_LAYOUT       = "Layout";
  public static final String TITOLO_PUBBLICA     = "Pubblicazione";
  public static final String TITOLO_GRUPPI       = "Gruppi";
  public static final String TITOLO_DATIGENERALI = "Dati Generali";

}