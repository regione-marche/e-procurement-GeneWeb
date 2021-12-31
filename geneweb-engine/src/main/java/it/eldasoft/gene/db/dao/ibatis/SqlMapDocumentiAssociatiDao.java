package it.eldasoft.gene.db.dao.ibatis;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.commons.web.spring.SqlMapClientDaoSupportBase;
import it.eldasoft.gene.db.dao.DocumentiAssociatiDao;
import it.eldasoft.gene.db.domain.docass.DocumentoAssociato;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposer;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.sql.comp.SqlManager;

import java.util.HashMap;
import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * @author Luca Giacomazzo
 */
public class SqlMapDocumentiAssociatiDao extends SqlMapClientDaoSupportBase
    implements DocumentiAssociatiDao{

  public int getNumeroUtentiByCodiceUte(String codiceUtente)
      throws DataAccessException {
    return ((Integer)
        this.getSqlMapClientTemplate().queryForObject("getNumUtentiByC0AKEY1",
            codiceUtente)).intValue();
  }

  public void updateCodiceUtente(String codiceAttualeUtente,
      String codiceNuovoUtente) throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("codiceUtente", codiceAttualeUtente);
    hash.put("nuovoCodiceUtente", codiceNuovoUtente);
    this.getSqlMapClientTemplate().update("updateC0AKEY1", hash);
  }

  public List<?> getListaDocumentiAssociati(DocumentoAssociato docAss)
      throws DataAccessException {
    HashMap<String, String> hash = new HashMap<String, String>();
    if(docAss.getCodApp() != null)
      hash.put("c0acod", docAss.getCodApp());
    hash.put("c0aent", docAss.getEntita());
    hash.put("c0akey1", docAss.getCampoChiave1());
    hash.put("c0akey2", docAss.getCampoChiave2());
    hash.put("c0akey3", docAss.getCampoChiave3());
    hash.put("c0akey4", docAss.getCampoChiave4());
    hash.put("c0akey5", docAss.getCampoChiave5());
    return this.getSqlMapClientTemplate().queryForList("getListaDocumentiAssociati", hash);
  }

  public DocumentoAssociato getDocumentoAssociatoByChiaviEntitaTitolo(
      DocumentoAssociato docAss) throws SqlComposerException{
    SqlComposer composer = SqlManager.getComposer(ConfigManager.getValore(
        CostantiGenerali.PROP_DATABASE));

    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("c0aent", docAss.getEntita());
    hash.put("c0akey1", docAss.getCampoChiave1());
    hash.put("c0akey2", docAss.getCampoChiave2());
    hash.put("c0akey3", docAss.getCampoChiave3());
    hash.put("c0akey4", docAss.getCampoChiave4());
    hash.put("c0akey5", docAss.getCampoChiave5());
    if(docAss.getTitolo() != null && docAss.getTitolo().length() > 0){
      hash.put("operatoreUpper", composer.getFunzioneUpperCase());
      hash.put("titolo", docAss.getTitolo().toUpperCase());
    }

    return (DocumentoAssociato) this.getSqlMapClientTemplate().queryForObject(
        "documentoAssociatoByChiaviEntitaTitolo", hash);
  }

  public int getNumeroDocumentiAssociatiByPathNome(String pathDocAss,
      String nomeDocAss) throws DataAccessException, SqlComposerException{
    HashMap<String, String> hash = new HashMap<String, String>();
    hash.put("pathDocAss", pathDocAss.toUpperCase());
    hash.put("nomeDocAss", nomeDocAss.toUpperCase());
    SqlComposer composer = SqlManager.getComposer(ConfigManager.getValore(
        CostantiGenerali.PROP_DATABASE));
    hash.put("operatoreUpper", composer.getFunzioneUpperCase());

    Integer numeroDocAssByPathNome = (Integer)
      this.getSqlMapClientTemplate().queryForObject("getNumeroDocAssByPathNome", hash);

    return numeroDocAssByPathNome.intValue();
  }

  public DocumentoAssociato getDocumentoAssociatoById(long idDocAss)
      throws DataAccessException{
    return (DocumentoAssociato) this.getSqlMapClientTemplate().queryForObject(
        "documentoAssociatoById", new Long(idDocAss));
  }

  public void deleteDocumentoAssociatoById(long idDocAss) throws DataAccessException {
    this.getSqlMapClientTemplate().delete("deleteDocumentoAssociatoById",
        new Long(idDocAss));
    this.getSqlMapClientTemplate().delete("deleteWSAllegatoById",
        new Long(idDocAss));
  }

  public void insertDocAss(DocumentoAssociato docAss) throws DataAccessException{
    this.getSqlMapClientTemplate().insert("insertDocAss", docAss);
  }

  public void updateDocAss(DocumentoAssociato docAss) throws DataAccessException {
    this.getSqlMapClientTemplate().update("updateDocAss", docAss);
  }

}