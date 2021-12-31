package it.eldasoft.gene.bl;


import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.dao.FileAllegatoDao;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityWeb;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

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
      throws IOException {
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
  public void downloadFileAllegato(String nomeFile, String idProgramma, Long idDocumento, HttpServletResponse response) throws IOException {
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
  public void downloadFileAllegato(String idProgramma, Long idDocumento, HttpServletResponse response) throws IOException {
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
  public BlobFile getFileAllegato(String idProgramma, Long idDocumento) throws IOException {
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
     throws IOException {
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
   * Utility per il controllo della validit&agrave; dell'estensione di un file secondo la configurazione impostata da applicativo.
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
      if (nomeFile != null) {
        String estensione = StringUtils.substringAfterLast(nomeFile, ".");
        if (estensione != null) {
          Set<String> setEstensioni = new HashSet<String>(Arrays.asList(StringUtils.split(estensioniAmmesse.toUpperCase(), ';')));
          if (!setEstensioni.contains(estensione.toUpperCase())) {
            // l'estensione non risulta tra quelle ammesse, quindi fallisco il controllo
            esito = false;
          }
        } else {
          // esistono delle estensioni ammesse, il file non ha estensione
          esito = false;
        }
      }
    }
    return esito;
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
  public void downloadFileAllegato(String nomeFile, String idProgramma, Long idDocumento, String codApp, String codProfilo, Integer idUtente, String ip, HttpServletResponse response) throws IOException {
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
