package it.eldasoft.gene.ws.objectstorage;/*
 * Created on 16/dec/24
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.utils.properties.ConfigManager;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class ObjectStorageManager {

  /** Logger Log4J di classe */
  static Logger            logger = Logger.getLogger(ObjectStorageManager.class);

  /**
   * Manager per la generazione delle chiavi
   */
  private GenChiaviManager genChiaviManager;

  /**
   * Dao per la gestione delle operazioni sul DB
   */
  private SqlManager sqlManager;

  /**
   * @param genChiaviManager
   *        genChiaviManager da settare internamente alla classe.
   */
  public void setGenChiaviManager(GenChiaviManager genChiaviManager) {
    this.genChiaviManager = genChiaviManager;
  }

  /**
   * @param sqlManager
   *        sqlManager da settare internamente alla classe.
   */
  public void setSqlManager(SqlManager sqlManager) {
    this.sqlManager = sqlManager;
  }


  /**
   * Utilizza la transazione corrente o ne apre una nuova: usata per eliminare il vecchio file in caso di update o per l'eliminazione.
   * Il valore di provenienza di provenienza del file corrisponderà a 1: objectstorage-ms
  */
  public void deleteFile(String bucket_ref, String bucket_uuid) {
    deleteFile(bucket_ref, bucket_uuid, null);
  }
  /**
   * Utilizza la transazione corrente o ne apre una nuova: usata per eliminare il vecchio file in caso di update o per l'eliminazione.
   * Qui è possibile specificare il valore di provenienza del file 1: objectstorage-ms, 2: m-largefiles
   */
  public void deleteFile(String bucket_ref, String bucket_uuid, Long provenienza) {
    delete(bucket_ref, bucket_uuid, provenienza);
  }

  /**
   * Utilizza usa transazione indipendente: usata per rimuovere il file caricato in caso di eccezione nella transazione principale.
   * Il valore di provenienza di provenienza del file corrisponderà a 1: objectstorage-ms
   */
  public void rollbackFile(String bucket_ref, String bucket_uuid) {
    rollbackFile(bucket_ref, bucket_uuid, null);
  }
  /**
   * Utilizza usa transazione indipendente: usata per rimuovere il file caricato in caso di eccezione nella transazione principale.
   * Qui è possibile specificare il valore di provenienza del file 1: objectstorage-ms, 2: m-largefiles
   */
  public void rollbackFile(String bucket_ref, String bucket_uuid, Long provenienza) {
    delete(bucket_ref, bucket_uuid, provenienza);
  }

  /**
   * Le istruzioni di questo metodo, ovvero la scrittura in W_BUCKETFILEDELETE sono eseguite indipendentemente dall'abilitazione della gestione file su bucket ('objectStorage.abilita' = 1)
   * L'esecuzione avviene se e solo se esistono dei riferimenti al bucket.
   * Se provenienza non è valorizzato, viene utilizzato il valore di default 1 (Gestione file su Bucket)
   * @param bucket_ref
   * @param bucket_uuid
   * @param provenienza 1: objectstorage-ms, 2: m-largefiles
   */
  private void delete(String bucket_ref, String bucket_uuid, Long provenienza) {
    if(StringUtils.isNotBlank(bucket_ref) && StringUtils.isNotBlank(bucket_uuid)) {
      if(provenienza == null) {
        provenienza = 1L;
      }
      int id = this.genChiaviManager.getNextId("W_BUCKETFILESDELETE");
      try {
        sqlManager.update("insert into W_BUCKETFILESDELETE (ID,BUCKET_REF,BUCKET_UUID,PROVENIENZA) values (?,?,?,?)", new Object[]{id,bucket_ref,bucket_uuid, provenienza});
      } catch (SQLException e) {
        logger.error("Errore nell'inserimento del record "+bucket_ref+"|"+bucket_uuid+" nella tabella W_BUCKETFILESDELETE");
      }
    }
  }
  /**
   * Le istruzioni di questo metodo, ovvero la scrittura in W_BUCKETFILEADD, sono eseguite solo se è abilitata la gestione file su bucket ('objectStorage.abilita' = 1)
   * L'esecuzione avviene se e solo se esiste il record in w_docdig
   * @param idprg
   * @param iddocdig
   */

  public void addFile(String idprg, Long iddocdig){
    if("1".equals(ConfigManager.getValore(CostantiGenerali.BUCKET_ABILITAZIONE)) && StringUtils.isNotBlank(idprg) && iddocdig!=null) {
      try {
        Long count = (Long) sqlManager.getObject("select count(*) from W_BUCKETFILESADD where idprg=? and iddocdig=? and esito is null", new Object[]{idprg,iddocdig});
        if(count==0) {
          int id = this.genChiaviManager.getNextId("W_BUCKETFILESADD");
          sqlManager.update("insert into W_BUCKETFILESADD (ID,IDPRG,IDDOCDIG) values (?,?,?)", new Object[]{id, idprg, iddocdig});
        }
      } catch (SQLException e) {
        logger.error("Errore nell'inserimento del record "+idprg+"|"+iddocdig+" nella tabella W_BUCKETFILESDELETE");
      }
    }
  }

  public UploadInfo getUploadInfo(String query, Object[] parameters){
    UploadInfo uploadInfo = new UploadInfo();
    try {
      List v = this.sqlManager.getListVector(query,parameters);
      if(v!=null && !v.isEmpty()) {
        uploadInfo.setBucket_ref((String) SqlManager.getValueFromVectorParam(v.get(0), 0).getValue());
        uploadInfo.setUuid((String) SqlManager.getValueFromVectorParam(v.get(0), 1).getValue());
      }
    } catch (SQLException e) {
      logger.error("Errore nel recupero delle informazioni attaverso la query "+query+" con parametri "+Arrays.stream(parameters).map(String::valueOf).collect(Collectors.joining(", ")));
    }
    return uploadInfo;
  }
}
