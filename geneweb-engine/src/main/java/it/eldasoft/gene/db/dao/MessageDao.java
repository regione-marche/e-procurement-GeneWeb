/*
 * Created on 26/gen/2018
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.db.dao;

import java.sql.Timestamp;

import org.springframework.dao.DataAccessException;


public interface MessageDao {
/**
  * @param int senderId
  *        id del mittente
  * @param String obj 
  *        testo del messaggio
  * @param String body
  *        body del messaggio ("solitamente stringa vuota")
  * @param int destId
  *        id del destinatario del messaggio
  * @param Timestamp date 
  *        data e ora attuale
  * @param int messageId 
  *        id da generare, che sarà identificativo del messaggio nella tabella w_message_in
 */
  void addMessageIn(int senderId, String obj, String body, int destId, Timestamp date, int messageId) throws DataAccessException;

  /**
   * @param int senderId
   *        id del mittente
   * @param String obj 
   *        testo del messaggio
   * @param String body
   *        body del messaggio ("solitamente stringa vuota")
   * @param int messageIdOut
   *        id da generare, che sarà identificativo del messaggio nella tabella w_message_out
   * @param Timestamp date 
   *        data e ora attuale
   */
  void addMessageOut(int senderId, String obj, String body, int messageIdOut, Timestamp date) throws DataAccessException;

  /**
   * @param int syscon
   *        id del mittente
   * @param int messageIdOut
   *        id del messaggio nella tabella w_message_out
   * @param int messageIdIn
   *        id del messaggio nella tabella w_message_in
   */
  void addMessageOutRec(int syscon, int messageIdOut , int messageIdIn) throws DataAccessException;

}
