package it.eldasoft.gene.db.sql.sqlparser;

import it.eldasoft.gene.db.sql.stringsql.SQLSelect;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Contenitore di espressioni della select
 *
 * @author marco.franceschin
 *
 */
public class JdbcSelect {

  private Vector<JdbcExpression> espressioni;

  private boolean distinct;

  public JdbcSelect() {
    espressioni = new Vector<JdbcExpression>();
    distinct = false;
  }

  public JdbcSelect(String sez) {
    espressioni=new Vector<JdbcExpression>();
    distinct = false;
    // Divido per , e aggiungo tutte le espressioni
    String divs[]=SQLSelect.dividiStringa(sez,",");
    for(int i=0; i<divs.length;i++){
      this.append(JdbcExpression.getExpression(divs[i]));
    }
  }

  public void setDistinct(boolean distinct){
	  this.distinct = distinct;
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
    lsbTmp.append("select ");
    if (distinct)
    	lsbTmp.append("distinct ");
    for (Enumeration<JdbcExpression> e = espressioni.elements(); e.hasMoreElements();) {
      JdbcExpression lExpr = e.nextElement();
      lsbTmp.append(lExpr.toStringAlias());
      // Se ha ancora elementi aggiungo i successivi
      if (e.hasMoreElements()) lsbTmp.append(",");
    }
    return lsbTmp.toString();
  }

  /**
   * @return Returns the espressioni.
   */
  public Vector<JdbcExpression> getEspressioni() {
    return espressioni;
  }
}
