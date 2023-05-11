/*
 * Created on 28-lug-2006
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

import it.eldasoft.gene.db.domain.Tabellato;
import it.eldasoft.gene.db.domain.TabellatoWsdm;

/**
 * Interfaccia DAO per l'accesso ai dati dei tabellati TAB1, TAB2,ecc
 *
 * @author Luca Giacomazzo
 */
public interface TabellatiDao {

  /**
   * Estrae la lista dei valori di un tabellato
   *
   * @param codiceTabellato
   * @return
   * @throws DataAccessException
   */
  public List<Tabellato> getTabellati(String codiceTabellato) throws DataAccessException;

  /**
   * Estrae la lista dei valori di un tabellato di una tanNN
   *
   * @param codiceTabellato
   * @param numeroTabella
   * @return
   * @throws DataAccessException
   */
  public List<Tabellato> getTabellati(String codiceTabellato, int numeroTabella)
      throws DataAccessException;

  /**
   * Estrae i dati di un tabellato
   *
   * @param codiceTabellato
   * @param valoreTabellato
   * @return Tabellato trovato
   * @throws DataAccessException
   */
  public Tabellato getTabellato(String codiceTabellato, String valoreTabellato)
      throws DataAccessException;

  /**
   * Estrae la decrizione di un tabellato
   *
   * @param codiceTabellato
   * @param valoreTabellato
   * @return
   * @throws DataAccessException
   */
  public String getDescrTabellato(String codiceTabellato, String valoreTabellato)
      throws DataAccessException;

  /**
   * Estrae la decrizione supplementare di un tabellato
   *
   * @param codiceTabellato
   * @param valoreTabellato
   * @return
   * @throws DataAccessException
   */
  public String getDescrSupplementare(String codiceTabellato, String valoreTabellato)
      throws DataAccessException;

  /**
   * Estrae l'elenco dei tabellati (codice - descrizione) dal TAB6, filtrando
   * per il codiceTabellato
   *
   * @param codiceTabellato
   * @return
   * @throws DataAccessException
   */
  public List<Tabellato> getElencoTabellati(String codiceTabellato)
      throws DataAccessException;

  public List<String> getCampiTabellati(String schemaViste,String entita) throws DataAccessException;
  /**
   * Aggiorna la decrizione di un tabellato
   *
   * @param valoreCampoDesc
   * @param valoreChiaveCOD
   * @param valoreChiaveTIP
   * @param tabellato
   * @return
   * @throws DataAccessException
   */
  public void updateDescTabellato(String valoreCampoDesc,String valoreChiaveCOD,String valoreChiaveTIP,String tabellato)
      throws DataAccessException;

  /**
   * Estrae la lista dei valori di un tabellato
   *
   * @param sistema
   * @param codice
   * @return
   * @throws DataAccessException
   */
  public List<TabellatoWsdm> getTabellatiWsdm(Long idconfi, String sistema, String codice) throws DataAccessException;

  /**
   * Estrae la lista dei valori di un tabellato
   *
   * @param sistema
   * @param codice
   * @return
   * @throws DataAccessException
   */
  public List<TabellatoWsdm> getTabellatiFromIdconfiCftab(Long idconfi, String cftab) throws DataAccessException;


  /**
   * Estrae l'elenco dei tabellati (codice - descrizione) dal TAB6, filtrando
   * per il codiceTabellato
   *
   * @param codapp
   * @param sistema
   * @return
   * @throws DataAccessException
   */
  public List<Tabellato> getElencoTabellatiWsdm(String codapp, String sistema, Long idconfi)
      throws DataAccessException;
}
