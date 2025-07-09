package it.eldasoft.gene.db.domain;

import java.io.Serializable;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;

/**
 * Bean che estende l'oggetto Account di Gene per memorizzare attributi specifici
 * dell'accesso con SSO 
 * 
 * @author Manuel Bridda
 */
public class AccountSSO implements Serializable {
  
  /**   UID   */
  private static final long serialVersionUID = -7144374769585022447L;
  
  public AccountSSO() {
    this.login = null;
    this.nome = null;
    this.cognome = null;
    this.email = null;
    this.loginProvider = null;
    this.standardProvider = null;
    this.currentLoa = null;
  }
    
  private String login;
  private String nome;
  private String cognome;
  private String email;
  private String loginProvider; 
  private String standardProvider; //SPID, CIE, null
  private Integer currentLoa; //2/3/4
  private String authId;
  
  public boolean hasLoa(int loa) {
    return currentLoa>=loa;
  }
  
  public boolean isProviderStandard() {
    return CostantiGenerali.PROP_SSO_SPID.equals(standardProvider) || CostantiGenerali.PROP_SSO_CIE.equals(standardProvider);
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getLogin() {
    return login;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getNome() {
    return nome;
  }

  public void setCognome(String cognome) {
    this.cognome = cognome;
  }

  public String getCognome() {
    return cognome;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getEmail() {
    return email;
  }
  /** Ritorna un valore tra [SPID, CIE, WEBSERVER, COHESION, GATEWAY] ***/
  public String getLoginProvider() {
    return loginProvider;
  }

  public void setLoginProvider(String loginProvider) {
    this.loginProvider = loginProvider;
  }
  
  public String getStandardProvider() {
    return standardProvider;
  }

  public void setStandardProvider(String standardProvider) {
    this.standardProvider = standardProvider;
  }

  public Integer getCurrentLoa() {
    return currentLoa;
  }

  public void setCurrentLoa(Integer currentLoa) {
    this.currentLoa = currentLoa;
  }
  
  public String getAuthId() {
    return authId;
  }

  public void setAuthId(String authId) {
    this.authId = authId;
  }

  @Override
  public String toString() {
    return "AccountSSO [login="
        + login
        + ", nome="
        + nome
        + ", cognome="
        + cognome
        + ", email="
        + email
        + ", loginProvider="
        + loginProvider
        + ", standardProvider="
        + standardProvider
        + ", isProviderStandard()="
        + isProviderStandard()
        + ", currentLoa="
        + currentLoa
        + "]";
  }
}
