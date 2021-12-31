/*
 * Created on 20-ago-2007
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.struts;

import javax.servlet.ServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

/**
 * Action Form per l'upload del file XML contenente la definizione del report da
 * importare 
 * 
 * @author Luca.Giacomazzo
 */
public class UploadFileForm extends ActionForm {
  
  /**   UID   */
  private static final long serialVersionUID = 8916473922107003071L;

  private FormFile          selezioneFile;
  
  public UploadFileForm(){
    super();
    this.inizializzaOggetto();
  }

  /*
   * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
   *      javax.servlet.ServletRequest)
   */
  public void reset(ActionMapping mapping, ServletRequest request) {
    super.reset(mapping, request);
    this.inizializzaOggetto();
  }
  
  private void inizializzaOggetto(){
    this.selezioneFile = null;
  }
  
  /**
   * @return Ritorna selezioneFile.
   */
  public FormFile getSelezioneFile() {
    return selezioneFile;
  }
  
  /**
   * @param selezioneFile selezioneFile da settare internamente alla classe.
   */
  public void setSelezioneFile(FormFile selezioneFile) {
    this.selezioneFile = selezioneFile;
  }

}