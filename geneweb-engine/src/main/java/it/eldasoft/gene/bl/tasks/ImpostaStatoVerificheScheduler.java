/*
 * Created on 03/03/2010
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
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

public class ImpostaStatoVerificheScheduler {

  static Logger       logger = Logger.getLogger(ImpostaStatoVerificheScheduler.class);

  private SqlManager  sqlManager;


  /**
   *
   * @param sqlManager
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }


  /**
   * Task attivato per verificare se ci sono attivit&agrave; in scadenza per le quali va generato il promemoria.
   *
   * @throws GestoreException
   */
  @SuppressWarnings("unchecked")
  public void impostaStatoVerificheArt80() throws GestoreException {

    // il task deve operare esclusivamente nel caso di applicativo attivo e
    // correttamente avviato ed inoltre deve esistere l'OP133
    if (WebUtilities.isAppNotReady()) return;
    ServletContext context = SpringAppContext.getServletContext();
    if (!GeneManager.checkOP(context, "OP133")) return;
    String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);

    ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);

    logger.debug("impostaStatoVerificheArt80: inizio metodo");

    // Lettura dei dati delle verifiche
    String selectDatiVerifiche = "select id,gg_avviso_scadenza,data_scadenza,stato_verifica" +
    		" from verifiche where gg_avviso_scadenza is not null and stato_verifica is not null  order by id";
    //and stato_verifica < ? new Long(3)

    List datiVerifiche = null;
    try {
      datiVerifiche = this.sqlManager.getListVector(selectDatiVerifiche, new Object[] {});
      if (datiVerifiche != null && datiVerifiche.size() > 0) {
        for (int k = 0; k < datiVerifiche.size(); k++) {
          Vector vect = (Vector) datiVerifiche.get(k);
          Long idVerifica = (Long) ((JdbcParametro) vect.get(0)).getValue();
          Long ggAvvisoscadenza = (Long) ((JdbcParametro) vect.get(1)).getValue();
          Date dataScadenza = (Date) ((JdbcParametro) vect.get(2)).getValue();
          try {
            Date dataOdierna = UtilityDate.getDataOdiernaAsDate();
            if(dataScadenza!=null){
              Calendar calendar = Calendar.getInstance();
              calendar.setTime(dataScadenza);
              if(ggAvvisoscadenza!=null){
                calendar.add(Calendar.DATE, -ggAvvisoscadenza.intValue());
                Date dataAvviso = calendar.getTime();
                if(dataOdierna.after(dataAvviso)){
                  this.sqlManager.update("update verifiche set stato_verifica = ? where id = ?", new Object[] {new Long(2), idVerifica});
                }
              }
              if(dataOdierna.after(dataScadenza)){
                this.sqlManager.update("update verifiche set stato_verifica = ? where id = ?", new Object[] {new Long(3), idVerifica});
              }
            }
          } catch (SQLException e) {
            logger.error("Errore durante la impostazione dello stato delle verifiche", e);
          }
        }
      }
    } catch (SQLException e) {
      throw new GestoreException("Errore nella lettura dei dati delle verifiche: " + codapp, null, e);
    }


    logger.debug("impostaStatoVerificheArt80: fine metodo");

  }


}
