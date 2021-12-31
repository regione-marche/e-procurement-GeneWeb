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
package it.eldasoft.gene.bl;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.domain.ProfiloUtente;
import it.eldasoft.gene.db.dao.AccountDao;
import it.eldasoft.gene.db.dao.KronosDao;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityNumeri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

/**
 * Manager che si occupa di gestire tutte le operazioni di business logic sulla
 * parte di gestione degli account
 *
 * @author Stefano.Sabbadin
 *
 */
public class LoginManager {

  /** Reference al DAO per l'accesso alla tabella ACCOUNT */
  private AccountDao accountDao;

  /** Reference al DAO per l'accesso alle tabelle di KRONOS. */
  private KronosDao kronosDao;

  /**
   * @return Ritorna accountDao.
   */
  public AccountDao getAccountDao() {
    return accountDao;
  }

  /**
   * @param accountDao
   *        accountDao da settare internamente alla classe.
   */
  public void setAccountDao(AccountDao accountDao) {
    this.accountDao = accountDao;
  }

  /**
   * @param kronosDao kronosDao da settare internamente alla classe.
   */
  public void setKronosDao(KronosDao kronosDao) {
    this.kronosDao = kronosDao;
  }

  /**
   * Estrae un utente a partire dalla sua login
   *
   * @param login
   *        login dell'utente
   * @return account associato all'utente, oppure null
   * @throws CriptazioneException
   * @throws SqlComposerException
   * @throws DataAccessException
   */
  public Account getAccountByLogin(String login) throws CriptazioneException, DataAccessException, SqlComposerException {

    // F.D. 04/09/08 PB5858: la login diventa NON case sensitive
    // in base al login passato carichiamo la lista completa degli utenti
    // decriptiamo tutti i login e ricerchiamo il dato, una volta ottenuto il
    // syscon carichiamo l'account direttamente

    // Sabbadin 17/07/2015: con l'introduzione del campo SYSLOGIN (login in chiaro) si semplifica l'algoritmo in modo da estrarre l'utenza
    // per login e poi si confronta la password
    Account account = this.accountDao.getAccountByLogin(login);

    if (account != null) {
      if (account.getPassword() != null) {
        ICriptazioneByte decriptatorePsw = FactoryCriptazioneByte.getInstance(
            ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
            account.getPassword().getBytes(),
            ICriptazioneByte.FORMATO_DATO_CIFRATO);
        account.setPassword(new String(decriptatorePsw.getDatoNonCifrato()));
      }
    }

    return account;
  }

  /**
   * Estrae un utente in base alla login e alla password (per la login
   * il controllo &egrave; NON case sensitive).
   *
   * @param login
   * @param password
   * @return account associato all'utente, oppure null
   * @throws CriptazioneException
   * @throws SqlComposerException
   * @throws DataAccessException
   */
  public Account getAccountByLoginEPassword(String login, String password)
      throws CriptazioneException, DataAccessException, SqlComposerException {
    // la prima scrematura viene fatta in base alla password, si ricercano gli
    // utenti con la password corrispondente a quella inserita e da questa lista
    // si cerca la login corrispondente in modo da estrarre l'utente. Si ripete
    // la ricerca della login per avere la certezza che non ci siano più
    // elementi con login e password uguale visto che con il vecchio sistema la
    // login risultava diversa visto che il controllo veniva fatto
    // case-sensitive

    // Sabbadin 17/07/2015: in seguito all'introduzione del campo SYSLOGIN contenente la login in chiaro si semplifica la ricerca filtrando
    // per login e verificando la password
    Account account = this.getAccountByLogin(login);
    if (account != null && account.getFlagLdap().intValue() != 1) {
      if ((account.getPassword() != null && !account.getPassword().equals(password))
          || (account.getPassword() == null && password != null)){
        account = null;
      }
    }

    return account;
  }

  /**
   * Estrae dall'oggetto account le informazioni per costruire il profilo
   * dell'utente da inserire in sessione
   *
   * @param login
   *        login dell'utente
   * @param codiceApplicazione
   *        codice dell'applicazione
   * @return dati utente da inserire in sessione
   * @throws CriptazioneException
   */
  public ProfiloUtente getProfiloUtente(Account account,
      String codiceApplicazione) throws CriptazioneException {
    ProfiloUtente utente = new ProfiloUtente();

    utente.setId(account.getIdAccount());
    utente.setNome(account.getNome());

//    ICriptazioneByte decriptatore = FactoryCriptazioneByte.getInstance(
//        ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
//        account.getLogin().getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
//    utente.setLogin(new String(decriptatore.getDatoNonCifrato()));
    utente.setLogin(account.getLogin());

    // LG: 17/10/2006: aggiunta delle funzioniUtenteAbilitate, caricando da
    // USRSYS lettura dalla tabella USRSYS dei campi SYSABILAP, SYSPWBOU e
    // SYSPRI ed inserimento in una lista di tutte le voci inserite in tali
    // campi dopo averne fatto il parsing
    List<String> listaFunzioniUtente = new ArrayList<String>();
    this.parsingFunzioniUtente(listaFunzioniUtente, account);
    utente.setFunzioniUtenteAbilitate(listaFunzioniUtente.toArray(new String[0]));

    // L.G. 03/04/2007: modifica per caricare nel profilo utente i campi della
    // USRSYS denominati SYSAB3, SYSABG, SYSABC e SYSLIV, SYSLIG, SYSLIC per
    // poter applicare le condizioni di filtro per livello utente
    utente.setOpzioniUtente(account.getOpzioniUtente());
    utente.setAbilitazioneStd(account.getAbilitazioneStd());
    utente.setAbilitazioneContratti(account.getAbilitazioneContratti());
    utente.setAbilitazioneGare(account.getAbilitazioneGare());
    utente.setLivelloContratti(UtilityNumeri.convertiIntero(account.getLivelloContratti()));
    utente.setLivelloStd(UtilityNumeri.convertiIntero(account.getLivelloStd()));
    utente.setLivelloGare(UtilityNumeri.convertiIntero(account.getLivelloGare()));
    utente.setUtenteLdap(account.getFlagLdap());
    // L.G.: - fine modifica -
    //C.F. 26/11/2009 carico anche l'uff.app.
    utente.setUfficioAppartenenza(UtilityNumeri.convertiIntero(account.getUfficioAppartenenza()));
    utente.setRuoloUtenteMercatoElettronico(UtilityNumeri.convertiIntero(account.getRuoloUtenteMercatoElettronico()));
    if (account.getOpzioniPrivilegi() != null)
    	utente.setOpzioniPrivilegi(account.getOpzioniPrivilegi().split("\\|"));
    else
    	utente.setOpzioniPrivilegi(new String[]{});

    if (account.getEmail() != null) {
      utente.setMail(account.getEmail());
    }
    if (account.getCodfisc() != null) {
      utente.setCodiceFiscale(account.getCodfisc());
    }
    if (account.getAbilitazioneAP() != null) {
      utente.setAbilitazioneAP(account.getAbilitazioneAP());
    }
    return utente;
  }

  private void parsingFunzioniUtente(List<String> listaFunzioniUtente, Account account) {
    // Parsing del campo SYSABILAP
    String str = account.getOpzioniApplicazione();
    if (str != null) {
      String[] array = str.split("\\|");
      for (int i = 0; i < array.length; i++) {
        listaFunzioniUtente.add(array[i]);
      }
    }

    // Parsing del campo SYSPRI
    str = account.getOpzioniPrivilegi();
    // Non uso la costante 'CostantiGenerali.SEPARATORE_OPZIONI_LICENZIATE'
    // perche' essa fa riferimento al carattere di token usato nel file di
    // properties gene_sprotetto.properties
    if (str != null) {
      String[] array = str.split("\\|");
      for (int i = 0; i < array.length; i++) {
        listaFunzioniUtente.add(array[i]);
      }
    }
    // Parsing del campo SYSPWBOU
    str = account.getOpzioniUtente();
    // Non uso la costante 'CostantiGenerali.SEPARATORE_OPZIONI_LICENZIATE'
    // perche' essa fa riferimento al carattere di token usato nel file di
    // properties gene_sprotetto.properties
    if (str != null) {
      String[] array = str.split("\\|");
      for (int i = 0; i < array.length; i++) {
        listaFunzioniUtente.add(array[i]);
      }
    }
  }

  /**
   * Estrae un utente a partire dal suo id
   *
   * @param id
   *        pk dell'utente
   * @return account associato all'utente, oppure null
   * @throws CriptazioneException
   */
  public Account getAccountById(Integer id) throws CriptazioneException {
    Account account = this.accountDao.getAccountById(id);

    if (account != null) {
//      ICriptazioneByte decriptatore = FactoryCriptazioneByte.getInstance(
//          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
//          account.getLogin().getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
//      account.setLogin(new String(decriptatore.getDatoNonCifrato()));

      if (account.getPassword() != null) {
        ICriptazioneByte decriptatorePsw = FactoryCriptazioneByte.getInstance(
            ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
            account.getPassword().getBytes(),
            ICriptazioneByte.FORMATO_DATO_CIFRATO);
        account.setPassword(new String(decriptatorePsw.getDatoNonCifrato()));
      }
    }

    return account;
  }

  /**
   * Estrae i dati dell'utente che richiede l'accesso al sistema di reportistica per KRONOS.
   *
   * @param id
   *        id della richiesta di accesso attribuita all'utente
   * @return hash contenente i parametri inviati dal portale al sistema di reportistica
   */
  public Map<String, String> getDatiUtenteKronos(Integer id) {
    Map<String, String> hash = new HashMap<String, String>();
    String[] datiUtente = null;
    String datiUtenteConcatenati = this.kronosDao.getDatiUtente(id);
    if (datiUtenteConcatenati != null) {
      datiUtente = datiUtenteConcatenati.split("\\|");
      for (int i = 0; i < datiUtente.length; i++) {
        int pos = datiUtente[i].indexOf('=');
        hash.put(datiUtente[i].substring(0, pos), datiUtente[i].substring(pos + 1));
      }
    }
    return hash;
  }

}
