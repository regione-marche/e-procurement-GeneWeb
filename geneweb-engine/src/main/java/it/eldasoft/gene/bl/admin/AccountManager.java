/*
 * Created on 28-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.admin;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.dao.AccountDao;
import it.eldasoft.gene.db.dao.ModelliDao;
import it.eldasoft.gene.db.dao.ProfiliDao;
import it.eldasoft.gene.db.dao.RicercheDao;
import it.eldasoft.gene.db.dao.UffintDao;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.domain.admin.AccountCodFiscDuplicati;
import it.eldasoft.gene.db.domain.admin.AccountGruppo;
import it.eldasoft.gene.db.domain.admin.GruppoConProfiloAccount;
import it.eldasoft.gene.db.domain.admin.TrovaAccount;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.sql.comp.SqlComposerException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

/**
 * Manager che si occupa di gestire tutte le operazioni di business logic sulla
 * parte di gestione di un utente, le relative proprieta' e l'associazione o
 * meno ad un gruppo
 *
 * @author Luca.Giacomazzo
 */

public class AccountManager {

  private GenChiaviManager genChiaviManager;
  private AccountDao       accountDao;
  private ProfiliDao       profiliDao;
  private RicercheDao      ricercheDao;
  private ModelliDao       modelliDao;
  private UffintDao        uffintDao;


  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(AccountManager.class);

  /**
   * @param accountDao
   *        accountDao da settare internamente alla classe.
   */
  public void setAccountDao(AccountDao accountDao) {
    this.accountDao = accountDao;
  }

  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  public void setProfiliDao(ProfiliDao profiliDao) {
    this.profiliDao = profiliDao;
  }

  /**
   * @param modelliDao
   *        modelliDao da settare internamente alla classe.
   */
  public void setModelliDao(ModelliDao modelliDao) {
    this.modelliDao = modelliDao;
  }

  /**
   * @param ricercheDao
   *        ricercheDao da settare internamente alla classe.
   */
  public void setRicercheDao(RicercheDao ricercheDao) {
    this.ricercheDao = ricercheDao;
  }

  /**
   * @param uffintDao uffintDao da settare internamente alla classe.
   */
  public void setUffintDao(UffintDao uffintDao) {
    this.uffintDao = uffintDao;
  }

  /**
   * @return Ritorna la lista degli utenti/account associati al gruppo in
   *         analisi, filtrandoli per codice profilo attivo
   * @throws CriptazioneException
   */
  public List<AccountGruppo> getAccountDiGruppo(int idGruppo, String codiceProfilo)
      throws CriptazioneException {
    List<AccountGruppo> risultatoDao = this.accountDao.getUtentiDiGruppo(idGruppo,
        codiceProfilo);
    return risultatoDao;
  }

  /**
   * @return Ritorna la lista di tutti utentiDigruppo, con l'attributo
   *         'associato' popolato
   * @throws CriptazioneException
   */
  public List<Account> getAccountConAssociazioneGruppo(int idGruppo, String codiceProfilo)
      throws CriptazioneException {
    // Lista di tutti gli utenti
    List<Account> listaUtenti = this.accountDao.getAllUtenti(codiceProfilo);

    // Lista degli utenti associati al gruppo in analisi, filtrati per codice
    // profilo attivo
    List<AccountGruppo> listaUtentiGruppo = this.accountDao.getUtentiDiGruppo(idGruppo,
        codiceProfilo);

    // Osservazione: entrambe le liste appena estratte sono ordinate per nome (e
    // non per idAccount) e la lunghezza della lista degli utenti di gruppo è
    // minore o uguale alla lista degli utenti.
    ListIterator<AccountGruppo> iterUtentiGruppo = listaUtentiGruppo.listIterator();
    ListIterator<Account> iterUtenti = listaUtenti.listIterator();
    int idUtenteGruppo = 0;
    AccountGruppo utente = null;
    AccountGruppo utenteGruppo = null;
    Set<Integer> setIdUtentiAssociati = new HashSet<Integer>();

    while (iterUtentiGruppo.hasNext()) {
      utenteGruppo = iterUtentiGruppo.next();
      idUtenteGruppo = utenteGruppo.getIdAccount();
      setIdUtentiAssociati.add(new Integer(idUtenteGruppo));
    }

    while (iterUtenti.hasNext()) {
      utente = (AccountGruppo) iterUtenti.next();

      if (setIdUtentiAssociati.contains(new Integer(utente.getIdAccount()))) {
        utente.setAssociato(true);
      } else {
        utente.setAssociato(false);
      }
    }

    return listaUtenti;
  }

  public void updateAssociazioneAccountGruppo(int idGruppo,
      String[] idAccountAssociati, String codiceProfilo) {

    List<Integer> listaUtentiAssociati = new ArrayList<Integer>();

    if (idAccountAssociati != null) {
      // popolamento delle due liste appena create, facendo il parsing delle
      // stringa appena letta dal request
      for (int i = 0; i < idAccountAssociati.length; i++) {
        listaUtentiAssociati.add(new Integer(idAccountAssociati[i]));
      }

      // Delete degli utenti non associati al gruppo in analisi
      this.accountDao.deleteAccountNonAssociati(idGruppo, listaUtentiAssociati);

      if (!listaUtentiAssociati.isEmpty()) {
        // select degli utenti già associati al gruppo in analisi
        List<AccountGruppo> listaUtentiPreAssociati = this.accountDao.getUtentiDiGruppo(
            idGruppo, codiceProfilo);

        Iterator<AccountGruppo> iterPreAssociati = listaUtentiPreAssociati.iterator();
        // Creo un HashMap contenente gli elementi della lista estratta con
        // chiave l'idGruppo di ciascun gruppo estratto
        HashMap<Integer, AccountGruppo> mappaUtentiPreAssociati = new HashMap<Integer, AccountGruppo>();
        AccountGruppo accountGruppo = null;
        while (iterPreAssociati.hasNext()) {
          accountGruppo = iterPreAssociati.next();
          mappaUtentiPreAssociati.put(
              new Integer(accountGruppo.getIdAccount()), accountGruppo);
        }

        Integer idAccount = null;
        Iterator<Integer> iter = listaUtentiAssociati.iterator();
        // insert degli utenti da associare al gruppo in analisi,
        if (!listaUtentiPreAssociati.isEmpty()) {
          // Esistono utenti già associati al gruppo in analisi, perciò devo
          // controllare che l'idAccount
          // degli utenti da inserire (contenuti in listautentiAss) siano
          // presenti o meno nella
          // tabella W_ACCGRP
          while (iter.hasNext()) {
            idAccount = iter.next();
            if (!mappaUtentiPreAssociati.containsKey(idAccount)) {
              this.accountDao.insertAssociazioneAccountGruppo(idGruppo,
                  idAccount.intValue());
            }
          }
        } else {
          // Non esistono utenti pre associati al gruppo in analisi e posso
          // quindi inserire
          // gli utenti presenti nella lista 'listaUtentiAssociati' senza alcun
          // controllo
          while (iter.hasNext()) {
            idAccount = iter.next();
            this.accountDao.insertAssociazioneAccountGruppo(idGruppo,
                idAccount.intValue());
          }
        }
      }
    } else {
      this.accountDao.deleteAccountNonAssociati(idGruppo, listaUtentiAssociati);
    }
  }

  // F.D. 18/10/2006 aggiunta funzioni di inserimento e selezione record da
  // usrsys
  /**
   * Funzione che inserisce un record nella Usrsys. Prende il Max(SYSCON)+1 e lo
   * utilizza come chiave. Vengono criptati la Login e la Password tramite il
   * sistema di criptazione standard legacy
   *
   * @throws CriptazioneException
   */
  public void insertAccount(Account account) throws CriptazioneException {
    if (logger.isDebugEnabled()) logger.debug("insertAccount: inizio metodo");
    // estraggo il max(syscon) from usrsys
    int id = this.genChiaviManager.getMaxId("USRSYS", "SYSCON") + 1;

    account.setIdAccount(id);
    // criptazione Login prima del salvataggio dell'account
    ICriptazioneByte criptatore = FactoryCriptazioneByte.getInstance(
        ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
        account.getLogin().getBytes(),
        ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
    //Sabbadin 15/07/2015: si allinea per retrocompatibilita' il valore criptato
    account.setLoginCriptata(new String(criptatore.getDatoCifrato()));
    // criptazione Password prima del salvataggio dell'account
    if ((account.getPassword() == null) || "".equals(account.getPassword())) {
      account.setPassword(null);
    } else {
      criptatore = FactoryCriptazioneByte.getInstance(
          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          account.getPassword().getBytes(),
          ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
      account.setPassword(new String(criptatore.getDatoCifrato()));
    }
    // inserisco l'occorrenza di usrsys in questione
    this.accountDao.insertAccount(account);

    if (logger.isDebugEnabled()) logger.debug("insertAccount: fine metodo");
  }

  /**
   * @param trovaAccount
   *        Dati di filtro della maschera di trova utenti
   * @return Lista degli utenti che soddisfano i criteri di ricerca in input
   * @throws SqlComposerException
   * @throws CriptazioneException
   */
  public List<Account> getListaAccount(TrovaAccount trovaAccount)
      throws SqlComposerException, CriptazioneException {
    logger.debug("getAccount: inizio metodo");

    // estrazione degli utenti
    List<Account> listaAccount = this.accountDao.getAccount(trovaAccount);

    logger.debug("getAccount: fine metodo");

    return listaAccount;
  }

  /**
   * @return Ritorna la lista di tutti gli account
   */
  public List<Account> getListaAccount() throws CriptazioneException {
    logger.debug("getAccount: inizio metodo");

    List<Account> listaAccount = this.accountDao.getAccount();

    logger.debug("getAccount: fine metodo");

    return listaAccount;
  }

  public List<Account> getListaAccountByCodProCodApp(String codApp, String codPro)
      throws CriptazioneException {
    logger.debug("getAccount: inizio metodo");

    List<Account> listaAccount = this.accountDao.getAccountByCodAppCodPro(codApp, codPro);

    logger.debug("getAccount: fine metodo");

    return listaAccount;

  }

  public List<Account> getListaAccountByCodProfili(String[] codProfili) throws CriptazioneException {
    logger.debug("getListaAccountByCodProfili: inizio metodo");

    List<Account> listaAccount = this.accountDao.getListaAccountByCodProfili(codProfili);

    logger.debug("getListaAccountByCodProfili: fine metodo");

    return listaAccount;

  }

  /**
   * modifica un account
   *
   * @param Account
   *        account da modificare
   */
  public void updateAccount(Account account) throws CriptazioneException {
    if (logger.isDebugEnabled()) logger.debug("updateAccount: inizio metodo");

    // criptazione Login prima del salvataggio dell'account
    ICriptazioneByte criptatore = FactoryCriptazioneByte.getInstance(
        ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
        account.getLogin().getBytes(),
        ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
    //Sabbadin 15/07/2015: si allinea per retrocompatibilita' il valore criptato
    account.setLoginCriptata(new String(criptatore.getDatoCifrato()));

    // modifico l'occorrenza di usrsys in questione
    this.accountDao.updateAccount(account);

    if (logger.isDebugEnabled()) logger.debug("updateAccount: fine metodo");
  }

  /**
   * @return Ritorna la lista di tutti account
   */
  public Account getAccountByLogin(String login) throws SqlComposerException {
    logger.debug("getAccountByLogin: fine metodo");
    Account account = this.accountDao.getAccountByLogin(login);
    logger.debug("getAccountByLogin: fine metodo");
    return account;
  }

  /**
   * @return Ritorna la lista di tutti account
   */
  public List<Account> getAccountByPassword(String password) {
    logger.debug("getAccountByPassword: fine metodo");
    List<Account> lista = this.accountDao.getAccountByPassword(password);
    logger.debug("getAccountByPassword: fine metodo");
    return lista;
  }

  /**
   * Ritorna il dettaglio di un account, a meno della password.
   *
   * @param id
   *        chiave primaria account
   * @return dettaglio account
   */
  public Account getAccountById(Integer id) throws CriptazioneException {

    logger.debug("getAccountById: inizio metodo");

    Account utente = this.accountDao.getAccountById(id);

    logger.debug("getAccountById: fine metodo");

    return utente;

  }

  /**
   * Elimina un account
   *
   * @param id
   *        id dell'account
   */
  public void deleteAccount(Integer id) {
    if (logger.isDebugEnabled()) logger.debug("deleteAccount: inizio metodo");
    deleteAccountCommon(id);
    // eliminazione delle associazioni con gli uffici intestatari
    this.uffintDao.deleteUfficiAccount(id);
    if (logger.isDebugEnabled()) logger.debug("deleteAccount: fine metodo");
  }

  /**
   * Elimina un account in seguito alla delete di un ufficio intestatario
   *
   * @param id
   *        id dell'account
   */
  public void deleteAccountFromDeleteUffint(Integer id) {
    if (logger.isDebugEnabled()) logger.debug("deleteAccountFromDeleteUffint: inizio metodo");
    deleteAccountCommon(id);
    if (logger.isDebugEnabled()) logger.debug("deleteAccountFromDeleteUffint: fine metodo");
  }

  /**
   *
   * @param id
   */
  private void deleteAccountCommon(Integer id) {
    // elimino l'occorrenza di usrsys in questione
    this.accountDao.deleteAccount(id);
    // elimino l'occorrenza di associazione con i gruppi
    this.accountDao.deleteAccountConAssociazioneGruppo(id);
    // elimino le occorrenze di associazione con i profili
    this.profiliDao.deleteAccountConAssociazioneProfili(id);
    // elimino le occorrenze di associazione con i permessi
    this.accountDao.deletePermessiAccount(id);
    // elimino la storia dell'account
    this.accountDao.deleteStoriaAccount(id);
    // eliminazione della cache dei parametri ricerche per l'utente
    this.ricercheDao.deleteCacheParametriEsecuzioneUtente(id);
    // eliminazione della cache dei parametri modello per l'utente
    this.modelliDao.deleteCacheParametriComposizioneUtente(id);
    //disassociazione dei tecnici
    this.accountDao.deleteAssociazioneTecniciAccount(id);
    // elimino le occorrenze legate all'account e contenenti credenziali verso sistemi esterni
    this.accountDao.deleteCredenzialiServiziEsterniAccount(id);
  }

  /**
   * Aggiorna la password di un account
   *
   * @param idAccount
   *        id dell'account a cui aggiornare la password
   *
   * @param vecchiaPassword
   *        vecchio valore della password per l'account
   * @param nuovaPassword
   *        nuovo valore della password
   * @throws CriptazioneException
   */
  public void updatePassword(int idAccount, String vecchiaPassword,
      String nuovaPassword) throws CriptazioneException {

    // SS 19/06/2007: gestisce anche il caso di password null

    String vecchiaPasswordCriptata = null;
    if (vecchiaPassword != null && vecchiaPassword.length() > 0) {
      ICriptazioneByte criptatoreOld = FactoryCriptazioneByte.getInstance(
          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          vecchiaPassword.getBytes(), ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
      vecchiaPasswordCriptata = new String(criptatoreOld.getDatoCifrato());
    }

    String nuovaPasswordCriptata = null;
    if (nuovaPassword != null && nuovaPassword.length() > 0) {
      ICriptazioneByte criptatoreNew = FactoryCriptazioneByte.getInstance(
          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          nuovaPassword.getBytes(), ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
      nuovaPasswordCriptata = new String(criptatoreNew.getDatoCifrato());
    }

    this.accountDao.updatePassword(idAccount, vecchiaPasswordCriptata,
        nuovaPasswordCriptata);
  }

  /**
   * Aggiorna la password di un account
   *
   * @param idAccount
   *        id dell'account a cui aggiornare la password
   *
   * @param nuovaPassword
   *        nuovo valore della password
   * @throws CriptazioneException
   */
  public void updatePasswordSenzaVecchia(int idAccount, String nuovaPassword)
      throws CriptazioneException {

    // SS 19/06/2007: gestisce anche il caso di password null

    String nuovaPasswordCriptata = null;
    if (nuovaPassword != null && nuovaPassword.length() > 0) {
      ICriptazioneByte criptatoreNew = FactoryCriptazioneByte.getInstance(
          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          nuovaPassword.getBytes(), ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
      nuovaPasswordCriptata = new String(criptatoreNew.getDatoCifrato());
    }

    this.accountDao.updatePasswordSenzaVecchia(idAccount, nuovaPasswordCriptata);
  }

  /**
   * Funzione che controlla se la login è già utilizzata
   *
   * @return true se la login è già utilizzata false se la logn non è utilizzata
   *
   * @param login
   *        login da controllare
   * @param id
   *        id dell'account
   * @throws SqlComposerException
   */
  public boolean isUsedLogin(String login, int id) throws CriptazioneException, SqlComposerException {
    if (logger.isDebugEnabled()) logger.debug("isUsedLogin: inizio metodo");

    boolean isUsed = false;
    Account account = this.getAccountByLogin(login);
    if (account != null && account.getIdAccount() != id) isUsed = true;

    if (logger.isDebugEnabled()) logger.debug("isUsedLogin: fine metodo");
    return isUsed;
  }

  public boolean isUsedDn(String dn, int id) {
    if (logger.isDebugEnabled()) logger.debug("isUsedDn: inizio metodo");

    boolean isUsed = this.accountDao.isUsedDn(dn, id);

    if (logger.isDebugEnabled()) logger.debug("isUsedDn: fine metodo");
    return isUsed;
  }

  /**
   * Ritorna l'elenco di tutti i gruppi, filtrati per codice applicazione, a cui
   * e' associato l'account.
   *
   * @param idAccount
   *        id dell'account
   * @param codiceProfilo
   *        codice del profilo attivo
   * @return Ritorna la lista degli idGruppo dei gruppi associati all'utente in
   *         analisi, filtrando per codice profilo attivo
   */
  public List<GruppoConProfiloAccount> getListaGruppiAccount(int idAccount, String codApp) {
    return this.accountDao.getGruppiAccount(idAccount, codApp);
  }

  /**
   * Metodo per estrarre la lista degli idGruppo dei gruppi, filtrati per codice
   * profilo attivo, associati all'utente.
   */
  public List<Integer> getListaGruppiAccountByCodAppCodPro(int idAccount, String codApp,
      String codProfilo) {
    // Questo metodo viene richiamato dalla action SetProfilo, per salvare nel
    // oggetto ProfiloUtente, presente in sessione, la lista degli idGruppo di
    // ciascun gruppo a cui l'utente può accedere
    return this.accountDao.getGruppiAccountByCodAppCodPro(idAccount, codApp,
        codProfilo);
  }

  /**
   * Ritorna la lista dei gruppi di un account con anche la priorità
   *
   * @param idAccount
   * @return
   *
   * public List getListaGruppiAccountWithPriorita(int idAccount) { return
   * this.accountDao.getGruppiAccountWithPriorita(idAccount); }
   */

  /**
   * Aggiorna le associazioni fra l'account e i gruppi
   *
   * @param idAccount
   * @param idGruppiAssociati
   */
  public void updateAssociazioneGruppoAccount(int idAccount,
      String[] idGruppiAssociati, String codApp, String codiceProfilo) {

    List<Integer> listaGruppiAssociati = new ArrayList<Integer>();

    if (idGruppiAssociati != null) {
      for (int i = 0; i < idGruppiAssociati.length; i++)
        listaGruppiAssociati.add(new Integer(idGruppiAssociati[i]));

      this.accountDao.deleteGruppiNonAssociatiAccountDaDettaglioAccount(
          idAccount, codApp, listaGruppiAssociati);
    }

    if (!listaGruppiAssociati.isEmpty()) {
      List<GruppoConProfiloAccount> listaGruppiPreAssociati = this.accountDao.getGruppiAccount(
          idAccount, codApp);
      Iterator<GruppoConProfiloAccount> iterLista = listaGruppiPreAssociati.iterator();

      Map<Integer, GruppoConProfiloAccount> mappaGruppiPreAssociati = new HashMap<Integer, GruppoConProfiloAccount>();
      GruppoConProfiloAccount gruppoAccount = null;
      while (iterLista.hasNext()) {
        gruppoAccount = iterLista.next();
        mappaGruppiPreAssociati.put(new Integer(gruppoAccount.getIdGruppo()),
            gruppoAccount);
      }

      Iterator<Integer> iter = listaGruppiAssociati.iterator();
      Integer idGruppo = null;

      // insert dei gruppi da associare all'utente in analisi,
      if (!listaGruppiAssociati.isEmpty()) {
        while (iter.hasNext()) {
          idGruppo = iter.next();
          if (!mappaGruppiPreAssociati.containsKey(idGruppo)) {
            this.accountDao.insertAssociazioneAccountGruppo(
                idGruppo.intValue(), idAccount);
          }
        }
      } else {
        // Non esistono gruppi pre associati all'utente in analisi e posso
        // quindi inserire i gruppi presenti nella lista 'listaGruppiAssociati'
        // senza alcun controllo
        while (iter.hasNext()) {
          idGruppo = iter.next();
          this.accountDao.insertAssociazioneAccountGruppo(idGruppo.intValue(),
              idAccount);
        }
      }
    } else {
      // In questo caso non si deve associare alcun gruppo all'account in
      // analisi
      // e si devono rimuovere le associazioni esistenti
      this.accountDao.deleteGruppiNonAssociatiAccountDaDettaglioAccount(
          idAccount, codApp, null);
    }
  }

  /**
   * @return Ritorna la lista di tutti gruppi, filtrati per codice applicazione,
   *         associati ai profili ai quali può accedere l'account in analisi. La
   *         lista presenta anche l'attributo 'associato' popolato
   * @throws CriptazioneException
   */
  public List<GruppoConProfiloAccount> getGruppiConAssociazioneAccount(int idAccount, String codApp,
      String codiceProfilo) {
    // Lista di tutti i gruppi (con il nome del profilo padre) a cui l'utente
    // può essere associato, cioe' i gruppi figli dei profili a cui l'utente
    // può accedere
    List<GruppoConProfiloAccount> listaGruppi = this.accountDao.getGruppiConProfiloByCodApp(idAccount,
        codApp);

    // Lista dei gruppi (con il nome del profilo padre) associati all'account
    // in analisi
    List<GruppoConProfiloAccount> listaGruppiAccount = this.accountDao.getGruppiAccount(idAccount,
        codApp);

    List<GruppoConProfiloAccount> listaResult = new ArrayList<GruppoConProfiloAccount>();

    ListIterator<GruppoConProfiloAccount> iterGruppiAccount = listaGruppiAccount.listIterator();
    ListIterator<GruppoConProfiloAccount> iterGruppi = listaGruppi.listIterator();

    GruppoConProfiloAccount gruppo = null;
    GruppoConProfiloAccount gruppoAccount = null;
    HashMap<Integer, GruppoConProfiloAccount> mapGruppiAccount = new HashMap<Integer, GruppoConProfiloAccount>();

    while (iterGruppiAccount.hasNext()) {
      gruppoAccount = iterGruppiAccount.next();
      mapGruppiAccount.put(new Integer(gruppoAccount.getIdGruppo()),
          gruppoAccount);
    }
    int i = 0;
    while (iterGruppi.hasNext()) {
      gruppo = iterGruppi.next();

      if (mapGruppiAccount.containsKey(new Integer(gruppo.getIdGruppo())))
        gruppo.setAssociato(true);
      else
        gruppo.setAssociato(false);

      listaResult.add(gruppo);
      i++;
    }
    return listaResult;
  }

  /**
   * Funzione che modifica la password dell'utente e inserisce un record nella
   * Stoutesys. Vengono criptati la Login e la Password tramite il sistema di
   * criptazione standard legacy
   *
   * @throws CriptazioneException
   */
  public void updatePasswordInsertStorico(int idAccount, String login,
      String vecchiaPassword, String nuovaPassword) throws CriptazioneException {

    // SS 19/06/2007: gestisce anche il caso di password null

    ICriptazioneByte criptatore = FactoryCriptazioneByte.getInstance(
        ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
        login.getBytes(), ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
    // Sabbadin 15/07/2015: si aggiorna la password criptata per retrocompatibilita'
    String loginCriptata = new String(criptatore.getDatoCifrato());

    String vecchiaPasswordCriptata = null;
    if (vecchiaPassword != null && vecchiaPassword.length() > 0) {
      ICriptazioneByte criptatoreOld = FactoryCriptazioneByte.getInstance(
          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          vecchiaPassword.getBytes(), ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
      vecchiaPasswordCriptata = new String(criptatoreOld.getDatoCifrato());
    }

    String nuovaPasswordCriptata = null;
    if (nuovaPassword != null && nuovaPassword.length() > 0) {
      ICriptazioneByte criptatoreNew = FactoryCriptazioneByte.getInstance(
          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          nuovaPassword.getBytes(), ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
      nuovaPasswordCriptata = new String(criptatoreNew.getDatoCifrato());
    }

    // inserisco l'occorrenza di usrsys in questione
    this.accountDao.insertStoriaAccount(idAccount, login, loginCriptata,
        nuovaPasswordCriptata, new Date());

    this.accountDao.updatePassword(idAccount, vecchiaPasswordCriptata,
        nuovaPasswordCriptata);
  }

  /**
   * Funzione che modifica la password dell'utente senza bisogno della vecchia
   * password e inserisce un record nella Stoutesys. Vengono criptati la Login e
   * la Password tramite il sistema di criptazione standard legacy
   *
   * @throws CriptazioneException
   */
  public void updatePasswordSenzaVecchiaInsertStorico(int idAccount,
      String login, String nuovaPassword) throws CriptazioneException {

    // SS 19/06/2007: gestisce anche il caso di password null

    String nuovaPasswordCriptata = null;
    if (nuovaPassword != null && nuovaPassword.length() > 0) {
      ICriptazioneByte criptatoreNew = FactoryCriptazioneByte.getInstance(
          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          nuovaPassword.getBytes(), ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
      nuovaPasswordCriptata = new String(criptatoreNew.getDatoCifrato());
    }

    ICriptazioneByte criptatore = FactoryCriptazioneByte.getInstance(
        ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
        login.getBytes(), ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
    // Sabbadin 15/07/2015: si aggiorna la password criptata per retrocompatibilita'
    String loginCriptata = new String(criptatore.getDatoCifrato());

    // inserisco l'occorrenza di usrsys in questione
    this.accountDao.insertStoriaAccount(idAccount, login, loginCriptata,
        nuovaPasswordCriptata, new Date());

    this.accountDao.updatePasswordSenzaVecchia(idAccount, nuovaPasswordCriptata);
  }

  /**
   * Metodo che inserisce un'occorrenza nella storia dell'account (STOUTESYS)
   *
   * @param idAccount
   * @param login
   * @param loginCriptata
   * @param passwordCriptata
   */
  public void insertStoriaAccount(int idAccount, String login, String loginCriptata,
      String passwordCriptata) {

    // inserisco l'occorrenza di usrsys in questione
    this.accountDao.insertStoriaAccount(idAccount, login, loginCriptata,
        passwordCriptata, new Date());

  }

  public void deleteStoriaAccount(Integer id) {
    if (logger.isDebugEnabled())
      logger.debug("deleteStoriaAccount: inizio metodo");
    // modifico l'occorrenza di usrsys in questione
    this.accountDao.deleteStoriaAccount(id);

    if (logger.isDebugEnabled())
      logger.debug("deleteStoriaAccount: fine metodo");
  }

  /**
   * Funzione che restituisce la data da cui è attiva la password in oggetto
   *
   * @param login
   * @param password
   * @return
   * @throws CriptazioneException
   */
  public Date getDataUltimoCambioPsw(String login, String password)
      throws CriptazioneException {
    if (logger.isDebugEnabled())
      logger.debug("getDataUltimoCambioPsw: inizio metodo");

    if (password != null && password.length() > 0) {
      ICriptazioneByte criptatore = FactoryCriptazioneByte.getInstance(
          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          password.getBytes(), ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
      password = new String(criptatore.getDatoCifrato());
    }
    Date dataUltimoCambio = this.accountDao.getDataUltimoCambioPsw(login,
        password);

    if (logger.isDebugEnabled())
      logger.debug("getDataUltimoCambioPsw: fine metodo");
    return dataUltimoCambio;
  }

  public void updateAbilitazioneUtente(int idAccount, String utenteAbilitato)
      throws DataAccessException {
    this.accountDao.updateAbilitazioneUtente(idAccount, utenteAbilitato);
  }

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
  public int getNumeroAssociazioniProfili(int idAccount, String codApp) {
    return this.accountDao.getNumeroAssociazioniProfili(idAccount, codApp);
  }



  /**
   * Estrae la lista di utenti con codice fiscale uguale a quello indicato
   *
   * @param idAccount
   *        chiave primaria dell'account
   * @param codfisc
   *        codice fiscale per il quale ricercare gli utenti
   * @return elenco di utenti
   *
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public List<AccountCodFiscDuplicati> getListaUtentiUgualeCodfisc(int idAccount, String codfisc){
    return this.accountDao.getListaUtentiUgualeCodfisc(idAccount,codfisc);
  }

  /**
   * Sincronizza i campi login e login in chiaro, sia per la tabella utenti che per lo storico password.
   *
   * @throws CriptazioneException
   */
  public void updateLogins() throws CriptazioneException {
    logger.debug("updateLogins: inizio metodo");
    List<Account> accountNoLogin = this.accountDao.getListaAccountLoginNull();
    for (Account account : accountNoLogin) {
      //aggiornare campo login dalla login criptata
      ICriptazioneByte criptatore = FactoryCriptazioneByte.getInstance(
          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          account.getLoginCriptata().getBytes(),
          ICriptazioneByte.FORMATO_DATO_CIFRATO);
      account.setLogin(new String(criptatore.getDatoNonCifrato()));
      this.accountDao.updateAccount(account);
    }
    List<Account> storiaAccountNoLogin = this.accountDao.getListaStoriaAccountLoginNull();
    for (Account account : storiaAccountNoLogin) {
      //aggiornare campo login dalla login criptata
      ICriptazioneByte criptatore = FactoryCriptazioneByte.getInstance(
          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          account.getLoginCriptata().getBytes(),
          ICriptazioneByte.FORMATO_DATO_CIFRATO);
      account.setLogin(new String(criptatore.getDatoNonCifrato()));
      this.accountDao.updateStoricoPassword(account);
    }
    List<Account> accountNoLoginCriptata = this.accountDao.getListaAccountLoginCriptataNull();
    for (Account account : accountNoLoginCriptata) {
      //aggiornare campo login criptata dalla login in chiaro
      ICriptazioneByte criptatore = FactoryCriptazioneByte.getInstance(
          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          account.getLogin().getBytes(),
          ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
      //Sabbadin 15/07/2015: si allinea per retrocompatibilita il valore criptato
      account.setLoginCriptata(new String(criptatore.getDatoCifrato()));
      this.accountDao.updateAccount(account);
    }
    logger.debug("updateLogins: fine metodo");
  }
  public Timestamp getUltimoCambioPassword(int id){
    return this.accountDao.getUltimoCambioPassword(id);
  }

  /**
   * Estrae la lista di utenti con codice fiscale uguale a quello indicato
   *
   * @param account
   *        account dell'utente del quale si vuole registrare la cancellazione nella tabella USRCANC, quindi utente amministratore
   */
  public void insertCancellazioneUtente(Account account){
    if (logger.isDebugEnabled()) logger.debug("insertCancellazioneUtente: inizio metodo");
    int id = this.genChiaviManager.getNextId("USRCANC");
    int syscon = account.getIdAccount();
    String syslogin = account.getLogin();
    this.accountDao.insertCancellazioneUtente(id, syscon, syslogin, new Date());
    if (logger.isDebugEnabled()) logger.debug("insertCancellazioneUtente: fine metodo");
  }

  /**
   * Verifica l'utilizzo delle credenziali in input negli ultimi 6 mesi.
   *
   * @param login
   *        login inserito da l'utente, da controllare se negli ultimi 6 mesi è già esistito utente amministatore con stessa login
   * @return elenco di utenti
   *        true se ha il permesso di usare la login inserita, false altrimenti
   */
  public boolean getCredenziaDisponibili(String login){
    Date today = new Date();
    Date dataCancellazione = this.accountDao.getDataCancellazione(login);
    if(dataCancellazione == null){
      return true;
    }
    dataCancellazione = DateUtils.addMonths(dataCancellazione, 6);
    if(today.after(dataCancellazione)){
      return true;
    }else{
      return false;}
  }


  public boolean getPasswordDisallineata(Account account){
    int id = account.getIdAccount();
    String pwd = this.accountDao.getPasswordDisallineataStorico(id);
    if(pwd == null){
      return true;
    }
    return false;
  }

  /**
   * Aggiorna l'informazione ultimo accesso per l'account con la data/ora attuale.
   *
   * @param idAccount id utente da aggiornare
   */
  public void updateUltimoAccesso(int idAccount) {
    Date dataUltimoAccesso = new Date();
    this.accountDao.updateUltimoAccesso(idAccount, dataUltimoAccesso);
  }

  /**
   * Ritorna il numero di login fallite successive all'ultima login effettuata con successo e tracciate nel sistema per l'utente in input.
   *
   * @param username
   *        username da verificare
   * @return numero di login falliti
   */
  public int getNumeroLoginFallite(String username) {
    return this.accountDao.getNumeroLoginFallite(username);
  }

  /**
   * Ritorna l'ultimo tentativo di login fallito successive all'ultima login effettuata con successo e tracciata nel sistema per l'utente in input.
   *
   * @param username
   *        username da verificare
   * @return data ultima login fallita, null se l'ultimo login è andato a buon fine
   */
  public Date getUltimaLoginFallita(String username) {
    return this.accountDao.getUltimaLoginFallita(username);
  }

  /**
   * Inserisce nella tabella della tracciatura accessi falliti il tentativo di login fallito.
   *
   * @param username
   *        utilizzato in fase di autenticazione
   * @param ipAddress
   *        indirizzo IP di provenienza della richiesta
   */
  public void insertLoginFallita(String username, String ipAddress) {
    if (logger.isDebugEnabled()) logger.debug("insertLoginFallita: inizio metodo");

    int id = this.genChiaviManager.getNextId("G_LOGINKO");
    this.accountDao.insertLoginFallita(id, username, new Date(), ipAddress);
    if (logger.isDebugEnabled()) logger.debug("insertLoginFallita: fine metodo");
  }

  /**
   * Rimuove tutte le occorrenze relative a login fallite per un utente.
   *
   * @param username
   *        utente per cui rimuovere le login fallite
   */
  public void deleteLoginFallite(String username) {
    if (logger.isDebugEnabled()) logger.debug("deleteLoginFallite: inizio metodo");
    this.accountDao.deleteLoginFallite(username);
    if (logger.isDebugEnabled()) logger.debug("deleteLoginFallite: fine metodo");
  }

}