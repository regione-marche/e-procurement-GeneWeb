/*
 * Created on 21-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo nome sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.genric;

import java.io.Serializable;

/**
 * Rappresenta i dati di testata di una ricerca, presenti nella tabella
 * W_RICERCHE
 *
 * @author Luca.Giacomazzo
 */

public class DatiGenRicerca implements Serializable {

  /** UID */
  private static final long serialVersionUID = -3845611496841427261L;

  private Integer           idRicerca;
  private Integer           tipo;
  private String            nome;
  private String            descrizione;
  private int               disp;
  private int               valDistinti;
  private Integer           risPerPag;
  private int               visModelli;
  private String            entPrinc;
  private String            codApp;
  private Integer           owner;
  private Integer           famiglia;
  private Integer           idProspetto;
  private int               personale;
  private int               filtroUtente;
  private String            profiloOwner;
  private Integer           visParametri;
  private Integer           linkScheda;
  private String            codReportWS;
  private int               filtroUfficioIntestatario;
  private String            defSql;
  private Integer           versione;
  private String            tipoDB;

  public DatiGenRicerca() {
    this.idRicerca = null;
    this.tipo = null;
    this.nome = null;
    this.descrizione = null;
    this.disp = 1;
    this.valDistinti = 0;
    this.risPerPag = null;
    this.visModelli = 0;
    this.entPrinc = null;
    this.codApp = null;
    this.owner = null;
    this.famiglia = null;
    this.idProspetto = null;
    this.personale = 0;
    this.filtroUtente = 0;
    this.profiloOwner = null;
    this.visParametri = null;
    this.linkScheda = null;
    this.codReportWS = null;
    this.filtroUfficioIntestatario = 0;
    this.defSql = null;
    this.versione = null;
    this.tipoDB =  null;
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
  public void setNome(String codice) {
    this.nome = codice;
  }

  /**
   * @return Returns the descrizione.
   */
  public String getDescrizione() {
    return descrizione;
  }

  /**
   * @param descrizione
   *        The descrizione to set.
   */
  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }

  /**
   * @return Returns the idRicerca.
   */
  public Integer getIdRicerca() {
    return idRicerca;
  }

  /**
   * @param idRic
   *        The idRicerca to set.
   */
  public void setIdRicerca(Integer idRic) {
    this.idRicerca = idRic;
  }

  /**
   * @return Returns the valDistinti.
   */
  public int getValDistinti() {

    return valDistinti;
  }

  /**
   * @param valDistinti
   *        The valDistinti to set.
   */
  public void setValDistinti(int valoriDistinti) {

    this.valDistinti = valoriDistinti;
  }

  /**
   * @return Ritorna disp.
   */
  public int getDisp() {
    return disp;
  }

  /**
   * @param disp
   *        disp da settare internamente alla classe.
   */
  public void setDisp(int disp) {
    this.disp = disp;
  }

  /**
   * @return Ritorna risPerPag.
   */
  public Integer getRisPerPag() {
    return risPerPag;
  }

  /**
   * @param risPerPag
   *        risPerPag da settare internamente alla classe.
   */
  public void setRisPerPag(Integer risPerPag) {
    this.risPerPag = risPerPag;
  }

  /**
   * @return Ritorna tipo.
   */
  public Integer getTipo() {
    return tipo;
  }

  /**
   * @param tipo
   *        tipo da settare internamente alla classe.
   */
  public void setTipo(Integer tipo) {
    this.tipo = tipo;
  }

  /**
   * @return Ritorna visModelli.
   */
  public int getVisModelli() {
    return visModelli;
  }

  /**
   * @param visModelli
   *        visModelli da settare internamente alla classe.
   */
  public void setVisModelli(int visModelli) {
    this.visModelli = visModelli;
  }

  /**
   * @return Ritorna entPrinc.
   */
  public String getEntPrinc() {
    return entPrinc;
  }

  /**
   * @param entPrinc
   *        entPrinc da settare internamente alla classe.
   */
  public void setEntPrinc(String entPrinc) {
    this.entPrinc = entPrinc;
  }

  /**
   * @return Ritorna codApp.
   */
  public String getCodApp() {
    return codApp;
  }

  /**
   * @param codApp
   *        codApp da settare internamente alla classe.
   */
  public void setCodApp(String codApp) {
    this.codApp = codApp;
  }

  /**
   * @return Ritorna owner.
   */
  public Integer getOwner() {
    return owner;
  }

  /**
   * @param owner
   *        owner da settare internamente alla classe.
   */
  public void setOwner(Integer owner) {
    this.owner = owner;
  }

  /**
   * @return Ritorna famiglia.
   */
  public Integer getFamiglia() {
    return famiglia;
  }

  /**
   * @param famiglia famiglia da settare internamente alla classe.
   */
  public void setFamiglia(Integer famiglia) {
    this.famiglia = famiglia;
  }

  /**
   * @return Ritorna idProspetto.
   */
  public Integer getIdProspetto() {
    return idProspetto;
  }

  /**
   * @param idProspetto idProspetto da settare internamente alla classe.
   */
  public void setIdProspetto(Integer idProspetto) {
    this.idProspetto = idProspetto;
  }

  /**
   * @return Ritorna personale.
   */
  public int getPersonale() {
    return personale;
  }

  /**
   * @param personale personale da settare internamente alla classe.
   */
  public void setPersonale(int personale) {
    this.personale = personale;
  }

  /**
   * @return Ritorna filtroIdUtente.
   */
  public int getFiltroUtente() {
    return filtroUtente;
  }

  /**
   * @param filtroIdUtente filtroIdUtente da settare internamente alla classe.
   */
  public void setFiltroUtente(int filtroIdUtente) {
    this.filtroUtente = filtroIdUtente;
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
   * @return Ritorna visParametri.
   */
  public Integer getVisParametri() {
    return visParametri;
  }

  /**
   * @param visParametri visParametri da settare internamente alla classe.
   */
  public void setVisParametri(Integer visParametri) {
    this.visParametri = visParametri;
  }

  /**
   * @return Ritorna linkScheda.
   */
  public Integer getLinkScheda() {
    return linkScheda;
  }

  /**
   * @param linkScheda linkScheda da settare internamente alla classe.
   */
  public void setLinkScheda(Integer linkScheda) {
    this.linkScheda = linkScheda;
  }

  /**
   * @return Ritorna codReportWS.
   */
  public String getCodReportWS() {
    return codReportWS;
  }

  /**
   * @param codReportWS codReportWS da settare internamente alla classe.
   */
  public void setCodReportWS(String codReportWS) {
    this.codReportWS = codReportWS;
  }

  /**
   * @return Ritorna fltroUfficioIntestatario.
   */
  public int getFiltroUfficioIntestatario() {
    return filtroUfficioIntestatario;
  }

  /**
   * @param filtroUfficioIntestatario filtroUfficioIntestatario da settare internamente alla classe.
   */
  public void setFiltroUfficioIntestatario(int filtroUfficioIntestatario) {
    this.filtroUfficioIntestatario = filtroUfficioIntestatario;
  }

  /**
   * @return Ritorna defSql.
   */
  public String getDefSql() {
    return defSql;
  }

  /**
   * @param defSql defSql da settare internamente alla classe.
   */
  public void setDefSql(String defSql) {
    this.defSql = defSql;
  }

  /**
   * @return Ritorna versione.
   */
  public Integer getVersione() {
    return versione;
  }

  /**
   * @param versione da settare internamente alla classe.
   */
  public void setVersione(Integer versione) {
    this.versione = versione;
  }

  /**
   * @return Ritorna tipoDB.
   */
  public String getTipoDB() {
    return tipoDB;
  }

  /**
   * @param tipoDB da settare internamente alla classe.
   */
  public void setTipoDB(String tipoDB) {
    this.tipoDB = tipoDB;
  }

}