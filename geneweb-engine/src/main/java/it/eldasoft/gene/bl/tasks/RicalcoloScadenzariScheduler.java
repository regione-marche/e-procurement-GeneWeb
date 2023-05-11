/*
 * Created on 24/05/13
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.tasks;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.scadenz.ScadenzariManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.SpringAppContext;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class RicalcoloScadenzariScheduler {

  static Logger           logger = Logger.getLogger(RicalcoloScadenzariScheduler.class);

  private SqlManager      sqlManager;

  private ScadenzariManager scadenzariManager;

  /**
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
  *
  * @param scadenzariManager
  */
 public void setScadenzariManager(ScadenzariManager scadenzariManager) {
   this.scadenzariManager = scadenzariManager;
 }

  /**
   *
   * @throws GestoreException
   */
  public void ricalcolaScadenzario() throws GestoreException {

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato ed inoltre deve esistere l'OP128
    if (WebUtilities.isAppNotReady()) return;
    ServletContext context = SpringAppContext.getServletContext();
    if(!GeneManager.checkOP(context, "OP128")) return;
    String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);

    if (logger.isDebugEnabled())
      logger.debug("ricalcolaScadenzario: inizio metodo");

    //Lettura dei dati dello scadenzario
    String select="select distinct id,ent,key1,key2,key3,key4,key5 from g_scadenz where "+
      " prg=? and prev=? and datacons is null order by id";

    List<?> datiG_SCADENZ=null;
    try {
      datiG_SCADENZ = this.sqlManager.getListVector(select,new Object[] { codapp, new Long((0)) });
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura dei dati dello scadenzario per l'applicativo: " + codapp, null, e);
    }

    if(datiG_SCADENZ!=null && datiG_SCADENZ.size() > 0){
      Long id=null;
      String ent=null;
      String key1=null;
      String key2=null;
      String key3=null;
      String key4=null;
      String key5=null;
      String errMsg="Errore durante il ricalcolo delle date per lo scadenzario " ;

      for (int i = 0; i < datiG_SCADENZ.size(); i++) {
        id = SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(i), 0).longValue();
        ent = SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(i), 1).stringValue();
        key1 = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(i), 2).stringValue());
        key2 = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(i), 3).stringValue());
        key3 = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(i), 4).stringValue());
        key4 = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(i), 5).stringValue());
        key5 = StringUtils.stripToNull(SqlManager.getValueFromVectorParam(datiG_SCADENZ.get(i), 6).stringValue());

        errMsg+= "(ENT=" + ent ;

        Vector<String> valoriChiave = new Vector<String>();
        if(key1!=null){
          valoriChiave.add(0, key1);
          errMsg+=",KEY1=" + key1 ;
        }

        if(key2!=null){
          valoriChiave.add(1, key2);
          errMsg+=",KEY2=" + key2 ;
        }

        if(key3!=null){
          valoriChiave.add(2, key3);
          errMsg+=",KEY3=" + key3 ;
        }

        if(key4!=null){
          valoriChiave.add(3, key4);
          errMsg+=",KEY4=" + key4 ;
        }

        if(key5!=null){
          valoriChiave.add(4, key5);
          errMsg+=",KEY5=" + key5 ;
        }
        errMsg+=")";

        try {
          this.scadenzariManager.updateDateScadenzarioEntita(ent, valoriChiave.toArray(), codapp, false, id);
        } catch (SQLException e) {

          logger.error(errMsg,e );
        }
      }
    }




    if (logger.isDebugEnabled())
      logger.debug("ricalcolaScadenzario: fine metodo");

  }



}
