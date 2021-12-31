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
package it.eldasoft.gene.db.dao.jdbc;

import it.eldasoft.gene.db.dao.QueryDao;
import it.eldasoft.gene.db.dao.QueryDaoException;
import it.eldasoft.utils.metadata.domain.Campo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;

/**
 * Classe che via DAO si interfaccia con la base dati per l'estrazione di query
 * generiche
 *
 * @author Stefano.Sabbadin
 */
public class QueryDaoJdbc implements QueryDao {

  /** Definizione del fetch size di default nell'esecuzione di una query */
  private static final int DEFAULT_FETCH_SIZE    = 100;

  /** Datasource per l'accesso ai dati */
  private DataSource dataSource;

  /**
   * @param dataSource
   *        dataSource da settare internamente alla classe.
   */
  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  /**
   * @throws QueryDaoException
   * @see it.eldasoft.gene.db.dao.QueryDao#getDatiSelect(java.lang.String,
   *      it.eldasoft.gene.db.dao.jdbc.ParametroStmt[],
   *      it.eldasoft.utils.metadata.domain.Campo[])
   */
  public ListaDati getDatiSelect(String sql, ParametroStmt[] parametri,
      Campo[] campiEstratti) throws DataAccessException, QueryDaoException {
    return this.getDatiSelect(sql, parametri, campiEstratti, 0, false);
  }

  /**
   * @throws QueryDaoException
   * @see it.eldasoft.gene.db.dao.QueryDao#getDatiSelect(java.lang.String,
   *      it.eldasoft.gene.db.dao.jdbc.ParametroStmt[],
   *      it.eldasoft.utils.metadata.domain.Campo[],
   *      int maxNumeroRecordEstraibili
   *      )
   */
  public ListaDati getDatiSelect(String sql, ParametroStmt[] parametri,
      Campo[] campiEstratti, int numeroMassimoRecordEstraibili,
      boolean emettiEccezione)
        throws DataAccessException, QueryDaoException {

    //Controllo dei gli argomenti in ingresso:
    if(numeroMassimoRecordEstraibili < 0)
      throw new QueryDaoException(QueryDaoException.CODICE_ERRORE_MAX_RECORD_ESTRAIBILI_NON_VALIDO);

    OggettoQuery pq = new OggettoQuery(this.dataSource, sql, parametri,
        campiEstratti);

    Object[] oa = new Object[parametri.length];
    for (int i = 0; i < parametri.length; i++) {
      oa[i] = parametri[i].getValoreObject();
    }

    if(numeroMassimoRecordEstraibili > 0)
      pq.setMaxRows(numeroMassimoRecordEstraibili + 1);

    pq.setFetchSize(DEFAULT_FETCH_SIZE);

    List<?> lista = pq.execute(oa);

    if(lista != null && lista.size() > 0) {
      if(lista.size() > numeroMassimoRecordEstraibili && emettiEccezione)
        throw new QueryDaoException(QueryDaoException.CODICE_ERRORE_NUMERO_RECORD_ESTRATTI_MAGGIORE_VALORE_MAX);
      else {
        ListaDati listaDatiEstratti = new ListaDati(lista, lista.size(), pq.getListaCampi());
        return listaDatiEstratti;
      }
    } else return new ListaDati();
  }

  public ListaDatiPaginati getDatiSelect(String sql, ParametroStmt[] parametri,
      Campo[] campiEstratti, int numeroPagina, int numeroRecordPerPagina)
    throws DataAccessException, QueryDaoException {

    return this.getDatiSelect(sql, parametri, campiEstratti, numeroPagina,
        numeroRecordPerPagina, 0);
  }

  public ListaDatiPaginati getDatiSelect(String sql, ParametroStmt[] parametri,
      Campo[] campiEstratti, int numeroPagina, int numeroRecordPerPagina,
      int numeroMassimoRecordEstraibili) throws DataAccessException, QueryDaoException {

    if(numeroPagina < 1)
      throw new QueryDaoException(QueryDaoException.CODICE_ERRORE_NUMERO_PAGINA_NON_VALIDO);
    if(numeroRecordPerPagina < 1)
      throw new QueryDaoException(QueryDaoException.CODICE_ERRORE_NUMERO_RECORD_PER_PAGINA_NON_VALIDO);

    ListaDati listaDatiEstratti = this.getDatiSelect(sql, parametri,
        campiEstratti, numeroMassimoRecordEstraibili, true);

    if(listaDatiEstratti != null                    &&
       listaDatiEstratti.getListaDati() != null     &&
       listaDatiEstratti.getListaDati().size() > 0){
      List<?> lista = listaDatiEstratti.getListaDati();

      ListaDatiPaginati listaPaginata = new ListaDatiPaginati();
      listaPaginata.setNumeroTotaleRecord(listaDatiEstratti.getNumeroTotaleRecord());
      listaPaginata.setArrayCampi(listaDatiEstratti.getArrayCampi());

      if(lista.size() <= numeroRecordPerPagina){
        int subListStartIndex = (numeroPagina - 1) * numeroRecordPerPagina;
        if(subListStartIndex > lista.size())
          throw new QueryDaoException(QueryDaoException.CODICE_ERRORE_NUMERO_PAGINA_NON_VALIDO);

        listaPaginata.setNumeroPagina(1);
        //listaPaginata.setNumeroTotalePagine(1);
        listaPaginata.setNumeroRecordPerPagina(lista.size());
        listaPaginata.setListaDati(lista);
      } else {
        int subListStartIndex = (numeroPagina - 1) * numeroRecordPerPagina;
        if(subListStartIndex > lista.size())
          throw new QueryDaoException(QueryDaoException.CODICE_ERRORE_NUMERO_PAGINA_NON_VALIDO);

        listaPaginata.setNumeroPagina(numeroPagina);
        listaPaginata.setNumeroRecordPerPagina(numeroRecordPerPagina);
        //listaPaginata.setNumeroTotalePagine(
        //    lista.size() % numeroRecordPerPagina == 0 ?
        //        lista.size() / numeroRecordPerPagina : lista.size() / numeroRecordPerPagina +1);

        int subListEndIndex = (numeroPagina * numeroRecordPerPagina);
        if(subListEndIndex > lista.size())
          subListEndIndex = lista.size();
        listaPaginata.setListaDati(lista.subList(subListStartIndex, subListEndIndex));
      }
      return listaPaginata;
    } else
      return new ListaDatiPaginati();
  }

  /**
   * Inner class per la gestione dell'estrazione dei dati da una query
   *
   * @author Stefano.Sabbadin
   */
  class OggettoQuery extends MappingSqlQuery {

    /**
     * Definizione dei campi da estrarre
     */
    private Campo[] campiEstratti;
    private Campo[] listaCampi;

    /**
     * @return the campiEstratti
     */
    public Campo[] getCampiEstratti() {
      return campiEstratti;
    }

    /**
     * @return the listaCampi
     */
    public Campo[] getListaCampi() {
      return listaCampi;
    }

    /**
     * Costruttore
     *
     * @param ds
     *        datasource da utilizzare
     * @param sql
     *        SQL da eseguire
     * @param parametri
     *        elenco parametri
     * @param campiEstratti
     *        definizione campi da estrarre
     */
    OggettoQuery(DataSource ds, String sql, ParametroStmt[] parametri, Campo[] campiEstratti) {
      super(ds, sql);
      this.campiEstratti = campiEstratti;
      if (parametri != null) {
      for (int i = 0; i < parametri.length; i++) {
          super.declareParameter(new SqlParameter(parametri[i].getTipoDatoDB()));
        }
      }
      compile();
    }

    /**
     * Esegue il mapping di una riga di dati estratti in una Hash
     *
     * @see org.springframework.jdbc.object.MappingSqlQuery#mapRow(java.sql.ResultSet,
     *      int)
     */
    @Override
    protected Object mapRow(ResultSet rs, int rowNum) throws SQLException {
      HashMap<String, Object> map = new HashMap<String, Object>();

      if (this.campiEstratti != null ) {
        for (int i = 0; i < this.campiEstratti.length; i++) {
          map.put(rs.getMetaData().getColumnLabel(i + 1),
              this.campiEstratti[i].getObjectResultSet(rs, (i + 1)));
        }
        this.listaCampi = this.campiEstratti;
      } else {
        for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
          if (this.listaCampi == null) {
            this.listaCampi = new Campo[rs.getMetaData().getColumnCount()];
          }

          String columnLabel = rs.getMetaData().getColumnLabel(i + 1);

          switch (rs.getMetaData().getColumnType(i+1)) {
          case java.sql.Types.DATE:
            map.put(columnLabel, rs.getDate(i+1));
            this.listaCampi[i] = new Campo("", "", columnLabel, columnLabel, columnLabel, true, Campo.TIPO_DATA);
            break;
          case java.sql.Types.TIMESTAMP:
            map.put(columnLabel, rs.getTimestamp(i+1));
            this.listaCampi[i] = new Campo("", "", columnLabel, columnLabel, columnLabel, true, Campo.TIPO_TIMESTAMP);
            break;
          case java.sql.Types.INTEGER:
          case java.sql.Types.NUMERIC:
          case java.sql.Types.BIGINT:
          case java.sql.Types.SMALLINT:
          case java.sql.Types.TINYINT:
            if (rs.getString(i+1) != null) {
              if (rs.getMetaData().getScale(i+1) > 0) {
                map.put(columnLabel, new Double(rs.getDouble(i+1)));
                Campo tmpCampo = new Campo( "", "", columnLabel, columnLabel, columnLabel, true, Campo.TIPO_DECIMALE);
                tmpCampo.setDecimali(rs.getMetaData().getScale(i+1));
                this.listaCampi[i] = tmpCampo;
              } else {
                map.put(columnLabel, new Integer(rs.getInt(i+1)));
                this.listaCampi[i] = new Campo("", "", columnLabel, columnLabel, columnLabel, true, Campo.TIPO_INTERO);
              }
            } else {
              if (rs.getMetaData().getScale(i+1) > 0) {
                Campo tmpCampo = new Campo( "", "", columnLabel, columnLabel, columnLabel, true, Campo.TIPO_DECIMALE);
                tmpCampo.setDecimali(rs.getMetaData().getScale(i+1));
                this.listaCampi[i] = tmpCampo;
              } else {
                this.listaCampi[i] = new Campo("", "", columnLabel, columnLabel, columnLabel, true, Campo.TIPO_INTERO);
              }
            }

            break;
          case java.sql.Types.DECIMAL:
          case java.sql.Types.DOUBLE:
            if (rs.getString(i+1) != null) {
              map.put(columnLabel, new Double(rs.getDouble(i+1)));
            }
            Campo tmpCampo = new Campo( "", "", columnLabel, columnLabel, columnLabel, true, Campo.TIPO_DECIMALE);
            tmpCampo.setDecimali(rs.getMetaData().getScale(i+1));

            this.listaCampi[i] = tmpCampo;
            break;
          case java.sql.Types.FLOAT:
            if (rs.getString(i+1) != null) {
              map.put(columnLabel, new Double(rs.getFloat(i+1)));
            }
            Campo tmpCampo1 = new Campo( "", "", columnLabel, columnLabel, columnLabel, true, Campo.TIPO_DECIMALE);
            tmpCampo1.setDecimali(rs.getMetaData().getScale(i+1));

            this.listaCampi[i] = tmpCampo1;
            break;

          case java.sql.Types.CLOB:
            if (rs.getString(i+1) != null) {
              map.put(columnLabel, rs.getString(i+1));
            }
            this.listaCampi[i] = new Campo("", "", columnLabel, columnLabel, columnLabel, true, Campo.TIPO_STRINGA);
            break;
          case java.sql.Types.BLOB:
            this.listaCampi[i] = new Campo("", "", columnLabel, columnLabel, columnLabel, false, Campo.TIPO_STRINGA);

          case java.sql.Types.LONGVARCHAR:
          case java.sql.Types.VARCHAR:
            if (rs.getString(i+1) != null) {
              map.put(columnLabel, rs.getString(i+1));
            }
            this.listaCampi[i] = new Campo("", "", columnLabel, columnLabel, columnLabel, true, Campo.TIPO_STRINGA);
            break;

          default:
            break;
          }
        }
      }
      return map;
    }
  }

}
