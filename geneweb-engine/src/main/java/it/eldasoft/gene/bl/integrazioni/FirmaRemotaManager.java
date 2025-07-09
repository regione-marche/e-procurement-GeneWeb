/*
 * Created on 25/09/2018
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.integrazioni;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.integrazioni.codifiche.CodiciErroriFirmaRemotaRAI;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.gene.ws.objectstorage.ObjectStorageUtils;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityDate;
import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.apache.axis.encoding.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Gestione firma remota, firma remota automatica e verifica remota di un
 * documento firmato.
 * 
 * @author Stefano.Cestaro
 * 
 */
public class FirmaRemotaManager {

  static Logger               logger                       = Logger.getLogger(FirmaRemotaManager.class);

  private static final String PROP_PROVIDER                = "digital-signature-provider";
  private static final String PROP_AUTO_URL                = "digital-signature-auto-url";
  private static final String PROP_REMOTE_URL              = "digital-signature-remote-url";
  private static final String PROP_VERIFY_URL              = "digital-signature-check-url";
  private static final String PROP_CLIENT_ID               = "digital-signature-client-id";

  private static final String PROVIDER_INFOCERT            = "2";
  private static final String PROVIDER_COSIGN              = "3";

  private static final String MODALITA_REMOTA              = "remota";
  private static final String MODALITA_AUTOMATICA          = "automatica";

  private static final String FORMATO_CADES                = "cades";
  private static final String FORMATO_PADES                = "pades";
  private static final String FORMATO_CADES_T              = "cades-t";
  private static final String FORMATO_PADES_T              = "pades-t";

  private static final String EXT_PDF                      = "pdf";
  private static final String EXT_P7M                      = "p7m";

  private SqlManager          sqlManager;

  private FileAllegatoManager fileAllegatoManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }

  /**
   * Firma un documento.
   * 
   * @param mode
   *        Modalita':
   *        <ul>
   *        <li>remota</li>
   *        <li>automatica</li>
   *        </ul>
   * @param format
   *        Formato:
   *        <ul>
   *        <li>cades</li>
   *        <li>pades</li>
   *        <li>cades-t</li>
   *        <li>pades-t</li>
   *        </ul>
   * @param alias
   *        Alias del certificato.
   * @param pin
   *        PIN di accesso per la firma.
   * @param otp
   *        One Time Password per firma remota (non automatica).
   * @param bfToSigned
   *        Contenuto del file da firmare.
   * @return
   * @throws GestoreException
   * @throws SQLException 
   */
  public byte[] sign(String idprg, Long iddocdig, String mode, String format, String alias, String pin, String otp, BlobFile bfToSigned) throws GestoreException, SQLException {

    byte[] signedContent = null;

    String provider = ConfigManager.getValore(PROP_PROVIDER);
    if (PROVIDER_INFOCERT.equals(provider.toLowerCase())) {
      signedContent = this.__signInfocert(idprg, iddocdig, mode, format, alias, pin, otp, bfToSigned);
    } else if (PROVIDER_COSIGN.equals(provider.toLowerCase())) {
      signedContent = this.__signCosign(idprg, iddocdig, mode, format, alias, pin, otp, bfToSigned);
    } else {
      throw new GestoreException("Firma remota: non e' stato indicato il provider o il provider indicato non e' supportato",
          "firmaremota.noprovider", null);
    }
    
    if(signedContent != null) {
      java.sql.Timestamp dataOraCorrente = new java.sql.Timestamp(UtilityDate.getDataOdiernaAsDate().getTime());
      String queryUpdate = "update w_docdig set firmacheck = ?,firmacheckts = ? where idprg = ? and iddocdig = ? and (firmacheck is null or firmacheck = ?)";
      sqlManager.update(queryUpdate,new Object[]{ "1", dataOraCorrente, idprg, iddocdig, "2"});
    }

    return signedContent;

  }

  /**
   * Verifica un documento firmato.
   * 
   * @param bfToVerify
   *        Contenuto del file da verificare.
   * @return
   * @throws GestoreException
   */
  public String verify(BlobFile bfToVerify) throws GestoreException {

    String report = null;

    String provider = ConfigManager.getValore(PROP_PROVIDER);
    if (PROVIDER_INFOCERT.equals(provider.toLowerCase())) {
      report = __verifyInfocert(bfToVerify);
    } else {
      throw new GestoreException("Firma remota: non e' stato indicato il provider o il provider indicato non e' supportato",
          "firmaremota.noprovider", null);
    }

    return report;
  }

  /**
   * Estrae il documento originale da un documento firmato.
   * 
   * @param bfSigned
   *        Contenuto del file firmato da cui estrarre il file originale.
   * @return
   * @throws GestoreException
   */
  public BlobFile extract(BlobFile bfSigned) throws GestoreException {

    BlobFile bfOriginal = null;

    String provider = ConfigManager.getValore(PROP_PROVIDER);
    if (PROVIDER_INFOCERT.equals(provider.toLowerCase())) {
      bfOriginal = __extractInfocert(bfSigned);
    } else {
      throw new GestoreException("Firma remota: non e' stato indicato il provider o il provider indicato non e' supportato",
          "firmaremota.noprovider", null);
    }

    return bfOriginal;
  }

  /**
   * Verifica un documento firmato.
   * 
   * @param idprg
   *        Valore del campo W_DOCDIG.IDPRG.
   * @param iddocdig
   *        Valore del campo W_DOCDIG.IDDOCDIG.
   * @return
   * @throws GestoreException
   */
  public String verify(String idprg, Long iddocdig) throws GestoreException {

    String report = null;

    try {

      BlobFile bfToVerify = fileAllegatoManager.getFileAllegato(idprg, iddocdig);
      report = this.verify(bfToVerify);

    } catch (IOException e) {
      throw new GestoreException("Firma remota, si e' verificato un errore durante l'interazione con i servizi: " + e.getMessage(),
          "firmaremota.ws.remote.error", e);
    }

    return report;
  }

  /**
   * Estrae il documento originale da un documento firmato.
   * 
   * @param idprg
   *        Valore del campo W_DOCDIG.IDPRG.
   * @param iddocdig
   *        Valore del campo W_DOCDIG.IDDOCDIG.
   * @return
   * @throws GestoreException
   */
  public BlobFile extract(String idprg, Long iddocdig) throws GestoreException {

    BlobFile bfOriginal = null;

    try {

      BlobFile bfSigned = fileAllegatoManager.getFileAllegato(idprg, iddocdig);
      bfOriginal = this.extract(bfSigned);

    } catch (IOException e) {
      throw new GestoreException("Firma remota, si e' verificato un errore durante l'interazione con i servizi: " + e.getMessage(),
          "firmaremota.ws.remote.error", e);
    }

    return bfOriginal;
  }

  /**
   * INFOCERT: verifica di un documento firmato.
   * 
   * @param bfToVerify
   *        Contenuto del file da verificare.
   * @return
   * @throws GestoreException
   */
  private String __verifyInfocert(BlobFile bfToVerify) throws GestoreException {

    String report = null;
    HttpURLConnection conn = null;
    int responseCode = 0;

    try {

      String url = ConfigManager.getValore(PROP_VERIFY_URL);
      if (url == null || (url != null && "".equals(url.trim()))) {
        throw new GestoreException("Firma remota: non e' definito l'indirizzo del servizio di verifica INFOCERT",
            "firmaremota.infocert.noverifyurl", null);
      }

      if (!"/".equals(url.substring(url.length() - 1))) url += "/";
      conn = (HttpURLConnection) new URL(url).openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");

      MultipartEntityBuilder mp = MultipartEntityBuilder.create();
      mp.setMode(HttpMultipartMode.STRICT);
      mp.addTextBody("language", "it");

      ByteArrayBody contentToVerifyBody = new ByteArrayBody(bfToVerify.getStream(), bfToVerify.getNome());
      mp.addPart("contentToVerify", contentToVerifyBody);

      HttpEntity httpEntity = mp.build();

      conn.setRequestProperty("Content-Type", httpEntity.getContentType().getValue());
      OutputStream os = conn.getOutputStream();
      httpEntity.writeTo(os);
      os.flush();

      responseCode = conn.getResponseCode();
      conn.getResponseMessage();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        report = org.apache.commons.io.IOUtils.toString(br);
      } else {

        String errorMessage = "";

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
        String outputErrorStream = org.apache.commons.io.IOUtils.toString(br);

        try {
          Document documentErrorStream = DocumentHelper.parseText(outputErrorStream);
          Element elRoot = documentErrorStream.getRootElement();
          if (elRoot != null) {
            Element elError = elRoot.element("error");
            if (elError != null) {
              String errorCode = elError.element("error-code").getText();
              String errorDescription = elError.element("error-description").getText();

              if (errorCode != null) errorMessage = errorCode;
              if (errorDescription != null) errorMessage += " - " + errorDescription;
            } else {
              errorMessage = conn.getResponseMessage();
            }
          } else {
            errorMessage = conn.getResponseMessage();
          }
        } catch (DocumentException e) {
          errorMessage = outputErrorStream;
        }

        throw new GestoreException("Firma remota, si e' verificato un errore durante l'interazione con i servizi: " + errorMessage,
            "firmaremota.ws.remote.error", null);
      }

    } catch (MalformedURLException e) {
      throw new GestoreException("Firma remota, si e' verificato un errore durante l'interazione con i servizi: " + e.getMessage(),
          "firmaremota.ws.remote.error", e);
    } catch (IOException e) {
      throw new GestoreException("Firma remota, si e' verificato un errore durante l'interazione con i servizi: " + e.getMessage(),
          "firmaremota.ws.remote.error", e);
    } finally {
      if (conn != null) conn.disconnect();
    }

    return report;

  }

  /**
   * INFOCERT: estrae il documento originale da un documento firmato.
   * 
   * @param bfSigned
   *        Contenuto del file firmato da cui estrarre il file originale.
   * @return
   * @throws GestoreException
   */
  private BlobFile __extractInfocert(BlobFile bfSigned) throws GestoreException {

    BlobFile bfOriginal = new BlobFile();
    HttpURLConnection conn = null;
    int responseCode = 0;

    try {

      __testExtractP7M(bfSigned.getNome());

      String url = ConfigManager.getValore(PROP_VERIFY_URL);
      if (url == null || (url != null && "".equals(url.trim()))) {
        throw new GestoreException("Firma remota: non e' definito l'indirizzo del servizio di verifica INFOCERT",
            "firmaremota.infocert.noverifyurl", null);
      }
      if (!"/".equals(url.substring(url.length() - 1))) url += "/";
      url += "extract";
      conn = (HttpURLConnection) new URL(url).openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");

      MultipartEntityBuilder mp = MultipartEntityBuilder.create();
      mp.setMode(HttpMultipartMode.STRICT);
      mp.addTextBody("language", "it");

      ByteArrayBody contentToVerifyBody = new ByteArrayBody(bfSigned.getStream(), bfSigned.getNome());
      mp.addPart("contentToVerify", contentToVerifyBody);

      HttpEntity httpEntity = mp.build();

      conn.setRequestProperty("Content-Type", httpEntity.getContentType().getValue());
      OutputStream os = conn.getOutputStream();
      httpEntity.writeTo(os);
      os.flush();

      responseCode = conn.getResponseCode();
      conn.getResponseMessage();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        String nameOriginal = bfSigned.getNome().replaceAll(".p7m", "");
        byte[] contentOriginal = org.apache.commons.io.IOUtils.toByteArray(conn.getInputStream());
        bfOriginal.setNome(nameOriginal);
        bfOriginal.setStream(contentOriginal);
      } else {

        String errorMessage = "";

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
        String outputErrorStream = org.apache.commons.io.IOUtils.toString(br);

        try {
          Document documentErrorStream = DocumentHelper.parseText(outputErrorStream);
          Element elRoot = documentErrorStream.getRootElement();
          if (elRoot != null) {
            Element elError = elRoot.element("error");
            if (elError != null) {
              String errorCode = elError.element("error-code").getText();
              String errorDescription = elError.element("error-description").getText();
              String proxysignErrorCode = elError.element("proxysign-error-code").getText();
              String proxysignErrorDescription = elError.element("proxysign-error-description").getText();
              if (errorCode != null) errorMessage = errorCode;
              if (errorDescription != null) errorMessage += " - " + errorDescription;
              if (proxysignErrorCode != null) errorMessage += " - " + proxysignErrorCode;
              if (proxysignErrorDescription != null) errorMessage += " - " + proxysignErrorDescription;
            } else {
              errorMessage = conn.getResponseMessage();
            }
          } else {
            errorMessage = conn.getResponseMessage();
          }
        } catch (DocumentException e) {
          errorMessage = outputErrorStream;
        }

        throw new GestoreException("Firma remota, si e' verificato un errore durante l'interazione con i servizi: " + errorMessage,
            "firmaremota.ws.remote.error", null);
      }

    } catch (MalformedURLException e) {
      throw new GestoreException("Firma remota, si e' verificato un errore durante l'interazione con i servizi: " + e.getMessage(),
          "firmaremota.ws.remote.error", e);
    } catch (IOException e) {
      throw new GestoreException("Firma remota, si e' verificato un errore durante l'interazione con i servizi: " + e.getMessage(),
          "firmaremota.ws.remote.error", e);
    } finally {
      if (conn != null) conn.disconnect();
    }

    return bfOriginal;

  }

  /**
   * Verifica se il file firmato e' un file in formato P7M. Solo in questo caso
   * e' possibile procedere all'estrazione del contenuto.
   * 
   * @param nameSigned
   * @throws GestoreException
   */
  private void __testExtractP7M(String nameSigned) throws GestoreException {
    String ext = FilenameUtils.getExtension(nameSigned);
    if (ext == null || (ext != null && !EXT_P7M.equals(ext.toLowerCase()))) {
      throw new GestoreException("Firma remota: file di input con formato non conosciuto (e' possibile estrarre il contenuto solo da P7M)",
          "firmaremota.nop7m", null);
    }
  }

  /**
   * Firma un documento.
   * 
   * @param idprg
   *        Valore del campo W_DOCDIG.IDPRG.
   * @param iddocdig
   *        Valore del campo W_DOCDIG.IDDOCDIG.
   * @param mode
   *        Modalita':
   *        <ul>
   *        <li>remota</li>
   *        <li>automatica</li>
   *        </ul>
   * @param format
   *        Formato:
   *        <ul>
   *        <li>cades</li>
   *        <li>pades</li>
   *        <li>cades-t</li>
   *        <li>pades-t</li>
   *        </ul>
   * @param alias
   *        Alias del certificato.
   * @param pin
   *        PIN di accesso per la firma.
   * @param otp
   *        One Time Password per firma remota (non automatica).
   * @throws GestoreException
   */
  public void sign(String idprg, Long iddocdig, String mode, String format, String alias, String pin, String otp, String codiceProfilo, Integer idUtente, String ip) throws GestoreException {

    int livEvento = 1;
    String errMsgEvento = "";
    String providerMessage = "";
    switch (ConfigManager.getValore(PROP_PROVIDER)) {
    case "2":
      providerMessage = "INFOCERT";
      break;
    case "3":
      providerMessage = "COSIGN";
      break;
    default:
      break;
    }
    try {
      BlobFile bfToSign = fileAllegatoManager.getFileAllegato(idprg, iddocdig);
      byte[] signedContent = this.sign(idprg, iddocdig, mode, format, alias, pin, otp, bfToSign);

      DataColumnContainer dccW_DOCDIG = new DataColumnContainer(this.sqlManager, "W_DOCDIG",
          "select idprg, iddocdig from w_docdig where idprg = ? and iddocdig = ?", new Object[] { idprg, iddocdig });
      dccW_DOCDIG.getColumn("W_DOCDIG.IDPRG").setChiave(true);
      dccW_DOCDIG.getColumn("W_DOCDIG.IDDOCDIG").setChiave(true);

      if (FORMATO_CADES.equals(format.toLowerCase()) || FORMATO_CADES_T.equals(format.toLowerCase())) {
        dccW_DOCDIG.addColumn("W_DOCDIG.DIGNOMDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, bfToSign.getNome() + ".p7m"));
      }

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      baos.write(signedContent);
      dccW_DOCDIG.addColumn("W_DOCDIG.DIGOGG", new JdbcParametro(JdbcParametro.TIPO_BINARIO, baos));
      dccW_DOCDIG.update("W_DOCDIG", this.sqlManager);
      ObjectStorageUtils.addFile(idprg,iddocdig);
    } catch (Exception e) {
      livEvento = 3;
      if(e.getMessage().contains("ErroreServizioEsternoGestitoCosign:")) {
        errMsgEvento = e.getMessage().replace("ErroreServizioEsternoGestitoCosign:", "");
        throw new GestoreException(e.getMessage(), "firmaremota.ws.remote.error", null);
      } else {
        errMsgEvento = e.getMessage();
        throw new GestoreException("Firma remota, si e' verificato un errore durante l'interazione con i servizi: " + e.getMessage(),
          "firmaremota.ws.remote.error", e);
      }
    } finally {
      try{
          LogEvento logEvento =  new LogEvento();
          logEvento.setCodApplicazione("PG");
          logEvento.setLivEvento(livEvento);
          String oggetto = idprg;
          if(oggetto!=null)
            oggetto+="/" + iddocdig;
          logEvento.setOggEvento(oggetto);
          logEvento.setCodEvento("FIRMA_FILE");
          logEvento.setDescr("Firma file con servizi "+providerMessage);
          logEvento.setErrmsg(errMsgEvento);
          logEvento.setCodProfilo(codiceProfilo);
          logEvento.setIdUtente(idUtente);
          logEvento.setIp(ip);
          LogEventiUtils.insertLogEventi(logEvento);
      }catch(Exception e){
          logger.error("Errore inaspettato durante la tracciatura su w_logeventi");
      }
    }
  }
  
  

  /**
   * COSIGN: firma remota automatica.
   * 
   * @param mode
   * @param format
   * @param alias
   * @param pin
   * @param otp
   * @param bfToSign
   * @return 
   * @throws GestoreException
   */
  private byte[] __signCosign(String idprg, Long iddocdig, String mode, String format, String alias, String pin, String otp, BlobFile bfToSign)
      throws GestoreException {
    String clientId = null;
    byte[] signedContent = null;
    try {
      clientId = ConfigManager.getValore(PROP_CLIENT_ID);
      
      __testClientId(clientId);
      __testMode(mode);
      __testFormat(format);
      __testAlias(alias);
      __testPIN(pin);
      __testOTP(mode, otp);
      __testNameToSign(format, bfToSign.getNome());
  
      String urlStr = getBaseUrlCosign(mode);
      if (!"/".equals(urlStr.substring(urlStr.length() - 1))) urlStr += "/";
      urlStr += "signature/signP7M";
      
      HttpURLConnection connection = null;
      URL url = new URL(urlStr);
      
      connection = (HttpURLConnection) url.openConnection();
      connection.setDoInput(true);
      connection.setDoOutput(true);
      connection.setUseCaches(false);
      connection.setRequestMethod("POST");
      connection.setRequestProperty("x-ibm-client-id", clientId);
      connection.setRequestProperty("Connection", "Keep-Alive");
      connection.setRequestProperty("Content-Type", "text/plain");
      String encode = Base64.encode(bfToSign.getStream());
      connection.setRequestProperty("Content-Length", String.valueOf(encode.length()));
      connection.setRequestProperty("Host", url.getHost());
      
      JSONObject input = new JSONObject();
      JSONObject accountCredentials = new JSONObject();
      accountCredentials.put("userid", alias);
      accountCredentials.put("password", pin);
      accountCredentials.put("signaturePassword", otp);
      accountCredentials.put("activationPassword", "");
      accountCredentials.put("oneshot", 0);
      
      JSONObject signatureDataP7M = new JSONObject();
      signatureDataP7M.put("content", encode);//.substring(0, 8192)
      signatureDataP7M.put("digestType", "SHA256");
      signatureDataP7M.put("X509certificate", null);
      signatureDataP7M.put("timestamp", true);
      signatureDataP7M.put("ltv", false);
      signatureDataP7M.put("p7m", false);
      signatureDataP7M.put("counterSignaturePath", "");
      
      input.put("AccountCredentialsExtended", accountCredentials);
      input.put("SignatureDataP7M", signatureDataP7M);
      
      DataOutputStream output = new DataOutputStream(connection.getOutputStream());
      output.write(input.toString().getBytes());
      output.flush();
      
      int httpStatus = connection.getResponseCode();

      String res = getInputStream(connection);
      JSONObject jsonOutput = JSONObject.fromObject(res);
      
      if (httpStatus == 200) {
        if (jsonOutput.getBoolean("success")) {
          signedContent = Base64.decode(jsonOutput.getString("signature"));
          this.sqlManager.update("update w_docdig set digfirma=null where idprg = ? and iddocdig = ?", new Object[] {idprg, iddocdig });
          sbiancaAttributiVerificaFirma(idprg, iddocdig);
        } else {
          recuperoErroreDaResponseCosign(jsonOutput);
        }
      } else {
        recuperoErroreDaResponseCosign(jsonOutput);
      }
      
    } catch (Exception e) {
      if(e.getMessage().contains("ErroreServizioEsternoGestitoCosign:")) {
        throw new GestoreException(e.getMessage(), "firmaremota.ws.remote.error", null);
      } else {
        throw new GestoreException("Firma remota Cosign, si e' verificato un errore durante l'interazione con i servizi: " + e.getMessage(),
          "firmaremota.ws.remote.error", e);
      }
    }
    return signedContent;

  }
  
  private String getInputStream(HttpURLConnection connection) throws IOException {
    InputStream isR = connection.getInputStream();
    String res = "";
    int countByte = 0;
    byte[] buffer = new byte[1024];
    while ((countByte = isR.read(buffer)) != -1) {
      byte[] tmpByte = new byte[countByte];
      for (int i = 0; i < countByte; i++)
        tmpByte[i] = buffer[i];
      String s = new String(tmpByte);
      buffer = new byte[1024];
      res += s;
    }
    res = res.trim();
    return res;
  }

  /**
   * COSIGN: Apertura sessione per firma remota automatica.
   * 
   * @param mode
   * @param format
   * @param alias
   * @param pin
   * @param otp
   * @param clientId
   * @return tokenSessione
   * @throws GestoreException
   */
  public String openSignatureSession(String mode, String alias, String pin, String otp, String clientId) throws GestoreException {
    String tokenSessione = "";
    try {
      String url = getBaseUrlCosign(mode);
      if (!"/".equals(url.substring(url.length() - 1))) url += "/";
      url += "authentication/openSignatureSession";
      
      Client client = ClientBuilder.newClient();
      WebTarget webTarget = client.target(url);
      
      JSONObject input = new JSONObject();
      JSONObject accountCredentials = new JSONObject();
      accountCredentials.put("userid", alias);
      accountCredentials.put("password", pin);
      accountCredentials.put("signaturePassword", otp);
      accountCredentials.put("activationPassword", "");
      accountCredentials.put("oneshot", 0);
      
      input.put("AccountCredentials", accountCredentials);
      
      JSONObject response = webTarget.request(MediaType.APPLICATION_JSON).header("x-ibm-client-id", clientId).post(Entity.json(input),JSONObject.class);
      
      if (response.getBoolean("success")) {
        tokenSessione = response.getString("sessionId");
      } else {
        recuperoErroreDaResponseCosign(response);
      }
    } catch (Exception e) {
      if(e.getMessage().contains("ErroreServizioEsternoGestitoCosign:")) {
        throw new GestoreException(e.getMessage(), "firmaremota.ws.remote.error", null);
      } else {
        throw new GestoreException("Firma remota Cosign, si e' verificato un errore durante l'interazione con i servizi: " + e.getMessage(),
          "firmaremota.ws.remote.error", e);
      }
    }
    return tokenSessione;      
    
  }
  
  /**
   * COSIGN: Chiusura sessione per firma remota automatica.
   * 
   * @param mode
   * @param format
   * @param alias
   * @param pin
   * @param otp
   * @param clientId
   * @return tokenSessione
   * @throws GestoreException
   */
  public void closeSignatureSession(String mode, String alias, String pin, String otp, String clientId, String sessionId) throws GestoreException {
    try {
      String url = getBaseUrlCosign(mode);
      if (!"/".equals(url.substring(url.length() - 1))) url += "/";
      url += "authentication/closeSignatureSession";
      
      Client client = ClientBuilder.newClient();
      WebTarget webTarget = client.target(url);
      
      JSONObject input = new JSONObject();
      input.put("userid", alias);
      input.put("sessionId", sessionId);
      
      JSONObject response = webTarget.request(MediaType.APPLICATION_JSON).header("x-ibm-client-id", clientId).post(Entity.json(input),JSONObject.class);
      
      if (response.getBoolean("success")) {
        ;
      } else {
        recuperoErroreDaResponseCosign(response);
      }
    } catch (Exception e) {
      if(e.getMessage().contains("ErroreServizioEsternoGestitoCosign:")) {
        throw new GestoreException(e.getMessage(), "firmaremota.ws.remote.error", null);
      } else {
        throw new GestoreException("Firma remota Cosign, si e' verificato un errore durante l'interazione con i servizi: " + e.getMessage(),
          "firmaremota.ws.remote.error", e);
      }
    }     
    
  }
  
  /**
   * COSIGN: Recupero base url per firma remota automatica.
   * 
   * @param mode
   * @return url
   * @throws GestoreException
   */
  private String getBaseUrlCosign(String mode) throws GestoreException {
    String url = "";
    if (MODALITA_REMOTA.equals(mode.toLowerCase())) {
      url = ConfigManager.getValore(PROP_REMOTE_URL);
      if (url == null || (url != null && "".equals(url.trim()))) {
        throw new GestoreException("Firma remota: non e' definito l'indirizzo del servizio di firma remota",
            "firmaremota.cosign.noremoteurl", null);
      }
    } else if (MODALITA_AUTOMATICA.equals(mode.toLowerCase())) {
      url = ConfigManager.getValore(PROP_AUTO_URL);
      if (url == null || (url != null && "".equals(url.trim()))) {
        throw new GestoreException("Firma remota: non e' definito l'indirizzo del servizio di firma remota automatica",
            "firmaremota.cosign.noautourl", null);
      }
    }
    return url;
  }
  
  /**
   * COSIGN: Recupero errore dalla response del servizio esposto di firma remota automatica.
   * 
   * @param response
   * @throws GestoreException
   */
  private void recuperoErroreDaResponseCosign(JSONObject response) throws GestoreException {
    String errorMessage = "";
    if(response.has("httpCode") && response.has("httpMessage")) {
      errorMessage = response.getString("httpCode")+" - "+response.getString("httpMessage");
      if(response.has("moreInformation")) {
        errorMessage += "\nDetail error: "+response.getString("moreInformation");
      }
    } else if(response.has("errorCode")) {
      errorMessage = response.getString("errorCode");
    }
    throw new GestoreException("ErroreServizioEsternoGestitoCosign: errore " + errorMessage + " - " + CodiciErroriFirmaRemotaRAI.getErrorMessage(errorMessage), "firmaremota.ws.remote.error", null);
  }


  /**
   * INFOCERT: firma remota automatica.
   * 
   * @param mode
   * @param format
   * @param alias
   * @param pin
   * @param otp
   * @param bfToSign
   * @return
   * @throws GestoreException
   */
  private byte[] __signInfocert(String idprg, Long iddocdig, String mode, String format, String alias, String pin, String otp, BlobFile bfToSign)
      throws GestoreException {

    byte[] signedContent = null;
    HttpURLConnection conn = null;
    int responseCode = 0;

    try {

      __testMode(mode);
      __testFormat(format);
      __testAlias(alias);
      __testPIN(pin);
      __testOTP(mode, otp);
      __testNameToSign(format, bfToSign.getNome());

      String url = "";
      if (MODALITA_REMOTA.equals(mode.toLowerCase())) {
        url = ConfigManager.getValore(PROP_REMOTE_URL);
        if (url == null || (url != null && "".equals(url.trim()))) {
          throw new GestoreException("Firma remota: non e' definito l'indirizzo del servizio di firma remota",
              "firmaremota.infocert.noremoteurl", null);
        }
      } else if (MODALITA_AUTOMATICA.equals(mode.toLowerCase())) {
        url = ConfigManager.getValore(PROP_AUTO_URL);
        if (url == null || (url != null && "".equals(url.trim()))) {
          throw new GestoreException("Firma remota: non e' definito l'indirizzo del servizio di firma remota automatica",
              "firmaremota.infocert.noautourl", null);
        }
      }
      if (!"/".equals(url.substring(url.length() - 1))) url += "/";
      url += "sign/" + format.toLowerCase() + "/" + alias;
      conn = (HttpURLConnection) new URL(url).openConnection(); 
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");

      MultipartEntityBuilder mp = MultipartEntityBuilder.create();
      mp.setMode(HttpMultipartMode.STRICT);
      mp.addTextBody("pin", pin);
      if (MODALITA_REMOTA.equals(mode.toLowerCase())) {
        mp.addTextBody("otp", otp);
        mp.addTextBody("filename", bfToSign.getNome());
      }
      ByteArrayBody contentToSignBody = new ByteArrayBody(bfToSign.getStream(), bfToSign.getNome());
      if (MODALITA_REMOTA.equals(mode.toLowerCase())) {
        mp.addPart("contentToSign-1", contentToSignBody);
      } else {
        mp.addPart("contentToSign-0", contentToSignBody);
      }

      HttpEntity httpEntity = mp.build();

      conn.setRequestProperty("Content-Type", httpEntity.getContentType().getValue());
      OutputStream os = conn.getOutputStream();
      httpEntity.writeTo(os);
      os.flush();

      responseCode = conn.getResponseCode();
      conn.getResponseMessage();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        signedContent = org.apache.commons.io.IOUtils.toByteArray(conn.getInputStream());
        sbiancaAttributiVerificaFirma(idprg, iddocdig);
      } else {

        String errorMessage = "";

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
        String outputErrorStream = org.apache.commons.io.IOUtils.toString(br);

        try {
          Document documentErrorStream = DocumentHelper.parseText(outputErrorStream);
          Element elRoot = documentErrorStream.getRootElement();
          if (elRoot != null) {
            Element elError = elRoot.element("error");
            if (elError != null) {
              String errorCode = elError.element("error-code").getText();
              String errorDescription = elError.element("error-description").getText();
              String errorCodeSignature = elError.element("error-code-signature").getText();
              String proxysignErrorCode = elError.element("proxysign-error-code").getText();
              String proxysignErrorDescription = elError.element("proxysign-error-description").getText();

              if (errorCode != null) errorMessage = errorCode;
              if (errorDescription != null) errorMessage += " - " + errorDescription;
              if (errorCodeSignature != null) errorMessage += ", " + errorCodeSignature;
              if (proxysignErrorCode != null) errorMessage += " - " + proxysignErrorCode;
              if (proxysignErrorDescription != null) errorMessage += " - " + proxysignErrorDescription;
            } else {
              errorMessage = conn.getResponseMessage();
            }
          } else {
            errorMessage = conn.getResponseMessage();
          }
        } catch (DocumentException e) {
          errorMessage = outputErrorStream;
        }

        throw new GestoreException("Firma remota, si e' verificato un errore durante l'interazione con i servizi: " + errorMessage,
            "firmaremota.ws.remote.error", null);
      }

    } catch (MalformedURLException e) {
      throw new GestoreException("Firma remota, si e' verificato un errore durante l'interazione con i servizi: " + e.getMessage(),
          "firmaremota.ws.remote.error", e);
    } catch (IOException e) {
      throw new GestoreException("Firma remota, si e' verificato un errore durante l'interazione con i servizi: " + e.getMessage(),
          "firmaremota.ws.remote.error", e);
    } finally {
      if (conn != null) conn.disconnect();
    }

    return signedContent;

  }
  
  private void sbiancaAttributiVerificaFirma(String idprg, Long iddocdig) throws GestoreException {
    String queryUpdate = "update w_docdig set firmacheck = ?,firmacheckts = ? where idprg = ? and iddocdig = ?";
    try {
      sqlManager.update(queryUpdate,new Object[]{null, null, idprg, iddocdig});
    } catch (SQLException e) {
      throw new GestoreException("Firma remota: sbianca campi verifica firma terminato in errore", "firmaremota.ws.remote.error", null);
    }
  }

  /**
   * Verifica presenza ClientId.
   * 
   * @param alias
   * @throws GestoreException
   */
  private void __testClientId(String clientId) throws GestoreException {
    if (clientId == null || (clientId != null && "".equals(clientId.trim()))) {
      throw new GestoreException("Firma remota: indicare il clientId nella configurazione per la firma elettronica", "firmaremota.noclientid", null);
    }
  }

  /**
   * Verifica presenza PIN.
   * 
   * @param pin
   * @throws GestoreException
   */
  private void __testPIN(String pin) throws GestoreException {
    if (pin == null || (pin != null && "".equals(pin.trim()))) {
      throw new GestoreException("Firma remota: indicare il PIN dell'utente abilitato alla firma elettronica", "firmaremota.nopin", null);
    }
  }

  /**
   * Verifica presenza Alias.
   * 
   * @param alias
   * @throws GestoreException
   */
  private void __testAlias(String alias) throws GestoreException {
    if (alias == null || (alias != null && "".equals(alias.trim()))) {
      throw new GestoreException("Firma remota: indicare l'alias dell'utente abilitato alla firma elettronica", "firmaremota.noalias", null);
    }
  }

  /**
   * Verifica che per il formato PAdES e PAdES_T il file indicato sia un PDF.
   * 
   * @param format
   * @param nameToSign
   * @throws GestoreException
   */
  private void __testNameToSign(String format, String nameToSign) throws GestoreException {
    if (FORMATO_PADES.equals(format.toLowerCase()) || FORMATO_PADES_T.equals(format.toLowerCase())) {
      String ext = FilenameUtils.getExtension(nameToSign);
      if (ext == null || (ext != null && !EXT_PDF.equals(ext.toLowerCase()))) {
        throw new GestoreException("Firma remota: il formato PAdES richiede in input solo file PDF", "firmaremota.nopdf", null);
      }
    }
  }

  /**
   * Controllo dei formati supportati.
   * <ul>
   * <li>cades</li>
   * <li>cades_t</li>
   * <li>pades</li>
   * <li>pades-t</li>
   * </ul>
   * 
   * @param format
   * @throws GestoreException
   */
  private void __testFormat(String format) throws GestoreException {
    if (!FORMATO_CADES.equals(format.toLowerCase())
        && !FORMATO_PADES.equals(format.toLowerCase())
        && !FORMATO_CADES_T.equals(format.toLowerCase())
        && !FORMATO_PADES_T.equals(format.toLowerCase())) {
      throw new GestoreException("Firma remota: il formato indicato non e' supportato", "firmaremota.noformat", null);
    }
  }

  /**
   * Verifica presenza OTP per la modalita' remota non automatica.
   * 
   * @param mode
   * @param otp
   * @throws GestoreException
   */
  private void __testOTP(String mode, String otp) throws GestoreException {
    if (MODALITA_REMOTA.equals(mode.toLowerCase())) {
      if (otp == null || (otp != null && "".equals(otp.trim()))) {
        throw new GestoreException("Firma remota: indicare il codice temporaneo OTP (necessario in caso di firma remota non automatica)",
            "firmaremota.nootp", null);
      }
    }
  }

  /**
   * Controllo delle modalita' supportate.
   * <ul>
   * <li>remota</li>
   * <li>automatica</li>
   * </ul>
   * 
   * @param mode
   * @throws GestoreException
   */
  private void __testMode(String mode) throws GestoreException {
    if (!MODALITA_REMOTA.equals(mode.toLowerCase()) && !MODALITA_AUTOMATICA.equals(mode.toLowerCase())) {
      throw new GestoreException("Firma remota: la modalita' indicata non e' supportata", "firmaremota.nomode", null);
    }
  }

  /**
   * Richiede l'invio dell'OTP per l'alias indicato.
   * 
   * @param alias
   *        Alias del certificato.
   * @return
   * @throws GestoreException
   */
  public String remoteRequestOtp(String alias) throws GestoreException {

    String status = null;

    String provider = ConfigManager.getValore(PROP_PROVIDER);
    if (PROVIDER_INFOCERT.equals(provider.toLowerCase())) {
      status = this.__remoteRequestOtpInfocert(alias);
    }

    return status;

  }

  /**
   * INFOCERT: richiede l'invio dell'OTP per l'alias indicato
   * 
   * @param alias
   * @return
   * @throws GestoreException
   */
  private String __remoteRequestOtpInfocert(String alias) throws GestoreException {

    String requestOtpStatus = null;
    HttpURLConnection conn = null;
    int responseCode = 0;

    try {

      String url = ConfigManager.getValore(PROP_REMOTE_URL);
      if (url == null || (url != null && "".equals(url.trim()))) {
        throw new GestoreException("Firma remota: non e' definito l'indirizzo del servizio di verifica INFOCERT",
            "firmaremota.infocert.noverifyurl", null);
      }
      if (!"/".equals(url.substring(url.length() - 1))) url += "/";
      url += "request-otp/" + alias + "/it";

      conn = (HttpURLConnection) new URL(url).openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("GET");

      responseCode = conn.getResponseCode();
      conn.getResponseMessage();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        requestOtpStatus = org.apache.commons.io.IOUtils.toString(br);
      } else {

        String errorMessage = "";

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
        String outputErrorStream = org.apache.commons.io.IOUtils.toString(br);

        try {
          Document documentErrorStream = DocumentHelper.parseText(outputErrorStream);
          Element elRoot = documentErrorStream.getRootElement();
          if (elRoot != null) {
            Element elError = elRoot.element("error");
            if (elError != null) {
              String errorCode = elError.element("error-code").getText();
              String errorDescription = elError.element("error-description").getText();
              String proxysignErrorCode = elError.element("proxysign-error-code").getText();
              String proxysignErrorDescription = elError.element("proxysign-error-description").getText();
              if (errorCode != null) errorMessage = errorCode;
              if (errorDescription != null) errorMessage += " - " + errorDescription;
              if (proxysignErrorCode != null) errorMessage += " - " + proxysignErrorCode;
              if (proxysignErrorDescription != null) errorMessage += " - " + proxysignErrorDescription;
            } else {
              errorMessage = conn.getResponseMessage();
            }
          } else {
            errorMessage = conn.getResponseMessage();
          }
        } catch (DocumentException e) {
          errorMessage = outputErrorStream;
        }

        throw new GestoreException("Firma remota, si e' verificato un errore durante l'interazione con i servizi: " + errorMessage,
            "firmaremota.ws.remote.error", null);
      }

    } catch (MalformedURLException e) {
      throw new GestoreException("Firma remota, si e' verificato un errore durante l'interazione con i servizi: " + e.getMessage(),
          "firmaremota.ws.remote.error", e);
    } catch (IOException e) {
      throw new GestoreException("Firma remota, si e' verificato un errore durante l'interazione con i servizi: " + e.getMessage(),
          "firmaremota.ws.remote.error", e);
    } finally {
      if (conn != null) conn.disconnect();
    }

    return requestOtpStatus;

  }

}
