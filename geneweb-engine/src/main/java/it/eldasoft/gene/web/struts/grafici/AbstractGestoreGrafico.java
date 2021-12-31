/*
 * Created on 27/gen/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.grafici;

import it.eldasoft.gene.bl.SqlManager;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;

/**
 * Classe di base per la realizzazione di un gestore grafico per l'estrazione
 * dei dati da usare nel grafico stesso, e per eventuali customizzazioni al
 * layout
 * 
 * @since 1.4.0
 * @author Stefano.Sabbadin
 */
abstract class AbstractGestoreGrafico {

  /** Manager standard per effettuare query SQL sul DB */
  private SqlManager sqlManager;

  /**
   * @return Ritorna sqlManager.
   */
  public SqlManager getSqlManager() {
    return sqlManager;
  }

  /**
   * @param sqlManager
   *        sqlManager da settare internamente alla classe.
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * Personalizza il layout del grafico
   * 
   * @param chart
   *        oggetto grafico da personalizzare
   */
  public abstract void customizeLayout(JFreeChart chart);
  
  public abstract void customizeTooltips(JFreeChart chart, ChartRenderingInfo info);

}
