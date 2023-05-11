/*
 * Created on 25-mag-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.console.bl.schedric;

import it.eldasoft.console.db.dao.SchedRicDao;
import it.eldasoft.console.db.domain.schedric.CodaSched;
import it.eldasoft.console.db.domain.schedric.SchedRic;
import it.eldasoft.console.db.domain.schedric.TrovaCodaSched;
import it.eldasoft.console.db.domain.schedric.TrovaSchedRic;
import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityDate;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

/**
 * Manager che si occupa di gestire tutte le operazioni di business logic sulla
 * parte di gestione delle schedulazioni per le ricerche
 * 
 * @author Francesco.DeFilippis
 */
public class SchedRicManager {

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(SchedRicManager.class);

  /**
   * Reference al Dao per l'interrogazione della tabella W_SCHEDRIC e
   * W_CODASCHED
   */
  private SchedRicDao      schedRicDao;

  /** Reference al Dao per ottenere le chiavi delle entità */
  private GenChiaviManager genChiaviManager;

  /**
   * @param schedRicDao
   *        schedRicDao da settare internamente alla classe.
   */
  public void setSchedRicDao(SchedRicDao schedRicDao) {
    this.schedRicDao = schedRicDao;
  }

  /**
   * @param genChiaviManager
   *        genChiaviManager da settare internamente alla classe.
   */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  /**
   * @param trovaSched
   *        parametri di filtro per reperire le schedulazioni
   * @return Ritorna la lista di tutte le schedulazioni estratte nel rispetto
   *         dei criteri di filtro in input
   */
  public List<?> getSchedulazioniRicerche(TrovaSchedRic trovaSched)
      throws SqlComposerException {
    return this.schedRicDao.getSchedulazioniRicerche(trovaSched);
  }

  public SchedRic getSchedulazioneRicerca(int idSchedRic)
      throws SqlComposerException {
    return this.schedRicDao.getSchedulazioneRicerca(idSchedRic);
  }

  /**
   * Metodo per la gestione della cancellazione multipla di schedulazioni dalla
   * lista
   * 
   * @param elencoSchedRic
   */
  public void deleteSchedulazioniRicerche(int[] elencoSchedRic) {
    this.schedRicDao.deleteSchedulazioniRicerche(elencoSchedRic);
  }

  /**
   * Metodo per la gestione della cancellazione singola
   * 
   * @param idSchedRic
   */
  public void deleteSchedulazioneRicerca(int idSchedRic) {
    this.deleteSchedulazioniRicerche(new int[] { idSchedRic });
  }

  /**
   * Metodo per la gestione dell'inserimento di una schedulazione
   * 
   * @param schedRic
   * @return id della schedulazione creata
   */
  public int insertSchedulazioneRicerca(SchedRic schedRic) {
    int idSchedRic = genChiaviManager.getNextId("W_SCHEDRIC");
    schedRic.setIdSchedRic(new Integer(idSchedRic));
    schedRic.setDataProxEsec(CalcoloDate.calcolaDataProxEsec(schedRic,
        new Date()).getData());
    schedRicDao.insertSchedulazioneRicerca(schedRic);
    return idSchedRic;
  }

  /**
   * Metodo per la gestione dell'update di una schedulazione
   * 
   * @param schedRic
   */
  public void updateSchedulazioneRicerca(SchedRic schedRic) {
    schedRic.setDataProxEsec(CalcoloDate.calcolaDataProxEsec(schedRic,
          new Date()).getData());
    this.schedRicDao.updateSchedulazioneRicerca(schedRic);
  }

  /**
   * Metodo che modifica il valore del campo W_SCHEDRIC.ATTIVO settando il
   * valore del campo con il parametro
   * 
   * @param idSchedRic
   *        id del record
   * @param attivo
   *        Valore da associare al campo
   * @param dataProxEsec
   *        data prossima esecuzione, nel caso di riattivazione
   */
  public void updateAttivaDisattivaSchedulazione(int idSchedRic, String attivo,
      Date dataProxEsec, int ora, int minuti) {
    this.schedRicDao.updateAttivaDisattivaSchedulazione(idSchedRic,
        new Integer(attivo).intValue(), dataProxEsec, ora, minuti);
  }

  /**
   * Ritorna la coda di schedulazioni eseguite, in base ai filtri
   * 
   * @param trovaCodaSched
   *        criteri di filtro da soddisfare nella ricerca
   * @return Lista coda di schedulazioni dei report in corso o terminate
   * @throws SqlComposerException
   */
  public List<?> getCodaSchedulazioni(TrovaCodaSched trovaCodaSched)
      throws SqlComposerException {
    return this.schedRicDao.getCodaSchedulazioni(trovaCodaSched);
  }

  /**
   * Ritorna il dettaglio di una schedulazione eseguita
   * 
   * @param id
   *        id della schedulazione
   * @return dettaglio dell'esecuzione
   * @throws SqlComposerException
   */
  public CodaSched getSchedulazioneEseguita(int id) throws SqlComposerException {
    return this.schedRicDao.getSchedulazioneEseguita(id);
  }

  /**
   * Cancella la schedulazione dalla coda di quelle in esecuzione o eseguite
   * 
   * @param idSchedulazione
   *        id della schedulazione da eliminare dalla coda
   */
  public void deleteSchedulazioneEseguita(int idSchedulazione) {
    this.schedRicDao.deleteSchedulazioneEseguita(idSchedulazione);
  }

  /**
   * Metodo di controllo che restituisce true se la schedulazione è associata ad
   * un report senza paramentri
   * 
   * @param idSchedRic
   * @return
   * @throws DataAccessException
   */
  public boolean isSchedulazioneReportSenzaParametri(int idSchedRic)
      throws DataAccessException {
    return this.schedRicDao.isSchedulazioneReportSenzaParametri(idSchedRic);
  }

  /**
   * Ritorna la lista delle schedulazioni da eseguire per ora e data di
   * esecuzione
   * 
   * @param ora
   * @param minuti
   * @param dataEsec
   * @param codiceApplicazione
   * @return
   * @throws DataAccessException
   */
  public List<?> getSchedulazioniPerOrario(int ora, int minuti, Date dataEsec, String codiceApplicazione)
      throws DataAccessException {
    int oraInMinuti = ora * 60 + minuti;
    Date data = UtilityDate.convertiData(UtilityDate.convertiData(dataEsec,
        UtilityDate.FORMATO_GG_MM_AAAA), UtilityDate.FORMATO_GG_MM_AAAA);
    return this.schedRicDao.getSchedulazioniPerOrario(new Integer(oraInMinuti),
        data, codiceApplicazione);
  }

  /**
   * Inserisce un record nella coda delle schedulazioni
   * 
   * @param codaSched
   */
  public void insertCodaSched(CodaSched codaSched) throws DataAccessException {
    int idCodaSched = genChiaviManager.getNextId("W_CODASCHED");
    codaSched.setIdCodaSched(idCodaSched);
    schedRicDao.insertCodaSched(codaSched);

  }

  /**
   * Aggiorna la data prossima esecuzione della schedulazione
   * 
   * @param idSchedRic
   * @param dataProxEsec
   * @param ora
   * @param minuti
   * @param dataUltEsec
   */
  public void updateDataProxEsecSchedRic(int idSchedRic, Date dataProxEsec,
      int ora, int minuti, Date dataUltEsec) throws DataAccessException {
    this.schedRicDao.updateDataProxEsecSchedRic(new Integer(idSchedRic),
        dataProxEsec, ora, minuti, dataUltEsec);
  }

  /**
   * Aggiorna lo stato del record di coda schedulazioni aggiungendo
   * eventualmente pure il messaggio
   * 
   * @param idCodaSched
   * @param stato
   * @param msg
   * @throws DataAccessException
   */
  public void updateStatoCodaSched(int idCodaSched, int stato, String msg,
      String nomeFile) throws DataAccessException {
    this.schedRicDao.updateStatoCodaSched(new Integer(idCodaSched),
        new Integer(stato), msg, nomeFile);
  }

}