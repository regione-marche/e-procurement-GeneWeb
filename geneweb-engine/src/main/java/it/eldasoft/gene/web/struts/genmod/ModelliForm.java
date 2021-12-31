/*
 * Created on 09-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod;

import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.ServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

public class ModelliForm extends ActionForm {

  /**
   * UID
   */
  private static final long serialVersionUID = 8975646697979681419L;

  /*
   * Dati appartenenti al modello
   */
  private int               idModello;

  private String            tipoModello;

  private String            nomeModello;

  private String            descrModello;

  private String            nomeFile;

  private boolean           disponibile;

  private FormFile          selezioneFile;

  private String            codiceApplicativo;

  private String            profiloOwner;

  private Integer           owner;

  private String            nomeOwner;

  private String            entPrinc;

  private Integer           prospetto;
  private boolean           personale;

  private String            schemaPrinc;
  private String            descSchemaPrinc;
  private String            mneEntPrinc;
  private String            descEntPrinc;

  private boolean           riepilogativo;

  private String            filtroEntPrinc;
  
  public ModelliForm() {
    super();
    this.inizializzaOggetto();
  }

  /*
   * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
   *      javax.servlet.ServletRequest)
   */
  public void reset(ActionMapping mapping, ServletRequest request) {
    super.reset(mapping, request);
    this.inizializzaOggetto();
  }

  private void inizializzaOggetto() {
    this.idModello = 0;
    this.tipoModello = null;
    this.nomeModello = null;
    this.descrModello = null;
    this.nomeFile = null;
    this.disponibile = false;
    this.selezioneFile = null;
    this.codiceApplicativo = null;
    this.profiloOwner = null;
    this.owner = null;
    this.nomeOwner = null;
    this.entPrinc = null;
    this.schemaPrinc = null;
    this.descSchemaPrinc = null;
    this.mneEntPrinc = null;
    this.descEntPrinc = null;
    this.prospetto = null;
    this.personale = false;
    this.riepilogativo = false;
    this.filtroEntPrinc = null;
  }

  public ModelliForm(DatiModello datiModel) {
    this.idModello = datiModel.getIdModello();
    this.tipoModello = datiModel.getTipoModello();
    this.nomeModello = datiModel.getNomeModello();
    this.descrModello = datiModel.getDescrModello();
    this.nomeFile = datiModel.getNomeFile();
    this.disponibile = (datiModel.getDisponibile() == 1 ? true : false);
    this.selezioneFile = null;
    this.codiceApplicativo = datiModel.getCodiceApplicativo();
    this.profiloOwner = datiModel.getProfiloOwner();
    this.owner = datiModel.getOwner();
    this.nomeOwner = datiModel.getNomeOwner();
    this.entPrinc = datiModel.getEntPrinc();
    this.prospetto = datiModel.getProspetto();
    this.personale = (datiModel.getPersonale() == 1 ? true : false);
    this.riepilogativo = (datiModel.getRiepilogativo() == 1 ? true : false);
    this.filtroEntPrinc = datiModel.getFiltroEntPrinc();
  }

  public DatiModello getDatiPerModel() {
    DatiModello datiModello = new DatiModello();

    datiModello.setIdModello(this.idModello);
    datiModello.setTipoModello(this.tipoModello);
    datiModello.setNomeModello(this.nomeModello);
    datiModello.setDescrModello(UtilityStringhe.convertiStringaVuotaInNull(this.descrModello));
    datiModello.setNomeFile(this.nomeFile);
    datiModello.setDisponibile(this.disponibile ? 1 : 0);
    datiModello.setCodiceApplicativo(UtilityStringhe.convertiStringaVuotaInNull(this.codiceApplicativo));
    datiModello.setProfiloOwner(this.profiloOwner);
    datiModello.setOwner(this.owner);
    datiModello.setNomeOwner(this.nomeOwner);
    datiModello.setEntPrinc(this.entPrinc);
    datiModello.setProspetto(this.prospetto);
    datiModello.setPersonale(this.personale ? 1 : 0);
    datiModello.setRiepilogativo(this.riepilogativo ? 1 : 0);
    datiModello.setFiltroEntPrinc(UtilityStringhe.convertiStringaVuotaInNull(this.filtroEntPrinc));

    return datiModello;
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
  public boolean getDisponibile() {
    return disponibile;
  }

  /**
   * @param disponibile
   *        The disponibile to set.
   */
  public void setDisponibile(boolean disponibile) {
    this.disponibile = disponibile;
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
   * @return Returns the selezioneFile.
   */
  public FormFile getSelezioneFile() {
    return selezioneFile;
  }

  /**
   * @param selezioneFile
   *        The selezioneFile to set.
   */
  public void setSelezioneFile(FormFile selezioneFile) {
    this.selezioneFile = selezioneFile;
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
   * @return Ritorna schemaPrinc.
   */
  public String getSchemaPrinc() {
    return schemaPrinc;
  }

  /**
   * @param schemaPrinc
   *        schemaPrinc da settare internamente alla classe.
   */
  public void setSchemaPrinc(String schemaPrinc) {
    this.schemaPrinc = schemaPrinc;
  }

  /**
   * @return Ritorna descSchemaPrinc.
   */
  public String getDescSchemaPrinc() {
    return descSchemaPrinc;
  }

  /**
   * @param descSchemaPrinc
   *        descSchemaPrinc da settare internamente alla classe.
   */
  public void setDescSchemaPrinc(String descSchemaPrinc) {
    this.descSchemaPrinc = descSchemaPrinc;
  }

  /**
   * @return Ritorna descEntPrinc.
   */
  public String getDescEntPrinc() {
    return descEntPrinc;
  }

  /**
   * @param descEntPrinc
   *        descEntPrinc da settare internamente alla classe.
   */
  public void setDescEntPrinc(String descEntPrinc) {
    this.descEntPrinc = descEntPrinc;
  }

  /**
   * @return Ritorna mneEntPrinc.
   */
  public String getMneEntPrinc() {
    return mneEntPrinc;
  }

  /**
   * @param mneEntPrinc
   *        mneEntPrinc da settare internamente alla classe.
   */
  public void setMneEntPrinc(String mneEntPrinc) {
    this.mneEntPrinc = mneEntPrinc;
  }

  /**
   * @return Ritorna personale.
   */
  public boolean isPersonale() {
    return personale;
  }

  /**
   * @return Ritorna personale.
   */
  public boolean getPersonale() {
    return personale;
  }

  /**
   * @param personale
   *        personale da settare internamente alla classe.
   */
  public void setPersonale(boolean personale) {
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
  public boolean isRiepilogativo() {
    return riepilogativo;
  }

  /**
   * @param riepilogativo
   *        riepilogativo da settare internamente alla classe.
   */
  public void setRiepilogativo(boolean riepilogativo) {
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

}