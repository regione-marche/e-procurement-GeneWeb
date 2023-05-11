package it.eldasoft.gene.db.sql.sqlparser;

import it.eldasoft.gene.db.sql.stringsql.SQLSelect;

import java.util.Vector;

/**
 * Classe che gestisce una select
 *
 * @author marco.franceschin
 *
 */
public class JdbcSqlSelect {

	public static final int SQL_SELECT = 0;

	public static final int SQL_FROM = 1;

	public static final int SQL_WHERE = 2;

	public static final int SQL_GROUP = 3;

	public static final int SQL_SORT = 4;

	public static final int SQL_HAVING = 5;

	public static final int SQL_UNION = 6;

	private JdbcSelect select;

	private JdbcFrom from;

	private JdbcWhere where;

	private JdbcGroupBy groupby;

	private JdbcOrderBy order;

	private JdbcWhere having;

	private JdbcSqlSelect union;

	public JdbcSqlSelect() {
		select = new JdbcSelect();
		from = new JdbcFrom();
		where = new JdbcWhere();
        where.setFrom(from);
		groupby = new JdbcGroupBy();
		order = new JdbcOrderBy();
		having = new JdbcWhere();
		union = null;
	}

    /**
     * Costruttore partendo da una stringa SQL
     * @param sql
     */
    public JdbcSqlSelect(String sql) {
        // Trasformo l'SQL
        SQLSelect sqlSelect=new SQLSelect(sql);
        select = new JdbcSelect(sqlSelect.getSez(SQLSelect.SQL_SELECT));
        from = new JdbcFrom(sqlSelect.getSez(SQLSelect.SQL_FROM));
        where = new JdbcWhere(sqlSelect.getSez(SQLSelect.SQL_WHERE));
        where.setFrom(from);
        groupby = new JdbcGroupBy(sqlSelect.getSez(SQLSelect.SQL_GROUP));
        order = new JdbcOrderBy(sqlSelect.getSez(SQLSelect.SQL_SORT));
        having = new JdbcWhere(sqlSelect.getSez(SQLSelect.SQL_HAVING));
        union = null;
    }

    public void setDistinct(boolean distinct){
    	select.setDistinct(distinct);
    }

	@Override
  public String toString() {
		StringBuffer lBuf = new StringBuffer();
		lBuf.append(select.toString());
		lBuf.append(from.toString());
        lBuf.append(where.toString());
        lBuf.append(groupby.toString());
		lBuf.append(order.toString());
		if (union != null) {
			lBuf.append(" union ");
			lBuf.append(union.toString());
		}
		lBuf.append(having.toString());
		return lBuf.toString();
	}

	/**
	 * @return Returns the from.
	 */
	public JdbcFrom getFrom() {
		return from;
	}

	/**
	 * @return Returns the groupby.
	 */
	public JdbcGroupBy getGroupby() {
		return groupby;
	}

	/**
	 * @return Returns the having.
	 */
	public JdbcWhere getHaving() {
		return having;
	}

	/**
	 * @return Returns the order.
	 */
	public JdbcOrderBy getOrder() {
		return order;
	}

	/**
	 * @return Returns the select.
	 */
	public JdbcSelect getSelect() {
		return select;
	}

	/**
	 * @return Returns the union.
	 */
	public JdbcSqlSelect getUnion() {
		return union;
	}

	/**
	 * @return Returns the where.
	 */
	public JdbcWhere getWhere() {
		return where;
	}


/*
	public static JdbcSqlSelect newJdbcSqlSelect(String asSql){
		// ************************************************************
		// Storia Modifiche:
		// Utente Data Descrizione
		// M.F. 02/05/2006 Prima Versione
		// ************************************************************
		// Inizializzazioni

		int liUnion, liSelect,liFrom, liWhere, liGroup, liOrder, liHaving ;
		String lsSqlUnion="";
		JdbcSqlSelect loRet=new JdbcSqlSelect();


		// Eseguo le prime conversioni dell'SQL
		asSql=JdbcSqlSelect.convSQL(asSql);
		// Verifico se esiste una Union
		liUnion=JdbcSqlSelect.posSql(asSql,JdbcSqlSelect.SQL_UNION);
		if(liUnion>0){
			lsSqlUnion=asSql.substring(liUnion+7);
			asSql=asSql.substring(0,liUnion);
		}
		// Estraggo tutte le sezioni dell'SQL
		liSelect=JdbcSqlSelect.posSql(asSql,JdbcSqlSelect.SQL_SELECT);
		liFrom=JdbcSqlSelect.posSql(asSql,JdbcSqlSelect.SQL_FROM);
		liWhere=JdbcSqlSelect.posSql(asSql,JdbcSqlSelect.SQL_WHERE);
		liGroup=JdbcSqlSelect.posSql(asSql,JdbcSqlSelect.SQL_GROUP);
		liHaving=JdbcSqlSelect.posSql(asSql,JdbcSqlSelect.SQL_HAVING);
		if(liSelect<0)
			return null;

		// Aggiungo le eventuali union
		if(!lsSqlUnion.equals("")){
			loRet.union=JdbcSqlSelect.newJdbcSqlSelect(lsSqlUnion);
		}

		return loRet;

	}
	*/
	/**
	 * Funzoine he estrae tutti i parametri che fanno parte della select
	 *
	 */
	public Vector<JdbcParametro> getParemetri(){
		Vector<JdbcParametro> ret=new Vector<JdbcParametro>();
		ret.addAll(JdbcUtils.getParametri(this.getSelect().getEspressioni()));
		ret.addAll(JdbcUtils.getParametri(this.getFrom().getSezioni()));
		ret.addAll(JdbcUtils.getParametri(this.getWhere().getSezioni()));
		ret.addAll(JdbcUtils.getParametri(this.getOrder().getExpressions()));
		return ret;
	}

  /**
   * Funzione che setta una nuova form
   * @param from
   */
  public void setFrom(JdbcFrom from) {
	this.from = new JdbcFrom();
    this.from.append(from);
    where.setFrom(from);

  }
}
