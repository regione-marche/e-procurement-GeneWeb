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

import it.eldasoft.gene.db.dao.PropsConfigDao;
import it.eldasoft.gene.db.dao.WsdmPropsConfigDao;
import it.eldasoft.gene.db.domain.PropsConfig;
import it.eldasoft.gene.db.domain.WsdmPropsConfig;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;

import java.util.List;

/**
 * Manager che si occupa di gestire tutte le operazioni di business logic sulla
 * parte di gestione dell'entità W_CONFIG
 *
 * @author Francesco.DeFilippis
 */
public class PropsConfigManager {

  private PropsConfigDao propsConfigDao;
  
  private WsdmPropsConfigDao wsdmPropsConfigDao;

  /**
   * @param propsConfigDao
   *        The propsConfigDao to set.
   */
  public void setPropsConfigDao(PropsConfigDao propsConfigDao) {
    this.propsConfigDao = propsConfigDao;
  }
  
  /**
   * @param propsConfigDao
   *        The propsConfigDao to set.
   */
  public void setWsdmPropsConfigDao(WsdmPropsConfigDao wsdmPropsConfigDao) {
    this.wsdmPropsConfigDao = wsdmPropsConfigDao;
  }
  
  /**
   * Estrae una configurazione
   *
   * @return dati del server
   * @throws CriptazioneException
   */
  public PropsConfig getProperty(String codApp, String chiave) {
    return propsConfigDao.getProperty(codApp, chiave);
  }
  
  /**
   * Estrae una configurazione
   *
   * @return dati del server
   */
  public WsdmPropsConfig getWsdmProperty(Long idconfi, String chiave) {
    return wsdmPropsConfigDao.getProperty(idconfi, chiave);
  }
  
  /**
   * Estrae le configurazioni filtrate per codice applicazione e con il prefisso
   * in input.
   *
   * @param codapp
   *        codice applicazione delle properties da estrarre
   * @param prefix
   *        prefisso delle chiavi delle properties da estrarre
   * @return lista delle configurazioni che rispettano i parametri in input e
   *         tipizzate mediante la classe PropsConfig
   * @throws CriptazioneException
   */
  public List<PropsConfig> getPropertiesByPrefix(String codApp, String prefix) {
    return propsConfigDao.getPropertiesByPrefix(codApp, prefix);
  }

  /**
   * Estrae le configurazioni filtrate per codice applicazione.
   *
   * @param codapp
   *        codice applicazione delle properties da estrarre
   * @param prefix
   *        prefisso delle chiavi delle properties da estrarre
   * @return lista delle configurazioni che rispettano i parametri in input e
   *         tipizzate mediante la classe PropsConfig
   * @throws CriptazioneException
   */
  public List<PropsConfig> getPropertiesByCodapp(String codApp) {
    return propsConfigDao.getPropertiesByCodapp(codApp);
  }

  /**
   * Estrae le configurazioni wsdm filtrate per codice applicazione.
   *
   * @param codapp
   *        codice applicazione delle properties da estrarre
   * @return lista delle configurazioni che rispettano i parametri in input e
   *         tipizzate mediante la classe PropsConfig
   * @throws CriptazioneException
   */
  public List<WsdmPropsConfig> getWsdmPropertiesByCodapp(String codApp) {
    return wsdmPropsConfigDao.getPropertiesByCodapp(codApp);
  }
  
  /**
   * Carica tutte le configurazioni definite nella tabella W_CONFIG per
   * l'applicativo corrente e la componente generale (W_). Questo metodo
   * rispetta la priorita' delle configurazioni definite nel file
   * global.properties: se un configurazione e' gia' stata definita nel file non
   * viene sostituita da quella memorizzata nella tabella W_CONFIG.
   *
   * @param codApp
   *        Codice dell'applicativo
   */
  public void loadProperties(String codApp) {

    // Configurazioni dell'applicativo corrente
    List<PropsConfig> propsConfig = getPropertiesByCodapp(codApp);
    for (int p = 0; p < propsConfig.size(); p++) {
      String chiave = propsConfig.get(p).getChiave();
      String valore = propsConfig.get(p).getValore();
      if (valore == null) valore = new String("");
      ConfigManager.caricaProprietaDB(chiave, valore);
    }
    
    List<WsdmPropsConfig> propsWsdmConfig = getWsdmPropertiesByCodapp(codApp);
    for (int p = 0; p < propsWsdmConfig.size(); p++) {
      Long idconfi = propsWsdmConfig.get(p).getIdconfi();
      String chiave = propsWsdmConfig.get(p).getChiave();
      String valore = propsWsdmConfig.get(p).getValore();
      if (valore == null) valore = new String("");
      ConfigManager.caricaProprietaDB(chiave + "." + idconfi.toString(), valore);
    }
    
    // Configurazioni generali W_
    List<PropsConfig> propsConfigW_ = getPropertiesByCodapp("W_");
    for (int p = 0; p < propsConfigW_.size(); p++) {
      String chiave = propsConfigW_.get(p).getChiave();
      String valore = propsConfigW_.get(p).getValore();
      if (valore == null) valore = new String("");
      ConfigManager.caricaProprietaDB(chiave, valore);
    }

  }

  /**
   * Inserisce nuove property eliminando se ci sono delle vecchie con codapp e
   * chiave uguale. <b>NOTA BENE: il codapp dell'array deve essere uguale per
   * tutte le properties!</b>
   *
   * @param props
   *        properties da inserire
   * @throws CriptazioneException
   */
  public void insertProperties(PropsConfig[] props) {
    String[] chiavi = new String[props.length];
    for (int i = 0; i < props.length; i++) {
      chiavi[i] = props[i].getChiave();
    }
    propsConfigDao.deleteProperties(props[0].getCodApp(), chiavi);
    for (int i = 0; i < props.length; i++) {
      propsConfigDao.insertProperty(props[i]);
    }
  }

  /**
   * Inserisce nuove property eliminando se ci sono delle vecchie con idconfi e
   * chiave uguale. <b>NOTA BENE: l'idconfi dell'array deve essere uguale per
   * tutte le properties!</b>
   *
   * @param props
   *        properties da inserire
   * @throws CriptazioneException
   */
  public void insertWsdmProperties(WsdmPropsConfig[] props) {
    String[] chiavi = new String[props.length];
    for (int i = 0; i < props.length; i++) {
      chiavi[i] = props[i].getChiave();
    }
    wsdmPropsConfigDao.deleteProperties(props[0].getIdconfi(), chiavi);
    for (int i = 0; i < props.length; i++) {
      wsdmPropsConfigDao.insertProperty(props[i]);
    }
  }
  
  public void updateProperties(PropsConfig[] props) {
    for (int i = 0; i < props.length; i++) {
      propsConfigDao.updateProperty(props[i]);
    }
  }

  /**
   * Elimina le properties indicate in input. <b>NOTA BENE: il codapp dell'array
   * deve essere uguale per tutte le properties!</b>
   *
   * @param props
   *        properties da eliminare
   * @throws CriptazioneException
   */
  public void deleteProperties(PropsConfig[] props) {
    String[] chiavi = new String[props.length];
    for (int i = 0; i < props.length; i++) {
      chiavi[i] = props[i].getChiave();
    }
    propsConfigDao.deleteProperties(props[0].getCodApp(), chiavi);
  }

  /**
   * Elimina le properties filtrate per codice applicazione e con il prefisso
   * delle chiavi in input.
   *
   * @param codapp
   *        codice applicazione delle properties da rimuovere
   * @param prefix
   *        prefisso delle chiavi delle properties da rimuovere
   * @throws CriptazioneException
   */
  public void deletePropertiesByPrefix(String codapp, String prefix) {
    propsConfigDao.deletePropertiesByPrefix(codapp, prefix);
  }

}
