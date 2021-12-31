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
package it.eldasoft.gene.db.dao.ibatis;

import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.dao.MessageDao;

import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

public class SqlMapMessageDao extends SqlMapClientDaoSupportBase implements MessageDao{

  public void addMessageIn(int senderId, String obj, String body, int idDestinatario,Timestamp date,int  messageId) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("id",messageId);                     
    hash.put("date",date);
    hash.put("obj",obj);
    hash.put("body",body);
    hash.put("sender",senderId);
    hash.put("destinatario",idDestinatario);
    hash.put("read",new Long(0));
    getSqlMapClientTemplate().insert("insertMessageIn",hash);
   
  }

  public void addMessageOut(int senderId, String obj, String body, int messageIdOut, Timestamp date) throws DataAccessException {
    Logger logger = Logger.getLogger(SqlMapMessageDao.class);
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("id",messageIdOut);
    hash.put("date",date);
    hash.put("obj",obj);
    hash.put("body",body);
    hash.put("sender",senderId);
    if (logger.isDebugEnabled()) logger.debug("id ="+messageIdOut+", obj="+ obj + ", date" + date + ", body" + body);
    getSqlMapClientTemplate().insert("insertMessageOut",hash);
  }

  public void addMessageOutRec(int syscon, int messageIdOut, int messageIdIn) throws DataAccessException {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("messageIdIn",messageIdIn);
    hash.put("messageIdOut", messageIdOut);
    hash.put("syscon",syscon);
    getSqlMapClientTemplate().insert("insertMessageOutRec",hash);
  }

}
