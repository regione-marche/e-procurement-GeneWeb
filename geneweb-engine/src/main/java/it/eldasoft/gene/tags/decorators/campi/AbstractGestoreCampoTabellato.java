/*
 * Created on 07-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.campi;

import it.eldasoft.gene.bl.SqlManager;
import it.eldasoft.gene.db.datautils.DataColumn;
import it.eldasoft.utils.spring.UtilitySpring;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Gestore semplificato per i campi di tipo tabellato, in cui basta estendere
 * questa classe e fornire la select per l'estrazione dei valori previsti per il
 * tabellato
 * 
 * @author Marco.Franceschin
 */
public abstract class AbstractGestoreCampoTabellato extends
    AbstractGestoreCampo {

  /** Logger */
  static Logger   logger = Logger.getLogger(AbstractGestoreCampoTabellato.class);

  private boolean addCodADescr;
  private String  tipoCampo;

  /**
   * Costruttore della classe
   * 
   * @param addCodADescr
   *        Flag per dire di aggiungere il codice prima della descrizione
   * @param tipoCampo
   *        formato del campo (Nx: numerico, Tx: testo, con x=dimensione)
   */
  public AbstractGestoreCampoTabellato(boolean addCodADescr, String tipoCampo) {
    this.addCodADescr = addCodADescr;
    this.tipoCampo = tipoCampo;
  }

  /*
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo#initGestore()
   */
  protected void initGestore() {
    SqlManager sql = (SqlManager) UtilitySpring.getBean("sqlManager",
        this.getPageContext(), SqlManager.class);
    this.getCampo().setTipo("E" + tipoCampo);
    this.getCampo().getValori().clear();
    this.getCampo().addValore("", " ");
    SqlSelect select = this.getSql();
    if (select != null
        && select.getSql() != null
        && select.getSql().length() > 0) {
      try {
        List ret = sql.getListVector(select.getSql(), select.getParam());
        for (int i = 0; i < ret.size(); i++) {
          Vector row = (Vector) ret.get(i);
          String cod = "";
          for (int k = 0; k < row.size() - 1; k++) {
            cod += row.get(k).toString();
          }
          String descr = row.get(row.size() - 1).toString();
          if (this.addCodADescr) descr = cod + " " + descr;
          this.getCampo().addValore(cod, descr);
        }
      } catch (SQLException e) {
        logger.error("Errore durante l'estrazione del tabellato", e);
        throw new RuntimeException("Errore durante l'estrazione del tabellato",
            e);
      }
    }
  }

  /**
   * Ritorna un oggetto di tipo SqlSelect con la configurazione della select da
   * eseguire per estrarre il tabellato
   */
  abstract public SqlSelect getSql();

  /**
   * Classe per il passaggio dell'SQL per la selezione
   * 
   * @author Marco.Franceschin
   */
  public class SqlSelect {

    /** SQL da eseguire */
    private String sql;
    /** Parametri da sostituire nella query */
    private Object param[];

    /**
     * Costruttore nel caso di nessun parametro
     * 
     * @param sql
     *        SQL da eseguire
     */
    public SqlSelect(String sql) {
      this(sql, new Object[] {});
    }

    /**
     * Costruttore nel caso di parametri
     * 
     * @param sql
     *        SQL da eseguire
     * @param param
     *        elenco dei parametri
     */
    public SqlSelect(String sql, Object param[]) {
      this.sql = sql;
      this.param = param;
    }

    /**
     * @return Ritorna param.
     */
    public Object[] getParam() {
      return param;
    }

    /**
     * @return Ritorna sql.
     */
    public String getSql() {
      return sql;
    }

  }

  /*
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo#preHTML(boolean,
   *      boolean)
   */
  public String preHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  /*
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo#getHTML(boolean,
   *      boolean)
   */
  public String getHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  /*
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo#postHTML(boolean,
   *      boolean)
   */
  public String postHTML(boolean visualizzazione, boolean abilitato) {
    return null;
  }

  /*
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo#getClasseEdit()
   */
  public String getClasseEdit() {
    return null;
  }

  /*
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo#getClasseVisua()
   */
  public String getClasseVisua() {
    return null;
  }

  /*
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo#getValore(java.lang.String)
   */
  public String getValore(String valore) {
    return null;
  }

  /*
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo#getValorePerVisualizzazione(java.lang.String)
   */
  public String getValorePerVisualizzazione(String valore) {
    return null;
  }

  /*
   * @see it.eldasoft.gene.tags.decorators.campi.AbstractGestoreCampo#getValorePreUpdateDB(java.lang.String)
   */
  public String getValorePreUpdateDB(String valore) {
    return null;
  }

  public String gestisciDaTrova(Vector params,
      DataColumn colWithValue, String conf, SqlManager manager) {
    return null;
  }

}
