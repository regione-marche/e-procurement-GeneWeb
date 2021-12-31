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
package it.eldasoft.gene.bl;

import it.eldasoft.gene.db.dao.LogEventiDao;
import it.eldasoft.gene.db.domain.LogEvento;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Manager per la gestione della tracciatura di eventi nella tabella W_LOGEVENTI
 *
 * @author stefano.sabbadin
 * @since 1.5.4
 */
public class LogEventiManager {

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(LogEventiManager.class);
  

  // traccia gli eventi su file di log
  private final DateFormat YYYYMMDD_HHMMSS = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
  private final String CSV_SEPARATOR  = ";";
  private final String CSV_VALUE_BEGIN    = "\"";
  private final String CSV_VALUE_END  = "\"";
  private org.apache.log4j.Logger loggerEvent = org.apache.log4j.Logger.getLogger("events");

  /**
   * Manager per la generazione delle chiavi
   */
  private GenChiaviManager genChiaviManager;

  /**
   * Dao per la gestione delle operazioni sul DB
   */
  private LogEventiDao     logEventiDao;

  /**
   * @param genChiaviManager
   *        genChiaviManager da settare internamente alla classe.
   */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  /**
   * @param logEventiDao
   *        logEventiDao da settare internamente alla classe.
   */
  public void setLogEventiDao(LogEventiDao logEventiDao) {
    this.logEventiDao = logEventiDao;
  }

  public void insertLog(LogEvento logEvento) {
    
    int id = this.genChiaviManager.getNextId("W_LOGEVENTI");
    logEvento.setId(id);
    this.logEventiDao.insertLog(logEvento);
    
    StringBuilder value = new StringBuilder();
    value.append(CSV_VALUE_BEGIN).append(id).append(CSV_VALUE_END).append(CSV_SEPARATOR)
         .append(CSV_VALUE_BEGIN).append(YYYYMMDD_HHMMSS.format(logEvento.getData().getTime())).append(CSV_VALUE_END).append(CSV_SEPARATOR)
         .append(CSV_VALUE_BEGIN).append(logEvento.getLivEvento()).append(CSV_VALUE_END).append(CSV_SEPARATOR)
         .append(CSV_VALUE_BEGIN).append((logEvento.getCodEvento() != null ? logEvento.getCodEvento() : "")).append(CSV_VALUE_END).append(CSV_SEPARATOR)
         .append(CSV_VALUE_BEGIN).append(logEvento.getOggEvento() != null ? logEvento.getOggEvento() : "").append(CSV_VALUE_END).append(CSV_SEPARATOR)
         .append(CSV_VALUE_BEGIN).append(logEvento.getDescr() != null ? logEvento.getDescr() : "").append(CSV_VALUE_END).append(CSV_SEPARATOR)
         .append(CSV_VALUE_BEGIN).append(logEvento.getErrmsg() != null ? logEvento.getErrmsg() : "").append(CSV_VALUE_END).append(CSV_SEPARATOR)
         .append(CSV_VALUE_BEGIN).append((logEvento.getCodProfilo() != null ? logEvento.getCodProfilo() : "")).append(CSV_VALUE_END).append(CSV_SEPARATOR)
         .append(CSV_VALUE_BEGIN).append((logEvento.getIdUtente() != null ? logEvento.getIdUtente() : "")).append(CSV_VALUE_END).append(CSV_SEPARATOR)
         .append(CSV_VALUE_BEGIN).append(logEvento.getIp() != null ? logEvento.getIp() : "").append(CSV_VALUE_END).append(CSV_SEPARATOR);
    this.loggerEvent.info(value);
  }

  public void deleteLogBeforeDate(Date data) {
    this.logEventiDao.deleteLogBeforeDate(data);
  }
  
  public String searchLogFromCodOggetto(String codice, String oggetto) {
    return this.logEventiDao.searchLogFromCodOggetto(codice,oggetto);
  }

}
