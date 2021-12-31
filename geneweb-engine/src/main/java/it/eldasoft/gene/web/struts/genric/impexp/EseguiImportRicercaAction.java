/*
 * Created on 23-ago-2007
 * 
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.impexp;

import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.bl.genmod.CompositoreException;
import it.eldasoft.gene.bl.genmod.GestioneFileModelloException;
import it.eldasoft.gene.bl.genric.ProspettoManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Gruppo;
import it.eldasoft.gene.db.domain.admin.RicercaGruppo;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiImport;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiProspetto;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiRicerca;
import it.eldasoft.gene.db.domain.genric.DatiGenProspetto;
import it.eldasoft.gene.db.domain.genric.DatiGenRicerca;
import it.eldasoft.gene.db.domain.genric.FiltroRicerca;
import it.eldasoft.gene.db.domain.genric.GruppoRicerca;
import it.eldasoft.gene.db.domain.genric.TrovaRicerche;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.gruppo.GruppiRicercaForm;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.DatoBase64;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.sql.comp.SqlElementoCondizione;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Action per l'operazione di insert/update della definizione di un report
 * contenuta nel file xml specificato nella prima pagina del wizard di import 
 * 
 * @author Luca.Giacomazzo
 */
public class EseguiImportRicercaAction extends ActionBaseNoOpzioni {

  static Logger logger = Logger.getLogger(EseguiImportRicercaAction.class);

  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }
  
  /**
   * Reference al manager per l'accesso alla tabella W_GRUPPI e W_GRPRIC
   */
  private GruppiManager gruppiManager;

  /**
   * Reference al manager per l'accesso alla tabella W_RICERCHE
   */
  private RicercheManager ricercheManager;
  
  /**
   * Reference al manager per l'accesso alla tabella W_RICERCHE, W_MODELLI
   */
  private ProspettoManager prospettoManager;
  
  /**
   * @param gruppiManager gruppiManager da settare internamente alla classe.
   */
  public void setGruppiManager(GruppiManager gruppiManager) {
    this.gruppiManager = gruppiManager;
  }

  /**
   * @param prospettoManager prospettoManager da settare internamente alla classe.
   */
  public void setProspettoManager(ProspettoManager prospettoManager) {
    this.prospettoManager = prospettoManager;
  }
  
  /**
   * @param ricercheManager ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
    
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    String target = CostantiGeneraliStruts.FORWARD_OK;
    
    String pageFrom = request.getParameter("pageFrom");
    if(pageFrom == null || pageFrom.length() == 0)
      pageFrom = (String) request.getAttribute("pageFrom");
    
    ContenitoreDatiImport contenitore = (ContenitoreDatiImport) 
      request.getSession().getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    
    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    
    String tmpTarget = null;

    try {
      if(contenitore.getContenitoreDatiRicerca() != null){
        tmpTarget = this.eseguiImportReport(request, contenitore,
            profiloUtente.getId(), (String)
            request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO));  
      } else {
        tmpTarget =  this.eseguiImportProspetto(request, contenitore,
            profiloUtente.getId(), (String)
            request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO));
      }
    } catch (Throwable t){
      tmpTarget = CostantiWizard.ERROR_IMPORT_REPORT;
      String messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    }

    // In caso di errore ritorna alla pagina da cui e' stato richiamato il 
    // salvataggio dei dati
    if(tmpTarget != null) {
      request.setAttribute("pageTo", pageFrom);
      target = tmpTarget;
    }
    
    // set del messaggio che l'importazione è avvenuta con successo in caso positivo
    if (CostantiGeneraliStruts.FORWARD_OK.equals((target))) {
      this.aggiungiMessaggio(request, "info.genric.import.success");

      // Cancellazione dalla sessione del contenitore dati import qualsiasi sia
      // l'esito dell'operazione di insert del report da importare
      request.getSession().removeAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    }
    
    // set nel request del parameter per disabilitare la navigazione
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);
    
    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Metodo che esegue l'insert/update su DB di una ricerca base o avanzata
   * 
   * @param request
   * @param contenitoreDatiImport
   * @param idUtenteImportatore
   * @param codApp
   * @return Ritorna null se l'operazione di insert va a buon fine, altrimenti
   *         ritorna il target di destinazione 
   */
  private String eseguiImportReport(HttpServletRequest request,
      ContenitoreDatiImport contenitoreDatiImport, int idUtenteImportatore,
      String codApp){
    String target = null;
    String messageKey = null;
    
    boolean pubblicaReport = contenitoreDatiImport.getPubblicaReport();
    boolean esisteReport = contenitoreDatiImport.getEsisteReport();
    String tipoImport = contenitoreDatiImport.getTipoImport();
    String nuovoTitoloReport = contenitoreDatiImport.getNuovoTitoloReport();
    GruppiRicercaForm gruppiRicercaForm = contenitoreDatiImport.getGruppiRicercaForm();
    ContenitoreDatiRicerca contenitore = contenitoreDatiImport.getContenitoreDatiRicerca();

    // A prescindere dalla pubblicazione o meno del report in importazione
    // cancello l'oggetto contenente l'associazione report-gruppi, perchè
    // va comunque ridefinita
    contenitore.getElencoGruppi().removeAllElements();
    
    // Effettuo una correzione dei campi notCaseSensitive per i filtri, se si
    // effettua un import a partire da un export con una versione che non
    // prevede ancora la gestione di tale campo
    for (int i = 0; i < contenitore.getNumeroFiltri(); i++) {
      FiltroRicerca filtro = contenitore.estraiFiltro(i);
      // nel caso arrivi il flag notCaseSensitive allora lo forzo a 0 per gli
      // operatori di confronto binari
      if (filtro.getNotCaseSensitive() == null
          && (SqlElementoCondizione.STR_OPERATORE_CONFRONTO_UGUALE.equals(filtro.getOperatore())
              || SqlElementoCondizione.STR_OPERATORE_CONFRONTO_DIVERSO.equals(filtro.getOperatore())
              || SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MAGGIORE.equals(filtro.getOperatore())
              || SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MAGGIORE_UGUALE.equals(filtro.getOperatore())
              || SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MINORE.equals(filtro.getOperatore())
              || SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MINORE_UGUALE.equals(filtro.getOperatore())
              || SqlElementoCondizione.STR_OPERATORE_CONFRONTO_IN.equals(filtro.getOperatore())
              || SqlElementoCondizione.STR_OPERATORE_CONFRONTO_NOT_IN.equals(filtro.getOperatore())
              || SqlElementoCondizione.STR_OPERATORE_CONFRONTO_MATCH.equals(filtro.getOperatore()) 
              || SqlElementoCondizione.STR_OPERATORE_CONFRONTO_NOT_MATCH.equals(filtro.getOperatore()))) {
        filtro.setNotCaseSensitive(new Integer(0));
      }
    }

    DatiGenRicerca datiGenerali = contenitore.getDatiGenerali();
    
    try {
      if (pubblicaReport){
        contenitore.getDatiGenerali().setDisp(1);
        contenitore.getDatiGenerali().setPersonale(0);
          
        // Nel caso la gestione dei gruppi sia disabilitata e visto che l'utente
        // ha scelto di pubblicare, allora inserisco nell'elenco dei gruppi il 
        // gruppo di default
        if (this.bloccaGestioneGruppiDisabilitata(request, false, false)) {
          ProfiloUtente profiloUtente = (ProfiloUtente)
              request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
          int idGruppo = -1;
          
          if(profiloUtente.getIdGruppi() != null && profiloUtente.getIdGruppi().length > 0){
            idGruppo = profiloUtente.getIdGruppi()[0].intValue();
            Gruppo gruppoDefault = this.gruppiManager.getGruppoById(idGruppo);
            contenitore.aggiungiGruppo(new GruppoRicerca(gruppoDefault));
          } else {
            // se per caso tale id di default non risulta valorizzato quando
            // richiesto, allora si termina con un errore generale
            target = CostantiWizard.ERROR_IMPORT_REPORT;
            messageKey = "errors.applicazione.idGruppoDefaultNull";
            logger.error(this.resBundleGenerale.getString(messageKey));
            this.aggiungiMessaggio(request, messageKey);
          }
        } else {
          // In questo caso l'utente ha scelto di pubblicare ed definito 
          // l'associazione report-gruppi (sempre che il profilo in uso
          // abbia almeno un gruppo definito)
  
          // lista di tutti i gruppi esistenti ordinata per nome
          List<?> listaGruppi = this.gruppiManager.getGruppiOrderByNome((String)
              request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO));
            
          if (gruppiRicercaForm != null && gruppiRicercaForm.getIdGruppo() != null) {
            Set<String> insiemeGruppiAssociati = new HashSet<String>();
          
            for (int i = 0; i < gruppiRicercaForm.getIdGruppo().length; i++)
              insiemeGruppiAssociati.add((gruppiRicercaForm.getIdGruppo()[i]));
    
            for (int i = 0; i < listaGruppi.size(); i++) {
              if (insiemeGruppiAssociati.contains("" + ((Gruppo) listaGruppi.get(i)).getIdGruppo()))
                contenitore.aggiungiGruppo(new GruppoRicerca((Gruppo) listaGruppi.get(i)));
            }
          }
        }
      } else {
        // In questo caso bisogna settare nell'oggetto ContenitoreDatiRicerca 
        // costruito a partire dal file XML le informazioni su report personale,
        // report pubblicato, associazione report-gruppi della ricerca esistente
        // su DB
        if (tipoImport.equals(CostantiWizard.IMPORT_SOVRASCRIVI_PARZIALE)) {
          try {
            TrovaRicerche trovaRicerche = new TrovaRicerche();
            trovaRicerche.setNomeRicerca(datiGenerali.getNome());
            trovaRicerche.setCodiceApplicazione(codApp);
            trovaRicerche.setProfiloOwner(datiGenerali.getProfiloOwner());
      
            List<?> listaRicerche = this.ricercheManager.getRicerche(trovaRicerche, true);
            RicercaGruppo datiGenRicercaInDb = (RicercaGruppo) listaRicerche.get(0);
                     
            int idRicercaEsistente = datiGenRicercaInDb.getIdRicerca();
            ContenitoreDatiRicerca contenitoreDatiRicercaEsistente =
                this.ricercheManager.getRicercaByIdRicerca(idRicercaEsistente);
    
            datiGenerali.setDisp(
                contenitoreDatiRicercaEsistente.getDatiGenerali().getDisp());
            datiGenerali.setPersonale(
                contenitoreDatiRicercaEsistente.getDatiGenerali().getPersonale());
            datiGenerali.setOwner(
                contenitoreDatiRicercaEsistente.getDatiGenerali().getOwner());
            contenitore.setElencoGruppi(
                contenitoreDatiRicercaEsistente.getElencoGruppi());
            contenitore.getDatiGenerali().setOwner(
                contenitoreDatiRicercaEsistente.getDatiGenerali().getOwner());
          } catch(SqlComposerException sc){
            target = CostantiWizard.ERROR_IMPORT_REPORT;
            messageKey = sc.getChiaveResourceBundle();
            logger.error(this.resBundleGenerale.getString(messageKey), sc);
            this.aggiungiMessaggio(request, messageKey);
          }
        } else {
          // SS 01/10/2008
          // il report, anche se rimane personale, deve essere disponibile,
          // altrimenti non si riesce a listare se l'utente non è un
          // amministratore
          contenitore.getDatiGenerali().setDisp(1);
          contenitore.getDatiGenerali().setPersonale(1);
        }
      }
      if (esisteReport) {
        if(tipoImport.equals(CostantiWizard.IMPORT_SOVRASCRIVI_ESISTENTE) ||
           tipoImport.equals(CostantiWizard.IMPORT_SOVRASCRIVI_PARZIALE)) {
          TrovaRicerche trovaRicerche = new TrovaRicerche();
          trovaRicerche.setNomeRicerca(datiGenerali.getNome());
          trovaRicerche.setCodiceApplicazione(codApp);
          trovaRicerche.setProfiloOwner(datiGenerali.getProfiloOwner());

          List<?> listaRicerche = this.ricercheManager.getRicerche(trovaRicerche, true);
          RicercaGruppo datiGenRicercaInDb = (RicercaGruppo) listaRicerche.get(0);
          
          int idRicercaEsistente = datiGenRicercaInDb.getIdRicerca();
          ContenitoreDatiRicerca contenitoreDatiRicercaEsistente =
            this.ricercheManager.getRicercaByIdRicerca(idRicercaEsistente);
          
          // controllo, dato che si tratta di una ricerca esistente, il codice
          // di pubblicazione report nel web: se specificato nel dato in import
          // non deve essere utilizzato da altri report, altrimenti va resettato
          if (contenitore.getDatiGenerali().getCodReportWS() != null) {
            Integer idRicercaCodReport = this.ricercheManager.getIdRicercaByCodReportWS(contenitore.getDatiGenerali().getCodReportWS());
            if (idRicercaCodReport != null
                && idRicercaCodReport.intValue() != idRicercaEsistente) {
              contenitore.getDatiGenerali().setCodReportWS(null);
              messageKey = "warnings.genRic.import.uniqueCodReportWS";
              logger.warn(this.resBundleGenerale.getString(messageKey));
              this.aggiungiMessaggio(request, messageKey);
            }
          }
          
          // Aggiornamento di idRicerca con quello del report esistente
          contenitore.setIdRicerca(idRicercaEsistente);
          // Aggiornamento del codApp della ricerca da importare
          datiGenerali.setCodApp(codApp);
          // Aggiornamento dell'owner del report con l'owner del report presente
          // nella base dati
          datiGenerali.setOwner(contenitoreDatiRicercaEsistente.getDatiGenerali().getOwner());
          
          this.ricercheManager.updateRicerca(contenitore);
        } else if(tipoImport.equals(CostantiWizard.IMPORT_INSERT_CON_NUOVO_TITOLO)) {
          // Cambio il titolo al report che devo importare 
          datiGenerali.setNome(nuovoTitoloReport);

          // controllo, dato che si tratta di una ricerca esistente, il codice
          // di pubblicazione report nel web: se specificato nel dato in import
          // non deve essere utilizzato da altri report, altrimenti va resettato
          if (contenitore.getDatiGenerali().getCodReportWS() != null) {
            Integer idRicercaCodReport = this.ricercheManager.getIdRicercaByCodReportWS(
                contenitore.getDatiGenerali().getCodReportWS());
            if (idRicercaCodReport != null) {
              contenitore.getDatiGenerali().setCodReportWS(null);
              messageKey = "warnings.genRic.import.uniqueCodReportWS";
              logger.warn(this.resBundleGenerale.getString(messageKey));
              this.aggiungiMessaggio(request, messageKey);
            }
          }

          this.insertReport(contenitore, codApp, idUtenteImportatore);
        }
      } else {
        // controllo, dato che si tratta di una ricerca esistente, il codice
        // di pubblicazione report nel web: se specificato nel dato in import
        // non deve essere utilizzato da altri report, altrimenti va resettato
        if (contenitore.getDatiGenerali().getCodReportWS() != null) {
          Integer idRicercaCodReport = this.ricercheManager.getIdRicercaByCodReportWS(
              contenitore.getDatiGenerali().getCodReportWS());
          if (idRicercaCodReport != null) {
            contenitore.getDatiGenerali().setCodReportWS(null);
            messageKey = "warnings.genRic.import.uniqueCodReportWS";
            logger.warn(this.resBundleGenerale.getString(messageKey));
            this.aggiungiMessaggio(request, messageKey);
          }
        }

        this.insertReport(contenitore, codApp, idUtenteImportatore);
      }
    } catch (DataIntegrityViolationException e) {
      target = CostantiWizard.ERROR_IMPORT_REPORT;
      messageKey = "errors.genRic.salvaRicerca.vincoloUnique";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (DataAccessException e) {
      target = CostantiWizard.ERROR_IMPORT_REPORT;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiWizard.ERROR_IMPORT_REPORT;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }
    
    return target;
  }

  /**
   * Effettua l'insert del report, modificando prima codApp e l'owner
   * del report stesso
   * 
   * @param contenitoreDatiRicerca
   * @param codApp
   * @param idUtenteOwner
   * @param datiGenerali
   * @return Ritorna idRicerca del report appena inserito, null altrimenti 
   */
  private void insertReport(ContenitoreDatiRicerca contenitoreDatiRicerca,
      String codApp, int idUtenteOwner) {
    
    // Aggiornamento del codApp della ricerca da importare
    contenitoreDatiRicerca.getDatiGenerali().setCodApp(codApp);
    // Aggiornamento dell'owner del report
    contenitoreDatiRicerca.getDatiGenerali().setOwner(new Integer(idUtenteOwner));
    this.ricercheManager.insertRicerca(contenitoreDatiRicerca);
  }

  /**
   * Metodo che esegue l'insert/update su DB di una ricerca con modello
   * 
   * @param request
   * @param contenitoreDatiImport
   * @param idUtenteImportatore
   * @param codApp
   * @return Ritorna null se l'operazione di insert va a buon fine, altrimenti
   *         ritorna il target di destinazione
   */ 
  private String eseguiImportProspetto(HttpServletRequest request,
      ContenitoreDatiImport contenitoreDatiImport, int idUtenteImportatore, 
      String codApp){
    
    /* Rispetto al metodo eseguiImportReport questo metodo differisce per
     * l'implementazione dell'update del report con modello esistente nella
     * base dati. 
     * Infatti la gestione di un report con modello non sfrutta la copia
     * temporanea del report stesso presente in sessione, quindi ogni modifica 
     * ha effetto diretto sulla base dati e sul modello ad esso associato.
     * Per evitare complicazioni nell'implementazione dell'operazioni di
     * roll-back in seguito ad una eccezione durante l'operazione di update di 
     * un report con modello (dati generali, modello rtf, associazione
     * report-gruppi) e parametri) l'operazione di update di un report con
     * prospetto è stato suddivisa in due operazioni:
     * - modifica del nome al report originale
     * - insert su db di un nuovo report con modello e del relativo modello
     * - cancellazione del report originale
     */
    
    String target = null;
    Integer result = null;

    boolean pubblicaReport = contenitoreDatiImport.getPubblicaReport();
    boolean esisteReport = contenitoreDatiImport.getEsisteReport();
    String tipoImport = contenitoreDatiImport.getTipoImport();
    String nuovoTitoloReport = contenitoreDatiImport.getNuovoTitoloReport();
    GruppiRicercaForm gruppiRicercaForm = contenitoreDatiImport.getGruppiRicercaForm();
    ContenitoreDatiProspetto contenitoreDatiProspetto =  contenitoreDatiImport.getContenitoreDatiProspetto();
    
    // A prescindere dalla pubblicazione o meno del report in importazione
    // cancello l'oggetto contenente l'associazione report-gruppi, perchè va
    // comunque ridefinita
    contenitoreDatiProspetto.getElencoGruppi().removeAllElements();

    DatiGenRicerca datiGenerali =
          contenitoreDatiProspetto.getDatiGenProspetto().getDatiGenRicerca();
    DatiModello datiModello =
          contenitoreDatiProspetto.getDatiGenProspetto().getDatiModello();
    datiGenerali.setProfiloOwner((String)request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO));
    datiModello.setProfiloOwner((String)request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO));
    
    try {
      if(pubblicaReport){
        datiGenerali.setDisp(1);
        datiGenerali.setPersonale(0);
  
        // Nel caso la gestione dei gruppi sia disabilitata e visto che l'utente
        // ha scelto di pubblicare, allora inserisco nell'elenco dei gruppi il 
        // gruppo di default
        if(this.bloccaGestioneGruppiDisabilitata(request, false, false)){
          ProfiloUtente profiloUtente = (ProfiloUtente)
              request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
          int idGruppo = -1;
          
          if(profiloUtente.getIdGruppi() != null && profiloUtente.getIdGruppi().length > 0){
            idGruppo = profiloUtente.getIdGruppi()[0].intValue();
            
            Gruppo gruppoDefault = this.gruppiManager.getGruppoById(idGruppo);
            contenitoreDatiProspetto.aggiungiGruppo(new GruppoRicerca(gruppoDefault));
          } else {
            // se per caso tale id di default non risulta valorizzato quando
            // richiesto, allora si termina con un errore generale
            target = CostantiWizard.ERROR_IMPORT_REPORT;
            String messageKey = "errors.applicazione.idGruppoDefaultNull";
            logger.error(this.resBundleGenerale.getString(messageKey));
            this.aggiungiMessaggio(request, messageKey);
          }
        } else {
          // In questo caso l'utente, avendo scelto di pubblicare, ha potuto 
          // definire l'associazione report-gruppi (sempre che il profilo in uso
          // abbia almeno un gruppo definito)
          
          // lista di tutti i gruppi esistenti ordinata per nome
          List<?> listaGruppi = this.gruppiManager.getGruppiOrderByNome((String)
              request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO));
          
          if(gruppiRicercaForm != null && gruppiRicercaForm.getIdGruppo() != null){
            Set<String> insiemeGruppiAssociati = new HashSet<String>();
          
            for (int i = 0; i < gruppiRicercaForm.getIdGruppo().length; i++)
              insiemeGruppiAssociati.add((gruppiRicercaForm.getIdGruppo()[i]));
      
            for (int i = 0; i < listaGruppi.size(); i++) {
              if(insiemeGruppiAssociati.contains("" + ((Gruppo) listaGruppi.get(i)).getIdGruppo()))
                contenitoreDatiProspetto.aggiungiGruppo(
                    new GruppoRicerca((Gruppo) listaGruppi.get(i)));
            }
          }
        }
      } else {
        // In questo caso bisogna settare nell'oggetto ContenitoreDatiProspetto 
        // costruito a partire dal file XML le informazioni su report personale,
        // report pubblicato, associazione report-gruppi della ricerca esistente
        // su DB
        if(tipoImport.equals(CostantiWizard.IMPORT_SOVRASCRIVI_PARZIALE)){
          try {
            TrovaRicerche trovaRicerche = new TrovaRicerche();
            trovaRicerche.setNomeRicerca(datiGenerali.getNome());
            trovaRicerche.setCodiceApplicazione(codApp);
            trovaRicerche.setProfiloOwner(datiGenerali.getProfiloOwner());
      
            List<?> listaRicerche = this.ricercheManager.getRicerche(trovaRicerche, true);
            RicercaGruppo ricercaDB = (RicercaGruppo) listaRicerche.get(0);
                     
            int idRicercaEsistente = ricercaDB.getIdRicerca();
            List<?> listaGruppiProspetto = gruppiManager.getGruppiByIdRicerca(idRicercaEsistente);
            Vector<GruppoRicerca> tmpElencoGruppi = new Vector<GruppoRicerca>();
            for(int i=0; i < listaGruppiProspetto.size(); i++)
              tmpElencoGruppi.addElement(
                  new GruppoRicerca((Gruppo) listaGruppiProspetto.get(i)));
            
            DatiGenRicerca datiGenRicercaDB = this.ricercheManager.getDatiGenRicerca(idRicercaEsistente);
            
            datiGenerali.setDisp(ricercaDB.getDisponibile() ? 1 : 0);
            datiGenerali.setPersonale(ricercaDB.getDisponibile() ? 0 : 1);
            datiGenerali.setOwner(datiGenRicercaDB.getOwner());
            contenitoreDatiProspetto.setElencoGruppi(tmpElencoGruppi);
  
          } catch(SqlComposerException sc){
            target = CostantiWizard.ERROR_IMPORT_REPORT;
            String messageKey = sc.getChiaveResourceBundle();
            logger.error(this.resBundleGenerale.getString(messageKey), sc);
            this.aggiungiMessaggio(request, messageKey);
          }
        } else {
          // SS 01/10/2008
          // il prospetto, anche se rimane personale, deve essere disponibile,
          // altrimenti non si riesce a listare se l'utente non è un
          // amministratore
          datiGenerali.setDisp(1);
          datiGenerali.setPersonale(1);
        }
      }
    } catch (DataAccessException e) {
      target = CostantiWizard.ERROR_IMPORT_REPORT;
      String messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiWizard.ERROR_IMPORT_REPORT;
      String messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }
    
    if(target == null){
      // Il file contenente il modello associato al report deve essere
      // decodificato dalla base 64 in caratteri ASCII
      DatoBase64 contenutoFileModello = new DatoBase64(contenitoreDatiProspetto.getFileModello(),
          DatoBase64.FORMATO_BASE64);
      
      if(esisteReport){
        if(tipoImport.equals(CostantiWizard.IMPORT_SOVRASCRIVI_ESISTENTE) ||
           tipoImport.equals(CostantiWizard.IMPORT_SOVRASCRIVI_PARZIALE)){
          result = this.insertProspettoEsistente(request, contenitoreDatiProspetto,
              codApp, datiGenerali.getOwner().intValue(), 
              contenutoFileModello.getByteArrayDatoAscii());
          
        } else if(tipoImport.equals(CostantiWizard.IMPORT_INSERT_CON_NUOVO_TITOLO)){
          // Cambio del titolo del report con modello che si va ad importare
          datiGenerali.setNome(nuovoTitoloReport);
          datiModello.setNomeModello(nuovoTitoloReport);
          result = this.insertProspetto(request, contenitoreDatiProspetto, codApp,
              idUtenteImportatore, contenutoFileModello.getByteArrayDatoAscii());
        }
      } else {
        result = this.insertProspetto(request, contenitoreDatiProspetto, codApp,
            idUtenteImportatore, contenutoFileModello.getByteArrayDatoAscii());
      }
    }
    
    if(result != null){
      // L'operazione di import è andata a buon fine, allora carico in sessione
      // un attributo indicato dalla costante 
      // CostantiGenRicerche.ID_RICERCA_PRECEDENTE in modo che il forward
      // permetta la visualizzazione del dettaglio del report appena importato
      request.getSession().setAttribute(CostantiGenRicerche.ID_RICERCA_PRECEDENTE,
          result.toString());
    } else {
      target = CostantiWizard.ERROR_IMPORT_REPORT;
    }
    return target;
  }

  private Integer insertProspetto(HttpServletRequest request,
      ContenitoreDatiProspetto contenitoreDatiProspetto, String codApp,
      int idUtenteOwner, byte[] contenutoFileProspetto){
    Integer result = null;
    String messageKey = null;
    
    DatiGenRicerca datiGenerali =
      contenitoreDatiProspetto.getDatiGenProspetto().getDatiGenRicerca();

    ProfiloUtente profiloUtente = (ProfiloUtente)request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    String contesto = null;
    if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_CONTESTI_ABILITATI)))
      contesto = profiloUtente.getUfficioAppartenenza();

    try {
      // controllo, dato che si tratta di una ricerca esistente, il codice
      // di pubblicazione report nel web: se specificato nel dato in import
      // non deve essere utilizzato da altri report, altrimenti va resettato
      if (contenitoreDatiProspetto.getDatiGenProspetto().getDatiGenRicerca().getCodReportWS() != null) {
        Integer idRicercaCodReport = this.ricercheManager.getIdRicercaByCodReportWS(contenitoreDatiProspetto.getDatiGenProspetto().getDatiGenRicerca().getCodReportWS());
        if (idRicercaCodReport != null) {
          contenitoreDatiProspetto.getDatiGenProspetto().getDatiGenRicerca().setCodReportWS(
              null);
          messageKey = "warnings.genRic.import.uniqueCodReportWS";
          logger.warn(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        }
      }

      // Inserimento del nuovo prospetto
      this.prospettoManager.importProspetto(contenitoreDatiProspetto, codApp,
          idUtenteOwner, contenutoFileProspetto, contesto);        
      result = datiGenerali.getIdRicerca();
    } catch (DataAccessException da) {
      // L'istruzione prospettoManager.importProspetto emette sempre eccezioni
      // di tipo DataAccessException (o una sua classe figlia). Tuttavia se
      // il messaggio di tale eccezione è null, allora l'eccezione originale è
      // stata wrappata con una DataAccessException (o una sua classe figlia).
      if(da.getCause() != null && da.getCause() instanceof RemoteException){
        //target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
        messageKey = "errors.modelli.compositoreDisattivo";
        logger.error(this.resBundleGenerale.getString(messageKey), da.getCause());
        this.aggiungiMessaggio(request, messageKey);

      } else if(da.getCause() != null && da.getCause() instanceof GestioneFileModelloException){
        GestioneFileModelloException e = (GestioneFileModelloException) da.getCause();
        //target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
        messageKey = "errors.modelli.uploaderror";
        // Aggiungo l'eventuale codice in più
        if (!e.getCodiceErrore().equals(""))
          messageKey += "." + e.getCodiceErrore();
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);

      } else if(da.getCause() != null && da.getCause() instanceof IOException){
        //target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
        IOException e = (IOException) da.getCause();
        messageKey = "errors.modelli.delete";
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
        
      } else if(da.getCause() != null && da.getCause() instanceof CompositoreException){
        // Gestione dell'eccezione in compilazione
        //target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
        CompositoreException e = (CompositoreException) da.getCause();
        messageKey = e.getChiaveResourceBundle();
        if (e.getParametri() == null) {
          logger.error(this.resBundleGenerale.getString(messageKey), e);
          this.aggiungiMessaggio(request, messageKey);
        } else if (e.getParametri().length == 1) {
          logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(0),
              (String) e.getParametri()[0]), e);
          this.aggiungiMessaggio(request, messageKey, e.getParametri()[0]);
        } else {
          logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(0),
              (String) e.getParametri()[0]).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(1),
              (String) e.getParametri()[1]), e);
          this.aggiungiMessaggio(request, messageKey, e.getParametri()[0],
              e.getParametri()[1]);
        }
      } else if(da.getMessage() != null){
        // L'eccezione emessa è effettivamente una DataAccessException o una
        // classe figlia
        if(da instanceof DataIntegrityViolationException){
          messageKey = "errors.prospetti.salva.vincoloUnique";
          logger.error(this.resBundleGenerale.getString(messageKey), da);
          this.aggiungiMessaggio(request, messageKey);
        } else if(da instanceof DataAccessException){
          messageKey = "errors.database.dataAccessException";
          logger.error(this.resBundleGenerale.getString(messageKey), da);
          this.aggiungiMessaggio(request, messageKey);
        }
      } 
    } catch (Throwable t) {
      //target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
  
    }
    return result;
  }
  
  private Integer insertProspettoEsistente(HttpServletRequest request,
      ContenitoreDatiProspetto contenitoreDatiProspetto, String codApp,
      int idUtenteOwner, byte[] contenutoFileProspetto){
    
    Integer result = null;
    String messageKey = null;
    
    DatiGenRicerca datiGenerali =
      contenitoreDatiProspetto.getDatiGenProspetto().getDatiGenRicerca();

    DatiGenProspetto datiProspettoEsistente = null;

    TrovaRicerche trovaRicerche = new TrovaRicerche();
    trovaRicerche.setNomeRicerca(datiGenerali.getNome());
    trovaRicerche.setCodiceApplicazione(codApp);
    trovaRicerche.setProfiloOwner(datiGenerali.getProfiloOwner());
    
    ProfiloUtente profiloUtente = (ProfiloUtente)request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    String contesto = null;
    if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_CONTESTI_ABILITATI)))
      contesto = profiloUtente.getUfficioAppartenenza();

    try {
      List<?> listaRicerche = this.ricercheManager.getRicerche(trovaRicerche, true);
      RicercaGruppo datiGenRicercaInDb = (RicercaGruppo) listaRicerche.get(0);

      //int idRicercaEsistente = datiGenRicercaInDb.getIdRicerca();
      
      // Estrazione dei dati generali del report con modello esistente su DB
      datiProspettoEsistente = 
        this.prospettoManager.getProspettoById(datiGenRicercaInDb.getIdRicerca());
      
      // controllo, dato che si tratta di una ricerca esistente, il codice
      // di pubblicazione report nel web: se specificato nel dato in import
      // non deve essere utilizzato da altri report, altrimenti va resettato
      if (contenitoreDatiProspetto.getDatiGenProspetto().getDatiGenRicerca().getCodReportWS() != null) {
        Integer idRicercaCodReport = this.ricercheManager.getIdRicercaByCodReportWS(contenitoreDatiProspetto.getDatiGenProspetto().getDatiGenRicerca().getCodReportWS());
        if (idRicercaCodReport != null
            && idRicercaCodReport.intValue() != datiGenRicercaInDb.getIdRicerca()) {
          contenitoreDatiProspetto.getDatiGenProspetto().getDatiGenRicerca().setCodReportWS(
              null);
          messageKey = "warnings.genRic.import.uniqueCodReportWS";
          logger.warn(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        }
      }

      DatiModello datiModelloEsistente = 
        datiProspettoEsistente.getDatiModello();
      
      String tmpNomeReport = 
        datiProspettoEsistente.getDatiGenRicerca().getNome().length() > 36 ?
        datiProspettoEsistente.getDatiGenRicerca().getNome().substring(0, 36) +
            new GregorianCalendar().getTimeInMillis() :
        datiProspettoEsistente.getDatiGenRicerca().getNome() +
            new GregorianCalendar().getTimeInMillis();
      // Aggiornamento dei dati generali del prospetto esistente su DB e dei dati
      // generali del modello associato. Cambio il nome del report con modello
      // da aggiornare con:
      // <nomeOriginale (troncato se troppo lungo)> + <timeStamp in millisecondi>
      datiProspettoEsistente.getDatiGenRicerca().setNome(tmpNomeReport);
      datiModelloEsistente.setNomeModello(tmpNomeReport);

      // idem per il codreportws: dato che si deve mantenere temporaneamente il
      // vecchio report e si crea il nuovo, ne modifico il codreportws
      // dell'esistente in modo da non violare vincoli di unicita' sul campo
      if (datiProspettoEsistente.getDatiGenRicerca().getCodReportWS() != null) {
        String tmpCodReport = datiProspettoEsistente.getDatiGenRicerca().getCodReportWS();
        if (tmpCodReport.length() > 15) {
          tmpCodReport = tmpCodReport.substring(0, 15);
        }
        datiProspettoEsistente.getDatiGenRicerca().setCodReportWS(
            tmpCodReport + new GregorianCalendar().getTimeInMillis());
      }

      // Cambio il nome del modello associato al report con modello
      // da aggiornare per evitare la sostituzione del modello da parte del
      // metodo pubblico modelliManager.updateModello
      this.prospettoManager.importProspettoEsistente(
          datiProspettoEsistente, 
          contenitoreDatiProspetto, codApp, idUtenteOwner,
          contenutoFileProspetto, contesto);
      result = datiGenerali.getIdRicerca();

    } catch (DataAccessException da) {
      // L'istruzione prospettoManager.importProspetto emette sempre eccezioni
      // di tipo DataAccessException (o una sua classe figlia). Tuttavia se
      // il messaggio di tale eccezione è null, allora l'eccezione originale è
      // stata wrappata con una DataAccessException (o una sua classe figlia).
      if(da.getCause() != null && da.getCause() instanceof CompositoreException){
        // Gestione dell'eccezione in compilazione
        //target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
        CompositoreException e = (CompositoreException) da.getCause();
        messageKey = e.getChiaveResourceBundle();
        if (e.getParametri() == null) {
          logger.error(this.resBundleGenerale.getString(messageKey), e);
          this.aggiungiMessaggio(request, messageKey);
        } else if (e.getParametri().length == 1) {
          logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(0),
              (String) e.getParametri()[0]), e);
          this.aggiungiMessaggio(request, messageKey, e.getParametri()[0]);
        } else {
          logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(0),
              (String) e.getParametri()[0]).replaceAll(
              UtilityStringhe.getPatternParametroMessageBundle(1),
              (String) e.getParametri()[1]), e);
          this.aggiungiMessaggio(request, messageKey, e.getParametri()[0],
              e.getParametri()[1]);
        }
      } else if(da.getCause() != null && da.getCause() instanceof RemoteException){
        //target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
        messageKey = "errors.modelli.compositoreDisattivo";
        logger.error(this.resBundleGenerale.getString(messageKey), da.getCause());
        this.aggiungiMessaggio(request, messageKey);

      } else if(da.getCause() != null && da.getCause() instanceof GestioneFileModelloException){
        GestioneFileModelloException e = (GestioneFileModelloException) da.getCause();
        //target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
        messageKey = "errors.modelli.uploaderror";
        // Aggiungo l'eventuale codice in più
        if (!e.getCodiceErrore().equals(""))
          messageKey += "." + e.getCodiceErrore();
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
      } else if(da.getCause() != null && da.getCause() instanceof IOException){
        IOException e = (IOException) da.getCause();
        messageKey = "errors.modelli.delete";
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
      } else if(da.getMessage() != null){
        // L'eccezione emessa è effettivamente una DataAccessException o una
        // classe figlia
        if(da instanceof DataIntegrityViolationException){
          messageKey = "errors.prospetti.salva.vincoloUnique";
          logger.error(this.resBundleGenerale.getString(messageKey), da);
          this.aggiungiMessaggio(request, messageKey);
        } else if(da instanceof DataAccessException){
          messageKey = "errors.database.dataAccessException";
          logger.error(this.resBundleGenerale.getString(messageKey), da);
          this.aggiungiMessaggio(request, messageKey);
        }
      }
    } catch (Throwable t) {
      //target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }
    
    return result;
  }
}