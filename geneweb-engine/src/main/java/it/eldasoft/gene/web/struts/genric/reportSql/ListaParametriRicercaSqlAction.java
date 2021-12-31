/*
 * Created on 30-mar-2015
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
import it.eldasoft.gene.web.struts.genric.CambiaTabAction;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.parametro.ListaParametriRicercaAction;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

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
 * Classe per la gestione degli eventi della pagina lista parametri per una ricerca
 * sql.
 * 
 * @author Luca.Giacomazzo
 */
public class ListaParametriRicercaSqlAction extends ListaParametriRicercaAction {

  static Logger            logger = Logger.getLogger(ListaParametriRicercaSqlAction.class);

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioneAcquistata()
   */
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_GEN_RICERCHE;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * ListaParametriRicercaSql
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniListaParametriRicercaSql() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
  }

  /**
   * Ridefinizione del metodo che inizializza nel request la gestione dei tab.
   * 
   * @param request
   */
  public void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);

    boolean isInSessione = true;
    if (obj == null) {
      isInSessione = false;
      obj = new GestioneTab();
    }

    GestioneTab gestoreTab = (GestioneTab) obj;
    gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_SQL);
    gestoreTab.setTabSelezionabili(new String[] {
        CostantiGenRicerche.TAB_DATI_GENERALI, CostantiGenRicerche.TAB_GRUPPI,
        CostantiGenRicerche.TAB_SQL, CostantiGenRicerche.TAB_PARAMETRI });

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * creaParametroRicerca
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniCreaParametroRicerca() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * modificaParametroModello
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModificaParametroRicercaSql() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * eliminaParametroModello
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniElimina() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaSu
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaSu() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaGiù
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaGiu() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * spostaInPosizioneMarcata
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaInPosizioneMarcata() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
  }

  public ActionForward elimina(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("elimina: inizio metodo");
    }

    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
        session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    String id = request.getParameter("id");

    // Cancellazione del parametro dalla sessione
    contenitore.eliminaParametro(Integer.parseInt(id));

    this.setMenuTab(request);
    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    request.setAttribute("tab", CambiaTabAction.CODICE_TAB_PARAMETRI);

    if (logger.isDebugEnabled()) {
      logger.debug("elimina: fine metodo");
    }

    return mapping.findForward(target);
  }

}
