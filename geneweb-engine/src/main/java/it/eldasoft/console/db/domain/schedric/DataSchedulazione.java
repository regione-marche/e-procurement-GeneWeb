/*
 * Created on 05/ago/2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.console.db.domain.schedric;

import java.util.Date;

/**
 * Wrapper della data schedulazione calcolata dall'algoritmo, comprensiva di ore
 * e minuti (che rimangono sempre fissi eccetto il caso di schedulazione
 * giornaliera ogni giorno ad intervalli di x minuti.
 * 
 * @author Stefano.Sabbadin
 */
public class DataSchedulazione {

  private Date data;
  private int  ora;
  private int  minuti;

  public DataSchedulazione() {
    this.data = null;
    this.ora = 0;
    this.minuti = 0;
  }

  /**
   * @return Ritorna data.
   */
  public Date getData() {
    return data;
  }

  /**
   * @param data
   *        data da settare internamente alla classe.
   */
  public void setData(Date data) {
    this.data = data;
  }

  /**
   * @return Ritorna ora.
   */
  public int getOra() {
    return ora;
  }

  /**
   * @param ora
   *        ora da settare internamente alla classe.
   */
  public void setOra(int ora) {
    this.ora = ora;
  }

  /**
   * @return Ritorna minuti.
   */
  public int getMinuti() {
    return minuti;
  }

  /**
   * @param minuti
   *        minuti da settare internamente alla classe.
   */
  public void setMinuti(int minuti) {
    this.minuti = minuti;
  }

}
