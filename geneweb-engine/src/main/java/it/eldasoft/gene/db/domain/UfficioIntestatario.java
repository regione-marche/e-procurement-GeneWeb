/*
 * Created on 02/ott/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Bean per l'interfacciamento con i dati presenti nella tabella UFFINT
 *
 * @author Stefano.Sabbadin
 */
public class UfficioIntestatario implements Serializable {

  /**
   * UID
   */
  private static final long serialVersionUID = -1590267675316804154L;

  private String            codice;
  private String            nome;
  private String            codFiscale;
  private String            partitaIVA;
  private Date              dataTermineValidita;

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
   * @return Ritorna nome.
   */
  public String getNome() {
    return nome;
  }

  /**
   * @param nome
   *        nome da settare internamente alla classe.
   */
  public void setNome(String nome) {
    this.nome = nome;
  }

  /**
   * @return Ritorna codFiscale.
   */
  public String getCodFiscale() {
    return codFiscale;
  }

  /**
   * @param codFiscale
   *        codFiscale da settare internamente alla classe.
   */
  public void setCodFiscale(String codFiscale) {
    this.codFiscale = codFiscale;
  }

  /**
   * @return Ritorna partitaIVA.
   */
  public String getPartitaIVA() {
    return partitaIVA;
  }

  /**
   * @param partitaIVA
   *        partitaIVA da settare internamente alla classe.
   */
  public void setPartitaIVA(String partitaIVA) {
    this.partitaIVA = partitaIVA;
  }

  /**
   * @return Ritorna dataTermineValidita.
   */
  public Date getDataTermineValidita() {
    return dataTermineValidita;
  }

  /**
   * @param partitaIVA
   *        partitaIVA da settare internamente alla classe.
   */
  public void setDataTermineValidita(Date dataTermineValidita) {
    this.dataTermineValidita = dataTermineValidita;
  }

}
