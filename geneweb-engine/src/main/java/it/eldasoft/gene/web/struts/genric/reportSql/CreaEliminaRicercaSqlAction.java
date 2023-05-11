package it.eldasoft.gene.web.struts.genric.reportSql;

import org.apache.log4j.Logger;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.web.struts.genric.CreaEliminaRicercaAction;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

/**
 * Azione per la gestione di creazione ed eliminazione di una ricerca con sql.
 * Questa action e' stata definita estendendo la Action CreaEliminaRicercaAction
 * per poter definire un livello diverso di accesso per le opzioni acquistate dall'utente
 * 
 * @author Luca.Giacomazzo
 */
public class CreaEliminaRicercaSqlAction extends CreaEliminaRicercaAction {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(CreaEliminaRicercaSqlAction.class);
  
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
    return new CheckOpzioniUtente(CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action crea
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniCrea() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
  }
  
  
  
}
