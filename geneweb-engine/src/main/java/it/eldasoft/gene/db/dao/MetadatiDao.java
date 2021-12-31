/*
 * Created on 27-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao;

import it.eldasoft.utils.metadata.domain.Tabella;

import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * Interfaccia DAO per l'accesso ai dati delle tabelle di definizione della
 * struttura C0CAMPI e C0ENTIT.
 * 
 * @author Stefano.Sabbadin
 */
public interface MetadatiDao {

  /**
   * Estrae l'elenco degli schemi presenti nella banca dati
   * 
   * @return lista di oggetti di tipo
   *         {@link it.eldasoft.utils.metadata.domain.Schema}
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<?> getElencoSchemi() throws DataAccessException;

  /**
   * Estrae l'elenco dei nomi fisici delle tabelle presenti nella banca dati
   * 
   * @return lista di oggetti di tipo
   *         {@link it.eldasoft.utils.metadata.domain.NomeFisicoTabella}
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<?> getElencoNomiFisiciTabelle() throws DataAccessException;

  /**
   * Estrae i dati di una tabella a partire dal suo nome fisico
   * 
   * @param nomeFisico
   *        nome fisico della tabella
   * 
   * @return tabella individuata
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  Tabella getTabella(String nomeFisico) throws DataAccessException;

  /**
   * Estrae l'elenco delle chiavi esterne che referenziano la tabella in input
   * 
   * @param nomeFisico
   *        nome fisico della tabella
   * 
   * @return lista di oggetti di tipo
   *         {@link it.eldasoft.utils.metadata.domain.LegameTabelle}
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<?> getElencoChiaviEsterneReferenti(String nomeFisico)
      throws DataAccessException;

  /**
   * Estrae l'elenco dei campi della tabella in input
   * 
   * @param nomeFisico
   *        nome fisico della tabella
   * 
   * @return lista di oggetti di tipo
   *         {@link it.eldasoft.utils.metadata.domain.LegameTabelle}
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<?> getElencoCampiTabella(String nomeFisico) throws DataAccessException;

  /**
   * Estrae l'elenco di campi che soddisfano le condizioni di filtro in input
   * 
   * @param mnemonico
   *        mnemonico da ricercare
   * @param operatoreMnemonico
   *        operatore di confronto per il mnemonico da ricercare
   * @param descrizione
   *        descrizione da ricercare
   * @param operatoreDescrizione
   *        operatore di confronto per la descrizione da ricercare
   * @return lista di oggetti di tipo java.lang.String
   * 
   * @throws DataAccessException
   *         eccezione emessa dal framework Spring nel qual caso si siano
   *         verificati problemi durante l'accesso ai dati
   */
  List<?> getElencoMnemoniciPerRicerche(String mnemonico, 
      String operatoreMnemonico, String descrizione, String operatoreDescrizione)
          throws DataAccessException;

  /**
   * Metodo per l'estrazione del campo C0E_KEY della tabella C0ENTIT, a partire
   * dall'id, per risalire dalla tabella della vista delle ricerche base alla 
   * tabella fisica 
   * 
   * @param idC0entit
   * @return 
   * @throws DataAccessException
   */
  String getC0eKeyById(String idC0entit) throws DataAccessException;
}
