/*
 * Created on 2-nov-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.campi.gestori;

import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampoPercentuale;

/**
 * @author Marcello Caminiti
 */
public class GestoreCampoPercentuale0Dec extends AbstractGestoreCampoPercentuale {

  /**
   * Inizializzo il tipo percentuale con due cifre decimali
   */
  protected void initGestore() {
    super.initGestore();
    this.getCampo().setTipo("N3");
  }

  public int getNumeroDecimali() {
    return 0;
  }

}
