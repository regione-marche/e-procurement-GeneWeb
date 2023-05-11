package it.eldasoft.gene.db.sql.sqlparser;

import it.eldasoft.gene.db.sql.stringsql.SQLSelect;

import java.util.Enumeration;
import java.util.Vector;

public class JdbcOrderBy {

  private Vector<JdbcExpressionSort> expressions;

  public JdbcOrderBy() {
    expressions = new Vector<JdbcExpressionSort>();
  }

  public JdbcOrderBy(String sez) {
    expressions = new Vector<JdbcExpressionSort>();
    // Divido per , e aggiungo tutte le espressioni
    String divs[] = SQLSelect.dividiStringa(sez, ",");
    for (int i = 0; i < divs.length; i++) {
      this.append(JdbcExpressionSort.getExpressionSort(divs[i]));
    }
  }

  /**
   * Aggiunta di un elemento per l'ordinamento
   *
   * @param aSort
   */
  public void append(JdbcExpressionSort aSort) {
    expressions.add(aSort);
  }

  /**
   * Conversione in stringa
   */
  @Override
  public String toString() {
    StringBuffer lBuf = new StringBuffer();

    if (expressions == null || expressions.size() == 0) return "";
    lBuf.append(" order by ");
    for (Enumeration<JdbcExpressionSort> e = expressions.elements(); e.hasMoreElements();) {
      JdbcExpressionSort lSort = e.nextElement();
      lBuf.append(lSort.toString());
      if (e.hasMoreElements()) lBuf.append(",");
    }
    return lBuf.toString();
  }

  /**
   * @return Returns the expressions.
   */
  protected Vector<JdbcExpressionSort> getExpressions() {
    return expressions;
  }

  /**
   * Funzione che reimposta l'ordinamento con l'elenco dei sort
   * @param sorts
   */
  public void setSort(JdbcExpressionSort[] sorts) {
    this.expressions.clear();
    for(int i=0;i<sorts.length;i++){
      this.expressions.add(sorts[i]);
    }
  }
}
