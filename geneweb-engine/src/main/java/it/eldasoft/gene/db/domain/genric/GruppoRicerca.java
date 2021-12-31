/*
 * Created on 21-ago-2006
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.genric;

/**
 * Rappresenta un elemento dell'associativa tra i gruppi e le ricerche,
 * presenti nella tabella W_GRPRIC
 * 
 * @author Luca Giacomazzo
 */
import it.eldasoft.gene.db.domain.admin.Gruppo;

public class GruppoRicerca extends Gruppo {
  
  /**   UID   */
  private static final long serialVersionUID = 5041736460502021715L;
  
  private Integer id;
  private boolean associato;
  
  public GruppoRicerca() {
    super();
    this.id = null;;
    this.associato = false;
  }
  
  public GruppoRicerca(Gruppo gruppo){
    this.setIdGruppo(gruppo.getIdGruppo());
    this.setNomeGruppo(gruppo.getNomeGruppo());
    this.setDescrGruppo(gruppo.getDescrGruppo());
    this.id = null;
    this.associato = false;
  }

  /**
   * @return Ritorna id.
   */
  public Integer getId() {
    return id;
  }
  
  /**
   * @param id id da settare internamente alla classe.
   */
  public void setId(Integer id) {
    this.id = id;
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