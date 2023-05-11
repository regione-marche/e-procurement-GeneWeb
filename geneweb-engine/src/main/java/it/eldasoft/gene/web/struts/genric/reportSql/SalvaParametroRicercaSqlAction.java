/*
 * Created on 13-set-2006
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
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractDispatchActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.CambiaTabAction;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.parametro.ParametroRicercaForm;
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
 * Action per l'inserimento di un nuovo filtro in una ricerca o la modifica di
 * un filtro esistente
 * 
 * @author Luca Giacomazzo
 */
public class SalvaParametroRicercaSqlAction extends
    AbstractDispatchActionBaseGenRicerche {

  //private static final String SUCCESS_MODIFICA_FORWARD = "successUpdate";

  /** Logger Log4J di classe */
  static Logger               logger = Logger.getLogger(SalvaParametroRicercaSqlAction.class);

  /**
   * Funzione che restituisce le opzioni per accedere alla action insert
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniInsert() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  /**
   * Action per l'insert di un nuovo ordinamento in una ricerca
   */
  public ActionForward insert(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("insert: inizio metodo");
    }

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
        session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    // creazione di un nuovo parametro con CODICE = Parametro<i>, dove i =
    // max(fra i parametri esistenti) + 1
    // e il tipo di dato e il tabellato sono determinati a partire dal campo
    // selezionato nel filtro.
    ParametroRicercaForm parametroRicercaForm = (ParametroRicercaForm) form;
    parametroRicercaForm.setCodiceParametro(parametroRicercaForm.getCodiceParametro());
    parametroRicercaForm.setNome(parametroRicercaForm.getNome());
    parametroRicercaForm.setTipoParametro(parametroRicercaForm.getTipoParametro());

    // set in sessione del nuovo parametro
    contenitore.aggiungiParametro(parametroRicercaForm);

    // se nel request e' presente l'attributo 'id', allora elimino
    // il parametro il cui progressivo e' specificato dall'attributo stesso
    if (request.getAttribute("parametroDaCancellare") != null)
      contenitore.eliminaParametro(Integer.parseInt((String) request.getAttribute("parametroDaCancellare")));

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    request.setAttribute("tab", CambiaTabAction.CODICE_TAB_PARAMETRI);

    if (logger.isDebugEnabled()) {
      logger.debug("insert: fine metodo");
    }

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action modifica
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModifica() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
  }
  
  /**
   * Action per l'update di un filtro in una ricerca
   */
  public ActionForward modifica(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug("modifica: inizio metodo");
    }

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    ParametroRicercaForm parametro = (ParametroRicercaForm) form;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
        session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    int progressivo = Integer.parseInt(parametro.getProgressivo());

    ParametroRicercaForm parametroRicercaForm = contenitore.estraiParametro(progressivo);

    // copia degli attributi modificabili a video
    parametroRicercaForm.setCodiceParametro(parametro.getCodiceParametro());
    parametroRicercaForm.setDescrizione(parametro.getDescrizione());
    parametroRicercaForm.setNome(parametro.getNome());
    parametroRicercaForm.setTipoParametro(parametro.getTipoParametro());
     
    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    request.setAttribute("tab", CambiaTabAction.CODICE_TAB_PARAMETRI);
    this.setMenuTab(request);

    if (logger.isDebugEnabled()) {
      logger.debug("modifica: fine metodo");
    }

    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menu a tab in fase di visualizzazione del dettaglio della lista degli
   * ordinamenti.
   * 
   * @param request
   */
  private void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiDettaglioGruppo.NOME_GESTORE_TAB);

    boolean isInSessione = true;
    if (obj == null) {
      isInSessione = false;
      obj = new GestioneTab();
    }

    GestioneTab gestoreTab = (GestioneTab) obj;
    gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_PARAMETRI);
    gestoreTab.setTabSelezionabili(new String[] {
        CostantiGenRicerche.TAB_DATI_GENERALI,
        CostantiGenRicerche.TAB_GRUPPI,
        CostantiGenRicerche.TAB_SQL });

    if (!isInSessione) {
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
    }
  }
  
}