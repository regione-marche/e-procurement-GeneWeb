/*
 * Created on 02-ott-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao;

import it.eldasoft.gene.db.domain.UfficioIntestatario;

import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Interfaccia DAO per l'accesso ai dati della tabella UFFINT.
 * 
 * @author Stefano.Sabbadin
 */
public interface UffintDao {

  /**
   * Metodo per estrarre la lista di tutti gli uffici intestatari
   * 
   * @return Ritorna la lista di tutti gli uffici intestatari
   */
  public List<?> getUfficiIntestatari() throws DataAccessException;

  /**
   * Estrare il singolo ufficio intestatario a partire dalla chiave
   * 
   * @param codice
   *        chiave primaria di UFFINT
   * @return record estratto a partire dalla chiave
   * @throws DataAccessException
   */
  public UfficioIntestatario getUfficioIntestatarioByPK(String codice)
      throws DataAccessException;
  /**
   * Estrare il singolo ufficio intestatario con le informazioni aggiuntive di Address e Nation che servono principalmente a NSO a partire dalla chiave
   * 
   * @param codice
   *        chiave primaria di UFFINT
   * @return record estratto a partire dalla chiave
   * @throws DataAccessException
   */
  public UfficioIntestatario getUfficioIntestatarioByPKWithAddressAndNation(String codice)
      throws DataAccessException;

  /**
   * Metodo per estrarre la lista degli uffici intestatari a cui un utente e'
   * associato
   * 
   * @param idAccount
   *        id account
   * @return Ritorna la lista degli uffici a cui un utente e' associato
   */
  public List<?> getUfficiIntestatariAccount(int idAccount)
      throws DataAccessException;

  /**
   * Delete da USR_EIN di tutti gli uffici che erano associati ad un utente
   * 
   * @param idAccount
   *        idAccount a cui eliminare l'associazione con gli uffici
   * @throws DataAccessException
   */
  public int deleteUfficiAccount(Integer idAccount) throws DataAccessException;

  /**
   * Delete da USR_EIN di tutti gli uffici che erano associati ad un utente ed
   * ora non lo sono più
   * 
   * @param idAccount
   *        idAccount a cui eliminare l'associazione con gli uffici indicati
   * @param listaUfficiAssociati
   *        lista di codici degli uffici non piu' associati al'account
   * @throws DataAccessException
   */
  public int deleteUfficiNonAssociatiAccount(int idAccount,
      List<?> listaUfficiAssociati) throws DataAccessException;

  /**
   * Effettua l'insert in USR_EIN di una nuova associazione tra un utente ed
   * ufficio intestatario
   * 
   * @param codUfficio
   * @param idAccount
   * @throws DataAccessException
   */
  public void insertAssociazioneAccountUfficio(String codUfficio, int idAccount)
      throws DataAccessException;

}
