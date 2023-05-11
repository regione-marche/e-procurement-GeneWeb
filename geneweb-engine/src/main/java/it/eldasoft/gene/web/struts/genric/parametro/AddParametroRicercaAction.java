/*
 * Created on 26-set-2009
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.parametro;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.CambiaTabAction;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Aggiunge un parametro alla ricerca previo controllo che non esista già con il
 * medesimo codice
 * 
 * @author Stefano.Sabbadin
 */
public class AddParametroRicercaAction extends AbstractActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(AddParametroRicercaAction.class);

  /**
   * Inserisce nella request l'elenco degli operatori, delle tabelle e l'elenco
   * dei campi associati a tali tabelle per inserire un filtro.
   * 
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

    ParametroRicercaForm parametroRicercaForm = (ParametroRicercaForm) form;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    ParametroRicercaForm parametroEsistente = null;
    boolean trovato = false;
    // si esegue la verifica che il parametro non esista già
    for (int i = 0; i < contenitore.getNumeroParametri() && !trovato; i++) {
      parametroEsistente = contenitore.estraiParametro(i);
      if (parametroEsistente.getCodiceParametro().equals(
          parametroRicercaForm.getCodiceParametro())) {
        trovato = true;
        String messageKey = "errors.genRic.parametroDuplicato";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
        target = "forwardErroreParametroDuplicato";
      }
    }
    // se il parametro non esiste, allora lo si aggiunge
    if (!trovato) {
      contenitore.aggiungiParametro(parametroRicercaForm);
      this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    }
    request.setAttribute("tab", CambiaTabAction.CODICE_TAB_PARAMETRI);
    this.setMenuTab(request, target);

    // set nel request del parameter per disabilitare la navigazione in fase di
    // editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di visualizzazione del dettaglio dei Dati Generali di
   * una ricerca
   * 
   * @param request
   * @param target
   */
  private void setMenuTab(HttpServletRequest request, String target) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);

    boolean isInSessione = true;
    if (obj == null) {
      isInSessione = false;
      obj = new GestioneTab();
    }

    GestioneTab gestoreTab = (GestioneTab) obj;
    gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_PARAMETRI);
    if (!"forwardErroreParametroDuplicato".equals(target))
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiGenRicerche.TAB_DATI_GENERALI,
          CostantiGenRicerche.TAB_GRUPPI, CostantiGenRicerche.TAB_ARGOMENTI,
          CostantiGenRicerche.TAB_CAMPI, CostantiGenRicerche.TAB_JOIN,
          CostantiGenRicerche.TAB_FILTRI, CostantiGenRicerche.TAB_ORDINAMENTI,
          CostantiGenRicerche.TAB_LAYOUT });
    else
      gestoreTab.setTabSelezionabili(null);

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }

}