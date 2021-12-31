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

import java.io.File;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

/**
 * Form per il recupero della password.
 *
 * @author Franzoni
 */
public class AdminAccessForm extends ActionForm {

  /**
   * UID
   */
  private static final long serialVersionUID = 7069584817341361992L;

  /** login per cui recuperare la password */
  private String            certificatoText;
  
  private FormFile          selezioneFile;
  
  private String            motivazione;
  
  private String            metodo;     
  
  private String            passwordDominio;
  
  private String            emailDominio;

  public AdminAccessForm(){
    super();
    this.inizializzaOggetto();
  }
  
  private void inizializzaOggetto(){
    this.selezioneFile = null;
  }
  
  public void setSelezioneFile(FormFile selezioneFile) {
    this.selezioneFile = selezioneFile;
  }

  public FormFile getSelezioneFile() {
    return selezioneFile;
  }
  
  public void setCertificatoText(String certificatoText) {
    this.certificatoText = certificatoText;
  }

  public String getCertificatoText() {
    return certificatoText;
  }

  public void setMotivazione(String motivazione) {
    this.motivazione = motivazione;
  }

  public String getMotivazione() {
    return motivazione;
  }

  public void setMetodo(String metodo) {
    this.metodo = metodo;
  }

  public String getMetodo() {
    return metodo;
  }

  public void setPasswordDominio(String passwordDominio) {
    this.passwordDominio = passwordDominio;
  }

  public String getPasswordDominio() {
    return passwordDominio;
  }

  public void setEmailDominio(String emailDominio) {
    this.emailDominio = emailDominio;
  }

  public String getEmailDominio() {
    return emailDominio;
  }

}
