package it.eldasoft.gene.tags.decorators.campi.gestori;

import java.util.Vector;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;

public class GestoreCampoDataElda extends AbstractGestoreCampo {

	/**
	 * Imposto il tipo data
	 */
	protected void initGestore() {
		this.getCampo().setTipo("D");
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

  public String gestisciDaTrova(Vector params, DataColumn colWithValue, String conf, SqlManager manager) {
    return null;
  }
	

}
