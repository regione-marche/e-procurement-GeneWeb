package it.eldasoft.gene.tags.decorators.campi.gestori;

import java.util.Vector;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo;

public class GestoreCampoSiNo extends AbstractGestoreCampo {

	public String getValore(String valore) {
		return null;
	}

	public String getValorePerVisualizzazione(String valore) {
		return null;
	}

	public String getValorePreUpdateDB(String valore) {
		return null;
	}

	public String preHTML(boolean visualizzazione,boolean abilitato) {
		return null;
	}

	public String getHTML(boolean visualizzazione,boolean abilitato) {
		return null;
	}

	public String postHTML(boolean visualizzazione,boolean abilitato) {
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
        this.getCampo().addValore("", "");
		this.getCampo().addValore("1", "Si");
		this.getCampo().addValore("2", "No");
	}

  public String gestisciDaTrova(Vector params, DataColumn colWithValue, String conf, SqlManager manager) {
    if("2".equals(colWithValue.getValue().getValue())){
      // Se ha no allora modifico il valore
      StringBuffer buf=new StringBuffer("");
      buf.append(colWithValue.getNomeFisico());
      buf.append(" = ? or ");
      buf.append(colWithValue.getNomeFisico());
      buf.append(" = ? or ");
      buf.append(colWithValue.getNomeFisico());
      buf.append(" is null");
      params.add(colWithValue.getValue().getValue());
      if(colWithValue.getTipoCampo()==JdbcParametro.TIPO_TESTO)
        params.add("0");
      else
        params.add(new Long(0));
      return buf.toString();
    }
    return null;
  }

}
