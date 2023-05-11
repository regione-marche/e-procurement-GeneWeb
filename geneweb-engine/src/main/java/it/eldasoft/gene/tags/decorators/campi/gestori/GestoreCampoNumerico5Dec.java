/*
 * Created on 18-ott-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.campi.gestori;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Vector;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
/**
 * @author Marcello Caminiti
 */

public class GestoreCampoNumerico5Dec extends AbstractGestoreCampo{

  public String getValore(String valore) {
    
    return null;
  }

  // imposto la visualizzazione a 5 cifre decimali fisse
  public String getValorePerVisualizzazione(String valore) {
    Double valoreNumerico = null;
    if(valore != null && valore.length() > 0)
        valoreNumerico = new Double(valore);
    else
        valoreNumerico = new Double(0);
    
    DecimalFormatSymbols simbols=new DecimalFormatSymbols();
    simbols.setDecimalSeparator(',');
    simbols.setGroupingSeparator('.');
    DecimalFormat decFormat=new DecimalFormat("###,##0.#####",simbols);
     
    String ret=decFormat.format(valoreNumerico.doubleValue());
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
    
    return null;
  }

  public String getClasseEdit() {
    
    return null;
  }

  public String getClasseVisua() {
    
    return null;
  }

  /**
     * Inizializzo il tipo numerico con 5 cifre decimali
     */
  protected void initGestore() {
    
    this.getCampo().setTipo("F10.5");
  }

  public String gestisciDaTrova(Vector params, DataColumn colWithValue, String conf, SqlManager manager) {
    return null;
  }

}
