package it.eldasoft.gene.db.sql.sqlparser;

import it.eldasoft.gene.db.sql.stringsql.SQLSelect;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Classe che gestisce la from
 *
 * @author marco.franceschin
 *
 */
public class JdbcFrom {

  /**
   * Sezioni delle where
   */
  protected Vector<JdbcExpressionFrom> sezioni;

  public JdbcFrom() {
    sezioni = new Vector<JdbcExpressionFrom>();
  }

  public JdbcFrom(String sql_from) {
    sezioni = new Vector<JdbcExpressionFrom>();
    // Divido per , e aggiungo tutte le espressioni
    String divs[] = SQLSelect.dividiStringa(sql_from, ",");
    for (int i = 0; i < divs.length; i++) {
      this.append(JdbcExpressionFrom.getExpressionFrom(divs[i]));
    }
  }

  // Aggiungo un'espressione
  public void append(JdbcExpressionFrom aExpr) {
    sezioni.add(aExpr);
  }
  // Rimuovo una tabella dalla lista
  public boolean remove(String table) {
	  for (int i=0; i < this.sezioni.size(); i++) {
		  if (this.sezioni.get(i).toString().trim().equalsIgnoreCase(table))
			  return sezioni.remove(sezioni.get(i));
	  }
	  return false;
	  }

  // Aggiungo un'espressione
  public void append(JdbcTable aTable) {
    boolean trovato = false;
    for (int j = 0; j < this.sezioni.size() && !trovato; j++) {
      if (aTable.toString().trim().equalsIgnoreCase(this.sezioni.get(j).toString().trim()))
        trovato = true;
    }
    if (!trovato) sezioni.add(new JdbcExpressionFrom(aTable));
  }

  @Override
  public String toString() {
    return this.toString(true);
  }

  public String toString(boolean startWithFrom) {
    StringBuffer lsbTmp = new StringBuffer();
    if (sezioni == null || sezioni.size() == 0) return "";
    if (startWithFrom)
      lsbTmp.append(" from ");
    // Scorro tutte le sezioni
    for (Enumeration<JdbcExpressionFrom> e = sezioni.elements(); e.hasMoreElements();) {
      JdbcExpressionFrom expr = e.nextElement();
      String lsTmp = expr.toString();
      if (lsTmp != null && lsTmp.length() > 0) {
        lsbTmp.append(lsTmp);
        // Aggiungo la virgola solo se ha ancora elementi
        if (e.hasMoreElements()) lsbTmp.append(",");
      }
    }
    return lsbTmp.toString();
  }

  public String getWhereJoin() {
    int numAdd = 0;
    StringBuffer buf = new StringBuffer("(");
    for (Enumeration<JdbcExpressionFrom> e = sezioni.elements(); e.hasMoreElements();) {
      JdbcExpressionFrom expr = e.nextElement();
      String lsTmp = expr.toStringWhereJoin();
      if (lsTmp != null && lsTmp.length() > 0) {
        if (numAdd > 0) buf.append(" and ");
        buf.append(lsTmp);
        numAdd++;
      }
    }
    if (numAdd == 0) return "";
    buf.append(")");
    return buf.toString();
  }

  /**
   * @return Returns the sezioni.
   */
  public Vector<JdbcExpressionFrom> getSezioni() {
    return sezioni;
  }

  /**
   * Funzione che aggiunge una from alla from gia esistente
   *
   * @param from
   */
  public void append(JdbcFrom from) {
    if (from != null) {
      for (int i = 0; i < from.sezioni.size(); i++) {
        String[] listaFrom = from.sezioni.get(i).toString().split(",");
        for (int k = 0; k < listaFrom.length; ++k) {
            boolean trovato = false;
	        for (int j = 0; j < this.sezioni.size() && !trovato; j++) {
	          if (listaFrom[k].trim().equalsIgnoreCase(
	              this.sezioni.get(j).toString().trim())) trovato = true;

	          // In postgresql vengono aggiunte delle clausole di join. In
	          // questo caso si devono rimuovere le tabelle coinvolte, se sono
	          // state in precedenza inserite singolarmente
	          else if (listaFrom[k].indexOf("join") > 0) {
	        	  String[] listaJoin = listaFrom[k].split(" ");
	        	  this.remove(listaJoin[0]);
	        	  boolean foundSecondTable = false;
	        	  for (int m=0; m < listaJoin.length && !foundSecondTable; ++m){
	        		  if (listaJoin[m].equalsIgnoreCase("join")) {
	        			this.remove(listaJoin[m+1]);
	        			foundSecondTable = true;
	        		  }
	        	  }
	          }
	          // Oppure non inserirle se è già presente la clausola di join in buffer
        	  else if (this.sezioni.get(j).toString().indexOf("join") > 0) {
	        	  String[] listaJoin = this.sezioni.get(j).toString().split(" ");
		          if (listaFrom[k].trim().equalsIgnoreCase(
			              listaJoin[0].trim())) trovato = true;
	        	  boolean foundSecondTable = false;
	        	  for (int m=0; m < listaJoin.length && !foundSecondTable; ++m){
	        		  if (listaJoin[m].equalsIgnoreCase("join")) {
	    		          if (listaFrom[k].trim().equalsIgnoreCase(
	    			              listaJoin[m+1].trim())) trovato = true;
	        			foundSecondTable = true;
	        		  }
	        	  }
	          }
	        }
	        if (!trovato && !listaFrom[k].trim().equals(""))
	        	this.append(new JdbcExpressionFrom(new JdbcTable(listaFrom[k].trim())));
        }
      }
    }
  }

  public void appendJoinFromWhere(String entita1, String entita2,
      String asFrom, String asWhere, JdbcWhere whereAdd, char tipoDB) {
    Vector<JdbcColumn> campiEntit1 = new Vector<JdbcColumn>();
    Vector<JdbcColumn> campiEntit2 = new Vector<JdbcColumn>();
    JdbcWhere where = new JdbcWhere(asWhere);
    JdbcWhere whereTemp = new JdbcWhere();

    // Scorro tutti i parametri e creo i link tra le tabelle
    for (int i = 0; i < where.getSezioni().size(); i++) {
      JdbcExpressionWhere exp1 = where.get(i);
      if (i <= where.getSezioni().size() - 3) {
        JdbcExpressionWhere expConf = where.get(i + 1);
        if (expConf.toString().trim().equals("=")) {
          JdbcExpressionWhere exp2 = where.get(i + 2);
          // Se le due espressioni sono colonne
          if (exp1.getTipo() == JdbcUtils.JDBC_COLUMN
              && exp2.getTipo() == JdbcUtils.JDBC_COLUMN) {
            boolean add = false;
            // Verifico che le due colonne siano di entità esatta
            if (exp1.getColumn().getTable().toString().equalsIgnoreCase(entita1)
                && exp2.getColumn().getTable().toString().equalsIgnoreCase(
                    entita2)) {
              campiEntit1.add(exp1.getColumn());
              campiEntit2.add(exp2.getColumn());
              add = true;

            } else if (exp2.getColumn().getTable().toString().equalsIgnoreCase(
                entita1)
                && exp1.getColumn().getTable().toString().equalsIgnoreCase(
                    entita2)) {
              campiEntit1.add(exp2.getColumn());
              campiEntit2.add(exp1.getColumn());
              add = true;

            }
            // Se è stato aggiunto nella join allora continuo
            if (add) {
              i += 2;
              // Se l'espressione successiva è una and non la attacco alla where
              // esterna
              if (where.get(i + 1) != null
                  && (where.get(i + 1).getTipo() == JdbcUtils.JDBC_PARTICELLA_AND || where.get(
                      i + 1).getTipo() == JdbcUtils.JDBC_PARTICELLA_OR)) i++;
              continue;
            }
          }
        }
      }
      whereTemp.append(exp1);
    }
    JdbcColumn linkCampi1[] = new JdbcColumn[campiEntit1.size()];
    JdbcColumn linkCampi2[] = new JdbcColumn[campiEntit1.size()];
    for (int i = 0; i < campiEntit1.size(); i++) {
      linkCampi1[i] = campiEntit1.get(i);
      linkCampi2[i] = campiEntit2.get(i);
    }
    JdbcExpressionFrom expFrom1 = new JdbcExpressionFrom(
        JdbcUtils.JDBC_LEFTJOIN, new JdbcTable(entita1), linkCampi1);
    JdbcExpressionFrom expFrom2 = new JdbcExpressionFrom(
        new JdbcTable(entita2), linkCampi2, expFrom1, tipoDB);
    this.append(expFrom1);
    this.append(expFrom2);
    whereAdd.append(whereTemp);
  }

}
