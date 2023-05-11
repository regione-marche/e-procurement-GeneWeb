/*
 * Created on 11/mar/09
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
 * Classe di costanti per le integrazioni con il sistema KRONOS
 * 
 * @author Stefano.Sabbadin
 */
public class CostantiIntegrazioneKronos {

  /** Integrazione con sistema KRONOS */
  public static final String   INTEGRAZIONE_KRONOS                        = "kronos";

  /**
   * Parametro passato dall'applicativo esterno durante la login e identificante
   * l'utente del portale durante l'accesso
   */
  public static final String   PARAM_ESTERNO_UTENTE                       = "idUtente";

  /**
   * Parametro passato dall'applicativo esterno durante la login e identificante
   * il ruolo dell'utente durante l'accesso
   */
  public static final String   PARAM_ESTERNO_RUOLO                        = "idRuolo";

  /**
   * Parametro passato dall'applicativo esterno durante la login e identificante
   * il codice dell'impresa
   */
  public static final String   PARAM_ESTERNO_IMPRESA                      = "codiceImpresa";

  /**
   * Parametro passato dall'applicativo esterno durante la login e identificante
   * la quadratura
   */
  public static final String   PARAM_ESTERNO_QUADRATURA                   = "quadratura";

  /**
   * Parametro passato dall'applicativo esterno durante la login e identificante
   * il codice contratto
   */
  public static final String   PARAM_ESTERNO_CONTRATTO                    = "codiceContratto";

  private static final String  PREFISSO_PARAM                             = "KR";

  /**
   * Tipo di ricerca che richiede la gestione personalizzata dei parametri
   * KRONOS
   */
  public static final String   TIPO_RICERCA_KRONOS                        = "99";

  /**
   * Tipo di modello che richiede la gestione personalizzata dei parametri
   * KRONOS
   */
  public static final String   TIPO_MODELLO_GIUSTIFICATIVO_KRONOS         = "99";

  /** Prefisso utilizzato per tutte le view dell'integrazione KRONOS */
  public static final String   PREFISSO_VIEW_KRONOS                       = "V_K";

  /**
   * Mnemonico della vista necessaria per realizzare i report con integrazione
   * KRONOS
   */
  public static final String   MNEMONICO_ARGOMENTO_PRINCIPALE             = PREFISSO_VIEW_KRONOS
                                                                              + "DIPE";

  /**
   * Elenco delle tabelle originarie usate per la creazione delle view sui
   * dipendenti
   */
  public static final String[] ELENCO_TAB_VISTA_ARG_PRINCIPALE            = {
      "ANAGPERS", "ANAGRLAV", "INCARLAV", "POSTOORGAN", "UNITAORG",
      "INQUAPREST"                                                       };

  /** Parametro data inizio validità per i report */
  public static final String   PARAM_RICERCA_DATA_INIZIO_VALIDITA         = PREFISSO_PARAM
                                                                              + "DATINVAL";

  /** Parametro data fine validità per i report */
  public static final String   PARAM_RICERCA_DATA_FINE_VALIDITA           = PREFISSO_PARAM
                                                                              + "DATFINVAL";

  // /** Parametro filtra per quadratura */
  // public static final String PARAM_RICERCA_FILTRA_QUADRATURA = PREFISSO_PARAM
  // + "QUADR";

  /**
   * Prefisso delle variabili utente contenenti filtri aggiuntivi da applicare
   * alle query
   */
  public static final String   PREFISSO_VARIABILE_UTENTE                  = "UTE_VAR";

  /** Parametro data inizio periodo per i modelli */
  public static final String   PARAM_MODELLO_DATA_INIZIO_PERIODO          = PREFISSO_PARAM
                                                                              + "DATINPER";

  /** Parametro data fine periodo per i modelli */
  public static final String   PARAM_MODELLO_DATA_FINE_PERIODO            = PREFISSO_PARAM
                                                                              + "DATFINPER";

  /** Parametro raggruppamento per i modelli */
  public static final String   PARAM_MODELLO_RAGGRUPPAMENTO               = PREFISSO_PARAM
                                                                              + "RAGGR";

  /** Parametro gruppo per i modelli */
  public static final String   PARAM_MODELLO_GRUPPO                       = PREFISSO_PARAM
                                                                              + "GRUP";

  /** Parametro giustificativo per i modelli */
  public static final String   PARAM_MODELLO_GIUSTIFICATIVO               = PREFISSO_PARAM
                                                                              + "GIUST";

  /** Parametro escludi i sabati per i modelli */
  public static final String   PARAM_MODELLO_ESCLUDI_SABATI               = PREFISSO_PARAM
                                                                              + "ESCLSAB";

  /** Parametro escludi le domeniche per i modelli */
  public static final String   PARAM_MODELLO_ESCLUDI_DOMENICHE            = PREFISSO_PARAM
                                                                              + "ESCLDOM";

  /** Parametro escludi le feste per i modelli */
  public static final String   PARAM_MODELLO_ESCLUDI_FESTE                = PREFISSO_PARAM
                                                                              + "ESCLFESTE";

  /** Parametro dettaglio dipendente per i modelli */
  public static final String   PARAM_MODELLO_OPZ_DETT_DIPENDENTE          = PREFISSO_PARAM
                                                                              + "OPDETTDIP";

  /** Parametro totali generali per i modelli */
  public static final String   PARAM_MODELLO_OPZ_TOTALI_GENERALI          = PREFISSO_PARAM
                                                                              + "OPTOTGEN";

  /** Parametro totali dipendente per i modelli */
  public static final String   PARAM_MODELLO_OPZ_TOTALI_DIPENDENTE        = PREFISSO_PARAM
                                                                              + "OPTOTDIP";

  /** Parametro raggruppa giustificativo per i modelli */
  public static final String   PARAM_MODELLO_OPZ_RAGGRUPPA_GIUSTIFICATIVO = PREFISSO_PARAM
                                                                              + "OPRAGGRGIUST";

  /** Parametro descrizione turno per i modelli */
  public static final String   PARAM_MODELLO_OPZ_DESCRIZIONE_TURNO        = PREFISSO_PARAM
                                                                              + "OPDESCTURNO";

  // /** Parametro conteggio a mesi per i modelli */
  // public static final String PARAM_MODELLO_OPZ_CONTEGGIO_MESI =
  // PREFISSO_PARAM
  // + "OPCONTMESI";

  /** Parametro solo giustificativo con note per i modelli */
  public static final String   PARAM_MODELLO_OPZ_GIUSTIFICATIVO_NOTE      = PREFISSO_PARAM
                                                                              + "OPSLGIUSTNOT";

  /** Parametro mostra note per i modelli */
  public static final String   PARAM_MODELLO_OPZ_MOSTRA_NOTE              = PREFISSO_PARAM
                                                                              + "OPMOSTRANOTE";
}
