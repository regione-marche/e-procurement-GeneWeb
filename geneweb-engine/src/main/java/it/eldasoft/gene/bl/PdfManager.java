/*
 * Created on 12/apr/10
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl;

import it.eldasoft.utils.properties.ConfigManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Manager per la gestione della business logic relativa alla creazione di file
 * PDF a partire da altri formati di documento
 * 
 * @author Stefano.Sabbadin
 * @since 1.5.0
 */
public class PdfManager {

  public static final String PROP_CONVERTITORE_PDF = "it.eldasoft.conversione.pdf.task";

  /** Logger Log4J di classe */
  Logger                     logger                = Logger.getLogger(PdfManager.class);

  /**
   * Testa la configurazione della property e fornisce l'indicazione
   * 
   * @return true se il convertitore Pdf è configurato, false altrimenti
   */
  public static boolean isConfigurato() {
    String task = ConfigManager.getValore(PdfManager.PROP_CONVERTITORE_PDF);
    return task != null && task.length() > 0;
  }

  /**
   * Genera un documento pdf a partire dal documento in input
   * 
   * @param pathFileSorgente
   *        path completo del documento da convertire
   * 
   * @return path completo del file pdf generato
   */
  public String convertiDocumento(String pathFileSorgente) {
    if (logger.isDebugEnabled())
      logger.debug("convertiDocumento(" + pathFileSorgente + "): inizio metodo");

    String pathFileDestinazione = null;

    File task = new File(
        ConfigManager.getValore(PdfManager.PROP_CONVERTITORE_PDF));
    Vector<String> parametriChiamata = new Vector<String>();
    if (task.getAbsolutePath().toLowerCase().indexOf(".cmd") > 0) {
      parametriChiamata.add("cmd.exe");
      parametriChiamata.add("/C");
    }
    parametriChiamata.add(task.getAbsolutePath());
    parametriChiamata.add(new File(pathFileSorgente).getAbsolutePath());

    try {
      if (logger.isDebugEnabled()) {
        StringBuffer strPar = new StringBuffer();
        for (int i = 0; i < parametriChiamata.size(); i++)
          strPar.append(parametriChiamata.get(i)).append(" ");
        logger.debug("Si sta per eseguire la chiamata " + strPar.toString());
      }

      Process proc = Runtime.getRuntime().exec(
          (String[]) parametriChiamata.toArray(new String[0]), null,
          task.getParentFile());
      InputStream stdoutStream = new BufferedInputStream(proc.getInputStream());
      proc.waitFor();

      StringBuffer buffer = new StringBuffer();
      for (;;) {
        int c = stdoutStream.read();
        if (c == -1) break;
        buffer.append((char) c);
      }
      stdoutStream.close();
      
      if (proc.exitValue() != 0)
        buffer.append("(Codice di ritorno = ").append(proc.exitValue()).append(")");
      
      if (buffer.length() > 0)
        throw new RuntimeException(buffer.toString());

      pathFileDestinazione = pathFileSorgente.substring(0,
          pathFileSorgente.lastIndexOf('.'))
          + ".pdf";

      if (logger.isDebugEnabled()) {
        logger.debug("E' stato prodotto il file " + pathFileDestinazione);
      }
    } catch (IOException e) {
      throw new RuntimeException(
          "Errore durante l'esecuzione del comando per la generazione pdf a partire dal file "
              + pathFileSorgente, e);
    } catch (InterruptedException e) {
      throw new RuntimeException(
          "Errore durante l'attesa per l'esecuzione del comando per la generazione pdf a partire dal file "
              + pathFileSorgente, e);
    }

    if (logger.isDebugEnabled())
      logger.debug("convertiDocumento(" + pathFileSorgente + "): fine metodo");

    return pathFileDestinazione;
  }
}
