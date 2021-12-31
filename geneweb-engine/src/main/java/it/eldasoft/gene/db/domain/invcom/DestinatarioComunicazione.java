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
import java.util.Date;

/**
 * Bean che identifica i dati del destinatario di una comunicazione.
 *
 * @author Stefano.Sabbadin
 * @since 2.0.14
 */
public class DestinatarioComunicazione implements Serializable {

  /**
   * UID.
   */
  private static final long           serialVersionUID = 6872309616669811113L;

  public static final int             INDIRIZZO_PEC    = 1;
  public static final int             INDIRIZZO_MAIL   = 2;
  public static final int             INDIRIZZO_FAX    = 3;

  /** Chiave. */
  private PKDestinatarioComunicazione pk;

  /** Eventuale nome entit&agrave; dell'archivio contenente il riferimento al soggetto destinatario. */
  private String                      entitaArchivioDestinatario;
  /** Chiave del soggetto destinatario preso dall'archivio. */
  private String                      codiceSoggettoArchivio;
  /** Indirizzo di destinazione. */
  private String                      indirizzo;
  /**
   * Tipologia.
   * <ul>
   * <li>1=PEC</li>
   * <li>2=MAIL</li>
   * <li>3=FAX</li>
   * </ul>
   */
  private Integer                     tipoIndirizzo;

  /** Data ora di invio. */
  private Date                        dataOraInvio;

  /** Data ora di invio in formato stringa. */
  private String                      dataOraInvioStringa;

  /** Stato della comunicazione. */
  private String                      statoComunicazione;

  /** Eventuale errore rilevato. */
  private String                      errore;

  /** Intestazione. */
  private String                      intestazione;

  public DestinatarioComunicazione() {
    this.pk = new PKDestinatarioComunicazione();
  }

  /**
   * @return Ritorna pk.
   */
  public PKDestinatarioComunicazione getPk() {
    return pk;
  }

  /**
   * @param pk
   *        pk da settare internamente alla classe.
   */
  public void setPk(PKDestinatarioComunicazione pk) {
    this.pk = pk;
  }

  /**
   * @return Ritorna entitaArchivioDestinatario.
   */
  public String getEntitaArchivioDestinatario() {
    return entitaArchivioDestinatario;
  }

  /**
   * @param entitaArchivioDestinatario
   *        entitaArchivioDestinatario da settare internamente alla classe.
   */
  public void setEntitaArchivioDestinatario(String entitaArchivioDestinatario) {
    this.entitaArchivioDestinatario = entitaArchivioDestinatario;
  }

  /**
   * @return Ritorna codiceSoggettoArchivio.
   */
  public String getCodiceSoggettoArchivio() {
    return codiceSoggettoArchivio;
  }

  /**
   * @param codiceSoggettoArchivio
   *        codiceSoggettoArchivio da settare internamente alla classe.
   */
  public void setCodiceSoggettoArchivio(String codiceSoggettoArchivio) {
    this.codiceSoggettoArchivio = codiceSoggettoArchivio;
  }

  /**
   * @return Ritorna indirizzo.
   */
  public String getIndirizzo() {
    return indirizzo;
  }

  /**
   * @param indirizzo
   *        indirizzo da settare internamente alla classe.
   */
  public void setIndirizzo(String indirizzo) {
    this.indirizzo = indirizzo;
  }

  /**
   * @return Ritorna tipoIndirizzo.
   */
  public Integer getTipoIndirizzo() {
    return tipoIndirizzo;
  }

  /**
   * @param tipoIndirizzo
   *        tipoIndirizzo da settare internamente alla classe.
   */
  public void setTipoIndirizzo(Integer tipoIndirizzo) {
    this.tipoIndirizzo = tipoIndirizzo;
  }

  /**
   * @return Ritorna dataOraInvio.
   */
  public Date getDataOraInvio() {
    return dataOraInvio;
  }

  /**
   * @param dataOraInvio
   *        dataOraInvio da settare internamente alla classe.
   */
  public void setDataOraInvio(Date dataOraInvio) {
    this.dataOraInvio = dataOraInvio;
  }

  /**
   * @return Ritorna dataOraInvioStringa.
   */
  public String getDataOraInvioStringa() {
    return dataOraInvioStringa;
  }

  /**
   * @param dataOraInvioStringa
   *        dataOraInvioStringa da settare internamente alla classe.
   */
  public void setDataOraInvioStringa(String dataOraInvioStringa) {
    this.dataOraInvioStringa = dataOraInvioStringa;
  }

  /**
   * @return Ritorna statoComunicazione.
   */
  public String getStatoComunicazione() {
    return statoComunicazione;
  }

  /**
   * @param statoComunicazione
   *        statoComunicazione da settare internamente alla classe.
   */
  public void setStatoComunicazione(String statoComunicazione) {
    this.statoComunicazione = statoComunicazione;
  }

  /**
   * @return Ritorna errore.
   */
  public String getErrore() {
    return errore;
  }

  /**
   * @param errore
   *        errore da settare internamente alla classe.
   */
  public void setErrore(String errore) {
    this.errore = errore;
  }

  /**
   * @return Ritorna intestazione.
   */
  public String getIntestazione() {
    return intestazione;
  }

  /**
   * @param intestazione
   *        intestazione da settare internamente alla classe.
   */
  public void setIntestazione(String intestazione) {
    this.intestazione = intestazione;
  }

}
