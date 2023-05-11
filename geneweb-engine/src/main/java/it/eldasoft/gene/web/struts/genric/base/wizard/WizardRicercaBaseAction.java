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
package it.eldasoft.gene.web.struts.genric.base.wizard;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.bl.genric.GestoreVisibilitaDati;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.genric.GruppoRicerca;
import it.eldasoft.gene.web.struts.genric.AbstractActionBaseGenRicercheBase;
import it.eldasoft.gene.web.struts.genric.ContenitoreDatiRicercaForm;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.gene.web.struts.genric.argomenti.TabellaRicercaForm;
import it.eldasoft.gene.web.struts.genric.campo.CampoRicercaForm;
import it.eldasoft.gene.web.struts.genric.gruppo.GruppiRicercaForm;
import it.eldasoft.gene.web.struts.genric.gruppo.GruppoForm;
import it.eldasoft.gene.web.struts.genric.ordinamento.OrdinamentoRicercaForm;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioSchemi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Schema;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
 * Gestore del wizard di creazione di una ricerca base per il controllo della
 * successione delle pagine e della visualizzazione dell'avanzamento del wizard
 * stesso.
 *
 * @author Luca.Giacomazzo
 */
public class WizardRicercaBaseAction extends AbstractActionBaseGenRicercheBase {

  // Target delle diverse chiamate nel wizard
  private static final String SUCCESS_EDIT_ARGOMENTO      = "successEditArg";
  private static final String SUCCESS_EDIT_CAMPI          = "successEditCam";
  private static final String SUCCESS_DOMANDA_FILTRI      = "successDomandaFil";
  private static final String SUCCESS_EDIT_FILTRO         = "successEditFil";
  private static final String SUCCESS_LISTA_FILTRI        = "successListaFil";
  private static final String SUCCESS_DOMANDA_ORDINAMENTI = "successDomandaOrd";
  private static final String SUCCESS_EDIT_ORDINAMENTO    = "successEditOrd";
  private static final String SUCCESS_LISTA_ORDINAMENTI   = "successListaOrd";
  private static final String SUCCESS_EDIT_LAYOUT         = "successEditLay";
  private static final String SUCCESS_DOMANDA_PUBBLICA    = "successDomandaPub";
  private static final String SUCCESS_EDIT_GRUPPI         = "successEditGrp";
  private static final String SUCCESS_EDIT_DATI_GENERALI  = "successEditDg";

  static Logger               logger                      = Logger.getLogger(WizardRicercaBaseAction.class);

  /**
   * Reference alla classe di business logic per l'accesso a W_GRUPPI e/o
   * W_GRPRIC
   */
  private GruppiManager       gruppiManager;

  /**
   * Reference alla classe di business logic per l'accesso ai tabellati
   */
  private TabellatiManager    tabellatiManager;

  /**
   * Reference al Manager per la gestione delle protezioni di tabelle e campi
   * rispetto al profilo attivo
   */
  private GeneManager geneManager;

  /**
   * @param gruppiManager
   *        gruppiManager da settare internamente alla classe.
   */
  public void setGruppiManager(GruppiManager gruppiManager) {
    this.gruppiManager = gruppiManager;
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

  @Override
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("RunAction: inizio metodo");

    // Dal request leggo il parametro pageFrom, il quale contiene il codice
    // della
    // pagina di destinazione
    String pageTo = request.getParameter("pageTo");
    if (pageTo == null || pageTo.length() == 0)
      pageTo = (String) request.getAttribute("pageTo");

    String target = null;

    if (CostantiWizard.CODICE_PAGINA_ARGOMENTO.equals(pageTo)) {
      // Inizializzazione della pagina di edit dall'argomento della ricerca e
      // setting del target
      target = this.editArgomento(request);
    } else if (CostantiWizard.CODICE_PAGINA_CAMPI.equals(pageTo)) {
      // Inizializzazione della pagina di edit dai campi da estrarre nella
      // ricerca e setting del target
      target = this.editCampi(request);
    } else if (CostantiWizard.CODICE_PAGINA_DOMANDA_FILTRI.equals(pageTo)) {
      // Non e' necessario fare alcuna inizializzazione della pagina
      target = WizardRicercaBaseAction.SUCCESS_DOMANDA_FILTRI;
    } else if (CostantiWizard.CODICE_PAGINA_FILTRO.equals(pageTo)) {
      // Inizializzazione della pagina di edit di una condizione di filtro nella
      // ricerca e setting del target
      target = this.editFiltro(request);
    } else if (CostantiWizard.CODICE_PAGINA_LISTA_FILTRI.equals(pageTo)) {
      // Non e' necessario fare alcuna inizializzazione della pagina
      target = WizardRicercaBaseAction.SUCCESS_LISTA_FILTRI;
    } else if (CostantiWizard.CODICE_PAGINA_DOMANDA_ORDINAMENTI.equals(pageTo)) {
      // Non e' necessario fare alcuna inizializzazione della pagina
      target = WizardRicercaBaseAction.SUCCESS_DOMANDA_ORDINAMENTI;
    } else if (CostantiWizard.CODICE_PAGINA_ORDINAMENTO.equals(pageTo)) {
      target = this.editOrdinamento(request);
    } else if (CostantiWizard.CODICE_PAGINA_LISTA_ORDINAMENTI.equals(pageTo)) {
      // Non e' necessario fare alcuna inizializzazione della pagina
      target = WizardRicercaBaseAction.SUCCESS_LISTA_ORDINAMENTI;
    } else if (CostantiWizard.CODICE_PAGINA_LAYOUT.equals(pageTo)) {
      // Non e' necessario fare alcuna inizializzazione della pagina
      target = WizardRicercaBaseAction.SUCCESS_EDIT_LAYOUT;
    } else if (CostantiWizard.CODICE_PAGINA_PUBBLICAZIONE.equals(pageTo)) {
      // Si verifica che l'utente che richiede l'accesso alla pagina di
      // pubblicazione sia autorizzato alla gestione completa delle ricerche

      ProfiloUtente account = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      // carico le opzioni per cui è abilitato l'utente loggato
      OpzioniUtente opzioniUtente = new OpzioniUtente(
          account.getFunzioniUtenteAbilitate());
      CheckOpzioniUtente opzioniPerAbilitazione = new CheckOpzioniUtente(
          CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
      // l'entrata nel tab dei gruppi è valida solo per utenti con gestione
      // completa delle ricerche
      if (!opzioniPerAbilitazione.test(opzioniUtente)) {
        target = CostantiGeneraliStruts.FORWARD_OPZIONE_NON_ABILITATA;
        String messageKey = "errors.opzione.noAbilitazione";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      } else {
        target = WizardRicercaBaseAction.SUCCESS_DOMANDA_PUBBLICA;
      }

    } else if (CostantiWizard.CODICE_PAGINA_GRUPPI.equals(pageTo)) {

      if (this.bloccaGestioneGruppiDisabilitata(request, false, false)) {
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        String messageKey = "errors.applicazione.gruppiDisabilitati";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      } else {
        ProfiloUtente account = (ProfiloUtente) request.getSession().getAttribute(
            CostantiGenerali.PROFILO_UTENTE_SESSIONE);
        // carico le opzioni per cui è abilitato l'utente loggato
        OpzioniUtente opzioniUtente = new OpzioniUtente(
            account.getFunzioniUtenteAbilitate());
        CheckOpzioniUtente opzioniPerAbilitazione = new CheckOpzioniUtente(
            CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC);
        // l'entrata nel tab dei gruppi è valida solo per utenti con gestione
        // completa delle ricerche
        if (!opzioniPerAbilitazione.test(opzioniUtente)) {
          target = CostantiGeneraliStruts.FORWARD_OPZIONE_NON_ABILITATA;
          String messageKey = "errors.opzione.noAbilitazione";
          logger.error(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        } else {
          target = this.editGruppi(request);
        }
      }

    } else if (CostantiWizard.CODICE_PAGINA_DATI_GENERALI.equals(pageTo)) {
      // In tutti i casi in cui si vada alla pagina dei 'Dati Generali' per
      // poter
      // tornare indietro bisogna che la pagina di origine passi alla pagina di
      // destinazione il parameter (o un attribute) denominato 'pageFrom'
      // valorizzato con il codice della pagina di origine. Per questo nel
      // request deve essere presente tale attribute, il quale deve essere
      // rigirato
      // alla pagina di destinazione.
      request.setAttribute("pageFrom", request.getAttribute("pageFrom"));
      target = this.editDatiGenerali(request);
    } else {
      // Si è tentato l'accesso ad un tab non previsto, e quindi si effettua
      // la tracciatura su log e si visualizza la pagina di inserimento
      // dell'argomento della ricerca come default
      String messageKey = "info.genRic.wizardBase.defaultPage";
      logger.warn(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
      target = this.editArgomento(request);
    }

    if (target != null
        && !target.equals(CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE)){
      this.gestioneVociAvanzamento(request, pageTo);

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);
    }

    if (logger.isDebugEnabled()) logger.debug("RunAction: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Metodo per l'inizializzazione della pagina di edit dell'argomento della
   * ricerca base
   */
  private String editArgomento(HttpServletRequest request) {
    if (logger.isDebugEnabled()) logger.debug("editArgomento: inizio metodo");

    // target di default
    String target = WizardRicercaBaseAction.SUCCESS_EDIT_ARGOMENTO;

    String nomeSchemaViste = ConfigManager.getValore(
        CostantiGenerali.PROP_SCHEMA_VISTE_REPORT_BASE);
    
    if (nomeSchemaViste != null && nomeSchemaViste.length() > 0) {
      DizionarioSchemi dizSchemi = DizionarioSchemi.getInstance();
      Schema schemaViste = dizSchemi.get(nomeSchemaViste);
      if (schemaViste != null) {
        List<String> elencoMnemoniciTabelleSchema = schemaViste.getMnemoniciTabelle();

        if (elencoMnemoniciTabelleSchema != null
            && elencoMnemoniciTabelleSchema.size() > 0) {
          Vector<TabellaRicercaForm> elencoTabelle = new Vector<TabellaRicercaForm>();
          DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
          
          TabellaRicercaForm tabellaForm = null;
          String mnemonicoTabella = null;
          Tabella tabella = null;

          GestoreVisibilitaDati gestoreVisibilita = this.geneManager.getGestoreVisibilitaDati();

          String profiloAttivo = (String) request.getSession().getAttribute(
              CostantiGenerali.PROFILO_ATTIVO);

          // a partire dalle tabelle censite nello schema delle viste per le
          // ricerche base, si estrae dai dizionari l'elenco delle tabelle e
          // dei campi, creando una lista di tabelle, e una lista di campi per
          // ogni tabella, ognuna delle quali viene posta nel request sotto un
          // nome "elencoCampi"+nome della tabella dell'elenco dei campi
          for (int i = 0; i < elencoMnemoniciTabelleSchema.size(); i++) {
            mnemonicoTabella = elencoMnemoniciTabelleSchema.get(i);
            tabella = dizTabelle.get(mnemonicoTabella);
            if (gestoreVisibilita.checkEntitaVisibile(tabella, profiloAttivo)) {
              
              tabellaForm = new TabellaRicercaForm();
              tabellaForm.setAliasTabella(tabella.getNomeTabella());
              tabellaForm.setDescrizioneSchema(schemaViste.getDescrizione());
              tabellaForm.setDescrizioneTabella(tabella.getDescrizione());
              tabellaForm.setId(null);
              tabellaForm.setMnemonicoSchema(nomeSchemaViste);
              tabellaForm.setMnemonicoTabella(tabella.getCodiceMnemonico());
              tabellaForm.setNomeTabella(tabella.getNomeTabella());
              tabellaForm.setProgressivo(i);
              tabellaForm.setVisibile(true);
              elencoTabelle.addElement(tabellaForm);
            }
          }

          if (elencoTabelle.size() > 0) { //== 0) {
            request.setAttribute("elencoTabelle", elencoTabelle);

            HttpSession session = request.getSession();
            ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
                session.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

            if (contenitore.getNumeroTabelle() > 0) {
              // Set nel request l'alias della tabella attualmente selezionata
              TabellaRicercaForm tabellaRicercaForm = (TabellaRicercaForm)
                  contenitore.getElencoArgomenti().get(0);
              request.setAttribute("aliasTabellaAttiva",
                  tabellaRicercaForm.getAliasTabella());
            }
  
            // set nel request del parameter per disabilitare la navigazione in
            // fase di editing
            request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
                CostantiGenerali.DISABILITA_NAVIGAZIONE);
          } else {          
            // Nel profilo attivo tutte le tabelle dalla viste per i report base
            // non sono visibili, pertanto l'utente non può creare report base
            target = "tornaASceltaTipoReport";
            String messageKey = "warnings.genRic.base.wizard.noTabelleVisibiliNelProfiloAttivo";
            if (logger.isDebugEnabled()) {
              logger.warn(this.resBundleGenerale.getString(messageKey));
            }
            this.aggiungiMessaggio(request, messageKey);
          } 
        } else {
          // Nei metadati non esistono tabelle associate allo schema delle viste
          // per le ricerche base
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          String messageKey = "errors.genRic.ricercaBase.noTabelleSchemaViste";
          if (logger.isDebugEnabled())
            logger.debug(this.resBundleGenerale.getString(messageKey));
          this.aggiungiMessaggio(request, messageKey);
        }
      } else {
        // Nei metadati non esite alcuno schema con il nome specificato nella
        // properties specificata
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        String messageKey = "errors.genRic.ricercaBase.noDefSchemaViste";
        if (logger.isDebugEnabled())
          logger.debug(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }
    } else {
      // la properties con il nome dello schema con le viste delle ricerche base
      // non e' stata definita e/o configurata
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      String messageKey = "errors.genRic.ricercaBase.noDefPropertySchemaViste";
      if (logger.isDebugEnabled())
        logger.debug(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey);
    }
    if (logger.isDebugEnabled()) logger.debug("editArgomento: fine metodo");

    return target;
  }

  /**
   * Metodo per l'inizializzazione della pagina di edit dei campi da estrarre
   * nella ricerca base
   */
  private String editCampi(HttpServletRequest request) {
    if (logger.isDebugEnabled()) logger.debug("editCampi: inizio metodo");

    // target di default
    String target = WizardRicercaBaseAction.SUCCESS_EDIT_CAMPI;

    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
        sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    // Estraggo dalla sessione la tabella scelta al passo precedente
    TabellaRicercaForm tabellaRic = contenitore.estraiTabella(0);

    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
    DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    String mnemonicoTabella = null;
    Tabella tabella = null;
    List<String> elencoMnemoniciCampi = null;
    Vector<Campo> elencoCampi = null;
    String mnemonicoCampo = null;
    Campo campo = null;

    GestoreVisibilitaDati gestoreVisibilita = this.geneManager.getGestoreVisibilitaDati();
    String profiloAttivo = (String) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_ATTIVO);

    // a partire dalle tabella selezionata nella pagina precedente del wizard,
    // si estrae dai dizionari l'elenco delle dei campi, creando una lista di
    // campi, ognuna delle quali viene posta nel request con nome
    // "elencoCampi" della tabella dell'elenco dei campi
    // for (int i = 0; i < elencoMnemoniciTabelleSchema.size(); i++) {
    mnemonicoTabella = tabellaRic.getMnemonicoTabella();
    tabella = dizTabelle.get(mnemonicoTabella);

    elencoMnemoniciCampi = tabella.getMnemoniciCampi();
    elencoCampi = new Vector<Campo>(elencoMnemoniciCampi.size());
    for (int j = 0; j < elencoMnemoniciCampi.size(); j++) {
      mnemonicoCampo = elencoMnemoniciCampi.get(j);
      campo = dizCampi.get(mnemonicoCampo);
      if (gestoreVisibilita.checkCampoVisibile(campo, profiloAttivo))
        elencoCampi.addElement(campo);
    }
    if(elencoCampi.size() > 0)
      request.setAttribute("elencoCampi", elencoCampi);
    request.setAttribute("tabella", tabellaRic);

    if (contenitore.getNumeroTabelle() > 0) {
      // Set nel request dell'elenco dei campi attualmente selezionati
      Vector<CampoRicercaForm> elencoCampiSelezionati = contenitore.getElencoCampi();
      request.setAttribute("campiSelezionati", elencoCampiSelezionati);
    }

    // set nel request del parameter per disabilitare la navigazione in fase
    // di editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);

    if (logger.isDebugEnabled()) logger.debug("editCampi: fine metodo");
    return target;
  }

  /**
   * Metodo per l'inizializzazione della pagina di edit dei filtri da applicare
   * alla ricerca base
   */
  private String editFiltro(HttpServletRequest request) {
    if (logger.isDebugEnabled()) logger.debug("addFiltro: inizio metodo");

    String target = WizardRicercaBaseAction.SUCCESS_EDIT_FILTRO;

    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
        sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    Vector<CampoRicercaForm> elencoCampiForm = contenitore.getElencoCampi();

    Vector<String> elencoTabellatiCampi = new Vector<String>(elencoCampiForm.size());
    DizionarioCampi dizCampi = DizionarioCampi.getInstance();

    for (int i = 0; i < elencoCampiForm.size(); i++) {
      String mnemonicoCampo = ((CampoRicercaForm)elencoCampiForm.get(i)).getMnemonicoCampo();
      Campo campo = dizCampi.get(mnemonicoCampo);
      elencoTabellatiCampi.addElement(campo.getCodiceTabellato());
    }

    if (elencoCampiForm.size() > 0) {
      request.setAttribute("elencoCampi", elencoCampiForm);
      request.setAttribute("elencoTabellatiCampi", elencoTabellatiCampi);
      request.setAttribute("elencoOperatori",
          CostantiGenRicerche.CBX_OPERATORI_VALUE_REPORT_BASE);
      request.setAttribute("elencoOperatoriLabel",
          CostantiGenRicerche.CBX_OPERATORI_LABEL_REPORT_BASE);
    }

    // set nel request del parameter per disabilitare la navigazione in fase
    // di editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);

    if (logger.isDebugEnabled()) logger.debug("addFiltro: fine metodo");
    return target;
  }

  /**
   * Metodo per l'inizializzazione della pagina di edit degli ordinamenti da
   * applicare alla ricerca base
   */
  private String editOrdinamento(HttpServletRequest request) {
    if (logger.isDebugEnabled())
      logger.debug("editOrdinamento: inizio metodo");

    String target = WizardRicercaBaseAction.SUCCESS_EDIT_ORDINAMENTO;

    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
        sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    Vector<CampoRicercaForm> elencoCampiForm = contenitore.getElencoCampi();
    CampoRicercaForm campo = null;

    // Dall'elenco dei mnemonici dei campi rimuovi quelli usati per gli
    // ordinamenti preesistenti
    if(contenitore.getNumeroOrdinamenti() > 0){
      Vector<OrdinamentoRicercaForm> elencoOrdinamentiEsistenti = contenitore.getElencoOrdinamenti();
      for(int j = 0; j < elencoOrdinamentiEsistenti.size(); j++){
        OrdinamentoRicercaForm tmpOrdinamento = (OrdinamentoRicercaForm) elencoOrdinamentiEsistenti.get(j);
        boolean campoUsatoPerOrdinamento = false;
        for(int l= elencoCampiForm.size()-1; l >= 0 && !campoUsatoPerOrdinamento; l--){
          campo = (CampoRicercaForm) elencoCampiForm.get(l);
          if((campo.getAliasTabella().equals(tmpOrdinamento.getAliasTabella())) &&
              campo.getMnemonicoCampo().equals(tmpOrdinamento.getMnemonicoCampo())){
            elencoCampiForm.remove(l);
            campoUsatoPerOrdinamento = true;
          }
        }
      }
    }

    if (elencoCampiForm.size() > 0) {
      request.setAttribute("elencoCampi", elencoCampiForm);
    }

    // set nel request del parameter per disabilitare la navigazione in fase
    // di editing
    request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
        CostantiGenerali.DISABILITA_NAVIGAZIONE);

    if (logger.isDebugEnabled()) logger.debug("editOrdinamento: fine metodo");
    return target;
  }

  /**
   * Metodo per l'inizializzazione della pagina di edit dei gruppi da asoociare
   * alla ricerca base
   */
  private String editGruppi(HttpServletRequest request) {
    if (logger.isDebugEnabled()) logger.debug("runAction: inizio metodo");

    // target di default, da modificare nel momento in cui si verificano degli
    // errori
    String target = WizardRicercaBaseAction.SUCCESS_EDIT_GRUPPI;

    String messageKey = null;

    try {
      HttpSession sessione = request.getSession();

      ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
          sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

      Set<String> setGruppiAssociati = new HashSet<String>();
      for (int i = 0; i < contenitore.getElencoGruppi().size(); i++) {
        setGruppiAssociati.add(((GruppoForm) contenitore.getElencoGruppi().elementAt(
            i)).getIdGruppo());
      }
      // lista di tutti i gruppi esistenti con l'attributo 'associato' di tipo
      // boolean valorizzato a true se il gruppo è associato alla ricerca in
      // analisi e a false altrimenti. La lista è ordinata per nome dei gruppi
      List<?> listaGruppiAssociatiRicerca =
          this.gruppiManager.getGruppiConAssociazioneRicerca(setGruppiAssociati,
              (String) request.getSession().getAttribute(
                  CostantiGenerali.PROFILO_ATTIVO));

      GruppiRicercaForm gruppiRicercaForm = new GruppiRicercaForm();
      Vector<String> tmp = new Vector<String>();
      for (int i = 0; i < listaGruppiAssociatiRicerca.size(); i++)
        if (((GruppoRicerca) listaGruppiAssociatiRicerca.get(i)).getAssociato())
          tmp.add(""
              + ((GruppoRicerca) listaGruppiAssociatiRicerca.get(i)).getIdGruppo());

      gruppiRicercaForm.setIdGruppo((String[]) tmp.toArray(new String[0]));

      // set nel request della stringa necessaria al form di modifica
      // associazione utenti-gruppo
      request.setAttribute("gruppiRicercaForm", gruppiRicercaForm);

      // set nel request della lista di tutte le ricerche e lo stato di
      // associazione con il gruppo in analisi
      request.setAttribute("listaGruppiRicerca", listaGruppiAssociatiRicerca);

      if(listaGruppiAssociatiRicerca == null || listaGruppiAssociatiRicerca.size() == 0){
        messageKey = "warnings.genRic.base.wizard.profiloSenzaGruppi";
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

  /**
   * Metodo per l'inizializzazione della pagina di edit dei dati generali della
   * ricerca base
   */
  private String editDatiGenerali(HttpServletRequest request) {
    if (logger.isDebugEnabled()) logger.debug("editDatiGen: inizio metodo");

    // target di default
    String target = WizardRicercaBaseAction.SUCCESS_EDIT_DATI_GENERALI;
    String messageKey = null;

    // Se esiste nel request un parameter/attribute con nome 'pageFrom'
    // (contenente il codice della pagina da cui si e' partiti), lo re-inserisco
    // nel request per passarlo alla pagina di destinazione. Tale parametro e'
    // utile quando l'utente clicca sul pulsante 'Indietro'.
    /*
     * if(request.getAttribute("pageFrom") != null)
     * request.setAttribute("pageFrom", request.getAttribute("pageFrom")); else
     * if(request.getParameter("pageFrom") != null)
     * request.setAttribute("pageFrom", request.getParameter("pageFrom"));
     */
    HttpSession sessione = request.getSession();
    ContenitoreDatiRicercaForm contenitore = (ContenitoreDatiRicercaForm)
        sessione.getAttribute(CostantiGenRicerche.OGGETTO_DETTAGLIO);

    if (contenitore.getTestata().getTipoRicerca() != null
        && contenitore.getTestata().getNome() != null) {
      request.setAttribute("testataRicercaForm", contenitore.getTestata());
    }

    try {
      // Caricamento degli oggetti per popolare le comboBox presenti nella
      // pagina Lista per popolamento comboBox 'Tipo Ricerca'
      List<Tabellato> listaTipoRicerca = this.tabellatiManager.getTabellato(
          TabellatiManager.TIPO_RICERCHE);

      // Set nel request delle liste per il popolamento della combobox tipo
      // report
      request.setAttribute("listaTipoRicerca", listaTipoRicerca);

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

    if (logger.isDebugEnabled()) logger.debug("editDatiGen: fine metodo");
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
    // 'Gruppi':
    // il titolo viene mostrato quando l'applicativo e' stato configurato con i
    // gruppi abilitati
    if (this.bloccaGestioneGruppiDisabilitata(request, false, false))
      mostraTitoloGruppi = false;

    boolean mostraTitoloPubblica = true;
    // Condizione discriminante per la visualizzazione del titolo della pagina
    // 'Pubblica':
    // il titolo viene mostrato quando l'utente puo' creare solo report
    // personali,
    // cioe' nel profilo utente e' presente solo l'opzione ou49 e non la ou48.
    if (listaFunzioneUtenteAbilitate.contains(
          CostantiGeneraliAccount.SOLO_REPORT_PERSONALI_GENRIC)) {
      mostraTitoloPubblica = false;
      mostraTitoloGruppi = false;
    }

    if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_ARGOMENTO)) {
      pagineVisitate.add(CostantiWizard.TITOLO_ARGOMENTO);

      pagineDaVisitare.add(CostantiWizard.TITOLO_CAMPI);
      pagineDaVisitare.add(CostantiWizard.TITOLO_FILTRI);
      pagineDaVisitare.add(CostantiWizard.TITOLO_ORDINAMENTI);
      pagineDaVisitare.add(CostantiWizard.TITOLO_LAYOUT);
      if (mostraTitoloPubblica)
        pagineDaVisitare.add(CostantiWizard.TITOLO_PUBBLICA);
      if (mostraTitoloGruppi)
        pagineDaVisitare.add(CostantiWizard.TITOLO_GRUPPI);
      pagineDaVisitare.add(CostantiWizard.TITOLO_DATIGENERALI);
    } else if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_CAMPI)) {
      pagineVisitate.add(CostantiWizard.TITOLO_ARGOMENTO);
      pagineVisitate.add(CostantiWizard.TITOLO_CAMPI);

      pagineDaVisitare.add(CostantiWizard.TITOLO_FILTRI);
      pagineDaVisitare.add(CostantiWizard.TITOLO_ORDINAMENTI);
      pagineDaVisitare.add(CostantiWizard.TITOLO_LAYOUT);
      pagineDaVisitare.add(CostantiWizard.TITOLO_PUBBLICA);
      if (mostraTitoloGruppi)
        pagineDaVisitare.add(CostantiWizard.TITOLO_GRUPPI);
      pagineDaVisitare.add(CostantiWizard.TITOLO_DATIGENERALI);
    } else if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_DOMANDA_FILTRI)
        || paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_FILTRO)
        || paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_LISTA_FILTRI)) {
      pagineVisitate.add(CostantiWizard.TITOLO_ARGOMENTO);
      pagineVisitate.add(CostantiWizard.TITOLO_CAMPI);
      pagineVisitate.add(CostantiWizard.TITOLO_FILTRI);

      pagineDaVisitare.add(CostantiWizard.TITOLO_ORDINAMENTI);
      pagineDaVisitare.add(CostantiWizard.TITOLO_LAYOUT);
      if (mostraTitoloPubblica)
        pagineDaVisitare.add(CostantiWizard.TITOLO_PUBBLICA);
      if (mostraTitoloGruppi)
        pagineDaVisitare.add(CostantiWizard.TITOLO_GRUPPI);
      pagineDaVisitare.add(CostantiWizard.TITOLO_DATIGENERALI);
    } else if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_DOMANDA_ORDINAMENTI)
        || paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_ORDINAMENTO)
        || paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_LISTA_ORDINAMENTI)) {
      pagineVisitate.add(CostantiWizard.TITOLO_ARGOMENTO);
      pagineVisitate.add(CostantiWizard.TITOLO_CAMPI);
      pagineVisitate.add(CostantiWizard.TITOLO_FILTRI);
      pagineVisitate.add(CostantiWizard.TITOLO_ORDINAMENTI);

      pagineDaVisitare.add(CostantiWizard.TITOLO_LAYOUT);
      if (mostraTitoloPubblica)
        pagineDaVisitare.add(CostantiWizard.TITOLO_PUBBLICA);
      if (mostraTitoloGruppi)
        pagineDaVisitare.add(CostantiWizard.TITOLO_GRUPPI);
      pagineDaVisitare.add(CostantiWizard.TITOLO_DATIGENERALI);
    } else if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_LAYOUT)) {
      pagineVisitate.add(CostantiWizard.TITOLO_ARGOMENTO);
      pagineVisitate.add(CostantiWizard.TITOLO_CAMPI);
      pagineVisitate.add(CostantiWizard.TITOLO_FILTRI);
      pagineVisitate.add(CostantiWizard.TITOLO_ORDINAMENTI);
      pagineVisitate.add(CostantiWizard.TITOLO_LAYOUT);

      if (mostraTitoloPubblica)
        pagineDaVisitare.add(CostantiWizard.TITOLO_PUBBLICA);
      if (mostraTitoloGruppi)
        pagineDaVisitare.add(CostantiWizard.TITOLO_GRUPPI);
      pagineDaVisitare.add(CostantiWizard.TITOLO_DATIGENERALI);
    } else if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_PUBBLICAZIONE)) {
      pagineVisitate.add(CostantiWizard.TITOLO_ARGOMENTO);
      pagineVisitate.add(CostantiWizard.TITOLO_CAMPI);
      pagineVisitate.add(CostantiWizard.TITOLO_FILTRI);
      pagineVisitate.add(CostantiWizard.TITOLO_ORDINAMENTI);
      pagineVisitate.add(CostantiWizard.TITOLO_LAYOUT);
      if (mostraTitoloPubblica)
        pagineVisitate.add(CostantiWizard.TITOLO_PUBBLICA);

      if (mostraTitoloGruppi)
        pagineDaVisitare.add(CostantiWizard.TITOLO_GRUPPI);
      pagineDaVisitare.add(CostantiWizard.TITOLO_DATIGENERALI);
    } else if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_GRUPPI)) {
      pagineVisitate.add(CostantiWizard.TITOLO_ARGOMENTO);
      pagineVisitate.add(CostantiWizard.TITOLO_CAMPI);
      pagineVisitate.add(CostantiWizard.TITOLO_FILTRI);
      pagineVisitate.add(CostantiWizard.TITOLO_ORDINAMENTI);
      pagineVisitate.add(CostantiWizard.TITOLO_LAYOUT);
      pagineVisitate.add(CostantiWizard.TITOLO_PUBBLICA);
      pagineVisitate.add(CostantiWizard.TITOLO_GRUPPI);

      pagineDaVisitare.add(CostantiWizard.TITOLO_DATIGENERALI);
    } else if (paginaAttiva.equals(CostantiWizard.CODICE_PAGINA_DATI_GENERALI)) {
      pagineVisitate.add(CostantiWizard.TITOLO_ARGOMENTO);
      pagineVisitate.add(CostantiWizard.TITOLO_CAMPI);
      pagineVisitate.add(CostantiWizard.TITOLO_FILTRI);
      pagineVisitate.add(CostantiWizard.TITOLO_ORDINAMENTI);
      pagineVisitate.add(CostantiWizard.TITOLO_LAYOUT);
      if (mostraTitoloPubblica)
        pagineVisitate.add(CostantiWizard.TITOLO_PUBBLICA);
      if (mostraTitoloGruppi) pagineVisitate.add(CostantiWizard.TITOLO_GRUPPI);
      pagineVisitate.add(CostantiWizard.TITOLO_DATIGENERALI);
    }
    request.setAttribute("pagineVisitate", pagineVisitate);
    request.setAttribute("pagineDaVisitare", pagineDaVisitare);
  }
}