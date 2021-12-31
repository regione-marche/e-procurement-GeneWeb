/*
 * Created on 06/04/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori;

import java.sql.SQLException;

import it.eldasoft.gene.db.datautils.DataColumnContainer;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore di submit dell'entita' G_SCADENZ
 *
 * @author Marcello Caminiti
 */
public class GestoreCancellazioneAttivita extends AbstractGestoreEntita {


  @Override
  public String getEntita() {
    return "G_SCADENZ";
  }

  public GestoreCancellazioneAttivita() {
    super(false);
  }

  public GestoreCancellazioneAttivita(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {


  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    String id=this.getRequest().getParameter("id");
    Long idLong = Long.valueOf(id);
    try {
      this.getSqlManager().update("delete from G_SCADENZ where id=?", new Object[]{idLong});
    } catch (SQLException e) {
      throw new GestoreException("errore nella cancellazione dell'attività: " + id,null, e);
    }
    this.getRequest().setAttribute("eliminazioneEseguita", "1");

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

}