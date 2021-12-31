/*
 * Created on 21-aog-2006
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
 * Rappresenta un elemento dell'elenco delle tabelle usate in una ricerca,
 * presenti nella tabella W_RICTAB
 * 
 * @author Luca.Giacomazzo
 */
public class TabellaRicerca implements Serializable {

  /** UID */
  private static final long serialVersionUID = 5541226561669426392L;

  private Integer           id;
  private int               progressivo;
  private String            mneTabella;
  private String            aliasTabella;
  private int               visibile;

  public TabellaRicerca() {
    this.id = null;
    this.progressivo = 0;
    this.mneTabella = null;
    this.aliasTabella = null;
    this.visibile = 1;
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
   * @return Returns the mneTabella.
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
   * @return Ritorna visibile.
   */
  public int getVisibile() {
    return visibile;
  }

  /**
   * @param visibile
   *        visibile da settare internamente alla classe.
   */
  public void setVisibile(int visibile) {
    this.visibile = visibile;
  }
}