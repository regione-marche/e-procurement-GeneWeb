/*
 * Created on 21-ago-2006
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.parametro;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractDispatchActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.CambiaTabAction;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.filtro.FiltroRicercaForm;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * DispatchAction per la gestione di tutte le azioni che possono essere lanciate
 * dalla pagina 'Lista Parametri'
 * 
 * @author Luca.Giacomazzo
 */
public class ListaParametriRicercaAction extends
    AbstractDispatchActionBaseGenRicerche {

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi alle
   * ricerche
   */
  private TabellatiManager tabellatiManager;

  /**
   * @return Ritorna tabellatiManager.
   */
  public TabellatiManager getTabellatiManager() {
    return this.tabellatiManager;
  }

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(ListaParametriRicercaAction.class);

  /**
   * Funzione che restituisce le opzioni per accedere alla action elimina
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniElimina() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  public ActionForward elimina(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("elimina: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    String id = request.getParameter("id");

    ParametroRicercaForm parametro = contenitore.estraiParametro(Integer.parseInt(id));

    // Cerco fra la lista dei filtri, quali fa uso del parametro in
    // cancellazione
    Vector<String> elencoFiltriDaCancellare = new Vector<String>();
    Vector<FiltroRicercaForm> elencoFiltri = contenitore.getElencoFiltri();
    Iterator<FiltroRicercaForm> iter = elencoFiltri.iterator();
    while (iter.hasNext()) {
      FiltroRicercaForm filtro = (FiltroRicercaForm) iter.next();
      if (parametro.getCodiceParametro().equals(filtro.getParametroConfronto()))
        elencoFiltriDaCancellare.add(filtro.getProgressivo());
    }
    // Cancellazione dalla sessione dei filtri che usano il parametro da
    // cancellare
    if (elencoFiltriDaCancellare.size() > 0) {
      String messageKey = "info.genRic.eliminaParametro.checkFiltri";
      if (logger.isDebugEnabled())
        logger.debug(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);

      for (int i = 0; i < elencoFiltriDaCancellare.size(); i++)
        contenitore.eliminaFiltro((Integer.parseInt((String) elencoFiltriDaCancellare.get(i)))
            - i);
    }

    // Cancellazione del parametro dalla sessione
    contenitore.eliminaParametro(Integer.parseInt(id));

    this.setMenuTab(request, target);
    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    request.setAttribute("tab", CambiaTabAction.CODICE_TAB_PARAMETRI);

    if (logger.isDebugEnabled()) logger.debug("elimina: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaSu
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaSu() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  public ActionForward spostaSu(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("spostaSu: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    int id = Integer.parseInt(request.getParameter("id"));
    contenitore.spostaParametro(id, id - 1);

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    request.setAttribute("tab", CambiaTabAction.CODICE_TAB_PARAMETRI);
    if (logger.isDebugEnabled()) logger.debug("spostaSu: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaGiu
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaGiu() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  public ActionForward spostaGiu(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("spostaGiu: inizio metodo");
    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    int id = Integer.parseInt(request.getParameter("id"));
    contenitore.spostaParametro(id, id + 1);

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    request.setAttribute("tab", CambiaTabAction.CODICE_TAB_PARAMETRI);
    if (logger.isDebugEnabled()) logger.debug("spostaGiu: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaInPosizioneMarcata
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaInPosizioneMarcata() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  public ActionForward spostaInPosizioneMarcata(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("spostaInPosizioneMarcata: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    String id1 = request.getParameter("id");
    String id2 = request.getParameter("idNew");
    contenitore.spostaParametro(Integer.parseInt(id1), Integer.parseInt(id2));

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    request.setAttribute("tab", CambiaTabAction.CODICE_TAB_PARAMETRI);
    if (logger.isDebugEnabled())
      logger.debug("spostaInPosizioneMarcata: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di visualizzazione del dettaglio dei Filtri di una
   * ricerca
   * 
   * @param request
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
    if (!"apriModifica".equals(target))
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiGenRicerche.TAB_DATI_GENERALI,
          CostantiGenRicerche.TAB_GRUPPI, CostantiGenRicerche.TAB_ARGOMENTI,
          CostantiGenRicerche.TAB_CAMPI, CostantiGenRicerche.TAB_JOIN,
          CostantiGenRicerche.TAB_FILTRI, CostantiGenRicerche.TAB_ORDINAMENTI,
          CostantiGenRicerche.TAB_LAYOUT});
    else
      gestoreTab.setTabSelezionabili(null);
    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }
}