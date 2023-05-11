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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

/**
 * Oggetto che gestisce un vettore di history Item
 * @author cit_franceschin
 *
 */
public class HistoryVector implements Serializable {

  // Costanti
  private static final long serialVersionUID = -637658293654068008L;
  
  // Campi
  private HttpSession session;
  private List<HistoryItem> items;

  // Costruttori
  public HistoryVector(final HttpSession session) {
	this.session = session;
    items = new ArrayList<HistoryItem>();
  }
  
  // Metodi
  public HistoryItem get(final int index) {
	  return items.get(index);
  }
  
  public void push(final HistoryItem item) {
	  items.add(item);
  }
  
  public HistoryItem pop() {
	  HistoryItem item = items.remove(items.size() - 1); //pop();
	  item.clearDeftrova(session);
	  
	  return item;
  }
  
  public HistoryItem popWithoutClear() {
	  return items.remove(items.size() - 1);
  }
  
  public HistoryItem update(final HistoryItem item) {
	  return items.set(size() - 1, item);
  }
  
  public boolean isEmpty() {
	  return items.isEmpty();
  }
  
  public List<HistoryItem> popMultiple(final int amount) {
	  final List<HistoryItem> poppedItems = new ArrayList<HistoryItem>();
	  
	  for (int i = 0; i < amount; i++) {
		  poppedItems.add(pop());
	  }
	  
	  return poppedItems;
  }
  
  public void clear() {
	  while(!items.isEmpty()) {
		  pop();
	  }
  }
  
  public HistoryItem peek() {
	  if (items.isEmpty()) return null;
	  return items.get(items.size() - 1);
  }
  
  public int size() {
	  return items.size();
  }
  
  @Override
  public String toString() {
    return items.toString();
  }

}