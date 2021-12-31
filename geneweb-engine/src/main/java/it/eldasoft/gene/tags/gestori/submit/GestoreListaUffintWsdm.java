/*
 * Created on 03/mar/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.tags.gestori.submit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;


public class GestoreListaUffintWsdm  extends AbstractGestoreEntita{

  @Override
  public String getEntita() {
    // TODO Auto-generated method stub
    return "WSDMCONFIUFF";
  }

  @Override
  public void postDelete(DataColumnContainer arg0) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void postInsert(DataColumnContainer arg0) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void postUpdate(DataColumnContainer arg0) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void preDelete(TransactionStatus arg0, DataColumnContainer arg1) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void preInsert(TransactionStatus arg0, DataColumnContainer arg1) throws GestoreException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void preUpdate(TransactionStatus arg0, DataColumnContainer arg1) throws GestoreException {
    // TODO Auto-generated method stub
    String[] listaUffintSelezionati = this.getRequest().getParameterValues("keys");
    String idconfi=this.getRequest().getParameter("idconfi");
    String codapp=this.getRequest().getParameter("codapp");
   
    String uffintToDelete = "";
    String uffintDaControllare = "";
    ArrayList<String> uffintToAdd = new ArrayList<String>();
    
    ArrayList<String> currentUffint = new ArrayList<String>();
    ArrayList<String> newUffint = new ArrayList<String>();
    
    
    List currentData;
    try {
      currentData = sqlManager.getListVector("select CODEIN from WSDMCONFIUFF where IDCONFI = ?", new Object[] {new Long(idconfi)});
    
      for(int i=0;i<currentData.size();i++){
        String codein = (String) SqlManager.getValueFromVectorParam(currentData.get(i), 0).getValue();
        currentUffint.add(codein);
      }
      if(listaUffintSelezionati != null){
        for(int i=0;i<listaUffintSelezionati.length;i++){
          String codein = listaUffintSelezionati[i];
          newUffint.add(codein);
          if(!"".equals(uffintDaControllare)){
            uffintDaControllare+=", ";
          }
          uffintDaControllare+="'"+codein+"'";
        }
      }
      if(!"".equals(uffintDaControllare)){
        Long count = (Long) sqlManager.getObject("select count(*) from WSDMCONFIUFF U, WSDMCONFI W where U.IDCONFI = W.ID and U.IDCONFI != ? and W.CODAPP = ? and U.CODEIN in( "+ uffintDaControllare +" )", new Object[] {idconfi,codapp});
        if(count.intValue()>0){
          this.getRequest().setAttribute("modalita", "modifica");
          throw new GestoreException("Gli uffici intestatari selezionati sono già associati ad altre configurazioni", "wsdm.uffintGiaAssociato", new Exception());
        }
      }
      
      //Aggiungo gli uffint che ancora non sono presenti in DB
      for(int i=0;i<newUffint.size();i++){
        String codein = newUffint.get(i);
        if(!currentUffint.contains(codein)){
          uffintToAdd.add(codein);
        }
      }
      
      //Cancello gli uffint che erano presenti in DB ma sono stati deselezionati
      for(int i=0;i<currentUffint.size();i++){
        String codein = currentUffint.get(i);
        if(!newUffint.contains(codein)){
          if(!"".equals(uffintToDelete)){
            uffintToDelete+=", ";
          }
          uffintToDelete+="'"+codein+"'";
        }
      }
      
      if(!"".equals(uffintToDelete)){
        this.sqlManager.update("delete from WSDMCONFIUFF where idconfi = ? and codein in ( "+ uffintToDelete +" )", new Object[] { new Long(idconfi) });
      }
      
      for(int i=0;i<uffintToAdd.size();i++){
        String codein = uffintToAdd.get(i);
        this.sqlManager.update("insert into WSDMCONFIUFF (idconfi, codein) values (?,?)", new Object[] {new Long(idconfi), codein});
      }
      
    } catch (SQLException e) {
      this.getRequest().setAttribute("modalita", "modifica");
      throw new GestoreException("Errore durante il salvataggio delle associazione WSDMCONFI-UFFINT", "wsdm.erroreGenerico", e);
    }
    
  }

}





