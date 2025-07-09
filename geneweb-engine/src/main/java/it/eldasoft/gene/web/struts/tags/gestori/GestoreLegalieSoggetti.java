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
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import org.springframework.transaction.TransactionStatus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    LogEvento logevento = LogEventiUtils.createLogEvento(this.getRequest());
    logevento.setLivEvento(1);
    logevento.setOggEvento(impl.getString("IMPR.CODIMP"));
    logevento.setCodEvento("IMPR_EDIT");
    logevento.setDescr("Modifica anagrafica impresa (legali e soggetti)");
    logevento.setErrmsg(elaboraModifiche(impl));
    LogEventiUtils.insertLogEventi(logevento);
  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

  private String elaboraModifiche(DataColumnContainer datiForm) throws GestoreException {
    String descrErrMsg = "Dettaglio modifiche:";
    HashMap<String, DataColumn> hm = datiForm.getColonne();
    Iterator<?> it = hm.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, DataColumn> pair = (Map.Entry<String, DataColumn>) it.next();
      if (pair.getValue().isModified()) {
        if(pair.getKey().indexOf("MOD_")>=0 || pair.getKey().indexOf("INDICE_")>=0 )
          continue;
        if (!"".equals(descrErrMsg)) {
          descrErrMsg += "\n";
        }
        if (pair.getKey().indexOf("DEL_IMPLEG") > 0) {
          descrErrMsg += "Eliminato dai legali rappresentanti il soggetto "+datiForm.getColumn("IMPLEG.CODLEG_"+pair.getKey().substring(pair.getKey().indexOf("DEL_IMPLEG_")+11)).getOriginalValue();
        } else if(pair.getKey().indexOf("DEL_IMPAZI") > 0) {
          descrErrMsg += "Eliminato il soggetto incaricato "+datiForm.getColumn("IMPAZI.CODTEC_"+pair.getKey().substring(pair.getKey().indexOf("DEL_IMPAZI_")+11)).getOriginalValue();
        } else if(pair.getKey().indexOf("DEL_IMPDTE") > 0) {
          descrErrMsg += "Eliminato il direttore tecnico "+datiForm.getColumn("IMPDTE.CODDTE_"+pair.getKey().substring(pair.getKey().indexOf("DEL_IMPDTE_")+11)).getOriginalValue();
        } else if(pair.getKey().indexOf("DEL_G_IMPCOL") > 0) {
          descrErrMsg += "Eliminato dai collaboratori il soggetto "+datiForm.getColumn("G_IMPCOL.CODTEC_"+pair.getKey().substring(pair.getKey().indexOf("DEL_G_IMPCOL_")+13)).getOriginalValue();
        }else {
          JdbcParametro paramOrigin = pair.getValue().getOriginalValue();
          String valOriginale = "";
          if (paramOrigin != null) {
            valOriginale = paramOrigin.toString();
          }
          JdbcParametro paramNuovo = pair.getValue().getValue();
          String valNuovo = "";
          if (paramNuovo != null) {
            valNuovo = paramNuovo.toString();
          }
          if ("".equals(valOriginale)) {
            valOriginale = "NULL";
          }
          if ("".equals(valNuovo)) {
            valNuovo = "NULL";
          }
          descrErrMsg += pair.getKey() + ": ";
          descrErrMsg += "valore vecchio= " + valOriginale + ", ";
          descrErrMsg += "valore nuovo= " + valNuovo;
        }
      }
    }
    return descrErrMsg;
  }

 }