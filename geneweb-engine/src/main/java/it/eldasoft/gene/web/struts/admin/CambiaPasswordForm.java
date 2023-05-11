/*
 * Created on 16-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import it.eldasoft.utils.sicurezza.psw.ValidatorePassword;
import it.eldasoft.utils.sicurezza.psw.ValidatorePasswordException;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * Contenitore dei dati del form di cambio password
 * 
 * @author Stefano.Sabbadin
 */
public class CambiaPasswordForm extends ActionForm {

  /**
   * UID
   */
  private static final long serialVersionUID      = 7077412012991213216L;

  /** vecchia password */
  private String            vecchiaPassword       = null;

  /** nuovo valore della password */
  private String            nuovaPassword         = null;

  /** conferma del nuovo valore della password */
  private String            confermaNuovaPassword = null;
  
  /** metodo di apertura della action */
  private String            metodo                = null;

  public CambiaPasswordForm() {
    super();
    this.inizializzaOggetto();
  }

  private void inizializzaOggetto() {
    this.vecchiaPassword = null;
    this.nuovaPassword = null;
    this.confermaNuovaPassword = null;
    this.metodo = null;
  }

  /**
   * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
   *      javax.servlet.http.HttpServletRequest)
   */
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.inizializzaOggetto();
  }

  /**
   * @return Ritorna confermaNuovaPassword.
   */
  public String getConfermaNuovaPassword() {
    return confermaNuovaPassword;
  }

  /**
   * @param confermaNuovaPassword
   *        confermaNuovaPassword da settare internamente alla classe.
   */
  public void setConfermaNuovaPassword(String confermaNuovaPassword) {
    this.confermaNuovaPassword = confermaNuovaPassword;
  }

  /**
   * @return Ritorna nuovaPassword.
   */
  public String getNuovaPassword() {
    return nuovaPassword;
  }

  /**
   * @param nuovaPassword
   *        nuovaPassword da settare internamente alla classe.
   */
  public void setNuovaPassword(String nuovaPassword) {
    this.nuovaPassword = nuovaPassword;
  }

  /**
   * @return Ritorna vecchiaPassword.
   */
  public String getVecchiaPassword() {
    return vecchiaPassword;
  }

  /**
   * @param vecchiaPassword
   *        vecchiaPassword da settare internamente alla classe.
   */
  public void setVecchiaPassword(String vecchiaPassword) {
    this.vecchiaPassword = vecchiaPassword;
  }

  /**
   * Metodo di validazione tramite Struts dei dati del form, prima di eseguire
   * l'elaborazione vera e propria dell'azione.
   */
  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
    ActionErrors errors = new ActionErrors();
    boolean esito = true;

    if (this.vecchiaPassword == null
        || this.vecchiaPassword.trim().length() == 0) {
      errors.add("vecchiaPassword", new ActionMessage(
          "errors.chgPsw.vecchiaPassword"));
      esito = false;
    }

    if (this.nuovaPassword == null || this.nuovaPassword.trim().length() == 0) {
      errors.add("nuovaPassword", new ActionMessage(
          "errors.chgPsw.nuovaPassword"));
      esito = false;
    }

    if (this.confermaNuovaPassword == null
        || this.confermaNuovaPassword.trim().length() == 0) {
      errors.add("confermaNuovaPassword", new ActionMessage(
          "errors.chgPsw.confermaNuovaPassword"));
      esito = false;
    }

    if (!this.confermaNuovaPassword.equals(this.nuovaPassword)) {
      errors.add("nuovaPassword", new ActionMessage(
          "errors.chgPsw.nuovePasswordDiverse"));
      esito = false;
    }

    try {
      ValidatorePassword.validaPassword(this.nuovaPassword);
    } catch (ValidatorePasswordException e) {
      errors.add("nuovaPassword", new ActionMessage(
          "errors.chgPsw.noValidazione"));
      esito = false;
    }

    if (!esito) this.reset(mapping, request);

    return errors;
  }

  
  /**
   * @return Returns the metodo.
   */
  public String getMetodo() {
    return metodo;
  }

  
  /**
   * @param metodo The metodo to set.
   */
  public void setMetodo(String metodo) {
    this.metodo = metodo;
  }
}
