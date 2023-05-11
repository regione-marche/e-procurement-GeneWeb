/*
 * Created on 21-ago-2006
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod.impexp;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Contenitore dei dati del form Lista Gruppi associati ad un Modello
 * 
 * @author Francesco De Filippis
 */
public class GruppiModelliImportForm extends ActionForm {

  /**   UID   */
  private static final long serialVersionUID = 9185818726045474141L;
  
  private String[] idGruppo = null;
  
  public GruppiModelliImportForm() {
    super();
  }

  public void reset(ActionMapping mapping, HttpServletRequest request) {
    this.idGruppo = null;
  }
  
  /**
   * @return Ritorna idGruppo.
   */
  public String[] getIdGruppo() {
    return idGruppo;
  }
  
  /**
   * @param idGruppo idGruppo da settare internamente alla classe.
   */
  public void setIdGruppo(String[] idGruppo) {
    this.idGruppo = idGruppo;
  }
  
  
}