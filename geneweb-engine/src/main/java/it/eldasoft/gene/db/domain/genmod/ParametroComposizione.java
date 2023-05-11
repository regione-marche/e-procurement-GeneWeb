/*
 * Created on 05-dic-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.genmod;

import java.io.Serializable;

/**
 * Classe che permette la memorizzazione di un parametro attribuito ad un
 * modello in fase di composizione dello stesso. Il parametro viene inserito a
 * runtime dall'utente non appena intende comporre un modello su determinati
 * dati
 * 
 * @author Stefano.Sabbadin
 */
public class ParametroComposizione implements Serializable {

  /**
   * UID
   */
  private static final long serialVersionUID = -5738491267274623000L;

  /** identificativo di sessione della richiesta */
  private int               idSessione;

  /** codice del parametro */
  private String            codice;

  /** descrizione del parametro */
  private String            descrizione;

  /** valore del parametro */
  private String            valore;

  public ParametroComposizione() {
    this.idSessione = 0;
    this.codice = null;
    this.descrizione = null;
    this.valore = null;
  }

  /**
   * @return Ritorna codice.
   */
  public String getCodice() {
    return codice;
  }

  /**
   * @param codice
   *        codice da settare internamente alla classe.
   */
  public void setCodice(String codice) {
    this.codice = codice;
  }

  /**
   * @return Ritorna descrizione.
   */
  public String getDescrizione() {
    return descrizione;
  }

  /**
   * @param descrizione
   *        descrizione da settare internamente alla classe.
   */
  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  /**
   * @return Ritorna idSessione.
   */
  public int getIdSessione() {
    return idSessione;
  }

  /**
   * @param idSessione
   *        idSessione da settare internamente alla classe.
   */
  public void setIdSessione(int idSessione) {
    this.idSessione = idSessione;
  }

  /**
   * @return Ritorna valore.
   */
  public String getValore() {
    return valore;
  }

  /**
   * @param valore
   *        valore da settare internamente alla classe.
   */
  public void setValore(String valore) {
    this.valore = valore;
  }

}
