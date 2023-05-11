/*
 * Created on 23-feb-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.domain;

/**
 * Classe di costanti generali per l'applicazione.
 *
 * @author cit_defilippis
 */
public class CostantiGeneraliAccount {

  /**
   * condizione di accesso in scrittura alla gestione utenti
   */
  public static final String   CONDIZIONE_SCRITTURA_ADMIN        = "ou11&!ou12";

  /**
   * condizione di accesso in lettura alla gestione utenti
   */
  public static final String   CONDIZIONE_LETTURA_ADMIN          = "ou11";

  /**
   * condizione per la gestione completa dei report
   */
  public static final String   GESTIONE_COMPLETA_GENRIC          = "ou48";

  /**
   * condizione per la gestione/creazione dei report ad uso solo personale
   */
  public static final String   SOLO_REPORT_PERSONALI_GENRIC      = "ou49";

  /**
   * condizione di accesso in scrittura al generatore ricerche
   */
  public static final String   CONDIZIONE_SCRITTURA_GENRIC       =
    GESTIONE_COMPLETA_GENRIC + "|" + SOLO_REPORT_PERSONALI_GENRIC;

  /**
   * condizione per la gestione completa dei modelli
   */
  public static final String   GESTIONE_COMPLETA_GENMOD          = "ou50";

  /**
   * condizione per la gestione/creazione dei modelli ad uso solo personale
   */
  public static final String   SOLO_MODELLI_PERSONALI_GENMOD      = "ou51";


  /**
   * condizione di accesso in scrittura al generatore modelli
   */
  public static final String   CONDIZIONE_ACCESSO_GENMOD         =
    GESTIONE_COMPLETA_GENMOD + "|" + SOLO_MODELLI_PERSONALI_GENMOD;


  /**
   * Opzioni abilitazione ai report base
   */
  public static final String   OPZIONI_REPORT_BASE                = "ou53";

  /**
   * Opzioni abilitazione ai report avanzati
   */
  public static final String   OPZIONI_REPORT_AVANZATI            = "ou54";

  /**
   * Opzioni abilitazione ai report prospetto
   */
  public static final String   OPZIONI_REPORT_PROSPETTO           = "ou55";

  /**
   * Costante per abilitazione delle Action relative ai report base
   */
  public static final String   ABILITAZIONE_REPORT_BASE =
    "(" + GESTIONE_COMPLETA_GENRIC + "|(" + SOLO_REPORT_PERSONALI_GENRIC + "&" + OPZIONI_REPORT_BASE + "))";

  /**
   * Costante per abilitazione delle Action relative ai report avanzati
   */
  public static final String   ABILITAZIONE_REPORT_AVANZATI =
    "(" + GESTIONE_COMPLETA_GENRIC + "|(" + SOLO_REPORT_PERSONALI_GENRIC + "&" + OPZIONI_REPORT_AVANZATI + "))";

  /**
   * Costante per abilitazione delle Action relative ai report con modello
   */
  public static final String   ABILITAZIONE_REPORT_CON_PROSPETTO =
    "(" + GESTIONE_COMPLETA_GENRIC + "|(" + SOLO_REPORT_PERSONALI_GENRIC + "&" + OPZIONI_REPORT_PROSPETTO + "))&(" + GESTIONE_COMPLETA_GENMOD + "|" + SOLO_MODELLI_PERSONALI_GENMOD + ")";


  /**
   * condizione per la gestione completa dei qform
   */
  public static final String   GESTIONE_COMPLETA_QFORM          = "ou232";

  /**
   * condizione per la gestione in gare /elenchi dei qform
   */
  public static final String   SOLO_GARE_ELENCHI_QFORM      = "ou231";

  /**
   * condizione di accesso in scrittura al generatore ricerche
   */
  public static final String   CONDIZIONE_SCRITTURA_QFORM       =
      GESTIONE_COMPLETA_QFORM + "|" + SOLO_GARE_ELENCHI_QFORM;


  /**
   * Opzioni utente menù Strumenti
   */
  public static final String   OPZIONI_MENU_STRUMENTI            = "ou30";

  /**
   * Opzioni utente menù Strumenti
   */
  public static final String   OPZIONI_NOTE                     = "ou59";

  /**
   * Opzioni utente Gestione Funzionalita' avanzate
   */
  public static final String   OPZIONI_GESTIONE_FUNZIONI_AVANZATE = "ou56";

  /**
   * Opzioni utente Amministrazione schedulazioni
   */
  public static final String   OPZIONI_AMMINISTRAZIONE_SCHEDULAZIONI = "ou62";

  /**
   * Opzioni utente Gestione Protezioni
   */
  public static final String   OPZIONI_SICUREZZA_PASSWORD       = "ou39";

  /**
   * Opzioni utente Amministrazione di parametri di sistema
   */
  public static final String   OPZIONI_AMMINISTRAZIONE_PARAM_SISTEMA = "ou89";

  /**
   * Opzioni utente blocco modifica dati della stazione appaltante
   */
  public static final String   OPZIONI_BLOCCO_MODIFICA_UFFINT = "ou214";

  /**
   * Opzioni utente blocco eliminazione entita principale
   */
  public static final String   OPZIONI_BLOCCO_ELIMINAZIONE_ENTITA_PRINCIPALE = "ou215";

  /**
   * Prefisso opzioni utente
   */
  public static final String   PREFISSO_OPZIONI_PRIVILEGI        = "UT";

  /**
   * lista valori si/no per DropDownList
   */
  public static final String[] LISTA_SI_NO                       = { "Si", "No" };

  /**
   * lista testo per DropDownList opzioni privilegi
   */
  public static final String[] LISTA_TEXT_PRIVILEGI              = {
      "Nessun privilegio", "Sola lettura", "Gestione completa"    };

  /**
   * lista valori per DropDownList opzioni privilegi
   */
  public static final String[] LISTA_VALUE_PRIVILEGI             = { "",
      "ou11|ou12", CONDIZIONE_LETTURA_ADMIN                                       };

  /**
   * lista testo per DropDownList opzioni generatore ricerche
   */
  public static final String[] LISTA_TEXT_GENRIC                 = {
      "Non abilitata", "Solo report personali", "Gestione completa" };

  /**
   * lista valori per DropDownList opzioni generatore ricerche
   */
  public static final String[] LISTA_VALUE_GENRIC                = { "",
      SOLO_REPORT_PERSONALI_GENRIC, GESTIONE_COMPLETA_GENRIC };

  /**
   * lista testo per DropDownList opzioni generatore modelli
   */
  public static final String[] LISTA_TEXT_GENMOD                 = {
      "Non abilitata", "Solo modelli personali", "Gestione completa" };

  /**
   * lista valori per DropDownList opzioni generatore modelli
   */
  public static final String[] LISTA_VALUE_GENMOD                = { "",
      SOLO_MODELLI_PERSONALI_GENMOD, GESTIONE_COMPLETA_GENMOD };


  /**
   * lista testo per DropDownList opzioni qform
   */
  public static final String[] LISTA_TEXT_QFORM                 = {
      "Non abilitata", "Gestione in gare o elenchi", "Gestione completa" };


  /**
   * lista valori per DropDownList opzioni qform
   */
  public static final String[] LISTA_VALUE_QFORM                = { "",
      SOLO_GARE_ELENCHI_QFORM, GESTIONE_COMPLETA_QFORM };

  /**
   * lista testo per DropDownList opzioni abilitazione lavori
   */
  // C.F. 19/11/2009
  // Per Gare e Lavori:Il valore 'Non definito' non viene più utilizzato:
  //il default diviene 'Utente'
  public static final String[] LISTA_TEXT_LAVORI = {
    "Utente (accesso consentito solo sulle commesse assegnate)",
    "Responsabile (accesso consentito su tutte le commesse)" };

  /**
   * lista testo per DropDownList opzioni abilitazione gare
   */
  public static final String[] LISTA_TEXT_GARE = {
    "Utente (accesso consentito solo sulle gare assegnate)",
    "Responsabile (accesso consentito su tutte le gare)" };

  /**
   * lista testo per DropDownList Scadenza Account
   */
  public static final String[] LISTA_TEXT_SCADENZA = {"Mai", "Alla fine di: "};

  /**
   * lista testo per DropDownList abilitazioneAP
   */
  public static final String[] LISTA_TEXT_AP = {
    "Utente",
    "Responsabile" };

  public static final String[] LISTA_VALUE_AP ={"U", "A"};

  /**
   * lista valori per DropDownList opzioni abilitazione lavori
   */
  public static final String[] LISTA_VALUE_LAVORI ={"U", "A"};

  // L.G. 21/11/2007: modifica per set dei campi SYSAB3, SYSABG, SYSABC
  // SYSLIV, SYSLIG, SYSLIC ai valori di default al momento della
  // creazione di un nuovo account. In particolare:
  // SYSABC = NDEFM
  // SYSLIC = 0
  // SYSABG = NDEFM
  // SYSLIG = 0
  // SYSAB3 = N
  // SYSLIV = null

  /**
   * lista testo per DropDownList opzioni privilegi su verifiche Art.80
   */
  public static final String[] LISTA_TEXT_PRIVILEGI_ART80              = {
      "Accesso (in sola lettura) agli esiti", "Accesso ai contenuti dei documenti", "Accesso con operazioni sui documenti"  };

  /**
   * lista valori per DropDownList opzioni privilegi
   */
  public static final String[] LISTA_VALUE_PRIVILEGI_ART80             = { "",
      "ou227", "ou228" };

  /**   Valore di default del campo SYSABC  */
  public static final String  DEFAULT_ABILITAZIONE_CONTRATTI = "NDEFM";

  /**   Valore di default del campo SYSLIC  */
  public static final Integer DEFAULT_LIVELLO_CONTRATTI      = new Integer(0);

  /**   Valore di default del campo SYSABG  */
  //public static final String  DEFAULT_ABILITAZIONE_GARE      = "NDEFM";
  // F.D. 31/07/08 nuova gestione abilitazione gare come lavori
  public static final String  DEFAULT_ABILITAZIONE_GARE      = "U";

  /**   Valore di default del campo SYSLIG  */
  public static final Integer DEFAULT_LIVELLO_GARE           = new Integer(0);

  /**   Valore di default del campo SYSAB3  */
  public static final String  DEFAULT_ABILITAZIONE_LAVORI    = "U";

  /**   Valore di default del campo SYSAB3  */
  public static final String  DEFAULT_ABILITAZIONE_STANDARD    = "U";

  /**   Valore di default del campo SYSLIV  */
  public static final Integer DEFAULT_LIVELLO_LAVORI         = new Integer(0);

  /**   Valore di default del campo SYSABU  */
  //public static final String DEFAULT_ABILITAZIONE_UGC        = "$$22";

  /**   Valore di default del campo SYSAB3  */
  public static final String  DEFAULT_ABILITAZIONE_AP    = "U";

  /**
   * condizione per la selezione automatica degli operatori da elenco
   */
  public static final String   SELEZIONE_AUTOMATICA_OPERATORE_ELENCO         = "ou235";

  /**
   * condizione per la selezione manuale degli operatori da elenco
   */
  public static final String   SELEZIONE_MANUALE_OPERATORE_ELENCO      = "ou236";

  /**
   * condizione per la selezione automatica e manuale degli operatori da elenco
   */
  public static final String   SELEZIONE_AUTOMATICA_MANUALE_OPERATORE_ELENCO      = "ou237";

  /**
   * lista testo per DropDownList opzioni selezione da elenco operatori
   */
  public static final String[] LISTA_TEXT_SELOP                 = {
      "Applica configurazione generale", "Selezione automatica", "Selezione manuale", "Selezione automatica e manuale" };


  /**
   * lista valori per DropDownList opzioni selezione da elenco operatori
   */
  public static final String[] LISTA_VALUE_SELOP                = { "",
      SELEZIONE_AUTOMATICA_OPERATORE_ELENCO, SELEZIONE_MANUALE_OPERATORE_ELENCO, SELEZIONE_AUTOMATICA_MANUALE_OPERATORE_ELENCO };

}