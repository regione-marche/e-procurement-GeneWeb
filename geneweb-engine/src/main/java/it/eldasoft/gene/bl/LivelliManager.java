/*
 * Created on 1-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl;

import it.eldasoft.gene.db.dao.LivelliDao;
import it.eldasoft.utils.profiles.cache.DizionarioLivelli;
import it.eldasoft.utils.profiles.domain.Livello;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * Manager adibito al caricamento del dizionario di entità che prevede una
 * gestione di filtro sui livelli degli utenti
 * 
 * @author Stefano.Sabbadin
 */
public class LivelliManager {

  /** DAO per l'accesso ai livelli definiti per le entità */
  private LivelliDao livelliDao;

  /** Logger Log4J di classe */
  static Logger      logger = Logger.getLogger(LivelliManager.class);

  /**
   * @param livelliDao
   *        livelliDao da settare internamente alla classe.
   */
  public void setLivelliDao(LivelliDao livelliDao) {
    this.livelliDao = livelliDao;
  }

  /**
   * Esegue il caricamento della gestione dei livelli utente per le entità
   */
  public void carica() {
    List<?> listaLivelli = this.livelliDao.getElencoLivelli();

    DizionarioLivelli diz = DizionarioLivelli.getInstance();
    Livello livello = null;
    for (int i = 0; i < listaLivelli.size(); i++) {
      livello = (Livello) listaLivelli.get(i);
      diz.put(livello.getTabella(), livello);
    }
  }

}
