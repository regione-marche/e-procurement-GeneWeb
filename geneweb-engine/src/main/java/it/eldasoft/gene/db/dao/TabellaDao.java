/*
 * Created on Oct 30, 2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao;

import org.springframework.dao.DataAccessException;

/**
 * Classe Dao che gestisce la copia e l'eliminazione di una tabella
 * 
 * @author cit_defilippis
 *
 */
public interface TabellaDao {
  
  /**
   * Metodo per la copia di un record di una tabella
   * 
   * @param nomeEntita
   * @param valoriCampiChiaveDestinazione
   * @param valoriCampiChiaveOrigine
   * @throws DataAccessException
   */
  void copiaRecord(String nomeEntita, String[] valoriCampiChiaveOrigine, 
        String[] valoriCampiChiaveDestinazione) throws DataAccessException;
  
  /**
   * Metodo per la copia di un record da una tabella sorgente ad una tabella
   * destinazione 
   * 
   * @param nomeEntitaSorgente
   * @param nomeEntitaDestinazione
   * @param valoriCampiChiaveSorgente valori dei campi chiave di origine
   * @param valoriCampiChiaveDestinazione valori dei campi chiave di destinazione
   * @throws DataAccessException
   */
  void copiaRecord(String nomeEntitaSorgente, String nomeEntitaDestinazione,
        String[] valoriCampiChiaveSorgente, String[] valoriCampiChiaveDestinazione)
                throws DataAccessException;
  
  /**
   * Controlla se la chiave è già utilizzata
   * 
   * @param nprat
   *        key della pratica
   * @param modulo
   * @return flag (true se la key esiste in db)
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  boolean isKeyUsed(String campoChiave, String valoreChiave, String tabella)
        throws DataAccessException;
  
  /**
   * modifica il valore di un campo
   * 
   * @param nomeCampo nome del campo da aggiornare
   * @param valoreCampo valore da assegnare al campo da aggiornare
   * @param valoriCampiChiave valori dei campi chiave della tabella
   * @param tabella tabella di appartenenza del campo da aggiornare
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  public void updateCampo(String nomeCampo, Object valoreCampo,
        String[] valoriCampiChiave, String tabella) throws DataAccessException;
  
  /**
   * Elimina un record di una tabella
   * 
   * @param tabella
   * @param valoriCampiChiave
   * @param condizioneSuppl
   *        condizione supplementare completa andrà in AND con quella principale può essere anche vuota
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  void deleteTabella(String tabella, String[] valoriCampiChiave,
        String condizioneSuppl) throws DataAccessException;
}
