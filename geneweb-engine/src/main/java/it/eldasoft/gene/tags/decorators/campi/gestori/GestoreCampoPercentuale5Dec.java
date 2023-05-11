/*
 * Created on 12-feb-2008
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
 * @author Stefano.Sabbadin
 */
public class GestoreCampoPercentuale5Dec extends
    AbstractGestoreCampoPercentuale {

  /**
   * Inizializzo il tipo percentuale con nove cifre decimali. Tra il formato
   * F24.9 e F13.9 è stato scelto quello più restrittivo, dato che comunque
   * essendo una percentuale, non ha senso che abbia 24 cifre totali
   */
  protected void initGestore() {
    super.initGestore();
    this.getCampo().setTipo("F13.5");
  }
  
  public int getNumeroDecimali() {
    return 5;
  }

}
