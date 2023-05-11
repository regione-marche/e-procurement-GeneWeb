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
package it.eldasoft.gene.web.struts.genric.impexp;

import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.RicercaGruppo;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiImport;
import it.eldasoft.gene.db.domain.genric.DatiGenRicerca;
import it.eldasoft.gene.db.domain.genric.TrovaRicerche;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.sql.comp.SqlManager;
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
 * definizione di ricerche base, avanzate e con modello
 * 
 * @author Luca.Giacomazzo
 */
public class WizardImportaRicercaAction extends ActionBaseNoOpzioni {

  /**   logger di Log4J   */
  static Logger logger = Logger.getLogger(WizardImportaRicercaAction.class);
  
  protected CheckOpzioniUtente getOpzioniRunAction() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.OPZIONI_GESTIONE_FUNZIONI_AVANZATE);
  }
  
  /**
   * Reference alla classe di business logic per l'accesso a W_GRUPPI e/o
   * W_GRPRIC
   */
  private GruppiManager       gruppiManager;

  /**
   * Reference alla classe di business logic per l'accesso a W_RICERCHE
   */
  private RicercheManager    ricercheManager;

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
  
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response) 
          throws IOException, ServletException {
    if (logger.isDebugEnabled()) {
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
      // definizione della ricerca da importare
      
      // Se in sessione e' presente l'attributo individuato dalla costante 
      // CostantiGenRicerche.OGGETTO_DETTAGLIO allora lo rimuovo, sia che esso 
      // sia un report precedentemente caricato in sessione, sia che si ritorni
      // alla pagina di upload del wizard di import
      if (request.getSession().getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO) != null) {
        request.getSession().removeAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
      }
      target = CostantiWizard.SUCCESS_UPLOAD;
    } else if (CostantiWizard.CODICE_PAGINA_DATI_GENERALI.equals(pageTo)) {
      // Inizializzazione della pagina di visualizzazione dei dati generali
      // della ricerca da importare con eventuale domanda sul tipo di
      // importazione da effettuare
      target = this.editDatiGenerali(request);
    } else if (CostantiWizard.CODICE_PAGINA_PUBBLICAZIONE.equals(pageTo)) {

      // l'accesso alla pagina per la pubblicazione e' valida solo se 
      // l'applicazione e' avviata in configurazione aperta
      if (request.getSession().getServletContext().getAttribute(
          CostantiGenerali.ATTR_CONFIGURAZIONE_CHIUSA).equals("1")) {
        
        ContenitoreDatiImport contenitoreObj = (ContenitoreDatiImport)
            request.getSession().getAttribute(
                CostantiGenRicerche.OGGETTO_DETTAGLIO);
        // In configurazione chiusa non viene chiesto se pubblicare il report,
        // ma viene chiesto a quali gruppi pubblicare, se la gestione dei gruppi
        // e' abilitata. Nell'oggetto ContenitoreDatiImport si setta l'attributo
        // pubblicaReport a true
        if (this.bloccaGestioneGruppiDisabilitata(request, false, false)) {
          contenitoreObj.setPubblicaReport(false);
          target = CostantiWizard.SUCCESS_FINE;
        } else {
          contenitoreObj.setPubblicaReport(true);
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
        // In caso di eccezione durante l'esecuzione dell'import della ricerca
        // l'attributo 'pageFrom' assume il valore 'GRP': tale valore e'
        // sbagliato perchè dalla pagina dei gruppi, premendo il pulsante
        // 'Indietro' si ricarica la stessa pagina, invece di tornare alla
        // pagina di pubblicazione
        if (! CostantiWizard.CODICE_PAGINA_GRUPPI.equals(request.getAttribute("pageFrom"))) {
          request.setAttribute("pageFrom", request.getAttribute("pageFrom"));
        } else {
          request.setAttribute("pageFrom", CostantiWizard.CODICE_PAGINA_PUBBLICAZIONE);
        }
        target = this.editGruppi(request);
      }
    } else {
      // Si è tentato l'accesso ad un tab non previsto, e quindi si effettua
      // la tracciatura su log e si visualizza la pagina di inserimento
      // dell'argomento della ricerca come default
      String messageKey = "info.genRic.wizardBase.defaultPage";
      logger.warn(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
      target = CostantiWizard.ERROR_IMPORT_REPORT;
      
      // Cancellazione dalla sessione del contenitore dati import
      request.getSession().removeAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    }
    
    if (target != null
        && !target.equals(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE)) {
      this.gestioneVociAvanzamento(request, pageTo);
      
      if(!target.equals(CostantiWizard.SUCCESS_FINE)) {
        // set nel request del parameter per disabilitare la navigazione
        request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
            CostantiGenerali.DISABILITA_NAVIGAZIONE);
      }
    }

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: fine metodo");  
    }
    return mapping.findForward(target);
  }

  /**
   * Metodo per l'inizializzazione della pagina di visualizzazione dei dati
   * generali della ricerca da importare. 
   */
  private String editDatiGenerali(HttpServletRequest request) {
    if (logger.isDebugEnabled()) {
      logger.debug("editDatiGen: inizio metodo");
    }

    // target di default
    String target = CostantiWizard.SUCCESS_DATI_GENERALI;
    String messageKey = null;

    // Tipo di Database in uso
    String tipoDB = ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);
    
    DatiGenRicerca datiGenerali = null;
    HttpSession sessione = request.getSession();
    ContenitoreDatiImport contenitoreObj = (ContenitoreDatiImport)
      sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);
    if (contenitoreObj.getContenitoreDatiRicerca() != null ) {
      datiGenerali = contenitoreObj.getContenitoreDatiRicerca().getDatiGenerali();
    } else {
      datiGenerali = contenitoreObj.getContenitoreDatiProspetto().getDatiGenProspetto().getDatiGenRicerca();
    }
    
    // L.G. 07/04/2015: introduzione della versione del report. In fase di importazione si
    // controlla che il report da importare contenga o meno la versione e se presente che la
    // versione coincida con la costante CostantiGenRicerche.VERSIONE_REPORT

    if (!CostantiGenRicerche.VERSIONE_REPORT.equals(datiGenerali.getVersione())) {
      if (datiGenerali.getVersione() != null) {
        messageKey = "errors.genric.import.versioneDiversa";
        
        logger.error(UtilityStringhe.replaceParametriMessageBundle(
            this.resBundleGenerale.getString(messageKey), 
                new String[] {datiGenerali.getVersione().toString(),
                    CostantiGenRicerche.VERSIONE_REPORT.toString()} ));
        this.aggiungiMessaggio(request, messageKey, datiGenerali.getVersione().toString(),
            CostantiGenRicerche.VERSIONE_REPORT.toString());
      } else {
        // Report in importazione con versione non valorizzata
        messageKey = "errors.genric.import.versioneNonValorizzata";
        
        logger.error(UtilityStringhe.replaceParametriMessageBundle(this.resBundleGenerale.getString(messageKey),
            new String[] { CostantiGenRicerche.VERSIONE_REPORT.toString() } ));
        this.aggiungiMessaggio(request, messageKey, CostantiGenRicerche.VERSIONE_REPORT.toString());
      }
      target = CostantiWizard.SUCCESS_UPLOAD;
    } else if (CostantiGenRicerche.REPORT_SQL == datiGenerali.getFamiglia().intValue() && 
        !tipoDB.equalsIgnoreCase(datiGenerali.getTipoDB())) {

      if (datiGenerali.getTipoDB() != null) {
        // Nel caso si importi un report sql con indicato il tipo di DBMS da cui
        // e' stato esportato, si verifica che il tipo di DBMS in uso sia uguale
        // al tipo di DBMS da cui e' stato esportato il report. Se diverso si
        // segnala con un warning che il report protebbe non essere eseguibile
        messageKey = "warnings.genric.import.reportSql.tipoDatabaseDiverso";
        
        String dbReport = this.getNomeDBMS(datiGenerali.getTipoDB());
        String dbInUso = this.getNomeDBMS(tipoDB);
        
        logger.warn(UtilityStringhe.replaceParametriMessageBundle(
            this.resBundleGenerale.getString(messageKey), 
                new String[] { dbReport, dbInUso } ));
        this.aggiungiMessaggio(request, messageKey, dbReport, dbInUso);
        
      } else {
        // Nel caso si importi un report sql privo del tipo di DBMS da cui e'
        // stato esportato. Non e' possibile quindi stabilire se il report sia
        // eseguibile non DBMS di destinazione.
        messageKey = "warnings.genric.import.reportSql.noTipoDatabase";
        
        String dbInUso = this.getNomeDBMS(tipoDB);
        
        logger.warn(UtilityStringhe.replaceParametriMessageBundle(
            this.resBundleGenerale.getString(messageKey),
                new String[] { dbInUso } ));
        this.aggiungiMessaggio(request, messageKey, dbInUso);        
      }
    } else {
      ProfiloUtente profiloUtente = (ProfiloUtente) sessione.getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
  
      try {
        // Set dell'esistenza o meno su DB di un report con la
        // stessa chiave CODAPP - NOME
        TrovaRicerche trovaRicerche = new TrovaRicerche();
        trovaRicerche.setNomeRicerca(datiGenerali.getNome());
        trovaRicerche.setCodiceApplicazione(datiGenerali.getCodApp());
        trovaRicerche.setProfiloOwner(datiGenerali.getProfiloOwner());
        trovaRicerche.setNoCaseSensitive(false);
  
        List<?> listaRicerche = this.ricercheManager.getRicerche(trovaRicerche, true);
        if (listaRicerche != null && listaRicerche.size() > 0) {
          contenitoreObj.setEsisteReport(true);
          RicercaGruppo datiGenRicercaInDb = (RicercaGruppo) listaRicerche.get(0);
          
          // Il fatto che il report su DB sia pubblicato o meno e' un'informazione
          // necessaria quando sono verificate queste condizioni:
          // - l'utente importatore ha la gestione report personali;
          // - il report esiste nel base dati;
          // - il report in importazione e il report su DB hanno la stessa famiglia;
          // - l'utente e' l'owner del report presente su DB;
          // - il report su DB e' stato pubblicato ad altri utenti (da un 
          //   amministratore)
          // In questo caso l'utente puo' importare il report solo specificando
          // un nome diverso da quello esistente
          contenitoreObj.setReportEsistentePubblicato(datiGenRicercaInDb.getDisponibile());
          contenitoreObj.setTipoImport(CostantiWizard.IMPORT_INSERT_CON_NUOVO_TITOLO);
          
          // Se il report esiste controllo che quello che si sta importando sia della
          // stessa famiglia di quello presente su DB. Se la famiglia è la stessa
          // allora l'utente potra' sovrascrivere o inserire un report con nuovo
          // titolo (se ne ha i diritti), mentre se la famiglia e' diversa, allora
          // l'utente potra' solo inserire un report con nuovo titolo
          if ((datiGenRicercaInDb.getFamiglia().equalsIgnoreCase("report base")
                 && 
              CostantiGenRicerche.REPORT_BASE == datiGenerali.getFamiglia().intValue()
             ) || (
              datiGenRicercaInDb.getFamiglia().equalsIgnoreCase("report avanzato")
                 &&
              CostantiGenRicerche.REPORT_AVANZATO == datiGenerali.getFamiglia().intValue()
             ) || (
              datiGenRicercaInDb.getFamiglia().equalsIgnoreCase("report con modello")
                 &&
              CostantiGenRicerche.REPORT_PROSPETTO == datiGenerali.getFamiglia().intValue()
             ) || (datiGenRicercaInDb.getFamiglia().equalsIgnoreCase("report sql")
                 && 
              CostantiGenRicerche.REPORT_SQL == datiGenerali.getFamiglia().intValue())
             )  {
            contenitoreObj.setFamigliaUguale(true);
          } else {
            contenitoreObj.setFamigliaUguale(false);
          }  
        } else {
          contenitoreObj.setEsisteReport(false);
          contenitoreObj.setReportEsistentePubblicato(false);
          contenitoreObj.setTipoImport(CostantiWizard.IMPORT_INSERT_NUOVO_REPORT);
        }
  
        // Set se l'utente e' l'owner del report in importazione o meno
        if(datiGenerali.getOwner().intValue() == profiloUtente.getId()) {
          contenitoreObj.setUtenteOwner(true);
        } else {
          contenitoreObj.setUtenteOwner(false);
        }
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
  
  private String getNomeDBMS(String codiceDBMS) {
    String result = "";
    if (SqlManager.DATABASE_ORACLE.equalsIgnoreCase(codiceDBMS))
      result = "Oracle";
    else if (SqlManager.DATABASE_SQL_SERVER.equalsIgnoreCase(codiceDBMS))
      result = "Microsoft SQL Server";
    else if (SqlManager.DATABASE_POSTGRES.equalsIgnoreCase(codiceDBMS))
      result = "PostGreSQL";
    else if (SqlManager.DATABASE_DB2.equalsIgnoreCase(codiceDBMS))
      result = "IBM DB2";
    
    return result;
  }
  
  /**
   * Metodo per l'inizializzazione della pagina di edit dei gruppi da associare
   * alla ricerca in fase di import
   */
  private String editGruppi(HttpServletRequest request) {
    if (logger.isDebugEnabled()) {
      logger.debug("runAction: inizio metodo");
    }

    // target di default, da modificare nel momento in cui si verificano degli
    // errori
    String target = CostantiWizard.SUCCESS_EDIT_GRUPPI;
    String messageKey = null;

    try {
      // lista di tutti i gruppi esistenti con l'attributo 'associato' di tipo
      // boolean valorizzato a true se il gruppo è associato alla ricerca in
      // analisi e a false altrimenti. La lista è ordinata per nome dei gruppi
      List<?> listaGruppiAssociatiRicerca =
        this.gruppiManager.getGruppiOrderByNome((String)
          request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO));

      // set nel request della lista di tutte le ricerche e lo stato di
      // associazione con il gruppo in analisi
      request.setAttribute("listaGruppiRicerca", listaGruppiAssociatiRicerca);

      if (listaGruppiAssociatiRicerca == null || listaGruppiAssociatiRicerca.size() == 0) {
        messageKey = "warnings.genRic.import.profiloSenzaGruppi";
        logger.warn(this.resBundleGenerale.getString(messageKey), null);
        this.aggiungiMessaggio(request, messageKey);
        // Metto nel request un attributo che permettera' alla jsp di proseguire senza
        // effettuare i controlli sulla valorizzazione delle checkbox di associazione
        request.setAttribute("profiloSenzaGruppi", "1");
      }
      
      // set nel request del parameter per disabilitare la navigazione
      // in fase di editing
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

    if (logger.isDebugEnabled()) {
      logger.debug("runAction: fine metodo");
    }
    return target;
  }
  
  private void gestioneVociAvanzamento(HttpServletRequest request, String paginaAttiva) {
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
    if (this.bloccaGestioneGruppiDisabilitata(request, false, false)) {
      mostraTitoloGruppi = false;
    }
    
    boolean mostraTitoloPubblica = true;
    if (request.getSession().getServletContext().getAttribute(
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