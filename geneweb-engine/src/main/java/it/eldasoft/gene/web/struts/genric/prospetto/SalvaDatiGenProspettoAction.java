/*
 * Created on 07-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genric.prospetto;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.bl.admin.GruppiManager;
import it.eldasoft.gene.bl.genmod.CompositoreException;
import it.eldasoft.gene.bl.genmod.GestioneFileModelloException;
import it.eldasoft.gene.bl.genric.GestoreVisibilitaDati;
import it.eldasoft.gene.bl.genric.ProspettoManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.CostantiGeneraliAccount;
import it.eldasoft.gene.commons.web.domain.GestioneTab;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.admin.Gruppo;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genric.DatiGenProspetto;
import it.eldasoft.gene.db.domain.genric.DatiGenRicerca;
import it.eldasoft.gene.db.domain.genric.GruppoRicerca;
import it.eldasoft.gene.web.struts.genric.AbstractDispatchActionBaseGenRicercheProspetto;
import it.eldasoft.gene.web.struts.genric.CostantiGenRicerche;
import it.eldasoft.utils.metadata.cache.DizionarioSchemi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Schema;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.profiles.CheckOpzioniUtente;
import it.eldasoft.utils.profiles.OpzioniUtente;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
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
 * Action per salvare in sessione dati generali del prospetto in analisi
 *
 * @author Luca Giacomazzo
 */
public class SalvaDatiGenProspettoAction extends
    AbstractDispatchActionBaseGenRicercheProspetto {

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(SalvaDatiGenProspettoAction.class);

  /**
   * Reference al manager per la gestione della tabella W_GENCHIAVI
   */
  private GenChiaviManager genChiaviManager;

  /**
   * Reference alla classe di business logic per l'accesso ai tabellati
   */
  private TabellatiManager tabellatiManager;

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi ai
   * prospetti
   */
  private ProspettoManager prospettoManager;

  /**
   * Reference alla classe di business logic per l'accesso ai dati relativi ai
   * gruppi
   */
  private GruppiManager    gruppiManager;

  /**
   * Reference al Manager per la gestione delle protezioni di tabelle e campi
   * rispetto al profilo attivo
   */
  private GeneManager geneManager;

  /**
   * Reference alla classe di business logic per accesso ai dati relativi alle
   * ricerche
   */
  private RicercheManager     ricercheManager;

  /**
   * @param genChiaviManager
   *        genChiaviManager da settare internamente alla classe.
   */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  /**
   * @param tabellatiManager
   *        tabellatiManager da settare internamente alla classe.
   */
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

  /**
   * @param prospettoManager
   *        prospettoManager da settare internamente alla classe.
   */
  public void setProspettoManager(ProspettoManager prospettoManager) {
    this.prospettoManager = prospettoManager;
  }

  /**
   * @param gruppiManager
   *        gruppiManager da settare internamente alla classe.
   */
  public void setGruppiManager(GruppiManager gruppiManager) {
    this.gruppiManager = gruppiManager;
  }

  /**
   * @param geneManager geneManager da settare internamente alla classe.
   */
  public void setGeneManager(GeneManager geneManager) {
    this.geneManager = geneManager;
  }

  /**
   * @param ricercheManager ricercheManager da settare internamente alla classe.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * insertProspetto
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniInsertProspetto() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_CON_PROSPETTO);
  }

  public ActionForward insertProspetto(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    /*
     * L'operazione di inserimento di una ricerca con modello si compone di 2
     * fasi: 1. attraverso la classe modelliManager si effettua l'insert in
     * W_MODELLI, l'upload del file e la sua compilazione da parte del
     * compositore 2. insert in W_RICERCHE ed eventualmente in W_GRPRIC; Queste
     * 2 operazioni sono implementate in 2 transazioni diverse. Nel caso di
     * eccezioni durante la transazione T2 e' necessario effettuare la rollback
     * dell'operazione in T1 attraverso una apposita chiamata, che in questo
     * caso consiste in modelliManager.deleteModello(...)
     */

    if (logger.isDebugEnabled())
      logger.debug("insertProspetto: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    // Esito dell'operazione di insert della ricerca con modello
    boolean isModelloInserito = false;

    int idProspetto = -1;
    int idModello = -1;
    DatiModello datiModello = null;
    DatiGenRicerca datiGenRicerca = null;
    DatiGenProspettoForm datiGenProspettoForm = null;
    HttpSession session = request.getSession();

    String codApp = (String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO);

    ProfiloUtente profiloUtente = (ProfiloUtente) session.getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    String contesto = null;
    if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_CONTESTI_ABILITATI)))
      contesto = profiloUtente.getUfficioAppartenenza();

    try {
      datiGenProspettoForm = (DatiGenProspettoForm) form;
      datiGenProspettoForm.setCodApp(codApp);

      datiGenRicerca = datiGenProspettoForm.getDatiPerRicercaModel();
      datiModello = datiGenProspettoForm.getDatiPerModelloModel();

      datiModello.setNomeFile(datiGenProspettoForm.getSelezioneFile().getFileName());
      // datiModello.setCodiceApplicativo(codApp);

      // Per default viene impostato il tipo di modello a 'Prosp', invece di
      // leggere il valore dal tabellato. Il tutto perchè il campo su DB e'
      // Not Null.
      // Inoltre per modello associato ad una ricerca con modello non sarà mai
      // possibile visualizzarlo nella lista dei modelli perchè ha il campo
      // prospetto = 1
      datiModello.setTipoModello("Prosp");

      // Setto l'attributo prospetto ad 1, in modo da rendere il modello un
      // prospetto
      datiModello.setProspetto(new Integer(1));

      // si aggiorna il codice solo se non esiste un altro report nel db con lo stesso codice
      if (StringUtils.stripToNull(datiGenRicerca.getCodReportWS()) != null) {
        Integer id = this.ricercheManager.getIdRicercaByCodReportWS(datiGenRicerca.getCodReportWS());
        if (id != null) {
          if (datiGenRicerca.getIdRicerca() == null || id != datiGenRicerca.getIdRicerca()) {
            target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
            messageKey = "warnings.genRic.codReportWS.nonModificabile";
            logger.warn(this.resBundleGenerale.getString(messageKey));
            this.aggiungiMessaggio(request, messageKey);
          }
        }
        //if (testataSessione.getId() != null && testataSessione.getId().)
      } else
        datiGenRicerca.setCodReportWS(null);


      // si inserisce il modello associato al prospetto in W_MODELLI
      this.prospettoManager.insertProspetto(datiModello,
          datiGenProspettoForm.getSelezioneFile().getFileData(), contesto);
      // Aggiornamento del flag per memorizzare l'avvenuto insert del modello
      isModelloInserito = true;

      // Determino l'id_modello del modello appena inserito
      idModello = datiModello.getIdModello();

      // Set di id_prospetto nei dati generali della ricerca
      datiGenRicerca.setIdProspetto(new Integer(datiModello.getIdModello()));

      // Determino l'idRicerca della ricerca che vado ad inserire
      idProspetto = this.genChiaviManager.getNextId("W_RICERCHE");
      datiGenRicerca.setIdRicerca(new Integer(idProspetto));
      // Set della famiglia della ricerca con modello: il valore e' stato
      // ricavato direttamente dal tabellato
      datiGenRicerca.setFamiglia(new Integer(2));

      // Gestione del gruppo di default o meno
      if (this.bloccaGestioneGruppiDisabilitata(request, false, false) &&
              datiGenRicerca.getPersonale() == 0) {

        int idGruppo = -1;
        if(profiloUtente.getIdGruppi() != null && profiloUtente.getIdGruppi().length > 0)
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
            // Per effettuare l'insert dei dati generali della ricerca in
            // W_RICERCHE e per effettuare l'insert nella tabella W_GRPRIC mi
            // appoggio alla classe ProspettoManager, dalla quale, usando
            // l'oggetto RicercheDao, accedo alle funzioni insertTestataRicerca
            // e insertAssociazioneRicercaGruppo che effettuano rispettivamente
            // l'insert in W_RICERCHE e in W_GRPRIC
            this.prospettoManager.insertDatiGenRicerca(datiGenRicerca,
                idGruppo);
          }
        }
      } else {
        // Vedi commento precedente. IdGruppo <= 0 significa che il report non
        // e' ancora stato associato ad un gruppo e nemmeno a quello di default
        this.prospettoManager.insertDatiGenRicerca(datiGenRicerca, 0);
      }
    } catch (CompositoreException e) {
      // Gestione dell'eccezione in compilazione
      target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
      messageKey = e.getChiaveResourceBundle();
      if (e.getParametri() == null) {
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
      } else if (e.getParametri().length == 1) {
        logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
            UtilityStringhe.getPatternParametroMessageBundle(0),
            e.getParametri()[0]), e);
        this.aggiungiMessaggio(request, messageKey, e.getParametri()[0]);
      } else {
        logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
            UtilityStringhe.getPatternParametroMessageBundle(0),
            e.getParametri()[0]).replaceAll(
            UtilityStringhe.getPatternParametroMessageBundle(1),
            e.getParametri()[1]), e);
        this.aggiungiMessaggio(request, messageKey, e.getParametri()[0],
            e.getParametri()[1]);
      }
    } catch (GestioneFileModelloException e) {
      target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
      messageKey = "errors.modelli.uploaderror";
      // Aggiungo l'eventuale codice in più
      if (!e.getCodiceErrore().equals(""))
        messageKey += "." + e.getCodiceErrore();
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);

    } catch (DataIntegrityViolationException e) {
      if (isModelloInserito) {
        // Eccezione durante l'insert in W_RICERCHE, allora devo cancellare
        // il modello appena inserito
        this.prospettoManager.deleteModello(idModello, codApp);
      }
      target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
      if (new OpzioniUtente(profiloUtente.getFunzioniUtenteAbilitate()).isOpzionePresente(CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC))
        messageKey = "errors.prospetti.salva.vincoloUniqueConCodReport";
      else
        messageKey = "errors.prospetti.salva.vincoloUnique";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (DataAccessException e) {
      if (isModelloInserito) {
        // Eccezione durante l'insert in W_RICERCHE, allora devo cancellare
        // il modello appena inserito
        this.prospettoManager.deleteModello(idModello, codApp);

        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.database.dataAccessException";
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
      } else {
        // Eccezione durante l'insert in W_MODELLI
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.database.dataAccessException";
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);

      }
    } catch (RemoteException r) {
      target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
      messageKey = "errors.modelli.compositoreDisattivo";
      logger.error(this.resBundleGenerale.getString(messageKey), r);
      this.aggiungiMessaggio(request, messageKey);

    } catch (Throwable t) {
      // Verifico se l'insert del modello è andata a buon fine: se si allora
      // devo
      // cancellare il modello appena inserito
      if (isModelloInserito)
        this.prospettoManager.deleteModello(idModello, codApp);

      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    } finally {

      // Caricamento degli oggetti per popolare sia la comboBox presente nella
      // pagina di edit per popolamento comboBox 'Tipo Ricerca', sia per
      // popolare
      // lo stesso campo nella pagina di visualizzazione
      List<Tabellato> listaTipoRicerca = this.tabellatiManager.getTabellato(TabellatiManager.TIPO_RICERCHE);
      // Set nel request delle liste per il popolamento delle varie combobox
      request.setAttribute("listaTipoRicerca", listaTipoRicerca);

      if (messageKey != null) {
        // nel caso di errore nei controlli, devo ritornare alla pagina di edit
        // prospetto e predisporre nel request il form gli elenchi
        // schemi/tabelle
        if (CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO.equals(target)) {
          this.setElencoSchemiEntitaNelRequest(request);
          //CostantiGenProspetto.setListaTipoRicercaNelRequest(request);

          request.setAttribute("datiGenProspettoForm", datiGenProspettoForm);
          // Aggiorno del tab menu'
          this.setMenuTab(request, false);
        }
      } else {
        // Se non si verificano eccezioni, allora carico la pagina di dettaglio
        // dei dati generali della ricerca con modello. Aggiorno l'oggetto
        // per la gestione del tab menu', e inserisco nel request l'oggetto
        // datiGenProspettoForm, aggiornato.

        DatiGenProspetto tmpDatiGenPros = new DatiGenProspetto();
        tmpDatiGenPros.setDatiGenRicerca(datiGenRicerca);
        tmpDatiGenPros.setDatiModello(datiModello);
        DatiGenProspettoForm tmpDatiGenProsForm = new DatiGenProspettoForm(
            tmpDatiGenPros);

        // Estrazione delle informazioni relative alla tabella selezionata come
        // argomento principale
        String entita = datiModello.getEntPrinc();
        // Set in tmpDatiGenProsForm degli attributi, schema-descrizione
        // ed entita-descrizione
        this.setInfoEntitaPrincipale(entita, tmpDatiGenProsForm);

        request.setAttribute("datiGenProspettoForm", tmpDatiGenProsForm);

        this.setMenuTab(request, true);

        session.removeAttribute(CostantiGenerali.ID_OGGETTO_SESSION);
        session.removeAttribute(CostantiGenerali.NOME_OGGETTO_SESSION);
        session.setAttribute(CostantiGenerali.ID_OGGETTO_SESSION,
            datiGenRicerca.getIdRicerca().toString());
        session.setAttribute(CostantiGenerali.NOME_OGGETTO_SESSION,
            datiGenRicerca.getNome());

        // Rimuovo l'oggetto in sessione per riabilitare la navigazione;
        request.getSession().removeAttribute(
            CostantiGenerali.DISABILITA_NAVIGAZIONE);
      }
    }

    if (logger.isDebugEnabled()) logger.debug("insertProspetto: fine metodo");

    return mapping.findForward(target);
  }

  /**
   * Funzione che restituisce le opzioni per accedere alla action
   * updateProspetto
   *
   * @return opzioni per accedere alla action
   */
  public CheckOpzioniUtente getOpzioniUpdateProspetto() {
    return new CheckOpzioniUtente(
        CostantiGeneraliAccount.ABILITAZIONE_REPORT_CON_PROSPETTO);
  }

  public ActionForward updateProspetto(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    /*
     * L'operazione di update di una ricerca con modello si compone di 2 fasi:
     * 1. attraverso la classe modelliManager si effettua l'update in W_MODELLI,
     * l'eventuale upload del nuovo file, la sua compilazione da parte del
     * compositore; 2. update in W_RICERCHE; Queste 2 operazioni sono
     * implementate in 2 transazioni diverse. Nel caso di eccezioni durante la
     * transazione T2 e' necessario effettuare la rollback dell'operazione in T1
     * attraverso una apposita chiamata, che in questo caso consiste in
     * modelliManager.deleteModello(...)
     */

    if (logger.isDebugEnabled())
      logger.debug("updateProspetto: inizio metodo");

    // target di default
    String target = CostantiGeneraliStruts.FORWARD_OK;
    String messageKey = null;

    DatiModello datiModello = null;
    DatiGenRicerca datiGenRicerca = null;
    DatiGenProspettoForm datiGenProspettoForm = null;
    HttpSession session = request.getSession();

    String codApp = (String) session.getAttribute(CostantiGenerali.MODULO_ATTIVO);

    ProfiloUtente profiloUtente = (ProfiloUtente) session.getAttribute(
        CostantiGenerali.PROFILO_UTENTE_SESSIONE);
    String contesto = null;
    if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_CONTESTI_ABILITATI)))
      contesto = profiloUtente.getUfficioAppartenenza();

    try {
      datiGenProspettoForm = (DatiGenProspettoForm) form;

      datiGenRicerca = datiGenProspettoForm.getDatiPerRicercaModel();
      datiModello = datiGenProspettoForm.getDatiPerModelloModel();

      if (!this.bloccaGestioneGruppiDisabilitata(request, false, false)) {
        // L.G. 08/06/2007: modifica per controllare il settaggio della ricerca a
        // personale. Se la ricerca e' gia' stata associata/pubblicata ad almeno
        // un gruppo la ricerca non puo' ritornare ad essere personale
        if (datiModello.getPersonale() == 1) {
          List<?> listaGruppi = this.gruppiManager.getGruppiByIdRicerca(
              datiGenRicerca.getIdRicerca().intValue());
          if (listaGruppi != null && listaGruppi.size() > 0) {
            datiModello.setPersonale(0);
            datiGenRicerca.setPersonale(0);
            String message = "warnings.prospetto.datiGenerali.noProspettoPersonaleSeGiaPubblicato";
            logger.warn(this.resBundleGenerale.getString(message));
            this.aggiungiMessaggio(request, message);
          }
        }
        // L.G. 08/06/2007: fine modifica
      }

      // si aggiorna il codice solo se non esiste un altro report nel db con lo stesso codice
      if (StringUtils.stripToNull(datiGenRicerca.getCodReportWS()) != null) {
        Integer id = this.ricercheManager.getIdRicercaByCodReportWS(datiGenRicerca.getCodReportWS());
        if (id != null) {
          if (datiGenRicerca.getIdRicerca() == null || id != datiGenRicerca.getIdRicerca()) {
            target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
            messageKey = "warnings.genRic.codReportWS.nonModificabile";
            logger.warn(this.resBundleGenerale.getString(messageKey));
            this.aggiungiMessaggio(request, messageKey);
          }
        }
        //if (testataSessione.getId() != null && testataSessione.getId().)
      } else
        datiGenRicerca.setCodReportWS(null);

      if (datiGenProspettoForm.getSelezioneFile() != null
          && datiGenProspettoForm.getSelezioneFile().getFileName() != null
          && datiGenProspettoForm.getSelezioneFile().getFileName().length() > 0) {

      // effettuo l'update del modello associato al prospetto in W_MODELLI
      datiModello.setNomeFile(datiGenProspettoForm.getSelezioneFile().getFileName());
      this.prospettoManager.updateProspetto(datiGenRicerca, datiModello,
          datiGenProspettoForm.getSelezioneFile().getFileData(), codApp, profiloUtente.getId(), contesto);
      } else {
        this.prospettoManager.updateProspetto(datiGenRicerca, datiModello,
            null, codApp, profiloUtente.getId(), contesto);
      }

      // SS 25/06/2007: si inserisce anche l'associativa al gruppo di default
      // per la gestione dei gruppi disabilitati solo nel caso in cui il
      // report non sia personale, e la si elimina nel momento in cui si rimette a 1
      // il flag personale
      if (this.bloccaGestioneGruppiDisabilitata(request, false, false)) {
        List<?> listaGruppi = this.gruppiManager.getGruppiByIdRicerca(
            datiGenRicerca.getIdRicerca().intValue());

        if(datiGenRicerca.getPersonale() == 1) {
          //devo eliminare i gruppi
          if (listaGruppi != null && listaGruppi.size() > 0)
            this.prospettoManager.deleteGruppiProspetto(
                datiGenRicerca.getIdRicerca().intValue());
        } else {
          //devo inserire i gruppi se non esistono
          if (listaGruppi == null || listaGruppi.size() == 0) {
            int idGruppo = -1;

            if(profiloUtente.getIdGruppi() != null && profiloUtente.getIdGruppi().length > 0)
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
                // si inserisce il gruppo di default
                GruppoRicerca gruppoRicerca = new GruppoRicerca(
                    gruppo);
                    gruppoRicerca.setAssociato(true);
                    gruppoRicerca.setId(datiGenRicerca.getIdRicerca());
                this.prospettoManager.insertGruppoProspetto(gruppoRicerca);
              }
            }
          }
        }
      }

   } catch (CompositoreException e) {
      // Gestione dell'eccezione in compilazione
      target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
      messageKey = e.getChiaveResourceBundle();
      if (e.getParametri() == null) {
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
      } else if (e.getParametri().length == 1) {
        logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
            UtilityStringhe.getPatternParametroMessageBundle(0),
            e.getParametri()[0]), e);
        this.aggiungiMessaggio(request, messageKey, e.getParametri()[0]);
      } else {
        logger.error(this.resBundleGenerale.getString(messageKey).replaceAll(
            UtilityStringhe.getPatternParametroMessageBundle(0),
            e.getParametri()[0]).replaceAll(
            UtilityStringhe.getPatternParametroMessageBundle(1),
            e.getParametri()[1]), e);
        this.aggiungiMessaggio(request, messageKey, e.getParametri()[0],
            e.getParametri()[1]);
      }

    } catch (GestioneFileModelloException e) {
      target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
      messageKey = "errors.modelli.uploaderror";
      // Aggiungo l'eventuale codice in più
      if (!e.getCodiceErrore().equals(""))
        messageKey += "." + e.getCodiceErrore();
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);

    } catch (DataIntegrityViolationException e) {
      target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
      if (new OpzioniUtente(profiloUtente.getFunzioniUtenteAbilitate()).isOpzionePresente(CostantiGeneraliAccount.GESTIONE_COMPLETA_GENRIC))
        messageKey = "errors.prospetti.salva.vincoloUniqueConCodReport";
      else
        messageKey = "errors.prospetti.salva.vincoloUnique";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (DataAccessException e) {
        // Eccezione durante l'insert in W_MODELLI
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.database.dataAccessException";
        logger.error(this.resBundleGenerale.getString(messageKey), e);
        this.aggiungiMessaggio(request, messageKey);
    } catch (RemoteException r) {
      target = CostantiGenProspetto.FORWARD_ERRORE_INSERIMENTO_PROSPETTO;
      messageKey = "errors.modelli.compositoreDisattivo";
      logger.error(this.resBundleGenerale.getString(messageKey), r);
      this.aggiungiMessaggio(request, messageKey);

    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    } finally {

      // Caricamento degli oggetti per popolare sia la comboBox presente nella
      // pagina di edit per popolamento comboBox 'Tipo Ricerca', sia per
      // popolare lo stesso campo nella pagina di visualizzazione
      List<Tabellato> listaTipoRicerca = this.tabellatiManager.getTabellato(TabellatiManager.TIPO_RICERCHE);
      // Set nel request delle liste per il popolamento delle varie combobox
      request.setAttribute("listaTipoRicerca", listaTipoRicerca);

      if (messageKey == null) {
        // Rimuovo l'oggetto in sessione per riabilitare la navigazione;
        request.getSession().removeAttribute(
            CostantiGenerali.DISABILITA_NAVIGAZIONE);
      }
    }

    if (logger.isDebugEnabled()) logger.debug("updateProspetto: fine metodo");

    return mapping.findForward(target);
  }

  private void setMenuTab(HttpServletRequest request, boolean isVisualizzazione) {
    HttpSession sessione = request.getSession();
    Object obj = sessione.getAttribute(CostantiGenRicerche.NOME_GESTORE_TAB);
    if (isVisualizzazione) {
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
    } else {
      if (obj != null) {
        GestioneTab gestoreTab = (GestioneTab) obj;
        gestoreTab.setTabAttivo(CostantiGenProspetto.TAB_DATI_GENERALI);
        gestoreTab.setTabSelezionabili(null);
      } else {
        GestioneTab gestoreTab = new GestioneTab();
        gestoreTab.setTabAttivo(CostantiGenProspetto.TAB_DATI_GENERALI);
        gestoreTab.setTabSelezionabili(null);
        sessione.setAttribute(CostantiGenRicerche.NOME_GESTORE_TAB, gestoreTab);
      }
    }
  }

  /**
   * Aggiunge al request l'elenco degli schemi e delle tabelle utilizzabili
   *
   * N.B: Metodo copiato dalla classe ModelliAction
   * @param request
   */
  private void setElencoSchemiEntitaNelRequest(HttpServletRequest request) {
    // si popola il request per valorizzare correttamente le combobox degli
    // schemi e degli argomenti
    DizionarioSchemi dizSchemi = DizionarioSchemi.getInstance();
    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();

    List<String> elencoMnemoniciSchemi = dizSchemi.getMnemoniciPerRicerche();
    Vector<Schema> elencoSchemi = new Vector<Schema>();
    String mnemonicoSchema = null;
    Schema schema = null;
    List<String> elencoMnemoniciTabelle = null;
    Vector<Tabella> elencoTabelle = null;
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
      elencoTabelle = new Vector<Tabella>(elencoMnemoniciTabelle.size());
      for (int j = 0; j < elencoMnemoniciTabelle.size(); j++) {
        mnemonicoTabella = elencoMnemoniciTabelle.get(j);
        tabella = dizTabelle.get(mnemonicoTabella);
        if (gestoreVisibilita.checkEntitaVisibile(tabella, profiloAttivo))
          elencoTabelle.addElement(tabella);
      }
      // set nel request dell'elenco dell'elenco delle tabelle associate al
      // j-esimo schema con nome elencoTabelle<codiceSchema>
      if(elencoTabelle.size() > 0){
        request.setAttribute("elencoTabelle" + schema.getCodice(), elencoTabelle);
        elencoSchemi.addElement(schema);
      }
    }
    // set nel request dell'elenco degli schemi
    request.setAttribute("elencoSchemi", elencoSchemi);
  }

  /**
   * Imposta i campi ausiliari per lo schema e l'argomento principale
   *
   * N.B: Metodo copiato e addattato dalla classe ModelliAction
   * @param entita
   *        nome della tabella da cui si parte
   * @param datiGenProspettoForm
   *        form da valorizzare
   */
  private void setInfoEntitaPrincipale(String entita,
      DatiGenProspettoForm datiGenProspettoForm){
    DizionarioSchemi dizSchemi = DizionarioSchemi.getInstance();
    DizionarioTabelle dizTabelle = DizionarioTabelle.getInstance();

    if (entita != null) {
      Tabella t = dizTabelle.getDaNomeTabella(entita);
      Schema s = dizSchemi.get(t.getNomeSchema());
      datiGenProspettoForm.setMneEntPrinc(t.getCodiceMnemonico());
      datiGenProspettoForm.setDescEntPrinc(t.getDescrizione());
      datiGenProspettoForm.setSchemaPrinc(s.getCodice());
      datiGenProspettoForm.setDescSchemaPrinc(s.getDescrizione());
    }
  }

}