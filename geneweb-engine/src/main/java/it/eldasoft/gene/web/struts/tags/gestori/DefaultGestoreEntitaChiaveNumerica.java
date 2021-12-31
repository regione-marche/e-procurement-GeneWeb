/*
 * Created on 06/ago/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori;

import it.eldasoft.gene.db.datautils.DataColumnContainer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore di default per entità con chiave composta ed in cui un attributo è
 * numerico e incrementale, utilizzato esplicitamente nel caso di creazione di
 * nuovi record per entità figlie in relazione 1:N con un'entità padre, definite
 * mediante il generatore attributi, in quanto sarebbe inutile definire un
 * gestore di chiave numerica esplicito per ogni sottoentità, poichè si
 * perderebbe la potenza della generalizzazione fornita dal generatore
 * attributi.
 * 
 * @author Stefano.Sabbadin
 */
public class DefaultGestoreEntitaChiaveNumerica extends
    AbstractGestoreChiaveNumerica {

  private String   entitaDefault;
  private String   campoNumericoChiaveDefault;
  private String[] altriCampiChiaveDefault;

  public DefaultGestoreEntitaChiaveNumerica(String entita,
      String campoNumerico, String[] altreChiavi, HttpServletRequest request) {
    super();
    this.entitaDefault = entita;
    this.campoNumericoChiaveDefault = campoNumerico;
    this.altriCampiChiaveDefault = altreChiavi;
    this.setRequest(request);
  }

  public String getEntita() {
    return this.entitaDefault;
  }

  public String getCampoNumericoChiave() {
    return this.campoNumericoChiaveDefault;
  }

  public String[] getAltriCampiChiave() {
    return this.altriCampiChiaveDefault;
  }

  /*
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postInsert(it.eldasoft.gene.db.sql.sqlparser.JdbcColumnWithValueImpl)
   */
  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  /*
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#preUpdate(org.springframework.transaction.TransactionStatus,
   *      it.eldasoft.gene.db.sql.sqlparser.JdbcColumnWithValueImpl)
   */
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  /*
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postUpdate(it.eldasoft.gene.db.sql.sqlparser.JdbcColumnWithValueImpl)
   */
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

  /*
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#preDelete(org.springframework.transaction.TransactionStatus,
   *      it.eldasoft.gene.db.sql.sqlparser.JdbcColumnWithValueImpl)
   */
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  /*
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postDelete(it.eldasoft.gene.db.sql.sqlparser.JdbcColumnWithValueImpl)
   */
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

}
