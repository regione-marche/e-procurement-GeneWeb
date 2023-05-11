/*
 * Created on 04-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.console.db.domain.schedric;

import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;

import java.io.Serializable;

/**
 * Bean contenente i parametri di filtro nella query per l'estrazione delle
 * schedulazioni delle ricerche da W_SCHEDRIC E W_RICERCHE
 *
 * @author Luca Giacomazzo
 */
public class TrovaSchedRic implements Serializable {

  /** UID */
  private static final long serialVersionUID = 8607369490700073469L;

  private String            tipo;
  private String            attivo;
  private String            nome;
  private String            operatoreNome;
  private String            escapeNome;
  private String            idRicerca;
  private String            nomeRicerca;
  private String            owner;
  private String            esecutore;
  private boolean           noCaseSensitive;
  private String            profiloOwner;
  private String            codiceApplicazione;

  /**
   * Costruttore vuoto
   */
  public TrovaSchedRic() {
    this.tipo = null;
    this.attivo = null;
    this.nome = null;
    this.operatoreNome = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[0];
    this.escapeNome = null;
    this.idRicerca = "";
    this.nomeRicerca = null;
    this.owner = "";
    this.esecutore = "";
    this.noCaseSensitive = false;
    this.profiloOwner = null;
    this.codiceApplicazione = null;
  }

  /**
   * @return Returns the noCaseSensitive.
   */
  public boolean isNoCaseSensitive() {
    return noCaseSensitive;
  }

  /**
   * @param noCaseSensitive
   *        The noCaseSensitive to set.
   */
  public void setNoCaseSensitive(boolean noCaseSensitive) {
    this.noCaseSensitive = noCaseSensitive;
  }

  /**
   * @return Returns the attivo.
   */
  public String getAttivo() {
    return attivo;
  }

  /**
   * @param attivo
   *        The attivo to set.
   */
  public void setAttivo(String attivo) {
    this.attivo = attivo;
  }

  /**
   * @return Returns the nome.
   */
  public String getNome() {
    return nome;
  }

  /**
   * @param nome
   *        The nome to set.
   */
  public void setNome(String nome) {
    this.nome = nome;
  }

  /**
   * @return Ritorna operatoreNome.
   */
  public String getOperatoreNome() {
    return operatoreNome;
  }

  /**
   * @param operatoreNome
   *        operatoreNome da settare internamente alla classe.
   */
  public void setOperatoreNome(String operatoreNome) {
    this.operatoreNome = operatoreNome;
  }

  /**
   * @return Ritorna escapeNome.
   */
  public String getEscapeNome() {
    return escapeNome;
  }

  /**
   * @param escapeNome
   *        escapeNome da settare internamente alla classe.
   */
  public void setEscapeNome(String escapeNome) {
    this.escapeNome = escapeNome;
  }

  /**
   * @return Returns the owner.
   */
  public String getOwner() {
    return owner;
  }

  /**
   * @param owner
   *        The owner to set.
   */
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /**
   * @return Returns the tipo.
   */
  public String getTipo() {
    return tipo;
  }

  /**
   * @param tipo
   *        The tipo to set.
   */
  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  /**
   * @return Returns the idRicerca.
   */
  public String getIdRicerca() {
    return idRicerca;
  }

  /**
   * @param idRicerca
   *        The idRicerca to set.
   */
  public void setIdRicerca(String idRicerca) {
    this.idRicerca = idRicerca;
  }

  /**
   * @return Returns the nomeRicerca.
   */
  public String getNomeRicerca() {
    return nomeRicerca;
  }

  /**
   * @param nomeRicerca
   *        The nomeRicerca to set.
   */
  public void setNomeRicerca(String nomeRicerca) {
    this.nomeRicerca = nomeRicerca;
  }

  /**
   * @return Ritorna profiloOwner.
   */
  public String getProfiloOwner() {
    return profiloOwner;
  }

  /**
   * @param profiloOwner
   *        profiloOwner da settare internamente alla classe.
   */
  public void setProfiloOwner(String profiloOwner) {
    this.profiloOwner = profiloOwner;
  }

  /**
   * @return Ritorna esecutore.
   */
  public String getEsecutore() {
    return esecutore;
  }

  /**
   * @param esecutore
   *        esecutore da settare internamente alla classe.
   */
  public void setEsecutore(String esecutore) {
    this.esecutore = esecutore;
  }

  /**
   * @return Ritorna codiceApplicazione.
   */
  public String getCodiceApplicazione() {
    return codiceApplicazione;
  }

  /**
   * @param codiceApplicazione codiceApplicazione da settare internamente alla classe.
   */
  public void setCodiceApplicazione(String codiceApplicazione) {
    this.codiceApplicazione = codiceApplicazione;
  }

}