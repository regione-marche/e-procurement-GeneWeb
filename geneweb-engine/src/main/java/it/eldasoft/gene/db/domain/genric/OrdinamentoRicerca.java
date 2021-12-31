/*
 * Created on 15-lug-2005
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.genric;

import it.eldasoft.utils.sql.comp.SqlElementoOrderBy;

import java.io.Serializable;

/**
 * Rappresenta un elemento dell'elenco degli ordinamenti usati 
 * in una ricerca, presenti nella tabella WEB_RICERCHE_ORDINAMENTI
 * 
 * @author Luca.Giacomazzo
 */
public class OrdinamentoRicerca implements Serializable {
  
  /**   UID   */
  private static final long serialVersionUID = 1640658920232549696L;
  
  public static final short ORDINAMENTO_ASCENDENTE = SqlElementoOrderBy.ORDINAMENTO_ASCENDENTE; 
  public static final short ORDINAMENTO_DISCENDENTE = SqlElementoOrderBy.ORDINAMENTO_DISCENDENTE;

  private Integer id;
  private int progressivo;
  private String mnemonicoTabella;
  private String aliasTabella;
  private String mnemonicoCampo;
  private int ordinamento;
  
  public OrdinamentoRicerca() {
    this.id = null;
    this.progressivo = 0;
    this.mnemonicoTabella = null;
    this.aliasTabella = null;
    this.mnemonicoCampo = null;
    this.ordinamento = OrdinamentoRicerca.ORDINAMENTO_ASCENDENTE;
  }
  
  /**
   * @return Returns the aliasTabella.
   */
  public String getAliasTabella() {
    return aliasTabella;
  }
  /**
   * @param aliasTabella The aliasTabella to set.
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
   * @param id The id to set.
   */
  public void setId(Integer id) {
    this.id = id;
  }
  /**
   * @return Returns the mnemonicoCampo.
   */
  public String getMnemonicoCampo() {
    return mnemonicoCampo;
  }
  /**
   * @param mnemonicoCampo The mnemonicoCampo to set.
   */
  public void setMnemonicoCampo(String mnemonicoCampo) {
    this.mnemonicoCampo = mnemonicoCampo;
  }
  /**
   * @return Returns the mnemonicoTabella.
   */
  public String getMnemonicoTabella() {
    return mnemonicoTabella;
  }
  /**
   * @param mnemonicoTabella The mnemonicoTabella to set.
   */
  public void setMnemonicoTabella(String mnemonicoTabella) {
    this.mnemonicoTabella = mnemonicoTabella;
  }
  /**
   * @return Returns the ordinamento.
   */
  public int getOrdinamento() {
    return ordinamento;
  }
  /**
   * @param ordinamento The ordinamento to set.
   */
  public void setOrdinamento(int ordinamento) {
    this.ordinamento = ordinamento;
  }
  /**
   * @return Returns the progressivo.
   */
  public int getProgressivo() {
    return progressivo;
  }
  /**
   * @param progressivo The progressivo to set.
   */
  public void setProgressivo(int progressivo) {
    this.progressivo = progressivo;
  }
}