/*
 * Created on 02-ott-2007
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
 * Bean per l'interfacciamento con i dati presenti nella tabella
 * W_PROFILI e W_ACCPRO
 * 
 * @author Luca.Giacomazzo
 */
public class ProfiloAccount extends Profilo {
  
  /**   UID   */
  private static final long serialVersionUID = -2866906293795955411L;
  
  /**  associato? */
  private boolean associato;

  /**
   * Costruttore
   */
  public ProfiloAccount(){
    super();
    this.associato = false;
  }

  /**
   * Costruttore in base al Gruppo g
   * @param g
   *        gruppo da cui creare l'interfacciamento
   */
  public ProfiloAccount(Profilo p){
    super();
    this.setCodApp(p.getCodApp());
    this.setCodiceCliente(p.getCodiceCliente());
    this.setCodiceProfilo(p.getCodiceProfilo());
    this.setDescrizione(p.getDescrizione());
    this.setNome(p.getNome());
    this.setProfiloInterno(p.getProfiloInterno());
    this.setDiscriminante(p.getDiscriminante());
    
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