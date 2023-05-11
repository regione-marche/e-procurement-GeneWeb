/*
 * Created on 02-ago-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod.impexp;

import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.genmod.ContenitoreDatiImport;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genmod.TrovaModelli;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * Action per la gestione del wizard per l'importazione da file xml della
 * definizione dei modelli
 * 
 * @author Francesco.DeFilippis
 */
public class WizardImportaModelloAction extends ActionBaseNoOpzioni {

  /**   logger di Log4J   */
  static Logger logger = Logger.getLogger(WizardImportaModelloAction.class);
  
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }
  
  /**
   * Reference alla classe di business logic per l'accesso a W_GRUPPI e/o
   * W_GRPMOD
   */
  private GruppiManager       gruppiManager;

  /**
   * Reference alla classe di business logic per l'accesso a W_MODELLI
   */
  private ModelliManager    modelliManager;

  /**
   * Reference alla classe di businnes logic per i tabellati
   */
  private TabellatiManager  tabellatiManager;
  
  /**
   * @param tabellatiManager The tabellatiManager to set.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

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
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }
  
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) 
          throws IOException, ServletException {
    if(logger.isDebugEnabled()) {
      logger.debug("runAction: inizio metodo");
    }
    String target = null;
    
    // Dal request leggo il parametro pageFrom, il quale contiene il codice
    // della pagina di destinazione
    String pageTo = request.getParameter("pageTo");
    if (pageTo == null || pageTo.length() == 0) {
      pageTo = (String) request.getAttribute("pageTo");
    }
    
    if (CostantiWizard.CODICE_PAGINA_UPLOAD.equals(pageTo)) {
      // Inizializzazione della pagina di upload del file XML contenente la
      // definizione del modello da importare
      
      // Se in sessione è presente l'attributo individuato dalla costante 
      // CostantiGenModelli.OGGETTO_DETTAGLIO allora lo rimuovo, sia che esso 
      // sia un modello precedentemente caricato in sessione, sia che si ritorni
      // alla pagina di upload del wizard di import
      if(request.getSession().getAttribute(CostantiGenModelli.OGGETTO_DETTAGLIO) != null)
        request.getSession().removeAttribute(CostantiGenModelli.OGGETTO_DETTAGLIO);
      
      target = CostantiWizard.SUCCESS_UPLOAD;
    } else if (CostantiWizard.CODICE_PAGINA_DATI_GENERALI.equals(pageTo)) {
      // Inizializzazione della pagina di visualizzazione dei dati generali
      // del modello da importare con eventuale domanda sul tipo di
      // importazione da effettuare
      target = this.editDatiGenerali(request);
    } else if (CostantiWizard.CODICE_PAGINA_PUBBLICAZIONE.equals(pageTo)) {

      // l'accesso alla pagina per la pubblicazione è valida solo se 
      // l'applicazione e' avviata in configurazion aperta
      if(request.getSession().getServletContext().getAttribute(
          CostantiGenerali.ATTR_CONFIGURAZIONE_CHIUSA).equals("1")){
        
        ContenitoreDatiImport contenitoreObj = (ContenitoreDatiImport)
            request.getSession().getAttribute(
                CostantiGenModelli.OGGETTO_DETTAGLIO);
        // In configurazione chiusa non viene chiesto se pubblicare il modello,
        // ma viene chiesto a quali gruppi pubblicare, se la gestione dei gruppi
        // e' abilitata. Nell'oggetto ContenitoreDatiImport si setta l'attributo
        // pubblicaNuovoModello a true
        if (this.bloccaGestioneGruppiDisabilitata(request, false, false)) {
          contenitoreObj.setPubblicaNuovoModello(false);
          target = CostantiWizard.SUCCESS_FINE;
        } else {
          contenitoreObj.setPubblicaNuovoModello(true);
          target = CostantiWizard.SUCCESS_EDIT_GRUPPI;
          request.setAttribute("configurazioneChiusa", "1");
          request.setAttribute("pageFrom", request.getAttribute("pageFrom"));
        }
      } else {
        target = CostantiWizard.SUCCESS_DOMANDA_PUBBLICA;
      }
      
      request.setAttribute("pageFrom", CostantiWizard.CODICE_PAGINA_DATI_GENERALI);
    } else if (CostantiWizard.CODICE_PAGINA_GRUPPI.equals(pageTo)) {

      if (this.bloccaGestioneGruppiDisabilitata(request, false, false)) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        String messageKey = "errors.applicazione.gruppiDisabilitati";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      } else {
        
        if (! CostantiWizard.CODICE_PAGINA_GRUPPI.equals(request.getAttribute("pageFrom")))
          request.setAttribute("pageFrom", request.getAttribute("pageFrom"));
        else
          request.setAttribute("pageFrom", CostantiWizard.CODICE_PAGINA_PUBBLICAZIONE); 
        target = this.editGruppi(request);
      }
    } else {
      // Si è tentato l'accesso ad un tab non previsto, e quindi si effettua
      // la tracciatura su log e si visualizza la pagina di inserimento
      // dell'argomento del modello come default
      String messageKey = "info.genMod.defaultPage";
      logger.warn(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
      target = CostantiWizard.ERROR_IMPORT_MODELLI;
      
      // Cancellazione dalla sessione del contenitore dati import
      request.getSession().removeAttribute(CostantiGenModelli.OGGETTO_DETTAGLIO);
    }
    
    if (target != null
        && !target.equals(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE)) {
      this.gestioneVociAvanzamento(request, pageTo);
      
      if (!target.equals(CostantiWizard.SUCCESS_FINE)) {
        // set nel request del parameter per disabilitare la navigazione
        request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
            CostantiGenerali.DISABILITA_NAVIGAZIONE);
      }
    }

    if(logger.isDebugEnabled()) {
      logger.debug("runAction: fine metodo");
    }
    return mapping.findForward(target);
  }

  /**
   * Metodo per l'inizializzazione della pagina di visualizzazione dei dati
   * generali del modello da importare. 
   */
  private String editDatiGenerali(HttpServletRequest request) {
    if (logger.isDebugEnabled()) {
      logger.debug("editDatiGen: inizio metodo");
    }

    // target di default
    String target = CostantiWizard.SUCCESS_DATI_GENERALI;
    String messageKey = null;

    DatiModello datiGenerali = null;
    HttpSession sessione = request.getSession();
    ContenitoreDatiImport contenitoreObj = (ContenitoreDatiImport)
      sessione.getAttribute(CostantiGenModelli.OGGETTO_DETTAGLIO);
    if (contenitoreObj.getContenitoreDatiGenerali() != null ) {
      datiGenerali = contenitoreObj.getContenitoreDatiGenerali().getDatiGenModello();
    }
    
    // L.G. 07/04/2015: introduzione della versione del modello. In fase di importazione
    // si controlla che il modello da importare contenga o meno la versione e se presente
    // che la versione coincida con la costante CostantiGenModelli.VERSIONE_MODELLO
    if (!CostantiGenModelli.VERSIONE_MODELLO.equals(datiGenerali.getVersione())) {
      if (datiGenerali.getVersione() != null) {
        messageKey = "errors.genmod.import.versioneDiversa";
        
        logger.error(UtilityStringhe.replaceParametriMessageBundle(
            this.resBundleGenerale.getString(messageKey), 
                new String[] {datiGenerali.getVersione().toString(),
              CostantiGenModelli.VERSIONE_MODELLO.toString()} ));
        this.aggiungiMessaggio(request, messageKey, datiGenerali.getVersione().toString(),
            CostantiGenModelli.VERSIONE_MODELLO.toString());
      } else {
        // Report in importazione con versione non valorizzata
        messageKey = "errors.genmod.import.versioneNonValorizzata";
        
        logger.error(UtilityStringhe.replaceParametriMessageBundle(
            this.resBundleGenerale.getString(messageKey),
                new String[] { CostantiGenRicerche.VERSIONE_REPORT.toString() } ));
        this.aggiungiMessaggio(request, messageKey, CostantiGenRicerche.VERSIONE_REPORT.toString());
      }
      target = CostantiWizard.SUCCESS_UPLOAD;
    } else {
    
      ProfiloUtente profiloUtente = (ProfiloUtente) sessione.getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
  
      try {
        // Set dell'esistenza o meno su DB di un modello con la
        // stessa chiave CODAPP - NOME
        TrovaModelli trovaModelli = new TrovaModelli();
        trovaModelli.setNomeModello(datiGenerali.getNomeModello());
        trovaModelli.setCodiceProfiloAttivo(datiGenerali.getProfiloOwner());
        trovaModelli.setNoCaseSensitive(false);
  
        List<?> listaModelli = this.modelliManager.getModelli(trovaModelli);
        if (listaModelli != null && listaModelli.size() > 0) {
          contenitoreObj.setEsisteModello(true);
          
          // Se esiste un modello con lo stesso nome, codice applicativo, profilo 
          // di creazione e disponibilità allora posso inserire solo cambiando nome
          contenitoreObj.setModelloEsistenteDisponibile(((DatiModello)listaModelli.get(0)).getDisponibile());
          contenitoreObj.setTipoImport(CostantiWizard.IMPORT_INSERT_CON_NUOVO_TITOLO);
           
        } else {
          contenitoreObj.setEsisteModello(false);
          contenitoreObj.setModelloEsistenteDisponibile(0);
          contenitoreObj.setTipoImport(CostantiWizard.IMPORT_INSERT_NUOVO_MODELLO);
        }
  
        // Set se l'utente è l'owner del modello in importazione o meno
        if (datiGenerali.getOwner().intValue() == profiloUtente.getId()) {
          contenitoreObj.setUtenteOwner(true);
        } else {
          contenitoreObj.setUtenteOwner(false);
        }
        
        request.setAttribute("tipoModello",this.tabellatiManager.getDescrTabellato(
            TabellatiManager.TIPO_MODELLI, datiGenerali.getTipoModello()));
        
      } catch (SqlComposerException sc){
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = sc.getChiaveResourceBundle();
        logger.error(this.resBundleGenerale.getString(messageKey), sc);
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
    if (logger.isDebugEnabled()) {
      logger.debug("editDatiGen: fine metodo");
    }
    return target;
  }
  
  /**
   * Metodo per l'inizializzazione della pagina di edit dei gruppi da associare
   * al modello in fase di import
   */
  private String editGruppi(HttpServletRequest request) {
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    // target di default, da modificare nel momento in cui si verificano degli
    // errori
    String target = CostantiWizard.SUCCESS_EDIT_GRUPPI;
    String messageKey = null;

    try {
      // lista di tutti i gruppi esistenti con l'attributo 'associato' di tipo
      // boolean valorizzato a true se il gruppo è associato al modello in
      // analisi e a false altrimenti. La lista è ordinata per nome dei gruppi
      List<?> listaGruppiAssociatiModello =
        this.gruppiManager.getGruppiOrderByNome((String)
          request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO));

      // set nel request della lista di tutti i modello e lo stato di
      // associazione con il gruppo in analisi
      request.setAttribute("listaGruppiModelli", listaGruppiAssociatiModello);

      if(listaGruppiAssociatiModello == null || listaGruppiAssociatiModello.size() == 0){
        messageKey = "warnings.genMod.import.profiloSenzaGruppi";
        logger.warn(this.resBundleGenerale.getString(messageKey), null);
        this.aggiungiMessaggio(request, messageKey);
        // Metto nel request un attributo che permettera' alla jsp di proseguire senza
        // effettuare i controlli sulla valorizzazione delle checkbox di associazione
        request.setAttribute("profiloSenzaGruppi", "1");
      }
      
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

    if (logger.isDebugEnabled()) logger.debug("runAction: fine metodo");
    return target;
  }
  
  private void gestioneVociAvanzamento(HttpServletRequest request,
      String paginaAttiva) {
    List<String> pagineVisitate = new ArrayList<String>();
    List<String> pagineDaVisitare = new ArrayList<String>();
    ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    Set<String> listaFunzioneUtenteAbilitate = new HashSet<String>();
    for (int i = 0; i < profiloUtente.getFunzioniUtenteAbilitate().length; i++)
      listaFunzioneUtenteAbilitate.add(profiloUtente.getFunzioniUtenteAbilitate()[i]);

    boolean mostraTitoloGruppi = true;
    // Condizione discriminante per la visualizzazione del titolo della pagina
    // 'Gruppi': il titolo viene mostrato quando l'applicativo e' stato
    // configurato con i gruppi abilitati
    if (this.bloccaGestioneGruppiDisabilitata(request, false, false))
      mostraTitoloGruppi = false;

    boolean mostraTitoloPubblica = true;
    if(request.getSession().getServletContext().getAttribute(
        CostantiGenerali.ATTR_CONFIGURAZIONE_CHIUSA).equals("1"))
      mostraTitoloPubblica = false;
    
    if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_UPLOAD)) {
      pagineVisitate.add(CostantiWizard.TITOLO_UPLOAD);
      
      pagineDaVisitare.add(CostantiWizard.TITOLO_DATIGENERALI);
      if (mostraTitoloPubblica)
        pagineDaVisitare.add(CostantiWizard.TITOLO_PUBBLICA);
      if (mostraTitoloGruppi)
        pagineDaVisitare.add(CostantiWizard.TITOLO_GRUPPI);
    } else if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_DATI_GENERALI)) {
      pagineVisitate.add(CostantiWizard.TITOLO_UPLOAD);
      pagineVisitate.add(CostantiWizard.TITOLO_DATIGENERALI);

      if (mostraTitoloPubblica)
        pagineDaVisitare.add(CostantiWizard.TITOLO_PUBBLICA);
      if (mostraTitoloGruppi)
        pagineDaVisitare.add(CostantiWizard.TITOLO_GRUPPI);
    } else if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_PUBBLICAZIONE)) {
      pagineVisitate.add(CostantiWizard.TITOLO_UPLOAD);
      pagineVisitate.add(CostantiWizard.TITOLO_DATIGENERALI);
      if (mostraTitoloPubblica)
        pagineVisitate.add(CostantiWizard.TITOLO_PUBBLICA);

      if (mostraTitoloGruppi)
        pagineDaVisitare.add(CostantiWizard.TITOLO_GRUPPI);
    } else if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_GRUPPI)) {
      pagineVisitate.add(CostantiWizard.TITOLO_UPLOAD);
      pagineVisitate.add(CostantiWizard.TITOLO_DATIGENERALI);
      if (mostraTitoloPubblica)
        pagineVisitate.add(CostantiWizard.TITOLO_PUBBLICA);
      if (mostraTitoloGruppi)
        pagineVisitate.add(CostantiWizard.TITOLO_GRUPPI);
    }
    request.setAttribute("pagineVisitate", pagineVisitate);
    request.setAttribute("pagineDaVisitare", pagineDaVisitare);
  }
  
}