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
package it.eldasoft.gene.bl.admin;

import it.eldasoft.gene.db.dao.AccountDao;
import it.eldasoft.gene.db.dao.ProfiliDao;
import it.eldasoft.gene.db.domain.admin.Account;
import it.eldasoft.gene.db.domain.admin.AccountProfilo;
import it.eldasoft.gene.db.domain.admin.Gruppo;
import it.eldasoft.gene.db.domain.admin.Profilo;
import it.eldasoft.gene.db.domain.admin.ProfiloAccount;
import it.eldasoft.utils.sicurezza.CriptazioneException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Manager che si occupa di gestire tutte le operazioni di business logic sulla
 * parte di gestione di un profilo, le relative proprieta' e l'associazione o
 * meno ad un account
 *
 * @author Luca.Giacomazzo
 */
public class ProfiliManager {

  private AccountDao       accountDao;
  private ProfiliDao       profiliDao;

  /** Logger Log4J di classe */
  static Logger logger = Logger.getLogger(ProfiliManager.class);

  /**
   * @param accountDao
   *        accountDao da settare internamente alla classe.
   */
  public void setAccountDao(AccountDao accountDao) {
    this.accountDao = accountDao;
  }

  /**
   * @param profiliDao
   *        profiliDao da settare internamente alla classe.
   */
  public void setProfiliDao(ProfiliDao profiliDao) {
    this.profiliDao = profiliDao;
  }

  public List<?> getListaProfiliByCodApp(String codApp){
    return this.profiliDao.getProfiliByCodApp(codApp);
  }

  public Profilo getProfiloByCodProfilo(String codiceProfilo){
    return this.profiliDao.getProfiloByPK(codiceProfilo);
  }

  /**
   * Metodo per l'estrazione della lista degli account associati al profilo,
   * filtrato per codice applicazione
   * @return Ritorna la lista di tutti gli account associati al profilo, filtrato
   *         per codice applicazione
   * @throws CriptazioneException
   */
  public List<?> getUtentiProfiloByCodApp(String codProfilo, String codApp) {
   return this.profiliDao.getAccountProfiloByCodApp(codProfilo, codApp);
  }

  /**
   * Metodo per l'estrazione della lista degli account associati al profilo,
   * filtrato per codice applicazione, con l'attributo 'associato' popolato
   *
   * @param codiceProfilo
   * @param codApp
   * @return Ritorna la lista degli account associati al profilo, con
   * l'attributo 'associato' popolato, filtrato per codice applicazione
   */
  public List<AccountProfilo> getUtentiConAssociazioneProfiloByCodApp(String codiceProfilo,
      String codApp) throws CriptazioneException {

    // Lista di tutti gli account
    List<Account> listaAccount = this.accountDao.getAccount();

    // Trasforma la listaAccount (contenente oggetti di tipo Account) in
    // una lista di oggetti di tipo UtenteProfilo
    List<AccountProfilo> listaUtenti = new ArrayList<AccountProfilo>();
    for(int i=0; i < listaAccount.size(); i++){
      listaUtenti.add(new AccountProfilo((Account) listaAccount.get(i)));
    }
    // Lista account associati al profilo, filtrati per codApp
    List<?> listaUtentiProfilo = this.profiliDao.getAccountProfiloByCodApp(
        codiceProfilo, codApp);

    List<AccountProfilo> listaResult = new ArrayList<AccountProfilo>();

    ListIterator<?> iterUtentiProfilo = listaUtentiProfilo.listIterator();
    ListIterator<?> iterUtenti = listaUtenti.listIterator();

    AccountProfilo utente = null;
    AccountProfilo utenteProfilo = null;
    HashMap<Integer, AccountProfilo> mapUtentiProfilo = new HashMap<Integer, AccountProfilo>();

    while (iterUtentiProfilo.hasNext()) {
      utenteProfilo = (AccountProfilo) iterUtentiProfilo.next();
      //gruppoAccount = (GruppoAccount) iterUtentiProfilo.next();
      mapUtentiProfilo.put(new Integer(utenteProfilo.getIdAccount()), utenteProfilo);
    }
    int i = 0;
    while (iterUtenti.hasNext()) {
      utente = (AccountProfilo) iterUtenti.next();
      utenteProfilo = (AccountProfilo) mapUtentiProfilo.get(new Integer(
                utente.getIdAccount()));

      if(utenteProfilo != null)
        utente.setAssociato(true);
      else
        utente.setAssociato(false);

    //Sabbadin 15/07/2015: con l'introduzione di SYSLOGIN non serve piu'
//      ICriptazioneByte decriptatore = FactoryCriptazioneByte.getInstance(
//          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
//          utente.getLogin().getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
//      utente.setLogin(new String(decriptatore.getDatoNonCifrato()));

      listaResult.add(utente);
      i++;
    }
    return listaResult;
  }

  /**
   * Estrazione della lista dei profili a cui un utente e' associato, filtrando
   * per codice applicazione
   *
   * @param idAccount
   * @param codApp codice applicazione.  Se il codice applicazione e' costituito
   *        da piu' codici applicazione separati da ';', allora tale stringa
   *        viene splittata per estrarre tutti i profili a cui l'utente e'
   *        associato per ciascun codice applicativo
   * @return Ritorna la lista dei profili a cui un utente e' associato, filtrando
   *         sempre per codice applicazione
   */
  public List<?> getProfiliAccountByCodApp(int idAccount, String codApp){
    return this.profiliDao.getProfiliUtenteByCodApp(idAccount, codApp);
  }

  /**
   * Metodo per aggiornare l'associazione utenti-profilo
   *
   * @param codProfilo codice profilo
   * @param codiceApplicazione codice applicazione
   * @param idAccountAssociati array degli idAccount da associare al profilo
   * @param gruppiDisabilitati
   */
  public void updateAssociazioneAccountProfilo(String codProfilo,
      String codiceApplicazione, String[] idAccountAssociati,
      boolean gruppiDisabilitati) {

    List<Integer> listaUtentiDaAssociare = new ArrayList<Integer>();
    Set<Integer> mappaUtentiDaAssociare = new HashSet<Integer>();
    if(idAccountAssociati != null && idAccountAssociati.length > 0) {
      // popolamento delle due liste appena create, facendo il parsing delle
      // stringa appena letta dal request
      for (int i = 0; i < idAccountAssociati.length; i++) {
        listaUtentiDaAssociare.add(new Integer(idAccountAssociati[i]));
        mappaUtentiDaAssociare.add(new Integer(idAccountAssociati[i]));
      }

      // Mappa degli utenti associati al profilo secondo la base dati
      Map<?,?> mappaUtentiAssociatiDaDB =
            this.profiliDao.getUtentiAssociatiAProfiloAsMap(codProfilo);

      Set<Integer> insiemeChiavi = (Set<Integer>) mappaUtentiAssociatiDaDB.keySet();
      Iterator<Integer> iterChiavi =  insiemeChiavi.iterator();
      while(iterChiavi.hasNext()){
        Integer idAccountTmp = (Integer) iterChiavi.next();
        if(!mappaUtentiDaAssociare.contains(idAccountTmp)){
          //delete associazione gruppi-utente dell'utente da cancellare
          this.accountDao.deleteGruppiNonAssociatiAccountDaDettaglioProfilo(
              idAccountTmp.intValue(), codiceApplicazione, codProfilo, null);
        }
      }

      // Delete degli utenti non associati al profilo in analisi
      this.profiliDao.deleteAccountNonAssociatiProfilo(codProfilo,
          codiceApplicazione, listaUtentiDaAssociare);

      if(!listaUtentiDaAssociare.isEmpty()) {
        // select degli utenti già associati al profilo in analisi
        Map<?,?> listaUtentiPreAssociati =
              this.profiliDao.getUtentiAssociatiAProfiloAsMap(codProfilo);

        Iterator<?> iter = listaUtentiDaAssociare.iterator();
        Integer idAccount = null;

        // insert degli utenti da associare al gruppo in analisi,
        if (!listaUtentiPreAssociati.isEmpty()) {
          // Esistono utenti già associati al profilo in analisi, perciò devo
          // controllare che l'idAccount degli utenti da inserire (contenuti
          // in listautentiAss) siano presenti o meno nella tabella W_ACCPRO
          while (iter.hasNext()) {
            idAccount = (Integer) iter.next();
            if (!listaUtentiPreAssociati.containsKey(idAccount)) {
              this.profiliDao.insertAssociazioneAccountProfilo(codProfilo,
                  idAccount.intValue());
              if(gruppiDisabilitati){
                // Estraggo il gruppo di default del profilo in analisi
                List<?> listaGruppiProfilo = this.profiliDao.getGruppiProfiloByCodApp(
                    codProfilo, codiceApplicazione);
                int idGruppoDefault = ((Gruppo) listaGruppiProfilo.get(0)).getIdGruppo();
                this.accountDao.insertAssociazioneAccountGruppo(idGruppoDefault,
                    idAccount.intValue());
              }
            }
          }
        } else {
          // Non esistono utenti pre associati al profilo in analisi e posso
          // quindi inserire gli utenti presenti nella lista
          // 'listaUtentiAssociati' senza alcun controllo
          while (iter.hasNext()) {
            idAccount = (Integer) iter.next();
            this.profiliDao.insertAssociazioneAccountProfilo(codProfilo,
                idAccount.intValue());
            if(gruppiDisabilitati){
              // Estraggo il gruppo di default del profilo in analisi
              List<?> listaGruppiProfilo = this.profiliDao.getGruppiProfiloByCodApp(
                  codProfilo, codiceApplicazione);
              int idGruppoDefault = ((Gruppo) listaGruppiProfilo.get(0)).getIdGruppo();
              this.accountDao.insertAssociazioneAccountGruppo(idGruppoDefault,
                  idAccount.intValue());
            }
          }
        }
      }
    } else {
      // Mappa degli utenti associati al profilo secondo la base dati
      Map<?,?> mappaUtentiAssociatiDaDB =
            this.profiliDao.getUtentiAssociatiAProfiloAsMap(codProfilo);

      Set<Integer> insiemeChiavi = (Set<Integer>) mappaUtentiAssociatiDaDB.keySet();
      Iterator<?> iterChiavi = insiemeChiavi.iterator();
      while(iterChiavi.hasNext()){
        Integer idAccountTmp = (Integer) iterChiavi.next();
        //delete associazione gruppi-utente dell'utente da cancellare
        this.accountDao.deleteGruppiNonAssociatiAccountDaDettaglioProfilo(idAccountTmp.intValue(),
            codiceApplicazione, codProfilo, null);
      }
      this.profiliDao.deleteAccountNonAssociatiProfilo(codProfilo,
          codiceApplicazione, listaUtentiDaAssociare);
    }
  }

  /**
   * Metodo per l'estrazione della lista dei profili associati all'utente in,
   * analisi, filtrato per codice applicazione, con l'attributo 'associato'
   * popolato
   *
   * @param idAccount
   * @param codApp
   * @return Ritorna la lista dei profili associati all'utente in analisi, con
   * l'attributo 'associato' popolato, filtrato per codice applicazione
   */
  public List<ProfiloAccount> getProfiliConAssociazioneUtenteByCodApp(int idAccount, String codApp){

    List<ProfiloAccount> listaResult = new ArrayList<ProfiloAccount>();

    // Lista di tutti i profili filtrati per codice applicazione
    List<?> listaProfili = this.profiliDao.getProfiliByCodApp(codApp);

    // Trasforma la listaProfili (contenente oggetti di tipo Profilo) in
    // una lista di oggetti di tipo ProfiloAccount
    List<ProfiloAccount> listaProfiliAssociati = new ArrayList<ProfiloAccount>();
    for(int i=0; i < listaProfili.size(); i++){
      listaProfiliAssociati.add(new ProfiloAccount((Profilo) listaProfili.get(i)));
    }
    // Lista profili a cui un utente e' associato, filtrati per codApp
    List<?> listaProfiliUtente = this.profiliDao.getProfiliUtenteByCodApp(
        idAccount, codApp);

    ListIterator<?> iterProfili = listaProfiliUtente.listIterator();
    ListIterator<?> iterProfiliUtente = listaProfiliAssociati.listIterator();

    Profilo profilo = null;
    ProfiloAccount profiloUtente = null;
    HashMap<String, Profilo> mapProfiliUtente = new HashMap<String, Profilo>();

    while (iterProfili.hasNext()) {
      profilo = (Profilo) iterProfili.next();
      mapProfiliUtente.put(profilo.getCodiceProfilo(), profilo);
    }
    int i = 0;
    while (iterProfiliUtente.hasNext()) {
      profiloUtente = (ProfiloAccount) iterProfiliUtente.next();
      profilo = (Profilo) mapProfiliUtente.get(profiloUtente.getCodiceProfilo());

      if(profilo != null)
        profiloUtente.setAssociato(true);
      else
        profiloUtente.setAssociato(false);

      listaResult.add(profiloUtente);
      i++;
    }
    return listaResult;
  }

  /**
   * Metodo per aggiornare l'associazione profili-utente
   * Con gestione gruppi disabilitata, viene fatta anche l'associazione tra utente
   * e il gruppo di default del profilo
   *
   * @param idAccount idAccount a cui associare i profili
   * @param codiciProfiliAssociati array dei codici Profili da associare
   *        all'utente
   * @param codiceApplicazione
   * @param gruppiDisabilitati true se la gestione gruppi e' disabiltata, false
   *        altrimenti
   */
  public void updateAssociazioneProfiliAccount(int idAccount,
      String[] codiciProfiliAssociati, String codiceApplicazione,
      boolean gruppiDisabilitati) {

    List<String> listaProfiliDaAssociare = new ArrayList<String>();
    Set<String> mappaProfiliDaAssociare = new HashSet<String>();

    if (codiciProfiliAssociati != null && codiciProfiliAssociati.length > 0) {
      // popolamento delle due liste appena create, facendo il parsing delle
      // stringa appena letta dal request
      for (int i = 0; i < codiciProfiliAssociati.length; i++) {
        listaProfiliDaAssociare.add(codiciProfiliAssociati[i]);
        mappaProfiliDaAssociare.add(codiciProfiliAssociati[i]);
      }

      // Mappa degli utenti associati al profilo secondo la base dati
      List<?> listaProfiliAssociatiDaDB =
            this.profiliDao.getProfiliUtenteByCodApp(idAccount, codiceApplicazione);
      Iterator<?> iterListaDaDb = listaProfiliAssociatiDaDB.iterator();
      Profilo profilo = null;

      // Conversione della lista in una mappa con chiave cod_profilo
      Map<String, Profilo> mappaProfiliAssociatiDaDb = new HashMap<String, Profilo>();
      while(iterListaDaDb.hasNext()){
        profilo = (Profilo) iterListaDaDb.next();
        mappaProfiliAssociatiDaDb.put(profilo.getCodiceProfilo(), profilo);
      }
      // Nel diassociare un profilo ad un utente, bisogna anche cancellare tutte
      // le associazioni gruppi-utente dei gruppi appartenenti al profilo in
      // cancellazione
      Set<String> insiemeChiavi = mappaProfiliAssociatiDaDb.keySet();
      Iterator<?> iterChiavi = insiemeChiavi.iterator();
      while(iterChiavi.hasNext()){
        String codProfiloTmp = (String) iterChiavi.next();
        if(!mappaProfiliDaAssociare.contains(codProfiloTmp)){
          this.accountDao.deleteGruppiNonAssociatiProfilo(idAccount,
              codiceApplicazione, codProfiloTmp, null);
        }
      }

      // Delete degli utenti non associati al gruppo in analisi
      this.profiliDao.deleteProfiliNonAssociatiUtente(idAccount,
          listaProfiliDaAssociare, codiceApplicazione);

      if (!listaProfiliDaAssociare.isEmpty()) {
        // select degli utenti già associati al gruppo in analisi
        Map<String, Profilo> listaUtentiPreAssociati = new HashMap<String, Profilo> ();

        List<?> listaProfiliAssociatiUtente = this.profiliDao.getProfiliUtenteByCodApp(idAccount,
              codiceApplicazione);
        Iterator<?> iterListaProfili = listaProfiliAssociatiUtente.iterator();

        // Trasformo la lista in un oggetto Map, associando ciascun elemento
        // della lista la chiave e' codiceProfilo dell'elemento stesso
        while(iterListaProfili.hasNext()){
          Profilo profiloDiUtente = (Profilo) iterListaProfili.next();
          listaUtentiPreAssociati.put(profiloDiUtente.getCodiceProfilo(),
              profiloDiUtente);
        }

        Iterator<?> iter = listaProfiliDaAssociare.iterator();
        String codiceProfilo = null;

        // insert dei profili da associare all'utente in analisi,
        if (!listaUtentiPreAssociati.isEmpty()) {
          // Esistono profili già associati all'utente in analisi, perciò devo
          // controllare che il cod_Profilo dei profili da inserire (contenuti
          // in listaProfiliAss) siano presenti o meno nella tabella W_ACCPRO
          while (iter.hasNext()) {
            codiceProfilo = (String) iter.next();
            if (!listaUtentiPreAssociati.containsKey(codiceProfilo)) {
              this.profiliDao.insertAssociazioneAccountProfilo(codiceProfilo, idAccount);
              if (gruppiDisabilitati) {
                // Estraggo il gruppo di default del profilo in analisi
                List<?> listaGruppiProfilo = this.profiliDao.getGruppiProfiloByCodApp(
                    codiceProfilo, codiceApplicazione);
                int idGruppoDefault = ((Gruppo) listaGruppiProfilo.get(0)).getIdGruppo();
                this.accountDao.insertAssociazioneAccountGruppo(idGruppoDefault,
                    idAccount);
              }
            }
          }
        } else {
          // Non esistono profili pre associati all'utente in analisi e posso
          // quindi inserire i profili utenti presenti nella lista
          // 'listaProfiliAssociati' senza alcun controllo
          while(iter.hasNext()) {
            codiceProfilo = (String) iter.next();
            this.profiliDao.insertAssociazioneAccountProfilo(codiceProfilo, idAccount);
            if(gruppiDisabilitati){
              // Estraggo il gruppo di default del profilo in analisi
              List<?> listaGruppiProfilo = this.profiliDao.getGruppiProfiloByCodApp(
                  codiceProfilo, codiceApplicazione);
              int idGruppoDefault = ((Gruppo) listaGruppiProfilo.get(0)).getIdGruppo();
              this.accountDao.insertAssociazioneAccountGruppo(idGruppoDefault,
                  idAccount);
            }
          }
        }
      }
    } else {
      List<?> listaProfiliAssociatiDaDB =
        this.profiliDao.getProfiliUtenteByCodApp(idAccount, codiceApplicazione);
      Iterator<?> iterListaDaDb = listaProfiliAssociatiDaDB.iterator();
      Profilo profilo = null;

      while(iterListaDaDb.hasNext()){
        profilo = (Profilo) iterListaDaDb.next();
        this.accountDao.deleteGruppiNonAssociatiProfilo(idAccount,
            codiceApplicazione, profilo.getCodiceProfilo(), null);
      }

      this.profiliDao.deleteProfiliNonAssociatiUtente(idAccount, null,
          codiceApplicazione);
    }
  }

  /**
   * Metodo per l'estrazione della lista dei gruppi associati al profilo,
   * filtrato per codice applicazione
   * @return Ritorna la lista di tutti gli account associati al profilo, filtrato
   *         per codice applicazione
   * @throws CriptazioneException
   */
  public List<?> getGruppiProfiloByCodApp(String codProfilo, String codApp) {
   return this.profiliDao.getGruppiProfiloByCodApp(codProfilo, codApp);
  }
}