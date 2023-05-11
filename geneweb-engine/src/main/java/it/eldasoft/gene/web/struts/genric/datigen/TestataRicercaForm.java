/*
 * Created on 21-ag0-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo nome sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.datigen;

import it.eldasoft.gene.db.domain.genric.DatiGenRicerca;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author Luca.Giacomazzo
 */
public class TestataRicercaForm extends ActionForm {

  /** UID */
  private static final long serialVersionUID = -8021275841252225886L;

  private String            id;
  private String            tipoRicerca;
  private String            nome;
  private String            descrizione;
  private boolean           valDistinti;
  private boolean           disp;
  private String            risPerPag;
  private boolean           visModelli;
  private String            entPrinc;
  private String            codApp;
  private Integer           owner;
  private Integer           famiglia;
  private Integer           idProspetto;
  private boolean           personale;
  private boolean           filtroUtente;
  private String            profiloOwner;
  private Boolean           visParametri;
  private Boolean           linkScheda;
  private String            codReportWS;
  private boolean           filtroUfficioIntestatario;
  private String            defSql;
  private Integer           versione;

  public TestataRicercaForm() {
    super();
    this.inizializzaOggetto();
  }

  private void inizializzaOggetto() {
    this.id = null;
    this.tipoRicerca = null;
    this.nome = null;
    this.descrizione = null;
    this.valDistinti = false;
    this.disp = false;
    this.risPerPag = null;
    this.visModelli = false;
    this.entPrinc = null;
    this.codApp = null;
    this.owner = null;
    this.famiglia = null;
    this.idProspetto = null;
    this.personale = false;
    this.filtroUtente = false;
    this.profiloOwner = null;
    this.visParametri = null;
    this.linkScheda = null;
    this.codReportWS = null;
    this.filtroUfficioIntestatario = false;
    this.defSql = null;
    this.versione = null;
  }

  public TestataRicercaForm(DatiGenRicerca datiModel) {
    this.id = datiModel.getIdRicerca().toString();
    this.tipoRicerca = datiModel.getTipo().toString();
    this.nome = datiModel.getNome();
    this.descrizione = datiModel.getDescrizione();
    this.valDistinti = (datiModel.getValDistinti() == 1 ? true : false);
    this.disp = (datiModel.getDisp() == 1 ? true : false);

    this.risPerPag = (datiModel.getRisPerPag() == null
        ? "Tutti"
        : datiModel.getRisPerPag().toString());
    /*this.risPerPag = (datiModel.getRisPerPag() == null
        ? new Integer(0)
        : datiModel.getRisPerPag());*/
    this.visModelli = (datiModel.getVisModelli() == 1 ? true : false);
    this.entPrinc = datiModel.getEntPrinc();
    this.codApp = datiModel.getCodApp();
    this.owner = datiModel.getOwner();
    this.famiglia = datiModel.getFamiglia();
    this.idProspetto = datiModel.getIdProspetto();
    this.personale = (datiModel.getPersonale() == 1 ? true : false);
    this.filtroUtente = (datiModel.getFiltroUtente() == 1 ? true : false);
    this.profiloOwner = datiModel.getProfiloOwner();

    if (datiModel.getVisParametri() != null) {
      this.visParametri = (datiModel.getVisParametri() == 1 ? true : false);
    }
    if (datiModel.getLinkScheda() != null) {
      this.linkScheda = (datiModel.getLinkScheda() == 1 ? true : false);
    }

    this.codReportWS = datiModel.getCodReportWS();
    this.filtroUfficioIntestatario = (datiModel.getFiltroUfficioIntestatario() == 1 ? true : false);
    this.defSql = datiModel.getDefSql();
    this.versione = datiModel.getVersione();
  }

  public DatiGenRicerca getDatiPerModel() {
    DatiGenRicerca datiGenerali = new DatiGenRicerca();

    datiGenerali.setIdRicerca(UtilityNumeri.convertiIntero(this.id));
    datiGenerali.setTipo(UtilityNumeri.convertiIntero(this.tipoRicerca));
    datiGenerali.setNome(this.nome);
    datiGenerali.setDescrizione(UtilityStringhe.convertiStringaVuotaInNull(this.descrizione));
    datiGenerali.setValDistinti(this.valDistinti ? 1 : 0);
    datiGenerali.setDisp(this.disp ? 1 : 0);
    datiGenerali.setRisPerPag(this.risPerPag.equals("Tutti")
        ? null
        : new Integer(this.risPerPag));
    datiGenerali.setVisModelli(this.visModelli ? 1 : 0);
    datiGenerali.setEntPrinc(UtilityStringhe.convertiStringaVuotaInNull(this.entPrinc));
    datiGenerali.setCodApp(UtilityStringhe.convertiStringaVuotaInNull(this.codApp));
    datiGenerali.setOwner(this.owner);
    datiGenerali.setFamiglia(this.famiglia);
    datiGenerali.setIdProspetto(this.idProspetto);
    datiGenerali.setPersonale(this.personale ? 1 : 0);
    datiGenerali.setFiltroUtente(this.filtroUtente ? 1 : 0);
    datiGenerali.setProfiloOwner(this.profiloOwner);
    if (this.visParametri != null) {
      datiGenerali.setVisParametri(this.visParametri ? 1 : 0);
    }
    if (this.linkScheda != null) {
      datiGenerali.setLinkScheda(this.linkScheda ? 1 : 0);
    }
    datiGenerali.setCodReportWS(UtilityStringhe.convertiStringaVuotaInNull(this.codReportWS));
    datiGenerali.setFiltroUfficioIntestatario(this.filtroUfficioIntestatario ? 1 : 0);
    datiGenerali.setDefSql(this.defSql);
    datiGenerali.setVersione(this.getVersione());
    return datiGenerali;
  }

  /**
   * @return Returns the password.
   */
  public String getDescrizione() {
    return descrizione;
  }

  /**
   * @param password
   *        The password to set.
   */
  public void setDescrizione(String password) {
    this.descrizione = password;
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
  public String getId() {
    return this.id;
  }

  /**
   * @param id
   *        The id to set.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return Returns the valDistinti.
   */
  public boolean getValDistinti() {

    return valDistinti;
  }

  /**
   * @param valDistinti
   *        The valDistinti to set.
   */
  public void setValDistinti(boolean valDistinti) {

    this.valDistinti = valDistinti;
  }

  /**
   * @return Ritorna disp.
   */
  public boolean getDisp() {
    return disp;
  }

  /**
   * @param disp
   *        disp da settare internamente alla classe.
   */
  public void setDisp(boolean disp) {
    this.disp = disp;
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
   * @return Ritorna risPerPag.
   */
  public String getRisPerPag() {
    return risPerPag;
  }

  /**
   * @param risPerPag
   *        risPerPag da settare internamente alla classe.
   */
  public void setRisPerPag(String risPerPag) {
    this.risPerPag = risPerPag;
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
   * @return Ritorna visModelli.
   */
  public boolean getVisModelli() {
    return visModelli;
  }

  /**
   * @param visModelli
   *        visModelli da settare internamente alla classe.
   */
  public void setVisModelli(boolean visModelli) {
    this.visModelli = visModelli;
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
   * @return Ritorna filtroIdUtente.
   */
  public boolean getFiltroUtente() {
    return filtroUtente;
  }

  /**
   * @param filtroIdUtente filtroIdUtente da settare internamente alla classe.
   */
  public void setFiltroUtente(boolean filtroIdUtente) {
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
  public Boolean getVisParametri() {
    return visParametri;
  }

  /**
   * @param visParametri visParametri da settare internamente alla classe.
   */
  public void setVisParametri(Boolean visParametri) {
    this.visParametri = visParametri;
  }

  /**
   * @return Ritorna linkScheda.
   */
  public Boolean getLinkScheda() {
    return linkScheda;
  }

  /**
   * @param linkScheda linkScheda da settare internamente alla classe.
   */
  public void setLinkScheda(Boolean linkScheda) {
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
   * @return Ritorna filtroUfficioIntestatario.
   */
  public boolean getFiltroUfficioIntestatario() {
    return filtroUfficioIntestatario;
  }

  /**
   * @param filtroUfficioIntestatario filtroUfficioIntestatario da settare internamente alla classe.
   */
  public void setFiltroUfficioIntestatario(boolean filtroUfficioIntestatario) {
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
   * @return Ritorna versione
   */
  public Integer getVersione() {
    return versione;
  }

  /**
   * @param versione  da settare internamente alla classe.
   */
  public void setVersione(Integer versione) {
    this.versione = versione;
  }

}