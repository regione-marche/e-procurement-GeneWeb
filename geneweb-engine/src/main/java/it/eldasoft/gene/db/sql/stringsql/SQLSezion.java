package it.eldasoft.gene.db.sql.stringsql;

/**
 * Parte di SQL
 * @author marco.franceschin
 */
public final class SQLSezion {
	private int start;
	private int len;
	
	public SQLSezion(int aiStart, int aiLen) {
		start=aiStart;
		len = aiLen;
	}

	/**
	 * @return Returns the len.
	 */
	public int getLen() {
		return len;
	}

	/**
	 * @return Returns the start.
	 */
	public int getStart() {
		return start;
	}
	
}
