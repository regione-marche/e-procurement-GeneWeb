package it.eldasoft.gene.bl;

import it.eldasoft.gene.db.dao.QuartzConfigDao;
import it.eldasoft.gene.db.domain.admin.QuartzConfig;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.scheduling.quartz.CronTriggerBean;

public class QuartzConfigManager {

  private QuartzConfigDao quartzConfigDao;

  static Logger           logger = Logger.getLogger(QuartzConfigManager.class);

  public void setQuartzConfigDao(QuartzConfigDao quartzConfigDao) {
    this.quartzConfigDao = quartzConfigDao;
  }

  public List<QuartzConfig> getQuartzByCodapp(String codapp) {
    return quartzConfigDao.getQuartzConfigByCodapp(codapp);
  }

  /**
   * Carica/ricarica l'intera lista delle pianificazioni definite nella tabella
   * W_QUARTZ ed identificate dal codice applicativo
   *
   * @param codapp
   *        Codice applicativo
   * @param ctx
   *        Contesto applicativo
   */
  public void loadListQuartzConfig(String codapp, ApplicationContext ctx) {

    List<QuartzConfig> quartzConfig = getQuartzByCodapp(codapp);
    for (int q = 0; q < quartzConfig.size(); q++) {
      String beanId = quartzConfig.get(q).getBean_id();
      String cronExpression = quartzConfig.get(q).getCron_expression();
      this.loadQuartzConfig(ctx, beanId, cronExpression);

    }
  }

  /**
   * Carica/ricarica la singola pianificazione identificata dal nome del bean
   *
   * @param ctx
   *        Contesto applicativo
   * @param beanId
   *        Indentificativo della pianificazione
   * @param cronExpression
   *        Espressione della pianificazione
   */
  public void loadQuartzConfig(ApplicationContext ctx, String beanId, String cronExpression) {
    try {
      CronTriggerBean cronTriggerBean = (CronTriggerBean) ctx.getBean(beanId);
      cronTriggerBean.setCronExpression(cronExpression);
      Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
      scheduler.rescheduleJob(cronTriggerBean.getName(), cronTriggerBean.getGroup(), cronTriggerBean);
    } catch (NoSuchBeanDefinitionException b) {
      logger.error("Il bean indicato non esiste: " + beanId, b);
    } catch (ParseException pe) {
      logger.error("Errore durante la pianificazione del trigger: " + beanId, pe);
    } catch (SchedulerException se) {
      logger.error("Errore durante la schedulazione del trigger: " + beanId, se);
    }
  }

  /**
   * Tenta di ottenere il lock esclusivo sull'esecuzione del job di Quartz in
   * input.
   *
   * @param codapp
   *            codice applicazione
   * @param jobClass
   *            classe (comprensiva di package) del job da porre in esecuzione
   * @param jobMethod
   *            metodo della classe da porre in esecuzione
   * @param server
   *            indirizzo del server
   * @param node
   *            eventuale istanza in caso di pi&uagrave; nodi
   * @return true se il lock esclusivo viene ottenuto inserendo l'occorrenza
   *         in W_QUARTZLOCK, false altrimenti (&egrave; in corso l'esecuzione
   *         da parte di un altro nodo/server)
   */
  public synchronized boolean insertQuartzLock(String codapp,
          String jobClass, String jobMethod, String server, String node) {
      logger.debug("insertQuartzLock: inizio metodo");
      boolean lock = false;

      String job = jobClass + "." + jobMethod;

      try {
          // serve per comprendere se e' possibile procedere con la insert del lock esclusivo
          boolean isReleased = true;
          // serve per comprendere se esiste una riga nella tabella per il dato job
          boolean isLocked = this.quartzConfigDao.isQuartzLock(codapp, job);
          if (isLocked) {
              // si calcola la massima data lock per la quale si procede in automatico
              // alla cancellazione se esiste il record: si suppone che un task attivo
              // da 2h sia stato fermato da un riavvio del server
              Calendar maxDataLock = Calendar.getInstance();
              maxDataLock.add(Calendar.HOUR, -2);
              // si rimuove l'eventuale lock esistente da piu' di 2 ore
              isReleased = this.quartzConfigDao.deleteQuartzLockByDate(codapp,
                      job, maxDataLock.getTime());
          }
          if (isReleased) {
              // si procede con l'ottenimento del lock esclusivo
              this.quartzConfigDao.insertQuartzLock(codapp, job, new Date(),
                      server, node);
              // NOTA: la insert potrebbe andare in errore pur avendo
              // verificato l'assenza dell'occorrenza, in quanto ad esempio su
              // Oracle la insert effettuata da un'altra transazione non
              // ancora chiusa non causa il lock sul record in attesa dello
              // sblocco della transazione bensi' non si estrae alcuna
              // occorrenza
              lock = true;
          } else {
            logger.debug("Esecuzione in corso " + job + " su altro nodo");
        }
      } catch (DataIntegrityViolationException e) {
          logger.debug("Esecuzione in corso " + job + " su altro nodo: " + e.getMessage());
      } catch (DeadlockLoserDataAccessException e) {
          logger.error(
                  "Interrotto causa deadlock l'ottenimento del lock esclusivo per "
                          + job, e);
      }
      logger.debug("insertQuartzLock: fine metodo");
      return lock;
  }

  /**
   * Rimuove il lock esclusivo per il job di Quartz in input.
   *
   * @param codapp
   *        codice applicazione
   * @param jobClass
   *        classe (comprensiva di package) del job da porre in esecuzione
   * @param jobMethod
   *        metodo della classe da porre in esecuzione
   */
  public synchronized void deleteQuartzLock(String codapp, String jobClass, String jobMethod) {
    logger.debug("deleteQuartzLock: inizio metodo");
    this.quartzConfigDao.deleteQuartzLock(codapp, jobClass + "." + jobMethod);
    logger.debug("deleteQuartzLock: fine metodo");
  }

}
