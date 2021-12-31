/*
 * Created on 01-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain.admin;

import java.io.Serializable;

/**
 * Bean per l'interfacciamento con i dati presenti nella tabella W_PROFILI
 * 
 * @author Luca.Giacomazzo
 */
public class Profilo implements Serializable {

  /**   UID   */
  private static final long serialVersionUID = -3516198011781014834L;

  private String  codiceProfilo;
  private String  codApp;
  private String  nome;
  private String  descrizione;
  private boolean profiloInterno;
  private String  discriminante;
  private String  codiceCliente;
  
  public Profilo(){
    this.codiceProfilo  = null;
    this.codApp         = null;
    this.nome           = null;
    this.descrizione    = null;
    this.profiloInterno = false;
    this.discriminante  = null;
    this.codiceCliente  = null;
  }
  
  /**
   * @return Ritorna codapp.
   */
  public String getCodApp() {
    return codApp;
  }
  
  /**
   * @param codapp codapp da settare internamente alla classe.
   */
  public void setCodApp(String codapp) {
    this.codApp = codapp;
  }
  
  /**
   * @return Ritorna codiceCliente.
   */
  public String getCodiceCliente() {
    return codiceCliente;
  }
  
  /**
   * @param codiceCliente codiceCliente da settare internamente alla classe.
   */
  public void setCodiceCliente(String codiceCliente) {
    this.codiceCliente = codiceCliente;
  }
  
  /**
   * @return Ritorna codiceProfilo.
   */
  public String getCodiceProfilo() {
    return codiceProfilo;
  }
  
  /**
   * @param codiceProfilo codiceProfilo da settare internamente alla classe.
   */
  public void setCodiceProfilo(String codiceProfilo) {
    this.codiceProfilo = codiceProfilo;
  }
  
  /**
   * @return Ritorna descrizione.
   */
  public String getDescrizione() {
    return descrizione;
  }
  
  /**
   * @param descrizione descrizione da settare internamente alla classe.
   */
  public void setDescrizione(String descrizione) {
    this.descrizione = descrizione;
  }
  
  /**
   * @return Ritorna discriminante.
   */
  public String getDiscriminante() {
    return discriminante;
  }
  
  /**
   * @param discriminante discriminante da settare internamente alla classe.
   */
  public void setDiscriminante(String discriminante) {
    this.discriminante = discriminante;
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
   * @return Ritorna profiloInterno.
   */
  public boolean isProfiloInterno() {
    return profiloInterno;
  }

  /**
   * @return Ritorna profiloInterno.
   */
  public boolean getProfiloInterno() {
    return profiloInterno;
  }

  /**
   * @param profiloInterno profiloInterno da settare internamente alla classe.
   */
  public void setProfiloInterno(boolean profiloInterno) {
    this.profiloInterno = profiloInterno;
  }
 
}