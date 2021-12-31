/*
 * Created on 16-giu-2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl;

import it.eldasoft.gene.db.dao.ConfigurazioneMailDao;
import it.eldasoft.gene.db.domain.system.ConfigurazioneMail;
import it.eldasoft.utils.sicurezza.CriptazioneException;

import java.util.List;

/**
 * Manager che si occupa di gestire tutte le operazioni di business logic sulla
 * parte di gestione dell'entità W_MAIL
 *
 * @author Cristian.Febas
 */
public class ConfigurazioneMailManager {

  private ConfigurazioneMailDao ConfigurazioneMailDao;

  /**
   * @param ConfigurazioneMailDao
   *        The ConfigurazioneMailDao to set.
   */
  public void setConfigurazioneMailDao(ConfigurazioneMailDao ConfigurazioneMailDao) {
    this.ConfigurazioneMailDao = ConfigurazioneMailDao;
  }

  /**
   * Estrae le configurazioni filtrate per codice applicazione e con il prefisso
   * in input.
   *
   * @param codapp
   *        codice applicazione della configurazione di posta
   * @param idcfg
   *        id configurazione di posta
   * @return Configurazione tipizzata mediante la classe ConfigurazioneMail
   * @throws CriptazioneException
   */
  public ConfigurazioneMail getConfigurazioneMailByCodappIdcfg(String codapp, String idcfg) {
    return ConfigurazioneMailDao.getConfigurazioneMailByCodappIdcfg(codapp, idcfg);
  }

  /**
   * Estrae le configurazioni filtrate per codice applicazione.
   *
   * @param codapp
   *        codice applicazione delle properties da estrarre
   * @return lista delle configurazioni che rispettano i parametri in input e
   *         tipizzate mediante la classe ConfigurazioneMail
   * @throws CriptazioneException
   */

  public List<ConfigurazioneMail> getListaConfigurazioneMailByCodapp(String codapp){
    return this.ConfigurazioneMailDao.getListaConfigurazioneMailByCodapp(codapp);
  }


  /**
   * Inserisce su W_MAIL la nuova configurazione
   *
   * @param configMail
   *        configurazione da inserire
   * @throws CriptazioneException
   */
  public void insertConfigurazioneMail(ConfigurazioneMail configMail) {
      ConfigurazioneMailDao.insertConfigurazioneMail(configMail);
  }

  /**
   * Aggiorna su W_MAIL la configurazione
   *
   * @param configMail
   *        configurazione da aggiornare
   * @throws CriptazioneException
   */
  public void updateConfigurazioneMail(ConfigurazioneMail configMail) {
      ConfigurazioneMailDao.updateConfigurazioneMail(configMail);
  }

  /**
   * Elimina la configurazione
   *
   * @param configMail
   *        configurazione da aggiornare
   * @throws CriptazioneException
   */
  public void deleteConfigurazioneMail(String codapp,String idcfg) {
    ConfigurazioneMailDao.deleteConfigurazioneMail(codapp, idcfg);
  }

}
