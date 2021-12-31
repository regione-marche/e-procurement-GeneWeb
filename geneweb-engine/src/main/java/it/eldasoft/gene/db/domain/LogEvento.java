/*
 * Created on 10/gen/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Bean per la gestione della tracciatura di eventi di log nella tabella
 * W_LOGEVENTI
 *
 * @author stefano.sabbadin
 * @since 1.5.4
 */
public class LogEvento {

  /** Livello di tracciatura info. */
  public static final int LIVELLO_INFO = 1;
  /** Livello di tracciatura errore non bloccante. */
  public static final int LIVELLO_WARNING = 2;
  /** Livello di tracciatura errore bloccante. */
  public static final int LIVELLO_ERROR = 3;

  /** Id dell'occorrenza */
  private int     id;
  /** Codice applicazione */
  private String  codApplicazione;
  /** Codice profilo utilizzato dall'utente */
  private String  codProfilo;
  /** Identificativo dell'utente connesso */
  private Integer idUtente;
  /** Indirizzo IP della macchina dell'utente */
  private String  ip;
  /** Data di inserimento dell'evento */
  private Date    data;
  /** Oggetto dell'evento */
  private String  oggEvento;
  /** livello dell'evento: 1 info, 2 errore, 3 avviso */
  private int     livEvento;
  /** Codice dell'evento */
  private String  codEvento;
  /** Descrizione dell'evento */
  private String  descr;
  /** Msg Errore dell'evento */
  private String  errmsg;


  public LogEvento() {
    this.id = 0;
    this.codApplicazione = null;
    this.codProfilo = null;
    this.idUtente = null;
    this.ip = null;
    this.setData(new Date());
    this.oggEvento = null;
    this.livEvento = 1;
    this.codEvento = null;
  }

  /**
   * @return Ritorna id.
   */
  public int getId() {
    return id;
  }

  /**
   * @param id
   *        id da settare internamente alla classe.
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * @return Ritorna codApplicazione.
   */
  public String getCodApplicazione() {
    return codApplicazione;
  }

  /**
   * @param codApplicazione
   *        codApplicazione da settare internamente alla classe.
   */
  public void setCodApplicazione(String codApplicazione) {
    this.codApplicazione = codApplicazione;
  }

  /**
   * @return Ritorna codProfilo.
   */
  public String getCodProfilo() {
    return codProfilo;
  }

  /**
   * @param codProfilo
   *        codProfilo da settare internamente alla classe.
   */
  public void setCodProfilo(String codProfilo) {
    this.codProfilo = codProfilo;
  }

  /**
   * @return Ritorna idUtente.
   */
  public Integer getIdUtente() {
    return idUtente;
  }

  /**
   * @param idUtente
   *        idUtente da settare internamente alla classe.
   */
  public void setIdUtente(Integer idUtente) {
    this.idUtente = idUtente;
  }

  /**
   * @return Ritorna ip.
   */
  public String getIp() {
    return ip;
  }

  /**
   * @param ip
   *        ip da settare internamente alla classe.
   */
  public void setIp(String ip) {
    this.ip = ip;
  }

  /**
   * @return Ritorna data.
   */
  public Date getData() {
    return data;
  }

  /**
   * @param data
   *        data da settare internamente alla classe.
   */
  public void setData(Date data) {
    this.data = data;
    if (data != null) {
      GregorianCalendar gc = new GregorianCalendar();
      gc.setTime(data);
    }
  }

  /**
   * @return Ritorna oggEvento.
   */
  public String getOggEvento() {
    return oggEvento;
  }

  /**
   * @param oggEvento
   *        oggEvento da settare internamente alla classe.
   */
  public void setOggEvento(String oggEvento) {
    this.oggEvento = oggEvento;
  }

  /**
   * @return Ritorna livEvento.
   */
  public int getLivEvento() {
    return livEvento;
  }

  /**
   * @param livEvento
   *        livEvento da settare internamente alla classe.
   */
  public void setLivEvento(int livEvento) {
    this.livEvento = livEvento;
  }

  /**
   * @return Ritorna codEvento.
   */
  public String getCodEvento() {
    return codEvento;
  }

  /**
   * @param codEvento
   *        codEvento da settare internamente alla classe.
   */
  public void setCodEvento(String codEvento) {
    this.codEvento = codEvento;
  }

  /**
   * @return Ritorna descr.
   */
  public String getDescr() {
    return descr;
  }

  /**
   * @param descr
   *         da settare internamente alla classe.
   */
  public void setDescr(String descr) {
    this.descr = descr;
  }
  /**
   * @return Ritorna errmsg.
   */
  public String getErrmsg() {
    return errmsg;
  }

  /**
   * @param errmsg
   *        errmsg da settare internamente alla classe.
   */
  public void setErrmsg(String errmsg) {
    this.errmsg = errmsg;
  }

}
