/*
 * Created on 19-giu-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * Classe di gestione del file system effettua download ed eliminazione in base
 * a nomeFile e chiaveProperty
 *
 * @author Francesco De Filippis
 */
public class FileManager {

  /**
   * L.G. 03/07/2007: sono stati definiti i metodi copy, rename, move e
   * moveRename. Per un caso d'uso di questi metodi si faccia riferimento alla
   * Action presente nella cartella
   * test/src/java/it/eldasoft/gene/web/struts/FileManagerTestAction.java. Tale
   * classe oltre ad essere una Action usata per testare i metodi della classe
   * FileManager, rappresenta inoltre un caso d'uso di tali metodi.
   */

  public static final String ARGOMENTO_PATH_FILE_SRC     = "pathFileSrc";
  public static final String ARGOMENTO_PATH_FILE_DST     = "pathFileDst";
  public static final String ARGOMENTO_NOME_FILE_SRC     = "nomeFileSrc";
  public static final String ARGOMENTO_NOME_FILE_DST     = "nomeFileDst";
  public static final String ARGOMENTO_PATHNAME_FILE_SRC = "pathNameFile";

  /** Logger Log4J di classe */
  static Logger              logger                      = Logger.getLogger(FileManager.class);

  /**
   * Funzione che esegue l'eliminazione di un file
   *
   * @param pathFile
   *        path del file da cancellare
   * @param nomeFile
   *        nome del file da cancellare
   * @throws FileManagerException
   */
  public void delete(String pathFile, String nomeFile)
      throws FileManagerException {
    if (logger.isDebugEnabled()) logger.debug("Delete: inizio metodo");

    if (pathFile != null && nomeFile != null) {
      if (logger.isDebugEnabled())
        logger.debug("Cancellazione del file: " + pathFile + nomeFile);

      // Eseguo l'eliminazione del file
      boolean result = true;
      File fileDel = new File(pathFile + nomeFile);
      if (fileDel.exists() && fileDel.canWrite()) {
        result = fileDel.delete();
        if (!result) {
          FileManagerException fm = new FileManagerException(
              FileManagerException.CODICE_ERRORE_CANCELLAZIONE_FILE);
          fm.setParametri(new Object[] { pathFile + nomeFile });
          throw fm;
        }
      } else {
        FileManagerException fm = null;
        if (!fileDel.exists()) {
          fm = new FileManagerException(
              FileManagerException.CODICE_ERRORE_FILE_INESISTENTE);
          fm.setParametri(new Object[] { pathFile + nomeFile });
          throw fm;
        } else {
          fm = new FileManagerException(
              FileManagerException.CODICE_ERRORE_FILE_ESISTENTE_NO_ACCESSIBILE);
          fm.setParametri(new Object[] { pathFile + nomeFile });
          throw fm;
        }
      }
    } else {
      FileManagerException fm = new FileManagerException(
          FileManagerException.CODICE_ERRORE_CANCELLAZIONE_FILE_ARG_NULL);
      if (pathFile == null)
        fm.setParametri(new Object[] { FileManager.ARGOMENTO_PATH_FILE_SRC });
      else
        fm.setParametri(new Object[] { FileManager.ARGOMENTO_NOME_FILE_SRC });
      throw fm;
    }
    if (logger.isDebugEnabled()) logger.debug("Delete: fine metodo");
  }

  /**
   * Funzione che esegue il download di un file
   *
   * @param pathFile
   *        path del file di cui fare il download
   * @param nomeFile
   *        nome del file di cui fare il download
   * @param response
   * @throws FileManagerException
   */
  public void download(String pathFile, String nomeFile,
      HttpServletResponse response) throws FileManagerException {
    if (logger.isDebugEnabled())
      logger.debug("download("
          + pathFile
          + ", "
          + nomeFile
          + "): inizio metodo");

    if (pathFile != null && nomeFile != null) {
      this.downloadFile(pathFile + nomeFile, response);
    } else {
      FileManagerException gfe = new FileManagerException(
          FileManagerException.CODICE_ERRORE_DOWNLOAD_FILE_ARG_NULL);
      if (pathFile == null)
        gfe.setParametri(new Object[] { FileManager.ARGOMENTO_PATH_FILE_SRC });
      else
        gfe.setParametri(new Object[] { FileManager.ARGOMENTO_NOME_FILE_SRC });

      throw gfe;
    }
    if (logger.isDebugEnabled()) logger.debug("download("
          + pathFile
          + ", "
          + nomeFile
          + "): fine metodo");
  }

  /**
   * Funzione che esegue il download di un file
   *
   * @param pathNameFile
   *        pathname completo del file di cui fare il download
   * @param response
   * @throws FileManagerException
   */
  public void download(String pathNameFile, HttpServletResponse response)
      throws FileManagerException {
    if (logger.isDebugEnabled())
      logger.debug("download(" + pathNameFile + "): inizio metodo");

    if (pathNameFile != null) {
      this.downloadFile(pathNameFile, response);
    } else {
      FileManagerException gfe = new FileManagerException(
          FileManagerException.CODICE_ERRORE_DOWNLOAD_FILE_ARG_NULL);
      gfe.setParametri(new Object[] { FileManager.ARGOMENTO_PATHNAME_FILE_SRC });
      throw gfe;
    }
    if (logger.isDebugEnabled())
      logger.debug("download(" + pathNameFile + "): fine metodo");
  }

  /**
   * Funzione che esegue il download di uno stream creato in memoria.
   *
   * @param nome da attribuire al file da scaricare
   * @param contenuto contenuto del file
   * @param response
   * @throws FileManagerException
   */
  public void download(String nomeFile, byte[] contenuto, HttpServletResponse response) throws FileManagerException {
    if (logger.isDebugEnabled()) logger.debug("download(" + nomeFile + ", byte[]): inizio metodo");

    if (nomeFile == null) {
      FileManagerException gfe = new FileManagerException(FileManagerException.CODICE_ERRORE_DOWNLOAD_FILE_ARG_NULL);
      gfe.setParametri(new Object[] {FileManager.ARGOMENTO_NOME_FILE_DST });
      throw gfe;
    }

    if (contenuto == null) {
      FileManagerException gfe = new FileManagerException(FileManagerException.CODICE_ERRORE_STREAM_NULL);
      throw gfe;
    }

    response.setContentType("application/octet-stream");
    response.setHeader("Content-Disposition", "attachment;filename=\"" + nomeFile + "\"");
    response.setContentLength(contenuto.length);
    try {
      OutputStream out = response.getOutputStream();
      out.write(contenuto);
      out.flush();
      out.close();
    } catch (IOException io) {
      FileManagerException gfe = null;
      gfe = new FileManagerException(FileManagerException.CODICE_ERRORE_KO_STREAM_DOWNLOAD, io);
      gfe.setParametri(new Object[] {nomeFile });
      throw gfe;
    }

    if (logger.isDebugEnabled()) logger.debug("download(" + nomeFile + ", byte[]): fine metodo");
  }

  /**
   * Funzione che esegue effettivamente il download di un file e che viene
   * richiamata dai metodi pubblici
   *
   * @param pathNameFile
   *        pathname completo del file di cui fare il download
   * @param response
   * @throws FileManagerException
   */
  private void downloadFile(String pathNameFile, HttpServletResponse response)
      throws FileManagerException {
    try {
      if (logger.isDebugEnabled())
        logger.debug("Download del file: " + pathNameFile);

      // Eseguo l'upload del file
      File f = new File(pathNameFile);
      if (!f.exists()) throw new FileNotFoundException();

      FileInputStream stream = new FileInputStream(f);

      int start = pathNameFile.lastIndexOf("/");
      if (start == -1) {
        start = pathNameFile.lastIndexOf("\\");
      }

      response.setContentType("application/octet-stream");
      response.setHeader("Content-Disposition", "attachment;filename=\""
          + pathNameFile.substring(start + 1)
          + "\"");
      OutputStream out = response.getOutputStream();
      response.addHeader("Content-Length", Long.toString(f.length()));

      byte[] buffer = new byte[2048];
      int bytesRead = stream.read(buffer);
      while (bytesRead >= 0) {
        if (bytesRead > 0) out.write(buffer, 0, bytesRead);
        bytesRead = stream.read(buffer);
      }
      stream.close();
      out.flush();
      out.close();
    } catch (FileNotFoundException fnf) {
      FileManagerException fm = null;
      if (fnf.getMessage() != null) {
        fm = new FileManagerException(
            FileManagerException.CODICE_ERRORE_FILE_ESISTENTE_NO_ACCESSIBILE,
            fnf);
        fm.setParametri(new Object[] { pathNameFile });
        throw fm;
      } else {
        fm = new FileManagerException(
            FileManagerException.CODICE_ERRORE_FILE_INESISTENTE, fnf);
        fm.setParametri(new Object[] { pathNameFile });
        throw fm;
      }
    } catch (IOException io) {
      FileManagerException gfe = null;
      gfe = new FileManagerException(
          FileManagerException.CODICE_ERRORE_KO_STREAM_DOWNLOAD, io);
      gfe.setParametri(new Object[] { pathNameFile });
      throw gfe;
    }
  }

  /**
   * Metodo per la copia di un file in due possbili casi: - da una directory ad
   * un'altra - nella stessa directory, ma con nome diverso Tutti gli argomenti
   * devono essere obbligatoriamente non nulli Emette eccezioni di tipo
   * FileManagerException nel caso in cui: - almeno un argomento sia null - il
   * file sorgente non esiste - il file destinazione esiste gia'
   *
   * @param pathFileSrc
   *        path del file sorgente
   * @param nomeFileSrc
   *        nome del file sorgente
   * @param pathFileDst
   *        path del file destinazione
   * @param nomeFileDst
   *        nome del file destinazione
   * @throws FileManagerException
   */
  public void copy(String pathFileSrc, String nomeFileSrc, String pathFileDst,
      String nomeFileDst) throws FileManagerException {
    if (logger.isDebugEnabled()) logger.debug("Copy: inizio metodo");

    // Tutti gli argomenti devono essere obbligatoriamente diversi da null
    if (pathFileSrc != null
        && pathFileDst != null
        && nomeFileSrc != null
        && nomeFileDst != null) {

      File fileSrc = new File(pathFileSrc + nomeFileSrc);
      File fileDst = new File(pathFileDst + nomeFileDst);

      FileInputStream fis = null;
      FileOutputStream fos = null;
      try {
        fis = new FileInputStream(fileSrc);
      } catch (FileNotFoundException fnf) {
        FileManagerException gfe = new FileManagerException(
            FileManagerException.CODICE_ERRORE_FILE_INESISTENTE, fnf);
        gfe.setParametri(new Object[] { pathFileSrc + nomeFileSrc });
        throw gfe;
      }

      if (fileDst.exists()) {
        FileManagerException gfe = new FileManagerException(
            FileManagerException.CODICE_ERRORE_FILE_ESISTENTE);
        gfe.setParametri(new Object[] { pathFileDst + nomeFileDst });
        throw gfe;
      }

      try {
        fos = new FileOutputStream(fileDst);
      } catch (FileNotFoundException fnf) {
        FileManagerException gfe = new FileManagerException(
            FileManagerException.CODICE_ERRORE_NO_PERMESSI_CARTELLA, fnf);
        gfe.setParametri(new Object[] { pathFileDst });
        throw gfe;
      }

      try {
        byte[] buf = new byte[1024];
        int i = 0;
        while ((i = fis.read(buf)) != -1) {
          fos.write(buf, 0, i);
        }
        fis.close();
        fos.close();
      } catch (IOException io) {
        FileManagerException gfe = null;
        gfe = new FileManagerException(
            FileManagerException.CODICE_ERRORE_COPIA_FILE, io);
        gfe.setParametri(new Object[] { pathFileSrc + nomeFileSrc,
            pathFileDst + nomeFileDst });
        throw gfe;
      }
    } else {
      FileManagerException gfe = new FileManagerException(
          FileManagerException.CODICE_ERRORE_COPIA_FILE_ARG_NULL);
      if (pathFileSrc == null)
        gfe.setParametri(new Object[] { FileManager.ARGOMENTO_PATH_FILE_SRC });
      else if (pathFileDst == null)
        gfe.setParametri(new Object[] { FileManager.ARGOMENTO_PATH_FILE_DST });
      else if (nomeFileSrc == null)
        gfe.setParametri(new Object[] { FileManager.ARGOMENTO_NOME_FILE_SRC });
      else
        gfe.setParametri(new Object[] { FileManager.ARGOMENTO_NOME_FILE_DST });

      throw gfe;
    }
    if (logger.isDebugEnabled()) logger.debug("Copy: fine metodo");
  }

  /**
   * Metodo per rinominare un file
   *
   * @param pathFileSrc
   *        path del file sorgente
   * @param nomeFileSrc
   *        nome del file sorgente
   * @param nomeFileDst
   *        nome del file destinazione
   * @throws FileManagerException
   */
  public void rename(String pathFileSrc, String nomeFileSrc, String nomeFileDst)
      throws FileManagerException {
    if (logger.isDebugEnabled()) logger.debug("Rename: inizio metodo");

    if (pathFileSrc != null && nomeFileSrc != null && nomeFileDst != null) {

      File fileSrc = new File(pathFileSrc + nomeFileSrc);
      File fileDst = new File(pathFileSrc + nomeFileDst);

      if (!fileSrc.exists()) {
        FileManagerException gfe = new FileManagerException(
            FileManagerException.CODICE_ERRORE_FILE_INESISTENTE);
        gfe.setParametri(new String[] { pathFileSrc + nomeFileSrc });
        throw gfe;
      }

      if (fileDst.exists()) {
        FileManagerException gfe = new FileManagerException(
            FileManagerException.CODICE_ERRORE_FILE_ESISTENTE);
        gfe.setParametri(new String[] { pathFileSrc + nomeFileDst });
        throw gfe;
      }

      try {
        boolean renameOK = fileSrc.renameTo(fileDst);
        if (renameOK) {
          if (logger.isDebugEnabled())
            logger.debug("Il file "
                + nomeFileSrc
                + " nella directory "
                + pathFileSrc
                + " e' stato rinominato in "
                + nomeFileDst);
        } else {
          FileManagerException gfe = new FileManagerException(
              FileManagerException.CODICE_ERRORE_RINOMINA_FILE);
          gfe.setParametri(new Object[] { pathFileSrc + nomeFileSrc,
              pathFileSrc + nomeFileDst });
          throw gfe;
        }
      } catch (NullPointerException e) {
        throw new FileManagerException(
            FileManagerException.CODICE_ERRORE_RINOMINA_FILE, e);
      }
    } else {
      FileManagerException gfe = new FileManagerException(
          FileManagerException.CODICE_ERRORE_RINOMINA_FILE_ARG_NULL);
      if (pathFileSrc == null)
        gfe.setParametri(new Object[] { FileManager.ARGOMENTO_PATH_FILE_SRC });
      else if (nomeFileSrc == null)
        gfe.setParametri(new Object[] { FileManager.ARGOMENTO_NOME_FILE_SRC });
      else
        gfe.setParametri(new Object[] { FileManager.ARGOMENTO_NOME_FILE_DST });

      throw gfe;
    }
    if (logger.isDebugEnabled()) logger.debug("RenameFile: fine metodo");
  }

  /**
   * Metodo per spostare un file da una directory ad un'altra, mantenendo il
   * nome orginale del file
   *
   * @param pathFileSrc
   *        path del file sorgente
   * @param pathFileDst
   *        path del file destinazione
   * @param nomeFileSrc
   *        nome del file sorgente
   * @throws FileManagerException
   */
  public void move(String pathFileSrc, String pathFileDst, String nomeFileSrc)
      throws FileManagerException {
    if (logger.isDebugEnabled()) logger.debug("Move: inizio metodo");

    if (pathFileSrc != null && pathFileDst != null && nomeFileSrc != null) {

      File fileSrc = new File(pathFileSrc + nomeFileSrc);
      File fileDst = new File(pathFileDst + nomeFileSrc);

      if (!fileSrc.exists()) {
        FileManagerException gfe = new FileManagerException(
            FileManagerException.CODICE_ERRORE_FILE_INESISTENTE);
        gfe.setParametri(new String[] { pathFileSrc + nomeFileSrc });
        throw gfe;
      }

      if (fileDst.exists()) {
        FileManagerException gfe = new FileManagerException(
            FileManagerException.CODICE_ERRORE_FILE_ESISTENTE);
        gfe.setParametri(new String[] { pathFileSrc + nomeFileSrc });
        throw gfe;
      }
      try {
        boolean renameOK = fileSrc.renameTo(fileDst);
        if (renameOK) {
          if (logger.isDebugEnabled())
            logger.debug("Il file "
                + nomeFileSrc
                + " dalla directory "
                + pathFileSrc
                + " e' stato spostato nella directory "
                + pathFileSrc);
        } else {
          FileManagerException gfe = new FileManagerException(
              FileManagerException.CODICE_ERRORE_SPOSTA_FILE);
          gfe.setParametri(new Object[] { nomeFileSrc, pathFileSrc, pathFileDst });
          throw gfe;
        }
      } catch (NullPointerException np) {
        throw new FileManagerException(
            FileManagerException.CODICE_ERRORE_RINOMINA_FILE, np);
      }
    } else {
      FileManagerException gfe = new FileManagerException(
          FileManagerException.CODICE_ERRORE_SPOSTA_FILE_ARG_NULL);
      if (pathFileSrc == null)
        gfe.setParametri(new Object[] { FileManager.ARGOMENTO_PATH_FILE_SRC });
      else if (pathFileDst == null)
        gfe.setParametri(new Object[] { FileManager.ARGOMENTO_PATH_FILE_DST });
      else
        gfe.setParametri(new Object[] { FileManager.ARGOMENTO_NOME_FILE_DST });

      throw gfe;
    }
    if (logger.isDebugEnabled()) logger.debug("Move: fine metodo");
  }

  /**
   * Metodo per spostare e rinominare un file da una directory ad un'altra
   *
   * @param pathFileSrc
   *        path del file sorgente
   * @param pathFileDst
   *        path del file destinazione
   * @param nomeFileSrc
   *        nome del file sorgente
   * @param nomeFileDst
   *        nome del file destinazione
   * @throws FileManagerException
   */
  public void moveRename(String pathFileSrc, String pathFileDst,
      String nomeFileSrc, String nomeFileDst) throws FileManagerException {
    if (logger.isDebugEnabled()) logger.debug("MoveRename: inizio metodo");

    if (pathFileSrc != null
        && pathFileDst != null
        && nomeFileSrc != null
        && nomeFileDst != null) {

      File fileSrc = new File(pathFileSrc + nomeFileSrc);
      File fileDst = new File(pathFileDst + nomeFileDst);

      if (!fileSrc.exists()) {
        FileManagerException gfe = new FileManagerException(
            FileManagerException.CODICE_ERRORE_FILE_INESISTENTE);
        gfe.setParametri(new String[] { pathFileSrc + nomeFileSrc });
        throw gfe;
      }
      if (fileDst.exists()) {
        FileManagerException gfe = new FileManagerException(
            FileManagerException.CODICE_ERRORE_FILE_ESISTENTE);
        gfe.setParametri(new String[] { pathFileDst + nomeFileDst });
        throw gfe;
      }
      try {
        boolean renameOK = fileSrc.renameTo(fileDst);
        if (renameOK) {
          if (logger.isDebugEnabled())
            logger.debug("Il file "
                + nomeFileSrc
                + " dalla directory "
                + pathFileSrc
                + " e' stato spostato nella directory "
                + pathFileDst
                + nomeFileDst);
        } else {
          FileManagerException gfe = new FileManagerException(
              FileManagerException.CODICE_ERRORE_SPOSTA_RINOMINA_FILE);
          gfe.setParametri(new Object[] { pathFileSrc + nomeFileSrc,
              pathFileDst + nomeFileDst });
          throw gfe;
        }
      } catch (NullPointerException np) {
        FileManagerException gfe = new FileManagerException(
            FileManagerException.CODICE_ERRORE_SPOSTA_RINOMINA_FILE, np);
        gfe.setParametri(new Object[] { pathFileSrc + nomeFileSrc,
            pathFileDst + nomeFileDst });
      }
    } else {
      FileManagerException gfe = new FileManagerException(
          FileManagerException.CODICE_ERRORE_SPOSTA_RINOMINA_FILE_ARG_NULL);
      if (pathFileSrc == null)
        gfe.setParametri(new Object[] { FileManager.ARGOMENTO_PATH_FILE_SRC });
      else if (pathFileDst == null)
        gfe.setParametri(new Object[] { FileManager.ARGOMENTO_PATH_FILE_DST });
      else if (nomeFileSrc == null)
        gfe.setParametri(new Object[] { FileManager.ARGOMENTO_NOME_FILE_SRC });
      else
        gfe.setParametri(new Object[] { FileManager.ARGOMENTO_NOME_FILE_DST });

      throw gfe;
    }
    if (logger.isDebugEnabled()) logger.debug("MoveRename: fine metodo");
  }
}