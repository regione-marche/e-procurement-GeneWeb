/*
 * Created on 27-Ott-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.docass;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.FileManager;
import it.eldasoft.gene.bl.FileManagerException;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.dao.DocumentiAssociatiDao;
import it.eldasoft.gene.db.domain.docass.DocumentoAssociato;
import it.eldasoft.gene.web.struts.docass.CostantiDocumentiAssociati;
import it.eldasoft.gene.web.struts.docass.GestioneFileDocumentiAssociatiException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOExceptionWithCause;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Manager di gestione le operazioni di download e upload di un documento
 * associato
 *
 * @author Luca Giacomazzo
 */

public class DocumentiAssociatiManager {

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(DocumentiAssociatiManager.class);

  /**
   * Reference al manager per la generazione di una nuova chiave
   */
  private GenChiaviManager genChiaviManager;

  /**
   * @param genChiaviManager
   *        genChiaviManager da settare internamente alla classe.
   */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  private DocumentiAssociatiDao documentiAssociatiDao;

  /**
   * @param documentiAssociatiDao
   *        documentiAssociatiDao da settare internamente alla classe.
   */
  public void setDocumentiAssociatiDao(
      DocumentiAssociatiDao documentiAssociatiDao) {
    this.documentiAssociatiDao = documentiAssociatiDao;
  }

  /**
   * Reference al manager per la gestione dei file sul server
   */
  private FileManager fileManager;

  /**
   * @param fileManager
   *        fileManager da settare internamente alla classe.
   */
  public void setFileManager(FileManager fileManager) {
    this.fileManager = fileManager;
  }

  private DocumentiAssociatiManager nestedDocumentiAssociatiManager;

  /**
   * @param nestedDocumentiAssociatiManager
   *        nestedDocumentiAssociatiManager da settare internamente alla classe.
   */
  public void setNestedDocumentiAssociatiManager(
      DocumentiAssociatiManager nestedDocumentiAssociatiManager) {
    this.nestedDocumentiAssociatiManager = nestedDocumentiAssociatiManager;
  }

  private FileAllegatoManager fileAllegatoManager;

  /**
  *
  * @param fileAllegatoManager
  */
   public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
     this.fileAllegatoManager = fileAllegatoManager;
   }

  /**
   * Metodo per determinare il nome del file di destinazione nella cartella dei
   * documenti associati. Se idDocAss > 0, il metodo ritorna la stringa XXXXXX_<nomefile.est>,
   * dove XXXXXX rappresenta idDocAss che è pari al campo C0ACOD del record
   * indentato a destra e riempito con zeri a sinistra
   *
   * In ogni caso, se in caso di file e' gia' presente nella cartella di
   * destinazione il metodo ritorna la stringa XXXXXX_<nomefile>+<n>.est> dove
   * n e' un progressivo che parte da 0
   *
   * @param nomeFile
   * @param pathSulServer
   * @param idDocAss
   * @return Ritorna il nome del file con cui inserire il documento associato
   *         nella cartella dei documenti associati
   */
  private String getNomeFileSulServer(String nomeFile, String pathSulServer,
      long idDocAss) {

    String prefisso = "";
    String nomeFileDestinazione = null;
    if (idDocAss > 0) {
      prefisso = UtilityStringhe.fillLeft("" + idDocAss, '0', 6);
      if (nomeFile.startsWith(prefisso))
        nomeFileDestinazione = nomeFile;
      else
        nomeFileDestinazione = prefisso.concat("_").concat(nomeFile);
    } else {
      nomeFileDestinazione = nomeFile;
    }

    File directorySulServer = new File(pathSulServer);

    String[] listaFileSulServer = directorySulServer.list();
    // Creo un Set contenente i nomi dei file presenti sul server
    Set<String> setFileSulServer = new HashSet<String>();
    for (int i = 0; i < listaFileSulServer.length; i++)
      setFileSulServer.add(listaFileSulServer[i].toUpperCase());
    // Effettuo l'upperCase per evitare di gestire il seguente test:
    // 'report.rft' == 'rEport.rtf'
    // Effettuando l'upperCase il test appena proposto risulta sempre vero.

    int ripetizioni = 0;
    String nomeTmp = nomeFileDestinazione;
    while (setFileSulServer.contains(nomeTmp.toUpperCase())) {
      ripetizioni++;
      nomeTmp = FilenameUtils.getBaseName(nomeFileDestinazione).concat(
          "" + ripetizioni).concat(
          ".".concat(FilenameUtils.getExtension(nomeFileDestinazione)));
    }
    return nomeTmp;
  }

  /**
   * Funzione che associa un modello composto alla entita' dalla quale e' stata
   * lanciata la composizione, inserendo un record in C0OGGASS e copiando il
   * modello dalla cartella di out dei modelli alla cartella dei documenti
   * associati
   *
   * @param moduloAttivo
   * @param entita
   * @param valoriChiavi
   * @param dataDiCreazione
   * @param titoloDocumento
   * @param nomeFileSrc
   * @param pathServerDocAss
   * @param pathAssolutoModelliOut
   * @param tipoDocumento
   * @return
   * @throws IOException
   */
  public String associaModello(String moduloAttivo, String entita,
      String valoriChiavi, Date dataDiCreazione, String titoloDocumento,
      String nomeFileSrc, String pathServerDocAss, String pathClientDocAss,
      String pathAssolutoModelliOut, String tipoDocumento) throws IOException {

    if (logger.isDebugEnabled()) logger.debug("associaModello: inizio metodo");

    String nomeFileDst = null;
    File fileIn = null;
    File fileOut = null;

    try {
      boolean inserito = false;
      int numeroTentativi = 0;
      // tento di inserire il record finchè non genero un ID univoco a causa
      // della concorrenza, o raggiungo il massimo numero di tentativi

      DocumentoAssociato documento = new DocumentoAssociato();
      documento.setCodApp(moduloAttivo);
      documento.setEntita(entita);
      String[] tmp = valoriChiavi.split(";");

      if (tmp.length >= 1)
        documento.setCampoChiave1(tmp[0]);
      else
        documento.setCampoChiave1("#");
      if (tmp.length >= 2)
        documento.setCampoChiave2(tmp[1]);
      else
        documento.setCampoChiave2("#");
      if (tmp.length >= 3)
        documento.setCampoChiave3(tmp[2]);
      else
        documento.setCampoChiave3("#");
      if (tmp.length >= 4)
        documento.setCampoChiave4(tmp[3]);
      else
        documento.setCampoChiave4("#");
      if (tmp.length >= 5)
        documento.setCampoChiave5(tmp[4]);
      else
        documento.setCampoChiave5("#");
      documento.setDataInserimento(dataDiCreazione);
      documento.setTitolo(titoloDocumento);
      documento.setNomeDocAss(nomeFileSrc);
      if ("1".equals(ConfigManager.getValore(CostantiDocumentiAssociati.PROP_DOCUMENTI_DB))) {
        documento.setPathDocAss("[default]");
      } else {
        documento.setPathDocAss(pathClientDocAss);
      }
      documento.setTipoDocumento(tipoDocumento);

      while (!inserito
          && numeroTentativi < CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
        try {
          // Nuovo id della tabella C0OGGASS
          int id = this.genChiaviManager.getMaxId("C0OGGASS", "C0ACOD");
          documento.setId(id + 1);

          this.nestedDocumentiAssociatiManager.nestedInsertDocAss(documento);
          inserito = true;
        } catch (DataIntegrityViolationException e) {
          if (numeroTentativi < CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
            logger.error(
                "Fallito tentativo "
                    + (numeroTentativi + 1)
                    + " di inserimento record per chiave duplicata, si ritenta nuovamente",
                e);
            numeroTentativi++;
          } else
            throw e;
        }
      }

      if ("1".equals(ConfigManager.getValore(CostantiDocumentiAssociati.PROP_DOCUMENTI_DB))) {

        int iddocdig_curr = this.genChiaviManager.getMaxId("W_DOCDIG", "IDDOCDIG", "IDPRG = '" + moduloAttivo + "'");
        Long iddocdig_next = new Long(iddocdig_curr + 1);

        fileIn = new File(pathAssolutoModelliOut + nomeFileSrc);
        this.fileAllegatoManager.insertFileAllegatoAssociaModello(moduloAttivo, iddocdig_next, "C0OGGASS", Long.toString(documento.getId()), FileUtils.readFileToByteArray(fileIn));

        if (fileIn != null && fileIn.exists()) {
          fileIn.delete();
        }


      } else {
        nomeFileDst = this.getNomeFileSulServer(nomeFileSrc, pathServerDocAss, -1);

        // Copia del modello appena composto in DocAss...
        fileIn = new File(pathAssolutoModelliOut + nomeFileSrc);
        fileOut = new File(pathServerDocAss + nomeFileDst);
        FileUtils.copyFile(fileIn, fileOut);

        // Cancellazione del modello composto della cartella di .../Modelli/out/
        // di default non necessaria in quanto gestita nella action
        // ComponiModello.eliminaFileComposto

        if (logger.isDebugEnabled())
          logger.debug("Copiato il file "
              + pathAssolutoModelliOut
              + nomeFileSrc
              + " in "
              + pathServerDocAss
              + nomeFileDst);
      }

    } catch (SQLException e) {
      logger.error("Errore durante la copia del file", e);
      throw new IOExceptionWithCause(e);
    } catch (IOException io) {
      logger.error("Errore durante la copia del file "
          + pathAssolutoModelliOut
          + nomeFileSrc
          + " in "
          + pathServerDocAss
          + nomeFileDst
          + "\n"
          + io.getMessage(), io);

      // in caso di errore si eliminano i file

      // Cancellazione del file destinatario
      if (fileOut != null && fileOut.exists()) {
        fileOut.delete();
      }

      // Cancellazione del file sorgente: teoricamente andrebbe eliminato
      // mediante chiamata al WS, ma è un caso estremamente remoto che vada in
      // errore la copia del file
      if (fileIn != null && fileIn.exists()) {
        fileIn.delete();
      }

      throw io;
    }

    if (logger.isDebugEnabled()) logger.debug("associaModello: fine metodo");
    return nomeFileDst;
  }

  /**
   * Metodo per la cancellazione del singolo documento associato
   *
   * @param idDocumento
   *        id del documento associato da eliminare
   * @param pathDocAss
   *        path del file da cancellare
   * @param nomeDocAss
   *        nome del file da cancellare
   * @param cancellaFile
   * @throws FileManagerException
   */
  public void deleteDocumento(long idDocumento, String pathDocAss,
      String nomeDocAss, String moduloAttivo, boolean cancellaFile)
      throws FileManagerException, GestioneFileDocumentiAssociatiException,
      SqlComposerException {
    if (logger.isDebugEnabled())
      logger.debug("deleteDocumento: inizio metodo");

    this.documentiAssociatiDao.deleteDocumentoAssociatoById(idDocumento);
    if (cancellaFile) {
      if (this.documentiAssociatiDao.getNumeroDocumentiAssociatiByPathNome(
          pathDocAss, nomeDocAss) == 0) {
        if (CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT.equals(pathDocAss))
          pathDocAss = ConfigManager.getValore(CostantiDocumentiAssociati.PROP_PATH_DOCUMENTI);
        this.fileManager.delete(pathDocAss, nomeDocAss);
      } else {
        // Il file da cancellare e' associato ad altre occorrenze di C0OGGASS,
        // pertanto non e' possibile cancellarlo. All'utente bisogna segnalare
        // che
        // il motivo della mancata cancellazione di tale file (compreso di path)
        GestioneFileDocumentiAssociatiException gfda = new GestioneFileDocumentiAssociatiException(
            GestioneFileDocumentiAssociatiException.CODICE_ERRORE_KO_CANCELLA_FILE_PIU_OCCORRENZE);
        if (CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT.equals(pathDocAss))
          gfda.setParametri(new Object[] { nomeDocAss });
        else {
          gfda.setParametri(new Object[] { pathDocAss + nomeDocAss });
        }
        throw gfda;
      }
    }
    if (logger.isDebugEnabled()) logger.debug("deleteDocumento: fine metodo");
  }

  /**
   * Funzione che estrae la lista dei documenti associati a partire dal codice
   * applicativo (o modulo attivo), dall'entità principale di partenza e dai
   * valori della chiave primaria di tale entità
   *
   * @param docAss
   *        documento associato
   */
  public List<?> getListaDocumentiAssociati(DocumentoAssociato docAss) {
    return this.documentiAssociatiDao.getListaDocumentiAssociati(docAss);
  }

  /**
   *
   * @param id
   *        id del documento associato da estrarre dalla base dati
   * @return Ritorna l'oggetto DocumentoAssociato relativo all'id specificato
   */
  public DocumentoAssociato getDocumentoAssociatobyId(long id) {
    return this.documentiAssociatiDao.getDocumentoAssociatoById(id);
  }

  /**
   * Funzione che estrae un documento associato in funzione dell'entita', valori
   * dei campi chiave dell'entita', del titolo del documento associato e del
   * nome del file. Titolo del documento associato, nome del file e codice
   * applicazione sono opzionali. A tutti i campi viene applicato l'operatore di
   * uguglianza
   *
   * @param docAss
   * @return Ritorna l'oggetto DocumentoAssociato relativo all'id specificato
   * @throws SqlComposerException
   */
  public DocumentoAssociato getDocumentoAssociatobyChiaviEntitaTitolo(
      DocumentoAssociato docAss) throws SqlComposerException {
    return this.documentiAssociatiDao.getDocumentoAssociatoByChiaviEntitaTitolo(docAss);
  }

  /**
   * Metodo per l'insert nella tabella C0OGGASS e salvataggio nell'area shared
   * del file relativo
   *
   * @param docAss
   * @return Ritorna l'id del doc ass inserito. Se l'inserimento
   */
  public long insertDocAss(DocumentoAssociato docAss, byte[] fileData,
      String pathDocAss) throws GestioneFileDocumentiAssociatiException {

    long newId = -1;
    String nomeFileUploadato = null;

    if (fileData != null && fileData.length > 0) {
      try {
        // Nuovo id della tabella C0OGGASS
        newId = this.genChiaviManager.getMaxId("C0OGGASS", "C0ACOD") + 1;

        // Upload del file nella cartella dei documenti associati
        nomeFileUploadato = this.uploadDocAss(pathDocAss,
            docAss.getNomeDocAss(), fileData, newId, docAss.getCodApp());

        docAss.setId(newId);
        docAss.setNomeDocAss(nomeFileUploadato.substring(nomeFileUploadato.lastIndexOf("/") + 1));
        this.documentiAssociatiDao.insertDocAss(docAss);
      } catch (DataAccessException da) {
        try {
          this.fileManager.delete(
              nomeFileUploadato.substring(0,
                  nomeFileUploadato.lastIndexOf("/") + 1),
              nomeFileUploadato.substring(nomeFileUploadato.lastIndexOf("/") + 1));
          throw da;
        } catch (FileManagerException fm) {
          logger.error("Errore durante la cancellazione del file "
              + nomeFileUploadato
              + " dopo i diversi tentativi di insert generando diversi id. Tale "
              + "file potrebbe essere rimasto nella cartella. Farlo cancellare "
              + "da un amministratore.");
        }
      }

      return newId;
    } else {
      throw new GestioneFileDocumentiAssociatiException(
          GestioneFileDocumentiAssociatiException.ERROR_UPLOAD_FILE_VUOTO);
    }
  }

  /**
   * Metodo per effettuare l'update di un record della tabella c0oggass e del
   * relativo file, ammesso che esso abbia lo stesso nome del file originale
   *
   * @param docAss
   * @param file
   * @throws GestioneFileDocumentiAssociatiException,
   *         Exception
   */
  public void updateDocAss(DocumentoAssociato docAss, byte[] fileData)
      throws GestioneFileDocumentiAssociatiException, Exception {

    DocumentoAssociato copiaDocAss = null;
    String tmpNomeDocAss = null;
    String tmpPathDocAss = null;
    short faseUpdateDocAss = 0;
    GestioneFileDocumentiAssociatiException gfda = null;
    String nomeFileUploadato = null;

    String moduloAttivo = docAss.getCodApp();
    if (fileData != null && fileData.length > 0) {
      if (logger.isDebugEnabled())
        logger.debug("Update dei dati in c0oggass con sostituzione del file");

      /*
       * L'operazione di update di un documento associato avviene in diverse
       * fasi: 1. rename del file originale in <nomeFileOriginale>.tmp 2. upload
       * del nuovo file 3. update nel DB dell'occorrenza 4. cancellazione del
       * file originale e rinominato. In caso di errore/eccezione si effettuano
       * le operazioni tali da ripristinare la situazione precedente
       * all'operazione di update
       */

      long id = docAss.getId();
      // Carico da DB il record da modificare in modo da poter effettuare la
      // copia temporanea del file relativo al documento associato. Tale file
      // verra' ripristinato in caso di eccezione
      copiaDocAss = this.documentiAssociatiDao.getDocumentoAssociatoById(id);

      // Cambio il nome al file originale per ripristinarlo in caso di
      // eccezione:
      // il file temporaneo ha il seguente formato:
      // <nome file originale>.<estensione file originale>.tmp
      tmpNomeDocAss = copiaDocAss.getNomeDocAss().concat(".tmp");
      tmpPathDocAss = copiaDocAss.getPathDocAss();

      // Conversione del path da [default] al valore reale
      if (CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT.equals(tmpPathDocAss))
        tmpPathDocAss = ConfigManager.getValore(CostantiDocumentiAssociati.PROP_PATH_DOCUMENTI);

      try {
        // Fase 1: Rinomina del file originale
        fileManager.rename(tmpPathDocAss, copiaDocAss.getNomeDocAss(),
            tmpNomeDocAss);
        faseUpdateDocAss = 1;
        // Fase 1 eseguita

        // Fase 2: Upload del nuovo file da inserire
        nomeFileUploadato = this.uploadDocAss(docAss.getPathDocAss(),
            docAss.getNomeDocAss(), fileData, copiaDocAss.getId(), moduloAttivo);
        faseUpdateDocAss = 2;
        // Fase 2 eseguita

        // Fase 3: Update del record nella tabella c0oggass
        docAss.setNomeDocAss(nomeFileUploadato.substring(nomeFileUploadato.lastIndexOf("/") + 1));
        this.documentiAssociatiDao.updateDocAss(docAss);
        faseUpdateDocAss = 3;
        // Fase 3 eseguita

        // Fase 4: Delete del file temporaneo per ripristino in caso di
        // eccezione. Se l'operazione di rename e' stata effettuata allora
        // si cancella il file originale
        String tmp = docAss.getPathDocAss();
        if (CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT.equals(tmp))
          tmp = ConfigManager.getValore(CostantiDocumentiAssociati.PROP_PATH_DOCUMENTI);

        if (faseUpdateDocAss > 0) fileManager.delete(tmp, tmpNomeDocAss);
        faseUpdateDocAss = 4;
        // Fase 4 eseguita

      } catch (Exception t) {
        if (t instanceof FileManagerException
            || t instanceof GestioneFileDocumentiAssociatiException
            || t instanceof DataAccessException) {
          switch (faseUpdateDocAss) {
          case 0:
            gfda = new GestioneFileDocumentiAssociatiException(
                GestioneFileDocumentiAssociatiException.ERROR_UPLOAD_RENAMING_KO,
                t);
            gfda.setParametri(new Object[] {
                copiaDocAss.getNomeDocAss(),
                tmpNomeDocAss });
            // Messaggio nel GENERESOURCES: Errore durante l'operazione di
            // renaming del file {0} nel file {1}

            // Operazione di ripristino della situazione precedente all'errore:
            // rename del file temporaneo al nome originale
            this.ripristinoFileOriginale(copiaDocAss, tmpNomeDocAss,
                moduloAttivo);

            break;
          case 1:
            gfda = new GestioneFileDocumentiAssociatiException(
                GestioneFileDocumentiAssociatiException.ERROR_UPLOAD_FILE_KO, t);
            gfda.setParametri(new Object[] { docAss.getNomeDocAss(),
                docAss.getPathDocAss() });
            // Messaggio nel GENERESOURCES: Errore durante l'operazione di
            // upload del nuovo file {0} nella directory {1}

            // Operazione di ripristino della situazione precedente all'errore:
            // rename del file temporaneo al nome originale e cancellazione del
            // file in fase di upload se esistente
            this.ripristinoFileOriginale(copiaDocAss, tmpNomeDocAss,
                moduloAttivo);
            if (nomeFileUploadato != null) {
              String tmp = docAss.getPathDocAss();
              if (CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT.equals(tmp))
                tmp = ConfigManager.getValore(CostantiDocumentiAssociati.PROP_PATH_DOCUMENTI);
              this.deleteNuovoFile(tmp, docAss.getNomeDocAss(), docAss.getId());
            }
            break;
          case 2:
            gfda = new GestioneFileDocumentiAssociatiException(
                GestioneFileDocumentiAssociatiException.ERROR_UPLOAD_DB_KO, t);

            // Operazione di ripristino condizione precedente all'errore:
            // rename del file temporaneo al nome originale e cancellazione del
            // file in fase di upload se esistente
            this.ripristinoFileOriginale(copiaDocAss, tmpNomeDocAss,
                moduloAttivo);
            if (nomeFileUploadato != null) {
              String tmp = docAss.getPathDocAss();
              if (CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT.equals(tmp))
                tmp = ConfigManager.getValore(CostantiDocumentiAssociati.PROP_PATH_DOCUMENTI);
              this.deleteNuovoFile(tmp, docAss.getNomeDocAss(), docAss.getId());
            }
            break;
          case 3:
            gfda = new GestioneFileDocumentiAssociatiException(
                GestioneFileDocumentiAssociatiException.ERROR_UPLOAD_DELETE_TMP_KO,
                t);
            gfda.setParametri(new Object[] { copiaDocAss.getPathDocAss()
                + tmpNomeDocAss });
            // Messaggio GENERESOURCES: Errore durante l'operazione di rimozione
            // del file {0}" + creato come copia temporanea del file originale

            // Operazione di ripristino condizione precedente all'errore:
            // riprovo a cancellare il file temporaneo. Eventualmente rimane
            // nella cartella dei documenti associati, ma non ha alcun legame
            // con la tabella c0oggass. Tale file potra' essere cancellato solo
            // da un amministratore.
            this.deleteFileTmp(copiaDocAss.getPathDocAss(),
                copiaDocAss.getNomeDocAss());
            break;
          }
        } else
          throw t;
      } finally {
        if (faseUpdateDocAss < 4) {
          if (gfda != null) throw gfda;
        }
      }
    } else {
      if (fileData == null) {
        if (logger.isDebugEnabled())
          logger.debug("Update dei dati in c0oggass senza sostituzione del file");
        // Update del record nella tabella c0oggass
        this.documentiAssociatiDao.updateDocAss(docAss);
      } else {
        throw new GestioneFileDocumentiAssociatiException(
            GestioneFileDocumentiAssociatiException.ERROR_UPLOAD_FILE_VUOTO);
      }
    }
  }

  /**
   * @param copiaDocAss
   * @param tmpNomeDocAss
   */
  private void ripristinoFileOriginale(DocumentoAssociato copiaDocAss,
      String tmpNomeDocAss, String moduloAttivo) {

    String tmp = copiaDocAss.getPathDocAss();
    if (CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT.equals(tmp))
      tmp = ConfigManager.getValore(CostantiDocumentiAssociati.PROP_PATH_DOCUMENTI);

    try {
      this.fileManager.rename(tmp, tmpNomeDocAss, copiaDocAss.getNomeDocAss());
    } catch (FileManagerException fm) {
      logger.error("Tentativo fallito di ripristino del file originale "
          + "relativo all'occorrenza del documento associato con campo "
          + "C0ACOD = "
          + copiaDocAss.getId()
          + ". Il file originale potrà "
          + "essere ripristinato da un amministratore eliminando l'estensione "
          + "'tmp' al file "
          + copiaDocAss.getPathDocAss()
          + tmpNomeDocAss);
    }
  }

  private void deleteNuovoFile(String pathDocAss, String nomeDocAss,
      long idDocAss) {
    try {
      this.fileManager.delete(pathDocAss, nomeDocAss);
    } catch (FileManagerException fm) {
      logger.error("Tentativo fallito di cancellazione del nuovo file "
          + "da associare all'occorrenza del documento associato con campo "
          + "C0ACOD = "
          + idDocAss);
    }
  }

  private void deleteFileTmp(String pathDocAss, String nomeDocAss) {
    try {
      this.fileManager.delete(pathDocAss, nomeDocAss);
    } catch (FileManagerException fm) {
      logger.error("Tentativo fallito di cancellazione del file temporaneo "
          + pathDocAss
          + nomeDocAss
          + " .Tale file potrà essere cancellato da un "
          + "amministratore.");
    }
  }

  /**
   * Metodo che effettua l'upload del file, cioe' effettua la copia del file
   * relativo al documento associato dall'oggetto FormFile presente nel request
   * nella cartella dei documenti associati dell'applicativo
   *
   * @param documento
   *        bean DocumentoAssociato
   * @throws GestioneFileDocumentiAssociatiException
   */
  private String uploadDocAss(String pathServer, String nomeFileSrc,
      byte[] fileData, long newIdDocAss, String moduloAttivo)
      throws GestioneFileDocumentiAssociatiException {
    if (logger.isDebugEnabled()) logger.debug("uploadFile: inizio metodo");
    String nomeFileCopiato = null;

    try {
      String nomeFileDst = null;

      if (CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT.equals(pathServer)) {
        // Conversione del path da [default] al valore reale
        pathServer = ConfigManager.getValore(CostantiDocumentiAssociati.PROP_PATH_DOCUMENTI);

        // Verifica del nome del file da inserire rispetto ai file gia'
        // presenti sul server e set del file di destinazione
        nomeFileSrc = nomeFileSrc.replaceAll("[^a-zA-Z0-9_.-?()=\\[\\]]", "_");
        nomeFileDst = this.getNomeFileSulServer(nomeFileSrc, pathServer,
            newIdDocAss);
      } else {
        // Verifica del nome del file da inserire rispetto ai file gia'
        // presenti sul server e set del file di destinazione
        nomeFileDst = this.getNomeFileSulServer(nomeFileSrc, pathServer, -1);
      }

      File fileDst = new File(pathServer + nomeFileDst);
      // Se il file gia' esiste ritorno al cliente un messaggio opportuno
      if (fileDst.exists())
        throw new GestioneFileDocumentiAssociatiException(
            GestioneFileDocumentiAssociatiException.ERROR_FILE_ESISTENTE);

      FileOutputStream output = new FileOutputStream(fileDst);
      output.write(fileData);
      output.close();

      nomeFileCopiato = pathServer+nomeFileDst;
      if (logger.isDebugEnabled())
        logger.debug("Eseguito upload del file: "
            + nomeFileSrc
            + " nella directory di output: "
            + pathServer
            + " con il nome "
            + nomeFileDst);
    } catch (GestioneFileDocumentiAssociatiException gfda) {
      // Cancellazione del documento appena copiato
//      if (nomeFileCopiato != null) {
//        try {
//          this.fileManager.delete(pathServer, nomeFileCopiato);
//        } catch (FileManagerException fm) {
//          logger.error("Errore durante la rimozione del file "
//              + pathServer
//              + nomeFileCopiato
//              + " in caso di eccezione", fm);
//        }
//      }
      throw gfda;
    } catch (Throwable t) {
      logger.error("Errore inaspettato nella gestione file upload: ", t);
      throw new GestioneFileDocumentiAssociatiException(
          GestioneFileDocumentiAssociatiException.ERROR_INASPETTATO, t);
    }
    if (logger.isDebugEnabled()) logger.debug("uploadFile: fine metodo");
    return nomeFileCopiato;
  }

  /**
   * Metodo per associare (insert/update) un file come documento associato alla
   * entita specificata nell'oggetto docAss. Nel caso il documento associato sia
   * già presente nella C0OGGASS, viene solo sostituito il file, con ripristino
   * della situazione iniziale in caso di errore
   *
   * Esempio d'uso: vedere le funzioni di import/export OEPV e/o Lista
   * lavorazioni e forniture di GareWeb
   *
   * @param docAss
   * @param tempFile
   * @throws Exception
   * @throws GestioneFileDocumentiAssociatiException
   */
  public void associaFile(DocumentoAssociato docAss, byte[] arrayTempFile)
      throws GestioneFileDocumentiAssociatiException, Exception {

    // Determino se il file e' gia' stato esportato almeno una volta
    DocumentoAssociato exportEsistente = this.getDocumentoAssociatobyChiaviEntitaTitolo(docAss);
    if (exportEsistente != null) {
      // Sostituzione del file nella cartella predefinita dei documenti
      // associati, con aggiornamento della data di inserimento
      exportEsistente.setDataInserimento(new Date());
      this.updateDocAss(exportEsistente, arrayTempFile);
    } else {
      // Inserimento del file in C0OGGASS e copia del file nella cartella dei
      // documenti associati
      this.insertDocAss(docAss, arrayTempFile,
          CostantiDocumentiAssociati.PATH_DOCUMENTI_DEFAULT);
    }
  }

  /**
   * Insert nella documenti associati utilizzata in fase di associazione
   * modello, per cui si utilizza un ciclo di tentativi di inserimento, e deve
   * essere garantito il funzionamento anche in postgres sfruttando una
   * transazione nested con savepoint automatico ad inizio metodo
   *
   * @param documento documento da inserire
   */
  public void nestedInsertDocAss(DocumentoAssociato documento) {
    this.documentiAssociatiDao.insertDocAss(documento);
  }

}