/*
 * Created on 21-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.genric;

import it.eldasoft.utils.sql.comp.SqlJoin;
import java.io.Serializable;

/**
 * Rappresenta un elemento dell'elenco delle join da effettuare in una ricerca,
 * presenti nella tabella W_RICJOIN
 * 
 * @author Stefano.Sabbadin
 */
public class GiunzioneRicerca implements Serializable {

  /** UID */
  private static final long serialVersionUID      = 987788011725787658L;

  public static final short INNER_JOIN            = SqlJoin.INNER_JOIN;
  public static final short LEFT_OUTER_JOIN       = SqlJoin.LEFT_OUTER_JOIN;
  public static final short RIGHT_OUTER_JOIN      = SqlJoin.RIGHT_OUTER_JOIN;

  public static final char  SEPARATORE_CAMPI_JOIN = ',';

  private Integer           id;
  private int               progressivo;
  private short           giunzioneAttiva;
  private String            mnemonicoTabella1;
  private String            aliasTabella1;
  private String            campiTabella1;
  private String            mnemonicoTabella2;
  private String            aliasTabella2;
  private String            campiTabella2;
  private short             tipoGiunzione;

  public GiunzioneRicerca() {
    this.id = null;
    this.progressivo = 0;
    this.giunzioneAttiva = 1;
    this.mnemonicoTabella1 = null;
    this.aliasTabella1 = null;
    this.campiTabella1 = null;
    this.mnemonicoTabella2 = null;
    this.aliasTabella2 = null;
    this.campiTabella2 = null;
    this.tipoGiunzione = GiunzioneRicerca.INNER_JOIN;
  }

  /**
   * @return Returns the aliasTabella1.
   */
  public String getAliasTabella1() {
    return aliasTabella1;
  }

  /**
   * @param aliasTabella1
   *        The aliasTabella1 to set.
   */
  public void setAliasTabella1(String aliasTabella1) {
    this.aliasTabella1 = aliasTabella1;
  }

  /**
   * @return Returns the aliasTabella2.
   */
  public String getAliasTabella2() {
    return aliasTabella2;
  }

  /**
   * @param aliasTabella2
   *        The aliasTabella2 to set.
   */
  public void setAliasTabella2(String aliasTabella2) {
    this.aliasTabella2 = aliasTabella2;
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
   * @return Returns the mnemonicoTabella1.
   */
  public String getMnemonicoTabella1() {
    return mnemonicoTabella1;
  }

  /**
   * @param mnemonicoTabella1
   *        The mnemonicoTabella1 to set.
   */
  public void setMnemonicoTabella1(String mnemonicoTabella1) {
    this.mnemonicoTabella1 = mnemonicoTabella1;
  }

  /**
   * @return Returns the mnemonicoTabella2.
   */
  public String getMnemonicoTabella2() {
    return mnemonicoTabella2;
  }

  /**
   * @param mnemonicoTabella2
   *        The mnemonicoTabella2 to set.
   */
  public void setMnemonicoTabella2(String mnemonicoTabella2) {
    this.mnemonicoTabella2 = mnemonicoTabella2;
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
   * @return Returns the tipoGiunzione.
   */
  public short getTipoGiunzione() {
    return tipoGiunzione;
  }

  /**
   * @param tipoGiunzione
   *        The tipoGiunzione to set.
   */
  public void setTipoGiunzione(short tipoGiunzione) {
    this.tipoGiunzione = tipoGiunzione;
  }

  /**
   * @return Returns the giunzioneAttiva.
   */
  public short getGiunzioneAttiva() {

    return giunzioneAttiva;
  }

  /**
   * @param giunzioneAttiva
   *        The giunzioneAttiva to set.
   */
  public void setGiunzioneAttiva(short giunzioneAttiva) {

    this.giunzioneAttiva = giunzioneAttiva;
  }

  /**
   * @return Ritorna campiTabella1.
   */
  public String getCampiTabella1() {
    return campiTabella1;
  }

  /**
   * @param campiTabella1
   *        campiTabella1 da settare internamente alla classe.
   */
  public void setCampiTabella1(String campiTabella1) {
    this.campiTabella1 = campiTabella1;
  }

  /**
   * @return Ritorna campiTabella2.
   */
  public String getCampiTabella2() {
    return campiTabella2;
  }

  /**
   * @param campiTabella2
   *        campiTabella2 da settare internamente alla classe.
   */
  public void setCampiTabella2(String campiTabella2) {
    this.campiTabella2 = campiTabella2;
  }

}