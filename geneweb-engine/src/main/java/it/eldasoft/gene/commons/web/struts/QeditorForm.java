package it.eldasoft.gene.commons.web.struts;

import javax.servlet.ServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Action Form per il json del qeditor
 *
 * @author Marcello.Caminiti
 */
public class QeditorForm extends ActionForm {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  /**   UID   */


  private String          jsonFile;

  public QeditorForm(){
    super();
    this.inizializzaOggetto();
  }

  /*
   * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
   *      javax.servlet.ServletRequest)
   */
  @Override
  public void reset(ActionMapping mapping, ServletRequest request) {
    super.reset(mapping, request);
    this.inizializzaOggetto();
  }

  private void inizializzaOggetto(){
    this.jsonFile = null;
  }

  /**
   * @return Ritorna jsonFile.
   */
  public String getJsonFile() {
    return jsonFile;
  }

  /**
   * @param selezioneFile selezioneFile da settare internamente alla classe.
   */
  public void setJsonFile(String jsonFile) {
    this.jsonFile = jsonFile;
  }

}