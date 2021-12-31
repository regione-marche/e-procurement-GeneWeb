/*
 * Created on 26/set/2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.invcom;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.dao.InvioComunicazioniDao;
import it.eldasoft.gene.db.domain.invcom.DestinatarioComunicazione;
import it.eldasoft.gene.db.domain.invcom.InvioComunicazione;
import it.eldasoft.gene.db.domain.invcom.ModelloComunicazione;
import it.eldasoft.gene.db.domain.invcom.PKDestinatarioComunicazione;
import it.eldasoft.gene.db.domain.invcom.PKInvioComunicazione;

import org.apache.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Manager per la gestione dell'invio comunicazioni.
 *
 * @author Stefano.Sabbadin
 * @since 2.0.14
 */
public class InvioComunicazioniManager {

  private static final int      MAX_NUM_TENTATIVI_INSERT_COMUNICAZIONE = 5;

  Logger                        logger                                 = Logger.getLogger(InvioComunicazioniManager.class);

  /** Manager per la generazione e gestione delle chiavi. */
  private GenChiaviManager      genChiaviManager;

  /** Dao per l'interfacciamento con W_INVCOM e tabelle figlie. */
  private InvioComunicazioniDao invioComunicazioniDao;

  /**
   * @param genChiaviManager
   *        genChiaviManager da settare internamente alla classe.
   */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  /**
   * @param invioComunicazioniDao
   *        invioComunicazioniDao da settare internamente alla classe.
   */
  public void setInvioComunicazioniDao(InvioComunicazioniDao invioComunicazioniDao) {
    this.invioComunicazioniDao = invioComunicazioniDao;
  }

  public void insertComunicazione(InvioComunicazione comunicazione) {
    // si determina il potenziale id comunicazione da utilizzare per l'inserimento
    int idcom = this.genChiaviManager.getMaxId("W_INVCOM", "IDCOM", "IDPRG='" + comunicazione.getPk().getIdProgramma() + "'");
    // si tenta di inserire la testata della comunicazione eventualmente aumentando idcom un massimo di 5 volte
    int numeroTentativi = MAX_NUM_TENTATIVI_INSERT_COMUNICAZIONE;
    boolean insert = false;
    do {
      idcom++;
      comunicazione.getPk().setIdComunicazione(new Long(idcom));
      try {
        this.invioComunicazioniDao.insertComunicazione(comunicazione);
        insert = true;
      } catch (DataIntegrityViolationException e) {
        // fallito l'ultimo tentativo emetto l'eccezione
        if (numeroTentativi == 1) {
          logger.error("Falliti " + MAX_NUM_TENTATIVI_INSERT_COMUNICAZIONE + " di inserimento in W_INVCOM", e);
          throw e;
        }
      } finally {
        numeroTentativi--;
      }
    } while (numeroTentativi > 0 && !insert);
    // inserita la testata si procede con l'inserimento dei destinatari previo svuotamento (piu' che altro per evitare che qualcuno a mano
    // abbia eliminato l'ultima comunicazione in W_INVCOM senza eliminare le tabelle correlate)
    this.invioComunicazioniDao.deleteDestinatariComunicazione(comunicazione.getPk());
    long idDestinatario = 1;
    for (DestinatarioComunicazione destinatario : comunicazione.getDestinatariComunicazione()) {
      destinatario.setPk(new PKDestinatarioComunicazione(comunicazione.getPk()));
      destinatario.getPk().setIdDestinatario(new Long(idDestinatario));
      this.invioComunicazioniDao.insertDestinatarioComunicazione(destinatario);
      idDestinatario++;
    }
  }

  public void updateStatoComunicazione(PKInvioComunicazione pk, String stato) {
    this.invioComunicazioniDao.updateStatoComunicazione(pk, stato);
  }

  public ModelloComunicazione getModelloComunicazioneByGenere(int genere) {
    return this.invioComunicazioniDao.getModelloComunicazioneByGenere(genere);
  }

  /**
   * Genera una testata comunicazione a partire da un modello di comunicazione valorizzandone le informazioni che possono essere riportate a
   * partire dal modello.
   *
   * @param modello
   *        modello da utilizzare per creare la comunicazione
   * @return comunicazione vuota riempita nei soli dati derivati dal modello
   */
  public InvioComunicazione createComunicazioneFromModello(ModelloComunicazione modello) {
    InvioComunicazione comunicazione = null;
    if (modello != null) {
      comunicazione = new InvioComunicazione();
      comunicazione.setOggetto(modello.getOggetto());
      comunicazione.setTesto(modello.getTesto());
      comunicazione.setAbilitaIntestazioneVariabile(modello.getAbilitaIntestazioneVariabile());
    }
    return comunicazione;
  }

}
