/*
 * Created on 29/gen/2014
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
import java.util.Date;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.utility.UtilityDate;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore non standard che si occupa di gestire la funzionalità
 * di aggiornamento del campo datfin per la pagina uffint-lista.jsp
 *
 * @author Marcello Caminiti
 */

public class GestorePopupAttivazioneUffint extends AbstractGestoreEntita{

  @Override
  public String getEntita() {
    return "UFFINT";
  }

  public GestorePopupAttivazioneUffint() {
    super(false);
  }

  public GestorePopupAttivazioneUffint(boolean isGestoreStandard) {
    super(isGestoreStandard);
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    String codice = UtilityStruts.getParametroString(this.getRequest(),"codice");
    String operazione = UtilityStruts.getParametroString(this.getRequest(),"operazione");
    Date datfin=null;

    if("2".equals(operazione))
      datfin=UtilityDate.getDataOdiernaAsDate();

    try {
      this.sqlManager.update("update uffint set datfin=? where codein=?", new Object[]{datfin, codice});
    } catch (SQLException e) {
      this.getRequest().setAttribute("erroreOperazione", "1");
      throw new GestoreException("Errore nell'aggiornamento di UFFINT.DATFIN  della riga con UFFINT.CODEIN=" + codice, null, e);
    }



    //Se tutto è andato bene setto nel request il parametro operazioneEseguita = 1
    this.getRequest().setAttribute("operazioneEseguita", "1");

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

}
