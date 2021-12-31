package it.eldasoft.gene.db.dao.ibatis;

import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.dao.DocumentoWDISCALLDao;
import it.eldasoft.gene.db.domain.BlobFile;

import java.util.HashMap;

import org.springframework.dao.DataAccessException;

public class SqlMapDocumentoWDISCALLDao extends SqlMapClientDaoSupportBase implements DocumentoWDISCALLDao {

  public BlobFile getStream(HashMap params) throws DataAccessException {
    return (BlobFile) this.getSqlMapClientTemplate().queryForObject("getStream", params);
  }

}
