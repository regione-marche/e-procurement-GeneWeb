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
  private String viaein;
  private String nciein;
  private String citein;
  private String proein;
  private String capein;
  private String codnaz;

  
  /**
   * @return the viaein
   */
  public String getViaein() {
    return viaein;
  }

  
  /**
   * @param viaein the viaein to set
   */
  public void setViaein(String viaein) {
    this.viaein = viaein;
  }

  
  /**
   * @return the nciein
   */
  public String getNciein() {
    return nciein;
  }

  
  /**
   * @param nciein the nciein to set
   */
  public void setNciein(String nciein) {
    this.nciein = nciein;
  }

  
  /**
   * @return the citein
   */
  public String getCitein() {
    return citein;
  }

  
  /**
   * @param citein the citein to set
   */
  public void setCitein(String citein) {
    this.citein = citein;
  }

  
  /**
   * @return the proein
   */
  public String getProein() {
    return proein;
  }

  
  /**
   * @param proein the proein to set
   */
  public void setProein(String proein) {
    this.proein = proein;
  }

  
  /**
   * @return the capein
   */
  public String getCapein() {
    return capein;
  }

  
  /**
   * @param capein the capein to set
   */
  public void setCapein(String capein) {
    this.capein = capein;
  }

  
  /**
   * @return the codnaz
   */
  public String getCodnaz() {
    return codnaz;
  }

  
  /**
   * @param codnaz the codnaz to set
   */
  public void setCodnaz(String codnaz) {
    this.codnaz = codnaz;
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


  @Override
  public String toString() {
    return "UfficioIntestatario ["
        + (codice != null ? "codice=" + codice + ", " : "")
        + (nome != null ? "nome=" + nome + ", " : "")
        + (codFiscale != null ? "codFiscale=" + codFiscale + ", " : "")
        + (partitaIVA != null ? "partitaIVA=" + partitaIVA + ", " : "")
        + (dataTermineValidita != null ? "dataTermineValidita=" + dataTermineValidita + ", " : "")
        + (viaein != null ? "viaein=" + viaein + ", " : "")
        + (nciein != null ? "nciein=" + nciein + ", " : "")
        + (citein != null ? "citein=" + citein + ", " : "")
        + (proein != null ? "proein=" + proein + ", " : "")
        + (capein != null ? "capein=" + capein + ", " : "")
        + (codnaz != null ? "codnaz=" + codnaz : "")
        + "]";
  }

}
