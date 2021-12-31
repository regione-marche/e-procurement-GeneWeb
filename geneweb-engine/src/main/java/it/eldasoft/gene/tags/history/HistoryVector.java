/*
 * Created on 29-nov-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.history;

import java.io.Serializable;
import java.util.Vector;

/**
 * Oggetto che gestisce un vettore di history Item
 * @author cit_franceschin
 *
 */
public class HistoryVector implements Serializable {

  /** UID */
  private static final long serialVersionUID = -637658293654068008L;
  
  private Vector<HistoryItem> vect;

  public HistoryVector() {
    this.vect = new Vector<HistoryItem>();
  }


  /**
   * @return Returns the vect.
   */
  public Vector<HistoryItem> getVect() {
    return vect;
  }

  @Override
  public String toString() {
    return vect.toString();
  }

}