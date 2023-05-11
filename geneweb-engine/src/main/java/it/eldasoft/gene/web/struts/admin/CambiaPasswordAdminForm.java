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
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * Contenitore dei dati del form di cambio password
 * 
 * @author Stefano.Sabbadin
 */
public class CambiaPasswordAdminForm extends CambiaPasswordForm {

  /**
   * UID
   */
  private static final long serialVersionUID      = 7077412012991213216L;

  /** idAccount in modifica   */
  private String            idAccount;

  public CambiaPasswordAdminForm() {
    super();
    this.inizializzaOggetto();
  }
  
  private void inizializzaOggetto() {
    this.idAccount = null;
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
   * Metodo di validazione tramite Struts dei dati del form, prima di eseguire
   * l'elaborazione vera e propria dell'azione.
   */
  public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
    ActionErrors errors = new ActionErrors();
    boolean esito = true;

    if (this.getNuovaPassword() == null || this.getNuovaPassword().trim().length() == 0) {
      errors.add("nuovaPassword", new ActionMessage(
          "errors.chgPsw.nuovaPassword"));
      esito = false;
    }

    if (this.getConfermaNuovaPassword() == null
        || this.getConfermaNuovaPassword().trim().length() == 0) {
      errors.add("confermaNuovaPassword", new ActionMessage(
          "errors.chgPsw.confermaNuovaPassword"));
      esito = false;
    }

    if (!this.getConfermaNuovaPassword().equals(this.getNuovaPassword())) {
      errors.add("nuovaPassword", new ActionMessage(
          "errors.chgPsw.nuovePasswordDiverse"));
      esito = false;
    }

    try {
      ValidatorePassword.validaPassword(this.getNuovaPassword());
    } catch (ValidatorePasswordException e) {
      errors.add("nuovaPassword", new ActionMessage(
          "errors.chgPsw.noValidazione"));
      esito = false;
    }

    if (!esito) this.reset(mapping, request);

    return errors;
  }

  
  public String getIdAccount() {
    return idAccount;
  }

  
  public void setIdAccount(String idAccount) {
    this.idAccount = idAccount;
  }
}
