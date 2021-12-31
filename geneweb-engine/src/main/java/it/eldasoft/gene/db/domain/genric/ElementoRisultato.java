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

import it.eldasoft.utils.io.export.ElementoExport;

import java.io.Serializable;

/**
 * @author Stefano.Sabbadin
 */
public class ElementoRisultato implements Serializable {

  /** UID */
  private static final long serialVersionUID = 4492911985866007095L;

  private Object            valore;
  private short             tipo;
  private String            formatoDisplay;
  private String            formattazioneHtml;

  public ElementoRisultato() {
    this.valore = null;
    this.tipo = 0;
    this.formatoDisplay = "";
    this.formattazioneHtml = "";
  }

  /**
   * @return Returns the formattazioneHtml.
   */
  public String getFormattazioneHtml() {

    return formattazioneHtml;
  }

  /**
   * @param formattazioneHtml
   *        The formattazioneHtml to set.
   */
  public void setFormattazioneHtml(String formattazioneHtml) {

    this.formattazioneHtml = formattazioneHtml;
  }

  /**
   * @return Returns the valore.
   */
  public Object getValore() {

    return valore;
  }

  /**
   * @param valore
   *        The valore to set.
   */
  public void setValore(Object valore) {

    this.valore = valore;
  }

  /**
   * @return Ritorna tipo.
   */
  public short getTipo() {
    return tipo;
  }

  /**
   * @param tipo
   *        tipo da settare internamente alla classe.
   */
  public void setTipo(short tipo) {
    this.tipo = tipo;
  }

  /**
   * @return Ritorna formatoDisplay.
   */
  public String getFormatoDisplay() {
    return formatoDisplay;
  }

  /**
   * @param formatoDisplay
   *        formatoDisplay da settare internamente alla classe.
   */
  public void setFormatoDisplay(String formatoDisplay) {
    this.formatoDisplay = formatoDisplay;
  }

  /**
   * Ritorna il dato elementare per l'esportazione
   * 
   * @return dato elementare costituito dal tipo e dal valore assunto
   */
  public ElementoExport getElementoExport() {
    ElementoExport dato = new ElementoExport();
    dato.setTipo(this.tipo);
    dato.setValore(this.valore);
    return dato;
  }
}