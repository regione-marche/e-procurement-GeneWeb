/*
 * Created on 21/nov/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.scheda;

import it.eldasoft.gene.tags.TagAttributes;


public class GruppoCampiAttributes extends TagAttributes {
  
  /** Identificativo delle protezioni */
  private String            idProtezioni;
  /** Flag per la visibilita del gruppo di campo */
  private boolean           visibile;
  /** Flag per abilitare o meno i campi in modifica */
  private boolean           modifica;

  public GruppoCampiAttributes(String nomeVar) {
    super(nomeVar);
    this.idProtezioni=null;
    this.visibile=true;
    this.modifica=true;
  }
  /**
   * 
   */
  private static final long serialVersionUID = 2019019547544249867L;

  
  public String getIdProtezioni() {
    return idProtezioni;
  }
  
  public void setIdProtezioni(String idProtezioni) {
    this.idProtezioni = idProtezioni;
  }
  
  public boolean isModifica() {
    return modifica;
  }
  
  public void setModifica(boolean modifica) {
    this.modifica = modifica;
  }
  
  public boolean isVisibile() {
    return visibile;
  }
  
  public void setVisibile(boolean visibile) {
    this.visibile = visibile;
  }

}
