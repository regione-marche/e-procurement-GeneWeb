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
package it.eldasoft.gene.db.domain.genric;

import it.eldasoft.utils.sql.comp.SqlElementoCondizione;

import java.io.Serializable;

/**
 * Rappresenta un elemento dell'elenco dei filtri da impostare nelle condizioni di 
 * ricerca, presenti nella tabella W_RICFILTRI
 * 
 * @author Luca.Giacomazzo
 */
public class FiltroRicerca implements Serializable {

  /**   UID   */
  private static final long serialVersionUID = -3422503289063390812L;
  
  public static final short OPERATORE_PARENTESI_APERTA = SqlElementoCondizione.OPERATORE_PARENTESI_APERTA;
  public static final short OPERATORE_PARENTESI_CHIUSA = SqlElementoCondizione.OPERATORE_PARENTESI_CHIUSA;

  public static final short OPERATORE_LOGICO_AND = SqlElementoCondizione.OPERATORE_LOGICO_AND;
  public static final short OPERATORE_LOGICO_OR = SqlElementoCondizione.OPERATORE_LOGICO_OR;
  public static final short OPERATORE_LOGICO_NOT = SqlElementoCondizione.OPERATORE_LOGICO_NOT;

  public static final short OPERATORE_CONFRONTO_UGUALE = SqlElementoCondizione.OPERATORE_CONFRONTO_UGUALE;
  public static final short OPERATORE_CONFRONTO_DIVERSO = SqlElementoCondizione.OPERATORE_CONFRONTO_DIVERSO;
  public static final short OPERATORE_CONFRONTO_MAGGIORE = SqlElementoCondizione.OPERATORE_CONFRONTO_MAGGIORE;
  public static final short OPERATORE_CONFRONTO_MAGGIORE_UGUALE = SqlElementoCondizione.OPERATORE_CONFRONTO_MAGGIORE_UGUALE;
  public static final short OPERATORE_CONFRONTO_MINORE = SqlElementoCondizione.OPERATORE_CONFRONTO_MINORE;
  public static final short OPERATORE_CONFRONTO_MINORE_UGUALE = SqlElementoCondizione.OPERATORE_CONFRONTO_MINORE_UGUALE;
  public static final short OPERATORE_CONFRONTO_IN = SqlElementoCondizione.OPERATORE_CONFRONTO_IN;
  
  public static final short OPERATORE_CONFRONTO_NULL = SqlElementoCondizione.OPERATORE_CONFRONTO_NULL;
  public static final short OPERATORE_CONFRONTO_NOT_NULL = SqlElementoCondizione.OPERATORE_CONFRONTO_NOT_NULL;
  public static final short OPERATORE_CONFRONTO_MATCH = SqlElementoCondizione.OPERATORE_CONFRONTO_MATCH;
  public static final short OPERATORE_CONFRONTO_NOT_MATCH = SqlElementoCondizione.OPERATORE_CONFRONTO_NOT_MATCH;

  public static final short TIPO_CONFRONTO_CAMPO = 0;
  public static final short TIPO_CONFRONTO_VALORE = 1;
  public static final short TIPO_CONFRONTO_PARAMETRO = 2;
  public static final short TIPO_CONFRONTO_DATA_ODIERNA = 3;
  public static final short TIPO_CONFRONTO_UTENTE_CONNESSO = 4;
  public static final short TIPO_CONFRONTO_UFFICIO_INTESTATARIO = 5;
  
  private Integer id;
  private int    progressivo;
  private String operatore;
  private String mnemonicoTabella;
  private String aliasTabella;
  private String mnemonicoCampo;
  private Integer  tipoConfronto;
  private String mnemonicoTabellaConfronto;
  private String aliasTabellaConfronto;
  private String mnemonicoCampoConfronto;
  private String valoreConfronto;
  private String parametroConfronto;
  private Integer notCaseSensitive;
  
  public FiltroRicerca() {
    this.id = null;
    this.progressivo = 0;
    this.operatore = null;
    this.mnemonicoTabella = null;
    this.aliasTabella = null;
    this.mnemonicoCampo = null;
    this.tipoConfronto = null;
    this.mnemonicoTabellaConfronto = null;
    this.aliasTabellaConfronto = null;
    this.mnemonicoCampoConfronto = null;
    this.valoreConfronto = null;
    this.parametroConfronto = null;
    this.notCaseSensitive = null;
  }
  
  /**
   * Costruttore per copia 
   * @param filtroIn
   */
  public FiltroRicerca(FiltroRicerca filtroIn){
    this.id = filtroIn.getId();
    this.progressivo = filtroIn.getProgressivo();
    this.operatore = filtroIn.getOperatore();
    this.mnemonicoTabella = filtroIn.getMnemonicoTabella();
    this.aliasTabella = filtroIn.getAliasTabella();
    this.mnemonicoCampo = filtroIn.getMnemonicoCampo();
    this.tipoConfronto = filtroIn.getTipoConfronto();
    this.mnemonicoTabellaConfronto = filtroIn.getMnemonicoTabellaConfronto();
    this.aliasTabellaConfronto = filtroIn.getAliasTabellaConfronto();
    this.mnemonicoCampoConfronto = filtroIn.getMnemonicoCampoConfronto();
    this.valoreConfronto = filtroIn.getValoreConfronto();
    this.parametroConfronto = filtroIn.getParametroConfronto();
    this.notCaseSensitive = filtroIn.getNotCaseSensitive();
  }
  
  /**
   * @return Returns the aliasTabella.
   */
  public String getAliasTabella() {
    return aliasTabella;
  }
  /**
   * @param aliasTabella The aliasTabella to set.
   */
  public void setAliasTabella(String aliasTabella) {
    this.aliasTabella = aliasTabella;
  }
  /**
   * @return Returns the aliasTabellaConfronto.
   */
  public String getAliasTabellaConfronto() {
    return aliasTabellaConfronto;
  }
  /**
   * @param aliasTabellaConfronto The aliasTabellaConfronto to set.
   */
  public void setAliasTabellaConfronto(String aliasTabellaConfronto) {
    this.aliasTabellaConfronto = aliasTabellaConfronto;
  }
  /**
   * @return Returns the id.
   */
  public Integer getId() {
    return id;
  }
  /**
   * @param id The id to set.
   */
  public void setId(Integer id) {
    this.id = id;
  }
  /**
   * @return Returns the mnemonicoCampo.
   */
  public String getMnemonicoCampo() {
    return mnemonicoCampo;
  }
  /**
   * @param mnemonicoCampo The mnemonicoCampo to set.
   */
  public void setMnemonicoCampo(String mnemonicoCampo) {
    this.mnemonicoCampo = mnemonicoCampo;
  }
  /**
   * @return Returns the mnemonicoCampoConfronto.
   */
  public String getMnemonicoCampoConfronto() {
    return mnemonicoCampoConfronto;
  }
  /**
   * @param mnemonicoCampoConfronto The mnemonicoCampoConfronto to set.
   */
  public void setMnemonicoCampoConfronto(String mnemonicoCampoConfronto) {
    this.mnemonicoCampoConfronto = mnemonicoCampoConfronto;
  }
  /**
   * @return Returns the mnemonicoTabella.
   */
  public String getMnemonicoTabella() {
    return mnemonicoTabella;
  }
  /**
   * @param mnemonicoTabella The mnemonicoTabella to set.
   */
  public void setMnemonicoTabella(String mnemonicoTabella) {
    this.mnemonicoTabella = mnemonicoTabella;
  }
  /**
   * @return Returns the mnemonicoTabellaConfronto.
   */
  public String getMnemonicoTabellaConfronto() {
    return mnemonicoTabellaConfronto;
  }
  /**
   * @param mnemonicoTabellaConfronto The mnemonicoTabellaConfronto to set.
   */
  public void setMnemonicoTabellaConfronto(String mnemonicoTabellaConfronto) {
    this.mnemonicoTabellaConfronto = mnemonicoTabellaConfronto;
  }
  /**
   * @return Returns the operatore.
   */
  public String getOperatore() {
    return operatore;
  }
  /**
   * @param operatore The operatore to set.
   */
  public void setOperatore(String operatore) {
    this.operatore = operatore;
  }
  /**
   * @return Returns the progressivo.
   */
  public int getProgressivo() {
    return progressivo;
  }
  /**
   * @param progressivo The progressivo to set.
   */
  public void setProgressivo(int progressivo) {
    this.progressivo = progressivo;
  }
  /**
   * @return Returns the tipoConfronto.
   */
  public Integer getTipoConfronto() {
    return tipoConfronto;
  }
  /**
   * @param tipoConfronto The tipoConfronto to set.
   */
  public void setTipoConfronto(Integer tipoConfronto) {
    this.tipoConfronto = tipoConfronto;
  }
  /**
   * @return Returns the valoreConfronto.
   */
  public String getValoreConfronto() {
    return valoreConfronto;
  }
  /**
   * @param valoreConfronto The valoreConfronto to set.
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
   * @param parametroConfronto parametroConfronto da settare internamente alla classe.
   */
  public void setParametroConfronto(String parametroConfronto) {
    this.parametroConfronto = parametroConfronto;
  }
  
  /**
   * @return Ritorna notCaseSensitive.
   */
  public Integer getNotCaseSensitive() {
    return notCaseSensitive;
  }
 
  /**
   * @param notCaseSensitive caseSensitive da settare internamente alla classe.
   */
  public void setNotCaseSensitive(Integer notCaseSensitive) {
    this.notCaseSensitive = notCaseSensitive;
  }
}
