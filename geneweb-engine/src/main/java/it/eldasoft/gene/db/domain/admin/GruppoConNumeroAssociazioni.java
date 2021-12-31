/*
 * Created on 20-feb-2007
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
 * Classe contenitore dell'estrazione di un gruppo con il numero di
 * ricerche/modelli/utenti associati
 * 
 * @author Stefano.Sabbadin
 */
public class GruppoConNumeroAssociazioni extends Gruppo {

  /**
   * UID
   */
  private static final long serialVersionUID = 8631773224741304881L;

  private int               numeroUtenti;

  private int               numeroRicerche;

  private int               numeroModelli;

  public GruppoConNumeroAssociazioni() {
    super();
    this.numeroUtenti = 0;
    this.numeroRicerche = 0;
    this.numeroModelli = 0;
  }

  /**
   * @return Ritorna numeroModelli.
   */
  public int getNumeroModelli() {
    return numeroModelli;
  }

  /**
   * @param numeroModelli
   *        numeroModelli da settare internamente alla classe.
   */
  public void setNumeroModelli(int numeroModelli) {
    this.numeroModelli = numeroModelli;
  }

  /**
   * @return Ritorna numeroRicerche.
   */
  public int getNumeroRicerche() {
    return numeroRicerche;
  }

  /**
   * @param numeroRicerche
   *        numeroRicerche da settare internamente alla classe.
   */
  public void setNumeroRicerche(int numeroRicerche) {
    this.numeroRicerche = numeroRicerche;
  }

  /**
   * @return Ritorna numeroUtenti.
   */
  public int getNumeroUtenti() {
    return numeroUtenti;
  }

  /**
   * @param numeroUtenti
   *        numeroUtenti da settare internamente alla classe.
   */
  public void setNumeroUtenti(int numeroUtenti) {
    this.numeroUtenti = numeroUtenti;
  }

}
