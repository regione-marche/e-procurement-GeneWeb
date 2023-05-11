/*
 * Created on 18/dic/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import org.apache.struts.action.ActionForm;

/**
 * Form per il recupero della password.
 *
 * @author Franzoni
 */
public class RecuperaPasswordForm extends ActionForm {

  /**
   * UID
   */
  private static final long serialVersionUID = 7069584817341361992L;

  /** login per cui recuperare la password */
  private String            username;

  /** Captcha per verificare che l'utilizzatore della funzione sia un umano. */
  private String            captcha;

  /** Hash di controllo per il captcha. */
  private String            captchaHash;

  /**
   * Costruttore vuoto.
   */
  public RecuperaPasswordForm() {
    inizializzaOggetto();
  }

  private void inizializzaOggetto() {
    this.username = null;
    this.captcha = null;
    this.captchaHash = null;
  }

  /**
   * @return Ritorna username.
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username
   *        username da settare internamente alla classe.
   */
  public void setUsername(String username) {
    this.username = username;
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
