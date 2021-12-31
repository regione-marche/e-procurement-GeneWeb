/*
 * Created on 3-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao;

import it.eldasoft.gene.db.domain.permessi.PermessoEntita;

import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Interfaccia DAO per l'accesso ai dati della tabella G_PERMESSI
 *
 * @author Luca.Giacomazzo
 */
public interface PermessiDao {

  /**
   * Funzione per l'estrazione dei permessi di una entita
   *
   * @param campoChiave nome del campo chiave
   * @param valoreChiave valore del campo chiave
   * @return
   * @throws DataAccessException
   */
  List<?> getPermessiEntita(String campoChiave, String valoreChiave)
  throws DataAccessException;

  /**
   * Funzione per estrarre i permessi di un utente su un lavoro specifico
   *
   * @param campoChiave nome del campo chiave
   * @param valoreChiave valore del campo chiave
   * @param idAccount
   * @return
   * @throws DataAccessException
   */
  public PermessoEntita getPermessoEntitaByIdAccount(String campoChiave,
      String valoreChiave, int idAccount) throws DataAccessException;

  /**
   * Funzione per estrarre la lista completa degli account con i rispetti permessi
   * sull'entita' in analisi
   *
   * @param campoChiave
   * @param valoreChiave
   * @param idAccount
   * @param codiceUffint
   * @return
   * @throws DataAccessException
   */
  public List<?> getPermessiEntitaAccount(String campoChiave,
      String valoreChiave, int idAccount, String codiceUffint) throws DataAccessException;


  /**
   * Funzione per cancellare i permessi dalla G_PERMESSI a partire da idPermesso
   *
   * @param listaPermessiDaCancellare contenente gli idPermessi
   * @return Ritorna il numero di record cancellati
   * @throws DataAccessException
   */
  public int deletePermessi(List<?> listaPermessiDaCancellare) throws DataAccessException;

  /**
   * Funzione per effettuare l'update di un permesso a partire di idPermesso
   *
   * @param permessoEntita
   * @throws DataAccessException
   */
  public void updatePermesso(PermessoEntita permessoEntita) throws DataAccessException;

  /**
   * Funzione per effettaure l'insert di un nuovo permesso
   *
   * @param permessoEntita
   * @throws DataAccessException
   */
  public void insertPermesso(PermessoEntita permessoEntita) throws DataAccessException;

  /**
   * Funzione per determinare se nella tabella G_PERMESSI esistono dei permessi
   * predefiniti per l'utente di riferimento e per il valore del
   *
   * @param idAccount
   * @param predefinito
   * @return Ritorna il numero di permessi predefiniti per l'utente predefinito
   * @throws DataAccessException
   */
  public int getNumeroPermessiPredefiniti(Integer riferimento, Integer predefinito)
      throws DataAccessException;

  /**
   * Funzione per effettuare l'insert di un permesso predefinito
   *
   * @param permessoEntita
   * @param idAccount id dell'account che sta definendo il permesso predefinito
   * @param predefinito valore del campo G_PERMESSI.PREDEF, il quale può cambiare
   *        per Lavori, Gare, ecc..
   * @throws DataAccessException
   */
  public void insertPermessoPredefinito(PermessoEntita permessoEntita,
      Integer idAccount, Integer predefinito) throws DataAccessException;

  /**
   * Funzione per la cancellazione dei premessi predefiniti a partire dall'utente
   * di riferimento e dal valore del predefinito
   *
   * @param riferimento id account di riferimento per il permesso
   * @param predefinito valore del campo G_PERMESSI.PREDEF
   * @return ritorna il numero di record cancellati
   * @throws DataAccessException
   */
  public int deletePermessiPredefinitiByIdAccount(Integer riferimento,
      Integer predefinito) throws DataAccessException;

}