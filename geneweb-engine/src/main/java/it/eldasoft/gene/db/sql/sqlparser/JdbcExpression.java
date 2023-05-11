package it.eldasoft.gene.db.sql.sqlparser;

import it.eldasoft.utils.metadata.domain.Campo;

import java.util.Vector;

/**
 * Espressione SQL
 *
 * @author marco.franceschin
 *
 */
public class JdbcExpression {

  // Formula
  private String         formula     = "";

  // Colonna eventuale
  private JdbcColumn     column      = null;

  // Eventuale alias
  private String         alias       = "";

  // Parametri delle formula
  private JdbcExpression parametri[] = null;

  // Select
  private JdbcSqlSelect  select;

  // Parametro di input
  private JdbcParametro  parametro;

  // Tipo di espressione
  private int            tipo;

  protected void setNull() {
    this.formula = null;
    this.alias = null;
    this.column = null;
    this.parametri = null;
    this.parametro = null;
    this.select = null;
    this.tipo = -1;

  }

  protected JdbcExpression() {
    this.setNull();
  }

  public JdbcExpression(String fixParam, JdbcParametro params[]) {
    this.setNull();
    this.formula = fixParam;

    JdbcExpression vectTmp[] = new JdbcExpression[params.length];
    if (params != null && params.length > 0) {
      // Scorro tutti i parametri
      for (int i = 0; i < params.length; i++) {
        vectTmp[i] = new JdbcExpression(params[i].getValue(),
            params[i].getTipo());
      }
    }
    this.parametri = vectTmp;
    this.tipo = JdbcUtils.JDBC_FIXED_WITH_PARAM;
  }

  /**
   * Costruttore passando un'espressione
   *
   * @param expr
   */
  public JdbcExpression(JdbcExpression expr) {
    this.setNull();
    this.tipo = expr.getTipo();
    switch (expr.getTipo()) {
    case JdbcUtils.JDBC_COLUMN:
      this.column = expr.getColumn();

    // Espressione
    case JdbcUtils.JDBC_EXPR:
      this.formula = expr.getFormula();
      this.parametri = expr.getParametri();
    // Espressione fissa
    case JdbcUtils.JDBC_EXPRFIX:
      this.formula = expr.getFormula();
    // Espressione select
    case JdbcUtils.JDBC_SELECT:
      this.select = expr.getSelect();
    // Parametro di input
    case JdbcUtils.JDBC_PARAMETER:
      this.parametro = expr.getParametro();
    // Fisso con parametri
    case JdbcUtils.JDBC_FIXED_WITH_PARAM:
      this.parametri = expr.getParametri();
      this.formula = expr.getFormula();
    }
  }

  /**
   * Creazione di una formula fissa
   *
   * @param asFormula
   */
  public JdbcExpression(String asFormula) {
    // Si tratta di una formula fissa o di una costante
    this.setNull();
    formula = asFormula;
    tipo = JdbcUtils.JDBC_EXPRFIX;
  }

  /**
   * Creazione di una colonna
   *
   * @param aColumn
   */
  public JdbcExpression(JdbcColumn aColumn) {
    this.setNull();
    column = aColumn;
    tipo = JdbcUtils.JDBC_COLUMN;
  }

  /**
   * Creazione di una formula con parametri
   *
   * @param aColumn
   */
  public JdbcExpression(String asFormula, JdbcExpression aParametri[]) {
    this.setNull();
    if (aParametri == null) aParametri = new JdbcExpression[] {};
    formula = asFormula;
    parametri = aParametri;
    tipo = JdbcUtils.JDBC_EXPR;
  }

  /**
   * Costruttore partendo da una select
   *
   * @param select
   *        da aggiungere
   */

  public JdbcExpression(JdbcSqlSelect select) {
    this.setNull();
    this.select = select;
    this.tipo = JdbcUtils.JDBC_SELECT;
  }

  /**
   * Costruttore per un parametro
   *
   * @param par
   */
  public JdbcExpression(Object par, char tipo) {
    this.setNull();
    JdbcParametro parametro = new JdbcParametro(tipo, par);
    this.parametro = parametro;
    this.tipo = JdbcUtils.JDBC_PARAMETER;
  }

  /**
   * Costruzione con un parametro di passaggio
   *
   * @param param
   */
  public JdbcExpression(JdbcParametro param) {
    this.setNull();
    this.parametro = param;
    this.tipo = JdbcUtils.JDBC_PARAMETER;
  }

  /**
   * @return Returns the alias.
   */
  public String getAlias() {
    return alias;
  }

  /**
   * @param alias
   *        The alias to set.
   */
  public void setAlias(String alias) {
    this.alias = alias;
  }

  /**
   * @return Returns the column.
   */
  public JdbcColumn getColumn() {
    return column;
  }

  /**
   * @param column
   *        The column to set.
   */
  public void setColumn(JdbcColumn column) {
    this.column = column;
  }

  /**
   * @return Returns the parametri.
   */
  public JdbcExpression[] getParametri() {
    return parametri;
  }

  /**
   * @param parametri
   *        The parametri to set.
   */
  public void setParametri(JdbcExpression[] parametri) {
    this.parametri = parametri;
  }

  /**
   * @return Returns the formula.
   */
  public String getFormula() {
    return formula;
  }

  /**
   * Trasformazione l'espressione in stringa
   */
  @Override
  public String toString() {
    StringBuffer lsbTmp = new StringBuffer();
    switch (this.getTipo()) {
    case JdbcUtils.JDBC_COLUMN:
      lsbTmp.append(column.toString());
      lsbTmp.append(" ");
      break;
    case JdbcUtils.JDBC_EXPR:
      lsbTmp.append(this.getFormula());
      // Se non è una costante appendo le parentesi e gli
      // eventuali parametri
      lsbTmp.append("(");
      if (parametri != null) {
        // Aggiungo gli eventuali parametri
        for (int i = 0; i < parametri.length; i++) {
          if (i > 0) lsbTmp.append(",");
          lsbTmp.append(parametri[i].toString());
        }
      }
      lsbTmp.append(") ");

      break;
    case JdbcUtils.JDBC_EXPRFIX:
      lsbTmp.append(this.getFormula());
      lsbTmp.append(" ");
      break;
    case JdbcUtils.JDBC_SELECT:
      lsbTmp.append(this.getSelect());
      lsbTmp.append(" ");
      break;
    case JdbcUtils.JDBC_PARAMETER: // Si tratta di un parametro
      lsbTmp.append("? ");
      break;
    case JdbcUtils.JDBC_FIXED_WITH_PARAM:
      lsbTmp.append(this.getFormula());
      lsbTmp.append(" ");
      break;
    }
    return lsbTmp.toString();
  }

  /**
   * Trasformazione in stringa aggiungendo l'eventuale alias
   *
   * @return
   */
  public String toStringAlias() {
    if (this.getAlias() == null || this.getAlias().equals(""))
      return this.toString();
    StringBuffer lsbTmp = new StringBuffer();
    lsbTmp.append(this.toString());
    lsbTmp.append(" as ");
    lsbTmp.append(this.getAlias());
    return lsbTmp.toString();
  }

  /**
   * @return Returns the tipo.
   */
  public int getTipo() {
    return tipo;
  }

  /**
   * @return Returns the select.
   */
  public JdbcSqlSelect getSelect() {
    return select;
  }

  /**
   * @return Returns the parametro.
   */
  public JdbcParametro getParametro() {
    return parametro;
  }

  /**
   * @param tipo
   *        The tipo to set.
   */
  protected void setTipo(int tipo) {
    this.tipo = tipo;
  }

  public boolean isParemeter() {
    if (this.getTipo() == JdbcUtils.JDBC_PARAMETER) return true;
    return false;
  }

  /**
   * Funzione che restituisce un espressione che punta ad una colonna
   *
   * @param nomeFisicoCampo
   * @return
   */
  public static JdbcExpression getColumn(String nomeFisicoCampo) {
    return new JdbcExpression(new JdbcColumn(null, nomeFisicoCampo));
  }

  /**
   * Funzione che restituisce una formula fissa o costante
   *
   * @param string
   *        formula o costante
   * @return
   */
  public static JdbcExpression getFixed(String string) {
    return new JdbcExpression(string);
  }

  /**
   * Funzione che da un parametro
   *
   * @param tipoColonna
   *        Tipo colonna definito nel campo
   * @param object
   *        valore del parametro
   * @return
   */
  public static JdbcExpression getParametro(short tipoColonna, Object object) {
    char tipo;
    switch (tipoColonna) {
    case Campo.TIPO_DATA:
      tipo = JdbcParametro.TIPO_DATA;
      break;
    case Campo.TIPO_DECIMALE:
      tipo = JdbcParametro.TIPO_DECIMALE;
      break;
    case Campo.TIPO_INTERO:
      tipo = JdbcParametro.TIPO_NUMERICO;
      break;
    default:
      tipo = JdbcParametro.TIPO_TESTO;
    }
    return new JdbcExpression(new JdbcParametro(tipo, object));
  }

  public static JdbcExpression getExpression(String string) {
    if (string != null) {
      string = string.trim();
      String split[] = string.split(" ");
      String alias = null;
      if (split.length >= 3 && split[split.length - 2].equalsIgnoreCase("as")) {
        alias = split[split.length - 1];
        string = "";
        for (int i = 0; i < split.length - 2; i++) {
          if (i > 0) string += " ";
          string += split[i];
        }
      }
      JdbcExpression exp = null;
      // Verifico se si tratta del nome di un campo
      if (string.matches("^\\w+.\\w+$")) {
        exp = new JdbcExpression(new JdbcColumn(null, string));

      } else if (string.matches("^[{][TNDF]:[.]+[}]$")) {
        // Se si tratta di un parametro lo converto
        exp = new JdbcExpression(new JdbcParametro(string.substring(1,
            string.length() - 2)));
      } else if (string.matches("^\\w+([.]*)$")) {
        // Si tratta di una formula
        Vector<JdbcExpression> params=new Vector<JdbcExpression>();
        string = JdbcUtils.getFormula(string, params);
        JdbcExpression exprs[]=new JdbcExpression[params.size()];
        for(int i=0;i<params.size();i++)
          exprs[i]=params.get(i);
        exp=new JdbcExpression(string,exprs);
      }
      if (exp == null) exp = new JdbcExpression(string);
      exp.setAlias(alias);
      return exp;
    }
    return null;
  }

}
