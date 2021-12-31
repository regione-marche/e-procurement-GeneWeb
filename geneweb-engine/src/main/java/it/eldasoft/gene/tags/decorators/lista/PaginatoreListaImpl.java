/*
 * Created on Nov 16, 2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.lista;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.sql.sqlparser.JdbcColumn;
import it.eldasoft.gene.db.sql.sqlparser.JdbcExpression;
import it.eldasoft.gene.db.sql.sqlparser.JdbcExpressionSort;
import it.eldasoft.gene.db.sql.sqlparser.JdbcFrom;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.db.sql.sqlparser.JdbcSqlSelect;
import it.eldasoft.gene.db.sql.sqlparser.JdbcWhere;
import it.eldasoft.gene.tags.decorators.trova.FormTrovaTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityObject;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
/**
 * Classe che gestisce la paginazione di una lista
 *
 * @author cit_franceschin
 *
 */
public class PaginatoreListaImpl {

  // costanti estratte così come sono dalla libreria open source displaytag

  public static final String PROPERTY_STRING_PAGING_PAGE_LINK       = "paging.banner.page.link";

  public static final String PROPERTY_STRING_PAGING_BANNER_DISABLED = "paging.banner.disabled";

  public static final String PROPERTY_STRING_PAGING_BANNER_FULL     = "paging.banner.full";

  public static final String PROPERTY_STRING_PAGING_BANNER_LAST     = "paging.banner.last";

  public static final String PROPERTY_STRING_PAGING_BANNER_FIRST    = "paging.banner.first";

  public static final String PROPERTY_STRING_PAGING_PAGE_SPARATOR   = "paging.banner.page.separator";

  public static final String PROPERTY_STRING_PAGING_FOUND_SOMEITEMS = "paging.banner.some_items_found";

  public static final String PROPERTY_STRING_PAGING_FOUND_ALLITEMS  = "paging.banner.all_items_found";

  public static final String PROPERTY_STRING_PAGING_BANNER_ONEPAGE  = "paging.banner.onepage";

  public static final String PROPERTY_STRING_PAGING_FOUND_ONEITEM   = "paging.banner.one_item_found";

  public static final String PROPERTY_STRING_PAGING_ITEMS_NAME      = "paging.banner.items_name";

  public static final String PROPERTY_STRING_PAGING_ITEM_NAME       = "paging.banner.item_name";

  private static final int   NUMERO_PAGINE_LINK                     = 8;

  public static final String HIDDEN_CURRENT_PG                      = "pgCorrente";
  public static final String HIDDEN_GOTO_PG                         = "pgVaiA";
  public static final String HIDDEN_LAST_VALUE                      = "pgLastValori";
  public static final String HIDDEN_SORT                            = "pgSort";
  public static final String HIDDEN_LAST_SORT                       = "pgLastSort";

  private static Logger      logger                                 = Logger.getLogger(PaginatoreListaImpl.class);

  /** Nomero di righe totali */
  private int                rowCount;
  /** Numero di righe per pagina */
  private final int                numRowForPage;
  /** Numero di pagina totali */
  private int                pageCount;
  /** Manager per l'esecuzione di select SQL */
  private SqlManager         sqlManager;

  /** Elenco degli ordinamenti */
  private JdbcExpressionSort orders[];

  /** Valori dell'ultima riga precedenti */
  private JdbcParametro      valFirstRow[]                          = null;

  /** Pagina corrente */
  private int                curPage;
  /** From sulla lista */
  private JdbcFrom           from;
  /** Where sualla lista */
  private JdbcWhere          where;

  /** Ultimo ordinamento */
  private final String             lastSort;

  private List               valori                                 = null;

  /**
   * Oggetto che gestisce la comparazione tra due righe della lista
   *
   * @author marco.franceschin
   *
   */
  private class ComparatorRows implements Comparator {

    private final int sortElement[];

    public ComparatorRows(String elencoCampi) {
      String campiSort[] = UtilityTags.stringToArray(elencoCampi, ';');
      Vector sElements = new Vector();

      for (int i = 0; i < campiSort.length; i++) {
        if (campiSort[i] != null && campiSort[i].length() > 0) {
          boolean desc = campiSort[i].charAt(0) == '!';
          if (desc) campiSort[i] = campiSort[i].substring(1);
          // Elimino anche cla C
          campiSort[i] = campiSort[i].substring(1);
          int numCampo = Integer.parseInt(campiSort[i]);
          if (desc) numCampo *= -1;
          sElements.add(new Integer(numCampo));

        }
      }
      sortElement = new int[sElements.size()];
      for (int i = 0; i < sElements.size(); i++)
        sortElement[i] = ((Integer) sElements.get(i)).intValue();
    }

    public int compare(Object arg0, Object arg1) {
      Vector v1 = (Vector) arg0;
      Vector v2 = (Vector) arg1;
      if (v1 != null && v2 != null) {
        for (int i = 0; i < this.sortElement.length; i++) {
          int numCampo = sortElement[i];
          boolean asc = sortElement[i] >= 0;
          if (!asc) numCampo = -1 * numCampo;
          numCampo--;
          int ret = 0;
          if (v1.get(numCampo) instanceof JdbcParametro) {
            JdbcParametro par1 = (JdbcParametro) v1.get(numCampo);
            JdbcParametro par2 = (JdbcParametro) v2.get(numCampo);
            ret = par1.compare(par2);
          } else if (v1.get(numCampo) instanceof String) {
            try {
              Object r = UtilityObject.invoke(v1.get(numCampo), "compareTo",
                  new Object[] { v2.get(numCampo) });
              if (r instanceof Integer) ret = ((Integer) r).intValue();
            } catch (Exception e) {
              // No do errore perche significa che non esiste la funzione
            }

          }
          if (ret > 0) {
            if (asc)
              return 1;
            else
              return -1;
          } else if (ret < 0) {
            if (asc)
              return -1;
            else
              return 1;
          }
        }

      } else if (v1 == null) {
        return -1;
      } else
        return 1;
      return 0;
    }

  }

  /**
   * Costruttore di default per la paginazione di una lista
   *
   * @param from
   *        From ler la selezione sulla lista. Nella from deve contenere anche
   *        le eventuali Join Left (se il campo d'ordinamento è esterno alla
   *        tabella principale)
   * @param where
   *        Where di filtro
   * @param elencoCampiSort
   *        elenco contenente tutti e soli i campi da utilizzare per
   *        l'ordinamento. I campi sono divisi da ; (nome del campo è il nome
   *        fisico) e se l'ordinamento è ascendente deve avere ! davanti
   * @param campiOrdiEKey
   *        elenco contenente tutti i campi dell'ordinamento e i campi chiave.
   *        Le regole sono le medesime del campo elencoCampiSort
   * @param numRow
   *        Numero di righe per pagina
   * @param sql
   *        sql manager
   * @param session
   *        sessione http
   */
  public PaginatoreListaImpl(JdbcFrom from, JdbcWhere where,
      String elencoCampiSort, String campiOrdiEKey, int numRow, SqlManager sql, HttpSession session)
      throws JspException {
    this(from, where, elencoCampiSort, campiOrdiEKey, null, numRow, sql, session);
  }
  /**
   * Costruttore per la paginazione di una lista con distinct; a differenza del
   * costruttore precedente viene introdotto un parametro aggiuntivo valorizzato
   * solo nel caso in cui la select vada fatta considerando le distinte righe
   * estratte
   *
   * @param from
   *        From ler la selezione sulla lista. Nella from deve contenere anche
   *        le eventuali Join Left (se il campo d'ordinamento è esterno alla
   *        tabella principale)
   * @param where
   *        Where di filtro
   * @param elencoCampiSort
   *        elenco contenente tutti e soli i campi da utilizzare per
   *        l'ordinamento. I campi sono divisi da ; (nome del campo è il nome
   *        fisico) e se l'ordinamento è ascendente deve avere ! davanti
   * @param campiOrdiEKey
   *        elenco contenente tutti i campi dell'ordinamento e i campi chiave.
   *        Le regole sono le medesime del campo elencoCampiSort
   * @param campiDaEstrarre
   *        elenco contenente tutti i campi lista da estrarre nella select
   * @param numRow
   *        Numero di righe per pagina
   * @param sql
   *        sql manager
   * @param session
   *        sessione http
   */
  public PaginatoreListaImpl(JdbcFrom from, JdbcWhere where,
      String elencoCampiSort, String campiOrdiEKey, Vector campiDaEstrarre, int numRow, SqlManager sql, HttpSession session)
      throws JspException {
    if (numRow < 0) numRow = 0;
    this.lastSort = elencoCampiSort;
    // Calcolo del numero di righe
    this.sqlManager = sql;
    this.numRowForPage = numRow;
    this.from = from;
    this.where = where;

    try {
      // Conto il numero di righe a seconda della tipologia di input, count
      // semplice o count di una lista con elementi distinti (in tal caso si
      // considerano i dati estratti)
      String queryCount = null;
      if (campiDaEstrarre == null) {
       queryCount =  "select count(*) "
         + from.toString()
         + where.toString(true);
      } else {
        String elencoCampiDistinti = "";
        for (Enumeration en = campiDaEstrarre.elements(); en.hasMoreElements();) {
          CampoListaTagImpl cell = (CampoListaTagImpl) en.nextElement();
          if (!cell.isCampoFittizio() && cell.getCampo() != null) {
            if (!elencoCampiDistinti.equals("")) elencoCampiDistinti += ",";
            elencoCampiDistinti += cell.getCampo();
          }
        }
        queryCount = "select count(*) from (select distinct " + elencoCampiDistinti + "  "
        + from.toString()
        + where.toString(true) + " ) newTable";
      }
      Vector ret = sqlManager.getVector(queryCount,
          SqlManager.getObjectFromPram(where.getParametri()));
      Object countRet = ((JdbcParametro) ret.get(0)).getValue();
      if (countRet instanceof Long)
        rowCount = ((Long) countRet).intValue();
      else if (countRet instanceof Double)
        rowCount = ((Double) countRet).intValue();
      // Se il numero di righe per pagina è attivo allora calcola anche il
      // numero di pagine
      if (numRow > 0) {
        // Calcolo anche il numero delle pagine
        pageCount = (int) (rowCount / (long) numRow);
        // Se avanzano un po di righe allora il numero di pagina deve essere
        // incrementato di 1
        if ((rowCount % numRow) > 0) pageCount++;
      } else
        this.pageCount = 0;
    } catch (SQLException e) {
      throw new JspException(
          "PaginatoreListaImpl: Errore nel calcolo del numero di righe", e);
    }
    // Creo la select con l'ordinamento
    String campi[] = UtilityTags.stringToArray(campiOrdiEKey, ';');
    orders = new JdbcExpressionSort[campi.length];
    for (int i = 0; i < campi.length; i++) {
      boolean sort = true;
      String campo = campi[i];
      if (campo.charAt(0) == '!') {
        sort = false;
        campo = campo.substring(1);
      }
      // Se non è ascendente inverto l'ordinamento
      orders[i] = new JdbcExpressionSort(new JdbcExpression(new JdbcColumn(
          null, campo)), sort);
    }

    // Se si ha la paginazione eseguo l'alter Session
    // {MF060207} Eseguo l'alter session solo se si tratta di un database oracle
    if (it.eldasoft.utils.sql.comp.SqlManager.DATABASE_ORACLE.equalsIgnoreCase(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE))) {
//      if (this.numRowForPage > 0) {
        try {
          String nlsSort = StringUtils.stripToNull(ConfigManager.getValore("it.eldasoft.dbms.ora.nls.sort"));
          if (nlsSort != null) {
            sqlManager.execute("alter session set nls_sort = " + nlsSort);
          }
          String nlsComp = StringUtils.stripToNull(ConfigManager.getValore("it.eldasoft.dbms.ora.nls.comp"));
          if (nlsComp != null) {
            sqlManager.execute("alter session set nls_comp = " + nlsComp);
          }
        } catch (SQLException e) {
          ResourceBundle resBundleGenerale = ResourceBundle.getBundle(CostantiGenerali.RESOURCE_BUNDLE_APPL_GENERALE);
          logger.error(
              resBundleGenerale.getString("errors.database.dataAccessException"),
              e);
        }
//      }
    }

  }
  /**
   * Costruttore utilizzato quando si genera la lista a partire da un set di
   * dati a lista inseriti nel request
   *
   * @param elencoCampiSort
   * @param pagesize
   * @param valori
   */
  public PaginatoreListaImpl(String elencoCampiSort, int pagesize, List valori) {
    this.numRowForPage = pagesize;
    this.valori = valori;
    this.lastSort = elencoCampiSort;
    rowCount = valori.size();
    // Se il numero di righe per pagina è attivo allora calcola anche il
    // numero di pagine
    if (pagesize > 0) {
      // Calcolo anche il numero delle pagine
      pageCount = (int) (rowCount / (long) pagesize);
      // Se avanzano un po di righe allora il numero di pagina deve essere
      // incrementato di 1
      if ((rowCount % pagesize) > 0) pageCount++;
    } else
      this.pageCount = 0;
  }

//  private void setValPrec(String valPrec) {
//    // Creo l'array con i parametri del sort
//    if (valPrec == null || valPrec.length() == 0)
//      valFirstRow = null;
//    else {
//      // Si hanno i valori della prima riga
//      String vals[] = UtilityTags.stringToArray(valPrec, ';');
//      valFirstRow = new JdbcParametro[vals.length];
//      for (int i = 0; i < valFirstRow.length; i++) {
//        valFirstRow[i] = new JdbcParametro(vals[i]);
//      }
//    }
//  }

  /**
   * Funzione che si sposta ad una determinata pagina
   *
   * @param numPg
   *        Numero di pagina dove spostarsi
   * @throws JspException
   */
  public void gotoPg(int numPg) throws JspException {
    if (this.numRowForPage > 0) {
      if (numPg < 0) numPg = 0;
      if (numPg >= this.pageCount) numPg = (this.pageCount - 1);
//      int pageFromTop = numPg;
//      int pageFromBottom = (this.pageCount - 1) - numPg;
//      int pageFromCurrent = this.curPage > numPg ? this.curPage - numPg : numPg
//          - this.curPage;
//      int numRighe;
//      // Verifico la strategia migliore per la selezione dei primi dati
//      if (pageFromTop <= pageFromBottom && pageFromTop <= pageFromCurrent) {
//        numRighe = pageFromTop * this.numRowForPage;
//        if (numRighe > 0)
//          this.valFirstRow = this.findValFirstRow(null, numRighe);
//        else
//          this.valFirstRow = null;
//      } else if (pageFromBottom <= pageFromCurrent) {
//        if (rowCount % this.numRowForPage != 0)
//          numRighe = (rowCount % this.numRowForPage)
//              + pageFromBottom
//              * this.numRowForPage
//              - 1;
//        else
//          numRighe = this.numRowForPage
//              + pageFromBottom
//              * this.numRowForPage
//              - 1;
//        // Calcolo dalla fine
//        this.valFirstRow = this.findValFirstRow(null, -numRighe);
//      } else {
//        // Ricalcolo il numero di pagine
//        numRighe = (numPg - this.curPage) * this.numRowForPage;
//        this.valFirstRow = this.findValFirstRow(this.valFirstRow, numRighe);
//      }
      this.curPage = numPg;

    }
  }

//  /**
//   * Funzione che ricerca i valori delle prima riga a partire dalla chiave
//   * individuata dal primo parametro (ed è dall'inizio se il primo parametro è
//   * null), ed estraendo un numero di record pari a numRighe, se positivo in
//   * avanti, se negativo indietro
//   *
//   * @param valoriCampiPrec
//   * @param numRighe
//   * @return
//   * @throws JspException
//   */
//  private JdbcParametro[] findValFirstRow(JdbcParametro[] valoriCampiPrec,
//      int numRighe) throws JspException {
//    if (numRighe == 0 && this.numRowForPage <= 0) return valoriCampiPrec;
//    JdbcSqlSelect select = new JdbcSqlSelect();
//    boolean asc = numRighe > 0;
//
//    if (asc)
//      numRighe++;
//    else
//      numRighe--;
//    // Scorro tutte le colonne per l'ordinamento
//    for (int i = 0; i < this.orders.length; i++) {
//      JdbcExpressionSort expSort = this.orders[i];
//      JdbcColumn col = expSort.getColumn();
//      // Aggiungo la colonna alla select
//      select.getSelect().append(new JdbcExpression(col));
//      select.getOrder().append(
//          new JdbcExpressionSort(new JdbcExpression(col), asc
//              ? expSort.isAsc()
//              : !expSort.isAsc()));
//    }
//    select.getFrom().append(this.from);
//    select.getWhere().append(this.where);
//    this.addWhereFromParam(select.getWhere(), valoriCampiPrec, asc);
//    if (numRighe < 0) numRighe = -numRighe;
//    try {
//      List righe = sqlManager.getListVector(select, numRighe);
//      Vector valori = (Vector) righe.get(righe.size() - 1);
//      JdbcParametro retParm[] = new JdbcParametro[valori.size()];
//      for (int i = 0; i < retParm.length; i++)
//        retParm[i] = (JdbcParametro) valori.get(i);
//      return retParm;
//    } catch (SQLException e) {
//      throw new JspException("Errore nella selezione dei primi valori", e);
//    }
//  }

//  /**
//   * Funzione che attacca la where per il confronto
//   *
//   * @param where
//   * @param campo
//   * @param confronto
//   * @param param
//   */
//  private void addConfrontoAWhere(JdbcWhere where, JdbcColumn campo,
//      String confronto, JdbcParametro param) {
//    String tipoDB = ConfigManager.getValore(CostantiGenerali.PROP_DATABASE);
//    // Oracle e Postgres considerano i null come maggiori a qualsiasi valore,
//    // mentre SQL Server come inferiori a qualsiasi valore.
//    // Per cui nel caso di > o >= in Oracle e Postgres, e nel caso di < o <= per
//    // SQL Server, va aggiunta "is null" come condizione per includere dati
//    // successivi
//    int tipoConf = UtilityTags.indexOf(confronto, new Object[] { "<", "<=",
//        ">", ">=" });
//    if (tipoConf >= 0) {
//      if (param != null && param.getValue() != null) {
//        if (((it.eldasoft.utils.sql.comp.SqlManager.DATABASE_ORACLE.equalsIgnoreCase(tipoDB) || it.eldasoft.utils.sql.comp.SqlManager.DATABASE_POSTGRES.equalsIgnoreCase(tipoDB) || it.eldasoft.utils.sql.comp.SqlManager.DATABASE_DB2.equalsIgnoreCase(tipoDB)) && tipoConf >= 2)
//            || ((it.eldasoft.utils.sql.comp.SqlManager.DATABASE_SQL_SERVER.equalsIgnoreCase(tipoDB)) && tipoConf < 2)) {
//          where.append(new JdbcExpressionWhere(true));
//          where.append(new JdbcExpression(campo));
//          where.append(new JdbcExpression("is null"));
//          where.append(new JdbcExpressionWhere(JdbcUtils.JDBC_PARTICELLA_OR));
//        }
//        where.append(new JdbcExpression(campo));
//        where.append(new JdbcExpression(confronto));
//        where.append(new JdbcExpression(param));
//        if ((!it.eldasoft.utils.sql.comp.SqlManager.DATABASE_SQL_SERVER.equalsIgnoreCase(tipoDB) && tipoConf >= 2)
//            || (it.eldasoft.utils.sql.comp.SqlManager.DATABASE_SQL_SERVER.equalsIgnoreCase(tipoDB) && tipoConf < 2))
//          where.append(new JdbcExpressionWhere(false));
//      } else {
//        where.append(new JdbcExpression(campo));
//        if (((it.eldasoft.utils.sql.comp.SqlManager.DATABASE_ORACLE.equalsIgnoreCase(tipoDB) || it.eldasoft.utils.sql.comp.SqlManager.DATABASE_POSTGRES.equalsIgnoreCase(tipoDB) || it.eldasoft.utils.sql.comp.SqlManager.DATABASE_DB2.equalsIgnoreCase(tipoDB)) && tipoConf >= 2)
//            || ((it.eldasoft.utils.sql.comp.SqlManager.DATABASE_SQL_SERVER.equalsIgnoreCase(tipoDB)) && tipoConf < 2))
//          where.append(new JdbcExpression("is null"));
//        else
//          where.append(new JdbcExpression("is not null"));
//      }
//    } else {
//      where.append(new JdbcExpression(campo));
//      if (param != null && param.getValue() != null) {
//        where.append(new JdbcExpression(confronto));
//        where.append(new JdbcExpression(param));
//      } else {
//        where.append(new JdbcExpression("is null"));
//      }
//    }
//  }

//  private void addWhereFromParam(JdbcWhere whereToAdd,
//      JdbcParametro valoriCampiPrec[], boolean asc) {
//    JdbcWhere whereSort = new JdbcWhere();
//    // Se devo aggiungo anche la where su tutti i campi valori precedenti
//    if (valoriCampiPrec != null) {
//      String add = null;
//      String confronto = null;
//      whereSort.append(new JdbcExpressionWhere(true));
//
//      boolean inseritaPrimaCondizione = false;
//      for (int i = 0; i < this.orders.length; i++) {
//        // solo il primo campo potrebbe non essere della chiave, e quindi
//        // contenere valori null, per cui solo nel primo ciclo potrebbe venire
//        // saltato il codice sottostante
//        if (valoriCampiPrec[i] != null && valoriCampiPrec[i].getValue() != null) {
//          if (inseritaPrimaCondizione)
//            whereSort.append(new JdbcExpressionWhere(
//                JdbcUtils.JDBC_PARTICELLA_OR));
//
//          whereSort.append(new JdbcExpressionWhere(true));
//          for (int k = 0; k <= i; k++) {
//            if (k > 0)
//              whereSort.append(new JdbcExpressionWhere(
//                  JdbcUtils.JDBC_PARTICELLA_AND));
//
//            JdbcExpressionSort expSort = this.orders[k];
//            JdbcColumn col = expSort.getColumn();
//            if (k == i) {
//              if (k == this.orders.length - 1)
//                add = "=";
//              else
//                add = "";
//
//              confronto = (((asc && expSort.isAsc()) || (!asc && !expSort.isAsc()))
//                  ? ">"
//                  : "<")
//                  + add;
//            } else
//              confronto = "=";
//
//            addConfrontoAWhere(whereSort, col, confronto, valoriCampiPrec[k]);
//          }
//          whereSort.append(new JdbcExpressionWhere(false));
//          inseritaPrimaCondizione = true;
//
//        } else {
//          // nel caso di prima colonna uguale a null, verifico se si genera una
//          // condizione di filtro "is not null", e solo in tal caso la includo
//          // in modo da poter recuperare anche valori non nulli che vengono
//          // prima o dopo del record attuale di partenza a seconda della
//          // tipologia di ordinamento e di DBMS
//          JdbcWhere whereSortOrdinamentoSuNullable = new JdbcWhere();
//          JdbcExpressionSort expSort = this.orders[i];
//          JdbcColumn col = expSort.getColumn();
//          confronto = (((asc && expSort.isAsc()) || (!asc && !expSort.isAsc()))
//              ? ">"
//              : "<");
//          addConfrontoAWhere(whereSortOrdinamentoSuNullable, col, confronto,
//              valoriCampiPrec[i]);
//          if (whereSortOrdinamentoSuNullable.getSezioni().size() == 2
//              && "is not null".equals(whereSortOrdinamentoSuNullable.get(1).getFormula())) {
//            if (inseritaPrimaCondizione)
//              whereSort.append(new JdbcExpressionWhere(
//                  JdbcUtils.JDBC_PARTICELLA_OR));
//            whereSort.append(new JdbcExpressionWhere(true));
//            whereSort.append(new JdbcExpression(col));
//            whereSort.append(new JdbcExpression("is not null"));
//            whereSort.append(new JdbcExpressionWhere(false));
//            inseritaPrimaCondizione = true;
//          }
//
//        }
//      }
//      whereSort.append(new JdbcExpressionWhere(false));
//      whereToAdd.append(new JdbcExpressionWhere(JdbcUtils.JDBC_PARTICELLA_AND));
//      whereToAdd.append(whereSort);
//    }
//  }

  /**
   * Funzione che modifica la select in modo da visualizzare dal punto d'arrivo
   * in poi
   *
   * @param select
   *        Select in cui eseguire la modifica
   */
  public void modificaSelect(JdbcSqlSelect select) {
    // Aggiungo l'ordinamento e la where di selezione
    select.getOrder().setSort(this.orders);
    //this.addWhereFromParam(select.getWhere(), this.valFirstRow, true);
  }

  /**
   * @return Returns the rowCount.
   */
  public long getRowCount() {
    return rowCount;
  }

  /**
   * @return Returns the curPage.
   */
  public int getCurPage() {
    return curPage;
  }

  /**
   * @param curPage
   *        The curPage to set.
   */
  public void setCurPage(int curPage) {
    this.curPage = curPage;
  }

  /**
   * @return Returns the pageCount.
   */
  public int getPageCount() {
    return pageCount;
  }

  /**
   * Funzione che trasforma il paginatore nell'HTML da inserire prima della
   * lista
   *
   * @param navigazioneAbilitata
   *        Flag che dice che è abilitata la navigazione
   *
   * @return
   */
  public String getHtmlPaginatore(boolean navigazioneAbilitata) {
    StringBuffer buf = new StringBuffer("");

    if (this.rowCount > 0) {
      // si determina il singolare o il plurale dell'elemento
      String item = UtilityTags.getResource(
          CostantiGenerali.RESOURCE_BUNDLE_DISPLAYTAG,
          PROPERTY_STRING_PAGING_ITEM_NAME, null, true);
      String items = UtilityTags.getResource(
          CostantiGenerali.RESOURCE_BUNDLE_DISPLAYTAG,
          PROPERTY_STRING_PAGING_ITEMS_NAME, null, true);

      // caso con un solo elemento estratto (ovviamente manca la paginazione)
      if (this.rowCount == 1) {
        buf.append(UtilityTags.getResource(
            CostantiGenerali.RESOURCE_BUNDLE_DISPLAYTAG,
            PROPERTY_STRING_PAGING_FOUND_ONEITEM, new String[] { item }, true));
        buf.append(UtilityTags.getResource(
            CostantiGenerali.RESOURCE_BUNDLE_DISPLAYTAG,
            PROPERTY_STRING_PAGING_BANNER_ONEPAGE, null, true));
      } else {
        if (this.pageCount == 1) {
          // caso con una sola pagina estratta
          buf.append(UtilityTags.getResource(
              CostantiGenerali.RESOURCE_BUNDLE_DISPLAYTAG,
              PROPERTY_STRING_PAGING_FOUND_ALLITEMS, new String[] {
                  String.valueOf(this.rowCount), items, items }, true));
          buf.append(UtilityTags.getResource(
              CostantiGenerali.RESOURCE_BUNDLE_DISPLAYTAG,
              PROPERTY_STRING_PAGING_BANNER_ONEPAGE, null, true));
        } else {
          // caso con più pagine estratte

          // determino la porzione di record visualizzati
          int primoRecord = this.curPage * this.numRowForPage + 1;
          int ultimoRecord = (this.curPage + 1) * this.numRowForPage;
          if (ultimoRecord > this.rowCount) ultimoRecord = this.rowCount;
          buf.append(UtilityTags.getResource(
              CostantiGenerali.RESOURCE_BUNDLE_DISPLAYTAG,
              PROPERTY_STRING_PAGING_FOUND_SOMEITEMS, new String[] {
                  String.valueOf(this.rowCount), items,
                  String.valueOf(primoRecord), String.valueOf(ultimoRecord) },
              true));

          // imposto il banner di paginazione

          // per prima cosa si calcola il set di pagine da visualizzare
          int startPage = this.curPage - NUMERO_PAGINE_LINK / 2;
          if (startPage < 0) startPage = 0;
          int endPage = startPage + NUMERO_PAGINE_LINK;
          if (endPage > pageCount) endPage = pageCount;
          startPage = endPage - NUMERO_PAGINE_LINK;
          if (startPage < 0) startPage = 0;
          // si generano i link alle pagine interessante nella visualizzazione
          StringBuffer bufferPaginazione = new StringBuffer("");
          for (int i = startPage; i < endPage; i++) {
            if (i > startPage)
              bufferPaginazione.append(UtilityTags.getResource(
                  CostantiGenerali.RESOURCE_BUNDLE_DISPLAYTAG,
                  PROPERTY_STRING_PAGING_PAGE_SPARATOR, null, true));
            if (i != this.curPage)
              this.addLinkToPage(bufferPaginazione, i, navigazioneAbilitata);
            else
              bufferPaginazione.append("<strong>").append(this.curPage + 1).append(
                  "</strong>");
          }

          // si calcolano le pagine
          int primaPagina = 0;
          int paginaPrecedente = this.curPage - 1;
          int paginaSuccessiva = this.curPage + 1;
          int ultimaPagina = this.pageCount - 1;

          String[] parametriBannerPaginazione = new String[] {
              bufferPaginazione.toString(),
              "javascript:listaVaiAPagina(" + primaPagina + ");",
              "javascript:listaVaiAPagina(" + paginaPrecedente + ");",
              "javascript:listaVaiAPagina(" + paginaSuccessiva + ");",
              "javascript:listaVaiAPagina(" + ultimaPagina + ");" };
          String chiaveBannerPaginazione = null;
          if (navigazioneAbilitata) {
            if (this.curPage == 0) {
              // si tratta della visualizzazione della prima pagina
              chiaveBannerPaginazione = PROPERTY_STRING_PAGING_BANNER_FIRST;
            } else if (this.curPage == this.pageCount - 1) {
              // si tratta della visualizzazione dell'ultima pagina
              chiaveBannerPaginazione = PROPERTY_STRING_PAGING_BANNER_LAST;
            } else {
              // è una pagina intermedia
              chiaveBannerPaginazione = PROPERTY_STRING_PAGING_BANNER_FULL;
            }
          } else {
            chiaveBannerPaginazione = PROPERTY_STRING_PAGING_BANNER_DISABLED;
          }
          buf.append(UtilityTags.getResource(
              CostantiGenerali.RESOURCE_BUNDLE_DISPLAYTAG,
              chiaveBannerPaginazione, parametriBannerPaginazione, true));
        }
      }
    }
    return buf.toString();
  }

  private void addLinkToPage(StringBuffer buffer, int pagina,
      boolean navigazioneAbilitata) {
    if (navigazioneAbilitata)
      buffer.append(UtilityTags.getResource(
          CostantiGenerali.RESOURCE_BUNDLE_DISPLAYTAG,
          PROPERTY_STRING_PAGING_PAGE_LINK, new String[] {
              String.valueOf(pagina + 1),
              "javascript:listaVaiAPagina(" + pagina + ");" }, true));
    else
      buffer.append(String.valueOf(pagina + 1));
  }

  /**
   * Funzione che attacca gli hidden per la gestione della paginazione
   *
   * @return String Stringa da appendere
   */
  public String getDefaultHidden(PageContext pageContext) {
    StringBuffer buf = new StringBuffer("");
    buf.append(UtilityTags.getHtmlHideInput(HIDDEN_CURRENT_PG,
        String.valueOf(this.curPage)));
    buf.append(UtilityTags.getHtmlHideInput(HIDDEN_GOTO_PG, ""));
    // {MF 20.11.2006} Aggiunta del campo con l'ordinamento
    buf.append(UtilityTags.getHtmlHideInput(HIDDEN_SORT,
        UtilityTags.getParametro(pageContext, PaginatoreListaImpl.HIDDEN_SORT)));
    String valori = "";
    if (this.valFirstRow != null && this.valFirstRow.length > 0)
      for (int i = 0; i < this.valFirstRow.length; i++) {
        if (i > 0) valori += ';';
        valori += this.valFirstRow[i].toString(true);
      }
    buf.append(UtilityTags.getHtmlHideInput(HIDDEN_LAST_VALUE, valori));
    buf.append(UtilityTags.getHtmlHideInput(HIDDEN_LAST_SORT, this.lastSort));
    // Aggiungo alche il campo con il numero di righe
    buf.append(UtilityTags.getHtmlHideInput(
        FormTrovaTag.CAMPO_RISULTATI_PER_PAGINA,
        String.valueOf(this.numRowForPage)));
    return buf.toString();
  }

  /**
   * Funzione che gestisce il cambio di pagina
   *
   * @param select
   *        Select da eventualmente modificare
   * @param context
   * @throws JspException
   */
  public void gestisciCambioPg(JdbcSqlSelect select, PageContext context)
      throws JspException {
    if (this.numRowForPage <= 0) {
      // Se non è diviso in pagine setto solo l'ordinamento
      select.getOrder().setSort(this.orders);
      this.pageCount = 1;
      return;
    }
    String tmp;
    // Estraggo la pagina precedente e la successiva
    tmp = UtilityTags.getParametro(context, HIDDEN_CURRENT_PG);
    int lastPg = tmp == null ? 0 : new Integer(tmp).intValue();
    this.curPage = lastPg;
    tmp = UtilityTags.getParametro(context, HIDDEN_GOTO_PG);
    int newPg = tmp == null ? 0 : new Integer(tmp).intValue();
    tmp = UtilityTags.getParametro(context, HIDDEN_LAST_SORT);
    // Se è stato modificato l'ordinamento ricalcolo tutto
    if (tmp == null || !tmp.equals(this.lastSort)) {
      //valFirstRow = null;
      this.curPage = 0;
    } else {
      //this.setValPrec(UtilityTags.getParametro(context, HIDDEN_LAST_VALUE));
      this.gotoPg(newPg);
    }
    modificaSelect(select);
  }

  public List getDati(PageContext context) throws JspException {
    // Come prima cosa imposto l'ordinamento dei dati (solo se è settato un
    // ordinamento o sono settati i campi chiave)
    if (this.lastSort != null && this.lastSort.length() > 0)
      Collections.sort(this.valori, new ComparatorRows(this.lastSort));
    if (this.numRowForPage <= 0
        || this.lastSort == null
        || this.lastSort.length() == 0) {
      return this.valori;
    }
    // Come prima cosa riordino i dati
    Vector ret = new Vector();
    String tmp;
    // Estraggo la pagina precedente e la successiva
    tmp = UtilityTags.getParametro(context, HIDDEN_GOTO_PG);
    if (tmp == null || tmp.length() == 0)
      tmp = UtilityTags.getParametro(context, HIDDEN_CURRENT_PG);
    int newPg = tmp == null ? 0 : new Integer(tmp).intValue();
    this.curPage = newPg;
    // Eseguo l'ordinamento sui dati
    int lastRow = newPg * this.numRowForPage + this.numRowForPage;
    if (lastRow > this.valori.size()) lastRow = this.valori.size();
    for (int i = newPg * this.numRowForPage; i < lastRow; i++) {
      ret.add(this.valori.get(i));
    }

    return ret;

  }

  /**
   * Estrae i campi di ordinamento da applicare, che possono essere campi della
   * lista
   *
   * @param context
   *        page context
   * @param tag
   *        tag della lista
   * @return campi di ordinamento da applicare, null
   */
  public static CampoListaTagImpl[] getCampiSort(PageContext context, FormListaTag tag) throws JspException {
    CampoListaTagImpl[] campi = null;
    Vector elencoCampi = new Vector();
    CampoListaTagImpl campoAppoggio = null;

    // Verifico se è stato impostato un nuovo ordinamento nella pagina (in tal
    // caso, l'ordinamento avviene mediante selezione di una singola colonna,
    // quindi si ha un singolo campo di ordinamento)
    String tmp = UtilityTags.getParametro(context,
        PaginatoreListaImpl.HIDDEN_SORT);
    if (tmp != null) {
      campi = new CampoListaTagImpl[1];
      if (tmp.charAt(0) == '!') {
        campi[0] = tag.getCella(new Integer(tmp.substring(1)).intValue());
        campi[0].setSort(-1);
      } else {
        campi[0] = tag.getCella(new Integer(tmp).intValue());
        campi[0].setSort(1);
      }
    }

    // Se non è stata settata nella richiesta ricevuta, allora utilizzo l'ultimo
    // criterio di ordinamento utilizzato (questo avviene ad esempio quando si
    // vuole visualizzare una pagina diversa della lista dei risultati, sempre
    // sull'ordinamento impostato)
    if (campi == null) {
      tmp = UtilityTags.getParametro(context,
          PaginatoreListaImpl.HIDDEN_LAST_SORT);
      if (tmp != null) {
        String[] elencoCampiPrecOrdinamento = UtilityStringhe.deserializza(tmp,
            ';');
        String nomeCampo = null;
        for (int i = 0; i < elencoCampiPrecOrdinamento.length; i++) {
          // si estrae il nome puro del campo di ordinamento
          if (elencoCampiPrecOrdinamento[i].charAt(0) == '!') {
            nomeCampo = elencoCampiPrecOrdinamento[i].substring(1);
          } else {
            nomeCampo = elencoCampiPrecOrdinamento[i];
          }
          campoAppoggio = null;
          // si estrae la cella della lista associata a tale campo
          for (int cnt = 0; cnt < tag.getCells().size(); cnt++) {
            if (nomeCampo.equals(tag.getCella(cnt).getNomeFisico())) {
              campoAppoggio = tag.getCella(cnt);
              if (elencoCampiPrecOrdinamento[i].charAt(0) == '!')
                campoAppoggio.setSort(-1);
              else
                campoAppoggio.setSort(1);
              break;
            }
          }
          // si controlla la positività della ricerca effettuata
          if (campoAppoggio == null)
            throw new JspException("La colonna "
                + nomeCampo
                + " non è un campo della lista");
          // se si arriva qui, allora il campo è stato reperito nella lista e lo
          // si aggiunge al contenitore
          elencoCampi.add(campoAppoggio);
        }
        campi = (CampoListaTagImpl[]) elencoCampi.toArray(new CampoListaTagImpl[0]);
      }
    }

    // Se si arriva qui senza aver ottenuto un campo, allora si utilizza
    // l'impostazione di default indicata nel tag form:lista
    if (campi == null && !"0".equals(tag.getSortColumn())) {
      campi = tag.getCampiSortColumn();
    }

    return campi;
  }

  /**
   * Riordina i campi della chiave in modo da fornire un ordinamento prima per i
   * campi in input e poi per i rimanenti campi della chiave.
   *
   * @param campiSort
   *        campi sui quale effettuare l'ordinamento
   * @param campiKey
   *        campi chiave
   * @return stringa con i campi da utilizzare per l'ordinamento, con prima i
   *         campi in input e poi i campi chiave diversi da tali campi se
   *         appartengono alla chiave
   */
  public static String getCampiSortConChiave(CampoListaTagImpl[] campiSort,
      String campiKey) {
    if (campiSort != null) {

      StringBuffer risultato = new StringBuffer("");
//      HashSet setCampiOrdinamento = new HashSet();

      // prima si inseriscono nel risultato tutte le occorrenze dei campi su cui
      // effettuare l'ordinamento
      for (int contCampi = 0; contCampi < campiSort.length; contCampi++) {
        if (campiSort[contCampi] != null) {
          if (risultato.length() > 0) risultato.append(";");
          if (campiSort[contCampi].getSort() < 0) risultato.append("!");
          risultato.append(campiSort[contCampi].getNomeFisico());
//          setCampiOrdinamento.add(campiSort[contCampi].getNomeFisico());
        }
      }

//      // poi si inseriscono tutti i campi chiave a patto che non siano stati
//      // inseriti nel ciclo precedente
//      String[] campiChiave = UtilityTags.stringToArray(campiKey, ';');
//      for (int contCampiChiave = 0; contCampiChiave < campiChiave.length; contCampiChiave++) {
//        if (!setCampiOrdinamento.contains(campiChiave[contCampiChiave].startsWith("!")
//            ? campiChiave[contCampiChiave].substring(1)
//            : campiChiave[contCampiChiave])) {
//          if (risultato.length() > 0) risultato.append(";");
//          risultato.append(campiChiave[contCampiChiave]);
//        }
//      }

      campiKey = risultato.toString();
    }
    return campiKey;
  }

  /**
   * @return the numRowForPage
   */
  public int getNumRowForPage() {
    return numRowForPage;
  }

}
