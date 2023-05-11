/*
 * Created on 20-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao;

import it.eldasoft.gene.db.domain.genric.CacheParametroEsecuzione;
import it.eldasoft.gene.db.domain.genric.CampoRicerca;
import it.eldasoft.gene.db.domain.genric.DatiGenRicerca;
import it.eldasoft.gene.db.domain.genric.FiltroRicerca;
import it.eldasoft.gene.db.domain.genric.GiunzioneRicerca;
import it.eldasoft.gene.db.domain.genric.GruppoRicerca;
import it.eldasoft.gene.db.domain.genric.OrdinamentoRicerca;
import it.eldasoft.gene.db.domain.genric.ParametroRicerca;
import it.eldasoft.gene.db.domain.genric.TabellaRicerca;
import it.eldasoft.gene.db.domain.genric.TrovaRicerche;
import it.eldasoft.utils.sql.comp.SqlComposerException;

import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Interfaccia DAO per l'accesso ai dati per la gestione delle ricerche,
 * relative proprietà e l'associazione o meno con i gruppi
 * 
 * @author Luca Giacomazzo
 */
public interface RicercheDao {

  /**
   * Estrae da W_GRPRIC e W_GRUPPI gli utenti associati al gruppo in analisi
   * filtrando per codiceProfilo
   * 
   * @param idGruppo
   * @param codiceApplicazione
   * @param codiceProfilo
   * @return Ritorna la lista delle ricerche associati al gruppo in analisi
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<?> getRicercheDiGruppo(int idGruppo, String codiceApplicazione, 
      String codiceProfilo) throws DataAccessException;

  /**
   * Estrae tutte le ricerche esistenti, filtrandole per codice applicazione e
   * per codice profilo attivo
   * 
   * @return Ritorna la lista delle ricerche
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<?> getAllRicercheByCodApp(String codiceApplicazione, String codiceProfilo)
      throws DataAccessException;

  /**
   * Estrae la lista di IdAccount degli utenti associati ad un gruppo
   * 
   * @param idGruppo
   * @param codiceApplicazione
   * @return Ritorna la lista di idAccount degli utenti associati ad un gruppo
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public List<?> getRicercheAssociateAGruppoAsList(int idGruppo,
      String codiceApplicazione, String profiloAttivo) 
          throws DataAccessException;

  // Effettua l'insert in W_ACCGRP di una nuova associazione tra un utente ed
  // gruppo

  /**
   * Insert dell'associazione tra gruppo e ricerche nella tabella W_GRPRIC
   * 
   * @param idGruppo
   * @param idRicerca
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public void insertAssociazioneRicercaGruppo(int idGruppo, int idRicerca)
      throws DataAccessException;

  /**
   * Delete da W_GRPRIC di tutte ricerche che sono associate ad un gruppo ed ora
   * non lo sono più
   * 
   * @param idGruppo
   * @param lista
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public int deleteRicercheNonAssociate(int idGruppo, String codApp,
      List<?> ricercheDiGruppo) throws DataAccessException;

  /**
   * Estrae la lista delle ricerche filtrando in funzione dei parametri indicati
   * nel form della pagina 'Trova Ricerche' e per codice applicazione
   * 
   * @param trovaRicerche
   *        contenitore con i criteri di ricerca
   * @return Ritorna la lista delle ricerche
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   * @throws SqlComposerException 
   */
  public List<?> getRicerche(TrovaRicerche trovaRicerche, boolean mostraRicercheBase)
      throws DataAccessException, SqlComposerException;

  /**
   * Estrae la lista delle ricerche predefinite/pubblicate filtrandole per
   * idAccount
   * 
   * @param idAccount
   * @return
   * @throws DataAccessException
   */
  public List<?> getRicerchePredefinite(int idAccount, String codiceApplicazione,
      String codiceProfilo, boolean mostraReportBase, boolean mostraReportSql)
    throws DataAccessException;
  
  /**
   * Estrae la lista delle ricerche filtrando in funzione dei parametri indicati
   * nel form della pagina 'Trova Ricerche' e per codice applicazione escludendo tutte 
   * quelle che necessitano di parametri per l'esecuzione
   * 
   * @param trovaRicerche
   *        contenitore con i criteri di ricerca
   * @return Ritorna la lista delle ricerche
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   * @throws SqlComposerException 
   */
  public List<?> getRicercheSenzaParametri(TrovaRicerche trovaRicerche, boolean mostraRicercheBase)
      throws DataAccessException, SqlComposerException;

  /**
   * Estrae la lista delle ricerche predefinite/pubblicate filtrandole per
   * idAccount escludendo tutte quelle che necessitano di parametri per l'esecuzione
   * 
   * @param idAccount
   * @return
   * @throws DataAccessException
   */
  public List<?> getRicerchePredefiniteSenzaParametri(int idAccount,
      String codiceApplicazione, String codiceProfilo, boolean mostraReportBase)
    throws DataAccessException;

  /**
   * Estrae la testata della ricerca individuata dal campo idRicerca
   * 
   * @param idRicerca
   * @return Ritorna la testata di una ricerca
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public DatiGenRicerca getTestataRicercaByIdRicerca(int idRicerca)
      throws DataAccessException;

  /**
   * Estrae dalla tabella W_RICTAB le informazioni relative alle tabelle
   * coinvolte nella ricerca in analisi
   * 
   * @param idRicerca
   * @return Ritorna le tabelle coinvolte nella ricerca
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public List<?> getTabelleRicercaByIdRicerca(int idRicerca)
      throws DataAccessException;

  /**
   * Estrae dalla tabella W_RICCAMPI le informazioni relative ai campi estratti
   * dalla ricerca in analisi
   * 
   * @param idRicerca
   * @return Ritorna i campi coinvolti nella ricerca
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public List<?> getCampiRicercaByIdRicerca(int idRicerca)
      throws DataAccessException;

  /**
   * Estrae dalla tabella W_RICJOIN le informazioni relative alle Join delle
   * ricerca in analisi
   * 
   * @param idRicerca
   * @return Ritorna le join della ricerca
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public List<?> getGiunzioniRicercaByIdRicerca(int idRicerca)
      throws DataAccessException;

  /**
   * Estrae dalla tabella W_RICPARAM i paramentri relativi alla ricerca in
   * analisi
   * 
   * @param idRicerca
   * @return Ritorna i parametri della ricerca
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public List<?> getParametriRicercaByIdRicerca(int idRicerca)
      throws DataAccessException;

  /**
   * Estrae dalla tabella W_RICFILTRI le informazioni relative ai filtri delle
   * ricerca in analisi
   * 
   * @param idRicerca
   * @return Ritorna i filtri della ricerca
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public List<?> getFiltriRicercaByIdRicerca(int idRicerca)
      throws DataAccessException;

  /**
   * Estrae dalla tabella W_RICORD le informazioni relative all'ordinamento
   * delle ricerca in analisi
   * 
   * @param idRicerca
   * @return Ritorna l'ordinamento della ricerca
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public List<?> getOrdinamentiRicercaByIdRicerca(int idRicerca)
      throws DataAccessException;

  /**
   * Ritorna il numero di parametri della ricerca (base/avanzata)
   * @param idRicerca
   * @return
   * @throws DataAccessException
   */
  Integer getNumeroParametriRicercaByIdRicerca(int idRicerca) throws DataAccessException;
  
  /**
   * Ritorna il numero di parametri del prospetto
   * @param idRicerca
   * @return
   * @throws DataAccessException
   */
  Integer getNumeroParametriProspettoByIdRicerca(int idRicerca) throws DataAccessException;
  
  /**
   * Ritorna il numero di parametri del report base/avanzato collegato come sorgente dati al prospetto
   * @param idRicerca
   * @return
   * @throws DataAccessException
   */
  Integer getNumeroParametriReportSorgenteProspettoByIdRicerca(int idRicerca) throws DataAccessException;
  
  
  /**
   * Estrae la "famiglia" della ricerca
   * @param idRicerca
   * @return
   * @throws DataAccessException
   */
  Integer getFamigliaRicercaById(int idRicerca) throws DataAccessException;
  /**
   * Effettua la cancellazione di un elenco di ricerche a partire da idRicerca
   * nella tabella W_RICERCHE
   * 
   * @param idRicerca
   *        chiavi primarie della ricerche da eliminare
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public void deleteRicercheById(List<?> idRicerca) throws DataAccessException;

  /**
   * Effettua la cancellazione nella tabella W_GRPRIC dei gruppi associati alle
   * ricerche individuate a partire da idRicerca
   * 
   * @param idRicerca
   *        chiavi primarie delle ricerche da eliminare
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public void deleteGruppiByIdRicerca(List<?> idRicerca)
      throws DataAccessException;

  /**
   * Effettua la cancellazione nella tabella W_RICTAB delle tabelle associate
   * alle ricerche individuate a partire da idRicerca
   * 
   * @param idRicerca
   *        chiavi primarie delle ricerche da eliminare
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public void deleteTabelleByIdRicerca(List<?> idRicerca)
      throws DataAccessException;

  /**
   * Effettua la cancellazione nella tabella W_RICCAMPI dei campi associati alle
   * ricerche individuate a partire da idRicerca
   * 
   * @param idRicerca
   *        chiavi primarie delle ricerche da eliminare
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public void deleteCampiByIdRicerca(List<?> idRicerca) throws DataAccessException;

  /**
   * Effettua la cancellazione nella tabella W_RICJOIN delle join associate alle
   * ricerche individuate a partire da idRicerca
   * 
   * @param idRicerca
   *        chiavi primarie delle ricerche da eliminare
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public void deleteJoinByIdRicerca(List<?> idRicerca) throws DataAccessException;

  /**
   * Effettua la cancellazione nella tabella W_RICFILTRI dei filtri associati
   * alle ricerche individuate a partire da idRicerca
   * 
   * @param idRicerca
   *        chiavi primarie delle ricerche da eliminare
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public void deleteFiltriByIdRicerca(List<?> idRicerca)
      throws DataAccessException;

  /**
   * Effettua la cancellazione nella tabella W_RICORD degli ordinamenti
   * associati alle ricerche individuate a partire da idRicerca
   * 
   * @param idRicerca
   *        chiavi primarie delle ricerche da eliminare
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public void deleteOrdinamentiByIdRicerca(List<?> idRicerca)
      throws DataAccessException;

  /**
   * Effettua la cancellazione nella tabella W_RICPARAM dei parametri associati
   * alle ricerche individuate a partire da idRicerca
   * 
   * @param idRicerca
   *        chiavi primarie delle ricerche da eliminare
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public void deleteParametriByIdRicerca(List<?> idRicerca)
      throws DataAccessException;

  /**
   * Inserisce la tabella usata in una ricerca in W_RICTAB
   * 
   * @param tabellaRicerca
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public void insertTabellaRicerca(TabellaRicerca tabellaRicerca)
      throws DataAccessException;

  /**
   * Inserisce il campo usato in una ricerca in W_RICCAMPI
   * 
   * @param campoRicerca
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public void insertCampoRicerca(CampoRicerca campoRicerca)
      throws DataAccessException;

  /**
   * Inserisce la giunzione definita in una nuova ricerca in W_RICJOIN
   * 
   * @param joinRicerca
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public void insertGiunzioneRicerca(GiunzioneRicerca joinRicerca)
      throws DataAccessException;

  /**
   * Inserisce i paramentri in una nuova ricerca in W_RICPARAM
   * 
   * @param parametroRicerca
   * @throws DataAccessException
   */
  public void insertParametroRicerca(ParametroRicerca parametroRicerca)
      throws DataAccessException;

  /**
   * Inserisce il filtro definito in una nuova ricerca in W_RICFILTRI
   * 
   * @param filtroRicerca
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public void insertFiltroRicerca(FiltroRicerca filtroRicerca)
      throws DataAccessException;

  /**
   * Inserisce un ordinamento definito in una nuova ricerca in W_RICORD
   * 
   * @param ordinamentoRicerca
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public void insertOrdinamentoRicerca(OrdinamentoRicerca ordinamentoRicerca)
      throws DataAccessException;

  /**
   * Inserisce in W_GRPRIC il gruppo associato ad una nuova ricerca
   * 
   * @param gruppoRicerca
   * @throws DataAccessException
   */
  public void insertGruppoRicerca(GruppoRicerca gruppoRicerca)
      throws DataAccessException;

  /**
   * Inserisce in W_RICTAB la testata di una nuova ricerca
   * 
   * @param testataRicerca
   *        testata della ricerca
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public void insertTestataRicerca(DatiGenRicerca datiGenRicerca)
      throws DataAccessException;

  /**
   * Aggiorna il campo personale di una ricerca con modello con il valore passato
   * @param idRicerca
   * @param i
   */
  void updatePersonale(int idRicerca, int i) throws DataAccessException;

  /**
   * Elimina le occorrenze nella W_CACHERICPAR
   * 
   * @param idAccount
   *        identificativo univoco dell'account
   * @param idRicerca
   *        identificativo univoco della ricerca
   * @throws DataAccessException
   */
  void deleteCacheParametriEsecuzione(int idAccount, int idRicerca)
      throws DataAccessException;

  /**
   * Elimina tutte le occorrenze nella W_CACHERICPAR di un utente
   * 
   * @param idAccount
   *        identificativo univoco dell'account
   * @throws DataAccessException
   */
  void deleteCacheParametriEsecuzioneUtente(Integer idAccount)
      throws DataAccessException;

  /**
   * Elimina tutte le occorrenze nella W_CACHERICPAR di una ricerca
   * 
   * @param idRicerca
   *        identificativo univoco della ricerca
   * @throws DataAccessException
   */
  void deleteCacheParametriEsecuzioneRicerca(int idRicerca)
      throws DataAccessException;

  /**
   * Inserisce nella banca dati (tabella W_CACHERICPAR) un parametro istanziato
   * per la corretta esecuzione di una ricerca nella tabella di cache per le
   * interazioni dell'utente.
   * 
   * @param cacheParametroEsecuzione
   *        parametro da inserire
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void insertCacheParametroEsecuzione(
      CacheParametroEsecuzione cacheParametroEsecuzione)
      throws DataAccessException;

  /**
   * Estrae il valore di un parametro per l'esecuzione da parte di un utente di
   * una ricerca
   * 
   * @param idAccount
   *        identificativo univoco dell'account
   * @param idRicerca
   *        identificativo univoco della ricerca
   * @param codice
   *        codice del parametro
   * @return valore del parametro
   * @throws DataAccessException
   */
  String getCacheParametroEsecuzione(int idAccount, int idRicerca, String codice)
      throws DataAccessException;

  /**
   * Ritorna l'id ricerca avente il codice di pubblicazione nel WEB valorizzato
   * con il parametro in input
   * 
   * @param codReportWS
   *        codice di pubblicazione report nel WEB
   * @return id della ricerca corrispondente al codice pubblicazione report nel
   *         WEB, null altrimenti
   * @throws DataAccessException
   */
  Integer getIdRicercaByCodReportWS(String codReportWS) throws DataAccessException;
}