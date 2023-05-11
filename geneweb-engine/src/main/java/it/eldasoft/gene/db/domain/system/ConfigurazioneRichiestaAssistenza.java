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
 * Bean contenitore dei parametri di configurazione per la richiesta di
 * assistenza
 *
 * @author Marco.Perazzetta
 */
public class ConfigurazioneRichiestaAssistenza implements Serializable {

	/**
	 * UID.
	 */
	private static final long serialVersionUID = 5245288242314635566L;

	/**
	 * Codice applicazione di riferimento della configurazione.
	 */
	private String codapp;
	/**
	 * Modalita' di richiesta assistenza (0=disabilitato,1=servizio,2=mail).
	 */
	private String modo;
		/**
	 * Modalita' di richiesta assistenza (0=disabilitato,1=servizio,2=mail).
	 */
	private String oldModo;
	/**
	 * Lista delle tipolgie di oggetto che descrive la richiesta di assistenza.
	 */
	private String oggetto;
	/**
	 * L'indirizzo email per richiedere assistenza.
	 */
	private String mail;
	/**
	 * Indirizzo del servizio che gestisce le richieste di assistenza.
	 */
	private String servizioUrl;
	/**
	 * Username per l'autenticazione al servizio di gestione richieste di
	 * assistenza
	 */
	private String servizioUsr;
	/**
	 * Password per l'autenticazione al servizio di gestione richieste di
	 * assistenza
	 */
	private String servizioPwd;
	/**
	 * Password per l'autenticazione al servizio di gestione richieste di
	 * assistenza
	 */
	private String servizioFileSize;

	/**
	 * Inizializza tutti i campi a null.
	 */
	public ConfigurazioneRichiestaAssistenza() {
		this.codapp = null;
		this.modo = null;
		this.oggetto = null;
		this.mail = null;
		this.servizioUrl = null;
		this.servizioUsr = null;
		this.servizioPwd = null;
		this.servizioFileSize = null;
	}

	/**
	 * @return the codapp
	 */
	public String getCodapp() {
		return codapp;
	}

	/**
	 * @param codapp the codapp to set
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
	 * @param modo the modo to set
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
	 * @param oggetto the oggetto to set
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
	 * @param mail the mail to set
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
	 * @param servizioUrl the servizioUrl to set
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
	 * @param servizioUsr the servizioUsr to set
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
	 * @param servizioPwd the servizioPwd to set
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
	 * @param servizioFileSize the servizioFileSize to set
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
