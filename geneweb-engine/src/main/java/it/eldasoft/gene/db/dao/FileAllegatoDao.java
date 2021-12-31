package it.eldasoft.gene.db.dao;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

import it.eldasoft.gene.db.domain.BlobFile;

public interface FileAllegatoDao {

  /**
   * Estrae il file allegato nella W_DOCDIG a partire dalla sua chiave.
   * 
   * @param idProgramma
   *        identificativo del programma
   * @param idDocumento
   *        identificativo del documento
   * @return oggetto contenente il bytearray
   * 
   * @throws DataAccessException
   */
  BlobFile getFileAllegato(String idProgramma, Long idDocumento) throws DataAccessException;
  
  /**
   * Estrae i file allegati secondo le chiavi in input
   * @param inputMap
   *        Mappa delle chiavi che deve contenere almeno le seguenti chiavi: idprg, digent, digkey1.<br>Le seguenti sono opzionali: digkey2,digkey3,digkey4,digkey5
   * @return la lista dei file allegati, se presenti.
   * @throws DataAccessException
   */
  BlobFile getFileAllegatoFromExternalReference(Map<String,String> inputMap) throws DataAccessException;


  /**
   * Inserisce un nuovo file allegato in W_DOCDIG
   * @param idprg
   * @param iddocdig
   * @param digogg
   * @throws DataAccessException
   */
  void insertFileAllegatoAssociaModello(String idprg, Long iddocdig, String digent, String digkey1, byte[] digogg) throws DataAccessException;

}
