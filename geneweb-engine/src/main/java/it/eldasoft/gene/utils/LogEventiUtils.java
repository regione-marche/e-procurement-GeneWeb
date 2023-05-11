/*
 * Created on 03/02/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.utils;

import it.eldasoft.gene.bl.LogEventiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionEvent;

import org.apache.log4j.Logger;


/**
 * Classe di utilit&agrave; per la tracciatura del log-eventi in db.
 *
 * @author Cristian.Febas
 */
public class LogEventiUtils {

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(LogEventiUtils.class);


  public static final String COD_EVENTO_LOGIN = "LOGIN";
  public static final String COD_EVENTO_SET_PROFILO = "SET_PROFILO";
  public static final String COD_EVENTO_SET_UFFINT = "SET_UFFINT";
  public static final String COD_EVENTO_OPEN_APPLICATION = "OPEN_APPLICATION";
  public static final String COD_EVENTO_LOGOUT = "LOGOUT";
  public static final String COD_EVENTO_INVCOM = "INVCOM";
  public static final String COD_EVENTO_CHANGE_PASSWORD = "CHANGE_PSW";
  public static final String COD_EVENTO_ADD_USER = "ADD_USER";
  public static final String COD_EVENTO_DEL_USER = "DELETE_USER";
  public static final String COD_EVENTO_READ_ART80 = "READ_ART80";
  public static final String COD_EVENTO_LOCK_LOGIN_UTENTE = "LOGIN_LOCK";
  public static final String COD_EVENTO_UNLOCK_LOGIN_UTENTE = "LOGIN_UNLOCK";
  public static final String COD_EVENTO_CHANGE_PROFILO = "CHANGE_USER_PROFILI";
  public static final String COD_EVENTO_ACCESSO_SIMULTANEO = "ACCESSO_SIMULTANEO";

  /**
   * Crea un'istanza di LogEvento,prepopolandola con alcuni dati di default.
   *
   * @param request
   *            request da cui ricava gli attributi in  sessione
   *
   * @return logEvento
   *            istanza di LogEvento parzialmente prepopolata
   *
   */
  public static LogEvento createLogEvento(HttpServletRequest request){

    LogEvento logEvento = new LogEvento();
    String codiceApplicazione = (String) request.getSession().getAttribute(CostantiGenerali.MODULO_ATTIVO);
    codiceApplicazione = UtilityStringhe.convertiNullInStringaVuota(codiceApplicazione);
    if("".equals(codiceApplicazione)){
      codiceApplicazione = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
    }
    logEvento.setCodApplicazione(codiceApplicazione);
    String codiceProfilo = (String) request.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);
    logEvento.setCodProfilo(codiceProfilo);
    String ip = request.getRemoteAddr();
    logEvento.setIp(ip);
    if(request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE) != null){
      int idUtente = ((ProfiloUtente) request.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId();
      logEvento.setIdUtente(idUtente);
    }

    return logEvento ;

  }

  /**
   * Crea un'istanza di LogEvento,prepopolandola con alcuni dati di default.
   *
   * @param sessionEvent
   *            sessione da cui si ricavano gli attributi
   *
   * @return logEvento
   *            istanza di LogEvento parzialmente prepopolata
   *
   */
  public static LogEvento createLogEvento(HttpSessionEvent sessionEvent){

    LogEvento logEvento = new LogEvento();
    String codiceApplicazione = (String) sessionEvent.getSession().getAttribute( CostantiGenerali.MODULO_ATTIVO);
    logEvento.setCodApplicazione(codiceApplicazione);
    String codiceProfilo = (String) sessionEvent.getSession().getAttribute(CostantiGenerali.PROFILO_ATTIVO);
    logEvento.setCodProfilo(codiceProfilo);

    if(sessionEvent.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE) != null){
      String ip =((ProfiloUtente) sessionEvent.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getIp();
      logEvento.setIp(ip);
      int idUtente = ((ProfiloUtente) sessionEvent.getSession().getAttribute(CostantiGenerali.PROFILO_UTENTE_SESSIONE)).getId();
      logEvento.setIdUtente(idUtente);
    }

    return logEvento ;

  }

  /**
   * Scrive sulla tabella W_LOGEVENTI
   *
   * @param logEvento
   *        classe che contiene tutti gli attributi da scrivere su W_LOGEVENTI
   *
   * @throws Exception
   *        eccezione generica
   */
  public static void insertLogEventi(LogEvento logEvento) {

    ServletContext context = SpringAppContext.getServletContext();
    String confLogEventi = ConfigManager.getValore(CostantiGenerali.PROP_LOG_EVENTI);
    confLogEventi = UtilityStringhe.convertiNullInStringaVuota(confLogEventi);
    try {
    if("1".equals(confLogEventi)){
      LogEventiManager logEventiManager = (LogEventiManager) UtilitySpring.getBean("logEventiManager", context, LogEventiManager.class);
      logEventiManager.insertLog(logEvento);
    }
    } catch (Exception le) {
      logger.error("Errore inaspettato durante la tracciatura su w_logeventi", le);
    }

  }

}
