/*
 * Created on 01-09-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 *
 * Created by Marco Franceschin
 * @see it.eldasoft.gene.web.struts.genmod.ModelliForm
 */

package it.eldasoft.gene.db.domain.genmod;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * Dati del modello utilizzati dalla connessione al database
 *
 * @author marco.franceschin
 * @see it.eldasoft.gene.web.struts.genmod.ModelliForm
 */
public class DatiModello implements Serializable {

  /**
   * UID
   */
  private static final long serialVersionUID = 7488347629720450841L;

  /*
   * Dati appartenenti al modello
   */
  private int               idModello;

  private String            tipoModello;

  private String            nomeModello;

  private String            descrModello;

  private String            nomeFile;

  private int               disponibile;

  private String            codiceApplicativo;

  private Integer           owner;

  private String            nomeOwner;

  private String            entPrinc;

  private Integer           prospetto;

  private int               personale;

  private String            profiloOwner;

  private int               riepilogativo;

  private String            filtroEntPrinc;
  
  private int               pdf;

  private Integer           idRicercaSrc;

  private String            nomeRicercaSrc;
  
  private Integer           versione;

  public DatiModello() {
    this.idModello = 0;
    this.tipoModello = null;
    this.nomeModello = null;
    this.descrModello = null;
    this.nomeFile = null;
    this.disponibile = 0;
    this.codiceApplicativo = null;
    this.owner = null;
    this.nomeOwner = null;
    this.entPrinc = null;
    this.prospetto = null;
    this.personale = 0;
    this.profiloOwner = null;
    this.riepilogativo = 0;
    this.filtroEntPrinc = null;
    this.pdf = 0;
    this.idRicercaSrc = null;
    this.nomeRicercaSrc = null;
    this.versione = null;
  }

  /**
   * Funzione che restituisce il nome del modello con convertiti gli apici
   * singoli per i javascript
   *
   * @return Stringa con il replace degli apici singoli
   */
  public String getNomeModelloPerJs() {
    if (this.nomeModello == null) return null;
    return StringUtils.replace(this.nomeModello, "'", "\\\'");
  }

  /**
   * @return Returns the descrModello.
   */
  public String getDescrModello() {
    return descrModello;
  }

  /**
   * @param descrModello
   *        The descrModello to set.
   */
  public void setDescrModello(String descrModello) {
    this.descrModello = descrModello;
  }

  /**
   * @return Returns the disponibile.
   */
  public int getDisponibile() {
    return disponibile;
  }

  /**
   * @param disponibile
   *        The disponibile to set.
   */
  public void setDisponibile(int disponibile) {
    this.disponibile = disponibile;
  }

  /**
   * @return Returns the idModello.
   */
  public int getIdModello() {
    return idModello;
  }

  /**
   * @param idModello
   *        The idModello to set.
   */
  public void setIdModello(int idModello) {
    this.idModello = idModello;
  }

  /**
   * @return Returns the nomeFile.
   */
  public String getNomeFile() {
    return nomeFile;
  }

  /**
   * @param nomeFile
   *        The nomeFile to set.
   */
  public void setNomeFile(String nomeFile) {
    this.nomeFile = nomeFile;
  }

  /**
   * @return Returns the nomeModello.
   */
  public String getNomeModello() {
    return nomeModello;
  }

  /**
   * @param nomeModello
   *        The nomeModello to set.
   */
  public void setNomeModello(String nomeModello) {
    this.nomeModello = nomeModello;
  }

  /**
   * @return Returns the tipoModello.
   */
  public String getTipoModello() {
    return tipoModello;
  }

  /**
   * @param tipoModello
   *        The tipoModello to set.
   */
  public void setTipoModello(String tipoModello) {
    this.tipoModello = tipoModello;
  }

  /**
   * @return Returns the codiceApplicativo.
   */
  public String getCodiceApplicativo() {
    return codiceApplicativo;
  }

  /**
   * @param codiceApplicativo
   *        The codiceApplicativo to set.
   */
  public void setCodiceApplicativo(String codiceApplicativo) {
    this.codiceApplicativo = codiceApplicativo;
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
   * @return Ritorna nomeOwner.
   */
  public String getNomeOwner() {
    return nomeOwner;
  }

  /**
   * @param nomeOwner
   *        nomeOwner da settare internamente alla classe.
   */
  public void setNomeOwner(String nomeOwner) {
    this.nomeOwner = nomeOwner;
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
   * @return Ritorna personale.
   */
  public int getPersonale() {
    return personale;
  }

  /**
   * @param personale
   *        personale da settare internamente alla classe.
   */
  public void setPersonale(int personale) {
    this.personale = personale;
  }

  /**
   * @return Ritorna prospetto.
   */
  public Integer getProspetto() {
    return prospetto;
  }

  /**
   * @param prospetto
   *        prospetto da settare internamente alla classe.
   */
  public void setProspetto(Integer prospetto) {
    this.prospetto = prospetto;
  }

  /**
   * @return Ritorna profiloOwner.
   */
  public String getProfiloOwner() {
    return profiloOwner;
  }

  /**
   * @param profiloOwner
   *        profiloOwner da settare internamente alla classe.
   */
  public void setProfiloOwner(String profiloOwner) {
    this.profiloOwner = profiloOwner;
  }

  /**
   * @return Ritorna riepilogativo.
   */
  public int getRiepilogativo() {
    return riepilogativo;
  }

  /**
   * @param riepilogativo
   *        riepilogativo da settare internamente alla classe.
   */
  public void setRiepilogativo(int riepilogativo) {
    this.riepilogativo = riepilogativo;
  }

  /**
   * @return Ritorna filtroEntPrinc.
   */
  public String getFiltroEntPrinc() {
    return filtroEntPrinc;
  }

  /**
   * @param filtroEntPrinc filtroEntPrinc da settare internamente alla classe.
   */
  public void setFiltroEntPrinc(String filtroEntPrinc) {
    this.filtroEntPrinc = filtroEntPrinc;
  }

  /**
   * @return Ritorna pdf.
   */
  public int getPdf() {
    return pdf;
  }

  /**
   * @param pdf
   *        pdf da settare internamente alla classe.
   */
  public void setPdf(int pdf) {
    this.pdf = pdf;
  }
  
  /**
   * @return Ritorna idRicercaSrc.
   */
  public Integer getIdRicercaSrc() {
    return idRicercaSrc;
  }

  /**
   * @param idRicercaSrc idRicercaSrc da settare internamente alla classe.
   */
  public void setIdRicercaSrc(Integer idRicercaSrc) {
    this.idRicercaSrc = idRicercaSrc;
  }

  /**
   * @return Ritorna nomeRicercaSrc.
   */
  public String getNomeRicercaSrc() {
    return nomeRicercaSrc;
  }

  /**
   * @param nomeRicercaSrc nomeRicercaSrc da settare internamente alla classe.
   */
  public void setNomeRicercaSrc(String nomeRicercaSrc) {
    this.nomeRicercaSrc = nomeRicercaSrc;
  }
  
  /**
   * @return Ritorna versione.
   */
  public Integer getVersione() {
    return versione;
  }
  
  /**
   * @param versione versione da settare internamente alla classe.
   */
  public void setVersione(Integer versione) {
    this.versione = versione;
  }

}