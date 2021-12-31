package it.eldasoft.gene.tags.decorators.scheda;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.db.sql.sqlparser.JdbcSqlSelect;
import it.eldasoft.gene.tags.decorators.lista.CampoListaTagImpl;

import java.util.Vector;

import javax.servlet.jsp.JspException;

/**
 * Classe che gestisce i campi che non sono di un'entita
 *
 * @author cit_franceschin
 *
 */
public class CampiNonDiEntita {
	public class EntitaLocal {
		private String entita;

		public JdbcSqlSelect select;

		Vector valori;

		public EntitaLocal(String entita) {
			if (entita == null)
				throw new RuntimeException(
						"EntitaLocal: il nome dell'entita non può essere vuota");
			this.entita = entita;
			this.select = new JdbcSqlSelect();
			valori = null;
		}

		public String getEntita() {
			return entita;
		}

		public void leggi(SqlManager sql) throws JspException {
			try {
				this.valori = sql.getVector(this.select);
			} catch (Throwable t) {
				throw new JspException(
						"Errore in lettura dell'entità non principale: \n"
								+ t.getMessage(), t);
			}

		}

		public String getValue(int numeroCampo) {
			if (valori != null && numeroCampo >= 0
					&& numeroCampo < this.valori.size())
				return this.valori.get(numeroCampo).toString();
			return "";

		}

		public void setParametri(Vector list) {
			Vector param = this.select.getParemetri();
			for (int i = 0; i < param.size(); i++) {
				if (i >= list.size())
					break;
				JdbcParametro lParToSet = (JdbcParametro) param.get(i);
				JdbcParametro lPar = (JdbcParametro) list.get(i);
				lParToSet.set(lPar);
			}

		}
	}

	private Vector elencoEntita;

	public CampiNonDiEntita() {
		this.elencoEntita = new Vector();
	}

	public EntitaLocal addEntita(String ent) {
		EntitaLocal lEnt = new EntitaLocal(ent);
		elencoEntita.add(lEnt);
		return lEnt;
	}

	public EntitaLocal getEntitaEsterna(String entita) {
		if (entita != null) {
			for (int i = 0; i < elencoEntita.size(); i++) {
				EntitaLocal lEnt = (EntitaLocal) elencoEntita.get(i);
				if (lEnt.getEntita().compareToIgnoreCase(entita) == 0)
					return lEnt;
			}

		}
		return null;
	}

	public void leggiTutti(SqlManager sql) throws JspException {
		for (int i = 0; i < elencoEntita.size(); i++) {
			EntitaLocal lEnt = (EntitaLocal) elencoEntita.get(i);
			lEnt.leggi(sql);
		}

	}

	/**
	 * Funzione che estrae il valore
	 *
	 * @param campo
	 * @return
	 */
	public String getValue(CampoSchedaTagImpl campo) {
		EntitaLocal ent = this.getEntitaEsterna(campo.getEntita());
		if (ent != null) {
			return ent.getValue(campo.getNumeroCampo());
		}
		return "";
	}

	/**
	 * Esegue la lettura di tutti impostando i valori nei parametri
	 *
	 * @param sql
	 *            SqlManager per l'esecuzione della lettura
	 * @param list
	 *            Lista di parametri
	 */
	public void leggiTutti(SqlManager sql, Vector valori) throws JspException {
		// Come prima cosa setto tutti i parametri
		for (int i = 0; i < elencoEntita.size(); i++) {
			EntitaLocal lEnt = (EntitaLocal) elencoEntita.get(i);
			lEnt.setParametri(valori);
			lEnt.leggi(sql);
		}
	}

	/**
	 * Funzione che ricava il valore del campo.
	 *
	 * @param campo
	 */
  public String getValue(CampoListaTagImpl campo) {
    String idTabella = (campo.getAlias() == null ? campo.getEntita() : campo.getAlias());
    EntitaLocal ent = this.getEntitaEsterna(idTabella);
    if (ent != null) {
      return ent.getValue(campo.getNCampo());
    }
    return "";
  }
}
