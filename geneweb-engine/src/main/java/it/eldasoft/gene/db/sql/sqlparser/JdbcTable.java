package it.eldasoft.gene.db.sql.sqlparser;
/**
 * Entità che definisce una tabella
 * @author marco.franceschin
 *
 */
public class JdbcTable {
	/**
	 * Nome della tabella
	 */
	private String name="";
	/**
	 * Alias delle tabella
	 */
	private String alias="";
	/**
	 * Costruttore di default
	 * @param asName Nome della tabella
	 */
	public JdbcTable(String asName) {
		this.name=asName;
		this.alias="";
	}
	public JdbcTable(String asName,String asAlias) {
		this.name=asName;
		this.alias=asAlias;
	}

	/**
	 * @return Returns the alias.
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias The alias to set.
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * Nome delle tabella
	 */
	public String toString() {
		if(alias.equals(""))
			return this.getName();
		return this.getAlias();
	}
	/**
	 * Tabella da mettere nella from
	 * @return
	 */
	public String toStringForFrom() {
		StringBuffer lsbTmp=new StringBuffer();
		
		lsbTmp.append(this.getName());
		if(!this.getAlias().equals(""))
		{
			lsbTmp.append(" ");
			lsbTmp.append(this.getAlias());
		}
		return lsbTmp.toString();
		
	}
}
