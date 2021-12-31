package it.eldasoft.gene.db.dao;

import it.eldasoft.gene.db.domain.BlobFile;

import java.util.HashMap;

import org.springframework.dao.DataAccessException;

public interface DocumentoWDISCALLDao {

  BlobFile getStream(HashMap params) throws DataAccessException;

}
