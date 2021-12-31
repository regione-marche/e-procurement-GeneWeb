/*
 * Created on 28/set/2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.commons.web.struts.UploadFileForm;

import javax.servlet.ServletRequest;

import org.apache.struts.action.ActionMapping;

/**
 * Form contenitore dei dati raccolti in fase di richiesta assistenza.
 *
 * @author Stefano.Sabbadin
 */
public class AssistenzaForm extends UploadFileForm {

  /**
   * UID
   */
  private static final long serialVersionUID = -55917473404352371L;

  /** Denominazione dell'ente o amministrazione a cui appartiene il richiedente l'assistenza. */
  private String            denominazioneEnte;
  /** Nome e cognome del richiedente l'assistenza. */
  private String            nomeRichiedente;
  /** Mail del richiedente. */
  private String            mailRichiedente;
  /** Telefono del richiedente. */
  private String            telefonoRichiedente;
  /** Oggetto della richiesta. */
  private String            oggetto;
  /** Testo della richiesta. */
  private String            testo;
  /** Informazioni sistema. */
  private String            infoSystem;
  /** Captcha per verificare che l'utilizzatore della funzione sia un umano. */
  private String            captcha;

  /** Hash di controllo per il captcha. */
  private String            captchaHash;

  /**
   * Costruttore vuoto.
   */
  public AssistenzaForm() {
    super();
  	inizializza();
  }

  private void inizializza() {
    this.denominazioneEnte = null;
    this.nomeRichiedente = null;
    this.mailRichiedente = null;
    this.telefonoRichiedente = null;
    this.oggetto = null;
    this.testo = null;
    this.infoSystem = null;
    this.captcha = null;
    this.captchaHash = null;
  }

  /*
   * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
   *      javax.servlet.ServletRequest)
   */
  @Override
  public void reset(ActionMapping mapping, ServletRequest request) {
    super.reset(mapping, request);
    this.inizializza();
  }

  /**
   * @return Ritorna denominazioneEnte.
   */
  public String getDenominazioneEnte() {
    return denominazioneEnte;
  }

  /**
   * @param denominazioneEnte
   *        denominazioneEnte da settare internamente alla classe.
   */
  public void setDenominazioneEnte(String denominazioneEnte) {
    this.denominazioneEnte = denominazioneEnte;
  }

  /**
   * @return Ritorna nomeRichiedente.
   */
  public String getNomeRichiedente() {
    return nomeRichiedente;
  }

  /**
   * @param nomeRichiedente
   *        nomeRichiedente da settare internamente alla classe.
   */
  public void setNomeRichiedente(String nomeRichiedente) {
    this.nomeRichiedente = nomeRichiedente;
  }

  /**
   * @return Ritorna mailRichiedente.
   */
  public String getMailRichiedente() {
    return mailRichiedente;
  }

  /**
   * @param mailRichiedente
   *        mailRichiedente da settare internamente alla classe.
   */
  public void setMailRichiedente(String mailRichiedente) {
    this.mailRichiedente = mailRichiedente;
  }

  /**
   * @return Ritorna telefonoRichiedente.
   */
  public String getTelefonoRichiedente() {
    return telefonoRichiedente;
  }

  /**
   * @param telefonoRichiedente
   *        telefonoRichiedente da settare internamente alla classe.
   */
  public void setTelefonoRichiedente(String telefonoRichiedente) {
    this.telefonoRichiedente = telefonoRichiedente;
  }

  /**
   * @return Ritorna oggetto.
   */
  public String getOggetto() {
    return oggetto;
  }

  /**
   * @param oggetto
   *        oggetto da settare internamente alla classe.
   */
  public void setOggetto(String oggetto) {
    this.oggetto = oggetto;
  }

  /**
   * @return Ritorna testo.
   */
  public String getTesto() {
    return testo;
  }

  /**
   * @param testo
   *        testo da settare internamente alla classe.
   */
  public void setTesto(String testo) {
    this.testo = testo;
  }

  /**
   * @return Ritorna infoSystem.
   */
  public String getInfoSystem() {
    return infoSystem;
  }

  /**
   * @param infoSystem
   *        infoSystem da settare internamente alla classe.
   */
  public void setInfoSystem(String infoSystem) {
    this.infoSystem = infoSystem;
  }

  /**
   * @return Ritorna captcha.
   */
  public String getCaptcha() {
    return captcha;
  }

  /**
   * @param captcha
   *        captcha da settare internamente alla classe.
   */
  public void setCaptcha(String captcha) {
    this.captcha = captcha;
  }

  /**
   * @return Ritorna captchaHash.
   */
  public String getCaptchaHash() {
    return captchaHash;
  }

  /**
   * @param captchaHash
   *        captchaHash da settare internamente alla classe.
   */
  public void setCaptchaHash(String captchaHash) {
    this.captchaHash = captchaHash;
  }

}
