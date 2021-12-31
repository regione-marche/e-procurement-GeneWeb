/*
 * Created on 28-feb-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo nome sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.prospetto;

import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genric.DatiGenProspetto;
import it.eldasoft.gene.db.domain.genric.DatiGenRicerca;
import it.eldasoft.utils.metadata.cache.DizionarioSchemi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Schema;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

/**
 * Form per i dati generali di una ricerca con modello: rappresenta una
 * versione ibrida tra quella standard per le ricerche e quella presente per la
 * gestione dei modelli. Infatti gli attributti di questo form sono l'unione
 * degli attributi dei form TestataRicercaForm (o DatiGenRicercaForm) e
 * ModelliForm.
 *
 * @author Luca.Giacomazzo
 */
public class DatiGenProspettoForm extends ActionForm {

  /**   UID   */
  private static final long serialVersionUID = -4467005797785942175L;

  //Da TestataRicercaForm
  private String            idRicerca;
  private String            tipoRicerca;
  private String            nome;
  private String            descrRicerca;
  private boolean           ricercaDisponibile;
  private String            codApp;
  private Integer           owner;
  private Integer           famiglia;
  private Integer           idProspetto;
  private boolean           personale;
  private String            profiloOwner;
  private String            codReportWS;

  //Da ModelliForm
  private int               idModello;
  private String            tipoModello;
  private String            nomeFile;
  private FormFile          selezioneFile;
  private Integer           prospetto;
  /**
   * 0 = fonte dati DB (va valorizzato schema ed entità), 1 = fonte dati report
   *
   * @since 1.5.0
   */
  private int               tipoFonteDati;
  private String            entPrinc;
  private String            schemaPrinc;
  private String            descSchemaPrinc;
  private String            mneEntPrinc;
  private String            descEntPrinc;
  private String            idRicercaSrc;
  private String            nomeRicercaSrc;

// N.B.: gli attributi codApp, owner, entPrinc, personale sono in comune per
// le classi DatiGenRicerca e DatiModello

  public DatiGenProspettoForm() {
    super();
    this.inizializzaOggetto();
  }

  private void inizializzaOggetto() {

    this.idRicerca = null;
    this.tipoRicerca = null;
    this.nome = null;
    this.descrRicerca = null;
    this.ricercaDisponibile = false;
    this.codApp = null;
    this.owner = null;
    this.famiglia = null;
    this.idProspetto = null;
    this.personale = false;
    this.profiloOwner = null;
    this.codReportWS = null;

    this.idModello = 0;
    this.tipoModello = null;
    this.nomeFile = null;
    this.selezioneFile = null;
    this.prospetto = null;
    this.tipoFonteDati = 0;
    this.entPrinc = null;
    this.schemaPrinc = null;
    this.descSchemaPrinc = null;
    this.mneEntPrinc = null;
    this.descEntPrinc = null;
    this.idRicercaSrc  = null;
    this.nomeRicercaSrc = null;
  }

  public DatiGenProspettoForm(DatiGenProspetto datiGenProspetto) {
    DatiGenRicerca datiGenRic = datiGenProspetto.getDatiGenRicerca();
    DatiModello datiModello = datiGenProspetto.getDatiModello();

    this.idRicerca = datiGenRic.getIdRicerca().toString();
    this.tipoRicerca = datiGenRic.getTipo().toString();
    this.nome = datiGenRic.getNome();
    this.descrRicerca = datiGenRic.getDescrizione();
    this.ricercaDisponibile = (datiGenRic.getDisp() == 1 ? true : false);
    this.codApp = datiGenRic.getCodApp();
    this.owner = datiGenRic.getOwner();
    this.famiglia = datiGenRic.getFamiglia();
    this.idProspetto = datiGenRic.getIdProspetto();
    this.personale = (datiGenRic.getPersonale() == 1 ? true : false);
    this.profiloOwner = datiGenRic.getProfiloOwner();
    this.codReportWS = datiGenRic.getCodReportWS();

    this.idModello = datiModello.getIdModello();
    this.tipoModello = datiModello.getTipoModello();
    this.nomeFile = datiModello.getNomeFile();
    this.entPrinc = datiModello.getEntPrinc();
    this.selezioneFile = null;
    this.prospetto = datiModello.getProspetto();
    if (this.entPrinc != null) {
      DizionarioSchemi dizSchemi = DizionarioSchemi.getInstance();
      DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
      Tabella tab = dizTabelle.getDaNomeTabella(this.entPrinc);
      this.mneEntPrinc = tab.getCodiceMnemonico();
      this.descEntPrinc = tab.getDescrizione();
      Schema sch = dizSchemi.get(tab.getNomeSchema());
      this.schemaPrinc = sch.getCodice();
      this.descSchemaPrinc = sch.getDescrizione();
    }
    this.idRicercaSrc = (datiModello.getIdRicercaSrc() == null ? null : datiModello.getIdRicercaSrc().toString());
    this.nomeRicercaSrc = datiModello.getNomeRicercaSrc();
    if (this.idRicercaSrc != null)
      this.tipoFonteDati = 1;
    else
      this.tipoFonteDati = 0;
  }

  public DatiGenRicerca getDatiPerRicercaModel() {
    DatiGenRicerca datiGenerali = new DatiGenRicerca();

    datiGenerali.setIdRicerca(UtilityNumeri.convertiIntero(this.idRicerca));
    datiGenerali.setTipo(UtilityNumeri.convertiIntero(this.tipoRicerca));
    datiGenerali.setNome(this.nome);
    datiGenerali.setDescrizione(UtilityStringhe.convertiStringaVuotaInNull(this.descrRicerca));
    datiGenerali.setDisp(this.ricercaDisponibile ? 1 : 0);
    datiGenerali.setCodApp(UtilityStringhe.convertiStringaVuotaInNull(this.codApp));
    datiGenerali.setOwner(this.owner);
    datiGenerali.setFamiglia(this.famiglia);
    datiGenerali.setIdProspetto(new Integer(this.idModello));
    datiGenerali.setPersonale(this.personale ? 1 : 0);
    datiGenerali.setEntPrinc(UtilityStringhe.convertiStringaVuotaInNull(this.entPrinc));
    datiGenerali.setProfiloOwner(this.profiloOwner);
    datiGenerali.setCodReportWS(this.codReportWS);

    datiGenerali.setValDistinti(0);
    datiGenerali.setVisModelli(0);
    datiGenerali.setFiltroUtente(0);
    return datiGenerali;
  }

  public DatiModello getDatiPerModelloModel() {
    DatiModello datiModello = new DatiModello();

    datiModello.setIdModello(this.idModello);
    datiModello.setTipoModello(this.tipoModello);
    datiModello.setNomeModello(this.nome);
    datiModello.setDescrModello(UtilityStringhe.convertiStringaVuotaInNull(this.descrRicerca));
    datiModello.setNomeFile(this.nomeFile);
    datiModello.setDisponibile(this.ricercaDisponibile ? 1 : 0);
    datiModello.setCodiceApplicativo(UtilityStringhe.convertiStringaVuotaInNull(this.codApp));
    datiModello.setOwner(this.owner);
    datiModello.setEntPrinc(UtilityStringhe.convertiStringaVuotaInNull(this.entPrinc));
    datiModello.setProspetto(this.prospetto);
    datiModello.setPersonale(this.personale ? 1 : 0);
    datiModello.setProfiloOwner(this.profiloOwner);
    datiModello.setIdRicercaSrc(UtilityStringhe.convertiStringaVuotaInNull(this.idRicercaSrc) == null ? null : Integer.valueOf(this.idRicercaSrc));

    return datiModello;
  }

  /**
   * @return Returns the password.
   */
  public String getDescrRicerca() {
    return descrRicerca;
  }

  /**
   * @param password
   *        The password to set.
   */
  public void setDescrRicerca(String password) {
    this.descrRicerca = password;
  }

  /**
   * @return Returns the password.
   */
  public String getNome() {
    return this.nome;
  }

  /**
   * @param password
   *        The password to set.
   */
  public void setNome(String password) {
    this.nome = password;
  }

  /**
   * @return Returns the id.
   */
  public String getIdRicerca() {
    return this.idRicerca;
  }

  /**
   * @param id
   *        The id to set.
   */
  public void setIdRicerca(String id) {
    this.idRicerca = id;
  }

  /**
   * @return Ritorna disp.
   */
  public boolean getRicercaDisponibile() {
    return ricercaDisponibile;
  }

  /**
   * @param disp
   *        disp da settare internamente alla classe.
   */
  public void setRicercaDisponibile(boolean disp) {
    this.ricercaDisponibile = disp;
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
   * @return Ritorna tipoRicerca.
   */
  public String getTipoRicerca() {
    return tipoRicerca;
  }

  /**
   * @param tipoRicerca
   *        tipoRicerca da settare internamente alla classe.
   */
  public void setTipoRicerca(String tipo) {
    this.tipoRicerca = tipo;
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

  @Override
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.inizializzaOggetto();
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
   * @param personale personale da settare internamente alla classe.
   */
  public void setPersonale(boolean personale) {
    this.personale = personale;
  }

  /**
   * @return Ritorna nomeFile.
   */
  public String getNomeFile() {
    return nomeFile;
  }

  /**
   * @param nomeFile nomeFile da settare internamente alla classe.
   */
  public void setNomeFile(String nomeFile) {
    this.nomeFile = nomeFile;
  }

  /**
   * @return Ritorna selezioneFile.
   */
  public FormFile getSelezioneFile() {
    return selezioneFile;
  }

  /**
   * @param selezioneFile selezioneFile da settare internamente alla classe.
   */
  public void setSelezioneFile(FormFile selezioneFile) {
    this.selezioneFile = selezioneFile;
  }

  /**
   * @return Ritorna descEntPrinc.
   */
  public String getDescEntPrinc() {
    return descEntPrinc;
  }

  /**
   * @param descEntPrinc descEntPrinc da settare internamente alla classe.
   */
  public void setDescEntPrinc(String descEntPrinc) {
    this.descEntPrinc = descEntPrinc;
  }

  /**
   * @return Ritorna descSchemaPrinc.
   */
  public String getDescSchemaPrinc() {
    return descSchemaPrinc;
  }

  /**
   * @param descSchemaPrinc descSchemaPrinc da settare internamente alla classe.
   */
  public void setDescSchemaPrinc(String descSchemaPrinc) {
    this.descSchemaPrinc = descSchemaPrinc;
  }

  /**
   * @return Ritorna idModello.
   */
  public int getIdModello() {
    return idModello;
  }

  /**
   * @param idModello idModello da settare internamente alla classe.
   */
  public void setIdModello(int idModello) {
    this.idModello = idModello;
  }

  /**
   * @return Ritorna mneEntPrinc.
   */
  public String getMneEntPrinc() {
    return mneEntPrinc;
  }

  /**
   * @param mneEntPrinc mneEntPrinc da settare internamente alla classe.
   */
  public void setMneEntPrinc(String mneEntPrinc) {
    this.mneEntPrinc = mneEntPrinc;
  }

  /**
   * @return Ritorna prospetto.
   */
  public Integer getProspetto() {
    return prospetto;
  }

  /**
   * @param prospetto prospetto da settare internamente alla classe.
   */
  public void setProspetto(Integer prospetto) {
    this.prospetto = prospetto;
  }

  /**
   * @return Ritorna schemaPrinc.
   */
  public String getSchemaPrinc() {
    return schemaPrinc;
  }

  /**
   * @param schemaPrinc schemaPrinc da settare internamente alla classe.
   */
  public void setSchemaPrinc(String schemaPrinc) {
    this.schemaPrinc = schemaPrinc;
  }

  /**
   * @return Ritorna tipoModello.
   */
  public String getTipoModello() {
    return tipoModello;
  }

  /**
   * @param tipoModello tipoModello da settare internamente alla classe.
   */
  public void setTipoModello(String tipoModello) {
    this.tipoModello = tipoModello;
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
   * @return Ritorna tipoFonteDati.
   */
  public int getTipoFonteDati() {
    return tipoFonteDati;
  }

  /**
   * @param tipoFonteDati tipoFonteDati da settare internamente alla classe.
   */
  public void setTipoFonteDati(int tipoFonteDati) {
    this.tipoFonteDati = tipoFonteDati;
  }

  /**
   * @return Ritorna idRicercaSrc.
   */
  public String getIdRicercaSrc() {
    return idRicercaSrc;
  }

  /**
   * @param idRicercaSrc idRicercaSrc da settare internamente alla classe.
   */
  public void setIdRicercaSrc(String idRicercaSrc) {
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

}