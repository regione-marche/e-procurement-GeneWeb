/*
 * Created on 27/lug/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.tags.gestori.submit;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;


public class GestoreTab2 extends AbstractGestoreEntita{

  @Override
  public String getEntita() {
    // TODO Auto-generated method stub
    return "TAB2";
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub
    String titolo=this.getRequest().getParameter("titolo");
    String cod=this.getRequest().getParameter("cod");
    String desc=this.getRequest().getParameter("desc");
    String tip = datiForm.getString("TAB2.TAB2TIP");
  
    String descrEvento = "";
    descrEvento = "Eliminazione dato tabellato '" + cod + " - " + titolo + "', " + "idf. " + tip + ".";
    
    LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
    logEvento.setLivEvento(1);
    logEvento.setOggEvento("");
    logEvento.setCodEvento("SET_TABELLATI");
    logEvento.setDescr(descrEvento);
    logEvento.setErrmsg("");
    LogEventiUtils.insertLogEventi(logEvento);
  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

    String titolo=this.getRequest().getParameter("titolo");
    String cod=this.getRequest().getParameter("cod");

    String desc = datiForm.getString("TAB2.TAB2D2");
    String tip = datiForm.getString("TAB2.TAB2TIP");
  
    String descrEvento = "";
    descrEvento = "Inserimento nuovo dato tabellato '" + cod + " - " + titolo + "', " + "idf. " + tip + ". Valore assegnato: " + desc;
    
    LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
    logEvento.setLivEvento(1);
    logEvento.setOggEvento("");
    logEvento.setCodEvento("SET_TABELLATI");
    logEvento.setDescr(descrEvento);
    logEvento.setErrmsg("");
    LogEventiUtils.insertLogEventi(logEvento);
    
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {


    String titolo=this.getRequest().getParameter("titolo");
    String cod=this.getRequest().getParameter("cod");

    String desc = datiForm.getString("TAB2.TAB2D2");
    String tip = datiForm.getString("TAB2.TAB2TIP");
  
    String descrEvento = "";
    descrEvento = "Modifica dato tabellato '" + cod + " - " + titolo + "', " + "idf. " + tip + ". Valore assegnato: " + desc;
    
    LogEvento logEvento = LogEventiUtils.createLogEvento(this.getRequest());
    logEvento.setLivEvento(1);
    logEvento.setOggEvento("");
    logEvento.setCodEvento("SET_TABELLATI");
    logEvento.setDescr(descrEvento);
    logEvento.setErrmsg("");
    LogEventiUtils.insertLogEventi(logEvento);
  }

}
