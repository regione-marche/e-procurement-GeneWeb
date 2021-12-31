/*
 * Created on 15/ott/09
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.web.struts;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DownloadAction;

/**
 * Action per il download del file excel prodotto per l'export della lista delle
 * lavorazioni e forniture presente nella cartella temp dell'application server
 * 
 * @author Luca.Giacomazzo
 */
public class DownloadTempFileAction extends DownloadAction {

	static Logger logger = Logger.getLogger(DownloadTempFileAction.class);
	
	protected StreamInfo getStreamInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	
		if(logger.isDebugEnabled()) logger.debug("getStreamInfo: inizio metodo");
		String nomeFile = request.getParameter("nomeTempFile");
		if(nomeFile == null)
			nomeFile = (String) request.getAttribute("nomeTempFile");
		
		if(nomeFile != null && nomeFile.length() > 0){
			File tempExcelFile = new File(System.getProperty("java.io.tmpdir") +
					File.separator + nomeFile);

			response.setHeader("Content-Disposition", "attachment;filename=\"" +
					nomeFile + "\"");

			if(logger.isDebugEnabled()) logger.debug("getStreamInfo: fine metodo");
			
			return new FileStreamInfo("application/octet-stream", tempExcelFile );
		} else {
			logger.error("Il parameter nomeTempFile non e' presente nel request");
			return null;
		}
	}

}