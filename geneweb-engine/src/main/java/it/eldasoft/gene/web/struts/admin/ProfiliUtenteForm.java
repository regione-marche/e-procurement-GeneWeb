/*
 * Created on 12-ott-2007
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
 * Contenitore dei dati del form Lista Ricerche di Gruppo
 * 
 * @author Luca.Giacomazzo
 */
public class ProfiliUtenteForm extends ActionForm {

  /**   UID   */
  private static final long serialVersionUID = 793059487840204561L;
  
  private String[] codiceProfilo = null;

  public ProfiliUtenteForm() {
    super();
  }

  public void reset(ActionMapping mapping, HttpServletRequest request){
    super.reset(mapping, request);
    this.codiceProfilo = null;
  }
  /**
   * @return Ritorna idGruppo.
   */
  public String[] getCodiceProfilo() {
    return codiceProfilo;
  }

  /**
   * @param idGruppo da settare internamente alla classe.
   */
  public void setCodiceProfilo(String[] idGruppo) {
    this.codiceProfilo = idGruppo;
  }

}