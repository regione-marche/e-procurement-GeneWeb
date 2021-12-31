/*
 * Created on 02-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;

/**
 * Costanti in uso nelle pagine di dettaglio gruppo.
 * 
 * @author Luca Giacomazzo
 */
public class CostantiDettaglioAccount {

  /** Nome dell'istanza dell'oggetto GestioneTab in sessione */
  public static final String NOME_GESTORE_TAB   = "gestoreTab";

  /** Voci del menu a tab delle pagine di dettaglio gruppo */
  public static final String DETTAGLIO          = "Dettaglio";
  public static final String PROFILI            = "Profili";
  public static final String GRUPPI             = "Gruppi";
  public static final String UFFICI_INTESTATARI = "Uffici intestatari";
  public static final String TECNICI            = "Tecnici";

  /**
   * Valore campo USRSYS.SYSDISAB per attivare l'utente
   */
  public static final String ABILITATO          = "0";

  /**
   * Valore campo USRSYS.SYSDISAB per disattivate l'utente
   */
  public static final String DISABILITATO       = "1";

  /**
   * Chiave con cui viene inserito in SESSIONE l'oggetto contenente i parametri
   * per la ricerca nella pagina Trova utenti.
   */
  public static final String TROVA_ACCOUNT      = CostantiGenerali.PREFISSO_OGGETTO_TROVA
                                                    + "Account";
  /**
   * Chiave con cui viene inserito in SESSIONE l'oggetto contenente i parametri
   * per la ricerca nella pagina Trova utenti LDAP.
   */
  public static final String TROVA_ACCOUNT_LDAP = CostantiGenerali.PREFISSO_OGGETTO_TROVA
                                                    + "AccountLDAP";

}