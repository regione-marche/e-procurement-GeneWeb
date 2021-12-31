/*
 * Created on 14-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.struts;

/**
 * Classe indicante alcune costanti generali associate a Struts.
 *
 * @author Stefano.Sabbadin
 */
public class CostantiGeneraliStruts {

  /**
   * Indica l'etichetta da utilizzare dal framework Struts per il findForward in
   * seguito alla verifica che l'applicazione non è disponibile
   */
  public static final String FORWARD_APPLICAZIONE_NON_DISPONIBILE = "appNotLoaded";

  /**
   * Indica l'etichetta da utilizzare dal framework Struts per il findForward in
   * seguito alla verifica che la sessione è scaduta
   */
  public static final String FORWARD_SESSION_TIMEOUT              = "sessionTimeOut";

  /**
   * Indica l'etichetta da utilizzare dal framework Struts per il findForward in
   * seguito alla verifica che l'azione associata ad una richiesta appartiene ad
   * una funzionalità non disponibile
   */
  public static final String FORWARD_OPZIONE_NON_DISPONIBILE      = "opzioneNonDisponibile";

  /**
   * Indica l'etichetta da utilizzare dal framework Struts per il findForward in
   * seguito alla verifica che l'azione associata ad una richiesta appartiene ad
   * una funzionalità non abilitata all'utente
   */
  public static final String FORWARD_OPZIONE_NON_ABILITATA        = "opzioneNonAbilitata";

  /**
   * Indica l'etichetta da utilizzare dal framework Struts per il findForward in
   * seguito alla necessità di andare alla pagina di login
   */
  public static final String FORWARD_LOGIN                        = "login";

  /**
   * Indica l'etichetta da utilizzare dal framework Struts per il findForward in
   * seguito alla necessità di andare alla pagina di errore generale
   * dell'applicativo
   */
  public static final String FORWARD_ERRORE_GENERALE              = "error";

  /**
   * Indica l'etichetta da utilizzare dal framework Struts per il findForward
   * nel caso standard in cui tutto vada a buon fine, a meno che non siano
   * previste più possibilità di uscita "buone" in base ad una o più condizioni
   * discriminanti
   */
  public static final String FORWARD_OK                           = "success";

  /**
   * Indica l'etichetta da utilizzare dal framework Struts per il findForward
   * nel caso standard in cui una ricerca non reperisca alcun elemento. Si
   * dovrebbe rimanere nella pagina stessa di ricerca
   */
  public static final String FORWARD_NESSUN_RECORD_TROVATO        = "noRecordFound";
  
  /**
   * Utilizzata per indicare a struts che l'applicazione non e' attiva e richiede
   * l'attivazione mediante opportuna form di attivazione.
   */
  public static final String FORWARD_APPLICAZIONE_DA_ATTIVARE     = "applicazioneDaAttivare";
  
  /**
   * Contenitore di messaggi di errore in sessione per prelevarlo ad uno step
   * successivo in caso di chiamata di una pagina che effettua una forward ad
   * un'altra azione/pagina per cui si perdono i messaggi inseriti nella action
   * di partenza. Conviene perciò salvare il messaggio in sessione e quindi
   * riprenderlo dalla stessa per aggiungerlo nell'oggetto vero e proprio di
   * Struts
   */
  public static final String MESSAGGI_ERRORE_ACTION               = "actionMessages";

  /**
   * Indica l'etichetta da utilizzare dal framework Struts per il findForward in
   * seguito alla necessità di andare alla pagina di registrazione
   */
  public static final String FORWARD_REGISTRAZIONE                        = "register";

}
