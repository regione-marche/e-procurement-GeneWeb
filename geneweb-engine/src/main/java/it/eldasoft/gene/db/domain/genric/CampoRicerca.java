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

import java.io.Serializable;

/**
 * Rappresenta un elemento dell'elenco delle colonne estratte in una ricerca,
 * presenti nella tabella W_RICCAMPI
 * 
 * @author Stefano.Sabbadin
 */
public class CampoRicerca implements Serializable {

  /** UID */
  private static final long serialVersionUID = -4229615146574970454L;

  private Integer           id;
  private int               progressivo;
  private String            mneTabella;
  private String            aliasTabella;
  private String            mneCampo;
  private String            funzione;
  private String            titoloColonna;

  public CampoRicerca() {
    this.id = null;
    this.progressivo = 0;
    this.mneTabella = null;
    this.aliasTabella = null;
    this.mneCampo = null;
    this.funzione = null;
    this.titoloColonna = null;
  }

  /**
   * @return Returns the aliasTabella.
   */
  public String getAliasTabella() {
    return aliasTabella;
  }

  /**
   * @param aliasTabella
   *        The aliasTabella to set.
   */
  public void setAliasTabella(String aliasTabella) {
    this.aliasTabella = aliasTabella;
  }

  /**
   * @return Returns the id.
   */
  public Integer getId() {
    return id;
  }

  /**
   * @param id
   *        The id to set.
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * @return Ritorna mneTabella.
   */
  public String getMneTabella() {
    return mneTabella;
  }

  /**
   * @param mneTabella
   *        mneTabella da settare internamente alla classe.
   */
  public void setMneTabella(String mneTabella) {
    this.mneTabella = mneTabella;
  }

  /**
   * @return Returns the mneCampo.
   */
  public String getMneCampo() {
    return mneCampo;
  }

  /**
   * @param mneCampo
   *        The mneCampo to set.
   */
  public void setMneCampo(String mnemonicoCampo) {
    this.mneCampo = mnemonicoCampo;
  }

  /**
   * @return Returns the progressivo.
   */
  public int getProgressivo() {
    return progressivo;
  }

  /**
   * @param progressivo
   *        The progressivo to set.
   */
  public void setProgressivo(int progressivo) {
    this.progressivo = progressivo;
  }

  /**
   * @return Returns the funzione.
   */
  public String getFunzione() {

    return funzione;
  }

  /**
   * @param funzione
   *        The funzione to set.
   */
  public void setFunzione(String funzione) {

    this.funzione = funzione;
  }
  
  /**
   * @return Ritorna titolo.
   */
  public String getTitoloColonna() {
    return titoloColonna;
  }
  
  /**
   * @param titolo titolo da settare internamente alla classe.
   */
  public void setTitoloColonna(String titolo) {
    this.titoloColonna = titolo;
  }

}