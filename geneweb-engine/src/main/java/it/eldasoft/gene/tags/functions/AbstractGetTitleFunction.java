/*
 * Created on 06/mar/07
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.spring.UtilitySpring;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Funzione che ricava il titolo da mettere in una scheda
 * 
 * @author Marco.Franceschin
 * 
 */
public abstract class AbstractGetTitleFunction extends AbstractFunzioneTag {

  /**
   * Inizializzazione della funzione. Le tabelle definite così:<br> "<i>tabella</i>|<i>Msg
   * Inserimento</i>|<i>Msg Modifica/Visualizzazione</i> [|<i>Select
   * Inserimento</i>|<i>Select Modifica/Visualizzazione</i>]"<br>
   * <ul>
   * <li> <b>tabella</b> Tabella da considerate
   * <li> <b>Msg Inserimento</b> Messaggio d'inserimento in cui in {nn} vengono
   * sistituiti i valori
   * <li> <b>Msg Modifica/Visualizzazione</b> Messaggio di modifica o
   * visualizzazione in cui in {nn} viene fatto il replace dei valori
   * <li> <b>Select Inserimento</b> Select in inserimento
   * <li> <b>Select Modifica/Visualizzazione</b> Select in modifica o
   * visualizzazione<br>
   * </ul>
   * Es.<br>
   * <code>"CONCES|Inserimento Pratica|Pratica: {0} - {1}||select nprat, oggett from conces where nprat = #CONCES.NPRAT#"</code>
   */
  public abstract String[] initFunction();

  /**
   * Array con la definizione dei titoli:
   * <ul>
   * <li>0: Nome tabella</li>
   * <li>1: Inserimento|Modifica/visualizzazione</li>
   * <li>2: Select per l'estrazione dei dati in modifica/visualizzazione
   * replace con {0} {n} ....</li>
   * </ul>
   */
  private String tables[] = null;

  public AbstractGetTitleFunction() {
    super(2, new Class[] { PageContext.class, String.class });
    tables = this.initFunction();
  }

  private int indexOfTable(String valore) {
    valore = valore.toUpperCase() + "|";
    for (int i = 0; i < tables.length; i++) {
      if (tables[i].indexOf(valore) == 0) return i;
    }
    return -1;
  }

  /**
   * Restituisce il titolo di una pagina
   */
  public String function(PageContext pageContext, Object[] params)
      throws JspException {
    // ///////////////////////////////////////////////////////////////
    // Modifiche
    // ----------------
    // 20.09.2007: M.F. Aggiunta del richiamo delle funzioni di
    // personalizzazione del titolo. Aggiunta anche dell'estrazione della
    // descrizione della tabella se non settata la tabella
    // ///////////////////////////////////////////////////////////////

    String titolo = "";

    String modo = UtilityTags.getParametro(pageContext,
        UtilityTags.DEFAULT_HIDDEN_PARAMETRO_MODO);

    int idx = indexOfTable((String) params[1]);

    // inizializzazione titolo ed sql per l'estrazione
    String sql = "";
    if (idx >= 0) {
      String lsSplits[] = tables[idx].split("[|]");
      int liTitle = 2;
      int liSql = 4;
      if (UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modo)) {
        liTitle = 1;
        liSql = 3;
      }
      titolo = lsSplits.length > liTitle ? lsSplits[liTitle] : "";
      sql = lsSplits.length > liSql ? lsSplits[liSql] : "";
    }

    String key = UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA);
    // Se la chiave non è settata allora leggo la chiave del padre
    if (key == null) {
      key = UtilityTags.getParametro(pageContext, UtilityTags.DEFAULT_HIDDEN_KEY_TABELLA_PARENT);
    }

    // {MF200907} Aggiunta del richiamo delle funzioni astratte per la
    // personalizzazione dei titoli
    if (UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modo)) {
      String ret = this.getTitleInserimento(pageContext, (String) params[1]);
      if (ret != null) return ret;
    } else {
      String ret = this.getTitleModifica(pageContext, (String) params[1], key);
      if (ret != null) return ret;
    }

    // se esiste un titolo, allora si estraggono gli eventuali parametri
    if (titolo.length() > 0) {

      if (sql != null && sql.length() > 0) {
        SqlManager sqlManager = (SqlManager) UtilitySpring.getBean(
            "sqlManager", pageContext, SqlManager.class);
        Vector parametri = new Vector();
        sql = UtilityTags.replaceParametri(parametri, sql, key);
        try {
          Vector ret = sqlManager.getVector(sql,
              SqlManager.getObjectFromPram(parametri));
          if (ret != null)
            for (int i = 0; i < ret.size(); i++) {
              titolo = UtilityStringhe.replace(titolo, "{"
                  + String.valueOf(i)
                  + "}", ret.get(i).toString());
            }
        } catch (SQLException e) {
          throw new JspException(
              "Errore durante l'estrazione dei dati della query: " + sql, e);
        }
      } else {
        boolean replaceVar = titolo.indexOf("{") >= 0;
        if (!UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modo) || replaceVar) {
          // Se non è settato il l'sql Aggiungo solo i campi chiave
          Vector keys = UtilityTags.stringParamsToVector(key, null);
          for (int i = 0; i < keys.size(); i++) {
            if (replaceVar) {
              titolo = UtilityStringhe.replace(titolo, "{"
                  + String.valueOf(i)
                  + "}", keys.get(i).toString());
            } else {
              if (i > 0) titolo += " - ";
              titolo = keys.get(i).toString();
            }
          }
        }
      }
    } else {
      String descrTabella = null;
      // Aggiunta dell'estrazione della descrizione tabella dai metadati
      Tabella tab = DizionarioTabelle.getInstance().getDaNomeTabella(
          (String) params[1]);
      if (tab != null) {
        descrTabella = tab.getDescrizione();
      }
      if (UtilityTags.SCHEDA_MODO_INSERIMENTO.equals(modo)) {
        titolo += "Inserimento ";
        if (descrTabella != null) titolo += " in: " + descrTabella;
      } else if (descrTabella != null) {
        titolo += descrTabella + ": ";
      }
      if (key != null && key.length() > 0) {
        Vector keys = UtilityTags.stringParamsToVector(key, null);
        for (int i = 0; i < keys.size(); i++) {
          if (i > 0) titolo += " - ";
          titolo += keys.get(i).toString();
        }
      }
    }

    return titolo;
  }

  /**
   * Funzione che personalizza il titolo in modifica
   * 
   * @param pageContext
   *        Contesto delle pagina
   * @param table
   *        Tabella da cui estrarre il titolo
   * @param keys
   *        Campi chiave di tipo JdbcParametro divisi da ;
   * @return <b>null</b> e non personalizzato. Altrimenti il titolo da inserire
   */
  abstract protected String getTitleModifica(PageContext pageContext,
      String table, String keys);

  /**
   * Personalizzazione del titolo in inserimento
   * 
   * @param pageContext
   *        Contesto della pagina
   * @param table
   *        Tabella in considerazione
   * @return <b>null</b> se non personalizzato. Altrimenti il titolo da
   *         mettere.
   */
  abstract protected String getTitleInserimento(PageContext pageContext,
      String table);

}
