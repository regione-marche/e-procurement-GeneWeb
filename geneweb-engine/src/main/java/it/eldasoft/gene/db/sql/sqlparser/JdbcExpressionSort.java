package it.eldasoft.gene.db.sql.sqlparser;

public class JdbcExpressionSort extends JdbcExpression {

  private boolean asc = false;

  /**
   * @return Returns the asc.
   */
  public boolean isAsc() {
    return asc;
  }

  public JdbcExpressionSort(JdbcExpression aExpr, boolean abAsc) {
    super(aExpr);
    asc = abAsc;
  }

  /**
   * Conversione in stringa
   */
  public String toString() {
    StringBuffer lBuf = new StringBuffer();
    lBuf.append(super.toString());
    if (this.isAsc())
      lBuf.append(" asc");
    else
      lBuf.append(" desc");
    return lBuf.toString();
  }

  /**
   * Funzione che trasforma una stringa in un'espressione di sort
   * @param string
   * @return
   */
  public static JdbcExpressionSort getExpressionSort(String string) {
    if(string!=null){
      boolean asc=true;
      if(string.toUpperCase().endsWith(" ASC")){
        string=string.substring(0,string.length()-4);
      }else{       
        if(string.toUpperCase().endsWith(" DESC")){
          string=string.substring(0,string.length()-5);
          asc=false;
        }
      }
      return new JdbcExpressionSort(JdbcExpression.getExpression(string),asc);
    }
    return null;
  }
}
