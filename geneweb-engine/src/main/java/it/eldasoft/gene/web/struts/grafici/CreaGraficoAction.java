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
import it.eldasoft.gene.commons.web.struts.ActionBaseNoOpzioni;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.spring.UtilitySpring;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.xy.XYDataset;

/**
 * Action per la generazione di un grafico mediante la libreria JFreeChart
 * 
 * @since 1.4.0
 * @author Stefano.Sabbadin
 */
public class CreaGraficoAction extends ActionBaseNoOpzioni {

  /** Logger Log4J di classe */
  static Logger               logger         = Logger.getLogger(CreaGraficoAction.class);

  /**
   * Nome dell'attributo da inserire nel request per usare un tag map
   * sull'immagine prodotta
   */
  private static final String MAP_HTML       = "mapHtml";
  /**
   * Nome dell'attributo da inserire nel request per indicare il nome
   * dell'immagine contenente il grafico prodotto
   */
  private static final String CHART_FILENAME = "chartFilename";

  /**
   * Esegue l'operazione di estrazione dei dati e creazione del grafico
   * rappresentante i dati.
   * 
   * @see it.eldasoft.gene.commons.web.struts.ActionBase#runAction(org.apache.struts.action.ActionMapping,
   *      org.apache.struts.action.ActionForm,
   *      javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  protected ActionForward runAction(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String target = CostantiGeneraliStruts.FORWARD_OK;

    CreaGraficoForm chartForm = (CreaGraficoForm) form;
    String messageKey = null;

    // si definisce il grafico, popolato con i dati mediante l'istanziazione del
    // gestore identificato nel form ricevuto
    JFreeChart chart = null;
    try {
      chart = CreaGraficoAction.getChart(request, chartForm.getTipo(),
          chartForm.getGestore(),
          UtilityTags.stringToArrayJdbcParametro(chartForm.getInputGestore()),
          chartForm.getTitolo(), chartForm.isLegenda(), chartForm.isTooltips());
      if (chart == null) {
        // TIPOLOGIA DI GRAFICO NON VALIDA
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.grafici.tipoGraficoNonPrevisto";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey, chartForm.getTipo());
      } else {
        // si eseguono eventuali personalizzazioni di layout presenti nel
        // grafico
        CreaGraficoAction.customizeChart(request, chartForm.getGestore(), chart);

        // si esegue la creazione dell'immagine nell'area temporanea e
        // l'eventuale creazione della mappa sull'immagine stessa
        String targetAppoggio = this.createImage(request, chartForm, chart);
        if (targetAppoggio != null) target = targetAppoggio;

      }
    } catch (SQLException e) {
      // DURANTE L'ESTRAZIONE DEI DATI SI SONO VERIFICATI DEGLI ERRORI
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.grafici.datiNonEstratti";
      logger.error(this.resBundleGenerale.getString(messageKey));
      this.aggiungiMessaggio(request, messageKey, chartForm.getTipo());
    } catch (Throwable e) {
      // A QUESTO LIVELLO SI TRACCIANO GLI ALTRI ERRORI DI TIPO INASPETTATO
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    return mapping.findForward(target);
  }

  /**
   * Effettua il rendering dell'immagine salvandola nell'area temporanea, nonchè
   * genera gli eventuali tooltip per l'immagine da visualizzare mediante
   * browser
   * 
   * @param request
   *        request HTTP
   * @param chartForm
   *        form contenente le informazioni inviate dalla pagina
   * @param chart
   *        grafico
   * @return target per il mapping valorizzato solo se si sono verificati
   *         problemi durante l'elaborazione
   */
  private String createImage(HttpServletRequest request,
      CreaGraficoForm chartForm, JFreeChart chart) {
    String target = null;
    String messageKey = null;
    ChartRenderingInfo info = new ChartRenderingInfo(
        new StandardEntityCollection());

    String filename = null;
    try {
      if (CostantiGrafici.FORMATO_IMMAGINE_PNG.equals(chartForm.getFormatoImmagine()))
        filename = ServletUtilities.saveChartAsPNG(chart,
            chartForm.getLarghezza().intValue(),
            chartForm.getAltezza().intValue(), info, request.getSession());
      else if (CostantiGrafici.FORMATO_IMMAGINE_JPG.equals(chartForm.getFormatoImmagine()))
        filename = ServletUtilities.saveChartAsJPEG(chart,
            chartForm.getLarghezza().intValue(),
            chartForm.getAltezza().intValue(), info, request.getSession());
      else {
        // FORMATO DI OUTPUT DELL'IMMAGINE NON VALIDO
        target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
        messageKey = "errors.grafici.formatoImmagine";
        logger.error(this.resBundleGenerale.getString(messageKey));
        this.aggiungiMessaggio(request, messageKey,
            chartForm.getFormatoImmagine());
      }
    } catch (IOException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      // ERRORE DURANTE LA GENERAZIONE DELL'IMMAGINE
      messageKey = "errors.grafici.immagineNonGenerata";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    }

    if (filename != null) {
      // set del nome del grafico per la visualizzazione nella popup
      request.setAttribute(CHART_FILENAME, filename);

      if (chartForm.isTooltips()) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        try {
          //customizza la generazione di tooltip html
          CreaGraficoAction.customizeTooltips(request, chartForm.getGestore(),
              chart, info);
          
          // Write the image map to the PrintWriter
          ChartUtilities.writeImageMap(pw, filename, info, false);
          pw.flush();

          // set dell'attibuto contenente il tag map da utilizzare nella popup
          // sui dati del grafico
          request.setAttribute(MAP_HTML, baos.toString());
        } catch (IOException e) {
          target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
          // ERRORE DURANTE LA GENERAZIONE DEI TOOLTIPS
          messageKey = "errors.grafici.tooltips";
          logger.error(this.resBundleGenerale.getString(messageKey), e);
          this.aggiungiMessaggio(request, messageKey);
        }
      }
    }

    return target;
  }

  /**
   * Factory per la generazione dell'oggetto grafico
   * 
   * @param request
   *        request HTTP
   * @param tipo
   *        tipo di grafico da generare
   * @param nomeGestore
   *        nome della classe, completa di package, di gestione per estrarre i
   *        dati da utilizzare nel grafico e per personalizzare eventualmente il
   *        layout; estende la classe AbstractGestoreGrafico
   * @param parametriGestore
   *        parametri da utilizzare nel gestore per l'estrazione dei dati dal db
   * @param titolo
   *        titolo del grafico
   * @param legenda
   *        si vuole la legenda?
   * @param tooltips
   *        si vogliono i tooltip sull'immagine nella popup?
   * @return grafico
   * @throws SQLException
   *         eccezione generata in fase di estrazione dei dati
   */
  public static JFreeChart getChart(HttpServletRequest request, String tipo,
      String nomeGestore, JdbcParametro[] parametriGestore, String titolo,
      boolean legenda, boolean tooltips) throws SQLException {
    if (CostantiGrafici.TIPO_GRAFICO_GANTT.equals(tipo))
      return getGanttChart(request, nomeGestore, parametriGestore, titolo,
          legenda, tooltips);
    else if (CostantiGrafici.TIPO_GRAFICO_SERIE_TEMPORALE.equals(tipo))
      return getTimeSeriesChart(request, nomeGestore, parametriGestore, titolo,
          legenda, tooltips);
    else
      return null;
  }

  /**
   * Genera un GANTT
   * 
   * @param nomeGestore
   *        nome della classe, completa di package, di gestione per estrarre i
   *        dati da utilizzare nel grafico e per personalizzare eventualmente il
   *        layout; estende la classe AbstractGestoreGantt
   * @param parametriGestore
   *        parametri da utilizzare nel gestore per l'estrazione dei dati dal db
   * @param titolo
   *        titolo del grafico
   * @param legenda
   *        si vuole la legenda?
   * @param tooltips
   *        si vogliono i tooltip sull'immagine nella popup?
   * @return grafico
   * @throws SQLException
   *         eccezione generata in fase di estrazione dei dati
   */
  private static JFreeChart getGanttChart(HttpServletRequest request,
      String nomeGestore, JdbcParametro[] parametriGestore, String titolo,
      boolean legenda, boolean tooltips) throws SQLException {
    AbstractGestoreGantt gestore = (AbstractGestoreGantt) UtilityTags.createObject(nomeGestore);
    gestore.setSqlManager((SqlManager) UtilitySpring.getBean("sqlManager",
        request.getSession().getServletContext(), SqlManager.class));
    IntervalCategoryDataset dataset = gestore.getDataset(parametriGestore);
    JFreeChart chart = GraficiFactory.createGanttChart(titolo,
        gestore.getLabelAsseTask(), gestore.getLabelAsseTemporale(), dataset,
        legenda, tooltips, false, TimeZone.getDefault(), request.getLocale());
    return chart;
  }

  /**
   * Genera un grafico di tipo TimeSeries
   * 
   * @param nomeGestore
   *        nome della classe, completa di package, di gestione per estrarre i
   *        dati da utilizzare nel grafico e per personalizzare eventualmente il
   *        layout; estende la classe AbstractGestoreTimeSeries
   * @param parametriGestore
   *        parametri da utilizzare nel gestore per l'estrazione dei dati dal db
   * @param titolo
   *        titolo del grafico
   * @param legenda
   *        si vuole la legenda?
   * @param tooltips
   *        si vogliono i tooltip sull'immagine nella popup?
   * @return grafico
   * @throws SQLException
   *         eccezione generata in fase di estrazione dei dati
   */
  private static JFreeChart getTimeSeriesChart(HttpServletRequest request,
      String nomeGestore, JdbcParametro[] parametriGestore, String titolo,
      boolean legenda, boolean tooltips) throws SQLException {
    AbstractGestoreTimeSeries gestore = (AbstractGestoreTimeSeries) UtilityTags.createObject(nomeGestore);
    gestore.setSqlManager((SqlManager) UtilitySpring.getBean("sqlManager",
        request.getSession().getServletContext(), SqlManager.class));
    XYDataset dataset = gestore.getDataset(parametriGestore);
    JFreeChart chart = GraficiFactory.createTimeSeriesChart(titolo,
        gestore.getLabelAsseDati(), gestore.getLabelAsseTemporale(), dataset,
        legenda, tooltips, false, TimeZone.getDefault(), request.getLocale());
    return chart;
  }

  
  /**
   * Factory per la personalizzazione del layout del grafico
   * 
   * @param request
   *        request HTTP
   * @param nomeGestore
   *        nome della classe, completa di package, di gestione per estrarre i
   *        dati da utilizzare nel grafico e per personalizzare eventualmente il
   *        layout; estende la classe AbstractGestoreGrafico
   * @param chart
   *        grafico da personalizzare
   */
  public static void customizeChart(HttpServletRequest request,
      String nomeGestore, JFreeChart chart) {
    AbstractGestoreGrafico gestore = (AbstractGestoreGrafico) UtilityTags.createObject(nomeGestore);
    gestore.setSqlManager((SqlManager) UtilitySpring.getBean("sqlManager",
        request.getSession().getServletContext(), SqlManager.class));
    gestore.customizeLayout(chart);
  }

  /**
   * Factory per la personalizzazione del layout del grafico
   * 
   * @param request
   *        request HTTP
   * @param nomeGestore
   *        nome della classe, completa di package, di gestione per estrarre i
   *        dati da utilizzare nel grafico e per personalizzare eventualmente il
   *        layout; estende la classe AbstractGestoreGrafico
   * @param chart
   *        grafico da personalizzare
   * @param info
   *        Info del grafico
   */
  public static void customizeTooltips(HttpServletRequest request,
      String nomeGestore, JFreeChart chart, ChartRenderingInfo info) {
    AbstractGestoreGrafico gestore = (AbstractGestoreGrafico) UtilityTags.createObject(nomeGestore);
    gestore.setSqlManager((SqlManager) UtilitySpring.getBean("sqlManager",
        request.getSession().getServletContext(), SqlManager.class));
    gestore.customizeTooltips(chart, info);
  }

}
