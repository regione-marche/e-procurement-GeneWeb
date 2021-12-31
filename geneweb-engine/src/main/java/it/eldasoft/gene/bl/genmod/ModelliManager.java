/*
 * Created on 21-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.genmod;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.dao.ModelliDao;
import it.eldasoft.gene.db.domain.admin.ModelloGruppo;
import it.eldasoft.gene.db.domain.genmod.CacheParametroComposizione;
import it.eldasoft.gene.db.domain.genmod.ContenitoreDatiModello;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genmod.ParametroComposizione;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.gene.db.domain.genmod.TrovaModelli;
import it.eldasoft.gene.web.struts.genmod.CostantiGenModelli;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

/**
 * Manager che si occupa di gestire tutte le operazioni di business logic sulla
 * parte di gestione delle ricerche e l'associazione o meno ad un gruppo
 *
 * @author Luca.Giacomazzo
 */
public class ModelliManager {

  private static final String SEPARATORE_PROP                    = ".";

  protected ModelliDao        modelliDao;

  protected GenChiaviManager  genChiaviManager;

  /** Logger Log4J di classe */
  static Logger               logger                             = Logger.getLogger(ModelliManager.class);

  /** Tipi di gestione del file fisico del modello */
  public static final int     GESTIONE_MODELLO_ELIMINA           = 0;

  public static final int     GESTIONE_MODELLO_COPIA_IN_TMP      = 1;

  public static final int     GESTIONE_MODELLO_ELIMINA_DA_TMP    = 2;

  public static final int     GESTIONE_MODELLO_RIPRISTINA_DA_TMP = 3;

  /**
   * @param modelliDao
   *        modelliDao da settare internamente alla classe.
   */
  public void setModelliDao(ModelliDao modelliDao) {
    this.modelliDao = modelliDao;
  }

  /**
   * @param genChiaviManager
   *        The genChiaviManager to set.
   */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  /**
   * Estrae la lista dei modelli associati al gruppo in analisi
   *
   * @param idGruppo
   *        id del gruppo
   * @param codiceApplicazione
   *        codice applicazione
   * @param codiceProfilo
   *        codice del profilo attivo
   * @return Ritorna la lista dei modelli associati al gruppo in analisi
   */
  public List<?> getModelliDiGruppo(int idGruppo, String codiceApplicazione,
      String codiceProfilo) {
    return this.modelliDao.getModelliDiGruppo(idGruppo, codiceApplicazione,
        codiceProfilo);
  }

  /**
   * Estrazione della lista di tutti i modelli esistenti e popolamento
   * dell'attributo associato per l'indicazione se il modello è associato o meno
   * al gruppo. I modelli sono filtrati per codice profilo (e per codice
   * applicativo anche se non necessario)
   *
   * @param idGruppo
   *        id del gruppo
   * @param codiceApplicazione
   *        codice applicazione
   * @param codiceProfilo
   *        codice del profilo attivo
   * @return
   */
  public List<?> getModelliConAssociazioneGruppo(int idGruppo,
      String codiceApplicazione, String codiceProfilo) {

    // Lista di tutti i modelli filtrati per codice applicazione e codice
    // profilo
    List<?> listaModelli = this.modelliDao.getAllModelliByCodApp(
        codiceApplicazione, codiceProfilo);
    // Lista degli utenti associati al gruppo in analisi filtrati per codice
    // applicazione e codice profilo
    List<?> listaModelliDiGruppo = this.modelliDao.getModelliDiGruppo(idGruppo,
        codiceApplicazione, codiceProfilo);
    // Osservazione: entrambe le liste appena estratte sono ordinate per
    // nome (e non per idRicerca) e la lunghezza della lista delle ricerche di
    // gruppo è minore o uguale della lunghezza della lista delle ricerche.
    ListIterator<?> iterModelliDiGruppo = listaModelliDiGruppo.listIterator();
    ListIterator<?> iterModelli = listaModelli.listIterator();
    int idModelloGruppo = 0;
    ModelloGruppo modello = null;
    ModelloGruppo modelloGruppo = null;

    while (iterModelliDiGruppo.hasNext()) {
      modelloGruppo = (ModelloGruppo) iterModelliDiGruppo.next();
      idModelloGruppo = modelloGruppo.getIdModello();

      boolean test = false;
      while (iterModelli.hasNext() && !test) {
        modello = (ModelloGruppo) iterModelli.next();
        if (idModelloGruppo == modello.getIdModello()) {
          modello.setAssociato(true);
          test = true;
        } else
          modello.setAssociato(false);
      }
    }
    return listaModelli;
  }

  /**
   * Effettua l'update dei record relativi ai modelli associati ad un gruppo
   * nella tabella W_GRPMOD
   *
   * @param idGruppo
   *        id del gruppo
   * @param codiceApplicazione
   *        codice dell'applicazione
   * @param idModelliAssociati
   *        id dei modelli da attribuire al gruppo
   */
  public void updateAssociazioneModelliGruppo(int idGruppo,
      String codiceApplicazione, String codiceProfilo,
      String[] idModelliAssociati) {

    List<Integer> listaModelliAssociati = new ArrayList<Integer>();

    if (idModelliAssociati != null) {
      // popolamento della lista appena create
      for (int i = 0; i < idModelliAssociati.length; i++) {
        listaModelliAssociati.add(new Integer(idModelliAssociati[i]));
      }
      this.modelliDao.deleteModelliNonAssociati(idGruppo, codiceApplicazione,
          listaModelliAssociati);
      if (!listaModelliAssociati.isEmpty()) {
        // select delle ricerche già associate al gruppo in analisi
        Map<?,?> listaModelliPreAssociati = this.modelliDao.getModelliAssociatiAGruppoasMap(
            idGruppo, codiceApplicazione, codiceProfilo);

        Iterator<Integer> iter = listaModelliAssociati.iterator();
        Integer idModello = null;

        // insert dei modelli da associare al gruppo in analisi
        if (!listaModelliPreAssociati.isEmpty()) {
          // Esistono modelli gia' associati al gruppo in analisi, percio'
          // devo controllare che l'idModello dei modelli da inserire
          // (contenute in listaModelliAssociati) siano presenti o meno
          // nella tabella W_GRPMOD
          while (iter.hasNext()) {
            idModello = iter.next();
            if (!listaModelliPreAssociati.containsKey(idModello)) {
              this.modelliDao.insertAssociazioneModelloGruppo(idGruppo,
                  idModello.intValue());
            }
          }
        } else {
          // Non esistono modelli pre associati al gruppo in analisi e
          // posso quindi inserire
          // i modelli presenti nella lista 'listaModelliAssociati'
          // senza alcun controllo
          while (iter.hasNext()) {
            idModello = iter.next();
            this.modelliDao.insertAssociazioneModelloGruppo(idGruppo,
                idModello.intValue());
          }
        }
      }
    } else {
      // Delete di tutti i modelli associati al gruppo in analisi
      this.modelliDao.deleteModelliNonAssociati(idGruppo, codiceApplicazione,
          listaModelliAssociati);
    }
  }

  /**
   * Funzione che esegue la lettura delle lista dei modelli
   *
   * @param trovaModelli
   *        Dati della maschera di trova modelli
   * @return Lista dei modelli
   * @throws SqlComposerException
   * @throws DataAccessException
   */
  public List<?> getModelli(TrovaModelli trovaModelli) throws SqlComposerException {
    // Chiamo la funzione che estrae i modelli con il filtro con trova
    // modelli
    return this.modelliDao.getModelli(trovaModelli);
  }

  /**
   * Estrae la lista di modelli pubblicati per idAccount e codice Applicazione
   *
   * @param idAccount
   *        id dell'account
   * @param codiceApplicazione
   *        codice applicazione
   * @param codiceProfilo
   *        codice del profilo attivo
   * @param riepilogativo
   *        includere i modelli riepilogativi (da lanciare una volta sola su
   *        tutte le occorrenze), oppure no (da lanciare una volta per ogni
   *        occorrenza); queste tipologie di modelli si lanciano solo sui report
   *        e non sulle schede
   * @return
   */
  public List<?> getModelliPredefiniti(int idAccount, String codiceApplicazione,
      String codiceProfilo, boolean riepilogativo) {
    List<?> listaRicerche = this.modelliDao.getModelliPredefiniti(idAccount,
        codiceApplicazione, codiceProfilo, null, riepilogativo);

    return listaRicerche;
  }

  /**
   * Estrae la lista di modelli pubblicati per idAccount e codice Applicazione,
   * filtrando sull'entità associata al modello
   *
   * @param idAccount
   *        id dell'account
   * @param codiceApplicazione
   *        codice applicazione
   * @param codiceProfilo
   *        codice del profilo attivo
   * @param riepilogativo
   *        includere i modelli riepilogativi (da lanciare una volta sola su
   *        tutte le occorrenze), oppure no (da lanciare una volta per ogni
   *        occorrenza); queste tipologie di modelli si lanciano solo sui report
   *        e non sulle schede
   * @param entita
   *        entità principale di partenza, da verificare che sia anche l'entità
   *        principale attribuita al modello
   * @return
   */
  public List<?> getModelliPredefiniti(int idAccount, String codiceApplicazione,
      String codiceProfilo, boolean riepilogativo, String entita) {
    List<?> listaRicerche = this.modelliDao.getModelliPredefiniti(idAccount,
        codiceApplicazione, codiceProfilo, entita, riepilogativo);

    return listaRicerche;
  }

  /**
   * Funzione che restituisce i dati di un modello partendo da un identificativo
   *
   * @param idModello
   *        Identificativo del modello
   * @return dati del modello
   */
  public DatiModello getModelloById(int idModello) {
    return this.modelliDao.getModelloById(idModello);
  }

  /**
   * Funzione che estrae la lista dei gruppi appartenenti al modello
   *
   * @param idModello
   * @return Lista dei gruppi che appartengono al modello
   */
  public List<?> getGruppiModello(int idModello) {
    return this.modelliDao.getGruppiModello(idModello);
  }

  public void updateModello(DatiModello datiModello, byte[] fileData,
      String codiceApplicazione, int idUtente, String contesto) throws GestioneFileModelloException,
      CompositoreException, RemoteException {
    if (fileData != null)
      this.gestisciFileModello(datiModello, fileData, true, codiceApplicazione, idUtente, contesto);
    try {
      this.updateModello(datiModello);
    } catch (DataAccessException e) {
      // si ripristina il modello precedente se si verifica un errore nella
      // gestione dei dati sul DB
      if (fileData != null)
        this.gestisciFileFisicoModello(datiModello.getNomeFile(),
            ModelliManager.GESTIONE_MODELLO_RIPRISTINA_DA_TMP,
            codiceApplicazione);
      throw e;
    } finally {
      if (fileData != null)
        this.gestisciFileFisicoModello(datiModello.getNomeFile(),
            ModelliManager.GESTIONE_MODELLO_ELIMINA_DA_TMP, codiceApplicazione);
    }
  }

  /**
   * Funzione che esgue l'update dei dati di un modello
   *
   * @param datiModello
   */
  protected void updateModello(DatiModello datiModello) {
    this.modelliDao.updateModello(datiModello);
  }

  /**
   * Funzione che esegue l'eliminazione di un modello
   *
   * @param idModello
   *        id del modello da cancellare
   * @param codiceApplicazione
   *        codice dell'applicazione per cui gestire il file
   * @throws GestioneFileModelloException
   */
  public void deleteModello(int idModello, String codiceApplicazione)
      throws GestioneFileModelloException {
    String nomeFile = this.getModelloById(idModello).getNomeFile();

    // eliminazione modello, gruppi/modello e parametri
    this.modelliDao.deleteModello(idModello);

    // eliminazione della cache dei parametri per il modello
    this.modelliDao.deleteCacheParametriComposizioneModello(idModello);

    if (logger.isDebugEnabled())
      logger.debug("Elimino il file del modello: " + idModello);
    // Eseguo l'eliminazione del modello
    if (!this.gestisciFileFisicoModello(nomeFile,
        ModelliManager.GESTIONE_MODELLO_ELIMINA, codiceApplicazione)) {
      throw new GestioneFileModelloException(
          "Errore durante l'eliminazione del file associato al modello !", "");
    }

  }

  /**
   * Funzione che esegue l'eliminazione dei gruppi attribuiti ad un modello
   *
   * @param idModello
   *        id del modello da usare per cancellare i gruppi
   */
  public void deleteGruppiModello(int idModello)
      throws GestioneFileModelloException {
    this.modelliDao.deleteGruppiModello(idModello);
  }

  /**
   * Funzione che estrae l'elenco di tutti i gruppi per la selezione e
   * deselezione dei gruppi appartenenti al modello
   *
   * @param idModello
   * @return Lista con tutti i gruppi e settata la proprietà appartenenti si o
   *         no
   */
  public Object getGruppiModelloPerModifica(int idModello, String codiceProfilo) {
    return this.modelliDao.getGruppiModelloPerModifica(idModello, codiceProfilo);
  }

  /**
   * Funzione che esegue l'update dell'elenco dei gruppi associati al modello
   *
   * @param idModello
   *        identificativo del modello
   * @param idGruppi
   *        Elenco dei gruppi che devono essere associati
   */
  public void updateGruppiModello(int idModello, Integer[] idGruppi,
      String profiloOwner) {
    this.modelliDao.updateGruppiModello(idModello, idGruppi, profiloOwner);
  }

  /**
   * Funzione che esegue l'update dell'elenco dei gruppi associati al modello ed
   * aggiorna il flag personale a 0
   *
   * @param idModello
   *        identificativo del modello
   * @param idGruppi
   *        Elenco dei gruppi che devono essere associati
   */
  public void updateGruppiEPubblicaModello(int idModello, Integer[] idGruppi,
      String profiloOwner) {
    this.modelliDao.updateGruppiModello(idModello, idGruppi, profiloOwner);
    this.modelliDao.updatePersonale(idModello, 0);

  }

  /**
   * Funzione che esegue l'inserimento di un nuovo modello e l'upload con
   * compilazione del modello per una data applicazione
   *
   * @param datiModello
   *        Dati del modello da inserire
   * @param nomeFileSelezionato
   *        nome del file del modello
   * @param fileData
   *        stream di byte collegato al file da caricare
   * @param codiceApplicazione
   *        codice dell'applicazione
   * @param contesto
   *        id del contesto oracle (opzionale); se non si utilizzano i contesti
   *        valorizzare con null
   */
  public void insertModello(DatiModello datiModello, byte[] fileData,
      String codiceApplicazione, String contesto) throws GestioneFileModelloException,
      CompositoreException, RemoteException {
    this.gestisciFileModello(datiModello, fileData, false, codiceApplicazione, datiModello.getOwner().intValue(), contesto);
    try {
      this.insertModello(datiModello);
    } catch (DataAccessException e) {
      this.gestisciFileFisicoModello(datiModello.getNomeFile(),
          ModelliManager.GESTIONE_MODELLO_ELIMINA, codiceApplicazione);
      throw e;
    }
  }

  /**
   * Funzione che esegue l'inserimento di un nuovo modello, attribuendolo ad un
   * gruppo, e l'upload con compilazione del modello per una data applicazione
   *
   * @param datiModello
   *        Dati del modello da inserire
   * @param idGruppo
   *        identificativo del gruppo a cui attribuire il modello
   * @param fileData
   *        stream di byte collegato al file da caricare
   * @param codiceApplicazione
   *        codice dell'applicazione
   * @param idUtente
   *        id dell'utente
   * @param contesto
   *        id del contesto oracle (opzionale); se non si utilizzano i contesti
   *        valorizzare con null
   */
  public void insertModello(DatiModello datiModello, int idGruppo,
      byte[] fileData, String codiceApplicazione, String contesto)
      throws GestioneFileModelloException, CompositoreException,
      RemoteException {
    this.gestisciFileModello(datiModello, fileData, false, codiceApplicazione, datiModello.getOwner().intValue(), contesto);
    try {
      this.insertModello(datiModello);
      this.updateGruppiModello(datiModello.getIdModello(),
          new Integer[] { new Integer(idGruppo) },
          datiModello.getProfiloOwner());
    } catch (DataAccessException e) {
      this.gestisciFileFisicoModello(datiModello.getNomeFile(),
          ModelliManager.GESTIONE_MODELLO_ELIMINA, codiceApplicazione);
      throw e;
    }
  }

  /**
   * Funzione che esegue l'inserimento di un nuovo modello
   *
   * @param datiModello
   *        Dati del modello da inserire
   */
  private void insertModello(DatiModello datiModello) {
    // Estraggo il nuovo identificativo del modello
    int idModello = this.genChiaviManager.getNextId("W_MODELLI");
    datiModello.setIdModello(idModello);
    this.modelliDao.insertModello(datiModello);
  }

  /**
   * Esegue la compilazione di un modello. Tutti i dati aggiuntivi vengono
   * prelevati dal global.properties
   *
   * @param nomeModello
   *        Nome del modello da compilare
   * @param codiceApplicazione
   *        codice dell'applicazione
   * @param idUtente
   *        id dell'utente
   * @param contesto
   *        id del contesto oracle (opzionale); se non si utilizzano i contesti
   *        valorizzare con null
   * @param usaDB
   *        true se si deve sfruttare la connessione al db, false se vengono
   *        passati i dati in formato xml in modo da non far connettere il
   *        compositore al db
   *
   * @throws RemoteException
   */
  private void compilaModello(String nomeModello, String codiceApplicazione, int idUtente, String contesto, boolean usaDB)
      throws RemoteException, CompositoreException {

    if (logger.isDebugEnabled()) logger.debug("compilaModello: inizio metodo");

    String idApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_ID_APPLICAZIONE);

    String registri = "86=" + idUtente + ";";
    if (contesto != null) registri += "81=" + contesto + ";";

    ServizioCompositoreProxy servizio = new ServizioCompositoreProxy();
    servizio.setEndpoint(ConfigManager.getValore(CostantiGenModelli.PROP_URL_WEB_SERVICE));
    if (usaDB)
      servizio.compilaModello(nomeModello, idApplicazione, codiceApplicazione, registri);
    else
      servizio.compilaModelloSenzaConnessioneDB(nomeModello, idApplicazione, codiceApplicazione, registri);

    if (logger.isDebugEnabled()) logger.debug("compilaModello: fine metodo");
  }

  /**
   * Funzione che esegue la composizione di un modello per una data applicazione
   *
   * @param idModello
   * @param entita
   * @param elencoChiavi
   * @param valoriChiavi
   * @param codiceApplicazione
   * @param idUtente
   *        id dell'utente
   * @param idSessione
   *        id di sessione a cui sono collegati i parametri (opzionali); se non
   *        esistono parametri valorizzare con 0
   * @param contesto
   *        id del contesto oracle (opzionale); se non si utilizzano i contesti
   *        valorizzare con null
   * @return Ritorna il path completo del file compilato
   * @throws RemoteException
   */
  public String componiModello(int idModello, String entita,
      String elencoChiavi, String[] valoriChiavi, String codiceApplicazione,
      int idUtente, String contesto, int idSessione) throws RemoteException, CompositoreException {

    if (logger.isDebugEnabled())
      logger.debug("componiModello: inizio metodo");
    String fileComposto;
    // Estraggo i dati del modello
    DatiModello datiModello = this.getModelloById(idModello);

    String idApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_ID_APPLICAZIONE);

    String registri = "86=" + idUtente + ";";
    if (idSessione != 0) registri += "80=" + idSessione + ";";
    if (contesto != null) registri += "81=" + contesto + ";";

    ServizioCompositoreProxy servizio = new ServizioCompositoreProxy();
    servizio.setEndpoint(ConfigManager.getValore(CostantiGenModelli.PROP_URL_WEB_SERVICE));
    fileComposto = servizio.componi(datiModello.getNomeFile(), entita,
        elencoChiavi, valoriChiavi, idApplicazione, codiceApplicazione,
        registri);

    if (logger.isDebugEnabled())
      logger.debug("componiModello: fine metodo");

    return fileComposto;
  }

  /**
   * Funzione che esegue la composizione di un modello per una data applicazione
   *
   * @param idModello
   * @param nomeFileSorgenteDati
   * @param codiceApplicazione
   * @param idUtente
   *        id dell'utente
   * @param contesto
   *        id del contesto oracle (opzionale); se non si utilizzano i contesti
   *        valorizzare con null
   * @return Ritorna il path completo del file compilato
   * @throws RemoteException
   */
  public String componiModelloSenzaConnessioneDB(int idModello, String nomeFileSorgenteDati,
      String codiceApplicazione,
      int idUtente, String contesto) throws RemoteException, CompositoreException {

    if (logger.isDebugEnabled())
      logger.debug("componiModelloSenzaConnessioneDB: inizio metodo");
    String fileComposto;
    // Estraggo i dati del modello
    DatiModello datiModello = this.getModelloById(idModello);

    String idApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_ID_APPLICAZIONE);

    String registri = "86=" + idUtente + ";";
    if (contesto != null) registri += "81=" + contesto + ";";

    ServizioCompositoreProxy servizio = new ServizioCompositoreProxy();
    servizio.setEndpoint(ConfigManager.getValore(CostantiGenModelli.PROP_URL_WEB_SERVICE));
    fileComposto = servizio.componiModelloSenzaConnessioneDB(
        datiModello.getNomeFile(), nomeFileSorgenteDati, idApplicazione,
        codiceApplicazione, registri);

    if (logger.isDebugEnabled())
      logger.debug("componiModelloSenzaConnessioneDB: fine metodo");

    return fileComposto;
  }

  /**
   * Funzione che elimina il file composto dalla directory di output per una
   * determinata applicazione
   *
   * @param fileComposto
   * @param codiceApplicazione
   * @throws RemoteException
   */
  public void eliminaFileComposto(String fileComposto, String codiceApplicazione)
      throws RemoteException, CompositoreException {
    String idApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_ID_APPLICAZIONE);

    ServizioCompositoreProxy servizio = new ServizioCompositoreProxy();
    servizio.setEndpoint(ConfigManager.getValore(CostantiGenModelli.PROP_URL_WEB_SERVICE));
    servizio.eliminaFileComposto(fileComposto, idApplicazione,
        codiceApplicazione);
  }

  /**
   * funzione che esegue la gestione del file fisico del modello
   *
   * @param nomeFile
   *        Nome del file modello
   * @param modo
   *        modo di gestione
   * @param codiceApplicazione
   *        codice dell'applicazione per cui gestire il file
   * @return
   */
  public boolean gestisciFileFisicoModello(String nomeFile, int modo,
      String codiceApplicazione) {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 05/09/2006 M.F. Prima Versione
    // 11/09/2006 M.F. Eliminazione degli inf e idx
    // ************************************************************

    if (logger.isDebugEnabled())
      logger.debug("gestisciFileFisicoModello: inizio metodo");

    // Viene fatta la gestione solo se il nome del file e valido
    if (nomeFile != null && !nomeFile.equals("")) {
      // Estraggo il nome del file senza estensione
      String nomeFileSenzaEstensione;
      String pathFile = null;
      String pathDest = null;
      boolean lbDelete = true;

      if (nomeFile.lastIndexOf(SEPARATORE_PROP) >= 0)
        nomeFileSenzaEstensione = nomeFile.substring(0,
            nomeFile.lastIndexOf(SEPARATORE_PROP));
      else
        nomeFileSenzaEstensione = nomeFile;
      switch (modo) {
      case ModelliManager.GESTIONE_MODELLO_ELIMINA:
        if (logger.isDebugEnabled())
          logger.debug("Eliminazione dei file del modello: " + nomeFile);
        pathFile = ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI);
        break;
      case ModelliManager.GESTIONE_MODELLO_COPIA_IN_TMP:
        if (logger.isDebugEnabled())
          logger.debug("Copia del modello nell'area temporanea: " + nomeFile);
        lbDelete = false;
        pathFile = ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI);
        pathDest = ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI)
            + ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI_TMP);
        break;
      case ModelliManager.GESTIONE_MODELLO_RIPRISTINA_DA_TMP:
        if (logger.isDebugEnabled())
          logger.debug("Ripristino del modello dall'area temporanea: "
              + nomeFile);
        lbDelete = false;
        pathDest = ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI);
        pathFile = ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI)
            + ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI_TMP);
        break;
      case ModelliManager.GESTIONE_MODELLO_ELIMINA_DA_TMP:
        if (logger.isDebugEnabled())
          logger.debug("Elimino il modello dall'area temporanea: " + nomeFile);
        pathFile = ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI)
            + ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI_TMP);
        break;
      default:
        // Se nel parametro non è riconosciuto allora non fa nulla
        return false;
      }
      // Se devo eseguo la copia del modello nel path di destinazione
      if (pathDest != null) {
        this.fileCopy(pathFile, pathDest, nomeFile);
        this.fileCopy(pathFile, pathDest, nomeFileSenzaEstensione + ".idx");
        this.fileCopy(pathFile, pathDest, nomeFileSenzaEstensione + ".inf");
        this.fileCopy(pathFile, pathDest, nomeFileSenzaEstensione + ".err");
        this.fileCopy(pathFile, pathDest, nomeFileSenzaEstensione + ".tot");
        this.fileCopy(pathFile, pathDest, nomeFileSenzaEstensione + ".ok");
      }
      // Se devo eseguo l'eliminazione del file
      if (lbDelete) {
        if (!this.eliminaFile(pathFile, nomeFile)) return false;
        this.eliminaFile(pathFile, nomeFileSenzaEstensione + ".idx");
        this.eliminaFile(pathFile, nomeFileSenzaEstensione + ".inf");
        this.eliminaFile(pathFile, nomeFileSenzaEstensione + ".err");
        this.eliminaFile(pathFile, nomeFileSenzaEstensione + ".tot");
        this.eliminaFile(pathFile, nomeFileSenzaEstensione + ".ok");
      }

    }
    if (logger.isDebugEnabled())
      logger.debug("gestisciFileFisicoModello: fine metodo");
    return true;
  }

  /**
   * Funzione che esegue l'eliminazione di un file
   *
   * @param pathFile
   *        Path del file da eliminare
   * @param nomeFile
   *        Nome del file da eliminare
   * @return true OK; false errore in eliminazione
   */
  private boolean eliminaFile(String pathFile, String nomeFile) {
    File fileDel = new File(pathFile + nomeFile);
    if (fileDel.exists()) {
      if (!fileDel.delete()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Funzione che esegue la copia di un file da un sorgente alla destinazione
   *
   * @param pathFile
   *        Path sorgente
   * @param pathDest
   *        Path destinazione
   * @param nomeFile
   *        Nome del file da copiare
   * @return true se OK, false se c'è stato un errore
   */
  private boolean fileCopy(String pathFile, String pathDest, String nomeFile) {
    boolean esitoCopia = true;
    File inputFile = new File(pathFile + nomeFile);
    if (inputFile.exists()) {
      File outputFile = new File(pathDest + nomeFile);
      try {
        FileUtils.copyFile(inputFile, outputFile);
      } catch (Throwable t) {
        logger.error("Errore copiando il file "
            + nomeFile
            + ": da "
            + pathFile
            + " a "
            + pathDest
            + "\n"
            + t.getMessage(), t);
        esitoCopia = false;
      }
    }
    return esitoCopia;
  }

  /**
   * Funzione che gestisce l'upload e l'aggiornamento del file di un modello
   *
   * @return true se l'operazione e' avvenuta correttamente; false si è
   *         verificato un errore durante la copia del file
   */
  protected void gestisciFileModello(DatiModello datiModello, byte[] fileData,
      boolean mantieniCopiaInTmp, String codiceApplicazione, int idUtente, String contesto)
      throws GestioneFileModelloException, CompositoreException,
      RemoteException {
    if (logger.isDebugEnabled())
      logger.debug("gestisciFileModello: inizio metodo");

    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 05/09/2006 M.F. Prima Versione
    // 12/09/2006 M.F. Verifico che l'estensione del file sia fra quelle
    // gestite dal compositore
    // ************************************************************

    if (fileData == null || fileData.length == 0)
      throw new GestioneFileModelloException(null,
          GestioneFileModelloException.ERROR_FILE_VUOTO);

    boolean lbCopiatoInTmp = false;

    String nomeFile = datiModello.getNomeFile();
    String nomeFileFisico = nomeFile;

    try {
      // Determino il path dei modelli
      String pathModelli = ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI);

      // {MF120906} Verifico l'estensione del file
      if (!this.isValidExtension(nomeFileFisico)) {
        throw new GestioneFileModelloException("Il tipo di file: "
            + nomeFileFisico
            + " non è gestito dal compositore",
            GestioneFileModelloException.ERROR_FILE_ESTENSIONE_NON_GESTITA);
      }

      File f = null;

      if (!mantieniCopiaInTmp) {
        if (logger.isDebugEnabled())
          logger.debug("Gestione file upload: File: ".concat(nomeFile).concat(
              "\nDirectory di output: ").concat(pathModelli));

        f = new File(pathModelli.concat(nomeFileFisico));
        if (f.exists()) {
          // Se il file gia esiste, si aggiunge un progressivo al nome del file,
          // in modo da fare l'upload di un file non ancora presente nella
          // cartella dei modelli
          int progressivo = 1;
          boolean nomeFileUnivoco = false;
          String nuovoNomeFile = null;

          while (!nomeFileUnivoco) {
            nuovoNomeFile = nomeFileFisico.substring(0,
                nomeFileFisico.indexOf("."))
                + progressivo
                + "."
                + nomeFileFisico.substring(nomeFileFisico.indexOf(".") + 1);
            f = new File(pathModelli.concat(nuovoNomeFile));
            if (!f.exists()) {
              nomeFileUnivoco = true;
              nomeFileFisico = nuovoNomeFile;
              datiModello.setNomeFile(nuovoNomeFile);
            } else {
              progressivo++;
            }
          }
        }
      } else {
        nomeFile = datiModello.getNomeFile();

        if (logger.isDebugEnabled())
          logger.debug("Gestione file upload: File: ".concat(nomeFile).concat(
              "\nDirectory di output: ").concat(pathModelli));

        if (nomeFile != null && nomeFile.length() > 0) {
          f = new File(pathModelli.concat(nomeFileFisico));
          // Se si è in caso di update del modello e del file ad esso associato,
          // allora eseguo la copia nella cartella tmp dei modelli dei file
          // originali, per poterli ripristinare in caso di errore
          if (!this.gestisciFileFisicoModello(nomeFile,
              ModelliManager.GESTIONE_MODELLO_COPIA_IN_TMP, codiceApplicazione))
            // Copia nella directory temporanea non avvenuta correttamente
            throw new GestioneFileModelloException(
                "Errore in copia del file nei temporanei: " + nomeFile, "");
          else
            // Setto il flag che dice che è stata fatta una copia nei temporanei
            lbCopiatoInTmp = true;
          if (!this.gestisciFileFisicoModello(nomeFile,
              ModelliManager.GESTIONE_MODELLO_ELIMINA, codiceApplicazione))
          // Eliminazione non avvenuta correttamente lancio
            // l'eccezione
            throw new GestioneFileModelloException(
                "Errore in eliminazione del File: " + nomeFile, "");
        }
      }

      if (f != null) {
        FileOutputStream output = new FileOutputStream(f);
        output.write(fileData);
        output.close();
        if (logger.isDebugEnabled())
          logger.debug("Setto il nome del file da: ".concat(nomeFile).concat(
              " a ").concat(nomeFileFisico));
        // A questo punto eseguo la compilazione del file
        try {
            this.compilaModello(nomeFileFisico, codiceApplicazione, idUtente, contesto, datiModello.getIdRicercaSrc() == null);
        } catch (Throwable t) {
          // Se c'è stato un'errore allora elimino il file
          this.gestisciFileFisicoModello(nomeFileFisico,
              ModelliManager.GESTIONE_MODELLO_ELIMINA, codiceApplicazione);
          throw t;
        }
      }
      if (!mantieniCopiaInTmp && lbCopiatoInTmp)
        this.gestisciFileFisicoModello(nomeFile,
            ModelliManager.GESTIONE_MODELLO_ELIMINA_DA_TMP, codiceApplicazione);
    } catch (CompositoreException e) {
      if (lbCopiatoInTmp) {
        this.gestisciFileFisicoModello(nomeFile,
            ModelliManager.GESTIONE_MODELLO_RIPRISTINA_DA_TMP,
            codiceApplicazione);
        this.gestisciFileFisicoModello(nomeFile,
            ModelliManager.GESTIONE_MODELLO_ELIMINA_DA_TMP, codiceApplicazione);
      }
      throw e;
    } catch (GestioneFileModelloException e) {
      if (lbCopiatoInTmp) {
        this.gestisciFileFisicoModello(nomeFile,
            ModelliManager.GESTIONE_MODELLO_RIPRISTINA_DA_TMP,
            codiceApplicazione);
        this.gestisciFileFisicoModello(nomeFile,
            ModelliManager.GESTIONE_MODELLO_ELIMINA_DA_TMP, codiceApplicazione);
      }
      throw e;
    } catch (RemoteException e) {
      if (lbCopiatoInTmp) {
        this.gestisciFileFisicoModello(nomeFile,
            ModelliManager.GESTIONE_MODELLO_RIPRISTINA_DA_TMP,
            codiceApplicazione);
        this.gestisciFileFisicoModello(nomeFile,
            ModelliManager.GESTIONE_MODELLO_ELIMINA_DA_TMP, codiceApplicazione);
      }
      throw e;
    } catch (Throwable t) {
      if (logger.isDebugEnabled())
        logger.debug("gestisciFileModello: Eccezione:"
            + t.toString()
            + ":"
            + t.getMessage());
      if (lbCopiatoInTmp) {
        this.gestisciFileFisicoModello(nomeFile,
            ModelliManager.GESTIONE_MODELLO_RIPRISTINA_DA_TMP,
            codiceApplicazione);
        this.gestisciFileFisicoModello(nomeFile,
            ModelliManager.GESTIONE_MODELLO_ELIMINA_DA_TMP, codiceApplicazione);
      }
      throw new GestioneFileModelloException(t.getMessage(), "");
    }

    if (logger.isDebugEnabled())
      logger.debug("gestisciFileModello: fine metodo");
  }

  /**
   * Funzione che verifica che l'estensione del modello sia gestita dal
   * compositore
   *
   * @param fileName
   *        nome del file modello
   * @return
   */
  private boolean isValidExtension(String fileName) {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 12/09/2006 M.F. Prima Versione
    // ************************************************************

    String estensioni = ".rtf;.txt;.odt;";
    if (fileName.indexOf(SEPARATORE_PROP) >= 0) {
      String estensione = fileName.substring(
          fileName.lastIndexOf(SEPARATORE_PROP)).toLowerCase()
          + ";";
      if (estensioni.indexOf(estensione) >= 0) return true;

    }
    return false;
  }

  /**
   * Funzione che esegue il download di un file composto
   *
   * @param asPath
   *        Nome del file composto
   * @param codiceApplicazione
   *        codice dell'applicazione per cui è definito il file
   * @param response
   *        dove dare il messaggio
   * @throws GestioneFileModelloException
   */
  public void downloadFileComposto(String asPath, String codiceApplicazione,
      HttpServletResponse response) throws GestioneFileModelloException {
    this.downloadFileByPath(
        ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI)
            + ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI_OUTPUT)
            + asPath, response);
  }

  /**
   * Funzione che permette il download del file da parte dell'utente
   *
   * @param pathFile
   *        Path completo di dove si trova il file
   * @param response
   *        Response
   * @throws GestioneFileModelloException
   */
  private void downloadFileByPath(String pathFile, HttpServletResponse response)
      throws GestioneFileModelloException {
    String nomeFile;
    nomeFile = pathFile;
    if (nomeFile != null && nomeFile.indexOf("/") >= 0) {
      nomeFile = nomeFile.substring(nomeFile.lastIndexOf("/") + 1);
    }
    File f = new File(pathFile);
    if (!f.exists()) {
      throw new GestioneFileModelloException("File per il download "
          + pathFile
          + " non esiste", GestioneFileModelloException.ERROR_FILE_INESISTENTE);
    }
    response.setContentType("application/octet-stream");
    response.setHeader("Content-Disposition", "attachment;filename=\""
        + nomeFile
        + "\"");
    try {
      FileInputStream stream = new FileInputStream(f);

      OutputStream out = response.getOutputStream();
      response.setContentLength((int) f.length());

      byte[] buffer = new byte[2048];
      int bytesRead = stream.read(buffer);
      while (bytesRead >= 0) {
        if (bytesRead > 0) out.write(buffer, 0, bytesRead);
        bytesRead = stream.read(buffer);
      }
      stream.close();
      out.flush();
      out.close();
    } catch (Throwable t) {
      throw new GestioneFileModelloException(t.getMessage(), "");
    }
  }

  /**
   * Funzione che esegue il download del file
   *
   * @param nomeFile
   *        nome del file da scaricare
   * @param codiceApplicazione
   *        codice dell'applicazione per cui è definito il file
   * @param response
   *        Risposta data
   * @throws GestioneFileModelloException
   */
  public void downloadFile(String nomeFile, String codiceApplicazione,
      HttpServletResponse response) throws GestioneFileModelloException {

    try {
      // Estraggo il path dei modelli
      String pathModelli = ConfigManager.getValore(CostantiGenModelli.PROP_PATH_MODELLI);
      if (logger.isDebugEnabled())
        logger.debug("Download file: " + pathModelli + nomeFile);
      // Eseguo l'upload del file
      this.downloadFileByPath(pathModelli + nomeFile, response);
    } catch (GestioneFileModelloException e) {
      throw e;
    } catch (Throwable t) {
      throw new GestioneFileModelloException(t.getMessage(), "");
    }
  }

  /**
   * Lettura dell'ID_MODELLO a partire dal nome del modello (il nome e' un campo
   * univoco) e il codice applicazione
   *
   * @param nomeFile
   * @param codiceApplicazione
   * @return
   */
  public int getIdModelloByNomeFileCodApp(String nomeFile,
      String codiceApplicazione) {
    return this.modelliDao.getIdModelloByNomeFileCodApp(nomeFile,
        codiceApplicazione);
  }

  /**
   * Funzione che estrae la lista dei parametri definiti per il modello
   *
   * @param idModello
   *        identificativo univoco del modello
   * @return Lista dei parametri definiti per il modello
   */
  public List<?> getParametriModello(int idModello) {
    return this.modelliDao.getParametriModello(idModello);
  }

  /**
   * Funzione che estrae il nuovo progressivo da attribuire ad un parametro di
   * un modello
   *
   * @param idModello
   *        identificativo univoco del modello
   * @return nuovo valore di progressivo da utilizzare
   */
  public int getNuovoProgressivoParametroModello(int idModello) {
    return this.modelliDao.getNuovoProgressivoParametroModello(idModello);
  }

  /**
   * Funzione che estrae il parametro definiti a partire dai dati in input
   *
   * @param idModello
   *        identificativo univoco del modello
   * @param progressivo
   *        progressivo univoco del parametro
   * @return Parametro la cui chiave è individuata dai parametri in input
   */
  public ParametroModello getParametroModello(int idModello, int progressivo) {
    return this.modelliDao.getParametroModello(idModello, progressivo);
  }

  /**
   * Inserisce un nuovo parametro per un determinato modello, attribuendo il
   * primo progressivo disponibile
   *
   * @param parametro
   *        parametro da inserire
   */
  public void insertParametro(ParametroModello parametro) {
    int progressivo = this.modelliDao.getNuovoProgressivoParametroModello(parametro.getIdModello());
    parametro.setProgressivo(progressivo);
    this.modelliDao.insertParametro(parametro);
  }

  /**
   * Aggiorna i dati di un parametro
   *
   * @param parametro
   *        parametro da aggiornare
   */
  public void updateParametro(ParametroModello parametro) {
    this.modelliDao.updateParametro(parametro);
  }

  /**
   * Elimina il parametro di un modello ed aggiorna il progressivo per tutti i
   * parametri successivi a quello eliminato
   *
   * @param idModello
   *        identificativo univoco del modello
   * @param progressivo
   *        progressivo univoco del parametro per il modello
   */
  public void deleteParametro(int idModello, int progressivo) {
    this.modelliDao.deleteParametro(idModello, progressivo);
    this.modelliDao.updateDecrementaProgressivoParametri(idModello, progressivo);
  }

  /**
   * Sposta il parametro individuato in input su di una posizione nell'elenco,
   * se esistono posizioni precedenti
   *
   * @param idModello
   *        identificativo univoco del modello
   * @param progressivo
   *        progressivo univoco del parametro per il modello
   */
  public void updateSpostaSuParametro(int idModello, int progressivo) {
    if (progressivo > 0) {
      // sposto l'elemento nella posizione temporanea -1
      this.modelliDao.updateProgressivoParametro(idModello, progressivo, -1);
      // sposto quindi l'elemento precedente nella posizione del parametro da
      // spostare
      this.modelliDao.updateProgressivoParametro(idModello, (progressivo - 1),
          progressivo);
      // sposto l'elemento da spostare, messo in posizione provvisoria -1, nella
      // posizione definitiva
      this.modelliDao.updateProgressivoParametro(idModello, -1,
          (progressivo - 1));
    }
  }

  /**
   * Sposta il parametro individuato in input giù di una posizione nell'elenco,
   * se esistono posizioni successive
   *
   * @param idModello
   *        identificativo univoco del modello
   * @param progressivo
   *        progressivo univoco del parametro per il modello
   */
  public void updateSpostaGiuParametro(int idModello, int progressivo) {
    // sposto l'elemento nella posizione temporanea -1
    this.modelliDao.updateProgressivoParametro(idModello, progressivo, -1);
    // sposto quindi l'elemento successivo nella posizione del parametro da
    // spostare
    boolean aggiornatoSuccessivo = this.modelliDao.updateProgressivoParametro(
        idModello, (progressivo + 1), progressivo);
    if (aggiornatoSuccessivo)
      // sposto l'elemento da spostare, messo in posizione provvisoria -1, nella
      // posizione definitiva
      this.modelliDao.updateProgressivoParametro(idModello, -1,
          (progressivo + 1));
    else
      // ripristino l'elemento nella posizione iniziale, in quanto non esistono
      // elementi successivi
      this.modelliDao.updateProgressivoParametro(idModello, -1, progressivo);
  }

  /**
   * Sposta il parametro individuato in input dal progressivo nella posizione
   * indicata nell'ultimo parametro, e corregge i progressivi di tutti i
   * parametri che risultano essere spostati di conseguenza
   *
   * @param idModello
   *        identificativo univoco del modello
   * @param progressivo
   *        progressivo univoco del parametro da spostare
   * @param progressivoNew
   *        nuova posizione del parametro
   */
  public void updateSpostaPosizioneMarcataParametro(int idModello,
      int progressivo, int progressivoNew) {
    // sposto l'elemento in una posizione temporanea
    this.modelliDao.updateProgressivoParametro(idModello, progressivo, -1);
    if (progressivo < progressivoNew) {
      // l'elemento va spostato verso giù, quindi sposto indietro di una
      // posizione tutti i parametri successivi a quello spostato fino alla
      // nuova posizione
      this.modelliDao.updateDecrementaProgressivoParametri(idModello,
          (progressivo + 1), progressivoNew);
    } else {
      // l'elemento va spostato verso su, quindi sposto avanti di una
      // posizione tutti i parametri dalla nuova posizione fino a quello
      // spostato
      this.modelliDao.updateIncrementaProgressivoParametri(idModello,
          progressivoNew, (progressivo - 1));
    }
    // sposto l'elemento nella sua posizione definitiva
    this.modelliDao.updateProgressivoParametro(idModello, -1, progressivoNew);
  }

  /**
   * Inserisce un elenco di parametri nella tabella W_COMPARAM per poter
   * consentire la composizione di un modello a partire dai parametri stessi
   *
   * @param parametri
   *        elenco dei parametri da inserire
   * @return identificativo della sessione definita e contenente l'elenco dei
   *         parametri associati alla composizione
   */
  public int insertParametriComposizione(ParametroComposizione[] parametri) {
    int id = this.genChiaviManager.getNextId("W_COMPARAM");
    for (int i = 0; i < parametri.length; i++) {
      parametri[i].setIdSessione(id);
      this.modelliDao.insertParametroComposizione(parametri[i]);

    }
    return id;
  }

  /**
   * Inserisce un elenco di parametri nella tabella W_COMPARAM per poter
   * consentire la composizione di un modello a partire dai parametri stessi, ed
   * inoltre inserisce anche i valori dei parametri del modello (esclusi i
   * parametri introdotti per i modelli riepilogativi) in una cache per
   * agevolare l'inputazione da parte dell'utente all'atto della composizione
   *
   * @param parametri
   *        elenco dei parametri da inserire
   * @param idAccount
   *        identificativo univoco dell'account
   * @param idModello
   *        identificativo univoco del modello
   *
   * @return identificativo della sessione definita e contenente l'elenco dei
   *         parametri associati alla composizione
   */
  public int insertParametriComposizione(ParametroComposizione[] parametri,
      int idAccount, int idModello) {
    this.modelliDao.deleteCacheParametriComposizione(idAccount, idModello);
    int id = this.genChiaviManager.getNextId("W_COMPARAM");
    for (int i = 0; i < parametri.length; i++) {
      parametri[i].setIdSessione(id);
      this.modelliDao.insertParametroComposizione(parametri[i]);
      // se non è un parametro di un riepilogativo, allora lo inserisco in cache
      if (!parametri[i].getCodice().equals(
          CostantiGenModelli.PARAMETRO_RIEPILOGATIVO_NUM_CHIAVI)
          && !parametri[i].getCodice().startsWith(
              CostantiGenModelli.PARAMETRO_RIEPILOGATIVO_PREFISSO_CHIAVE))
        this.modelliDao.insertCacheParametroComposizione(new CacheParametroComposizione(
            parametri[i], idAccount, idModello));
    }
    return id;
  }

  /**
   * Elimina tutti i parametri definiti su una composizione
   *
   * @param idSessione
   *        identificativo univoco dal quale partire per eliminare i parametri
   *        associati alla composizione del modello
   */
  public void deleteParametriComposizione(int idSessione) {
    this.modelliDao.deleteParametriComposizione(idSessione);
  }

  /**
   * Effettua l'insert nella tabella W_GRPRIC dei gruppi associati alla ricerca
   * con modello, eliminando prima tutte le associazioni esistenti
   *
   * @param idGruppo
   * @param idRicerca
   */
  public void insertGruppoModello(int idModello, String[] arrayIdGruppo) {
    List<Integer> listaIdModello = new ArrayList<Integer>();
    listaIdModello.add(new Integer(idModello));

    // Inserimento delle nuove associazioni tra i gruppi e la ricerca con
    // prospetto in analisi
    if (arrayIdGruppo != null) {
      for (int i = 0; i < arrayIdGruppo.length; i++) {
        // Cancellazione di tutte le associazioni esistenti tra i gruppi e
        // la ricerca con modello in analisi
        this.modelliDao.deleteGruppiModello(UtilityNumeri.convertiIntero(
            arrayIdGruppo[i]).intValue());
        this.modelliDao.insertAssociazioneModelloGruppo(
            UtilityNumeri.convertiIntero(arrayIdGruppo[i]).intValue(),
            idModello);
      }

    }
  }

  /**
   * Metodo per effettuare l'import di un report con modello in un'unica
   * transazione
   *
   * @param contenitoreDatiModello
   * @param codApp
   * @param idUtenteOwner
   * @param contenutoFileProspetto
   * @param contesto
   *        id del contesto oracle (opzionale); se non si utilizzano i contesti
   *        valorizzare con null
   * @throws InvalidDataAccessApiUsageException
   * @throws DataAccessException
   */
  public void importModello(ContenitoreDatiModello contenitoreDatiModello,
      String codApp, int idUtenteOwner, byte[] contenutoFileProspetto, String contesto) {

    boolean isModelloInserito = false;
    // DatiGenRicerca datiGenerali =
    // contenitoreDatiProspetto.getDatiGenProspetto().getDatiGenRicerca();
    DatiModello datiModello = contenitoreDatiModello.getDatiGenModello();
    // Aggiornamento del codApp della ricerca da importare
    // datiGenerali.setCodApp(codApp);
    datiModello.setCodiceApplicativo(codApp);
    // Aggiornamento dell'owner del report e del modello associato
    // datiGenerali.setOwner(new Integer(idUtenteOwner));
    datiModello.setOwner(new Integer(idUtenteOwner));
    try {
      // si inserisce il modello associato al prospetto in W_MODELLI
      this.insertModello(datiModello, contenutoFileProspetto, codApp, contesto);
      isModelloInserito = true;

      // Determino l'id_modello del modello appena inserito
      Integer idModello = new Integer(datiModello.getIdModello());

      if (contenitoreDatiModello.getElencoGruppi().size() > 0) {
        String[] arrayIdGruppiAssociati = new String[contenitoreDatiModello.getElencoGruppi().size()];
        for (int i = 0; i < arrayIdGruppiAssociati.length; i++)
          arrayIdGruppiAssociati[i] = Integer.toString((contenitoreDatiModello.getElencoGruppi().elementAt(
              i)).getIdGruppo());
        // Insert dell'associazione gruppi-report in W_GRPRIC
        this.insertGruppoModello(idModello.intValue(), arrayIdGruppiAssociati);
      }

      // Insert dei parametri del prospetto
      for (int i = 0; i < contenitoreDatiModello.getElencoParametri().size(); i++) {
        ParametroModello parametro = contenitoreDatiModello.getElencoParametri().get(
            i);
        parametro.setIdModello(idModello.intValue());
        this.insertParametro(parametro);
      }
    } catch (DataAccessException da) {
      if (isModelloInserito)
        this.gestisciFileFisicoModello(datiModello.getNomeFile(),
            ModelliManager.GESTIONE_MODELLO_ELIMINA, codApp);
      throw da;
    } catch (Throwable t) {
      if (isModelloInserito)
        this.gestisciFileFisicoModello(datiModello.getNomeFile(),
            ModelliManager.GESTIONE_MODELLO_ELIMINA, codApp);
      throw new InvalidDataAccessApiUsageException(null, t);
    }
  }

  /**
   * Metodo per eseguire la sovrascrittura di un report con modello esistente
   * in un unica transazione
   *
   * @param datiGenProspettoEsistente
   * @param contenitoreDatiProspetto
   * @param codApp
   * @param idUtenteOwner
   * @param contenutoFileProspetto
   * @param contesto
   * @throws InvalidDataAccessApiUsageException
   * @throws DataAccessException
   */
  public void importModelloEsistente(
      ContenitoreDatiModello contenitoreDatiModello, String codApp,
      int idUtenteOwner, byte[] contenutoFileProspetto, String contesto) {

    // faccio update del modello
    int idModello = contenitoreDatiModello.getDatiGenModello().getIdModello();
    try {
      this.updateModello(contenitoreDatiModello.getDatiGenModello(),
          contenutoFileProspetto, codApp, idUtenteOwner, contesto);

      this.deleteGruppiModello(idModello);
      if (contenitoreDatiModello.getElencoGruppi().size() > 0) {
        String[] arrayIdGruppiAssociati = new String[contenitoreDatiModello.getElencoGruppi().size()];
        for (int i = 0; i < arrayIdGruppiAssociati.length; i++)
          arrayIdGruppiAssociati[i] = Integer.toString((contenitoreDatiModello.getElencoGruppi().elementAt(
              i)).getIdGruppo());
        // Insert dell'associazione gruppi-report in W_GRPMOD
        this.insertGruppoModello(idModello, arrayIdGruppiAssociati);
      }

      // Insert dei parametri del modello
      this.modelliDao.deleteParametri(idModello);

      for (int i = 0; i < contenitoreDatiModello.getElencoParametri().size(); i++) {
        ParametroModello parametro = contenitoreDatiModello.getElencoParametri().get(
            i);
        parametro.setIdModello(idModello);
        this.insertParametro(parametro);
      }

    } catch (Throwable t) {
      throw new InvalidDataAccessApiUsageException(null, t);
    }

  }

  /**
   * Estrae dalla cache un parametro di un modello per un determinato utente
   *
   * @param idAccount
   *        identificativo univoco dell'utente
   * @param idModello
   *        identificativo univoco del modello
   * @param codice
   *        codice del parametro da leggere
   * @return valore del parametro usato per l'ultima esecuzione del modello per
   *         tale utente
   */
  public String getCacheParametroModello(int idAccount, int idModello,
      String codice) {
    return this.modelliDao.getCacheParametroModello(idAccount, idModello,
        codice);
  }

  /**
   * Estrae il numero di report con modello aventi come sorgente dati il report
   * in input
   *
   * @param idRicerca
   *        identificativo univoco della ricerca per cui cercare se è sorgente
   *        dati di report con modello
   * @return numero di report con modello con sorgente il report in input
   */
  public Integer getNumeroModelliCollegatiASorgenteReport(int idRicerca) {
    return this.modelliDao.getNumeroModelliCollegatiASorgenteReport(idRicerca);
  }

}