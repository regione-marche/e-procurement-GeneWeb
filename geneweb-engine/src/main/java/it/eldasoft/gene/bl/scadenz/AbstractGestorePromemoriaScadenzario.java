/*
 * Created on 28/mag/2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.scadenz;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.utils.spring.UtilitySpring;

import java.io.StringWriter;
import java.sql.SQLException;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.Log4JLogChute;

/**
 * Classe da estendere per definire un gestore di promemoria attivit&agrave; in scadenza di uno scadenzario. Permette di utilizzare un
 * template Velocity e compilarlo con i dati richiesti dal template stesso, in modo da predisporre il testo della mail di promemoria da
 * inviare.
 *
 * @author Marcello.Caminiti
 */
public abstract class AbstractGestorePromemoriaScadenzario {

  Logger logger = Logger.getLogger(AbstractGestorePromemoriaScadenzario.class);

  /** Contesto applicativo. */
  private ServletContext servletContext;

  /** Manager per la gestione delle interrogazioni al db. */
  protected SqlManager   sqlManager;

  /**
   * @return Returns the servletContext.
   */
  public ServletContext getServletContext() {
    return servletContext;
  }

  /**
   * @param servletContext
   *        servletContext da settare internamente alla classe.
   */

  public void setServletContext(ServletContext servletContext) {
    this.servletContext = servletContext;
    // Estraggo il manager per gestire diversi SQL
    this.sqlManager = (SqlManager) UtilitySpring.getBean("sqlManager", this.getServletContext(), SqlManager.class);
  }

  /**
   * Viene restituito il path della cartella nell'applicativo contenente i modelli velocity.
   *
   * @return path modelli
   *
   */
  protected String getPathModello() {
    String path = servletContext.getRealPath("/velocitymodel/");
    //path = path.replace(":\\", "://");
    //path = path.replace('\\', '/');
    return path;
  }

  /**
   * Viene implementata la logica per popolare il contesto del modello di velocity
   *
   * @return template modello
   *
   */
  abstract public String getModello();

  /**
   * Viene popolato il contesto di velocity da usare per la composizione del modello.
   *
   * @param codapp
   *        codice applicativo
   * @param idAttivita
   *        id dell'attivita di cui comporre il modello
   * @param ent
   *        entita
   * @param chiavi
   *        chiave del record presente nell'entit&agrave;, a cui risulta collegato lo scadenzario
   * @param velocityContext
   *        contesto di velocity
   * @throws SQLException
   */
  abstract public void popolaContesto(String codapp, Long idAttivita, String ent, Object[] chiavi, VelocityContext velocityContext)
      throws SQLException;

  /**
   * Viene costruito il testo del promemoria.
   *
   * @param codapp
   *        codice applicativo
   * @param idAttivita
   *        id dell'attivita di cui comporre il modello
   * @param ent
   *        entita
   * @param chiavi
   *        chiave del record presente nell'entit&agrave;, a cui risulta collegato lo scadenzario
   * @throws Exception
   *
   * @return Testo composto secondo il modello
   *
   */
  public String getTestoMail(String codapp, Long idAttivita, String ent, Object[] chiavi) throws Exception {
    VelocityEngine velocityEngine = new VelocityEngine();
    velocityEngine.setProperty(Velocity.RESOURCE_LOADER, "file");
    velocityEngine.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
    velocityEngine.setProperty(Velocity.FILE_RESOURCE_LOADER_CACHE, "false");
    velocityEngine.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
    velocityEngine.setProperty(Log4JLogChute.RUNTIME_LOG_LOG4J_LOGGER, logger.getName());

    String velocityModelPath = this.getPathModello();
    velocityEngine.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, velocityModelPath);
    velocityEngine.init();

    VelocityContext velocityContext = new VelocityContext();
    StringWriter writerUtente = new StringWriter();

    String velocityModel = this.getModello();
    Template templateUtente = velocityEngine.getTemplate(velocityModel);
    this.popolaContesto(codapp, idAttivita, ent, chiavi, velocityContext);

    templateUtente.merge(velocityContext, writerUtente);
    String testoMail = writerUtente.toString();
    return testoMail;
  }

}
