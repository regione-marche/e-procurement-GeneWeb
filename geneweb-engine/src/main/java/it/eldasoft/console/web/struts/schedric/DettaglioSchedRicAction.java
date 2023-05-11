/*
 * Created on 25-mag-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.console.web.struts.schedric;

import it.eldasoft.console.bl.schedric.CalcoloDate;
import it.eldasoft.console.bl.schedric.SchedRicManager;
import it.eldasoft.console.db.domain.schedric.DataSchedulazione;
import it.eldasoft.console.db.domain.schedric.SchedRic;
import it.eldasoft.console.web.struts.schedric.wizard.WizardSchedRicAction;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiRicerca;
import it.eldasoft.gene.db.domain.genric.TrovaRicerche;
import it.eldasoft.gene.tags.history.UtilityHistory;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.GestoreEccezioni;
import it.eldasoft.utils.profiles.OpzioniUtente;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

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
 * Action di gestione delle schedulazioni Metodo elimina: elimina il singolo
 * record e riporta alla lista Metodo attiva: modifiche il record passando il
 * campo ATTIVO a 1 e riporta alla lista Metodo disattiva: modifica il record
 * passando il campo ATTIVO a 0 e riporta alla lista Metodo annullaCrea: elimina
 * l'oggetto form dalla sessione e riporta alla pagina da cui si è partiti per
 * il wizard Metodo visualizzaDettaglio: carica il record per la visualizzazione
 * del tab Dati Generali Metodo visualizzaSchedRic: carica il record per la
 * visualizzazione del tab Schedulazione Metodo modificaDettaglio: carica il
 * record per la modifica del tab Dati Generali Metodo modificaSchedRic: carica
 * il record per la modifica del tab Schedulazione
 * 
 * 
 * @author Francesco De Filippis
 */
public class DettaglioSchedRicAction extends DispatchActionBaseNoOpzioni {

  static Logger               logger                                     = Logger.getLogger(DettaglioSchedRicAction.class);

  private static final String FORWARD_SUCCESS_ELIMINA                    = "successElimina";
  private static final String FORWARD_SUCCESS_ATTIVA                     = "successAttiva";
  private static final String FORWARD_SUCCESS_DISATTIVA                  = "successDisattiva";
  // private static final String FORWARD_SUCCESS_MODIFICA = "successModifica";
  private static final String FORWARD_SUCCESS_DETTAGLIO_SOLA_LETTURA     = "successVisualizzaDettaglio";
  private static final String FORWARD_SUCCESS_SCHEDULAZIONE_SOLA_LETTURA = "successVisualizzaSched";
  private static final String FORWARD_SUCCESS_DETTAGLIO_MODIFICA         = "successModificaDettaglio";
  private static final String FORWARD_SUCCESS_SCHEDULAZIONE_MODIFICA     = "successModificaSched";

  protected SchedRicManager   schedRicManager;

  protected TabellatiManager  tabellatiManager;

  protected RicercheManager   ricercheManager;

  protected AccountManager   accountManager;
  
  /**
   * @param ricercheManager
   *        The ricercheManager to set.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * @param tabellatiManager
   *        The tabellatiManager to set.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @param schedRicManager
   *        The schedRicManager to set.
   */
  public void setSchedRicManager(SchedRicManager schedRicManager) {
    this.schedRicManager = schedRicManager;
  }

  /**
   * @param accountManager
   *        The accountManager to set.
   */
  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }

  public ActionForward elimina(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("elimina: inizio metodo");
    String target = FORWARD_SUCCESS_ATTIVA;
    String messageKey = null;

    String id = request.getParameter("idSchedRic");

    try {
      this.schedRicManager.deleteSchedulazioneRicerca(Integer.valueOf(id).intValue());
      request.setAttribute("metodo", "visualizzaLista");
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
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward attiva(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("attiva: inizio metodo");
    String target = FORWARD_SUCCESS_DISATTIVA;
    String messageKey = null;

    String id = request.getParameter("idSchedRic");
    int i = Integer.valueOf(id).intValue();
    try {
      // se la ricerca ha bisogno di parametri per l'esecuzione non effettuo
      // l'attivazione
      // e fornisco un errore per comunicare all'utente di cambiare il report
      if (schedRicManager.isSchedulazioneReportSenzaParametri(i)) {
        SchedRic schedRic = this.schedRicManager.getSchedulazioneRicerca(i);
        DataSchedulazione dataProxEsec = CalcoloDate.calcolaDataProxEsec(schedRic,
            new Date());
        this.schedRicManager.updateAttivaDisattivaSchedulazione(i,
            CostantiSchedRic.ATTIVA, dataProxEsec.getData(), dataProxEsec.getOra(), dataProxEsec.getMinuti());
      } else {
        messageKey = "errors.schedRic.attivaReportConParametri";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
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
    if (logger.isDebugEnabled()) logger.debug("attiva: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward disattiva(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("disattiva: inizio metodo");
    String target = FORWARD_SUCCESS_ELIMINA;
    String messageKey = null;

    String id = request.getParameter("idSchedRic");
    int i = Integer.valueOf(id).intValue();
    // String codApp = (String)
    // request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
    try {
      // SchedRic schedRic = this.schedRicManager.getSchedRicById(i,codApp);
      // Date dataProxEsec = CalcoloDate.calcolaDataProxEsec(schedRic);
      this.schedRicManager.updateAttivaDisattivaSchedulazione(i,
          CostantiSchedRic.DISATTIVA, null, 0, 0);

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
    if (logger.isDebugEnabled()) logger.debug("disattiva: fine metodo");
    return mapping.findForward(target);
  }

  public ActionForward annullaCrea(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("annullaCrea: inizio metodo");

    ActionForward actForward = null;
    UtilityHistory history = UtilityTags.getUtilityHistory(request.getSession());

    // carico dall'history la pagina da cui è stato chiamato il wizard

    try {
      actForward = history.last(request);
    } catch (Throwable t) {
      actForward = GestoreEccezioni.gestisciEccezioneAction(t, this, request,
          logger, mapping);
    }

    // elimino dalla sessione l'oggetto SchedRicForm
    request.getSession().removeAttribute(CostantiSchedRic.OGGETTO_DETTAGLIO);

    if (logger.isDebugEnabled()) logger.debug("annullaCrea: fine metodo ");
    return actForward;
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward visualizzaDettaglio(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled())
      logger.debug("visualizzaDettaglio: inizio metodo");

    String tabAttivo = CostantiSchedRic.DATI_GENERALI;
    String target = visualizza(request, tabAttivo);

    if (logger.isDebugEnabled())
      logger.debug("visualizzaDettaglio: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward visualizzaSchedulazione(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled())
      logger.debug("visualizzaSchedulazione: inizio metodo");

    String tabAttivo = CostantiSchedRic.SCHEDULAZIONE;
    String target = visualizza(request, tabAttivo);

    if (logger.isDebugEnabled())
      logger.debug("visualizzaSchedulazione: fine metodo");
    return mapping.findForward(target);
  }

  private String visualizza(HttpServletRequest request, String tabAttivo) {

    if (logger.isDebugEnabled()) logger.debug("visualizza: inizio metodo");

    String target;
    String id = request.getParameter("idSchedRic");
    if (id == null || id.length() == 0)
      id = ((Integer) request.getAttribute("idSchedRic")).toString();

    String metodo = request.getParameter("metodo");
    String messageKey = null;
    try {
      SchedRic schedRic = this.schedRicManager.getSchedulazioneRicerca((new Integer(
          id)).intValue());

      // L.G. 30/11/2007: e' stata modificata la query getScedulazioneById: ora
      // non estrae più il nome del report associata. Per determinare il nome
      // del report, si estrae la ricerca a partire dall'id.
      // Se il report non viene estratto, significa che il report e' stato
      // cancellato. Pertanto si segnala questa anomalia
      ContenitoreDatiRicerca contenitore = 
          this.ricercheManager.getRicercaByIdRicerca(schedRic.getIdRicerca());
      
      if(contenitore.getDatiGenerali() != null)
        schedRic.setNomeRicerca(contenitore.getDatiGenerali().getNome());
      else {
        messageKey = "warnings.schedRic.noReportEsistente";
        logger.warn(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }
      
      //L.G. 10/09/2007: e' stata modificata la query getSchedRicById in seguito
      //al fatto che alle schedulazioni associate ai report con modello non
      //e' possibile associare il tipo di report e quindi su DB il campo
      //W_SCHEDRIC.FORMATO può assumere il valore <NULL>.
      //Ora la query getSchedById non estrae la descrizione del formato, quindi
      //nel caso in cui venga estratta una schedulazione con il campo FORMATO
      //diverso da <NULL>, bisogna caricare dal tabellato TAB1, con TAB1COD =
      //'W0003' la relativa descrizione
      if(schedRic.getFormato() != null)
      schedRic.setDescFormato(this.tabellatiManager.getDescrTabellato(
          CostantiSchedRic.TABELLATO_FORMATO_SCHEDRIC,
          schedRic.getFormato().toString()));
      //L.G 10/09/2007 fine modifica
      
      SchedRicForm schedRicForm = new SchedRicForm(schedRic, "visualizza");
      if ("visualizzaDettaglio".equalsIgnoreCase(metodo)){
        target = FORWARD_SUCCESS_DETTAGLIO_SOLA_LETTURA;
        
        ProfiloUtente profiloUtente = (ProfiloUtente)
        request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        OpzioniUtente opzioniUtente = new OpzioniUtente(
            profiloUtente.getFunzioniUtenteAbilitate());
        if(opzioniUtente.isOpzionePresente(
            CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_SCHEDULAZIONI)){
          Account utenteEsecutore = this.accountManager.getAccountById(
              new Integer(schedRic.getEsecutore()));
          if(utenteEsecutore != null)
            request.setAttribute("utenteEsecutore",utenteEsecutore.getNome());
          else {
            request.setAttribute("utenteEsecutore", " ");
            messageKey = "warnings.schedRic.esecutoreCancellato";
            logger.warn(this.resBundleGenerale.getString(messageKey));
            this.aggiungiMessaggio(request, messageKey);
          }
        }
      } else
        target = FORWARD_SUCCESS_SCHEDULAZIONE_SOLA_LETTURA;
      this.setMenuTab(request, tabAttivo);
      HttpSession sessione = request.getSession();
      sessione.setAttribute(CostantiGenerali.ID_OGGETTO_SESSION, id);
      sessione.setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
          schedRic.getNome());
      request.setAttribute("schedRicForm", schedRicForm);
      request.setAttribute("metodo", metodo);

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
    return target;
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward modificaDettaglio(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled())
      logger.debug("modificaDettaglio: inizio metodo");

    String tabAttivo = CostantiSchedRic.DATI_GENERALI;
    String target = FORWARD_SUCCESS_DETTAGLIO_MODIFICA;
    String id = request.getParameter("idSchedRic");
    // L.G. 24/09/2007: modifica della lettura della variabile id
    if(id == null)
      id = (String) request.getAttribute("idSchedRic");
    
    String metodo = request.getParameter("metodo");
    String messageKey = null;

    HttpSession sessione = request.getSession();
    try {
      SchedRic schedRic = this.schedRicManager.getSchedulazioneRicerca((new Integer(
          id)).intValue());
      SchedRicForm schedRicForm = new SchedRicForm(schedRic, "modifica");
      
      ContenitoreDatiRicerca contenitore =
        this.ricercheManager.getRicercaByIdRicerca(schedRic.getIdRicerca());
      if(contenitore.getDatiGenerali() != null){
        // se il report scelto ha bisogno di parametri per essere eseguito
        // comunico che deve essere cambiata la selezione
        if (!schedRicManager.isSchedulazioneReportSenzaParametri((new Integer(id)).intValue())) {
          messageKey = "warnings.schedRic.reportConParametri";
          if (logger.isInfoEnabled())
            logger.info(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        }
      } else {
        messageKey = "warnings.schedRic.noReportEsistente";
        if (logger.isInfoEnabled())
          logger.warn(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }
      // Se l'utente connesso ha la gestione completa delle ricerche allora la 
      // lista contiene tutti i report se invece ha limitazioni vedrà solamente
      // le ricerche predefinite legate alla sua utenza
      ProfiloUtente profiloUtente = (ProfiloUtente)
              request.getSession().getAttribute("profiloUtente");
      OpzioniUtente opzioniUtente = new OpzioniUtente(
              profiloUtente.getFunzioniUtenteAbilitate());
      List<?> listaRicerche = null;
      
      if(opzioniUtente.isOpzionePresente(
            CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_SCHEDULAZIONI)){
        //Lista ricerche: carico tutte le ricerche passando come parametro alla
        //funzione un oggetto TrovaRicerche vuoto in modo da non imporre filtri
        TrovaRicerche filtroRicerca = new TrovaRicerche();
        filtroRicerca.setCodiceApplicazione((String)
            request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO));
        filtroRicerca.setProfiloOwner((String)
            request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO));
        
        listaRicerche = this.ricercheManager.getRicercheSenzaParametri(
            filtroRicerca,true);
        List<Account> listaUtentiEsecutori = this.accountManager.getListaAccountByCodProCodApp(
            (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO),
            (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO));
        
        request.setAttribute("listaUtentiEsecutori", listaUtentiEsecutori);
      } else {
        listaRicerche = this.ricercheManager.getRicerchePredefiniteSenzaParametri(
            profiloUtente.getId(),
            (String) request.getSession().getAttribute(
                CostantiGenerali.MODULO_ATTIVO),
            (String) request.getSession().getAttribute(
                CostantiGenerali.PROFILO_ATTIVO), true);
      }
      request.setAttribute("utenteEsecutore", new Integer(schedRic.getEsecutore()));
      request.setAttribute("listaRicerche", listaRicerche);

      List<Tabellato> listaFormatoSched = this.tabellatiManager.getTabellato(
              CostantiSchedRic.TABELLATO_FORMATO_SCHEDRIC);
      request.setAttribute("listaFormatoSched", listaFormatoSched);
      this.setMenuTabEdit(request, tabAttivo);
      // preparo la modifica
      sessione.setAttribute(CostantiGenerali.ID_OGGETTO_SESSION, id);
      sessione.setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
          schedRic.getNome());
      
      request.setAttribute("schedRicForm", schedRicForm);
      request.setAttribute("metodo", metodo);
      
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);
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

    // String target = modifica(request, tabAttivo);

    if (logger.isDebugEnabled())
      logger.debug("modificaDettaglio: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  public ActionForward modificaSchedulazione(ActionMapping mapping,
      ActionForm form, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled())
      logger.debug("modificaSchedulazione: inizio metodo");

    String tabAttivo = CostantiSchedRic.SCHEDULAZIONE;
    String target = FORWARD_SUCCESS_SCHEDULAZIONE_MODIFICA;
    String id = request.getParameter("idSchedRic");
    String metodo = request.getParameter("metodo");
    String messageKey = null;

    HttpSession sessione = request.getSession();
    try {

      SchedRic schedRic = this.schedRicManager.getSchedulazioneRicerca((new Integer(
          id)).intValue());
      SchedRicForm schedRicForm = new SchedRicForm(schedRic, "modifica");

      ContenitoreDatiRicerca contenitore =
        this.ricercheManager.getRicercaByIdRicerca(schedRic.getIdRicerca());
      if(contenitore.getDatiGenerali() != null){
        if(!schedRicManager.isSchedulazioneReportSenzaParametri((new Integer(id)).intValue())) {      
          // se la schedulazione è associata ad un report con parametri non
          // permetto la modifica dei dati di schedulazione e torno alla 
          // visualizzazione
          target = FORWARD_SUCCESS_SCHEDULAZIONE_SOLA_LETTURA;
          messageKey = "errors.schedRic.modificaReportConParametri";
          if (logger.isInfoEnabled())
            logger.info(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
          target = visualizza(request, tabAttivo);
          return mapping.findForward(target);
        }
      }
      // carico le lista dei dati
      List<Tabellato> listaTipo = tabellatiManager.getTabellato(CostantiSchedRic.TABELLATO_TIPO_SCHEDRIC);
      request.setAttribute("listaTipo", listaTipo);
      request.setAttribute("listaOre", this.caricaOre());
      request.setAttribute("listaMinuti", this.caricaMinuti());
      request.setAttribute("listaGiorniAnno", this.caricaGiorniAnno());
      request.setAttribute("listaSettimane", this.caricaSettimane());
      request.setAttribute("listaGiorniMese", this.caricaGiorniMese());
      request.setAttribute("listaSettimana", this.caricaTabSettimana());
      request.setAttribute("listaGiorniSettimana",
          this.caricaGiorniSettimana());
      request.setAttribute("listaIntervalli", WizardSchedRicAction.caricaIntervalliMinuti());

      // preparo la modifica
      this.setMenuTabEdit(request, tabAttivo);
      sessione = request.getSession();
      sessione.setAttribute(CostantiGenerali.ID_OGGETTO_SESSION, id);
      sessione.setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
          schedRic.getNome());
      request.setAttribute("schedRicForm", schedRicForm);
      request.setAttribute("metodo", metodo);
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

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

    if (logger.isDebugEnabled())
      logger.debug("modificaSchedulazione: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab
   * 
   * @param request
   */
  protected void setMenuTab(HttpServletRequest request, String tabAttivo) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiSchedRic.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(tabAttivo);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiSchedRic.DATI_GENERALI, CostantiSchedRic.SCHEDULAZIONE });
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(tabAttivo);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiSchedRic.DATI_GENERALI, CostantiSchedRic.SCHEDULAZIONE });
      sessione.setAttribute(CostantiSchedRic.NOME_GESTORE_TAB, gestoreTab);
    }
  }

  /**
   * Set o update dell'oggetto GestioneTab in sessione per la visualizzazione
   * del menua tab in fase di editing della lista utenti di gruppo
   * 
   * @param request
   */
  protected void setMenuTabEdit(HttpServletRequest request, String tabAttivo) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiSchedRic.NOME_GESTORE_TAB);
    if (obj != null) {
      GestioneTab gestoreTab = (GestioneTab) obj;
      gestoreTab.setTabAttivo(tabAttivo);
      gestoreTab.setTabSelezionabili(null);
    } else {
      GestioneTab gestoreTab = new GestioneTab();
      gestoreTab.setTabAttivo(tabAttivo);
      gestoreTab.setTabSelezionabili(null);
      sessione.setAttribute(CostantiSchedRic.NOME_GESTORE_TAB, gestoreTab);
    }
  }

  private Vector<Tabellato> caricaOre() {

    Vector<Tabellato> ore = new Vector<Tabellato>();
    Tabellato ora = null;
    for (int i = 0; i < 24; i++) {
      ora = new Tabellato();
      ora.setTipoTabellato("" + i);
      if (i < 10)
        ora.setDescTabellato("0" + i);
      else
        ora.setDescTabellato("" + i);
      ore.add(ora);
    }
    return ore;
  }

  private Vector<Tabellato> caricaMinuti() {

    Vector<Tabellato> minuti = new Vector<Tabellato>();
    Tabellato minuto = null;
    int j;
    for (int i = 0; i < 12; i++) {
      j = i * 5;
      minuto = new Tabellato();
      minuto.setTipoTabellato("" + j);
      if (j < 10)
        minuto.setDescTabellato("0" + j);
      else
        minuto.setDescTabellato("" + j);
      minuti.add(minuto);
    }
    return minuti;
  }

  private Vector<Tabellato> caricaGiorniMese() {

    Vector<Tabellato> giorni = new Vector<Tabellato>();
    Tabellato giorno = null;
    for (int i = 1; i < 32; i++) {
      giorno = new Tabellato();
      giorno.setTipoTabellato("" + i);
      giorno.setDescTabellato("" + i);
      giorni.add(giorno);
    }
    return giorni;
  }

  private Vector<Tabellato> caricaGiorniAnno() {

    Vector<Tabellato> giorni = new Vector<Tabellato>();
    Tabellato giorno = null;
    for (int i = 1; i < 366; i++) {
      giorno = new Tabellato();
      giorno.setTipoTabellato("" + i);
      giorno.setDescTabellato("" + i);
      giorni.add(giorno);
    }
    return giorni;
  }

  private Vector<Tabellato> caricaSettimane() {

    Vector<Tabellato> settimane = new Vector<Tabellato>();
    Tabellato settimana = null;
    for (int i = 1; i < 53; i++) {
      settimana = new Tabellato();
      settimana.setTipoTabellato("" + i);
      settimana.setDescTabellato("" + i);
      settimane.add(settimana);
    }
    return settimane;
  }

  private Vector<Tabellato> caricaTabSettimana() {

    Vector<Tabellato> settimane = new Vector<Tabellato>();
    Tabellato settimana = null;
    int j = 0;
    for (int i = 0; i < CostantiSchedRic.TABELLATO_SETTIMANA.length; i++) {
      settimana = new Tabellato();
      j = i + 1;
      settimana.setTipoTabellato("" + j);
      settimana.setDescTabellato(CostantiSchedRic.TABELLATO_SETTIMANA[i]);
      settimane.add(settimana);
    }
    return settimane;
  }

  private Vector<Tabellato> caricaGiorniSettimana() {

    Vector<Tabellato> settimane = new Vector<Tabellato>();
    Tabellato settimana = null;
    int j = 0;
    for (int i = 0; i < CostantiSchedRic.TABELLATO_GIORNI_SETTIMANA.length; i++) {
      settimana = new Tabellato();
      j = i + 1;
      settimana.setTipoTabellato("" + j);
      settimana.setDescTabellato(CostantiSchedRic.TABELLATO_GIORNI_SETTIMANA[i]);
      settimane.add(settimana);
    }
    return settimane;
  }

}
