/*
 * Created on 15/dic/13
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
 * Gestore di default per entità con chiave numerica auto incrementante.
 *
 * @author Stefano.Sabbadin
 */
public class DefaultGestoreEntitaChiaveIDAutoincrementante extends
    AbstractGestoreChiaveIDAutoincrementante {

  private String   entitaDefault;
  private String   campoNumericoChiaveDefault;

  public DefaultGestoreEntitaChiaveIDAutoincrementante(String entita,
      String campoNumerico, HttpServletRequest request) {
    super();
    this.entitaDefault = entita;
    this.campoNumericoChiaveDefault = campoNumerico;
    this.setRequest(request);
  }

  @Override
  public String getEntita() {
    return this.entitaDefault;
  }

  @Override
  public String getCampoNumericoChiave() {
    return this.campoNumericoChiaveDefault;
  }

  /*
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postInsert(it.eldasoft.gene.db.sql.sqlparser.JdbcColumnWithValueImpl)
   */
  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  /*
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#preUpdate(org.springframework.transaction.TransactionStatus,
   *      it.eldasoft.gene.db.sql.sqlparser.JdbcColumnWithValueImpl)
   */
  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  /*
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postUpdate(it.eldasoft.gene.db.sql.sqlparser.JdbcColumnWithValueImpl)
   */
  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

  /*
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#preDelete(org.springframework.transaction.TransactionStatus,
   *      it.eldasoft.gene.db.sql.sqlparser.JdbcColumnWithValueImpl)
   */
  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  /*
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postDelete(it.eldasoft.gene.db.sql.sqlparser.JdbcColumnWithValueImpl)
   */
  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

}
