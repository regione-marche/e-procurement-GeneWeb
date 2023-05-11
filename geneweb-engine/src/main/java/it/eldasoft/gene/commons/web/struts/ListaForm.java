/*
 * Created on 8-set-2006
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

import org.apache.struts.action.ActionMapping;

/**
 * Form da utilizzare per ottenere, in una maschera di lista, l'elenco dei
 * record spuntati
 * 
 * @author Stefano.Sabbadin
 */
public class ListaForm extends DispatchForm {

  /**
   * UID
   */
  private static final long serialVersionUID = -2943068315503125752L;

  /** Elenco degli id presenti nella pagina */
  private String[]          id;

  public ListaForm() {
    super();
    this.id = null;
  }

  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.id = null;
  }

  /**
   * @return Ritorna id.
   */
  public String[] getId() {
    return id;
  }

  /**
   * @param id
   *        id da settare internamente alla classe.
   */
  public void setId(String[] id) {
    this.id = id;
  }

}
