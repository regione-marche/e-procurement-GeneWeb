/*
 * Created on 26-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.domain;

import java.io.Serializable;

/**
 * Contenitore dei dati per la gestione dei tab di dettaglio
 *
 * @author Stefano.Sabbadin
 */
public class GestioneTab implements Serializable {

  /**
   * UID.
   */
  private static final long serialVersionUID = 7927210330279738023L;

  /** tab attivo */
	private String tabAttivo;

	/** elenco dei tab non attivi ma selezionabili */
	private String[] tabSelezionabili;

	/**
	 * Costruttore che inizializza tutto a null
	 */
	public GestioneTab() {
		this.tabAttivo = null;
		this.tabSelezionabili = null;
	}

	/**
	 * @return Ritorna tabAttivo.
	 */
	public String getTabAttivo() {
		return tabAttivo;
	}

	/**
	 * @param tabAttivo
	 *            tabAttivo da settare internamente alla classe.
	 */
	public void setTabAttivo(String tabAttivo) {
		this.tabAttivo = tabAttivo;
	}

	/**
	 * @return Ritorna tabSelezionabili.
	 */
	public String[] getTabSelezionabili() {
		return tabSelezionabili;
	}

	/**
	 * @param tabSelezionabili
	 *            tabSelezionabili da settare internamente alla classe.
	 */
	public void setTabSelezionabili(String[] tabSelezionabili) {
		this.tabSelezionabili = tabSelezionabili;
	}

	/**
	 * Funzione che trasforma la lista dei tab selezionabili in uma stringa
	 * divisa da # e con all'inizio e alla fine il carattere #
	 *
	 * @return stringa con concatenate i tab selezionati da #
	 * @author marco.franceschin
	 */
	public String getListaTabSelezionabili() {
		// ************************************************************
		// Storia Modifiche:
		// Data Utente Descrizione
		// 30/08/2006 M.F. Prima Versione
		// ************************************************************

		StringBuffer returnLista = new StringBuffer();
		returnLista.append("#");
		if (this.tabSelezionabili != null) {
			for (int i = 0; i < this.tabSelezionabili.length; i++) {
				returnLista.append(this.tabSelezionabili[i]);
				returnLista.append("#");
			}
		}
		return returnLista.toString();
	}

}
