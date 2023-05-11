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
package it.eldasoft.gene.bl;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;

import it.eldasoft.gene.db.dao.AccountDao;
import it.eldasoft.gene.db.dao.MessageDao;
import it.eldasoft.gene.web.struts.admin.SalvaAccountAction;
import it.eldasoft.utils.utility.UtilityDate;
 

public class MessageManager {

  private MessageDao messageDao;    
  private AccountDao accountDao;
  private GenChiaviManager genChiaviManager;
  
  public AccountDao getAccountDao() {
    return accountDao;
  }
  
  /**
   * @param accountDao 
   *        accountDao da settare internamente alla classe.
   */
  public void setAccountDao(AccountDao accountDao) {
    this.accountDao = accountDao;
  }
  
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }
  
  /**
   * @param accountDao 
   *        accountDao da settare internamente alla classe.
   */
  public void setMessageDao(MessageDao messageDao) {
    this.messageDao = messageDao;
  }
  
  /**
   * @param subject 
   *        testo del messaggio da inviare
   * @param body 
   *        body del messaggio, solitamente string vuota
   * @param mittenteId 
   *        id dell'utente che manda il messaggio
   * @param idDestinatario 
   *        id dell'utente che riceverà il messaggio
   */
  public void insertMessage(String subject, String body, int mittenteId,int idDestinatario){
    int messageIdOut = this.genChiaviManager.getNextId("W_MESSAGE_OUT");
    Timestamp date = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
    this.messageDao.addMessageOut(mittenteId, subject, body, messageIdOut, date);
   // this.messageDao.addMessageIn(mittenteId, subject, body,idDestinatario, date);
    //this.messageDao.addMessageOutRec(messageIdOut, mittenteId);
  }
  
  /**
   * @param subject 
   *        testo del messaggio da inviare
   * @param body 
   *        body del messaggio, solitamente string vuota
   * @param mittenteId 
   *        id dell'utente che manda il messaggio
   */
  public void insertMessageToUsersAdministrator(String subject, String body, int mittenteId){
    Logger logger = Logger.getLogger(MessageManager.class);
    int messageIdOut = this.genChiaviManager.getMaxId("W_MESSAGE_OUT", "MESSAGE_ID") + 1;
    if (logger.isDebugEnabled()) logger.debug("send: new iD = "+ messageIdOut);
    Timestamp date = new Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
    List<String> list =  this.accountDao.getAccountGestoriProfilo();
    this.messageDao.addMessageOut(mittenteId, subject, body, messageIdOut, date);
    if (logger.isDebugEnabled()) logger.debug("send:aggiunto in uscita, #destinatari: " + list.size());
    //int messageId = this.genChiaviManager.getNextId("W_MESSAGE_IN", list.size());
    for(String s : list){
      int messageIdIn = this.genChiaviManager.getMaxId("W_MESSAGE_IN","MESSAGE_ID") + 1;
      int idDestinatario = Integer.parseInt(s);
      if (logger.isDebugEnabled()) logger.debug("send:aggiunto message_in, dest.id: " + s +", messageId: "+ messageIdIn);
      this.messageDao.addMessageIn(mittenteId, subject, body,idDestinatario, date, messageIdIn);
      this.messageDao.addMessageOutRec(idDestinatario, messageIdOut, messageIdIn);
      
    }
  }
}
