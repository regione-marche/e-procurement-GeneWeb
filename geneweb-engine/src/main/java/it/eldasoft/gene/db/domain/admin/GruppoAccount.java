/*
 * Created on 22-Feb-2007
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
 * Bean per l'interfacciamento con i dati presenti nella tabella W_GRUPPI e W_ACCGRP
 * 
 * @author Francesco.DeFilippis
 */
public class GruppoAccount extends Gruppo {

  /** UID */
  private static final long serialVersionUID = -5820582210125844078L;
  
  /**  associato? */
  private boolean associato;
  
  private int priorita;
  
  /**
   * Costruttore 
   *
   */
  public GruppoAccount(){
    super();
    this.associato = false;
    priorita=0;
  }
  
  /**
   * Costruttore in base al Gruppo g
   * @param g
   *        gruppo da cui creare l'interfacciamento
   */
  public GruppoAccount(Gruppo g){
    super();
    this.setIdGruppo(g.getIdGruppo());
    this.setNomeGruppo(g.getNomeGruppo());
    this.setDescrGruppo(g.getDescrGruppo());
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

  
  /**
   * @return the priorita
   */
  public int getPriorita() {
    return priorita;
  }

  
  /**
   * @param priorita the priorita to set
   */
  public void setPriorita(int priorita) {
    this.priorita = priorita;
  }
  
}
