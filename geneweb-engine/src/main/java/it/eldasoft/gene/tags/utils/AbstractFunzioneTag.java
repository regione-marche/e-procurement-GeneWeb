/*
 * Created on 18-ott-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.utils;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;

import java.util.ResourceBundle;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public abstract class AbstractFunzioneTag {

  private int                paramNum;
  private Class              paramClasses[];
  // Eventuale request
  private HttpServletRequest request;

  /**
   * Resource bundle parte generale dell'applicazione
   */
  public ResourceBundle      resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

  /**
   * Costruttore di default
   * 
   * @param numParam
   *        Numero di parametri. -1 per indefinito
   * @param paramClasses[]
   *        Array contenente le classi dei parametri. sel un parametro è null
   *        vale quelsiasi tipo
   */
  public AbstractFunzioneTag(int numParam, Class[] paramClasses) {
    this.paramNum = numParam;
    this.paramClasses = paramClasses;
  }

  /**
   * Funzione richiamata dal tag gene per l'esecuzione di una funzione
   * 
   * @param pageContext
   *        pageContext del tag. (null) se passato con callFunction funzione
   * @param paramerto
   *        eventuale parametro se si intende passarne uno
   * @return
   */
  abstract public String function(PageContext pageContext, Object[] params)
      throws JspException;

  /**
   * @return Returns the paramNum.
   */
  public int getParamNum() {
    return paramNum;
  }

  public JspException getJspException(PageContext pageContext, Object[] params) {

    if (this.paramNum != -1 && this.paramNum < params.length)
      return new JspException(this.getClass().getName()
          + ": Numero di parametri errato !");
    if (this.paramClasses != null) {
      for (int i = 0; i < this.paramClasses.length; i++) {
        if (i >= params.length) return null;
        if (params[i] instanceof ServletRequest)
          this.setRequest((HttpServletRequest) params[0]);
        if (this.paramClasses[i] != null && params[i] != null) {
          // Se l'oggetto non è compatibile do l'errore
          if (!this.paramClasses[i].isInstance(params[i])) {
            return new JspException(this.getClass().getName()
                + ": Il parametro ("
                + i
                + ") non è di tipo esatto; è "
                + params[i].getClass().getName()
                + " anziché "
                + this.paramClasses[i].getName());
          }
        }
      }
    }
    return null;
  }

  /**
   * @return Returns the request.
   */
  public HttpServletRequest getRequest() {
    return request;
  }

  /**
   * @param request
   *        The request to set.
   */
  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }

}
