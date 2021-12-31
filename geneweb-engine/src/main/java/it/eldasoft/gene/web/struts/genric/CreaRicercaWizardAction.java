/*
 * Created on 30-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric;

import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Gruppo;
import it.eldasoft.gene.db.domain.genric.GruppoRicerca;
import it.eldasoft.gene.web.struts.genric.gruppo.GruppoForm;
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
import org.springframework.dao.DataAccessException;

/**
 * DispatchAction per la creazione e l'eliminazione di una ricerca
 * 
 * @author Francesco.DeFilippis
 */
public class CreaRicercaWizardAction extends
    AbstractDispatchActionBaseGenRicerche {

  // Forward predefiniti per il DispatchAction in linea con il file
  // struts-config.xml
  
  private static final String SUCCESS_CREA_REPORT_BASE             = "successCreaReportBase";
  private static final String SUCCESS_CREA_REPORT_AVANZATO         = "successCrea";
  private static final String SUCCESS_CREA_REPORT_CON_PROSPETTO    = "successCreaProspetto";
    
  /** Logger Log4J di classe */
  static Logger               logger                     = Logger.getLogger(DettaglioRicercaAction.class);

  /**
   * Reference alla classe di business logic per accesso ai dati relativi ai
   * gruppi
   */
  private GruppiManager       gruppiManager;

  /**
   * @param gruppiManager
   *        gruppiManager da settare internamente alla classe.
   */
  public void setGruppiManager(GruppiManager gruppiManager) {
    this.gruppiManager = gruppiManager;
  }
  
  /**
   * Reference alla classe di business logic per accesso ai dati relativi alle
   * ricerche
   */
  private RicercheManager     ricercheManager;

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
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#getOpzioneAcquistata()
   */
  protected String getOpzioneAcquistata() {
    return CostantiGenerali.OPZIONE_GEN_RICERCHE_PROFESSIONAL;
  }
 
  /**
   * Funzione che restituisce le opzioni per accedere alla action crea
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniCrea() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  public ActionForward crea(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("crea: inizio metodo");
    
    //L.G. 15/02/2007: inizio
    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitoreTMP = (ContenitoreDatiRicercaForm) 
        session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    if(contenitoreTMP != null){
      //Prima di cancellare dalla sessione l'oggetto ContenitoreDatiRicercaForm, 
      //contenente i dati della ricerca attualmente in visualizzazione, salvo in 
      //sessione l'ID della ricerca stessa per permettere di ripristinare la 
      //vecchia ricerca qualora l'utente annullasse l'operazione di creazione di
      //una nuova ricerca
      session.setAttribute(CostantiGenRicerche.ID_RICERCA_PRECEDENTE, 
          contenitoreTMP.getTestata().getId());
    }
    //L.G. 15/02/2007: fine
    
    // cancellazione di oggetti in sessione precedentemente creati
    this.cleanSession(request);

    //L.G. 27/02/2007: inizio modifica per creazione di un nuovo report
    // Ora è possibile creare 3 diversi tipi di report e quindi la creazione è
    // funzione del tipo di famiglia 
    
    // Lettura dal request del parametro per il tipo di report da creare 
    String famigliaRicerca = request.getParameter("famiglia");
    
    String target = null;
    // Set del target di in funzione della famiglia di appartenenza del report
    //da creare
    if(famigliaRicerca != null && famigliaRicerca.length() == 1)
      switch(Integer.parseInt(famigliaRicerca)){
      case CostantiGenRicerche.REPORT_BASE:
        target = CreaRicercaWizardAction.SUCCESS_CREA_REPORT_BASE;
        break;
      case CostantiGenRicerche.REPORT_AVANZATO:
        target = CreaRicercaWizardAction.SUCCESS_CREA_REPORT_AVANZATO;
        break;
      case CostantiGenRicerche.REPORT_PROSPETTO:
        target = CreaRicercaWizardAction.SUCCESS_CREA_REPORT_CON_PROSPETTO;
        break;
      }
    
    //L.G. 27/02/2007: fine modifica
    
    String messageKey = null;
    
    //L.G. 07/03/2007: modifica per implementazione ricerca con modello
    //Se si crea una ricerca di base o una ricerca avanzata, allora si crea in 
    //sessione l'oggetto ContenitoreDatiRicercaForm, altrimenti si passa alla
    //Action predisposta alla inizializzazione della pagina di creazione di una 
    //ricerca con modello
    if(Integer.parseInt(famigliaRicerca) != CostantiGenRicerche.REPORT_PROSPETTO){
      ContenitoreDatiRicercaForm contenitore = new ContenitoreDatiRicercaForm();
      contenitore.getTestata().setDisp(true);
      // la paginazione di default è quella con il maggior numero di righe, ovvero 100
      contenitore.getTestata().setRisPerPag(
          CostantiGenerali.CBX_RIS_PER_PAGINA[
              CostantiGenerali.CBX_RIS_PER_PAGINA.length - 1]);
      contenitore.getTestata().setOwner(new Integer(((ProfiloUtente)
          session.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId()));
      contenitore.getTestata().setFamiglia(new Integer(famigliaRicerca));
      contenitore.getTestata().setIdProspetto(new Integer(0));
      contenitore.getTestata().setPersonale(true);
                  
      session.setAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO, contenitore);
      try {
        // se non è prevista la gestione dei gruppi, si attribuisce il gruppo di
        // default
        if (this.bloccaGestioneGruppiDisabilitata(request, false, false)) {
          ProfiloUtente profiloUtente = (ProfiloUtente) session.getAttribute(
              CostantiGenerali.PROFILO_UTENTE_SESSIONE);
          int idGruppo = -1;
          
          if(profiloUtente.getIdGruppi() != null && profiloUtente.getIdGruppi().length > 0)
            idGruppo = profiloUtente.getIdGruppi()[0].intValue();
          
          if (idGruppo < 0){
            // se per caso tale id di default non risulta valorizzato quando
            // richiesto, allora si termina con un errore generale
            target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
            messageKey = "errors.applicazione.idGruppoDefaultNull";
            logger.error(this.resBundleGenerale.getString(messageKey));
            this.aggiungiMessaggio(request, messageKey);
          } else {
            Gruppo gruppo = this.gruppiManager.getGruppoById(idGruppo);
            // per scrupolo si controlla
            if (gruppo == null) {
              target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
              messageKey = "errors.applicazione.idGruppoDefaultNull";
              logger.error(this.resBundleGenerale.getString(messageKey));
              this.aggiungiMessaggio(request, messageKey);
            } else {
              GruppoRicerca gruppoRicerca = new GruppoRicerca(gruppo);
              gruppoRicerca.setAssociato(true);
              GruppoForm gruppoForm = new GruppoForm(gruppoRicerca);
              contenitore.aggiungiGruppo(gruppoForm);
            }
          }
        }
        session.removeAttribute(CostantiGenerali.NOME_OGGETTO_SESSION);
      } catch (DataAccessException e) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.database.dataAccessException";
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
      } catch (Throwable t) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.applicazione.inaspettataException";
        logger.error(this.resBundleGenerale.getString(messageKey), t);
        this.aggiungiMessaggio(request, messageKey);
      }
    }
    
    if (logger.isDebugEnabled()) logger.debug("crea: fine metodo");

    return mapping.findForward(target);
  }

  
}