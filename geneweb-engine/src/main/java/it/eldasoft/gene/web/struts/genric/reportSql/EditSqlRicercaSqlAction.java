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

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per l'apertura della pagina di Edit del sql della ricerca in analisi
 * 
 * @author Luca.Giacomazzo
 */
public class EditSqlRicercaSqlAction extends AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(EditSqlRicercaSqlAction.class);


  /**
   * Funzione che restituisce le opzioni per accedere alla action runAction
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
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
      
      if (StringUtils.isNotEmpty(contenitore.getTestata().getDefSql())) {
        request.setAttribute("defSql", contenitore.getTestata().getDefSql());
      }
      
      // set nel request del parameter per disabilitare la navigazione
      // in fase di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      // Update del menu tab
      this.setMenuTab(request);

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

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di visualizzazione del dettaglio del SQL di
   * una ricerca
   * 
   * @param request
   */
  private void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_SQL);
      gestoreTab.setTabSelezionabili(null);
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_SQL);
      gestoreTab.setTabSelezionabili(null);
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
    }
  }

}