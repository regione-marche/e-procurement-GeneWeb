/*
 * Created on 22-mag-2007
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
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiRicerca;
import it.eldasoft.gene.web.struts.genric.base.wizard.CostantiWizard;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action per caricare in sessione una versione modificata di una ricerca 
 * (avanzata o base) a partire dall'id della ricerca stessa, per permettere la 
 * creazione di un nuovo report a partire da un esistente.  
 * 
 * @author Luca.Giacomazzo
 */
public class CaricaRicercaAction extends AbstractActionBaseGenRicerche {

  private static final String SUCCESS_RICERCA_BASE     = "successBase";
  private static final String SUCCESS_RICERCA_AVANZATA = "successAvanzata";
  
  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(CaricaRicercaAction.class);
  
  /**
   * Reference alla classe di business logic per accesso ai dati relativi alle
   * ricerche
   */
  private RicercheManager     ricercheManager;

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action inizializza
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniInizializza() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  public ActionForward runAction(ActionMapping mapping, ActionForm form, 
      HttpServletRequest request, HttpServletResponse response){
    
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    String target = null;
    
    //Leggo dal request il parameter 'idRicerca' il quale rappresenta l'id
    //della ricerca da caricarein sessione
    String idRicerca = request.getParameter("idRicerca");
    
    //Carica ricerca da DB in sessione:
    ContenitoreDatiRicerca contenitoreHelper = 
        this.ricercheManager.getRicercaByIdRicerca(Integer.parseInt(idRicerca));
    ContenitoreDatiRicercaForm contenitore = 
        new ContenitoreDatiRicercaForm(contenitoreHelper);
    
    //Modifiche alla ricerca: 
    //metto a null il nome della ricerca
    contenitore.getTestata().setNome(null);
    contenitore.getTestata().setDescrizione(null);
    //cancellazione dell'id della ricerca in tutti gli oggetti del contenitore
    contenitore.setIdRicerca(null);
    //set del report a personale
    contenitore.getTestata().setPersonale(true);
    contenitore.getTestata().setDisp(true);
    //set dell'owner 
    contenitore.getTestata().setOwner(new Integer(
        ((ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId()));
    //cancellazione dell'eventuale lista dei gruppi 
    if (contenitore.getNumeroGruppi() > 0){
      for(int i = contenitore.getNumeroGruppi()-1; i >= 0; i--)
        contenitore.eliminaGruppo(i);
    }
    //Rimozione dalla sessione del contenitore precaricato dalla metodo crea 
    //della classe CreaEliminaRicercaAction
    request.getSession().removeAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    //Inserimento in sessione della ricerca caricata e modificata nei parametri
    //quali nome, id, personale. ecc...
    request.getSession().setAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO, contenitore);
    this.marcaRicercaModificata(request);
    
    switch(contenitore.getTestata().getFamiglia().intValue()){
    case CostantiGenRicerche.REPORT_BASE:
      target = SUCCESS_RICERCA_BASE;
      request.setAttribute("pageTo", CostantiWizard.CODICE_PAGINA_ARGOMENTO);
      break;
    case CostantiGenRicerche.REPORT_AVANZATO:
      target = SUCCESS_RICERCA_AVANZATA;
      break;
    }

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }
  
}