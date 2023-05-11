/*
 * Created on 23-feb-2007
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.layout;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.AbstractDispatchActionBaseGenRicerche;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.campo.CampoRicercaForm;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.io.IOException;
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
 * Action per la gestione delle operazioni sui titoli dei campi presenti
 * nella lista campi di una ricerca: le operazioni modifica, elimina, 
 * sposta su/giu' e sposta in posizione marcata hanno effetto sulla lista dei 
 * campi della ricerca contenuto nel contenitore presente in sessione  
 * 
 * @author Luca.Giacomazzo
 */
public class ListaTitoliCampiRicercaAction extends AbstractDispatchActionBaseGenRicerche {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(ListaTitoliCampiRicercaAction.class);

  /**
   * Funzione che restituisce le opzioni per accedere alla action modifica
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModifica() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  public ActionForward modifica(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("modifica: inizio metodo");

    // target di default
    String target = "apriModifica";

    //DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
    //DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
          sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    // lettura dal request del progressivo del campo da editare
    String id = request.getParameter("id");
    CampoRicercaForm campo = contenitore.estraiCampo(
        UtilityNumeri.convertiIntero(id).intValue());
    
    request.setAttribute("campoRicercaForm", campo);

    // set nel request del parameter per disabilitare la navigazione in fase di
    // editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);

    this.setMenuTab(request);

    if (logger.isDebugEnabled()) logger.debug("modifica: fine metodo");

    return mapping.findForward(target);
  }

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

    if (logger.isDebugEnabled()) logger.debug("cancella: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
          session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    String id = request.getParameter("id");
    CampoRicercaForm campo = contenitore.estraiCampo(Integer.parseInt(id));
    contenitore.eliminaCampo(Integer.parseInt(id));

    // si eliminano gli ordinamenti che fanno uso di tale campo
    Vector<Integer> ordinamentiDaEliminare = new Vector<Integer>();
    for (int i = 0; i < contenitore.getNumeroOrdinamenti(); i++) {
      if (contenitore.estraiOrdinamento(i).getAliasTabella().equals(
          campo.getAliasTabella())
          && contenitore.estraiOrdinamento(i).getMnemonicoCampo().equals(
              campo.getMnemonicoCampo()))
        ordinamentiDaEliminare.add(new Integer(i));
    }
    for (int i = 0; i < ordinamentiDaEliminare.size(); i++)
      contenitore.eliminaOrdinamento(((Integer) ordinamentiDaEliminare.elementAt(i)).intValue()
          - i);

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    this.setMenuTab(request);

    if (logger.isDebugEnabled()) logger.debug("cancella: fine metodo");

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

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    int id = Integer.parseInt(request.getParameter("id"));
    contenitore.spostaCampo(id, (id - 1));

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    this.setMenuTab(request);

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

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
            session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    int id = Integer.parseInt(request.getParameter("id"));
    contenitore.spostaCampo(id, (id + 1));

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    this.setMenuTab(request);

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
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
          session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    String id1 = request.getParameter("id");
    String id2 = request.getParameter("idNew");
    contenitore.spostaCampo(Integer.parseInt(id1), Integer.parseInt(id2));

    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    this.setMenuTab(request);

    if (logger.isDebugEnabled())
      logger.debug("spostaInPosizioneMarcata: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di visualizzazione del dettaglio dei Dati Generali di
   * una ricerca
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
    gestoreTab.setTabAttivo(CostantiGenRicerche.TAB_LAYOUT);
    gestoreTab.setTabSelezionabili(new String[] {
        CostantiGenRicerche.TAB_DATI_GENERALI, CostantiGenRicerche.TAB_GRUPPI,
        CostantiGenRicerche.TAB_ARGOMENTI, CostantiGenRicerche.TAB_CAMPI,
        CostantiGenRicerche.TAB_JOIN, CostantiGenRicerche.TAB_PARAMETRI,
        CostantiGenRicerche.TAB_FILTRI, CostantiGenRicerche.TAB_ORDINAMENTI });

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }

}