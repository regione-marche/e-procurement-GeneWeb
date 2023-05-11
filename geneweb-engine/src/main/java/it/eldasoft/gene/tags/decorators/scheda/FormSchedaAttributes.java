/*
 * Created on 4-gen-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
package it.eldasoft.gene.tags.decorators.scheda;

import java.util.Vector;

import it.eldasoft.gene.tags.TagAttributes;
import it.eldasoft.utils.metadata.domain.Tabella;

/**
 * Attributi variabili della scheda
 * 
 * @author marco.franceschin
 * 
 */
public class FormSchedaAttributes extends TagAttributes {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String            entita;

  private Vector            elencoCampi;

  /** Elenco degli archivi */
  private Vector            archivi;

  private Tabella           table;

  private String            where;

  private CampiNonDiEntita  entitaDiverse;

  private String            gestore;

  private int               campoAttivo;

  private boolean           isArchivio;

  /** Flag per definire la gestione delle protezioni */
  private boolean           gestisciProtezioni;

  /** Classe di visualizzazione del TAB */
  private String            tableClass;

  /** Gestore di precaricamento della pagina */
  private String            plugin;

  public FormSchedaAttributes(String tipoVar) {
    super(tipoVar);
    this.elencoCampi = new Vector();
    this.archivi = new Vector();
    this.entitaDiverse = new CampiNonDiEntita();
    this.table = null;
    this.campoAttivo = 0;
    this.gestisciProtezioni = false;
    this.entita = null;
    this.where = null;
    this.tableClass = null;
    this.plugin = null;
  }

  public Vector getArchivi() {
    return archivi;
  }

  public void setArchivi(Vector archivi) {
    this.archivi = archivi;
  }

  public int getCampoAttivo() {
    return campoAttivo;
  }

  public void setCampoAttivo(int campoAttivo) {
    this.campoAttivo = campoAttivo;
  }

  public Vector getElencoCampi() {
    return elencoCampi;
  }

  public void setElencoCampi(Vector elencoCampi) {
    this.elencoCampi = elencoCampi;
  }

  public String getEntita() {
    return entita;
  }

  public void setEntita(String entita) {
    this.entita = entita;
  }

  public CampiNonDiEntita getEntitaDiverse() {
    return entitaDiverse;
  }

  public void setEntitaDiverse(CampiNonDiEntita entitaDiverse) {
    this.entitaDiverse = entitaDiverse;
  }

  public String getGestore() {
    return gestore;
  }

  public void setGestore(String gestore) {
    this.gestore = gestore;
  }

  public boolean isArchivio() {
    return isArchivio;
  }

  public void setArchivio(boolean isArchivio) {
    this.isArchivio = isArchivio;
  }

  public Tabella getTable() {
    return table;
  }

  public void setTable(Tabella table) {
    this.table = table;
  }

  public String getWhere() {
    return where;
  }

  public void setWhere(String where) {
    this.where = where;
  }

  public boolean isGestisciProtezioni() {
    return gestisciProtezioni;
  }

  public void setGestisciProtezioni(boolean gestisciProtezioni) {
    this.gestisciProtezioni = gestisciProtezioni;
  }

  /**
   * @return the tableClass
   */
  public String getTableClass() {
    return tableClass;
  }

  /**
   * @param tableClass
   *        the tableClass to set
   */
  public void setTableClass(String tableClass) {
    this.tableClass = tableClass;
  }

  /**
   * @return Ritorna plugin.
   */
  public String getPlugin() {
    return plugin;
  }

  /**
   * @param plugin
   *        plugin da settare internamente alla classe.
   */
  public void setPlugin(String plugin) {
    this.plugin = plugin;
  }

}
