/*
 * Created on 28/gen/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.grafici;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.TimeZone;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.IntervalCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.urls.StandardCategoryURLGenerator;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.xy.XYDataset;

/**
 * Factory di estensione della ChartFactory in quanto la ChartFactory non
 * fornisce dei metodi per creare oggetti grafici esattamente secondo le
 * esigenze Eldasoft, ovvero indipendenti dalla localizzazione.
 * 
 * @author Stefano.Sabbadin
 */
public class GraficiFactory extends ChartFactory {

  /*
   * Rispetto alla versione presa dalla ChartFactory, questo metodo prevede
   * l'utilizzo di due parametri in più per settare l'asse delle date in base
   * alla localizzazione. Inoltre, la data viene stampata negli eventuali
   * tooltip con un formato MEDIUM e non con il formato di default dell'ambiente
   * in cui si manda in esecuzione questo codice (eventualmente lato server,
   * quindi potenzialmente su un sistema linux localizzato in inglese quando gli
   * utilizzatori sono con browser in italiano su sistema Windows)
   */

  /**
   * Creates a Gantt chart using the supplied attributes plus default values
   * where required. The chart object returned by this method uses a
   * {@link CategoryPlot} instance as the plot, with a {@link CategoryAxis} for
   * the domain axis, a {@link DateAxis} (with timezone and locale set) as the
   * range axis, and a {@link GanttRenderer} as the renderer.
   * 
   * @param title
   *        the chart title (<code>null</code> permitted).
   * @param categoryAxisLabel
   *        the label for the category axis (<code>null</code> permitted).
   * @param dateAxisLabel
   *        the label for the date axis (<code>null</code> permitted).
   * @param dataset
   *        the dataset for the chart (<code>null</code> permitted).
   * @param legend
   *        a flag specifying whether or not a legend is required.
   * @param tooltips
   *        configure chart to generate tool tips?
   * @param urls
   *        configure chart to generate URLs?
   * @param zone
   *        the timezone for the date axis (<code>null</code> permitted)
   * @param locale
   *        the locale for the date axis (<code>null</code> permitted)
   * 
   * @return A Gantt chart.
   */
  public static JFreeChart createGanttChart(String title,
      String categoryAxisLabel, String dateAxisLabel,
      IntervalCategoryDataset dataset, boolean legend, boolean tooltips,
      boolean urls, TimeZone zone, Locale locale) {

    CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
    DateAxis dateAxis = new DateAxis(dateAxisLabel, zone, locale);

    CategoryItemRenderer renderer = new GanttRenderer();
    if (tooltips) {
      // date format depends on locale
      renderer.setBaseToolTipGenerator(new IntervalCategoryToolTipGenerator(
          "{3} - {4}", DateFormat.getDateInstance(DateFormat.MEDIUM, locale)));
    }

    if (urls) {
      renderer.setBaseItemURLGenerator(new StandardCategoryURLGenerator());
    }

    CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, dateAxis,
        renderer);
    plot.setOrientation(PlotOrientation.HORIZONTAL);
    JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
        plot, legend);
    getChartTheme().apply(chart);
    return chart;
  }

  /**
   * Creates and returns a time series chart. A time series chart is an
   * {@link XYPlot} with a {@link DateAxis} for the x-axis and a
   * {@link NumberAxis} for the y-axis. The default renderer is an
   * {@link XYLineAndShapeRenderer}.
   * <P>
   * A convenient dataset to use with this chart is a
   * {@link org.jfree.data.time.TimeSeriesCollection}.
   * 
   * @param title
   *        the chart title (<code>null</code> permitted).
   * @param timeAxisLabel
   *        a label for the time axis (<code>null</code> permitted).
   * @param valueAxisLabel
   *        a label for the value axis (<code>null</code> permitted).
   * @param dataset
   *        the dataset for the chart (<code>null</code> permitted).
   * @param legend
   *        a flag specifying whether or not a legend is required.
   * @param tooltips
   *        configure chart to generate tool tips?
   * @param urls
   *        configure chart to generate URLs?
   * @param zone
   *        the timezone for the date axis (<code>null</code> permitted)
   * @param locale
   *        the locale for the date axis (<code>null</code> permitted)
   * 
   * @return A time series chart.
   */
  public static JFreeChart createTimeSeriesChart(String title,
      String timeAxisLabel, String valueAxisLabel, XYDataset dataset,
      boolean legend, boolean tooltips, boolean urls, TimeZone zone,
      Locale locale) {

    // time axis depends on locale
    ValueAxis timeAxis = new DateAxis(timeAxisLabel, zone, locale);
    timeAxis.setLowerMargin(0.02); // reduce the default margins
    timeAxis.setUpperMargin(0.02);
    // number axis depends on locale
    NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
    valueAxis.setNumberFormatOverride(NumberFormat.getInstance(locale));
    valueAxis.setAutoRangeIncludesZero(false); // override default
    XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, null);

    XYToolTipGenerator toolTipGenerator = null;
    if (tooltips) {
      toolTipGenerator = new StandardXYToolTipGenerator(
          StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT.replace(',', ';'),
          DateFormat.getDateInstance(DateFormat.MEDIUM, locale),
          NumberFormat.getInstance(locale));
    }

    XYURLGenerator urlGenerator = null;
    if (urls) {
      urlGenerator = new StandardXYURLGenerator();
    }

    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
    renderer.setBaseToolTipGenerator(toolTipGenerator);
    renderer.setURLGenerator(urlGenerator);
    plot.setRenderer(renderer);

    JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
        plot, legend);
    getChartTheme().apply(chart);
    return chart;

  }
}
