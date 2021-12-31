/*
 * Created on 18-lug-2005
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.giunzione;

import it.eldasoft.gene.db.domain.genric.GiunzioneRicerca;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.utility.UtilityBool;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author Stefano.Sabbadin
 */
public class GiunzioneRicercaForm extends ActionForm {

  /** UID */
  private static final long  serialVersionUID = 8908262378177077771L;

  public static final String INNER_JOIN       = Short.toString(GiunzioneRicerca.INNER_JOIN);
  public static final String LEFT_OUTER_JOIN  = Short.toString(GiunzioneRicerca.LEFT_OUTER_JOIN);
  public static final String RIGHT_OUTER_JOIN = Short.toString(GiunzioneRicerca.RIGHT_OUTER_JOIN);

  private String             id;
  private String             progressivo;
  private boolean            giunzioneAttiva;
  private String             mnemonicoTabella1;
  private String             aliasTabella1;
  private String             descrizioneTabella1;
  private String             campiTabella1;
  private String             mnemonicoTabella2;
  private String             aliasTabella2;
  private String             descrizioneTabella2;
  private String             campiTabella2;
  private String             tipoGiunzione;

  public GiunzioneRicercaForm() {
    super();
    this.inizializzaOggetto();
    this.tipoGiunzione = GiunzioneRicercaForm.INNER_JOIN;
  }

  private void inizializzaOggetto() {
    this.id = null;
    this.progressivo = null;
    this.giunzioneAttiva = false;
    this.mnemonicoTabella1 = null;
    this.aliasTabella1 = null;
    this.descrizioneTabella1 = null;
    this.mnemonicoTabella2 = null;
    this.aliasTabella2 = null;
    this.descrizioneTabella2 = null;
    this.tipoGiunzione = null;
    this.campiTabella1 = null;
    this.campiTabella2 = null;
  }

  public GiunzioneRicercaForm(GiunzioneRicerca datiModel) {
    Tabella tabella1 = DizionarioTabelle.getInstance().get(
        datiModel.getMnemonicoTabella1());
    Tabella tabella2 = DizionarioTabelle.getInstance().get(
        datiModel.getMnemonicoTabella2());

    this.id = datiModel.getId().toString();
    this.progressivo = new Integer(datiModel.getProgressivo()).toString();
    //this.giunzioneAttiva = datiModel.isGiunzioneAttiva();
    this.giunzioneAttiva = UtilityBool.convertiBooleano(datiModel.getGiunzioneAttiva());
    this.mnemonicoTabella1 = datiModel.getMnemonicoTabella1();
    this.aliasTabella1 = datiModel.getAliasTabella1();
    this.descrizioneTabella1 = tabella1.getDescrizione();
    this.campiTabella1 = datiModel.getCampiTabella1();
    this.mnemonicoTabella2 = datiModel.getMnemonicoTabella2();
    this.aliasTabella2 = datiModel.getAliasTabella2();
    this.descrizioneTabella2 = tabella2.getDescrizione();
    this.campiTabella2 = datiModel.getCampiTabella2();
    this.tipoGiunzione = new Integer(datiModel.getTipoGiunzione()).toString();
  }

  public GiunzioneRicerca getDatiPerModel() {
    GiunzioneRicerca giunzione = new GiunzioneRicerca();

    giunzione.setId(UtilityNumeri.convertiIntero(this.id));
    giunzione.setProgressivo(Integer.parseInt(this.progressivo));
    giunzione.setGiunzioneAttiva(UtilityBool.convertiBooleano(this.giunzioneAttiva));
    giunzione.setMnemonicoTabella1(UtilityStringhe.convertiStringaVuotaInNull(this.mnemonicoTabella1));
    giunzione.setAliasTabella1(UtilityStringhe.convertiStringaVuotaInNull(this.aliasTabella1));
    giunzione.setCampiTabella1(UtilityStringhe.convertiStringaVuotaInNull(this.campiTabella1));
    giunzione.setMnemonicoTabella2(UtilityStringhe.convertiStringaVuotaInNull(this.mnemonicoTabella2));
    giunzione.setAliasTabella2(UtilityStringhe.convertiStringaVuotaInNull(this.aliasTabella2));
    giunzione.setCampiTabella2(UtilityStringhe.convertiStringaVuotaInNull(this.campiTabella2));
    giunzione.setTipoGiunzione(Short.parseShort(this.tipoGiunzione));

    return giunzione;
  }

  /**
   * @return Returns the aliasTabella.
   */
  public String getAliasTabella1() {

    return aliasTabella1;
  }

  /**
   * @param aliasTabella
   *        The aliasTabella to set.
   */
  public void setAliasTabella1(String aliasTabella) {

    this.aliasTabella1 = aliasTabella;
  }

  /**
   * @return Returns the descrizioneTabella.
   */
  public String getDescrizioneTabella1() {

    return descrizioneTabella1;
  }

  /**
   * @param descrizioneTabella
   *        The descrizioneTabella to set.
   */
  public void setDescrizioneTabella1(String descrizioneTabella) {

    this.descrizioneTabella1 = descrizioneTabella;
  }

  /**
   * @return Returns the funzione.
   */
  public String getTipoGiunzione() {

    return tipoGiunzione;
  }

  /**
   * @param funzione
   *        The funzione to set.
   */
  public void setTipoGiunzione(String funzione) {

    this.tipoGiunzione = funzione;
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
   * @return Returns the mnemonicoTabella.
   */
  public String getMnemonicoTabella1() {

    return mnemonicoTabella1;
  }

  /**
   * @param mnemonicoTabella
   *        The mnemonicoTabella to set.
   */
  public void setMnemonicoTabella1(String mnemonicoTabella) {

    this.mnemonicoTabella1 = mnemonicoTabella;
  }

  /**
   * @return Returns the progressivo.
   */
  public String getProgressivo() {

    return progressivo;
  }

  /**
   * @param progressivo
   *        The progressivo to set.
   */
  public void setProgressivo(String progressivo) {

    this.progressivo = progressivo;
  }

  /**
   * @return Returns the aliasTabella2.
   */
  public String getAliasTabella2() {

    return aliasTabella2;
  }

  /**
   * @param aliasTabella2
   *        The aliasTabella2 to set.
   */
  public void setAliasTabella2(String aliasTabella2) {

    this.aliasTabella2 = aliasTabella2;
  }

  /**
   * @return Returns the descrizioneTabella2.
   */
  public String getDescrizioneTabella2() {

    return descrizioneTabella2;
  }

  /**
   * @param descrizioneTabella2
   *        The descrizioneTabella2 to set.
   */
  public void setDescrizioneTabella2(String descrizioneTabella2) {

    this.descrizioneTabella2 = descrizioneTabella2;
  }

  /**
   * @return Returns the mnemonicoTabella2.
   */
  public String getMnemonicoTabella2() {

    return mnemonicoTabella2;
  }

  /**
   * @param mnemonicoTabella2
   *        The mnemonicoTabella2 to set.
   */
  public void setMnemonicoTabella2(String mnemonicoTabella2) {

    this.mnemonicoTabella2 = mnemonicoTabella2;
  }

  /**
   * @return Returns the giunzioneAttiva.
   */
  public boolean getGiunzioneAttiva() {

    return giunzioneAttiva;
  }

  /**
   * @param giunzioneAttiva
   *        The giunzioneAttiva to set.
   */
  public void setGiunzioneAttiva(boolean giunzioneAttiva) {

    this.giunzioneAttiva = giunzioneAttiva;
  }

  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.inizializzaOggetto();
  }

  /**
   * @return Ritorna campiTabella1.
   */
  public String getCampiTabella1() {
    return campiTabella1;
  }

  /**
   * @param campiTabella1
   *        campiTabella1 da settare internamente alla classe.
   */
  public void setCampiTabella1(String campiTabella1) {
    this.campiTabella1 = campiTabella1;
  }

  /**
   * @return Ritorna campiTabella2.
   */
  public String getCampiTabella2() {
    return campiTabella2;
  }

  /**
   * @param campiTabella2
   *        campiTabella2 da settare internamente alla classe.
   */
  public void setCampiTabella2(String campiTabella2) {
    this.campiTabella2 = campiTabella2;
  }
}