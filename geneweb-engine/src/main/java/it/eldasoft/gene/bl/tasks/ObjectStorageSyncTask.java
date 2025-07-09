/*
 * Created on 03/03/2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl.tasks;

import it.eldasoft.gene.bl.FileAllegatoManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.WebUtilities;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.gene.ws.objectstorage.ObjectStorageClient;
import it.eldasoft.gene.ws.objectstorage.ObjectStorageManager;
import it.eldasoft.gene.ws.objectstorage.UploadInfo;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.spring.SpringAppContext;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityDate;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class ObjectStorageSyncTask {

  static Logger       logger = Logger.getLogger(ObjectStorageSyncTask.class);

  private SqlManager  sqlManager;

  private ObjectStorageManager objectStorageManager;

  private FileAllegatoManager fileAllegatoManager;

  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }

  public void setObjectStorageManager(ObjectStorageManager objectStorageManager) {
    this.objectStorageManager = objectStorageManager;
  }

  public void setFileAllegatoManager(FileAllegatoManager fileAllegatoManager) {
    this.fileAllegatoManager = fileAllegatoManager;
  }


  /**
   * Task attivato per verificare se ci sono attivit&agrave; in scadenza per le quali va generato il promemoria.
   *
   * @throws GestoreException
   */
  @SuppressWarnings("unchecked")
  public void sincronizzaBucket() throws GestoreException {

    if (WebUtilities.isAppNotReady())
      return;

    if("1".equals(ConfigManager.getValore(CostantiGenerali.BUCKET_ABILITAZIONE))){
      String codapp = ConfigManager.getValore(CostantiGenerali.PROP_CODICE_APPLICAZIONE);
      logger.debug("sincronizzaBucket: inizio metodo, applicativo " + codapp);

    // Lettura dei dati dei file da caricare
    String selectDati = "select w.idprg, w.iddocdig, w.bucket_ref, w.bucket_uuid, b.id from w_bucketfilesadd b, w_docdig w " +
            "where w.idprg=b.idprg and w.iddocdig=b.iddocdig and b.esito is null order by id";
    try {
      List dati = sqlManager.getListVector(selectDati, new Object[]{});
      int countFileGestiti = 0;
      int countFileInErrore = 0;
      if (dati != null && !dati.isEmpty()) {
        ObjectStorageClient obs_client = new ObjectStorageClient(ConfigManager.getValore(CostantiGenerali.BUCKET_TENANTID), ConfigManager.getValore(CostantiGenerali.BUCKET_URL));
        for (int i = 0; i < dati.size(); i++) {
          UploadInfo uploadInfo = new UploadInfo();
          TransactionStatus status = sqlManager.startTransaction();
          try {
            String idprg = (String) SqlManager.getValueFromVectorParam(dati.get(i), 0).getValue();
            Long iddocdig = (Long) SqlManager.getValueFromVectorParam(dati.get(i), 1).getValue();
            String bucketRefOld = (String) SqlManager.getValueFromVectorParam(dati.get(i), 2).getValue();
            String bucketUuidOld = (String) SqlManager.getValueFromVectorParam(dati.get(i), 3).getValue();
            Long idW_BFA = (Long) SqlManager.getValueFromVectorParam(dati.get(i), 4).getValue();

            byte[] file = fileAllegatoManager.getFileAllegato(idprg, iddocdig).getStream();
            uploadInfo = obs_client.full_upload(file);
            sqlManager.update("update w_docdig set bucket_ref=?, bucket_uuid=?, digogg=null where idprg=? and iddocdig=?", new Object[]{uploadInfo.bucket_ref(), uploadInfo.uuid(), idprg, iddocdig});
            objectStorageManager.deleteFile(bucketRefOld, bucketUuidOld);
            sqlManager.update("update w_bucketfilesadd set esito=1 where id=?",new Object[]{idW_BFA});
            sqlManager.commitTransaction(status);
            countFileGestiti++;
          } catch (Exception e) {
            objectStorageManager.rollbackFile(uploadInfo.bucket_ref(), uploadInfo.uuid());
            logger.error(e);
            countFileInErrore++;
            sqlManager.rollbackTransaction(status);
          }
        }
      }
      logger.debug("sincronizzaBucket: fine metodo, applicativo " + codapp+".\nFile gestiti: "+countFileGestiti+". File in errore: "+countFileInErrore);
    } catch (SQLException e) {
      throw new GestoreException("Errore sincronizzazione dei file su bucket: applicativo " + codapp, null, e);
    }
  }
    }
}
