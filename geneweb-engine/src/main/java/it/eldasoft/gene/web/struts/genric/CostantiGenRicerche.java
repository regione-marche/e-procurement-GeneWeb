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
package it.eldasoft.gene.web.struts.genric;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.utils.sql.comp.SqlElementoCondizione;

/**
 * Costanti in uso nelle pagine di Generatore Ricerche.
 *
 * @author Luca Giacomazzo
 */
public class CostantiGenRicerche {

  /**
   * Chiave con cui viene inserito in SESSIONE l'oggetto contenente i parametri
   * per la ricerca nella pagina Trova Ricerche.
   */
  public static final String   TROVA_RICERCHE                         = CostantiGenerali.PREFISSO_OGGETTO_TROVA
                                                                          + "Ricerca";

  /**
   * Chiave con cui viene inserito in SESSIONE l'oggetto contenente i parametri
   * per la ricerca nella pagina Trova Ricerche per l'esportazione della
   * definizione di un report.
   */
  public static final String    TROVA_RICERCHE_EXPORT                 = CostantiGenerali.PREFISSO_OGGETTO_TROVA
                                                                          + "RicercaExport";

  /** Chiave dell'istanza dell'oggetto GestioneTab in sessione */
  public static final String   NOME_GESTORE_TAB                       = "gestoreTab";

  /* Voci del menu a tab delle pagine di dettaglio ricerche/crea ricerca */
  /** Etichetta per il tab di dati generali della ricerca */
  public static final String   TAB_DATI_GENERALI                      = "Dati Generali";
  /** Etichetta per il tab gruppi della ricerca */
  public static final String   TAB_GRUPPI                             = "Gruppi";
  /** Etichetta per il tab di argomenti della ricerca */
  public static final String   TAB_ARGOMENTI                          = "Argomenti";
  /** Etichetta per il tab di campi della ricerca */
  public static final String   TAB_CAMPI                              = "Campi";
  /** Etichetta per il tab di join della ricerca */
  public static final String   TAB_JOIN                               = "Join";
  /** Etichetta per il tab di parametri della ricerca */
  public static final String   TAB_PARAMETRI                          = "Parametri";
  /** Etichetta per il tab di filtri della ricerca */
  public static final String   TAB_FILTRI                             = "Filtri";
  /** Etichetta per il tab di ordinamenti della ricerca */
  public static final String   TAB_ORDINAMENTI                        = "Ordinamenti";
  /** Etichetta per il tab di layout della ricerca */
  public static final String   TAB_LAYOUT                             = "Layout";
  /** Etichetta per il tab del sql della ricerca */
  public static final String   TAB_SQL                                = "Sql";

  /** Carattere separatore tra schema, tabella e campo */
  public static final String SEPARATORE_SCHEMA_TABELLA_CAMPO          = ".";

  /**
   * Chiave dell'istanza dell'oggetto ContenitoreDatiRicercaSession presente in
   * sessione
   */
  public static final String   OGGETTO_DETTAGLIO =
        CostantiGenerali.PREFISSO_OGGETTO_DETTAGLIO + "Ricerca";

  /**
   * Chiave dell'ID della precedente ricerca: si mette in sessione questa
   * informazione per permettere all'utente di ripristinare la precedente ricerca
   * qualora annullasse l'operazione di creazione di una nuova ricerca
   */
  public static final String   ID_RICERCA_PRECEDENTE = "RicercaPrecedente" +
        CostantiGenerali.PREFISSO_OGGETTO_DETTAGLIO;

  /**
   * Property indicante il suffisso da porre nel titolo della ricerca nel
   * momento in cui si eseguono delle modifiche al dato in sessione, in modo da
   * dare un'evidenza visiva all'utente finale
   */
  public static final String   LABEL_SUFFISSO_NOME_RICERCA_IN_MODIFICA = "label.generatoreRicerche.titolo.evidenzaModifica";

  /**
   * Chiave dell'istanza dell'oggetto presente in sessione contente la lista dei
   * valori attribuiti a parametri necessari ad una ricerca in fase di
   * estrazione
   */
  public static final String   PARAMETRI_PER_ESTRAZIONE               = CostantiGenerali.PREFISSO_OGGETTO_DETTAGLIO
                                                                          + "ParametriPerEstrazione";

  /**
   * Etichetta necessaria per filtrare i tipi di parametro da presentare nella
   * comboBox Tipo delle pagine di add e edit Parametro.
   */
  public static final String   TIPO_VALORE_TABELLATO                  = "G_x01";

  /**
   * Etichetta necessaria per filtrare i tabellati da presentare nella comboBox
   * Tabellato delle pagine di add e edit Parametro.
   */
  public static final String   TIPO_TABELLATO                         = "G__";

  /**
   * Etichetta usata per definire gli attributi codiceParametro e nome
   * dell'oggetto ParametroRicercaForm e l'attributo ParametroConfronto
   * nell'oggetto FiltroRicercaForm
   */
  public final static String   CODICE_PARAMETRO                       = "PARAMETRO";

  public static final String STR_OPERATORI_PARENTESI = SqlElementoCondizione.STR_OPERATORE_PARENTESI_APERTA
  + SqlElementoCondizione.STR_OPERATORE_PARENTESI_CHIUSA;

  public static final Integer  VERSIONE_REPORT                        = new Integer(3);

  /**
   * Voci con cui popolare la combobox Operatore nelle pagine 'addFiltroRicerca'
   * e 'editFiltroRicerca'
   */
  public static final String[] CBX_OPERATORI_VALUE                    = new String[] {
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_UGUALE,            //0
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_DIVERSO,           //1
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MAGGIORE,          //2
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MAGGIORE_UGUALE,   //3
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MINORE,            //4
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MINORE_UGUALE,     //5
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_NULL,              //6
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_NOT_NULL,          //7
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MATCH,             //8
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_NOT_MATCH,         //9
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_IN,                //10
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_NOT_IN,            //11
      SqlElementoCondizione.STR_OPERATORE_LOGICO_AND,                  //12
      SqlElementoCondizione.STR_OPERATORE_LOGICO_OR,                   //13
      SqlElementoCondizione.STR_OPERATORE_LOGICO_NOT,                  //14
      CostantiGenRicerche.STR_OPERATORI_PARENTESI,                     //15
      SqlElementoCondizione.STR_OPERATORE_PARENTESI_APERTA,            //16
      SqlElementoCondizione.STR_OPERATORE_PARENTESI_CHIUSA         };  //17

  /**
   * Voci con cui popolare la combobox Operatore nelle pagine 'addFiltroRicerca'
   * e 'editFiltroRicerca'
   */
  public static final String[] DESCRIZIONE_OPERATORI                  = new String[] {
      "Uguale", "Diverso", "Maggiore", "Maggiore uguale", "Minore",
      "Minore uguale", "Non valorizzato", "Valorizzato", "Contiene",
      "Non contiene", "Appartiene", "Non appartiene", "E", "Oppure", "Non",
      "Parentesi Aperta e Chiusa", "Parentesi Aperta", "Parentesi Chiusa"};

  /**
   * Voci con cui popolare la combobox Operatore nelle pagine 'addFiltroRicerca'
   * e 'editFiltroRicerca'
   */
  public static final String[] CBX_OPERATORI_LABEL                    = new String[] {
      CBX_OPERATORI_VALUE[0] + " - " + DESCRIZIONE_OPERATORI[0],
      CBX_OPERATORI_VALUE[1] + " - " + DESCRIZIONE_OPERATORI[1],
      CBX_OPERATORI_VALUE[2] + " - " + DESCRIZIONE_OPERATORI[2],
      CBX_OPERATORI_VALUE[3] + " - " + DESCRIZIONE_OPERATORI[3],
      CBX_OPERATORI_VALUE[4] + " - " + DESCRIZIONE_OPERATORI[4],
      CBX_OPERATORI_VALUE[5] + " - " + DESCRIZIONE_OPERATORI[5],
      CBX_OPERATORI_VALUE[6] + " - " + DESCRIZIONE_OPERATORI[6],
      CBX_OPERATORI_VALUE[7] + " - " + DESCRIZIONE_OPERATORI[7],
      CBX_OPERATORI_VALUE[8] + " - " + DESCRIZIONE_OPERATORI[8],
      CBX_OPERATORI_VALUE[9] + " - " + DESCRIZIONE_OPERATORI[9],
      CBX_OPERATORI_VALUE[10] + " - " + DESCRIZIONE_OPERATORI[10],
      CBX_OPERATORI_VALUE[11] + " - " + DESCRIZIONE_OPERATORI[11],
      CBX_OPERATORI_VALUE[12] + " - " + DESCRIZIONE_OPERATORI[12],
      CBX_OPERATORI_VALUE[13] + " - " + DESCRIZIONE_OPERATORI[13],
      CBX_OPERATORI_VALUE[14] + " - " + DESCRIZIONE_OPERATORI[14],
      CBX_OPERATORI_VALUE[15] + " - " + DESCRIZIONE_OPERATORI[15],
      CBX_OPERATORI_VALUE[16] + " - " + DESCRIZIONE_OPERATORI[16],
      CBX_OPERATORI_VALUE[17] + " - " + DESCRIZIONE_OPERATORI[17] };

  /**
   * Tipi di famiglia di report in linea con il campo tab1tip del tabellato
   * TAB1 e codice 'W0001'
   */
  public static final int REPORT_BASE      = 0;
  public static final int REPORT_AVANZATO  = 1;
  public static final int REPORT_PROSPETTO = 2;
  public static final int REPORT_SQL       = 4;

  /** Lunghezza massima del campo TITCOLONNA in W_RICCAMPI */
  public static final int MAX_LEN_TITOLO_COLONNA = 60;

  /**
   * Voci con cui popolare la combobox Operatore nelle pagine 'addFiltroRicerca'
   * e 'editFiltroRicerca' per le ricerche base
   */
  public static final String[] CBX_OPERATORI_VALUE_REPORT_BASE = new String[] {
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_UGUALE,
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_DIVERSO,
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MAGGIORE,
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MAGGIORE_UGUALE,
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MINORE,
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MINORE_UGUALE,
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MATCH,
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_NOT_MATCH, /*,
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_IN,
      SqlElementoCondizione.STR_OPERATORE_CONFRONTO_NOT_IN*/
      };

  /**
   * Voci con cui popolare la combobox Operatore nelle pagine 'addFiltroRicerca'
   * e 'editFiltroRicerca' per le ricerche base
   */
  public static final String[] DESCRIZIONE_OPERATORI_REPORT_BASE  = new String[] {
      "Uguale", "Diverso", "Maggiore", "Maggiore uguale", "Minore",
      "Minore uguale", "Contiene", "Non contiene"/*,"Appartiene", "Non appartiene" */};

  /**
   * Voci con cui popolare la combobox Operatore nelle pagine 'addFiltroRicerca'
   * e 'editFiltroRicerca' per le ricerche base
   */
  public static final String[] CBX_OPERATORI_LABEL_REPORT_BASE = new String[] {
      CBX_OPERATORI_VALUE_REPORT_BASE[0] + " - " + DESCRIZIONE_OPERATORI_REPORT_BASE[0],
      CBX_OPERATORI_VALUE_REPORT_BASE[1] + " - " + DESCRIZIONE_OPERATORI_REPORT_BASE[1],
      CBX_OPERATORI_VALUE_REPORT_BASE[2] + " - " + DESCRIZIONE_OPERATORI_REPORT_BASE[2],
      CBX_OPERATORI_VALUE_REPORT_BASE[3] + " - " + DESCRIZIONE_OPERATORI_REPORT_BASE[3],
      CBX_OPERATORI_VALUE_REPORT_BASE[4] + " - " + DESCRIZIONE_OPERATORI_REPORT_BASE[4],
      CBX_OPERATORI_VALUE_REPORT_BASE[5] + " - " + DESCRIZIONE_OPERATORI_REPORT_BASE[5],
      CBX_OPERATORI_VALUE_REPORT_BASE[6] + " - " + DESCRIZIONE_OPERATORI_REPORT_BASE[6],
      CBX_OPERATORI_VALUE_REPORT_BASE[7] + " - " + DESCRIZIONE_OPERATORI_REPORT_BASE[7] /*,
      CBX_OPERATORI_VALUE_REPORT_BASE[10] + " - " + DESCRIZIONE_OPERATORI_REPORT_BASE[10]
      CBX_OPERATORI_VALUE_REPORT_BASE[11] + " - " + DESCRIZIONE_OPERATORI_REPORT_BASE[11]*/};

  
  /**
   * Property per indicare l'elenco delle tabelle da tracciare nella LOGEVENTI nell'esecuzione di report.
   */
  public static final String PROPERTY_TRACCIA_TABELLE = "it.eldasoft.generatoreRicerche.tracciaTabelle";
}