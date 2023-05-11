/*
 * Created on 22-set-2006
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


/**
 * Contenitore dei dati del form Parametri associati ad una Ricerca
 * in fase di estrazione di una ricerca
 * 
 * @author Luca Giacomazzo
 */
public class ParametriRicercaForm extends ActionForm {

  /**   UID   */
  private static final long serialVersionUID = 972381972270716744L;

  private String[] parametriRicerca = null;
  
  public ParametriRicercaForm() {
    super();
  }

  public void reset(ActionMapping mapping, HttpServletRequest request) {
    this.parametriRicerca = null;
  }
  
  /**
   * @return Ritorna parametriRicerca.
   */
  public String[] getParametriRicerca() {
    return parametriRicerca;
  }

  /**
   * @param parametriRicerca parametriRicerca da settare internamente alla classe.
   */
  public void setParametriRicerca(String[] parametriRicerca) {
    this.parametriRicerca = parametriRicerca;
  } 
}