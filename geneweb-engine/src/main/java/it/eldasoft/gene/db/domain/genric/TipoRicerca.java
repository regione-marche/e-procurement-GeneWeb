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
package it.eldasoft.gene.db.domain.genric;

import java.io.Serializable;

/**
 * Bean per il caricamento da DB dei dati per il popolamento della comboBox 
 * Tipo Ricerca nella pagina Trova Ricerche
 * 
 * @author Luca.Giacomazzo
 */
public class TipoRicerca implements Serializable {
  
  /**   UID   */
  private static final long serialVersionUID = 7757789849509641941L;
  private String tipoRicerca;
  private String nomeRicerca;
  
  /**
   * @return Ritorna nomeRicerca.
   */
  public String getNomeRicerca() {
    return this.nomeRicerca;
  }
  
  /**
   * @param descrRicerca descrRicerca da settare internamente alla classe.
   */
  public void setNomeRicerca(String descrModello) {
    this.nomeRicerca = descrModello;
  }
  
  /**
   * @return Ritorna tipoRicerca.
   */
  public String getTipoRicerca() {
    return this.tipoRicerca;
  }
  
  /**
   * @param tipoRicerca tipoRicerca da settare internamente alla classe.
   */
  public void setTipoRicerca(String tipoRicerca) {
    this.tipoRicerca = tipoRicerca;
  }

}
