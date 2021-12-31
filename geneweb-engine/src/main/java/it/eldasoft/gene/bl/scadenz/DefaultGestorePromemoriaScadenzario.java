/*
 * Created on 28/mag/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.scadenz;

import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.utils.utility.UtilityDate;

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

import org.apache.velocity.VelocityContext;

/**
 * Gestore di default che fornisce un messaggio generico slegato dal contesto applicativo e legato ai dati in G_SCADENZ.
 *
 * @author Marcello.Caminiti
 */
public class DefaultGestorePromemoriaScadenzario extends AbstractGestorePromemoriaScadenzario {

  @Override
  public String getModello() {
    return "promemoria-scadenzario-def.txt";
  }

  @Override
  public void popolaContesto(String codapp, Long idAttivita, String ent, Object[] chiavi, VelocityContext velocityContext)
      throws SQLException {

    // Nel template di default si devono riportare solo la data scadenza ed il titolo dell'attivita corrente, insieme alle informazioni del
    // record a cui appartiene lo scadenzario(ENT e KEY1,...)
    @SuppressWarnings("unchecked")
    Vector<JdbcParametro> datiAttivita = this.sqlManager.getVector("select datascad,tit from g_scadenz where id=?",
        new Object[] {idAttivita });
    String dataScadenza = "";
    String tit = "";
    if (datiAttivita != null && datiAttivita.size() > 0) {
      Date datascad = (Date) (datiAttivita.get(0)).getValue();
      if (datascad != null) {
        dataScadenza = UtilityDate.convertiData(datascad, UtilityDate.FORMATO_GG_MM_AAAA);
      }
      tit = (String) (datiAttivita.get(1)).getValue();
    }

    // Caricamento dei dati nel modello velocity
    velocityContext.put("DATASCAD", dataScadenza);
    velocityContext.put("TIT", tit);
    velocityContext.put("ENT", ent);
    velocityContext.put("KEY1", chiavi[0]);
    velocityContext.put("KEY2", chiavi[1]);
    velocityContext.put("KEY3", chiavi[2]);
    velocityContext.put("KEY4", chiavi[3]);
    velocityContext.put("KEY5", chiavi[4]);

  }
}
