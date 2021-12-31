/*
 * Created on 16-06-2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.system;

import it.eldasoft.gene.bl.ConfigurazioneMailManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.domain.system.ConfigurazioneMail;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;

import java.util.List;

/**
 * Manager per la business logic per l'interfacciamento con la configurazione
 * dei parametri di posta, presenti nella tabella W_CONFIG.
 *
 * @author Stefano.Sabbadin
 */
public class MailManager {

  ConfigurazioneMailManager ConfigurazioneMailManager;

  /**
   * @param ConfigurazioneMailManager
   *        The ConfigurazioneMailManager to set.
   */
  public void setConfigurazioneMailManager(ConfigurazioneMailManager ConfigurazioneMailManager) {
    this.ConfigurazioneMailManager = ConfigurazioneMailManager;
  }

  /**
   * @return Ritorna ConfigurazioneMailManager.
   */
  public ConfigurazioneMailManager getConfigurazioneMailManager() {
    return ConfigurazioneMailManager;
  }

  /**
   * Estrae i parametri per la connessione al provider mail dalla W_MAIL.
   *
   * @param codapp
   *        codice applicazione
   * @param idcfg
   *        id configurazione
   * @return oggetto della classe ConfigurazioneMail contenente le informazioni relative
   *         alla configurazione mail e presenti nella W_MAIL
   * @throws CriptazioneException
   *         eccezione ritornata nel caso di problemi di decifratura della
   *         password
   */
  public ConfigurazioneMail getConfigurazione(String codapp, String idcfg)
      throws CriptazioneException {

    //@SuppressWarnings("unchecked")
    ConfigurazioneMail cm = this.ConfigurazioneMailManager.getConfigurazioneMailByCodappIdcfg(codapp, idcfg);
    if(cm == null){
      cm = this.ConfigurazioneMailManager.getConfigurazioneMailByCodappIdcfg(codapp, CostantiGenerali.PROP_CONFIGURAZIONE_MAIL_STANDARD);
    }
    if(cm != null){
      // forzo la configurazione anche se si recupera quella standard
      cm.setIdcfg(idcfg);
      // decripto la password
      if (cm.getPassword() != null) {
        ICriptazioneByte decriptatore = FactoryCriptazioneByte.getInstance(
            ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
            cm.getPassword().getBytes(), ICriptazioneByte.FORMATO_DATO_CIFRATO);
        cm.setPassword(new String(decriptatore.getDatoNonCifrato()));
      }
    }

    return cm;

  }

  /**
   * Estrae la lista di configurazioni dalla W_MAIL per una applicazione.
   *
   * @param codapp
   *        codice applicazione
   * @return Lista oggetti della classe ConfigurazioneMail contenente le informazioni relative
   *         alla configurazione mail e presenti nella W_MAIL
   * @throws CriptazioneException
   *         eccezione ritornata nel caso di problemi di decifratura della
   *         password
   */

  public List<ConfigurazioneMail> getListaConfigurazioni(String codapp)
    throws CriptazioneException {

    List<ConfigurazioneMail> props = this.ConfigurazioneMailManager.getListaConfigurazioneMailByCodapp(codapp);
    return props;
  }

  /**
   * Aggiorna la configurazione del server di posta
   * esclusa la password. Se viene variato l'indirizzo email, automaticamente si
   * sbianca anche la password.
   *
   * @param config
   *        contenitore con i dati da salvare nel DB
   */
  public void updateConfigurazione(ConfigurazioneMail config) {
    ConfigurazioneMail configMail = this.ConfigurazioneMailManager.getConfigurazioneMailByCodappIdcfg(config.getCodapp(), config.getIdcfg());
    if (configMail != null){
      // si resetta la password se cambia il mittente, oppure si lascia quella presente in db
      if (configMail.getMailMitt() != null
          && !config.getMailMitt().equals(configMail.getMailMitt())) {
        config.setPassword(null);
      } else {
        config.setPassword(configMail.getPassword());
      }
      this.ConfigurazioneMailManager.updateConfigurazioneMail(config);
    } else {
      this.ConfigurazioneMailManager.insertConfigurazioneMail(config);
    }
  }

  /**
   * Aggiorna l'attributo relativo alla password
   *
   * @param password
   *        la password da aggiornare nel DB
   * @param codapp
   *        codice applicazione a cui viene riferita la password
   * @param idcfg
   *        id configurazione a cui viene riferita la password
   * @throws CriptazioneException
   *         eccezione generata nel caso in cui la cifratura della password
   *         fallisca
   */
  public void updatePassword(String password, String codapp, String idcfg)
      throws CriptazioneException {
    String passwordCifrata = null;
    if (password != null) {
      // solo nel caso di password valorizzata devo applicare la cifratura
      ICriptazioneByte criptatore = FactoryCriptazioneByte.getInstance(
          ConfigManager.getValore(CostantiGenerali.PROP_TIPOLOGIA_CIFRATURA_DATI),
          password.getBytes(), ICriptazioneByte.FORMATO_DATO_NON_CIFRATO);
      passwordCifrata = new String(criptatore.getDatoCifrato());
    }

    ConfigurazioneMail configMail = this.ConfigurazioneMailManager.getConfigurazioneMailByCodappIdcfg(codapp,idcfg);
    configMail.setPassword(passwordCifrata);
    this.ConfigurazioneMailManager.updateConfigurazioneMail(configMail);

  }
}
