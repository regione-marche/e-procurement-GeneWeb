/*
 * Created on 22-ago-2006
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.filtro;

import it.eldasoft.gene.db.domain.genric.FiltroRicerca;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.sql.comp.SqlElementoCondizione;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * @author Stefano.Sabbadin
 */
public class FiltroRicercaForm extends ActionForm {

  /** UID */
  private static final long  serialVersionUID                    = 7848370902479381299L;

  public static final short  OPERATORE_PARENTESI_APERTA          = FiltroRicerca.OPERATORE_PARENTESI_APERTA;
  public static final short  OPERATORE_PARENTESI_CHIUSA          = FiltroRicerca.OPERATORE_PARENTESI_CHIUSA;

  public static final short  OPERATORE_LOGICO_AND                = FiltroRicerca.OPERATORE_LOGICO_AND;
  public static final short  OPERATORE_LOGICO_OR                 = FiltroRicerca.OPERATORE_LOGICO_OR;
  public static final short  OPERATORE_LOGICO_NOT                = FiltroRicerca.OPERATORE_LOGICO_NOT;

  public static final short  OPERATORE_CONFRONTO_UGUALE          = FiltroRicerca.OPERATORE_CONFRONTO_UGUALE;
  public static final short  OPERATORE_CONFRONTO_DIVERSO         = FiltroRicerca.OPERATORE_CONFRONTO_DIVERSO;
  public static final short  OPERATORE_CONFRONTO_MAGGIORE        = FiltroRicerca.OPERATORE_CONFRONTO_MAGGIORE;
  public static final short  OPERATORE_CONFRONTO_MAGGIORE_UGUALE = FiltroRicerca.OPERATORE_CONFRONTO_MAGGIORE_UGUALE;
  public static final short  OPERATORE_CONFRONTO_MINORE          = FiltroRicerca.OPERATORE_CONFRONTO_MINORE;
  public static final short  OPERATORE_CONFRONTO_MINORE_UGUALE   = FiltroRicerca.OPERATORE_CONFRONTO_MINORE_UGUALE;
  public static final short  OPERATORE_CONFRONTO_IN              = FiltroRicerca.OPERATORE_CONFRONTO_IN;
  
  public static final short  OPERATORE_CONFRONTO_NULL            = FiltroRicerca.OPERATORE_CONFRONTO_NULL;
  public static final short  OPERATORE_CONFRONTO_NOT_NULL        = FiltroRicerca.OPERATORE_CONFRONTO_NOT_NULL;
  public static final short  OPERATORE_CONFRONTO_MATCH           = FiltroRicerca.OPERATORE_CONFRONTO_MATCH;
  public static final short  OPERATORE_CONFRONTO_NOT_MATCH       = FiltroRicerca.OPERATORE_CONFRONTO_NOT_MATCH;

  public static final String TIPO_CONFRONTO_CAMPO                = Integer.toString(FiltroRicerca.TIPO_CONFRONTO_CAMPO);
  public static final String TIPO_CONFRONTO_VALORE               = Integer.toString(FiltroRicerca.TIPO_CONFRONTO_VALORE);
  public static final String TIPO_CONFRONTO_PARAMETRO            = Integer.toString(FiltroRicerca.TIPO_CONFRONTO_PARAMETRO);
  public static final String TIPO_CONFRONTO_DATA_ODIERNA         = Integer.toString(FiltroRicerca.TIPO_CONFRONTO_DATA_ODIERNA);
  public static final String TIPO_CONFRONTO_UTENTE_CONNESSO      = Integer.toString(FiltroRicerca.TIPO_CONFRONTO_UTENTE_CONNESSO);
  public static final String TIPO_CONFRONTO_UFFICIO_INTESTATARIO = Integer.toString(FiltroRicerca.TIPO_CONFRONTO_UFFICIO_INTESTATARIO);
  
  private String             id;
  private String             progressivo;
  private String             operatore;
  private String             mnemonicoTabella;
  private String             aliasTabella;
  private String             descrizioneTabella;
  private String             mnemonicoCampo;
  private String             descrizioneCampo;
  private String             tipoConfronto;
  private String             mnemonicoTabellaConfronto;
  private String             aliasTabellaConfronto;
  private String             descrizioneTabellaConfronto;
  private String             mnemonicoCampoConfronto;
  private String             descrizioneCampoConfronto;
  private String             valoreConfronto;
  private String             parametroConfronto;
  private boolean            notCaseSensitive;

  public FiltroRicercaForm() {
    super();
    this.inizializzaOggetto();
  }

  public FiltroRicercaForm(FiltroRicerca datiModel) {
    Tabella tabella = null;
    if (datiModel.getMnemonicoTabella() != null)
      tabella = DizionarioTabelle.getInstance().get(
          datiModel.getMnemonicoTabella());

    Campo campo = null;
    if (datiModel.getMnemonicoCampo() != null)
      campo = DizionarioCampi.getInstance().get(datiModel.getMnemonicoCampo());

    Tabella tabella2 = null;
    if (datiModel.getMnemonicoTabellaConfronto() != null)
      tabella2 = DizionarioTabelle.getInstance().get(
          datiModel.getMnemonicoTabellaConfronto());

    Campo campo2 = null;
    if (datiModel.getMnemonicoCampoConfronto() != null)
      campo2 = DizionarioCampi.getInstance().get(
          datiModel.getMnemonicoCampoConfronto());

    this.id = datiModel.getId().toString();
    this.progressivo = new Integer(datiModel.getProgressivo()).toString();
    this.operatore = datiModel.getOperatore();
    this.mnemonicoTabella = datiModel.getMnemonicoTabella();
    this.aliasTabella = datiModel.getAliasTabella();
    this.mnemonicoCampo = datiModel.getMnemonicoCampo();

    if (tabella != null) {
      this.descrizioneTabella = tabella.getDescrizione();
    }
    if (campo != null) {
      this.descrizioneCampo = campo.getDescrizione();
    }

    if (datiModel.getTipoConfronto() != null) {
      this.tipoConfronto = datiModel.getTipoConfronto().toString();
      //switch (Short.parseShort(datiModel.getTipoConfronto())) {
      switch (datiModel.getTipoConfronto().shortValue()) {
      case FiltroRicerca.TIPO_CONFRONTO_CAMPO:
        this.mnemonicoTabellaConfronto = datiModel.getMnemonicoTabellaConfronto();
        this.aliasTabellaConfronto = datiModel.getAliasTabellaConfronto();
        this.mnemonicoCampoConfronto = datiModel.getMnemonicoCampoConfronto();
        if (tabella2 != null) {
          this.descrizioneTabellaConfronto = tabella2.getDescrizione();
        }
        if (campo2 != null) {
          this.descrizioneCampoConfronto = campo2.getDescrizione();
        }
        break;
      case FiltroRicerca.TIPO_CONFRONTO_VALORE:
        this.valoreConfronto = datiModel.getValoreConfronto();
        break;
      case FiltroRicerca.TIPO_CONFRONTO_PARAMETRO:
        this.parametroConfronto = datiModel.getParametroConfronto();
      case FiltroRicerca.TIPO_CONFRONTO_DATA_ODIERNA:
        //L.G. 01/02/2007: nuovo tipo di confronto: per data odierna
        this.valoreConfronto = datiModel.getValoreConfronto();
      }
    }
    
    this.notCaseSensitive = (new Integer(1).equals(datiModel.getNotCaseSensitive())
        ? true
        : false);
  }

  /**
   * Determina la descrizione dell'operatore a partire dal suo codice
   * 
   * @return descrizione dell'operatore a partire dal campo operatore
   */
  private String determinaDescrizione() {
    String descrizione = null;

    // le parentesi le riporto così come stanno, gli altri operatori invece li
    // traduco in modo descrittivo
    if (SqlElementoCondizione.STR_OPERATORE_PARENTESI_APERTA.equals(this.operatore)
        || SqlElementoCondizione.STR_OPERATORE_PARENTESI_CHIUSA.equals(this.operatore))
      descrizione = this.operatore;
    else {
      for (int i = 0; i < CostantiGenRicerche.CBX_OPERATORI_VALUE.length; i++)
        if (CostantiGenRicerche.CBX_OPERATORI_VALUE[i].equals(this.operatore))
          descrizione = CostantiGenRicerche.DESCRIZIONE_OPERATORI[i];
    }

    return descrizione;
  }

  public FiltroRicerca getDatiPerModel() {
    FiltroRicerca filtro = new FiltroRicerca();

    filtro.setId(UtilityNumeri.convertiIntero(this.id));
    filtro.setProgressivo(Integer.parseInt(this.progressivo));
    filtro.setOperatore(UtilityStringhe.convertiStringaVuotaInNull(this.operatore));
    filtro.setMnemonicoTabella(UtilityStringhe.convertiStringaVuotaInNull(this.mnemonicoTabella));
    filtro.setAliasTabella(UtilityStringhe.convertiStringaVuotaInNull(this.aliasTabella));
    filtro.setMnemonicoCampo(UtilityStringhe.convertiStringaVuotaInNull(mnemonicoCampo));
    filtro.setMnemonicoTabellaConfronto(UtilityStringhe.convertiStringaVuotaInNull(this.mnemonicoTabellaConfronto));
    filtro.setAliasTabellaConfronto(UtilityStringhe.convertiStringaVuotaInNull(this.aliasTabellaConfronto));
    filtro.setMnemonicoCampoConfronto(UtilityStringhe.convertiStringaVuotaInNull(this.mnemonicoCampoConfronto));
    filtro.setValoreConfronto(UtilityStringhe.convertiStringaVuotaInNull(this.valoreConfronto));
    filtro.setTipoConfronto(UtilityNumeri.convertiIntero(this.tipoConfronto));
    //filtro.setTipoConfronto(this.tipoConfronto);
    filtro.setParametroConfronto(UtilityStringhe.convertiStringaVuotaInNull(this.parametroConfronto));
    
    // data la checkbox nella pagina per il casesensitive, pongo null nel caso
    // di operatori logici, parentesi o is/is not null, 0 o 1 in base al check
    // nel caso di operatori di confronto
    if (SqlElementoCondizione.STR_OPERATORE_CONFRONTO_UGUALE.equals(this.operatore)
        || SqlElementoCondizione.STR_OPERATORE_CONFRONTO_DIVERSO.equals(this.operatore)
        || SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MAGGIORE.equals(this.operatore)
        || SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MAGGIORE_UGUALE.equals(this.operatore)
        || SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MINORE.equals(this.operatore)
        || SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MINORE_UGUALE.equals(this.operatore)
        || SqlElementoCondizione.STR_OPERATORE_CONFRONTO_IN.equals(this.operatore)
        || SqlElementoCondizione.STR_OPERATORE_CONFRONTO_NOT_IN.equals(this.operatore)
        || SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MATCH.equals(this.operatore)
        || SqlElementoCondizione.STR_OPERATORE_CONFRONTO_NOT_MATCH.equals(this.operatore)) {
      filtro.setNotCaseSensitive(this.notCaseSensitive
          ? new Integer(1)
          : new Integer(0));
    } else {
      filtro.setNotCaseSensitive(null);
    }

    return filtro;
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
  public String getTipoConfronto() {

    return tipoConfronto;
  }

  /**
   * @param funzione
   *        The funzione to set.
   */
  public void setTipoConfronto(String funzione) {

    this.tipoConfronto = funzione;
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

  /**
   * @return Returns the aliasTabella2.
   */
  public String getAliasTabellaConfronto() {

    return aliasTabellaConfronto;
  }

  /**
   * @param aliasTabella2
   *        The aliasTabella2 to set.
   */
  public void setAliasTabellaConfronto(String aliasTabella2) {

    this.aliasTabellaConfronto = aliasTabella2;
  }

  /**
   * @return Returns the descrizioneCampo2.
   */
  public String getDescrizioneCampoConfronto() {

    return descrizioneCampoConfronto;
  }

  /**
   * @param descrizioneCampo2
   *        The descrizioneCampo2 to set.
   */
  public void setDescrizioneCampoConfronto(String descrizioneCampo2) {

    this.descrizioneCampoConfronto = descrizioneCampo2;
  }

  /**
   * @return Returns the descrizioneTabella2.
   */
  public String getDescrizioneTabellaConfronto() {

    return descrizioneTabellaConfronto;
  }

  /**
   * @param descrizioneTabella2
   *        The descrizioneTabella2 to set.
   */
  public void setDescrizioneTabellaConfronto(String descrizioneTabella2) {

    this.descrizioneTabellaConfronto = descrizioneTabella2;
  }

  /**
   * @return Returns the mnemonicoCampoConfronto.
   */
  public String getMnemonicoCampoConfronto() {

    return mnemonicoCampoConfronto;
  }

  /**
   * @param mnemonicoCampo2
   *        The mnemonicoCampoConfronto to set.
   */
  public void setMnemonicoCampoConfronto(String mnemonicoCampo2) {

    this.mnemonicoCampoConfronto = mnemonicoCampo2;
  }

  /**
   * @return Returns the mnemonicoTabellaConfronto.
   */
  public String getMnemonicoTabellaConfronto() {

    return mnemonicoTabellaConfronto;
  }

  /**
   * @param mnemonicoTabella2
   *        The mnemonicoTabellaConfronto to set.
   */
  public void setMnemonicoTabellaConfronto(String mnemonicoTabella2) {

    this.mnemonicoTabellaConfronto = mnemonicoTabella2;
  }

  /**
   * @return Returns the operatore.
   */
  public String getOperatore() {

    return operatore;
  }

  /**
   * @param operatore
   *        The operatore to set.
   */
  public void setOperatore(String operatore) {

    this.operatore = operatore;
  }

  /**
   * @return Ritorna descrizioneOperatore.
   */
  public String getDescrizioneOperatore() {
    return this.determinaDescrizione();
  }

  /**
   * @return Returns the valoreConfronto.
   */
  public String getValoreConfronto() {

    return valoreConfronto;
  }

  /**
   * @param valoreConfronto
   *        The valoreConfronto to set.
   */
  public void setValoreConfronto(String valoreConfronto) {

    this.valoreConfronto = valoreConfronto;
  }

  /**
   * @return Ritorna parametroConfronto.
   */
  public String getParametroConfronto() {
    return parametroConfronto;
  }

  /**
   * @param parametroConfronto
   *        parametroConfronto da settare internamente alla classe.
   */
  public void setParametroConfronto(String parametroConfronto) {
    this.parametroConfronto = parametroConfronto;
  }
  
  /**
   * @return Ritorna notCaseSensitive.
   */
  public boolean isNotCaseSensitive() {
    return notCaseSensitive;
  }

  /**
   * @param notCaseSensitive notCaseSensitive da settare internamente alla classe.
   */
  public void setNotCaseSensitive(boolean notCaseSensitive) {
    this.notCaseSensitive = notCaseSensitive;
  }

  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.inizializzaOggetto();
  }

  /**
   * 
   */
  private void inizializzaOggetto() {
    this.id = null;
    this.progressivo = null;
    this.operatore = null;
    this.mnemonicoTabella = null;
    this.aliasTabella = null;
    this.descrizioneTabella = null;
    this.mnemonicoCampo = null;
    this.descrizioneCampo = null;
    this.mnemonicoTabellaConfronto = null;
    this.aliasTabellaConfronto = null;
    this.descrizioneTabellaConfronto = null;
    this.mnemonicoCampoConfronto = null;
    this.descrizioneCampoConfronto = null;
    this.valoreConfronto = null;
    this.tipoConfronto = null;
    this.parametroConfronto = null;
    this.notCaseSensitive = false;
  }
}