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
import it.eldasoft.gene.db.dao.GruppiDao;
import it.eldasoft.gene.db.domain.admin.Gruppo;
import it.eldasoft.gene.db.domain.genric.GruppoRicerca;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Manager che si occupa di gestire tutte le operazioni di business logic sulla
 * parte di amministrazione di un singolo gruppo e le relative funzionalita'
 * 
 * @author Luca.Giacomazzo
 */

public class GruppiManager {

  /** Reference al DAO per l'accesso alla tabella TAB_GRUPPI */
  private GruppiDao        gruppiDao;

  /** Reference al manager per la gestione della tabella W_GENCHIAVI */
  private GenChiaviManager genChiaviManager;

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(GruppiManager.class);

  /**
   * @return Ritorna gruppiDao.
   */
  public GruppiDao getGruppiDao() {
    return gruppiDao;
  }

  /**
   * @param gruppiDao
   *        gruppiDao da settare internamente alla classe.
   */
  public void setGruppiDao(GruppiDao gruppoDao) {
    this.gruppiDao = gruppoDao;
  }

  /**
   * @return Ritorna getGenChiaviManager.
   */
  public GenChiaviManager getGenChiaviManager() {
    return genChiaviManager;
  }

  /**
   * @param genChiaviManager
   *        genChiaviManager da settare internamente alla classe.
   */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  /**
   * Estrae i dati di un gruppo a partire da idGruppo
   * 
   * @param idGruppo
   *        idGruppo id del gruppo che si vuole selezionare
   * @return Ritorna un gruppo
   */
  public Gruppo getGruppoById(int idGruppo){
    return gruppiDao.getGruppoById(idGruppo);
  }

  /**
   * Estrae la lista dei gruppi esistenti, filtrandoli per codice profilo ed
   * ordinandoli per nome
   * 
   * @return Ritorna la lista dei Gruppi ordinata per nome
   */
  public List<?> getGruppiOrderByNome(String codiceProfilo) {
    return this.gruppiDao.getGruppiOrderByNome(codiceProfilo);
  }

  /**
   * Update delle proprietà del gruppo in analisi per
   * l'applicazione in input
   * 
   * @param gruppo
   *        Gruppo
   */
  public void updateGruppo(Gruppo gruppo) {
    this.gruppiDao.updateGruppo(gruppo);
  }

  /**
   * Insert delle proprietà del gruppo in analisi per
   * l'applicazione in input
   * 
   * @param gruppo
   *        Gruppo
   * 
   * @return Ritorna l'ID del nuovo gruppo inserito
   */
  public int insertGruppo(Gruppo gruppo, String codiceProfilo) {
    // Fase 1: recupero l'ID_GRUPPO dalla tabella W_GENCHIAVI
    int idGruppo = this.genChiaviManager.getNextId("W_GRUPPI");
    // Aggiornamento dell'idGruppo per l'insert nella tabella W_GRUPPI
    gruppo.setIdGruppo(idGruppo);

    // Fase 2: operazione di insert del gruppo in analisi nella tabella
    // W_GRUPPI.
    this.gruppiDao.insertGruppo(gruppo, codiceProfilo);

    return idGruppo;
  }

  /**
   * Delete di un gruppo ea partire dall'ID del gruppo,
   * previa verifica che non possiede nulla di associato
   * 
   * @param idGruppo
   *        idGruppo id del gruppo che si vuole cancellare
   * @param codiceApplicazione
   *        codice dell'applicazione in cui eseguire alcuni controlli
   * 
   * @return Ritorna la stringa rappresentante l'esito della chiamata
   */
  public String deleteGruppo(int idGruppo,
      String codiceApplicazione) {
    String messaggioErrore = null;
    boolean continua = true;
    List<?> lista = null;

    // Fase 1: operazione di controllo se al gruppo da cancellare sono
    // associati comunque delle ricerche in altri applicativi
    if (continua) {
      int numeroRicercheTotaliCollegate = this.gruppiDao.getNumeroRicercheByIdGruppoAltriCodApp(idGruppo, codiceApplicazione);
      if (numeroRicercheTotaliCollegate > 0) {
        // Esistono ricerche associate al gruppo, quindi non è possibile
        // effettuare la cancellazione del gruppo
        messaggioErrore = "errors.deleteGruppo.legameGruppoRicercheAltroCodApp";
        lista = null;
        continua = false;
      }
    }

    // Fase 2: operazione di controllo se al gruppo da cancellare sono
    // associati comunque dei modelli in altri applicativi
    if (continua) {
      int numeroModelliTotaliCollegati = this.gruppiDao.getNumeroModelliByIdGruppoAltriCodApp(idGruppo, codiceApplicazione);
      if (numeroModelliTotaliCollegati > 0) {
        // Esistono modelli associati al gruppo, quindi non è possibile
        // effettuare la cancellazione del gruppo
        messaggioErrore = "errors.deleteGruppo.legameGruppoModelliAltroCodApp";
        lista = null;
        continua = false;
      }
    }

    // Fase 3: operazione di controllo se al gruppo da cancellare sono associati
    // degli account in W_ACCGRP
    if (continua) {
      lista = this.gruppiDao.getIdAccountByIdGruppo(idGruppo);

      if (!lista.isEmpty()) {
        // Esistono account associati al gruppo, quindi non è possibile
        // effettuare la cancellazione del gruppo
        messaggioErrore = "errors.deleteGruppo.legameGruppoUtenti";
        lista = null;
        continua = false;
      }
    }

    // Fase 4: operazione di controllo se al gruppo da cancellare sono
    // associati delle ricerche in W_GRPRIC per l'applicazione corrente
    if (continua) {
      int numeroRicerche = this.gruppiDao.getNumeroRicercheByIdGruppoCodApp(
          idGruppo, codiceApplicazione);
      if (numeroRicerche > 0) {
        // Esistono ricerche associate al gruppo, quindi non è possibile
        // effettuare la cancellazione del gruppo
        messaggioErrore = "errors.deleteGruppo.legameGruppoRicerche";
        lista = null;
        continua = false;
      }
    }

    // Fase 5: operazione di controllo se al gruppo da cancellare sono
    // associati dei modelli in W_GRPMOD per l'applicazione corrente
    if (continua) {
      int numeroModelli = this.gruppiDao.getNumeroModelliByIdGruppoCodApp(
          idGruppo, codiceApplicazione);
      if (numeroModelli > 0) {
        // Esistono modelli associati al gruppo, quindi non è possibile
        // effettuare la cancellazione del gruppo
        messaggioErrore = "errors.deleteGruppo.legameGruppoModelli";
        lista = null;
        continua = false;
      }
    }

    if (messaggioErrore == null) {
      // Il gruppo in analisi può essere cancellato, perchè non è associato a
      // nessun account, nessuna ricerca e a nessun modello
      this.gruppiDao.deleteGruppo(idGruppo);
    }
    return messaggioErrore;
  }
  /**
   * Ritorna la lista di tutti i gruppi, con l'attributo 'associato' settato a
   * true se il gruppo e' associato alla ricerca, a false altrimenti.
   * 
   * @param elencoGruppiRicerca
   *        elenco dei gruppi associati alla ricerca in analisi
   * @param codiceProfilo codice del profilo attivo
   * @return Ritorna la lista di tutti i gruppi, filtrati per codice profilo,
   *         con l'attributo 'associato' settato a true se il gruppo e'
   *         associato alla ricerca, a false altrimenti.
   */
  public List<?> getGruppiConAssociazioneRicerca(Set<?> elencoGruppiRicerca,
      String codiceProfilo) {
    // lista di tutti i gruppi esistenti ordinata per nome
    List<?> listaGruppi = this.getGruppiOrderByNome(codiceProfilo);

    Vector<GruppoRicerca> elencoGruppiConAssociazione = new Vector<GruppoRicerca>();
    GruppoRicerca gruppoRicerca = null;
    Gruppo gruppo = null;
    for (int i = 0; i < listaGruppi.size(); i++) {
      gruppo = (Gruppo) listaGruppi.get(i);
      gruppoRicerca = new GruppoRicerca(gruppo);
      if (elencoGruppiRicerca.contains("" + gruppo.getIdGruppo()))
        gruppoRicerca.setAssociato(true);
      elencoGruppiConAssociazione.add(gruppoRicerca);
    }
    return elencoGruppiConAssociazione;
  }

  /**
   * Estrae la lista dei gruppi associati al profilo attivo, con il numero di
   * elementi associati, ovvero numero di utenti, ricerche e modelli.
   * 
   * @param codiceProfilo codice del profilo attivo
   * 
   * @return Ritorna la lista di GruppoConNumeroAssociazioni
   */
  public List<?> getGruppiConNumeroAssociazioni(String codiceProfilo) {
    return this.gruppiDao.getGruppiConNumeroAssociazioniByCodApp(codiceProfilo);
  }

  /**
   * Estrae la lista dei gruppi associati ad una ricerca a partire dall'idRicerca
   * 
   * @param idRicerca
   * @return Ritorna  la lista dei gruppi associati ad una ricerca a partire 
   *         dall'idRicerca
   */
  public List<?> getGruppiByIdRicerca(int idRicerca) {
    return this.gruppiDao.getGruppiByIdRicerca(idRicerca);
  }
  
  public List<?> getGruppiByIdModello(int idModello) {
    return this.gruppiDao.getGruppiByIdModello(idModello);
  }
  
}