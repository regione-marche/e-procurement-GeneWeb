package it.eldasoft.gene.db.dao.ibatis;

import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.dao.FileAllegatoDao;
import it.eldasoft.gene.db.domain.BlobFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

public class SqlMapFileAllegatoDao extends SqlMapClientDaoSupportBase implements FileAllegatoDao {

  public BlobFile getFileAllegato(String idProgramma, Long idDocumento) throws DataAccessException {
    BlobFile blob = null;
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("idprg", idProgramma);
    params.put("iddocdig", idDocumento);
    blob = (BlobFile) this.getSqlMapClientTemplate().queryForObject("getFileAllegato", params);
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
