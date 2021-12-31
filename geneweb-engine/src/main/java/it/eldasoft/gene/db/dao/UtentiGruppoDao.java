/*
 * Created on 14-lug-2006
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
import java.util.Map;

import org.springframework.dao.DataAccessException;

/**
 * Interfaccia DAO per l'accesso ai dati per la gestione degli utenti, relative proprietà 
 * e l'associazione o meno con i gruppi  
 *
 * @author Luca Giacomazzo
 */
public interface UtentiGruppoDao {
  
  /**
   * Estrae da USRSYS e W_GRUPPI gli utenti associati al gruppo in analisi
   * 
   * @param idGruppo
   * @return Ritorna la lista degli utenti associati al gruppo in analisi
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati    
   */
  List<?> getUtentiDiGruppo(int idGruppo) throws DataAccessException;

  /**
   * Estrae da USRSYS tutti gli utenti 
   * 
   * @param idGruppo
   * @return Ritorna la lista degli utenti
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati    
   */  
  List<?> getAllUtenti() throws DataAccessException;
  
  /**
   * Estrae la lista di IdAccount degli utenti associati ad un gruppo
   * @param idGruppo
   * @return Ritorna la lista di idAccount degli utenti associati ad un gruppo
   * @throws DataAccessException
   */
  public List<?> getUtentiAssociatiAGruppo(int idGruppo) throws DataAccessException;
  
  /**
   * Estrae la lista di IdAccount degli utenti associati ad un gruppo
   * @param idGruppo
   * @return Ritorna la lista di idAccount degli utenti associati ad un gruppo
   * @throws DataAccessException
   */
  public Map<?,?> getUtentiAssociatiAGruppoasMap(int idGruppo) throws DataAccessException;
  
  /**
   * Effettua l'insert in W_ACCGRP di una nuova associazione tra un utente ed gruppo
   * @param idGruppo
   * @param idAccount
   * @param priorita
   * @throws DataAccessException
   */
  public void insertAssociazioneAccountGruppo(int idGruppo, int idAccount, int priorita) throws DataAccessException;
  
  /**
   * Delete da W_ACCGRP di tutti degli utenti che erano associati ad un gruppo ed ora non lo sono più
   * @param idGruppo
   * @param lista
   * @throws DataAccessException
   */
  public int deleteAccountNonAssociati(int idGruppo, List<?> lista) throws DataAccessException;
  
}