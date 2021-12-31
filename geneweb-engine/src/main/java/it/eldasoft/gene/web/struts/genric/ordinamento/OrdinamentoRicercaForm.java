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
package it.eldasoft.gene.web.struts.genric.ordinamento;

import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.gene.db.domain.genric.OrdinamentoRicerca;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author Stefano.Sabbadin
 */
public class OrdinamentoRicercaForm extends ActionForm {

  /** UID */
  private static final long serialVersionUID        = 8069622857127898487L;

  public static final short ORDINAMENTO_ASCENDENTE  = OrdinamentoRicerca.ORDINAMENTO_ASCENDENTE;
  public static final short ORDINAMENTO_DISCENDENTE = OrdinamentoRicerca.ORDINAMENTO_DISCENDENTE;

  private String            id;
  private String            progressivo;
  private String            mnemonicoTabella;
  private String            aliasTabella;
  private String            descrizioneTabella;
  private String            mnemonicoCampo;
  private String            descrizioneCampo;
  private String            ordinamento;

  public OrdinamentoRicercaForm() {
    super();
    this.inizializzaOggetto();
    this.ordinamento = "" + OrdinamentoRicercaForm.ORDINAMENTO_ASCENDENTE;
  }

  private void inizializzaOggetto() {
    this.id = null;
    this.progressivo = null;
    this.mnemonicoTabella = null;
    this.aliasTabella = null;
    this.descrizioneTabella = null;
    this.mnemonicoCampo = null;
    this.descrizioneCampo = null;
    this.ordinamento = "" + OrdinamentoRicercaForm.ORDINAMENTO_ASCENDENTE;
  }  
  public OrdinamentoRicercaForm(OrdinamentoRicerca datiModel) {
    Tabella tabella = DizionarioTabelle.getInstance().get(
        datiModel.getMnemonicoTabella());
    Campo campo = DizionarioCampi.getInstance().get(
        datiModel.getMnemonicoCampo());
    this.id = datiModel.getId().toString();
    this.progressivo = "" + datiModel.getProgressivo();
    this.mnemonicoTabella = datiModel.getMnemonicoTabella();
    this.aliasTabella = datiModel.getAliasTabella();
    this.descrizioneTabella = tabella.getDescrizione();
    this.mnemonicoCampo = datiModel.getMnemonicoCampo();
    this.descrizioneCampo = campo.getDescrizione();
    this.ordinamento = "" + datiModel.getOrdinamento();
  }

  public OrdinamentoRicerca getDatiPerModel() {
    OrdinamentoRicerca ordinamento = new OrdinamentoRicerca();

    ordinamento.setId(UtilityNumeri.convertiIntero(this.id));
    ordinamento.setProgressivo(Integer.parseInt(this.progressivo));
    ordinamento.setMnemonicoTabella(UtilityStringhe.convertiStringaVuotaInNull(this.mnemonicoTabella));
    ordinamento.setAliasTabella(UtilityStringhe.convertiStringaVuotaInNull(this.aliasTabella));
    ordinamento.setMnemonicoCampo(UtilityStringhe.convertiStringaVuotaInNull(this.mnemonicoCampo));
    // if (this.ordinamento != null)
    ordinamento.setOrdinamento(Integer.parseInt(this.ordinamento));

    return ordinamento;
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
   * @return Returns the descrizioneCampo.
   */
  public String getDescrizioneCampo() {

    return descrizioneCampo;
  }

  /**
   * @param descrizioneCampo
   *        The descrizioneCampo to set.
   */
  public void setDescrizioneCampo(String descrizioneCampo) {

    this.descrizioneCampo = descrizioneCampo;
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
   * @return Returns the funzione.
   */
  public String getOrdinamento() {

    return ordinamento;
  }

  /**
   * @param ordinamento
   *        The ordinamento to set.
   */
  public void setOrdinamento(String ordinamento) {

    this.ordinamento = ordinamento;
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
   * @return Returns the mnemonicoCampo.
   */
  public String getMnemonicoCampo() {

    return mnemonicoCampo;
  }

  /**
   * @param mnemonicoCampo
   *        The mnemonicoCampo to set.
   */
  public void setMnemonicoCampo(String mnemonicoCampo) {

    this.mnemonicoCampo = mnemonicoCampo;
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

  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.inizializzaOggetto();
  }


}