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
package it.eldasoft.gene.db.domain.genric;

import it.eldasoft.gene.commons.web.domain.GestioneOperatoreConfrontoStringa;

import java.io.Serializable;
import java.util.Vector;

/**
 * Bean contenente i parametri di filtro nella query per l'estrazione delle
 * ricerche da W_RICERCHE e W_GRPRIC
 *
 * @author Luca Giacomazzo
 */
public class TrovaRicerche implements Serializable {

  /** UID */
  private static final long serialVersionUID = 8607369490700073469L;

  private String            tipoRicerca;
  private String            nomeRicerca;
  private String            operatoreNomeRicerca;
  private String            escapeNomeRicerca;
  private String            descrizioneRicerca;
  private String            operatoreDescrizioneRicerca;
  private String            escapeDescrizioneRicerca;
  private String            disponibile;
  private String            idGruppo;
  private String            codiceApplicazione;
  private boolean           noCaseSensitive;
  private Vector<Integer>    famiglia;
  private String            personale;
  private String            owner;
  private String            profiloOwner;

  /**
   * Costruttore vuoto
   */
  public TrovaRicerche() {
    // ATTENZIONE: gli operatori nell'oggetto di trova di default sono impostati
    // a
    // "uguale" in modo da effettuare il match esatto

    this.tipoRicerca = null;
    this.nomeRicerca = null;
    this.operatoreNomeRicerca = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[0];
    this.escapeNomeRicerca = null;
    this.descrizioneRicerca = null;
    this.operatoreDescrizioneRicerca = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[0];
    this.escapeDescrizioneRicerca = null;
    this.disponibile = null;
    this.idGruppo = null;
    this.codiceApplicazione = null;
    this.noCaseSensitive = false;
    this.famiglia = null;
    this.personale = null;
    this.owner = null;
    this.profiloOwner = null;
  }

  /**
   * @return Ritorna disponibile.
   */
  public String getDisponibile() {
    return disponibile;
  }

  /**
   * @param disponibile
   *        disponibile da settare internamente alla classe.
   */
  public void setDisponibile(String disponibile) {
    this.disponibile = disponibile;
  }

  /**
   * @return Ritorna idGruppo.
   */
  public String getIdGruppo() {
    return idGruppo;
  }

  /**
   * @param idGruppo
   *        idGruppo da settare internamente alla classe.
   */
  public void setIdGruppo(String idGruppo) {
    this.idGruppo = idGruppo;
  }

  /**
   * @return Ritorna tipoRicerca.
   */
  public String getTipoRicerca() {
    return tipoRicerca;
  }

  /**
   * @param tipoRicerca
   *        tipoRicerca da settare internamente alla classe.
   */
  public void setTipoRicerca(String idTipoRicerca) {
    this.tipoRicerca = idTipoRicerca;
  }

  /**
   * @return Ritorna nomeRicerca.
   */
  public String getNomeRicerca() {
    return nomeRicerca;
  }

  /**
   * @param nomeRicerca
   *        nomeRicerca da settare internamente alla classe.
   */
  public void setNomeRicerca(String nomeRicerca) {
    this.nomeRicerca = nomeRicerca;
  }

  /**
   * @return Ritorna operatoreNomeRicerca.
   */
  public String getOperatoreNomeRicerca() {
    return operatoreNomeRicerca;
  }

  /**
   * @param operatoreNomeRicerca
   *        operatoreNomeRicerca da settare internamente alla classe.
   */
  public void setOperatoreNomeRicerca(String operatoreNomeRicerca) {
    this.operatoreNomeRicerca = operatoreNomeRicerca;
  }

  /**
   * @return Ritorna escapeNomeRicerca.
   */
  public String getEscapeNomeRicerca() {
    return escapeNomeRicerca;
  }

  /**
   * @param escapeNomeRicerca
   *        escapeNomeRicerca da settare internamente alla classe.
   */
  public void setEscapeNomeRicerca(String escapeNomeRicerca) {
    this.escapeNomeRicerca = escapeNomeRicerca;
  }

  /**
   * @return Ritorna codiceApplicazione.
   */
  public String getCodiceApplicazione() {
    return codiceApplicazione;
  }

  /**
   * @param codiceApplicazione
   *        codiceApplicazione da settare internamente alla classe.
   */
  public void setCodiceApplicazione(String codiceApplicazione) {
    this.codiceApplicazione = codiceApplicazione;
  }

  /**
   * @return Ritorna noCaseSensitive.
   */
  public boolean getNoCaseSensitive() {
    return noCaseSensitive;
  }

  /**
   * @return Ritorna noCaseSensitive.
   */
  public boolean isNoCaseSensitive() {
    return noCaseSensitive;
  }

  /**
   * @param noCaseSensitive
   *        noCaseSensitive da settare internamente alla classe.
   */
  public void setNoCaseSensitive(boolean noCaseSensitive) {
    this.noCaseSensitive = noCaseSensitive;
  }

  /**
   * @return Ritorna descrizione.
   */
  public String getDescrizioneRicerca() {
    return descrizioneRicerca;
  }

  /**
   * @param descrizione
   *        descrizione da settare internamente alla classe.
   */
  public void setDescrizioneRicerca(String descrizione) {
    this.descrizioneRicerca = descrizione;
  }

  /**
   * @return Ritorna operatoreDescrizioneRicerca.
   */
  public String getOperatoreDescrizioneRicerca() {
    return operatoreDescrizioneRicerca;
  }

  /**
   * @param operatoreDescrizioneRicerca
   *        operatoreDescrizioneRicerca da settare internamente alla classe.
   */
  public void setOperatoreDescrizioneRicerca(String operatoreDescrizioneRicerca) {
    this.operatoreDescrizioneRicerca = operatoreDescrizioneRicerca;
  }

  /**
   * @return Ritorna escapeDescrizioneRicerca.
   */
  public String getEscapeDescrizioneRicerca() {
    return escapeDescrizioneRicerca;
  }

  /**
   * @param escapeDescrizioneRicerca
   *        escapeDescrizioneRicerca da settare internamente alla classe.
   */
  public void setEscapeDescrizioneRicerca(String escapeDescrizioneRicerca) {
    this.escapeDescrizioneRicerca = escapeDescrizioneRicerca;
  }

  /**
   * @return Returns the famiglia.
   */
  public Vector<Integer> getFamiglia() {
    return famiglia;
  }

  /**
   * @param famiglia
   *        The famiglia to set.
   */
  public void setFamiglia(Vector<Integer> famiglia) {
    this.famiglia = famiglia;
  }

  /**
   * @return Ritorna personale.
   */
  public String getPersonale() {
    return personale;
  }

  /**
   * @param personale
   *        personale da settare internamente alla classe.
   */
  public void setPersonale(String personale) {
    this.personale = personale;
  }

  /**
   * @return Ritorna owner.
   */
  public String getOwner() {
    return owner;
  }

  /**
   * @param owner
   *        owner da settare internamente alla classe.
   */
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /**
   * @return Ritorna codiceprofiloAttivo.
   */
  public String getProfiloOwner() {
    return profiloOwner;
  }

  /**
   * @param codiceprofiloAttivo
   *        codiceprofiloAttivo da settare internamente alla classe.
   */
  public void setProfiloOwner(String codiceprofiloAttivo) {
    this.profiloOwner = codiceprofiloAttivo;
  }

}