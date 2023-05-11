/*
 * Created on 15/nov/2019
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.web.struts;

import it.eldasoft.gene.bl.FileManager;
import it.eldasoft.gene.bl.FileManagerException;
import it.eldasoft.gene.commons.web.struts.DispatchActionAjaxLogged;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import it.eldasoft.gene.bl.GeneManager;
import it.eldasoft.gene.bl.SqlManager;

import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;


public class ScaricaTuttiAllegatiVerificheAction  extends DispatchActionAjaxLogged{

  static Logger logger = Logger.getLogger(ScaricaTuttiAllegatiVerificheAction.class);

  private FileManager      fileManager;
  
  private GeneManager      geneManager;
  
  private SqlManager       sqlManager;

  private ScaricaDocumentiVerificheManager      scaricaDocumentiVerificheManager;
  
  private static final String selectW_DOCDIG = "select d.iddocdig, d.idprg, d.dignomdoc, v.tipo_verifica from verifiche v,documenti_verifiche vd,w_docdig d where v.id=vd.id_verifica "
  		+ "and vd.iddocdg=d.iddocdig and v.codimp=? and d.idprg='PG' and (vd.isarchi='2' or vd.isarchi is null) order by v.tipo_verifica";

  private static final String selectNOMIMP = "select nomimp from impr where codimp=?";
  
  public void setFileManager(FileManager fileManager) {
    this.fileManager = fileManager;
  }

  public void setScaricaDocumentiVerificheManager(ScaricaDocumentiVerificheManager scaricaDocumentiVerificheManager) {
    this.scaricaDocumentiVerificheManager = scaricaDocumentiVerificheManager;
  }
  
  public void setGeneManager(GeneManager geneManager) {
	    this.geneManager = geneManager;
  }
  
  public void setSqlManager(SqlManager sqlManager) {
	    this.sqlManager = sqlManager;
}

  public final ActionForward creaArchivio(final ActionMapping mapping,
                  final ActionForm form, final HttpServletRequest request,
                  final HttpServletResponse response) throws Exception {

    if (logger.isDebugEnabled()) {
        logger.debug("ScaricaTuttiAllegatiVerificheAction: inizio metodo");
    }
    this.scaricaDocumentiVerificheManager.getArchivio(mapping, form, request, response);
    return null;
  }

  public ActionForward download(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException, FileManagerException, SQLException {
    if(logger.isDebugEnabled()) logger.debug("ScaricaTuttiAllegatiVerificheAction: inizio download");

    String pathArchivioDocumenti = System.getProperty("java.io.tmpdir");
    String nomeArchivio = request.getParameter("path");

    nomeArchivio = nomeArchivio+ ".zip";
    this.fileManager.download(pathArchivioDocumenti, "/" + nomeArchivio, response);
    this.fileManager.delete(pathArchivioDocumenti, "/" + nomeArchivio);

    if (logger.isDebugEnabled()) {
      logger.debug("ScaricaTuttiAllegatiVerificheAction: fine metodo");
    }

    return mapping.findForward(null);
  }

  public ActionForward cancellaFileTemporanei(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException, FileManagerException, SQLException {
    if(logger.isDebugEnabled()) logger.debug("ScaricaTuttiScaricaTuttiAllegatiVerificheActionnicazioneAction: annulla download");

    String archivioCreato = request.getParameter("archivioCreato");
    if("true".equals(archivioCreato)){
      String pathArchivioDocumenti = System.getProperty("java.io.tmpdir");
      String nomeArchivio = request.getParameter("path");
      nomeArchivio = nomeArchivio+ ".zip";
      this.fileManager.delete(pathArchivioDocumenti, "/" + nomeArchivio);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("ScaricaTuttiAllegatiVerificheAction: fine metodo");
    }

    return mapping.findForward(null);
  }

  public ActionForward getPath(ActionMapping mapping, ActionForm form,
      HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException, FileManagerException, SQLException, ParseException {
    if(logger.isDebugEnabled()) logger.debug("ScaricaTuttiAllegatiVerificheAction: inizio download");
    response.setHeader("cache-control", "no-cache");
    response.setContentType("text/text;charset=utf-8");
    PrintWriter out = response.getWriter();

    String codimp = request.getParameter("codimp");
    String documenti = request.getParameter("documenti");
    String nomimp=(String)this.sqlManager.getObject(selectNOMIMP, new Object[]{codimp});

    String codiceEvento, descr;
    codiceEvento = "ART80_DOWNLOAD_DOC_ZIP";
    descr = "Download documenti verifiche art.80 su file zip (cod.ditta: " + codimp + ")";
    String idprgDoc = null;
    String iddocdg = null;
    String nomeDoc = null;

    String errMsg="";
    if(documenti!=null && !"".equals(documenti)){
      JSONParser parser = new JSONParser();
      org.json.simple.JSONObject jsonDocumenti = (org.json.simple.JSONObject) parser.parse(documenti);
      JSONArray jArray = (JSONArray)jsonDocumenti.get("documenti");
      Iterator<?> it = jArray.iterator();
      org.json.simple.JSONObject documento = null;
      while (it.hasNext()) {
        documento = (org.json.simple.JSONObject)it.next();
        idprgDoc = (String)documento.get("idprg");
        iddocdg = Long.toString((Long)documento.get("iddocdg"));
        nomeDoc = (String)documento.get("nomeDoc");
        errMsg += idprgDoc + "/" + iddocdg + " - " + nomeDoc + "\r\n";
      }
    }

    LogEvento logevento = LogEventiUtils.createLogEvento(request);
    logevento.setLivEvento(1);
    logevento.setOggEvento(codimp);
    logevento.setCodEvento(codiceEvento);
    logevento.setDescr(descr);
    logevento.setErrmsg(errMsg);
    LogEventiUtils.insertLogEventi(logevento);


    JSONObject result = new JSONObject();
    result.put("path", codimp + "_" + nomimp + "_art80");
    out.println(result);
    out.flush();

    return mapping.findForward(null);
  }

  public ActionForward getDocVerifiche(ActionMapping mapping, ActionForm form,
	      HttpServletRequest request, HttpServletResponse response)
	          throws IOException, ServletException, FileManagerException, SQLException, ParseException {
	    if(logger.isDebugEnabled()) logger.debug("ScaricaTuttiAllegatiVerificheAction: inizio getDoc");
	    response.setHeader("cache-control", "no-cache");
	    response.setContentType("text/text;charset=utf-8");
	    PrintWriter out = response.getWriter();

	    String codimp = request.getParameter("codimp");
	    List<?> listaDoc;
	    
	    //select che estrae tutti i record selectW_DOCDIG
	    try {
	        listaDoc = this.geneManager.getSql().getListVector(selectW_DOCDIG, new String[] {codimp});
	      } catch (SQLException e) {
	            // non si dovrebbe verificare mai...
	            logger.error(
	                this.resBundleGenerale.getString("errors.database.dataAccessException"),
	                e);
	            throw new RuntimeException(e.getMessage());
	    }

	    JSONObject result = new JSONObject();
	    result.put("listaDoc", listaDoc);
	    result.put("maxCount", listaDoc.size());
	    out.println(result);
	    out.flush();

	    return mapping.findForward(null);
	  }

}
