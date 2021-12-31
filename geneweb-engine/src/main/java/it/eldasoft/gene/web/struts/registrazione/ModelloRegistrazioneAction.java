/*
 * Created on 04-03-2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts.registrazione;

import it.eldasoft.gene.bl.FileManager;
import it.eldasoft.gene.bl.FileManagerException;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoSessione;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * Action che effettua il recupero del  modello di registrazionepresente nell'area shared
 * e configurato in db
 *
 * @author Cristian.Febas
 */
public class ModelloRegistrazioneAction extends DispatchActionBaseNoSessione {

  private static final String SUCCESS_DOWNLOAD         = null;
  private static final String ERROR_DOWNLOAD_MODELLO_REG = "errorDownloadModelloRegistrazione";

  static Logger logger = Logger.getLogger(ModelloRegistrazioneAction.class);

  /**
   * Reference al manager per la gestione delle operazioni in db
   */
  private SqlManager sqlManager;

  /**
   * Reference al manager per la gestione dei file da gestire dall'applicazione
   * web
   */
  private FileManager fileManager;


  /**
   * @param sqlManager
   *        sqlManager da settare internamente alla classe.
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * @param fileManager fileManager da settare internamente alla classe.
   */
  public void setFileManager(FileManager fileManager) {
    this.fileManager = fileManager;
  }


  public ActionForward download(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled()) logger.debug("download: inizio metodo");

    String messageKey = null;
    String target = SUCCESS_DOWNLOAD;
    String modelloRegistrazione = "";

    try {
      modelloRegistrazione = ConfigManager.getValore(CostantiGenerali.PROP_REG_FACSIMILE);

      // Download del documento associato richiesto tramite fileManager
      this.fileManager.download(modelloRegistrazione, response);


    } catch (FileManagerException fm) {
      String logMessage = this.resBundleGenerale.getString(fm.getChiaveResourceBundle());
      for(int i=0; i < fm.getParametri().length; i++)
        logMessage = logMessage.replaceAll("\\{" + i + "\\}", fm.getParametri()[i].toString());
      logger.error(logMessage, fm);
      messageKey = "errors.modelli.download";
      this.aggiungiMessaggio(request, messageKey);
    } finally {
      if(messageKey != null){
          target = ERROR_DOWNLOAD_MODELLO_REG;
          return mapping.findForward(target);
      }
    }
    if (logger.isDebugEnabled()) logger.debug("download: fine metodo");
    return null;
  }
}
