/*
 * Created on 17-lug-2008
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
 *
 */
public class GestoreIMPANTIMAFIA extends AbstractGestoreChiaveNumerica {

  public String[] getAltriCampiChiave() {
    return new String[]{"CODIMP"};
  }

  public String getCampoNumericoChiave() {
    return "NUMANT";
  }

  public String getEntita() {
    return "IMPANTIMAFIA";
  }

  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
  throws GestoreException {

    super.preInsert(status, datiForm);

    if(datiForm.isColumn("IMPANTIMAFIA.CODCIG") && datiForm.isModifiedColumn("IMPANTIMAFIA.CODCIG") && datiForm.getColumn("IMPANTIMAFIA.CODCIG").getValue().stringValue()!=null 
        && !"".equals(datiForm.getColumn("IMPANTIMAFIA.CODCIG").getValue().stringValue()) && datiForm.getColumn("IMPANTIMAFIA.CODCIG").getValue().stringValue().length()!= 10)
      throw new GestoreException("Il codice CIG specificato non è valido","controlloCodiceCIG");

  }
  
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {

    if(datiForm.isColumn("IMPANTIMAFIA.CODCIG") && datiForm.isModifiedColumn("IMPANTIMAFIA.CODCIG") && datiForm.getColumn("IMPANTIMAFIA.CODCIG").getValue().stringValue()!=null 
        && !"".equals(datiForm.getColumn("IMPANTIMAFIA.CODCIG").getValue().stringValue()) && datiForm.getColumn("IMPANTIMAFIA.CODCIG").getValue().stringValue().length()!= 10)
      throw new GestoreException("Il codice CIG specificato non è valido","controlloCodiceCIG");
  
  }

  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

}
