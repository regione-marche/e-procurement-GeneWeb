/*
 * Created on 20-lug-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web;

import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

/**
 * Listener per la gestione del numero massimo di utenti che possono connettersi
 * all'applicativo
 *
 * @author Stefano.Sabbadin
 */
public class ListenerSessioniUtente implements HttpSessionListener {

  static Logger               logger                         = Logger.getLogger(ListenerSessioniUtente.class);

  /**
   * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
   */
  public void sessionCreated(HttpSessionEvent arg0) {
  }

  /**
   * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
   */
  public void sessionDestroyed(HttpSessionEvent arg0) {
    // alla distruzione di una sessione si deve deallocare una connessione
    // all'applicativo per renderla disponibile ad altri utenti
    int livEvento = 1;
    String errMsgEvento = "";
    LogEvento logEvento = LogEventiUtils.createLogEvento(arg0);
    logEvento.setLivEvento(livEvento);
    logEvento.setCodEvento(LogEventiUtils.COD_EVENTO_LOGOUT);
    logEvento.setDescr("Logout id sessione = " + arg0.getSession().getId());
    logEvento.setErrmsg(errMsgEvento);
    LogEventiUtils.insertLogEventi(logEvento);

    LimitatoreConnessioniUtenti.getInstance().deallocaConnessione(
        arg0.getSession().getId());
  }

}
