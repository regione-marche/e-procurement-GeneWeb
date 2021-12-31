/*
 * Created on 01-set-2006
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.campo;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form per l'inserimento di un elenco di campi in una ricerca
 * 
 * @author Stefano.Sabbadin
 */
public class CampiRicercaForm extends ActionForm {

  /**
   * UID
   */
  private static final long serialVersionUID = 3897408334726803395L;

  /**
   * Elenco dei campi da inserire, caratterizzati da ALIAS TABELLA + separatore +
   * MNEMONICO CAMPO
   */
  private String[]          campiSelezionati;

  /**
   * @return Ritorna elencoCampi.
   */
  public String[] getCampiSelezionati() {
    return campiSelezionati;
  }

  /**
   * @param elencoCampi
   *        elencoCampi da settare internamente alla classe.
   */
  public void setCampiSelezionati(String[] elencoCampi) {
    this.campiSelezionati = elencoCampi;
  }
  
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.campiSelezionati = null;
  }
}
