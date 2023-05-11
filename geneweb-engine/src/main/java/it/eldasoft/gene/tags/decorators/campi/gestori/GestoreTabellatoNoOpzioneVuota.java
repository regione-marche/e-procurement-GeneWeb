/*
 * Created on 08/nov/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.campi.gestori;

import java.util.Vector;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;
import it.eldasoft.gene.tags.decorators.campi.ValoreTabellato;

/**
 * Gestore del campo per la visualizzazione di un tabellato in modifica senza
 * l'opzione vuota
 * 
 * @author Marco.Franceschin
 */
public class GestoreTabellatoNoOpzioneVuota extends AbstractGestoreCampo {

  public String getClasseEdit() {
    return null;
  }

  public String getClasseVisua() {
    return null;
  }

  public String getHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  public String getValore(String valore) {
    return null;
  }

  public String getValorePerVisualizzazione(String valore) {
    return null;
  }

  public String getValorePreUpdateDB(String valore) {
    return null;
  }

  protected void initGestore() {
  }

  public String postHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  public String preHTML(boolean visualizzazione, boolean abilitato) {
    // Elimino il valore vuoto se esistente
    ValoreTabellato opzioneVuota = new ValoreTabellato("", "");
    int posizione = this.getCampo().getValori().indexOf(opzioneVuota);
    if (posizione >= 0) this.getCampo().getValori().remove(posizione);
    return null;
  }

  public String gestisciDaTrova(Vector params,
      DataColumn colWithValue, String conf, SqlManager manager) {
    return null;
  }

}
