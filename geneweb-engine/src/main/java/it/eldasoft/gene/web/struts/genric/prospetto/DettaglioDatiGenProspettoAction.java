/*
 * Created on 09-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.prospetto;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.genmod.GestioneFileModelloException;
import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.bl.genric.ProspettoManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.genric.DatiGenProspetto;
import it.eldasoft.gene.tags.history.HistoryItem;
import it.eldasoft.gene.tags.history.UtilityHistory;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.gene.web.struts.genric.AbstractDispatchActionBaseGenRicercheProspetto;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.io.IOException;
import java.util.List;

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
 * Action per il dowload del modello associato alla ricerca con modello
 * Il codice di questa Action e' stato copiato dal metodo downloadModello della 
 * classe ModelliAction di GenMod.
 *  
 * @author Luca.Giacomazzo
 */

public class DettaglioDatiGenProspettoAction extends AbstractDispatchActionBaseGenRicercheProspetto {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(DettaglioDatiGenProspettoAction.class);

  /** Manager delle ricerche con modello */
  private ProspettoManager prospettoManager;
  
  /** Manager dei modelli */
  private ModelliManager   modelliManager;
  
  /**
   * Reference alla classe di business logic per l'accesso ai tabellati
   */
  private TabellatiManager tabellatiManager;
  
  /**
   * Reference al Manager per la gestione delle protezioni di tabelle e campi
   * rispetto al profilo attivo
   */
  private GeneManager geneManager;
  
  /**
   * @param prospettoManager prospettoManager da settare internamente alla classe.
   */
  public void setProspettoManager(ProspettoManager prospettoManager) {
    this.prospettoManager = prospettoManager;
  }
  
  /**
   * @param modelliManager modelliManager da settare internamente alla classe.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }
  
  
  /**
   * @param geneManager geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action visualizza
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniVisualizza() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_CON_PROSPETTO);
  }
  
  /**
   * Metodo per la visualizzazione dei dati generali di una ricerca con modello
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward visualizza(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("visualizza: inizio metodo");
    
    String target = CostantiGenProspetto.SUCCESS_VISUALIZZA;
    int idRicerca = -1;
    String messageKey = null;
    
    try {
      // Estraggo l'identificativo del prospetto (campo chiave)
      if (request.getParameter("idRicerca") != null)
        idRicerca = Integer.parseInt(request.getParameter("idRicerca"));
      else if (request.getAttribute("idRicerca") != null)
        idRicerca = ((Integer) request.getAttribute("idRicerca")).intValue();
      else {
        // Nel caso idProspetto non sia stato trovato nel request come parameter
        // o come attributo, allora lo recupero in sessione
        String tmp = (String) request.getSession().getAttribute(
            CostantiGenerali.ID_OGGETTO_SESSION);
        idRicerca = UtilityNumeri.convertiIntero(tmp).intValue();
      }
      // Estraggo i dati della ricerca con modello
      DatiGenProspetto datiGenProspetto = this.prospettoManager.getProspettoById(idRicerca);
      
      // Caricamento degli oggetti per popolare il campo 'Tipo Ricerca'
      List<Tabellato> listaTipoRicerca = this.tabellatiManager.getTabellato(TabellatiManager.TIPO_RICERCHE);
      // Set nel request delle liste per il popolamento delle varie combobox
      request.setAttribute("listaTipoRicerca", listaTipoRicerca);

      // Se non si verificano eccezioni, allora carico la pagina di dettaglio 
      // dei dati generali della ricerca con modello. Aggiorno l'oggetto
      // per la gestione del tab menu', e inserisco nel request l'oggetto
      // datiGenProspettoForm, aggiornato.
      DatiGenProspettoForm tmpDatiGenProsForm = new DatiGenProspettoForm(
          datiGenProspetto);
      
      // Controllo se l'entita' principale e' visibile nel profilo attivo
      this.isEntPrincVisibile(request, tmpDatiGenProsForm, true);
      
      request.setAttribute("datiGenProspettoForm", tmpDatiGenProsForm);
      
      // Aggiungo l'identificativo del modello per la gestione dei link nei tab
      // e nelle azioni di contesto
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO,
          new Integer(idRicerca));

      HttpSession session = request.getSession();
      // set in sessione del nome della ricerca con modello di cui si sta 
      // facendo il dettaglio
      session.setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
          datiGenProspetto.getDatiGenRicerca().getNome());
      // set in sessione dell'id della ricerca con modello di cui si sta 
      // facendo il dettaglio
      session.setAttribute(CostantiGenerali.ID_OGGETTO_SESSION, 
          datiGenProspetto.getDatiGenRicerca().getIdRicerca().toString());
      
      // -------------------------------------------------------------------------
      // In linea con le modifiche apportate ai metodi visualizza per le ricerche
      // avanzate, le ricerche base e i modelli....
      //
      // Solo nel caso si acceda al dettaglio di una ricerca dalla pagina Lista 
      // ricerche di gruppo, nel request e' presente in parameter denominato 'admin'.
      // Questo parameter avvia la seguente operazione:
      // si ottiene una referenza all'oggetto presente in sessione e contenente i 
      // dati del history. All'ultimo elemento presente nel history si cambia
      // l'attributo Id: in questo modo nella pagina di dettaglio della ricerca
      // nel sotto menu' "Torna a..." del menu azioni viene visualizzato il link
      // Dettaglio Gruppo  - <nomeGruppo>, il quale permette di tornare al 
      // dettaglio del gruppo dai cui si era partiti 
      // ----------------------------------------------------------------------
      String tmp = request.getParameter("admin");
      if(tmp != null){
        UtilityHistory history = UtilityTags.getUtilityHistory(request.getSession());
        HistoryItem hItem = history.get(history.size(0) - 1, 0);
        hItem.setId("scheda1");
      }
      // ----------------------------------------------------------------------
      
      // Setto i dati per la gestione dei tab
      this.setMenuTab(request);

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

    if (logger.isDebugEnabled()) logger.debug("visualizza: fine metodo");
    return mapping.findForward(target);
  }
  
  /**
   * Metodo per controllare se l'entita' principale e' visibile nel profilo
   * attivo
   * 
   * @param request
   * @param datiGenProspettoForm
   */
  private void isEntPrincVisibile(HttpServletRequest request,
      DatiGenProspettoForm datiGenProspettoForm, boolean visualizzaReport) {
    
    String messageKey;
    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
    if (datiGenProspettoForm.getEntPrinc() != null) {
      Tabella tabella = dizTabelle.getDaNomeTabella(datiGenProspettoForm.getEntPrinc()); 
      boolean isEntPrincVisibile = this.geneManager.getGestoreVisibilitaDati().checkEntitaVisibile(
          tabella,
          (String) request.getSession().getAttribute(
              CostantiGenerali.PROFILO_ATTIVO));

      if(visualizzaReport && !isEntPrincVisibile){
        messageKey = "warnings.prospetto.caricaProspetto.prospettoModificatoDaProfilo";
        logger.warn(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }
    }
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action downloadModello
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniDownloadModello() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.ABILITAZIONE_REPORT_CON_PROSPETTO);
  }
  
  /**
   * Metodo per effettuare il download del modello associato alla ricerca 
   * con modello
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward downloadModello(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("downloadModello: inizio metodo");

    String messageKey, errorTarget;
    String target = null;
    if (logger.isDebugEnabled()) {
      logger.debug("Download modello da " + request.getParameter("da"));
    }
    errorTarget = CostantiGenProspetto.FORWARD_ERRORE_DOWNLOAD_DETTAGLIO;
    if (request.getParameter("da") != null) {
      if (request.getParameter("da").equals("modifica"))
        errorTarget = CostantiGenProspetto.FORWARD_ERRORE_DOWNLOAD_MODIFICA;
    }
    try {
      modelliManager.downloadFile(request.getParameter("nomeFile"),
          (String) request.getSession().getAttribute(
              CostantiGenerali.MODULO_ATTIVO), response);
    } catch (GestioneFileModelloException e) {
      target = errorTarget;
      messageKey = "errors.modelli.download";
      // Aggiungo l'eventuale codice in più
      if (!e.getCodiceErrore().equals(""))
        messageKey += "." + e.getCodiceErrore();
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = errorTarget;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }
    
    ActionForward forward = null;
    if (target != null) {
      response.reset();
      forward = mapping.findForward(target);
    }

    if (logger.isDebugEnabled()) logger.debug("downloadModello: fine metodo");
    return forward;
  }
  
  
  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di visualizzazione del dettaglio dei Dati Generali di
   * una ricerca con modello
   * 
   * @param request
   */
  private void setMenuTab(HttpServletRequest request) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiGenRicerche.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(CostantiGenProspetto.TAB_DATI_GENERALI);
      gestoreTab.setTabSelezionabili(new String[] {
        CostantiGenProspetto.TAB_GRUPPI, CostantiGenProspetto.TAB_PARAMETRI });
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(CostantiGenProspetto.TAB_DATI_GENERALI);
      gestoreTab.setTabSelezionabili(new String[] {
        CostantiGenProspetto.TAB_GRUPPI, CostantiGenProspetto.TAB_PARAMETRI });
      sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
    }
  }

}