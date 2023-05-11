/*
 * Created on 13-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.console.db.domain.schedric;

import java.io.Serializable;
import java.util.Date;

/**
 * Bean per l'interfacciamento con i dati presenti nella tabella W_SCHEDRIC
 * 
 * @author Francesco.DeFilippis
 */
public class SchedRic implements Serializable {

  /**
   * UID
   */
  private static final long serialVersionUID = 944115733233619505L;

  private Integer           idSchedRic;
  private int               idRicerca;
  private String            nomeRicerca;
  private String            tipo;
  private String            descTipo;
  private int               attivo;
  private String            nome;
  private int               oraAvvio;
  private int               minutoAvvio;
  private Date              dataPrimaEsec;
  private Integer           giorno;
  private Integer           settimana;
  private String            giorniSettimana;
  private String            mese;
  private String            giorniMese;
  private Integer           formato;
  private String            descFormato;
  private String            email;
  private Date              dataUltEsec;
  private Date              dataProxEsec;
  private int               owner;
  private String            nomeOwner;
  private int               esecutore;
  private String            nomeEsecutore;
  private String            profiloOwner;
  private int               noOutputVuoto;
  private String            codiceApplicazione;
  private Integer           ripetiDopoMinuti;

  public SchedRic() {
    this.idSchedRic = null;
    this.idRicerca = 0;
    this.nomeRicerca = null;
    this.tipo = null;
    this.descTipo = null;
    this.attivo = 0;
    this.nome = null;
    this.oraAvvio = 0;
    this.minutoAvvio = 0;
    this.dataPrimaEsec = null;
    this.giorno = null;
    this.settimana = null;
    this.giorniSettimana = null;
    this.mese = null;
    this.giorniMese = null;
    this.formato = null;
    this.descFormato = null;
    this.email = null;
    this.dataUltEsec = null;
    this.dataProxEsec = null;
    this.owner = 0;
    this.nomeOwner = null;
    this.esecutore = 0;
    this.nomeEsecutore = null;
    this.profiloOwner = null;
    this.noOutputVuoto = 1;
    this.codiceApplicazione = null;
    this.ripetiDopoMinuti = null;
  }

  /**
   * @return Returns the attivo.
   */
  public int getAttivo() {
    return attivo;
  }

  /**
   * @param attivo
   *        The attivo to set.
   */
  public void setAttivo(int attivo) {
    this.attivo = attivo;
  }

  /**
   * @return Returns the dataPrimaEsec.
   */
  public Date getDataPrimaEsec() {
    return dataPrimaEsec;
  }

  /**
   * @param dataPrimaEsec
   *        The dataPrimaEsec to set.
   */
  public void setDataPrimaEsec(Date dataPrimaEsec) {
    this.dataPrimaEsec = dataPrimaEsec;
  }

  /**
   * @return Returns the dataProxEsec.
   */
  public Date getDataProxEsec() {
    return dataProxEsec;
  }

  /**
   * @param dataProxEsec
   *        The dataProxEsec to set.
   */
  public void setDataProxEsec(Date dataProxEsec) {
    this.dataProxEsec = dataProxEsec;
  }

  /**
   * @return Returns the dataUltEsec.
   */
  public Date getDataUltEsec() {
    return dataUltEsec;
  }

  /**
   * @param dataUltEsec
   *        The dataUltEsec to set.
   */
  public void setDataUltEsec(Date dataUltEsec) {
    this.dataUltEsec = dataUltEsec;
  }

  /**
   * @return Returns the giorniMese.
   */
  public String getGiorniMese() {
    return giorniMese;
  }

  /**
   * @param giorniMese
   *        The giorniMese to set.
   */
  public void setGiorniMese(String giorniMese) {
    this.giorniMese = giorniMese;
  }

  /**
   * @return Returns the giorniSettimana.
   */
  public String getGiorniSettimana() {
    return giorniSettimana;
  }

  /**
   * @param giorniSettimana
   *        The giorniSettimana to set.
   */
  public void setGiorniSettimana(String giorniSettimana) {
    this.giorniSettimana = giorniSettimana;
  }

  /**
   * @return Returns the idRicerca.
   */
  public int getIdRicerca() {
    return idRicerca;
  }

  /**
   * @param idRicerca
   *        The idRicerca to set.
   */
  public void setIdRicerca(int idRicerca) {
    this.idRicerca = idRicerca;
  }

  /**
   * @return Returns the idSchedric.
   */
  public Integer getIdSchedRic() {
    return idSchedRic;
  }

  /**
   * @return Returns the mese.
   */
  public String getMese() {
    return mese;
  }

  /**
   * @param mese
   *        The mese to set.
   */
  public void setMese(String mese) {
    this.mese = mese;
  }

  /**
   * @return Returns the minutoAvvio.
   */
  public int getMinutoAvvio() {
    return minutoAvvio;
  }

  /**
   * @param minutoAvvio
   *        The minutoAvvio to set.
   */
  public void setMinutoAvvio(int minutoAvvio) {
    this.minutoAvvio = minutoAvvio;
  }

  /**
   * @return Returns the nome.
   */
  public String getNome() {
    return nome;
  }

  /**
   * @param nome
   *        The nome to set.
   */
  public void setNome(String nome) {
    this.nome = nome;
  }

  /**
   * @return Returns the oraAvvio.
   */
  public int getOraAvvio() {
    return oraAvvio;
  }

  /**
   * @param oraAvvio
   *        The oraAvvio to set.
   */
  public void setOraAvvio(int oraAvvio) {
    this.oraAvvio = oraAvvio;
  }

  /**
   * @return Returns the owner.
   */
  public int getOwner() {
    return owner;
  }

  /**
   * @param owner
   *        The owner to set.
   */
  public void setOwner(int owner) {
    this.owner = owner;
  }

  /**
   * @return Returns the tipo.
   */
  public String getTipo() {
    return tipo;
  }

  /**
   * @param tipo
   *        The tipo to set.
   */
  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  /**
   * @return Returns the giorno.
   */
  public Integer getGiorno() {
    return giorno;
  }

  /**
   * @return Returns the settimana.
   */
  public Integer getSettimana() {
    return settimana;
  }

  /**
   * @return Returns the email.
   */
  public String getEmail() {
    return email;
  }

  /**
   * @param email
   *        The email to set.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * @return Returns the formato.
   */
  public Integer getFormato() {
    return formato;
  }

  /**
   * @param formato
   *        The formato to set.
   */
  public void setFormato(Integer formato) {
    this.formato = formato;
  }

  /**
   * @param idSchedRic
   *        The idSchedRic to set.
   */
  public void setIdSchedRic(Integer idSchedRic) {
    this.idSchedRic = idSchedRic;
  }

  /**
   * @return Returns the nomeRicerca.
   */
  public String getNomeRicerca() {
    return nomeRicerca;
  }

  /**
   * @param nomeRicerca
   *        The nomeRicerca to set.
   */
  public void setNomeRicerca(String nomeRicerca) {
    this.nomeRicerca = nomeRicerca;
  }

  /**
   * @return Returns the nomeOwner.
   */
  public String getNomeOwner() {
    return nomeOwner;
  }

  /**
   * @param nomeOwner
   *        The nomeOwner to set.
   */
  public void setNomeOwner(String nomeOwner) {
    this.nomeOwner = nomeOwner;
  }

  /**
   * @param giorno
   *        The giorno to set.
   */
  public void setGiorno(Integer giorno) {
    this.giorno = giorno;
  }

  /**
   * @param settimana
   *        The settimana to set.
   */
  public void setSettimana(Integer settimana) {
    this.settimana = settimana;
  }

  /**
   * @return Returns the descTipo.
   */
  public String getDescTipo() {
    return descTipo;
  }

  /**
   * @param descTipo
   *        The descTipo to set.
   */
  public void setDescTipo(String descTipo) {
    this.descTipo = descTipo;
  }

  /**
   * @return Returns the descFormato.
   */
  public String getDescFormato() {
    return descFormato;
  }

  /**
   * @param descFormato
   *        The descFormato to set.
   */
  public void setDescFormato(String descFormato) {
    this.descFormato = descFormato;
  }

  
  /**
   * @return Returns the esecutore.
   */
  public int getEsecutore() {
    return esecutore;
  }

  
  /**
   * @param esecutore The esecutore to set.
   */
  public void setEsecutore(int esecutore) {
    this.esecutore = esecutore;
  }
    
  /**
   * @return Ritorna nomeEsecutore.
   */
  public String getNomeEsecutore() {
    return nomeEsecutore;
  }
  
  /**
   * @param nomeEsecutore nomeEsecutore da settare internamente alla classe.
   */
  public void setNomeEsecutore(String nomeEsecutore) {
    this.nomeEsecutore = nomeEsecutore;
  }

  /**
   * @return Ritorna profiloOwner.
   */
  public String getProfiloOwner() {
    return profiloOwner;
  }
  
  /**
   * @param profiloOwner profiloOwner da settare internamente alla classe.
   */
  public void setProfiloOwner(String profiloOwner) {
    this.profiloOwner = profiloOwner;
  }

  /**
   * @return Ritorna noOutputVuoto.
   */
  public int getNoOutputVuoto() {
    return noOutputVuoto;
  }
  
  /**
   * @param noOutputVuoto noOutputVuoto da settare internamente alla classe.
   */
  public void setNoOutputVuoto(int noOutputVuoto) {
    this.noOutputVuoto = noOutputVuoto;
  }
  
  /**
   * @return Ritorna codiceApplicazione.
   */
  public String getCodiceApplicazione() {
    return codiceApplicazione;
  }
  
  /**
   * @param codiceApplicazione codiceApplicazione da settare internamente alla classe.
   */
  public void setCodiceApplicazione(String codiceApplicazione) {
    this.codiceApplicazione = codiceApplicazione;
  }
  
  /**
   * @return Ritorna ripetiDopoMinuti.
   */
  public Integer getRipetiDopoMinuti() {
    return ripetiDopoMinuti;
  }
  
  /**
   * @param ripetiDopoMinuti ripetiDopoMinuti da settare internamente alla classe.
   */
  public void setRipetiDopoMinuti(Integer ripetiDopoMinuti) {
    this.ripetiDopoMinuti = ripetiDopoMinuti;
  }
 
}