/*
 * Created on 30-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao;

import org.springframework.dao.DataAccessException;

/**
 * Interfaccia DAO per l'accesso ai dati della tabella W_APPLICAZIONE (o
 * ELDAVER) nel database di configurazione dell'applicazione Web. Consente di
 * ottenere informazioni relative alla versione dell'applicativo memorizzata nel
 * DB.
 *
 * @author Stefano.Sabbadin
 */
public interface VersioneDao {

  /**
   * Estrae il numero di versione del modulo applicativo in input
   *
   * @param codiceApplicazione
   *        codice applicazione
   *
   * @return versione in formato N.N.N
   *
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  String getVersione(String codiceApplicazione) throws DataAccessException;

}
