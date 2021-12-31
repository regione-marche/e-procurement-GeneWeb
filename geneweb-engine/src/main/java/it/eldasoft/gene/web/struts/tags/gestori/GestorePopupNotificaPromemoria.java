/*
 * Created on 13/05/2013
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
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore di submit della popup Notifica promemoria
 *
 * @author Marcello Caminiti
 */
public class GestorePopupNotificaPromemoria extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "G_SCADENZ";
  }

  public GestorePopupNotificaPromemoria() {
    super(false);
  }

  public GestorePopupNotificaPromemoria(boolean isGestoreStandard) {
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

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getServletContext(), SqlManager.class);

    String  listaId = UtilityStruts.getParametroString(this.getRequest(),"id");
    if(listaId!=null && !"".equals(listaId)){
      Long ggpromem = datiForm.getLong("GGPROMEM");
      String destpromem = datiForm.getString("DESTPROMEM");
      String refpromem= datiForm.getString("REFPROMEM");

      String[] vettoreId = listaId.split(";");
      for(int i=0; i< vettoreId.length;i++){
        String  id = vettoreId[i];
        try {
          sqlManager.update("update g_scadenz set GGPROMEM=?, DESTPROMEM=?, REFPROMEM=?, STPROMEM=0 where id=?", new Object[]{ggpromem,destpromem, refpromem, new Long(id)});
        } catch (SQLException e) {
          throw new GestoreException("Errore durante l'aggiornamento del promemoria dell'attività:" + id,null, e);
        }
      }
      this.getRequest().setAttribute("aggiornamentoNotificaEseguita", "1");

    }


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