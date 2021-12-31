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
package it.eldasoft.gene.web.struts.genric.campo;

import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;
import it.eldasoft.gene.commons.web.struts.DispatchForm;
import it.eldasoft.gene.db.domain.genric.CampoRicerca;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

/**
 * Form per la parte View; contiene un campo da estrarre con la ricerca
 * 
 * @author Stefano.Sabbadin
 */
public class CampoRicercaForm extends DispatchForm {

  /** UID */
  private static final long serialVersionUID = -7175548310272946253L;

  private String            id;
  private String            progressivo;
  private String            mnemonicoTabella;
  private String            aliasTabella;
  private String            descrizioneTabella;
  private String            mnemonicoCampo;
  private String            operatoreMnemonicoCampo;
  private String            descrizioneCampo;
  private String            operatoreDescrizioneCampo;
  private String            funzione;
  private String            titoloColonna;

  public CampoRicercaForm() {
    super();
    this.inizializzaOggetto();
  }

  private void inizializzaOggetto() {
    this.id = null;
    this.progressivo = null;
    this.mnemonicoTabella = null;
    this.aliasTabella = null;
    this.descrizioneTabella = null;
    this.mnemonicoCampo = null;
    this.operatoreMnemonicoCampo = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.descrizioneCampo = null;
    this.operatoreDescrizioneCampo = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[1];
    this.funzione = null;
    this.titoloColonna = null;
  }

  public CampoRicercaForm(CampoRicerca datiModel) {
    Tabella tabella = DizionarioTabelle.getInstance().get(
        datiModel.getMneTabella());
    Campo campo = DizionarioCampi.getInstance().get(datiModel.getMneCampo());
    this.id = datiModel.getId().toString();
    this.progressivo = Integer.toString(datiModel.getProgressivo());
    this.mnemonicoTabella = datiModel.getMneTabella();
    this.aliasTabella = datiModel.getAliasTabella();
    this.descrizioneTabella = tabella.getDescrizione();
    this.mnemonicoCampo = datiModel.getMneCampo();
    // SS 02/10/2006: si utilizza la descrizione breve del campo
    this.descrizioneCampo = campo.getDescrizioneBreve();
    this.funzione = UtilityStringhe.convertiStringaVuotaInNull(datiModel.getFunzione());
    this.titoloColonna = UtilityStringhe.convertiStringaVuotaInNull(datiModel.getTitoloColonna());
  }

  /**
   * Ritorna l'oggetto per la business logic
   * 
   * @return dato per la business logic/interfacciamento db
   */
  public CampoRicerca getDatiPerModel() {
    CampoRicerca campo = new CampoRicerca();

    campo.setId(UtilityNumeri.convertiIntero(this.id));
    campo.setProgressivo(Integer.parseInt(this.progressivo));
    campo.setMneTabella(this.mnemonicoTabella);
    campo.setAliasTabella(this.aliasTabella);
    campo.setMneCampo(this.mnemonicoCampo);
    campo.setFunzione(this.funzione);
    campo.setTitoloColonna(this.titoloColonna);
    
    return campo;
  }

  /**
   * @return Ritorna il mnemonico; viene utilizzato nelle combobox di selezione
   *         campi.
   */
  public String getValuePerSelect() {
    return this.mnemonicoCampo;
  }

  /**
   * @return Ritorna il mnemonico concatenato alla descrizione; viene utilizzato
   *         nelle combobox di selezione campi.
   */
  public String getTextPerSelect() {
    return this.mnemonicoCampo + " - " + this.descrizioneCampo;
  }

  /**
   * @return Ritorna descrizioneTabella.
   */
  public String getDescrizioneTabella() {
    return descrizioneTabella;
  }

  /**
   * @param descrizioneTabella
   *        descrizioneTabella da settare internamente alla classe.
   */
  public void setDescrizioneTabella(String descrizioneTabella) {
    this.descrizioneTabella = descrizioneTabella;
  }

  /**
   * @return Ritorna mnemonicoTabella.
   */
  public String getMnemonicoTabella() {
    return mnemonicoTabella;
  }

  /**
   * @param mnemonicoTabella
   *        mnemonicoTabella da settare internamente alla classe.
   */
  public void setMnemonicoTabella(String mnemonicoTabella) {
    this.mnemonicoTabella = mnemonicoTabella;
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
   * @return Returns the funzione.
   */
  public String getFunzione() {

    return funzione;
  }

  /**
   * @param funzione
   *        The funzione to set.
   */
  public void setFunzione(String funzione) {

    this.funzione = funzione;
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
   * @return Ritorna titolo.
   */
  public String getTitoloColonna() {
    return titoloColonna;
  }
  
  /**
   * @param titolo titolo da settare internamente alla classe.
   */
  public void setTitoloColonna(String titolo) {
    this.titoloColonna = titolo;
  }

  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.inizializzaOggetto();
  }

  /**
   * @return Ritorna operatoreDescrizioneCampo.
   */
  public String getOperatoreDescrizioneCampo() {
    return operatoreDescrizioneCampo;
  }
  
  /**
   * @param operatoreDescrizioneCampo operatoreDescrizioneCampo da settare internamente alla classe.
   */
  public void setOperatoreDescrizioneCampo(String operatoreDescrizioneCampo) {
    this.operatoreDescrizioneCampo = operatoreDescrizioneCampo;
  }
  
  /**
   * @return Ritorna operatoreMnemonicoCampo.
   */
  public String getOperatoreMnemonicoCampo() {
    return operatoreMnemonicoCampo;
  }
  
  /**
   * @param operatoreMnemonicoCampo operatoreMnemonicoCampo da settare internamente alla classe.
   */
  public void setOperatoreMnemonicoCampo(String operatoreMnemonicoCampo) {
    this.operatoreMnemonicoCampo = operatoreMnemonicoCampo;
  }
  
}