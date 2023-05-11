/*
 * Created on 10/gen/11
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao;

import java.util.Date;
import java.util.List;

import it.eldasoft.gene.db.domain.LogEvento;

import org.springframework.dao.DataAccessException;

/**
 * Interfaccia DAO per l'accesso ai dati delle tabelle di definizione dei
 * livelli sugli utenti per le entità, in W_LOGEVENTI.
 * 
 * @author stefano.sabbadin
 * @since 1.5.4
 */
public interface LogEventiDao {

  /**
   * Inserisce un record in W_LOGEVENTI
   * 
   * @param logEvento
   *        log evento da inserire
   * @throws DataAccessException
   */
  public void insertLog(LogEvento logEvento) throws DataAccessException;

  /**
   * Elimina tutti i record in W_LOGEVENTI precedenti la data in input
   * 
   * @param data
   *        data limite prima della quale le tracciature del log vanno eliminate
   * @throws DataAccessException
   */
  public void deleteLogBeforeDate(Date data) throws DataAccessException;
  
  /**
   * Legge un evento con codice e oggetto specifici
   * 
   * @param codice
   *        codice del log da estrarre
   * @param oggetto
   *        oggetto del log da estrarre
   * @throws DataAccessException
   */
  public String searchLogFromCodOggetto(String codice, String oggetto) throws DataAccessException;
  
  /**
   * Recupera gli eventi di un particolare utente
   * 
   * @param user
   *        user da estrarre
   * @throws DataAccessException
   */
  
  public List<LogEvento> getUltimiAccessi(Long idUtente) throws DataAccessException;

}
