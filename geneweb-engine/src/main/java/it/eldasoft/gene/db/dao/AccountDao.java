/*
 * Created on 13-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao;

import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.domain.admin.AccountCodFiscDuplicati;
import it.eldasoft.gene.db.domain.admin.AccountGruppo;
import it.eldasoft.gene.db.domain.admin.GruppoAccount;
import it.eldasoft.gene.db.domain.admin.GruppoConProfiloAccount;
import it.eldasoft.gene.db.domain.admin.TrovaAccount;
import it.eldasoft.utils.sql.comp.SqlComposerException;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Interfaccia DAO per l'accesso ai dati della tabella ACCOUNT nel database di
 * configurazione dell'applicazione Web.
 *
 * @author Stefano.Sabbadin
 */
public interface AccountDao {

  /**
   * Estrae un account a partire dalla sua login
   *
   * @param login
   *        login dell'account
   * @return oggetto {@link it.eldasoft.gene.db.domain.Account} individuato
   *         dalla login in input
   *
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  Account getAccountByLogin(String login) throws DataAccessException, SqlComposerException;

  List<Account> getAccountByPassword(String password) throws DataAccessException;

  /**
   * Estrae l'elenco dei gruppi associati all'utente, filtrati per codice
   * applicativo
   *
   * @param idAccount
   *        chiave primaria dell'account
   * @param codApp
   *        codice applicazione
   * @return lista dei gruppi associati all'utente filtrati per codice
   *         applicativo
   *
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<GruppoConProfiloAccount> getGruppiAccount(int idAccount, String codApp)
      throws DataAccessException;

  /**
   * Metodo per estrarre tutti i gruppi figli dei profili a cui l'utente in
   * analisi puo' accedere. La lista estrae anche il nome del profilo padre di
   * ciascun gruppo
   *
   * @param idAccount
   *        id dell'utente in analisi
   * @param codApp
   *        codice applicazione
   * @return Ritorna la lista di tutti i gruppi figli dei profili a cui l'utente
   *         puo' accedere, con il nome del profilo padre di ciascun gruppo
   * @throws DataAccessException
   */
  List<GruppoConProfiloAccount> getGruppiConProfiloByCodApp(int idAccount, String codApp)
      throws DataAccessException;

  /**
   * Estrae l'elenco di codici dei gruppi associati all'utente, filtrati per
   * codice applicativo e per codice profilo
   *
   * @param idAccount
   *        chiave primaria dell'account
   * @param codApp
   *        codice applicazione
   * @param codProfilo
   *        codice profilo attivo
   * @return lista dei gruppi associati all'utente filtrati per codice
   *         applicativo
   *
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */

  List<Integer> getGruppiAccountByCodAppCodPro(int idAccount, String codApp,
      String codProfilo) throws DataAccessException;

  /**
   * Estrae l'elenco dei gruppi dell'account con la rispettiva priorita
   *
   * @param idAccount
   * @return
   * @throws DataAccessException
   */
  // List getGruppiAccountWithPriorita(int idAccount) throws
  // DataAccessException;
  /**
   * Aggiorna la password per l'account in input, individuato dalla sua chiave
   * primaria e per sicurezza anche dalla vecchia password
   *
   * @param idAccount
   *        chiave primaria dell'account
   * @param vecchiaPassword
   *        vecchio valore assunto dalla password (è una sicurezza in più che
   *        chi va a richiedere la modifica è l'utente stesso)
   * @param nuovaPassword
   *        nuovo valore assunto dalla password
   *
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void updatePassword(int idAccount, String vecchiaPassword,
      String nuovaPassword) throws DataAccessException;

  /**
   * Estrae da USRSYS e W_GRUPPI gli utenti associati al gruppo in analisi
   *
   * @param idGruppo
   * @param codiceProfilo
   *        codice profilo attivo
   * @return Ritorna la lista degli utenti associati al gruppo in analisi
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<AccountGruppo> getUtentiDiGruppo(int idGruppo, String codiceProfilo)
      throws DataAccessException;

  /**
   * Estrae da USRSYS tutti gli utenti
   *
   * @param codiceProfilo
   *        codice del profilo attivo
   * @return Ritorna la lista degli utenti
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<Account> getAllUtenti(String codiceProfilo) throws DataAccessException;

  /**
   * Effettua l'insert in W_ACCGRP di una nuova associazione tra un utente ed
   * gruppo
   *
   * @param idGruppo
   * @param idAccount
   * @param priorita
   * @throws DataAccessException
   */
  public void insertAssociazioneAccountGruppo(int idGruppo, int idAccount)
      throws DataAccessException;

  /**
   * Delete da W_ACCGRP di tutti degli utenti che erano associati ad un gruppo
   * ed ora non lo sono più
   *
   * @param idGruppo
   * @param lista
   * @throws DataAccessException
   */
  public int deleteAccountNonAssociati(int idGruppo, List<Integer> lista)
      throws DataAccessException;

  // F.D. 18/10/2006 aggiunta funzioni di inserimento e selezione record da
  // usrsys
  /**
   * Inserisce un Account
   *
   * @param account
   *        account completo
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void insertAccount(Account account) throws DataAccessException;

  /**
   * Estrae la lista di tutti gli account presi da USRSYS
   *
   * @return Ritorna la lista degli utenti
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<Account> getAccount() throws DataAccessException;

  /**
   * Estrae la lista degli account presi da USRSYS che soddisfano i criteri di
   * ricerca in input
   *
   * @param trovaAccount
   *        contenitore con i filtri da applicare per la ricerca degli account
   * @return Ritorna la lista degli utenti
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<Account> getAccount(TrovaAccount trovaAccount) throws DataAccessException, SqlComposerException;

  /**
   * Estrae la lista degli account presi da USRSYS, filtrati per codice
   * applicazione e per codice profilo
   *
   * @param codApp
   *        codice applicazione
   * @param codiceProfilo
   *        codice del profilo attivo
   * @return Ritorna la lista degli utenti
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<Account> getAccountByCodAppCodPro(String codApp, String codiceProfilo)
      throws DataAccessException;

  /**
   * Estrae la lista degli account presi da USRSYS, filtrati per codice profilo (basta un profilo collegato
   * all'utente e si estrae l'utente)
   *
   * @param codProfili
   *        codici dei profili di collegamento agli utenti
   * @return elenco utenti associati all'applicativo
   */
  List<Account> getListaAccountByCodProfili(String[] codProfili);

  // F.D. 19/10/2006 aggiunta funzione di modifica delle opzioni di un account
  /**
   * Modifica un Account
   *
   * @param account
   *        account completo
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void updateAccount(Account account) throws DataAccessException;

  /**
   * Estrae un account a partire dalla sua login
   *
   * @param id
   *        id dell'account
   * @return oggetto {@link it.eldasoft.gene.db.domain.Account} individuato
   *         dalla login in input
   *
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  Account getAccountById(Integer id) throws DataAccessException;

  /**
   * Elimina un Account
   *
   * @param id
   *        id dell'account
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void deleteAccount(Integer id) throws DataAccessException;

  /**
   * Controlla se la login inserita è già presente nel DB
   *
   * @param login
   *        login dell'account
   * @param id
   *        id dell'account
   * @return boolean se esiste un Account con la stessa login
   *
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  boolean isUsedLogin(String login, int id) throws DataAccessException;

  /**
   * Controlla se il dn è già presente nel DB
   *
   * @param dn
   * @param id
   * @return
   * @throws DataAccessException
   */
  boolean isUsedDn(String dn, int id) throws DataAccessException;

  /**
   * Controlla se la coppia login/password è già presente nel DB
   *
   * @param login
   *        login dell'account
   *
   * @param password
   *        password dell'account
   *
   * @param id
   *        id dell'account
   *
   * @return boolean se esiste un Account con la stessa login e la stessa
   *         password
   *
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  boolean isUsedLoginPassword(String login, String password, int id)
      throws DataAccessException;

  /**
   * Elimina l'account dalla tabella di associazione con i gruppi
   *
   * @param id
   *        id dell'account
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void deleteAccountConAssociazioneGruppo(Integer id)
      throws DataAccessException;

  /**
   * Elimina i gruppi fitrati per codice profilo, non associati all'account che
   * non esistono nella lista
   *
   * @param idAccount
   * @param codiceApplicazione
   * @param codiceProfilo
   * @param lista
   * @return
   * @throws DataAccessException
   */
  int deleteGruppiNonAssociatiProfilo(int idAccount, String codiceApplicazione,
      String codiceProfilo, List<Integer> lista) throws DataAccessException;

  /**
   * Elimina i gruppi fitrati per codice applicazione, non associati all'account
   * che non esistono nella lista
   *
   * @param idAccount
   * @param codiceApplicazione
   * @param lista
   * @return
   * @throws DataAccessException
   */
  int deleteGruppiNonAssociatiAccountDaDettaglioAccount(int idAccount,
      String codiceApplicazione, List<Integer> lista) throws DataAccessException;

  /**
   * Elimina i gruppi fitrati per codice applicazione, non associati all'account
   * che non esistono nella lista
   *
   * @param idAccount
   * @param codiceApplicazione
   * @param lista
   * @return
   * @throws DataAccessException
   */
  int deleteGruppiNonAssociatiAccountDaDettaglioProfilo(int idAccount,
      String codiceApplicazione, String codiceProfilo, List<Integer> lista)
      throws DataAccessException;

  /**
   * restituisce un list con i gruppi associati all'account
   *
   * @param idAccount
   * @param codiceProfilo
   *        codice del profilo attivo
   * @return
   * @throws DataAccessException
   */
  List<GruppoAccount> getGruppiAssociatiAccountasList(int idAccount, String codiceProfilo)
      throws DataAccessException;

  /**
   * modifica la password senza avere a disposizione quella vecchia (opzione per
   * l'Admin)
   *
   * @param idAccount
   * @param nuovaPassword
   * @throws DataAccessException
   */
  void updatePasswordSenzaVecchia(int idAccount, String nuovaPassword)
      throws DataAccessException;

  /**
   * Funzione che inserisce un record nella STOUTESYS per mantenere la storia
   * del cambio di login e password
   *
   * @param idAccount
   * @param login
   * @param loginCriptata
   * @param password
   * @param dataInserimento
   * @throws DataAccessException
   */
  void insertStoriaAccount(int idAccount, String login, String loginCriptata, String password,
      Date dataInserimento) throws DataAccessException;

  /**
   * Funzione che controlla se la login e la password sono state utilizzate già
   * dall'utente in passato
   *
   * @param login
   * @param password
   * @param id
   * @return
   * @throws DataAccessException
   */
  /*
   * boolean isUsedStoriaLoginPassword(String login,String password, int id)
   * throws DataAccessException;
   */

  /**
   * Funzione che elimina la storia di un utente a partire dal suo id (SYSCON)
   *
   * @param id
   * @throws DataAccessException
   */
  void deleteStoriaAccount(Integer id) throws DataAccessException;

  /**
   * Funzione che legge la data da cui è attiva la password corrente
   *
   * @param login
   * @param password
   * @return
   * @throws DataAccessException
   */
  Date getDataUltimoCambioPsw(String login, String password)
      throws DataAccessException;

  /**
   * Funzione per la cancellazione dei permessi associati all'account. Questa
   * funzione viene usata nella transazione di cancellazione di un account.
   *
   * @param idAccount
   * @throws DataAccessException
   */
  void deletePermessiAccount(Integer idAccount) throws DataAccessException;

  /**
   * Funzione che cambia lo stato di abilitazione dell'utente
   *
   * @param idAccount
   * @param utenteAbilitato
   * @throws DataAccessException
   */
  void updateAbilitazioneUtente(int idAccount, String utenteAbilitato)
      throws DataAccessException;

  /**
   * Estrae il numero di associazioni a profili per l'utente
   *
   * @param idAccount
   *        chiave primaria dell'account
   * @param codApp
   *        codice applicazione per il quale ricercare i profili
   * @return numero di associazioni dell'utente a profili appartenenti al codice
   *         applicazione
   *
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  int getNumeroAssociazioniProfili(int idAccount, String codApp)
      throws DataAccessException;

  /**
   * Funzione che elimina l'associazione dei tecnici all'account
   *
   * @param id
   *        id dell'account
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void deleteAssociazioneTecniciAccount(Integer id) throws DataAccessException;

  /**
   * Estrae l'elenco di utenti col medesimo codice fiscale
   *
   * @param idAccount
   *        chiave primaria dell'account
   * @param codfisc
   *        codice fiscale di cui si vuole cercare la duplicazione
   * @return elenco di utenti col medesimo codice fiscale
   *
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<AccountCodFiscDuplicati> getListaUtentiUgualeCodfisc(int idAccount, String codfisc)
      throws DataAccessException;

  /**
   * Estrae la lista degli utenti che necessitano dell'allineamento del campo SYSLOGIN in quanto nullo.
   *
   * @return lista utenti per cui ricalcolare il campo SYSLOGIN partendo da SYSNOM
   */
  List<Account> getListaAccountLoginNull();

  /**
   * Estrae dalla tabella dello storico password le occorrenze per cui il campo SYSLOGIN non risulta valorizzato.
   * @return lista record dello storico modifiche da allineare nel campo SYSLOGIN
   */
  List<Account> getListaAccountLoginCriptataNull();

  /**
   * Estrae la lista degli utenti che necessitano dell'allineamento del campo SYSNOM in quanto nullo. Questa estrazione di fatto non
   * dovrebbe mai estrarre nulla, ma per sicurezza viene eseguita.
   *
   * @return lista utenti per cui ricalcolare il campo SYSNOM partendo da SYSLOGIN
   */
  List<Account> getListaStoriaAccountLoginNull();

  /**
   * Aggiorna l'occorrenza in STOUTESYS allineando il campo SYSLOGIN (login in chiaro) al corrispondente valore criptato presente nella
   * chiave.
   *
   * @param account
   *        dati da utilizzare per l'aggiornamento (si usano solo login, password e login criptata)
   */
  void updateStoricoPassword(Account account);

  /**
   * Elimina le occorrenze da WSLOGIN contenenti credenziali memorizzate per l'utente in input.
   *
   * @param id
   *        id dell'account
   */
  void deleteCredenzialiServiziEsterniAccount(Integer id);

  /**
   * Ottieni la lista degli Id degli account che posseggono le opzioni ou11 o ou89
   */
  List<String> getAccountGestoriProfilo();

  /**
   * Ottieni la data dell'ultimo cambio di password dell'utente con id  richiesto.
   * disponibile solo per utenti con flag password sicura TRUE
   */
  Timestamp getUltimoCambioPassword(Integer id);

  /**
   * Inserisce nella tabella USRCANC i dati dell'account elimiinato
   */
  void insertCancellazioneUtente( Integer id,Integer syscon, String syslogin, Date date);

  /**
   * ritorna la data di cancellazione ultimo utente se presente nella tabella usrcanc,
   * se è presente più di un record viene ritornato il più recente
   */
  Date getDataCancellazione(String login);

  String getPasswordDisallineataStorico(Integer id);

  /**
   * Aggiorna l'informazione ultimo accesso per l'account con la data/ora attuale.
   *
   * @param idAccount id utente da aggiornare
   * @param dataUltimoAccesso ultimo accesso
   */
  void updateUltimoAccesso(int idAccount, Date dataUltimoAccesso);

  /**
   * Ritorna il numero di login fallite successive all'ultima login effettuata con successo e tracciate nel sistema per l'utente in input.
   *
   * @param username
   *        username da verificare
   * @return numero di login falliti
   */
  int getNumeroLoginFallite(String username);

  /**
   * Ritorna l'ultimo tentativo di login fallito successive all'ultima login effettuata con successo e tracciata nel sistema per l'utente in input.
   *
   * @param username
   *        username da verificare
   * @return data ultima login fallita, null se l'ultimo login è andato a buon fine
   */
  Date getUltimaLoginFallita(String username);

  /**
   * Inserisce nella tabella G_LOGINKO il fallito tentativo di autenticazione.
   * @param id chiave primaria del record da inserire
   * @param username username utilizzato in fase di login
   * @param loginTime data/ora del tentativo di login fallito
   * @param ipAddress indirizzo ip
   */
  void insertLoginFallita(int id, String username, Date loginTime, String ipAddress);

  /**
   * Rimuove tutte le occorrenze relative a login fallite per un utente.
   *
   * @param username
   *        utente per cui rimuovere le login fallite
   */
  void deleteLoginFallite(String username);

}