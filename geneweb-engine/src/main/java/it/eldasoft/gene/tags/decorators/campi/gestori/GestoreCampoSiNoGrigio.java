package it.eldasoft.gene.tags.decorators.campi.gestori;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;

import java.util.Vector;

/**
 * Gestore campo associato ad un checkbox a 3 stati (checkato, non checkato,
 * disabilitato) come combobox con valori si, no, stringa vuota
 * 
 * @author cit_defilippis
 * 
 */
public class GestoreCampoSiNoGrigio extends AbstractGestoreCampo {

  public String getValore(String valore) {
    return null;
  }

  public String getValorePerVisualizzazione(String valore) {
    return null;
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
   * Inizializzo il campo Si/No
   */
  protected void initGestore() {
    // Dominio SN Lo tratto come un enumerato
    this.getCampo().setTipo("E" + this.getCampo().getTipo());
    this.getCampo().getValori().clear();
    this.getCampo().addValore("0", "");
    this.getCampo().addValore("1", "Si");
    this.getCampo().addValore("2", "No");
  }

  public String gestisciDaTrova(Vector params,
      DataColumn colWithValue, String conf, SqlManager manager) {

    return null;
  }

}
