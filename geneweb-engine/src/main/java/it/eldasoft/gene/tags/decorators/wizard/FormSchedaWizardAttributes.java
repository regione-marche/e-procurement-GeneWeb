/*
 * Created on 7-mag-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.wizard;

import it.eldasoft.gene.tags.decorators.scheda.FormSchedaAttributes;

/**
 * Attributi variabili della scheda di un wizard
 * 
 * @author stefano.sabbadin
 */
public class FormSchedaWizardAttributes extends FormSchedaAttributes {

  /** tipo di pagina del wizard */
  private String            tipoPagina;

//  /** numero di sottopagina della pagina di un wizard */
//  private int               numeroSottoPagina;

  /**
   * UID
   */
  private static final long serialVersionUID = 7742369363515322622L;

  public FormSchedaWizardAttributes(String tipoVar) {
    super(tipoVar);
    this.tipoPagina = null;
//    this.numeroSottoPagina = 0;
  }

  /**
   * @return Ritorna tipoPagina.
   */
  public String getTipoPagina() {
    return tipoPagina;
  }

  /**
   * @param tipoPagina
   *        tipoPagina da settare internamente alla classe.
   */
  public void setTipoPagina(String tipoPagina) {
    this.tipoPagina = tipoPagina;
  }

//  /**
//   * @return Ritorna numeroSottoPagina.
//   */
//  public int getNumeroSottoPagina() {
//    return numeroSottoPagina;
//  }
//
//  /**
//   * @param numeroSottoPagina
//   *        numeroSottoPagina da settare internamente alla classe.
//   */
//  public void setNumeroSottoPagina(int numeroSottoPagina) {
//    this.numeroSottoPagina = numeroSottoPagina;
//  }

}
