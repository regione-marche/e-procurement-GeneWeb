/*
 * Created on 13-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.login;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form per il contenimento di login e password per l'autenticazione
 * 
 * @author Stefano.Sabbadin
 */
public class LoginForm extends ActionForm {

  /**   UID   */
  private static final long serialVersionUID = -989053977808439700L;

  /** username/login */
  private String            username;
  
  /** password */
  private String            password;

  public LoginForm(){
    super();
    this.inizializzaOggetto();
  }
  
  private void inizializzaOggetto() {
    this.username = null;
    this.password = null;
  }
  
  /**
   * @return Returns the password.
   */
  public String getPassword() {
    return this.password;
  }

  /**
   * @param password
   *        The password to set.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return Returns the username.
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * @param username
   *        The username to set.
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
   *      javax.servlet.http.HttpServletRequest)
   */
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    inizializzaOggetto();
  }
  
}