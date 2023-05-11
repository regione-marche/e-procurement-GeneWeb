/*
 * Created on 27/gen/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.grafici;

import org.apache.struts.action.ActionForm;

/**
 * ActionForm per la generazione di un grafico mediante la libreria JFreeChart
 * 
 * @since 1.4.0
 * @author Stefano.Sabbadin
 */
public class CreaGraficoForm extends ActionForm {

  /**
   * UID
   */
  private static final long serialVersionUID = -4669501935859298436L;

  /** Tipologia di grafico da realizzare: vedere le costanti in CostantiChart */
  private String            tipo;
  /** Titolo del grafico */
  private String            titolo;
  /**
   * Classe di gestione del popolamento del dataset ed eventuali customizzazioni
   * grafiche
   */
  private String            gestore;
  /**
   * Eventuali dati di input per il gestore, nel formato
   * NOME=TIPO:VALORE[;NOME=TIPO:VALORE] previsto in UtilityTags.stringToXXX
   */
  private String            inputGestore;
  /** larghezza dell'immagine da generare, di default pari a 800 */
  private Integer           larghezza;
  /** altezza dell'immagine da generare, di default pari a 600 */
  private Integer           altezza;
  /** Deve essere inserita la legenda ? */
  private boolean           legenda;
  /** Va inserita la mappa dei tooltips sui dati ? */
  private boolean           tooltips;
  /** Formato dell'output: vedere le costanti in CostantiChart */
  private String            formatoImmagine;

  public CreaGraficoForm() {
    this.tipo = null;
    this.titolo = null;
    this.gestore = null;
    this.inputGestore = null;
    this.larghezza = new Integer(800);
    this.altezza = new Integer(600);
    this.legenda = true;
    this.tooltips = true;
    this.formatoImmagine = CostantiGrafici.FORMATO_IMMAGINE_PNG;
  }

  /**
   * @return Ritorna tipo.
   */
  public String getTipo() {
    return tipo;
  }

  /**
   * @param tipo
   *        tipo da settare internamente alla classe.
   */
  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  /**
   * @return Ritorna titolo.
   */
  public String getTitolo() {
    return titolo;
  }

  /**
   * @param titolo
   *        titolo da settare internamente alla classe.
   */
  public void setTitolo(String titolo) {
    this.titolo = titolo;
  }

  /**
   * @return Ritorna gestore.
   */
  public String getGestore() {
    return gestore;
  }

  /**
   * @param gestore
   *        gestore da settare internamente alla classe.
   */
  public void setGestore(String gestore) {
    this.gestore = gestore;
  }

  /**
   * @return Ritorna inputGestore.
   */
  public String getInputGestore() {
    return inputGestore;
  }

  /**
   * @param inputGestore
   *        inputGestore da settare internamente alla classe.
   */
  public void setInputGestore(String inputGestore) {
    this.inputGestore = inputGestore;
  }

  /**
   * @return Ritorna larghezza.
   */
  public Integer getLarghezza() {
    return larghezza;
  }

  /**
   * @param larghezza
   *        larghezza da settare internamente alla classe.
   */
  public void setLarghezza(Integer larghezza) {
    this.larghezza = larghezza;
  }

  /**
   * @return Ritorna altezza.
   */
  public Integer getAltezza() {
    return altezza;
  }

  /**
   * @param altezza
   *        altezza da settare internamente alla classe.
   */
  public void setAltezza(Integer altezza) {
    this.altezza = altezza;
  }

  /**
   * @return Ritorna legenda.
   */
  public boolean isLegenda() {
    return legenda;
  }

  /**
   * @param legenda
   *        legenda da settare internamente alla classe.
   */
  public void setLegenda(boolean legenda) {
    this.legenda = legenda;
  }

  /**
   * @return Ritorna tooltips.
   */
  public boolean isTooltips() {
    return tooltips;
  }

  /**
   * @param tooltips
   *        tooltips da settare internamente alla classe.
   */
  public void setTooltips(boolean tooltips) {
    this.tooltips = tooltips;
  }

  /**
   * @return Ritorna formatoImmagine.
   */
  public String getFormatoImmagine() {
    return formatoImmagine;
  }

  /**
   * @param formatoImmagine
   *        formatoImmagine da settare internamente alla classe.
   */
  public void setFormatoImmagine(String formatoImmagine) {
    this.formatoImmagine = formatoImmagine;
  }

}
