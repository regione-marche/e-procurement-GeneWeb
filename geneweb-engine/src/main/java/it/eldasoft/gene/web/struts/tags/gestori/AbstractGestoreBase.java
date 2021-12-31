package it.eldasoft.gene.web.struts.tags.gestori;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;

import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Classe che implementa le funzionalità di base di un gestore. Di Lista Scheda
 * Pagina Ecc.
 * 
 * @author marco.franceschin
 * 
 */
public abstract class AbstractGestoreBase {
 
  private ServletContext     servletContext=null;
  private HttpServletRequest request=null;

  /**
   * Resource bundle parte generale dell'applicazione
   */
  public ResourceBundle      resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  /**
   * @return Returns the servletContext.
   */
  public ServletContext getServletContext() {
    return servletContext;
  }

  /**
   * @return Ritorna request.
   */
  public HttpServletRequest getRequest() {
    return request;
  }

  /**
   * @param request
   *        request da settare internamente alla classe.
   */
  public void setRequest(HttpServletRequest request) {
    this.request = request;
    this.servletContext=request.getSession().getServletContext();
  }

}
