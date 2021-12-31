/*
 * Created on 21-lug-2006
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
 * Bean per l'interfacciamento con i dati presenti nella tabella ...
 * 
 * @author Luca.Giacomazzo
 */
public class ModelloGruppo implements Serializable {

  /**   UID   */
  private static final long serialVersionUID = -1995538671838548005L;

  private int idModello;
  private String tipoModello;
  private String nomeModello;
  private String descrModello;
  private String nomeFile;
  private boolean disponibile;
  private boolean associato;
  
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
   * @return Ritorna descrModello.
   */
  public String getDescrModello() {
    return descrModello;
  }
  
  /**
   * @param descrModello descrModello da settare internamente alla classe.
   */
  public void setDescrModello(String descrModello) {
    this.descrModello = descrModello;
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
   * @return Ritorna nomeFile.
   */
  public String getNomeFile() {
    return nomeFile;
  }
  
  /**
   * @param nomeFile nomeFile da settare internamente alla classe.
   */
  public void setNomeFile(String fileModello) {
    this.nomeFile = fileModello;
  }
  
  /**
   * @return Ritorna idModello.
   */
  public int getIdModello() {
    return idModello;
  }
  
  /**
   * @param idModello idModello da settare internamente alla classe.
   */
  public void setIdModello(int idModello) {
    this.idModello = idModello;
  }
  
  /**
   * @return Ritorna nomeModello.
   */
  public String getNomeModello() {
    return nomeModello;
  }
  
  /**
   * @param nomeModello nomeModello da settare internamente alla classe.
   */
  public void setNomeModello(String nomeModello) {
    this.nomeModello = nomeModello;
  }
  
  /**
   * @return Ritorna tipoModello.
   */
  public String getTipoModello() {
    return tipoModello;
  }
  
  /**
   * @param tipoModello tipoModello da settare internamente alla classe.
   */
  public void setTipoModello(String tipoModello) {
    this.tipoModello = tipoModello;
  }
}