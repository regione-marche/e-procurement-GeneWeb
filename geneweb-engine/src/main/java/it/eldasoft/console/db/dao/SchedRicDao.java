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
package it.eldasoft.console.db.dao;

import it.eldasoft.console.db.domain.schedric.CodaSched;
import it.eldasoft.console.db.domain.schedric.SchedRic;
import it.eldasoft.console.db.domain.schedric.TrovaCodaSched;
import it.eldasoft.console.db.domain.schedric.TrovaSchedRic;
import it.eldasoft.utils.sql.comp.SqlComposerException;

import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Interfaccia DAO per l'accesso ai dati della tabella W_SCHEDRIC e W_CODASCHED.
 * 
 * @author Francesco.DeFilippis
 */
public interface SchedRicDao {

  /**
   * Funzione che restituisce l'elenco delle ricerche schedulate che rispettano
   * i criteri di ricerca in input
   * 
   * @param trovaSched
   *        filtro di ricerca per schedulazioni
   * @return Lista contenente le schedulazioni
   */
  List<?> getSchedulazioniRicerche(TrovaSchedRic trovaSched)
      throws DataAccessException, SqlComposerException;

  /**
   * Funzione che restituisce la ricerca schedulata nell'applicativo
   * 
   * @param idSchedric
   *        identificativo della schedulazione
   * @return elemento contenente il dettaglio della schedulazione
   * @throws DataAccessException
   */
  SchedRic getSchedulazioneRicerca(int idSchedRic)
      throws DataAccessException;

  /**
   * Inserisce una schedulazione
   * 
   * @param schedRic
   *        record da inserire
   * @throws DataAccessException
   */
  void insertSchedulazioneRicerca(SchedRic schedRic) throws DataAccessException;

  /**
   * Funzione che modifica il campo W_SCHEDRIC.ATTIVO con il valore passato, ed
   * aggiorna data prossima esecuzione se deve essere riattivata la
   * schedulazione
   * 
   * @param idSchedRic
   *        id del record
   * @param attivo
   *        Valore da attribuire al campo W_SCHEDRIC.ATTIVO
   * @param dataProxEsec
   *        data prossima esecuzione da inserire in caso di attivazione
   * @param ora ora esecuzione
   * @param minuti minuto di esecuzione
   * @throws DataAccessException
   */
  void updateAttivaDisattivaSchedulazione(int idSchedRic, int attivo,
      Date dataProxEsec, int ora, int minuti) throws DataAccessException;

  /**
   * Aggiorna una schedulazione
   * 
   * @param schedRic
   * @throws DataAccessException
   */
  void updateSchedulazioneRicerca(SchedRic schedRic) throws DataAccessException;

  /**
   * Elimina un elenco di schedulazioni ricerche
   * 
   * @param id
   *        elenco degli id delle schedulazioni da eliminare
   * @throws DataAccessException
   */
  void deleteSchedulazioniRicerche(int[] id) throws DataAccessException;

  /**
   * Ritorna la coda di schedulazioni eseguite, in base ai filtri
   * 
   * @param trovaCodaSched
   *        criteri di filtro da soddisfare nella ricerca
   * @return Lista coda di schedulazioni dei report in corso o terminate
   * @throws SqlComposerException
   */
  List<?> getCodaSchedulazioni(TrovaCodaSched trovaCodaSched)
      throws DataAccessException, SqlComposerException;

  /**
   * Ritorna il dettaglio di una schedulazione eseguita
   * 
   * @param id
   *        id della schedulazione
   * @return dettaglio dell'esecuzione
   * @throws SqlComposerException
   */
  CodaSched getSchedulazioneEseguita(int id) throws DataAccessException;

  /**
   * Cancella la schedulazione dalla coda di quelle in esecuzione o eseguite
   * 
   * @param idSchedulazione
   *        id della schedulazione da eliminare dalla coda
   */
  void deleteSchedulazioneEseguita(int id) throws DataAccessException;

  /**
   * Controlla se la schedulazione è associata ad una ricerca con parametri
   * 
   * @param idSchedRic
   * @return
   * @throws DataAccessException
   */
  boolean isSchedulazioneReportSenzaParametri(int idSchedRic) throws DataAccessException;
  
  /**
   * Ritorna la lista delle schedulazioni da eseguire in base all'ora e alla data
   * 
   * @param ora
   * @param minuti
   * @param dataEsec
   * @param codiceApplicazione
   * @return
   * @throws DataAccessException
   */
  List<?> getSchedulazioniPerOrario(Integer oraInMinuti,Date dataEsec, String codiceApplicazione) throws DataAccessException;
  
  /**
   * Inserisce un record nella W_CODASCHED
   * @param codaSched
   * @throws DataAccessException
   */
  void insertCodaSched(CodaSched codaSched) throws DataAccessException;
  
  /**
   * Aggiorna la data prossima esecuzione
   * @param idSchedRic
   * @param dataProxEsec
   * @throws DataAccessException
   */
  void updateDataProxEsecSchedRic(Integer idSchedRic,Date dataProxEsec, int ora, int minuti, Date dataUltEsec) throws DataAccessException;
  
  /**
   * Aggiorna lo stato della coda aggiungendo l'eventuale messaggio
   * @param idCodaSched
   * @param stato
   * @param msg
   * @throws DataAccessException
   */
  void updateStatoCodaSched(Integer idCodaSched,Integer stato,String msg,String nomeFile) throws DataAccessException;
}