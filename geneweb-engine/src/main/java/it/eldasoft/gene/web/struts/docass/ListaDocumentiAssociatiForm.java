/*
 * Created on 16-gen-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.docass;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import it.eldasoft.gene.commons.web.struts.ListaForm;

/**
 * Form per la cancellazione multipla dei documenti associati 
 * 
 * @author Luca.Giacomazzo
 */
public class ListaDocumentiAssociatiForm extends ListaForm {

  /**   UID   */
  private static final long serialVersionUID = -942202113609736695L;

  private String cancellazioneFile;
  
  public ListaDocumentiAssociatiForm(){
    super();
    this.cancellazioneFile = null;
  }

  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);
    this.cancellazioneFile = null;
  }
  
  /**
   * @return Ritorna cancellazioneFile.
   */
  public String getCancellazioneFile() {
    return cancellazioneFile;
  }
  
  /**
   * @param cancellazioneFile cancellazioneFile da settare internamente alla classe.
   */
  public void setCancellazioneFile(String cancellazioneFile) {
    this.cancellazioneFile = cancellazioneFile;
  }
  
}