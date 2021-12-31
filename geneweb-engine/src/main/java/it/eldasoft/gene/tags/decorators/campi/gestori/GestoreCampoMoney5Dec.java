/*
 * Created on 24/set/08
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
import it.eldasoft.gene.tags.utils.UtilityTags;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Vector;

/**
 * Gestore di campi importo costituiti da almeno 2 decimali ed al più 5. In caso
 * di importi interi o con un solo decimale, viene effettuato un riempimento in
 * modo da visualizzare i 2 decimali
 * 
 * @author Stefano.Sabbadin
 */
public class GestoreCampoMoney5Dec extends AbstractGestoreCampo {

  public String getValore(String valore) {

    if (valore == null || valore.length() == 0) return "";
    double value = new Double(valore).doubleValue();
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setDecimalSeparator('.');
    symbols.setGroupingSeparator(' ');
    DecimalFormat decFormat = new DecimalFormat("################0.00###",
        symbols);

    return decFormat.format(value);
  }

  public String getValorePerVisualizzazione(String valore) {

    if (valore == null || valore.length() == 0) return "";
    double value = new Double(valore).doubleValue();
    // se l'importo è zero, allora si traduce in visualizzazione in spazio
    // vuoto. questo comportamento deriva dagli applicativi powerbuilder
    if (value == 0) return "";
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setDecimalSeparator(',');
    symbols.setGroupingSeparator('.');
    DecimalFormat decFormat = new DecimalFormat("###,###,###,##0.00###",
        symbols);

    String ret = decFormat.format(value) + "&nbsp;&euro;";
    if (ret.charAt(0) == '-')
      ret = "<span class=\"importoNegativo\">&#8209;"
          + ret.substring(1)
          + "</span>";
    else
      ret = "<span class=\"importo\">" + ret + "</span>";
    return ret;
  }

  public String getValorePreUpdateDB(String valore) {
    return null;
  }

  public String preHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  public String getHTML(boolean visualizzazione, boolean abilitato) {
    String result = null;
    String tipoPagina = (String) this.getPageContext().getRequest().getAttribute(
        UtilityTags.REQUEST_VAR_TIPO_PAGINA);

    if (!UtilityTags.PAGINA_LISTA.equalsIgnoreCase(tipoPagina)) {
      if (visualizzazione && !abilitato) {
        if (!UtilityTags.SCHEDA_MODO_VISUALIZZA.equals((String) this.getPageContext().getRequest().getAttribute(
            UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO))) {
          StringBuffer buf = new StringBuffer("<input type=\"text\" ");
          buf.append(UtilityTags.getHtmlAttrib("name",
              this.getCampo().getNome() + "edit"));
          buf.append(UtilityTags.getHtmlAttrib("id", this.getCampo().getNome()
              + "edit"));
          buf.append(UtilityTags.getHtmlAttrib("readOnly", "readOnly"));
          buf.append(UtilityTags.getHtmlAttrib("value",
              this.getCampo().getValue()));
          buf.append(UtilityTags.getHtmlAttrib("class", "importoNoEdit"));

          buf.append(UtilityTags.getHtmlAttrib("size",
              String.valueOf(this.getCampo().getLenForInput())));
          buf.append("/> &euro;");
          result = buf.toString();
        }
      }
    }
    return result;
  }

  public String postHTML(boolean visualizzazione, boolean abilitato) {
    String ret = null;
    if(this.campo.isVisibile())
      if (!visualizzazione) 
        ret = " &euro;";
    return ret;
  }

  public String getClasseEdit() {
    return null;
  }

  public String getClasseVisua() {
    return null;
  }

  /**
   * Inizializzo il tipo money come decimale a 5 cifre
   */
  protected void initGestore() {
    this.getCampo().setTipo("F15.5");
    this.getCampo().setDominio("MONEY5", this.getPageContext());
  }

  public String gestisciDaTrova(Vector params, DataColumn colWithValue,
      String conf, SqlManager manager) {
    return null;
  }

}
