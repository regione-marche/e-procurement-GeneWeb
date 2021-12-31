package it.eldasoft.gene.tags.decorators.campi.gestori;

import java.util.Vector;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;

/**
 * Gestore utilizzato per gestire inclusivo/esclusivo nelle protezioni
 * @author cit_franceschin
 *
 */
public class GestoreCampoInclusivo extends AbstractGestoreCampo {

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
   * Inizializzo il campo Inclusivo/Esclusivo
   */
  protected void initGestore() {
    // Dominio SN Lo tratto come un enumerato
    String tipo = this.getCampo().getTipo();
    if (tipo == null || tipo.length() == 0) tipo = "ET1";
    if (tipo.charAt(0) != 'E') tipo = "E" + tipo;
    this.getCampo().setTipo(tipo);
    this.getCampo().getValori().clear();
    this.getCampo().addValore("1", "Inclusivo");
    this.getCampo().addValore("0", "Esclusivo");
  }

  public String gestisciDaTrova(Vector params, DataColumn colWithValue, String conf, SqlManager manager) {
    return null;
  }

}
