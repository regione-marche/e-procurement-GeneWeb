/*
 * Created on 16-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.ActionBase;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

/**
 * Classe base per tutte le Action semplici del package di gestione dei modelli
 * 
 * @author Stefano.Sabbadin
 */
abstract class AbstractActionBaseGenModelli extends ActionBase {

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioneAcquistata()
   */
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_GEN_MODELLI;
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioniRunAction()
   */
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  

}
