/*
 * Created on 23-apr-2007
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.prospetto;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.web.struts.genmod.ParametriModelloForm;


/**
 * Contenitore dei dati del form parametri associati ad una ricerca con modello
 * in fase di composizione del modello associato alla ricerca stessa
 * 
 * @author Luca.Giacomazzo
 */
public class ParametriProspettoForm extends ParametriModelloForm {

  /**   UID   */
  private static final long serialVersionUID = 3522112932385506701L;

  private String idProspetto;
  
  public ParametriProspettoForm(){
    super();
    this.idProspetto = null;
  }
  
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.idProspetto = null;
  }
  
  /**
   * @return Ritorna idProspetto.
   */
  public String getIdProspetto() {
    return idProspetto;
  }
  
  /**
   * @param idProspetto idProspetto da settare internamente alla classe.
   */
  public void setIdProspetto(String idProspetto) {
    this.idProspetto = idProspetto;
  }
 
}