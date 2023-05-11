/*
 * Created on 17/mag/2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * avepackage it.eldasoft.gene.db.domain;
 */
package it.eldasoft.gene.db.domain;

import it.eldasoft.gene.db.domain.BlobFile;

/**
 * Bean da utilizzare in fase di lettura mediante framework iBatis di un campo
 * BLOB e del campo contenente l'estensione del file per conprenderne il formato
 * e poter consentire il corretto download del contenuto
 * 
 * @author Stefano.Sabbadin - Eldasoft S.p.A. Treviso
 * 
 * @since 1.5.0
 */
public class BlobFileWithExtension extends BlobFile {

  /**
   * UID
   */
  private static final long serialVersionUID = 1317983961108292130L;

  /**
   * estensione usata nel nome di file allegato nel campo blob (pdf, xls, zip,
   * txt, ...)
   */
  private String            estensione;

  public BlobFileWithExtension() {
    super();
    this.estensione = null;
  }

  /**
   * @return Ritorna estensione.
   */
  public String getEstensione() {
    return estensione;
  }

  /**
   * @param estensione
   *        estensione da settare internamente alla classe.
   */
  public void setEstensione(String estensione) {
    this.estensione = estensione;
  }

}
