/*
 * Created on 16/nov/2020
 *
 * Copyright (c) Maggioli S.p.A. - ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.eldasoft.gene.web.struts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityWeb;
import it.maggioli.eldasoft.security.EncryptionConstants;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;


public class ScaricaDocumentiVerificheManager {
	
  /**
  * A constants for buffer size used to read/write data
  */
  private static final int BUFFER_SIZE = 4096;
  
  private TabellatiManager tabellatiManager;

  static Logger logger = Logger.getLogger(ScaricaDocumentiVerificheManager.class);
  
  private SqlManager          sqlManager;

  private FileAllegatoManager fileAllegatoManager;
  
  protected ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);
  
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }
  
  public void setTabellatiManager(TabellatiManager tabellatiManager) {
	    this.tabellatiManager = tabellatiManager;
  }
  
  public void getArchivio(final ActionMapping mapping,
	      final ActionForm form, final HttpServletRequest request,
	      final HttpServletResponse response) throws Exception{

	    response.setHeader("cache-control", "no-cache");
	    response.setContentType("text/text;charset=utf-8");
	    PrintWriter out = response.getWriter();

	    String pathArchivioDocumenti = System.getProperty("java.io.tmpdir");
	    String archivioCreato = request.getParameter("archivioCreato");

	    String nomeArchivio = request.getParameter("path") + ".zip";
	    String allVerifica = request.getParameter("allegatiVerifica");
	    Boolean allegatiVerifica=false;
	    if(allVerifica != null) {
	    	allegatiVerifica=true;
	    }
	    String stringIdddocdg = request.getParameter("idddocdg");
	    String idprg = request.getParameter("idprg");
	    String stringTipoVer = request.getParameter("tipoVer");
	    String uffint = request.getParameter("uffint");
	    String stringTipoVerPre = request.getParameter("tipoVerPre");
	    String nomeCartella = "";
	    Boolean cambioTipo = false;
	    Long tipoVer = null;
	    Long tipoVerPre;
	    
	    
	    if(allegatiVerifica) {
	    	tipoVer = new Long(stringTipoVer);
	    	if (stringTipoVerPre != null && "".equals(stringTipoVerPre)) {
	    		tipoVerPre = new Long(stringTipoVerPre);
	    	}
	    	if (!stringTipoVer.equals(stringTipoVerPre)){
	    		cambioTipo = true;	
	    	}
	    	//tabellato
	    	nomeCartella= tabellatiManager.getDescrTabellato("G_z24", stringTipoVer);
	    	nomeCartella= stringTipoVer + "_" + nomeCartella;
	    	nomeCartella= nomeCartella.replaceAll("/","-");
	    }
	    	

	    logger.debug("ScaricaTuttiDocumentiBustaAction: download documento con identificativo: "
	        + idprg + ", " + stringIdddocdg + "; creazione archivio: " + nomeArchivio);

	    Long iddocdg = new Long(stringIdddocdg);
	    Long idEntita = (Long) this.sqlManager.getObject("select id from documenti_verifiche where iddocdg=?", new Object[] {iddocdg});
	    
	    ZipOutputStream zipOut = null;
	    String nomeFile = (String)sqlManager.getObject("select dignomdoc from w_docdig where idprg = ? and iddocdig = ?", new Object[] {idprg,iddocdg});
	    nomeFile = idprg +iddocdg + "_" + nomeFile;
	    if("false".equals(archivioCreato)){
	      zipOut = new ZipOutputStream(new FileOutputStream(pathArchivioDocumenti + "/" + nomeArchivio));
	      zipOut.setMethod(ZipOutputStream.DEFLATED);
	      zipOut.setLevel(Deflater.DEFAULT_COMPRESSION);
	      this.addFileToArchive(nomeArchivio,pathArchivioDocumenti,idprg,iddocdg,nomeFile,zipOut,nomeCartella,allegatiVerifica,idEntita,uffint,response);
	      zipOut.closeEntry();
	      zipOut.flush();
	      zipOut.close();
	    }else{
	      addFilesToZip(pathArchivioDocumenti + "/" + nomeArchivio,idprg, iddocdg ,nomeFile,cambioTipo,nomeCartella,allegatiVerifica,idEntita,uffint,response);
	    }

	    JSONObject result = new JSONObject();

	    out.print(result);
	    out.flush();

	  }
  
  public void addFileToArchive(String nomeArchivio, String pathArchivioDocumenti, String idprg, Long iddocdig, String nome, ZipOutputStream zipOut, String nomeCartella,Boolean allegatiVerifica,Long idEntita,String uffint,HttpServletResponse response) throws Exception {
	    BlobFile file = this.fileAllegatoManager.getFileAllegato(idprg,iddocdig);
	    byte[] fileDecod = this.decifraAllegato(nome,file, "DOCUMENTI_VERIFICHE",idEntita,uffint,response);
	    OutputStream out = new FileOutputStream(pathArchivioDocumenti + "/" + nomeArchivio);
	    out.write(fileDecod);
	    out.close();
	    //Aggiungo il file allo zip
	    try {
	    	if(allegatiVerifica) {
	    		zipOut.putNextEntry(new ZipEntry(nomeCartella+"/"));
	    		zipOut.putNextEntry(new ZipEntry(nomeCartella+"/"+nome));
	    	}else {
	    		zipOut.putNextEntry(new ZipEntry(nome));
	    	}
	        zipOut.write(fileDecod, 0, fileDecod.length);
	    } catch (ZipException e) {
	        // il file è già presente nello zip .., lo ignoro
	        String logMessageKey;
	        String logMessageError;
	    	logMessageKey = "warnings.art80.export.filedoppio";
	        logMessageError = resBundleGenerale.getString(logMessageKey);
	        logger.warn(logMessageError, e);
	    }
	  }
  
  public byte[] decifraAllegato(String nomeFile, BlobFile fileAllegatoBlob, String entita, Long idEntita, String uffint, HttpServletResponse response)
		    throws IOException, SQLException, GeneralSecurityException {
		    if (logger.isDebugEnabled())
		      logger.debug("decifraAllegato(" + nomeFile + "): inizio metodo");

		      String cfein = (String) this.sqlManager.getObject("select cfein from uffint where codein=?", new Object[] {uffint});
		      String keysess = (String) sqlManager.getObject("select keysess from documenti_verifiche where id = ?", new Object[]{idEntita});
		      byte[] ff = fileAllegatoBlob.getStream();
		      byte[] decodedSessionKey = this.decodeSessionKey(keysess, cfein);
		      //instanzio il decoder
		      Cipher cipher = SymmetricEncryptionUtils.getDecoder(decodedSessionKey, cfein);
		      //decifro il file
		      byte[] ffDecifrato = SymmetricEncryptionUtils.translate(cipher, ff);

		    if (logger.isDebugEnabled())
		      logger.debug("decifraAllegato: fine metodo");
			return ffDecifrato;
		  }
  
	private static byte[] decodeSessionKey(String sessionKey, String username)
			throws GeneralSecurityException, UnsupportedEncodingException {
		byte[] chiave = null;
		if (sessionKey != null) {
			// la chiave simmetrica di sessione viene criptata con una chiave
			// simmetrica AES dipendente dalla login impresa, e codificata in
			// Base64
			byte[] chiaveSessioneCifrataStringa = Base64.decodeBase64(sessionKey);
			byte[] chiaveProvvisoriaDecifratura = SymmetricEncryptionUtils.fill128bitKey(username);
			IvParameterSpec iv = new IvParameterSpec(chiaveProvvisoriaDecifratura);
			SecretKeySpec skeySpec = new SecretKeySpec(chiaveProvvisoriaDecifratura,
					EncryptionConstants.SESSION_KEY_GEN_ALGORITHM);
			Cipher cipher = Cipher.getInstance(EncryptionConstants.SESSION_KEY_ENCRYPTION_TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			chiave = cipher.doFinal(chiaveSessioneCifrataStringa);
		}
		return chiave;
	}

  public void addFilesToZip(String sourcePath, String idprg, Long iddocdig, String nome, Boolean cambioTipo, String nomeCartella, Boolean allegatiVerifica,Long idEntita,String uffint, HttpServletResponse response) throws GestoreException{
	    try{
	        File source = new File(sourcePath);
	        BlobFile file = this.fileAllegatoManager.getFileAllegato(idprg,iddocdig);
	        byte[] fileDecod = this.decifraAllegato(nome,file, "DOCUMENTI_VERIFICHE",idEntita,uffint,response);
	        File tmpZip = File.createTempFile(source.getName(), null);
	        tmpZip.delete();
	        if(!source.renameTo(tmpZip)){
	            throw new Exception("Could not make temp file (" + source.getName() + ")");
	        }
	        byte[] buffer = new byte[4096];
	        ZipInputStream zin = new ZipInputStream(new FileInputStream(tmpZip));
	        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(source));
	        if(allegatiVerifica) {
	        	if(cambioTipo) {
	        		out.putNextEntry(new ZipEntry(nomeCartella+"/"));
	        		out.putNextEntry(new ZipEntry(nomeCartella+"/"+nome));
	        	} else {
	        		out.putNextEntry(new ZipEntry(nomeCartella+"/"+nome));
	        	}
	        }else {
	        	out.putNextEntry(new ZipEntry(nome));
	        }
	        out.write(fileDecod, 0, fileDecod.length);
	        out.closeEntry();

	        for(ZipEntry ze = zin.getNextEntry(); ze != null; ze = zin.getNextEntry()){
	            if(!zipEntryMatch(ze.getName(), file)){
	                out.putNextEntry(ze);
	                for(int read = zin.read(buffer); read > -1; read = zin.read(buffer)){
	                    out.write(buffer, 0, read);
	                }
	                out.closeEntry();
	            }
	        }
	        out.close();
	        zin.close();
	        tmpZip.delete();
	    }catch(Exception e){
	    	throw new GestoreException("Export verifiche Art.80, si e' verificato un errore durante l'aggiunta di un file allo zip  " + e,
	    	          "art80.export.addfile", e);
	    }
	  }
  
  public static String replaceInvalidChar(String filename){
    String res = filename.replaceAll("[\\\\/:\"*?<>| ]", "_");
    return res;
  }

  public boolean zipEntryMatch(String zeName, BlobFile file){
        if((file.getNome()).equals(zeName)){
            return true;
        }
      return false;
  }
  
}
