/*
 * Created on 14-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.admin;

/**
 * Bean per l'interfacciamento con i dati presenti nella tabella USRSYS e W_ACCGRP
 * 
 * @author Luca.Giacomazzo
 */
public class AccountGruppo extends Account {

  /** UID */
  private static final long serialVersionUID = -5820582210125844078L;
  
  /**  */
  private boolean associato;
  
  public AccountGruppo(){
    super();
  }
  
  /**
   * @return Ritorna associato.
   */
  public boolean getAssociato() {
    return associato;
  }

  /**
   * @param associato associato da settare internamente alla classe.
   */
  public void setAssociato(boolean associato) {
    this.associato = associato;
  }
  
}
