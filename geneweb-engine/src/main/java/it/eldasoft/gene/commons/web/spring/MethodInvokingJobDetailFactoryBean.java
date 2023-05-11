/*
 * Created on 12/feb/2018
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.commons.web.spring;

import it.eldasoft.gene.bl.QuartzConfigManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.utils.spring.UtilitySpring;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Wrapper dell'invoker di Spring per Quartz a cui si aggiunge la gestione della mutua esclusione e unicit&agrave; delle esecuzioni in caso
 * di pi&ugrave; nodi (cluster) o pi&ugrave; server che eseguono gli stessi task a partire dallo stesso DB.
 *
 * @author Stefano.Sabbadin
 */
public class MethodInvokingJobDetailFactoryBean extends org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean {

  Logger logger = Logger.getLogger(MethodInvokingJobDetailFactoryBean.class);

  @Override
  public Object invoke() throws InvocationTargetException, IllegalAccessException {
    // si cerca di ottenere il nome del package+classe puro, e non il proxy transazionale costruito da spring, altrimenti potrei non gestire
    // correttamente la mutua esclusione se il nome generato in altri nodi risulta diverso
    String jobClass = StringUtils.substringBefore(this.getTargetObject().getClass().getName(), "$$");
    String jobMethod = this.getTargetMethod();
    logger.debug("invoke(): inizio metodo per invocare il metodo " + jobMethod + " sull'istanza di " + jobClass);

    Object object = null;
    QuartzConfigManager manager = (QuartzConfigManager) UtilitySpring.getBean("quartzConfigManager", SpringAppContext.getServletContext(),
        QuartzConfigManager.class);
    String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);

    boolean locked = false;

    try {
      // indirizzo del nodo che esegue il task
      String server = InetAddress.getLocalHost().getHostName();
      // nome istanza del nodo che esegue il task nel caso di cluster, se valorizzato con un parametro -DjvmRoute=...
      // NOTA: non ho trovato alcun modo per leggere l'attributo jvmRoute definito nel server.xml di tomcat, pertanto l'unica
      // strada percorribile sarebbe introdurre ANCHE il valore mediante parametro -D allo startup di tomcat
      String node = System.getProperty("jvmRoute");

      // tentare inserimento lock esclusivo
      locked = manager.insertQuartzLock(codapp, jobClass, jobMethod, server, node);
      if (locked) {
        // solo in caso di ottenimento del lock esclusivo si processa il task, altrimenti se lo sta facendo un altro nodo viene bypassata
        // l'esecuzione
        object = super.invoke();
      }
    } catch (UnknownHostException e) {
      logger.error("Impossibile procedere all'esecuzione del task a causa di problemi di reperimento del nome server");
    } finally {
      if (locked) {
        manager.deleteQuartzLock(codapp, jobClass, jobMethod);
      }
    }

    logger.debug("invoke(): fine metodo");

    return object;
  }

}
