package it.eldasoft.gene.db.sql.sqlparser;

import it.eldasoft.gene.db.sql.stringsql.SQLSelect;
import it.eldasoft.utils.sql.comp.SqlManager;

import java.util.Vector;

/**
 * Costatanti e utilita generali per i JDBC
 *
 * @author marco.franceschin
 *
 */
public class JdbcUtils {

  // Colonna
  public static final int JDBC_COLUMN           = 0;

  // Espressione
  public static final int JDBC_EXPR             = 1;

  // Espressione fissa
  public static final int JDBC_EXPRFIX          = 2;

  // Espressione select
  public static final int JDBC_SELECT           = 3;

  // Parametro di input
  public static final int JDBC_PARAMETER        = 4;

  // Parametro di input
  public static final int JDBC_FIXED_WITH_PARAM = 5;

  // PArentesi Aperte
  public static final int JDBC_PARON            = 6;

  // Parentesi chiuse
  public static final int JDBC_PAROFF           = 7;

  // Particella di tipo and
  public static final int JDBC_PARTICELLA_AND   = 8;

  // Particella di tipo OR
  public static final int JDBC_PARTICELLA_OR    = 9;

  // Tabella semplice
  public static final int JDBC_TABLE            = 10;

  // Tabella in join con la successiva
  public static final int JDBC_INNERJOIN        = 11;

  // Tabella in join left con la successiva
  public static final int JDBC_LEFTJOIN         = 12;

  // Tabella in join right con la successiva
  public static final int JDBC_RIGHTJOIN        = 13;

  // Tabella collegata con la precedente
  public static final int JDBC_TABLEJOIN        = 14;

  private static void addParametro(Vector<JdbcParametro> vect, JdbcExpression exp) {
    switch (exp.getTipo()) {
    case JdbcUtils.JDBC_PARAMETER:
      vect.add(exp.getParametro());
      break;
    case JdbcUtils.JDBC_SELECT:
      vect.addAll(getParametri(exp.getSelect().getWhere()));
      break;
    case JdbcUtils.JDBC_EXPR:
    case JdbcUtils.JDBC_FIXED_WITH_PARAM:
      for (int i = 0; i < exp.getParametri().length; i++)
        addParametro(vect, exp.getParametri()[i]);
      break;
    }
  }

  /**
   * Funzione che ricava tutti i parametri dell'oggetto
   *
   * @return
   */
  public static Vector<JdbcParametro> getParametri(Vector<? extends JdbcExpression> exprs) {
    Vector<JdbcParametro> ret = new Vector<JdbcParametro>();
    for (int i = 0; i < exprs.size(); i++)
      if (exprs.get(i) instanceof JdbcExpression) {
        addParametro(ret, exprs.get(i));
      }
    return ret;
  }

  /**
   * Funzione che restituisce tutti i parametri di un oggetto jdbc in funzione
   * di che tipo di oggetto si tratta
   *
   * @param jdbc
   * @return
   */
  public static Vector<JdbcParametro> getParametri(JdbcWhere jdbc) {
    return getParametri((jdbc).getSezioni());
  }

  /**
   * Funzione che splitta una stringa saltando le parti all'interno di un SQL
   *
   * @param stringa
   * @param div
   * @return
   */
  public static String[] splitNoInStr(String stringa, String div) {
    Vector<String> vret = new Vector<String>();
    String split[] = stringa.split(div);
    StringBuffer buf = null;
    boolean inStr = false;
    for (int i = 0; i < split.length; i++) {
      if (split[i].indexOf(SQLSelect.CHAR_STR) >= 0) {
        if (!inStr) {
          inStr = true;
          buf = new StringBuffer("");
          buf.append(split[i]);
        } else {
          buf.append(split[i]);
          inStr = false;
          vret.add(buf.toString());
        }
      } else {
        if (inStr) {
          buf.append(split[i]);
        } else
          vret.add(split[i]);
      }
    }
    String ret[] = new String[vret.size()];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = vret.get(i);
    }
    return ret;
  }

  /**
   * Funzione che ricava una formula
   *
   * @param string
   * @param params
   * @return
   */
  public static String getFormula(String string, Vector<JdbcExpression> params) {
    String formula = "";
    if (string.indexOf('(') >= 0) {
      formula = string.substring(0, string.indexOf('('));
      string = string.substring(string.indexOf('(') + 1);
      if (string.indexOf(')') >= 0)
        string = string.substring(0, string.lastIndexOf(')'));
      // Divido per , e aggiungo tutte le espressioni
      String divs[] = SQLSelect.dividiStringa(string, ",");
      for (int i = 0; i < divs.length; i++) {
        params.add(JdbcExpression.getExpression(divs[i]));
      }
    }
    return formula;
  }

  /**
   * Funzione che restituisce la sintassi della join da aggiungere alla from
   *
   * @param from
   * @param from2
   * @return
   */
  public static String getFromJoin(JdbcExpressionFrom from,
      JdbcExpressionFrom to) {
    // ///////////////////////////////////////////////////////////////
    // Modifiche
    // ----------------
    // 23.11.2007: M.F. Aggiunta in funzione del tipo di database
    // ////////////////////////////////////////////////////////////// /
    StringBuffer buf = new StringBuffer("");
    switch (to.getTipoDB()) {
    case SqlManager.DATABASE_ORACLE_PER_COMPOSITORE:
      buf.append(from.getTable().toStringForFrom());
      buf.append(", ");
      buf.append(to.getTable().toStringForFrom());
      break;
    case SqlManager.DATABASE_POSTGRES_PER_COMPOSITORE:
    case SqlManager.DATABASE_SQL_SERVER_PER_COMPOSITORE:
    	if (from.getLinkCol().length == 0){
	      buf.append(from.getTable().toStringForFrom());
	      buf.append(", ");
	      buf.append(to.getTable().toStringForFrom());
    	}
    	else {
	      buf.append(from.getTable().toStringForFrom());
	      switch (from.getTipo()) {
	      case JDBC_RIGHTJOIN:
	        buf.append(" right outer join ");

	        break;
	      case JDBC_LEFTJOIN:
	        buf.append(" left outer join ");
	        break;
	      }
	      buf.append(to.getTable().toStringForFrom());
	      buf.append(" on ( ");
	      for (int i = 0; i < from.getLinkCol().length; i++) {
	        if (i > 0) buf.append(" and ");
	        buf.append(from.getLinkCol()[i].toString());
	        buf.append("=");
	        buf.append(to.getLinkCol()[i]);
	      }
	      buf.append(" )");
    	}
    	break;
    case SqlManager.DATABASE_DB2_PER_COMPOSITORE:
      buf.append(from.getTable().toStringForFrom());
      switch (from.getTipo()) {
      case JDBC_RIGHTJOIN:
        buf.append(" right outer join ");

        break;
      case JDBC_LEFTJOIN:
        buf.append(" left outer join ");
        break;
      }
      buf.append(to.getTable().toStringForFrom());
      buf.append(" on ( ");
      for (int i = 0; i < from.getLinkCol().length; i++) {
        if (i > 0) buf.append(" and ");
        buf.append(from.getLinkCol()[i].toString());
        buf.append("=");
        buf.append(to.getLinkCol()[i]);
      }
      buf.append(" )");
      break;

    default:
      throw new RuntimeException(
          "Tipo di database non supportato per le join tipo: "
              + to.getTipoDB()
              + " !");
    }
    return buf.toString();
  }

  /**
   * Funzione che da la where da aggiungere alla where per la bestione delle
   * outer join
   *
   * @param from
   * @param from2
   * @return
   */
  public static String getWhereJoin(JdbcExpressionFrom from,
      JdbcExpressionFrom to) {
    // ///////////////////////////////////////////////////////////////
    // Modifiche
    // ----------------
    // 23.11.2007: M.F. Aggiunta in funzione del tipo di database
    // ////////////////////////////////////////////////////////////// /

    StringBuffer buf = new StringBuffer("");
    switch (to.getTipoDB()) {
    case SqlManager.DATABASE_ORACLE_PER_COMPOSITORE:
      for (int i = 0; i < from.getLinkCol().length; i++) {
        if (i > 0) buf.append(" and ");
        buf.append(from.getLinkCol()[i].toString());
        if (from.getTipo() == JdbcUtils.JDBC_RIGHTJOIN) buf.append("(+)");
        buf.append("=");
        buf.append(to.getLinkCol()[i]);
        if (from.getTipo() == JdbcUtils.JDBC_LEFTJOIN) buf.append("(+)");
      }
      break;
    case SqlManager.DATABASE_POSTGRES_PER_COMPOSITORE:
    case SqlManager.DATABASE_SQL_SERVER_PER_COMPOSITORE:
    case SqlManager.DATABASE_DB2_PER_COMPOSITORE:
      break;
    default:
      throw new RuntimeException("Tipo di database ("
          + to.getTipoDB()
          + ") non supportato per le join");
    }

    return buf.toString();
  }

}
