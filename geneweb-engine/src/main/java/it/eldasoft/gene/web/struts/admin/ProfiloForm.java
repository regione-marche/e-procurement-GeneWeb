/*
 * Created on 01-Ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.admin;

import javax.servlet.http.HttpServletRequest;

import it.eldasoft.gene.db.domain.admin.Profilo;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * ActionForm per gestione del Profilo
 * 
 * @author Luca.Giacomazzo
 */
public class ProfiloForm extends ActionForm {

  /**   UID   */
  private static final long serialVersionUID = 537730353436561270L;
  
  private String  codiceProfilo;
  private String  codapp;
  private String  nome;
  private String  descrizione;
  private boolean profiloInterno;
  private String  discriminante;
  private String  codiceCliente;
  
  public ProfiloForm(){
    super();
    this.inizializzaOggetto();
  }
  
  public ProfiloForm(Profilo profilo){
    if(profilo != null){
      this.codiceProfilo = profilo.getCodiceProfilo();
      this.codapp        = profilo.getCodApp();
      this.nome          = profilo.getNome();
      this.descrizione   = profilo.getDescrizione();
      this.profiloInterno = profilo.isProfiloInterno();
      this.discriminante = profilo.getDiscriminante();
      this.codiceCliente = profilo.getCodiceCliente();
    } else {
      this.inizializzaOggetto();
    }
  }
  
  public void reset(ActionMapping mapping, HttpServletRequest request){
    super.reset(mapping, request);
    this.inizializzaOggetto();
  }
  
  public Profilo getDatiPerModel(){
    Profilo result = new Profilo();
    result.setCodiceProfilo(this.codiceProfilo);
    result.setCodApp(this.codapp);
    result.setNome(this.nome);
    result.setDescrizione(this.descrizione);
    result.setProfiloInterno(this.profiloInterno);
    result.setDiscriminante(this.discriminante);
    result.setCodiceCliente(this.codiceCliente);
    
    return result;
  }
  
  private void inizializzaOggetto(){
    this.codiceProfilo  = null;
    this.codapp         = null;
    this.nome           = null;
    this.descrizione    = null;
    this.profiloInterno = false;
    this.discriminante  = null;
    this.codiceCliente  = null;
    
  }

  /**
   * @return Ritorna codapp.
   */
  public String getCodapp() {
    return codapp;
  }
  
  /**
   * @param codapp codapp da settare internamente alla classe.
   */
  public void setCodapp(String codapp) {
    this.codapp = codapp;
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