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
 * Created by Marco Franceschin
 */
package it.eldasoft.gene.web.struts.docass;

import it.eldasoft.utils.eccezioni.EldaCheckedException;

public class GestioneFileDocumentiAssociatiException extends EldaCheckedException {

  /**   UID   */
  private static final long serialVersionUID = -6123156762792579090L;

  private static final String FAMIGLIA = "GESTIONE_DOCASS";

  /** Codici di errore */
  public static final String ERROR_FILE_ESISTENTE       = "FILE_ESISTENTE";
  public static final String ERROR_INASPETTATO          = "ERRORE_INASPETTATO";

  public static final String ERROR_UPLOAD_FILE_VUOTO    = "UPLOAD_FILE_VUOTO";
  public static final String ERROR_UPLOAD_RENAMING_KO   = "UPLOAD_RENAMING_KO";
  public static final String ERROR_UPLOAD_FILE_KO       = "UPLOAD_FILE_KO";
  public static final String ERROR_ESTENSIONE_NON_AMMESSA = "ESTENSIONE_NON_AMMESSA";
  public static final String ERROR_UPLOAD_DB_KO         = "UPLOAD_DB_KO";
  public static final String ERROR_UPLOAD_DELETE_TMP_KO = "UPLOAD_DELETE_TMP_KO";
  public static final String ERROR_INASPETTATO_UPLOAD   = "ERRORE_INASPETTATO_UPLOAD";

  public static final String ERROR_DOWNLOAD_DEFAULT_PATH_KO = "DOWNLOAD_DEFAULT_PATH_KO";

  public static final String CODICE_ERRORE_KO_CANCELLA_FILE_PIU_OCCORRENZE = "DELETE_FILE_PIU_OCCORRENZE";

  @Override
  public String getFamiglia() {
    return GestioneFileDocumentiAssociatiException.FAMIGLIA;
  }

  public GestioneFileDocumentiAssociatiException(String codiceErrore) {
    super(codiceErrore);
  }

  public GestioneFileDocumentiAssociatiException(String codiceErrore, Throwable e) {
    super(codiceErrore, e);
  }

  public GestioneFileDocumentiAssociatiException(String codiceErrore, String message) {
    super(codiceErrore, message);
  }

  public GestioneFileDocumentiAssociatiException(String codiceErrore, String message, Throwable e) {
    super(codiceErrore, message, e);
  }

}