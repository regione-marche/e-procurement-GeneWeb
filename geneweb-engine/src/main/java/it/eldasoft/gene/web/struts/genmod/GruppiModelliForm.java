/*
 * Created on 09-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.genmod;

import org.apache.struts.action.ActionForm;

public class GruppiModelliForm  extends ActionForm{

	/**
	 * UID
	 */
	private static final long serialVersionUID = -8965781395349571653L;
	
	/** Variabili dei dati **/
	private int idModello;
	private int idGruppo;
	private String codApp;
	/* Proprietà aggiunte per l'estrazione dei dati nella lista*/
	private String nomeGruppo;
	private String descrGruppo;
	private int associato;
	private Integer[] gruppiAssociati;
	/**
	 * @return Returns the associato.
	 */
	public int getAssociato() {
		return associato;
	}
	/**
	 * @param associato The associato to set.
	 */
	public void setAssociato(int associato) {
		this.associato = associato;
	}
	/**
	 * @return Returns the codApp.
	 */
	public String getCodApp() {
		return codApp;
	}
	/**
	 * @param codApp The codApp to set.
	 */
	public void setCodApp(String codApp) {
		this.codApp = codApp;
	}
	/**
	 * @return Returns the descrGruppo.
	 */
	public String getDescrGruppo() {
		return descrGruppo;
	}
	/**
	 * @param descrGruppo The descrGruppo to set.
	 */
	public void setDescrGruppo(String descrGruppo) {
		this.descrGruppo = descrGruppo;
	}
	/**
	 * @return Returns the idGruppo.
	 */
	public int getIdGruppo() {
		return idGruppo;
	}
	/**
	 * @param idGruppo The idGruppo to set.
	 */
	public void setIdGruppo(int idGruppo) {
		this.idGruppo = idGruppo;
	}
	/**
	 * @return Returns the idModello.
	 */
	public int getIdModello() {
		return idModello;
	}
	/**
	 * @param idModello The idModello to set.
	 */
	public void setIdModello(int idModello) {
		this.idModello = idModello;
	}
	/**
	 * @return Returns the nomeGruppo.
	 */
	public String getNomeGruppo() {
		return nomeGruppo;
	}
	/**
	 * @param nomeGruppo The nomeGruppo to set.
	 */
	public void setNomeGruppo(String nomeGruppo) {
		this.nomeGruppo = nomeGruppo;
	}
	/**
	 * @return Returns the gruppiAssociati.
	 */
	public Integer[] getGruppiAssociati() {
		return gruppiAssociati;
	}
	/**
	 * @param gruppiAssociati The gruppiAssociati to set.
	 */
	public void setGruppiAssociati(Integer[] gruppiAssociati) {
		this.gruppiAssociati = gruppiAssociati;
	}

}
