/*
 * Created on 12/ott/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * avepackage it.eldasoft.gene.db.domain;
 */
package it.eldasoft.gene.db.domain;

import java.io.Serializable;

/**
 * Bean da utilizzare in fase di lettura mediante framework iBatis di un campo
 * BLOB
 *
 * @author Stefano.Sabbadin - Eldasoft S.p.A. Treviso
 *
 * @since 1.4.4
 */
public class BlobFile implements Serializable {

  /**
   * UID
   */
  private static final long serialVersionUID = 1433610022709943152L;
  private byte[]            stream;
  private String            nome;

  public BlobFile() {
    this.stream = null;
    this.nome = null;
  }

  /**
   * @return Ritorna stream.
   */
  public byte[] getStream() {
    return stream;
  }

  /**
   * @param stream
   *        stream da settare internamente alla classe.
   */
  public void setStream(byte[] stream) {
    this.stream = stream;
  }

  /**
   * @return Ritorna nome.
   */
  public String getNome() {
    return nome;
  }


  /**
   * @param nome nome da settare internamente alla classe.
   */
  public void setNome(String nome) {
    this.nome = nome;
  }

}
