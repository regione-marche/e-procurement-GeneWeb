/*
 * Created on 21-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric;

import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per l'apertura della lista delle ricerche predefinite e/o pubblicate
 * 
 * @author Luca.Giacomazzo
 */

public class ListaRicerchePredefiniteAction extends ActionBaseNoOpzioni {

  /** Logger Log4J di classe */
  static Logger           logger = Logger.getLogger(ListaRicerchePredefiniteAction.class);

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi alle
   * ricerche
   */
  private RicercheManager ricercheManager;

  /**
   * @return Ritorna ricercheManager.
   */
  public RicercheManager getRicercheManager() {
    return ricercheManager;
  }

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    // rimozione dalla sessione di oggetti precedentemente creati
    this.cleanSession(request);
    
    HttpSession session = request.getSession();
    // rimozione dalla sessione il nome della ricerca estratta precedentemente
    if (session.getAttribute(CostantiGenerali.NOME_OGGETTO_SESSION) != null)
      session.removeAttribute(CostantiGenerali.NOME_OGGETTO_SESSION);

    ProfiloUtente profiloUtente = (ProfiloUtente) session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    // Se la property it.eldasoft.generatoreRicerche.base.schemaViste
    // e' valorizzata allora è possibile accedere alle ricerche base come ricerche
    // predefinite, altrimenti no
    boolean mostraReportBase = false;
    String nomeSchemaVista =  ConfigManager.getValore(
        CostantiGenerali.PROP_SCHEMA_VISTE_REPORT_BASE);
    if (nomeSchemaVista != null && nomeSchemaVista.length() > 0) {
      mostraReportBase = true;
    }
    
    
    // I report SQL se pubblicati possono essere eseguiti da tutti gli utenti
    boolean mostraReportSql = true;
        
    List<?> listaRicerchePredefinite = this.ricercheManager.getRicerchePredefinite(
        profiloUtente.getId(),
        (String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO),
        (String) session.getAttribute(CostantiGenerali.PROFILO_ATTIVO),
        mostraReportBase, mostraReportSql);

    request.setAttribute("listaRicerchePredefinite", listaRicerchePredefinite);

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }
}