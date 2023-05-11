/*
 * Created on 02-apr-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.base;

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

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.web.struts.admin.CostantiDettaglioGruppo;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.campo.CampoRicercaForm;
import it.eldasoft.gene.web.struts.genric.layout.ListaTitoliCampiRicercaAction;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;


/**
 * Ridefinizione del metodo elimina della classe ListaTitoliCampiRicercaAction per
 * una diversa gestione dell'eliminazione dell'ultimo titolo del campo presente
 * nella lista: ora devo cancellare anceh l'ultima tabella selezionata.
 * @author Luca.Giacomazzo
 */
public class ListaTitoliCampiRicercaBaseAction extends
    ListaTitoliCampiRicercaAction {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(ListaTitoliCampiRicercaBaseAction.class);
 
  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioneAcquistata()
   */
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL;
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action modifica
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModifica() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action elimina
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniElimina() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaSu
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaSu() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaGiu
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaGiu() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action spostaInPosizioneMarcata
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSpostaInPosizioneMarcata() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
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

    //  Si eliminano i filtri che fanno uso di tale campo
    Vector<Integer> filtriDaEliminare = new Vector<Integer>();
    for (int i = 0; i < contenitore.getNumeroFiltri(); i++) {
      if (contenitore.estraiFiltro(i).getAliasTabella().equals(
          campo.getAliasTabella())
          && contenitore.estraiFiltro(i).getMnemonicoCampo().equals(
              campo.getMnemonicoCampo()))
        filtriDaEliminare.add(new Integer(i));
    }
    for (int i = 0; i < filtriDaEliminare.size(); i++)
      contenitore.eliminaFiltro(((Integer) filtriDaEliminare.elementAt(i)).intValue()
          - i);
    
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

    // Se sono stati cancellati tutti i campi, allora cancello anche l'unica
    // tabella relativa ai tali campi. Infatti le ricerche base permettono di
    // estrarre campi da un'unica tabella
    if(contenitore.getNumeroCampi() == 0)
      contenitore.eliminaTabella(0);
    
    this.marcaRicercaModificata(request, contenitore.getTestata().getNome());
    this.setMenuTab(request);

    if (logger.isDebugEnabled()) logger.debug("cancella: fine metodo");

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
        CostantiGenRicerche.TAB_CAMPI, CostantiGenRicerche.TAB_FILTRI, 
        CostantiGenRicerche.TAB_ORDINAMENTI });

    if (!isInSessione)
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
  }
}