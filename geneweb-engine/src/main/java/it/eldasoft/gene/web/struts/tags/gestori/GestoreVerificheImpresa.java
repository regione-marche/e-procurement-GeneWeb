/*
 * Created on 30/08/13
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.tags.gestori;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.verifiche.VerificheInterneManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard per le pagine ....jsp
 *
 * Questa classe NON e' un gestore standard e prepara i dati di ciascuna
 * occorrenza presente nella scheda e demanda alla classe DefaultGestoreEntita
 * le operazioni di insert, update e delete
 *
 * @author Cristian Febas
 */
public class GestoreVerificheImpresa extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "VERIFICHE";
  }

  public GestoreVerificheImpresa() {
    super(false);
  }

  /**
   * @param isGestoreStandard
   */

  public GestoreVerificheImpresa(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }




  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer impl)
      throws GestoreException {
  if(impl.isColumn("VERIFICHE.ID")) {
    Long id = impl.getLong("VERIFICHE.ID");
    try {
      //Cancellazione delle verifiche
      //prima bisogna cancellare le righe di W_DOCDIG eventualmente collegate ai documenti della verifica
      // ma non legati tramite fk
      List<?> listaDoc = this.getSqlManager().getListVector("select DOCUMENTI_VERIFICHE.ID,DOCUMENTI_VERIFICHE.IDDOCDG from DOCUMENTI_VERIFICHE" +
      		" where DOCUMENTI_VERIFICHE.ID_VERIFICA = ?", new Object[] {id});
      if (listaDoc != null && listaDoc.size() > 0) {
        for (int i = 0; i < listaDoc.size(); i++) {
          Long iddocdg = (Long) SqlManager.getValueFromVectorParam(listaDoc.get(i), 1).getValue();
          if(iddocdg != null){
            this.getSqlManager().update("delete from W_DOCDIG where IDPRG = ? and IDDOCDIG = ? and digent=?", new Object[] {"PG", iddocdg, "DOCUMENTI_VERIFICHE"});
          }
        }
      }

      String delete="delete from verifiche where id=?";

      this.sqlManager.update(delete,  new Object[] {id });
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException("Errore nella cancellazione di ANTICORLOTTI e tabelle figlie", null, e);
    }
  }


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

    VerificheInterneManager verificheInterneManager = (VerificheInterneManager) UtilitySpring.getBean(
        "verificheInterneManager", this.getServletContext(), VerificheInterneManager.class);

    String ufficioIntestatario = null;
    HttpSession session = this.getRequest().getSession();
    if (session != null) {
      ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
    }


    Long idVerifica = null;

    int numeroVerifiche = 0;
    String numVerifiche = this.getRequest().getParameter("numeroVerifiche");
    if(numVerifiche != null && numVerifiche.length() > 0){
      numeroVerifiche =  UtilityNumeri.convertiIntero(numVerifiche).intValue();
    }


    for (int i = 1; i <= numeroVerifiche; i++) {
      DataColumnContainer dataColumnContainerDiRiga = new DataColumnContainer(
      		impl.getColumnsBySuffix("_" + i, false));

      try {
        if (dataColumnContainerDiRiga.isModifiedTable("VERIFICHE")) {
          idVerifica = dataColumnContainerDiRiga.getLong("VERIFICHE.ID");
          Long origEsito = (Long) dataColumnContainerDiRiga.getColumn("VERIFICHE.ESITO_VERIFICA").getOriginalValue().getValue();
          Long newEsito = (Long) dataColumnContainerDiRiga.getColumn("VERIFICHE.ESITO_VERIFICA").getValue().getValue();
          dataColumnContainerDiRiga.update("VERIFICHE", sqlManager);
          if(origEsito==null || (new Long(3).equals(origEsito) || new Long(4).equals(origEsito)) && newEsito!= null){
            //calcolo le scadenze
            verificheInterneManager.calcolaScadenzeVerifiche(idVerifica.intValue(), 0, null, null, null, null, null, null, null);
          }
          if(origEsito !=null && (newEsito == null || new Long(3).equals(newEsito) || new Long(4).equals(newEsito))){
            this.sqlManager.update("update VERIFICHE set DATA_ULTIMA_RICHIESTA = null,DATA_SILENZIO_ASSENSO = null," +
            		" DATA_ULTIMA_CERTIFICAZIONE = null, DATA_SCADENZA = null, STATO_VERIFICA = null where ID =? ", new Object[] {idVerifica});
          }
        }
      } catch (SQLException e) {
           throw new GestoreException("Errore nell'aggiornamento dei dati in VERIFICHE",null, e);
      }
    }


  }

  @Override
  public void postUpdate(DataColumnContainer impl) throws GestoreException {
  }

}