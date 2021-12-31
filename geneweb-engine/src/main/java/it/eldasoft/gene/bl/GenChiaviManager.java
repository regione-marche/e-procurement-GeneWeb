/*
 * Created on 07-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl;

import it.eldasoft.gene.db.dao.GenChiaviDao;

/**
 * Manager che si occupa di gestire le operazioni di business logic sulla tabella W_GENCHIAVI
 *
 * @author Luca.Giacomazzo
 */
public class GenChiaviManager {

  private GenChiaviDao genChiaviDao;

  public GenChiaviDao getGenChiaviDao() {
    return genChiaviDao;
  }

  public void setGenChiaviDao(GenChiaviDao genChiaviDao) {
    this.genChiaviDao = genChiaviDao;
  }

  /**
   * Ritorna il prossimo id univoco da utilizzare per un inserimento, sfruttando W_GENCHIAVI.
   *
   * @param tabella
   *        nome della tabella
   * @return id da utilizzare per l'inserimento
   */
  public int getNextId(String tabella) {
    return this.genChiaviDao.getNextId(tabella, 1);
  }

  /**
   * Ritorna il prossimo id univoco da utilizzare <b>per una serie di inserimenti</b>, sfruttando W_GENCHIAVI. L'occorrenza in W_GENCHIAVI
   * verr&agrave; fatta avanzare del numIdAllocati in modo tale da poterli utilizzare liberamente senza effettuare ulteriori richieste di
   * Id.
   *
   * @param tabella
   *        nome della tabella
   * @param numIdAllocati
   *        numero di id da generare
   * @return id da utilizzare per il prossimo inserimento, da incrementare un numero di volte pari a numIdAllocati
   */
  public int getNextId(String tabella, int numIdAllocati) {
    return this.genChiaviDao.getNextId(tabella, numIdAllocati);
  }

  /**
   * Ritorna l'ultimo id utilizzato per inserire nella tabella.
   *
   * @param tabella
   *        nome della tabella
   * @param chiave
   *        campo chiave numerico
   * @return ultimo id inserito, 0 altrimenti
   */
  public int getMaxId(String tabella, String chiave) {
    return this.genChiaviDao.getMaxId(tabella, chiave, null);
  }

  /**
   * Ritorna l'ultimo id utilizzato per inserire nella tabella con l'aggiunta di eventuali condizioni di filtro.
   *
   * @param tabella
   *        nome della tabella
   * @param chiave
   *        campo chiave numerico
   * @return ultimo id inserito, 0 altrimenti
   */
  public int getMaxId(String tabella, String chiave, String condizioniExtra) {
    return this.genChiaviDao.getMaxId(tabella, chiave, condizioniExtra);
  }

}
