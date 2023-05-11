/*
 * Created on 20 - Feb - 2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.system.ldap;

import it.eldasoft.gene.commons.web.struts.DispatchForm;
import it.eldasoft.gene.db.domain.system.ConfigurazioneLdap;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;

/**
 * Form di gestione del server Ldap
 * 
 * @author Francesco.DeFilippis
 * 
 */
public class ConfigurazioneLdapForm extends DispatchForm {

  /**
   * UID
   */
  private static final long serialVersionUID = -989053977808439700L;

  private String            server           = "";
  private String            porta            = "";
  private String            base             = "";
  private String            dn               = "";
  private String            password         = "";
  
  private String            filtroOU = "";
  private String            filtroUtenti = "";
  private String            attributoLogin = "";
  private String            attributoNome = "";
  

  public ConfigurazioneLdapForm() {
    super();
    this.server = "";
    this.porta = "";
    this.base = "";
    this.dn = "";
    this.password = "";

    this.filtroOU = "";
    this.filtroUtenti = "";
    this.attributoLogin = "";
    this.attributoNome = "";
    
  }
  
  /**
   * @return Returns the base.
   */
  public String getBase() {
    return base;
  }

  
  /**
   * @param base The base to set.
   */
  public void setBase(String base) {
    this.base = base;
  }

  
  /**
   * @return Returns the dn.
   */
  public String getDn() {
    return dn;
  }

  
  /**
   * @param dn The dn to set.
   */
  public void setDn(String dn) {
    this.dn = dn;
  }


  
  /**
   * @return Returns the password.
   */
  public String getPassword() {
    return password;
  }

  
  /**
   * @param password The password to set.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  
  /**
   * @return Returns the porta.
   */
  public String getPorta() {
    return porta;
  }

  
  /**
   * @param porta The porta to set.
   */
  public void setPorta(String porta) {
    this.porta = porta;
  }

  
  /**
   * @return Returns the server.
   */
  public String getServer() {
    return server;
  }

  
  /**
   * @param server The server to set.
   */
  public void setServer(String server) {
    this.server = server;
  }

  /**
   * Costruttore con parametri prepara il form per la visualizzazione e per la
   * modifica
   * 
   * @param server
   */
  public ConfigurazioneLdapForm(ConfigurazioneLdap server) {
    super();
    this.server = server.getServer();
    this.porta = server.getPorta();
    this.base = server.getBase();
    this.dn = server.getDn();
    this.password = server.getPassword();

    this.filtroOU = server.getFiltroOU();
    this.filtroUtenti = server.getFiltroUtenti();
    this.attributoLogin = server.getAttributoLogin();
    this.attributoNome = server.getAttributoNome();
    
  }

  /**
   * Funzione di reset del form
   */
  public void reset(ActionMapping mapping, HttpServletRequest request) {
    super.reset(mapping, request);

  }

  /**
   * Prepara il form per essere salvato
   * 
   * @param serverForm
   * @return bean per il salvataggio
   */
  public ConfigurazioneLdap getDatiPerModel() {
    ConfigurazioneLdap server = new ConfigurazioneLdap();
    server.setBase(this.base);
    server.setPassword(this.password);
    server.setServer(this.server);
    server.setPorta(this.porta);
    server.setDn(this.dn);

    server.setFiltroOU(this.filtroOU);
    server.setFiltroUtenti(this.filtroUtenti);
    server.setAttributoLogin(this.attributoLogin);
    server.setAttributoNome(this.attributoNome);
    
    return server;
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

}
