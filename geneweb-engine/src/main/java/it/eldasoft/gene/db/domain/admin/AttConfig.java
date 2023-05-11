/*
 * Created on 01/04/2016
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

public class AttConfig implements Serializable {

  private static final long serialVersionUID = -6777575948238196450L;

  private String            codapp;
  private String            chiave;
  private String            valore;

  public String getCodapp() {
    return codapp;
  }

  public void setCodapp(String codapp) {
    this.codapp = codapp;
  }

  public String getChiave() {
    return chiave;
  }

  public void setChiave(String chiave) {
    this.chiave = chiave;
  }

  public String getValore() {
    return valore;
  }

  public void setValore(String valore) {
    this.valore = valore;
  }

}