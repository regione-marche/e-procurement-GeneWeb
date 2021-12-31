/*
 * Created on 20-lug-2006
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
 * Bean contenente i dati idRicerca, tipoRicerca, nomeRicerca, descrRicerca, disponibile 
 * (cioe' ricerca disponibile) e associato (cioè stato di associazione tra ricerca e 
 * gruppo in analisi. I primi 5 attributi sono estratti dalle tabelle W_GRUPPI e il 
 * 
 * @author Luca.Giacomazzo
 */
public class RicercaGruppo implements Serializable {

  /**   UID   */
  private static final long serialVersionUID = -8509384006033025233L;

  private int idRicerca;
  private String tipoRicerca;
  private String nomeRicerca;
  private String descrRicerca;
  private boolean associato;
  private boolean disponibile;
  private String famiglia;
  private boolean personale;
  private String owner;
  
  /**
   * @return Ritorna descrRicerca.
   */
  public String getDescrRicerca() {
    return descrRicerca;
  }
  
  /**
   * @param descrRicerca descrRicerca da settare internamente alla classe.
   */
  public void setDescrRicerca(String descrRicerca) {
    this.descrRicerca = descrRicerca;
  }
  
  /**
   * @return Ritorna idRicerca.
   */
  public int getIdRicerca() {
    return idRicerca;
  }
  
  /**
   * @param idRicerca idRicerca da settare internamente alla classe.
   */
  public void setIdRicerca(int idRicerca) {
    this.idRicerca = idRicerca;
  }
  
  /**
   * @return Ritorna nomeRicerca.
   */
  public String getNomeRicerca() {
    return nomeRicerca;
  }
  
  /**
   * @param nomeRicerca nomeRicerca da settare internamente alla classe.
   */
  public void setNomeRicerca(String nomeRicerca) {
    this.nomeRicerca = nomeRicerca;
  }
  
  /**
   * @return Ritorna tipoRicerca.
   */
  public String getTipoRicerca() {
    return tipoRicerca;
  }
  
  /**
   * @param tipoRicerca tipoRicerca da settare internamente alla classe.
   */
  public void setTipoRicerca(String tipoRicerca) {
    this.tipoRicerca = tipoRicerca;
  }
  
  /**
   * @return Ritorna associato.
   */
  public boolean getAssociato() {
    return associato;
  }

  /**
   * @param associato associato da settare internamente alla classe.
   */
  public void setAssociato(boolean associato) {
    this.associato = associato;
  }
  
  /**
   * @return Ritorna disponibile.
   */
  public boolean getDisponibile() {
    return disponibile;
  }

  /**
   * @param disponibile disponibile da settare internamente alla classe.
   */
  public void setDisponibile(boolean disponibile) {
    this.disponibile = disponibile;
  }
  
  /**
   * @return Ritorna famiglia.
   */
  public String getFamiglia() {
    return famiglia;
  }
  
  /**
   * @param famiglia famiglia da settare internamente alla classe.
   */
  public void setFamiglia(String famiglia) {
    this.famiglia = famiglia;
  }
  
  /**
   * @return Ritorna owner.
   */
  public String getOwner() {
    return owner;
  }
  
  /**
   * @param owner owner da settare internamente alla classe.
   */
  public void setOwner(String owner) {
    this.owner = owner;
  }
  
  /**
   * @return Ritorna personale.
   */
  public boolean isPersonale() {
    return personale;
  }
  
  /**
   * @param personale personale da settare internamente alla classe.
   */
  public void setPersonale(boolean personale) {
    this.personale = personale;
  }
  
}