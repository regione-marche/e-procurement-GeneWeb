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
import java.util.Date;

/**
 * Bean contenente i parametri di filtro nella query per l'estrazione della coda
 * delle schedulazioni delle ricerche da W_SCHEDRIC, W_CODASCHED, W_RICERCHE
 *
 * @author Luca Giacomazzo
 */
public class TrovaCodaSched implements Serializable {

  /** UID */
  private static final long serialVersionUID = 8607369490700073469L;

  private String            stato;
  private String            nome;
  private String            idSchedRic;
  private String            idRicerca;
  private String            nomeRicerca;
  private String            msg;
  private String            operatoreMsg;
  private String            escapeMsg;
  private String            esecutore;
  private Date              dataEsecSuc;
  private String            operatoreDataEsecSuc;
  private Date              dataEsecPrec;
  private String            operatoreDataEsecPrec;
  private boolean           noCaseSensitive;
  private String            profiloOwner;
  private String            codiceApplicazione;

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
   * Costruttore vuoto
   */
  public TrovaCodaSched() {
    this.stato = "";
    this.nome = null;
    this.idSchedRic = "";
    this.idRicerca = "";
    this.nomeRicerca = null;
    this.esecutore = "";
    this.msg = null;
    this.operatoreMsg = GestioneOperatoreConfrontoStringa.CBX_VALORI_CONFRONTO_STRINGA[0];
    this.escapeMsg = null;
    this.dataEsecSuc = null;
    this.operatoreDataEsecSuc = null;
    this.dataEsecPrec = null;
    this.operatoreDataEsecPrec = null;
    this.noCaseSensitive = false;
    this.profiloOwner = null;
    this.codiceApplicazione = null;
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
   * @return Returns the owner.
   */
  public String getEsecutore() {
    return esecutore;
  }

  /**
   * @param owner
   *        The owner to set.
   */
  public void setEsecutore(String owner) {
    this.esecutore = owner;
  }

  /**
   * @return Returns the msg.
   */
  public String getMsg() {
    return msg;
  }

  /**
   * @param msg
   *        The msg to set.
   */
  public void setMsg(String msg) {
    this.msg = msg;
  }

  /**
   * @return Ritorna operatoreMsg.
   */
  public String getOperatoreMsg() {
    return operatoreMsg;
  }

  /**
   * @param operatoreMsg
   *        operatoreMsg da settare internamente alla classe.
   */
  public void setOperatoreMsg(String operatoreMsg) {
    this.operatoreMsg = operatoreMsg;
  }

  /**
   * @return Ritorna escapeMsg.
   */
  public String getEscapeMsg() {
    return escapeMsg;
  }

  /**
   * @param escapeMsg
   *        escapeMsg da settare internamente alla classe.
   */
  public void setEscapeMsg(String escapeMsg) {
    this.escapeMsg = escapeMsg;
  }

  /**
   * @return Returns the stato.
   */
  public String getStato() {
    return stato;
  }

  /**
   * @param stato
   *        The stato to set.
   */
  public void setStato(String stato) {
    this.stato = stato;
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
   * @return Returns the dataEsecPrec.
   */
  public Date getDataEsecPrec() {
    return dataEsecPrec;
  }

  /**
   * @param dataEsecPrec
   *        The dataEsecPrec to set.
   */
  public void setDataEsecPrec(Date dataEsecPrec) {
    this.dataEsecPrec = dataEsecPrec;
  }

  /**
   * @return Returns the dataEsecSuc.
   */
  public Date getDataEsecSuc() {
    return dataEsecSuc;
  }

  /**
   * @param dataEsecSuc
   *        The dataEsecSuc to set.
   */
  public void setDataEsecSuc(Date dataEsecSuc) {
    this.dataEsecSuc = dataEsecSuc;
  }

  /**
   * @return Ritorna operatoreDataEsec.
   */
  public String getOperatoreDataEsecPrec() {
    return operatoreDataEsecPrec;
  }

  /**
   * @param operatoreDataEsec
   *        operatoreDataEsec da settare internamente alla classe.
   */
  public void setOperatoreDataEsecPrec(String operatoreDataEsec) {
    this.operatoreDataEsecPrec = operatoreDataEsec;
  }

  /**
   * @return Ritorna operatoreDataEsecSuc.
   */
  public String getOperatoreDataEsecSuc() {
    return operatoreDataEsecSuc;
  }

  /**
   * @param operatoreDataEsecSuc
   *        operatoreDataEsecSuc da settare internamente alla classe.
   */
  public void setOperatoreDataEsecSuc(String operatoreDataEsecSuc) {
    this.operatoreDataEsecSuc = operatoreDataEsecSuc;
  }

  /**
   * @return Returns the idSchedRic.
   */
  public String getIdSchedRic() {
    return idSchedRic;
  }

  /**
   * @param idSchedRic
   *        The idSchedRic to set.
   */
  public void setIdSchedRic(String idSchedRic) {
    this.idSchedRic = idSchedRic;
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