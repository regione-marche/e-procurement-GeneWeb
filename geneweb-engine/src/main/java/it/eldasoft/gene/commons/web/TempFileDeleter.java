/*
 * Created on 07-ott-2009
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * Cancellazione dei file temporanei presenti nella directory temporanea
 * dell'application server al momento di invalidazione della sessione
 * dell'utente. I file sono stati memorizzati in tale directory durante tutta la
 * sessione dell'utente
 *
 * @author Luca.Giacomazzo
 */
public class TempFileDeleter implements HttpSessionBindingListener,
    Serializable {

  /** UID */
  private static final long serialVersionUID = 8510640134465010102L;

  /** Nome dei file nella directory temporanea */
  private List<String>      tempFileNames    = new ArrayList<String>();

  public TempFileDeleter() {
    super();
  }

  /**
   * Aggiunta di un file che deve essere cancellato qunado la sessione scade o
   * viene invalidata
   *
   * @param filename
   *        nome del file nel directory temporanea che deve essere cancellato.
   */
  public void addTempFile(String filename) {
    this.tempFileNames.add(filename);
  }

  /**
   * Controllo se un file e' presente nella lista dei file da cancellare
   *
   * @param filename
   *        nome del file nella directory temporanea.
   * @return Un valore booleano che indica se il file e' presente nella lista o
   *         meno.
   */
  public boolean isTempFileAvailable(String filename) {
    return (this.tempFileNames.contains(filename));
  }

  /**
   * Associa questo oggetto in sessione (e niente di più), al momento della
   * creazione della sessione stessa.
   *
   * @param event
   *        evento di bind (associazione) alla sessione.
   */
  public void valueBound(HttpSessionBindingEvent event) {
    return;
  }

  /**
   * Quando questo oggetto viene rimosso dalla sessione (incluso anche la
   * invalidazione o il time-out della sessione stessa) i file che sono stati
   * aggiunti all'ArrayList vengono ciclati e rimossi dalla cartella temp
   * dell'application server.
   *
   * @param event
   *        evento di unbind (disassociazione) della sessione.
   */
  public void valueUnbound(HttpSessionBindingEvent event) {
    Iterator<String> iter = this.tempFileNames.listIterator();
    String pathTempDir = System.getProperty("java.io.tmpdir");
    while(iter.hasNext()) {
      String filename = iter.next();
      File file = new File(pathTempDir, filename);
      if(file.exists())
        file.delete();
    }
  }

}