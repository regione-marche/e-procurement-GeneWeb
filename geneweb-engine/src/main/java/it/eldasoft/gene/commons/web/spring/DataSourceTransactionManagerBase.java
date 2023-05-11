/*
 * Created on 10/gen/08
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.spring;

import it.eldasoft.gene.bl.SqlManager;

import java.util.HashMap;

import javax.servlet.ServletRequest;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;

/**
 * Ridefinizione della classe per intervenire nei DB Oracle nella gestione di:
 * <ul>
 * <li>storia delle modifiche</li>
 * <li>uso dei contesti nell'estrazione dei dati</li>
 * </ul>
 *
 * @author Marco.Franceschin, Stefano.Sabbadin
 */
public class DataSourceTransactionManagerBase extends
    DataSourceTransactionManager {

  static Logger             logger           = Logger.getLogger(DataSourceTransactionManagerBase.class);

  /**
   *
   */
  private static final long serialVersionUID = -7827088291515999814L;

  private static HashMap<String, ServletRequest>    mapRequest       = new HashMap<String, ServletRequest>();

  public static ServletRequest getRequest() {
    ServletRequest req = mapRequest.get(Thread.currentThread().toString());
    //if (req != null) mapRequest.remove(Thread.currentThread().toString());
    return req;
  }

  /**
   * Operazione effettuata in ogni action dell'applicativo, per cui si setta nella mappa la request HTTP
   */
  public static void setRequest(ServletRequest request) {
    mapRequest.put(Thread.currentThread().toString(), request);
  }

  /**
   * Nel caso di apertura di una transazione, nel caso di gestione della storia delle modifiche
   * abilitata si attiva il salvataggio dello storico delle modifiche stesse
   */
  @Override
  protected void doBegin(Object transaction, TransactionDefinition definition) {
    ServletRequest req = getRequest();
    super.doBegin(transaction, definition);
    SqlManager.setUserStoriaModifiche(req);
  }
}
