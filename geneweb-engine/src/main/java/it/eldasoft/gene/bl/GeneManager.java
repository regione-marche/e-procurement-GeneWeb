/*
 * Created on Mar 1, 2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.bl;

import it.eldasoft.gene.bl.genric.GestoreVisibilitaDati;
import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.db.sql.sqlparser.JdbcParametro;
import it.eldasoft.gene.tags.decorators.campi.CampoDecorator;
import it.eldasoft.gene.tags.decorators.scheda.FormSchedaTag;
import it.eldasoft.gene.tags.decorators.scheda.IFormScheda;
import it.eldasoft.gene.tags.decorators.wizard.FormSchedaWizardTag;
import it.eldasoft.gene.tags.utils.UtilityTags;
import it.eldasoft.gene.web.struts.docass.CostantiDocumentiAssociati;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.metadata.cache.DizionarioCampi;
import it.eldasoft.utils.metadata.cache.DizionarioTabelle;
import it.eldasoft.utils.metadata.domain.Campo;
import it.eldasoft.utils.metadata.domain.LegameTabelle;
import it.eldasoft.utils.metadata.domain.Tabella;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.sql.comp.SqlComposer;
import it.eldasoft.utils.sql.comp.SqlComposerException;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.zip.CRC32;

import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Classe che gestisce alcune funzionalita di business di base generali
 *
 * @author cit_franceschin
 */
public class GeneManager {

  static Logger          logger = Logger.getLogger(GeneManager.class);
  /** Manager per le transazioni e selezioni su database */
  private SqlManager     sql;

  /** Elenco degli oggetti di proprietà */
  //private Vector         oggettiDiProprieta;

  private final GestoreProfili profili;

  /** Gestore della visibilita' dei dati nelle ricerche/modelli (profili per entita' standard, profili o dyncam per entità dinamiche) */
  private final GestoreVisibilitaDati gestoreVisibilitaDati;

  /**
   * Elenco delle impostazioni per le entità del generatore attributi collegate
   * a tabelle
   */
  private final HashMap<String, EntDyn>        entitaDynCollegate;
  /**
   * Elenco delle impostazioni per le entità dinamiche figlie 1:N collegate a
   * tabelle
   */
  private final HashMap<String, List<EntDyn>>  entitaDynFiglie1NCollegate;
  /** Elenco delle join con c0oggass per le varie tabelle */
  private final HashMap<String, EntOggass>     entitaOggassCollegate;
  /** Elenco delle join con NoteAvvisi per le varie tabelle */
  private final HashMap<String, EntNoteAvvisi> entitaNoteAvvisiCollegate;
  /** Elenco delle join con G_SCADENZ per le varie tabelle */
  private final HashMap<String, EntScadenziario>    entitaG_ScadenzCollegate;

  /**
   * Costruttore della classe
   */
  public GeneManager() {
    //this.oggettiDiProprieta = null;
    this.profili = new GestoreProfili(this);
    this.gestoreVisibilitaDati = new GestoreVisibilitaDati(this);
    this.entitaDynCollegate = new HashMap<String, EntDyn>();
    this.entitaDynFiglie1NCollegate = new HashMap<String, List<EntDyn>>();
    this.entitaOggassCollegate = new HashMap<String, EntOggass>();
    this.entitaNoteAvvisiCollegate = new HashMap<String, EntNoteAvvisi>();
    this.entitaG_ScadenzCollegate = new HashMap<String, EntScadenziario>();
  }

  private class EntOggass {

    private final String   entita;
    private final String   where;
    private String   join;
    private Object[] params;
    private final String[] campiKey;

    public EntOggass(String entita, String where, String join, Object[] params,
        String[] campiKey) {
      this.entita = entita;
      if (join != null && join.length() > 0)
        this.join = join;
      else
        this.join = null;
      if (params == null)
        this.params = new Object[] {};
      else
        this.params = params;
      this.where = where;
      this.campiKey = campiKey;
    }

    public String getEntita() {
      return this.entita;
    }

    public String getJoin() {
      return this.join;
    }

    public boolean isOk() {
      return this.join != null;
    }

    public Object[] getParams(Object[] postpend) {
      Vector<Object> ret = new Vector<Object>();
      for (int i = 0; i < this.params.length; i++)
        ret.add(this.params[i]);
      for (int i = 0; i < postpend.length; i++)
        ret.add(postpend[i]);

      return ret.toArray(new Object[0]);
    }

    public String getWhere() {
      return this.where;
    }

    /**
     * Funzione che ritrova il nome del campo di destinazione per il campo
     * sorgente
     *
     * @param campo
     * @return
     */
    public String getKey(String campo) {
      String result = null;
      if (campo != null) for (int i = 0; i < campiKey.length; i++) {
        if (campo.equalsIgnoreCase(campiKey[i])) {
          result = "C0AKEY" + (i + 1);
        }
      }
      return result;
    }
  }

  /**
   * Classe per la gestione delle entità dinamiche
   *
   * @author Marco.Franceschin
   *
   */
  private class EntDyn {

    private final String entita;
    private final String entitaP;
    private final Vector<String> key;
    private final Vector<String> keyP;

    public EntDyn(String ent, String entP) {
      this.key = new Vector<String>();
      this.keyP = new Vector<String>();
      this.entita = ent != null ? ent.toUpperCase() : null;
      this.entitaP = entP != null ? entP.toUpperCase() : null;
    }

    /**
     * @return the entita
     */
    public String getEntita() {
      return this.entita;
    }

    /**
     * @return the entitaP
     */
    public String getEntitaP() {
      return this.entitaP;
    }

    public void addKey(String col, String colP) {
      this.key.add(col);
      this.keyP.add(colP);
    }

    public boolean isOk() {
      return this.entita != null && this.key.size() > 0;
    }

    /**
     * @return the key
     */
    public String getKey(int index) {
      return this.entita + "." + this.key.get(index);
    }

    /**
     * @return the keyP
     */
    public String getKeyP(int index) {
      return this.entitaP + "." + this.keyP.get(index);
    }

    public int keyCount() {
      return this.key.size();
    }

    /**
     * Funzione che verifica se ha i campi chiave
     *
     * @param impl
     * @return
     */
    public boolean hasKey(DataColumnContainer impl) {
      boolean hasKey = true;
      if (this.keyCount() == 0 || !this.isOk()) {
        hasKey = false;
      } else {
        // Verifico se nella pagina esistono campi chiave dell'entità dinamica
        for (int i = 0; i < keyCount() && hasKey; i++) {
          if (!impl.isColumn(getKey(i))) hasKey = false;
        }
      }
      return hasKey;
    }

    public StringBuffer getJoin() {
      StringBuffer buf = new StringBuffer("");
      for (int i = 0; i < keyCount(); i++) {
        if (i > 0) buf.append(" and ");
        buf.append(getKey(i));
        buf.append(" = ");
        buf.append(getKeyP(i));
      }
      return buf;
    }

    public String getKey(String chiavePadre) {
      String result = null;
      if (chiavePadre != null) {
        for (int i = 0; i < keyP.size(); i++) {
          chiavePadre = chiavePadre.toUpperCase();
          if (chiavePadre.equals(keyP.get(i))) {
            result = (String) key.get(i);
          }
        }
      }
      return result;
    }
  }

  private class EntNoteAvvisi {

    private final String   entita;
    private final String   where;
    private String   join;
    private Object[] params;
    private final String[] campiKey;

    public EntNoteAvvisi(String entita, String where, String join, Object[] params,
        String[] campiKey) {
      this.entita = entita;
      if (join != null && join.length() > 0)
        this.join = join;
      else
        this.join = null;
      if (params == null)
        this.params = new Object[] {};
      else
        this.params = params;
      this.where = where;
      this.campiKey = campiKey;
    }

    public String getEntita() {
      return this.entita;
    }

    public String getJoin() {
      return this.join;
    }

    public boolean isOk() {
      return this.join != null;
    }

    public Object[] getParams(Object[] postpend) {
      Vector<Object> ret = new Vector<Object>();
      for (int i = 0; i < this.params.length; i++)
        ret.add(this.params[i]);
      for (int i = 0; i < postpend.length; i++)
        ret.add(postpend[i]);

      return ret.toArray(new Object[0]);
    }

    public String getWhere() {
      return this.where;
    }

    /**
     * Funzione che ritrova il nome del campo di destinazione per il campo
     * sorgente
     *
     * @param campo
     * @return
     */
    public String getKey(String campo) {
      String result = null;
      if (campo != null) for (int i = 0; i < campiKey.length; i++) {
        if (campo.equalsIgnoreCase(campiKey[i])) {
          result = "NOTEKEY" + (i + 1);
        }
      }
      return result;
    }
  }

  /**
   * @return Returns the sql.
   */
  public SqlManager getSql() {
    return this.sql;
  }

  /**
   * @param sql
   *        The sql to set.
   */
  public void setSql(SqlManager sql) {
    this.sql = sql;
  }

  /**
   * @return Ritorna gestoreVisibilitaDati.
   */
  public GestoreVisibilitaDati getGestoreVisibilitaDati() {
    return gestoreVisibilitaDati;
  }

  /**
   * Funzione che esegue il conto delle occorrenze
   *
   * @param sql
   *        Sql per il conteggio
   * @param param
   *        Parametri
   * @param gestEccezioni
   *        Flag per dire di dare l'ecezione
   * @return Valore di conto delle occorrenze
   * @throws GestoreException
   */
  public long getLongFromSelect(String sql, Object[] param,
      boolean gestEccezioni) throws GestoreException {
    try {
      Vector<?> ret = this.sql.getVector(sql, param);
      if (ret.size() > 0) {
        Long count = SqlManager.getValueFromVectorParam(ret, 0).longValue();
        if (count != null) {
          return count.longValue();
        }
      }
    } catch (Throwable e) {
      // Se non esiste lavsche non è un problema
      if (gestEccezioni) {
        throw new GestoreException(e.getMessage(), e.toString());
      }
    }
    return 0;
  }

  /**
   * Funzione che verifica l'esistenza o meno di una tabella sul database
   *
   * @param tabella
   *        Nome della tabella
   * @return
   */
  public boolean esisteTabella(String tabella) {
    return this.sql.isTable(tabella);
    /*boolean result = false;
    try {
      Vector vRect = this.sql.getVector("select 0 from " + tabella,
          new Object[] {});
   if (vRect != null && vRect.size() > 0) result = true;
    } catch (SQLException e) {
    }
    return result;*/
  }

  /**
   * Funzione che esegue l'eliminazione di un insieme di tabelle. La transazione
   * deve essere già aperta prima di chiamare la funzione
   *
   * @param tables
   *        Elenco delle tabelle
   * @param where
   *        Filtro sulle tabelle
   * @param param
   *        Parametri
   * @throws GestoreException
   */
  public void deleteTabelle(String[] tables, String where, Object[] param)
      throws GestoreException {
    // ///////////////////////////////////////////////////////////////
    // Modifiche
    // ----------------
    // 13.11.2007: M.F. Aggiunta dell'eliminazione delle tabelle collegate
    // X.....
    // ////////////////////////////////////////////////////////////// /
    for (int i = 0; i < tables.length; i++) {
      if (this.sql.isTable(tables[i])) {
        eliminaOccorrenzeOggettiAssociati(tables[i], where, param);
        eliminaOccorrenzeNoteAvvisi(tables[i], where, param);
        eliminaOccorrenzeScadenzario(tables[i], where, param);
        // Se esiste la tabella eseguo l'eliminazione
        eliminaOccorrenzeGeneratoreAttributi(tables[i], where, param);
        eliminaOccorrenzeEntitaFiglie1N(tables[i], where, param);
        try {
          this.sql.update("delete from " + tables[i] + " where " + where, param);
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore durante l'eliminazione dele righe delle tabella: "
                  + tables[i], "deleteTables", new Object[] { tables[i] }, e);
        }
      }
    }
  }

  /**
   * Funzione che esegue l'eliminazione di un insieme di tabelle. La transazione
   * deve essere già aperta prima di chiamare la funzione. In questo caso vengono
   * eliminate le sole occorrenze delle tabelle senza la cancellazione delle
   * occorrenze in note e avvisi, tra i documenti associati e l'estensione del
   * generatore attributi
   *
   * @param tables
   *        Elenco delle tabelle
   * @param where
   *        Filtro sulle tabelle
   * @param param
   *        Parametri
   * @throws GestoreException
   */
  public void deleteTabelleLight(String[] tables, String where, Object[] param)
      throws GestoreException {
    for (int i = 0; i < tables.length; i++) {
      if (this.sql.isTable(tables[i])) {
        try {
          this.sql.update("delete from " + tables[i] + " where " + where, param);
        } catch (SQLException e) {
          throw new GestoreException(
              "Errore durante l'eliminazione dele righe delle tabella: "
                  + tables[i], "deleteTables", new Object[] { tables[i] }, e);
        }
      }
    }
  }

  /**
   * Funzione che esegue l'eliminazione delle occorrenze dei documenti associati
   * collegate a una tabella
   *
   * @param entita
   *        Entita in cui sono collegate le occorrenze del generatore attributi
   * @param where
   *        Filtro sulla tabella
   * @param param
   *        Paramentri del filtro
   * @throws GestoreException
   */
  public void eliminaOccorrenzeOggettiAssociati(String entita, String where,
      Object[] param) throws GestoreException {
    EntOggass ent = getEntOggass(entita);

    if (ent.isOk()) {
      // Cancellazione W_DOCDIG collegata a C0OGGASS
      StringBuffer bufW = new StringBuffer("");
      bufW.append("delete from w_docdig where digent = 'C0OGGASS' and digkey1 in ");
      bufW.append("(select ");
      bufW.append(sql.getDBFunction("inttostr", new String[] {"c0acod"}));
      bufW.append(" from c0oggass where ");
      bufW.append(ent.getWhere());
      bufW.append(" and exists(select 1 from ");
      bufW.append(entita);
      bufW.append(" where ");
      bufW.append(ent.getJoin());
      bufW.append(" and ");
      bufW.append(where);
      bufW.append(" ))");
            
      // Se s'è l'entita dinamica collegata allora la elimino
      StringBuffer buf = new StringBuffer("");
      buf.append("delete from C0OGGASS where ");
      buf.append(ent.getWhere());
      buf.append(" and exists(select 1 from ");
      buf.append(entita);
      buf.append(" where ");

      buf.append(ent.getJoin());
      buf.append(" and ");
      buf.append(where);

      buf.append(" ) ");
      // Eseguo la vera e proprie eliminazione dell'entità collegata
      try {
        this.sql.update(bufW.toString(), ent.getParams(param));
        this.sql.update(buf.toString(), ent.getParams(param));
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore in eliminazione dei documenti associati",
            "deleteOggettiAssociati", new String[] { entita }, e);
      }
    }

  }

  public void eliminaOccorrenzeNoteAvvisi(String entita, String where,
      Object[] param) throws GestoreException {
    EntNoteAvvisi ent = getEntNoteAvvisi(entita);

    if (ent.isOk()) {
      // Se s'è l'entita dinamica collegata allora la elimino
      StringBuffer buf = new StringBuffer("");
      buf.append("delete from G_NOTEAVVISI where ");
      buf.append(ent.getWhere());
      buf.append(" and exists(select 1 from ");
      buf.append(entita);
      buf.append(" where ");

      buf.append(ent.getJoin());
      buf.append(" and ");
      buf.append(where);

      buf.append(" ) ");
      // Eseguo la vera e proprie eliminazione dell'entità collegata
      try {
        this.sql.update(buf.toString(), ent.getParams(param));
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore in eliminazione delle note e avvisi",
            "deleteNoteAvvisi", new String[] { entita }, e);
      }
    }
  }

  /**
   * Elimina l'estensione del generatore attributi
   *
   * @param tabella
   *        Tabella in sui sono agganciate delle estensioni
   * @param where
   *        Where per il filtro delle righe sulla tabella
   * @param param
   *        Parametri per il filtro
   * @throws GestoreException
   */
  public void eliminaOccorrenzeGeneratoreAttributi(String tabella,
      String where, Object[] param) throws GestoreException {
    // Eliminazione delle entita del generatore attributi

    EntDyn ent = this.getEntDynGenAttributi(tabella);
    if (ent.isOk()) {
      // Se s'è l'entita dinamica collegata allora la elimino
      StringBuffer buf = new StringBuffer("");
      buf.append("delete from ");
      buf.append(ent.getEntita());
      buf.append(" where exists(select 1 from ");
      buf.append(ent.getEntitaP());
      buf.append(" where ");
      buf.append(where);
      if (where != null && where.length() > 0) buf.append(" and ");
      buf.append(ent.getJoin());
      buf.append(" ) ");
      // Eseguo la vera e proprie eliminazione dell'entità collegata
      try {
        this.sql.update(buf.toString(), param);

      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nell'eliminazione delle occorrenze del generatore attributi per: "
                + tabella
                + " !", "deleteEstensioneGeneratore",
            new String[] { ent.getEntita() }, e);
      }
    }
  }

  /**
   * Elimina le occorrenze nelle tabelle estensione 1:N di un'entità
   *
   * @param tabella
   *        Tabella in cui sono agganciate delle estensioni
   * @param where
   *        Where per il filtro delle righe sulla tabella
   * @param param
   *        Parametri per il filtro
   * @throws GestoreException
   */
  public void eliminaOccorrenzeEntitaFiglie1N(String tabella, String where,
      Object[] param) throws GestoreException {
    // Eliminazione delle entita del generatore attributi

    EntDyn[] lista = this.getEntDynFiglie1N(tabella);
    if (lista != null) {
      EntDyn ent = null;
      for (int i = 0; i < lista.length; i++) {
        ent = lista[i];
        if (ent.isOk()) {
          // Se s'è l'entita dinamica collegata allora la elimino
          StringBuffer buf = new StringBuffer("");
          buf.append("delete from ");
          buf.append(ent.getEntita());
          buf.append(" where exists(select 1 from ");
          buf.append(ent.getEntitaP());
          buf.append(" where ");
          buf.append(where);
          if (where != null && where.length() > 0) buf.append(" and ");
          buf.append(ent.getJoin());
          buf.append(" ) ");
          // Eseguo la vera e proprie eliminazione dell'entità collegata
          try {
            this.sql.update(buf.toString(), param);

          } catch (SQLException e) {
            throw new GestoreException(
                "Errore nell'eliminazione delle occorrenze nelle entità dinamiche per: "
                    + tabella
                    + " !", "deleteEstensioneGeneratore",
                new String[] { ent.getEntita() }, e);
          }
        }
      }
    }

  }

  /**
   * Funzione che esegue la copia del generatore attributi
   *
   * @param entita
   *        Entità di partenza
   * @param where
   *        Filtro sull'entità
   * @param params
   *        Parametri del filtro
   * @param campi
   *        Campi da settare sulla destinazione
   * @param valori
   *        Valori da settare
   * @param eliminaOriginali
   *        Flag per dire di eliminare gli originali
   */
  public void copiaOccorrenzeGeneratoreAttributi(String entita, String where,
      Object[] params, String[] campi, Object[] valori, boolean eliminaOriginali)
      throws GestoreException {
    // Eliminazione delle entita del generatore attributi

    EntDyn ent = this.getEntDynGenAttributi(entita);
    if (ent.isOk()) {
      // Se s'è l'entita dinamica collegata allora la elimino
      StringBuffer buf = new StringBuffer("");
      StringBuffer bufWhere = new StringBuffer("exists(select 1 from ");
      bufWhere.append(ent.getEntitaP());
      bufWhere.append(" where ");
      bufWhere.append(where);
      if (where != null && where.length() > 0) bufWhere.append(" and ");
      bufWhere.append(ent.getJoin());
      bufWhere.append(" ) ");

      buf.append("select * from ");
      buf.append(ent.getEntita());
      buf.append(" where ");
      buf.append(bufWhere);

      // Eseguo la vera e proprie eliminazione dell'entità collegata
      try {
        // Verifico che esista almeno una riga
        if (countOccorrenze(ent.getEntita(), bufWhere.toString(), params) > 0) {
          // Esiste almeno un0occorrenza

          List<?> ret = this.sql.getListHashMap(buf.toString(), params);
          if (ret != null && ret.size() > 0) {
            for (int i = 0; i < ret.size(); i++) {
              
              DataColumnContainer impl = new DataColumnContainer(sql,
                  ent.getEntita(), buf.toString(), params);
              
              // Chiavi dell'entità di estensione
              String colKeyDest[] = new String[ent.key.size()];
              for (int ik = 0; ik < colKeyDest.length; ik++) {
                colKeyDest[ik] = ent.getKey(ik);
                if (colKeyDest[ik] != null)
                  impl.getColumn(colKeyDest[ik]).setChiave(true);
              }
              
              String colDest[] = new String[campi.length];
              for (int ic = 0; ic < colDest.length; ic++) {
                colDest[ic] = ent.getKey(campi[ic]);
              }
              
              impl.setValoriFromMap((HashMap<?,?>) ret.get(i), !eliminaOriginali);

              // Setto i valori da settare
              for (int col = 0; col < colDest.length; col++) {
                if (colDest[col] != null && impl.isColumn(colDest[col])) {
                  impl.setValue(colDest[col], valori[col]);
                }
              }
              if (eliminaOriginali)
                impl.update(ent.getEntita(), sql);
              else
                // A questo punto eseguo l'inserimento
                impl.insert(ent.getEntita(), sql);
            }
          }
        }

      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella copia delle occorrenze del generatore attributi per: "
                + entita
                + " !", "copiaEstensioneGeneratore",
            new String[] { ent.getEntita() }, e);
      }
    }

  }

  /**
   * Funzione che esegue la copia degli oggetti associati. Non copiando
   * l'oggetto ma copiando solo il riferimento
   *
   * @param entita
   *        Entita
   * @param where
   *        Filtro sull'entità
   * @param params
   *        Parametri di filtro
   * @param campi
   * @param valoriChiavi
   * @param eliminaOriginali
   * @throws GestoreException
   */
  public void copiaOccorrenzeOggettiAssociati(String entita, String where,
      Object[] params, String[] campi, Object[] valori, boolean eliminaOriginali)
      throws GestoreException {
    EntOggass ent = getEntOggass(entita);

    if (ent.isOk()) {
      // Se s'è l'entita dinamica collegata allora la elimino
      StringBuffer buf = new StringBuffer("Select * from C0OGGASS where ");
      StringBuffer bufWhere = new StringBuffer(ent.getWhere());
      bufWhere.append(" and exists(select 1 from ");
      bufWhere.append(entita);
      bufWhere.append(" where ");

      bufWhere.append(ent.getJoin());
      bufWhere.append(" and ");
      bufWhere.append(where);

      bufWhere.append(" ) ");
      buf.append(bufWhere);
      // Eseguo la vera e proprie eliminazione dell'entità collegata
      try {
        // Verifico che esista almeno una riga
        if (countOccorrenze("C0OGGASS", bufWhere.toString(),
            ent.getParams(params)) > 0) {
          // Esiste almeno un0occorrenza

          List<?> ret = this.sql.getListHashMap(buf.toString(),
              ent.getParams(params));
          if (ret != null && ret.size() > 0) {
            String colDest[] = new String[campi.length];
            for (int i = 0; i < colDest.length; i++)
              colDest[i] = ent.getKey(campi[i]);
            Long c0acod = null;

            for (int i = 0; i < ret.size(); i++) {
              DataColumnContainer impl = new DataColumnContainer(sql, "C0OGGASS",
                  buf.toString(), ent.getParams(params));
              impl.getColumn("C0OGGASS.C0ACOD").setChiave(true);

              impl.setValoriFromMap((HashMap<?,?>) ret.get(i), !eliminaOriginali);

              // Setto i valori da settare
              for (int col = 0; col < colDest.length; col++) {
                if (colDest[col] != null && impl.isColumn(colDest[col])) {
                  impl.setValue(colDest[col], valori[col]);
                }
              }
              if (!eliminaOriginali) {
                if (c0acod == null) {
                  c0acod = new Long(getLongFromSelect(
                      "select max(c0acod) from c0oggass", null, true));
                }
                c0acod = new Long(c0acod.longValue() + 1);
                impl.setValue("C0OGGASS.C0ACOD", c0acod);
                // A questo punto eseguo l'inserimento
                impl.insert("C0OGGASS", sql);
              } else {
                // Se non devo eliminare gli originali allora eseguo solo
                // l'update sul campo
                impl.update("C0OGGASS", sql);
              }

            }
          }
        }

      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella copia delle occorrenze dei documenti associati: "
                + entita, "copiaOggettiAssociati",
            new String[] { ent.getEntita() }, e);
      }

    }

  }

  /**
   * Funzione che esegue la copia degli oggetti associati. Non copiando
   * l'oggetto ma copiando solo il riferimento
   *
   * @param entita
   *        Entita
   * @param where
   *        Filtro sull'entità
   * @param params
   *        Parametri di filtro
   * @param campi
   * @param valoriChiavi
   * @param eliminaOriginali
   * @throws GestoreException
   */
  public void copiaOccorrenzeNoteAvvisi(String entita, String where,
      Object[] params, String[] campi, Object[] valori, boolean eliminaOriginali)
      throws GestoreException {
    EntNoteAvvisi ent = getEntNoteAvvisi(entita);

    if (ent.isOk()) {
      // Se s'è l'entita dinamica collegata allora la elimino
      StringBuffer buf = new StringBuffer("Select * from G_NOTEAVVISI where ");
      StringBuffer bufWhere = new StringBuffer(ent.getWhere());
      bufWhere.append(" and exists(select 1 from ");
      bufWhere.append(entita);
      bufWhere.append(" where ");

      bufWhere.append(ent.getJoin());
      bufWhere.append(" and ");
      bufWhere.append(where);

      bufWhere.append(" ) ");
      buf.append(bufWhere);
      // Eseguo la vera e proprie eliminazione dell'entità collegata
      try {
        // Verifico che esista almeno una riga
        if (countOccorrenze("G_NOTEAVVISI", bufWhere.toString(),
            ent.getParams(params)) > 0) {
          // Esiste almeno un0occorrenza

          List<?> ret = this.sql.getListHashMap(buf.toString(),
              ent.getParams(params));
          if (ret != null && ret.size() > 0) {
            String colDest[] = new String[campi.length];
            for (int i = 0; i < colDest.length; i++)
              colDest[i] = ent.getKey(campi[i]);
            Long notecod = null;

            for (int i = 0; i < ret.size(); i++) {
              DataColumnContainer impl = new DataColumnContainer(sql, "G_NOTEAVVISI",
                  buf.toString(), ent.getParams(params));
              impl.getColumn("G_NOTEAVVISI.NOTECOD").setChiave(true);

              impl.setValoriFromMap((HashMap<?,?>) ret.get(i), !eliminaOriginali);

              // Setto i valori da settare
              for (int col = 0; col < colDest.length; col++) {
                if (colDest[col] != null && impl.isColumn(colDest[col])) {
                  impl.setValue(colDest[col], valori[col]);
                }
              }
              if (!eliminaOriginali) {
                if (notecod == null) {
                  notecod = new Long(getLongFromSelect(
                      "select max(notecod) from g_noteavvisi", null, true));
                }
                notecod = new Long(notecod.longValue() + 1);
                impl.setValue("G_NOTEAVVISI.NOTECOD", notecod);
                // A questo punto eseguo l'inserimento
                impl.insert("G_NOTEAVVISI", sql);
              } else {
                // Se non devo eliminare gli originali allora eseguo solo
                // l'update sul campo
                impl.update("G_NOTEAVVISI", sql);
              }
            }
          }
        }
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nella copia delle occorrenze delle note e avvisi: "
                + entita, "copiaNoteAvvisi",
            new String[] { ent.getEntita() }, e);
      }
    }
  }

  /**
   * Funzione che conta il numero di occorrenze nei documenti associati per un
   * record di un'entit&agrave.
   *
   * @param entita
   *        Entita
   * @param where
   *        Filtro sull'entità
   * @param params
   *        Parametri di filtro
   * @throws GestoreException
   */
  public long contaOccorrenzeOggettiAssociati(String entita, String where,
      Object[] params) throws GestoreException {
    long risultato = -1;
    EntOggass ent = getEntOggass(entita);

    if (ent.isOk()) {
    //Si contano il numero di caratteri '?' presenti nella where per capire
      //quanti campi C0AKEY devono essere valorizzati con i valori della chiave
      //dell'entità a cui sono associati i documenti
      //int n = GeneManager.contaCaratteriInStringa(where, "?");
      int n =StringUtils.countMatches(where, "?");
      StringBuffer bufWhere = new StringBuffer(ent.getWhere());
      if(n>0){
        for(int i=0;i<n;i++){
          bufWhere.append(" and C0OGGASS.C0AKEY");
          bufWhere.append(i + 1);
          bufWhere.append(" = ?");
          if (params!=null && params[i] instanceof Long)
            params[i] =((Long)params[i]).toString();
          else if(params!=null && params[i] instanceof Double)
            params[i] =((Double)params[i]).toString();
        }
      }
      
      risultato = countOccorrenze("C0OGGASS", bufWhere.toString(),
          ent.getParams(params));
    }

    return risultato;
  }

  /**
   * Funzione che conta il numero di occorrenze nelle note avvisi per un record
   * di un'entit&agrave.
   *
   * @param entita
   *        Entita
   * @param where
   *        Filtro sull'entità
   * @param params
   *        Parametri di filtro
   * @throws GestoreException
   */
  public long contaOccorrenzeNoteAvvisi(String entita, String where,
      Object[] params) throws GestoreException {
    long risultato = -1;
    EntNoteAvvisi ent = getEntNoteAvvisi(entita);

    if (ent.isOk()) {
    //Si contano il numero di caratteri '?' presenti nella where per capire
      //quanti campi NOTEKEY devono essere valorizzati con i valori della chiave
      //dell'entità a cui sono associati i documenti
      //int n = GeneManager.contaCaratteriInStringa(where, "?");
      int n =StringUtils.countMatches(where, "?");
      StringBuffer bufWhere = new StringBuffer(ent.getWhere());
      bufWhere.append(" and statonota <> 90 ");
      if(n>0){
        for(int i=0;i<n;i++){
          bufWhere.append(" and G_NOTEAVVISI.NOTEKEY");
          bufWhere.append(i + 1);
          bufWhere.append(" = ?");
          if (params!=null && params[i] instanceof Long)
            params[i] =((Long)params[i]).toString();
          else if(params!=null && params[i] instanceof Double)
            params[i] =((Double)params[i]).toString();
        }

      }

      risultato = countOccorrenze("G_NOTEAVVISI", bufWhere.toString(),
          ent.getParams(params));
    }

    return risultato;
  }

  /**
   * Funzione che verifica se un oggetto ha l'attributo di proprietà. (Campo
   * <b>GRPID</b>)
   *
   * @param oggetto
   *        Nome dell'oggetto
   * @return true se ha l'identificazione di proprietà, false se non ha
   *         proprietà
   *//*
  public boolean haveProprieta(String oggetto) {
    // Se non sono ancora stati caricati carico l'elenco degli oggetti che hanno
    // la proprietà
    if (this.oggettiDiProprieta == null) {

      try {
        List oggetti = this.sql.getListVector("select oggetto from W_GRPOBJS",
            new Object[] {});
        this.oggettiDiProprieta = new Vector();
        for (int i = 0; i < oggetti.size(); i++) {
          this.oggettiDiProprieta.add(((Vector) oggetti.get(i)).get(0).toString());
        }
      } catch (SQLException e) {

      }
    }
    if (this.oggettiDiProprieta != null)
      return this.oggettiDiProprieta.contains(oggetto);
    return false;
  }*/

/*  public int[] getElencoGruppi() {
    try {
      List vect = this.getSql().getListVector(
          "select id_gruppo from w_gruppi order by id_gruppo", new Object[] {});
      if (vect != null && vect.size() > 0) {
        int ret[] = new int[vect.size()];
        for (int i = 0; i < vect.size(); i++) {
          ret[i] = SqlManager.getValueFromVectorParam(vect.get(i), 0).longValue().intValue();
        }
        return ret;
      }
    } catch (Throwable e) {

    }
    return new int[] {};
  }*/

  public long getCRCAzioni(Object tipo, Object azione, Object oggetto,
      Object valore, Object inOr, Object viselenco) {
    StringBuffer buf = new StringBuffer("");
    buf.append(tipo);
    buf.append(azione);
    buf.append(oggetto);
    buf.append(valore);
    buf.append(inOr);
    buf.append(viselenco);

    CRC32 crc = new CRC32();
    crc.update(buf.toString().getBytes());
    return crc.getValue();

  }

  public long getCRCproazi(Object tipo, Object azione, Object oggetto,
      Object valore) {
    StringBuffer buf = new StringBuffer("");
    buf.append(tipo);
    buf.append(azione);
    buf.append(oggetto);
    buf.append(valore);

    CRC32 crc = new CRC32();
    crc.update(buf.toString().getBytes());
    return crc.getValue();
  }

  /**
   * Funzione che esebue il carico dei dati delle protezioni in un profilo
   *
   * @param profRet
   */
  public void caricaProfilo(ConfigurazioneProfilo prof) {
    // Aggiungo tutti i dati di default
    HashMap<String, Long> hashAppoggio = new HashMap<String, Long>();
    try {
      // Come prima cosa carico i valori di default
      List<?> ret = getSql().getListVector(
          "select TIPO, AZIONE, OGGETTO, VALORE, INOR, VISELENCO, CRC from W_AZIONI",
          new Object[] {});
      if (ret != null) {
        for (int i = 0; i < ret.size(); i++) {
          Vector<?> row = (Vector<?>) ret.get(i);
          Long crc = new Long(getCRCAzioni(row.get(0), row.get(1), row.get(2),
              row.get(3), row.get(4), row.get(5)));
          prof.addProtec(
              row.get(0).toString(),
              row.get(1).toString(),
              row.get(2).toString(),
              "1".equals(row.get(3).toString()),
              true,
              crc.equals(SqlManager.getValueFromVectorParam(row, 6).longValue()),
              SqlManager.getValueFromVectorParam(row, 3).longValue());
          hashAppoggio.put(row.get(0).toString()
              + "."
              + row.get(1).toString()
              + "."
              + row.get(2).toString(), SqlManager.getValueFromVectorParam(row,
              3).longValue());

          if (!crc.equals(SqlManager.getValueFromVectorParam(row, 6).longValue())) {
        	  String msgErr = "Caricamento profilo del " + prof.getIdProfilo()
        	  		+ ": profilo corrotto a causa dell'errato valore del CRC nella tabella W_AZIONI del record con "
        	  		+ "TIPO=" + row.get(0).toString()
                    + ", AZIONE=" + row.get(1).toString()
                    + ", OGGETTO=" + row.get(2).toString()
                    + ". CRC su DB = " + row.get(6).toString() + "; CRC calcolato = " +crc + ".";

        	  logger.error(msgErr);
          }
        }
      }
    } catch (Throwable t) {
      logger.error(t.getMessage(), t);
    }
    // Aggiungo tutti i dati con i valori in or
    try {
      List<?> ret = getSql().getListVector(
          "select TIPO, AZIONE, OGGETTO, VALORE, CRC "
              + "from W_PROAZI where COD_PROFILO=? and  "
              + "exists( select 1 from W_AZIONI "
              + "where W_AZIONI.TIPO = W_PROAZI.TIPO and "
              + "W_AZIONI.AZIONE = W_PROAZI.AZIONE and "
              + "W_AZIONI.OGGETTO = ? and "
              + "W_AZIONI.INOR = ? ) order by valore asc",
          new Object[] { prof.getIdProfilo(), "*", new Integer(1) });
      if (ret != null) {
        for (int i = 0; i < ret.size(); i++) {
          Vector<?> row = (Vector<?>) ret.get(i);
          Long crc = new Long(getCRCproazi(row.get(0), row.get(1), row.get(2),
              row.get(3)));
          prof.addProtec(
              row.get(0).toString(),
              row.get(1).toString(),
              row.get(2).toString(),
              "1".equals(row.get(3).toString()),
              false,
              crc.equals(SqlManager.getValueFromVectorParam(row, 4).longValue()),
              (Long) hashAppoggio.get(row.get(0).toString()
                  + "."
                  + row.get(1).toString()
                  + "."
                  + row.get(2).toString()));

          if (!crc.equals(SqlManager.getValueFromVectorParam(row, 4).longValue())) {
        	  String msgErr = "Caricamento profilo con COD_PROFILO=' " + prof.getIdProfilo()
        	  		+ "': profilo corrotto a causa dell'errato valore del CRC nella tabella W_PROAZI del record con"
        	  		+ "TIPO=" + row.get(0).toString()
                    + ", AZIONE=" + row.get(1).toString()
                    + ", OGGETTO=" + row.get(2).toString()
                    +". CRC su DB = " + row.get(4).toString() + "; CRC calcolato = " + crc + ".";

        	  logger.error(msgErr);
          }
        }
      }
    } catch (Throwable e) {
      logger.error(e.getMessage(), e);
    }
    // Aggiungo tutti i dati con i valori in and
    try {
      List<?> ret = getSql().getListVector(
          "select TIPO, AZIONE, OGGETTO, VALORE, CRC "
              + "from W_PROAZI where COD_PROFILO=? and  "
              + "exists( select 1 from W_AZIONI "
              + "where W_AZIONI.TIPO = W_PROAZI.TIPO and "
              + "W_AZIONI.AZIONE = W_PROAZI.AZIONE and "
              + "W_AZIONI.OGGETTO = ? and "
              + "W_AZIONI.INOR = ? ) order by valore desc",
          new Object[] { prof.getIdProfilo(), "*", new Integer(0) });
      if (ret != null) {
        for (int i = 0; i < ret.size(); i++) {
          Vector<?> row = (Vector<?>) ret.get(i);
          Long crc = new Long(getCRCproazi(row.get(0), row.get(1), row.get(2),
              row.get(3)));
          prof.addProtec(
              row.get(0).toString(),
              row.get(1).toString(),
              row.get(2).toString(),
              "1".equals(row.get(3).toString()),
              false,
              crc.equals(SqlManager.getValueFromVectorParam(row, 4).longValue()),
              (Long) hashAppoggio.get(row.get(0).toString()
                  + "."
                  + row.get(1).toString()
                  + "."
                  + row.get(2).toString()));

            if (!crc.equals(SqlManager.getValueFromVectorParam(row, 4).longValue())) {
              String msgErr = "Caricamento profilo con COD_PROFILO=' " + prof.getIdProfilo()
                    + "': profilo corrotto a causa dell'errato valore del CRC nella tabella W_PROAZI del record con"
                    + "TIPO=" + row.get(0).toString()
                    + ", AZIONE=" + row.get(1).toString()
                    + ", OGGETTO=" + row.get(2).toString()
                    +". CRC su DB = " + row.get(4).toString() + "; CRC calcolato = " + crc + ".";

              logger.error(msgErr);
            }
        }
      }
    } catch (Throwable e) {
      logger.error(e.getMessage(), e);
    }

  }

  /**
   * @return the profili
   */
  public GestoreProfili getProfili() {
    return profili;
  }

  public long getCRCProfilo(DataColumnContainer impl, boolean original)
      throws GestoreException {
    StringBuffer buf = new StringBuffer("");
    String cols[] = new String[] { "W_PROFILI.COD_PROFILO",
        "W_PROFILI.COD_PROFILO", "W_PROFILI.NOME", "W_PROFILI.CODAPP",
        "W_PROFILI.FLAG_INTERNO", "W_PROFILI.COD_CLIENTE" };

    try {
      DataColumn col;
      for (int i = 0; i < cols.length; i++) {
        col = impl.getColumn(cols[i]);
        if (original)
          buf.append(col.getOriginalValue().toString(false));
        else
          buf.append(col.getValue().toString(false));
      }
      CRC32 crc = new CRC32();
      crc.update(buf.toString().getBytes());
      return crc.getValue();
    } catch (GestoreException e) {
      throw e;
    }
  }

  /**
   * Funzione che estrae la sottodescrizione di un tipo dai W_OGGETTI
   *
   * @param tipo
   *        Tipo di oggetto
   * @param oggetto
   *        Identificativo dell'oggetto
   * @return String con la descrizione da mettere in pendice all'oggetto
   */
  public String getSubDescrFromW_OGGETTI(String tipo, String oggetto) {
    String lTipo = null;
    String lOggetto = null;
    boolean continua = false;
    StringBuffer buf = new StringBuffer("");
    if ("PAGE".equals(tipo)) {
      lTipo = "MASC";
      lOggetto = oggetto.indexOf('.') > 0 ? oggetto.substring(0,
          oggetto.lastIndexOf('.')) : oggetto;
    } else if ("FUNZ".equals(tipo) || "SEZ".equals(tipo)) {
      // Se si tratta di una funzione elimino il tipo di funzione d'inizio
      if ("FUNZ".equals(tipo))
        oggetto = oggetto.indexOf('.') > 0
            ? oggetto.substring(oggetto.indexOf('.') + 1)
            : oggetto;
      lOggetto = oggetto.indexOf('.') > 0 ? oggetto.substring(0,
          oggetto.lastIndexOf('.')) : oggetto;
      if (lOggetto.indexOf('.') >= 0
          && lOggetto.indexOf('.', lOggetto.indexOf('.') + 1) > 0) {
        lTipo = "PAGE";
        continua = true;
      } else
        lTipo = "MASC";
    } else
      return "";
    try {
      Vector<?> v;
      v = this.sql.getVector(
          "select descr from W_OGGETTI where tipo = ? and oggetto = ?",
          new Object[] { lTipo, lOggetto });
      if (v != null && v.size() > 0) {
        buf.append(SqlManager.getValueFromVectorParam(v, 0).toString());
      }
    } catch (SQLException e) {
    }
    if (buf.length() > 0) buf.append(" - ");
    return (continua ? getSubDescrFromW_OGGETTI(lTipo, lOggetto) : "")
        + buf.toString();
  }

  /**
   * Funzione che esegue l'aggiornamento delle intestazioni nel Database
   *
   * @param entita
   *        Entita modificata
   * @param intestazione
   *        Nuova intestazione
   * @param chiavi
   *        Chiavi dell'entità
   */
  public void aggiornaIntestazioniInDB(String entita, String intestazione,
      Object[] chiavi) throws GestoreException {
    String updateTables[] = new String[] {};
    Object params[] = new Object[] {};
    if ("IMPR".equalsIgnoreCase(entita)) {
      params = new Object[] { intestazione, chiavi[0] };
      // Aggiornamento delle imprese
      updateTables = new String[] { "ditg",
          "update ditg set nomimo = ? where dittao = ?", "edit",
          "update edit set nomime = ? where codime = ?", "gare",
          "update gare set nomima = ? where ditta = ?", "appa",
          "update appa set impagg = ? where cimpag = ?", "appa",
          "update appa set imprese = ? where ncodim = ?", "bglpag",
          "update bglpag set nimpfat = ? where cimpfat = ?", "ordi",
          "update ordi set impres = ? where ditimp = ?", "suba",
          "update suba set nosuba = ? where codsub = ?", "ragimp",
          "update ragimp set nomdic = ? where coddic = ?", "perp",
          "update perp set nomimp = ? where codimp = ?", "rapint",
          "update rapint set nimrap = ? where cimrap = ?", "c3pozzi",
          "update c3pozzi set nomdit = ? where coddit = ?", "ximseor",
          "update ximseor set dittainst = ? where eff_co_dit = ?", "ximsever",
          "update ximsever set dittainst = ? where efd_co_dit = ?", "ximsever",
          "update ximsever set efd_ditins = ? where efd_codins = ?",
          "xsegnpann",
          "update xsegnpann set dittainst = ? where seg_co_dit = ?",
          "xsegnpann", "update xsegnpann set prod = ? where seg_codins = ?",
          "itc", "update itc set eseitc = ? where cesitc = ?", "impazi",
          "update impazi set nomimp4 = ? where codimp4 = ?" };
    } else if ("TECNI".equalsIgnoreCase(entita)) {
      // Aggiornamento dei tecnici
      updateTables = new String[] { "comp_g",
          "update comp_g set c_nomr = ? where c_resp = ?", "membro",
          "update membro set memnom = ? where memcod = ?", "gfof",
          "update gfof set nomfof = ? where codfof = ?", "r2tecn",
          "update r2tecn set nomtec = ? where r2tecn.codtec = ?", "d2tecn",
          "update d2tecn set nomtec = ? where d2tecn.codtec = ?", "g2tecn",
          "update g2tecn set nomtec = ? where codtec = ?", "contab",
          "update contab set contab = ? where ccontab = ?", "teccoll",
          "update teccoll set nomcol = ? where codcol = ?", "citric",
          "update citric set nteric = ? where cteric = ?", "itc",
          "update itc set nteitc = ? where cteitc = ?", "uffint",
          "update uffint set nomres = ? where codres = ?", "c1commi",
          "update c1commi set relato = ? where codtec = ?", "c2autor",
          "update c2autor set nomtec = ? where codtec = ?", "cdu",
          "update cdu set cdu_nomop = ? where cdu_codop = ?", "esposti",
          "update esposti set esp_nom_tec = ? where esp_codtec = ?",
          "pericola",
          "update pericola set per_nom_tec = ? where per_codtec = ?", "parcon",
          "update parcon set memnom = ? where memcod = ?", "condoni",
          "update condoni set con_tecper = ? where con_codper = ?", "provve",
          "update provve set pro_nomfun = ? where pro_codfun = ?", "costr",
          "update costr set nometec = ? where codtec = ?", "citric",
          "update citric set nterec = ? where cterec = ?", "c1autor",
          "update c1autor set nomtec = ? where codpro = ?" };
      params = new Object[] { intestazione, chiavi[0] };
    } else if ("TEIM".equalsIgnoreCase(entita)) {
      // Aggiornamento dei tecnici delle imprese
      updateTables = new String[] { "impleg",
          "update impleg set nomleg = ? where codleg = ?", "impdte",
          "update impdte set nomdte = ? where coddte = ?", "impazi",
          "update impazi set nomtec = ? where codtec = ?", "g_impcol",
          "update g_impcol set nomtec = ? where codtec = ?", "appa",
          "update appa set apnleg = ? where apcleg = ?", "appa",
          "update appa set apndte = ? where apcdte = ?" };
      params = new Object[] { intestazione, chiavi[0] };
    }else if ("UFFINT".equalsIgnoreCase(entita)) {
        // Aggiornamento degli uffici intestatari
        updateTables = new String[] { "peri",
            "update peri set entint = ? where cenint = ?" };
        params = new Object[] { intestazione, chiavi[0] };
      }
    for (int i = 0; (i + 1) < updateTables.length; i += 2) {
      // Eseguo l'update solo se esiste la tabella
      if (this.sql.isTable(updateTables[i])) {
        // Eseguo l'update
        try {
          this.sql.update(updateTables[i + 1], params);
        } catch (SQLException e) {
          throw new GestoreException(
              "Errroe durante l'aggiornamento dell'intestazione di "
                  + entita
                  + " per la tabella: "
                  + updateTables[i], "aggiornaIntestazioniInDB" + entita, e);
        }
      }
    }
  }

  /**
   * Funzione che verifica se un campo è impostato come campo nella codifica
   * automatica
   *
   * @param entita
   *        Entita
   * @param campo
   *        Campo dell'entità
   * @return true se deve essere calcolato
   */
  public boolean isCodificaAutomatica(String entita, String campo) {
    try {
      List<?> ret = this.sql.getVector(
          "Select Count(*) From G_CONFCOD Where NOMENT = ? And NOMCAM = ? And "
              + "CODCAL is not null", new Object[] { entita, campo });
      if (ret != null) {
        if (SqlManager.getValueFromVectorParam(ret, 0).longValue().longValue() > 0)
          return true;
      }
    } catch (Throwable e) {
    }
    return false;
  }

  public HashMap<?,?> getParametriCodificaAutomatica(String entita, String campo)
      throws SQLException, GestoreException{
    HashMap<String, Object> result = new HashMap<String, Object>();

    JdbcParametro parametro = null;
    Long contatore = null;

    List<?> ret = this.sql.getVector(
        "select CODCAL, CONTAT from G_CONFCOD where NOMENT = ? and NOMCAM = ?",
        new Object[] { entita, campo });
    parametro = SqlManager.getValueFromVectorParam(ret, 0);

    if(parametro != null){
      contatore = SqlManager.getValueFromVectorParam(ret, 1).longValue();
      result.put("parametro", parametro);
      result.put("contatore", contatore);
    }
    return result;
  }

  /**
   * Funzione che esegue il calcolo delle codifica automatica per un campo di
   * database
   *
   * @param entita
   *        Entita
   * @param campo
   *        Campo
   * @return Campo chiave ricalcolato
   * @throws GestoreException
   */
  public String calcolaCodificaAutomatica(String entita, String campo)
      throws GestoreException {
    try {
      JdbcParametro jdbcParametroCodiceCalcolo = null;
      Long contatore = null;

      // Inizializzazione delle variabili jdbcParametroCodiceCalcolo e contat
      HashMap<?,?> parametriPerCodifica = this.getParametriCodificaAutomatica(entita, campo);

      if(parametriPerCodifica != null){
        jdbcParametroCodiceCalcolo = (JdbcParametro) parametriPerCodifica.get("parametro");
        contatore = (Long) parametriPerCodifica.get("contatore");
      }

      if (jdbcParametroCodiceCalcolo != null) {
        String codcal = jdbcParametroCodiceCalcolo.stringValue();
        boolean codiceUnivoco = false;
        int numeroTentativi = 0;
        StringBuffer strBuffer = null;

        long tmpContatore = contatore.longValue();
        // tento di inserire il record finchè non genero un codice univoco a
        // causa della concorrenza, o raggiungo il massimo numero di tentativi
        while (!codiceUnivoco
            && numeroTentativi < CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {

          strBuffer = new StringBuffer("");
          // Come prima cosa eseguo l'update del contatore
          tmpContatore++;
          this.sql.update(
              "update G_CONFCOD set contat = ? where NOMENT = ? And NOMCAM = ?",
              new Object[] { new Long(tmpContatore), entita, campo });

          strBuffer = this.calcoloCodiceAutomatico(tmpContatore, codcal);

          List<?> listaTMP = this.sql.getListVector(
              "select * from " + entita + " where " + campo + " = ?",
              new Object[] { strBuffer.toString() });
          if (listaTMP == null || listaTMP.size() == 0)
            codiceUnivoco = true;
          else {
            numeroTentativi++;
            //codcal = jdbcParametroCodiceCalcolo.stringValue();
          }
        }

        if (!codiceUnivoco
            && numeroTentativi >= CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT) {
          // throw new DataIntegrityViolationException("Raggiunto limite massimo
          // di tentativi");
          throw new GestoreException(
              "Codifica automatica non riuscita dopo "
                  + CostantiGenerali.NUMERO_MAX_TENTATIVI_INSERT
                  + " tentativi a causa del disallineamento del contatore è rispetto "
                  + "ai codici inseriti", "erroreCodiceCodificaAutomatica");
        }
        return strBuffer.toString();
      } else
        throw new GestoreException("Codice di codifica automatica sbagliato !",
            "erroreCodiceCodificaAutomatica");

    } catch (GestoreException e) {
      throw e;
    } catch (Throwable t) {
      throw new GestoreException(
          "Errore nel calcolo della codifica automatica per "
              + entita
              + "."
              + campo
              + " !", "calcoloCodificaAutomatica", t);
    }
  }

  /**
   * Calcolo del codice in base alla sintassi e al contatore passati per argomento
   *
   * @param contatore
   * @param codcal
   * @param strBuffer
   * @throws GestoreException
   */
  public StringBuffer calcoloCodiceAutomatico(long contatore, String codcal)
      throws GestoreException {
    StringBuffer strBuffer = new StringBuffer("");

    while (codcal.length() > 0) {
      switch (codcal.charAt(0)) {
      case '<': // Si tratta di un'espressione numerica
        String strNum = codcal.substring(1, codcal.indexOf('>'));

        // Verifico se ci sta la lunghezza
        if (strNum.length() < String.valueOf(contatore).length())
          throw new GestoreException(
              "Codifica automatica non riuscita. Superamento del numero "
                  + "di cifre disponibili: il numero "
                  + String.valueOf(contatore)
                  + " non sta nelle cifre <"
                  + strNum
                  + ">", "erroreCodiceCodificaAutomatica");
        if (strNum.charAt(0) == '0') {
          // Giustificato a destra
          for (int i = 0; i < (strNum.length() - String.valueOf(contatore).length()); i++)
            strBuffer.append('0');
        }
        strBuffer.append(String.valueOf(contatore));

        codcal = codcal.substring(codcal.indexOf('>') + 1);
        break;
      case '"': // Si tratta di una parte costante
        strBuffer.append(codcal.substring(1, codcal.indexOf('"', 1)));
        codcal = codcal.substring(codcal.indexOf('"', 1) + 1);
        break;
      default:
        throw new GestoreException(
            "Codice di codifica automatica sbagliato: \"" + codcal + "\"",
            "erroreCodiceCodificaAutomatica");
      }
    }
    return strBuffer;
  }

  /**
   * Funzione che esegue l'eliminazione di tutte le note estese
   *
   * @param entita
   *        Entità
   * @param where
   *        Where per il filtro delle occorrenze
   * @param from
   *        Eventuale from per la Join
   * @param params
   *        Parametri per la where
   */
  public void deleteNOTEOWF(String entita, String where, String from,
      Object[] params) throws GestoreException {
    try {
      String escape = "";
      String entitaEscaped = entita;
      if (UtilityStringhe.containsSqlWildCards(entita)) {
        entitaEscaped = UtilityStringhe.escapeSqlString(entita);
        try {
          SqlComposer composer = it.eldasoft.utils.sql.comp.SqlManager.getComposer(ConfigManager.getValore(CostantiGenerali.PROP_DATABASE));
          escape = composer.getEscapeSql();
        } catch (SqlComposerException e) {
          // non si verifica mai, il caricamento metadati gia' testa che la
          // property sia settata correttamente
          logger.error(
              "Tipo di database non configurato correttamente nel file di configurazione",
              e);
        }
      }
      List<?> listOWF = this.sql.getListVector(
          "Select nometab from noteowf where nometab like ? "
              + escape
              + " group by nometab", new Object[] { entitaEscaped + ".%" });
      if (listOWF != null) {
        for (int i = 0; i < listOWF.size(); i++) {
          StringBuffer select = new StringBuffer("select ");
          String nometab = SqlManager.getValueFromVectorParam(listOWF.get(i), 0).stringValue();
          select.append(nometab);
          select.append(" from ");
          select.append(entita);
          if (from != null && from.length() > 0) {
            select.append(", ");
            select.append(from);
          }
          select.append(" where ");
          select.append(nometab);
          select.append(" like '>>%'");
          if (where != null && where.length() > 0) {
            select.append(" and ");
            select.append(where);
          }
          List<?> values = this.sql.getListVector(select.toString(), params);
          if (values != null) {
            for (int k = 0; k < values.size(); k++) {
              gestNoteDEL(nometab, SqlManager.getValueFromVectorParam(
                  values.get(k), 0).stringValue());
            }
          }

        }
      }
    } catch (Throwable t) {
      throw new GestoreException("Errore in eliminazione delle note estese !",
          "deleteNOTEOWF", t);
    }

  }

  /**
   * Funzione che esegue l'eventuale eliminazione delle note estese
   *
   * @param tabECol
   * @param value
   */
  private void gestNoteDEL(String tabECol, String value) {
    if (value != null && value.length() == 2000) {
      try {
        if (value.indexOf('&') > 0) {
          String lsTmp = value.substring(value.lastIndexOf('&') + 1);
          lsTmp = lsTmp.substring(0, lsTmp.length() - 2);
          Long next = Long.valueOf(lsTmp);
          while (next != null && next.longValue() != 0) {

          }
        }
      } catch (Throwable t) {

      }
    }
  }

  /**
   * Metodo richiamato dal gestore prima dell'eliminazione dell'entità
   *
   * @param entita
   *        entità da eliminare
   * @param impl
   *        contenitore dei dati ricevuti dalla pagina
   * @throws GestoreException
   */
  public void preDeleteGestore(String entita, DataColumnContainer impl)
      throws GestoreException {
    Tabella tab = DizionarioTabelle.getInstance().getDaNomeTabella(entita);
    if (tab != null) {
      // Se la tabella esiste allora creo la where sui campi chiave
      StringBuffer where = new StringBuffer("");
      Vector<Object> par = new Vector<Object>();
      Object params[] = null;
      // Scorro tutti i campi chiave
      for (int i = 0; i < tab.getCampiKey().size(); i++) {
        String campo = (tab.getCampiKey().get(i)).getNomeFisicoCampo();
        if (impl.isColumn(campo)) {
          if (where.toString().length() > 0) where.append(" and ");
          where.append(campo);
          where.append(" = ? ");
          par.add(impl.getObject(campo));
        }
      }
      params = par.toArray(new Object[0]);
      eliminaOccorrenzeGeneratoreAttributi(entita, where.toString(), params);
      eliminaOccorrenzeEntitaFiglie1N(entita, where.toString(), params);
      eliminaOccorrenzeOggettiAssociati(entita, where.toString(), params);
      eliminaOccorrenzeNoteAvvisi(entita, where.toString(), params);
      eliminaOccorrenzeScadenzario(entita, where.toString(), params);
    }
  }

  /**
   * Funzione chiamata dal gestore perima dell'inserimento dell'entità
   *
   * @param entita
   * @param impl
   * @throws GestoreException
   */
  public void preInsertGestore(String entita, DataColumnContainer impl)
      throws GestoreException {
    EntDyn ent = getEntDynGenAttributi(entita);

    if (ent.hasKey(impl) && impl.isModifiedTable(ent.getEntita())) {
      try {
        // Copio i campi chiave prima dell'inserimento
        for (int i = 0; i < ent.keyCount(); i++) {
          if (impl.isColumn(ent.getKeyP(i)))
            impl.setValue(ent.getKey(i), impl.getObject(ent.getKeyP(i)));
        }
        impl.insert(ent.getEntita(), this.getSql());
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore nell'inserimento dell'entità dinamica !", "insertDinamica",
            new Object[] { ent.getEntita() }, e);
      }
    }

  }

  /**
   * Funzione chemata dal gestore prima dell'update sull'entità
   *
   * @param entita
   * @param impl
   * @throws GestoreException
   */
  public void preUpdateGestore(String entita, DataColumnContainer impl)
      throws GestoreException {
    EntDyn ent = getEntDynGenAttributi(entita);

    if (ent.hasKey(impl) && impl.isModifiedTable(ent.getEntita())) {
      boolean update = true;
      try {
        // Copio i campi chiave prima dell'inserimento
        for (int i = 0; i < ent.keyCount(); i++) {
          // Se almeno un campo chiave è vuoto allora si deve inserire
          // l'occorrenza
          if (impl.isColumn(ent.getKeyP(i))
              && impl.getColumn(ent.getKey(i)).getValue().getValue() == null) {
            impl.setValue(ent.getKey(i), impl.getObject(ent.getKeyP(i)));
            update = false;
          }
        }
        if (update)
          impl.update(ent.getEntita(), this.getSql());
        else
          impl.insert(ent.getEntita(), this.getSql());
      } catch (SQLException e) {
        throw new GestoreException("Errore nell'update dell'entità dinamica !",
            "updateDinamica", new Object[] { ent.getEntita() }, e);
      }
    }

  }

  private EntOggass getEntOggass(String entita) throws GestoreException {
    entita = entita.toUpperCase();
    EntOggass ent = (EntOggass) this.entitaOggassCollegate.get(entita);
    if (ent == null) {
      StringBuffer join = new StringBuffer("");
      StringBuffer where = new StringBuffer("");
      Vector<String> campiKey = new Vector<String>();
      Tabella tab = DizionarioTabelle.getInstance().getDaNomeTabella(entita);
      Vector<String> params = new Vector<String>();
      if (tab != null) {
        where.append("C0OGGASS.C0AENT = ?");
        params.add(entita);
        // Se l'entità è valida allora creo la join
        for (int i = 0; i < tab.getCampiKey().size(); i++) {
          Campo oCampo = tab.getCampiKey().get(i);
          if (join.length() > 0) join.append(" and ");
          join.append("C0OGGASS.C0AKEY");
          join.append(i + 1);
          join.append(" = ");
          switch (oCampo.getTipoColonna()) {
          case Campo.TIPO_STRINGA:
            join.append(oCampo.getNomeFisicoCampo());
            break;
          case Campo.TIPO_INTERO:
            join.append(getSql().getDBFunction("inttostr",
                new String[] { oCampo.getNomeFisicoCampo() }));
            break;
          default:
            throw new GestoreException(
                "Tipo di dato per un campo chiave nella C0OGGASS non ancora gestito ("
                    + oCampo.getTipoColonna()
                    + ")", "tipoChiaveOggettiAssociati");
          }
          campiKey.add(oCampo.getNomeCampo().toUpperCase());
        }
        for (int i = tab.getCampiKey().size() + 1; i < 6; i++) {
          if (where.length() > 0) where.append(" and ");
          where.append("C0OGGASS.C0AKEY");
          where.append(i);
          where.append(" = ?");
          params.add("#");
        }
      }
      ent = new EntOggass(entita, where.toString(), join.toString(),
          params.toArray(new Object[0]),
          (String[]) campiKey.toArray(new String[0]));
      this.entitaOggassCollegate.put(entita, ent);
    }

    return ent;
  }

  private EntNoteAvvisi getEntNoteAvvisi(String entita) throws GestoreException {
    entita = entita.toUpperCase();
    EntNoteAvvisi ent = (EntNoteAvvisi) this.entitaNoteAvvisiCollegate.get(entita);
    if (ent == null) {
      StringBuffer join = new StringBuffer("");
      StringBuffer where = new StringBuffer("");
      Vector<String> campiKey = new Vector<String>();
      Tabella tab = DizionarioTabelle.getInstance().getDaNomeTabella(entita);
      Vector<String> params = new Vector<String>();
      if (tab != null) {
        where.append("G_NOTEAVVISI.NOTEENT = ?");
        params.add(entita);
        // Se l'entità è valida allora creo la join
        for (int i = 0; i < tab.getCampiKey().size(); i++) {
          Campo oCampo = tab.getCampiKey().get(i);
          if (join.length() > 0) join.append(" and ");
          join.append("G_NOTEAVVISI.NOTEKEY");
          join.append(i + 1);
          join.append(" = ");
          switch (oCampo.getTipoColonna()) {
          case Campo.TIPO_STRINGA:
            join.append(oCampo.getNomeFisicoCampo());
            break;
          case Campo.TIPO_INTERO:
            join.append(getSql().getDBFunction("inttostr",
                new String[] { oCampo.getNomeFisicoCampo() }));
            break;
          default:
            throw new GestoreException(
                "Tipo di dato per un campo chiave nella G_NOTEAVVISI non ancora gestito ("
                    + oCampo.getTipoColonna()
                    + ")", "tipoChiaveNoteAvvisi");
          }
          campiKey.add(oCampo.getNomeCampo().toUpperCase());
        }
        /*for (int i = tab.getCampiKey().size() + 1; i < 6; i++) {
          if (where.length() > 0) where.append(" and ");
          where.append("G_NOTEAVVISI.NOTEKEY");
          where.append(i);
          where.append(" is null");
          //params.add("#");
        }*/
      }
      ent = new EntNoteAvvisi(entita, where.toString(), join.toString(),
          params.toArray(new Object[0]),
          (String[]) campiKey.toArray(new String[0]));
      this.entitaNoteAvvisiCollegate.put(entita, ent);
    }

    return ent;
  }

  /**
   * Estrae le impostazioni dell'entità collegata mediante generatore attributi
   * all'entità in input
   *
   * @param entita
   *        entità per la quale ricercare l'entità del generatore attributi
   * @return definizione entità del generatore attributi
   */
  private EntDyn getEntDynGenAttributi(String entita) {
    if (entita != null) entita = entita.toUpperCase();
    EntDyn ent = (EntDyn) this.entitaDynCollegate.get(entita);
    // Se non è stata trovata allora la estraggo da dynent
    if (ent == null) {
      // si aggiunge un blocco sincronizzato, e non appena si accede all'interno
      // del blocco si verifica che durante l'attesa non sia stata definita
      // l'entità dinamica da qualcun altro
      synchronized (this.entitaDynCollegate) {
        ent = (EntDyn) this.entitaDynCollegate.get(entita);
        if (ent == null) {

          try {
            String entRet = (String) this.getSql().getObject(
                "select dynent_name from DYNENT where dynent_name_p = ? and dynent_type = 2",
                new Object[] { entita });
            if (entRet != null) {
              ent = new EntDyn(entRet, entita);
              List<?> campi = this.getSql().getListVector(
                  "select dyncam_name, dyncam_name_p from DYNCAM where dynent_name = ? and dyncam_pk = '1' and dynent_type = 2",
                  new Object[] { entRet });
              if (campi != null && campi.size() > 0) {
                for (int i = 0; i < campi.size(); i++)
                  ent.addKey(
                      SqlManager.getValueFromVectorParam(campi.get(i), 0).stringValue(),
                      SqlManager.getValueFromVectorParam(campi.get(i), 1).stringValue());
              }
            } else
              ent = new EntDyn(null, null);
            entitaDynCollegate.put(entita, ent);
          } catch (Throwable t) {
            if (logger.isDebugEnabled()) logger.error(t);
          }

        }
      }
    }
    return ent;
  }

  /**
   * Estrae le impostazioni delle entità estensione figlie 1:N collegate
   * all'entità in input
   *
   * @param entita
   *        entità per la quale ricercare le entità figlie 1:N
   * @return definizione entità figlie 1:N
   */
  private EntDyn[] getEntDynFiglie1N(String entita) {
    if (entita != null) entita = entita.toUpperCase();
    ArrayList<EntDyn> lista = (ArrayList<EntDyn>) this.entitaDynFiglie1NCollegate.get(entita);
    // Se non è stata trovata allora la estraggo da dynent
    if (lista == null) {
      // si aggiunge un blocco sincronizzato, e non appena si accede all'interno
      // del blocco si verifica che durante l'attesa non sia stata definita
      // l'entità dinamica da qualcun altro
      synchronized (this.entitaDynFiglie1NCollegate) {
        lista = (ArrayList<EntDyn>) this.entitaDynFiglie1NCollegate.get(entita);
        if (lista == null) {

          lista = new ArrayList<EntDyn>();
          try {
            List<?> entRet = this.getSql().getListVector(
                "select dynent_name from DYNENT where dynent_name_p = ? and dynent_type = 3",
                new Object[] { entita });
            if (entRet != null && entRet.size() > 0) {
              EntDyn ent = null;
              for (int j = 0; j < entRet.size(); j++) {
                ent = new EntDyn(SqlManager.getValueFromVectorParam(
                    entRet.get(j), 0).stringValue(), entita);
                List<?> campi = this.getSql().getListVector(
                    "select dyncam_name, dyncam_name_p "
                        + "from DYNCAM "
                        + "where dynent_name = ? and dynent_type = 3 "
                        + "and dyncam_pk = '1' and dyncam_name_p is not null",
                    new Object[] { SqlManager.getValueFromVectorParam(
                        entRet.get(j), 0).stringValue() });
                if (campi != null && campi.size() > 0) {
                  for (int i = 0; i < campi.size(); i++)
                    ent.addKey(SqlManager.getValueFromVectorParam(campi.get(i),
                        0).stringValue(), SqlManager.getValueFromVectorParam(
                        campi.get(i), 1).stringValue());
                }
                lista.add(ent);
              }

            }
            this.entitaDynFiglie1NCollegate.put(entita, lista);
          } catch (Throwable t) {
            if (logger.isDebugEnabled()) logger.error(t);
          }

        }
      }
    }
    return (EntDyn[]) lista.toArray(new EntDyn[0]);
  }

  /**
   * Funzione che esegue il conteggio delle occorrenze di un entità con un
   * eventuale filtro
   *
   * @param entita
   * @param where
   * @param params
   * @return
   */
  public long countOccorrenze(String entita, String where, Object[] params) {
    StringBuffer stringSql = new StringBuffer("select count(*) from ");
    stringSql.append(entita);
    if (where != null && where.length() > 0) {
      stringSql.append(" where ");
      stringSql.append(where);
    }
    try {
      List<?> ret = this.getSql().getVector(stringSql.toString(), params);
      if (ret != null && ret.size() > 0) {
        Long valore = SqlManager.getValueFromVectorParam(ret, 0).longValue();
        if (valore != null) return valore.longValue();
      }
    } catch (Throwable e) {

    }
    return 0;
  }

  /**
   * Funzione che verifica se si ha un'opzione prodotto
   *
   * @param context
   *        Contesto dell'applicazione
   * @param op
   *        Opzione prodotto da verificare
   * @return
   */
  public static boolean checkOP(ServletContext context, String op) {
    Collection<String> opzioni = Arrays.asList((String[]) context.getAttribute(
        CostantiGenerali.OPZIONI_DISPONIBILI));
    return opzioni.contains(op);
  }

  /**
   * Ritorna il nome della tabella del generatore attributi associata ad una
   * entita
   *
   * @param entita
   * @return Ritorna il nome della tabella del generatore attributi associata ad
   *         una entita se esistente, null altrimenti
   */
  public String getNomeEntitaDinamica(String entita) {
    return this.getEntDynGenAttributi(entita).getEntita();
  }

  /**
   * Metodo per l'estrazione dei campi del generatore attributi relativi alla
   * entita' dinamica da inserire in una sezione di pagina jsp. Questo metodo
   * inserisce nel PageContext gli oggetti necessari alla pagina
   * ...\pages\gene\attributi\sezione-attributi-generici.jsp o
   * pagina-attributi-generici.jsp per generare l'intera sezione o l'intera
   * pagina
   *
   * @param pageContext
   * @param entitaDinamica
   * @param entPadre
   * @throws SQLException
   */
  public void setCampiGeneratoreAttributi(PageContext pageContext,
      String entitaDinamica, String entPadre) throws SQLException {
    this.addCampiGeneratoreAttributi(pageContext, entitaDinamica, entPadre, 0);
  }

  /**
   * Metodo per l'estrazione dei campi del generatore attributi relativi alla
   * entita' dinamica da inserire nelle sezione dinamiche di una jsp a scheda.
   * Questo metodo inserisce nel PageContext tutti gli oggetti necessari alla
   * creazione delle n occorrenze dei campi del generatori attributi da
   * aggiungere nelle n sezioni dinamiche
   *
   * @param pageContext
   * @param entitaDinamica
   * @param entPadre
   * @param numeroSezioniDinamiche
   *        numero delle sezioni dinamiche presenti nella scheda (visibile e non
   *        visibili)
   * @throws SQLException
   */
  public void setCampiGeneratoreAttributi(PageContext pageContext,
      String entitaDinamica, String entPadre, int numeroSezioniDinamiche)
      throws SQLException {
    this.addCampiGeneratoreAttributi(pageContext, entitaDinamica, entPadre,
        numeroSezioniDinamiche);
  }

  /**
   * Metodo per l'estrazione dei campi del generatore attributi relativi alla
   * entita' dinamica da inserire in una specifica sezione di una jsp a scheda o
   * in una scheda con sezioni dinamiche, a seconda che il parametro
   * isSezioneGeneratoreAttributi sia valorizzato, rispettivamente, a false o a
   * true.
   *
   * Questo metodo inserisce nel PageContext tutti gli oggetti necessari alla
   * creazione dei campi del generatori attributi da aggiungere alla scheda,
   * oltre i campi dell'entita reale
   *
   * @param pageContext
   * @param entitaDinamica
   * @param entPadre
   * @param numeroSezioniDinamiche
   *        numero delle sezioni dinamiche presenti nella scheda (visibile e non
   *        visibili)
   * @param entitaJoin
   * @param whereJoin
   * @throws SQLException
   */
  private void addCampiGeneratoreAttributi(PageContext pageContext,
      String entitaDinamica, String entPadre, int numeroSezioniDinamiche,
      String entitaJoin, String whereJoin)
      throws SQLException {

    // Estraggo la scheda parent per la verifica dell'esistenza del campo in
    // visualizzazione in funzione del valore di un campo
    IFormScheda scheda = (FormSchedaTag) UtilityTags.getFromStack(pageContext,
        FormSchedaTag.class);
    if (scheda == null)
      scheda = (FormSchedaWizardTag) UtilityTags.getFromStack(pageContext,
          FormSchedaWizardTag.class);

    // scheda = null nel caso in cui devo generare la pagina per il generatore
    // attributi, != null se devo generare la sezione in una form esistente
    if (scheda == null || (scheda != null && scheda.isFirstIteration())) {

      boolean isSezioneGeneratoreAttributi = numeroSezioniDinamiche < 1;
      DizionarioCampi dizCampi = DizionarioCampi.getInstance();
      String select="select DYNCAM_NAME, DYNCAM_DESC, DYNCAM_PK, DYNCAM_SCH_B, DYNCAM_NAME_P, "
          + "DYNCAM_SCH, DYNCAM_TNF, DYNCAM_FNF, DYNCAM_OP, DYNCAM_VAL,"
          + "DYNCAM_VAL_S, DYNCAM_TIPO "
          + "from DYNCAM_GEN ";
      if(entitaJoin!=null)
        select+=", " + entitaJoin;
      select += " WHERE DYNENT_NAME=? and DYNENT_TYPE = 2 ";
      if(whereJoin!=null)
        select+=" and " + whereJoin;
      select += "order by DYNCAM_NUMORD";


      // Estraggo l'elenco di tutti i campi
      List<?> campiOut = this.sql.getListVector(
          select, new Object[] { entitaDinamica });
      String keyWhere = "";
      Vector<HashMap<String, Object>> ret = new Vector<HashMap<String, Object>>();
      HashSet<HashMap<String, String>> campiCondizioni = new HashSet<HashMap<String, String>>();
      HashMap<String, String> campiCollegatiTitoli = new HashMap<String, String>();
      HashMap<String, String> funzioniCampiCollegatiTitoli = new HashMap<String, String>();
      String nomeTitolo = null;
      String campiCollegatiTitolo = null;
      Vector<String> campiChiavi = new Vector<String>();
      Vector<String> funzioniCalcoliScheda = new Vector<String>();
      for (int i = 0; i < campiOut.size(); i++) {
        Vector<?> row = (Vector<?>) campiOut.get(i);

        // Se il campo dinamico e' presente nei metadati lo inserisco
        // nell'elenco dei campi della sezione generatore attributi,
        // altrimenti non lo inserisco.
        // In questo modo si evita che il tag gene:campoScheda dia un errore in
        // fase di creazione di quei campi non ancora presenti nei metadati.
        // Questo caso si presenta quando dall'applicativo PWB viene inserito un
        // nuovo campo per il generatore attributi e l'applicativo web non ha
        // ancora aggiornato i metadati, cosa che avviene solo riavviando
        // l'applicativo stesso

        Campo metaCampo = dizCampi.getCampoByNomeFisico(entitaDinamica.concat(
            ".").concat(row.get(0).toString()));
        if (metaCampo != null) {

          HashMap<String, Object> campo = new HashMap<String, Object>();
          campo.put("nome", row.get(0).toString());
          campo.put("entitaDinamica", entitaDinamica);
          campo.put("descr", row.get(1).toString());
          campo.put("titolo", new Boolean(false));
          campo.put("visScheda", "1".equals(row.get(5).toString()) ? new Boolean(true) : new Boolean(false));
          campo.put("modScheda", "2".equals(row.get(3).toString()) ? new Boolean(true) : new Boolean(false));

          if (!isSezioneGeneratoreAttributi) {
            StringBuffer definizione = new StringBuffer("");
            CampoDecorator campoDecorator = new CampoDecorator();
            definizione.append(campoDecorator.getTipoPerJS(metaCampo).concat(
                ";"));
            definizione.append(metaCampo.isCampoChiave() ? "1;" : "0;");
            definizione.append(metaCampo.getCodiceTabellato() != null
                ? metaCampo.getCodiceTabellato().concat(";")
                : ";");
            definizione.append(metaCampo.getDominio() != null
                ? metaCampo.getDominio().concat(";")
                : ";");
            definizione.append(metaCampo.getCodiceMnemonico());

            campo.put("definizione", definizione.toString());
          }

          String campoEsterno = row.get(4).toString();
          // Se è un campo di collegamento con l'entità padre allora lo aggiungo
          // alla chiave
          if (campoEsterno != null && campoEsterno.length() > 0) {
            if (keyWhere.length() > 0) keyWhere += " and ";
            keyWhere += entPadre
                + "."
                + campoEsterno
                + " = "
                + entitaDinamica
                + "."
                + row.get(0).toString();
            campiChiavi.add(campoEsterno);
            campo.put("chiave", new Boolean(true));
          } else
            campo.put("chiave", new Boolean(false));

          // Se è visibile il campo verifico se c'è una condizione di
          // visualizzazione
          if (!"".equals(row.get(7).toString())
              && !"".equals(row.get(8).toString())
              && (!"".equals(row.get(9).toString()) || !"".equals(row.get(10).toString()))) {
            String campoParent = entPadre + "." + row.get(7).toString();
            // Si tratta di una visibilita in funzione di un'altro campo del
            // database
            if (scheda == null || !scheda.isCampo(campoParent)) {
              // Se il campo non esiste nella scheda aggiungo il campo
              // invisibile
              // nella maschera
              HashMap<String, String> campoCondizione = new HashMap<String, String>();
              campoCondizione.put("nome", row.get(7).toString());
              campoCondizione.put("ent", entPadre);

              if (!isSezioneGeneratoreAttributi) {
                StringBuffer definizione = new StringBuffer("");
                CampoDecorator campoDecorator = new CampoDecorator();
                definizione.append(campoDecorator.getTipoPerJS(metaCampo).concat(
                    ";"));
                definizione.append(metaCampo.isCampoChiave() ? "1;" : "0;");
                definizione.append(metaCampo.getCodiceTabellato() != null
                    ? metaCampo.getCodiceTabellato().concat(";")
                    : ";");
                definizione.append(metaCampo.getDominio() != null
                    ? metaCampo.getDominio().concat(";")
                    : ";");
                definizione.append(metaCampo.getCodiceMnemonico());

                campoCondizione.put("definizione", definizione.toString());
              }

              campiCondizioni.add(campoCondizione);
            }
            // Ora genero il javascript per la gestione in visualizzazione degli
            // attributi generici in base ad un campo discriminante
            if (isSezioneGeneratoreAttributi) {
              StringBuffer buf = new StringBuffer("checkCondAttrib('#");
              buf.append(entPadre);
              buf.append("_");
              buf.append(row.get(7));
              buf.append("#','");
              buf.append(row.get(8));
              buf.append("','");
              buf.append(row.get(9));
              buf.append("','");
              buf.append(row.get(10));
              buf.append("','");
              buf.append(entitaDinamica);
              buf.append("_");
              buf.append(row.get(0));
              buf.append("')");
              funzioniCalcoliScheda.add(buf.toString()
                  + ";"
                  + entPadre
                  + "_"
                  + row.get(7).toString());

              // se per un campo è prevista la regola di visualizzazione, si
              // inserisce anche la regola corrispondente per la gestione
              // dell'accensione/spegnimento del titolo associato, se presente
              StringBuffer bufferFunzTitolo = new StringBuffer("isAttributoVisibile('#");
              bufferFunzTitolo.append(entPadre);
              bufferFunzTitolo.append("_");
              bufferFunzTitolo.append(row.get(7));
              bufferFunzTitolo.append("#','");
              bufferFunzTitolo.append(row.get(8));
              bufferFunzTitolo.append("','");
              bufferFunzTitolo.append(row.get(9));
              bufferFunzTitolo.append("','");
              bufferFunzTitolo.append(row.get(10));
              bufferFunzTitolo.append("')");
              funzioniCampiCollegatiTitoli.put(row.get(0).toString(),
                  bufferFunzTitolo.toString()
                      + ";"
                      + entPadre
                      + "_"
                      + row.get(7).toString());

            } else {
              for (int j = 1; j <= numeroSezioniDinamiche; j++) {
                StringBuffer buf = new StringBuffer("checkCondAttrib('#");
                buf.append(entPadre);
                buf.append("_");
                buf.append(row.get(7));
                buf.append("_" + j);
                buf.append("#','");
                buf.append(row.get(8));
                buf.append("','");
                buf.append(row.get(9));
                buf.append("','");
                buf.append(row.get(10));
                buf.append("','");
                buf.append(entitaDinamica);
                buf.append("_");
                buf.append(row.get(0));
                buf.append("_" + j);
                buf.append("')");
                funzioniCalcoliScheda.add(buf.toString()
                    + ";"
                    + entPadre
                    + "_"
                    + row.get(7).toString()
                    + "_"
                    + j);

                // se per un campo è prevista la regola di visualizzazione, si
                // inserisce anche la regola corrispondente per la gestione
                // dell'accensione/spegnimento del titolo associato, se presente
                StringBuffer bufferFunzTitolo = new StringBuffer("isAttributoVisibile('#");
                bufferFunzTitolo.append(entPadre);
                bufferFunzTitolo.append("_");
                bufferFunzTitolo.append(row.get(7));
                bufferFunzTitolo.append("#','");
                bufferFunzTitolo.append(row.get(8));
                bufferFunzTitolo.append("','");
                bufferFunzTitolo.append(row.get(9));
                bufferFunzTitolo.append("','");
                bufferFunzTitolo.append(row.get(10));
                bufferFunzTitolo.append("')");
                funzioniCampiCollegatiTitoli.put(row.get(0).toString() + "_" + j,
                    bufferFunzTitolo.toString()
                        + ";"
                        + entPadre
                        + "_"
                        + row.get(7).toString()
                        + "_"
                        + j);
              }
            }
          }
          ret.add(campo);

          // il campo è associato al titolo eventuale definito precedentemente
          if (campiCollegatiTitolo != null && nomeTitolo != null) {
            if (campiCollegatiTitolo.length() > 0) campiCollegatiTitolo += ";";
            campiCollegatiTitolo += row.get(0).toString();
          }

        } else {
          // in questo caso si è in presenza di un titolo e non di un campo reale

          // si processa l'elemento solo se il campo titolo è abilitato
          if ("1".equals(row.get(5).toString())) {
            // si definisce un nuovo elenco con i campi collegati al titolo e si
            // salva l'elenco finora popolato e relativo al titolo precedente
            if (campiCollegatiTitolo != null && nomeTitolo != null)
              campiCollegatiTitoli.put(nomeTitolo, campiCollegatiTitolo);
            campiCollegatiTitolo = "";
            nomeTitolo = row.get(0).toString();

            // si aggiunge il campo titolo
            HashMap<String, Object> campo = new HashMap<String, Object>();
            campo.put("nome", nomeTitolo);
            campo.put("entitaDinamica", entitaDinamica);
            campo.put("descr", row.get(1).toString());
            campo.put("titolo", new Boolean(true));
            campo.put("chiave", new Boolean(false));

            ret.add(campo);
          }

        }
      }

      // al termine del ciclo si salva l'elenco finora popolato e relativo al
      // titolo precedente
      if (campiCollegatiTitolo != null && nomeTitolo != null)
        campiCollegatiTitoli.put(nomeTitolo, campiCollegatiTitolo);

      // Salvo nel context i valori estratti
      pageContext.setAttribute("elencoCampi_".concat(entPadre), ret,
          PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("campiCondizioni_".concat(entPadre), campiCondizioni,
          PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("whereKeys_".concat(entPadre), keyWhere,
          PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("campiChiavi_".concat(entPadre), campiChiavi,
          PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("funzioniCalcoliScheda_".concat(entPadre),
          funzioniCalcoliScheda, PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("campiCollegatiTitoli_".concat(entPadre), campiCollegatiTitoli,
          PageContext.REQUEST_SCOPE);
      pageContext.setAttribute("funzioniCampiCollegatiTitoli_".concat(entPadre), funzioniCampiCollegatiTitoli,
          PageContext.REQUEST_SCOPE);
    }
  }

  private void addCampiGeneratoreAttributi(PageContext pageContext,
      String entitaDinamica, String entPadre, int numeroSezioniDinamiche)
      throws SQLException {
    this.addCampiGeneratoreAttributi(pageContext, entitaDinamica, entPadre, numeroSezioniDinamiche, null, null);
  }

  private class EntScadenziario {

    //private final String   entita;
    private final String   where;
    private String   join;
    private Object[] params;
    //private final String[] campiKey;

    public EntScadenziario(String entita, String where, String join, Object[] params,
        String[] campiKey) {
      //this.entita = entita;
      if (join != null && join.length() > 0)
        this.join = join;
      else
        this.join = null;
      if (params == null)
        this.params = new Object[] {};
      else
        this.params = params;
      this.where = where;
      //this.campiKey = campiKey;
    }

    public String getJoin() {
      return this.join;
    }

    public boolean isOk() {
      return this.join != null;
    }

    public Object[] getParams(Object[] postpend) {
      Vector<Object> ret = new Vector<Object>();
      for (int i = 0; i < params.length; i++)
        ret.add(params[i]);
      for (int i = 0; i < postpend.length; i++)
        ret.add(postpend[i]);

      return ret.toArray(new Object[0]);
    }

    public String getWhere() {
      return this.where;
    }

  }


  private EntScadenziario getEntScadenziario(String entita) throws GestoreException {
    entita = entita.toUpperCase();
    EntScadenziario ent = (EntScadenziario) this.entitaG_ScadenzCollegate.get(entita);
    if (ent == null) {
      StringBuffer join = new StringBuffer("");
      StringBuffer where = new StringBuffer("");
      Vector<String> campiKey = new Vector<String>();
      Tabella tab = DizionarioTabelle.getInstance().getDaNomeTabella(entita);
      Vector<String> params = new Vector<String>();
      if (tab != null) {
        where.append("G_SCADENZ.ENT = ?");
        params.add(entita);
        // Se l'entità è valida allora creo la join
        for (int i = 0; i < tab.getCampiKey().size(); i++) {
          Campo oCampo = tab.getCampiKey().get(i);
          if (join.length() > 0) join.append(" and ");
          join.append("G_SCADENZ.KEY");
          join.append(i + 1);
          join.append(" = ");
          switch (oCampo.getTipoColonna()) {
          case Campo.TIPO_STRINGA:
            join.append(oCampo.getNomeFisicoCampo());
            break;
          case Campo.TIPO_INTERO:
            join.append(getSql().getDBFunction("inttostr",
                new String[] { oCampo.getNomeFisicoCampo() }));
            break;
          default:
            throw new GestoreException(
                "Tipo di dato per un campo chiave nella G_GAREAVVISI non ancora gestito ("
                    + oCampo.getTipoColonna()
                    + ")", "tipoChiaveScadenziario");
          }
          campiKey.add(oCampo.getNomeCampo().toUpperCase());
        }
      }
      ent = new EntScadenziario(entita, where.toString(), join.toString(),
          params.toArray(new Object[0]),
          (String[]) campiKey.toArray(new String[0]));
      this.entitaG_ScadenzCollegate.put(entita, ent);
    }

    return ent;
  }

  /**
   * Funzione che esegue l'eliminazione delle occorrenze dello scadenzario
   * collegate a una tabella
   *
   * @param entita
   *        Entita in cui sono collegate le occorrenze del generatore attributi
   * @param where
   *        Filtro sulla tabella
   * @param param
   *        Paramentri del filtro
   * @throws GestoreException
   */
   public void eliminaOccorrenzeScadenzario(String entita, String where,
      Object[] param) throws GestoreException {
     EntScadenziario ent = getEntScadenziario(entita);

    if (ent.isOk()) {
      // Se c'è l'entita collegata allo scadenzario allora la elimino
      StringBuffer buf = new StringBuffer("");
      buf.append("delete from G_SCADENZ where ");
      buf.append(ent.getWhere());
      buf.append(" and exists(select 1 from ");
      buf.append(entita);
      buf.append(" where ");

      buf.append(ent.getJoin());
      buf.append(" and ");
      buf.append(where);

      buf.append(" ) ");
      // Eseguo la vera e proprie eliminazione dell'entità collegata
      try {
        this.sql.update(buf.toString(), ent.getParams(param));
      } catch (SQLException e) {
        throw new GestoreException(
            "Errore in eliminazione dello scadenzario",
            "deleteScadenzario", new String[] { entita }, e);
      }
    }
  }

	/**
	 * Metodo che verifica se ci sono occorrenze di un'entita' referenziate
	 * in un'altra entita'.
	 * @param codiceTabella
	 * @param constraints valori dei campi che formano la chiave esterna.
	 * Fondamentale prestare attenzione alla posizione dei campi restituiti
	 * dal metodo getElencoCampiTabellaDestinazione(), quindi occo passare i
	 * valori nella posizione corretta dell'array.
	 * @throws GestoreException
	 */
	public void checkConstraints(String codiceTabella, Object[] constraints) throws GestoreException {

		Tabella tabella = DizionarioTabelle.getInstance().getDaNomeTabella(codiceTabella);
		List<LegameTabelle> legami = tabella.getLegamiTabelle();
		if (legami != null) {
			//Object[] params;
			for (LegameTabelle legame : legami) {
				String campi[] = legame.getElencoCampiTabellaDestinazione();
				String tabellaDest = legame.getTabellaDestinazione();
				Tabella tabellaDestinazione = DizionarioTabelle.getInstance().getDaNomeTabella(tabellaDest);
				//params = new Object[campi.length];
				if (this.getSql().isTable(tabellaDest)
								&& campi.length > 0
								&& campi.length == constraints.length) {
					//si suppone che se il numero di chiavi sia diverso dal numero di
					//parametri-legame delle 2 entita', allora si sta considerando la
					//relazione con il padre (es: da PUNTICON ho sempre un record in
					//UFFINT, ma questa condizione non deve bloccare l'eliminazione)
					try {
						StringBuilder sbQuery = new StringBuilder();
						sbQuery.append("SELECT COUNT(1) FROM ").append(tabellaDest).append(" WHERE ");
						for (int j = 0; j < campi.length; j++) {
							if (j > 0) {
								sbQuery.append(" AND ");
							}
							sbQuery.append(campi[j]).append(" = ?");
							//params[j] = constraints[j];
						}
						Long occorrenze = (Long) this.getSql().getObject(sbQuery.toString(), constraints);
						if (occorrenze != null && occorrenze > 0) {
							String msg = "Impossibile eliminare l'occorrenza (" + StringUtils.join(constraints,",") + ") dell'entita' "
											+ tabella.getDescrizione() + " in quanto viene utilizzata in almeno un record nella tabella "
											+ tabellaDestinazione.getDescrizione();
							Exception e = null;
							throw new GestoreException(msg, "eliminazioneIstanza",
											new Object[]{StringUtils.join(constraints,","), tabella.getDescrizione(),
												tabellaDestinazione.getDescrizione()}, e);

						}
					} catch (SQLException e) {
						throw new GestoreException("Errore nel conteggio dei riferimenti dell'entita' " + tabellaDest, null, e);
					}
				}
			}
		}
	}

	public void setCampiGeneratoreAttributi(PageContext pageContext,
	      String entitaDinamica, String entPadre, String entitaJoin, String whereJoin) throws SQLException {
	    this.addCampiGeneratoreAttributi(pageContext, entitaDinamica, entPadre, 0, entitaJoin, whereJoin);
	  }
}