/*
 * Created on 16/set/2016
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.commons.web.spring;

import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Factory wrapper della factory di Spring per risolvere i memory leak in fase di reload della web application legati all'incapacit&agrave;
 * da parte del server di fermare i thread istanziati da Quartz.
 *
 * @author Stefano.Sabbadin
 * @since 2.1.3
 */
public class SchedulerFactoryBeanWithWait extends SchedulerFactoryBean {

  @Override
  public void destroy() throws SchedulerException {
    super.destroy();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      this.logger.error(e);
    }
  }
}
