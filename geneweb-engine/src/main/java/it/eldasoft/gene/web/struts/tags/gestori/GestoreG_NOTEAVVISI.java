/*
 * Created on 18/ago/2009
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
 * Gestore di submit dell'entita' G_NOTEAVVISI
 * 
 * @author Luca.Giacomazzo
 */
public class GestoreG_NOTEAVVISI extends AbstractGestoreChiaveNumerica {

  public String getCampoNumericoChiave() {
    return "NOTECOD";
  }

  public String[] getAltriCampiChiave() {
    return null;
  }
  
  public String getEntita() {
    return "G_NOTEAVVISI";
  }

  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    super.preInsert(status, datiForm);
  }

  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

}