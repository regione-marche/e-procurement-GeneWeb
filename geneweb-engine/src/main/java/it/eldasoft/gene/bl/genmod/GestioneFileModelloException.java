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
package it.eldasoft.gene.bl.genmod;

import java.io.IOException;

public class GestioneFileModelloException extends IOException {

  /**
   * UID
   */
  private static final long  serialVersionUID                  = 3546170096751206948L;
  /** Codici di errore */
  public static final String ERROR_FILE_GIA_ESISTENTE          = "filegiaesistente";
  public static final String ERROR_FILE_INESISTENTE            = "fileinesistente";
  public static final String ERROR_FILE_ESTENSIONE_NON_GESTITA = "exterr";
  public static final String ERROR_FILE_VUOTO                  = "fileVuoto";
  
  private String             codiceErrore;

  /**
   * Costruttore standard
   * 
   * @param msg
   *        Messaggio
   */
  public GestioneFileModelloException(String msg, String codice) {
    super(msg);
    this.codiceErrore = codice;
  }

  /**
   * @return Returns the codiceErrore.
   */
  public String getCodiceErrore() {
    if (codiceErrore == null) return "";
    return codiceErrore;
  }

  /**
   * @param codiceErrore
   *        The codiceErrore to set.
   */
  public void setCodiceErrore(String codiceErrore) {
    this.codiceErrore = codiceErrore;
  }

}
