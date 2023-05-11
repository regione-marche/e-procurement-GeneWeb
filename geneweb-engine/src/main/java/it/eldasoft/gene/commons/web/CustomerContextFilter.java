/*
 * Created on 15/gen/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.spring.ContextHolder;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Filtro HTTP per il set nel thread di gestione della richiesta delle
 * informazioni id e ufficio appartenenza per l'impostazione del contesto oracle
 * nelle connessioni al DB effettuate durante la richiesta
 * 
 * @author Stefano.Sabbadin
 * 
 * @since 1.4.5.1 
 */
public class CustomerContextFilter implements Filter {

  /**
   * @see javax.servlet.Filter#destroy()
   */
  public void destroy() {
  }

  /**
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
   *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
   */
  public void doFilter(ServletRequest req, ServletResponse resp,
      FilterChain chain) throws IOException, ServletException {

    if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_CONTESTI_ABILITATI))
        && it.eldasoft.utils.sql.comp.SqlManager.DATABASE_ORACLE.equalsIgnoreCase(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE))
        && req instanceof HttpServletRequest) {
      HttpServletRequest request = (HttpServletRequest) req;

      ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      String user = null;
      String context = null;
      if (profiloUtente != null) {
        user = Integer.toString(profiloUtente.getId());
        context = profiloUtente.getUfficioAppartenenza();
      }
      ContextHolder.setUserId(user);
      ContextHolder.setContext(context);
    }

    chain.doFilter(req, resp);
  }

  /**
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  public void init(FilterConfig arg0) throws ServletException {
  }

}
