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

import java.util.List;

import it.eldasoft.gene.db.domain.PropsConfig;
import it.eldasoft.gene.db.domain.WsdmPropsConfig;

import org.springframework.dao.DataAccessException;

/**
 * Interfaccia DAO per l'accesso ai dati della tabella W_CONFIG nel database di
 * configurazione dell'applicazione Web.
 * 
 * @author Francesco.DeFilippis
 */
public interface WsdmPropsConfigDao {

  /**
   * Estrae la property dalla tabella W_Config per codApp e chiave
   * @param codiceApplicazione codice applicazione 
   * @param chiave chiave della property da estrarre
   * @return property estratta
   * @throws DataAccessException
   */
  WsdmPropsConfig getProperty(Long idconfi,String chiave) throws DataAccessException;

  /**
   * Estrae le configurazioni filtrate per codice applicazione e con il prefisso
   * in input.
   * 
   * @param codapp
   *        codice applicazione
   * @param prefix
   *        prefisso comune delle chiavi da rimuovere
   * @return lista delle configurazioni che rispettano i parametri in input e
   *         tipizzate mediante la classe PropsConfig
   * @throws DataAccessException
   */
  List getPropertiesByPrefix(Long idconfi, String prefix)
      throws DataAccessException;

  /**
   * Estrae le configurazioni filtrate per codice applicazione.
   * 
   * @param codapp
   *        codice applicazione
   * @return lista delle configurazioni che rispettano i parametri in input e
   *         tipizzate mediante la classe PropsConfig
   * @throws DataAccessException
   */
  List getPropertiesByCodapp(String codapp) throws DataAccessException;
  
  /**
   * Elimina delle properties per codApp e chiave.
   * @param codiceApplicazione codice applicazione 
   * @param chiavi elenco delle chiavi delle properties da eliminare
   * @throws DataAccessException
   */
  void deleteProperties(Long idconfi,String[] chiavi) throws DataAccessException;

  /**
   * Inserisce una property nella tabella W_CONFIG
   * @param property
   * @throws DataAccessException
   */
  void insertProperty(WsdmPropsConfig property) throws DataAccessException;

  /**
   * Update di una property nella tabella W_CONFIG
   * @param property
   * @throws DataAccessException
   */
  void updateProperty(WsdmPropsConfig property) throws DataAccessException;
  
  /**
   * Eliina le properties per codapp e prefisso delle chiavi.
   * @param codapp codice applicazione
   * @param prefix prefisso comune delle chiavi da rimuovere
   * @throws DataAccessException
   */
  void deletePropertiesByPrefix(String codapp, String prefix) throws DataAccessException;
}