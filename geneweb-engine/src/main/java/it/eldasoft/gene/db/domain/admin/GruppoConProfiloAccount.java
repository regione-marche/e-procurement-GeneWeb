/*
 * Created on 22-ott-2007
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
 * Bean per l'estrazione dei gruppi associati ad un account, con il nome del 
 * profilo a cui il gruppo e' associato
 * 
 * @author Luca.Giacomazzo
 */
public class GruppoConProfiloAccount extends Gruppo {

  /**   UID   */
  private static final long serialVersionUID = -7735071853016895195L;

  /**  Gruppo associato all'account? */
  private boolean associato;  
  
  /** Nome del profilo a cui il gruppo e' associato */
  private String nomeProfilo;
  
  public GruppoConProfiloAccount(){
    super();
    this.associato = false;
    this.nomeProfilo = null;
  }
  
  public GruppoConProfiloAccount(Gruppo g){
    super();
    this.setIdGruppo(g.getIdGruppo());
    this.setNomeGruppo(g.getNomeGruppo());
    this.setDescrGruppo(g.getDescrGruppo());
    this.associato = false;
    this.nomeProfilo = null;
  }
  
  /**
   * @return Ritorna associato.
   */
  public boolean isAssociato() {
    return associato;
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
  
  /**
   * @return Ritorna nomeProfilo.
   */
  public String getNomeProfilo() {
    return nomeProfilo;
  }
  
  /**
   * @param nomeProfilo nomeProfilo da settare internamente alla classe.
   */
  public void setNomeProfilo(String nomeProfilo) {
    this.nomeProfilo = nomeProfilo;
  }
 
}