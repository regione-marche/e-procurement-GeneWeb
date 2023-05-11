/*
 * Created on 01-ott-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao;

import it.eldasoft.gene.db.domain.admin.Profilo;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;


/**
 * Interfaccia DAO per l'accesso ai dati della tabella PROFILI nel database di
 * configurazione dell'applicazione Web.
 * 
 * @author Luca.Giacomazzo
 */
public interface ProfiliDao {

  /** 
   * Metodo per estrarre la lista dei profili filtrati per codice applicazione
   * 
   * @param codApp codice applicazione
   * @return Ritorna la lista dei profili filtrati per codice applicazione
   */
  public List<?> getProfiliByCodApp(String codApp);
  
  /** 
   * Metodo per estrarre un profilo a partire del codice profilo
   * 
   * @param codiceProfilo codice del profilo da estrarre
   * @return Ritorna un profilo a partire dal codice del profilo
   */
  public Profilo getProfiloByPK(String codiceProfilo);

  /**
   * Metodo per estrarre la lista degli account associati al profilo, filtrando
   * per codice applicazione
   * 
   * @param codiceProfilo codice del profilo
   * @param codApp codice applicazione
   * @return Ritorna la lista degli account associati al profilo, filtrando
   *         per codice applicazione
   */
  public List<?> getAccountProfiloByCodApp(String codiceProfilo, String codApp);
  
  /**
   * Delete da W_ACCPRO di tutti degli utenti che erano associati ad un profilo
   * ed ora non lo sono più
   * 
   * @param codiceProfilo
   * @param codiceApplicazione codice applicazione
   * @param lista lista con idAccount degli utenti non piu' associati al profilo
   * @throws DataAccessException
   */
  public int deleteAccountNonAssociatiProfilo(String codiceProfilo, String
      codiceApplicazione, List<?> lista) throws DataAccessException;
  
  /**
   * Estrae la lista di IdAccount degli utenti associati ad un profilo
   * 
   * @param codiceProfilo
   * @return Ritorna la lista di idAccount degli utenti associati ad un gruppo
   * @throws DataAccessException
   */
  public Map<?,?> getUtentiAssociatiAProfiloAsMap(String codiceProfilo)
      throws DataAccessException;
  
  /**
   * Effettua l'insert in W_ACCPRO di una nuova associazione tra un utente ed
   * profilo
   * 
   * @param codiceProfilo
   * @param idAccount
   * @throws DataAccessException
   */
  public void insertAssociazioneAccountProfilo(String codiceProfilo,
      int idAccount) throws DataAccessException;

  /**
   * Metodo per estrarre la lista dei profili a cui un utente e' associato,
   * filtrando per codice applicazione
   * 
   * @param idAccount id account 
   * @param codApp codice applicazione. Se il codice applicazione e' costituito
   *        da piu' codici applicazione separati da ';', allora tale stringa 
   *        viene splittata per estrarre tutti i profili a cui l'utente e'
   *        associato per ciascun codice applicativo
   * @return Ritorna la lista dei profili a cui un utente e' associato,
   *         filtrando per codice applicazione
   */
  public List<?> getProfiliUtenteByCodApp(int idAccount, String codApp);
  
  /**
   * Delete da W_ACCPRO di tutti i profili che erano associati ad un utente ed
   * ora non lo sono più
   * 
   * @param idAccount idAccount a cui eliminare l'associazione con i profili
   *        indicati
   * @param listaCodiceProfili lista con idAccount degli utenti non piu'
   *        associati al profilo
   * @param codiceApplicazione codice applicazione
   * @throws DataAccessException
   */
  public int deleteProfiliNonAssociatiUtente(int idAccount, List<?>
      listaCodiceProfili, String codiceApplicazione) throws DataAccessException;

  /**
   * Funzione per la cancellazione dell'associazione account-profili, uisata 
   * nella cancellazione di un account.
   * @param idAccount
   * @throws DataAccessException
   */
  void deleteAccountConAssociazioneProfili(Integer idAccount) throws DataAccessException;

  /**
   * Metodo per estrarre la lista dei gruppi associati al profilo, filtrando
   * per codice applicazione
   * 
   * @param codiceProfilo codice del profilo
   * @param codApp codice applicazione
   * @return Ritorna la lista dei gruppi associati al profilo, filtrando
   *         per codice applicazione
   */
  public List<?> getGruppiProfiloByCodApp(String codiceProfilo, String codApp);
  
}