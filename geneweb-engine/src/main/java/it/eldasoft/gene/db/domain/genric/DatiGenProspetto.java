/*
 * Created on 07-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

package it.eldasoft.gene.db.domain.genric;

import it.eldasoft.gene.db.domain.genmod.DatiModello;

import java.io.Serializable;

/**
 * Rappresenta un elemento dell'elenco delle colonne estratte in una ricerca,
 * presenti nella tabella W_RICCAMPI
 * 
 * @author Luca.Giacomazzo
 */
public class DatiGenProspetto implements Serializable {

  /**   UID   */
  private static final long serialVersionUID = -4573781282001755708L;

  private DatiGenRicerca datiGenRicerca;
  private DatiModello    datiModello;
  
  public DatiGenProspetto(){
    this.datiGenRicerca = null;
    this.datiModello = null;
  }
  
  /**
   * @return Ritorna datiGenRicerca.
   */
  public DatiGenRicerca getDatiGenRicerca() {
    return datiGenRicerca;
  }
  
  /**
   * @param datiGenRicerca datiGenRicerca da settare internamente alla classe.
   */
  public void setDatiGenRicerca(DatiGenRicerca datiGenRicerca) {
    this.datiGenRicerca = datiGenRicerca;
  }
  
  /**
   * @return Ritorna datiModello.
   */
  public DatiModello getDatiModello() {
    return datiModello;
  }
  
  /**
   * @param datiModello datiModello da settare internamente alla classe.
   */
  public void setDatiModello(DatiModello datiModello) {
    this.datiModello = datiModello;
  }
 
}