/*
 * Created on 20-lug-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.utils.properties.ConfigManager;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

/**
 * Pattern "Singleton" per la gestione dell'elenco delle sessioni collegate
 * all'applicativo.
 *
 * @author Stefano.Sabbadin
 *
 */
public class LimitatoreConnessioniUtenti {

  /** Numero di connessioni ancora disponibili */
  private int                                numeroConnessioniDisponibili;

  /** Numero massimo di utenti che possono connettersi all'applicativo */
  private int                                numeroMassimoUtentiConnessi;

  /**
   * Hash contenente gli identificativi delle sessioni degli utenti connessi all'applicativo
   */
  private HashMap<String, String[]>          datiSessioniUtentiConnessi;

  /**
   * Hash contenente le sessioni di utenti loggati all'applicativo
   */
  private HashMap<String, HttpSession>       sessioniUtentiConnessi;

  /**
   * Singleton
   */
  private static LimitatoreConnessioniUtenti instance;

  /**
   * Costruttore privato del singleton che inizializza l'oggetto con le
   * variabili per le connessioni al valore massimo letto dal file di
   * configurazione, mentre il set di id sessione è inizialmente vuoto
   */
  private LimitatoreConnessioniUtenti() {
    int numeroMassimo = Integer.parseInt(ConfigManager.getValore(CostantiGenerali.PROP_NUMERO_MAX_UTENTI_CONNESSI));
    this.numeroMassimoUtentiConnessi = numeroMassimo;
    this.numeroConnessioniDisponibili = numeroMassimo;
    this.datiSessioniUtentiConnessi = new HashMap<String, String[]>();
    this.sessioniUtentiConnessi = new HashMap<String, HttpSession>();
  }

  /**
   * Metodo statico per ottenere l'unica referenza al dizionario. Viene creato
   * l'oggetto solo la prima volta, le altre volte l'oggetto viene semplicemente
   * restituito
   *
   * @return oggetto limitatore delle connessioni
   */
  public static LimitatoreConnessioniUtenti getInstance() {
    if (instance != null) return instance;

    synchronized (LimitatoreConnessioniUtenti.class) {
      if (instance == null) {
        instance = new LimitatoreConnessioniUtenti();
      }
    }
    return instance;
  }

  /**
   * Verifica se l'utente con la sessione in input può connettersi
   * all'applicativo testando se esistono ancora connessioni disponibili
   *
   * @param idSessione
   *        sessione richiedente l'allocazione di una connessione
   * @return true se è l'allocazione ha avuto esito positivo, false se invece è
   *         stato raggiunto il numero massimo di utenti connessi e non è quindi
   *         possibile l'accesso all'applicativo
   */
  public synchronized boolean allocaConnessione(String idSessione) {
    boolean esito = false;

    if (this.numeroConnessioniDisponibili > 0) {
      // decremento il numero di connessioni in quanto ora l'utenza in input fa
      // parte del set di utenti connessi
      this.numeroConnessioniDisponibili--;
      this.datiSessioniUtentiConnessi.put(idSessione, new String[3]);
      esito = true;
    }

    return esito;
  }

  /**
   * Esegue la deallocazione della connessione per la sessione in input. Si
   * esegue un test preventivo che la sessione sia tra quelle che han ottenuto
   * con successo l'allocazione in precedenza, altrimenti non libera alcuna
   * connessione.<br>
   * Inoltre è presente una protezione aggiuntiva per cui se nell'eventualità si
   * richiedesse la deallocazione ma si è già raggiunto il numero massimo
   * possibile di connessioni disponibili, allora non si esegue alcun incremento
   *
   * @param idSessione
   *        sessione richiedente la deallocazione della connessione
   */
  public synchronized void deallocaConnessione(String idSessione) {
    if (this.datiSessioniUtentiConnessi.containsKey(idSessione)) {
      if (this.numeroMassimoUtentiConnessi > this.numeroConnessioniDisponibili) {
        // aumento le connessioni disponibili deallocando la richiedente
        this.numeroConnessioniDisponibili++;
        this.datiSessioniUtentiConnessi.remove(idSessione);
        this.sessioniUtentiConnessi.remove(idSessione);
      } else {
        // non dealloco alcuna connessione perchè sono già pari al numero
        // massimo ammissibile
      }
    } else {
      // in questo caso la richiedente è una sessione che è stata generata ed
      // aperta in
      // seguito ad un logout o alla scadenza di una sessione, e quindi non deve
      // generare la deallocazione di connessioni in quanto tale sessione non ha
      // mai preso uso esclusivo di una utenza per l'applicativo
    }
  }

  /**
   * Imposta le informazioni dell'utente connesso a partire dal suo id sessione
   *
   * @param idSessione
   *        id della sessione andata a buon fine
   * @param ip
   *        indirizzo ip del client
   * @param login
   *        login utilizzata per l'accesso
   * @param dataAccesso
   *        data di accesso all'applicativo
   */
  public synchronized void setDatiSessioneUtente(String idSessione, String ip, String login,
      String dataAccesso, HttpSession sessione) {
    String[] info = this.datiSessioniUtentiConnessi.get(idSessione);
    info[0] = ip;
    info[1] = login;
    info[2] = dataAccesso;
    this.sessioniUtentiConnessi.put(idSessione, sessione);
  }

  /**
   * @return Ritorna numeroConnessioniDisponibili.
   */
  public synchronized int getNumeroConnessioniDisponibili() {
    return numeroConnessioniDisponibili;
  }

  /**
   * @return Ritorna numeroMassimoUtentiConnessi.
   */
  public synchronized int getNumeroMassimoUtentiConnessi() {
    return numeroMassimoUtentiConnessi;
  }

  /**
   * @return Ritorna datiSessioniUtentiConnessi.
   */
  public HashMap<String, String[]> getDatiSessioniUtentiConnessi() {
    return datiSessioniUtentiConnessi;
  }


  /**
   * @return Ritorna sessioniUtentiConnessi.
   */
  public HashMap<String, HttpSession> getSessioniUtentiConnessi() {
    return sessioniUtentiConnessi;
  }


}
