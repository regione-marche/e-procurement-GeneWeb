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

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.scadenz.ScadenzariManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.tags.functions.GeneralTagsFunction;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.springframework.transaction.TransactionStatus;

/**
 * Gestore di submit dell'entita' G_SCADENZ
 *
 * @author Marcello Caminiti
 */
public class GestoreG_SCADENZ extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "G_SCADENZ";
  }

  public GestoreG_SCADENZ() {
    super(false);
  }

  public GestoreG_SCADENZ(boolean isGestoreStandard) {
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

    ScadenzariManager scadenzariManager = (ScadenzariManager) UtilitySpring.getBean("scadenzariManager",
        this.getServletContext(), ScadenzariManager.class);

    String codapp = (String)this.getRequest().getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
    String  modello = UtilityStruts.getParametroString(this.getRequest(),"modello");
    if(modello!=null && !"".equals(modello)){
      //Inserimento attività da modello
      String entita = UtilityStruts.getParametroString(this.getRequest(),"entitaPartenza");
      String chiavi = UtilityStruts.getParametroString(this.getRequest(),"chiavi");
      if(chiavi!=null && !"".equals(chiavi)){

        //Si devono estrarre i valori di KEY1,KEY2,KEY3,KEY4,KEY5
        String key1=GeneralTagsFunction.getValCampo(chiavi, "KEY1");
        String key2=GeneralTagsFunction.getValCampo(chiavi, "KEY2");
        String key3=GeneralTagsFunction.getValCampo(chiavi, "KEY3");
        String key4=GeneralTagsFunction.getValCampo(chiavi, "KEY4");
        String key5=GeneralTagsFunction.getValCampo(chiavi, "KEY5");
        String discriminante = UtilityStruts.getParametroString(this.getRequest(),"discriminante");

        String select="select id from g_scadenz where ent='" + entita +"' and prev = 0";
        String condizioniChiavi="";
        Vector<String> valoriChiave = new Vector<String>();
        if(key1!=null && !"".equals(key1)){
          valoriChiave.add(0, key1);
          condizioniChiavi+= " and key1='" + key1 +"'";
        }
        if(key2!=null && !"".equals(key2)){
          valoriChiave.add(1, key2);
          condizioniChiavi+= " and key2='" + key2 +"'";
        }
        if(key3!=null && !"".equals(key3)){
          valoriChiave.add(2, key3);
          condizioniChiavi+= " and key3='" + key3 +"'";
        }
        if(key4!=null && !"".equals(key4)){
          valoriChiave.add(3, key4);
          condizioniChiavi+= " and key4='" + key4 +"'";
        }
        if(key5!=null && !"".equals(key5)){
          valoriChiave.add(4, key5);
          condizioniChiavi+= " and key5='" + key5 +"'";
        }

        select += condizioniChiavi;

        try {
          scadenzariManager.insertAttivitaScadenzarioDaModello(modello, entita, valoriChiave.toArray(), codapp);

          //Si deve impostare DATAFI con la data attuale
          this.getSqlManager().update("UPDATE G_SCADENZ SET DATAFI=? WHERE TIPOFI=1 AND PREV=0 " + condizioniChiavi,
              new Object[]{UtilityDate.getDataOdiernaAsDate()});

          //Si deve ciclare per le attività inserite e per ognuna si deve effettuare l'aggiornamentod delle date
          @SuppressWarnings("rawtypes")
          List listaNuoveAttivita= this.getSqlManager().getListVector(select, null);
          if(listaNuoveAttivita!=null && listaNuoveAttivita.size()>0){
            for(int i=0;i<listaNuoveAttivita.size();i++){
              Long idAttivita = SqlManager.getValueFromVectorParam(listaNuoveAttivita.get(i), 0).longValue();
              //Valorizzazione del campo G_SCADENZ.DISCR
              this.getSqlManager().update("update g_scadenz set discr=? where id=?", new Object[]{discriminante,idAttivita});
              scadenzariManager.updateDateScadenzarioEntita(entita, valoriChiave.toArray(), codapp, false, idAttivita);
            }

          }
          this.getRequest().setAttribute("modelloAssociato", "1");
        } catch (SQLException e) {
          throw new GestoreException("Errore durante l'inserimento delle attività",null, e);
        }
      }
    }else{
      //Inserimento attività da scheda
      DefaultGestoreEntitaChiaveNumerica gestore = new DefaultGestoreEntitaChiaveNumerica(
          "G_SCADENZ", "ID",null, this.getRequest());
      gestore.inserisci(status, datiForm);
      Long idAttivitaStart= datiForm.getLong("G_SCADENZ.ID");
      String ent = datiForm.getString("G_SCADENZ.ENT");
      String key1=datiForm.getString("G_SCADENZ.KEY1");
      String key2=datiForm.getString("G_SCADENZ.KEY2");
      String key3=datiForm.getString("G_SCADENZ.KEY3");
      String key4=datiForm.getString("G_SCADENZ.KEY4");
      String key5=datiForm.getString("G_SCADENZ.KEY5");

      //Object valoriChiave[] = new Object[]{key1,key2,key3,key4,key5};
      Vector<String> valoriChiave = new Vector<String>();
      if(key1!=null && !"".equals(key1))
        valoriChiave.add(0, key1);
      if(key2!=null && !"".equals(key2))
        valoriChiave.add(1, key2);
      if(key3!=null && !"".equals(key3))
        valoriChiave.add(2, key3);
      if(key4!=null && !"".equals(key4))
        valoriChiave.add(3, key4);
      if(key5!=null && !"".equals(key5))
        valoriChiave.add(4, key5);
      try {
        scadenzariManager.updateDateScadenzarioEntita(ent, valoriChiave.toArray(), codapp, false, idAttivitaStart);
      } catch (SQLException e) {
        throw new GestoreException("Errore durante l'aggiornamento delle date dell'attività:" + idAttivitaStart,null, e);
      }

    }


  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {
  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
      throws GestoreException {
    DefaultGestoreEntitaChiaveNumerica gestore = new DefaultGestoreEntitaChiaveNumerica(
        "G_SCADENZ", "ID",null, this.getRequest());
    gestore.update(status, datiForm);

    ScadenzariManager scadenzariManager = (ScadenzariManager) UtilitySpring.getBean("scadenzariManager",
        this.getServletContext(), ScadenzariManager.class);

    String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);

    Long idAttivitaStart= datiForm.getLong("G_SCADENZ.ID");
    String ent = datiForm.getString("G_SCADENZ.ENT");
    String key1=datiForm.getString("G_SCADENZ.KEY1");
    String key2=datiForm.getString("G_SCADENZ.KEY2");
    String key3=datiForm.getString("G_SCADENZ.KEY3");
    String key4=datiForm.getString("G_SCADENZ.KEY4");
    String key5=datiForm.getString("G_SCADENZ.KEY5");

    Vector<String> valoriChiave = new Vector<String>();
    if(key1!=null && !"".equals(key1))
      valoriChiave.add(0, key1);
    if(key2!=null && !"".equals(key2))
      valoriChiave.add(1, key2);
    if(key3!=null && !"".equals(key3))
      valoriChiave.add(2, key3);
    if(key4!=null && !"".equals(key4))
      valoriChiave.add(3, key4);
    if(key5!=null && !"".equals(key5))
      valoriChiave.add(4, key5);
    try {
      scadenzariManager.updateDateScadenzarioEntita(ent, valoriChiave.toArray(), codapp, false, idAttivitaStart);
    } catch (SQLException e) {
      throw new GestoreException("Errore durante l'aggiornamento delle date dell'attività:" + idAttivitaStart,null, e);
    }
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {
  }

}