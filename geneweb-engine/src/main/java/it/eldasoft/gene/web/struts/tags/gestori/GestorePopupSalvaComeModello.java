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
import it.eldasoft.gene.bl.scadenz.ScadenzariManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.Vector;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore di submit della popup Notifica promemoria
 *
 * @author Marcello Caminiti
 */
public class GestorePopupSalvaComeModello extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "G_MODSCADENZ";
  }

  public GestorePopupSalvaComeModello() {
    super(false);
  }

  public GestorePopupSalvaComeModello(boolean isGestoreStandard) {
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

    ScadenzariManager scadenzariManager = (ScadenzariManager) UtilitySpring.getBean("scadenzariManager",
        this.getServletContext(), ScadenzariManager.class);


    String  ent = UtilityStruts.getParametroString(this.getRequest(),"ent");
    String  discriminante = UtilityStruts.getParametroString(this.getRequest(),"discriminante");
    String chiave = UtilityStruts.getParametroString(this.getRequest(),"chiave");
    String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);

    String cod = datiForm.getString("COD");
    String tit = datiForm.getString("TIT");
    String desc = datiForm.getString("DESCR");

    try {
      //Si deve controllare se aggiornare o inserire
      Long numOccorrenze= (Long)sqlManager.getObject("select count(cod) from g_modscadenz where cod=?", new Object[]{cod});
      if(numOccorrenze!=null && numOccorrenze.longValue()==0)
        sqlManager.update("insert into g_modscadenz(cod,prg,ent,discr,tit,descr) values(?,?,?,?,?,?)", new Object[]{cod,codapp,ent,discriminante,tit,desc});
      else{
        sqlManager.update("update g_modscadenz set tit=?,descr=? where cod=?", new Object[]{tit,desc,cod});
        sqlManager.update("delete from  g_dettmodscadenz where cod=?", new Object[]{cod});
      }
      //Popolamento dell'entità G_DETTMODSCADENZ
      if(chiave!=null && !"".equals(chiave)){
        String campiKey[] = chiave.split(";");
        Vector<String> valoriChiave = new Vector<String>();
        for(int i=0;i<campiKey.length;i++){
          String key = campiKey[i].substring(campiKey[i].indexOf(':')+1);
          if(key!=null && !"".equals(key))
            valoriChiave.add(i, key);
        }
        scadenzariManager.insertAttivitaModelloDaScadenzario(ent, valoriChiave.toArray(), codapp, cod);

      }

    } catch (SQLException e) {
      throw new GestoreException("Errore durante il salvataggio del modello:" + cod,null, e);
    }
    this.getRequest().setAttribute("salvataggioModelloEseguito", "1");




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