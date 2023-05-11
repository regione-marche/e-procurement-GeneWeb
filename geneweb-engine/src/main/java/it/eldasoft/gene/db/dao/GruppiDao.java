/*
 * Created on 03-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao;

import it.eldasoft.gene.db.domain.admin.Gruppo;

import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Interfaccia DAO per l'accesso ai dati della tabella W_GRUPPI nel database di
 * configurazione dell'applicazione Web.
 * 
 * @author Luca.Giacomazzo
 */
public interface GruppiDao {

  /**
   * Estrae un gruppo a partire dall'id del gruppo
   * 
   * @param idGruppo
   *        idGruppo del gruppo
   * 
   * @return oggetto {@link it.eldasoft.gene.db.domain.Gruppo} individuato
   *         dall'id del gruppo in input
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  Gruppo getGruppoById(int idGruppo) throws DataAccessException;

  /**
   * Estrae la lista dei gruppi dalla tabella W_GRUPPI ordinata per il campo
   * NOME
   * 
   * @return oggetto {@link java.util.List}
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<?> getGruppiOrderByNome(String codiceProfilo) throws DataAccessException;

  /**
   * Estrae l'elenco di codici delle funzioni abilitate per l'utente tramite i
   * gruppi di appartenenza
   * 
   * @param gruppo
   *        oggetto contenente i dati da inserire nella tabella W_GRUPPI
   * @param codiceProfilo codice del profilo a cui il gruppo e' associato
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void insertGruppo(Gruppo gruppo, String codiceProfilo) throws DataAccessException;

  /**
   * Aggiorna la password per l'account in input, individuato dalla sua chiave
   * primaria e per sicurezza anche dalla vecchia password
   * 
   * @param gruppo
   *        bean contenente i dati da aggiornare
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void updateGruppo(Gruppo gruppo) throws DataAccessException;

  /**
   * Elimina il gruppo dalla tabella W_GRUPPI, a partire dalla chiave primaria
   * 
   * @param idGruppo
   *        chiave primaria del gruppo
   * 
   * @return int
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void deleteGruppo(int idGruppo) throws DataAccessException;

  /**
   * Estrae dalla tabella W_ACCGRP la lista degli account associati ad un gruppo
   * 
   * @param idGruppo
   *        idGruppo del gruppo
   * 
   * @return oggetto {@link java.util.List}
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<?> getIdAccountByIdGruppo(int idGruppo) throws DataAccessException;

  /**
   * Estrae dalla tabella W_GRPRIC il numero di ricerche associate ad un gruppo
   * filtrate per codice applicativo
   * 
   * @param idGruppo
   *        id del gruppo
   * @param codApp
   *        codice dell'applicazione in esecuzione
   * 
   * @return oggetto {@link java.util.List}
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  int getNumeroRicercheByIdGruppoCodApp(int idGruppo, String codApp)
      throws DataAccessException;

  /**
   * Estrae dalla tabella W_GRPRIC il numero di ricerche associate ad un gruppo
   * filtrate escludendo un codice applicativo
   * 
   * @param idGruppo
   *        id del gruppo
   * @param codApp
   *        codice dell'applicazione in esecuzione
   * 
   * @return oggetto {@link java.util.List}
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  int getNumeroRicercheByIdGruppoAltriCodApp(int idGruppo, String codApp)
      throws DataAccessException;

  /**
   * Estrae dalla tabella W_GRPMOD il numero di modelli associati ad un gruppo
   * 
   * @param idGruppo
   *        idGruppo del gruppo
   * @param codApp
   *        codice dell'applicazione in esecuzione
   * 
   * @return oggetto {@link java.util.List}
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  int getNumeroModelliByIdGruppoCodApp(int idGruppo, String codApp)
      throws DataAccessException;

  /**
   * Estrae dalla tabella W_GRPMOD il numero di modelli associati ad un gruppo
   * filtrati escludendo un codice applicativo
   * 
   * @param idGruppo
   *        idGruppo del gruppo
   * @param codApp
   *        codice dell'applicazione in esecuzione
   * 
   * @return oggetto {@link java.util.List}
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  int getNumeroModelliByIdGruppoAltriCodApp(int idGruppo, String codApp)
      throws DataAccessException;

  /**
   * Estrae la lista dei gruppi (id, nome e descrizione) associati ad una
   * ricerca da W_GRPRIC e da W_GRUPPI
   * 
   * @param idRicerca
   *        idRicerca della ricerca in analisi
   * @return oggetto {@link java.util.List}
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<?> getGruppiByIdRicerca(int idRicerca) throws DataAccessException;

  /**
   * Estrae dalla tabella W_GRUPPI l'elenco dei gruppi associati al profilo
   * attivo, con il numero di associazioni di ricerche, modelli ed utenti a
   * partire dallo stesso codice profilo
   * 
   * @param codiceProfilo
   *        codice profilo attivo
   * 
   * @return oggetto {@link java.util.List}
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<?> getGruppiConNumeroAssociazioniByCodApp(String codiceProfilo)
      throws DataAccessException;

  List<?> getGruppiByIdModello(int idModello) throws DataAccessException;
}