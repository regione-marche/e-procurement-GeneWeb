/*
 * Created on 24-mar-2015
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.reportSql;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.web.struts.genric.CambiaTabAction;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

/**
 * Azione per la gestione del cambio Tab, nelle pagine di creazione e/o modifica
 * di una ricerca sql
 * Questa action e' stata definita estendendo la Action CambiaTabAction
 * per poter definire a livello di struts dei forward diversi da quelli definiti
 * per i report avanzati a parita' di target
 * 
 * @author Luca.Giacomazzo
 */
public class CambiaTabRicercaSqlAction extends CambiaTabAction {
  
  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioneAcquistata()
   */
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL;
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioniRunAction()
   */
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
  }

}