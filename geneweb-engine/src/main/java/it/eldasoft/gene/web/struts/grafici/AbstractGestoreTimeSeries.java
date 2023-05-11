/*
 * Created on 2/feb/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.grafici;

import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;

import java.sql.SQLException;

import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;

/**
 * Gestore per la creazione di un grafico a linee su asse temporale
 * 
 * @since 1.4.0
 * @author Stefano.Sabbadin
 */
public abstract class AbstractGestoreTimeSeries extends AbstractGestoreGrafico {

  /**
   * Implementazione attualmente vuota del metodo
   * 
   * @see it.eldasoft.gene.web.struts.grafici.AbstractGestoreGrafico#customizeLayout(org.jfree.chart.JFreeChart)
   */
  public void customizeLayout(JFreeChart chart) {
  }

  /**
   * Estrae i dati per popolare il grafico a partire dai dati di input
   * 
   * @param datiInput
   *        parametri in input per l'estrazione dei dati
   * 
   * @return oggetto dell'interfaccia XYDataset contenente una
   *         collezione (classe TimeSeriesCollection) di serie (classe TimeSeries) con
   *         i singoli dati temporali
   * @throws SQLException
   *         eccezione emessa nel caso di errori in fase di estrazione dei dati
   */
  public abstract XYDataset getDataset(JdbcParametro[] datiInput)
      throws SQLException;

  /**
   * @return etichetta da associare all'asse dei dati
   */
  public abstract String getLabelAsseDati();

  /**
   * @return etichetta da associare all'asse temporale
   */
  public abstract String getLabelAsseTemporale();

}
