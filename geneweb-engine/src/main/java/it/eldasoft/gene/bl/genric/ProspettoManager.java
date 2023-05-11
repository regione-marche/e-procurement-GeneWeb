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
package it.eldasoft.gene.bl.genric;

import it.eldasoft.gene.bl.genmod.CompositoreException;
import it.eldasoft.gene.bl.genmod.GestioneFileModelloException;
import it.eldasoft.gene.bl.genmod.ModelliManager;
import it.eldasoft.gene.db.dao.QueryDao;
import it.eldasoft.gene.db.dao.QueryDaoException;
import it.eldasoft.gene.db.dao.RicercheDao;
import it.eldasoft.gene.db.dao.jdbc.ListaDati;
import it.eldasoft.gene.db.dao.jdbc.ParametroStmt;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiProspetto;
import it.eldasoft.gene.db.domain.genric.DatiGenProspetto;
import it.eldasoft.gene.db.domain.genric.DatiGenRicerca;
import it.eldasoft.gene.db.domain.genric.DatiRisultato;
import it.eldasoft.gene.db.domain.genric.GruppoRicerca;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityHashMap;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

/**
 * Manager che si occupa di gestire tutte le operazioni di business logic sulla
 * parte di gestione delle ricerche con modello e l'associazione o meno ad un
 * gruppo
 *
 * @author Luca.Giacomazzo
 */
public class ProspettoManager extends ModelliManager {

  /** Logger Log4J di classe */
  static Logger          logger = Logger.getLogger(ProspettoManager.class);

  /** Reference al Dao per l'interrogazione della tabella W_RICERCHE */
  private RicercheDao     ricercheDao;

  /** Reference al Dao per la gestione delle query generiche su DB */
  private QueryDao        queryDao;

  /**
   * @param ricercheDao
   *        ricercheDao da settare internamente alla classe.
   */
  public void setRicercheDao(RicercheDao ricercheDao) {
    this.ricercheDao = ricercheDao;
  }

  /**
   * @param queryDao
   *        queryDao da settare internamente alla classe.
   */
  public void setQueryDao(QueryDao queryDao) {
    this.queryDao = queryDao;
  }

  /**
   * Funzione che restituisce i dati di un prospetto partendo da un
   * identificativo
   *
   * @param idProspetto
   *        Identificativo del prospetto
   * @return dati del prospetto
   */
  public DatiGenProspetto getProspettoById(int idProspetto) {
    DatiGenProspetto datiGenProspetto = new DatiGenProspetto();
    datiGenProspetto.setDatiGenRicerca(this.ricercheDao.getTestataRicercaByIdRicerca(idProspetto));
    Integer idModello = datiGenProspetto.getDatiGenRicerca().getIdProspetto();
    datiGenProspetto.setDatiModello(this.modelliDao.getModelloById(idModello.intValue()));
    // Sabbadin 12/03/2010: aggiunto settaggio eventuale nome ricerca in base
    // all'id valorizzato
    if (datiGenProspetto.getDatiModello().getIdRicercaSrc() != null) {
      DatiGenRicerca datiGenRicercaSrc = this.ricercheDao.getTestataRicercaByIdRicerca(datiGenProspetto.getDatiModello().getIdRicercaSrc().intValue());
      if (datiGenRicercaSrc != null)
        datiGenProspetto.getDatiModello().setNomeRicercaSrc(
            datiGenRicercaSrc.getNome());
    }

    return datiGenProspetto;
  }

  /**
   * Ritorna il primo record di un'entita, in modo da consentire il lancio del
   * compositore sul modello a partire dall'entità stessa e su un record
   * fittizio
   *
   * @param entita
   *        tabella
   * @param campiChiave
   *        campi chiave della tabella
   * @return Ritorna null se non esiste nemmeno un'occorrenza nella tabella,
   *         altrimenti ritorna il valore di una chiave concatenata mediante ";"
   * @throws SqlComposerException
   * @throws QueryDaoException
   */
  public String[] getChiavePrimoRecordEntitaPerCompositore(Tabella tabella)
      throws SqlComposerException, QueryDaoException {
    DatiRisultato datiRisultato = new DatiRisultato();

    List<Campo> elencoCampi = tabella.getCampiKey();
    Campo[] campi = new Campo[elencoCampi.size()];

    if (elencoCampi.size() > 0) {
    // Costruzione 'a mano'della query:
    // SELECT campoKey1 AS CAMPO_1, campoKey2 AS CAMPO_2, ... FROM enetita
    String query = "SELECT ";
    for (int i = 0; i < elencoCampi.size(); i++) {
      campi[i] = elencoCampi.get(i);
      query = query.concat((elencoCampi.get(i)).getNomeCampo()).concat(
          " AS ".concat(RicercheManager.PREFISSO_CAMPI_SELECT).concat(
              "" + (i + 1))
              + ", ");
    }
    query = query.substring(0, query.length() - 2);
    query = query.concat(" FROM ").concat(tabella.getNomeTabella());

    // Set del testo della query nel risultato della ricerca
    datiRisultato.setQuerySql(query);

    ParametroStmt[] parametri = new ParametroStmt[0];
    ListaDati listaDatiEstratti = this.queryDao.getDatiSelect(query, parametri,
        campi, 1, false);

    List<?> listaDati = listaDatiEstratti.getListaDati();
    if (listaDati != null && listaDati.size() > 0) {
      String[] valoriCampiChiave = new String[1];
      StringBuffer chiave = new StringBuffer("");
      for (int i = 0; i < elencoCampi.size(); i++) {
        Object object = UtilityHashMap.getValueCaseInsensitive(
            (HashMap<?,?>) listaDati.get(0),
            RicercheManager.PREFISSO_CAMPI_SELECT.concat("" + (i + 1)));
        if (object != null) {
          if (object instanceof Integer)
            chiave.append(((Integer) object).toString());
          if (object instanceof String) chiave.append((String) object);
        }
        if (i < elencoCampi.size() - 1) chiave.append(";");
      }
      valoriCampiChiave[0] = chiave.toString();
      return valoriCampiChiave;
    } else
      return new String[]{ null };
    } else {
      return null;
    }
  }

  /**
   * Metodo per l'insert dei dati generali di una ricerca con modello con il
   * gruppo di default, se il parametro idGruppo e' maggiore di zero
   *
   * @param datiGenRicerca
   * @param idGruppo
   */
  public void insertDatiGenRicerca(DatiGenRicerca datiGenRicerca, int idGruppo) {
    try {
      this.ricercheDao.insertTestataRicerca(datiGenRicerca);
    } catch (DataAccessException da) {
      logger.error("Errore durante l'operazione di insert di una ricerca con"
          + " modello in W_RICERCHE");
      throw da;
    }

    if (idGruppo > 0) {
      try {
        this.ricercheDao.insertAssociazioneRicercaGruppo(idGruppo,
            datiGenRicerca.getIdRicerca().intValue());
      } catch (DataAccessException da) {
        logger.error("Errore durante l'operazione di insert in W_GRPRIC dell'"
            + "associazione tra la ricerca con modello ed il gruppo di default");
        throw da;
      }
    }
  }

  /**
   * Effettua l'insert nella tabella W_GRPRIC dei gruppi associati alla ricerca
   * con modello, eliminando prima tutte le associazioni esistenti
   *
   * @param idGruppo
   * @param idRicerca
   */
  public void insertGruppoRicerca(int idRicerca, String[] arrayIdGruppo) {
    List<Integer> listaIdRicerca = new ArrayList<Integer>();
    listaIdRicerca.add(new Integer(idRicerca));
    // Cancellazione di tutte le associazioni esistenti tra i gruppi e
    // la ricerca con modello in analisi
    this.ricercheDao.deleteGruppiByIdRicerca(listaIdRicerca);

    // Inserimento delle nuove associazioni tra i gruppi e la ricerca con
    // prospetto in analisi
    if (arrayIdGruppo != null) {
      for (int i = 0; i < arrayIdGruppo.length; i++)
        this.ricercheDao.insertAssociazioneRicercaGruppo(
            UtilityNumeri.convertiIntero(arrayIdGruppo[i]).intValue(),
            idRicerca);
      // F.D. 02/05/07 se è stato associato almeno un gruppo il report non è +
      // personale
      DatiGenRicerca datiRicerca = this.ricercheDao.getTestataRicercaByIdRicerca(idRicerca);
      if (datiRicerca.getPersonale() == 1)
        this.ricercheDao.updatePersonale(idRicerca, 0);
    }
  }

  public void updateDatiGenRicerca(DatiGenRicerca datiGenRicerca) {
    List<Integer> elencoIdRicerca = new ArrayList<Integer>();
    elencoIdRicerca.add(datiGenRicerca.getIdRicerca());

    // Cancellazione dei dati generali della ricerca dalla tabella W_RICERCHE
    this.ricercheDao.deleteRicercheById(elencoIdRicerca);

    // Inserimento dei dati generali della ricerca dalla tabella W_RICERCHE
    this.ricercheDao.insertTestataRicerca(datiGenRicerca);
  }

  /**
   * Cancellazione di tutti i gruppi associati ad un prospetto
   *
   * @param idProspetto
   *        id del prospetto
   */
  public void deleteGruppiProspetto(int idProspetto) {
    ArrayList<Integer> elenco = new ArrayList<Integer>();
    elenco.add(new Integer(idProspetto));
    this.ricercheDao.deleteGruppiByIdRicerca(elenco);
  }

  /**
   * Cancellazione del prospetto a partire dall'id: cancellazione nell'ordine:
   * dei parametri, del modello, del file rtf e i relativi file idx, inf ed err,
   * dall'associazione ricerca - gruppi ed infine della ricerca
   *
   * @param idProspetto
   *        id del prospetto
   * @param codiceApplicativo
   *        codice applicativo da utilizzare per reperire il path dei modelli
   *        nella configurazione
   * @throws IOException
   *         eccezione emessa nel qual caso il file non sia eliminabile
   */
  public void deleteProspetto(int idProspetto, String codiceApplicativo)
      throws IOException {

    if (logger.isDebugEnabled())
      logger.debug("deleteProspetto: inizio metodo");

    List<Integer> listaIdRicerche = new ArrayList<Integer>();
    listaIdRicerche.add(new Integer(idProspetto));

    DatiGenRicerca datiGenRicerca = this.ricercheDao.getTestataRicercaByIdRicerca(idProspetto);
    // Determino il modello associato alla ricerca con modello prima di
    // cancellare la ricerca stessa
    int idModello = datiGenRicerca.getIdProspetto().intValue();

    DatiModello datiModello = this.modelliDao.getModelloById(idModello);
    String nomeFile = datiModello.getNomeFile();

    // l'eliminazione dei parametri la fa la deleteModello
    // // Si eliminano eventuali parametri della ricerca, collegati nel modello
    // if (logger.isDebugEnabled())
    // logger.debug("Delete dei parametri associati al modello: " + nomeFile);
    // this.modelliDao.deleteParametri(idModello);

    // Cancellazione del modello
    if (logger.isDebugEnabled())
      logger.debug("Cancellazione del modello associato alla ricerca con modello");
    this.modelliDao.deleteModello(idModello);

    // eliminazione della cache dei parametri per il modello
    this.modelliDao.deleteCacheParametriComposizioneModello(idModello);

    // Cancellazione dell'associazione ricerca-gruppi e della ricerca stessa
    if (logger.isDebugEnabled())
      logger.debug("Cancellazione della ricerca con modello e"
          + " dell'associazione con i gruppi");
    this.ricercheDao.deleteGruppiByIdRicerca(listaIdRicerche);
    this.ricercheDao.deleteRicercheById(listaIdRicerche);

    if (logger.isDebugEnabled())
      logger.debug("Eliminazione dei file del modello: " + nomeFile);
    // Eseguo l'eliminazione del modello
    if (!this.gestisciFileFisicoModello(nomeFile,
        ModelliManager.GESTIONE_MODELLO_ELIMINA, codiceApplicativo)) {
      throw new GestioneFileModelloException(
          "Errore durante l'eliminazione del file associato al modello", "");
    }

    if (logger.isDebugEnabled()) logger.debug("deleteProspetto: fine metodo");
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
   * @param contesto
   *        id del contesto oracle (opzionale); se non si utilizzano i contesti
   *        valorizzare con null
   */
  public void insertProspetto(DatiModello datiModello, byte[] fileData, String contesto)
      throws GestioneFileModelloException, CompositoreException,
      RemoteException {
    // Estraggo il nuovo identificativo del modello
    int idModello = this.genChiaviManager.getNextId("W_MODELLI");
    datiModello.setIdModello(idModello);
    // Al modello associo come nome del file l'id del modello stesso seguito
    // dalla estensione originale
    String estensioneFile = datiModello.getNomeFile().substring(
        datiModello.getNomeFile().indexOf("."));
    datiModello.setNomeFile(idModello + estensioneFile);

    this.gestisciFileModello(datiModello, fileData, false,
        datiModello.getCodiceApplicativo(), datiModello.getOwner().intValue(), contesto);
    try {
      this.modelliDao.insertModello(datiModello);
    } catch (DataAccessException e) {
      this.gestisciFileFisicoModello(datiModello.getNomeFile(),
          ModelliManager.GESTIONE_MODELLO_ELIMINA,
          datiModello.getCodiceApplicativo());
      throw e;
    }
  }

  /**
   * Funzione che esegue l'inserimento di un gruppo associato ad un prospetto
   *
   * @param gruppoRicerca
   *        gruppo della ricerca da inserire
   */
  public void insertGruppoProspetto(GruppoRicerca gruppoRicerca) {
    this.ricercheDao.insertGruppoRicerca(gruppoRicerca);
  }

  /**
   * Metodo per l'update dei dati generali di un report con modello
   *
   * @param datiModello
   * @param fileData
   * @param codiceApplicazione
   * @param idUtente
   *        id dell'utente
   * @param contesto
   *        id del contesto oracle (opzionale); se non si utilizzano i contesti
   *        valorizzare con null
   * @throws GestioneFileModelloException
   * @throws CompositoreException
   * @throws RemoteException
   */
  public void updateProspetto(DatiGenRicerca datiRicerca, DatiModello datiModello, byte[] fileData,
      String codiceApplicazione, int idUtente, String contesto) throws GestioneFileModelloException,
      CompositoreException, RemoteException {
    if (logger.isDebugEnabled())
      logger.debug("updateProspetto: inizio metodo");

    // se cambia il file, ovvero viene allegato un nuovo prospetto, allora si
    // procede con l'aggiornamento dello stesso nel file system, altrimenti si
    // salta il presente passo
    if (fileData != null) {
      // Al modello associo come nome del file l'id del modello stesso seguito
      // dalla estensione originale
      if (datiModello.getNomeFile() != null
          && datiModello.getNomeFile().length() > 0) {
        String estensioneFile = datiModello.getNomeFile().substring(
            datiModello.getNomeFile().indexOf("."));
        datiModello.setNomeFile(datiModello.getIdModello()
            + (estensioneFile != null ? estensioneFile : ""));
      }
      this.gestisciFileModello(datiModello, fileData, true, codiceApplicazione, idUtente, contesto);
    }
    try {
      List<Integer> elencoIdRicerca = new ArrayList<Integer>();
      elencoIdRicerca.add(datiRicerca.getIdRicerca());

      // Cancellazione dei dati generali della ricerca dalla tabella W_RICERCHE
      this.ricercheDao.deleteRicercheById(elencoIdRicerca);

      // Inserimento dei dati generali della ricerca dalla tabella W_RICERCHE
      this.ricercheDao.insertTestataRicerca(datiRicerca);

      this.modelliDao.updateModello(datiModello);
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

    if (logger.isDebugEnabled()) logger.debug("updateProspetto: fine metodo");
  }

  /**
   * Metodo per effettuare l'import di un report con modello in un'unica
   * transazione
   *
   * @param contenitoreDatiProspetto
   * @param codApp
   * @param idUtenteOwner
   * @param contenutoFileProspetto
   * @param contesto
   *        id del contesto oracle (opzionale); se non si utilizzano i contesti
   *        valorizzare con null
   * @throws InvalidDataAccessApiUsageException
   * @throws DataAccessException
   */
  public void importProspetto(
      ContenitoreDatiProspetto contenitoreDatiProspetto, String codApp,
      int idUtenteOwner, byte[] contenutoFileProspetto, String contesto) {

    boolean isModelloInserito = false;
    DatiGenRicerca datiGenerali = contenitoreDatiProspetto.getDatiGenProspetto().getDatiGenRicerca();
    DatiModello datiModello = contenitoreDatiProspetto.getDatiGenProspetto().getDatiModello();
    // Aggiornamento del codApp della ricerca da importare
    datiGenerali.setCodApp(codApp);
    datiModello.setCodiceApplicativo(codApp);
    // Aggiornamento dell'owner del report e del modello associato
    datiGenerali.setOwner(new Integer(idUtenteOwner));
    datiModello.setOwner(new Integer(idUtenteOwner));
    try {
      // si inserisce il modello associato al prospetto in W_MODELLI
      this.insertProspetto(datiModello, contenutoFileProspetto, contesto);
      isModelloInserito = true;

      // Determino l'id_modello del modello appena inserito
      Integer idModello = new Integer(datiModello.getIdModello());

      // Set di idProspetto nei dati generali della ricerca
      datiGenerali.setIdProspetto(idModello);

      // Determino l'idRicerca della ricerca che vado ad inserire
      int idProspetto = this.genChiaviManager.getNextId("W_RICERCHE");
      contenitoreDatiProspetto.setIdRicerca(idProspetto);

      // Insert dei dati generali del prospetto in W_RICERCHE
      this.insertDatiGenRicerca(datiGenerali, 0);
      if (contenitoreDatiProspetto.getElencoGruppi().size() > 0) {
        String[] arrayIdGruppiAssociati = new String[contenitoreDatiProspetto.getElencoGruppi().size()];
        for (int i = 0; i < arrayIdGruppiAssociati.length; i++)
          arrayIdGruppiAssociati[i] = Integer.toString(((GruppoRicerca) contenitoreDatiProspetto.getElencoGruppi().elementAt(
              i)).getIdGruppo());
        // Insert dell'associazione gruppi-report in W_GRPRIC
        this.insertGruppoRicerca(idProspetto, arrayIdGruppiAssociati);
      }

      // Insert dei parametri del prospetto
      for (int i = 0; i < contenitoreDatiProspetto.getElencoParametri().size(); i++)
        this.insertParametro((ParametroModello) contenitoreDatiProspetto.getElencoParametri().get(
            i));
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
  public void importProspettoEsistente(DatiGenProspetto datiProspettoEsistente,
      ContenitoreDatiProspetto contenitoreDatiProspetto, String codApp,
      int idUtenteOwner, byte[] contenutoFileProspetto, String contesto) {

    /*
     * Rispetto al metodo importProspetto questo metodo differisce per
     * l'implementazione dell'update del report con modello esistente nella
     * base dati. Infatti la gestione di un report con modello non sfrutta la
     * copia temporanea del report stesso presente in sessione, quindi ogni
     * modifica ha effetto diretto sulla base dati e sul modello ad esso
     * associato. Per evitare complicazioni nell'implementazione dell'operazioni
     * di roll-back in seguito ad una eccezione durante l'operazione di update
     * di un report con modello (dati generali, modello rtf, associazione
     * report-gruppi e parametri) l'operazione di update di un report con
     * prospetto è stato suddivisa in due operazioni: - modifica del nome
     * originale al report originale - insert su db di un nuovo report con
     * prospetto e del relativo modello - cancellazione del report originale
     */

    // Cambio del titolo del report con modello esistente su DB, per non
    // venire
    // meno al vincolo di unicita' dei report (a partita' di codApp e
    // codProfilo)
    this.updateDatiGenRicerca(datiProspettoEsistente.getDatiGenRicerca());

    try {
      // Aggiornamento del modello
      this.updateModello(datiProspettoEsistente.getDatiModello());
    } catch (Throwable t) {
      throw new InvalidDataAccessApiUsageException(null, t);
    }
    // Insert del nuovo report con modello
    this.importProspetto(contenitoreDatiProspetto, codApp, idUtenteOwner,
        contenutoFileProspetto, contesto);
    try {
      // Delete del report con modello esistente
      this.deleteProspetto(
          datiProspettoEsistente.getDatiGenRicerca().getIdRicerca().intValue(),
          codApp);
    } catch (Throwable t) {
      // Eccezione durante la cancellazione del report con modello originale:
      // cancello il file del modello appena inserito ed emettendo una eccezione
      // di tipo DataAccessException vengono effettuate in modo automatico la
      // rollback delle diverse query appena eseguite
      this.gestisciFileFisicoModello(
          contenitoreDatiProspetto.getDatiGenProspetto().getDatiModello().getNomeFile(),
          ModelliManager.GESTIONE_MODELLO_ELIMINA, codApp);
      throw new InvalidDataAccessApiUsageException(null, t);
    }
  }

}