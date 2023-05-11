/*
 * Created on 16-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;

/**
 * Action per l'esecuzione di report predefiniti, creata per ridefinire il
 * metodo getOpzioneAcquistata(), rispetto a quello definito per la classe
 * padre DettaglioRicercaAction 
 * 
 * @author Luca.Giacomazzo
 */
public class EseguiRicercaPredefinitaAction extends DettaglioRicercaAction {

  /**
   * @param gruppiManager
   *        gruppiManager da settare internamente alla classe.
   */
  public void setGruppiManager(GruppiManager gruppiManager) {
    this.gruppiManager = gruppiManager;
  }

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }
  
  /**
   * @param geneManager geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }
  
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_DEFAULT;
  }

}