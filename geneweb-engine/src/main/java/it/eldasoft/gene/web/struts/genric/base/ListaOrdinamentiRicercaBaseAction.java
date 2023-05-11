/*
 * Created on 02-apr-2007
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
import it.eldasoft.gene.web.struts.genric.ordinamento.ListaOrdinamentiRicercaAction;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.util.List;


/**
 * Ridefinizione del metodo getMnemoniciCampiDaTabella perche' per le ricerche
 * base, che usano tabelle definite in apposite viste, i campi sono non
 * utilizzabili nelle ricerche (cioe' sono di tipo 'C') e quindi la chiamata
 * tabella.getMnemoniciCampiPerRicerche() ritorna null.
 *
 * @author Luca.Giacomazzo
 */
public class ListaOrdinamentiRicercaBaseAction extends ListaOrdinamentiRicercaAction {

  @Override
  protected List<String> getMnemoniciCampiDaTabella(Tabella tabella){
    return tabella.getMnemoniciCampi();
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioneAcquistata()
   */
  @Override
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action modifica
   * @return opzioni per accedere alla action
   */
  @Override
  public CheckOpzioniUtente getOpzioniModifica() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action elimina
   * @return opzioni per accedere alla action
   */
  @Override
  public CheckOpzioniUtente getOpzioniElimina() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaSu
   * @return opzioni per accedere alla action
   */
  @Override
  public CheckOpzioniUtente getOpzioniSpostaSu() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaSu
   * @return opzioni per accedere alla action
   */
  @Override
  public CheckOpzioniUtente getOpzioniSpostaGiu() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaInPosizioneMarcata
   * @return opzioni per accedere alla action
   */
  @Override
  public CheckOpzioniUtente getOpzioniSpostaInPosizioneMarcata() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * eliminaMultiplo
   *
   * @return opzioni per accedere alla action
   */
  @Override
  public CheckOpzioniUtente getOpzioniEliminaMultiplo() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
}
