/*
 * Created on 13-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.domain;

import java.io.Serializable;

/**
 * Bean per l'interfacciamento con i dati presenti nella tabella W_CONFIG
 * 
 * @author Francesco.DeFilippis
 */
public class PropsConfig implements Serializable {

  /**
   * UID
   */
  private static final long serialVersionUID = 944115733233619505L;

  private String            codApp;
  private String            chiave;
  private String            valore;
  
  /**
   * @return Returns the chiave.
   */
  public String getChiave() {
    return chiave;
  }
  
  /**
   * @param chiave The chiave to set.
   */
  public void setChiave(String chiave) {
    this.chiave = chiave;
  }
  
  /**
   * @return Returns the codApp.
   */
  public String getCodApp() {
    return codApp;
  }
  
  /**
   * @param codApp The codApp to set.
   */
  public void setCodApp(String codApp) {
    this.codApp = codApp;
  }
  
  /**
   * @return Returns the valore.
   */
  public String getValore() {
    return valore;
  }
  
  /**
   * @param valore The valore to set.
   */
  public void setValore(String valore) {
    this.valore = valore;
  }
  

  

}