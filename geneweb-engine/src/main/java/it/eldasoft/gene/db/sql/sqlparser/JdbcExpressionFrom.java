package it.eldasoft.gene.db.sql.sqlparser;

import it.eldasoft.utils.sql.comp.SqlManager;

public class JdbcExpressionFrom extends JdbcExpression {

  /**
   * Tabella
   */
  private JdbcTable  table     = null;

  /**
   * Campi di collegamento per la join
   */
  private JdbcColumn linkCol[] = null;
  
  private char tipoDB = SqlManager.DATABASE_ORACLE_PER_COMPOSITORE;

  /**
   * Collegamento all'eventuale espressione in Join
   */
  JdbcExpressionFrom linkExpr  = null;

  protected void setNull() {
    this.table = null;
    this.linkCol = null;
    this.linkExpr = null;
    super.setNull();
  }

  /**
   * Costruttore con una singola tabella
   * 
   * @param aTable
   */
  public JdbcExpressionFrom(JdbcTable aTable) {
    setNull();
    this.setTipo(JdbcUtils.JDBC_TABLE);
    table = aTable;
    linkCol = null;
    linkExpr = null;
  }

  public JdbcExpressionFrom(JdbcExpression exp, String alias) {
    super(exp);
    this.setAlias(alias);
  }

  /**
   * Costruttore di una tabella collegata con la precedente
   * 
   * @param aTable
   *        Tabella
   * @param aLink
   *        Campi di collegamento
   * @param aPrec
   *        Collegamento con l'espressione in Join
   */
  public JdbcExpressionFrom(JdbcTable aTable, JdbcColumn aLink[],
      JdbcExpressionFrom aPrec, char tipoDB) {
    // Setto come tabella in join con la precedente
    this.setTipo(JdbcUtils.JDBC_TABLEJOIN);
    this.table = aTable;
    this.linkCol = aLink;
    this.linkExpr = aPrec;
    this.tipoDB=tipoDB;
  }

  /**
   * Costruttore con il tipo di join precedente
   * 
   * @param aTipo
   *        Tipo di Join
   * @param aTable
   *        Tabella
   * @param aLink
   *        Campi di collegamento
   */
  public JdbcExpressionFrom(int aTipo, JdbcTable aTable, JdbcColumn aLink[]) {
    this.setTipo(aTipo);
    table = aTable;
    linkCol = aLink;
    linkExpr = null;
  }

  /**
   * Conversione in stringa
   * 
   */
  public String toString() {
    switch (this.getTipo()) {
    case JdbcUtils.JDBC_TABLE:
      return this.getTable().toStringForFrom();
    case JdbcUtils.JDBC_INNERJOIN:
    case JdbcUtils.JDBC_LEFTJOIN:
    case JdbcUtils.JDBC_RIGHTJOIN:
      return "";
    case JdbcUtils.JDBC_TABLEJOIN:
      return JdbcUtils.getFromJoin(this.linkExpr, this);
    default:
      super.toStringAlias();
    }
    return "";
  }

  public String toStringWhereJoin() {
    if (this.getTipo() == JdbcUtils.JDBC_TABLEJOIN)
      return JdbcUtils.getWhereJoin(this.linkExpr, this);
    return "";
  }

  /**
   * @return Returns the linkCol.
   */
  public JdbcColumn[] getLinkCol() {
    return linkCol;
  }

  /**
   * @param linkCol
   *        The linkCol to set.
   */
  public void setLinkCol(JdbcColumn[] linkCol) {
    this.linkCol = linkCol;
  }

  /**
   * @return Returns the table.
   */
  public JdbcTable getTable() {
    return table;
  }

  /**
   * @param table
   *        The table to set.
   */
  public void setTable(JdbcTable table) {
    this.table = table;
  }

  public static JdbcExpressionFrom getExpressionFrom(String exp) {
    if (exp != null) {
      exp = exp.trim();
      // Verifico se si tratta di un nome di campo
      if (exp.matches("^\\w+$")) {
        // Si tratta del nome di una tabella
        return new JdbcExpressionFrom(new JdbcTable(exp));
      } else if (exp.matches("^\\w+ \\w+$")) {
        String table = exp.substring(0, exp.indexOf(' '));
        String alias = exp.substring(exp.indexOf(' ') + 1);
        // si tratta di un nome tabella con alias
        return new JdbcExpressionFrom(new JdbcTable(table, alias));
      }
      return new JdbcExpressionFrom(JdbcExpression.getExpression(exp), null);
    }
    return null;

  }

  
  public char getTipoDB() {
    return tipoDB;
  }
}
