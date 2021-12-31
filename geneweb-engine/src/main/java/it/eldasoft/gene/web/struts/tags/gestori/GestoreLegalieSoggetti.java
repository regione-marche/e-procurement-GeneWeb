/*
 * Created on 3/feb/2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori;

import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import org.springframework.transaction.TransactionStatus;
/**
 * Gestore della pagina 'Legali e altri soggetti': contiene sezini multiple per le entità IMPLEG, IMPDTE e IMPAZI
 *
 * Questa classe NON e' un gestore standard e prepara i dati di ciascuna
 * occorrenza presente nella scheda e demanda alla classe DefaultGestoreEntita
 * le operazioni di insert, update e delete
 *
 * @author Sara Santi
 */
public class GestoreLegalieSoggetti extends AbstractGestoreEntita {

  public GestoreLegalieSoggetti() {
    super(false);
  }

  @Override
  public String getEntita() {
    return "IMPR";
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  }

  @Override
  public void postInsert(DataColumnContainer impl) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {

	GestoreIMPLEG.gestisciEntitaDaImpr(this.getRequest(), this.getServletContext(), status, impl);
    GestoreIMPDTE.gestisciEntitaDaImpr(this.getRequest(), status, impl);

    Long tipimp = impl.getLong("IMPR.TIPIMP");
    if (tipimp == null || (tipimp != null && tipimp.longValue() != 3 && tipimp.longValue() != 10)) {
      // Gestione della sezione dinamica Azionisti, solo se l'impresa non è un ATI
      // Non viene utilizzato il metodo standard perchè la chiave dell'entità IMPAZI è composta direttamente
      // dal codice del tecnico e non da un numero progressivo
      //GestoreIMPAZI.gestisciEntitaDaImpr(this.getRequest(), status, impl);

      AbstractGestoreChiaveNumerica gestoreIMPAZI = new DefaultGestoreEntitaChiaveNumerica(
          "IMPAZI", "NUMAZI", new String[] { "CODIMP4" }, this.getRequest());
      this.gestisciAggiornamentiRecordSchedaMultipla(status, impl,
          gestoreIMPAZI, "IMPAZI",
          new DataColumn[] { impl.getColumn("IMPR.CODIMP") }, null);

      // Gestione delle sezioni 'Altre cariche o qualifiche'
      AbstractGestoreChiaveNumerica gestoreG_IMPCOL = new DefaultGestoreEntitaChiaveNumerica(
          "G_IMPCOL", "NUMCOL", new String[] { "CODIMP" }, this.getRequest());
      this.gestisciAggiornamentiRecordSchedaMultipla(status, impl,
          gestoreG_IMPCOL, "G_IMPCOL",
          new DataColumn[] { impl.getColumn("IMPR.CODIMP") }, null);
    }
  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

 }