/*
 * Created on 02-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Contenitore dei dati del form Lista utenti associati ad un profilo
 * 
 * @author Luca.Giacomazzo
 */
public class UtentiProfiloForm extends ActionForm {

  /**   UID   */
  private static final long serialVersionUID = 2223451297451287595L;

  private String[] idAccount;
  
  public UtentiProfiloForm(){
    this.idAccount = null;
  }
  
  public void reset(ActionMapping mapping, HttpServletRequest request){
    super.reset(mapping, request);
    this.idAccount = null;
  }
 
  /**
   * @return Ritorna idAccount.
   */
  public String[] getIdAccount() {
    return idAccount;
  }
  
  /**
   * @param idAccount idAccount da settare internamente alla classe.
   */
  public void setIdAccount(String[] idAccount) {
    this.idAccount = idAccount;
  }
  
}