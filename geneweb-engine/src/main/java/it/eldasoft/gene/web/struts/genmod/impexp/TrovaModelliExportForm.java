/*
 * Created on 13-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod.impexp;

import it.eldasoft.gene.web.struts.genmod.TrovaModelliForm;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

/**
 * Contenitore dei dati del form dei parametri di ricerca di un modello la cui
 * definzione e' da esportare
 * 
 * @author Francesco.DeFilippis
 */
public class TrovaModelliExportForm extends TrovaModelliForm {

  /**   UID   */
  private static final long serialVersionUID = 4552165038029007328L;

  public TrovaModelliExportForm() {
    super();
  }

  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
  }
  
}