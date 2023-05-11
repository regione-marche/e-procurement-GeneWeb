package it.eldasoft.gene.bl;

import it.eldasoft.gene.db.dao.AttConfigDao;
import it.eldasoft.gene.db.domain.admin.AttConfig;
import it.eldasoft.utils.properties.ConfigManager;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

public class AttConfigManager {

  private AttConfigDao attConfigDao;

  static Logger        logger = Logger.getLogger(AttConfigManager.class);

  public void setAttConfigDao(AttConfigDao attConfigDao) {
    this.attConfigDao = attConfigDao;
  }

  public List<AttConfig> getAttConfigByCodapp(String codapp) {
    return attConfigDao.getAttConfigByCodapp(codapp);
  }

  public AttConfig getAttConfig(String codapp, String chiave) {
    return attConfigDao.getAttConfig(codapp, chiave);
  }
  
  public void insertAttConfig(String codapp, String chiave, String valore) {
    attConfigDao.insertAttConfig(codapp, chiave, valore);
  }
  
  public void updateAttConfig(String codapp, String chiave, String valore) {
    attConfigDao.updateAttConfig(codapp, chiave, valore);
  }
  
  public Long countAttConfig(String codapp, String chiave) {
    return attConfigDao.countAttConfig(codapp, chiave);
  }
  
  /**
   * Carica le configurazioni di attivazione (tabella W_ATT).
   * 
   * @param codapp
   *        Codice applicativo
   */
  public void loadListAttConfig(String codapp) {
    List<AttConfig> attConfig = getAttConfigByCodapp(codapp);
    for (int att = 0; att < attConfig.size(); att++) {
      String chiave = attConfig.get(att).getChiave();
      String valore = attConfig.get(att).getValore();
      if (valore == null) valore = new String("");
      ConfigManager.caricaProprietaDB(chiave, valore);
    }
  }
  
  /**
   * Salva la configurazione di attivazione (tabella W_ATT)
   * @param codapp
   * @param chiave
   * @param valore
   */
  public void saveAttConfig(String codapp, String chiave, String valore) {
    Long cnt = countAttConfig(codapp, chiave);
    if (cnt != null && cnt.longValue() > 0) {
      updateAttConfig(codapp, chiave, valore);
      ConfigManager.ricaricaProprietaDB(chiave, valore);
    } else {
      insertAttConfig(codapp, chiave, valore);
      ConfigManager.caricaProprietaDB(chiave, valore);
    }
  }
}
