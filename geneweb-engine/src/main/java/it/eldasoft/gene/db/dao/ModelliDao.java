/*
 * Created on 21-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao;

import it.eldasoft.gene.db.domain.genmod.CacheParametroComposizione;
import it.eldasoft.gene.db.domain.genmod.DatiModello;
import it.eldasoft.gene.db.domain.genmod.ParametroComposizione;
import it.eldasoft.gene.db.domain.genmod.ParametroModello;
import it.eldasoft.gene.db.domain.genmod.TrovaModelli;
import it.eldasoft.utils.sql.comp.SqlComposerException;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

/**
 * Interfaccia DAO per l'accesso ai dati per la gestione dei modelli, relative
 * proprietà e l'associazione o meno con i gruppi
 * 
 * @author Luca Giacomazzo
 */
public interface ModelliDao {

  /**
   * Estrae la lista dei modelli associati al gruppo in analisi e filtrati per
   * codice applicazione
   * 
   * @param idGruppo
   *        id del gruppo
   * @param codiceApplicazione
   *        codice dell'applicazione in esecuzione
   * @param codiceProfilo
   *        codice del profilo attivo
   * @return Ritorna la lista dei modelli filtrati per codice applicazione
   *         associati al gruppo in analisi
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<?> getModelliDiGruppo(int idGruppo, String codiceApplicazione,
      String codiceProfilo) throws DataAccessException;

  /**
   * Estrae la lista dei modelli filtrati per codice applicazione
   * 
   * @param codiceApplicazione
   *        codice dell'applicazione in uso
   * @param codiceProfilo
   *        codice del profilo attivo
   * @return Ritorna la lista dei modelli filtrati per codice applicazione
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<?> getAllModelliByCodApp(String codiceApplicazione, String codiceProfilo)
      throws DataAccessException;

  /**
   * Cancella i record non associati o non più associati al gruppo in analisi e
   * con codice applicazione pari a quello dell'applicazione in uso
   * 
   * @param idGruppo
   * @param codiceApplicazione
   *        codice dell'applicazione in uso
   * @param listaModelliNonAssociati
   * @return Ritorna il numero di record cancellati
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  int deleteModelliNonAssociati(int idGruppo, String codiceApplicazione,
      List<?> listaModelliNonAssociati) throws DataAccessException;

  /**
   * Estrae come Map
   * 
   * @param idGruppo
   * @param codiceApplicazione
   *        codice dell'applicazione in uso
   * @return Ritorna i modelli associati al gruppo in analisi come oggetto Map
   * @throws DataAccessException
   */
  Map<?,?> getModelliAssociatiAGruppoasMap(int idGruppo, String codiceApplicazione,
      String codiceProfilo) throws DataAccessException;

  /**
   * Inserisce un record nella tabella associativa W_GRPRIC
   * 
   * @param idGruppo
   * @param idModello
   *        id del modello da associare al gruppo
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void insertAssociazioneModelloGruppo(int idGruppo, int idModello)
      throws DataAccessException;

  /**
   * Funzione che estrae la lista degli oggetti filtrando per trovaModelli
   * 
   * @param trovaModelli
   *        Dati di filtro sui modelli
   * @return lista dei modelli
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   * @throws SqlComposerException
   */
  List<?> getModelli(TrovaModelli trovaModelli) throws DataAccessException,
      SqlComposerException;

  /**
   * Estrae la lista dei modelli predefiniti/pubblicati filtrandoli per
   * idAccount, codice applicazione, ed eventualmente per entità principale
   * 
   * @param idAccount
   *        identificativo univoco dell'account
   * @param codiceApplicazione
   *        codice dell'applicazione
   * @param codiceProfilo
   *        codice del profilo attivo
   * @param entita
   *        entità principale attribuita al modello
   * @param riepilogativo
   *        includere i modelli riepilogativi (da lanciare una volta sola su
   *        tutte le occorrenze), oppure no (da lanciare una volta per ogni
   *        occorrenza); queste tipologie di modelli si lanciano solo sui report
   *        e non sulle schede
   * @return lista dei modelli pubblicati filtrati per account e codice
   *         applicazione
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<?> getModelliPredefiniti(int idAccount, String codiceApplicazione,
      String codiceProfilo, String entita, boolean riepilogativo)
      throws DataAccessException;

  /**
   * Funzione che estrae i dati di un modello partendo da suo identificativo
   * 
   * @param idModello
   *        Id del modello
   * @return Map con i campi settati
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  DatiModello getModelloById(int idModello) throws DataAccessException;

  /**
   * Funzione che estrae la lista dei gruppi che hanno visibilita su un modello
   * 
   * @param idModello
   *        chiave del modello
   * @return elenco dei gruppi associati al modello
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<?> getGruppiModello(int idModello) throws DataAccessException;

  /**
   * Funzione che restituisce l'elenco di tutti i gruppi con la proprietà
   * associato settata a true o false in funzione se il gruppo appartiene gia al
   * modello
   * 
   * @param idModello
   *        Identificativo del modello
   * @return List Lista con tutti i gruppi
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<?> getGruppiModelloPerModifica(int idModello, String codiceProfilo)
      throws DataAccessException;

  /**
   * Funzionalita che esegue l'update di un modello
   * 
   * @param datiModello
   *        Dati del modello
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void updateModello(DatiModello datiModello) throws DataAccessException;

  /**
   * Inserimento di un modello con dati relativi
   * 
   * @param datiModello
   *        Dati del modello
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void insertModello(DatiModello datiModello) throws DataAccessException;

  /**
   * Funzione che esegue l'update dei gruppi associati ad un modello
   * 
   * @param idModello
   *        Identificativo del modello
   * @param idGruppi
   *        Elenco dei gruppi da associare
   * @param codApp
   *        codice applicazione
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void updateGruppiModello(int idModello, Integer[] idGruppi, String codApp)
      throws DataAccessException;

  /**
   * Funzione che esegue l'eliminazione di un modello e dei gruppi ad esso
   * associati
   * 
   * @param idModello
   *        Identificativo del modello
   * @throws DataAccessException
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void deleteModello(int idModello) throws DataAccessException;

  /**
   * Funzione che esegue l'eliminazione dei gruppi attribuiti ad un modello
   * 
   * @param idModello
   *        Identificativo del modello
   * @throws DataAccessException
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void deleteGruppiModello(int idModello) throws DataAccessException;

  /**
   * Metodo che ritorna la chiave nella tabella dei modelli a partire dal nome
   * del file
   * 
   * @param nomeFile
   *        nome del file
   * @param codApp
   *        codice dell'applicazione
   * @return identificativo univoco (chiave primaria) del record nei modelli
   *         associato ai dati in input
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  int getIdModelloByNomeFileCodApp(String nomeFile, String codApp)
      throws DataAccessException;

  /**
   * Funzione che estrae la lista dei parametri associati al modello
   * 
   * @param idModello
   *        chiave del modello
   * @return elenco dei parametri associati al modello
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<?> getParametriModello(int idModello) throws DataAccessException;

  /**
   * Funzione che estrae un parametro di un modello
   * 
   * @param idModello
   *        chiave del modello
   * @param progressivo
   *        progressivo univoco del parametro
   * @return record individuato dalla chiave composta dai parametri in input
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  ParametroModello getParametroModello(int idModello, int progressivo)
      throws DataAccessException;

  /**
   * Metodo che ritorna il primo progressivo disponibile e non usato di
   * parametro per un modello
   * 
   * @param idModello
   *        identificativo univoco del modello
   * @return primo progressivo libero ed utilizzabile per il modello, 0
   *         altrimenti
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  int getNuovoProgressivoParametroModello(int idModello)
      throws DataAccessException;

  /**
   * Inserisce nella banca dati un parametro associato ad un modello
   * 
   * @param parametro
   *        parametro da inserire
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void insertParametro(ParametroModello parametro) throws DataAccessException;

  /**
   * Aggiorna nella banca dati il parametro associato ad un modello
   * 
   * @param parametro
   *        parametro da aggiornare
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void updateParametro(ParametroModello parametro) throws DataAccessException;

  /**
   * Elimina il parametro associato al modello
   * 
   * @param idModello
   *        identificativo univoco del modello
   * @param progressivo
   *        progressivo del parametro da eliminare
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void deleteParametro(int idModello, int progressivo)
      throws DataAccessException;

  /**
   * Elimina tutti i parametri associati al modello
   * 
   * @param idModello
   *        identificativo univoco del modello
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void deleteParametri(int idModello) throws DataAccessException;

  /**
   * } Aggiorna nella banca dati il progressivo di un parametro
   * 
   * @param idModello
   *        identificativo univoco del modello
   * @param progressivoOld
   *        progressivo attuale del parametro da variare
   * @param progressivoNew
   *        nuovo valore del progressivo del parametro da variare
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  boolean updateProgressivoParametro(int idModello, int progressivoOld,
      int progressivoNew) throws DataAccessException;

  /**
   * Aggiorna nella banca dati il progressivo di tutti i parametri con
   * progressivo maggiore o uguale al progressivo in input, decrementandolo di
   * una unità. Operazione necessaria per mantenere i progressivi adiacenti in
   * seguito ad una cancellazione di un elemento.
   * 
   * @param idModello
   *        identificativo univoco del modello
   * @param progressivoInizio
   *        valore minimo di progressivo dal quale cominciare l'aggiornamento
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void updateDecrementaProgressivoParametri(int idModello, int progressivoInizio)
      throws DataAccessException;

  /**
   * Aggiorna nella banca dati il progressivo di tutti i parametri con
   * progressivo nel range dei progressivi indicati, decrementandolo di una
   * unità. Operazione necessaria per mantenere i progressivi adiacenti in
   * seguito ad uno spostamento in posizione marcata di un elemento.
   * 
   * @param idModello
   *        identificativo univoco del modello
   * @param progressivoInizio
   *        valore minimo di progressivo dal quale cominciare l'aggiornamento
   * @param progressivoFine
   *        valore massimo di progressivo al quale terminare l'aggiornamento
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void updateDecrementaProgressivoParametri(int idModello,
      int progressivoInizio, int progressivoFine) throws DataAccessException;

  /**
   * Aggiorna nella banca dati il progressivo di tutti i parametri con
   * progressivo nel range dei progressivi indicati, incrementandolo di una
   * unità. Operazione necessaria per mantenere i progressivi adiacenti in
   * seguito ad uno spostamento in posizione marcata di un elemento.
   * 
   * @param idModello
   *        identificativo univoco del modello
   * @param progressivoInizio
   *        valore minimo di progressivo dal quale cominciare l'aggiornamento
   * @param progressivoFine
   *        valore massimo di progressivo al quale terminare l'aggiornamento
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void updateIncrementaProgressivoParametri(int idModello,
      int progressivoInizio, int progressivoFine) throws DataAccessException;

  /**
   * Inserisce nella banca dati (tabella W_COMPARAM) un parametro istanziato per
   * la corretta composizione di un modello.
   * 
   * @param parametro
   *        parametro da inserire
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void insertParametroComposizione(ParametroComposizione parametro)
      throws DataAccessException;

  /**
   * Elimina dalla banca dati tutti i parametri da W_COMPARAM definiti per la
   * sessione con l'identificativo in input
   * 
   * @param idSessione
   *        identificativo della sessione di composizione modello, per la quale
   *        vanno rimossi tutti i parametri
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void deleteParametriComposizione(int idSessione) throws DataAccessException;

  /**
   * Aggiorna il campo personale di un modello con il valore passato
   * 
   * @param idModello
   * @param i
   */
  void updatePersonale(int idModello, int i) throws DataAccessException;

  /**
   * Elimina le occorrenze nella W_CACHEMODPAR
   * 
   * @param idAccount
   *        identificativo univoco dell'account
   * @param idModello
   *        identificativo univoco del modello
   * @throws DataAccessException
   */
  void deleteCacheParametriComposizione(int idAccount, int idModello)
      throws DataAccessException;

  /**
   * Elimina tutte le occorrenze nella W_CACHEMODPAR di un utente
   * 
   * @param idAccount
   *        identificativo univoco dell'account
   * @throws DataAccessException
   */
  void deleteCacheParametriComposizioneUtente(Integer idAccount)
      throws DataAccessException;

  /**
   * Elimina tutte le occorrenze nella W_CACHEMODPAR di un modello
   * 
   * @param idModello
   *        identificativo univoco del modello
   * @throws DataAccessException
   */
  void deleteCacheParametriComposizioneModello(int idModello)
      throws DataAccessException;

  /**
   * Inserisce nella banca dati (tabella W_CACHEMODPAR) un parametro istanziato
   * per la corretta composizione di un modello nella tabella di cache per le
   * interazioni dell'utente.
   * 
   * @param cacheParametroComposizione
   *        parametro da inserire
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void insertCacheParametroComposizione(
      CacheParametroComposizione cacheParametroComposizione)
      throws DataAccessException;

  /**
   * Estrae il valore di un parametro per l'esecuzione da parte di un utente di
   * un modello
   * 
   * @param idAccount
   *        identificativo univoco dell'account
   * @param idModello
   *        identificativo univoco del modello
   * @param codice
   *        codice del parametro
   * @return valore del parametro
   * @throws DataAccessException
   */
  String getCacheParametroModello(int idAccount, int idModello, String codice)
      throws DataAccessException;
  
  /**
   * Estrae il numero di report con modello aventi come sorgente dati il report
   * in input
   * 
   * @param idRicerca
   *        identificativo univoco della ricerca per cui cercare se è sorgente
   *        dati di report con modello
   * @return numero di report con modello con sorgente il report in input
   * @throws DataAccessException
   */
  Integer getNumeroModelliCollegatiASorgenteReport(int idRicerca)
      throws DataAccessException;
}
