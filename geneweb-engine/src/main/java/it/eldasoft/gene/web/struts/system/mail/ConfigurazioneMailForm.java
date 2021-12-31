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
package it.eldasoft.gene.web.struts.system.mail;

import it.eldasoft.gene.commons.web.struts.DispatchForm;
import it.eldasoft.gene.db.domain.system.ConfigurazioneMail;

import org.apache.commons.lang.StringUtils;

/**
 * Form di gestione della configurazione dei parametri di posta.
 *
 * @author Stefano.Sabbadin
 */
public class ConfigurazioneMailForm extends DispatchForm {

  /** UID. */
  private static final long serialVersionUID = -5375952400501521745L;

  /** Codice applicazione di riferimento della configurazione. */
  private String            codapp;
  /** ID della configurazione. */
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
  private boolean           debug;
  /** Mail del mittente. */
  private String            mail;
  /** Password eventuale in caso di autenticazione nel provider di posta. */
  private String            password;
  /**
   * Eventuale identificativo da utilizzare in fase di autenticazione, in coppia
   * alla password, se diversa dall'indirizzo di email.
   */
  private String            userId;
  /**
   * Numero massimo di Mb che possono essere allegati alla comunicazione.
   */
  private String            maxMb;
  /** Ritardo in secondi tra un invio ed il successivo in caso di uso massivo della funzione di invio mail. */
  private String            delay;


  public ConfigurazioneMailForm() {
    super();
    this.codapp = null;
    this.idcfg = null;
    this.nomein = null;
    this.server = null;
    this.porta = null;
    this.protocollo = null;
    this.timeout = null;
    this.debug = false;
    this.mail = null;
    this.password = null;
    this.userId = null;
    this.maxMb = null;
    this.delay = null;
  }

  /**
   * Prepara il form per la visualizzazione e per la
   * modifica con i dati estratti dalla business logic.
   *
   * @param cfg configurazione estratta dal DB
   */
  public ConfigurazioneMailForm(ConfigurazioneMail cfg) {
    super();
    this.codapp = cfg.getCodapp();
    this.idcfg = cfg.getIdcfg();
    this.nomein = cfg.getNomein();
    this.server = cfg.getServer();
    this.porta = cfg.getPorta();
    this.protocollo = cfg.getProtocollo();
    this.timeout = cfg.getTimeout();
    this.debug = "1".equals(cfg.getDebug());
    this.mail = cfg.getMailMitt();
    this.password = cfg.getPassword();
    this.userId = cfg.getUserId();
    this.maxMb = cfg.getDimTotAll();
    this.delay = cfg.getDelay();
  }

  /**
   * Prepara l'oggetto di business per essere salvato nel DB.
   *
   * @return bean per il salvataggio
   */
  public ConfigurazioneMail getDatiPerModel() {
    ConfigurazioneMail cfg = new ConfigurazioneMail();
    cfg.setCodapp(this.codapp);
    cfg.setIdcfg(this.idcfg);
    cfg.setNomein(this.nomein);
    cfg.setServer(this.server);
    cfg.setPorta(StringUtils.stripToNull(this.porta));
    cfg.setProtocollo(this.protocollo);
    cfg.setTimeout(StringUtils.stripToNull(this.timeout));
    cfg.setDebug(this.debug ? "1" : "0");
    cfg.setMailMitt(this.mail);
    cfg.setPassword(this.password);
    cfg.setUserId(StringUtils.stripToNull(this.userId));
    cfg.setDimTotAll(StringUtils.stripToNull(this.maxMb));
    cfg.setDelay(StringUtils.stripToNull(this.delay));

    return cfg;

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
    this.server = StringUtils.strip(server);
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
  public boolean getDebug() {
    return debug;
  }

  /**
   * @param debug debug da settare internamente alla classe.
   */
  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  /**
   * @return Ritorna mail.
   */
  public String getMail() {
    return mail;
  }

  /**
   * @param mail
   *        mail da settare internamente alla classe.
   */
  public void setMail(String mail) {
    this.mail = mail;
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
  public String getMaxMb() {
    return maxMb;
  }

  /**
   * @param maxMb
   *        maxMb da settare internamente alla classe.
   */
  public void setMaxMb(String maxMb) {
    this.maxMb = maxMb;
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
}
