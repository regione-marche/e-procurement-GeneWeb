/*
 * Created on 7-feb-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.admin;

import java.io.Serializable;

/**
 * Aoggetto per la lettura delle protezioni dell'account
 * @author Marco.Franceschin
 *
 */
public class ProtezioneAccount implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 2871762244967563320L;
  private String tipo;
  private String azione;
  private String oggetto;
  private String valore;
  
  public String getAzione() {
    return azione;
  }
  
  public void setAzione(String azione) {
    this.azione = azione;
  }
  
  public String getOggetto() {
    return oggetto;
  }
  
  public void setOggetto(String oggetto) {
    this.oggetto = oggetto;
  }
  
  public String getTipo() {
    return tipo;
  }
  
  public void setTipo(String tipo) {
    this.tipo = tipo;
  }
  
  public String getValore() {
    return valore;
  }
  
  public void setValore(String valore) {
    this.valore = valore;
  }
  
}
