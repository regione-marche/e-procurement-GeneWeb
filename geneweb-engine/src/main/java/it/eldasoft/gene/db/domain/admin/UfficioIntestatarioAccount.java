/*
 * Created on 02-ott-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.admin;

import it.eldasoft.gene.db.domain.UfficioIntestatario;

/**
 * Bean per l'interfacciamento con i dati presenti nella tabella
 * UFFINT e USR_EIN
 * 
 * @author Stefano.Sabbadin
 */
public class UfficioIntestatarioAccount extends UfficioIntestatario {
  
  /**   UID   */
  private static final long serialVersionUID = -2866906293795955411L;
  
  /**  associato? */
  private boolean associato;

  /**
   * Costruttore
   */
  public UfficioIntestatarioAccount(){
    super();
    this.associato = false;
  }

  /**
   * Costruttore in base al Gruppo g
   * @param g
   *        gruppo da cui creare l'interfacciamento
   */
  public UfficioIntestatarioAccount(UfficioIntestatario u){
    super();
    this.setCodice(u.getCodice());
    this.setNome(u.getNome());
    this.setCodFiscale(u.getCodFiscale());
    this.setPartitaIVA(u.getPartitaIVA());
    
    this.associato = false;
  }

  /**
   * @return Ritorna associato.
   */
  public boolean getAssociato() {
    return associato;
  }

  /**
   * @param associato 
   *        associato da settare internamente alla classe.
   */
  public void setAssociato(boolean associato) {
    this.associato = associato;
  }

}