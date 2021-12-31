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
package it.eldasoft.gene.db.domain.admin;

import java.io.Serializable;

/**
 * Bean per l'interfacciamento con i dati presenti nella directory LDAP
 *
 * @author Francesco.DeFilippis
 */
public class AccountLdap implements Serializable,Comparable<AccountLdap> {

  /**
   * UID
   */
  private static final long serialVersionUID = 944115733233619505L;
  private String            cn;
  private String            dn;
  private String            objectCategory;
  private String            sn;

  /**
   * @return Returns the cn.
   */
  public String getCn() {
    return cn;
  }

  /**
   * @param cn
   *        The cn to set.
   */
  public void setCn(String cn) {
    this.cn = cn;
  }

  /**
   * @return Returns the dn.
   */
  public String getDn() {
    return dn;
  }

  /**
   * @param dn
   *        The dn to set.
   */
  public void setDn(String dn) {
    this.dn = dn;
  }

  /**
   * @return Returns the objectCategory.
   */
  public String getObjectCategory() {
    return objectCategory;
  }

  /**
   * @param objectCategory
   *        The objectCategory to set.
   */
  public void setObjectCategory(String objectCategory) {
    this.objectCategory = objectCategory;
  }

  /**
   * @return Returns the sn.
   */
  public String getSn() {
    return sn;
  }

  /**
   * @param sn
   *        The sn to set.
   */
  public void setSn(String sn) {
    this.sn = sn;
  }

  public int compareTo(AccountLdap arg0) {
    int result = 0;

    AccountLdap arg = arg0;

    result = this.sn.compareTo(arg.getSn());

    return result;
  }

}