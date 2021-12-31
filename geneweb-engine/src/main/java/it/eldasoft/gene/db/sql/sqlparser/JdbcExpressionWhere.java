package it.eldasoft.gene.db.sql.sqlparser;

/**
 * Particella per un confronto tra due espressioni
 * 
 * @author marco.franceschin
 * 
 */
public class JdbcExpressionWhere extends JdbcExpression {

  protected void setNull() {
    super.setNull();
  }

  /**
   * Costruttore di una sezione di where senza parte destra
   * 
   * @param aLeft
   *        Parte sinistra
   * @param aConfronto
   *        Operatore di confronto. Es. Null, not null ecc
   */
  public JdbcExpressionWhere(JdbcExpression aExpr) {
    super(aExpr);
  }

  /**
   * Costruttori per una parentesi
   * 
   * @param abParOn
   *        true se inizio false se fine
   */
  public JdbcExpressionWhere(boolean abParOn) {
    this.setNull();
    if (abParOn)
      this.setTipo(JdbcUtils.JDBC_PARON);
    else
      this.setTipo(JdbcUtils.JDBC_PAROFF);
  }

  /**
   * Creazione di un tipo particella
   * 
   * @param tipoParticella
   */
  public JdbcExpressionWhere(int tipoParticella) {
    this.setNull();
    if (tipoParticella == JdbcUtils.JDBC_PARTICELLA_OR) {
      this.setTipo(JdbcUtils.JDBC_PARTICELLA_OR);
    } else {
      // Di default è la particella AND
      this.setTipo(JdbcUtils.JDBC_PARTICELLA_AND);
    }
  }

  /**
   * Trasformazione in stringa delle sezione
   */
  public String toString() {
    switch (this.getTipo()) {
    case JdbcUtils.JDBC_PARON:
      return "( ";
    case JdbcUtils.JDBC_PAROFF:
      return ") ";
    case JdbcUtils.JDBC_PARTICELLA_AND:
      return "and ";
    case JdbcUtils.JDBC_PARTICELLA_OR:
      return "or ";
    }
    return super.toString();
  }

  /**
   * Funzione che da un espressione parentesi
   * 
   * @param open
   *        Flag che dice se si tratta di una perentesi aperta
   * @return
   */
  public static JdbcExpressionWhere getParentesi(boolean open) {
    return new JdbcExpressionWhere(open);
  }

  /**
   * Funzione che trasforma un espressione da stringa a JdbcExpressionWhere
   * 
   * @param string
   * @return
   */
  public static JdbcExpressionWhere getExpressionWhere(String string) {
    if (string != null) {
      JdbcExpressionWhere exp = null;
      string = string.trim();
      if ("and".equalsIgnoreCase(string)) {
        exp = new JdbcExpressionWhere(JdbcUtils.JDBC_PARTICELLA_AND);
      } else if ("or".equalsIgnoreCase(string)) {
        exp = new JdbcExpressionWhere(JdbcUtils.JDBC_PARTICELLA_OR);
      } else if ("(".equalsIgnoreCase(string)) {
        exp = new JdbcExpressionWhere(true);
      } else if (")".equalsIgnoreCase(string)) {
        exp = new JdbcExpressionWhere(false);
      }
      if (exp == null)
        exp = new JdbcExpressionWhere(JdbcExpression.getExpression(string));
      return exp;
    }
    return null;
  }
}
