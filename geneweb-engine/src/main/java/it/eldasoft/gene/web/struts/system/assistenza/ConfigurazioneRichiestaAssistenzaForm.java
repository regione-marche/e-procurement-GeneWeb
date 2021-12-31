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
package it.eldasoft.gene.web.struts.system.assistenza;

import it.eldasoft.gene.commons.web.struts.DispatchForm;
import it.eldasoft.gene.db.domain.system.ConfigurazioneRichiestaAssistenza;

/**
 * Form di gestione della configurazione della richiesta di assistenza.
 *
 * @author Marco.Perazzetta
 */
public class ConfigurazioneRichiestaAssistenzaForm extends DispatchForm {

  /**
   * UID.
   */
  private static final long serialVersionUID = -5675152400501521945L;

  /**
   * Codice applicazione di riferimento della configurazione.
   */
  private String            codapp;
  /**
   * La modalita' di richiesta assistenza.
   */
  private String            modo;
	/**
   * La modalita' di richiesta assistenza.
   */
  private String            oldModo;
  /**
   * Lista di oggetti possibili della richiesta.
   */
  private String            oggetto;
  /**
   * Indirizzo mail a cui recapitare la richiesta.
   */
  private String            mail;
  /**
   * Indirizzo del servizio di richiesta assistenza.
   */
  private String            servizioUrl;
  /**
   * Username per l'autenticazione al servizio.
   */
  private String            servizioUsr;
  /**
   * Password per l'autenticazione al servizio.
   */
  private String            servizioPwd;
  /**
   * Massima dimensione di un file allegato alla segnalazione
   */
  private String            servizioFileSize;

  public ConfigurazioneRichiestaAssistenzaForm() {
    super();
    this.codapp = null;
    this.modo = null;
    this.oggetto = null;
    this.mail = null;
    this.servizioUrl = null;
    this.servizioUsr = null;
    this.servizioPwd = null;
    this.servizioFileSize = null;
		this.oldModo = null;
  }

  /**
   * Prepara il form per la visualizzazione e per la modifica con i dati estratti dalla business logic.
   *
   * @param cfg
   *        configurazione estratta dal DB
   */
  public ConfigurazioneRichiestaAssistenzaForm(ConfigurazioneRichiestaAssistenza cfg) {
    super();
    this.codapp = cfg.getCodapp();
    this.modo = cfg.getModo();
    this.oggetto = cfg.getOggetto();
    this.mail = cfg.getMail();
    this.servizioUrl = cfg.getServizioUrl();
    this.servizioUsr = cfg.getServizioUsr();
    this.servizioPwd = cfg.getServizioPwd();
    this.servizioFileSize = cfg.getServizioFileSize();
		this.oldModo = cfg.getOldModo();
  }

  /**
   * Prepara l'oggetto di business per essere salvato nel DB.
   *
   * @return bean per il salvataggio
   */
  public ConfigurazioneRichiestaAssistenza getDatiPerModel() {
    ConfigurazioneRichiestaAssistenza cfg = new ConfigurazioneRichiestaAssistenza();
    cfg.setCodapp(this.codapp);
    cfg.setModo(this.getModo());
    cfg.setOggetto(this.getOggetto());
    cfg.setMail(this.getMail());
    cfg.setServizioUrl(this.getServizioUrl());
    cfg.setServizioUsr(this.getServizioUsr());
    cfg.setServizioPwd(this.getServizioPwd());
    cfg.setServizioFileSize(this.getServizioFileSize());
		cfg.setOldModo(this.getOldModo());
    return cfg;
  }

  /**
   * @return Ritorna codapp.
   */
  public String getCodapp() {
    return codapp;
  }

  /**
   * @param codapp
   *        codapp da settare internamente alla classe.
   */
  public void setCodapp(String codapp) {
    this.codapp = codapp;
  }

  /**
   * @return the modo
   */
  public String getModo() {
    return modo;
  }

  /**
   * @param modo
   *        the modo to set
   */
  public void setModo(String modo) {
    this.modo = modo;
  }

  /**
   * @return the oggetto
   */
  public String getOggetto() {
    return oggetto;
  }

  /**
   * @param oggetto
   *        the oggetto to set
   */
  public void setOggetto(String oggetto) {
    this.oggetto = oggetto;
  }

  /**
   * @return the mail
   */
  public String getMail() {
    return mail;
  }

  /**
   * @param mail
   *        the mail to set
   */
  public void setMail(String mail) {
    this.mail = mail;
  }

  /**
   * @return the servizioUrl
   */
  public String getServizioUrl() {
    return servizioUrl;
  }

  /**
   * @param servizioUrl
   *        the servizioUrl to set
   */
  public void setServizioUrl(String servizioUrl) {
    this.servizioUrl = servizioUrl;
  }

  /**
   * @return the servizioUsr
   */
  public String getServizioUsr() {
    return servizioUsr;
  }

  /**
   * @param servizioUsr
   *        the servizioUsr to set
   */
  public void setServizioUsr(String servizioUsr) {
    this.servizioUsr = servizioUsr;
  }

  /**
   * @return the servizioPwd
   */
  public String getServizioPwd() {
    return servizioPwd;
  }

  /**
   * @param servizioPwd
   *        the servizioPwd to set
   */
  public void setServizioPwd(String servizioPwd) {
    this.servizioPwd = servizioPwd;
  }

  /**
   * @return the servizioFileSize
   */
  public String getServizioFileSize() {
    return servizioFileSize;
  }

  /**
   * @param servizioFileSize
   *        the servizioFileSize to set
   */
  public void setServizioFileSize(String servizioFileSize) {
    this.servizioFileSize = servizioFileSize;
  }

	/**
	 * @return the oldModo
	 */
	public String getOldModo() {
		return oldModo;
	}

	/**
	 * @param oldModo the oldModo to set
	 */
	public void setOldModo(String oldModo) {
		this.oldModo = oldModo;
	}
}
