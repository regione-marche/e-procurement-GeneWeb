/*
 * Created on 04-mag-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.base.wizard;

import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.web.struts.genric.AbstractDispatchActionBaseGenRicercheBase;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.gene.web.struts.genric.campo.CampoRicercaForm;
import it.eldasoft.gene.web.struts.genric.ordinamento.OrdinamentoRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

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
 * DispatchAction la memorizzazione e la modifica dell'ordinamento in sessione
 * della ricerca base
 * 
 * @author Luca.Giacomazzo
 */
public class OrdinamentiAction extends AbstractDispatchActionBaseGenRicercheBase {

  private final String SUCCESS_ADD           = "successAdd";
  private final String SUCCESS_SALVA         = "successSalva";
  private final String SUCCESS_ANNULLA_LISTA = "successAnnullaLista";
  private final String SUCCESS_FINE          = "successFine";
  private final String SUCCESS_ELIMINA       = "successElimina";
  private final String SUCCESS_MODIFICA      = "successModifica";
  
  static Logger        logger                = Logger.getLogger(OrdinamentiAction.class);

  /**
   * Funzione che restituisce le opzioni per accedere al metodo salvaOrdinamento
   * della DispatchAction
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSalvaOrdinamento() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }

  public ActionForward salvaOrdinamento(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("salvaOrdinamento: inizio metodo");

    // target di default
    String target = SUCCESS_SALVA;

    String appoggio = this.eseguiInsert(form, request);
    if (appoggio != null) target = appoggio;

    // Gestione delle voci di avanzamento della creazione guidata
    //AvanzamentoWizard.gestioneVociAvanzamento(request, CostantiWizard.CODICE_PAGINE_ORDINAMENTI);
    
    if (logger.isDebugEnabled()) logger.debug("salvaOrdinamento: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere al metodo
   * annullaListaOrdinamenti della DispatchAction
   * 
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniAnnullaListaOrdinamenti() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }

  public ActionForward annullaListaOrdinamenti(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled())
      logger.debug("annullaListaOrdinamenti: inizio metodo");

    // target di default
    String target = SUCCESS_ANNULLA_LISTA;

    String pageFrom = request.getParameter("pageFrom");
    if (pageFrom != null) {
      target = SUCCESS_FINE;
      request.setAttribute("pageFrom", pageFrom);
    }

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
          session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    // Cancello dalla sessione la lista dei filtri esistente
    for (int i = contenitore.getNumeroOrdinamenti() - 1; i >= 0; i--)
      contenitore.eliminaOrdinamento(i);

    // Gestione delle voci di avanzamento della creazione guidata
    //if(target.equals(SUCCESS_ANNULLA_LISTA))
      //AvanzamentoWizard.gestioneVociAvanzamento(request, CostantiWizard.CODICE_PAGINE_ORDINAMENTI);
    
    if (logger.isDebugEnabled()) logger.debug("annullaListaOrdinamenti: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere al metodo elimina 
   * della DispatchAction
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniElimina(){
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
  
  public ActionForward elimina(ActionMapping mapping, ActionForm form, 
      HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {

    if(logger.isDebugEnabled()) logger.debug("Elimina: inizio metodo");
    
    // target di default
    String target = SUCCESS_ELIMINA;
    
    int idOrdinamento = new Integer(request.getParameter("id")).intValue();
    
    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
            session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    
    if(contenitore.getNumeroOrdinamenti() > idOrdinamento)
      contenitore.eliminaOrdinamento(idOrdinamento);
    
    if(contenitore.getNumeroOrdinamenti() == 0)
      target = SUCCESS_SALVA;

    // Gestione delle voci di avanzamento della creazione guidata
    // AvanzamentoWizard.gestioneVociAvanzamento(request, CostantiWizard.CODICE_PAGINE_ORDINAMENTI);
    
    if(logger.isDebugEnabled()) logger.debug("Elimina: fine metodo");
    
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere al metodo modifica
   * della DispatchAction
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModifica(){
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_BASE);
  }
  
  public ActionForward modifica(ActionMapping mapping, ActionForm form, 
      HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {

    if(logger.isDebugEnabled()) logger.debug("Modifica: inizio metodo");
    
    // target di default
    String target = SUCCESS_ADD;
    
    int idOrdinamento = new Integer(request.getParameter("id")).intValue();
    
    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
            session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    
    OrdinamentoRicercaForm ordinamentoRicerca = (OrdinamentoRicercaForm) 
            contenitore.getElencoOrdinamenti().get(idOrdinamento);
    request.setAttribute("ordinamentoRicercaForm", ordinamentoRicerca);
    
    Vector<CampoRicercaForm> elencoCampiForm = contenitore.getElencoCampi();
    
    if(elencoCampiForm.size() > 0)
      request.setAttribute("elencoCampi", elencoCampiForm);
    
    // Gestione delle voci di avanzamento della creazione guidata
    // AvanzamentoWizard.gestioneVociAvanzamento(request, CostantiWizard.CODICE_PAGINE_ORDINAMENTI);
    
    if(logger.isDebugEnabled()) logger.debug("Modifica: fine metodo");
    return mapping.findForward(target);
  }
  
  /**
   * Metodo che effettua l'effettivo insert di un nuovo ordinamento in sessione,
   * (effettuando i controlli del caso, attualmente nessuno).
   * 
   * @param form
   * @param request
   */
  private String eseguiInsert(ActionForm form, HttpServletRequest request) {
    String target = null;

    OrdinamentoRicercaForm ordinamentoRicercaForm = (OrdinamentoRicercaForm) form;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
            session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    // Set degli attributi dell'oggetto OrdinamentoRicercaForm necessari ad
    // individuare l'unica tabella della ricerca base
    TabellaRicercaForm tabella = (TabellaRicercaForm) contenitore.getElencoArgomenti().get(
        0);
    ordinamentoRicercaForm.setMnemonicoTabella(tabella.getMnemonicoTabella());
    ordinamentoRicercaForm.setAliasTabella(tabella.getAliasTabella());
    ordinamentoRicercaForm.setDescrizioneTabella(tabella.getDescrizioneTabella());
    ordinamentoRicercaForm.setDescrizioneCampo(DizionarioCampi.getInstance().get(
        ordinamentoRicercaForm.getMnemonicoCampo()).getDescrizione());

    // Se l'oggetto ordinamentoRicercaForm non settato il campo progressivo, allora
    // si e' nel caso di inserimento di un nuovo criterio di ordinamento.
    // Altrimenti si e' nel caso di modifica di un criterio di ordinamento
    // esistente.
    if(ordinamentoRicercaForm.getProgressivo().length() == 0)
      contenitore.aggiungiOrdinamento(ordinamentoRicercaForm);
    else {
      OrdinamentoRicercaForm ordinamentoSessione = (OrdinamentoRicercaForm) 
        contenitore.getElencoOrdinamenti().get(new Integer(ordinamentoRicercaForm.getProgressivo()).intValue());
      ordinamentoSessione.setMnemonicoCampo(ordinamentoRicercaForm.getMnemonicoCampo());
      ordinamentoSessione.setOrdinamento(ordinamentoRicercaForm.getOrdinamento());
      ordinamentoSessione.setDescrizioneCampo(DizionarioCampi.getInstance().get(
          ordinamentoRicercaForm.getMnemonicoCampo()).getDescrizione());
      
      // Cambio del target per tornare alla pagina di riepilogo dei criteri di 
      // ordinamento dopo aver modificato un ordinamento esistente
      target = SUCCESS_MODIFICA;
    }
    
    return target;
  }
}