/*
 * Created on 21-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.db.dao;

import it.eldasoft.gene.db.dao.jdbc.ListaDati;
import it.eldasoft.gene.db.dao.jdbc.ListaDatiPaginati;
import it.eldasoft.gene.db.dao.jdbc.ParametroStmt;
import it.eldasoft.utils.metadata.domain.Campo;

import org.springframework.dao.DataAccessException;

/**
 * Dao per la gestione di interazioni generiche sul database. I metodi definiti
 * nell'interfaccia prendono in input l'SQL (in formato adatto alle
 * PreparedStatement) nel dialetto del DBMS, i parametri da valorizzare, e
 * l'elenco dei campi estratti così come sono definiti nella C0CAMPI in modo da
 * effettuare opportuni cast delle informazioni
 * 
 * @author Stefano.Sabbadin
 * 
 */
public interface QueryDao {

  /**
   * Estrae i dati a partire dalla select in input
   * 
   * @param sql
   *        Stringa SQL da eseguire
   * @param parametri
   *        parametri da valorizzare
   * @param campiEstratti
   *        definizioni dei campi estratti con la query
   * @return lista contenente una hash per ogni record, in cui ogni chiave è il
   *         nome del campo estratto nella query, e il valore è il valore della
   *         colonna nel DB
   * @throws DataAccessException, QueryDaoException
   */
  public ListaDati getDatiSelect(String sql, ParametroStmt[] parametri,
      Campo[] campiEstratti) throws DataAccessException, QueryDaoException;

  /**
   * Estrae dalla base dati un numero di record  minore o uguale al numero
   * massimo di record estraibili a partire dalla select in input 
   * 
   * @param sql
   *        Stringa SQL da eseguire
   * @param parametri
   *        parametri da valorizzare
   * @param campiEstratti
   *        definizioni dei campi estratti con la query
   * @param massimoNumeroRecordEstraibili
   *        numero massimo dei record estraibili
   * @param emettiEccezioneTroppiRecordEstratti
   *        se valorizzato a true e viene estratto un numero di record maggiore
   *        del numero massimo di record il metodo ritorna una QueryDaoException, 
   *        se valorizzato a false e viene estratto un numero di record maggiore
   *        del numero massimo di record il metodo ritorna la lista dei dati
   *        estratti
   * @return lista contenente una hash per ogni record, in cui ogni chiave è il
   *         nome del campo estratto nella query, e il valore è il valore della
   *         colonna nel DB. La lista ha una dimensione massima pari al
   *         argomento 'massimoNumeroRecordEstraibili', se tale parametro è 
   *         maggiore di 0
   * @throws DataAccessException, QueryDaoException
   */
  public ListaDati getDatiSelect(String sql, ParametroStmt[] parametri,
      Campo[] campiEstratti, int massimoNumeroRecordEstraibili,
      boolean emettiEccezione)
    throws DataAccessException, QueryDaoException;
  
  /**
   * Estrae la i-esima pagina dei dati con un numero di record pari all'argomento
   * 'numeroRecordPerPagina' a partire dalla select in input 
   * 
   * @param sql
   *        Stringa SQL da eseguire
   * @param parametri
   *        parametri da valorizzare
   * @param campiEstratti
   *        definizioni dei campi estratti con la query
   * @param numeroPagina
   *        numero della pagina da visualizzare
   * @param numeroRecordPerPagina
   *        numero di record da visualizzare per pagina
   * @return lista contenente una hash per ogni record, in cui ogni chiave è il
   *         nome del campo estratto nella query, e il valore è il valore della
   *         colonna nel DB.
   * 
   * @throws DataAccessException, QueryDaoException
   */
  public ListaDatiPaginati getDatiSelect(String sql, ParametroStmt[] parametri,
      Campo[] campiEstratti, int numeroPagina, int numeroRecordPerPagina)
    throws DataAccessException, QueryDaoException;
  
  /**
   * Estrae dalla base dati un numero di record  minore o uguale al numero
   * massimo di record estraibili a partire dalla select in input 
   * 
   * @param sql
   *        Stringa SQL da eseguire
   * @param parametri
   *        parametri da valorizzare
   * @param campiEstratti
   *        definizioni dei campi estratti con la query
   * @param numeroPagina
   *        numero della pagina da visualizzare
   * @param numeroRecordPerPagina
   *        numero di record da visualizzare per pagina
   * @param massimoNumeroRecordEstraibili
   *        numero massimo dei record estraibili
   * @return lista contenente una hash per ogni record, in cui ogni chiave è il
   *         nome del campo estratto nella query, e il valore è il valore della
   *         colonna nel DB. La lista presenta la i-esima pagina dei dati estraibili
   *         ed ha una dimensione massima pari all'argomento 'numeroRecordPerPagina', 
   * 
   * @throws DataAccessException, QueryDaoException
   */
  public ListaDatiPaginati getDatiSelect(String sql, ParametroStmt[] parametri,
      Campo[] campiEstratti, int numeroPagina, int numeroRecordPerPagina,
      int massimoNumeroRecordEstraibili)
    throws DataAccessException, QueryDaoException;
}
