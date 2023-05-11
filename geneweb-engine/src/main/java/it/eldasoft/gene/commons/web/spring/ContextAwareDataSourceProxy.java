/*
 * Created on 18/gen/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.spring;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.utils.properties.ConfigManager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

/**
 * Proxy del datasource per poter effettuare il set dei contesti oracle
 * all'apertura di ogni connessione durante la gestione della richiesta, in modo
 * da filtrare opportunamente i dati indipendentemente dall'oggetto connessione
 * in uso e prelevato dal pool disponibile nel server
 * 
 * @author Stefano.Sabbadin
 * @since 1.4.5.1
 */
public class ContextAwareDataSourceProxy extends
    TransactionAwareDataSourceProxy {

  private final static Logger logger = Logger.getLogger(ContextAwareDataSourceProxy.class);

  public Connection getConnection() throws SQLException {
    Connection con = super.getConnection();
    setContextOnConnection(con);
    return con;
  }

  /**
   * Imposta l'eventuale contesto oracle nella connessione
   * 
   * @param con
   *        connessione sulla quale impostare il contesto
   * @throws SQLException
   */
  private void setContextOnConnection(Connection con) throws SQLException {
    if ("1".equals(ConfigManager.getValore(CostantiGenerali.PROP_CONTESTI_ABILITATI))
        && it.eldasoft.utils.sql.comp.SqlManager.DATABASE_ORACLE.equalsIgnoreCase(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE))) {
      String userId = ContextHolder.getUserId();
      String context = ContextHolder.getContext();
      if (logger.isDebugEnabled())
        logger.debug("Setting current context to '"
            + context
            + "' for user '"
            + userId
            + "' on connection "
            + con);

      CallableStatement stmt = con.prepareCall("call INFO_PERSPECTIVE_SESSION_PKG.SET_PERSPECTIVE (?, ?, ?, ?)");
      try {
        if (userId != null)
          stmt.setString(1, userId);
        else
          stmt.setNull(1, Types.VARCHAR);

          stmt.setNull(2, Types.VARCHAR);
        
        if (context != null)
          stmt.setString(3, context);
        else
          stmt.setNull(3, Types.VARCHAR);
        
          stmt.setString(4, "EXIT");
        
        stmt.execute();
      } finally {
        stmt.close();
      }
    }
  }

}
