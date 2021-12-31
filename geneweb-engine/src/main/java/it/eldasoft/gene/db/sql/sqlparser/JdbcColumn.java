package it.eldasoft.gene.db.sql.sqlparser;

public class JdbcColumn {
	private JdbcTable table = null;

	private String name = "";

	/**
	 * Cotruttore con tabella e nome
	 * 
	 * @param aTable
	 *            Tabella d'eppartenenza
	 * @param asName
	 *            Nome delle colonna
	 */
	public JdbcColumn(JdbcTable aTable, String asName) {
		// ************************************************************
		// Storia Modifiche:
		// Data Utente Descrizione
		// 29/09/2006 M.F. Se non impostata la tabella la estraggo la nome del campo
		// ************************************************************

		if (aTable == null) {
			// Se la tabella è vuota significa che sul nome è impostato
			// TABALLE.CAMPO
			if (asName.indexOf('.') >= 0) {
				aTable = new JdbcTable(asName.substring(0,
						asName.indexOf('.')));
				asName = asName.substring(asName.indexOf('.') + 1);
			}
		}
		this.table = aTable;
		this.name = asName;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the table.
	 */
	public JdbcTable getTable() {
		return table;
	}

	/**
	 * @param table
	 *            The table to set.
	 */
	protected void setTable(JdbcTable table) {
		this.table = table;
	}

	/**
	 * Funzione che converte in stringa da utilizzare in where, order ecc
	 */
	public String toString() {
		StringBuffer lsbTmp = new StringBuffer();
		if (table != null)
			lsbTmp.append(table.toString()).append(".");
		lsbTmp.append(this.getName());
		return lsbTmp.toString();
	}
}
