/*
 * Created on 08/mag/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.gestori.submit;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import org.springframework.transaction.TransactionStatus;


/**
 * Gestore per l'entit&agrave; eventi linkabili ad attivit&agrave; di uno scadenzario.
 *
 * @author Stefano.Sabbadin
 */
public class GestoreEventoScadenzario extends AbstractGestoreEntita {

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#getEntita()
   */
  @Override
  public String getEntita() {
    return "G_EVENTISCADENZ";
  }

  /**
   * Esegue opportune verifiche se il record da eliminare viene usato in scadenzari o in modelli: in tal caso blocca l'eliminazione fornendo un errore.
   *
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#preDelete(org.springframework.transaction.TransactionStatus, it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    String cod = datiForm.getString("G_EVENTISCADENZ.COD");
//    try {
//      Vector<JdbcParametro> evento = this.sqlManager.getVector("SELECT ENT FROM G_EVENTISCADENZ WHERE COD=?", new String[]{cod});
//      if (evento == null) {
//        throw new GestoreException("L'evento con codice " + cod + " risulta già eliminato", null);
//      }
//      String entita = evento.get(0).stringValue();
//    } catch (SQLException e) {
//      throw new GestoreException(
//          "Errore durante l'eliminazione dell'evento per scadenzario", null, e);
//    }
    long attivitaScadenzario = this.geneManager.countOccorrenze("G_SCADENZ", "CODEVENTO=?", new String[]{cod});
    if (attivitaScadenzario > 0)
      throw new GestoreException("Esistono " + attivitaScadenzario + " attività di scadenzario che utilizzano l'evento con codice " + cod, "eventoScadenzario.linkAttivita");
    long attivitaInModelliScadenzario = this.geneManager.countOccorrenze("G_DETTMODSCADENZ", "CODEVENTO=?", new String[]{cod});
    if (attivitaInModelliScadenzario > 0)
      throw new GestoreException("Esistono " + attivitaScadenzario + " attività in modelli di scadenzario che utilizzano l'evento con codice " + cod, "eventoScadenzario.linkModelliAttivita");
  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postDelete(it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#preInsert(org.springframework.transaction.TransactionStatus, it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postInsert(it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#preUpdate(org.springframework.transaction.TransactionStatus, it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
  }

  /**
   * @see it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita#postUpdate(it.eldasoft.gene.db.datautils.DataColumnContainer)
   */
  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

}
