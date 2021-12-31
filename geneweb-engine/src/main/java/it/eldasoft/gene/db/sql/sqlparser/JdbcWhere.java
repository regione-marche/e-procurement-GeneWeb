package it.eldasoft.gene.db.sql.sqlparser;

import it.eldasoft.gene.db.sql.stringsql.SQLSelect;

import java.util.Enumeration;
import java.util.Vector;

public class JdbcWhere {

  /**
   * Sezioni delle where
   */
  private Vector<JdbcExpressionWhere> sezioni;

  private JdbcFrom from=null;

  public JdbcWhere() {
    sezioni = new Vector<JdbcExpressionWhere>();
  }

  public JdbcWhere(String sez) {
    sezioni = new Vector<JdbcExpressionWhere>();
    // Divido per , e aggiungo tutte le espressioni
    String divs[] = SQLSelect.dividiStringa(sez, " ");
    for (int i = 0; i < divs.length; i++) {
      this.append(JdbcExpressionWhere.getExpressionWhere(divs[i]));
    }
  }

  /**
   * Appendo un'espressione alla where
   *
   * @param aSez
   *        Espressione da appendere
   */
  public void append(JdbcExpressionWhere aSez) {
    // Se si tratta del primo parametro non permetto di inserire una particella
    if(aSez.getTipo() == JdbcUtils.JDBC_PARTICELLA_OR || aSez.getTipo() == JdbcUtils.JDBC_PARTICELLA_AND){
     if(sezioni.size()==0)
       return;
      else{
        JdbcExpressionWhere last=this.sezioni.get(this.sezioni.size()-1);
        // Se il precedente e una particella non aggiungo la sezione
        if(last.getTipo() == JdbcUtils.JDBC_PARTICELLA_OR || last.getTipo() == JdbcUtils.JDBC_PARTICELLA_AND)
          return;
      }
    }

    sezioni.add(aSez);
  }

  /**
   * Aggiunge un'espressione generica
   *
   * @param aSez
   */
  public void append(JdbcExpression aSez) {
    this.append(new JdbcExpressionWhere(aSez));
  }

  /**
   * Aggiungo tutte le sezioni della where
   *
   * @param where
   */
  public void append(JdbcWhere where) {
    Vector<JdbcExpressionWhere> sezioniWhere = where.getSezioni();
    for(int i=0; i < sezioniWhere.size(); i++){
      this.append((JdbcExpression) sezioniWhere.get(i));
    }
  }

  public String toString(boolean startWithWhere) {
    StringBuffer lsbTmp = new StringBuffer();
    String lsWhereJoin="";
    if(from!=null)
    {
      lsWhereJoin=from.getWhereJoin();
    }

    if (sezioni.size() == 0 && ( lsWhereJoin==null || lsWhereJoin.length()==0)) return "";
    if (startWithWhere) {
      lsbTmp.append(" where ");
    }
    // Scorro tutti gli elementi
    for (Enumeration<JdbcExpressionWhere> e = sezioni.elements(); e.hasMoreElements();) {
      JdbcExpressionWhere el = e.nextElement();
      if (e.hasMoreElements() || (el.getTipo() != JdbcUtils.JDBC_PARTICELLA_OR && el.getTipo() != JdbcUtils.JDBC_PARTICELLA_AND))
    	  lsbTmp.append(el.toString());
    }
    if(lsWhereJoin!=null && lsWhereJoin.length()>0){
      if(sezioni.size()>0)
        lsbTmp.append(" and ");
      lsbTmp.append(lsWhereJoin);
    }

    return lsbTmp.toString();
  }

  /**
   * Conversione in stringa della where
   */
  @Override
  public String toString() {
    return this.toString(true);
  }

  /**
   * @return Returns the sezioni.
   */
  public Vector<JdbcExpressionWhere> getSezioni() {
    return sezioni;
  }

  /**
   * Funzione che da l'elenco dei parametri
   *
   * @return
   */
  public Vector<JdbcParametro> getParametri() {
    return JdbcUtils.getParametri(this.sezioni);
  }

  /**
   * Funzione che aggiunge una where con vari parametri
   *
   * @param where
   *        Stringa con la where con ? al posto dei parametri
   * @param vector
   *        Vettore di tipo JdbcParametro
   */

  public void append(String where, Vector<JdbcParametro> vector) {
    // Trasformo il vettore in array
    JdbcParametro parametri[] = new JdbcParametro[vector.size()];
    for (int i = 0; i < vector.size(); i++)
      parametri[i] = vector.get(i);
    this.append(new JdbcExpression(where, parametri));
  }

  public JdbcExpressionWhere get(int idx){
   if(idx>=0 && idx<this.sezioni.size())
     return this.sezioni.get(idx);
   return null;
  }

  public void append(String where){
    boolean trovato = false;
    for(int i=0; i < this.sezioni.size() && !trovato; i++){
      JdbcExpressionWhere sezionePresente = this.sezioni.get(i) ;
      if(sezionePresente.getTipo() != JdbcUtils.JDBC_PARTICELLA_AND && sezionePresente.getTipo() != JdbcUtils.JDBC_PARTICELLA_OR){
        if (sezionePresente.toString().trim().equalsIgnoreCase(where.toString().trim()))
          trovato = true;
      }
    }
    if (!trovato)
      this.append(new JdbcExpression(where));
  }

  /**
   * @return Returns the from.
   */
  public JdbcFrom getFrom() {
    return from;
  }


  /**
   * @param from The from to set.
   */
  public void setFrom(JdbcFrom from) {
    this.from = new JdbcFrom();
    this.from.append(from);
  }

}
