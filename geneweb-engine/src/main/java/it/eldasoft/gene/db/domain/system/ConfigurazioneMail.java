/*
 * Created on 01/feb/2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.system;

import java.io.Serializable;

/**
 * Bean contenitore dei parametri di configurazione della posta elettronica.
 *
 * @author Stefano.Sabbadin
 */
public class ConfigurazioneMail implements Serializable {

  /**
   * UID.
   */
  private static final long serialVersionUID = -1000166671857355784L;

  /** Codice applicazione di riferimento della configurazione. */
  private String            codapp;
  /** Id di riferimento della configurazione. */
  private String            idcfg;
  /** Descrizione id della configurazione. */
  private String            nomein;
  /** Nome del server MAIL o indirizzo IP. */
  private String            server;
  /** Porta di ascolto del server MAIL. */
  private String            porta;
  /** Protocollo di comunicazione con il server di posta. */
  private String            protocollo;
  /** Timeout di attesa connessione e risposta del server, in millisecondi. */
  private String            timeout;
  /** Abilita la modalit&agrave; di debug. */
  private String            debug;
  /** Mail del mittente. */
  private String            mailMitt;
  /** Password eventuale in caso di autenticazione nel provider di posta. */
  private String            password;
  /**
   * Eventuale identificativo da utilizzare in fase di autenticazione, in coppia
   * alla password, se diversa dall'indirizzo di email.
   */
  private String            userId;
  /**
   *Numero massimo di Mb che possono essere allegati alla comunicazione.
   */
  private String            dimTotAll;
  /** Ritardo in secondi tra un invio ed il successivo in caso di uso massivo della funzione di invio mail. */
  private String            delay;

  /**
   * Server di posta IMAP
   */
  private String            serverIMAP;
  
  /**
   * Porta del server di posta IMAP
   */
  private String            portaIMAP;
  
  /**
   * Protocollo IMAP utilizzato (Standard o SSL)
   */
  //private String            protocolloIMAP;
  
  /** Inizializza tutti i campi a null.  */
  public ConfigurazioneMail() {
    this.codapp = null;
    this.idcfg = null;
    this.nomein = null;
    this.server = null;
    this.porta = null;
    this.protocollo = null;
    this.timeout = null;
    this.debug = null;
    this.mailMitt = null;
    this.password = null;
    this.userId = null;
    this.dimTotAll = null;
    this.delay = null;
    this.serverIMAP = null;
    this.portaIMAP = null;
//    this.protocolloIMAP = null;
  }

  /**
   * @return Ritorna codapp.
   */
  public String getCodapp() {
    return codapp;
  }

  /**
   * @param codapp codapp da settare internamente alla classe.
   */
  public void setCodapp(String codapp) {
    this.codapp = codapp;
  }

  /**
   * @return Ritorna idcfg.
   */
  public String getIdcfg() {
    return idcfg;
  }

  /**
   * @param idcfg da settare internamente alla classe.
   */
  public void setIdcfg(String idcfg) {
    this.idcfg = idcfg;
  }

  /**
   * @return Ritorna nomein.
   */
  public String getNomein() {
    return nomein;
  }

  /**
   * @param nomein da settare internamente alla classe.
   */
  public void setNomein(String nomein) {
    this.nomein = nomein;
  }

  /**
   * @return Ritorna server.
   */
  public String getServer() {
    return server;
  }

  /**
   * @param server
   *        server da settare internamente alla classe.
   */
  public void setServer(String server) {
    this.server = server;
  }

  /**
   * @return Ritorna porta.
   */
  public String getPorta() {
    return porta;
  }

  /**
   * @param porta
   *        porta da settare internamente alla classe.
   */
  public void setPorta(String porta) {
    this.porta = porta;
  }

  /**
   * @return Ritorna protocollo.
   */
  public String getProtocollo() {
    return protocollo;
  }

  /**
   * @param protocollo
   *        protocollo da settare internamente alla classe.
   */
  public void setProtocollo(String protocollo) {
    this.protocollo = protocollo;
  }

  /**
   * @return Ritorna timeout.
   */
  public String getTimeout() {
    return timeout;
  }

  /**
   * @param timeout timeout da settare internamente alla classe.
   */
  public void setTimeout(String timeout) {
    this.timeout = timeout;
  }

  /**
   * @return Ritorna debug.
   */
  public String getDebug() {
    return debug;
  }

  /**
   * @param debug debug da settare internamente alla classe.
   */
  public void setDebug(String debug) {
    this.debug = debug;
  }

  /**
   * @return Ritorna mail.
   */
  public String getMailMitt() {
    return mailMitt;
  }

  /**
   * @param mail
   *        mail da settare internamente alla classe.
   */
  public void setMailMitt(String mailMitt) {
    this.mailMitt = mailMitt;
  }

  /**
   * @return Ritorna password.
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password
   *        password da settare internamente alla classe.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * @return Ritorna userId.
   */
  public String getUserId() {
    return userId;
  }

  /**
   * @param userId
   *        userId da settare internamente alla classe.
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * @return Ritorna maxMb.
   */
  public String getDimTotAll() {
    return dimTotAll;
  }

  /**
   * @param maxMb
   *        maxMb da settare internamente alla classe.
   */
  public void setDimTotAll(String dimTotAll) {
    this.dimTotAll = dimTotAll;
  }

  /**
   * @return Ritorna delay.
   */
  public String getDelay() {
    return delay;
  }

  /**
   * @param delay delay da settare internamente alla classe.
   */
  public void setDelay(String delay) {
    this.delay = delay;
  }
  
  public String getServerIMAP() {
    return serverIMAP;
  }
  
  public void setServerIMAP(String serverIMAP) {
    this.serverIMAP = serverIMAP;
  }
  
  public String getPortaIMAP() {
    return portaIMAP;
  }
  
  public void setPortaIMAP(String portaIMAP) {
    this.portaIMAP = portaIMAP;
  }
  
  public boolean isIMAPConfigurato() {
    return getServerIMAP() != null && !getServerIMAP().isEmpty();
  }

}
