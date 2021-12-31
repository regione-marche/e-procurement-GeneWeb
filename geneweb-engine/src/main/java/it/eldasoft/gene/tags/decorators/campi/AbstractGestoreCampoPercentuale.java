/*
 * Created on 08-feb-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.campi;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Vector;

/**
 * Classe di base per la definizione di gestori per percentuali
 * 
 * @author Stefano.Sabbadin
 */
public abstract class AbstractGestoreCampoPercentuale extends AbstractGestoreCampo {

  public String getValore(String valore) {
    return null;
  }

  public String getValorePerVisualizzazione(String valore) {
    // SS 30-09-2009: si modifica la visualizzazione in modo che, in analogia
    // agli importi, ci sia la virgola decimale

    if (valore == null || valore.length() == 0) return "";
    double value = new Double(valore).doubleValue();
        
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setDecimalSeparator(',');
    symbols.setGroupingSeparator('.');
    String pattern = "###,###,###,##0";
    if (this.getNumeroDecimali() > 0) {
      pattern += ".";
      for (int i = 0; i < this.getNumeroDecimali(); i++)
        pattern += "#";
    }
    DecimalFormat decFormat = new DecimalFormat(pattern, symbols);

    String ret = decFormat.format(value) + " %";
    return ret;
    
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
    String ret = null;
    if(this.campo.isVisibile())
      if (!visualizzazione) ret = " %";
    return ret;
  }

  public String getClasseEdit() {
    return null;
  }

  public String getClasseVisua() {
    return null;
  }

  protected void initGestore() {
    this.getCampo().setDominio("PRC", this.getPageContext());
  }

  public String gestisciDaTrova(Vector params,
      DataColumn colWithValue, String conf, SqlManager manager) {
    return null;
  }
  
  /**
   * Definisce il numero di decimali previsti nel campo percentuale
   * 
   * @return numero di decimali
   */
  public abstract int getNumeroDecimali();

}
