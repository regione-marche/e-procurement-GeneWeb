/**
 * 
 */
package it.eldasoft.gene.web.struts.admin;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;


/**
 * 
 * @author Luca Giacomazzo
 */
public class ModelliGruppoForm extends ActionForm {

  /**   UID   */
  private static final long serialVersionUID = -8493314320554944850L;

  private String[] idModello = null; 
  
  public ModelliGruppoForm() {
    super();
  }
  
  public void reset(ActionMapping mapping, HttpServletRequest request){
    super.reset(mapping, request);
    this.idModello = null;
  }
  
  /**
   * @return Ritorna idModello.
   */
  public String[] getIdModello() {
    return idModello;
  }

  /**
   * @param idModello idModello da settare internamente alla classe.
   */
  public void setIdModello(String[] idModello) {
    this.idModello = idModello;
  }
    
}
