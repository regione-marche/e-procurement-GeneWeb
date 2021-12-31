/*
 * Created on 1-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Interfaccia DAO per l'accesso ai dati delle tabelle di definizione dei
 * livelli sugli utenti per le entità, in W_ACCLIV.
 * 
 * @author Stefano.Sabbadin
 */
public interface LivelliDao {
  
  /**
   * Estrae l'elenco dei livelli definiti nella banca dati
   * 
   * @return lista di oggetti di tipo
   *         {@link it.eldasoft.utils.profiles.domain.Livello}
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<?> getElencoLivelli() throws DataAccessException;

}
