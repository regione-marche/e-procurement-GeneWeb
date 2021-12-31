/*
 * Created on 02/feb/2020
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.verifiche;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.bl.TabellatiManager;
import it.eldasoft.gene.commons.web.struts.UploadFileForm;
import it.eldasoft.gene.db.dao.FileAllegatoDao;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.domain.BlobFile;
import it.eldasoft.gene.db.domain.LogEvento;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.utils.LogEventiUtils;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityDate;
import it.eldasoft.utils.utility.UtilityStringhe;
import it.eldasoft.utils.utility.UtilityWeb;
import it.maggioli.eldasoft.security.EncryptionConstants;
import it.maggioli.eldasoft.security.SymmetricEncryptionUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Manager per le verifiche interne Art.80
 *
 * @author Cristian Febas
 *
 */
public class VerificheInterneManager {

  static Logger               logger                         = Logger.getLogger(VerificheInterneManager.class);

  private FileAllegatoDao fileAllegatoDao;

  private SqlManager          sqlManager;

  private TabellatiManager      tabellatiManager;

  public void setFileAllegatoDao(FileAllegatoDao fileAllegatoDao) {
    this.fileAllegatoDao = fileAllegatoDao;
  }


  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setTabellatiManager(TabellatiManager tabellatiManager) {
    this.tabellatiManager = tabellatiManager;
  }

/*
 *  Metodo per la gestione del file allegato in inserimento e aggiornamento
 */

  public String[] setDocumentoAllegato(HttpServletRequest request, String operazione, Long idDocumentiVerifiche,
      Long iddocdg, DataColumnContainer impl, UploadFileForm uploadFileForm)
  throws GestoreException {

    String outcome = "true";
    String msgDetail = "";
    String[] res = new String[2];


    if (impl.isColumn("FILEDAALLEGARE") && impl.getString("FILEDAALLEGARE") != null
        && !impl.getString("FILEDAALLEGARE").trim().equals("")) {

        ByteArrayOutputStream baos = null;
        Cipher cipher = null;

        try {

          String dimMassimaTabellatoStringa = tabellatiManager.getDescrTabellato("A1072", "1");
          if(dimMassimaTabellatoStringa==null || "".equals(dimMassimaTabellatoStringa)){
            throw new GestoreException("Non è presente il tabellato A1072 per determinare la dimensione "
                + "massima dell'upload del file", "upload.noTabellato", null);
          }
          int pos = dimMassimaTabellatoStringa.indexOf("(");
          if (pos<1){
            throw new GestoreException("Non è possibile determinare dal tabellato A1072 la dimensione "
                + "massima dell'upload del file", "upload.noValore", null);
          }
          dimMassimaTabellatoStringa = dimMassimaTabellatoStringa.substring(0, pos-1);
          dimMassimaTabellatoStringa = dimMassimaTabellatoStringa.trim();
          double dimMassimaTabellatoByte = Math.pow(2, 20) * Double.parseDouble(dimMassimaTabellatoStringa);


          if(uploadFileForm.getSelezioneFile().getFileSize() == 0 ){
            throw new GestoreException("Il file specificato è vuoto. Per continuare specificare un altro file",
                "upload.fileVuoto", null, null);
          }else if(uploadFileForm.getSelezioneFile().getFileSize()> dimMassimaTabellatoByte){
            throw new GestoreException("Il file selezionato ha una dimensione "
                + "superiore al massimo consentito (" + dimMassimaTabellatoStringa + " MB)" , "upload.overflow", new String[] { dimMassimaTabellatoStringa + " MB" },null);
          }else {
            String fileName = uploadFileForm.getSelezioneFile().getFileName();
            if(!FileAllegatoManager.isEstensioneFileAmmessa(fileName)){
              throw new GestoreException("Il file selezionato da caricare ha un'estensione non accettata",
                  "upload.estensioneNonAmmessa", new String[]{fileName}, null);
            }else{
              //variabili per tracciatura eventi
              String genericMsgErr = "Errore inaspettato durante la tracciatura su w_logeventi";
              int livEvento = 3;
              String errMsgEvento = genericMsgErr;
              //String messaggioPerLog ="";
              //boolean erroreGestito=false;
              try {
                //Tracciatura eventi
                try {
                  LogEvento logEvento = LogEventiUtils.createLogEvento(request);
                  logEvento.setLivEvento(1);
                  logEvento.setOggEvento(""+idDocumentiVerifiche);
                  logEvento.setCodEvento("G_CIFRATURA_DOCUMENTO");
                  logEvento.setDescr("Inizio cifratura documento");
                  logEvento.setErrmsg("");
                  LogEventiUtils.insertLogEventi(logEvento);
                } catch (Exception le) {
                  logger.error(genericMsgErr);
                }
                //provo a cifrare con il codice fiscale della SA  -- recupero il cf
                String ufficioIntestatario=null;
                HttpSession session = request.getSession();
                if ( session != null) {
                    ufficioIntestatario = StringUtils.stripToNull(((String) session.getAttribute("uffint")));
                    String cfein = (String) this.sqlManager.getObject(
                        "select cfein from uffint where codein=?", new Object[] {ufficioIntestatario});
                     SecretKey  chiaveSimmetrica = this.generaKey();
                    //la cifro ememorizzo in db
                    String  encodedSessionKey =this.cifraKey(chiaveSimmetrica,cfein);
                    this.sqlManager.update( "update documenti_verifiche set keysess = ? where id = ?",
                        new Object[] { encodedSessionKey,idDocumentiVerifiche});
                    //instanzio l'encoder
                     cipher = SymmetricEncryptionUtils.getEncoder(chiaveSimmetrica.getEncoded(), cfein);
                    //cifro il contenuto del file
                    baos = new ByteArrayOutputStream();
                    byte[] ff = uploadFileForm.getSelezioneFile().getFileData();

                    byte[] ffCifrato = SymmetricEncryptionUtils.translate(cipher, ff);
                    baos.write(ffCifrato);
                    //best case
                    livEvento = 1;
                    errMsgEvento = "";
                    //solo nel best case memorizzo in fb
                    String nomeFile="";
                    int len = impl.getString("FILEDAALLEGARE").length();
                    int posizioneBarra = impl.getString("FILEDAALLEGARE").lastIndexOf("\\");
                    nomeFile=impl.getString("FILEDAALLEGARE").substring(posizioneBarra+1,len).toUpperCase();
                    if(iddocdg!=null){
                      operazione = "UPDATE";
                      impl.addColumn("W_DOCDIG.IDPRG", JdbcParametro.TIPO_TESTO,"PG");
                      impl.addColumn("W_DOCDIG.IDDOCDIG", JdbcParametro.TIPO_NUMERICO,iddocdg);
                      impl.getColumn("W_DOCDIG.IDPRG").setObjectOriginalValue("PG");
                      impl.getColumn("W_DOCDIG.IDDOCDIG").setObjectOriginalValue(iddocdg);
                    }else{
                      operazione = "INSERT";
                      //Si deve calcolare il valore di IDDOCDIG
                      Long maxIDDOCDIG = (Long) this.sqlManager.getObject(
                                "select max(IDDOCDIG) from W_DOCDIG where IDPRG= ?",
                                new Object[] {"PG"} );
                      if (maxIDDOCDIG != null && maxIDDOCDIG.longValue()>0){
                        iddocdg  = maxIDDOCDIG.longValue() + 1;
                      }
                      impl.addColumn("W_DOCDIG.IDPRG", JdbcParametro.TIPO_TESTO,"PG");
                      impl.addColumn("W_DOCDIG.IDDOCDIG", JdbcParametro.TIPO_NUMERICO,iddocdg);
                    }
                    impl.getColumn("W_DOCDIG.IDPRG").setChiave(true);
                    impl.getColumn("W_DOCDIG.IDDOCDIG").setChiave(true);
                    impl.addColumn("W_DOCDIG.DIGENT", JdbcParametro.TIPO_TESTO,"DOCUMENTI_VERIFICHE");
                    impl.addColumn("W_DOCDIG.DIGKEY1", JdbcParametro.TIPO_TESTO, String.valueOf(idDocumentiVerifiche));
                    //impl.addColumn("W_DOCDIG.DIGKEY2", JdbcParametro.TIPO_NUMERICO,new Long(progressivo.longValue() + 1));
                    impl.addColumn("W_DOCDIG.DIGNOMDOC", JdbcParametro.TIPO_TESTO,nomeFile);
                    impl.addColumn("W_DOCDIG.DIGOGG", JdbcParametro.TIPO_BINARIO,baos);

                    if("INSERT".equals(operazione)){
                      impl.insert("W_DOCDIG", sqlManager);
                      sqlManager.update("update DOCUMENTI_VERIFICHE set IDPRG=?, IDDOCDG=? where ID=?",
                          new Object[] { "PG",iddocdg,idDocumentiVerifiche});
                    }else{
                      if("UPDATE".equals(operazione)){
                        impl.update("W_DOCDIG", sqlManager);
                      }
                    }
                }
              } catch (GeneralSecurityException e) {
                livEvento = 3;
                errMsgEvento = e.getMessage();
                throw new GestoreException("Errore nella cifratura del documento " + fileName + "("  + e.getMessage() + ")", null, e);
              }finally{

                try {
                  LogEvento logEvento = LogEventiUtils.createLogEvento(request);
                  logEvento.setLivEvento(livEvento);
                  logEvento.setOggEvento(""+idDocumentiVerifiche);
                  logEvento.setCodEvento("G_CIFRATURA_DOCUMENTO");
                  logEvento.setDescr("Fine cifratura documento");
                  logEvento.setErrmsg(errMsgEvento);
                  LogEventiUtils.insertLogEventi(logEvento);
                } catch (Exception le) {
                  logger.error(genericMsgErr);
                }
                logger.info("Cifratura del documento con id = " + idDocumentiVerifiche);
              }
            }

          }

        } catch (SQLException e) {
          throw new GestoreException("Errore nel caricamento in W_DOCDIG", null, e);
        } catch (FileNotFoundException e) {
          throw new GestoreException("Si è verificato un problema durante il caricamento del file", null, e);
        } catch (IOException e) {
          throw new GestoreException("Si è verificato un problema durante il caricamento del file", null, e);
        }

    }//file da allegare

    res[0] = outcome;
    res[1] = msgDetail;

    return res;

  }

  public Long calcolaStatoVerifica(int idVerifica, Date dataScadenza, Long ggValidita, Long ggAvvisoScadenza)
    throws GestoreException {

    Long statoVerifica = new Long(0);

    Date dataOdierna = UtilityDate.getDataOdiernaAsDate();
    if(dataScadenza!=null){
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(dataScadenza);
      if(ggAvvisoScadenza!=null){
        calendar.add(Calendar.DATE, -ggAvvisoScadenza.intValue());
        Date dataAvviso = calendar.getTime();
        if(dataOdierna.after(dataAvviso)){
          statoVerifica = new Long(2);
        }
      }else{
        statoVerifica = new Long(1);
      }

      if(dataOdierna.after(dataScadenza)){
        statoVerifica = new Long(3);
      }
    }


    return statoVerifica;

  }

  /*
   *  Metodo che calcola le date sensibili alle scadenze per i documenti (delle verifiche) e per le verifiche
   */

  public String[] calcolaScadenzeVerifiche(int idVerifica, int idDocumentiVerifiche, Long tipoDoc,
      Date dataInvioRichiestaDoc , Date dataEmissioneDoc, Date dataSilenzioAssensoDoc, Date dataScadenzaDoc,
      Long ggValiditaDoc,String archiviato)
  throws GestoreException {

    String outcome = "true";
    String msgDetail = "";
    String[] res = new String[2];


    try {

      Vector datiDOC_VERIF = this.sqlManager.getVector("select GG_VALIDITA,GG_AVVISO_SCADENZA from VERIFICHE" +
          " where VERIFICHE.ID = ?", new Object[] {idVerifica});

      Long ggValiditaVerifica = (Long) SqlManager.getValueFromVectorParam(datiDOC_VERIF, 0).getValue();
      if(ggValiditaVerifica == null){
        ggValiditaVerifica = new Long(30);
      }
      Long ggAvvisoScadenza = (Long) SqlManager.getValueFromVectorParam(datiDOC_VERIF, 1).getValue();


      Long numCertificazioni = (Long) this.sqlManager.getObject(
          "select count(*) from DOCUMENTI_VERIFICHE" +
          " where ID_VERIFICA = ? and tipo = ? and (ISARCHI is null OR ISARCHI <> 1)", new Object[] {idVerifica,new Long(2)});
      if(new Long(2).equals(tipoDoc)){
        numCertificazioni++;
      }

      archiviato = UtilityStringhe.convertiNullInStringaVuota(archiviato);

      Date mDataSilenzioAssenso = null;
      Date mDataUltimaRichiesta = null;
      Date mDataEmissione = null;

      Vector datiDocumenti = this.sqlManager.getVector(
          "select max(DATA_INVIO_RICHIESTA), max(DATA_SILENZIO_ASSENSO), max(DATA_EMISSIONE) from DOCUMENTI_VERIFICHE" +
          " where ID_VERIFICA = ? and (ISARCHI is null OR ISARCHI <> 1) and (ESITO_VERIFICA_DOC is null OR ESITO_VERIFICA_DOC <> 6)", new Object[] {idVerifica} );
      if (datiDocumenti != null && datiDocumenti.size() > 0) {
        mDataUltimaRichiesta = (Date) SqlManager.getValueFromVectorParam(datiDocumenti, 0).getValue();
        mDataSilenzioAssenso = (Date) SqlManager.getValueFromVectorParam(datiDocumenti, 1).getValue();
        mDataEmissione = (Date) SqlManager.getValueFromVectorParam(datiDocumenti, 2).getValue();
      }else{
        this.sqlManager.update("update VERIFICHE set DATA_ULTIMA_RICHIESTA = null,DATA_SILENZIO_ASSENSO = null," +
            " DATA_ULTIMA_CERTIFICAZIONE = null, DATA_SCADENZA = null, STATO_VERIFICA = null where ID =? ", new Object[] {idVerifica});
      }

      if(new Long(0).equals(numCertificazioni)){

        if(mDataSilenzioAssenso!=null){
          Calendar calScadenza = Calendar.getInstance();
          calScadenza.setTime(mDataSilenzioAssenso);
          calScadenza.add(Calendar.DAY_OF_MONTH,ggValiditaVerifica.intValue());
          Date dataScadenza=calScadenza.getTime();
          Long statoVerifica = this.calcolaStatoVerifica(idVerifica, dataScadenza, ggValiditaVerifica, ggAvvisoScadenza);
          this.sqlManager.update("update VERIFICHE set DATA_ULTIMA_RICHIESTA = ?,DATA_SILENZIO_ASSENSO = ?, DATA_ULTIMA_CERTIFICAZIONE = ?," +
          		" DATA_SCADENZA = ?, STATO_VERIFICA = ? " +
          		" where ID =? and ESITO_VERIFICA is not null and ESITO_VERIFICA <> 3 and ESITO_VERIFICA <> 4",
            new Object[] { mDataUltimaRichiesta,mDataSilenzioAssenso,mDataEmissione,dataScadenza,statoVerifica,idVerifica});
        }else{
          this.sqlManager.update("update VERIFICHE set DATA_ULTIMA_RICHIESTA = null,DATA_SILENZIO_ASSENSO = null," +
              " DATA_ULTIMA_CERTIFICAZIONE = null, DATA_SCADENZA = null, STATO_VERIFICA = null where ID =? ", new Object[] {idVerifica});
        }
      }else{//numCertificazioni >1
        if(mDataEmissione!=null && ((mDataUltimaRichiesta!=null && (mDataEmissione.after(mDataUltimaRichiesta) || mDataEmissione.equals(mDataUltimaRichiesta))) || mDataUltimaRichiesta==null)){
          Calendar calScadenza = Calendar.getInstance();
          calScadenza.setTime(mDataEmissione);
          calScadenza.add(Calendar.DAY_OF_MONTH,ggValiditaVerifica.intValue());
          Date dataScadenza=calScadenza.getTime();
          Long statoVerifica = this.calcolaStatoVerifica(idVerifica, dataScadenza, ggValiditaVerifica, ggAvvisoScadenza);
          this.sqlManager.update("update VERIFICHE set DATA_ULTIMA_RICHIESTA = ?,DATA_SILENZIO_ASSENSO = ?, DATA_ULTIMA_CERTIFICAZIONE = ?," +
                " DATA_SCADENZA = ?, STATO_VERIFICA = ? " +
                " where ID =? and ESITO_VERIFICA is not null and ESITO_VERIFICA <> 3 and ESITO_VERIFICA <> 4",
            new Object[] { mDataUltimaRichiesta,mDataSilenzioAssenso,mDataEmissione,dataScadenza,statoVerifica,idVerifica});
        }else{
          if(mDataSilenzioAssenso!=null){
            Calendar calScadenza = Calendar.getInstance();
            calScadenza.setTime(mDataSilenzioAssenso);
            calScadenza.add(Calendar.DAY_OF_MONTH,ggValiditaVerifica.intValue());
            Date dataScadenza=calScadenza.getTime();
            Long statoVerifica = this.calcolaStatoVerifica(idVerifica, dataScadenza, ggValiditaVerifica, ggAvvisoScadenza);
            this.sqlManager.update("update VERIFICHE set DATA_ULTIMA_RICHIESTA = ?,DATA_SILENZIO_ASSENSO = ?, DATA_ULTIMA_CERTIFICAZIONE = ?," +
                  " DATA_SCADENZA = ?, STATO_VERIFICA = ? " +
                  " where ID =? and ESITO_VERIFICA is not null and ESITO_VERIFICA <> 3 and ESITO_VERIFICA <> 4",
              new Object[] { mDataUltimaRichiesta,mDataSilenzioAssenso,mDataEmissione,dataScadenza,statoVerifica,idVerifica});
          }
        }
      }//numCertificazioni

    } catch (SQLException sqle) {
      throw new GestoreException(
          "Errore durante il calcolo della scadenza della verifica",
          "vart80.scadenza", sqle);
    }

    res[0] = outcome;
    res[1] = msgDetail;

    return res;

  }



  public void downloadFileAllegatoCifrato(String nomeFile, String idProgramma, Long idDocumento, String entita, Long idEntita, String uffint, HttpServletResponse response)
    throws IOException, SQLException, GeneralSecurityException {
    if (logger.isDebugEnabled())
      logger.debug("downloadFileAllegatoCifrato(" + nomeFile + "," + idProgramma + "," + idDocumento + "): inizio metodo");

      String cfein = (String) this.sqlManager.getObject("select cfein from uffint where codein=?", new Object[] {uffint});
      String keysess = (String) sqlManager.getObject("select keysess from documenti_verifiche where id = ?", new Object[]{idEntita});
      BlobFile fileAllegatoBlob = this.fileAllegatoDao.getFileAllegato(idProgramma, idDocumento);
      byte[] ff = fileAllegatoBlob.getStream();
      byte[] decodedSessionKey = this.decodeSessionKey(keysess, cfein);
      //instanzio il decoder
      Cipher cipher = SymmetricEncryptionUtils.getDecoder(decodedSessionKey, cfein);
      //decifro il file
      byte[] ffDecifrato = SymmetricEncryptionUtils.translate(cipher, ff);
      UtilityWeb.download(nomeFile, ffDecifrato, response);

    if (logger.isDebugEnabled())
      logger.debug("downloadFileAllegatoCifrato: fine metodo");
  }


  public SecretKey generaKey() throws NoSuchAlgorithmException, NoSuchProviderException{
    //genero la chiave
    KeyGenerator keyGenerator = KeyGenerator.getInstance(
        EncryptionConstants.SESSION_KEY_GEN_ALGORITHM,
        EncryptionConstants.SECURITY_PROVIDER);
        keyGenerator.init(128);
        SecretKey chiaveSimmetrica = keyGenerator.generateKey();

    return chiaveSimmetrica;
  }


  public String cifraKey(SecretKey chiaveSimmetrica,String keyCifratura)
    throws UnsupportedEncodingException, GeneralSecurityException{

    String encodedSessionKey = this.encodeSessionKey(chiaveSimmetrica.getEncoded(), keyCifratura);

    return encodedSessionKey;
  }


  /**
   * Cifra la chiave di sessione e la converte in formato Base64 per gestirne
   * la memorizzazione provvisoria in DB.
   *
   * @param sessionKey
   *            chiave di sessione
   * @param username
   *            login utente
   * @return chiave di sessione cifrata e convertita in base64
   */
  private String encodeSessionKey(byte[] sessionKey, String username)
          throws GeneralSecurityException, UnsupportedEncodingException {
      String chiave = null;

      byte[] chiaveProvvisoriaDecifratura = SymmetricEncryptionUtils.fill128bitKey(username);
      IvParameterSpec iv = new IvParameterSpec(chiaveProvvisoriaDecifratura);
      SecretKeySpec skeySpec = new SecretKeySpec(
              chiaveProvvisoriaDecifratura,
              EncryptionConstants.SESSION_KEY_GEN_ALGORITHM);
      Cipher cipher = Cipher
              .getInstance(EncryptionConstants.SESSION_KEY_ENCRYPTION_TRANSFORMATION);
      cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
      chiave = Base64.encodeBase64String(cipher.doFinal(sessionKey));
      return chiave;
  }

  private static byte[] decodeSessionKey(String sessionKey, String username)
  throws GeneralSecurityException, UnsupportedEncodingException {
    byte[] chiave = null;
    if (sessionKey != null) {
      // la chiave simmetrica di sessione viene criptata con una chiave
      // simmetrica AES dipendente dalla login impresa, e codificata in
      // Base64
      byte[] chiaveSessioneCifrataStringa = Base64
              .decodeBase64(sessionKey);
      byte[] chiaveProvvisoriaDecifratura = SymmetricEncryptionUtils.fill128bitKey(username);
      IvParameterSpec iv = new IvParameterSpec(
              chiaveProvvisoriaDecifratura);
      SecretKeySpec skeySpec = new SecretKeySpec(
              chiaveProvvisoriaDecifratura,
              EncryptionConstants.SESSION_KEY_GEN_ALGORITHM);
      Cipher cipher = Cipher
              .getInstance(EncryptionConstants.SESSION_KEY_ENCRYPTION_TRANSFORMATION);
      cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
      chiave = cipher.doFinal(chiaveSessioneCifrataStringa);
    }
    return chiave;
}


}
