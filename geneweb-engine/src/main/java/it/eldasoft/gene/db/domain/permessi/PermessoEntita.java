/*
 * Created on 23-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.permessi;

import java.io.Serializable;

/**
 * Bean per l'interfacciamento con i dati presenti nella tabella G_PERMESSI
 *
 * @author Luca.Giacomazzo
 */
public class PermessoEntita implements Serializable {

  /**   UID   */
  private static final long serialVersionUID = -7909477072038460544L;

  private Integer idPermesso;
  private Integer idAccount;
  private Integer autorizzazione;
  private Integer proprietario;
  private String  campoChiave;
  private Object  valoreChiave;
  private Integer predefinito;
  private Integer riferimento;
  private Integer ruolo;
  private Integer ruoloUsrsys;
  private String utenteDisabilitato;

  // Campi che vengono estratti da DB, ma non vengono gestiti in fase di insert
  private String login;
  private String nome;

  public PermessoEntita(){
    this.idPermesso        = null;
    this.idAccount         = null;
    this.autorizzazione    = null;
    this.proprietario      = null;
    this.campoChiave       = null;
    this.valoreChiave      = null;
    this.predefinito       = null;
    this.riferimento       = null;
    this.ruolo             = null;
    this.ruoloUsrsys       = null;
    this.utenteDisabilitato =null;
  }

  /**
   * @return Ritorna autorizzazione.
   */
  public Integer getAutorizzazione() {
    return autorizzazione;
  }

  /**
   * @param autorizzazione autorizzazione da settare internamente alla classe.
   */
  public void setAutorizzazione(Integer autorizzazione) {
    this.autorizzazione = autorizzazione;
  }

  /**
   * @return Ritorna valoreChiave.
   */
  public Object getValoreChiave() {
    return valoreChiave;
  }

  /**
   * @param valoreChiave valoreChiave da settare internamente alla classe.
   */
  public void setValoreChiave(Object valoreChiave) {
    this.valoreChiave = valoreChiave;
  }

  /**
   * @return Ritorna codiceLavoro.
   */
  public String getCampoChiave() {
    return campoChiave;
  }

  /**
   * @param codiceLavoro codiceLavoro da settare internamente alla classe.
   */
  public void setCampoChiave(String codiceLavoro) {
    this.campoChiave = codiceLavoro;
  }

  /**
   * @return Ritorna idAccount.
   */
  public Integer getIdAccount() {
    return idAccount;
  }

  /**
   * @param idAccount idAccount da settare internamente alla classe.
   */
  public void setIdAccount(Integer idAccount) {
    this.idAccount = idAccount;
  }

  /**
   * @return Ritorna idPermesso.
   */
  public Integer getIdPermesso() {
    return idPermesso;
  }

  /**
   * @param idPermesso idPermesso da settare internamente alla classe.
   */
  public void setIdPermesso(Integer idPermesso) {
    this.idPermesso = idPermesso;
  }

  /**
   * @return Ritorna predefinito.
   */
  public Integer getPredefinito() {
    return predefinito;
  }

  /**
   * @param predefinito predefinito da settare internamente alla classe.
   */
  public void setPredefinito(Integer predefinito) {
    this.predefinito = predefinito;
  }

  /**
   * @return Ritorna proprietario.
   */
  public Integer getProprietario() {
    return proprietario;
  }

  /**
   * @param proprietario proprietario da settare internamente alla classe.
   */
  public void setProprietario(Integer proprietario) {
    this.proprietario = proprietario;
  }

  /**
   * @return Ritorna riferimento.
   */
  public Integer getRiferimento() {
    return riferimento;
  }

  /**
   * @param riferimento riferimento da settare internamente alla classe.
   */
  public void setRiferimento(Integer riferimento) {
    this.riferimento = riferimento;
  }

  /**
   * @return Ritorna login.
   */
  public String getLogin() {
    return login;
  }

  /**
   * @param login login da settare internamente alla classe.
   */
  public void setLogin(String login) {
    this.login = login;
  }

  /**
   * @return Ritorna nome.
   */
  public String getNome() {
    return nome;
  }

  /**
   * @param nome nome da settare internamente alla classe.
   */
  public void setNome(String nome) {
    this.nome = nome;
  }

  /**
   * @return Ritorna ruolo.
   */
  public Integer getRuolo() {
    return ruolo;
  }

  /**
   * @param ruolo ruolo da settare internamente alla classe.
   */
  public void setRuolo(Integer ruolo) {
    this.ruolo = ruolo;
  }

  /**
   * @return Ritorna ruolo della usrsys.
   */
  public Integer getRuoloUsrsys() {
    return ruoloUsrsys;
  }

  /**
   * @param ruoloUsrsys ruolo della usrsys da settare internamente alla classe.
   */
  public void setRuoloUsrsys(Integer ruoloUsrsys) {
    this.ruoloUsrsys = ruoloUsrsys;
  }

  /**
   * @return Ritorna utenteDisabilitato.
   */
  public String getUtenteDisabilitato() {
    return utenteDisabilitato;
  }

  /**
   * @param utenteDisabilitato utenteDisabilitato da settare internamente alla classe.
   */
  public void setUtenteDisabilitato(String utenteDisabilitato) {
    this.utenteDisabilitato = utenteDisabilitato;
  }

}