/*
 * Created on 4-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Contiene il discriminante per la Dispatch
 * 
 * @author Stefano.Sabbadin
 */
public class DispatchForm extends ActionForm {

  /**
   * UID
   */
  private static final long serialVersionUID = -6602541297891119765L;

  /**
   * Metodo da richiamare da parte del dispatcher
   */
  private String            metodo;

  public DispatchForm() {
    super();
    this.metodo = null;
  }

  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.metodo = null;
  }

  /**
   * @return Ritorna metodo.
   */
  public String getMetodo() {
    return metodo;
  }

  /**
   * @param metodo
   *        metodo da settare internamente alla classe.
   */
  public void setMetodo(String metodo) {
    this.metodo = metodo;
  }
}
