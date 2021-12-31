/*
 * Created on Nov 14, 2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.campi.gestori;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

public class GestoreCampoProvincia extends AbstractGestoreCampo {

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(GestoreCampoProvincia.class);

  public String getValore(String valore) {
    return null;
  }

  public String getValorePerVisualizzazione(String valore) {
    return null;
  }

  public String getValorePreUpdateDB(String valore) {
    return null;
  }

  public String preHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  public String getHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  public String postHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  public String getClasseEdit() {
    return null;
  }

  public String getClasseVisua() {
    return null;
  }

  protected void initGestore() {
    // Inserisco tutti i dati
    // Dominio SN lo tratto come un enumerato
    this.getCampo().setTipo("ET2");
    this.getCampo().getValori().clear();
    this.getCampo().addValore("", "");
    // Estraggo il manager per gestire diversi SQL
    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
        "sqlManager", this.getServletContext(), SqlManager.class);
    try {
      List lista = sqlManager.getListVector(
          "select tabcod3, tabdesc from tabsche where tabsche.tabcod = 'S2003' and tabsche.tabcod1='07'  order by tabdesc",
          new Object[] {});
      this.getCampo().addValore("","");
      for (int i = 0; i < lista.size(); i++) {
        Vector riga = (Vector) lista.get(i);
        this.getCampo().addValore(riga.get(0).toString(),
            riga.get(1).toString());

      }
    } catch (SQLException e) {
      logger.error(this.resBundleGenerale.getString("errors.database.dataAccessException"), e);
    }
  }

  public String gestisciDaTrova(Vector params, DataColumn colWithValue, String conf, SqlManager manager) {
    return null;
  }

}
