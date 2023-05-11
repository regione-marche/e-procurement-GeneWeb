/*
 * Created on 21-ago-2006
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.argomenti;

import javax.servlet.http.HttpServletRequest;

import it.eldasoft.gene.db.domain.genric.TabellaRicerca;
import it.eldasoft.utils.metadata.cache.DizionarioSchemi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Schema;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author Luca.Giacomazzo
 */
public class TabellaRicercaForm extends ActionForm {

  /** UID */
  private static final long serialVersionUID = -8805061852759030146L;

  private String            id;
  private int               progressivo;
  private String            mnemonicoSchema;
  private String            descrizioneSchema;
  private String            mnemonicoTabella;
  private String            nomeTabella;
  private String            aliasTabella;
  private String            descrizioneTabella;
  private boolean           visibile;

  public TabellaRicercaForm() {
    super();
    this.inizializzaOggetto();
  }

  private void inizializzaOggetto() {
    this.id = null;
    this.progressivo = 0;
    this.mnemonicoSchema = null;
    this.descrizioneSchema = null;
    this.mnemonicoTabella = null;
    this.nomeTabella = null;
    this.aliasTabella = null;
    this.descrizioneTabella = null;
    this.visibile = false;
  }

  public TabellaRicercaForm(TabellaRicerca datiModel) {
    Tabella tabella = DizionarioTabelle.getInstance().get(
        datiModel.getMneTabella());
    Schema schema = DizionarioSchemi.getInstance().get(tabella.getNomeSchema());
    this.id = datiModel.getId().toString();
    this.progressivo = datiModel.getProgressivo();
    this.mnemonicoSchema = tabella.getNomeSchema();
    this.descrizioneSchema = schema.getDescrizione();
    this.mnemonicoTabella = datiModel.getMneTabella();
    this.nomeTabella = tabella.getNomeTabella();
    this.aliasTabella = datiModel.getAliasTabella();
    this.descrizioneTabella = tabella.getDescrizione();
    this.visibile = (datiModel.getVisibile() == 1 ? true : false);
  }

  public TabellaRicerca getDatiPerModel() {
    TabellaRicerca tabella = new TabellaRicerca();

    tabella.setId(UtilityNumeri.convertiIntero(this.id));
    tabella.setProgressivo(this.progressivo);
    tabella.setMneTabella(this.mnemonicoTabella);
    tabella.setAliasTabella(UtilityStringhe.convertiStringaVuotaInNull(this.aliasTabella));
    tabella.setVisibile((this.visibile ? 1 : 0));

    return tabella;
  }

  /**
   * @return Ritorna l'alias (o il mnemonico, se l'alias non è valorizzato);
   *         viene utilizzato nelle combobox di selezione tabelle.
   */
  public String getValuePerSelect() {
    return this.aliasTabella;
  }

  /**
   * @return Ritorna l'alias (o il mnemonico, se l'alias non è valorizzato)
   *         concatenato alla descrizione; viene utilizzato nelle combobox di
   *         selezione tabelle.
   */
  public String getTextPerSelect() {
    return this.aliasTabella + " - " + this.descrizioneTabella;
  }

  /**
   * @return Returns the aliasTabella.
   */
  public String getAliasTabella() {

    return aliasTabella;
  }

  /**
   * @param aliasTabella
   *        The aliasTabella to set.
   */
  public void setAliasTabella(String aliasTabella) {

    this.aliasTabella = aliasTabella;
  }

  /**
   * @return Returns the descrizioneSchema.
   */
  public String getDescrizioneSchema() {

    return descrizioneSchema;
  }

  /**
   * @param descrizioneSchema
   *        The descrizioneSchema to set.
   */
  public void setDescrizioneSchema(String descrizioneSchema) {

    this.descrizioneSchema = descrizioneSchema;
  }

  /**
   * @return Returns the descrizioneTabella.
   */
  public String getDescrizioneTabella() {

    return descrizioneTabella;
  }

  /**
   * @param descrizioneTabella
   *        The descrizioneTabella to set.
   */
  public void setDescrizioneTabella(String descrizioneTabella) {

    this.descrizioneTabella = descrizioneTabella;
  }

  /**
   * @return Returns the id.
   */
  public String getId() {

    return id;
  }

  /**
   * @param id
   *        The id to set.
   */
  public void setId(String id) {

    this.id = id;
  }

  /**
   * @return Returns the mnemonicoSchema.
   */
  public String getMnemonicoSchema() {

    return mnemonicoSchema;
  }

  /**
   * @param mnemonicoSchema
   *        The mnemonicoSchema to set.
   */
  public void setMnemonicoSchema(String mnemonicoSchema) {

    this.mnemonicoSchema = mnemonicoSchema;
  }

  /**
   * @return Returns the mnemonicoTabella.
   */
  public String getMnemonicoTabella() {

    return mnemonicoTabella;
  }

  /**
   * @param mnemonicoTabella
   *        The mnemonicoTabella to set.
   */
  public void setMnemonicoTabella(String mnemonicoTabella) {

    this.mnemonicoTabella = mnemonicoTabella;
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
   * @return Returns the nomeTabella.
   */
  public String getNomeTabella() {

    return nomeTabella;
  }

  /**
   * @param nomeTabella
   *        The nomeTabella to set.
   */
  public void setNomeTabella(String nomeTabella) {

    this.nomeTabella = nomeTabella;
  }

  public String getNomeTabellaUnivoco() {
    if (this.aliasTabella != null && !(this.aliasTabella.trim().equals("")))
      return this.aliasTabella;
    return this.nomeTabella;
  }

  /**
   * @return Ritorna visibile.
   */
  public boolean getVisibile() {
    return visibile;
  }

  /**
   * @param visibile
   *        visibile da settare internamente alla classe.
   */
  public void setVisibile(boolean visibile) {
    this.visibile = visibile;
  }

  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.inizializzaOggetto();
  }

}