/*
 * Created on 09-mag-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.console.web.struts.schedric.wizard;

import it.eldasoft.console.web.struts.schedric.CostantiSchedRic;
import it.eldasoft.console.web.struts.schedric.SchedRicForm;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.domain.genric.TrovaRicerche;
import it.eldasoft.utils.profiles.OpzioniUtente;

import java.io.IOException;
import java.util.ArrayList;
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
 * Gestore del wizard di creazione di una schedulazione per una ricerca
 * per il controllo della successione delle pagine e della visualizzazione 
 * dell'avanzamento del wizard stesso.
 * 
 * @author Francesco De Filippis
 */
public class WizardSchedRicAction extends ActionBaseNoOpzioni {
  
  // Target delle diverse chiamate nel wizard
  private static final String SUCCESS_REPORT      = "successReport"; 
  private static final String SUCCESS_FREQUENZA   = "successFrequenza";
  private static final String SUCCESS_GIORNO      = "successGiorno"; 
  private static final String SUCCESS_SETTIMANA   = "successSettimana";
  private static final String SUCCESS_MESE        = "successMese";
  private static final String SUCCESS_UNICA       = "successUnica"; 
  private static final String SUCCESS_RISULTATO   = "successRisultato";
  private static final String SUCCESS_TITOLO      = "successTitolo";
  
  static Logger logger = Logger.getLogger(WizardSchedRicAction.class);
  
  /**
   * Reference alla classe di business logic per l'accesso a W_RICERCHE 
   */
  private RicercheManager ricercheManager;

  /**
   * Reference alla classe di business logic per l'accesso ai tabellati
   */
  private TabellatiManager tabellatiManager;

  /**
   * Reference alla classe di business logic per l'accesso a UDRSYS
   */
  private AccountManager accountManager;
  
  /**
   * @param ricercheManager The ricercheManager to set.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * @param tabellatiManager tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @param tabellatiManager tabellatiManager da settare internamente alla classe.
   */
  public void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }


  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {

    if(logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");
    
    // Dal request leggo il parametro pageFrom, il quale contiene il codice della
    // pagina di destinazione
    String pageTo = request.getParameter("pageTo");
    if(pageTo == null || pageTo.length() == 0)
      pageTo = (String )request.getAttribute("pageTo");
    
    String target = null;
    
    HttpSession session = request.getSession();
    SchedRicForm schedRicForm = (SchedRicForm)
            session.getAttribute(CostantiSchedRic.OGGETTO_DETTAGLIO);

    if (schedRicForm == null) {
      schedRicForm = new SchedRicForm();
      schedRicForm.setNoOutputVuoto(true);
      schedRicForm.setCodiceApplicazione((String) request.getSession().getAttribute(
          CostantiGenerali.MODULO_ATTIVO));
    }

    if(CostantiWizard.CODICE_PAGINA_REPORT.equals(pageTo)){
      
      target = this.selezioneReport(request);
    } else if(CostantiWizard.CODICE_PAGINA_FREQUENZA.equals(pageTo)){
      
      target = WizardSchedRicAction.SUCCESS_FREQUENZA;
    } else if(CostantiWizard.CODICE_PAGINA_GIORNO.equals(pageTo)){
       
      target = this.configuraFrequenzaGiorno(request);
      request.setAttribute("pageFrom",CostantiWizard.CODICE_PAGINA_GIORNO);
    } else if(CostantiWizard.CODICE_PAGINA_SETTIMANA.equals(pageTo)){
      
      target = this.configuraFrequenzaSettimana(request);
      request.setAttribute("pageFrom",CostantiWizard.CODICE_PAGINA_SETTIMANA);
    } else if(CostantiWizard.CODICE_PAGINA_MESE.equals(pageTo)){
      
      target = this.configuraFrequenzaMese(request,schedRicForm);
      request.setAttribute("pageFrom",CostantiWizard.CODICE_PAGINA_MESE);
    } else if(CostantiWizard.CODICE_PAGINA_UNICA.equals(pageTo)){
      
      target = this.configuraFrequenzaUnica(request);
      request.setAttribute("pageFrom",CostantiWizard.CODICE_PAGINA_UNICA);
    } else if(CostantiWizard.CODICE_PAGINA_RISULTATO.equals(pageTo)){
      
      target = this.configuraRisultato(request,schedRicForm);
    } else if(CostantiWizard.CODICE_PAGINA_TITOLO.equals(pageTo)){
      
      target = this.configurazioneTitolo(request);
      request.setAttribute("pageFrom", request.getAttribute("pageFrom"));
    } else {
      // Si è tentato l'accesso ad un tab non previsto, e quindi si effettua
      // la tracciatura su log e si visualizza la pagina di selezione report come default
      String messageKey = "info.genRic.wizardBase.defaultPage";
      logger.warn(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
      target = this.selezioneReport(request);
    }
    
    request.setAttribute("schedRicForm",schedRicForm);
    
    if(target != null && !target.equals(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE))
      this.gestioneVociAvanzamento(request, pageTo);
    
    if(logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return mapping.findForward(target);
  }

  
  private String selezioneReport(HttpServletRequest request){
    
    if (logger.isDebugEnabled()) logger.debug("selezioneReport: inizio metodo");

    // target di default
    String target = WizardSchedRicAction.SUCCESS_REPORT;
    
    String messageKey = null;
    
    try {
      //carico i dati per la visualizzazione della pagina
          
      // Se l'utente connesso ha la gestione completa delle ricerche allora la 
      // lista contiene tutti i report se invece ha limitazioni vedrà solamente
      // le ricerche predefinite legate alla sua utenza
      ProfiloUtente profiloUtente = (ProfiloUtente)
              request.getSession().getAttribute("profiloUtente");
      OpzioniUtente opzioniUtente = new OpzioniUtente(
              profiloUtente.getFunzioniUtenteAbilitate());
      List<?> listaRicerche = null;
      
      if (opzioniUtente.isOpzionePresente(
            CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_SCHEDULAZIONI)) {
        //Lista ricerche: carico tutte le ricerche passando come parametro alla
        //funzione un oggetto TrovaRicerche vuoto in modo da non imporre filtri
        TrovaRicerche filtroRicerca = new TrovaRicerche();
        filtroRicerca.setCodiceApplicazione((String)
            request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO));
        filtroRicerca.setProfiloOwner((String)
            request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO));
        
        listaRicerche = this.ricercheManager.getRicercheSenzaParametri(
            filtroRicerca,true);
      } else {
        listaRicerche = this.ricercheManager.getRicerchePredefiniteSenzaParametri(
            profiloUtente.getId(),
            (String) request.getSession().getAttribute(
                CostantiGenerali.MODULO_ATTIVO),
            (String) request.getSession().getAttribute(
                CostantiGenerali.PROFILO_ATTIVO), true);
      }
      request.setAttribute("listaRicerche", listaRicerche);
      
      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
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
    
    if (logger.isDebugEnabled()) logger.debug("selezioneReport: fine metodo");
    return target;  
  }


  private String configuraFrequenzaGiorno(HttpServletRequest request){
    if (logger.isDebugEnabled()) logger.debug("configuraFrequenzaGiorno: inizio metodo");

    // target di default
    String target = WizardSchedRicAction.SUCCESS_GIORNO;

    request.setAttribute("listaOre", this.caricaOre());
    request.setAttribute("listaMinuti", this.caricaMinuti());
    request.setAttribute("listaGiorni", this.caricaGiorniAnno());
    request.setAttribute("listaIntervalli", WizardSchedRicAction.caricaIntervalliMinuti());
  
    // set nel request del parameter per disabilitare la navigazione in fase
    // di editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);
    
    if (logger.isDebugEnabled()) logger.debug("configuraFrequenzaGiorno: fine metodo");
    return target;  
  }
  
  private String configuraFrequenzaSettimana(HttpServletRequest request){
    if (logger.isDebugEnabled()) logger.debug("configuraFrequenzaSettimana: inizio metodo");

    // target di default
    String target = WizardSchedRicAction.SUCCESS_SETTIMANA;

    request.setAttribute("listaOre", this.caricaOre());
    request.setAttribute("listaMinuti", this.caricaMinuti());
    request.setAttribute("listaSettimane", this.caricaSettimane());
  
    // set nel request del parameter per disabilitare la navigazione in fase
    // di editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);
    
    if (logger.isDebugEnabled()) logger.debug("configuraFrequenzaSettimana: fine metodo");
    return target;
  }
  
  private String configuraFrequenzaMese(HttpServletRequest request,SchedRicForm schedRicForm){
    if (logger.isDebugEnabled()) logger.debug("configuraFrequenzaMese: inizio metodo");

    // target di default
    String target = WizardSchedRicAction.SUCCESS_MESE;

    request.setAttribute("listaOre", this.caricaOre());
    request.setAttribute("listaMinuti", this.caricaMinuti());
    request.setAttribute("listaGiorni", this.caricaGiorniMese());
    request.setAttribute("listaSettimana", this.caricaTabSettimana());
    request.setAttribute("listaGiorniSettimana",this.caricaGiorniSettimana());
  
    //se è la prima volta che si passa dalla painificazione mensile setto tutti i check
    //dei mesi a true
    if (!schedRicForm.isOpzioneGennaio() &&
        !schedRicForm.isOpzioneFebbraio() &&
        !schedRicForm.isOpzioneMarzo() &&
        !schedRicForm.isOpzioneAprile() &&
        !schedRicForm.isOpzioneMaggio() &&
        !schedRicForm.isOpzioneGiugno() &&
        !schedRicForm.isOpzioneLuglio() &&
        !schedRicForm.isOpzioneAgosto() &&
        !schedRicForm.isOpzioneSettembre() &&
        !schedRicForm.isOpzioneOttobre() &&
        !schedRicForm.isOpzioneNovembre() &&
        !schedRicForm.isOpzioneDicembre()) {
      schedRicForm.setOpzioneGennaio(true);
      schedRicForm.setOpzioneFebbraio(true);
      schedRicForm.setOpzioneMarzo(true);
      schedRicForm.setOpzioneAprile(true);
      schedRicForm.setOpzioneMaggio(true);
      schedRicForm.setOpzioneGiugno(true);
      schedRicForm.setOpzioneLuglio(true);
      schedRicForm.setOpzioneAgosto(true);
      schedRicForm.setOpzioneSettembre(true);
      schedRicForm.setOpzioneOttobre(true);
      schedRicForm.setOpzioneNovembre(true);
      schedRicForm.setOpzioneDicembre(true);
    }
    // set nel request del parameter per disabilitare la navigazione in fase
    // di editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);
    
    if (logger.isDebugEnabled()) logger.debug("configuraFrequenzaMese: fine metodo");
    return target;
  }
  
  private String configuraFrequenzaUnica(HttpServletRequest request){
    if (logger.isDebugEnabled()) logger.debug("configuraFrequenzaUnica: inizio metodo");

    // target di default
    String target = WizardSchedRicAction.SUCCESS_UNICA;

    request.setAttribute("listaOre", this.caricaOre());
    request.setAttribute("listaMinuti", this.caricaMinuti());
  
    // set nel request del parameter per disabilitare la navigazione in fase
    // di editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);

    if (logger.isDebugEnabled()) logger.debug("configuraFrequenzaUnica: fine metodo");
    return target;
  }
  
  private String configuraRisultato(HttpServletRequest request,SchedRicForm schedRicForm){
    if (logger.isDebugEnabled()) logger.debug("configuraRisultato: inizio metodo");

    // target di default
    String target = WizardSchedRicAction.SUCCESS_RISULTATO;
    
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);
    
    String messageKey = null;
    String pageFrom = (String)request.getAttribute("pageFrom");
    if (pageFrom == null || "".equals(pageFrom)) {
      String tipo = schedRicForm.getTipo();
      if (CostantiSchedRic.GIORNO.equalsIgnoreCase(tipo)) {
        request.setAttribute("pageFrom",CostantiWizard.CODICE_PAGINA_GIORNO);
      } else if (CostantiSchedRic.SETTIMANA.equalsIgnoreCase(tipo)){
        request.setAttribute("pageFrom",CostantiWizard.CODICE_PAGINA_SETTIMANA);
      } else if (CostantiSchedRic.MESE.equalsIgnoreCase(tipo)){
        request.setAttribute("pageFrom",CostantiWizard.CODICE_PAGINA_MESE);
      } else if (CostantiSchedRic.UNICA.equalsIgnoreCase(tipo)){
        request.setAttribute("pageFrom",CostantiWizard.CODICE_PAGINA_UNICA);
      }
    } else
      request.setAttribute("pageFrom",pageFrom);

    try {
      List<Tabellato> listaFormatoSched = this.tabellatiManager.getTabellato(
          CostantiSchedRic.TABELLATO_FORMATO_SCHEDRIC);
      request.setAttribute("listaFormatoSched", listaFormatoSched);
    
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
    
    if (logger.isDebugEnabled()) logger.debug("configuraRisultato: fine metodo");
    return target;
  }
  
  private String configurazioneTitolo(HttpServletRequest request){
    if (logger.isDebugEnabled()) logger.debug("configurazioneTitolo: inizio metodo");
    // target di default
    String target = WizardSchedRicAction.SUCCESS_TITOLO;
    String messageKey = null;
    try {
      ProfiloUtente profiloUtente = (ProfiloUtente)
          request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      OpzioniUtente opzioniUtente = new OpzioniUtente(
          profiloUtente.getFunzioniUtenteAbilitate());

      if(opzioniUtente.isOpzionePresente(
            CostantiGeneraliAccount.OPZIONI_AMMINISTRAZIONE_SCHEDULAZIONI)){
        List<Account> listaUtentiEsecutori = this.accountManager.getListaAccountByCodProCodApp(
            (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO),
            (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO));
        request.setAttribute("listaUtentiEsecutori", listaUtentiEsecutori);
      }
      request.setAttribute("utenteEsecutore", new Integer(profiloUtente.getId()));
      
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
    if (logger.isDebugEnabled()) logger.debug("configurazioneTitolo: fine metodo");
    return target;
  }
 
  private Vector<Tabellato> caricaOre() {
    
    Vector<Tabellato> ore = new Vector<Tabellato>();
    Tabellato ora = null;
    for (int i=0; i<24; i++) {
      ora = new Tabellato();
      ora.setTipoTabellato(""+i);
      if (i<10)
        ora.setDescTabellato("0"+i);
      else
        ora.setDescTabellato(""+i);
      ore.add(ora);
    }
    return ore;
  }

  private Vector<Tabellato> caricaMinuti() {
    
    Vector<Tabellato> minuti = new Vector<Tabellato>();
    Tabellato minuto = null;
    int j;
    for (int i=0;i<12;i++){
      j = i*5;
      minuto = new Tabellato();
      minuto.setTipoTabellato(""+j);
      if (j<10)
        minuto.setDescTabellato("0"+j);
      else
        minuto.setDescTabellato(""+j);
      minuti.add(minuto);
    }
    return minuti;
  }
  
  private Vector<Tabellato> caricaGiorniMese() {
    
    Vector<Tabellato> giorni = new Vector<Tabellato>();
    Tabellato giorno = null;
    for (int i=1;i<32;i++){
      giorno = new Tabellato();
      giorno.setTipoTabellato(""+i);
      giorno.setDescTabellato(""+i);
      giorni.add(giorno);
    }
    return giorni;
  }

  private Vector<Tabellato> caricaGiorniAnno() {
    
    Vector<Tabellato> giorni = new Vector<Tabellato>();
    Tabellato giorno = null;
    for (int i=1;i<366;i++){
      giorno = new Tabellato();
      giorno.setTipoTabellato(""+i);
      giorno.setDescTabellato(""+i);
      giorni.add(giorno);
    }
    return giorni;
  }

  public static Vector<Tabellato> caricaIntervalliMinuti() {
    Vector<Tabellato> minuti = new Vector<Tabellato>();
    Tabellato minuto = null;

    // multipli di 5 minuti divisori dell'ora 
    for (int i = 5; i <= 30; i = i + 5) {
      if (60 % i == 0) {
        minuto = new Tabellato();
        minuto.setTipoTabellato("" + i);
        minuto.setDescTabellato(i + " minuti");
        minuti.add(minuto);
      }
    }

    // l'ora
    minuto = new Tabellato();
    minuto.setTipoTabellato("60");
    minuto.setDescTabellato("1 ora");
    minuti.add(minuto);

    // multipli di ora divisori del giorno 
    for (int i = 120; i <= 720; i = i + 60) {
      if (1440 % i == 0) {
        minuto = new Tabellato();
        minuto.setTipoTabellato("" + i);
        minuto.setDescTabellato(i/60 + " ore");
        minuti.add(minuto);
      }
    }

    return minuti;
  }

  private Vector<Tabellato> caricaSettimane() {
    
    Vector<Tabellato> settimane = new Vector<Tabellato>();
    Tabellato settimana = null;
    for (int i=1;i<53;i++){
      settimana = new Tabellato();
      settimana.setTipoTabellato(""+i);
      settimana.setDescTabellato(""+i);
      settimane.add(settimana);
    }
    return settimane;
  }

  private Vector<Tabellato> caricaTabSettimana() {
    
    Vector<Tabellato> settimane = new Vector<Tabellato>();
    Tabellato settimana = null;
    int j=0;
    for (int i=0;i<CostantiSchedRic.TABELLATO_SETTIMANA.length;i++){
      settimana = new Tabellato();
      j = i + 1;
      settimana.setTipoTabellato(""+j);
      settimana.setDescTabellato(CostantiSchedRic.TABELLATO_SETTIMANA[i]);
      settimane.add(settimana);
    }
    return settimane;
  }

  private Vector<Tabellato> caricaGiorniSettimana() {
    
    Vector<Tabellato> settimane = new Vector<Tabellato>();
    Tabellato settimana = null;
    int j=0;
    for (int i=0;i<CostantiSchedRic.TABELLATO_GIORNI_SETTIMANA.length;i++){
      settimana = new Tabellato();
      j = i+1;
      settimana.setTipoTabellato(""+j);
      settimana.setDescTabellato(CostantiSchedRic.TABELLATO_GIORNI_SETTIMANA[i]);
      settimane.add(settimana);
    }
    return settimane;
  }
 
  private void gestioneVociAvanzamento(HttpServletRequest request,
      String paginaAttiva){
    List<String> pagineVisitate = new ArrayList<String>();
    List<String> pagineDaVisitare = new ArrayList<String>();
        
    if(paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_REPORT)){
      pagineVisitate.add(CostantiWizard.TITOLO_REPORT);
  
      pagineDaVisitare.add(CostantiWizard.TITOLO_FREQUENZA);
      pagineDaVisitare.add(CostantiWizard.TITOLO_SCHEDULAZIONE);
      pagineDaVisitare.add(CostantiWizard.TITOLO_RISULTATO);
      pagineDaVisitare.add(CostantiWizard.TITOLO_TITOLO);
      
    } else if(paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_FREQUENZA)) {
      
      pagineVisitate.add(CostantiWizard.TITOLO_REPORT);
      pagineVisitate.add(CostantiWizard.TITOLO_FREQUENZA);
      
      pagineDaVisitare.add(CostantiWizard.TITOLO_SCHEDULAZIONE);
      pagineDaVisitare.add(CostantiWizard.TITOLO_RISULTATO);
      pagineDaVisitare.add(CostantiWizard.TITOLO_TITOLO);
      
    } else if(paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_GIORNO) || 
    paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_SETTIMANA) ||
    paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_MESE) || 
    paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_UNICA)){
      pagineVisitate.add(CostantiWizard.TITOLO_REPORT);
      pagineVisitate.add(CostantiWizard.TITOLO_FREQUENZA);
      pagineVisitate.add(CostantiWizard.TITOLO_SCHEDULAZIONE);
      
      pagineDaVisitare.add(CostantiWizard.TITOLO_RISULTATO);
      pagineDaVisitare.add(CostantiWizard.TITOLO_TITOLO);
      
    } else if(paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_RISULTATO)) { 
      pagineVisitate.add(CostantiWizard.TITOLO_REPORT);
      pagineVisitate.add(CostantiWizard.TITOLO_FREQUENZA);
      pagineVisitate.add(CostantiWizard.TITOLO_SCHEDULAZIONE);
      pagineVisitate.add(CostantiWizard.TITOLO_RISULTATO);
      pagineDaVisitare.add(CostantiWizard.TITOLO_TITOLO);
      
    } else if(paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_TITOLO)){
      pagineVisitate.add(CostantiWizard.TITOLO_REPORT);
      pagineVisitate.add(CostantiWizard.TITOLO_FREQUENZA);
      pagineVisitate.add(CostantiWizard.TITOLO_SCHEDULAZIONE);
      pagineVisitate.add(CostantiWizard.TITOLO_RISULTATO);
      pagineVisitate.add(CostantiWizard.TITOLO_TITOLO);
      
    } 
    
    request.setAttribute("pagineVisitate", pagineVisitate);
    request.setAttribute("pagineDaVisitare", pagineDaVisitare);
  }

}