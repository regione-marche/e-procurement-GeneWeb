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
 * Costanti in uso nelle pagine di Coda della schedulazioni.
 * 
 * @author Francesco De Filippis
 */
public class CostantiCodaSched {

  /**
   * Chiave con cui viene inserito in SESSIONE l'oggetto contenente i parametri
   * per la ricerca nella pagina Trova CodaSched.
   */
  public static final String TROVA_CODASCHED             = CostantiGenerali.PREFISSO_OGGETTO_TROVA
                                                           + "CodaSched";

  public static final String TROVA_CODASCHED_SESSION     = "trovaCodaSchedFormSession";
  
  /** Codice del tabellato per il campo w_codasched.stato */
  public static final String TABELLATO_STATO_CODASCHED   = "W0002";
 
  /** Proprietà in cui è scritto il path dei modelli */
  public static final String PROP_PATH_FILE              = "it.eldasoft.webConsole.risSchedulazioni.path";
  
  public static final int STATO_INIZIO = 0;
  public static final int STATO_ESEGUITO_CON_SUCCESSO = 1;
  public static final int STATO_ESEGUITO_CON_ERRORE = 2;
  public static final int STATO_ESEGUITO_CON_WARNING = 3;
}