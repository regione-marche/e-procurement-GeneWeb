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
package it.eldasoft.console.web.struts.schedric;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;

/**
 * Costanti in uso nelle pagine di Schedulazione Ricerche.
 * 
 * @author Francesco De Filippis
 */
public class CostantiSchedRic {

  /**
   * Chiave con cui viene inserito in SESSIONE l'oggetto contenente i parametri
   * per la ricerca nella pagina Trova SchedRic.
   */
  public static final String   TROVA_SCHEDRIC             = CostantiGenerali.PREFISSO_OGGETTO_TROVA
                                                              + "SchedRic";

  /** Codice del tabellato per il campo w_schedric.tipo */
  public static final String   TABELLATO_TIPO_SCHEDRIC    = "G_x03";

  /** Codice del tabellato per il campo w_schedric.formato */
  public static final String   TABELLATO_FORMATO_SCHEDRIC = "W0003";

  /** Chiave dell'istanza dell'oggetto GestioneTab in sessione */
  public static final String   NOME_GESTORE_TAB           = "gestoreTab";

  /** Lista dati per tabellato w_schedric.settimana */
  public static final String[] TABELLATO_SETTIMANA        = { "primo",
      "secondo", "terzo", "quarto", "ultimo"             };

  /** Lista dati per tabellato w_schedric.giorni_settimana */
  public static final String[] TABELLATO_GIORNI_SETTIMANA = { "Domenica",
      "Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato" };

  /**
   * Chiave dell'istanza dell'oggetto SchedRicForm presente in sessione
   */
  public static final String   OGGETTO_DETTAGLIO          = CostantiGenerali.PREFISSO_OGGETTO_DETTAGLIO
                                                              + "SchedRic";

  /**
   * Valore campo W_SCHEDRIC.ATTIVO per attivare la schedulazione
   */
  public static final String   ATTIVA                     = "1";

  /**
   * Valore campo W_SCHEDRIC.ATTIVO per disattivare la schedulazione
   */
  public static final String   DISATTIVA                  = "0";

  /** Voci del menu a tab delle pagine di dettaglio gruppo */
  public static final String   DATI_GENERALI              = "Dati Generali";

  public static final String   SCHEDULAZIONE              = "Schedulazione";

  public static final String   GIORNO                     = "G";

  public static final String   SETTIMANA                  = "S";

  public static final String   MESE                       = "M";

  public static final String   UNICA                      = "U";
  
  public static final String   ESTENSIONE_EXCEL                     = "xls";
  
  public static final String   FORMATO_EXCEL                     = "Excel";
  
  public static final String   ESTENSIONE_XLSX                    = "xlsx";
  
  public static final String   FORMATO_XLSX                     = "XLSX";

}