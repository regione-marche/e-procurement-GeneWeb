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

import java.sql.SQLException;

import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;

import org.jfree.chart.JFreeChart;
import org.jfree.data.category.IntervalCategoryDataset;

/**
 * Gestore per la creazione di un GANTT
 * 
 * @since 1.4.0
 * @author Stefano.Sabbadin
 */
public abstract class AbstractGestoreGantt extends AbstractGestoreGrafico {

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
   * @return oggetto dell'interfaccia IntervalCategoryDataset contenente una
   *         collezione (classe TaskCollection) di serie (classe TaskSeries) con
   *         i singoli task (classe Task) temporali
   * @throws SQLException
   *         eccezione emessa nel caso di errori in fase di estrazione dei dati
   */
  public abstract IntervalCategoryDataset getDataset(JdbcParametro[] datiInput)
      throws SQLException;

  /**
   * @return etichetta da associare all'asse dei task/categorie
   */
  public abstract String getLabelAsseTask();

  /**
   * @return etichetta da associare all'asse temporale
   */
  public abstract String getLabelAsseTemporale();

}
