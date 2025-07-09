package it.eldasoft.gene.db.dao.ibatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.gene.ws.objectstorage.ObjectStorageClient;
import it.eldasoft.utils.properties.ConfigManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;

import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.dao.FileAllegatoDao;
import it.eldasoft.gene.db.domain.BlobFile;

public class SqlMapFileAllegatoDao extends SqlMapClientDaoSupportBase implements FileAllegatoDao {

  public BlobFile getFileAllegato(String idProgramma, Long idDocumento) throws DataAccessException, GestoreException {
    BlobFile blob = null;
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("idprg", idProgramma);
    params.put("iddocdig", idDocumento);
    blob = (BlobFile) this.getSqlMapClientTemplate().queryForObject("getFileAllegato", params);
    if(blob != null && ArrayUtils.isEmpty(blob.getStream()) && StringUtils.isNotBlank(blob.getBucketRef()) && StringUtils.isNotBlank(blob.getBucketUuid())){
      ObjectStorageClient obs_client = new ObjectStorageClient(ConfigManager.getValore(CostantiGenerali.BUCKET_TENANTID),ConfigManager.getValore(CostantiGenerali.BUCKET_URL));
      blob.setStream(obs_client.download(blob.getBucketRef(),blob.getBucketUuid()));
    }
    return blob;
  }

  public void insertFileAllegatoAssociaModello(String idprg, Long iddocdig, String digent, String digkey1, byte[] digogg) {
    HashMap<String, Object> hash = new HashMap<String, Object>();
    hash.put("idprg", idprg);
    hash.put("iddocdig", iddocdig);
    hash.put("digent", digent);
    hash.put("digkey1", digkey1);
    hash.put("digogg", digogg);
    this.getSqlMapClientTemplate().insert("insertFileAllegatoAssociaModello", hash);
  }


  @Override
  public BlobFile getFileAllegatoFromExternalReference(Map<String, String> inputMap) throws DataAccessException {
    return (BlobFile) this.getSqlMapClientTemplate().queryForObject("getFileAllegatoFromExternalReference", inputMap);
  }

}
