package it.eldasoft.gene.web.struts.login.cohesion;

import java.io.Serializable;

/**
 * Bean che estende l'oggetto Account di Gene per memorizzare attributi specifici
 * dell'accesso con Cohesion 
 * 
 * @author Luca.Giacomazzo
 */
public class AccountCohesion implements Serializable {
  
  /**   UID   */
  private static final long serialVersionUID = -7144374769585022447L;
  
  public static final String ID_ATTRIBUTO_SESSIONE_ACCOUNT_COHESION = "accountCohesion";
  public static final String ID_ATTRIBUTO_SESSIONE_COHESION_TOKEN   = "cohesion_token";
  
  public AccountCohesion() {
    this.cohesionLogin = false;
    this.tipoAutenticazione = null;
    this.login = null;
    this.nome = null;
    this.cognome = null;
    this.email = null;
  }
    
  private String login;
  private String nome;
  private String cognome;
  private String email;
  private String  tipoAutenticazione;
  private boolean cohesionLogin;
  
  public void setCohesionLogin(boolean cohesionLogin) {
    this.cohesionLogin = cohesionLogin;
  }

  public boolean isCohesionLogin() {
    return cohesionLogin;
  }

  public void setTipoAutenticazione(String tipoAutenticazione) {
    this.tipoAutenticazione = tipoAutenticazione;
  }

  public String getTipoAutenticazione() {
    return tipoAutenticazione;
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
  
}
