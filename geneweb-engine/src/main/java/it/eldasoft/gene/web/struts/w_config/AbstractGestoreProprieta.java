package it.eldasoft.gene.web.struts.w_config;

import javax.servlet.http.HttpServletRequest;


/**
 * Abstract Gestore per il salvataggio delle proprieta' nella W_CONFIG.
 * 
 * @author Luca.Giacomazzo
 *
 */
public abstract class AbstractGestoreProprieta {

  private HttpServletRequest request;
  
  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }
  
  /**
   * Metodo per eseguire l'aggiornamento di una proprieta' della W_CONFIG. 
   */
  public abstract void update();
  
}
