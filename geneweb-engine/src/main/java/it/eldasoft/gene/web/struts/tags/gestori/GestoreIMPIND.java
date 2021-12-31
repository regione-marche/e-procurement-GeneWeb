/*
 * Created on 24-lug-2008
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

import org.springframework.transaction.TransactionStatus;

/**
 * @author cit_defilippis
 */
public class GestoreIMPIND extends AbstractGestoreChiaveNumerica {


  public String[] getAltriCampiChiave() {
    return new String[] { "CODIMP5" };
  }

  public String getCampoNumericoChiave() {
    return "INDCON";
  }

  public String getEntita() {
    return "IMPIND";
  }

  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

  

}
