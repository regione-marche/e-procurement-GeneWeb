/*
 * Created on 04-09-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 *
 * Created by Luca Giacomazzo
 */
package it.eldasoft.gene.bl;

import it.eldasoft.utils.eccezioni.EldaCheckedException;

public class FileManagerException extends EldaCheckedException {

  /**   UID   */
  private static final long  serialVersionUID = 3546170096751206948L;

  /** Famiglia di eccezioni legate alla gestione del file system */
  private static final String FAMIGLIA        = "FILE_MANAGER";

  /** Codici di errore */
  public static final String CODICE_ERRORE_CANCELLAZIONE_FILE            = "DELETE_FILE";
  public static final String CODICE_ERRORE_CANCELLAZIONE_FILE_ARG_NULL   = "DELETE_FILE_ARG_NULL";
  public static final String CODICE_ERRORE_FILE_ESISTENTE_NO_ACCESSIBILE = "FILE_ESISTENTE_NO_ACCESS";
  public static final String CODICE_ERRORE_NO_PERMESSI_CARTELLA          = "NO_PERMESSI_CARTELLA";
  public static final String CODICE_ERRORE_FILE_INESISTENTE              = "FILE_INESISTENTE";
  public static final String CODICE_ERRORE_NO_ACCESS_FILE                = "NO_ACCESS_FILE";
  public static final String CODICE_ERRORE_KO_STREAM_DOWNLOAD            = "KO_STREAM_DOWNLOAD";
  public static final String CODICE_ERRORE_DOWNLOAD_FILE_ARG_NULL        = "DOWNLOAD_FILE_ARG_NULL";
  public static final String CODICE_ERRORE_FILE_ESISTENTE                = "FILE_ESISTENTE";
  public static final String CODICE_ERRORE_COPIA_FILE                    = "COPY_FILE";
  public static final String CODICE_ERRORE_COPIA_FILE_ARG_NULL           = "COPY_FILE_ARG_NULL";
  public static final String CODICE_ERRORE_RINOMINA_FILE                 = "RENAME_FILE";
  public static final String CODICE_ERRORE_RINOMINA_FILE_ARG_NULL        = "RENAME_FILE_ARG_NULL";
  public static final String CODICE_ERRORE_SPOSTA_FILE                   = "MOVE_FILE";
  public static final String CODICE_ERRORE_SPOSTA_FILE_ARG_NULL          = "MOVE_FILE_ARG_NULL";
  public static final String CODICE_ERRORE_SPOSTA_RINOMINA_FILE          = "MOVE_RENAME_FILE";
  public static final String CODICE_ERRORE_SPOSTA_RINOMINA_FILE_ARG_NULL = "MOVE_RENAME_FILE_ARG_NULL";
  public static final String CODICE_ERRORE_STREAM_NULL                   = "STREAM_NULL";

  /**
   * @see it.eldasoft.utils.eccezioni.EldaCheckedException#getFamiglia()
   */
  @Override
  public String getFamiglia() {
    return FileManagerException.FAMIGLIA;
  }

  public FileManagerException(String codiceErrore) {
    super(codiceErrore);
  }

  public FileManagerException(String codiceErrore, Throwable e) {
    super(codiceErrore, e);
  }

  public FileManagerException(String codiceErrore, String message) {
    super(codiceErrore, message);
  }

  public FileManagerException(String codiceErrore, String message, Throwable e) {
    super(codiceErrore, message, e);
  }

}