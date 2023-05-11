/*
 * Created on 7-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.commons.web.struts;

import it.eldasoft.gene.bl.AttConfigManager;
import it.eldasoft.gene.bl.LivelliManager;
import it.eldasoft.gene.bl.MetadatiManager;
import it.eldasoft.gene.bl.PropsConfigManager;
import it.eldasoft.gene.bl.QuartzConfigManager;
import it.eldasoft.gene.bl.admin.AccountManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sicurezza.CriptazioneException;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.config.ModuleConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Plugin di default per lo startup di Struts per un'applicazione web generica
 * Eldasoft SpA.
 *
 * @author Stefano.Sabbadin
 */
public class GeneStartupPlugIn extends PlugInBase {

  /**
   * @see it.eldasoft.gene.commons.web.struts.PlugInBase#getPathCartellaProperties()
   */
  @Override
  public String getPathCartellaProperties() {
    return CostantiGenerali.DEFAULT_PATH_CARTELLA_PROPERTIES;
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.PlugInBase#getNomeFilePropertiesAttivazione()
   */
  @Override
  public String getNomeFilePropertiesAttivazione() {
    return CostantiGenerali.DEFAULT_NOME_FILE_PROPERTIES_ATTIVAZIONE;
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.PlugInBase#getNomeFileProperties()
   */
  @Override
  public String getNomeFileProperties() {
    return CostantiGenerali.DEFAULT_NOME_FILE_PROPERTIES;
  }
  
  /**
   * @see it.eldasoft.gene.commons.web.struts.PlugInBase#getNomeFilePropertiesPlainText()
   */
  @Override
  public String getNomeFilePropertiesPlainText() {
    return CostantiGenerali.DEFAULT_NOME_FILE_PROPERTIES_PLAINTEXT;
  }

  /**
   * @see it.eldasoft.gene.commons.web.struts.PlugInBase#initConfigurabile(org.apache.struts.action.ActionServlet,
   *      org.apache.struts.config.ModuleConfig)
   */
  @Override
  protected void initConfigurabile(ActionServlet servlet, ModuleConfig moduleConfig) {

    ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servlet.getServletContext());

    // Carica le configurazioni della tabella W_ATT (contiene le informazioni relative all'attivazione dell'applicativo)
    AttConfigManager atm = (AttConfigManager) ctx.getBean("attConfigManager");
    atm.loadListAttConfig(ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE));
    
    // Carica le configurazioni dalla tabella W_CONFIG
    PropsConfigManager pcm = (PropsConfigManager) ctx.getBean("propsConfigManager");
    pcm.loadProperties(ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE));

    MetadatiManager mm = (MetadatiManager) ctx.getBean("metadatiManager");
    mm.carica();

    LivelliManager lm = (LivelliManager) ctx.getBean("livelliManager");
    lm.carica();

    AccountManager am = (AccountManager) ctx.getBean("accountManager");
    try {
      am.updateLogins();
    } catch (CriptazioneException e) {
      logger.error("Errore durante la sincronizzazione login in chiaro e criptate nella fase di calcolo login", e);
    } catch (DataAccessException e) {
      logger.error("Errore durante la sincronizzazione login in chiaro e criptate nella fase di aggiornamento db", e);
    }

    // Carica ed avvia/riavvia le pianificazioni definite nella tabella W_QUARTZ
    QuartzConfigManager qcm = (QuartzConfigManager) ctx.getBean("quartzConfigManager");
    qcm.loadListQuartzConfig(ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE), ctx);

  }

}