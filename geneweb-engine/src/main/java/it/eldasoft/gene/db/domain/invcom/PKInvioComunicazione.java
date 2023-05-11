/*
 * Created on 26/set/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.invcom;

import java.io.Serializable;

/**
 * Bean che identifica la chiave di una comunicazione (tabella W_INVCOM).
 *
 * @author Stefano.Sabbadin
 * @since 2.0.14
 */
public class PKInvioComunicazione implements Serializable {

  /**
   * UID.
   */
  private static final long serialVersionUID = -5399871684346584884L;
  /** Identificativo del programma. */
  private String            idProgramma;
  /** Progressivo univoco della comunicazione per il programma. */
  private Long              idComunicazione;

  /**
   * @return Ritorna idProgramma.
   */
  public String getIdProgramma() {
    return idProgramma;
  }

  /**
   * @param idProgramma
   *        idProgramma da settare internamente alla classe.
   */
  public void setIdProgramma(String idProgramma) {
    this.idProgramma = idProgramma;
  }

  /**
   * @return Ritorna idComunicazione.
   */
  public Long getIdComunicazione() {
    return idComunicazione;
  }

  /**
   * @param idComunicazione
   *        idComunicazione da settare internamente alla classe.
   */
  public void setIdComunicazione(Long idComunicazione) {
    this.idComunicazione = idComunicazione;
  }

}
