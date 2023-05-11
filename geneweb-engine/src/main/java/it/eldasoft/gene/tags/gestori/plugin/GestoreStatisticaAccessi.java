/*
 * Created on 11/Gen/2011
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.gestori.plugin;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.BodyTagSupportGene;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestorePreload;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Plugin per la visualizzazione delle statistiche degli accessi all'applicativo
 * suddivise per anno e mese.
 *
 * @author Alberto.Mazzero
 * @since 1.5.4
 */
public class GestoreStatisticaAccessi extends AbstractGestorePreload {

  public GestoreStatisticaAccessi(BodyTagSupportGene tag) {
    super(tag);

  }

  @Override
  public void doAfterFetch(PageContext page, String modoAperturaScheda)
      throws JspException {
  }

  @Override
  public void doBeforeBodyProcessing(PageContext page, String modoAperturaScheda)
      throws JspException {

    SqlManager sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager",
        page, SqlManager.class);

    String codiceApplicazione = (String) page.getAttribute(
        CostantiGenerali.MODULO_ATTIVO, PageContext.SESSION_SCOPE);

    String castCampoData = sqlManager.getDBFunction("datetimetodate", new String[] {"dataora"});

    String query = "select min(dataora) from w_logeventi where codapp = ?";
    Calendar calendar = new GregorianCalendar();

    int annoStatistica = 0;
    int annoMinimo = 0;

    try {
      Date dataMinima = (Date) sqlManager.getObject(query,
          new Object[] { codiceApplicazione });
      if (dataMinima == null) {
        // aggiunto nel caso la tabella sia vuota. non dovrebbe mai arrivare qui
        dataMinima = new Date();
      }
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
      calendar.setTime(sdf.parse(sdf.format(dataMinima)));
      annoMinimo = calendar.get(GregorianCalendar.YEAR);
    } catch (Exception e) {
      throw new JspException(
          "Errore durante il calcolo delle statistiche - recupero data minima",
          e);
    }

    calendar.setTime(new Date());
    annoStatistica = calendar.get(GregorianCalendar.YEAR);

    Vector datiAnno = new Vector();

    while (annoStatistica >= annoMinimo) {
      Vector datiProfili = new Vector();
      String[] profili = this.elencoProfili(castCampoData, annoStatistica, codiceApplicazione,
          sqlManager);
      for (int i = 0; i < profili.length; i++) {
        Vector datiProfilo = new Vector();
        String profilo = profili[i];
        datiProfilo.add(profilo);
        int totale = 0; // calcolato per fare una query in meno...
        for (int mese = 1; mese <= 12; mese++) {
          String limiteDataInferioreCompresa = "01/" + UtilityStringhe.fillLeft("" + mese, '0', 2) + "/" + annoStatistica;
          String limiteDataSuperioreEsclusa = null;
          if (mese < 12) {
            limiteDataSuperioreEsclusa = "01/" + UtilityStringhe.fillLeft("" + (mese+1), '0', 2) + "/" + annoStatistica;
          } else {
            limiteDataSuperioreEsclusa = "01/01/" + (annoStatistica+1);
          }
          query = "select count(*) from w_logeventi where codapp = ? and cod_profilo = ? and codevento = ? and " + castCampoData + " >= ? and " + castCampoData + " < ?";
          try {
            Long accessi = (Long) sqlManager.getObject(query, new Object[] {
                codiceApplicazione, profilo, LogEventiUtils.COD_EVENTO_LOGIN, UtilityDate.convertiData(limiteDataInferioreCompresa, UtilityDate.FORMATO_GG_MM_AAAA)
                , UtilityDate.convertiData(limiteDataSuperioreEsclusa, UtilityDate.FORMATO_GG_MM_AAAA)});
            datiProfilo.add(accessi);
            totale += accessi.intValue();
          } catch (SQLException e) {
            throw new JspException(
                "Errore durante il calcolo delle statistiche - calcolo numero accessi - anno: "
                    + annoStatistica
                    + " mese: "
                    + mese, e);
          }
        }
        datiProfilo.add(new Long(totale));
        datiProfili.add(datiProfilo);
      }

      Vector datiAnnuali = new Vector();
      datiAnnuali.add(generaIntestazioneAnno(annoStatistica));
      datiAnnuali.add(datiProfili);

      datiAnno.add(datiAnnuali);

      annoStatistica--;
    }

    page.setAttribute("datiStatistiche", datiAnno);
  }

  /**
   * Metodo che ritorna l'elenco dei profili su cui recuperare il numero di
   * accessi. Messo a parte perchè potrebbe cambiare
   *
   * @param castCampoData funzione dipendente dal dbms per convertire un campo timestamp in data
   * @param anno anno di riferimento
   * @param codiceApplicazione applicativo
   * @param sqlManager manager per l'esecuzione query
   * @return elenco dei profili sul quale sono stati fatti accessi nell'anno
   * @throws JspException
   */
  private String[] elencoProfili(String castCampoData, int anno, String codiceApplicazione,
      SqlManager sqlManager) throws JspException {
    String[] profili = null;
    List listaProfili = null;

    String query = "select distinct cod_profilo from w_logeventi where codapp = ? and codevento = ? and " + castCampoData + " >= ? and " + castCampoData + " < ?";
    try {
      listaProfili = sqlManager.getListVector(
          query,
          new Object[] {codiceApplicazione, LogEventiUtils.COD_EVENTO_LOGIN, UtilityDate.convertiData("01/01/" + anno, UtilityDate.FORMATO_GG_MM_AAAA),
              UtilityDate.convertiData("01/01/" + (anno+1), UtilityDate.FORMATO_GG_MM_AAAA) });
    } catch (SQLException e) {
      throw new JspException(
          "Errore durante il calcolo delle statistiche - recupero elenco profili - anno: "
              + anno, e);
    }

    profili = new String[listaProfili.size()];
    for (int i = 0; i < listaProfili.size(); i++) {
      profili[i] = ((JdbcParametro) ((Vector) listaProfili.get(i)).get(0)).getStringValue();
    }

    return profili;
  }

  /**
   *
   * @param anno
   * @return ritorna un vector contenente le colonne per l'intestazione della
   *         tabella vuoto,mesi da gen a dicembre, anno
   *
   */
  private Vector generaIntestazioneAnno(int anno) {
    Vector intestazione = new Vector();
    intestazione.add(""); // colonna Profili

    Calendar calendar = new GregorianCalendar();
    SimpleDateFormat sdf = new SimpleDateFormat("MMM");

    // in GregorianCalendar i mesi partono da 0
    for (int mese = 0; mese < 12; mese++) {
      calendar.set(anno, mese, 1);
      intestazione.add(UtilityStringhe.capitalize(sdf.format(calendar.getTime()))); // colonna
                                                                                    // mese
    }

    intestazione.add(Integer.toString(anno)); // colonna totale

    return intestazione;
  }

}
