package it.eldasoft.gene.utils;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.owasp.csrfguard.log.ILogger;
import org.owasp.csrfguard.log.LogLevel;

/**
 * Adaptation of org.owasp.csrfguard.log.Logger in order to use application log
 * @author gabriele.nencini
 *
 */
public class OwaspLogger implements ILogger {

  private static final long serialVersionUID = 1L;
  private final static Logger LOGGER = Logger.getLogger("org.owasp.csrfguard");
  
  @Override
  public void log(String msg) {
    LOGGER.info(msg.replaceAll("(\\r|\\n)", ""));
  }

  @Override
  public void log(Exception exception) {
    LOGGER.log(Level.WARN, exception.getLocalizedMessage(), exception);
  }

  @Override
  public void log(LogLevel level, String msg) {
 // Remove CR and LF characters to prevent CRLF injection
    String sanitizedMsg = msg.replaceAll("(\\r|\\n)", "");
    
    switch(level) {
        case Trace:
            LOGGER.trace(sanitizedMsg);
            break;
        case Debug:
            LOGGER.debug(sanitizedMsg);
            break;
        case Info:
            LOGGER.info(sanitizedMsg);
            break;
        case Warning:
            LOGGER.warn(sanitizedMsg);
            break;
        case Error:
            LOGGER.error(sanitizedMsg);
            break;
        case Fatal:
            LOGGER.fatal(sanitizedMsg);
            break;
        default:
            throw new RuntimeException("unsupported log level " + level);
    }
  }

  @Override
  public void log(LogLevel level, Exception exception) {
    switch(level) {
      case Trace:
          LOGGER.log(Level.TRACE, exception.getLocalizedMessage(), exception);
          break;
      case Debug:
          LOGGER.log(Level.DEBUG, exception.getLocalizedMessage(), exception);
          break;
      case Info:
          LOGGER.log(Level.INFO, exception.getLocalizedMessage(), exception);
          break;
      case Warning:
          LOGGER.log(Level.WARN, exception.getLocalizedMessage(), exception);
          break;
      case Error:
          LOGGER.log(Level.ERROR, exception.getLocalizedMessage(), exception);
          break;
      case Fatal:
          LOGGER.log(Level.FATAL, exception.getLocalizedMessage(), exception);
          break;
      default:
          throw new RuntimeException("unsupported log level " + level);
  
    }
  }

}
