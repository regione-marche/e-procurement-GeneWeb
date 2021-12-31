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

import it.eldasoft.gene.bl.CheckReportPerProfilo;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Gruppo;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiRicerca;
import it.eldasoft.gene.db.domain.genric.GruppoRicerca;
import it.eldasoft.gene.tags.history.HistoryItem;
import it.eldasoft.gene.tags.history.UtilityHistory;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.gene.web.struts.genric.filtro.ControlloFiltri;
import it.eldasoft.gene.web.struts.genric.filtro.ControlloFiltriException;
import it.eldasoft.gene.web.struts.genric.gruppo.GruppoForm;
import it.eldasoft.gene.web.struts.tags.GestoreEccezioni;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * DispatchAction per il caricamento in sessione di tutti i dati di una ricerca
 * di cui si vuole visualizzare o modificare il dettaglio, oppure sua
 * cancellazione dalla base dati e il salvataggio su DB.
 * 
 * @author Luca Giacomazzo
 */
public class DettaglioRicercaAction extends AbstractDispatchActionBaseGenRicerche {

  // Forward predefiniti per il DispatchAction in linea con il file
  // struts-config.xml
  private static final String SUCCESS_VISUALIZZA                   = "successVisualizza";
  //private static final String SUCCESS_MODIFICA                     = "successModifica";
  //private static final String SUCCESS_CREA_REPORT_BASE             = "successCreaReportBase";
  //private static final String SUCCESS_CREA_REPORT_AVANZATO         = "successCrea";
  //private static final String SUCCESS_CREA_REPORT_CON_PROSPETTO    = "successCreaProspetto";
  //private static final String SUCCESS_ELIMINA                      = "successElimina";
  private static final String SUCCESS_COPIA                        = "successCopia";
  protected static final String SUCCESS_SALVA                      = "successSalva";
  protected static final String REPORT_BASE_SUCCESS_SALVA          = "successSalvaReportBase";
  
  protected static final String REPORT_SQL_SUCCESS_SALVA           = "successSalvaReportSql";
  
  private static final String SUCCESS_ESEGUI_PREDEFINITA           = "successCaricaPerEstrazione";
  private static final String SUCCESS_SALVA_E_TROVA                = "successSalvaETrova";
  private static final String SUCCESS_SALVA_E_LISTA                = "successSalvaELista";
  private static final String SUCCESS_SALVA_E_CREA                 = "successSalvaECrea";
  private static final String SUCCESS_SALVA_E_ESEGUI               = "successSalvaEEsegui";
  protected static final String SUCCESS_ERROR_SALVA                = "errorSalva";
  protected static final String REPORT_BASE_SUCCESS_ERROR_SALVA    = "errorSalvaReportBase";
  
  protected static final String REPORT_SQL_SUCCESS_ERROR_SALVA     = "errorSalvaReportSql";
  
  private static final String SUCCESS_CARICA_E_ESEGUI              = "successCaricaEEsegui";
  private static final String FORWARD_DATI_GEN_PROSPETTO           = "forwardDatiGenProspetto";
  private static final String FORWARD_ESEGUI_PROSPETTO_PREDEFINITO = "forwardEseguiProspettoPredefinito";
  
  private static final String TORNA_A_HOME_PAGE                    = "tornaAHomePage";
  private static final String TORNA_A_LISTA_RICERCHE               = "tornaAListaRicerche";
  private static final String TORNA_A_LISTA_RICERCHE_PREDEFINITE   = "tornaAListaRicerchePredefinite";
  private static final String TORNA_A_TROVA_RICERCHE               = "tornaATrovaRicerche";
  private static final String TORNA_DETTAGLIO_RICERCA_PRECEDENTE   = "tornaDettaglioRicercaPrecedente";
  
  /** Costanti per creare il nuovo nome di una ricerca copiata */
  public static final String PREFISSO1_NOME_COPIATURA              = "Copia ";
  public static final String PREFISSO2_NOME_COPIATURA              = "di ";
  
  /** Logger Log4J di classe */
  static Logger               logger                     = Logger.getLogger(DettaglioRicercaAction.class);

  /**
   * Reference alla classe di business logic per accesso ai dati relativi alle
   * ricerche
   */
  protected RicercheManager     ricercheManager;

  /**
   * Reference alla classe di business logic per accesso ai dati relativi ai
   * gruppi
   */
  protected GruppiManager       gruppiManager;

  /**
   * Reference alla classe di business logic che gestisce tutte le funzionalita'
   *  di base di AL
   */
  protected GeneManager       geneManager;

  /**
   * @param gruppiManager
   *        gruppiManager da settare internamente alla classe.
   */
  public void setGruppiManager(GruppiManager gruppiManager) {
    this.gruppiManager = gruppiManager;
  }

  /**
   * @param ricercheManager
   *        ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }
  
  /**
   * @param geneManager geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  public ActionForward visualizza(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("visualizza: inizio metodo");

    // target di default, da modificare nel momento in cui si verificano dei
    // problemi
    String target = DettaglioRicercaAction.SUCCESS_VISUALIZZA;

    // caricamento in sessione dei dati relativi alla ricerca
    String appoggio = this.caricaRicercaInSessione(request, false);
    if (appoggio != null) target = appoggio;

    // l'oggetto è appena estratto dal DB e quindi non modificato
    request.getSession().removeAttribute(
        CostantiGenerali.SENTINELLA_OGGETTO_MODIFICATO);
    
    // -------------------------------------------------------------------------
    // Solo nel caso si acceda al dettaglio di una ricerca dalla pagina Lista 
    // ricerche di gruppo, nel request e' presente in parameter denominato 'admin'.
    // Questo parameter avvia la seguente operazione:
    // si ottiene una referenza all'oggetto presente in sessione e contenente i 
    // dati del history. All'ultimo elemento presente nel history si cambia
    // l'attributo Id: in questo modo nella pagina di dettaglio della ricerca
    // nel sotto menu' "Torna a..." del menu azioni viene visualizzato il link
    // Dettaglio Gruppo  - <nomeGruppo>, il quale permette di tornare al 
    // dettaglio del gruppo dai cui si era partiti 
    // -------------------------------------------------------------------------
    String tmp = request.getParameter("admin");
    if (tmp != null) {
      UtilityHistory history = UtilityTags.getUtilityHistory(request.getSession());
      HistoryItem hItem = history.get(history.size(0) - 1, 0);
      hItem.setId("scheda1");
    }
    
    if (logger.isDebugEnabled()) logger.debug("visualizza: fine metodo");
    return mapping.findForward(target);
  }

  public ActionForward caricaPerEstrazione(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled())
      logger.debug("caricaRicercaPerEstrazione: inizio metodo");

    // target di default, da modificare nel momento in cui si verificano dei
    // problemi
    String target = DettaglioRicercaAction.SUCCESS_ESEGUI_PREDEFINITA;

    // caricamento in sessione dei dati relativi alla ricerca
    String appoggio = this.caricaRicercaInSessione(request, true);
    if (appoggio != null) {
      if ((!appoggio.equals(DettaglioRicercaAction.TORNA_A_LISTA_RICERCHE)))
        target = appoggio;
      else
        target = DettaglioRicercaAction.TORNA_A_LISTA_RICERCHE_PREDEFINITE;
    }
    // set nel request di un form vuoto necessario alla pagina di setting
    // dei parametri necessari per l'estrazione di una ricerca
    request.setAttribute("parametriRicerca", new ParametriRicercaForm());

    // set nel request di un attributo che consente di distinguere se si va ad
    // estrarre una ricerca predefinita o meno
    request.setAttribute("tipoRicerca", "ricercaPredefinita");

    if (logger.isDebugEnabled())
      logger.debug("caricaRicercaPerEstrazione: fine metodo");
    return mapping.findForward(target);
  }

  public ActionForward caricaEEsegui(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("caricaEEsegui: inizio metodo");

    // target di default, da modificare nel momento in cui si verificano dei
    // problemi
    String target = DettaglioRicercaAction.SUCCESS_CARICA_E_ESEGUI;
    
    // caricamento in sessione dei dati relativi alla ricerca
    String appoggio = this.caricaRicercaInSessione(request, true);
    if (appoggio != null) target = appoggio;

    // set nel request di un form vuoto necessario alla pagina di setting
    // dei parametri necessari per l'estrazione di una ricerca
    request.setAttribute("parametriRicerca", new ParametriRicercaForm());
    
    // Il metodo caricaEEsegui e' invocato dalla pagina lista delle ricerche come
    // esito delle condizioni di filtro impostate nella pagina di trova: di 
    // conseguenza setto nel request un attributo per specificare la pagina
    // di partenza. Questo servirà nella pagina "Set Parametri" per visualizzare
    // il pulsante "Torna a lista report" o "Torna a dettaglio report"
    request.setAttribute("fromPage", "listaRicerche");
    
    if (logger.isDebugEnabled()) logger.debug("caricaEEsegui: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Metodo per il caricamento in sessione dei dati relativi ad una ricerca
   * 
   * @param request
   * @param caricaReportPerEsecuzione true se il report viene caricato in
   *        sessione per la sua esecuzione, false se il report viene caricato
   *        per la visualizzazione del dettaglio
   */
  protected String caricaRicercaInSessione(HttpServletRequest request,
      boolean caricaReportPerEsecuzione) {

    String messageKey = null;
    String target = null;

    int idRicerca = -1;
    try {
      String tmp = request.getParameter("idRicerca");
      if (tmp == null) {
        tmp = (String) request.getSession().getAttribute(CostantiGenRicerche.ID_RICERCA_PRECEDENTE);
        //Rimuovo dalla sessione l'oggetto per la pulizia della sessione stessa
        request.getSession().removeAttribute(CostantiGenRicerche.ID_RICERCA_PRECEDENTE);
      }
      
      if (tmp != null) {
        idRicerca = Integer.parseInt(tmp);
        ContenitoreDatiRicerca contenitoreHelper = this.ricercheManager.getRicercaByIdRicerca(idRicerca);
        // Se la ricerca e' con modello, allora demando il caricamento dei dati
        // generali della ricerca con modello alla Action DettaglioDatiGenProspetto
        if (contenitoreHelper.getDatiGenerali().getFamiglia().intValue() != CostantiGenRicerche.REPORT_PROSPETTO) {

          ContenitoreDatiRicercaForm contenitore = null;
          if (contenitoreHelper.getDatiGenerali().getFamiglia().intValue() != CostantiGenRicerche.REPORT_SQL) {
  
            // Salvataggio dell'entita' principale del report prima di mettere il
            // report in relazione con il profilo attivo
            String tmpEntitaPrincipale = contenitoreHelper.getDatiGenerali().getEntPrinc();
  
            // L.G. 30/10/2007: controllo del report rispetto al profilo attivo
            // Si effettuano due tipi di controlli:
            // - eseguibilita' del report: se il report deve essere caricato in
            //   sessione per la sua esecuzione (da lista report, da dettaglio 
            //   report o da lista report predefiniti);
            // - report 
            // Nel controllare il report, qualora esso presenti tabelle, campi,
            // filtri e ordinamenti definiti su tabelle e/o campi non visibili nel
            // profilo attivo, l'oggetto contenitore viene privato di tali oggetti
            CheckReportPerProfilo reportChecking = new CheckReportPerProfilo(
                this.geneManager.getGestoreVisibilitaDati(),
                (String) request.getSession().getAttribute(
                    CostantiGenerali.PROFILO_ATTIVO), contenitoreHelper);
            
            
            if (caricaReportPerEsecuzione) {
              if (!reportChecking.isReportEseguibile()) {
                target = DettaglioRicercaAction.TORNA_A_LISTA_RICERCHE;
                messageKey = "errors.genric.estraiRicerca.profiloAttivo.reportNonEseguibile";
                logger.error(this.resBundleGenerale.getString(messageKey));
                this.aggiungiMessaggio(request, messageKey);
              } else {
                contenitore = new ContenitoreDatiRicercaForm(contenitoreHelper);
                contenitore.setEseguiDaLista(true);
              }
            } else {
              if (!reportChecking.checkReport()) {
                messageKey = "warnings.genric.caricaRicerca.reportModificatoDaProfilo";
                logger.warn(this.resBundleGenerale.getString(messageKey));
                this.aggiungiMessaggio(request, messageKey);
                
                if (!tmpEntitaPrincipale.equals(contenitoreHelper.getDatiGenerali().getEntPrinc())
                    && contenitoreHelper.getNumeroTabelle() > 0) {
                  messageKey = "warnings.genRic.estraiRicerca.profiloAttivo.cambioEntPrinc";
                  logger.warn(this.resBundleGenerale.getString(messageKey));
                  this.aggiungiMessaggio(request, messageKey);
                }
                contenitore = new ContenitoreDatiRicercaForm(contenitoreHelper);            
                contenitore.setStatoReportNelProfiloAttivo(false);
              } else {
                contenitore = new ContenitoreDatiRicercaForm(contenitoreHelper);              
              }
            }
          } else {
            // Il report Sql non ha bisogno di particolari controlli. Si presume che in
            // fase di creazione/definizione dello stesso tutto sia a gia' stato controllato.
            contenitore = new ContenitoreDatiRicercaForm(contenitoreHelper);
            contenitore.setEseguiDaLista(caricaReportPerEsecuzione);
          }

          // Se il valore del target non e' cambiato, allora proseguo nel caricare
          // il report in sessione ed effettuare le impostazioni del caso
          if (target == null) {
            request.getSession().setAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO,
                contenitore);
    
            // set in sessione del nome della ricerca di cui si sta facendo il
            // dettaglio
            request.getSession().removeAttribute(CostantiGenerali.NOME_OGGETTO_SESSION);
            request.getSession().setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
              contenitore.getTestata().getNome());
            
            request.getSession().removeAttribute(CostantiGenerali.ID_OGGETTO_SESSION);
            request.getSession().setAttribute(CostantiGenerali.ID_OGGETTO_SESSION,
              contenitore.getTestata().getId());
            
            // L.G. modifiche per implementazione ricerche base: se la ricerca e'
            // una ricerca base cambio il target e lo controllo nel metodo visualizza
            if (CostantiGenRicerche.REPORT_BASE == contenitore.getTestata().getFamiglia().intValue()) {
              if (request.getParameter("metodo").equals("visualizza")) {
                target = DettaglioRicercaAction.REPORT_BASE_SUCCESS_SALVA;
              } else if (request.getParameter("metodo").equals("caricaPerEstrazione")) {
                request.setAttribute("fromPage", "listaPredefinite");
              }
            } else if (CostantiGenRicerche.REPORT_SQL == contenitore.getTestata().getFamiglia().intValue()) {
              if (request.getParameter("metodo").equals("visualizza")) {
                target = DettaglioRicercaAction.REPORT_SQL_SUCCESS_SALVA;
              } else if (request.getParameter("metodo").equals("caricaPerEstrazione")) {
                request.setAttribute("fromPage", "listaPredefinite");
              }
            }
          }
        } else {
          if (request.getParameter("metodo").equals("visualizza")) {
            target = DettaglioRicercaAction.FORWARD_DATI_GEN_PROSPETTO;
          } else {
            target = DettaglioRicercaAction.FORWARD_ESEGUI_PROSPETTO_PREDEFINITO;
            request.setAttribute("fromPage", "listaPredefinite");
          }
          request.setAttribute("idRicerca", new Integer(idRicerca));
        }
      } else {
        //In questo caso non è stato possibile trovare idRicerca nè sulla request
        //nè in sessione: questo significa che bisogna tornare alla pagina di 
        //trova ricerche 
        target = "noIdRicerca";
      }
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
    return target;
  }
  
  /**
   * Azione che gestisce l'evento di annullamento di creazione di una nuova
   * ricerca: bisogna ricaricare in sessione la ricerca precedentemente
   * visualizzata, sfruttando l'oggetto presente in sessione denominato
   * 'CostantiGenRicerche.ID_RICERCA_PRECEDENTE'.
   * Tale oggetto, dove aver ripristinato la ricerca precedente, viene a sua
   * volta rimosso dalla sessione 
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward annullaCrea(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("annullaCrea: inizio metodo");
    
    ActionForward actForward = null;
    UtilityHistory history = UtilityTags.getUtilityHistory(request.getSession());
        
    // L.G. 10/01/2008: modifica per gestire correttamente l'annullamento della
    // creazione di una nuova ricerca. Per far cio' si controlla, a partire dalla
    // dimensione del history, la successione degli id degli oggetti HistoryItem
    // presenti nel history stessa, in relazione con la presenza in sessione
    // dell'attributo CostantiGenRicerche.ID_RICERCA_PRECEDENTE.
    // In generale gli id devono contenere o coincidere con le stringhe 'lista',
    // 'scheda' e 'trova' e la successione degli id nel history deve essere
    // 'trova', 'lista' e 'scheda'.
    // Il seguente codice esegue quanto descritto:
    // - se history.size(0) = 1 e l'id dell'unico oggetto HistoryItem contiene
    //   la stringa 'trova', allora si ritorna alla pagina di trova ricerche,
    //   altrimenti si ritorna alla home page;
    // - se history.size(0) = 2 e l'id del primo oggetto HistoryItem contiene la
    //   stringa 'trova' e l'id del secondo oggetto HistoryItem contiene la
    //   stringa 'lista', allora si ritorna alla pagina lista delle ricerche,
    //   altrimenti si ritorna alla pagina di trova ricerche;
    // - se history.size(0) = 3 sicuramente l'id del primo oggetto HistoryItem
    //   contiene la stringa 'trova', l'id del secondo oggetto HistoryItem
    //   contiene la stringa 'lista' e l'id dell'ultimo oggetto HistoryItem
    //   contiene la stringa 'scheda'. Il controllo da effettuare in questo caso 
    //   e' il seguente: 
    //     se history.size(0) = 3 e in sessione NON e' presente l'attributo
    //     CostantiGenRicerche.ID_RICERCA_PRECEDENTE, allora si ritorna alla
    //     pagina lista ricerche, altrimenti si ritorna al dettaglio della
    //     ricerca visualizzato in precedenza.
    
    boolean esisteIdRicercaPrecedente = request.getSession().getAttribute(
        CostantiGenRicerche.ID_RICERCA_PRECEDENTE) != null;
    
    HistoryItem historyItem0 = null;
    HistoryItem historyItem1 = null;
        
    switch(history.size(0)) {
    case 0:
      actForward = mapping.findForward(TORNA_A_HOME_PAGE);
      break;
    case 1:
      historyItem0 = history.get(0, 0);
      if (historyItem0.getId().toLowerCase().indexOf("trova") >= 0)
        actForward = mapping.findForward(TORNA_A_TROVA_RICERCHE);
      else
        actForward = mapping.findForward(TORNA_A_HOME_PAGE);
      break;
    case 2:
      historyItem0 = history.get(0, 0);
      historyItem1 = history.get(1, 0);
      if (historyItem0.getId().toLowerCase().indexOf("trova") >= 0 &&
         historyItem1.getId().toLowerCase().indexOf("lista") >= 0)
        actForward = mapping.findForward(TORNA_A_LISTA_RICERCHE);
      else
        actForward = mapping.findForward(TORNA_A_TROVA_RICERCHE);
      break;
    case 3:
      if (esisteIdRicercaPrecedente) {
        try {
          actForward = mapping.findForward(TORNA_DETTAGLIO_RICERCA_PRECEDENTE);
        } catch (Throwable t) {
          actForward = GestoreEccezioni.gestisciEccezioneAction(t, this, request,
              logger, mapping);
        }
      } else {
        actForward = mapping.findForward(TORNA_A_LISTA_RICERCHE);
        // Dall'history si va a rimuovere l'ultimo elemento in modo da non
        // visualizzare voci che non hanno più significato
        history.removeAttribute(0, history.size(0)-1);
      }
      break;
    }

    if (logger.isDebugEnabled()) logger.debug("annullaCrea: fine metodo ");
    return actForward;
  }
  
  /*
   * Funzione che restituisce le opzioni per accedere alla action elimina
   * @return opzioni per accedere alla action
   *
  public CheckOpzioniUtente getOpzioniElimina() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  public ActionForward elimina(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("elimina: inizio metodo");

    // target di default, da modificare nel momento in cui si verificano degli
    // errori
    String target = DettaglioRicercaAction.SUCCESS_ELIMINA;
    String messageKey = null;

    try {
      int idRicerca = Integer.parseInt(request.getParameter("idRicerca"));
      this.ricercheManager.deleteRicerca(idRicerca);

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

    if (logger.isDebugEnabled()) logger.debug("elimina: fine metodo");

    return mapping.findForward(target);
  }*/

  public ActionForward copia(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("copia: inizio metodo");

    // target di default, da modificare nel momento in cui si verificano degli
    // errori
    String target = DettaglioRicercaAction.SUCCESS_COPIA;
    String messageKey = null;

    try {
      int idRicerca = Integer.parseInt(request.getParameter("idRicerca"));
      HttpSession sessione = request.getSession();
      this.insertCopiaRicerca(idRicerca, ((ProfiloUtente)
          sessione.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId());

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

    if (logger.isDebugEnabled()) logger.debug("copia: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Crea una copia della ricerca a meno del nome
   * 
   * @param idRicerca
   *        id della ricerca da copiare
   */
  private void insertCopiaRicerca(int idRicerca, int idAccount) {
    // si estraggono tutti i dati della ricerca da clonare
    ContenitoreDatiRicerca contenitore =
        this.ricercheManager.getRicercaByIdRicerca(idRicerca);
    // il codice di pubblicazione su web viene sbiancato in quanto deve essere
    // univoco e si decidera' che fare una volta generato il report
    contenitore.getDatiGenerali().setCodReportWS(null);
    
    String nomeRicercaOriginale = contenitore.getDatiGenerali().getNome();
    // si modifica il nome univoco impostando un prefisso in "stile Windows"
    String nomeRicercaClonata = null;
    int progressivo = 1;
    boolean inserimentoValido = false;
    do {
      nomeRicercaClonata = PREFISSO1_NOME_COPIATURA
          + (progressivo == 1 ? "" : ("(" + progressivo + ") "))
          + PREFISSO2_NOME_COPIATURA
          + nomeRicercaOriginale;
      if (nomeRicercaClonata.length() > 50)
        nomeRicercaClonata = nomeRicercaClonata.substring(0, 50);

      contenitore.getDatiGenerali().setNome(nomeRicercaClonata);
      // si definisce come owner l'utente che richiede la copia
      contenitore.getDatiGenerali().setOwner(new Integer(idAccount));
      // si effettua l'inserimento dell'oggetto clonato
      try {
        this.ricercheManager.insertRicerca(contenitore);
        inserimentoValido = true;
      } catch (DataIntegrityViolationException e) {
        inserimentoValido = false;
        progressivo++;
      }
    } while (!inserimentoValido);
  }
  
  
  public ActionForward salva(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("salva: inizio metodo");

    String tabOrigine = request.getParameter("tab");
    if (tabOrigine == null) tabOrigine = (String) request.getAttribute("tab");
    
    // target di default
    String target = DettaglioRicercaAction.SUCCESS_SALVA;

    String appoggio = this.memorizza(request, tabOrigine);
    if (appoggio != null)
      target = appoggio;

    if (DettaglioRicercaAction.SUCCESS_SALVA.equals(target) ||
        DettaglioRicercaAction.REPORT_BASE_SUCCESS_SALVA.equals(target)) {
      // set del tab di destinazione dopo l'operazione di insert/update:
      // si ritorna al tab che da cui e' stato scatenata l'azione.
      request.setAttribute("tab", tabOrigine);       
    }

    if (logger.isDebugEnabled()) {
      logger.debug("salva: fine metodo");
    }
    return mapping.findForward(target);
  }

  public ActionForward salvaETrova(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("salvaETrova: inizio metodo");

    String tabOrigine = request.getParameter("tab");

    // target di default
    String target = DettaglioRicercaAction.SUCCESS_SALVA_E_TROVA;

    String appoggio = this.memorizza(request, tabOrigine);
    if (appoggio != null) target = appoggio;

    if (logger.isDebugEnabled()) {
      logger.debug("salvaETrova: fine metodo");
    }
    return mapping.findForward(target);
  }

  public ActionForward salvaELista(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("salvaELista: inizio metodo");

    String tabOrigine = request.getParameter("tab");

    // target di default
    String target = DettaglioRicercaAction.SUCCESS_SALVA_E_LISTA;

    String appoggio = this.memorizza(request, tabOrigine);
    if (appoggio != null) target = appoggio;

    if (logger.isDebugEnabled()) {
      logger.debug("salvaELista: fine metodo");
    }
    return mapping.findForward(target);
  }

  public ActionForward salvaECrea(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("salvaELista: inizio metodo");

    String tabOrigine = request.getParameter("tab");

    // target di default
    String target = DettaglioRicercaAction.SUCCESS_SALVA_E_CREA;

    String appoggio = this.memorizza(request, tabOrigine);
    if (appoggio != null) target = appoggio;

    if (logger.isDebugEnabled()) {
      logger.debug("salvaELista: fine metodo");
    }
    return mapping.findForward(target);
  }

  public ActionForward salvaEEsegui(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("salvaEEsegui: inizio metodo");

    String tabOrigine = request.getParameter("tab");

    // target di default
    String target = DettaglioRicercaAction.SUCCESS_SALVA_E_ESEGUI;

    String appoggio = this.memorizza(request, tabOrigine);
    if (appoggio != null) target = appoggio;

    if (logger.isDebugEnabled()) {
      logger.debug("salvaEEsegui: fine metodo");
    }
    return mapping.findForward(target);
  }

  /**
   * @param request
   * @param tabOrigine
   */
  protected String memorizza(HttpServletRequest request, String tabOrigine) {
    boolean continua = true;
    String messageKey = null;
    String target = null;

    HttpSession session = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm) 
            session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    ProfiloUtente profiloUtente = (ProfiloUtente) session.getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);

    // L.G. 31/03/2015: tutti i controlli prima del salvataggio di un report non si applicano
    
    if (CostantiGenRicerche.REPORT_SQL != contenitore.getTestata().getFamiglia().intValue()) {
      // NOTA: i controlli indicati qui sotto prevedono l'apertura del tab mancante
      // di dati obbligatori per il salvataggio.
      // Per la SOLA testata è necessario caricare l'anagrafica dei gruppi, quindi
      // è necessario passare per l'azione "/CambiaTabRicerca"; in tal caso, dovendo
      // far poi una redirect per arrivare al tab stesso, si perdono i messaggi di
      // errore. Di conseguenza tali messaggi sono salvati in SESSIONE per essere
      // poi inseriti immediatamente nel request e rimossi dalla sessiones stessa
      // non appena si esegue l'azione di CambioTab se non c'è almeno una tabella
      // selezionata, presento un errore ed apro il tab relativo alle tabelle.
      
      // L.G. 29/03/2007: modifiche per implementazione della ricerca base:
      // una ricerca base può essere salvata solo se ha almeno un campo (e quindi
      // ha una tabella). Si evita di fare il controllo sul numero di tabelle solo
      // per presentare a video un messaggio in linea con i tab visibili dal client
      // (infatti la ricerca base non ha il tab delle tabelle)
      if (CostantiGenRicerche.REPORT_BASE != contenitore.getTestata().getFamiglia().intValue()) {
        if (contenitore.getNumeroTabelle() == 0) {
    
          messageKey = "errors.genRic.salvaRicerca.mancaTabella";
          if (logger.isDebugEnabled()) {
            logger.debug(this.resBundleGenerale.getString(messageKey));
          }
          
          this.aggiungiMessaggio(request, messageKey);
          request.setAttribute("tab", CambiaTabAction.CODICE_TAB_ARGOMENTI);
          target = DettaglioRicercaAction.SUCCESS_ERROR_SALVA;
          continua = false;
        } else {
          Vector<TabellaRicercaForm> elencoTabelle = contenitore.getElencoArgomenti();
          boolean tabelleVisibili = false;
          int indice = 0;
          // ciclo per verificare se almeno una tra le tabelle presenti
          // nell'elencoTabelle sia visibile
          while (indice < elencoTabelle.size() && !tabelleVisibili) {
            tabelleVisibili = ((TabellaRicercaForm) elencoTabelle.elementAt(indice)).getVisibile();
            indice++;
          }
    
          if (!tabelleVisibili) {
            messageKey = "errors.genRic.salvaRicerca.tabelleNonVisibili";
            if (logger.isDebugEnabled()) {
              logger.debug(this.resBundleGenerale.getString(messageKey));
            }
            
            this.aggiungiMessaggio(request, messageKey);
            request.setAttribute("tab", CambiaTabAction.CODICE_TAB_ARGOMENTI);
            target = DettaglioRicercaAction.SUCCESS_ERROR_SALVA;
            continua = false;
          }
        }
      }
      // se non c'è almeno un campo da estrarre, presento un errore ed apro il tab
      // relativo ai campi
      if (continua && contenitore.getNumeroCampi() == 0) {
  
        messageKey = "errors.genRic.salvaRicerca.mancaCampo";
        if (logger.isDebugEnabled()) {
          logger.debug(this.resBundleGenerale.getString(messageKey));
        }
        this.aggiungiMessaggio(request, messageKey);
        request.setAttribute("tab", CambiaTabAction.CODICE_TAB_CAMPI);
        
        // L.G. 29/03/2007: implementazione ricerca base: in caso di errore nel
        // salvataggio della ricerca si cambia il target
        if (CostantiGenRicerche.REPORT_BASE != contenitore.getTestata().getFamiglia().intValue()) {
          target = DettaglioRicercaAction.SUCCESS_ERROR_SALVA;
        } else {
          target = DettaglioRicercaAction.REPORT_BASE_SUCCESS_ERROR_SALVA;
        }
        continua = false;
      }
  
      // se non ci sono i dati di testata, presento un errore
      // ed apro il tab di testata
      if (continua
          && (contenitore.getTestata().getNome() == null || 
              contenitore.getTestata().getNome().trim().length() == 0)) {
  
        messageKey = "errors.genRic.salvaRicerca.mancaTestata";
        if (logger.isDebugEnabled()) {
          logger.debug(this.resBundleGenerale.getString(messageKey));
        }
        this.aggiungiMessaggio(request, messageKey);
        request.setAttribute("tab", CambiaTabAction.CODICE_TAB_DATI_GENERALI);
        
        // L.G. 29/03/2007: implementazione ricerca base: in caso di errore nel
        // salvataggio della ricerca si cambia il target
        if (CostantiGenRicerche.REPORT_BASE != contenitore.getTestata().getFamiglia().intValue()) {
          target = DettaglioRicercaAction.SUCCESS_ERROR_SALVA;
        } else {
          target = DettaglioRicercaAction.REPORT_BASE_SUCCESS_ERROR_SALVA;
        }
        continua = false;
      }
  
      //F.D. 09/07/07 inserisco il controllo di correttezza per i filtri
      //se i filtri sono imposta in maniera errata passo al tab dei filtri e do errore
      if (continua) {
        try {
          ControlloFiltri.checkFiltro(ControlloFiltri.creaStringaPerControllo(contenitore.getElencoFiltri())); 
        
        } catch (ControlloFiltriException e) {
          target = SUCCESS_ERROR_SALVA;
          messageKey = e.getChiaveResourceBundle();
          logger.error(this.resBundleGenerale.getString(messageKey), e);
          this.aggiungiMessaggio(request, messageKey);
          request.setAttribute("tab", CambiaTabAction.CODICE_TAB_FILTRI);
          target = DettaglioRicercaAction.SUCCESS_ERROR_SALVA;
          continua = false;
        }
      }
    } else {
      // Ricerca SQL
      if (StringUtils.isEmpty(contenitore.getTestata().getDefSql())) {
        // Non e' stata specificata la query sql da eseguire
        
        messageKey = "errors.genRic.salvaRicerca.reportSql.noQuerySql";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
        request.setAttribute("tab", CambiaTabAction.CODICE_TAB_SQL);
        target = DettaglioRicercaAction.REPORT_SQL_SUCCESS_ERROR_SALVA;;
        continua = false;
      }
    }
    
    // se si arriva qui, i controlli sono tutti OK,
    // quindi si procede al salvataggio
    if (continua) {
      try {

        ContenitoreDatiRicerca contenitorePerModel = contenitore.getDatiPerModel();
        // se il numero di join è inferiore al numero delle tabelle meno 1 
        // segnalo che potrebbero esserci prodotti cartesiani
        if (CostantiGenRicerche.REPORT_SQL != contenitore.getTestata().getFamiglia().intValue()
            && contenitorePerModel.getNumeroGiunzioni() < contenitorePerModel.getNumeroTabelle()-1) {
          messageKey = "warnings.genRic.numeroCriticoGiunzioniAttive";
          if (logger.isInfoEnabled())
            logger.info(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        }
        contenitorePerModel.getDatiGenerali().setCodApp(
            (String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO));
        contenitorePerModel.getDatiGenerali().setProfiloOwner(
            (String) session.getAttribute(CostantiGenerali.PROFILO_ATTIVO));
        
        if (this.bloccaGestioneGruppiDisabilitata(request, false, false)) {
          contenitore.getElencoGruppi().removeAllElements();
          contenitorePerModel.getElencoGruppi().removeAllElements();
          if (contenitorePerModel.getDatiGenerali().getPersonale() == 0) {
            int idGruppo = -1;
            
            if (profiloUtente.getIdGruppi() != null && profiloUtente.getIdGruppi().length > 0) {
              idGruppo = profiloUtente.getIdGruppi()[0].intValue();
            }
            if (idGruppo < 0) {
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
                gruppoRicerca.setId(contenitorePerModel.getDatiGenerali().getIdRicerca());
                GruppoForm gruppoForm = new GruppoForm(gruppoRicerca);
                contenitorePerModel.getElencoGruppi().removeAllElements();
                contenitorePerModel.aggiungiGruppo(gruppoRicerca);
                contenitore.getElencoGruppi().removeAllElements();
                contenitore.aggiungiGruppo(gruppoForm);
              }
            }
          }
        }
        
        if (contenitorePerModel.getDatiGenerali().getIdRicerca() == null) {
          contenitorePerModel = this.ricercheManager.insertRicerca(contenitorePerModel);
          // aggiornamento di 'idRicerca' in tutti gli oggetti che costituiscono
          // la ricerca in sessione dopo l'operazione diinsert nella base dati.
          contenitore.setIdRicerca(contenitorePerModel.getDatiGenerali().getIdRicerca().toString());
        } else {
          this.ricercheManager.updateRicerca(contenitorePerModel);
        }

        // ora che l'oggetto è stato salvato sul database allora si elimina la
        // sentinella che indica che le modifiche non sono state ancora salvate
        session.removeAttribute(CostantiGenerali.SENTINELLA_OGGETTO_MODIFICATO);

        // si reimposta il nome dell'oggetto salvato e pulito da etichette
        // successive
        request.getSession().setAttribute(
            CostantiGenerali.NOME_OGGETTO_SESSION,
            contenitore.getTestata().getNome());
        
        // L.G. 29/03/2007: implementazione ricerca base: in caso di ricerca base
        // si cambia il target dal valore null e lo si controlla nel metodo salva
        if (CostantiGenRicerche.REPORT_BASE == contenitore.getTestata().getFamiglia().intValue()) {
          target = DettaglioRicercaAction.REPORT_BASE_SUCCESS_SALVA;
        } else if (CostantiGenRicerche.REPORT_SQL == contenitore.getTestata().getFamiglia().intValue()) {
          target = DettaglioRicercaAction.REPORT_SQL_SUCCESS_SALVA;
          
        }
        // Nel caso in cui si stia salvando un report modificato in seguito al
        // cambiamento della definizione del profilo attivo e di visualizzazione
        // di un dettaglio di un report, si setta a true lo stato del report
        // rispetto al profilo attivo
        if (! contenitore.getStatoReportNelProfiloAttivo()) {
          contenitore.setStatoReportNelProfiloAttivo(true);
        }
      } catch (DataIntegrityViolationException e) {
        target = SUCCESS_ERROR_SALVA;
        if (new OpzioniUtente(profiloUtente.getFunzioniUtenteAbilitate()).isOpzionePresente(
            CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC)) {
          messageKey = "errors.genRic.salvaRicerca.vincoloUniqueConCodReport";
        } else {
          messageKey = "errors.genRic.salvaRicerca.vincoloUnique";
        }
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
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
    return target;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action visualizza
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniVisualizza() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action caricaPerEstrazione
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniCaricaPerEstrazione() {
    return new CheckOpzioniUtente("");
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action caricaEEsegui
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniCaricaEEsegui() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action modifica
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModifica() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action annullaCrea
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniAnnullaCrea() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action copia
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniCopia() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action salva
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSalva() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action salvaETrova
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSalvaETrova() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action salvaELista
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSalvaELista() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action salvaECrea
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSalvaECrea() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
  /**
   * Funzione che restituisce le opzioni per accedere alla action salvaEEsegui
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniSalvaEEsegui() {
    return new CheckOpzioniUtente(CostantiGeneraliAccount.CONDIZIONE_SCRITTURA_GENRIC);
  }
  
}