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

/**
 * Bean che identifica la chiave del destinatario comunicazione (tabella W_INVCOMDES).
 *
 * @author Stefano.Sabbadin
 * @since 2.0.14
 */
public class PKDestinatarioComunicazione extends PKInvioComunicazione {

  /**
   * UID.
   */
  private static final long serialVersionUID = -5399871684346584884L;

  /** Progressivo univoco della comunicazione per il programma. */
  private Long              idDestinatario;

  public PKDestinatarioComunicazione() {
    super();
  }

  public PKDestinatarioComunicazione(PKInvioComunicazione pk) {
    this.setIdProgramma(pk.getIdProgramma());
    this.setIdComunicazione(pk.getIdComunicazione());
    this.idDestinatario = null;
  }

  /**
   * @return Ritorna idDestinatario.
   */
  public Long getIdDestinatario() {
    return idDestinatario;
  }

  /**
   * @param idDestinatario
   *        idDestinatario da settare internamente alla classe.
   */
  public void setIdDestinatario(Long idDestinatario) {
    this.idDestinatario = idDestinatario;
  }

}
