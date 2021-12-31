/*
 * Created on 02-ott-2007
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
 * Bean per la configurazione dell'interfacciamento LDAP. Contiene dati
 * memorizzati nella tabella W_CONFIG
 * 
 * @author cit_defilippis
 */
public class ConfigurazioneLdap implements Serializable {

  /**
   * UID
   */
  private static final long serialVersionUID = 2813651369607746213L;

  /** Nome del server LDAP o indirizzo IP */
  private String            server;
  /** Porta di ascolto del server LDAP */
  private String            porta;
  /**
   * Identificativo univoco (distinguishedName) di un utente per l'accesso
   * all'elenco utenti/gruppi
   */
  private String            dn;
  /** Password dell'utente identificato dall'attributo "dn" */
  private String            password;
  /** Percorso base di ricerca elementi nella struttura LDAP */
  private String            base;
  /** Classe di filtro sugli oggetti di tipo utente */
//  private String            objectClass;
//  /**
//   * Filtro di categoria per reperire le organizational unit (OU) in un dominio
//   * LDAP
//   */
//  private String            objectCategoryOU;
//  /** Filtro di categoria per reperire gli utenti di un dominio LDAP */
//  private String            objectCategory;
//
//  /** eventuale filtro supplementare da applicare alle ricerche */
//  private String            filtroSupplementare;
  
  private String            filtroOU;
  
  private String            filtroUtenti;
  
  private String            attributoLogin;
  
  private String            attributoNome;
  
  
  public ConfigurazioneLdap() {
    this.server = null;
    this.porta = null;
    this.dn = null;
    this.password = null;
    this.base = null;
//    this.objectClass = null;
//    this.objectCategory = null;
//    this.objectCategoryOU = null;
//    this.filtroSupplementare = null;
    this.filtroOU = null;
    this.filtroUtenti = null;
    this.attributoLogin = null;
    this.attributoNome = null;
    
  }

  /**
   * @return Ritorna base.
   */
  public String getBase() {
    return base;
  }

  /**
   * @param base
   *        base da settare internamente alla classe.
   */
  public void setBase(String base) {
    this.base = base;
  }

  /**
   * @return Ritorna dn.
   */
  public String getDn() {
    return dn;
  }

  /**
   * @param dn
   *        dn da settare internamente alla classe.
   */
  public void setDn(String dn) {
    this.dn = dn;
  }

//  /**
//   * @return Ritorna objectCategory.
//   */
//  public String getObjectCategory() {
//    return objectCategory;
//  }
//
//  /**
//   * @param objectCategory
//   *        objectCategory da settare internamente alla classe.
//   */
//  public void setObjectCategory(String objectCategory) {
//    this.objectCategory = objectCategory;
//  }
//
//  /**
//   * @return Ritorna objectCategoryOU.
//   */
//  public String getObjectCategoryOU() {
//    return objectCategoryOU;
//  }
//
//  /**
//   * @param objectCategoryOU
//   *        objectCategoryOU da settare internamente alla classe.
//   */
//  public void setObjectCategoryOU(String objectCategoryOU) {
//    this.objectCategoryOU = objectCategoryOU;
//  }
//
//  /**
//   * @return Ritorna objectClass.
//   */
//  public String getObjectClass() {
//    return objectClass;
//  }
//
//  /**
//   * @param objectClass
//   *        objectClass da settare internamente alla classe.
//   */
//  public void setObjectClass(String objectClass) {
//    this.objectClass = objectClass;
//  }

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
   * @return Ritorna attributoLogin.
   */
  public String getAttributoLogin() {
    return attributoLogin;
  }

  
  /**
   * @param attributoLogin attributoLogin da settare internamente alla classe.
   */
  public void setAttributoLogin(String attributoLogin) {
    this.attributoLogin = attributoLogin;
  }

  
  /**
   * @return Ritorna attributoNome.
   */
  public String getAttributoNome() {
    return attributoNome;
  }

  
  /**
   * @param attributoNome attributoNome da settare internamente alla classe.
   */
  public void setAttributoNome(String attributoNome) {
    this.attributoNome = attributoNome;
  }

  
  /**
   * @return Ritorna filtroOU.
   */
  public String getFiltroOU() {
    return filtroOU;
  }

  
  /**
   * @param filtroOU filtroOU da settare internamente alla classe.
   */
  public void setFiltroOU(String filtroOU) {
    this.filtroOU = filtroOU;
  }

  
  /**
   * @return Ritorna filtroUtenti.
   */
  public String getFiltroUtenti() {
    return filtroUtenti;
  }

  
  /**
   * @param filtroUtenti filtroUtenti da settare internamente alla classe.
   */
  public void setFiltroUtenti(String filtroUtenti) {
    this.filtroUtenti = filtroUtenti;
  }

  
//  /**
//   * @return Returns the filtroSupplementare.
//   */
//  public String getFiltroSupplementare() {
//    return filtroSupplementare;
//  }
//
//  
//  /**
//   * @param filtroSupplementare The filtroSupplementare to set.
//   */
//  public void setFiltroSupplementare(String filtroSupplementare) {
//    this.filtroSupplementare = filtroSupplementare;
//  }

}