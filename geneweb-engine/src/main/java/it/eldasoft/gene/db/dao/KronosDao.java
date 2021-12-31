/*
 * Created on 23/mar/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Dao dell'integrazione con KRONOS
 *
 * @author Stefano.Sabbadin
 */
public interface KronosDao {

  /**
   * Estrae i dati associati all'utente in input mediante l'id di richiesta accesso al sistema.
   *
   * @param id
   *        id associato alla richiesta di accesso dell'utente al sistema
   * @return stringa concatenata con le coppie chiave=valore[|chiave=valore]*
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano verificati problemi durante l'accesso ai dati
   */
  String getDatiUtente(Integer id) throws DataAccessException;

  /**
   * Estrae la temporalit&agrave; associata ad una tabella
   *
   * @param tabella
   *        nome della tabella
   *
   * @return codice della temporalit&agrave; (NT, PV, PT, VT)
   *
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  String getTemporalita(String tabella) throws DataAccessException;

  /**
   * Estrae l'elenco dei legami tra la tabella in input ed altre tabelle
   *
   * @param tabella
   *        nome della tabella
   *
   * @return lista di HashMap contenenti i campi tabella, campo, tabella_pk e
   *         campo_pk
   *
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List getCampiJoin(String tabella) throws DataAccessException;

  /**
   * Estrae l'elenco delle variabili utente definite per l'applicativo, aventi
   * il prefisso indicato (solitamente si utilizza UTE_VAR)
   *
   * @param prefissoVariabileUtente
   *        prefisso delle variabili da ricercare
   *
   * @return lista di HashMap contenenti l'elenco delle variabili utente
   *
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List getVariabiliUTE(String prefissoVariabileUtente)
      throws DataAccessException;

  /**
   * Estrae l'elenco dei valori della variabile utente in input, filtrate per
   * utente e ruolo, corrispondenti a filtri da aggiungere per estrarre l'elenco
   * dei dipendenti
   *
   * @param prefissoVariabileUtente
   *        prefisso delle variabili da ricercare
   *
   * @return lista di Tabellato contenente le coppie (codice con la stringa sql
   *         da eseguire, descrizione testuale)
   *
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List getValoriVariabileUTE(String variabileUtente, String idUtente, String idRuolo)
      throws DataAccessException;

}
