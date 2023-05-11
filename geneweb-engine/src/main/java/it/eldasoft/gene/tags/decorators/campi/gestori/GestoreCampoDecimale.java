/*
 * Created on 30-set-2009
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
import it.eldasoft.utils.utility.UtilityStringhe;

import java.util.Vector;

/**
 * Classe di base per la definizione di gestori per campi decimali non di
 * dominio importo o percentuale
 * 
 * @author Stefano.Sabbadin
 */
public class GestoreCampoDecimale extends AbstractGestoreCampo {

  public String getValore(String valore) {
    return null;
  }

  public String getValorePerVisualizzazione(String valore) {
    if (valore == null || valore.length() == 0) return null;
    // in visualizzazione si sostituisce il punto decimale con la virgola
    return UtilityStringhe.replace(valore, ".", ",");
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
  }

  public String gestisciDaTrova(Vector params, DataColumn colWithValue,
      String conf, SqlManager manager) {
    return null;
  }

}
