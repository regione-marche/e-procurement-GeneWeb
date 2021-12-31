package it.eldasoft.gene.db.dao;

import it.eldasoft.gene.db.domain.docass.DocumentoAssociato;
import it.eldasoft.utils.sql.comp.SqlComposerException;

import java.util.List;

import org.springframework.dao.DataAccessException;

public interface DocumentiAssociatiDao {
  
  int getNumeroUtentiByCodiceUte(String codiceAttualeUtente) 
    throws DataAccessException;
  
  void updateCodiceUtente(String codiceAttualeUtente, String codiceNuovoUtente)
    throws DataAccessException;

  List<?> getListaDocumentiAssociati(DocumentoAssociato docAss) throws DataAccessException; 
  
  DocumentoAssociato getDocumentoAssociatoById(long idDocAss) throws DataAccessException;
  
  DocumentoAssociato getDocumentoAssociatoByChiaviEntitaTitolo(
      DocumentoAssociato docAss) throws SqlComposerException;
  
  int getNumeroDocumentiAssociatiByPathNome(String pathDocAss, String nomeDocAss)
    throws DataAccessException, SqlComposerException;
  
  void deleteDocumentoAssociatoById(long idDocAss) throws DataAccessException;
  
  void insertDocAss(DocumentoAssociato docAss) throws DataAccessException;
  
  void updateDocAss(DocumentoAssociato docAss) throws DataAccessException;
}