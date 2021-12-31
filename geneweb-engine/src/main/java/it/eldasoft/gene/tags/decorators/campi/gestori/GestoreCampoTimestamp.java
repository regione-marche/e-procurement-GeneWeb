package it.eldasoft.gene.tags.decorators.campi.gestori;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;

import java.util.Vector;
/**
 * Gestore di campi di dominio TIMESTAMP, ovvero date in formato GG/MM/AAAA HH:MI:SS in sola visualizzazione.
 *
 * @author Stefano.Sabbadin
 */
public class GestoreCampoTimestamp extends GestoreCampoDataElda {

	/**
	 * Imposto il tipo data
	 */
	@Override
  protected void initGestore() {
		this.getCampo().setTipo("D");
	}

	@Override
  public String getValore(String valore) {
		return null;
	}

	@Override
  public String getValorePerVisualizzazione(String valore) {
		return null;
	}

	@Override
  public String getValorePreUpdateDB(String valore) {
		return null;
	}

	@Override
  public String preHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

	@Override
  public String getHTML(boolean visualizzazione, boolean abilitato) {
		return null;
	}

	@Override
  public String postHTML(boolean visualizzazione, boolean abilitato) {
		return null;
	}

	@Override
  public String getClasseEdit() {
		return null;
	}

	@Override
  public String getClasseVisua() {
		return null;
	}

  @Override
  public String gestisciDaTrova(Vector params, DataColumn colWithValue, String conf, SqlManager manager) {
    return null;
  }


}
