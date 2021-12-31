/*
 * Created on 25-mag-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.console.web.struts.schedric;

import it.eldasoft.console.bl.schedric.SchedRicManager;
import it.eldasoft.console.db.domain.schedric.CodaSched;
import it.eldasoft.gene.bl.FileManager;
import it.eldasoft.gene.bl.FileManagerException;
import it.eldasoft.gene.bl.LogEventiManager;
import it.eldasoft.gene.bl.genric.RicercheManager;
import it.eldasoft.gene.commons.web.struts.CostantiGeneraliStruts;
import it.eldasoft.gene.commons.web.struts.DispatchActionBaseNoOpzioni;
import it.eldasoft.gene.db.dao.jdbc.ParametroStmt;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.domain.genric.ContenitoreDatiRicerca;
import it.eldasoft.gene.db.domain.genric.ParametroRicerca;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.dao.DataAccessException;

/**
 * Action di gestione della coda delle schedulazioni
 * Metodo download: permette di effettuare il download del file associato
 * Metodo elimina: elimina il record della coda e il file associato
 * 
 * @author Francesco De Filippis
 */
public class DettaglioCodaSchedAction extends DispatchActionBaseNoOpzioni {

  static Logger               logger                       = Logger.getLogger(DettaglioCodaSchedAction.class);

  public static final String FORWARD_ERRORE_DOWNLOAD   = "errorDownload";
  public static final String FORWARD_OK_ELIMINA        = "successElimina";
  public static final String FORWARD_KO_ELIMINA        = "errorElimina";
  
  
  protected FileManager    fileManager;
  protected RicercheManager    ricercheManager;
  protected SchedRicManager    schedRicManager;
  protected LogEventiManager    logEventiManager;

  /**
   * @param fileManager The fileManager to set.
   */
  public void setFileManager(FileManager fileManager) {
    this.fileManager = fileManager;
  }

  /**
   * @param schedRicManager schedRicManager da settare internamente alla classe.
   */
  public void setSchedRicManager(SchedRicManager schedRicManager) {
    this.schedRicManager = schedRicManager;
  }

  /**
   * @param RicercheManager The RicercheManager to set.
   */
  public void setRicercheManager(RicercheManager ricercheManager) {
    this.ricercheManager = ricercheManager;
  }
  
  /**
   * @param fileManager The fileManager to set.
   */
  public void setLogEventiManager(LogEventiManager logEventiManager) {
    this.logEventiManager = logEventiManager;
  }
  
  /**
   * Action che effettua il download del file
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward download(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (logger.isDebugEnabled())
      logger.debug("download: inizio metodo");

    String messageKey = null;
    String logMessageKey = null;
    String target = null;
    
    Integer idCodaSched = new Integer(request.getParameter("idCodaSched"));
    
    CodaSched codaSched = null;
    
    try {
      codaSched = schedRicManager.getSchedulazioneEseguita(idCodaSched.intValue());
      String nomeFile = codaSched.getNomeFile();
      fileManager.download(ConfigManager.getValore(CostantiCodaSched.PROP_PATH_FILE),
          nomeFile, response);
    } catch (FileManagerException e) {
      target = DettaglioCodaSchedAction.FORWARD_ERRORE_DOWNLOAD;
      logMessageKey = e.getFamiglia() + "." + e.getCodiceErrore();
      logger.error(this.resBundleGenerale.getString(
          logMessageKey).replaceAll(UtilityStringhe.getPatternParametroMessageBundle(0),
          (String) e.getParametri()[0]), e);
      
      messageKey = "errors.codaSched.download";
      if(logMessageKey.equals(FileManagerException.CODICE_ERRORE_FILE_ESISTENTE_NO_ACCESSIBILE) 
         ||
         logMessageKey.equals(FileManagerException.CODICE_ERRORE_FILE_INESISTENTE))
        messageKey += ".noAccessoFile";
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = DettaglioCodaSchedAction.FORWARD_ERRORE_DOWNLOAD;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }
    String sql = logEventiManager.searchLogFromCodOggetto("REPORT_SCHED", ""+codaSched.getIdCodaSched());
    if(sql != null && sql.length()>0){
      LogEvento logEvento = LogEventiUtils.createLogEvento(request);
      logEvento.setLivEvento(1);
      logEvento.setCodEvento("DOWNLOAD_REPORT");
      logEvento.setDescr("Download estrazione report con id= " + codaSched.getIdRicerca() + " eseguito con schedulazione con id= " + idCodaSched);
      logEvento.setErrmsg(sql);
      LogEventiUtils.insertLogEventi(logEvento);
    }
    
    
    if (logger.isDebugEnabled()) logger.debug("download: fine metodo");
    return mapping.findForward(target);
  }

  /**
   * Eliminazione di un documento
   * 
   * @param mapping
   * @param form
   * @param request
   * @param response
   * @return
   * @throws IOException
   * @throws ServletException
   */
  public ActionForward elimina(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 05/09/2006 M.F. Prima Versione
    // ************************************************************

    if (logger.isDebugEnabled()) logger.debug("elimina: inizio metodo");
    // Di default rivisualizza il dettaglio del modello
    String target = DettaglioCodaSchedAction.FORWARD_OK_ELIMINA;

    Integer idCodaSched = new Integer(request.getParameter("idCodaSched"));
    String messageKey = null;
    String logMessageKey = null;
    try {
      CodaSched codaSched = schedRicManager.getSchedulazioneEseguita(idCodaSched.intValue());
      String nomeFile = codaSched.getNomeFile();
      if (nomeFile != null || "".equalsIgnoreCase(nomeFile))
    	  fileManager.delete(ConfigManager.getValore(CostantiCodaSched.PROP_PATH_FILE), nomeFile);
      schedRicManager.deleteSchedulazioneEseguita(idCodaSched.intValue());
    } catch (FileManagerException e) {
      target = DettaglioCodaSchedAction.FORWARD_KO_ELIMINA;
      //Log dell'errore
      logMessageKey = e.getFamiglia() + "." + e.getCodiceErrore();
      logger.error(this.resBundleGenerale.getString(logMessageKey).replaceAll(
        UtilityStringhe.getPatternParametroMessageBundle(0),
        (String) e.getParametri()[0]), e);
      // Messaggio di errore per l'utente: questo messaggio e' diverso da quello scritto nel log
      this.aggiungiMessaggio(request, "errors.codaSched.delete");
    } catch (DataAccessException e) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.database.dataAccessException";
      logger.error(this.resBundleGenerale.getString(messageKey), e);
      this.aggiungiMessaggio(request, messageKey);
    } catch (Throwable t) {
      target = CostantiGeneraliStruts.FORWARD_ERRORE_GENERALE;
      messageKey = "errors.applicazione.inaspettataException";
      logger.error(this.resBundleGenerale.getString(messageKey), t);
      this.aggiungiMessaggio(request, messageKey);
    }
    if (logger.isDebugEnabled()) logger.debug("elimina: fine metodo");
    return mapping.findForward(target);
  }

}
