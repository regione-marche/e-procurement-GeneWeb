/*
 * Created on 04/apr/19
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.commons.web;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Filtro HTTP per il la pulizia nei parametri di informazioni oggetto di vulnerabilit&agrave; XSS.
 *
 * @author Stefano.Sabbadin
 *
 * @since 2.7.0
 */
public class XSSRequestFilter implements Filter {

  /**
   * @see javax.servlet.Filter#destroy()
   */
  public void destroy() {
  }
  
  String exclusionFields;
  /**
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
   *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
   */
  public void doFilter(ServletRequest req, ServletResponse resp,
      FilterChain chain) throws IOException, ServletException {
    XSSRequestWrapper requestWrapper = new XSSRequestWrapper((HttpServletRequest) req, exclusionFields);
    chain.doFilter(requestWrapper, resp);
  }

  /**
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  public void init(FilterConfig arg0) throws ServletException {
    String exFields = "";
    if(arg0.getInitParameter("exclusionFields") != null){
      exFields = arg0.getInitParameter("exclusionFields");
    }
    this.exclusionFields = exFields;
  }

}
