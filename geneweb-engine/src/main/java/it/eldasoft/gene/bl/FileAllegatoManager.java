package it.eldasoft.gene.bl;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.common.PDDestinationOrAction;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.dao.FileAllegatoDao;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityWeb;

public class FileAllegatoManager {

  static Logger           logger = Logger.getLogger(FileAllegatoManager.class);

  private FileAllegatoDao fileAllegatoDao;

  /**
   *
   * @param fileAllegatoDao
   */
  public void setFileAllegatoDao(FileAllegatoDao fileAllegatoDao) {
    this.fileAllegatoDao = fileAllegatoDao;
  }

  private SqlManager sqlManager;

  /**
   * @param sqlManager the sqlManager to set
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  /**
   * Inserisce nella response il file allegato individuato dai parametri in input.
   *
   * @param dignomdoc nome del documento
   * @param params parametri per estrarre il documento dalla W_DOCDIG
   * @param response response HTTP
   * @throws IOException
   * @throws GestoreException
   * @deprecated utilizzare il metodo che downloadFileAllegato(String, String, Long, HttpServletResponse)
   */
  @Deprecated
  public void downloadFileAllegato(String dignomdoc, HashMap params, HttpServletResponse response)
          throws IOException, GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("downloadFileAllegato: inizio metodo");

    String idProgramma = (String)params.get("idprg");
    Long  idDocumento =  (Long)params.get("iddocdig");

    BlobFile fileAllegatoBlob = this.fileAllegatoDao.getFileAllegato(idProgramma, idDocumento);
    UtilityWeb.download(dignomdoc, fileAllegatoBlob.getStream(), response);

    if (logger.isDebugEnabled())
      logger.debug("downloadFileAllegato: fine metodo");
  }

  /**
   * Inserisce nella response il file allegato individuato dai parametri in input.
   *
   * @param nomeFile nome del documento
   * @param idProgramma
   *        identificativo del programma
   * @param idDocumento
   *        identificativo del documento
   * @param response response HTTP
   * @throws IOException
   * @throws GestoreException
   */
  public void downloadFileAllegato(String nomeFile, String idProgramma, Long idDocumento, HttpServletResponse response) throws IOException, GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("downloadFileAllegato(" + nomeFile + "," + idProgramma + "," + idDocumento + "): inizio metodo");

    BlobFile fileAllegatoBlob = this.fileAllegatoDao.getFileAllegato(idProgramma, idDocumento);
    UtilityWeb.download(nomeFile, fileAllegatoBlob.getStream(), response);

    logger.debug("downloadFileAllegato: fine metodo");
  }

  /**
   * Inserisce nella response il file allegato individuato dai parametri in input ed utilizzando il nome originale del documento.
   *
   * @param idProgramma
   *        identificativo del programma
   * @param idDocumento
   *        identificativo del documento
   * @param response response HTTP
   * @throws IOException
   * @throws GestoreException
   */
  public void downloadFileAllegato(String idProgramma, Long idDocumento, HttpServletResponse response) throws IOException, GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("downloadFileAllegato(" + idProgramma + "," + idDocumento + "): inizio metodo");

    BlobFile fileAllegatoBlob = this.fileAllegatoDao.getFileAllegato(idProgramma, idDocumento);
    UtilityWeb.download(fileAllegatoBlob.getNome(), fileAllegatoBlob.getStream(), response);

    logger.debug("downloadFileAllegato: fine metodo");
  }

  /**
   * Estrae il file allegato individuato dai parametri in input.
   *
   * @param idProgramma
   *        identificativo del programma
   * @param idDocumento
   *        identificativo del documento
   * @return classe contenente il bytearray del campo blob
   * @throws IOException
   */
  public BlobFile getFileAllegato(String idProgramma, Long idDocumento) throws IOException, GestoreException {
    if (logger.isDebugEnabled()) logger.debug("getFileAllegato(" + idProgramma + "," + idDocumento + "): inizio metodo");
    BlobFile fileAllegatoBlob = this.fileAllegatoDao.getFileAllegato(idProgramma, idDocumento);
    logger.debug("getFileAllegato: fine metodo");

    return fileAllegatoBlob;
  }

  /**
   * Estrae il file allegato individuato dai parametri in input.
   *
   * @param params
   *        parametri per estrarre il documento dalla W_DOCDIG
   * @return classe contenente il bytearray del campo blob
   * @throws IOException
   * @deprecated utilizzare il metodo che getFileAllegato(String, Long)
   */
  @Deprecated
 public BlobFile getFileAllegato(HashMap params)
          throws IOException, GestoreException {
   if (logger.isDebugEnabled())
     logger.debug("getFileAllegato: inizio metodo");

   //BlobFile fileAllegatoBlob = this.fileAllegatoDao.getFileAllegato(params);
   String idProgramma = (String)params.get("idprg");
   Long  idDocumento =  (Long)params.get("iddocdig");

   BlobFile fileAllegatoBlob = this.fileAllegatoDao.getFileAllegato(idProgramma, idDocumento);

   if (logger.isDebugEnabled())
     logger.debug("getFileAllegato: fine metodo");

   return fileAllegatoBlob;
 }


  public void eliminaFileAllegato(String idprg, Long iddocdig, String codpra,Long con_fi) throws SQLException{
    //cancella le righe di w_docdig
    String query = " delete from W_DOCDIG where IDPRG = ? AND IDDOCDIG = ? ";
    sqlManager.update(query, new Object[]{idprg,iddocdig});
    //aggiorna MPEALDOC.DOCALL1
    query = " update MPEALDOC set DOCALL1 = NULL where CODPRA = ? AND CON_FI = ?";
    sqlManager.update(query, new Object[]{codpra,con_fi});
  }

  /**
   * Inserimento del file allegato in W_DOCDIG
   * @param idprg
   * @param iddocdig
   * @param digogg
   * @throws SQLException
   */
  public void insertFileAllegatoAssociaModello(String idprg, Long iddocdig, String digent, String digkey1, byte[] digogg) throws SQLException {
    this.fileAllegatoDao.insertFileAllegatoAssociaModello(idprg, iddocdig, digent, digkey1, digogg);
  }

  /**
   * Utility per il controllo della validit&agrave; dell'estensione di un file secondo la configurazione impostata da applicativo. Si
   * controllano tutte le estensioni associate al file (es: pdf.p7m.psd).
   *
   * @param nomeFile
   *        nome del file da controllare
   *
   * @return true se la configurazione non contiene valori e quindi qualsiasi estensione del file &egrave; ammessa, oppure l'estensione del
   *         file in input rientra in una delle estensioni ammesse, false altrimenti
   */
  public static boolean isEstensioneFileAmmessa(String nomeFile) {
    boolean esito = true;
    String estensioniAmmesse = StringUtils.stripToNull(ConfigManager.getValore(CostantiGenerali.PROP_ESTENSIONI_AMMESSE));
    if (estensioniAmmesse != null) {
      // si controlla se il nome del file caricato rientra in una delle estensioni ammesse da configurazione
      if (nomeFile == null || nomeFile.indexOf('.') <= 0 || (nomeFile.indexOf('.') == (nomeFile.length()-1))) {
        // devono esistere file con un nome reale ed una suddivisione nome+"."+estensione
        esito = false;
      } else {
        Set<String> setEstensioni = new HashSet<String>(Arrays.asList(StringUtils.split(estensioniAmmesse.toUpperCase(), ';')));
        String nomeFileConEstensioni = nomeFile;
        while (nomeFileConEstensioni.contains(".")) {
          String estensione = StringUtils.substringAfterLast(nomeFileConEstensioni, ".");
          if (estensione != null) {
            if (!setEstensioni.contains(estensione.toUpperCase())) {
              // l'estensione non risulta tra quelle ammesse, quindi fallisco il controllo
              esito = false;
              break;
            }
          }
          // si itera sulla eventuale estensione precedente
          nomeFileConEstensioni = StringUtils.substringBeforeLast(nomeFileConEstensioni, ".");
        }
      }
    }
    return esito;
  }
  
  /**
   * Verifica la coerenza tra l'estensione assegnata al file ed il suo contenuto.<br/>
   * Attualmente le estensioni gestite sono le seguenti:
   * <ul>
   * <li>TXT</li>
   * <li>RTF</li>
   * <li>DOC</li>
   * <li>DOCX</li>
   * <li>ODT</li>
   * <li>CSV</li>
   * <li>XLS</li>
   * <li>XLSX</li>
   * <li>ODS</li>
   * <li>PDF</li>
   * <li>P7M</li>
   * <li>TSD</li>
   * <li>JPG</li>
   * <li>PNG</li>
   * <li>XML</li>
   * <li>ZIP</li>
   * <li>7Z</li>
   * <li>EPS</li>
   * <li>MSG</li>
   * <li>EML</li>
   * <li>XLSM</li>
   * <li>DWG</li>
   * <li>DXF</li>
   * </ul>
   * Nel qual caso si volessero gestire nuove estensioni di file caricabili, occorre estendere i controlli contenuti all'interno.
   * 
   * @param fullPathName
   *        path assoluto del file da verificare
   * @return true se il contenuto del file &egrave; coerente con la sua estensione, false altrimenti
   * 
   * @throws java.io.IOException
   */
  public static boolean checkFileContent(String fullPathName) throws java.io.IOException {
    InputStream stream = new FileInputStream(fullPathName);
    return checkFileContent(stream, fullPathName);
  }

  /**
   * Verifica la coerenza tra l'estensione assegnata al file ed il suo contenuto.<br/>
   * Attualmente le estensioni gestite sono le seguenti:
   * <ul>
   * <li>TXT</li>
   * <li>RTF</li>
   * <li>DOC</li>
   * <li>DOCX</li>
   * <li>ODT</li>
   * <li>CSV</li>
   * <li>XLS</li>
   * <li>XLSX</li>
   * <li>ODS</li>
   * <li>PDF</li>
   * <li>P7M</li>
   * <li>TSD</li>
   * <li>JPG</li>
   * <li>PNG</li>
   * <li>XML</li>
   * <li>ZIP</li>
   * <li>7Z</li>
   * <li>EPS</li>
   * <li>MSG</li>
   * <li>EML</li>
   * <li>XLSM</li>
   * <li>DWG</li>
   * <li>DXF</li>
   * </ul>
   * Nel qual caso si volessero gestire nuove estensioni di file caricabili, occorre estendere i controlli contenuti all'interno.
   * 
   * @param stream
   *        input stream del file caricato e da verificare
   * @param filename
   *        nome, comprensivo di estensione, del file caricato e da verificare
   * @return true se il contenuto del file &egrave; coerente con la sua estensione, false altrimenti
   * 
   * @throws java.io.IOException
   */
  public static boolean checkFileContent(InputStream stream, String filename) throws java.io.IOException {
    Tika tika = new Tika();

    String mediaTypeByContent = tika.detect(stream, filename);
    stream.close();
    String mediaTypeByExtension = tika.detect(filename);

    boolean esito = checkTikaMediaType(mediaTypeByExtension, mediaTypeByContent);
    logger.debug(esito + " =>" + mediaTypeByExtension + " , " + mediaTypeByContent + " FILE " + filename);

    return esito;
  }

  /**
   * Verifica la coerenza tra l'estensione assegnata al file ed il suo contenuto.<br/>
   * Attualmente le estensioni gestite sono le seguenti:
   * <ul>
   * <li>TXT</li>
   * <li>RTF</li>
   * <li>DOC</li>
   * <li>DOCX</li>
   * <li>ODT</li>
   * <li>CSV</li>
   * <li>XLS</li>
   * <li>XLSX</li>
   * <li>ODS</li>
   * <li>PDF</li>
   * <li>P7M</li>
   * <li>TSD</li>
   * <li>JPG</li>
   * <li>PNG</li>
   * <li>XML</li>
   * <li>ZIP</li>
   * <li>7Z</li>
   * <li>EPS</li>
   * <li>MSG</li>
   * <li>EML</li>
   * <li>XLSM</li>
   * <li>DWG</li>
   * <li>DXF</li>
   * </ul>
   * Nel qual caso si volessero gestire nuove estensioni di file caricabili, occorre estendere i controlli contenuti all'interno.
   * 
   * @param stream
   *        byte array del file caricato e da verificare
   * @param filename
   *        nome, comprensivo di estensione, del file caricato e da verificare
   * @return true se il contenuto del file &egrave; coerente con la sua estensione, false altrimenti
   * 
   * @throws java.io.IOException
   */
  public static boolean checkFileContent(byte[] stream, String filename) throws java.io.IOException {
    Tika tika = new Tika();

    String mediaTypeByContent = tika.detect(stream, filename);
    String mediaTypeByExtension = tika.detect(filename);

    boolean esito = checkTikaMediaType(mediaTypeByExtension, mediaTypeByContent);
    logger.debug(esito + " =>" + mediaTypeByExtension + " , " + mediaTypeByContent + " FILE " + filename);

    return esito;
  }

  private static boolean checkTikaMediaType(String mediaTypeByExtension, String mediaTypeByContent) {
    boolean esito = false;
    
    if ("1".equals(ConfigManager.getValore("uploadFile.checkContenuto"))) {
      // eseguire i controlli
      if (mediaTypeByExtension.equals(mediaTypeByContent)) {
        // c'e' corrispondenza esatta tra contenuto del file e la sua estensione riportata nel nome
        esito = true;
      } else if (mediaTypeByExtension.startsWith("text/") && mediaTypeByContent.startsWith("text/")) {
        // alcuni file testuali, se analizzati a partire dal path del file stesso, vengono ritornati sempre come text/plain e non con il mime
        // type text/xxx legato all'estensione del file, ma bypasso la mancanza del match e considero il file comunque buono
        esito = true;
      } else if ("application/msword".equals(mediaTypeByExtension) && "application/x-tika-msoffice".equals(mediaTypeByContent)) {
        // i file .DOC vengono mappati come application/msword mentre analizzando il contenuto Tika li classifica come
        // application/x-tika-msoffice
        esito = true;
      } else if (mediaTypeByExtension.startsWith("application/vnd.openxmlformats-officedocument.")
          && "application/x-tika-ooxml".equals(mediaTypeByContent)) {
        // i file .DOCX vengono mappati come application/vnd.openxmlformats-officedocument.wordprocessingml.document mentre analizzando il
        // contenuto Tika li classifica come application/x-tika-ooxml
        // i file .XLSX vengono mappati come application/vnd.openxmlformats-officedocument.spreadsheetml.sheet mentre analizzando il contenuto
        // Tika li classifica come application/x-tika-ooxml
        esito = true;
      } else if ("application/pkcs7-mime".equals(mediaTypeByExtension)
          && ("application/pkcs7-signature".equals(mediaTypeByContent) || "application/x-dbf".equals(mediaTypeByContent))) {
        // i file .P7M vengono mappati in diversi modi distinti (prevalentemente sono application/pkcs7-signature) 
        esito = true;
      } else if ("application/vnd.ms-outlook".equals(mediaTypeByExtension) && "application/x-tika-msoffice".equals(mediaTypeByContent)) {
        // i file .MSG vengono mappati come application/vnd.ms-outlook mentre analizzando il contenuto Tika li classifica come
        // application/x-tika-msoffice
        esito = true;
      } else if ("application/vnd.ms-excel.sheet.macroenabled.12".equals(mediaTypeByExtension)
          && "application/x-tika-ooxml".equals(mediaTypeByContent)) {
        // i file .XLSM vengono mappati come application/vnd.ms-excel.sheet.macroenabled.12 mentre analizzando il contenuto Tika li classifica
        // come application/x-tika-ooxml
        esito = true;
      } else if ("image/vnd.dxf".equals(mediaTypeByExtension) && mediaTypeByContent.startsWith("image/vnd.dxf")) {
        // i file .DXF vengono mappati come image/vnd.dxf postponendo pero' il suffisso "; format=ascii"
        esito = true;
      }      
    } else {
      // skip dei controlli
      esito = true;      
    }
    return esito;
  }

  /**
   * Verifica la presenza di codice malevolo all'interno di un file PDF.
   * Il metodo esegue diversi controlli di sicurezza sul documento PDF:
   * <ul>
   * <li>Verifica la presenza di JavaScript potenzialmente dannoso</li>
   * <li>Controlla le annotazioni per individuare link o azioni sospette</li>
   * <li>Analizza i file incorporati nel PDF</li>
   * <li>Cerca pattern malevoli nel testo estratto</li>
   *</ul>
   * I controlli vengono eseguiti solo se abilitati tramite il parametro di configurazione "uploadFile.checkPdfVulnerabile"
   *
   * @param stream array di byte contenente il documento PDF da analizzare
   * @param filename nome del file, utilizzato per le tracciature su log
   * @return true se viene rilevato contenuto potenzialmente malevolo, false altrimenti
   * @throws RuntimeException in caso di errori durante l'analisi del documento
   */
  public static boolean isMaliciousPdf(byte[] stream, String filename) {
    boolean malicious = false;

    if ("1".equals(ConfigManager.getValore("uploadFile.checkPdfVulnerabile"))) {
      PDDocument document = null;
      try {
        document = PDDocument.load(stream);

        // test javascript malevolo
        PDDestinationOrAction action = document.getDocumentCatalog().getOpenAction();
        if (action != null && action instanceof PDActionJavaScript) {
          PDActionJavaScript jsAction = (PDActionJavaScript) action;
          String script = jsAction.getAction();
          malicious = hasPdfMaliciousJavaScript(script, filename);
        }

        // malicious e' la somma di tutti i controlli, la or com il booleano viene posta a destra in modo da lanciare prima tutte le funzioni
        malicious = hasMaliciousAnnotation(document, filename) || malicious;
        malicious = hasMaliciousEmbeddedFiles(document, filename) || malicious;

        if (!malicious) {
          // si aggiunge questo controllo sotto condizione in quanto la getText potrebbe causare una eccezione, allora
          // preferisco ritornare un esito con tutti i controlli errati loggati in precedenza anziche' andare a generare una eccezione
          PDFTextStripper stripper = new PDFTextStripper();
          String text = stripper.getText(document);
          malicious = hasMaliciousPattern(text, filename);
        }

        // al bisogno aggiungere altri controlli

      } catch (IOException e) {
        logger.error("Impossibile analizzare il contenuto del file " + filename + " per rilevare eventuale codice malevolo", e);
        throw new RuntimeException(e);
      } finally {
        if (document != null) {
          try {
            document.close();
          } catch (IOException e) {
            logger.error("Impossibile chiudere il documento " + filename, e);
          }
        }
      }
    }
    return malicious;
  }

  /**
   * Testa alcuni pattern tipici che si possono rilevare all'interno di un pdf e sono considerati codice javascript malevolo.
   * @param script codice javascript rilevato nel pdf
   * @param filename nome del file, usato per le tracciature su log
   * @return true se il codice risulta malevolo, false altrimenti
   */
  private static boolean hasPdfMaliciousJavaScript(String script, String filename) {
    boolean malicious = false;

    // List of suspicious functions or patterns
    String[] suspiciousPatterns = {"eval(", "setTimeout(", "setInterval(", "this.submitForm", "app.launchURL", "app.openDoc", "alert", "confirm", "prompt", "http://", "https://", "on\\w+\\s*="};
    for (String pattern : suspiciousPatterns) {
      if (script.contains(pattern)) {
        logger.error("File " + filename + ", rilevato pattern malevolo " + pattern + " all'interno del file nella seguente sequenza: " + script);
        malicious = true;
      }
    }

    // Check for obfuscated code (e.g., long hex strings)
    if (script.matches(".*([0-9A-Fa-f]{50,}).*")) {
      logger.error("File " + filename + ", rilevato codice offuscato all'interno del file nella seguente sequenza: " + script);
      malicious = true;
    }

    return malicious;
  }

  /**
   * Verifica la presenza di pattern malevoli all'interno del testo estratto da un file PDF.
   * Il metodo cerca pattern specifici come URL sospetti, domini di phishing e URL basati su IP.
   *
   * @param pdfText testo estratto dal file PDF da analizzare
   * @param filename nome del file, utilizzato per le tracciature su log
   * @return true se viene rilevato almeno un pattern malevolo, false altrimenti
   */
  private static boolean hasMaliciousPattern(String pdfText, String filename) {
    boolean malicious = false;

    // Ricerca di URL sospetti o malevoli (e.g., phishing)
    // Check for suspicious URL patterns
    Pattern maliciousPattern = Pattern.compile("(?i)(evil|hack|malware|phish|spam|attack|exploit)\\.\\w+");
    Matcher maliciousMatcher = maliciousPattern.matcher(pdfText);
    while (maliciousMatcher.find()) {
      logger.error("File " + filename + ", rilevata URL potenzialmente malevola: " + maliciousMatcher.group());
      malicious = true;
    }

    // Check for common phishing domains
    Pattern phishingPattern = Pattern.compile("(?i)(paypa1|google-security|bank-verify|secure-login)\\.\\w+");
    Matcher phishingMatcher = phishingPattern.matcher(pdfText);
    while (phishingMatcher.find()) {
      logger.error("File " + filename + ", trovato possibile dominio di phishing: " + phishingMatcher.group());
      malicious = true;
    }

    // Check for IP-based URLs
    Pattern ipPattern = Pattern.compile("http[s]?://\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    Matcher ipMatcher = ipPattern.matcher(pdfText);
    while (ipMatcher.find()) {
      logger.error("File " + filename + ",  trovato URL basato su IP: " + ipMatcher.group());
      malicious = true;
    }

    return malicious;
  }

  /**
   * Verifica la presenza di annotazioni potenzialmente malevole all'interno di un documento PDF.
   * Il metodo analizza tutte le annotazioni presenti nel documento controllando:
   * <ul>
   * <li>link che contengono URI potenzialmente pericolosi</li>
   * <li>annotazioni contenenti JavaScript</li>
   * <li>contenuto sospetto nei dizionari delle annotazioni</li>
   *</ul>
   * @param document documento PDF da analizzare
   * @param filename nome del file PDF, utilizzato per le tracciature su log
   * @return true se viene rilevata almeno un'annotazione potenzialmente malevola, false altrimenti
   */
  private static boolean hasMaliciousAnnotation(PDDocument document, String filename) {
    boolean malicious = false;

    int pageCount = document.getNumberOfPages();
    for (int i = 0; i < pageCount; i++) {
      try {
        List<PDAnnotation> annotations = document.getPage(i).getAnnotations();
        for (PDAnnotation annotation : annotations) {
          if (annotation instanceof PDAnnotationLink) {
            PDAnnotationLink link = (PDAnnotationLink) annotation;
            PDAction action = link.getAction();
            if (action instanceof PDActionURI) {
              PDActionURI uri = (PDActionURI) action;
              String uriString = uri.getURI();
              malicious = checkURIForMaliciousContent(uriString, filename) || malicious;
            }
            if (action instanceof PDActionJavaScript) {
              PDActionJavaScript jsAction = (PDActionJavaScript) action;
              String script = jsAction.getAction();
              malicious =  hasPdfMaliciousJavaScript(script, filename) || malicious;
            }
          }

          // Check the annotation's dictionary for suspicious content
          COSDictionary annotDict = annotation.getCOSObject();
          // Check the /V (value) entry
          if (annotDict.containsKey("V")) {
            Object value = annotDict.getDictionaryObject("V");
            if (value instanceof COSString) {
              String stringValue = ((COSString) value).getString();
              malicious = checkURIForMaliciousContent(stringValue, filename) || malicious;
            }
          }

          String contents = annotation.getContents();
          malicious = checkURIForMaliciousContent(contents, filename) || malicious;
        }
      } catch (IOException e) {
        logger.error("Impossibile analizzare il contenuto del file " + filename + " per rilevare eventuale codice malevolo", e);
        throw new RuntimeException(e);
      }
    }
    return malicious;
  }

  /**
   * Verifica la presenza di file embedded all'interno di un documento PDF che potrebbero contenere codice malevolo.
   * Il metodo analizza i file incorporati nel PDF verificando:
   * <ul>
   * <li>estensioni potenzialmente pericolose (exe, dll, bat, etc.)</li>
   * <li>contenuto sospetto nei file embedded</li>
   * <li>header di file eseguibili</li>
   * </ul>
   * @param document documento PDF da analizzare
   * @param filename nome del file PDF, utilizzato per le tracciature su log
   * @return true se viene rilevato almeno un file embedded potenzialmente malevolo, false altrimenti
   */
  private static boolean hasMaliciousEmbeddedFiles(PDDocument document, String filename) {
    boolean malicious = false;
    if (document.getDocumentCatalog().getNames() != null) {
      PDEmbeddedFilesNameTreeNode embeddedFiles = document.getDocumentCatalog().getNames().getEmbeddedFiles();
      try {
        if (embeddedFiles != null && embeddedFiles.getNames() != null) {
          for (Map.Entry<String, PDComplexFileSpecification> entry : embeddedFiles.getNames().entrySet()) {
            PDComplexFileSpecification fileSpec = entry.getValue();
            // Analyze each embedded file
            malicious = malicious || analyzeEmbeddedFile(fileSpec.getEmbeddedFile(), fileSpec.getFile());
          }
        }
      } catch (IOException e) {
        logger.error("File " + filename + ", impossibile analizzare i file embedded", e);
        throw new RuntimeException(e);
      }
    }

    return malicious;
  }

  /**
   * Analizza il contenuto di un file embedded in un PDF per verificare la presenza di contenuti potenzialmente malevoli.
   * Il metodo controlla:
   * <ul>
   * <li>se il file ha un'estensione potenzialmente pericolosa (exe, dll, bat, etc.)</li>
   * <li>se il contenuto del file contiene pattern sospetti come header di file eseguibili</li>
   * </ul>
   * @param embeddedFile file embedded da analizzare
   * @param filename nome del file embedded
   * @return true se viene rilevato contenuto potenzialmente malevolo, false altrimenti
   * @throws IOException in caso di errori durante la lettura del file
   */
  private static boolean analyzeEmbeddedFile(PDEmbeddedFile embeddedFile, String filename) throws IOException {
    boolean malicious = false;
    // Check file extension
    if (hasExecutableExtension(filename)) {
      logger.error("File " + filename + ", rilevata estensione malevola");
      malicious = true;
    }

    // Check file content (first few bytes)
    byte[] content = embeddedFile.toByteArray();
    if (containsSuspiciousContent(content)) {
      logger.error("File " + filename + ", rilevato sospetto contenuto malevolo");
      malicious = true;
    }

    return malicious;
  }

  /**
   * Verifica se il file ha un'estensione che potrebbe essere pericolosa.
   * Le estensioni considerate pericolose sono quelle tipicamente associate a file eseguibili
   * o script, come: .exe, .dll, .bat, .cmd, .vbs, .js, .sh, .jar
   *
   * @param filename nome del file da controllare
   * @return true se l'estensione del file è considerata pericolosa, false altrimenti
   */
  private static boolean hasExecutableExtension(String filename) {
    String[] suspiciousExtensions = {".exe", ".dll", ".bat", ".cmd", ".vbs", ".js", ".sh", ".jar"};
    for (String ext : suspiciousExtensions) {
      if (filename.toLowerCase().endsWith(ext)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Verifica se il contenuto di un file embedded in un PDF contiene pattern sospetti o malevoli.
   * Il metodo controlla la presenza di header tipici di file eseguibili (es. MZ header per .exe)
   * e altri pattern potenzialmente pericolosi nel contenuto del file.
   *
   * @param content array di byte contenente il contenuto del file da analizzare
   * @return true se viene rilevato contenuto sospetto, false altrimenti
   */
  private static boolean containsSuspiciousContent(byte[] content) {
    // Check for suspicious byte patterns, e.g., executable headers
    byte[] exeHeader = {0x4D, 0x5A}; // MZ header for executables
    if (content.length >= 2 && content[0] == exeHeader[0] && content[1] == exeHeader[1]) {
      return true;
    }
    // Add more checks as needed
    return false;
  }

  /**
   * Verifica se una URI all'interno di un pdf contiene contenuto potenzialmente malevolo.
   * Il metodo controlla la presenza di JavaScript, caratteri HTML, JavaScript codificato
   * e altri pattern sospetti all'interno dell'URI.
   *
   * @param uri stringa contenente l'URI da analizzare
   * @param filename nome del file, utilizzato per le tracciature su log
   * @return true se viene rilevato contenuto malevolo, false altrimenti
   */
  private static boolean checkURIForMaliciousContent(String uri, String filename) {
    boolean malicious = false;
    if (uri == null) return malicious;
    // Check for JavaScript
    if (uri.toLowerCase().contains("javascript:")) {
      logger.error("File " + filename + ", rilevato JavaScript all'interno di una URI : " + uri);
      malicious = true;
    }
    // Check for encoded JavaScript
    if (uri.toLowerCase().contains("%3cscript") || uri.toLowerCase().contains("script%3e")) {
      logger.error("File " + filename + ", rilevato encoded JavaScript all'interno di una URI : " + uri);
      malicious = true;
    }

    // Check for HTML characters
    if (uri.contains("<") || uri.contains(">")) {
      logger.error("File " + filename + ", rilevato HTML all'interno di una URI : " + uri);
      malicious = true;
    }

    // Add more checks as needed
    // For example, check for other suspicious patterns or encodings

    return malicious;
  }

  /**
       * Inserisce nella response il file allegato individuato dai parametri in input, effettuando la tracciatura sulla W_LOGEVENTI
       * @param nomeFile nome del documento
       * @param idProgramma
       *        identificativo del programma
       * @param idDocumento
       * @param codApp
       * @param codProfilo
       * @param idUtente
       * @param response
       * @param ip
       * @throws IOException
       */
  public void downloadFileAllegato(String nomeFile, String idProgramma, Long idDocumento, String codApp, String codProfilo, Integer idUtente, String ip, HttpServletResponse response) throws IOException, GestoreException {
    if (logger.isDebugEnabled())
      logger.debug("downloadFileAllegato(" + nomeFile + "," + idProgramma + "," + idDocumento + "): inizio metodo");

    String errMsgEvento = "";
    int livEvento = 1;
    try{
      BlobFile fileAllegatoBlob = this.fileAllegatoDao.getFileAllegato(idProgramma, idDocumento);
      UtilityWeb.download(nomeFile, fileAllegatoBlob.getStream(), response);
    } catch(IOException e){
      livEvento = 3;
      errMsgEvento = e.getLocalizedMessage();
      throw e;
    } finally{

      LogEvento logEvento =  new LogEvento();
      logEvento.setCodApplicazione(codApp);
      logEvento.setLivEvento(livEvento);
      String oggetto = idProgramma;
      if(idDocumento!=null)
        oggetto+="/" + idDocumento.toString();
      logEvento.setOggEvento(oggetto);
      logEvento.setCodEvento("DOWNLOAD_FILE");
      logEvento.setDescr("Download file");
      logEvento.setErrmsg(errMsgEvento);
      logEvento.setCodProfilo(codProfilo);
      logEvento.setIdUtente(idUtente);
      logEvento.setIp(ip);
      try{
        LogEventiUtils.insertLogEventi(logEvento);
      }catch(Exception e){
        logger.error("Errore inaspettato durante la tracciatura su w_logeventi");
      }
    }
    logger.debug("downloadFileAllegato: fine metodo");
  }

}
