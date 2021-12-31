/*
 * Created on 26-mar-2007
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.base;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.web.struts.genric.CreaEliminaRicercaAction;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

/**
 * Azione per la gestione di creazione ed eliminazione di una ricerca base
 * Questa action e' stata definita estendendo la Action CreaEliminaRicercaAction
 * per poter definire un livello diverso di accesso per le opzioni acquistate dall'utente
 * 
 * @author Francesco.DeFilippis
 */
public class CreaEliminaRicercaBaseAction extends CreaEliminaRicercaAction {
  
  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioneAcquistata()
   */
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action elimina
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniElimina() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action crea
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniCrea() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
  
}