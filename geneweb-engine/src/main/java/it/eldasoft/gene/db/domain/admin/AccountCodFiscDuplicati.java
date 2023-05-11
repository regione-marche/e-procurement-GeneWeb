/*
 * Created on 05/giu/2015
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



public class AccountCodFiscDuplicati implements Serializable{

  /**
   *
   */
  private static final long serialVersionUID = 2840174564989248977L;

  /** id dell'account (chiave primaria numerica) */
  private int                syscon;

  /**  */
  private String                sysute;


  /**
   * @return Ritorna syscon.
   */
  public int getSyscon() {
    return syscon;
  }

  /**
   * @param syscon
   *        syscon da settare internamente alla classe.
   */
  public void setSyscon(int syscon) {
    this.syscon = syscon;
  }

  public String getSysute() {
    return sysute;
  }

  public void setSysute(String sysute) {
    this.sysute = sysute;
  }

}
