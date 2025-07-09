/*
 * Created on 02/03/2023
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.web.struts;

import java.io.InputStream;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.json.simple.JSONObject;

import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.utils.pdfa.PdfAUtils;
import it.eldasoft.utils.sign.DigitalSignatureChecker;


public class CheckFormatoFilePDFaAction extends Action {
  
  Logger             logger = Logger.getLogger(CheckFormatoFilePDFaAction.class);
  
  private static final String DOCUMENTO_MARCATO           = "tsd";
  private static final String DOCUMENTO_FIRMATO           = "p7m";

  public final ActionForward execute(final ActionMapping mapping, final ActionForm form,
      final HttpServletRequest request, final HttpServletResponse response) {
    
    if (logger.isDebugEnabled()) {
      logger.debug("CheckFormatoFilePDFaAction: inizio metodo");
    }

    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    
    JSONObject resp = new JSONObject();
    boolean controlliOk = true;
    FormFile file = null;
    PrintWriter out = null;
    InputStream inputStream = null;
    try {
        out = response.getWriter();
  
        UploadFileForm uploadForm = (UploadFileForm) form;
        file = (FormFile) uploadForm.getMultipartRequestHandler().getFileElements().get("file");
      
        String nomeFile = file.getFileName();
        
        String[] fileNameSplit = nomeFile.split("\\.");
        String ext = fileNameSplit[fileNameSplit.length - 1].toLowerCase();

        byte[] doc = null;
        DigitalSignatureChecker digitalSignatureChecker = new DigitalSignatureChecker();
        boolean fileOriginale = false;
        while(!fileOriginale){
          if(doc == null){doc = file.getFileData();}
          try{
            if (DOCUMENTO_MARCATO.equals(ext)) {
              byte[] innerDoc = digitalSignatureChecker.getContentTimeStamp(doc);
              if (DOCUMENTO_FIRMATO.equals(fileNameSplit[fileNameSplit.length - 2])) {
                doc = digitalSignatureChecker.getContent(innerDoc);
              } else {
                // caso di file non firmato ma solamente marcato temporalmente
                doc = innerDoc;
              }

            } else {
              doc = digitalSignatureChecker.getContent(doc);
            }
          }catch(Exception e){
            fileOriginale = true;
          }
          if (nomeFile.toLowerCase().indexOf(".p7m") >= 0) {
            nomeFile = nomeFile.substring(0, nomeFile.toLowerCase().lastIndexOf(".p7m"));
          } else {
            if (nomeFile.toLowerCase().indexOf(".tsd") >= 0) {
              nomeFile = nomeFile.substring(0, nomeFile.toLowerCase().lastIndexOf(".tsd"));
            }
          }
        }
      
        
        // validazione del PDF/A e PDF/UA...
        controlliOk = false;
        controlliOk = PdfAUtils.checkIsPdfACompliant(doc, nomeFile);
        
            
    } catch (Exception t) {
        logger.error("checkDocumentPdf", t);
    } finally {
        if(inputStream != null) 
            try{ inputStream.close(); } catch(Exception e) {}
    }
    
    resp.put("esito", Boolean.TRUE.equals(controlliOk)?"ok":"ko");
    out.print(resp);
    out.flush();
    
    return null;
    
  }
}
