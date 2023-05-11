package it.eldasoft.gene.db.sql.stringsql;

import java.util.Vector;

public class SQLSelect {

  /*
   * Costanti
   */
  // Carattere di stringa
  public static final char CHAR_STR    = '\'';

  // Carattere delle parentesi
  public static final char CHAR_PARS[] = { '(', ')' };

  public static final int  SQL_SELECT  = 0;

  public static final int  SQL_FROM    = 1;

  public static final int  SQL_WHERE   = 2;

  public static final int  SQL_GROUP   = 3;

  public static final int  SQL_HAVING  = 4;

  public static final int  SQL_SORT    = 5;

  public static final int  SQL_UNION   = 6;

  private String           sql;

  /**
   * Costruttore di default
   */
  public SQLSelect() {
    sql = "";
  }

  /**
   * Costruttore con select
   *
   * @param asSql
   */
  public SQLSelect(String asSql) {
    sql = SQLSelect.convSQL(asSql);
  }

  /**
   * Funzione che ritrova una stringa che non sia contenuta all'interno di una
   * stringa nell'SQL o all'interno di parentesi
   *
   * @param asSql
   *        Sql dove eseguire la ricerca
   * @param asSub
   *        Sottostringa da ricercare
   * @return posizione di ritrovo; -1 se no trovato
   */
  private static int posNotInStr(String asSql, String asSub) {
    // ************************************************************
    // Storia Modifiche:
    // Utente Data Descrizione
    // M.F. 02/05/2006 Prima Versione
    // ************************************************************
    // Inizializzazioni
    int liPos = -1, liPosNo = -1, liTmp;
    String lsSql = asSql.toLowerCase();
    String lsSub = asSub.toLowerCase();

    do {
      liPos = lsSql.indexOf(lsSub, liPos + 1);
      if (liPos >= 0) {
        // Varifico che non sia stato trovato all'interno di una stringa
        // o di parentesi
        do {
          liTmp = lsSql.indexOf(CHAR_PARS[0], liPosNo + 1);
          liPosNo = lsSql.indexOf(CHAR_STR, liPosNo + 1);

          if (liPosNo < 0 || liTmp < liPosNo) liPosNo = liTmp;

          if (liPosNo >= 0) {
            if (liPosNo > liPos)
              return liPos;
            else {
              if (liPosNo == liTmp) {
                // Ritrovo la parentedi di fine
                int liPar = 1;
                liPosNo++;
                // Scorro tutti i caratteri sino a trovate la
                // fine
                for (; liPosNo < lsSql.length() && liPar > 0; liPosNo++) {
                  if (lsSql.charAt(liPosNo) == CHAR_PARS[0]) liPar++;
                  if (lsSql.charAt(liPosNo) == CHAR_PARS[1]) liPar--;
                }
                if (liPar > 0) return -1;
              } else
                liPosNo = lsSql.indexOf(CHAR_STR, liPosNo + 1);
              if (liPosNo < 0)
                return -1;
              else {
                if (liPosNo <= liPos) continue;
                liPos = liPosNo;
              }
            }
          } else
            return liPos;
        } while (liPosNo > 0);
      }
    } while (liPos >= 0);
    return -1;
  }

  /**
   * Funzione che estrae l'inizio di un tipo di sezione
   *
   * @param aiTipo
   *        Tipo di sezione
   * @return inizio della sezione
   */
  private static String getIniSez(int aiTipo) {
    switch (aiTipo) {
    case SQLSelect.SQL_SELECT:
      return " select ";
    case SQLSelect.SQL_FROM:
      return " from ";

    case SQLSelect.SQL_WHERE:
      return " where ";
    case SQLSelect.SQL_GROUP:
      return " group by ";

    case SQLSelect.SQL_SORT:
      return " order by ";

    case SQLSelect.SQL_HAVING:
      return " having ";

    case SQLSelect.SQL_UNION:
      return " union ";

    }
    return "";
  }

  /**
   * Funzione che estrae la posizione e la lunghezza di una sezione
   *
   * @param asSql
   *        Sql da cui estrarre la posizione
   * @param aiSez
   *        Tipo di sezione da estrarre
   * @return Posizione d'inizio della sezione; -1 se non trovata
   */
  private static int posSql(String asSql, int aiSez) {
    String lsSez = getIniSez(aiSez);
    if (!lsSez.equals("")) {
      return posNotInStr(asSql, lsSez);
    }
    return -1;
  }

  /**
   * Funzione che converte gli enter e i tab nell'SQL
   *
   * @param asSql
   * @return
   */
  private static String convSQL(String asSql) {
    // ************************************************************
    // Storia Modifiche:
    // Data Utente Descrizione
    // 13/11/2006 M.F. Eliminazione di tutti gli spazi non utilizzati
    // ************************************************************
    StringBuffer lBuf = new StringBuffer();
    char lcLast = ' ';
    boolean lbInString = false;
    String lsNome="abcdefghijklmnopqrstuvwxjzABCDEFGHIJKLMNOPQRSTUVWXYZ_.0123456789";
    String lsPart="><=!'";

    lBuf.append(' ');
    // Corro tutti i caratteri
    for (int i = 0; i < asSql.length(); i++) {
      char lcToAdd = asSql.charAt(i);
      if (!lbInString)
        switch (lcToAdd) {
        case '\n':
        case '\t':
        case '\r':
          lcToAdd = ' ';
          break;
        case CHAR_STR:
          lbInString = true;
        }
      else {
        lcLast = '\0';
        if (lcToAdd == CHAR_STR) {
          lbInString = false;
        }
      }
      if (lcToAdd != ' ' || lbInString) {
        if (!lbInString) {
          if (lcLast == ' ') {
            lBuf.append(' ');
          }else if((lsNome.indexOf(lcLast)>=0 && lsPart.indexOf(lcToAdd)>=0) ||
              (lsPart.indexOf(lcLast)>=0 && lsNome.indexOf(lcToAdd)>=0)){
            // Metto uno spazio se cambia il tipo
            lBuf.append(' ');
          }
        }
        lBuf.append(lcToAdd);
      }

      lcLast = lcToAdd;

    }
    return lBuf.toString();
  }

  /**
   * Funzione che estrae la posizione di una sezione
   *
   * @param aiTipo
   *        Tipo di sezione da estrarre
   * @param aiLen
   *        Lunghezza della sezione
   * @return -1 Se non trovata altrimenti ilò carattere d'inizio compreso
   *         dell'indentificatore SQL della sezione
   */
  private SQLSezion posSez(int aiTipo) {
    String lsSql = this.getSql();
    int liPos, liLen = -1;

    // Elimino le eventuali union
    liPos = SQLSelect.posSql(lsSql, SQLSelect.SQL_UNION);
    if (liPos >= 0) {
      lsSql = lsSql.substring(0, liPos);
    }
    liPos = SQLSelect.posSql(lsSql, aiTipo);
    if (liPos >= 0) {

      lsSql = lsSql.substring(liPos + 1);
      for (int liLoop = aiTipo + 1; liLen < 0 && liLoop < SQLSelect.SQL_UNION; liLoop++) {
        liLen = SQLSelect.posSql(lsSql, liLoop);
      }
      if (liLen < 0) liLen = lsSql.length();
      liLen++;
    }
    // Setto la dimensione
    return new SQLSezion(liPos, liLen);
  }

  /**
   * @return Returns the sql.
   */
  public String getSql() {
    return sql;
  }

  /**
   * @param sql
   *        The sql to set.
   */
  public void setSql(String sql) {
    this.sql = SQLSelect.convSQL(sql);
  }

  /**
   * Funzione che estrae una sezione
   *
   * @param aiTipo
   * @return
   */
  public String getSez(int aiTipo) {
    // ************************************************************
    // Storia Modifiche:
    // Utente Data Descrizione
    // M.F. 02/05/2006 Prima Versione
    // M.F. 03/05/2006 M.F. Elimino l'inizio della sezione
    // ************************************************************

    // Inizializzazioni
    SQLSezion lSez = this.posSez(aiTipo);
    if (lSez.getStart() >= 0) {
      // Elimino la sezione d'inizio
      return this.sql.substring(lSez.getStart(),
          lSez.getStart() + lSez.getLen()).substring(getIniSez(aiTipo).length());
    }
    return "";
  }

  /**
   * Funzione che ritrova la posizione per appendere
   *
   * @param aiTipo
   * @return
   */
  private SQLSezion posToAppend(int aiTipo) {
    for (int i = aiTipo; i >= 0; i--) {
      SQLSezion lSez = this.posSez(i);
      if (lSez.getStart() >= 0) {
        if (i == aiTipo)
          return lSez;
        else
          return new SQLSezion(lSez.getStart() + lSez.getLen(), 0);
      }
    }
    return new SQLSezion(-1, -1);
  }

  public int replaceSez(int aiTipo, String asReplaceStr) {
    SQLSezion lSez = this.posToAppend(aiTipo);
    if (lSez.getStart() >= 0) {
      StringBuffer lBuf = new StringBuffer();
      lBuf.append(this.sql.substring(0, lSez.getStart()));
      if (asReplaceStr.length() > 0) {
        lBuf.append(getIniSez(aiTipo));
        lBuf.append(asReplaceStr);
      } else
        switch (aiTipo) {
        case SQL_SELECT:
        case SQL_FROM:
          return -1;
        }

      lBuf.append(this.sql.substring(lSez.getStart() + lSez.getLen()));
      this.setSql(lBuf.toString());
      return 1;
    }
    return -1;
  }

  public int addToSez(int aiTipo, String asAdd) {
    SQLSezion lSez = this.posToAppend(aiTipo);
    if (lSez.getStart() >= 0) {
      StringBuffer lBuf = new StringBuffer();
      String lsIni, lsDiv = "";
      lsIni = getIniSez(aiTipo);
      switch (aiTipo) {
      case SQLSelect.SQL_SELECT:
      case SQLSelect.SQL_FROM:
      case SQLSelect.SQL_GROUP:
      case SQLSelect.SQL_SORT:
        lsDiv = ",";
        break;
      case SQLSelect.SQL_WHERE:
      case SQLSelect.SQL_HAVING:
        asAdd = asAdd.trim();
        // Controllo se all'inizio e settata la particella
        if (asAdd.substring(0, 4).toLowerCase().equals("and ")) {
          lsDiv = " and ";
          asAdd = asAdd.substring(4);
        } else if (asAdd.substring(0, 3).toLowerCase().equals("or ")) {

          lsDiv = " or ";
          asAdd = asAdd.substring(3);
        } else
          lsDiv = " and ";
        break;

      }
      lBuf.append(sql.substring(0, lSez.getStart() + lSez.getLen()));
      if (lSez.getLen() == 0) {
        lBuf.append(lsIni);
      } else
        lBuf.append(lsDiv);
      lBuf.append(asAdd);
      // Appendo il resto
      lBuf.append(sql.substring(lSez.getStart() + lSez.getLen()));
      this.setSql(lBuf.toString());
      return 1;
    }
    return -1;
  }

  /**
   * Do l'sql se trasformato in stringa
   */
  @Override
  public String toString() {

    return this.getSql();
  }

  /**
   * Funzione che divide una stringa per un determinato divisore. Ignora
   * l'interno di parentesi tonde e di stringhe con ' come determinatore
   *
   * @param stringa
   *        Stringa da dividere
   * @param divisore
   *        Stringa di divisione
   * @return Array con le varie sezioni divise
   */
  public static String[] dividiStringa(String stringa, String divisore) {
    Vector<String> vect = new Vector<String>();
    // 0 normale, 1 Stringa
    int stato = 0;
    int parentesi = 0;
    StringBuffer sez = new StringBuffer("");
    if(stringa==null || stringa.length()==0)
      return new String[]{};
    for (int i = 0; i < stringa.length(); i++) {
      char carattere = stringa.charAt(i);
      switch (carattere) {
      case '\'':
        if (stato == 0)
          stato = 1;
        else
          stato = 0;
        break;
      case '(':
        parentesi++;
        break;
      case ')':
        parentesi--;
        break;
      default:
        if (parentesi == 0 && stato == 0) {
          if (divisore.charAt(0) == carattere) {
            // Se è il divisore allora aggiungo all'array la parte
            if (stringa.substring(i).indexOf(divisore) == 0) {
              vect.add(sez.toString());
              i += divisore.length() - 1;

              sez = new StringBuffer("");
              continue;
            }
          }
        }
      }
      sez.append(carattere);
    }
    if (sez.toString().length() > 0) vect.add(sez.toString());
    String divs[] = new String[vect.size()];
    for (int i = 0; i < vect.size(); i++)
      divs[i] = vect.get(i);
    return divs;
  }
}
