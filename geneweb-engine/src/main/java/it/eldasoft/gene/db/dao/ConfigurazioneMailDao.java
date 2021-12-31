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

import it.eldasoft.gene.db.domain.system.ConfigurazioneMail;

import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Interfaccia DAO per l'accesso ai dati della tabella W_MAIL nel database di
 * configurazione dell'applicazione Web.
 *
 * @author Cristian.Febas
 */
public interface ConfigurazioneMailDao {

  /**
   * Estrae le configurazioni filtrate per codice applicazione e con il prefisso
   * in input.
   *
   * @param codapp
   *        codice applicazione
   * @param prefix
   *        prefisso comune delle chiavi da rimuovere
   * @return lista delle configurazioni che rispettano i parametri in input e
   *         tipizzate mediante la classe ConfigurazioneMail
   * @throws DataAccessException
   */
  ConfigurazioneMail getConfigurazioneMailByCodappIdcfg(String codapp, String idcfg)
      throws DataAccessException;

  /**
   * Estrae le configurazioni filtrate per codice applicazione.
   *
   * @param codapp
   *        codice applicazione
   * @return lista delle configurazioni che rispettano i parametri in input e
   *         tipizzate mediante la classe ConfigurazioneMail
   * @throws DataAccessException
   */

  List<ConfigurazioneMail> getListaConfigurazioneMailByCodapp(String codiceApplicazione) throws DataAccessException;

  /**
   * Elimina delle properties per codApp e chiave.
   * @param codiceApplicazione codice applicazione
   * @param chiavi elenco delle chiavi delle properties da eliminare
   * @throws DataAccessException
   */
  void deleteConfigurazioneMail(String codiceApplicazione,String idConfigurazione) throws DataAccessException;

  /**
   * Inserisce una property nella tabella W_MAIL.
   * @param property
   * @throws DataAccessException
   */
  void insertConfigurazioneMail(ConfigurazioneMail configMail) throws DataAccessException;

  /**
   * Update di una property nella tabella W_MAIL.
   * @param property
   * @throws DataAccessException
   */
  void updateConfigurazioneMail(ConfigurazioneMail configMail) throws DataAccessException;

}