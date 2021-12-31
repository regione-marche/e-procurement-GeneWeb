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
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

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

  private static final String PROP_PROVIDER                = "firmaremota.provider";
  private static final String PROP_INFOCERT_AUTO_URL       = "firmaremota.auto.url";
  private static final String PROP_INFOCERT_REMOTE_URL     = "firmaremota.remote.url";
  private static final String PROP_INFOCERT_VERIFY_URL     = "firmaremota.verify.url";

  private static final String PROVIDER_INFOCERT            = "infocert";

  private static final String INFOCERT_MODALITA_REMOTA     = "remota";
  private static final String INFOCERT_MODALITA_AUTOMATICA = "automatica";

  private static final String INFOCERT_FORMATO_CADES       = "cades";
  private static final String INFOCERT_FORMATO_PADES       = "pades";
  private static final String INFOCERT_FORMATO_CADES_T     = "cades-t";
  private static final String INFOCERT_FORMATO_PADES_T     = "pades-t";

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
   */
  public byte[] sign(String mode, String format, String alias, String pin, String otp, BlobFile bfToSigned) throws GestoreException {

    byte[] signedContent = null;

    String provider = ConfigManager.getValore(PROP_PROVIDER);
    if (PROVIDER_INFOCERT.equals(provider.toLowerCase())) {
      signedContent = this.__signInfocert(mode, format, alias, pin, otp, bfToSigned);
    } else {
      throw new GestoreException("Firma remota: non e' stato indicato il provider o il provider indicato non e' supportato",
          "firmaremota.noprovider", null);
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

      String url = ConfigManager.getValore(PROP_INFOCERT_VERIFY_URL);
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

      String url = ConfigManager.getValore(PROP_INFOCERT_VERIFY_URL);
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
  public void sign(String idprg, Long iddocdig, String mode, String format, String alias, String pin, String otp) throws GestoreException {

    try {
      BlobFile bfToSign = fileAllegatoManager.getFileAllegato(idprg, iddocdig);
      byte[] signedContent = this.sign(mode, format, alias, pin, otp, bfToSign);

      DataColumnContainer dccW_DOCDIG = new DataColumnContainer(this.sqlManager, "W_DOCDIG",
          "select idprg, iddocdig from w_docdig where idprg = ? and iddocdig = ?", new Object[] { idprg, iddocdig });
      dccW_DOCDIG.getColumn("W_DOCDIG.IDPRG").setChiave(true);
      dccW_DOCDIG.getColumn("W_DOCDIG.IDDOCDIG").setChiave(true);

      if (INFOCERT_FORMATO_CADES.equals(format.toLowerCase()) || INFOCERT_FORMATO_CADES_T.equals(format.toLowerCase())) {
        dccW_DOCDIG.addColumn("W_DOCDIG.DIGNOMDOC", new JdbcParametro(JdbcParametro.TIPO_TESTO, bfToSign.getNome() + ".p7m"));
      }

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      baos.write(signedContent);
      dccW_DOCDIG.addColumn("W_DOCDIG.DIGOGG", new JdbcParametro(JdbcParametro.TIPO_BINARIO, baos));
      dccW_DOCDIG.update("W_DOCDIG", this.sqlManager);
    } catch (SQLException e) {
      throw new GestoreException("Firma remota, si e' verificato un errore durante l'interazione con i servizi: " + e.getMessage(),
          "firmaremota.ws.remote.error", e);
    } catch (IOException e) {
      throw new GestoreException("Firma remota, si e' verificato un errore durante l'interazione con i servizi: " + e.getMessage(),
          "firmaremota.ws.remote.error", e);
    }
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
  private byte[] __signInfocert(String mode, String format, String alias, String pin, String otp, BlobFile bfToSign)
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
      if (INFOCERT_MODALITA_REMOTA.equals(mode.toLowerCase())) {
        url = ConfigManager.getValore(PROP_INFOCERT_REMOTE_URL);
        if (url == null || (url != null && "".equals(url.trim()))) {
          throw new GestoreException("Firma remota: non e' definito l'indirizzo del servizio di firma remota",
              "firmaremota.infocert.noremoteurl", null);
        }
      } else if (INFOCERT_MODALITA_AUTOMATICA.equals(mode.toLowerCase())) {
        url = ConfigManager.getValore(PROP_INFOCERT_AUTO_URL);
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

      ByteArrayBody contentToSignBody = new ByteArrayBody(bfToSign.getStream(), bfToSign.getNome());
      mp.addPart("contentToSign-0", contentToSignBody);

      HttpEntity httpEntity = mp.build();

      conn.setRequestProperty("Content-Type", httpEntity.getContentType().getValue());
      OutputStream os = conn.getOutputStream();
      httpEntity.writeTo(os);
      os.flush();

      responseCode = conn.getResponseCode();
      conn.getResponseMessage();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        signedContent = org.apache.commons.io.IOUtils.toByteArray(conn.getInputStream());
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
    if (INFOCERT_FORMATO_PADES.equals(format.toLowerCase()) || INFOCERT_FORMATO_PADES_T.equals(format.toLowerCase())) {
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
    if (!INFOCERT_FORMATO_CADES.equals(format.toLowerCase())
        && !INFOCERT_FORMATO_PADES.equals(format.toLowerCase())
        && !INFOCERT_FORMATO_CADES_T.equals(format.toLowerCase())
        && !INFOCERT_FORMATO_PADES_T.equals(format.toLowerCase())) {
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
    if (INFOCERT_MODALITA_REMOTA.equals(mode.toLowerCase())) {
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
    if (!INFOCERT_MODALITA_REMOTA.equals(mode.toLowerCase()) && !INFOCERT_MODALITA_AUTOMATICA.equals(mode.toLowerCase())) {
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

      String url = ConfigManager.getValore(PROP_INFOCERT_REMOTE_URL);
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
