/*
 * Created on 26-mar-2015
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.reportSql;

import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per salvare in sessione la query sql della ricerca sql in analisi.
 * 
 * @author Luca.Giacomazzo
 */
public class SalvaSqlRicercaSqlAction extends AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(SalvaSqlRicercaSqlAction.class);
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action runAction
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
  }
  
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) {
      logger.debug("execute: inizio metodo");
    }

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;
   
    try {

      // lettura dalla sessione dei parametri relativi alla ricerca in analisi
      HttpSession sessione = request.getSession();
      ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
          sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

      String sql = StringUtils.trimToNull(request.getParameter("defSql"));

      if (sql != null) {
        String lineSeparator = System.getProperty("line.separator");
        boolean selectTrovato = false;
        int numeroParametri = 0;
        
        if (StringUtils.contains(sql, lineSeparator)) {
          String[] sqlRighe = StringUtils.split(sql, lineSeparator);
          
          for (int i=0; i < sqlRighe.length; i++) {
            if (sqlRighe[i].toUpperCase().startsWith("SELECT")) {
              selectTrovato = true;
            }
            if (!sqlRighe[i].trim().toUpperCase().startsWith("---")) {
              numeroParametri = numeroParametri + StringUtils.countMatches(sqlRighe[i], "#");
            }
          }
        } else {
          if (sql.toUpperCase().startsWith("SELECT")) {
            selectTrovato = true;
            numeroParametri = numeroParametri + StringUtils.countMatches(sql, "#");
          }
        }

        if (!selectTrovato) {
          this.aggiungiMessaggio(request, "errors.query.noselect");
          target = "errorSql";
        }
        
        if (numeroParametri % 2 != 0) {
          this.aggiungiMessaggio(request, "errors.genric.sql.parametri");
          target = "errorSql";
        }
        
        contenitore.getTestata().setDefSql(sql);
        String sql4html = StringUtils.replace(
            new String(StringEscapeUtils.escapeHtml(sql)), lineSeparator, "<br/>");
        
        request.setAttribute("defSql", sql4html);
      } else {
        contenitore.getTestata().setDefSql(null);
      }
      
      
      this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
      request.setAttribute("tab", CambiaTabRicercaSqlAction.CODICE_TAB_SQL);
  
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }
  
    if (logger.isDebugEnabled()) {
      logger.debug("execute: fine metodo");
    }  
    return mapping.findForward(target);
  }

}
