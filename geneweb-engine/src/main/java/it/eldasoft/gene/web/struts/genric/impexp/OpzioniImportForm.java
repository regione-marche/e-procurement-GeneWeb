package it.eldasoft.gene.web.struts.genric.impexp;

import javax.servlet.ServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


public class OpzioniImportForm extends ActionForm {

  /**   UID   */
  private static final long serialVersionUID = -8352288055749303357L;

  private String tipoImport;
  private String nuovoTitolo;
  
  public OpzioniImportForm(){
    super();
    this.inizializzaOggetto();
  }

  private void inizializzaOggetto(){
    this.tipoImport = null;
    this.nuovoTitolo = null;
  }
 
  /*
   * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
   *      javax.servlet.ServletRequest)
   */
  public void reset(ActionMapping mapping, ServletRequest request) {
    super.reset(mapping, request);
    this.inizializzaOggetto();
  }
  
  /**
   * @return Ritorna nuovoTitolo.
   */
  public String getNuovoTitolo() {
    return nuovoTitolo;
  }
 
  /**
   * @param nuovoTitolo nuovoTitolo da settare internamente alla classe.
   */
  public void setNuovoTitolo(String nuovoTitolo) {
    this.nuovoTitolo = nuovoTitolo;
  }
  
  /**
   * @return Ritorna tipoImport.
   */
  public String getTipoImport() {
    return tipoImport;
  }
  
  /**
   * @param tipoImport tipoImport da settare internamente alla classe.
   */
  public void setTipoImport(String tipoImport) {
    this.tipoImport = tipoImport;
  }
  
}