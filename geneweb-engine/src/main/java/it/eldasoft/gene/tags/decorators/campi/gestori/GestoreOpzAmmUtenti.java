package it.eldasoft.gene.tags.decorators.campi.gestori;

import java.util.Vector;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;

/**
 * Gestore campo che memorizza i privilegi di amministrazione utenti applicativo
 * 
 * @author Stefano.Sabbadin - Eldasoft S.p.A. Treviso
 */
public class GestoreOpzAmmUtenti extends AbstractGestoreCampo {

  public String gestisciDaTrova(Vector params, DataColumn col, String conf,
     SqlManager manager) {
    return null;
  }

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
    this.getCampo().setTipo("ET2");
    this.getCampo().getValori().clear();
    this.getCampo().addValore("", "Nessun privilegio");
    this.getCampo().addValore("ou11|ou12", "Sola lettura");
    this.getCampo().addValore("ou11", "Gestione completa");
  }

  public String postHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  public String preHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

}
