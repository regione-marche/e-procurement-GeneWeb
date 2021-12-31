/*
 * Created on 09-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 *
 * Created by Marco Franceschin
 */
package it.eldasoft.gene.web.struts.genmod;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.bl.genmod.CompositoreException;
import it.eldasoft.gene.bl.genmod.GestioneFileModelloException;
import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.bl.genric.GestoreVisibilitaDati;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.admin.Gruppo;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.tags.history.HistoryItem;
import it.eldasoft.gene.tags.history.UtilityHistory;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.tags.GestoreEccezioni;
import it.eldasoft.utils.metadata.cache.DizionarioSchemi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Schema;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.rmi.RemoteException;
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
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Azioni sui modelli
 *
 * @author marco.franceschin
 */
public class ModelliAction extends AbstractDispatchActionBaseGenModelli {

  private static final String TORNA_A_LISTA_MODELLI = "tornaAListaModelli";

  private static final String TORNA_A_TROVA_MODELLI = "tornaATrovaModelli";

  private static final String TORNA_A_HOME_PAGE     = "tornaAHomePage";

  /* logger della classe */
  static Logger               logger                = Logger.getLogger(ModelliAction.class);

  /** Manager dei modelli */
  private ModelliManager      modelliManager;

  /** Manager dei tabellati */
  private TabellatiManager    tabellatiManager;

  /** Manager dei gruppi */
  private GruppiManager       gruppiManager;

  /**
   * Reference al Manager per la gestione delle protezioni di tabelle e campi
   * rispetto al profilo attivo
   */
  private GeneManager         geneManager;

  /**
   * @param modelliManager
   *        The modelliManager to set.
   */
  public void setModelliManager(ModelliManager modelliManager) {
    this.modelliManager = modelliManager;
  }

  /**
   * @param tabellatiManager
   *        The tabellatiManager to set.
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
   * @param geneManager
   *        geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * dettaglioModello
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniDettaglioModello() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  /**
   * Inizializzazione di un dettaglio di un modello
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward dettaglioModello(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 29/08/2006 M.F. Prima Versione
    // ************************************************************

    if (logger.isDebugEnabled())
      logger.debug("dettaglioModello: inizio metodo");
    String target = CostantiGenModelli.FORWARD_OK_DETTAGLIO_MODELLO;

    int idModello = -1;
    String messageKey = null;
    try {
      // Estraggo l'identificativo del modello (campo chiave)
      if (request.getAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO) != null) {
        idModello = ((Integer) request.getAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO)).intValue();
      }
      else if (request.getParameter(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO) != null)
        idModello = Integer.parseInt(request.getParameter(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO));

      // Estraggo i dati del modello
      DatiModello datiModello = this.modelliManager.getModelloById(idModello);
      // Estraggo la descrizione del tabellato per la visualizzazione
      datiModello.setTipoModello(this.tabellatiManager.getDescrTabellato(
          TabellatiManager.TIPO_MODELLI, datiModello.getTipoModello()));

      DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();
      Tabella tabella = dizTabelle.getDaNomeTabella(datiModello.getEntPrinc());
      boolean isEntPrincVisibile = this.geneManager.getGestoreVisibilitaDati().checkEntitaVisibile(
          tabella,
          (String) request.getSession().getAttribute(
              CostantiGenerali.PROFILO_ATTIVO));

      if (!isEntPrincVisibile) {
        messageKey = "warnings.modelli.caricaModello.modelloModificatoDaProfilo";
        logger.warn(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey);
      }
      // Setto i dati del modello
      ModelliForm modelloForm = new ModelliForm(datiModello);

      // estrae le informazioni relative alla tabella selezionata come argomento
      // principale
      String entita = datiModello.getEntPrinc();
      this.setInfoEntitaPrincipale(entita, modelloForm);

      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_DATI_MODELLO,
          modelloForm);
      // Aggiungo l'identificativo del modello per la gestione dei link nei tab
      // e nelle azioni di contesto
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO,
          new Integer(idModello));

      HttpSession session = request.getSession();
      // set in sessione del nome del modello di cui si sta facendo il dettaglio
      session.setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
          datiModello.getNomeModello());

      // -------------------------------------------------------------------------
      // Solo nel caso si acceda al dettaglio di un modello dalla pagina Lista
      // modelli di gruppo, nel request e' presente in parameter denominato
      // 'admin'.
      // Questo parameter avvia la seguente operazione:
      // si ottiene una referenza all'oggetto presente in sessione e contenente
      // i dati del history. All'ultimo elemento presente nel history si cambia
      // l'attributo Id: in questo modo nella pagina di dettaglio del modello
      // nel sotto menu' "Torna a..." del menu azioni viene visualizzato il link
      // Dettaglio Modello - <nomeModello>, il quale permette di tornare al
      // dettaglio del modello dai cui si era partiti
      // -------------------------------------------------------------------------
      String tmp = request.getParameter("admin");
      if (tmp != null) {
        UtilityHistory history = UtilityTags.getUtilityHistory(request.getSession());
        HistoryItem hItem = history.get(history.size(0) - 1, 0);
        hItem.setId("scheda1");
      }

      // Setto i dati per la gestione dei tab
      ModelliAction.setMenuTab(request, true);
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

    if (logger.isDebugEnabled()) logger.debug("dettaglioModello: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Imposta i campi ausiliari per lo schema e l'argomento principale
   *
   * @param entita
   *        nome della tabella da cui si parte
   * @param modelloForm
   *        form da valorizzare
   */
  private void setInfoEntitaPrincipale(String entita, ModelliForm modelloForm) {
    DizionarioSchemi dizSchemi = DizionarioSchemi.getInstance();
    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();

    if (entita != null) {
      Tabella t = dizTabelle.getDaNomeTabella(entita);
      Schema s = dizSchemi.get(t.getNomeSchema());
      modelloForm.setMneEntPrinc(t.getCodiceMnemonico());
      modelloForm.setDescEntPrinc(t.getDescrizione());
      modelloForm.setSchemaPrinc(s.getCodice());
      modelloForm.setDescSchemaPrinc(s.getDescrizione());
    }
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * modificaModello
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniModificaModello() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  /**
   * Funzione che gestisce la modifica dei dati di un modello
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward modificaModello(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // Di default chiama la maschera con la modifica del modello
    String target = CostantiGenModelli.FORWARD_OK_MODIFICA_MODELLO;

    if (logger.isDebugEnabled())
      logger.debug("modificaModello: inizio metodo");

    int idModello = -1;
    String messageKey = null;
    try {

      this.setElencoSchemiEntitaNelRequest(request);

      // Estraggo l'identificativo del modello (campo chiave)
      if (request.getAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO) != null) {
        idModello = ((Integer) request.getAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO)).intValue();
      }
      if (request.getParameter(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO) != null)
        idModello = Integer.parseInt(request.getParameter(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO));

      // Estraggo i dati del modello
      DatiModello datiModello = this.modelliManager.getModelloById(idModello);
      // Setto i dati del modello
      ModelliForm modelloForm = new ModelliForm(datiModello);

      /*
       * DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance(); Tabella
       * tabella = dizTabelle.getDaNomeTabella(datiModello.getEntPrinc());
       * boolean isEntPrincVisibile = this.geneManager.getProfili().checkProtec(
       * (String) request.getSession().getAttribute(
       * CostantiGenerali.PROFILO_ATTIVO), "TABS", "VIS",
       * tabella.getNomeSchema().concat(
       * CostantiGenRicerche.SEPARATORE_SCHEMA_TABELLA_CAMPO).concat(
       * tabella.getNomeTabella()));
       *
       * if(! isEntPrincVisibile){ //messageKey =
       * "warnings.modelli.caricaModello.modelloModificatoDaProfilo";
       * //logger.warn(this.resBundleGenerale.getString(messageKey));
       * //this.aggiungiMessaggio(request, messageKey); // Reset degli attributi
       * dell'oggetto ModelloForm legati all'entita' non // piu' visibile nel
       * profilo attivo //modelloForm.setDescEntPrinc(null);
       * //modelloForm.setDescSchemaPrinc(null); //modelloForm.setEntPrinc("");
       * //modelloForm.setSchemaPrinc(""); //modelloForm.setMneEntPrinc(null); }
       */

      // estrae le informazioni relative alla tabella selezionata come argomento
      // principale
      String entita = datiModello.getEntPrinc();
      this.setInfoEntitaPrincipale(entita, modelloForm);

      // Salvo nel request la lista dei tipi di modelli
      request.setAttribute("listaTipoModello",
          this.tabellatiManager.getTabellato(TabellatiManager.TIPO_MODELLI));

      // Salvo nel request il modello da modificare
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_DATI_MODELLO,
          modelloForm);
      // Aggiungo l'identificativo del modello per la gestione dei link nei tab
      // e nelle azioni di contesto
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO,
          new Integer(idModello));

      HttpSession session = request.getSession();
      // set in sessione del nome del modello di cui si sta facendo il dettaglio
      session.setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
          datiModello.getNomeModello());

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      // Setto i dati per la gestione dei tab
      ModelliAction.setMenuTab(request,
          CostantiGenModelli.FORWARD_OK_MODIFICA_MODELLO.equals(target));

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

    if (logger.isDebugEnabled()) logger.debug("modificaModello: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action creaModello
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniCreaModello() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  public ActionForward creaModello(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String target = CostantiGenModelli.FORWARD_OK_MODIFICA_MODELLO;
    if (logger.isDebugEnabled()) logger.debug("creaModello: inizio metodo");
    int idModello = -1;
    String messageKey = null;
    try {

      this.setElencoSchemiEntitaNelRequest(request);

      // Rimozione dalla sessione di oggetti comuni ai vari moduli
      // dell'applicazione, quali
      // CostantiGenerali.ID_OGGETTO_SESSION e
      // CostantiGenerali.NOME_OGGETTO_SESSION
      HttpSession sessione = request.getSession();
      sessione.removeAttribute(CostantiGenerali.ID_OGGETTO_SESSION);
      sessione.removeAttribute(CostantiGenerali.NOME_OGGETTO_SESSION);

      ModelliForm modelloForm = new ModelliForm();
      modelloForm.setDisponibile(true);
      modelloForm.setPersonale(true);
      modelloForm.setOwner(new Integer(
          ((ProfiloUtente) sessione.getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId()));
      modelloForm.setProfiloOwner((String) sessione.getAttribute(CostantiGenerali.PROFILO_ATTIVO));

      String tmpSchema = ConfigManager.getValore(CostantiGenModelli.PROP_DEFAULT_SCHEMA);
      String tmpEntita = ConfigManager.getValore(CostantiGenModelli.PROP_DEFAULT_ENTITA);
      if(tmpSchema != null && tmpSchema.length() > 0)
        modelloForm.setSchemaPrinc(tmpSchema.trim());
      if(tmpEntita != null && tmpEntita.length() > 0)
        modelloForm.setEntPrinc(tmpEntita.trim());

      // Salvo nel request la lista dei tipi di modelli
      request.setAttribute("listaTipoModello",
          this.tabellatiManager.getTabellato(TabellatiManager.TIPO_MODELLI));
      // Salvo nel request un modello vuoto
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_DATI_MODELLO,
          modelloForm);

      // Setto l'identificativo del modello
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO,
          new Integer(idModello));

      // set nel request del parameter per disabilitare la navigazione in fase
      // di editing
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);

      // Setto i dati per la gestione dei tab
      ModelliAction.setMenuTab(request,
          CostantiGenModelli.FORWARD_OK_MODIFICA_MODELLO.equals(target));
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

    if (logger.isDebugEnabled()) logger.debug("creaModello: fine metodo");
    return mapping.findForward(target);

  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action annullaCrea
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniAnnullaCrea() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  /**
   * Azione che gestisce l'evento di annullamento di creazione di un nuovo
   * modello.
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

    switch (history.size(0)) {
    case 0:
      actForward = mapping.findForward(TORNA_A_HOME_PAGE);
      break;
    case 1:
      actForward = mapping.findForward(TORNA_A_TROVA_MODELLI);
      break;
    case 2:
      actForward = mapping.findForward(TORNA_A_LISTA_MODELLI);
      break;
    case 3:
      try {
        actForward = history.last(request);
      } catch (Throwable t) {
        actForward = GestoreEccezioni.gestisciEccezioneAction(t, this, request,
            logger, mapping);
      }
      break;
    }

    if (logger.isDebugEnabled()) logger.debug("annullaCrea: fine metodo");
    return actForward;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action insertModello
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniInsertModello() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  public ActionForward insertModello(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("insertModello: inizio metodo");

    // Di default rivisualizza il dettaglio del modello
    String target = CostantiGenModelli.FORWARD_OK_UPDATE_MODELLO;

    int idModello = -1;
    String messageKey = null;
    ModelliForm modelliForm = null;
    DatiModello datiModello = null;
    HttpSession session = request.getSession();
    modelliForm = (ModelliForm) form;
    try {
      datiModello = modelliForm.getDatiPerModel();

      // Salvo nel request i dati del modello che si sta inserendo per
      // riproporli ad un'eventuale errore
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_DATI_MODELLO,
          modelliForm);
      // Setto l'identificativo del modello
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO,
          new Integer(idModello));
      // Salvo nel request la lista dei tipi di modelli
      request.setAttribute("listaTipoModello",
          this.tabellatiManager.getTabellato(TabellatiManager.TIPO_MODELLI));

      // Gestisco la copia di un modello
      // this.modelliManager.gestisciFileModello(modelliForm.getNomeFile(),
      // modelliForm.getSelezioneFile().getFileName(),
      // modelliForm.getSelezioneFile().getFileData());
      datiModello.setNomeFile(modelliForm.getSelezioneFile().getFileName().replaceAll("[^a-zA-Z0-9_.-?()=\\[\\]]", "_"));
      datiModello.setCodiceApplicativo((String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO));

      ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      String contesto = null;
      if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_CONTESTI_ABILITATI)))
        contesto = profiloUtente.getUfficioAppartenenza();

      // SS 25/06/2007: si inserisce anche l'associativa al gruppo di default
      // per la gestione dei gruppi disabilitati solo nel caso in cui il
      // modello non sia personale
      if (datiModello.getPersonale() != 1 && this.bloccaGestioneGruppiDisabilitata(request, false, false)) {
        int idGruppo = -1;
        if (profiloUtente.getIdGruppi() != null
            && profiloUtente.getIdGruppi().length > 0)
          idGruppo = profiloUtente.getIdGruppi()[0].intValue();

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
            // Insert del modello e del gruppo di default associato
            this.modelliManager.insertModello(datiModello,
                gruppo.getIdGruppo(),
                modelliForm.getSelezioneFile().getFileData(),
                (String) request.getSession().getAttribute(
                    CostantiGenerali.MODULO_ATTIVO), contesto);
          }
        }
      } else {
        // Insert del solo modello
        this.modelliManager.insertModello(datiModello,
            modelliForm.getSelezioneFile().getFileData(),
            (String) request.getSession().getAttribute(
                CostantiGenerali.MODULO_ATTIVO), contesto);
      }

      // Eseguo l'update dei dati del modello
      idModello = datiModello.getIdModello();
      // Estraggo la descrizione del tabellato per la visualizzazione
      datiModello.setTipoModello(this.tabellatiManager.getDescrTabellato(
          TabellatiManager.TIPO_MODELLI, datiModello.getTipoModello()));
      modelliForm = new ModelliForm(datiModello);
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_DATI_MODELLO,
          modelliForm);
      // {MF080906} Dopo l'update salvo l'id del modello nel response
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO,
          new Integer(idModello));

      // set in sessione del nome del modello di cui si sta facendo il dettaglio
      session.setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
          datiModello.getNomeModello());

    } catch (CompositoreException e) {
      // Gestione dell'eccezione in compilazione
      target = CostantiGenModelli.FORWARD_ERRORE_INSERIMENTO_MODELLO;

      messageKey = e.getChiaveResourceBundle();
      if (e.getParametri() == null) {
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
      } else {
        logger.error(UtilityStringhe.replaceParametriMessageBundle(
            this.resBundleGenerale.getString(messageKey), e.getParametri()), e);
        this.aggiungiMessaggio(request, messageKey, e.getParametri()[0]);
      }

    } catch (GestioneFileModelloException e) {
      target = CostantiGenModelli.FORWARD_ERRORE_INSERIMENTO_MODELLO;
      messageKey = "errors.modelli.uploaderror";
      // Aggiungo l'eventuale codice in più
      if (!e.getCodiceErrore().equals(""))
        messageKey += "." + e.getCodiceErrore();
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (DataIntegrityViolationException e) {
      target = CostantiGenModelli.FORWARD_ERRORE_INSERIMENTO_MODELLO;
      messageKey = "errors.modelli.salva.vincoloUnique";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
      // // Se si è verificato un errore allora elimino il file appena
      // // copiato
      // this.modelliManager.gestisciFileFisicoModello(datiModello.getNomeFile(),
      // ModelliManager.GESTIONE_MODELLO_ELIMINA);
    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
      // // Se si è verificato un errore allora elimino il file appena
      // // copiato
      // this.modelliManager.gestisciFileFisicoModello(datiModello.getNomeFile(),
      // ModelliManager.GESTIONE_MODELLO_ELIMINA);

    } catch (RemoteException r) {
      target = CostantiGenModelli.FORWARD_ERRORE_INSERIMENTO_MODELLO;
      messageKey = "errors.modelli.compositoreDisattivo";
      logger.error(this.resBundleGenerale.getString(messageKey), r);
      this.aggiungiMessaggio(request, messageKey);

    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }
    // Setto i dati per la gestione dei tab
    ModelliAction.setMenuTab(request,
        CostantiGenModelli.FORWARD_OK_UPDATE_MODELLO.equals(target));

    // nel caso di errore nei controlli, devo ritornare alla pagina di edit
    // modello, quindi devo ripredisporre nel request il form, e reinserire gli
    // elenchi schemi/tabelle
    if (CostantiGenModelli.FORWARD_ERRORE_INSERIMENTO_MODELLO.equals(target)) {
      this.setElencoSchemiEntitaNelRequest(request);
      this.setInfoEntitaPrincipale(modelliForm.getEntPrinc(), modelliForm);
      // set nel request del parameter per disabilitare la navigazione
      request.setAttribute(CostantiGenerali.NAVIGAZIONE_DISABILITATA,
          CostantiGenerali.DISABILITA_NAVIGAZIONE);
    }

    // ENNESIMO TACCONE DOVUTO AL FATTO CHE LA GESTIONE DEI LINK DI SINISTRA E'
    // FATTO USANDO UN'UNICA JSP, CON IL SET DI UNA INFORMAZIONE NELLA FORWARD
    // DELLO STRUTS CONFIG. ORA NON SE NE VIENE PIU' FUORI, ESISTONO ANCORA DEI
    // CASI IN CUI A SINISTRA IL MENU' AZIONI NON HA LE VOCI, E SONO COSTRETTO A
    // RIPARARE IN QUESTO MODO!!!
    if (!CostantiGenModelli.FORWARD_OK_UPDATE_MODELLO.equals(target))
      request.setAttribute("metodo", "modificaModello");

    if (logger.isDebugEnabled()) logger.debug("insertModello: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Aggiunge al request l'elenco degli schemi e delle tabelle utilizzabili
   *
   * @param request
   */
  private void setElencoSchemiEntitaNelRequest(HttpServletRequest request) {
    // si popola il request per valorizzare correttamente le combobox degli
    // schemi e degli argomenti
    DizionarioSchemi dizSchemi = DizionarioSchemi.getInstance();
    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();

    List<String> elencoMnemoniciSchemi = dizSchemi.getMnemoniciPerRicerche();
    List<Schema> elencoSchemi = new ArrayList<Schema>();
    String mnemonicoSchema = null;
    Schema schema = null;
    List<String> elencoMnemoniciTabelle = null;
    List<Tabella> elencoTabelle = null;
    String mnemonicoTabella = null;
    Tabella tabella = null;

    GestoreVisibilitaDati gestoreVisibilita = this.geneManager.getGestoreVisibilitaDati();
    String profiloAttivo = (String) request.getSession().getAttribute(
        CostantiGenerali.PROFILO_ATTIVO);

    // estrae dai dizionari l'elenco degli schemi e delle tabelle,
    // creando una lista di schemi, e una lista di tabelle per ogni
    // schema, ognuno dei quali viene posto nel request sotto un
    // nome "elencoTabelle"+nome dello schema dell'elenco delle tabelle
    for (int i = 0; i < elencoMnemoniciSchemi.size(); i++) {
      mnemonicoSchema = elencoMnemoniciSchemi.get(i);
      schema = dizSchemi.get(mnemonicoSchema);
      elencoMnemoniciTabelle = schema.getMnemoniciTabellePerRicerche();
      elencoTabelle = new ArrayList<Tabella>();
      for (int j = 0; j < elencoMnemoniciTabelle.size(); j++) {
        mnemonicoTabella = elencoMnemoniciTabelle.get(j);
        tabella = dizTabelle.get(mnemonicoTabella);
        if (gestoreVisibilita.checkEntitaVisibile(tabella, profiloAttivo)) {
          elencoTabelle.add(tabella);
        }
      }
      // set nel request dell'elenco dell'elenco delle tabelle associate al
      // j-esimo schema con nome elencoTabelle<codiceSchema>
      if (elencoTabelle.size() > 0) {
        request.setAttribute("elencoTabelle" + schema.getCodice(),
            elencoTabelle);
        elencoSchemi.add(schema);
      }
    }
    // set nel request dell'elenco degli schemi
    request.setAttribute("elencoSchemi", elencoSchemi);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action updateModello
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniUpdateModello() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  public ActionForward updateModello(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) logger.debug("updateModello: inizio metodo");
    // Di default rivisualizza il dettaglio del modello
    String target = CostantiGenModelli.FORWARD_OK_UPDATE_MODELLO;

    int idModello = -1;
    String messageKey = null;
    ModelliForm modelliForm = (ModelliForm) form;
    try {
      DatiModello datiModello = modelliForm.getDatiPerModel();

      // Eseguo l'update dei dati del modello
      idModello = datiModello.getIdModello();
      // Salvo nel request la lista dei modelli

      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_DATI_MODELLO,
          modelliForm);
      // Setto l'identificativo del modello
      request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO,
          new Integer(idModello));
      // Salvo nel request la lista dei tipi di modelli
      request.setAttribute("listaTipoModello",
          this.tabellatiManager.getTabellato(TabellatiManager.TIPO_MODELLI));

      ProfiloUtente profiloUtente = (ProfiloUtente) request.getSession().getAttribute(
          CostantiGenerali.PROFILO_UTENTE_SESSIONE);
      String contesto = null;
      if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_CONTESTI_ABILITATI)))
        contesto = profiloUtente.getUfficioAppartenenza();

      // SS 25/06/2007: si inserisce anche l'associativa al gruppo di default
      // per la gestione dei gruppi disabilitati solo nel caso in cui il
      // modello non sia personale, e la si elimina nel momento in cui si
      // rimette a 1
      // il flag personale
      if (this.bloccaGestioneGruppiDisabilitata(request, false, false)) {
        List listaGruppi = this.modelliManager.getGruppiModello(idModello);

        if (datiModello.getPersonale() == 1) {
          // devo eliminare i gruppi
          if (listaGruppi != null && listaGruppi.size() > 0)
            this.modelliManager.deleteGruppiModello(idModello);
        } else {
          // devo inserire i gruppi se non esistono
          if (listaGruppi == null || listaGruppi.size() == 0) {
            int idGruppo = -1;
            if (profiloUtente.getIdGruppi() != null
                && profiloUtente.getIdGruppi().length > 0)
              idGruppo = profiloUtente.getIdGruppi()[0].intValue();

            if (idGruppo < 0) {
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
                // si inserisce il gruppo di default
                this.modelliManager.updateGruppiModello(idModello,
                    new Integer[] { new Integer(idGruppo) },
                    (String) request.getSession().getAttribute(
                        CostantiGenerali.PROFILO_ATTIVO));
              }
            }
          }
        }
      } else {
        // L.G. 08/06/2007: modifica per controllare il settaggio del modello a
        // personale. Se il modello e' gia' stato associato/pubblicato ad almeno
        // un
        // gruppo il modello non puo' ritornare ad essere personale
        if (datiModello.getPersonale() == 1) {
          List listaGruppi = this.modelliManager.getGruppiModello(idModello);
          if (listaGruppi != null && listaGruppi.size() > 0) {
            datiModello.setPersonale(0);
            messageKey = "warnings.modelli.datiGenerali.noModelloPersonaleSeGiaPubblicato";
            logger.warn(this.resBundleGenerale.getString(messageKey));
            this.aggiungiMessaggio(request, messageKey);
          }
        }
        // L.G. 08/06/2007: fine modifica
      }

      if (CostantiGenModelli.FORWARD_OK_UPDATE_MODELLO.equals(target)) {
        if (modelliForm.getSelezioneFile() != null
            && modelliForm.getSelezioneFile().getFileName() != null
            && modelliForm.getSelezioneFile().getFileName().length() > 0) {
          this.modelliManager.updateModello(datiModello,
              modelliForm.getSelezioneFile().getFileData(),
              (String) request.getSession().getAttribute(
                  CostantiGenerali.MODULO_ATTIVO), profiloUtente.getId(), contesto);
        } else {
          // update del modello, ma non del file ad esso associato
          this.modelliManager.updateModello(datiModello, null,
              (String) request.getSession().getAttribute(
                  CostantiGenerali.MODULO_ATTIVO), profiloUtente.getId(), contesto);
        }
        // Estraggo la descrizione del tabellato per la visualizzazione
        datiModello.setTipoModello(this.tabellatiManager.getDescrTabellato(
            TabellatiManager.TIPO_MODELLI, datiModello.getTipoModello()));
        modelliForm = new ModelliForm(datiModello);
        request.setAttribute(CostantiGenModelli.ATTRIBUTO_REQUEST_DATI_MODELLO,
            modelliForm);
      }
    } catch (CompositoreException e) {
      // Gestione dell'eccezione in compilazione
      target = CostantiGenModelli.FORWARD_ERRORE_UPDATE_MODELLO;

      messageKey = e.getChiaveResourceBundle();
      if (e.getParametri() == null) {
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
      } else if (e.getParametri().length == 1) {
        logger.error(UtilityStringhe.replaceParametriMessageBundle(
            this.resBundleGenerale.getString(messageKey), e.getParametri()), e);
        this.aggiungiMessaggio(request, messageKey, e.getParametri()[0]);
      } else {
        logger.error(UtilityStringhe.replaceParametriMessageBundle(
            this.resBundleGenerale.getString(messageKey), e.getParametri()), e);
        this.aggiungiMessaggio(request, messageKey, e.getParametri()[0],
            e.getParametri()[1]);
      }

    } catch (GestioneFileModelloException e) {
      target = CostantiGenModelli.FORWARD_ERRORE_UPDATE_MODELLO;
      messageKey = "errors.modelli.uploaderror";

      // Aggiungo l'eventuale codice in più
      if (!e.getCodiceErrore().equals(""))
        messageKey += "." + e.getCodiceErrore();
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);

    } catch (DataIntegrityViolationException e) {
      target = CostantiGenModelli.FORWARD_ERRORE_UPDATE_MODELLO;
      messageKey = "errors.modelli.salva.vincoloUnique";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);

    } catch (RemoteException r) {
      target = CostantiGenModelli.FORWARD_ERRORE_UPDATE_MODELLO;
      messageKey = "errors.modelli.compositoreDisattivo";
      logger.error(this.resBundleGenerale.getString(messageKey), r);
      this.aggiungiMessaggio(request, messageKey);

    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }
    // Setto i dati per la gestione dei tab
    ModelliAction.setMenuTab(request,
        CostantiGenModelli.FORWARD_OK_UPDATE_MODELLO.equals(target));

    // nel caso di errore nei controlli, devo ritornare alla pagina di edit
    // modello, quindi devo ripredisporre nel request il form, e reinserire gli
    // elenchi schemi/tabelle
    if (CostantiGenModelli.FORWARD_ERRORE_UPDATE_MODELLO.equals(target)) {
      this.setElencoSchemiEntitaNelRequest(request);
      this.setInfoEntitaPrincipale(modelliForm.getEntPrinc(), modelliForm);
    }

    // ENNESIMO TACCONE DOVUTO AL FATTO CHE LA GESTIONE DEI LINK DI SINISTRA E'
    // FATTO USANDO UN'UNICA JSP, CON IL SET DI UNA INFORMAZIONE NELLA FORWARD
    // DELLO STRUTS CONFIG. ORA NON SE NE VIENE PIU' FUORI, ESISTONO ANCORA DEI
    // CASI IN CUI A SINISTRA IL MENU' AZIONI NON HA LE VOCI, E SONO COSTRETTO A
    // RIPARARE IN QUESTO MODO!!!
    if (!CostantiGenModelli.FORWARD_OK_UPDATE_MODELLO.equals(target))
      request.setAttribute("metodo", "modificaModello");

    if (logger.isDebugEnabled()) logger.debug("updateModello: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * downloadModello
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniDownloadModello() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  /**
   * Richiesta di download da parte dell'utente
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
    if (logger.isDebugEnabled())
      logger.debug("downloadModello: inizio metodo");

    String messageKey, errorTarget;
    String target = null;
    if (logger.isDebugEnabled()) {
      logger.debug("Download modello da " + request.getParameter("da"));
    }
    errorTarget = CostantiGenModelli.FORWARD_ERRORE_DOWNLOAD_DETTAGLIO;
    if (request.getParameter("da") != null) {
      if (request.getParameter("da").equals("modifica"))
        errorTarget = CostantiGenModelli.FORWARD_ERRORE_DOWNLOAD_MODIFICA;
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
   * Funzione che restituisce le opzioni per accedere alla action deleteModello
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniDeleteModello() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.CONDIZIONE_ACCESSO_GENMOD);
  }

  /**
   * Eliminazione di un documento
   *
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward deleteModello(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 05/09/2006 M.F. Prima Versione
    // ************************************************************

    if (logger.isDebugEnabled()) logger.debug("eliminaModello: inizio metodo");
    // F.D. 11/04/08 il codice della delete viene generalizzato per utilizarlo
    // anche nella delete export
    // Di default rivisualizza il dettaglio del modello
    String target = CostantiGenModelli.FORWARD_MODELLO_ELIMINATO;

    int idModello = -1;
    String messageKey = null;
    try {
      // Estraggo l'identificativo del modello (campo chiave)
      idModello = Integer.parseInt(request.getParameter(CostantiGenModelli.ATTRIBUTO_REQUEST_ID_MODELLO));
      modelliManager.deleteModello(idModello,
          (String) request.getSession().getAttribute(
              CostantiGenerali.MODULO_ATTIVO));
    } catch (GestioneFileModelloException e) {
      messageKey = "errors.modelli.delete";
      // Aggiungo l'eventuale codice in più
      if (!e.getCodiceErrore().equals("")) messageKey += e.getCodiceErrore();

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

    if (logger.isDebugEnabled()) logger.debug("eliminaModello: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Funzione che inizializza nel request la gestione dei tab
   *
   * @param request
   */
  public static void setMenuTab(HttpServletRequest request,
      boolean isVisualizzazione) {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 30/08/2006 M.F. Prima Versione
    // ************************************************************
    GestioneTab gestoreTab = (GestioneTab) request.getAttribute(CostantiGenModelli.NOME_GESTORE_TAB);

    if (gestoreTab == null) {
      gestoreTab = new GestioneTab();
      request.setAttribute(CostantiGenModelli.NOME_GESTORE_TAB, gestoreTab);
    }

    Vector metodi = new Vector();
    metodi.add(CostantiGenModelli.METODO_DETTAGLIO_MODELLO);
    metodi.add(CostantiGenModelli.METODO_MODIFICA_MODELLO);
    metodi.add(CostantiGenModelli.METODO_CREA_MODELLO);
    metodi.add(CostantiGenModelli.METODO_AGGIORNA_MODELLO);
    metodi.add(CostantiGenModelli.METODO_INSERISCI_MODELLO);
    metodi.add(CostantiGenModelli.METODO_LISTA_GRUPPI_MODELLO);
    metodi.add(CostantiGenModelli.METODO_LISTA_GRUPPI_MODIFICA);

    int idxMetodo = metodi.indexOf(request.getParameter("metodo"));
    switch (idxMetodo) {
    case 0:
    case 1:
    case 2:
    case 3:
    case 4:
      gestoreTab.setTabAttivo(CostantiGenModelli.TAB_DETTAGLIO);
      if (idxMetodo == 0)
        gestoreTab.setTabSelezionabili(new String[] {
            CostantiGenModelli.TAB_GRUPPI, CostantiGenModelli.TAB_PARAMETRI });
      if ((idxMetodo == 3 || idxMetodo == 4) && isVisualizzazione)
        gestoreTab.setTabSelezionabili(new String[] {
            CostantiGenModelli.TAB_GRUPPI, CostantiGenModelli.TAB_PARAMETRI });
      break;
    case 5:
    case 6:
      gestoreTab.setTabAttivo(CostantiGenModelli.TAB_GRUPPI);
      // Solo se in eliminazione posso selezionare il dettaglio
      if (idxMetodo == 5)
        gestoreTab.setTabSelezionabili(new String[] {
            CostantiGenModelli.TAB_DETTAGLIO, CostantiGenModelli.TAB_PARAMETRI });
      break;
    default:
      gestoreTab.setTabAttivo(CostantiGenModelli.TAB_DETTAGLIO);
      gestoreTab.setTabSelezionabili(new String[] {
          CostantiGenModelli.TAB_GRUPPI, CostantiGenModelli.TAB_PARAMETRI });
    }
  }

}
