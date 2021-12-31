/*
 * Created on 16/mar/09
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
 * Classe per la memorizzazione dei parametri inseriti dall'utente al fine di
 * agevolare l'operazione di cache sui dati e proporre all'atto dell'inserimento
 * i valori inseriti in precedenza
 * 
 * @author Stefano.Sabbadin
 */
public class CacheParametroEsecuzione implements Serializable {

  /** UID */
  private static final long serialVersionUID = -88323246581974177L;
  
  /** Id dell'utente */
  private int               idAccount;
  /** Id della ricerca */
  private int               idRicerca;
  /** Codice del parametro nel modello */
  private String            codice;
  /** Valore del parametro */
  private String            valore;

  public CacheParametroEsecuzione() {
    this.idAccount = 0;
    this.idRicerca = 0;
    this.codice = null;
    this.valore = null;
  }

  /**
   * @return Ritorna idAccount.
   */
  public int getIdAccount() {
    return idAccount;
  }

  /**
   * @param idAccount
   *        idAccount da settare internamente alla classe.
   */
  public void setIdAccount(int idAccount) {
    this.idAccount = idAccount;
  }

  /**
   * @return Ritorna idRicerca.
   */
  public int getIdRicerca() {
    return idRicerca;
  }

  /**
   * @param idRicerca
   *        idRicerca da settare internamente alla classe.
   */
  public void setIdRicerca(int idRicerca) {
    this.idRicerca = idRicerca;
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
