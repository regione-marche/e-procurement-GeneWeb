package it.eldasoft.gene.db.sql.sqlparser;

import it.eldasoft.gene.db.sql.stringsql.SQLSelect;

import java.util.Enumeration;
import java.util.Vector;

public class JdbcGroupBy {

  private Vector<JdbcExpression> espressioni;

  public JdbcGroupBy() {
    espressioni = new Vector<JdbcExpression>();
  }

  public JdbcGroupBy(String sez) {
    espressioni = new Vector<JdbcExpression>();
    // Divido per , e aggiungo tutte le espressioni
    String divs[] = SQLSelect.dividiStringa(sez, ",");
    for (int i = 0; i < divs.length; i++) {
      this.append(JdbcExpression.getExpression(divs[i]));
    }
  }

  /**
   * Funzione che appende un'espressione alla select
   *
   * @param aExpr
   *        Espressione da appendere
   */
  public void append(JdbcExpression aExpr) {
    espressioni.add(aExpr);
  }

  /**
   * Conversione in stringa
   */
  @Override
  public String toString() {
    StringBuffer lsbTmp = new StringBuffer();
    if (espressioni == null || espressioni.size() == 0) return "";
    lsbTmp.append(" group by ");
    for (Enumeration<JdbcExpression> e = espressioni.elements(); e.hasMoreElements();) {
      JdbcExpression lExpr = e.nextElement();
      lsbTmp.append(lExpr.toString());
      // Se ha ancora elementi aggiungo i successivi
      if (e.hasMoreElements()) lsbTmp.append(",");
    }
    return lsbTmp.toString();
  }
}
