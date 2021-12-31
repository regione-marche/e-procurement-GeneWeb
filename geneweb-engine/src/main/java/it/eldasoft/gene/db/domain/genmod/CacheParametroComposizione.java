/*
 * Created on 10/mar/09
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
 * Classe per la memorizzazione dei parametri inseriti dall'utente al fine di
 * agevolare l'operazione di cache sui dati e proporre all'atto dell'inserimento
 * i valori inseriti in precedenza
 * 
 * @author Stefano.Sabbadin
 */
public class CacheParametroComposizione implements Serializable {

  /** UID */
  private static final long serialVersionUID = 9135580998636795722L;

  /** Id dell'utente */
  private int               idAccount;
  /** Id del modello */
  private int               idModello;
  /** Codice del parametro nel modello */
  private String            codice;
  /** Valore del parametro */
  private String            valore;

  public CacheParametroComposizione() {
    this.idAccount = 0;
    this.idModello = 0;
    this.codice = null;
    this.valore = null;
  }

  public CacheParametroComposizione(ParametroComposizione parametro,
      int idAccount, int idModello) {
    this.idAccount = idAccount;
    this.idModello = idModello;
    this.codice = parametro.getCodice();
    this.valore = parametro.getValore();
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
   * @return Ritorna idModello.
   */
  public int getIdModello() {
    return idModello;
  }

  /**
   * @param idModello
   *        idModello da settare internamente alla classe.
   */
  public void setIdModello(int idModello) {
    this.idModello = idModello;
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
