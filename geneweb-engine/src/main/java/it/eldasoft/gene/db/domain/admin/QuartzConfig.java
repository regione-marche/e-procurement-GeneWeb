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

public class QuartzConfig implements Serializable {

  /**
   * UID
   */
  private static final long serialVersionUID = 944115733233619505L;

  private String            codapp;
  private String            bean_id;
  private String            cron_expression;
  
  public String getCodapp() {
    return codapp;
  }
  
  public void setCodapp(String codapp) {
    this.codapp = codapp;
  }
  
  public String getBean_id() {
    return bean_id;
  }
  
  public void setBean_id(String bean_id) {
    this.bean_id = bean_id;
  }
  
  public String getCron_expression() {
    return cron_expression;
  }
  
  public void setCron_expression(String cron_expression) {
    this.cron_expression = cron_expression;
  }


  

  

}